import static utilities.account.AccountTest.ADMIN_USERNAME_GOAPP;
import static utilities.account.AccountTest.ADMIN_USERNAME_GOLEAD;
import static utilities.account.AccountTest.ADMIN_USERNAME_GOPOS;
import static utilities.account.AccountTest.ADMIN_USERNAME_GOSOCIAL;
import static utilities.account.AccountTest.ADMIN_USERNAME_GOWEB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.login.Login;
import api.dashboard.orders.OrderAPI;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.orders.orderlist.OrderList;
import pages.dashboard.products.productreviews.ProductReviews;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.header.HeaderSF;
import pages.storefront.userprofile.MyOrders;
import utilities.PropertiesUtil;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;
import utilities.data.FormatDate;

public class ProductReviewTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	ProductReviews productReviewPage;
	OrderList orderList;
	pages.storefront.login.LoginPage sfLoginPage;
	HeaderSF sfHeader;
	ProductDetailPage sfProductDetailPage;
	CheckOutStep1 checkOutStep1;
	MyOrders myOrderPage;

	String displayLanguage = "ENG";

	String randomProduct = "";

	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String sellerUsername = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String sellerPassword = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String sellerCountry = sellerData.findValue("seller").findValue("mail").findValue("country").asText();
	JsonNode buyerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String buyerUsername = buyerData.findValue("buyer").findValue("phone").findValue("username").asText();
	String buyerPassword = buyerData.findValue("buyer").findValue("phone").findValue("password").asText();
	String buyerCountry = buyerData.findValue("buyer").findValue("phone").findValue("country").asText();

	@BeforeClass
	public void logIntoDashboardByAPI() {
		new Login().loginToDashboardByMail(sellerUsername, sellerPassword);
	}	
	
	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		productReviewPage = new ProductReviews(driver);
		sfLoginPage = new pages.storefront.login.LoginPage(driver);
		sfHeader = new HeaderSF(driver);
		checkOutStep1 = new CheckOutStep1(driver);
		sfProductDetailPage = new ProductDetailPage(driver);
		
		randomProduct = randomProduct();
	}

	public String randomProduct() {
		String[] pro1ducts = { "Fish Food", "Tetra Fish Food", "Dog Food", "Cat Food", "Bird Food" };
		return pro1ducts[new Random().nextInt(0, pro1ducts.length)];
	}	
	
	public String randomSearchProduct() {
        List<String> allProducts = new api.dashboard.products.ProductReviews().getProductNameList();
        Set<String> uniqueNames = new HashSet<String>(allProducts);
        List<String> productNames = new ArrayList<String>(uniqueNames);
        return productNames.get(new Random().nextInt(0, productNames.size()));
	}	

    public void confirmDeliverOrderByAPI(String orderID){
        OrderAPI orderAPI = new OrderAPI();
        orderAPI.confirmOrder(orderID);
        orderAPI.deliverOrder(orderID);
    }		
	
	public List<Integer> getRatingListByAPI() {
		return new api.dashboard.products.ProductReviews().getAllReviewJsonPath().getList("rate");
	}	
	
	public List<Date> getCreatedDateListByAPI() {
		List<String> rawList = new api.dashboard.products.ProductReviews().getAllReviewJsonPath().getList("reviewDate");
		List<Date> processedList = new ArrayList<>();
		FormatDate formatDate = new FormatDate();
		for (String date : rawList) {
			processedList.add(formatDate.convertStringToDate("yyyy-MM-dd", date));
		}
		return processedList;
	}	

    /**
     * 
     * @param condition newToOld/oldToNew/highToLow/lowToHigh
     * @param displayLanguage
     * @return
     * @throws Exception
     */
	public String tranlateSortText(String condition, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("product.review.filter." + condition, displayLanguage);
	}
	
	public List<Integer> sortRatingRetrievedFromAPI(List<Integer> ratings, String condition) throws Exception {
		if (condition.contentEquals("lowToHigh")) {
			Collections.sort(ratings);
			return ratings;
		}
		Collections.sort(ratings, Comparator.reverseOrder());
		return ratings;
	}    
	
	public List<Date> sortCreatedDate(List<Date> dates, String condition) throws Exception {
		if (condition.contentEquals("oldToNew")) {
			Collections.sort(dates);
			return dates;
		}
		Collections.sort(dates, Comparator.reverseOrder());
		return dates;
	}    
	
	public List<Date> extractCreatedDateFromReview(List<List<String>> reviews) throws Exception {
		List<Date> extractedCreatedDates = new ArrayList<>();
		FormatDate formatDate = new FormatDate();
		for (List<String> review : reviews) {
			extractedCreatedDates.add(formatDate.convertStringToDate("dd/MM/yyyy", review.get(5)));
		}
		return extractedCreatedDates;
	}    
	
	public List<Integer> extractRatingFromReview(List<List<String>> reviews) {
		List<Integer> extractedRatings = new ArrayList<>();
		for (List<String> review : reviews) {
			extractedRatings.add(Integer.parseInt(review.get(1)));
		}
		return extractedRatings;
	}    
	
	public List<String> extractProductNameFromReview(List<List<String>> reviews) {
		List<String> extractedNames = new ArrayList<>();
		for (List<String> review : reviews) {
			extractedNames.add(review.get(0));
		}
		return extractedNames;
	}    

    public void verifyReviewAppearOnSF(List<List<String>> dbReviews, List<List<String>> sfReviews){
		String expectedRating = dbReviews.get(0).get(1);
		String expectedTitle = dbReviews.get(0).get(2);
		String expectedDescription = dbReviews.get(0).get(3);
		String expectedReviewer = dbReviews.get(0).get(4);
		String expectedTime = new FormatDate().formatDate("dd/MM/yyyy", "yyyy-MM-dd", dbReviews.get(0).get(5));
		
		String actualRating = sfReviews.get(0).get(0);
		String actualTitle = sfReviews.get(0).get(3);
		String actualDescription = sfReviews.get(0).get(4);
		String actualReviewer = sfReviews.get(0).get(1);
		String actualTime = sfReviews.get(0).get(2);
		
		Assert.assertEquals(actualRating, expectedRating, "Rating");
		Assert.assertEquals(actualTitle, expectedTitle, "Review title");
		Assert.assertEquals(actualDescription, expectedDescription, "Review description");
		Assert.assertEquals(actualReviewer, expectedReviewer, "Reviewer");
		Assert.assertTrue(actualTime.contains(expectedTime) , "Review time matches");
    }	
    
    public void verifyResultMatchSearchTerm(List<List<String>> dbReviews, String searchTerm){
		Assert.assertNotEquals(dbReviews.size(), 0, "Number of found records");
    	List<String> names = extractProductNameFromReview(dbReviews);
    	for (String name : names) {
    		Assert.assertTrue(name.toLowerCase().contains(searchTerm.toLowerCase()));
    	}
    }	
    
	public String buyProductThenDeliverOrder(String product) throws Exception {
		/* Log into SF */
		sfLoginPage.navigate().performLogin(buyerCountry, buyerUsername, buyerPassword);
		sfHeader.waitTillLoaderDisappear();

		/* Buy product */
		sfHeader.clickUserInfoIcon()
		.changeLanguage(displayLanguage)
		.searchWithFullName(product)
		.clickSearchResult()
		.waitTillLoaderDisappear();
		
		sfProductDetailPage.clickOnBuyNow()
		.clickOnContinue()
		.getFullName();
		checkOutStep1.selectPaymentMethod("COD")
		.clickOnNextButton()
		.selectShippingMethod("Self delivery")
		.clickOnNextButton()
		.clickOnNextButton()
		.clickOnBackToMarket();

		/* See order details */
		List<List<String>> orderData = sfHeader.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyOrdersSection()
		.getOrderData();
		
		/* Use API to deliver the order */
		String orderId = orderData.get(0).get(0).replaceAll("\\D", "");
		confirmDeliverOrderByAPI(orderId);
		return orderId;
	}  
	
	public void leaveReview(String product) throws Exception {
		
		String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(10);
		int randomStar = new DataGenerator().generatNumberInBound(1, 6);
		
		/* Log into SF */
		sfLoginPage.navigate().performLogin(buyerCountry, buyerUsername, buyerPassword);
		sfHeader.waitTillLoaderDisappear();
		
		/* Buy product */
		sfHeader.clickUserInfoIcon()
		.changeLanguage(displayLanguage)
		.searchWithFullName(product)
		.clickSearchResult()
		.waitTillLoaderDisappear();
		
		sfProductDetailPage.clickOnBuyNow()
		.clickOnContinue()
		.getFullName();
		checkOutStep1.selectPaymentMethod("COD")
		.clickOnNextButton()
		.selectShippingMethod("Self delivery")
		.clickOnNextButton()
		.clickOnNextButton()
		.clickOnBackToMarket();
		
		/* See order details */
		List<List<String>> orderData = sfHeader.clickUserInfoIcon()
				.clickUserProfile()
				.clickMyOrdersSection()
				.getOrderData();
		
		/* Use API to deliver the order */
		String orderId = orderData.get(0).get(0).replaceAll("\\D", "");
		confirmDeliverOrderByAPI(orderId);
		
		/* Leave a review */
		commonAction.refreshPage();
		new MyOrders(driver).clickWriteReview(orderId)
		.leaveReview(randomStar, "So good " + randomNumber, "Absolutely love the product " + randomNumber);
	}  
	
	@Test
	public void PR_00_PermissionToUseProductReviews() throws Exception {
		
        Map<String, String> permission = new HashMap<String, String>();
        permission.put(ADMIN_USERNAME_GOWEB, "A");
        permission.put(ADMIN_USERNAME_GOAPP, "A");
        permission.put(ADMIN_USERNAME_GOPOS, "S");
        permission.put(ADMIN_USERNAME_GOSOCIAL, "S");
        permission.put(ADMIN_USERNAME_GOLEAD, "S");
		
		/* Log into dashboard */
        for (String username : permission.keySet()) {
    		dbLoginPage.navigate().performLogin(username, sellerPassword);
    		homePage.waitTillSpinnerDisappear();
    		productReviewPage.navigate().verifyPermissionToManageReviews(permission.get(username));
    		homePage.clickLogout();        	
        }
	}    
	
	@Test
	public void PR_01_CheckTranslation() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Check text at management screen */
		productReviewPage.navigate().verifyTextAtReviewManagementScreen(displayLanguage);
	}    
    
	public void PR_02_DisableProductReviews() throws Exception {

		buyProductThenDeliverOrder(randomProduct);
		
		/* Log into dashboard */
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Disable reviews */
		productReviewPage.navigate().disableProductReviews();
		
		/* See if Write Review link text is present on SF */
		commonAction.switchToWindow(0);
		commonAction.refreshPage();
		Assert.assertFalse(new MyOrders(driver).isWriteReviewDisplayed());
		
		sfHeader.searchWithFullName(randomProduct).clickSearchResult().waitTillLoaderDisappear();
		Assert.assertFalse(sfProductDetailPage.isReviewTabDisplayed(), "Is Review tab displayed");
		
		/* Enable reviews */
		commonAction.switchToWindow(1);
		productReviewPage.enableProductReviews();
		
		/* See if Write Review link text is present on SF */
		commonAction.switchToWindow(0);
		commonAction.refreshPage();
		Assert.assertTrue(sfProductDetailPage.isReviewTabDisplayed(), "Is Review tab displayed");	
		commonAction.navigateBack();
		Assert.assertTrue(new MyOrders(driver).isWriteReviewDisplayed());
	}
	
	@Test
	public void PR_03_EnableProductReviews() throws Exception {
		PR_02_DisableProductReviews();
	}
	
	@Test
	public void PR_04_HideProductReviews() throws Exception {
		
		int reviewIndex = 0;
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Enable reviews */
		productReviewPage.navigate().enableProductReviews();
		
		/* Approve reviews */
		productReviewPage.approveReview(reviewIndex);
		
		/* Click on the first navigation icon */
		productReviewPage.clickNavigationIcon(reviewIndex);
		
		/* Get reviews on SF */ 
		commonAction.switchToWindow(1);
		List<List<String>> sfReviews = sfProductDetailPage.getAllReviews();
		
		/* Hide reviews */
		commonAction.switchToWindow(0);
		productReviewPage.disapproveReview(reviewIndex);
		
		/* Get reviews on SF after hiding review */ 
		commonAction.switchToWindow(1);
		commonAction.refreshPage();
		List<List<String>> sfReviews1 = sfProductDetailPage.getAllReviews();
		
		/* Verify the review is hidden */ 
		Assert.assertNotEquals(sfReviews, sfReviews1);
	}	
	
	@Test
	public void PR_05_ShowProductReviews() throws Exception {
		
		int reviewIndex = 0;
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Enable reviews */
		productReviewPage.navigate().enableProductReviews();
		
		/* Hide review */
		productReviewPage.disapproveReview(reviewIndex);
		
		/* Click on the first navigation icon */
		productReviewPage.clickNavigationIcon(reviewIndex);
		
		/* Get reviews on SF */ 
		commonAction.switchToWindow(1);
		List<List<String>> sfReviews = sfProductDetailPage.getAllReviews();
		
		/* Approve reviews */
		commonAction.switchToWindow(0);
		productReviewPage.approveReview(reviewIndex);
		
		/* Get reviews on SF after hiding review */ 
		commonAction.switchToWindow(1);
		commonAction.refreshPage();
		List<List<String>> sfReviews1 = sfProductDetailPage.getAllReviews();
		
		/* Verify the review is display */ 
		Assert.assertNotEquals(sfReviews, sfReviews1);
	}	

	@Test
	public void PR_06_OnlyOneReviewEachOrder() throws Exception {
		
		String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(10);
		int randomStar = new DataGenerator().generatNumberInBound(1, 6);
		
		String orderId = buyProductThenDeliverOrder(randomProduct);

		/* Log into dashboard */
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Disable/Enable reviews */
		productReviewPage.navigate().enableProductReviews();
		
		/* Leave a review */
		commonAction.switchToWindow(0);
		commonAction.refreshPage();
		myOrderPage = new MyOrders(driver);
		myOrderPage.clickWriteReview(orderId);
		sfProductDetailPage.leaveReview(randomStar, "So good " + randomNumber, "Absolutely love the product " + randomNumber);

		/* Approve reviews */
		commonAction.switchToWindow(1);
		commonAction.refreshPage();
		productReviewPage.navigate().approveReview(0);
		
		/* Get reviews on Dashboard */
		List<List<String>> dbReviews = productReviewPage.getReviewTable();
		
		/* See if Write Review link text is present on SF */
		commonAction.switchToWindow(0);
		commonAction.refreshPage();
		sfProductDetailPage.clickReviewTab();
		List<List<String>> sfReviews = sfProductDetailPage.getAllReviews();
		
		verifyReviewAppearOnSF(dbReviews, sfReviews);
		
		/* See if Write Review link text is present on SF */
		commonAction.navigateBack();
		Assert.assertFalse(myOrderPage.isWriteReviewDisplayed(orderId));
	}	
	
	@Test
	public void PR_07_NavigateToProductDetailOnSF() throws Exception {

		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Enable reviews */
		productReviewPage.navigate().enableProductReviews();
		
		/* Get reviews on Dashboard */
		productReviewPage.approveReview(0);
		List<List<String>> dbReviews = productReviewPage.getReviewTable();
		
		/* Click on the first navigation icon */
		productReviewPage.clickNavigationIcon(0);
		
		/* Get reviews on SF */ 
		commonAction.switchToWindow(1);
		List<List<String>> sfReviews = sfProductDetailPage.getAllReviews();
		
		/* Check if review is present on SF */ 
		verifyReviewAppearOnSF(dbReviews, sfReviews);
	}	
	
	@Test
	public void PR_08_SearchReviews() throws Exception {
		
        String randomSearchProduct = randomSearchProduct();
        String searchTerm = randomSearchProduct.substring(0, randomSearchProduct.length()/2);
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Absolute match */
		productReviewPage.navigate().inputSearchTerm(searchTerm);
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm);
		
		/* Partly match */
		productReviewPage.navigate().inputSearchTerm(randomSearchProduct);
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm);
		
		/* Ignore case */
		productReviewPage.navigate().inputSearchTerm(randomSearchProduct.toLowerCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), randomSearchProduct.toLowerCase());
		productReviewPage.navigate().inputSearchTerm(searchTerm.toLowerCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm.toLowerCase());
		productReviewPage.navigate().inputSearchTerm(randomSearchProduct.toUpperCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), randomSearchProduct.toUpperCase());
		productReviewPage.navigate().inputSearchTerm(searchTerm.toUpperCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm.toUpperCase());
	}	
	
	@Test
	public void PR_09_SortReviews() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Get reviews */
		productReviewPage.navigate();
		
		List<Integer> initialRating = getRatingListByAPI();
		
		/* Sort reviews by low to high rating */
		String sortCondition = "lowToHigh";
		
		List<Integer> expectedRating = sortRatingRetrievedFromAPI(initialRating, sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition, displayLanguage));
		
		List<Integer> actualRating = extractRatingFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualRating, expectedRating);
		
		/* Sort reviews by high to low rating */
		sortCondition = "highToLow";
		
		expectedRating = sortRatingRetrievedFromAPI(initialRating, sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition, displayLanguage));
		
		actualRating = extractRatingFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualRating, expectedRating);		
		
		/* Sort reviews by new to old created date */
		sortCondition = "newToOld";
		
		commonAction.refreshPage();
		homePage.waitTillSpinnerDisappear1();
		
		List<List<String>> initialReviews = productReviewPage.getAllReviewTable();
		
		List<Date> expectedCreatedDate = sortCreatedDate(extractCreatedDateFromReview(initialReviews), sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition, displayLanguage));
		
		List<Date> actualCreatedDate = extractCreatedDateFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualCreatedDate, expectedCreatedDate);	
		
		/* Sort reviews by old to new created date */
		sortCondition = "oldToNew";
		
		expectedCreatedDate = sortCreatedDate(extractCreatedDateFromReview(initialReviews), sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition, displayLanguage));
		
		actualCreatedDate = extractCreatedDateFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualCreatedDate, expectedCreatedDate);	
		
	}	
	
}
