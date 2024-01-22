package web.Dashboard.settings.storeinformation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class StoreInformation {
	
	final static Logger logger = LogManager.getLogger(StoreInformation.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public StoreInformation (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_tabStoreInfo = By.cssSelector("li:nth-child(2) > a.nav-link");
    By loc_txtShopName = By.id("shopName");
    By loc_txtAppName = By.id("appName");
    By loc_txtHotline = By.id("contactNumber");
    By loc_txtEmail = By.cssSelector(".info-container #email");
    By loc_txtAddress = By.id("addressList");
    By loc_txtFacebook = By.id("FACEBOOK");
    By loc_txtInstagram = By.id("INSTAGRAM");
    By loc_txtYoutube = By.id("YOUTUBE_VIDEO");
    By loc_txtSEOTitle = By.cssSelector("input#seoTitle");
    By loc_buttonNoticeLogo = By.id("noticeEnabled");
    By loc_btnRegisteredLogo = By.id("registeredEnabled");
    By loc_btnSave = By.cssSelector(".info-container .setting_btn_save");
    
    public StoreInformation navigate() {
    	clickStoreInformationTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (commonAction.getElements(loc_btnRegisteredLogo).size() >0) break;
    		commonAction.sleepInMiliSecond(500);
    	}
        return this;
    }

    public StoreInformation clickStoreInformationTab() {
    	commonAction.click(loc_tabStoreInfo);
    	logger.info("Clicked on Store Information tab.");
        return this;
    }
    
    public StoreInformation inputShopName(String name) {
    	commonAction.inputText(loc_txtShopName, name);
    	logger.info("Input '" + name + "' into Shop Name field.");
        return this;
    }
    
    public String getShopName() {
    	String name = commonAction.getAttribute(loc_txtShopName, "value");
    	logger.info("Retrieved shop name: " + name);
    	return name;
    }
    
    public StoreInformation inputAppName(String name) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtAppName).findElement(By.xpath("./parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtAppName)));
    		return this;
    	}
    	commonAction.inputText(loc_txtAppName, name);
    	logger.info("Input '" + name + "' into App Name field.");
    	return this;
    }

    public String getAppName() {
    	String name = commonAction.getAttribute(loc_txtAppName, "value");
    	logger.info("Retrieved app name: " + name);
    	return name;
    }    
    
    public StoreInformation inputHotline(String phone) {
    	commonAction.inputText(loc_txtHotline, phone);
    	logger.info("Input '" + phone + "' into Hotline field.");
    	return this;
    }    

    public String getHoline() {
    	String value = commonAction.getAttribute(loc_txtHotline, "value");
    	logger.info("Retrieved Hotline: " + value);
    	return value;
    }      
    
    public StoreInformation inputEmail(String email) {
    	commonAction.inputText(loc_txtEmail, email);
    	logger.info("Input '" + email + "' into Email field.");
    	return this;
    }

    public String getEmail() {
    	String value = commonAction.getAttribute(loc_txtEmail, "value");
    	logger.info("Retrieved Hotline: " + value);
    	return value;
    }     
    
    public StoreInformation inputStoreAdress(String address) {
    	commonAction.inputText(loc_txtAddress, address);
    	logger.info("Input '" + address + "' into Store Address List field.");
    	return this;
    }
    
    public StoreInformation inputFacebookLink(String link) {
    	commonAction.inputText(loc_txtFacebook, link);
    	logger.info("Input '" + link + "' into Facebook Link field.");
    	return this;
    }
    
    public StoreInformation inputInstagramLink(String link) {
    	commonAction.inputText(loc_txtInstagram, link);
    	logger.info("Input '" + link + "' into Instagram Link field.");
    	return this;
    }
    
    public StoreInformation inputYoutubeLink(String link) {
    	commonAction.inputText(loc_txtYoutube, link);
    	logger.info("Input '" + link + "' into Youtube Link field.");
    	return this;
    }

    public StoreInformation inputSEOTitle(String seoTitle) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtSEOTitle).findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtSEOTitle)));
    		return this;
    	}
    	commonAction.inputText(loc_txtSEOTitle, seoTitle);
    	logger.info("Input '" + seoTitle + "' into SEO Title field.");
    	return this;
    }  
    
    public String getSEOTitle() {
    	String title = commonAction.getAttribute(loc_txtSEOTitle, "value");
		logger.info("Retrieved SEO Title: %s".formatted(title));
		return title;
    }     

    public boolean getNoticeLogoToggleStatus() {
    	String status = commonAction.getAttribute(loc_buttonNoticeLogo, "value");
		logger.info("Retrieved status of Notice Logo Toggle: %s".formatted(status));
		return Boolean.parseBoolean(status);
    }      
    public boolean getRegisteredLogoToggleStatus() {
    	String status = commonAction.getAttribute(loc_btnRegisteredLogo, "value");
    	logger.info("Retrieved status of Registered Logo Toggle: %s".formatted(status));
    	return Boolean.parseBoolean(status);
    }      
    
    public StoreInformation clickNoticeLogoToggle() {
    	By chainedBy = new ByChained(loc_buttonNoticeLogo, By.xpath("./preceding-sibling::*"));
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_buttonNoticeLogo).findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(chainedBy)));
    		return this;
    	}
    	commonAction.click(chainedBy);
    	logger.info("Clicked on Notice Logo toggle button.");
        return this;
    }   
    
    public StoreInformation clickRegisteredLogoToggle() {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnRegisteredLogo).findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnRegisteredLogo)));
    		return this;
    	}
    	commonAction.click(new ByChained(loc_btnRegisteredLogo, By.xpath("./preceding-sibling::*")));
    	logger.info("Clicked on Registered Logo toggle button.");
    	return this;
    }    
    
    public StoreInformation clickSaveBtn() {
    	commonAction.click(loc_btnSave);
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
    
}
