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
import pages.buyerapp.account.membershipinfo.MembershipInfo;
import pages.buyerapp.login.LoginPage;
import pages.buyerapp.navigationbar.NavigationBar;
import pages.buyerapp.notificationpermission.NotificationPermission;
import pages.dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.jsonFileUtility;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;

public class MembershipInfoApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	pages.buyerapp.login.LoginPage loginPage;
	
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	
	UICommonMobile commonAction;
	DataGenerator generate;
	
	pages.dashboard.login.LoginPage loginDB;
	pages.dashboard.home.HomePage homePageWeb;
	
	api.dashboard.marketing.LoyaltyProgram loyaltyProgramAPI;
	SegmentAPI segmentAPI;
	
	LoginInformation loginInformation;
	
	String language = "ENG";

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	List<String> customerList;
	
	// Loyalty program info
	String tierName = "Rich Customers";
	String avatar = "membership.jpg";
	String description = "Test BH_4602 and BH_4603";
	String discountPercent = "50";
	String maximunDiscount = "100000";
	
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
		instantiatePageObjects();
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
		
		segmentAPI = new SegmentAPI(loginInformation);
		loyaltyProgramAPI = new api.dashboard.marketing.LoyaltyProgram(loginInformation);
		
//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
	}	

	public AppiumDriver launchApp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "10.10.2.100:5555"); //10.10.2.100:5555 RF8N20PY57D 
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.shop0017");
        capabilities.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");
        capabilities.setCapability("noReset", "false");
        
        String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}	

	public void loginDashboard() {
		loginDB.navigate()
        .performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		homePageWeb.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}		
	
	@Test
	public void MB_01_LoginWithNonMembershipAccount() throws Exception {
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		loginPage.performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		
		MembershipInfo membershipPage = accountTab.clickMembershipInfoSection();
		
		Assert.assertEquals(membershipPage.getMembershipIntroduction(), "Bạn chưa thỏa điều kiện chương trình Hội Viên");
	}
	
	@Test
	public void MB_02_LoginWithMembershipAccount() throws Exception {
		
		driverWeb = new InitWebdriver().getDriver("chrome", "no");
		loginDB = new pages.dashboard.login.LoginPage(driverWeb);
		homePageWeb = new pages.dashboard.home.HomePage(driverWeb);
		
		// Create customer segment
		Customers customerAPI = new Customers(loginInformation);
		customerAPI.createSegment();
		String segment = customerAPI.getSegmentName();
		int segmentId = customerAPI.getSegmentID();
		
		// Log into Dashboard
		loginDashboard();
		
		// Create loyalty program
		new LoyaltyProgram(driverWeb).navigate().clickCreateMembershipBtn()
		.createMembershipLevel(tierName, avatar, segment, description, discountPercent, maximunDiscount);
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		loginPage.performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		accountTab.verifyMemberShipLevel(tierName);
		
		MembershipInfo membershipPage = accountTab.clickMembershipInfoSection();
		
		Assert.assertEquals(membershipPage.getMembershipIntroduction(), "Bạn đang là thành viên %s".formatted(tierName));
		Assert.assertEquals(membershipPage.getMembershipDescription(), description);
		
		// Delete loyalty program
		loyaltyProgramAPI.deleteMembership(loyaltyProgramAPI.getMembershipIdByName(tierName));
		
		// Delete customer segment
		segmentAPI.deleteSegment(segmentId);
	}
}
