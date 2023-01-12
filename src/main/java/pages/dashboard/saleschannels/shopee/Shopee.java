package pages.dashboard.saleschannels.shopee;

import static utilities.account.AccountTest.SHOPEE_COUNTRY;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import static utilities.account.AccountTest.*;

public class Shopee {

	final static Logger logger = LogManager.getLogger(Shopee.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Shopee(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sp-account .gs-button__green")
    WebElement CONNECT_SHOPEE_BTN;	
 
	public boolean isConnectShopeeBtnDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return commonAction.isElementDisplay(CONNECT_SHOPEE_BTN);
	}     
    
    public Shopee clickConnectShopee() {
    	commonAction.clickElement(CONNECT_SHOPEE_BTN);
    	logger.info("Clicked on 'Connect Shopee' button.");
    	return this;
    }      

    public void verifyPermissionToConnectShopee(String permission) {
    	if (permission.contentEquals("A")) {
    		if (isConnectShopeeBtnDisplayed()) {
        		clickConnectShopee();
        		new pages.thirdparty.Shopee(driver).performLogin(SHOPEE_COUNTRY, SHOPEE_USERNAME, SHOPEE_PASSWORD);
        		new HomePage(driver).navigateToPage("Account Information");
        		new AccountInformation(driver).clickDownloadShopeeProduct();
        		new HomePage(driver).navigateToPage("Account Management");
        		new AccountManagement(driver).deleteAccount();
    		}
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
}
