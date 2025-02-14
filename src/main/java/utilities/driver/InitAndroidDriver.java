package utilities.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import utilities.commons.UICommonAndroid;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static utilities.account.AccountTest.ANDROID_GoSELLER_APP;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;


public class InitAndroidDriver implements IAppiumDriverInitializer {
    String url = "http://127.0.0.1:%s/wd/hub".formatted(System.getProperty("appiumPort"));

    public AndroidDriver getAndroidDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setUdid(udid);
        options.setCapability("platformName", "Android");
        options.setCapability("appium:automationName", "uiautomator2");
        options.setCapability("appium:autoGrantPermissions", "true");
        options.setCapability("appium:appWaitActivity", "*");
        options.setCapability("appium:resetOnSessionStartOnly", "true");
        options.setCapability("appium:appWaitForLaunch", "false");
        options.setCapability("appium:fastReset", "true");
        options.setCapability("appium:noReset", "false");
        options.setCapability("appium:newCommandTimeout", "30000");
        options.setCapability("appium:app", appPath);
        return new AndroidDriver(new URI(url).toURL(), options);
    }    
    
	@Override
	public AppiumDriver getDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
		return getAndroidDriver(udid, appPath);
	}

    @Override
    public AndroidDriver getSellerDriver(String udid) {
		try {
			AndroidDriver driver = getAndroidDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + ANDROID_GoSELLER_APP);
			new UICommonAndroid(driver).relaunchApp(goSELLERBundleId);
			return driver;
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }

    @Override
    public AndroidDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        AndroidDriver driver = null;
        try {
            driver = getAndroidDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" +  "GoBUYER_PREPROD.apk");
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        new UICommonAndroid(driver).relaunchApp(goBuyerBundleId);
        return driver;
    }

}
