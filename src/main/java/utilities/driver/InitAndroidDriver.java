package utilities.driver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import utilities.commons.UICommonAndroid;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static utilities.account.AccountTest.ANDROID_GoBUYER_APP;
import static utilities.account.AccountTest.ANDROID_GoSELLER_APP;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;


public class InitAndroidDriver {
    String url = "http://127.0.0.1:%s/wd/hub".formatted(System.getProperty("appiumPort"));


    public AndroidDriver getAndroidDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setUdid(udid);
        options.setCapability("platformName", "Android");
        options.setCapability("appium:platformVersion", "12.0");
        options.setCapability("appium:automationName", "uiautomator2");
        options.setCapability("appium:autoGrantPermissions", "true");
        options.setCapability("appium:appWaitActivity", "");
        options.setCapability("appium:resetOnSessionStartOnly", "true");
        options.setCapability("appium:appWaitForLaunch", "false");
        options.setCapability("appium:fastReset", "true");
        options.setCapability("appium:noReset", "false");
        options.setCapability("appium:newCommandTimeout", "30000");
        options.setCapability("appium:app", appPath);
        return new AndroidDriver(new URI(url).toURL(), options);
    }


    public AndroidDriver getSellerDriver(String udid) throws MalformedURLException, URISyntaxException {
        AndroidDriver driver = getAndroidDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + ANDROID_GoSELLER_APP);
        new UICommonAndroid(driver).relaunchApp(goSELLERBundleId);
        return driver;
    }

    @SneakyThrows
    public AndroidDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        AndroidDriver driver = getAndroidDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" +  ANDROID_GoBUYER_APP);
        new UICommonAndroid(driver).relaunchApp(goBuyerBundleId);
        return driver;
    }

}
