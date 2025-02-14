package utilities.driver;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import io.appium.java_client.AppiumDriver;

/**
 * This interface helps maintain the consistency of the initialization of IOSDriver and AndroidDriver
 */
public interface IAppiumDriverInitializer {
	public AppiumDriver getDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException;
	public AppiumDriver getSellerDriver(String udid);
	public AppiumDriver getBuyerDriver(String udid, String goBuyerBundleId);
}
