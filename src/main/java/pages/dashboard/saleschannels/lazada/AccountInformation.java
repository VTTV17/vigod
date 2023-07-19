package pages.dashboard.saleschannels.lazada;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import utilities.UICommonAction;

public class AccountInformation {

	final static Logger logger = LogManager.getLogger(AccountInformation.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public AccountInformation(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".stores .seller-info")
    WebElement AVAILABLE_STORE;	

	@FindBy(css = ".stores .uik-btn__success")
	WebElement OK_BTN;

    @FindBy(xpath = "//div[contains(@class,'seller-account')]//img[@class='logo-url']/parent::*/following-sibling::button")
    WebElement DISCONNECT_BTN;		
    
    @FindBy(xpath = "//*[@data-icon='sync-alt']/parent::*/following-sibling::button")
    WebElement SYNCHRONIZE_PRODUCT_BTN;		
    
	public AccountInformation clickOK() {
		commonAction.clickElement(OK_BTN);
		logger.info("Clicked on 'OK' button to complete store selection process.");
		return this;
	} 
	
    public AccountInformation selectStore() {
    	commonAction.clickElement(AVAILABLE_STORE);
    	logger.info("Selected store.");
    	clickOK();
    	return this;
    }      

    public AccountInformation clickDisconnect() {
    	commonAction.clickElement(DISCONNECT_BTN);
    	logger.info("Clicked on 'Disconnect' button.");
    	return this;
    }   
    
    public AccountInformation clickSynchronizeProducts() {
    	commonAction.clickElement(SYNCHRONIZE_PRODUCT_BTN);
    	logger.info("Clicked on 'Synchronize' button to sync products between Lazada and Gosell.");
    	return this;
    }
    
    public AccountInformation disconnectAccount() {
    	clickDisconnect();
    	new ConfirmationDialog(driver).clickOKBtn();
    	return this;
    } 
    
    public AccountInformation synchronizeProducts() {
    	clickSynchronizeProducts();
    	new ConfirmationDialog(driver).clickOKBtn();
    	return this;
    }   

    
    
}
