package app.android.Buyer;
import static utilities.links.Links.SF_URL_TIEN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import app.android.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.orders.OrderAPI;
import io.appium.java_client.AppiumDriver;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.account.myorders.orderdetail.OrderDetails;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.login.LoginPage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.notificationpermission.NotificationPermission;
import app.Buyer.productDetail.BuyerProductDetailPage;
import app.Buyer.search.BuyerSearchDetailPage;
import web.Dashboard.products.productreviews.ProductReviews;
import web.StoreFront.checkout.checkoutstep1.CheckOutStep1;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.header.HeaderSF;
import utilities.commons.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;
import web.Dashboard.home.HomePage;

public class ProductReviewApp extends BaseTest {

	WebDriver driverWeb;
	LoginPage loginPage;
	
	web.StoreFront.login.LoginPage sfLoginPage;
	HeaderSF sfHeader;
	
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;

	UICommonMobile commonAction;
	DataGenerator generate;

	web.Dashboard.login.LoginPage loginDB;
	HomePage homePageWeb;
	ProductReviews productReviewPage;
	BuyerGeneral buyerGeneral;

	LoginInformation loginInformation;
	
	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;
	String BUYER_USERNAME;
	String BUYER_PASSWORD;
	String BUYER_COUNTRY;

	List<String> customerList;

	List<String> allProducts;

	@BeforeClass
	public void setUp() throws Exception {
		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		BUYER_USERNAME = AccountTest.BUYER_ACCOUNT_THANG;
		BUYER_PASSWORD = AccountTest.BUYER_PASSWORD_THANG;
		BUYER_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		loginInformation = new Login().setLoginInformation(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getLoginInformation();
		allProducts = new api.Seller.products.APIAllProducts(loginInformation).getAllProductNames();
	}

	@BeforeMethod
	public void generateData() throws Exception {
//		instantiatePageObjects();
	}

	@AfterMethod(alwaysRun = true)
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		super.tearDown();
		if (driverWeb != null) {
			new Screenshot().takeScreenshot(driverWeb);
			driverWeb.quit();
		}
	}

	public String randomProductToBuy() {
		String[] pro1ducts = { "Fish Food", "Tetra Fish Food", "Dog Food", "Cat Food", "Bird Food" };
//		String[] pro1ducts = { "Kem đánh răng Raiya Junior trẻ em vị Cam", "Kem đánh răng keo ong Abipolis tuýp 100g Abipha", "Kem đánh răng 2080 Trẻ Em hương Dâu tuýp 80g Hàn Quốc", "Sensodyne Fresh Mint kem đánh răng Tube 100g Gsk" };
		return pro1ducts[new Random().nextInt(0, pro1ducts.length)];
	}		
	
	/**
	 * @return a random product out of all product present in product preview management
	 */
	public String randomSearchProduct() {
        List<String> allProducts = new api.Seller.products.ProductReviews(loginInformation).getProductNameList();
        Set<String> uniqueNames = new HashSet<String>(allProducts);
        List<String> productNames = new ArrayList<String>(uniqueNames);
        return productNames.get(new Random().nextInt(0, productNames.size()));
	}		
	
	/**
	 * @return a random product out of all products available in the store
	 */
	public String randomProduct() {
		return allProducts.get(new Random().nextInt(0, allProducts.size()));
	}		

	/**
	 * Logs into SF and change user language
	 */
	public void loginSF() {
		sfLoginPage = new web.StoreFront.login.LoginPage(driverWeb);
		sfLoginPage.navigate(SF_URL_TIEN).performLogin(BUYER_COUNTRY, BUYER_USERNAME, BUYER_PASSWORD);
		sfHeader = new HeaderSF(driverWeb);
		sfHeader.waitTillLoaderDisappear();
		sfHeader.clickUserInfoIcon().changeLanguage(language);
	}		

    public void confirmDeliverOrderByAPI(String orderID){
        OrderAPI orderAPI = new OrderAPI(loginInformation);
        orderAPI.confirmOrder(orderID);
        orderAPI.deliverOrder(orderID);
    }		
	
	public String buyProductThenDeliverOrder(String product) throws Exception {
		/* Log into SF */
		loginSF();

		/* Buy product */
		sfHeader.searchWithFullName(product).clickSearchResult().waitTillLoaderDisappear();
		
		new ProductDetailPage(driverWeb).clickOnBuyNow()
		.clickOnContinue()
		.getFullName();
		new CheckOutStep1(driverWeb).selectPaymentMethod("COD")
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
	
	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		commonAction = new UICommonMobile(driver);
		buyerGeneral = new BuyerGeneral(driver);

//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
	}

	public AppiumDriver launchApp() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("udid", "RF8N20PY57D"); // 192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("appPackage", "com.mediastep.shop0017");
		capabilities.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");
		capabilities.setCapability("noReset", "false");

		String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}

	public void loginDashboard() {
		loginDB.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		homePageWeb.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}
	
	@Test
	public void MB_01_DisableProductReviews() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new web.Dashboard.login.LoginPage(driverWeb);
		homePageWeb = new HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().disableProductReviews();
		
		instantiatePageObjects();
		
		BuyerSearchDetailPage searchPage = navigationBar.tapOnSearchIcon().tapOnSearchBar();
		for(int i=0; i<3; i++) {
			searchPage.inputKeywordToSearch(randomProduct()).tapSearchSuggestion();
			Assert.assertFalse(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
			commonAction.navigateBack();
		}
	}

	@Test
	public void MB_02_EnableProductReviews() throws Exception {

		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new web.Dashboard.login.LoginPage(driverWeb);
		homePageWeb = new HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();

		instantiatePageObjects();
		
		BuyerSearchDetailPage searchPage = navigationBar.tapOnSearchIcon().tapOnSearchBar();
		for(int i=0; i<3; i++) {
			searchPage.inputKeywordToSearch(randomProduct()).tapSearchSuggestion();
			Assert.assertTrue(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
			commonAction.navigateBack();
		}
	}
	
	@Test
	public void MB_03_HideProductReviews() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new web.Dashboard.login.LoginPage(driverWeb);
		homePageWeb = new HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();
		
		productReviewPage.disapproveReview(0);
		
		List<List<String>> table = productReviewPage.getReviewTable();
		
		instantiatePageObjects();
		
		navigationBar.tapOnSearchIcon().tapOnSearchBar().inputKeywordToSearch(table.get(0).get(0)).tapSearchSuggestion();
		
		Assert.assertTrue(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
		
		String[] review = new BuyerProductDetailPage(driver).getReview();

		if (review != null) {
			Assert.assertNotEquals(review[0], table.get(0).get(2));
		}
	}
	
	@Test
	public void MB_04_ShowProductReviews() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new web.Dashboard.login.LoginPage(driverWeb);
		homePageWeb = new HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();
		
		productReviewPage.approveReview(0);
		
		List<List<String>> table = productReviewPage.getReviewTable();
		
		instantiatePageObjects();
		
		navigationBar.tapOnSearchIcon().tapOnSearchBar().inputKeywordToSearch(table.get(0).get(0)).tapSearchSuggestion();
		
		Assert.assertTrue(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
		
		String[] review = new BuyerProductDetailPage(driver).getReview();
		
		Assert.assertEquals(review[0], table.get(0).get(2));
		
	}
	
	@Test
	public void MB_05_OnlyOneReviewEachOrder() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new web.Dashboard.login.LoginPage(driverWeb);
		homePageWeb = new HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();
		
		String randomProduct = randomProductToBuy();
		
		String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(10);
		int randomStar = new DataGenerator().generatNumberInBound(1, 6);
		
		String orderId = buyProductThenDeliverOrder(randomProduct);
		
		instantiatePageObjects();
		
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(BUYER_COUNTRY, BUYER_USERNAME, BUYER_PASSWORD);
		
		accountTab.clickMyOrdersSection().clickShippedTab().clickOrder().clickWriteReviewBtn().leaveReview(randomStar, "So good " + randomNumber, "Absolutely love the product " + randomNumber);
		
		buyerGeneral.getToastMessage();
		
		Assert.assertEquals(new OrderDetails(driver).getOrderId().split(": ")[1], orderId);
		Assert.assertFalse(new OrderDetails(driver).isWriteReviewBtnDisplayed());
	}
	
}
