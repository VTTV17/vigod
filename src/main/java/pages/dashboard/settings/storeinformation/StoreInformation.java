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
    
    public StoreInformation inputAppName(String name) {
    	if (commonAction.isElementVisiblyDisabled(APP_NAME.findElement(By.xpath("./parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(APP_NAME));
    		return this;
    	}
    	commonAction.inputText(APP_NAME, name);
    	logger.info("Input '" + name + "' into App Name field.");
    	return this;
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
    	if (commonAction.isElementVisiblyDisabled(SEO_TITLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*")))) {
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

    public StoreInformation clickNoticeLogoToggle() {
    	if (commonAction.isElementVisiblyDisabled(NOTICE_LOGO_TOGGLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(NOTICE_LOGO_TOGGLE));
    		return this;
    	}
    	commonAction.clickElement(NOTICE_LOGO_TOGGLE);
    	logger.info("Clicked on Notice Logo toggle button.");
        return this;
    }   
    
    public StoreInformation clickRegisteredLogoToggle() {
    	if (commonAction.isElementVisiblyDisabled(REGISTERED_LOGO_TOGGLE.findElement(By.xpath("./parent::*/parent::*/parent::*/parent::*/parent::*/parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(REGISTERED_LOGO_TOGGLE));
    		return this;
    	}
    	commonAction.clickElement(REGISTERED_LOGO_TOGGLE);
    	logger.info("Clicked on Registered Logo toggle button.");
    	return this;
    }    
    
    public StoreInformation clickSaveBtn() {
    	commonAction.clickElement(SAVE_BTN);
    	logger.info("Clicked on Save button.");
        return this;
    }       

    public void completeVerify() {
        soft.assertAll();
    }    
    
}
