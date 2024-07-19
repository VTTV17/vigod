package mobile.seller.iOS.login;

import lombok.Getter;
import mobile.seller.iOS.home.HomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.model.sellerApp.login.LoginInformation;

import static mobile.seller.iOS.home.HomeElement.loc_icnAccount;

public class LoginScreen extends LoginElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    @Getter
    private static LoginInformation loginInformation = new LoginInformation();

    public LoginScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    void allowNotificationPermission() {
        try {
            // Switch to notification permission and accept
            commonIOS.allowPermission("Allow");

            // Log
            logger.info("Accept notification permission");
        } catch (NoAlertPresentException ignored) {
        }
    }

    void inputUsername(String username) {
        // Input username
        commonIOS.sendKeys(loc_txtUsername, username);

        // Log
        logger.info("Input username: {}", username);
    }

    void inputPassword(String password) {
        // Input password
        commonIOS.sendKeys(loc_txtPassword, password);

        // Log
        logger.info("Input password: ********");
    }

    void agreeTermOfUse() {
        // Agree term of use
        commonIOS.click(loc_chkTermOfUse);

        // Log
        logger.info("Agree term of use");
    }

    void clickLoginBtn() {
        // Tap login button
        commonIOS.click(loc_btnLogin);

        // Log
        logger.info("Tap login button");
    }

    void waitHomeScreenLoaded() {
        // Wait home screen loaded
        commonIOS.getElement(loc_icnAccount);
    }

    public void performLogin(LoginInformation loginInformation) {
        // Get login information
        LoginScreen.loginInformation = loginInformation;

        // Check if user are logged, logout and re-login with new account
        new HomeScreen(driver).logout();

        // Login with new account
        allowNotificationPermission();
        inputUsername(loginInformation.getEmail());
        inputPassword(loginInformation.getPassword());
        agreeTermOfUse();
        clickLoginBtn();

        // Wait home screen loaded
        waitHomeScreenLoaded();
    }
}
