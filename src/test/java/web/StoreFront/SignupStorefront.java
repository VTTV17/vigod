package web.StoreFront;

import static utilities.links.Links.SF_URL_TIEN;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.thirdparty.Mailnesia;
import web.GoMua.headergomua.HeaderGoMua;
import web.GoMua.logingomua.LoginGoMua;
import web.GoMua.myprofile.MyProfileGoMua;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAccount.MyAccount;

public class SignupStorefront extends BaseTest {
	
	SignupPage signupPage;

	String username;
	String mail;
	String phone;
	String password;
	String country;
	String countryCode;
	String displayName;
	String birthday;

	// This function generate test data for each test case
	public void generateTestData() {
		country = processCountry();
		countryCode = DataGenerator.getPhoneCode(country);
		phone = DataGenerator.randomPhoneByCountry(country);
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
		List<String> countries = DataGenerator.getCountryList();
		return countries.get(new Random().nextInt(0, countries.size()));
	}
	
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver);
		commonAction = new UICommonAction(driver);
	}	
	
	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
		generateTestData();
	}	
	
	public void BH_1288_LogIntoGomuaWithAccountCreatedOnStorefront() throws SQLException {
		
		boolean mailProvided = false;
		boolean birthdayProvided = false;
		username = phone;
		country = "Vietnam";
		birthday = (birthdayProvided) ? birthday : "";
		
		// Signup
		new GeneralSF(driver).navigateToURL(SF_URL_TIEN);
		
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
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
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
