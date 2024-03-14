package app.android.GoSeller;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.customers.Customers;
import api.Seller.customers.SegmentAPI;
import api.Seller.login.Login;
import io.appium.java_client.AppiumDriver;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.account.membershipinfo.MembershipInfo;
import app.Buyer.login.LoginPage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.notificationpermission.NotificationPermission;
import web.Dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import utilities.account.AccountTest;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;
import web.Dashboard.home.HomePage;

public class MembershipInfoApp extends BaseTest {

	WebDriver driverWeb;
	LoginPage loginPage;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	
	LoginInformation loginInformation;
	
	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	String BUYER_MAIL;
	String BUYER_PASSWORD;
	String BUYER_COUNTRY;	
	
	// Loyalty program info
	String tierName = "Rich Customers";
	String avatar = "membership.jpg";
	String description = "Test BH_4602 and BH_4603";
	String discountPercent = "50";
	String maximunDiscount = "100000";
	
	@BeforeClass
	public void setUp() throws Exception {
		
		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		BUYER_MAIL = AccountTest.BUYER_ACCOUNT_THANG;
		BUYER_PASSWORD = AccountTest.BUYER_PASSWORD_THANG;
		BUYER_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		loginInformation = new Login().setLoginInformation(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getLoginInformation();
	}

	public void instantiatePageObjects() throws Exception {
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		
//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
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
		driverWeb = new InitWebdriver().getDriver(browser, headless);
		new web.Dashboard.login.LoginPage(driverWeb).navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driverWeb).waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}		
	
	@Test
	public void MB_01_LoginWithNonMembershipAccount() throws Exception {
		
		instantiatePageObjects();
		
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		
		Assert.assertEquals(accountTab.clickMembershipInfoSection().getMembershipIntroduction(), "Bạn chưa thỏa điều kiện chương trình Hội Viên");
	}
	
	@Test
	public void MB_02_LoginWithMembershipAccount() throws Exception {
		
		// Create customer segment using API
		Customers customerAPI = new Customers(loginInformation);
		customerAPI.createSegment();
		String segment = customerAPI.getSegmentName();
		int segmentId = customerAPI.getSegmentID();
		
		// Log into Dashboard
		loginDashboard();
		
		// Create loyalty program
		new LoyaltyProgram(driverWeb).navigate().clickCreateMembershipBtn()
		.createMembershipLevel(tierName, avatar, segment, description, discountPercent, maximunDiscount);
		
		instantiatePageObjects();
		
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(BUYER_COUNTRY, BUYER_MAIL, BUYER_PASSWORD);
		accountTab.verifyMemberShipLevel(tierName);
		
		MembershipInfo membershipPage = accountTab.clickMembershipInfoSection();
		
		Assert.assertEquals(membershipPage.getMembershipIntroduction(), "Bạn đang là thành viên %s".formatted(tierName));
		Assert.assertEquals(membershipPage.getMembershipDescription(), description);
		
		// Delete loyalty program
		api.Seller.marketing.LoyaltyProgram loyaltyProgramAPI = new api.Seller.marketing.LoyaltyProgram(loginInformation);
		loyaltyProgramAPI.deleteMembership(loyaltyProgramAPI.getMembershipIdByName(tierName));
		
		// Delete customer segment
		new SegmentAPI(loginInformation).deleteSegment(segmentId);
	}
}
