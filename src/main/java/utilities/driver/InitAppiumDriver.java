package utilities.driver;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class InitAppiumDriver {

	/**
	 * This method returns an instance of the AppiumDriver class. It takes in the following parameters:
	 * @param udid The UDID of the device
	 * @param platformName The platform of the device (either "android" or "ios")
	 * @param appPackage The package name of the app
	 * @param appActivity The activity that needs to be started
	 * @param url The Appium server URL
	 * @return AppiumDriver returns an instance of the AppiumDriver class (AndroidDriver or IOSDriver)
	 * @throws MalformedURLException throws a MalformedURLException if the URL for the Appium server is malformed
	 * @throws IllegalArgumentException throws an IllegalArgumentException if the platform name is not recognized
	 */
    public AppiumDriver getAppiumDriver(String udid, String platformName, String appPackage, String appActivity, String url) throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", udid);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
		capabilities.setCapability("newCommandTimeout", 300000);
        capabilities.setCapability("noReset", "false");
		capabilities.setCapability("fastReset", "true");
		capabilities.setCapability("resetOnSessionStartOnly", "true");
		capabilities.setCapability("autoGrantPermissions","true");
        return getAppiumDriver(capabilities, url);
    }

    public AppiumDriver getAppiumDriver(DesiredCapabilities capabilities, String url) throws MalformedURLException {
    	String platformNameFromCapacity = capabilities.getCapability("platformName").toString();
    	if (platformNameFromCapacity.equalsIgnoreCase("android")) {
    		return new AndroidDriver(new URL(url), capabilities);
    	} else if(platformNameFromCapacity.equalsIgnoreCase("ios")) {
    		return new IOSDriver(new URL(url), capabilities);
    	} else {
    		throw new IllegalArgumentException("Unknown platform: " + platformNameFromCapacity);
    	}
    }
}
