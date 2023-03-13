package pages.dashboard.settings.storeinformation;

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

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class StoreInformation {
	
	final static Logger logger = LogManager.getLogger(StoreInformation.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public StoreInformation (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "li:nth-child(2) > a.nav-link")
    WebElement STORE_INFORMATION_TAB;
    
    @FindBy (id = "shopName")
    WebElement SHOP_NAME;
    
    @FindBy (id = "appName")
    WebElement APP_NAME;

    @FindBy (id = "contactNumber")
    WebElement HOTLINE;    
    
    @FindBy (css = ".info-container #email")
    WebElement EMAIL;
    
    @FindBy (id = "addressList")
    WebElement ADDRESS;
    
    @FindBy (id = "FACEBOOK")
    WebElement FACEBOOK_LINK;
    
    @FindBy (id = "INSTAGRAM")
    WebElement INSTAGRAM_LINK;
    
    @FindBy (id = "YOUTUBE_VIDEO")
    WebElement YOUTUBE_LINK;

    @FindBy(css = "input#seoTitle")
    WebElement SEO_TITLE;    
    
    @FindBy (id = "noticeEnabled")
    WebElement NOTICE_LOGO_TOGGLE;    
    
    @FindBy (id = "registeredEnabled")
    WebElement REGISTERED_LOGO_TOGGLE;    
    
    @FindBy (css = ".info-container .setting_btn_save")
    WebElement SAVE_BTN;    
    
    
    public StoreInformation navigate() {
    	clickStoreInformationTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
        return this;
    }

    public StoreInformation clickStoreInformationTab() {
    	commonAction.clickElement(STORE_INFORMATION_TAB);
    	logger.info("Clicked on Store Information tab.");
        return this;
    }
    
    public StoreInformation inputShopName(String name) {
    	commonAction.inputText(SHOP_NAME, name);
    	logger.info("Input '" + name + "' into Shop Name field.");
        return this;
    }
    
    public String getShopName() {
    	String name = commonAction.getElementAttribute(SHOP_NAME, "value");
    	logger.info("Retrieved shop name: " + name);
    	return name;
    }
    
    public StoreInformation inputAppName(String name) {
    	if (commonAction.isElementVisiblyDisabled(APP_NAME.findElement(By.xpath("./parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(APP_NAME));
    		return this;
    	}
    	commonAction.inputText(APP_NAME, name);
    	logger.info("Input '" + name + "' into App Name field.");
    	return this;
    }

    public String getAppName() {
    	String name = commonAction.getElementAttribute(APP_NAME, "value");
    	logger.info("Retrieved app name: " + name);
    	return name;
    }    
    
    public StoreInformation inputHotline(String phone) {
    	commonAction.inputText(HOTLINE, phone);
    	logger.info("Input '" + phone + "' into Hotline field.");
    	return this;
    }    
    
    public StoreInformation inputEmail(String email) {
    	commonAction.inputText(EMAIL, email);
    	logger.info("Input '" + email + "' into Email field.");
    	return this;
    }
    
    public StoreInformation inputStoreAdress(String address) {
    	commonAction.inputText(ADDRESS, address);
    	logger.info("Input '" + address + "' into Store Address List field.");
    	return this;
    }
    
    public StoreInformation inputFacebookLink(String link) {
    	commonAction.inputText(FACEBOOK_LINK, link);
    	logger.info("Input '" + link + "' into Facebook Link field.");
    	return this;
    }
    
    public StoreInformation inputInstagramLink(String link) {
    	commonAction.inputText(INSTAGRAM_LINK, link);
    	logger.info("Input '" + link + "' into Instagram Link field.");
    	return this;
    }
    
    public StoreInformation inputYoutubeLink(String link) {
    	commonAction.inputText(YOUTUBE_LINK, link);
    	logger.info("Input '" + link + "' into Youtube Link field.");
    	return this;
    }

    public StoreInformation inputSEOTitle(String seoTitle) {
    	if (commonAction.isElementVisiblyDisabled(SEO_TITLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(SEO_TITLE));
    		return this;
    	}
    	commonAction.inputText(SEO_TITLE, seoTitle);
    	logger.info("Input '" + seoTitle + "' into SEO Title field.");
    	return this;
    }  
    
    public String getSEOTitle() {
    	String title = commonAction.getElementAttribute(SEO_TITLE, "value");
		logger.info("Retrieved SEO Title: %s".formatted(title));
		return title;
    }     

    public boolean getNoticeLogoToggleStatus() {
    	String status = commonAction.getElementAttribute(NOTICE_LOGO_TOGGLE, "value");
		logger.info("Retrieved status of Notice Logo Toggle: %s".formatted(status));
		return Boolean.parseBoolean(status);
    }      
    public boolean getRegisteredLogoToggleStatus() {
    	String status = commonAction.getElementAttribute(REGISTERED_LOGO_TOGGLE, "value");
    	logger.info("Retrieved status of Registered Logo Toggle: %s".formatted(status));
    	return Boolean.parseBoolean(status);
    }      
    
    public StoreInformation clickNoticeLogoToggle() {
    	if (commonAction.isElementVisiblyDisabled(NOTICE_LOGO_TOGGLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(NOTICE_LOGO_TOGGLE.findElement(By.xpath("./preceding-sibling::*"))));
    		return this;
    	}
    	commonAction.clickElement(NOTICE_LOGO_TOGGLE.findElement(By.xpath("./preceding-sibling::*")));
    	logger.info("Clicked on Notice Logo toggle button.");
        return this;
    }   
    
    public StoreInformation clickRegisteredLogoToggle() {
    	if (commonAction.isElementVisiblyDisabled(REGISTERED_LOGO_TOGGLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(REGISTERED_LOGO_TOGGLE));
    		return this;
    	}
    	commonAction.clickElement(REGISTERED_LOGO_TOGGLE.findElement(By.xpath("./preceding-sibling::*")));
    	logger.info("Clicked on Registered Logo toggle button.");
    	return this;
    }    
    
    public StoreInformation clickSaveBtn() {
    	commonAction.clickElement(SAVE_BTN);
    	logger.info("Clicked on Save button.");
        return this;
    }       

    /*Verify permission for certain feature*/
    public void verifyPermissionToSetStoreName(String permission) {
    	navigate();
		if (permission.contentEquals("A")) {
			inputShopName("Test Permission");
			Assert.assertEquals(getShopName(), "Test Permission"); 
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToSetAppName(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		inputAppName("Test Permission");
    		Assert.assertEquals(getAppName(), "Test Permission"); 
    	} else if (permission.contentEquals("D")) {
    		inputAppName("Test Permission");
    		Assert.assertNotEquals(getAppName(), "Test Permission");
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToSetHotlineAndEmail(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		inputHotline("0123000100");
    		inputEmail("test@gmail.com");
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToSetStoreAddress(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		inputStoreAdress("100 Wall Street");
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToSetSocialMedia(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		inputFacebookLink("https://www.facebook.com/Shopping-Heaven-107830291950514/");
    		inputInstagramLink("https://www.instagram.com/samsung_vietnam/");
    		inputYoutubeLink("https://www.youtube.com/watch?v=NqDVlK4rohc");
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToSetSEO(String permission) {
    	navigate();
    	if (permission.contentEquals("A")) {
    		inputSEOTitle("Test Permission");
    		Assert.assertEquals(getSEOTitle(), "Test Permission"); 
    	} else if (permission.contentEquals("D")) {
    		inputSEOTitle("Test Permission");
    		Assert.assertNotEquals(getSEOTitle(), "Test Permission"); 
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToEnableTradeLogo(String permission) {
    	navigate();
		clickNoticeLogoToggle();
		clickRegisteredLogoToggle();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(getNoticeLogoToggleStatus());
    		Assert.assertTrue(getRegisteredLogoToggleStatus());
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(getNoticeLogoToggleStatus());
    		Assert.assertFalse(getRegisteredLogoToggleStatus());
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    /*-------------------------------------*/      
    
    public void completeVerify() {
        soft.assertAll();
    }    
    
}
