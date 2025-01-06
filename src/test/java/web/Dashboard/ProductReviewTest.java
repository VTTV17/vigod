package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.orders.OrderAPI;
import api.Seller.products.product_reviews.APIProductReviews;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.orderlist.order_list.OrderManagementPage;
import web.Dashboard.products.productreviews.ProductReviews;
import web.StoreFront.checkout.checkoutOneStep.Checkout;
import web.StoreFront.checkout.checkoutstep1.CheckOutStep1;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.userprofile.myorder.MyOrders;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.FormatDate;
import utilities.driver.InitWebdriver;
import utilities.utils.jsonFileUtility;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;

import static utilities.account.AccountTest.*;
import static utilities.links.Links.SF_URL_TIEN;

public class ProductReviewTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	ProductReviews productReviewPage;
	OrderManagementPage orderList;
	web.StoreFront.login.LoginPage sfLoginPage;
	HeaderSF sfHeader;
	ProductDetailPage sfProductDetailPage;
	CheckOutStep1 checkOutStep1;
	MyOrders myOrderPage;
	LoginInformation loginInformation;

	
	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String sellerUsername = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String sellerPassword = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String sellerCountry = sellerData.findValue("seller").findValue("mail").findValue("country").asText();
	JsonNode buyerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String buyerUsername = buyerData.findValue("buyer").findValue("phone").findValue("username").asText();
	String buyerPassword = buyerData.findValue("buyer").findValue("phone").findValue("password").asText();
	String buyerCountry = buyerData.findValue("buyer").findValue("phone").findValue("country").asText();

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		productReviewPage = new ProductReviews(driver);
		sfLoginPage = new web.StoreFront.login.LoginPage(driver);
		sfHeader = new HeaderSF(driver);
		checkOutStep1 = new CheckOutStep1(driver);
		sfProductDetailPage = new ProductDetailPage(driver);
		commonAction = new UICommonAction(driver);
	}	
	
	public void loginDashboard() {
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear1().selectLanguage(language);
	}	
	
	/**
	 * Logs into SF and change user language
	 */
	public void loginSF() {
		sfLoginPage.navigate(SF_URL_TIEN).performLogin(buyerCountry, buyerUsername, buyerPassword);
		sfHeader.waitTillLoaderDisappear();
		sfHeader.clickUserInfoIcon().changeLanguage(language);
	}	

	public String randomProduct() {
		//TODO make the list random
		String[] pro1ducts = { "Fish Food", "Tetra Fish Food", "Dog Food", "Cat Food", "Bird Food" };
		return pro1ducts[new Random().nextInt(0, pro1ducts.length)];
	}	
	
	public String randomSearchProduct() {
        List<String> allProducts = new APIProductReviews(loginInformation).getProductNameList();
        Set<String> uniqueNames = new HashSet<String>(allProducts);
        List<String> productNames = new ArrayList<String>(uniqueNames);
        return productNames.get(new Random().nextInt(0, productNames.size()));
	}	

    public void confirmDeliverOrderByAPI(String orderID){
        OrderAPI orderAPI = new OrderAPI(loginInformation);
        orderAPI.confirmOrder(orderID);
        orderAPI.deliverOrder(orderID);
    }		
	
	public List<Integer> getRatingListByAPI() {
		return new APIProductReviews(loginInformation).getAllReviewJsonPath().getList("rate");
	}	
	
	public List<Date> getCreatedDateListByAPI() {
		List<String> rawList = new APIProductReviews(loginInformation).getAllReviewJsonPath().getList("reviewDate");
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
     * @param
     * @return
     * @throws Exception
     */
	public String tranlateSortText(String condition) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("product.review.filter." + condition);
	}
	
	public List<Integer> sortRatingRetrievedFromAPI(List<Integer> ratings, String condition) {
		if (condition.contentEquals("lowToHigh")) {
			Collections.sort(ratings);
			return ratings;
		}
		Collections.sort(ratings, Comparator.reverseOrder());
		return ratings;
	}    
	
	public List<Date> sortCreatedDate(List<Date> dates, String condition) {
		if (condition.contentEquals("oldToNew")) {
			Collections.sort(dates);
			return dates;
		}
		Collections.sort(dates, Comparator.reverseOrder());
		return dates;
	}    
	
	public List<Date> extractCreatedDateFromReview(List<List<String>> reviews) {
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
    		Assert.assertTrue(name.toLowerCase().contains(searchTerm.toLowerCase()), "The name is " + name);
    	}
    }	
    
	public String buyProductThenDeliverOrder(String product) throws Exception {
		/* Log into SF */
		loginSF();

		/* Buy product */
		sfHeader.searchWithFullName(product).clickSearchResult().waitTillLoaderDisappear();
		
		sfProductDetailPage.clickOnBuyNow()
		.clickOnContinue()
		.getFullNameInDeliveryInfoSection();
		
		new Checkout(driver).clickOnCompleteBtn().clickOnBackToMarket();

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
		
		String randomNumber = DataGenerator.randomNumberGeneratedFromEpochTime(10);
		int randomStar = DataGenerator.generatNumberInBound(1, 6);
		
		/* Log into SF */
		loginSF();
		
		/* Buy product */
		sfHeader.searchWithFullName(product).clickSearchResult().waitTillLoaderDisappear();
		
		sfProductDetailPage.clickOnBuyNow()
		.clickOnContinue()
		.getFullNameInDeliveryInfoSection();
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
	
	@BeforeClass
	public void logIntoDashboardByAPI() {
		loginInformation = new Login().setLoginInformation(sellerUsername, sellerPassword).getLoginInformation();
	}	
	
	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
	}

	@Test
	public void PR_00_PermissionToUseProductReviews() {
		
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
		loginDashboard();
		
		/* Check text at management screen */
		productReviewPage.navigate().verifyTextAtReviewManagementScreen();
	}    
    
	@Test
	public void PR_02_DisableProductReviews() throws Exception {
		
		String randomProduct = randomProduct();
		
		buyProductThenDeliverOrder(randomProduct);
		
		/* Log into dashboard */
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		loginDashboard();
		
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
	public void PR_04_HideProductReviews() {
		
		int reviewIndex = 0;
		
		/* Log into dashboard */
		loginDashboard();
		
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
	public void PR_05_ShowProductReviews() {
		
		int reviewIndex = 0;
		
		/* Log into dashboard */
		loginDashboard();
		
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
		
		String randomProduct = randomProduct();
		String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(10);
		int randomStar = new DataGenerator().generatNumberInBound(1, 6);
		
		String orderId = buyProductThenDeliverOrder(randomProduct);

		/* Log into dashboard */
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		loginDashboard();
		
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
	public void PR_07_NavigateToProductDetailOnSF() {

		/* Log into dashboard */
		loginDashboard();
		
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
	public void PR_08_SearchReviews() {
		
        String randomSearchProduct = randomSearchProduct();
        String searchTerm = randomSearchProduct.substring(0, randomSearchProduct.length()/2);
		
		/* Log into dashboard */
        loginDashboard();
		
		/* Absolute match */
		productReviewPage.navigate().inputSearchTerm(searchTerm);
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm);
		commonAction.refreshPage();
		
		/* Partly match */
		productReviewPage.inputSearchTerm(randomSearchProduct);
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm);
		commonAction.refreshPage();
		
		/* Ignore case */
		productReviewPage.inputSearchTerm(randomSearchProduct.toLowerCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), randomSearchProduct.toLowerCase());
		commonAction.refreshPage();
		
		productReviewPage.inputSearchTerm(searchTerm.toLowerCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm.toLowerCase());
		commonAction.refreshPage();
		
		productReviewPage.inputSearchTerm(randomSearchProduct.toUpperCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), randomSearchProduct.toUpperCase());
		commonAction.refreshPage();
		
		productReviewPage.inputSearchTerm(searchTerm.toUpperCase());
		verifyResultMatchSearchTerm(productReviewPage.getAllReviewTable(), searchTerm.toUpperCase());
	}	
	
	@Test
	public void PR_09_SortReviews() throws Exception {
		
		/* Log into dashboard */
		loginDashboard();
		
		/* Get reviews */
		productReviewPage.navigate();
		
		List<Integer> initialRating = getRatingListByAPI();
		
		/* Sort reviews by low to high rating */
		String sortCondition = "lowToHigh";
		
		List<Integer> expectedRating = sortRatingRetrievedFromAPI(initialRating, sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition));
		
		List<Integer> actualRating = extractRatingFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualRating, expectedRating);
		
		/* Sort reviews by high to low rating */
		sortCondition = "highToLow";
		
		expectedRating = sortRatingRetrievedFromAPI(initialRating, sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition));
		
		actualRating = extractRatingFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualRating, expectedRating);		
		
		/* Sort reviews by new to old created date */
		sortCondition = "newToOld";
		
		commonAction.refreshPage();
		homePage.waitTillSpinnerDisappear1();
		
		List<List<String>> initialReviews = productReviewPage.getAllReviewTable();
		
		List<Date> expectedCreatedDate = sortCreatedDate(extractCreatedDateFromReview(initialReviews), sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition));
		
		List<Date> actualCreatedDate = extractCreatedDateFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualCreatedDate, expectedCreatedDate);	
		
		/* Sort reviews by old to new created date */
		sortCondition = "oldToNew";
		
		expectedCreatedDate = sortCreatedDate(extractCreatedDateFromReview(initialReviews), sortCondition);
		
		productReviewPage.selectSortCondition(tranlateSortText(sortCondition));
		
		actualCreatedDate = extractCreatedDateFromReview(productReviewPage.getAllReviewTable());
		
		Assert.assertEquals(actualCreatedDate, expectedCreatedDate);	
		
	}	

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }
	
}
