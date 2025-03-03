package utilities.driver;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static utilities.account.AccountTest.IOS_GoBUYER_APP;
import static utilities.account.AccountTest.IOS_GoSELLER_APP;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InitIOSDriver implements IAppiumDriverInitializer {
    String url = "http://127.0.0.1:%s/wd/hub".formatted(System.getProperty("appiumPort"));

    
    public IOSDriver getIOSDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
        XCUITestOptions options = new XCUITestOptions();
        options.setCapability("appium:udid", udid);
        options.setCapability("platformName", "iOS");
        options.setCapability("appium:newCommandTimeout", 300000);
        options.setCapability("appium:wdaLaunchTimeout", 300000);
        options.setCapability("appium:wdaConnectionTimeout", 300000);
        options.setCapability("appium:automationName", "XCUITest");
        options.setCapability("appium:app", appPath);
        return new IOSDriver(new URI(url).toURL(), options);
    }    
    
	@Override
	public IOSDriver getDriver(String udid, String appPath) throws MalformedURLException, URISyntaxException {
		return getIOSDriver(udid, appPath);
	}    
    
	@Override
    public IOSDriver getSellerDriver(String udid) {
        try {
            // Init driver
            IOSDriver driver = getIOSDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + IOS_GoSELLER_APP);

            // Open GoSeller app
            driver.terminateApp(goSELLERBundleId);
            driver.activateApp(goSELLERBundleId);

            // Return driver
            return driver;
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
    public IOSDriver getBuyerDriver(String udid, String goBuyerBundleId) {
        try {
            // Init driver
            IOSDriver driver = getIOSDriver(udid, System.getProperty("user.dir") + "/src/main/resources/app/" + IOS_GoBUYER_APP);

            // Open GoSeller app
            driver.terminateApp(goBuyerBundleId);
            driver.activateApp(goBuyerBundleId);

            // Return driver
            return driver;
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
