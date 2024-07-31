package mobile.buyer.iOS.login;

import mobile.buyer.iOS.home.account.AccountScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.model.sellerApp.login.LoginInformation;

import static mobile.buyer.iOS.home.HomeElement.loc_btnFooterSearch;

public class LoginScreen extends LoginElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public LoginScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    void inputUsername(String username) {
        // Input email
        commonIOS.sendKeys(loc_txtEmail, username);

        // Log
        logger.info("Input email: {}", username);
    }

    void inputPassword(String password) {
        // Input password
        commonIOS.sendKeys(loc_txtPassword, password);

        // Log
        logger.info("Input password: ********");
    }

    void clickLoginBtn() {
        // Click login button
        commonIOS.click(loc_btnLogin);

        // Log
        logger.info("Click login button");
    }

    public void performLogin(LoginInformation loginInformation) {
        // Navigate to login screen
        new AccountScreen(driver).navigateToLoginScreen();

        // Open login screen
        commonIOS.click(loc_btnLogin);

        // Perform login
        inputUsername(loginInformation.getEmail());
        inputPassword(loginInformation.getPassword());
        clickLoginBtn();

        // Wait home screen loaded
        commonIOS.getElement(loc_btnFooterSearch);

        // Log
        logger.info("Login successful");
    }
}
