package pages.dashboard.onlineshop.preferences;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Configuration {

	final static Logger logger = LogManager.getLogger(Configuration.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Configuration(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[1]//h3[contains(.,'Facebook')]/following-sibling::*")
    WebElement ENABLE_FACEBOOK_MESSENGER_TOGGLE;
    
    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[2]//h3[contains(.,'Zalo')]/following-sibling::*")
    WebElement ENABLE_ZALO_OA_MESSENGER_TOGGLE;
    
    @FindBy (xpath = "(//div[contains(@class,'live-chat-configuration')]//section)[3]//h3[contains(.,'login') or contains(.,'đăng nhập') ]/following-sibling::*")
    WebElement ENABLE_FACEBOOK_LOGIN_FOR_ONLINE_STORE_TOGGLE;
    
    @FindBy (id = "gaCode")
    WebElement GOOGLE_ANALYTICS;
    
    @FindBy (id = "gvHtmlTag")
    WebElement HTML_TAG;
    
    @FindBy (id = "note")
    WebElement GOOGLE_TAG_MANAGER;
    
    @FindBy (id = "fbPixelId")
    WebElement FACEBOOK_PIXEL;
    
    @FindBy (id = "fbAppId")
    WebElement FACEBOOK_APP_ID;
    
    @FindBy (css = ".btn-next")
    WebElement COMPLETE_BTN;
    
    public Configuration clickEnableFacebookMessengerToggle() {
    	commonAction.clickElement(ENABLE_FACEBOOK_MESSENGER_TOGGLE);
    	logger.info("Clicked on 'Enable Facebook Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableZaloOAMessengerToggle() {
    	commonAction.clickElement(ENABLE_ZALO_OA_MESSENGER_TOGGLE);
    	logger.info("Clicked on 'Zalo OA Messenger' toggle button.");
    	return this;
    }
    
    public Configuration clickEnableFacebookLoginForOnlineStoreToggle() {
    	commonAction.clickElement(ENABLE_FACEBOOK_LOGIN_FOR_ONLINE_STORE_TOGGLE);
    	logger.info("Clicked on 'Enable Facebook Login For Online Store' toggle button.");
    	return this;
    }

	public Configuration inputGoogleAnalyticsCode(String code) {
    	if (commonAction.isElementVisiblyDisabled(GOOGLE_ANALYTICS.findElement(By.xpath("./parent::*")))) {
    		new HomePage(driver).isMenuClicked(GOOGLE_ANALYTICS);
    		return this;
    	}
		commonAction.inputText(GOOGLE_ANALYTICS, code);
		logger.info("Input '" + code + "' into Google Analytics Code field.");
		return this;
	}  
	
    public String getGoogleAnalyticsCode() {
    	String text = commonAction.getElementAttribute(GOOGLE_ANALYTICS, "value");
    	logger.info("Retrieved Google Analytics Code: " + text);
    	return text;
    }  
    
    public Configuration inputHTMLTag(String htmlTag) {
    	if (commonAction.isElementVisiblyDisabled(HTML_TAG.findElement(By.xpath("./parent::*/parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(HTML_TAG);
    		return this;
    	}
    	commonAction.inputText(HTML_TAG, htmlTag);
    	logger.info("Input '" + htmlTag + "' into HTML Tag field.");
    	return this;
    }  
    
    public String getHTMLTag() {
    	String text = commonAction.getElementAttribute(HTML_TAG, "value");
    	logger.info("Retrieved HTML Tag: " + text);
    	return text;
    }  
    
    public Configuration inputGoogleTagManager(String tag) {
    	if (commonAction.isElementVisiblyDisabled(GOOGLE_TAG_MANAGER.findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(GOOGLE_TAG_MANAGER);
    		return this;
    	}
    	commonAction.inputText(GOOGLE_TAG_MANAGER, tag);
    	logger.info("Input '" + tag + "' into Google Tag Manager field.");
    	return this;
    }  
    
    public String getGoogleTagManager() {
    	String text = commonAction.getElementAttribute(GOOGLE_TAG_MANAGER, "value");
    	logger.info("Retrieved Google Tag Manager: " + text);
    	return text;
    }  
    
    public Configuration inputFacebookPixel(String pixel) {
    	if (commonAction.isElementVisiblyDisabled(FACEBOOK_PIXEL.findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(FACEBOOK_PIXEL);
    		return this;
    	}
    	commonAction.inputText(FACEBOOK_PIXEL, pixel);
    	logger.info("Input '" + pixel + "' into Facebook Pixel field.");
    	return this;
    }  
    
    public String getFacebookPixel() {
    	String text = commonAction.getElementAttribute(FACEBOOK_PIXEL, "value");
    	logger.info("Retrieved Facebook Pixel: " + text);
    	return text;
    }  
    
    public Configuration inputFacebookAppID(String id) {
    	if (commonAction.isElementVisiblyDisabled(FACEBOOK_APP_ID.findElement(By.xpath("./parent::*/parent::*")))) {
    		new HomePage(driver).isMenuClicked(FACEBOOK_APP_ID);
    		return this;
    	}
    	commonAction.inputText(FACEBOOK_APP_ID, id);
    	logger.info("Input '" + id + "' into Facebook App ID field.");
    	return this;
    }  
    
    public String getFacebookAppID() {
    	String text = commonAction.getElementAttribute(FACEBOOK_APP_ID, "value");
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
