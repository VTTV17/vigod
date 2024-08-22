package utilities.driver;

import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import utilities.commons.UICommonAndroid;

import java.net.MalformedURLException;
import java.net.URL;

import static utilities.environment.goBUYEREnvironment.goBUYERSplashActivity;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;
import static utilities.environment.goSELLEREnvironment.goSELLERLoginActivity;


public class InitAndroidDriver {
    Logger logger = LogManager.getLogger();
    private final static String url = "http://127.0.0.1:4723/wd/hub";

    /**
     * This method returns an instance of the AppiumDriver class. It takes in the following parameters:
     * @param udid The UDID of the device
     * @param appPackage The package name of the app
     * @param appActivity The activity that needs to be started
     * @return AppiumDriver returns an instance of the AppiumDriver class (AndroidDriver or IOSDriver)
     * @throws MalformedURLException throws a MalformedURLException if the URL for the Appium server is malformed
     * @throws IllegalArgumentException throws an IllegalArgumentException if the platform name is not recognized
     */
    public AndroidDriver getAndroidDriver(String udid, String appPackage, String appActivity) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", "ANDROID");
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("noReset", "false");
        capabilities.setCapability("fastReset", "true");
        capabilities.setCapability("resetOnSessionStartOnly", "true");
        capabilities.setCapability("autoGrantPermissions","true");
        capabilities.setCapability("automationName", "UIAutomator2");
        // Fix startActivity issue
        capabilities.setCapability("appWaitActivity","*");
        capabilities.setCapability("appWaitForLaunch", "false");
        return new AndroidDriver(new URL(url), capabilities);
    }

    public AndroidDriver getAndroidDriver(String udid) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", "ANDROID");
        capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("noReset", "false");
        capabilities.setCapability("fastReset", "true");
        capabilities.setCapability("resetOnSessionStartOnly", "true");
        capabilities.setCapability("autoGrantPermissions", "true");
        capabilities.setCapability("automationName", "UIAutomator2");
        // Fix startActivity issue
        capabilities.setCapability("appWaitActivity", "*");
        capabilities.setCapability("appWaitForLaunch", "false");
        return new AndroidDriver(new URL(url), capabilities);
    }


    public AndroidDriver getAndroidDriver(String udid, String appPath) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", "ANDROID");
        capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("noReset", "false");
        capabilities.setCapability("fastReset", "true");
        capabilities.setCapability("resetOnSessionStartOnly", "true");
        capabilities.setCapability("autoGrantPermissions", "true");
        capabilities.setCapability("automationName", "UIAutomator2");
        // Fix startActivity issue
        capabilities.setCapability("appWaitActivity", "*");
        capabilities.setCapability("appWaitForLaunch", "false");
        capabilities.setCapability("app", appPath);
        return new AndroidDriver(new URL(url), capabilities);
    }


    public AndroidDriver getSellerDriver(String udid) throws MalformedURLException {
        AndroidDriver driver = getAndroidDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/GoSELLER STAG.apk");
        new UICommonAndroid(driver).relaunchApp(goSELLERBundleId);
        return driver;
    }

    @SneakyThrows
    public AndroidDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        return getAndroidDriver(udid, goBuyerBundleId, goBUYERSplashActivity);
    }

}
