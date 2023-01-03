package pages.dashboard.saleschannels.shopee;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class AccountManagement {

	final static Logger logger = LogManager.getLogger(AccountManagement.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public AccountManagement(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'broken-link.svg')]")
    WebElement DISCONNECT_BTN;	
    
    @FindBy(xpath = "//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'icon-delete')]")
    WebElement DELETE_ACCOUNT_BTN;	
    
    @FindBy(css = ".boxCheckedDelete .uik-checkbox__label.green")
    WebElement DELETE_SYNCED_PRODUCT_CHK;	
    
    @FindBy(css = ".modal-footer .gs-button__red")
    WebElement DELETE_BTN;	
    
    public AccountManagement clickDisconnect() {
    	commonAction.clickElement(DISCONNECT_BTN);
    	logger.info("Clicked on 'Disconnect Shopee' button.");
    	return this;
    }      
    
    public AccountManagement clickDeleteConnectedAccount() {
    	commonAction.clickElement(DELETE_ACCOUNT_BTN);
    	logger.info("Clicked on 'Delete Connected Account' button.");
    	return this;
    }   
    
    public AccountManagement clickDeleteSyncedProductCheckbox() {
    	commonAction.clickElement(DELETE_SYNCED_PRODUCT_CHK);
    	logger.info("Clicked on 'Delete Synced Products' check box.");
    	return this;
    }     
    
    public AccountManagement clickDeleteBtnInConfirmationDialog() {
    	commonAction.clickElement(DELETE_BTN);
    	logger.info("Clicked on 'Delete' button in Confirmation Dialog.");
    	return this;
    }      
    
    public AccountManagement deleteAccount() {
    	new HomePage(driver).hideFacebookBubble();
    	clickDisconnect();
    	new ConfirmationDialog(driver).clickOKBtn();
    	clickDeleteConnectedAccount();
    	clickDeleteSyncedProductCheckbox();
    	clickDeleteBtnInConfirmationDialog();
    	return this;
    }       
    
}
