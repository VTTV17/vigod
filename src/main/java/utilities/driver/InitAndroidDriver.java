package utilities.driver;

import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import utilities.appium_server.AppiumServer;
import utilities.cmd.CommandWindows;
import utilities.get_port.FreePort;

import java.net.MalformedURLException;
import java.net.URL;

import static utilities.environment.goBUYEREnvironment.goBUYERSplashActivity;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;
import static utilities.environment.goSELLEREnvironment.goSELLERLoginActivity;


public class InitAndroidDriver {
    Logger logger = LogManager.getLogger();
    private final static String url = "http://127.0.0.1:%s/wd/hub".formatted(AppiumServer.getAppiumServerPort());

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
        if (udid.contains(":")) {
            // Get adb port
            int adbPort = FreePort.get();
            if (adbPort == AppiumServer.getAppiumServerPort()) adbPort = FreePort.get();

            // Restart ADB server
            CommandWindows.execute("adb -P %s kill-server".formatted(adbPort));
            CommandWindows.execute("adb -P %s start-server".formatted(adbPort));

            // Forward devices to new adb port
            CommandWindows.execute("adb -P %s connect %s".formatted(adbPort, udid));

            // Close command windows
            CommandWindows.execute("taskkill /F /IM cmd.exe");

            // Add adbPort config
            capabilities.setCapability("adbPort", adbPort);

            // Log
            logger.info("Appium adbPort: {}", adbPort);
        }
        // Fix startActivity issue
        capabilities.setCapability("appWaitActivity","*");
        return new AndroidDriver(new URL(url), capabilities);
    }


    public AndroidDriver getSellerDriver(String udid) {
        try {
            return getAndroidDriver(udid, goSELLERBundleId, goSELLERLoginActivity);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public AndroidDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        return getAndroidDriver(udid, goBuyerBundleId, goBUYERSplashActivity);
    }

}
