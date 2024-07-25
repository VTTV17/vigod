package mobile.buyer.login.forgot_password;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;

public class ForgotPasswordScreen extends ForgotPasswordElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonAndroid;
    Logger logger = LogManager.getLogger();

    public ForgotPasswordScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonAndroid = new UICommonAndroid(driver);
    }

    public ForgotPasswordScreen inputUsernameForgotPassword(String username) {
        commonAndroid.sendKeys(loc_txtUsername, username);
        logger.info("Input '{}' into Username field.", username);
        return this;
    }

    public ForgotPasswordScreen inputNewPassword(String password) {
        commonAndroid.sendKeys(loc_txtPassword, password);
        logger.info("Input '{}' into New Password field.", password);
        return this;
    }

}
