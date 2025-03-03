package utilities.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static utilities.account.AccountTest.ANDROID_GoSELLER_APP;


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
	public AppiumDriver getDriver(String udid, String appPath) {
		try {
			return getAndroidDriver(udid, appPath);
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

    @Override
    public AndroidDriver getSellerDriver(String udid) {
    	return (AndroidDriver) getDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + ANDROID_GoSELLER_APP);
    }

    @Override
    public AndroidDriver getBuyerDriver(String udid, String apkFileName) {
        return (AndroidDriver) getDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + apkFileName);
    }

}
