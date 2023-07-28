import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.customers.Customers;
import api.dashboard.customers.SegmentAPI;
import api.dashboard.login.Login;
import io.appium.java_client.AppiumDriver;
import pages.buyerapp.account.BuyerAccountPage;
import pages.buyerapp.login.LoginPage;
import pages.buyerapp.navigationbar.NavigationBar;
import pages.buyerapp.notificationpermission.NotificationPermission;
import pages.buyerapp.productDetail.BuyerProductDetailPage;
import pages.dashboard.products.productreviews.ProductReviews;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.jsonFileUtility;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;

public class ProductReviewApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	pages.buyerapp.login.LoginPage loginPage;

	BuyerAccountPage accountTab;
	NavigationBar navigationBar;

	UICommonMobile commonAction;
	DataGenerator generate;

	pages.dashboard.login.LoginPage loginDB;
	pages.dashboard.home.HomePage homePageWeb;
	ProductReviews productReviewPage;

	LoginInformation loginInformation;
	
	SegmentAPI segmentAPI;

	String language = "ENG";

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	List<String> customerList;

	JsonNode buyerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String BUYER_MAIL = buyerData.findValue("buyer").findValue("spareAccount").findValue("username").asText();
	String BUYER_PASSWORD = buyerData.findValue("buyer").findValue("spareAccount").findValue("password").asText();
	String BUYER_COUNTRY = buyerData.findValue("buyer").findValue("spareAccount").findValue("country").asText();

	@BeforeClass
	public void setUp() throws Exception {
		PropertiesUtil.setEnvironment("STAG");
		PropertiesUtil.setDBLanguage(language);

		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		loginInformation = new Login().setLoginInformation(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getLoginInformation();
		customerList = new Customers(loginInformation).getAllCustomerNames();
	}

	@BeforeMethod
	public void generateData() throws Exception {
//		instantiatePageObjects();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
		if (driverWeb != null) {
			new Screenshot().takeScreenshot(driverWeb);
			driverWeb.quit();
		}
	}

	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		commonAction = new UICommonMobile(driver);

//		segmentAPI = new SegmentAPI();

//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
	}

	public AppiumDriver launchApp() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("udid", "10.10.2.100:5555"); // 192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D
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
		loginDB = new pages.dashboard.login.LoginPage(driverWeb);
		homePageWeb = new pages.dashboard.home.HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().disableProductReviews();
		
		List<List<String>> table = productReviewPage.getReviewTable();
		
		instantiatePageObjects();
		
		navigationBar.tapOnSearchIcon().tapOnSearchBar().inputKeywordToSearch(table.get(0).get(0)).tapSearchSuggestion();
		
		Assert.assertFalse(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
	}

	@Test
	public void MB_02_EnableProductReviews() throws Exception {

		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new pages.dashboard.login.LoginPage(driverWeb);
		homePageWeb = new pages.dashboard.home.HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();

		List<List<String>> table = productReviewPage.getReviewTable();
		
		instantiatePageObjects();
		
		navigationBar.tapOnSearchIcon().tapOnSearchBar().inputKeywordToSearch(table.get(0).get(0)).tapSearchSuggestion();
		
		String[] review = new BuyerProductDetailPage(driver).getReview();

		Assert.assertEquals(review[0], table.get(0).get(2));
	}
	
	@Test
	public void MB_03_HideProductReviews() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new pages.dashboard.login.LoginPage(driverWeb);
		homePageWeb = new pages.dashboard.home.HomePage(driverWeb);
		
		loginDashboard();
		
		ProductReviews productReviewPage = new ProductReviews(driverWeb);
		productReviewPage.navigate().enableProductReviews();
		
		productReviewPage.disapproveReview(0);
		
		List<List<String>> table = productReviewPage.getReviewTable();
		
		instantiatePageObjects();
		
		navigationBar.tapOnSearchIcon().tapOnSearchBar().inputKeywordToSearch(table.get(0).get(0)).tapSearchSuggestion();
		
		Assert.assertTrue(new BuyerProductDetailPage(driver).isReviewTabDisplayed(), "Is review tab displayed");
		
		String[] review = new BuyerProductDetailPage(driver).getReview();

		Assert.assertNotEquals(review[0], table.get(0).get(2));
		
	}
	
	@Test
	public void MB_04_ShowProductReviews() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new pages.dashboard.login.LoginPage(driverWeb);
		homePageWeb = new pages.dashboard.home.HomePage(driverWeb);
		
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
	
}
