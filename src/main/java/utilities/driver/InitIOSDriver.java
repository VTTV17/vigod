package utilities.driver;

import io.appium.java_client.ios.IOSDriver;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InitIOSDriver {
    Logger logger = LogManager.getLogger();
    private final static String url = "http://127.0.0.1:4723/wd/hub";

    public IOSDriver getIOSDriver(String udid) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("automationName", "XCUITest");
        return new IOSDriver(new URL(url), capabilities);
    }


    public IOSDriver getSellerDriver(String udid) {
        try {
            // Init driver
            IOSDriver driver = getIOSDriver(udid);

//            // Uninstall app
//            if (driver.isAppInstalled(goSELLERBundleId)) driver.removeApp(goSELLERBundleId);
//
//            // Open TestFlight
//            driver.activateApp("com.apple.TestFlight");
//
//            // Init iOS commons
//            UICommonIOS commonIOS = new UICommonIOS(driver);
//
//            // Get Install button locator
//            By loc_btnInstall = By.xpath("//*[contains(@name, \"Seller\") and contains(@name, \"STG\")]/parent::*/following-sibling::*[1]/*");
//
//            // Start download
//            commonIOS.tap(loc_btnInstall);
//
//            // Wait app downloaded
//            logger.info("Wait GoSELLER app installed");
//
//            while (true) try {
//                if (commonIOS.getText(loc_btnInstall).equals("OPEN")) break;
//            } catch (StaleElementReferenceException ignored) {
//            }
//
//            // Quit TestFlight app
//            driver.terminateApp("com.apple.TestFlight");

            // Open GoSeller app
            driver.terminateApp(goSELLERBundleId);
            driver.activateApp(goSELLERBundleId);

            // Return driver
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public IOSDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        try {
            IOSDriver driver = getIOSDriver(udid);
            driver.activateApp(goBuyerBundleId);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
