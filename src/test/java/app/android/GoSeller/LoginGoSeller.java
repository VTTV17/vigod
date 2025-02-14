package app.android.GoSeller;
import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;
import static utilities.account.AccountTest.STAFF_VN_PASSWORD;
import static utilities.account.AccountTest.STAFF_VN_USERNAME;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import io.appium.java_client.AppiumDriver;
import utilities.account.AccountTest;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.driver.InitAndroidDriver;
import utilities.enums.Domain;
import utilities.screenshot.Screenshot;


public class LoginGoSeller extends BaseTest {

	AppiumDriver driver;
	LoginPage loginPage;
	HomePage homePage;
	
    @BeforeMethod
    public void launchApp() {
    	driver = new InitAndroidDriver().getSellerDriver("R5CW81WLFPT");
    	new UICommonMobile(driver).waitSplashScreenLoaded();
    }

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
	}	  
    
    @Test
    public void LoginGSA_01_LoginWithEmptyCredentials() {
    	loginPage = new LoginPage(driver);
    	
    	//Empty username and password
    	loginPage.inputUsername("").inputPassword("").clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty username
    	loginPage.inputUsername("").inputPassword(DataGenerator.randomValidPassword()).clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty password
    	loginPage.inputUsername(DataGenerator.randomCorrectFormatEmail()).inputPassword("").clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    }
    
    //TODO: From now on, GoSELLER won't validate phone length when logging
    
    @Test
    public void LoginGSA_03_LoginWithInvalidMailFormat() {
    	loginPage = new LoginPage(driver);
    	
    	//TODO: Workaround to simulate a tap on username field
    	loginPage.clickUsername(); 
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	// Mail does not have symbol @
    	loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), DataGenerator.randomValidPassword());
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    	
    	// Mail does not have suffix '.<>'. Eg. '.com'
    	loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), DataGenerator.randomValidPassword());
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    	
    	loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(), DataGenerator.randomValidPassword());
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    }

    @Test
    public void LoginGSA_04_LoginWithIncorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	//TODO: On first attempts, the screen always gets refreshed
    	loginPage.performLogin(DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
    	Thread.sleep(1000);
    	
    	// Incorrect mail account
    	loginPage.performLogin(DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Incorrect phone account
    	loginPage.performLogin(DataGenerator.randomPhone(), DataGenerator.randomValidPassword());
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    }    
    
    @Test
    public void LoginGSA_05_LoginWithCorrectCredentials() {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
		String mailCountry, mailUsername, mailPassword;
		String phoneCountry, phoneUsername, phonePassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailCountry = ADMIN_COUNTRY_TIEN;
			mailUsername = ADMIN_USERNAME_TIEN;
			mailPassword = ADMIN_PASSWORD_TIEN;
			phoneCountry = ADMIN_COUNTRY_TIEN;
			phoneUsername = ADMIN_SHOP_VI_USERNAME;
			phonePassword = ADMIN_SHOP_VI_PASSWORD;
		} else {
			mailCountry = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			mailUsername = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			mailPassword = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
			phoneCountry = AccountTest.ADMIN_PHONE_BIZ_COUNTRY;
			phoneUsername = AccountTest.ADMIN_PHONE_BIZ_USERNAME;
			phonePassword = AccountTest.ADMIN_PHONE_BIZ_PASSWORD;			
		}
    	
    	loginPage.performLogin(mailCountry, mailUsername, mailPassword);
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    	
    	loginPage.performLogin(phoneCountry, phoneUsername, phonePassword);
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    }
    
    @Test
    public void LoginGSA_06_StaffLogin() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
		String mailUsername, mailPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			mailUsername = STAFF_VN_USERNAME;
			mailPassword = STAFF_VN_PASSWORD;
		} else {
			mailUsername = AccountTest.STAFF_BIZ_USERNAME;
			mailPassword = AccountTest.STAFF_BIZ_PASSWORD;
		}
    	
		//TODO: On first attempts, the screen always gets refreshed
    	loginPage.clickStaffTab().performLogin(mailUsername, DataGenerator.randomValidPassword());
    	Thread.sleep(1000);
    	
    	// Incorrect credentials
    	loginPage.clickStaffTab().performLogin(mailUsername, DataGenerator.randomValidPassword());
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Correct credentials
    	loginPage.clickStaffTab().performLogin(mailUsername, mailPassword);
    	loginPage.clickAvailableShop();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    }
    
}
