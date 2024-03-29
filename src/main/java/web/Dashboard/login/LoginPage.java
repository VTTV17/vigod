package web.Dashboard.login;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.LOGIN_PATH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import api.Seller.login.Login;
import web.Dashboard.home.HomePage;
import utilities.thirdparty.Facebook;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class LoginPage {

    final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_lblLoginScreen = By.cssSelector(".login-widget");
    By loc_lblForgotPasswordScreen = By.cssSelector(".forgot-page-wrapper");
    
    By loc_lblSelectedLanguage = By.xpath("//span[contains(@class,'changeLanguage-selected')]");
    By loc_lnkEnglish = By.cssSelector(".login-widget__changeLanguage-english");
    By loc_lnkVietnamese = By.cssSelector(".login-widget__changeLanguage:nth-of-type(2)");

    By loc_ddlCountry = By.cssSelector(".phone-code div.uik-select__valueRenderedWrapper");

    By loc_frmLogin = By.xpath("//div[contains(@class,'login-widget__formBody') and not(@hidden)]");
    By loc_txtUsername = new ByChained(loc_frmLogin, By.cssSelector("input[name='username']")); 
    By loc_txtPassword = new ByChained(loc_frmLogin, By.cssSelector("input[name='password']"));
    By loc_btnLogin = new ByChained(loc_frmLogin, By.xpath(".//button[contains(@class,'gs-button') and contains(@class,'login-widget__btnSubmit')]"));
    By loc_lblUsernameError = By.cssSelector("#username + .invalid-feedback");
    By loc_lblPasswordError = By.cssSelector("#password + .invalid-feedback");
    By loc_lblLoginFailError = By.cssSelector("div[class~='alert__wrapper']:not(div[hidden])");
    By loc_btnFacebookLogin = By.cssSelector(".login-widget__btnSubmitFaceBook"); 
    By loc_tabStaff = By.cssSelector("span.login-widget__tab:nth-child(2)");
    
    By loc_dlgWarning = By.cssSelector("div.modal-content");

    By loc_lnkForgotPassword = By.cssSelector(".login-widget__forgotPassword");
    By loc_btnContinue = By.cssSelector(".login-widget__btnSubmit"); 
    By loc_txtVerificationCode = By.cssSelector("input[name='key']"); 
    By loc_lnkResendOTP = By.cssSelector(".btn-resend");

    public LoginPage navigate() {
        driver.get(DOMAIN + LOGIN_PATH);
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.click(loc_ddlCountry);
    	commonAction.click(By.xpath("//div[@class='phone-option']/div[text()='%s']".formatted(country)));
    	logger.info("Selected country: " + country);
        return this;
    }

    public LoginPage switchToStaffTab() {
        commonAction.click(loc_tabStaff);
        logger.info("Switched to Staff Tab.");
        return this;
    }

    public LoginPage clickFacebookBtn() {
        commonAction.click(loc_btnFacebookLogin);
        logger.info("Clicked on Facebook linktext.");
        return this;
    }

    public LoginPage inputEmailOrPhoneNumber(String username) {
        commonAction.inputText(loc_txtUsername, username);
        logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
        commonAction.inputText(loc_txtPassword, password);
        logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public LoginPage clickLoginBtn() {
        commonAction.click(loc_btnLogin);
        logger.info("Clicked on Login button.");
        return this;
    }

    public LoginPage clickForgotPassword() {
        commonAction.click(loc_lnkForgotPassword);
        logger.info("Clicked on Forgot Password linktext.");
        return this;
    }

    public LoginPage clickResendOTP() {
        commonAction.click(loc_lnkResendOTP);
        logger.info("Clicked on Resend linktext.");
        return this;
    }

    public LoginPage clickContinueOrConfirmBtn() {
        commonAction.click(loc_btnContinue);
        logger.info("Clicked on Continue/Confirm button.");
        return this;
    }

    public LoginPage inputVerificationCode(String verificationCode) {
        commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
        logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }

    public LoginPage performLogin(String username, String password) {
        inputEmailOrPhoneNumber(username);
        inputPassword(password);
        clickLoginBtn();
        return this;
    }

    public LoginPage performLogin(String country, String username, String password) {
        selectCountry(country);
        performLogin(username, password);
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }

    public LoginPage performLoginWithFacebook(String username, String password) {
        String originalWindow = commonAction.getCurrentWindowHandle();

        clickFacebookBtn();

        for (String windowHandle : commonAction.getAllWindowHandles()) {
            if (!originalWindow.contentEquals(windowHandle)) {
                commonAction.switchToWindow(windowHandle);
                break;
            }
        }

        new Facebook(driver).performLogin(username, password);

        commonAction.switchToWindow(originalWindow);
        return this;
    }

    public String getSelectedLanguage() {
        String selectedLanguage = commonAction.getText(loc_lblSelectedLanguage);
        logger.info("Retrieved selected language.");
        return selectedLanguage;
    }

    /**
     * Selects the display language on the login page.
     *
     * @param language the desired language to be displayed, either "ENG" or "VIE"
     * @throws Exception if an exception occurs during the execution of this method
     */
    public LoginPage selectDisplayLanguage(String language) throws Exception {
        commonAction.sleepInMiliSecond(1000);
        if (language.contentEquals("ENG")) {
            commonAction.click(loc_lnkEnglish);
        } else if (language.contentEquals("VIE")) {
            commonAction.click(loc_lnkVietnamese);
        } else {
            throw new Exception("Input value does not match any of the accepted values: VIE/ENG");
        }
        logger.info("Selected display language '%s'.".formatted(language));
        return this;
    }
    
    public String getLoginFailError() {
    	String text = commonAction.getText(loc_lblLoginFailError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }
    
    public String getUsernameError() {
    	String text = commonAction.getText(loc_lblUsernameError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }      

    public String getPasswordError() {
    	String text = commonAction.getText(loc_lblPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }      

    public void verifyLoginWithDeletedStaffAccount(String content) {
        Assert.assertTrue(commonAction.getText(loc_dlgWarning).contains(content),
                "[Login][Deleted Staff Account] No warning popup has been shown");
    }

    /* get dashboard information */
    public void loginDashboardByJsAndGetStoreInformation(LoginInformation loginInformation) {
        // access to dashboard to set cookie
        driver.get(DOMAIN);

        // init login information model
        LoginDashboardInfo loginInfo = new Login().getInfo(loginInformation);

        // login by js - local storage
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('accessToken', '%s')".formatted(loginInfo.getAccessToken()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('refreshToken', '%s')".formatted(loginInfo.getRefreshToken()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeId', %s)".formatted(loginInfo.getStoreID()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('userId', %s)".formatted(loginInfo.getUserId()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeOwnerId', %s)".formatted(loginInfo.getOwnerId()));
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('storeFull', 'storeFull')");
        if (loginInfo.getStaffPermissionToken() != null)
            ((JavascriptExecutor) driver).executeScript("localStorage.setItem('staffPermissionToken', '\"%s\"')".formatted(loginInfo.getStaffPermissionToken()));

        logger.info("Set local storage successfully");

        driver.navigate().refresh();
    }

    public void verifyVerificationCodeError(String signupLanguage) throws Exception {
        String text = commonAction.getText(loc_lblLoginFailError);
        String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("login.screen.error.wrongVerificationCode", signupLanguage);
        Assert.assertEquals(text, retrievedMsg, "[Signin][Wrong Verification Code] Message does not match.");
        logger.info("verifyVerificationCodeError completed");
    }

    public void verifyTextAtLoginScreen() throws Exception {
        String text = commonAction.getText(loc_lblLoginScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("login.screen.text"));
        logger.info("verifyTextAtLoginScreen completed");
    }

    public void verifyTextAtForgotPasswordScreen() throws Exception {
        String text = commonAction.getText(loc_lblForgotPasswordScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("login.forgotPassword.text"));
        logger.info("verifyTextAtForgotPasswordScreen completed");
    }
    public LoginPage staffLogin(String userName, String pass){
        navigate().switchToStaffTab().performLogin(userName, pass);
        logger.info("Staff login to dashboard successfully!");
        return this;
    }

}
