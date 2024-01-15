package pages.dashboard.onlineshop.preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Configuration {

	final static Logger logger = LogManager.getLogger(Configuration.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public Configuration(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnEnableFacebookMessengerToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[1]//h3[contains(.,'Facebook')]/following-sibling::*");
    By loc_btnEnableZaloOAToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[2]//h3[contains(.,'Zalo')]/following-sibling::*");
    By loc_btnEnableLoginViaFacebookToggle = By.xpath("(//div[contains(@class,'live-chat-configuration')]//section)[3]//h3[contains(.,'login') or contains(.,'đăng nhập')]/following-sibling::*");
    By loc_txtGoogleAnalytics = By.id("gaCode");
    By loc_txtHTMLTag = By.id("gvHtmlTag");
    By loc_txtGoogleTagManager = By.id("note");
    By loc_txtFacebookPixel = By.id("fbPixelId");
    By loc_txtFacebookAppId = By.id("fbAppId");
    By loc_btnComplete = By.cssSelector(".btn-next");

    public Configuration clickEnableFacebookMessengerToggle() {
    	commonAction.click(loc_btnEnableFacebookMessengerToggle);
    	logger.info("Clicked on 'Enable Facebook Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableZaloOAMessengerToggle() {
    	commonAction.click(loc_btnEnableZaloOAToggle);
    	logger.info("Clicked on 'Zalo OA Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableFacebookLoginForOnlineStoreToggle() {
    	commonAction.click(loc_btnEnableLoginViaFacebookToggle);
    	logger.info("Clicked on 'Enable Facebook Login For Online Store' toggle button.");
    	return this;
    }

	public Configuration inputGoogleAnalyticsCode(String code) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtGoogleAnalytics).findElement(By.xpath("./parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtGoogleAnalytics));
    		return this;
    	}
		commonAction.sendKeys(loc_txtGoogleAnalytics, code);
		logger.info("Input '" + code + "' into Google Analytics Code field.");
		return this;
	}  
	
    public String getGoogleAnalyticsCode() {
    	String text = commonAction.getValue(loc_txtGoogleAnalytics);
    	logger.info("Retrieved Google Analytics Code: " + text);
    	return text;
    }  
    
    public Configuration inputHTMLTag(String htmlTag) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtHTMLTag).findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtHTMLTag));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtHTMLTag, htmlTag);
    	logger.info("Input '" + htmlTag + "' into HTML Tag field.");
    	return this;
    }  
    
    public String getHTMLTag() {
    	String text = commonAction.getValue(loc_txtHTMLTag);
    	logger.info("Retrieved HTML Tag: " + text);
    	return text;
    }  
    
    public Configuration inputGoogleTagManager(String tag) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtGoogleTagManager).findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtGoogleTagManager));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtGoogleTagManager, tag);
    	logger.info("Input '" + tag + "' into Google Tag Manager field.");
    	return this;
    }  
    
    public String getGoogleTagManager() {
    	String text = commonAction.getValue(loc_txtGoogleTagManager);
    	logger.info("Retrieved Google Tag Manager: " + text);
    	return text;
    }  
    
    public Configuration inputFacebookPixel(String pixel) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtFacebookPixel).findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtFacebookPixel));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtFacebookPixel, pixel);
    	logger.info("Input '" + pixel + "' into Facebook Pixel field.");
    	return this;
    }  
    
    public String getFacebookPixel() {
    	String text = commonAction.getValue(loc_txtFacebookPixel);
    	logger.info("Retrieved Facebook Pixel: " + text);
    	return text;
    }  
    
    public Configuration inputFacebookAppID(String id) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtFacebookAppId).findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtFacebookAppId));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtFacebookAppId, id);
    	logger.info("Input '" + id + "' into Facebook App ID field.");
    	return this;
    }  
    
    public String getFacebookAppID() {
    	String text = commonAction.getValue(loc_txtFacebookAppId);
    	logger.info("Retrieved Facebook App ID: " + text);
    	return text;
    }  
    
    public void verifyPermissionToEnableFacebookMessenger(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickEnableFacebookMessengerToggle();
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    public void verifyPermissionToEnableZaloOAMessenger(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickEnableZaloOAMessengerToggle();
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToEnableFacebookLoginForOnlineStore(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		clickEnableFacebookLoginForOnlineStoreToggle();
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(commonAction.getCurrentURL().contains(url));
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToConfigureGoogleAnalytics(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		inputGoogleAnalyticsCode("Test Permission");
    		Assert.assertTrue(getGoogleAnalyticsCode().contentEquals("Test Permission"));
    	} else if (permission.contentEquals("D")) {
    		if (commonAction.getCurrentURL().contains(url)) {
    			inputGoogleAnalyticsCode("Test Permission");
    			Assert.assertTrue(getGoogleAnalyticsCode().contentEquals(""));
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToConfigureGoogleShopping(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		inputHTMLTag("Test Permission");
    		Assert.assertTrue(getHTMLTag().contentEquals("Test Permission"));
    	} else if (permission.contentEquals("D")) {
    		if (commonAction.getCurrentURL().contains(url)) {
        		inputHTMLTag("Test Permission");
        		Assert.assertTrue(getHTMLTag().contentEquals(""));
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToConfigureGoogleTagManager(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		inputGoogleTagManager("Test Permission");
    		Assert.assertTrue(getGoogleTagManager().contentEquals("Test Permission"));
    	} else if (permission.contentEquals("D")) {
    		if (commonAction.getCurrentURL().contains(url)) {
    			inputGoogleTagManager("Test Permission");
    			Assert.assertTrue(getGoogleTagManager().contentEquals(""));
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToConfigureFacebookPixel(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		inputFacebookPixel("Test Permission");
    		Assert.assertTrue(getFacebookPixel().contentEquals("Test Permission"));
    	} else if (permission.contentEquals("D")) {
    		if (commonAction.getCurrentURL().contains(url)) {
        		inputFacebookPixel("Test Permission");
        		Assert.assertTrue(getFacebookPixel().contentEquals(""));
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
    public void verifyPermissionToConfigureFacebookAppID(String permission, String url) {
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		inputFacebookAppID("Test Permission");
    		Assert.assertTrue(getFacebookAppID().contentEquals("Test Permission"));
    	} else if (permission.contentEquals("D")) {
    		if (commonAction.getCurrentURL().contains(url)) {
        		inputFacebookAppID("Test Permission");
        		Assert.assertTrue(getFacebookAppID().contentEquals(""));
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }    
    
}
