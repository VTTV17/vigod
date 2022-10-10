import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.signup.SignupPage;
import pages.gomua.headergomua.HeaderGoMua;
import utilities.UICommonAction;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import pages.Mailnesia;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupDashboard extends BaseTest {

	SignupPage signupPage;

	String randomNumber;
	String mail;
	String password;
	String referralCode;
	String country;
	String countryCode;
	String currency;
	String language;
	String storeName;
	String storeURL;
	String storePhone;
	String pickupAddress;
	String secondPickupAddress;
	String province;
	String district;
	String ward;
	String city;
	String zipCode;

	String INVALID_CODE_ERROR_VI = "Mã xác thực không đúng";
	String INVALID_CODE_ERROR_EN = "Incorrect confirmation code!";
	String USERNAME_EXIST_ERROR = "Email / số điện thoại đã tồn tại";
	String UPGRADENOW_MESSAGE_VI = "Xác nhận\nAdmin Staging - Nền tảng bán hàng Online & Offline chuyên nghiệp. Tạo website/ứng dụng bán hàng chỉ trong vài phút. Hỗ trợ kết nối các sàn TMĐT Shopee, Lazada, quản lý bán hàng đa kênh, quản lý danh sách khách hàng, tạo email quảng cáo, gửi thông báo cho khách hàng qua ứng dụng di động, tạo landing page ….\nNâng cấp ngay hôm nay để trải nghiệm thêm nhiều tính năng tuyệt vời từ Admin Staging.\nNâng cấp ngay";
	String UPGRADENOW_MESSAGE_EN = "Confirmation\nAdmin Staging - Online & Offline sales platform. Build your e-commerce Website/App in few minutes, connect multi-channel sales platform Shopee, Lazada, manage customer data, create promotional emails, send notifications to customers via mobile applications, create landing pages ….\nUpgrade today to experience more great features from Admin Staging.\nUpgrade Now";

	public void generateTestData() throws InterruptedException {
		randomNumber = generate.generateNumber(4);
		mail = "automation0-shop" + randomNumber + "@mailnesia.com";
		storePhone = "912345" + randomNumber;
		password = "fortesting!1";
		referralCode = "";
		country = "rd";
		countryCode = "+84";
		currency = "rd";
		language = "rd";
		storeName = "Automation Shop " + randomNumber;
		storeURL = "";
		pickupAddress = "12 Quang Trung";
		secondPickupAddress = "16 Wall Street";
		province = "rd";
		district = "rd";
		ward = "rd";
		city = "Cockney";
		zipCode = generate.generateNumber(6);
	}

	public String getVerificationCode(String username) throws InterruptedException, SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			Thread.sleep(10000);
			commonAction.openNewTab();
			commonAction.switchToWindow(1);
			verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
			commonAction.closeTab();
			commonAction.switchToWindow(0);
		} else {
			verificationCode = new InitConnection().getActivationKey(signupPage.countryCode + ":" + username);
		}
		return verificationCode;
	}

	public void setupShop(String username, String storeName, String url, String country, String currency,
			String language, String contact, String pickupAddress, String secondPickupAddress, String province,
			String district, String ward, String city, String zipCode) {
		signupPage.inputStoreName(storeName);
		if (url != "") {
			signupPage.inputStoreURL(url);
		}
		signupPage.selectCountryToSetUpShop(country).selectCurrency(currency).selectLanguage(language);
		if (!username.matches("\\d+")) {
			signupPage.inputStorePhone(contact);
		} else {
			signupPage.inputStoreMail(contact);
		}

		signupPage.inputPickupAddress(pickupAddress).selectProvince(province);

		if (!country.contentEquals("Vietnam")) {
			signupPage.inputSecondPickupAddress(secondPickupAddress).inputCity(city).inputZipCode(zipCode);
		} else {
			signupPage.selectDistrict(district).selectWard(ward);
		}
		signupPage.clickCompleteBtn();
	}

	public void verifyUpgradNowMessage() throws InterruptedException {
		String upgradeNowMessage;
		Thread.sleep(1000);
		if (new HomePage(driver).getDashboardLanguage().contentEquals("VIE")) {
			upgradeNowMessage = UPGRADENOW_MESSAGE_VI;
		} else {
			upgradeNowMessage = UPGRADENOW_MESSAGE_EN;
		}
		new HomePage(driver).verifyUpgradeNowMessage(upgradeNowMessage).completeVerify();
	}

	public void reLogintoShop(String country, String user, String password) throws InterruptedException {
		new LoginPage(driver).performLogin(country, user, password);
		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		signupPage = new SignupPage(driver);
		generateTestData();
	}

//	@Test
	public void SignUpForShopWithRandomData() throws SQLException, InterruptedException {

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//    @Test
	public void SignUpForForeignShopWithPhone() throws SQLException, InterruptedException {

		String country = "United Kingdom";
		String currency = "Pound Sterling - GBP(£)";
		String language = "Tiếng Anh";
		String pickupAddress = "12 HighWay Revenue";
		String secondPickupAddress = "16 Wall Street";
		String city = "East London";
		String province = "England";

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//    @Test
	public void SignUpForForeignShopWithEmail() throws SQLException, InterruptedException {

		String country = "United Kingdom";
		String currency = "Pound Sterling - GBP(£)";
		String language = "Tiếng Anh";
		String pickupAddress = "12 HighWay Revenue";
		String secondPickupAddress = "16 Wall Street";
		String city = "Cockney";
		String province = "England";

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//    @Test
	public void SignUpForVNShopWithPhone() throws SQLException, InterruptedException {

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		String pickupAddress = "12 Quang Trung";

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//    @Test
	public void SignUpForVNShopWithEmail() throws SQLException, InterruptedException {

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		String province = "Hồ Chí Minh";
		String district = "Quận 8";
		String ward = "Phường 2";

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

	@Test
	public void BH_4034_SignUpForPhoneAccountFromPromotionLink() throws SQLException, InterruptedException {

		String referralCode = "fromthompson";
		String domain = "abcdefgh";

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();

		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), referralCode.toUpperCase());
	}

	@Test
	public void BH_4036_SignUpForShopWithExistingPhoneAccount() throws SQLException, InterruptedException {

		JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
		storePhone = data.findValue("seller").findValue("phone").findValue("username").asText();
		password = data.findValue("seller").findValue("phone").findValue("password").asText();
		country = data.findValue("seller").findValue("phone").findValue("country").asText();

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, storePhone, password, referralCode);
		signupPage.verifyUsernameExistError(USERNAME_EXIST_ERROR).completeVerify();
		
		mail = data.findValue("seller").findValue("mail").findValue("username").asText();
		password = data.findValue("seller").findValue("mail").findValue("password").asText();
		country = data.findValue("seller").findValue("mail").findValue("country").asText();		

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, mail, password, referralCode);
		signupPage.verifyUsernameExistError(USERNAME_EXIST_ERROR).completeVerify();		
		
	}

	@Test
	public void BH_4038_ResendVerificationCodeToPhone() throws SQLException, InterruptedException {

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode);
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();
		signupPage.clickConfirmBtn();
		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		signupPage.clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

	@Test
	public void BH_4039_ResendVerificationCodeToEmail() throws SQLException, InterruptedException {

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode);
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();
		signupPage.clickConfirmBtn();
		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		signupPage.clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

	@Test
	public void BH_4054_ContinueSignupWizardAfterExitingSession() throws SQLException, InterruptedException {
	
		String username = mail;
	
		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		country = signupPage.country;
		signupPage.inputStoreName(storeName);
	
		// Exit current session
		driver.quit();
	
		// Re-login
		driver = new InitWebdriver().getDriver("chrome", "false");
		new LoginPage(driver).navigate().performLogin(country, username, password);
		new SignupPage(driver).inputStoreName(storeName);
	
	}

	@Test
	public void BH_5195_SignUpForShopWithURLInUpperCase() throws SQLException, InterruptedException {

		String username = mail;
		String contact = storePhone;

		Pattern p = Pattern.compile("[A-Za-z0-9]+");
		Matcher m = p.matcher(storeName + generate.generateString(10));
		while (m.find()) {
			storeURL += m.group();
		}

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
		Assert.assertEquals(new InitConnection().getStoreURL(storeName), storeURL.toLowerCase());
	}

	@Test
	public void BH_1363_SignUpForGoFreeAccountViaEmail() throws SQLException, InterruptedException {

		String referralCode = "fromthompson";
		String domain = "abcdefgh";

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);

		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), referralCode.toUpperCase());
	}
	
//	@Test
	public void BH_1277A_SignUpForGoFreeAccountViaPhone() throws SQLException, InterruptedException {
		
//		String referralCode = "frommark";
		String domain = "null";
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		
		String username = storePhone;
		String contact = mail;
		
		// Sign up
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		
		country = signupPage.country;
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
		
		// Verify Upgrade Now popup appears on the screen
		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).clickLogout();
		
		// Verify domain and referral code is configured as expected
		Assert.assertEquals(domain, new InitConnection().getStoreDomain(storeName));
		Assert.assertEquals(referralCode.toUpperCase(), new InitConnection().getStoreGiftCode(storeName));
	}
	
//	@Test
	public void BH_1277B_SignUpForGoFreeAccountViaPhone() throws SQLException, InterruptedException {
		
		String referralCode = "frommark";
		String domain = "abcdefgh";
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		
		String username = storePhone;
		String contact = mail;
		
		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		
		country = signupPage.country;
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Verify Upgrade Now popup appears on the screen
		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
		
		// Verify domain and referral code is configured as expected
		Assert.assertEquals(domain, new InitConnection().getStoreDomain(storeName));
		Assert.assertEquals(referralCode.toUpperCase(), new InitConnection().getStoreGiftCode(storeName));
	}

	@Test
	public void BH_1364_SignUpForShopUsingGomuaMailAccount() throws SQLException, InterruptedException {
		
		String domain = "gomua.vn";
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		String username = "gomua-seller"+ randomNumber +"@mailnesia.com";
		String contact = storePhone;
		String displayName  = "Gomua Seller " + randomNumber;
		storeName = displayName;
		
		// Signup in Gomua
		new HeaderGoMua(driver).navigateToGoMua()
		.clickCreateShop()
		.clickCreateGomuaAccountBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(username))
		.clickVerifyAndLoginBtn()
		.clickAgreeAndContiueBtn();
		commonAction.sleepInMiliSecond(1000);
		
		// Verify URL is shown as expected
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);
		
		// Login
		new LoginPage(driver).performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Verify upgrade now popup can be closed at home screen
		verifyUpgradNowMessage();
		new HomePage(driver).closeUpgradeNowPopUp();
		new HomePage(driver).skipIntroduction();
		new HomePage(driver).clickLogout();
		
		// Verify upgrade now popup does not appear at several screens
		new LoginPage(driver).performLogin(country, username, password);
		new HomePage(driver).waitTillSpinnerDisappear();
		verifyUpgradNowMessage();
		new HomePage(driver).closeUpgradeNowPopUp();
		
		new HomePage(driver).navigateToPage("Products", "All Products");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Orders", "Order List");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Settings");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Services");
		Assert.assertTrue(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		new HomePage(driver).clickUpgradeNow();
		
		// Verify domain is configured as expected
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
	}
	
	@Test
	public void BH_1365_SignUpForShopUsingGomuaPhoneAccount() throws SQLException, InterruptedException {
		
		String domain = "gomua.vn";
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String language = "Tiếng Việt";
		String username = storePhone;
		String contact = "gomua-seller"+ randomNumber +"@mailnesia.com";
		String displayName  = "Gomua Seller " + randomNumber;
		storeName = displayName;
		signupPage.countryCode = "+84"; // This is a temporary workaround. Solutions to the problem are being considered.
		
		// Signup in Gomua
		new HeaderGoMua(driver).navigateToGoMua()
		.clickCreateShop()
		.clickCreateGomuaAccountBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(username))
		.clickVerifyAndLoginBtn()
		.inputEmail(contact)
		.clickComplete()
		.clickAgreeAndContiueBtn();
		commonAction.sleepInMiliSecond(1000);
		
		// Verify URL is shown as expected
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);
		
		// Login
		new LoginPage(driver).performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Verify upgrade now popup can be closed at home screen
		verifyUpgradNowMessage();
		new HomePage(driver).closeUpgradeNowPopUp();
		new HomePage(driver).skipIntroduction();
		new HomePage(driver).clickLogout();
		
		// Verify upgrade now popup does not appear at several screens
		new LoginPage(driver).performLogin(country, username, password);
		new HomePage(driver).waitTillSpinnerDisappear();
		verifyUpgradNowMessage();
		new HomePage(driver).closeUpgradeNowPopUp();
		
		new HomePage(driver).navigateToPage("Products", "All Products");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Orders", "Order List");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Settings");
		Assert.assertFalse(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		
		new HomePage(driver).navigateToPage("Services");
		Assert.assertTrue(new HomePage(driver).checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(new HomePage(driver).checkPresenceOfCloseUpgradeNowPopUpIcon());
		new HomePage(driver).clickUpgradeNow();
		
		// Verify domain is configured as expected
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
	}	
	
}
