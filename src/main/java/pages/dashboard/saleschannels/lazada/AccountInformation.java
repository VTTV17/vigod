package pages.dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import utilities.UICommonAction;

public class AccountInformation {

	final static Logger logger = LogManager.getLogger(AccountInformation.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public AccountInformation(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_lblAvailableStore = By.cssSelector(".stores .seller-info");
    By loc_btnOK = By.cssSelector(".stores .uik-btn__success");
    By loc_btnDisconnect = By.xpath("//div[contains(@class,'seller-account')]//img[@class='logo-url']/parent::*/following-sibling::button");
    By loc_btnSynchronizeProduct = By.xpath("//*[@data-icon='sync-alt']/parent::*/following-sibling::button");
	
	public AccountInformation clickOK() {
		commonAction.click(loc_btnOK);
		logger.info("Clicked on 'OK' button to complete store selection process.");
		return this;
	} 
	
    public AccountInformation selectStore() {
    	commonAction.click(loc_lblAvailableStore);
    	logger.info("Selected store.");
    	clickOK();
    	return this;
    }      

    public AccountInformation clickDisconnect() {
    	commonAction.click(loc_btnDisconnect);
    	logger.info("Clicked on 'Disconnect' button.");
    	return this;
    }   
    
    public AccountInformation clickSynchronizeProducts() {
    	commonAction.click(loc_btnSynchronizeProduct);
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
