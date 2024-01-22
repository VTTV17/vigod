package web.StoreFront;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import web.BaseTest;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.CustomerDetails;
import web.Dashboard.home.HomePage;
import web.GoMua.headergomua.HeaderGoMua;
import web.GoMua.logingomua.LoginGoMua;
import web.GoMua.myprofile.MyProfileGoMua;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAddress;
import web.StoreFront.userprofile.MyAccount.MyAccount;
import web.StoreFront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.utils.jsonFileUtility;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;

import static utilities.links.Links.*;

public class SignupStorefront extends BaseTest {
	
	final static Logger logger = LogManager.getLogger(SignupStorefront.class);

	SignupPage signupPage;

	String username;
	String mail;
	String phone;
	String password;
	String country;
	String countryCode;
	String displayName;
	String birthday;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	JsonNode data1 = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String STORE_USERNAME = data.findValue("seller").findValue("mail").findValue("username").asText();
	String STORE_PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
	String STORE_COUNTRY = data.findValue("seller").findValue("mail").findValue("country").asText();
	String BUYER_MAIL_USERNAME = data1.findValue("buyer").findValue("mail").findValue("username").asText();
	String BUYER_MAIL_PASSWORD = data1.findValue("buyer").findValue("mail").findValue("password").asText();
	String BUYER_MAIL_COUNTRY = data1.findValue("buyer").findValue("mail").findValue("country").asText();
	String BUYER_PHONE_USERNAME = data1.findValue("buyer").findValue("phone").findValue("username").asText();
	String BUYER_PHONE_PASSWORD = data1.findValue("buyer").findValue("phone").findValue("password").asText();
	String BUYER_PHONE_COUNTRY = data1.findValue("buyer").findValue("phone").findValue("country").asText();	
	
	// This function generate test data for each test case
	public void generateTestData() {
		country = processCountry();
		countryCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country);
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		password = "fortesting!1";
		displayName = "Auto Buyer " + phone;
		birthday = "21/02/1990";
	}
	
	/**
	 * 
	 * @param username
	 * @return a verification code needed for sign-up procedure. It works for both phone and email account
	 * @throws SQLException
	 */
	public String getVerificationCode(String username) throws SQLException {
		if (!username.matches("\\d+")) {
			return new Mailnesia(driver).navigateToMailAndGetVerifyCode(username);
		}
		return new InitConnection().getActivationKey(countryCode + ":" + username);
	}
	
	/**
	 * @return a random country
	 */
	public String processCountry() {
		String country ="";
		List<String> countries = generate.getCountryList();
		country = countries.get(new Random().nextInt(0, countries.size()));
		return country;
	}
	
	// This function checks if an email is sent to the user saying the user has signed up for an account successfully
	public void verifyEmailUponSuccessfulSignup(String username) {
		String language = new GeneralSF(driver).getDisplayLanguage();
		String title = commonAction.getPageTitle();
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		String expectedVerificationCodeMessage;
		String expectedSuccessfulSignupMessage;
		if (language.contentEquals("vi")) {
			expectedSuccessfulSignupMessage = signupPage.SUCCESSFUL_SIGNUP_MESSAGE_VI.formatted(title);
			expectedVerificationCodeMessage = signupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		} else {
			expectedSuccessfulSignupMessage = signupPage.SUCCESSFUL_SIGNUP_MESSAGE_EN.formatted(title);
			expectedVerificationCodeMessage = signupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		}
		String [][] mailContent = new Mailnesia(driver).navigate(username).getListOfEmailHeaders();
		Assert.assertEquals(mailContent[0][3], expectedSuccessfulSignupMessage);
		Assert.assertTrue(mailContent[1][3].contains(expectedVerificationCodeMessage));
	}

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}	
	
	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
		generateTestData();
	}

	@Test
	public void SignupSF_01_CheckTranslation() throws Exception {
		
		/* Set value for some variables */
		String username = mail;
		
		/* Sign up */
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language)
        .clickUserInfoIcon()
        .clickSignupIcon();
		signupPage.verifyTextAtSignupScreen();
		signupPage.onlyFillOutSignupForm(country, username, password, displayName, birthday)
		.verifyTextAtVerificationCodeScreen(username);
	}		
	
	@Test
	public void SignupSF_02_SignUpWithInvalidCredential() throws Exception {
		String error = "";
		
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyUsername");
		signupPage.navigate().fillOutSignupForm(country, "", password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(error).completeVerify();

		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyPassword");
		signupPage.navigate().fillOutSignupForm(country, phone, "", displayName, birthday);
		signupPage.verifyPasswordError(error).completeVerify();

		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyDisplayName");
		signupPage.navigate().fillOutSignupForm(country, phone, password, "", birthday);
		signupPage.verifyDisplayNameError(error).completeVerify();		
		
		mail = "automation_mail.com";
		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.invalidUsernameFormat");
		signupPage.navigate().fillOutSignupForm(country, mail, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(error).completeVerify();

		phone = "3454567";
		signupPage.navigate().fillOutSignupForm(country, phone, password, displayName, birthday);
		signupPage.verifyEmailOrPhoneNumberError(error).completeVerify();
	}

	@Test
	public void SignupSF_03_ResendVerificationCodeToEmail() throws SQLException {
		
		username = mail;
		
		// Signup
		signupPage.navigate().fillOutSignupForm(country, username, password, displayName, birthday);

		// Verify if new code has been sent to users when they click on Resend button
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode).clickResendOTP();
		
		if (!username.matches("\\d+")) { //If that's a mail account, wait for 8s
			commonAction.sleepInMiliSecond(8000); 
		}
		
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode).clickConfirmBtn();
		
		Assert.assertNotEquals(firstCode, resentCode, "New verification code");
		
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn(); //If that's a phone account, input email info
		}
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);
	}

	@Test
	public void SignupSF_04_ResendVerificationCodeToPhone() throws SQLException {

		username = phone;

		// Signup
		signupPage.navigate().fillOutSignupForm(country, username, password, displayName, birthday);

		// Verify if new code has been sent to users when they click on Resend button
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode).clickResendOTP();
		
		if (!username.matches("\\d+")) { //If that's a mail account, wait for 8s
			commonAction.sleepInMiliSecond(8000); 
		}
		
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode).clickConfirmBtn();
		
		Assert.assertNotEquals(firstCode, resentCode, "New verification code");
		
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn(); //If that's a phone account, input email info
		}
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);
	}

	@Test
	public void SignupSF_05_SignUpWithUsedEmailAccount() throws Exception {
		
		String error = "";
		
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.mailExists");
		signupPage.fillOutSignupForm(BUYER_MAIL_COUNTRY, BUYER_MAIL_USERNAME, BUYER_MAIL_PASSWORD, displayName, birthday)
		.verifyUsernameExistError(error).completeVerify();
	}

	@Test
	public void SignupSF_06_SignUpWithUsedPhoneAccount() throws Exception {
		
		String error = "";
		
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.phoneExists");
		signupPage.fillOutSignupForm(BUYER_PHONE_COUNTRY, BUYER_PHONE_USERNAME, BUYER_PHONE_PASSWORD, displayName, birthday)
		.verifyUsernameExistError(error).completeVerify();
	}

	@Test
	public void SignupSF_07_SignupWithPhone() throws SQLException {
		username = phone;
		
		// Signup
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);

		// Verify user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new MyAccount(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);
		
		// Log into Dashboard
		web.Dashboard.login.LoginPage dashboard = new web.Dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear().selectLanguage("ENG");
		
		// Verify user info in Dashboard
		new AllCustomers(driver).navigate().selectBranch("None Branch").clickUser(displayName);
		
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			signupPage.navigate();
			verifyEmailUponSuccessfulSignup(username);			
		}
	}
	
	@Test
	public void SignupWithPhone() throws SQLException {
		username = phone; //Random number of 10 digits;
		
		country = "Vietnam";
		countryCode = generate.getPhoneCode(country);
		
		// Signup
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon();
		
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}
	}
	
	@Test
	public void SignupWithMail() throws SQLException {
		
		username = mail;
		country = "Vietnam";
		countryCode = generate.getPhoneCode(country);
		
		// Signup
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon();
		
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}
		
		// Logout
//		new HeaderSF(driver).clickUserInfoIcon().clickLogout();
		
		// Re-login with new password
//		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);
		
		// Verify user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		new MyAccount(driver).inputPhoneNumber(phone).clickOnSaveButton();
		commonAction.sleepInMiliSecond(2000);
	}

	@Test
	public void SignupSF_08_SignupWithEmail() throws SQLException {
		username = mail;
		
		// Signup
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);

		// Verify user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		Assert.assertEquals(new MyAccount(driver).getDisplayName(), displayName);
		Assert.assertEquals(new MyAccount(driver).getEmail(), mail);
		Assert.assertEquals(new MyAccount(driver).getBirthday(), birthday);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new MyAccount(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		new UserProfileInfo(driver).clickMyAddressSection();
		Assert.assertEquals(new MyAddress(driver).getCountry(), country);
		
		// Log into Dashboard
		web.Dashboard.login.LoginPage dashboard = new web.Dashboard.login.LoginPage(driver);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driver).waitTillSpinnerDisappear().selectLanguage("ENG");
		
		// Verify user info in Dashboard
		new AllCustomers(driver).navigate().selectBranch("None Branch").clickUser(displayName);
		
		Assert.assertEquals(new CustomerDetails(driver).getEmail(), mail);
		if (username.matches("\\d+")) {
			Assert.assertEquals(new CustomerDetails(driver).getPhoneNumber(), countryCode+":"+username);
		}
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			signupPage.navigate();
			verifyEmailUponSuccessfulSignup(username);			
		}
	}

	@Test
	public void SignupSF_09_SignupForEmailWithWrongVerificationCode() throws Exception {
		username = phone;
		
		signupPage.navigate();
		new HeaderSF(driver).clickUserInfoIcon()
		.changeLanguage(language);
		
		// Signup
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday);
		
		// Get verification code
		String code = getVerificationCode(username);
		
		// Input wrong verification code
		String error = PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.wrongVerificationCode");
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1)).clickConfirmBtn();
		signupPage.verifyVerificationCodeError(error).completeVerify();
		
		// Input correct verification code
		signupPage.inputVerificationCode(code).clickConfirmBtn();
		
		// Input mail info if this is a phone account
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();
		
	}

	public void BH_1288_LogIntoGomuaWithAccountCreatedOnStorefront() throws SQLException {
		
		boolean mailProvided = false;
		boolean birthdayProvided = false;
		username = phone;
		country = "Vietnam";
		birthday = (birthdayProvided) ? birthday : "";
		
		// Signup
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		if (username.matches("\\d+")) { // Check if this is a phone account
			if (mailProvided) { // Decide whether to provide an email account or not
				signupPage.inputEmail(mail).clickCompleteBtn();
			} else {
				signupPage.clickLater();
			}
		}
			
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		// Re-login with new password
		new LoginPage(driver).navigate(SF_URL_TIEN).performLogin(country, username, password);

		// Get user info in SF
		new HeaderSF(driver)
		.clickUserInfoIcon()
		.clickUserProfile()
		.clickMyAccountSection();
		
		String SF_DisplayName = new MyAccount(driver).getDisplayName();
		String SF_Gender = new MyAccount(driver).getGender();
		String SF_Email = new MyAccount(driver).getEmail();
		String SF_Birthday = new MyAccount(driver).getBirthday();
		String SF_Phone = (username.matches("\\d+")) ? new MyAccount(driver).getPhoneNumber() : "";
		
		// Log into Gomua
		new HeaderGoMua(driver).navigateToGoMua().clickOnLogInBTN();
		new LoginGoMua(driver).loginWithUserName(username, password);
		
		// Get user info in Gomua
		new HeaderGoMua(driver).goToMyProfile();
		new MyProfileGoMua(driver).clickOnEditProfile();
		
		String Gomua_DisplayName = new MyProfileGoMua(driver).getDisplayName();
		String Gomua_Email = new MyProfileGoMua(driver).getEmail();
		String Gomua_Phone = new MyProfileGoMua(driver).getPhoneNumber();
		String Gomua_Gender = new MyProfileGoMua(driver).getGender();
		String Gomua_Birthday = new MyProfileGoMua(driver).getBirthday();
		
		// Format birthday as dd/mm/yyyy
		String formattedBirthday = birthday;
		if (birthdayProvided) {
			Matcher m = Pattern.compile("\\d+").matcher(Gomua_Birthday);
			List<String> code=new ArrayList<String>(); 
			while (m.find()) {
				code.add(m.group());
			}
			formattedBirthday = String.join("/", code.get(2), code.get(1), code.get(0));
		}
		
		// Verify user info in SF and Gomua match
		Assert.assertEquals(Gomua_DisplayName, SF_DisplayName);
		Assert.assertEquals(Gomua_Gender, SF_Gender);
		Assert.assertEquals(Gomua_Email, SF_Email);
		if (username.matches("\\d+")) {
			Assert.assertEquals(Gomua_Phone.replace(" ", ":"), SF_Phone);
		}
		Assert.assertEquals(formattedBirthday, SF_Birthday);
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	
	
}
