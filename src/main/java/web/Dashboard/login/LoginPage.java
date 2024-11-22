package web.Dashboard.login;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.commons.WebUtils;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.thirdparty.Facebook;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;

import static utilities.links.Links.*;

public class LoginPage {

    final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    UICommonAction commonAction;

    Domain domain;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    public LoginPage(WebDriver driver, Domain domain) {
        this(driver);
        this.domain = domain;
    }

    //Will move these locators to a separate file later
    By loc_lblLoginScreen = By.cssSelector(".login-widget");
    By loc_lblForgotPasswordScreen = By.cssSelector(".forgot-page-wrapper");
    public static By loc_ddlLanguage = By.cssSelector(".change-language__wrapper");
    String loc_ddvLanguage = "//div[starts-with(@class,'select-country__option')]//div[@class='label' and .='%s']";
    By loc_ddlCountryDefaultValue = By.cssSelector(".select-country-wrapper .option");
    By loc_ddlCountry = By.cssSelector(".select-country-wrapper .select-country__input-container input");
    String loc_ddvCountry = "//*[contains(@class, 'select-country__option')]//div[@class='label' and text()=\"%s\"]";
    By loc_frmLogin = By.xpath("//div[contains(@class,'login-widget__formBody') and not(@hidden)]");
    By loc_txtUsername = new ByChained(loc_frmLogin, By.cssSelector("input[name='username']"));
    By loc_txtPassword = new ByChained(loc_frmLogin, By.cssSelector("input[name='password']"));
    By loc_btnLogin = new ByChained(loc_frmLogin, By.xpath(".//button[@type='submit']"));
//    By loc_btnLogin = new ByChained(loc_frmLogin, By.xpath(".//button[contains(@class,'gs-button') and contains(@class,'login-widget__btnSubmit')]"));  //4.5

    By loc_lblUsernameError = By.cssSelector("#username + .invalid-feedback");
    By loc_lblPasswordError = By.cssSelector("#password + .invalid-feedback");
    By loc_lblLoginFailError = By.cssSelector("div[class~='alert__wrapper']:not(div[hidden])");
    By loc_btnFacebookLogin = By.cssSelector(".login-widget__btnSubmitFaceBook");
    By loc_tabStaff = By.cssSelector("span.login-widget__tab:nth-child(2)");
    By loc_dlgWarning = By.cssSelector("div.modal-content");
    By loc_lnkForgotPassword = new ByChained(loc_frmLogin, By.cssSelector(".login-widget__forgotPassword"));

    By loc_icnDotSpinner = new ByChained(loc_btnLogin, By.xpath(".//i[contains(@class,'fa-spinner')]"));

    public LoginPage navigateBiz() {
        driver.get(DOMAIN_BIZ + LOGIN_PATH);
        return this;
    }

    public LoginPage navigate() {
        driver.get(DOMAIN + LOGIN_PATH);
        return this;
    }

    public LoginPage navigate(String url) {
        driver.get(url);
        return this;
    }

    public LoginPage navigate(Domain domain) {
        switch (domain) {
            case VN -> navigate();
            case BIZ -> navigateBiz();
            default -> throw new IllegalArgumentException("Unexpected value: " + domain);
        }
        return this;
    }

    public LoginPage navigateToPage(Domain domain, DisplayLanguage lang) {
        switch (domain) {
            case VN -> navigate().selectDisplayLanguage(lang);
            case BIZ -> navigateBiz();
            default -> throw new IllegalArgumentException("Unexpected value: " + domain);
        }
        return this;
    }

    public LoginPage selectCountry(String country) {
        commonAction.getElement(loc_ddlCountryDefaultValue); //Implicitly means the dropdown has a default value and ready for further actions. Reason #1
        commonAction.click(loc_ddlCountry);
        commonAction.click(By.xpath(loc_ddvCountry.formatted(country)));
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

    public LoginPage waitTillDotSpinnerDisappear() {
        commonAction.waitInvisibilityOfElementLocated(loc_icnDotSpinner);
        logger.info("Dot spinner disappeared");
        return this;
    }

    public ForgotPasswordPage clickForgotPassword() {
        commonAction.click(loc_lnkForgotPassword);
        logger.info("Clicked on Forgot Password linktext.");
        return new ForgotPasswordPage(driver);
    }

    public LoginPage performLogin(String username, String password) {
        inputEmailOrPhoneNumber(username);
        inputPassword(password);
        clickLoginBtn();
        waitTillDotSpinnerDisappear();
        return this;
    }

    public LoginPage performLogin(String country, String username, String password) {
        selectCountry(country);
        performLogin(username, password);
        new HomePage(driver).waitTillSpinnerDisappear1(); //Not sure if it's still needed as UI behavior has changed
        return this;
    }

    public LoginPage performValidLogin(String country, String username, String password) {
        selectCountry(country);
        performLogin(username, password);
        new HomePage(driver).waitTillSpinnerDisappear1().verifyPageLoaded();
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
        String selectedLanguage = commonAction.getText(loc_ddlLanguage);
        logger.info("Retrieved selected language: " + selectedLanguage);
        return selectedLanguage;
    }

    /**
     * Selects the display language on the login page.
     */
    public LoginPage selectDisplayLanguage(String language) {
        return selectDisplayLanguage(DisplayLanguage.valueOf(language));
    }

    public LoginPage selectDisplayLanguage(DisplayLanguage language) {
        if (getSelectedLanguage().equals(language.name())) return this;

        commonAction.click(loc_ddlLanguage);
        UICommonAction.sleepInMiliSecond(500, "Not sure why sometimes the page is white without this sleep");

        if (!language.equals(DisplayLanguage.ENG) && !language.equals(DisplayLanguage.VIE)) {
            language = DisplayLanguage.ENG;
            logger.info("Input value does not match 'VIE' or 'ENG', so 'ENG' will be selected by default");
        }

        commonAction.click(By.xpath(loc_ddvLanguage.formatted(language)));
        logger.info("Selected display language '%s'.".formatted(language));
        UICommonAction.sleepInMiliSecond(200, "Wait a little after changing display language");
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
    public void loginDashboardByJs(LoginInformation loginInformation) {
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
        if (!loginInfo.getStaffPermissionToken().isEmpty())
            ((JavascriptExecutor) driver).executeScript("localStorage.setItem('staffPermissionToken', '\"%s\"')".formatted(loginInfo.getStaffPermissionToken()));

        logger.info("Set local storage successfully");

        WebUtils.retryUntil(5, 1000, "Can not logging in to dashboard after 5 times",
                () -> driver.getCurrentUrl().contains("/home"),
                () -> {
                    driver.get(DOMAIN);
                    return null;
                });
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

    public LoginPage staffLogin(String userName, String pass) {
        navigate().switchToStaffTab().performLogin(userName, pass);
        logger.info("Staff login to dashboard successfully!");
        return this;
    }

}
