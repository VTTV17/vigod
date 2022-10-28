import org.apache.poi.ss.usermodel.Sheet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.settings.plans.PlansPage;
import pages.dashboard.signup.SignupPage;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.logingomua.LoginGoMua;
import pages.gomua.signup.SignupGomua;
import pages.storefront.header.HeaderSF;
import utilities.UICommonAction;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.excel.Excel;
import pages.InternalTool;
import pages.Mailnesia;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
	String signupLanguage;
	String storeLanguage;
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
		randomNumber = generate.generateNumber(2);
		storePhone = "81234567" + randomNumber;
		mail = "automation0-shop" + storePhone + "@mailnesia.com";
		password = "fortesting!1";
		referralCode = "";
		country = "rd";
		countryCode = "+84";
		currency = "rd";
		signupLanguage = "Vietnamese";
		storeLanguage = "rd";
		storeName = "Automation Shop " + storePhone;
		storeURL = "";
		pickupAddress = "12 Quang Trung";
		secondPickupAddress = "16 Wall Street";
		province = "rd";
		district = "rd";
		ward = "rd";
		city = "Cockney";
		zipCode = generate.generateNumber(6);
	}

	public String getVerificationCode(String username) throws SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
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
	
	public String getResetCode(String username) throws SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			commonAction.openNewTab();
			commonAction.switchToWindow(1);
			verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
			commonAction.closeTab();
			commonAction.switchToWindow(0);
		} else {
			verificationCode = new InitConnection().getResetKey(signupPage.countryCode + ":" + username);
		}
		return verificationCode;
	}

	public void setupShop(String username, String storeName, String url, String country, String currency,
			String storeLanguage, String contact, String pickupAddress, String secondPickupAddress, String province,
			String district, String ward, String city, String zipCode) {
		signupPage.inputStoreName(storeName);
		if (url != "") {
			signupPage.inputStoreURL(url);
		}
		if (country.length()>0) {
			signupPage.selectCountryToSetUpShop(country);
		}
		if (currency.length()>0) {
			signupPage.selectCurrency(currency);
		}
		if (storeLanguage.length()>0) {
			signupPage.selectLanguage(storeLanguage);
		}
		
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

	public void verifyUpgradNowMessage() {
		String upgradeNowMessage;
		if (new HomePage(driver).getDashboardLanguage().contentEquals("VIE")) {
			upgradeNowMessage = UPGRADENOW_MESSAGE_VI;
		} else {
			upgradeNowMessage = UPGRADENOW_MESSAGE_EN;
		}
		new HomePage(driver).verifyUpgradeNowMessage(upgradeNowMessage).completeVerify();
	}

	public void reLogintoShop(String country, String user, String password) {
		new LoginPage(driver).performLogin(country, user, password);
		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();
	}

	// This function checks if an email is sent to the user saying the user has signed up for an account successfully
	public void verifyEmailUponSuccessfulSignup(String username) {
		commonAction.sleepInMiliSecond(3000);
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		
		String expectedWelcomeMessage;
		String expectedVerificationCodeMessage;
		String expectedSuccessfulSignupMessage;
		if (signupLanguage.contentEquals("Vietnamese")) {
			expectedWelcomeMessage = "Chào mừng bạn đến với GoSell";
			expectedSuccessfulSignupMessage = "Đăng ký thành công tài khoản GoSell";
			expectedVerificationCodeMessage = "là mã xác minh tài khoản GoSell của bạn";
		} else {
			expectedWelcomeMessage = "Welcome to GoSell";
			expectedSuccessfulSignupMessage = "Successful GoSell registration";
			expectedVerificationCodeMessage = "is your GoSell's verification code";
		}
		
		String [][] mailContent = new Mailnesia(driver).navigate(username).getListOfEmailHeaders();
		
		commonAction.closeTab();
		commonAction.switchToWindow(0);
		
		Assert.assertEquals(mailContent[0][3], expectedWelcomeMessage);
		Assert.assertEquals(mailContent[1][3], expectedSuccessfulSignupMessage);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(expectedVerificationCodeMessage));
	}	
	
	// This function returns a list of menus we will navigate to
	public List<String> getParentMenuList() {
        Excel excel = new Excel();
        Sheet planPermissionSheet = null;
		try {
			planPermissionSheet = excel.getSheet(new HomePage(driver).planPermissionFileName, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
        List<String> menuList = new ArrayList<>();
        
        int lastRowIndex = planPermissionSheet.getLastRowNum();
        for (int row = 1; row <= lastRowIndex; row++) {
            int packageColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), "GoFree");
            int menuColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), "MenuItem");
            String permission = planPermissionSheet.getRow(row).getCell(packageColIndex).getStringCellValue();
            
            if (!permission.contentEquals("D")) continue;
            String menuItem = planPermissionSheet.getRow(row).getCell(menuColIndex).getStringCellValue();
            
            String parentMenu = menuItem.split("-")[0];
            if (parentMenu.contains("GoMua") || parentMenu.contains("Lazada")) {
                if (!currency.contains("VND")) continue; // If shop does not use VND, remove this menu item from the list
                if (!country.contentEquals("Vietnam")) continue; // If shop is not in VN, remove this menu item from the list
            }
            menuList.add(parentMenu);
        }
		return new ArrayList<>(new HashSet<>(menuList)); // Duplicated values is filtered out
	}

	// This function check if Upgrade Now pop-up appears at 3 random screens
	public void verifyUpgradNowMessageAppearEverywhere() {
		List<String> menus = getParentMenuList();
		for (int i=0; i<3; i++) {
        	String randomMenu = menus.get(new Random().nextInt(menus.size()));
    		new HomePage(driver).navigateToPage(randomMenu);
    		new HomePage(driver).clickUpgradeNow();	
		}
	}	
	
	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		signupPage = new SignupPage(driver);
		generateTestData();
	}

//	@Test
	public void SignUpForShopWithRandomData() throws SQLException {

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//	@Test
	public void BH_4034_SignUpForPhoneAccountFromPromotionLink() throws SQLException {

		String referralCode = "fromthompson";
		String domain = "abcdefgh";

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";

		String username = storePhone;
		String contact = mail;

		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		verifyUpgradNowMessage();
		new HomePage(driver).clickUpgradeNow();

		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), referralCode.toUpperCase());
	}

//	@Test
	public void BH_4036_SignUpForShopWithExistingPhoneAccount() throws SQLException {

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

//	@Test
	public void BH_4038_ResendVerificationCodeToPhone() throws SQLException {

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
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

//	@Test
	public void BH_4039_ResendVerificationCodeToEmail() throws SQLException {

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode);
		String firstCode = getVerificationCode(username);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();
		signupPage.clickConfirmBtn();
		signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR_VI).completeVerify();
		commonAction.sleepInMiliSecond(5000);
		String resentCode = getVerificationCode(username);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
		signupPage.clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
	}

	//This TC also covers BH_4054_ContinueSignupWizardAfterExitingSession()
//	@Test
	public void BH_4049_ExitWizard_EmailAccount() throws SQLException {
	
		String username = mail;
		String contact = storePhone;
	
		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		country = signupPage.country;
		signupPage.inputStoreName(storeName);
	
		// Exit current session
		driver.quit();
	
		// Re-signup
		driver = new InitWebdriver().getDriver("chrome", "false");
		signupPage = new SignupPage(driver);
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
		.verifyUsernameExistError(USERNAME_EXIST_ERROR).completeVerify();
		
		// Login
		new LoginPage(driver).navigate().performLogin(country, username, password);

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
	}		
	
//	@Test
	public void BH_4054_ContinueSignupWizardAfterExitingSession() throws SQLException {
	
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

//	@Test
	public void BH_5195_SignUpForShopWithURLInUpperCase() throws SQLException {

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
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		// Re-login to the shop
		reLogintoShop(country, username, password);
		Assert.assertEquals(new InitConnection().getStoreURL(storeName), storeURL.toLowerCase());
	}

//	@Test
	public void BH_1363_SignUpForGoFreeAccountViaEmail() throws SQLException {

		String referralCode = "fromthompson";
		String domain = "abcdefgh";

		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";

		String username = mail;
		String contact = storePhone;

		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();

		country = signupPage.country;

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
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
	public void BH_1277A_SignUpForGoFreeAccountViaPhone() throws SQLException {
		
//		String referralCode = "frommark";
		String domain = "null";
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		
		String username = storePhone;
		String contact = mail;
		
		// Sign up
		signupPage.navigate()
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		
		country = signupPage.country;
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
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
	public void BH_1277B_SignUpForGoFreeAccountViaPhone() throws SQLException {
		
		String referralCode = "frommark";
		String domain = "abcdefgh";
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		
		String username = storePhone;
		String contact = mail;
		
		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username)).clickConfirmBtn();
		
		country = signupPage.country;
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
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

//	@Test
	public void BH_1364_SignUpForShopWithGoFreePackageUsingGomuaMailAccount() throws SQLException {
		
		String domain = "gomua.vn";
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
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
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
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
	
//	@Test
	public void BH_1365_SignUpForShopWithGoFreePackageUsingGomuaPhoneAccount() throws SQLException {
		
		String domain = "gomua.vn";
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
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
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
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
	
//	@Test
	public void BH_1368_SignUpForShopUsingStorefrontEmailAccount() throws SQLException {
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		String username = mail;
		String contact = storePhone;
		String displayName  = storeName;
		String birthday = "02/02/1990";
		
		// Signup in SF
		new pages.storefront.signup.SignupPage(driver).navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();		
		
		
		// Login
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
		
		// Verify Upgrade Now popup appears on the screen
		new HomePage(driver).navigateToPage("Products", "All Products");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Orders", "Order List");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Services");
		new HomePage(driver).clickUpgradeNow();
	}	
	
//	@Test
	public void BH_1599_SignUpForShopUsingStorefrontPhoneAccount() throws SQLException {
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;
		String birthday = "02/02/1990";
		
		// Signup in SF
		pages.storefront.signup.SignupPage sf = new pages.storefront.signup.SignupPage(driver);
		sf.navigate()
		.fillOutSignupForm(country, username, password, displayName, birthday);
		signupPage.countryCode = sf.countryCode; // This is a temporary workaround. Solutions to the problem are being considered.
		sf.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		sf.inputEmail(contact)
		.clickCompleteBtn();

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();		
		
		// Login
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
		
		// Verify Upgrade Now popup appears on the screen
		new HomePage(driver).navigateToPage("Products", "All Products");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Orders", "Order List");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Services");
		new HomePage(driver).clickUpgradeNow();
		
		// Buy a package plan and approve the purchase in Internal tool
		PlansPage plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoWEB").selectPayment();
        String orderID = plansPage.getOrderId();
        InternalTool internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
        .login()
        .navigateToPage("GoSell","Packages","Orders list")
        .approveOrder(orderID);
	}		
	
//	@Test
	public void BH_1631_SignUpForShopUsingGomuaMailAccount() throws SQLException {
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		String username = mail;
		String contact = storePhone;
		String displayName  = storeName;
		
		// Signup in Gomua
		new HeaderGoMua(driver).navigateToGoMua()
		.clickSignUpBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(username))
		.clickVerifyAndLoginBtn();
		
		// Login
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
		
		// Verify Upgrade Now popup appears on the screen
		new HomePage(driver).navigateToPage("Products", "All Products");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Orders", "Order List");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Services");
		new HomePage(driver).clickUpgradeNow();
	}	
	
//	@Test
	public void BH_1632_SignUpForShopUsingGomuaPhoneAccount() throws SQLException {
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;

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
		.clickComplete();
		
		// Login
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
		
		// Verify Upgrade Now popup appears on the screen
		new HomePage(driver).navigateToPage("Products", "All Products");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Orders", "Order List");
		new HomePage(driver).clickUpgradeNow();
		new HomePage(driver).navigateToPage("Services");
		new HomePage(driver).clickUpgradeNow();
	}	

	// BH-18726: Email field is missing at shop setup wizard screen
//	@Test
	public void BH_4052_SignUpForShopUsingGomuaPhoneAccount() throws SQLException {
		
		country = "Vietnam";
		currency = "Dong - VND(đ)";
		storeLanguage = "Tiếng Việt";
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;
		
		String newPassword = "fortesting!4";

		signupPage.countryCode = "+84"; // This is a temporary workaround. Solutions to the problem are being considered.
		
		// Signup in Gomua
		new HeaderGoMua(driver).navigateToGoMua()
		.clickSignUpBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(username))
		.clickVerifyAndLoginBtn()
		.inputEmail(contact)
		.clickComplete();
		commonAction.sleepInMiliSecond(3000); // Temporarily put sleep here
		
		
		// Login
		new LoginPage(driver).navigate()
		.clickForgotPassword()
		.selectCountry(country)
		.inputEmailOrPhoneNumber(username)
		.inputPassword(newPassword)
		.clickContinueOrConfirmBtn();

		new LoginPage(driver).inputVerificationCode(getResetCode(username))
		.clickContinueOrConfirmBtn();

		// Setup store
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		// Check if user is redirected to package registration screen
		signupPage.clickLogout();
		
		// Re-login to the shop 
		reLogintoShop(country, username, password);
		
	}			
	
	//Not done as test case description is in processing of updating
//	@Test
	public void BH_4053_ForgotPasswordForGomuaPhoneAccount() throws SQLException {
		
		String country = "Vietnam";
		String currency = "Dong - VND(đ)";
		String storeLanguage = "Tiếng Việt";
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;
		
		signupPage.countryCode = "+84"; // This is a temporary workaround. Solutions to the problem are being considered.
		
		username = "9123455780";
		String currentPass = "fortesting!8";
		String newPass = "fortesting!9";
		
		new HeaderGoMua(driver).navigateToGoMua()
		.clickOnLogInBTN();
		new LoginGoMua(driver).clickForgotPassword()
		.inputUsername(username)
		.inputPassWord(currentPass);
		new SignupGomua(driver).clickContinueBtn()
		.inputVerificationCode(getResetCode(username))
		.clickVerifyAndLoginBtn();
		new HeaderGoMua(driver).clickOnDisplayName()
		.clickChangePassword()
		.inputCurrentPassword(currentPass)
		.inputNewPassword(newPass)
		.clickDoneBtn();
	}	

	@Test
	public void BH_7668_SignUpForGoFreeEmailAccountViaPromotionLink() throws Exception {
		
		String domain = "abcdefgh";
		String [] referralCodePool = {"frommark", ""}; 
		String referralCode = referralCodePool[new Random().nextInt(referralCodePool.length)];
		
		String username = storePhone;
		String contact = mail;
		
		// Workaround to decide display language at Signup screen
		country = signupPage.navigate().selectCountry(country).country;
		signupLanguage = (country.contentEquals("Vietnam")) ? "Vietnamese" : "English";
		
		// Sign up
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
		.selectDisplayLanguage(signupLanguage) // Select display language at Signup screen
		.fillOutSignupForm(country, username, password, referralCode)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
		
		// Setup store
		storeLanguage = ""; // This is to select the default value
		setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		new HomePage(driver).waitTillSpinnerDisappear();
		currency = signupPage.currency;
		
		// Upgrade now dialog pops up immediately
		verifyUpgradNowMessage();
		
		// Verify users are redirected to plan purchase screen when clicking on Upgrade Now button
		new HomePage(driver).clickUpgradeNow();
		PlansPage plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoWEB");

		// Verify sign-up mails are sent to users
		if (!username.matches("\\d+")) verifyEmailUponSuccessfulSignup(username);        
        
		// Verify Upgrade Now popup appears on the screen
		verifyUpgradNowMessageAppearEverywhere();
		
		// Verify Upgrade Now popup appears on the screen when re-logging into dashboard
		new HomePage(driver).clickLogout();
		reLogintoShop(country, username, password);
		verifyUpgradNowMessageAppearEverywhere();
		
		// Check store's data in database
		String expectedReferralCode = (referralCode.length()>1) ? referralCode.toUpperCase():null;
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), expectedReferralCode);
		
	}	
}
