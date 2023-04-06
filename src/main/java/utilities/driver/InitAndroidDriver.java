package utilities.driver;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class InitAndroidDriver {
    private AndroidDriver driver;

    public AndroidDriver getAndroidDriver(String udid, String platformName, String appPackage, String appActivity, String url) throws MalformedURLException {
        if (driver == null) {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("udid", udid);
            capabilities.setCapability("platformName", platformName);
            capabilities.setCapability("appPackage", appPackage);
            capabilities.setCapability("appActivity", appActivity);
            driver = new AndroidDriver(new URL(url), capabilities);
        }
        return driver;
    }

}
