import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.appium.java_client.AppiumDriver;
import pages.buyerapp.LoginPage;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.NotificationPermission;
import pages.buyerapp.SignupPage;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;

public class LoginBuyerApp {

	AppiumDriver driver;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	LoginPage loginPage;
	UICommonMobile commonAction;
	DataGenerator generate;

	JsonNode sf = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String BUYER_MAIL_USERNAME = sf.findValue("buyer").findValue("mail").findValue("username").asText();
	String BUYER_MAIL_PASSWORD = sf.findValue("buyer").findValue("mail").findValue("password").asText();
	String BUYER_MAIL_COUNTRY = sf.findValue("buyer").findValue("mail").findValue("country").asText();

	@BeforeClass
	public void setUp() throws Exception {
		PropertiesUtil.setEnvironment("STAG");
		launchApp();
	}

	public void launchApp() throws Exception {
      String udid = "RF8N20PY57D";
//		String udid = "10.10.2.100:5555";
//		String udid = "192.168.2.43:5555";
//		String udid = "10.10.10.78:5555";
		String platformName = "Android";
		String appPackage = "com.mediastep.shop0017";
		String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
		String url = "http://127.0.0.1:4723/wd/hub";
		driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);

		new NotificationPermission(driver).clickAllowBtn();
	}

	@BeforeMethod
	public void generateData() throws Exception {
		generate = new DataGenerator();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		commonAction = new UICommonMobile(driver);
	}

	@Test
	public void Login_02_LoginWithFieldsLeftEmpty() throws InterruptedException {
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(BUYER_MAIL_COUNTRY, "", BUYER_MAIL_PASSWORD);
		Assert.assertFalse(loginPage.isLoginBtnEnabled());

		loginPage.performLogin(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, "");
		Assert.assertFalse(loginPage.isLoginBtnEnabled());

		loginPage.performLogin(BUYER_MAIL_COUNTRY, "", "");
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		commonAction.navigateBack();
	}

	@Test
	public void Login_03_LoginWithInvalidMailFormat() throws InterruptedException {
		navigationBar.tapOnAccountIcon().clickLoginBtn();

    	loginPage.clickUsername(); //Workaround to simulate a tap on username field
    	commonAction.hideKeyboard("android");
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, generate.generateString(10), generate.generateString(10));
		Assert.assertEquals(new SignupPage(driver).getUsernameError(), "Email không đúng"); 
		Assert.assertFalse(loginPage.isLoginBtnEnabled());		
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, generate.generateString(10) + "@", generate.generateString(10));
		Assert.assertEquals(new SignupPage(driver).getUsernameError(), "Email không đúng"); 
		Assert.assertFalse(loginPage.isLoginBtnEnabled());		
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, generate.generateString(10) + "@" + generate.generateString(5) + ".", generate.generateString(10));
		Assert.assertEquals(new SignupPage(driver).getUsernameError(), "Email không đúng"); 
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		commonAction.navigateBack();
	}
	
	@Test
	public void Login_04_LoginWithInvalidPhoneFormat() throws InterruptedException {
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		loginPage.clickUsername(); //Workaround to simulate a tap on username field
		commonAction.hideKeyboard("android");
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, generate.generateNumber(7), generate.generateString(10));
		Assert.assertEquals(new SignupPage(driver).getUsernameError(), "Điền từ 8 - 15 số"); 
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		loginPage.performLogin(BUYER_MAIL_COUNTRY, generate.generateNumber(16), generate.generateString(10));
		Assert.assertEquals(new SignupPage(driver).getUsernameError(), "Điền từ 8 - 15 số");
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		commonAction.navigateBack();
	}

//	@Test
	public void LoginSF_05_LoginWithCorrectCredentials() throws InterruptedException {
		loginPage = new LoginPage(driver);
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);

		navigationBar.tapOnAccountIcon().clickLoginBtn();
		loginPage.inputUsername(BUYER_MAIL_USERNAME).inputPassword(BUYER_MAIL_PASSWORD).clickLoginBtn();

		accountTab.clickLogoutBtn();
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}

}
