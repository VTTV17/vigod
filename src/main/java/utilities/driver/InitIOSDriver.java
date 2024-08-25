package utilities.driver;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InitIOSDriver {
    String url = "http://127.0.0.1:%s/wd/hub".formatted(System.getProperty("appiumPort"));

    public IOSDriver getIOSDriver(String udid) throws MalformedURLException {
        LogManager.getLogger().info("Appium port: {}", System.getProperty("appiumPort"));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("wdaStartupRetries", 5);
        capabilities.setCapability("automationName", "XCUITest");
        return new IOSDriver(new URL(url), capabilities);
    }

    public IOSDriver getIOSDriver(String udid, String appPath) throws MalformedURLException {
        XCUITestOptions options = new XCUITestOptions();
        options.setCapability("appium:udid", udid);
        options.setCapability("platformName", "iOS");
        options.setCapability("appium:newCommandTimeout", 300000);
        options.setCapability("appium:wdaLaunchTimeout", 300000);
        options.setCapability("appium:wdaConnectionTimeout", 300000);
        options.setCapability("appium:automationName", "XCUITest");
        options.setCapability("appium:app", appPath);
        return new IOSDriver(new URL(url), options);
    }


    public IOSDriver getSellerDriver(String udid) {
        try {
            // Init driver
            IOSDriver driver = getIOSDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/GoSeller STG.zip");

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
            // Init driver
            IOSDriver driver = getIOSDriver(udid);

            // Open GoSeller app
            driver.terminateApp(goBuyerBundleId);
            driver.activateApp(goBuyerBundleId);

            // Return driver
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
