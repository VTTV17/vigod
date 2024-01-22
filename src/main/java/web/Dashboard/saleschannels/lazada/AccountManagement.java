package web.Dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class AccountManagement {

	final static Logger logger = LogManager.getLogger(AccountManagement.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public AccountManagement(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnDisconnect = By.xpath("//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'broken-link.svg')]");
    By loc_btnDeleteAccount = By.xpath("//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'icon-delete')]");
    By loc_chkDeleteSyncedProduct = By.cssSelector(".boxCheckedDelete .uik-checkbox__label.green");
    By loc_btnDelete = By.cssSelector(".modal-footer .gs-button__red");
    
    public AccountManagement clickDisconnect() {
    	commonAction.click(loc_btnDisconnect);
    	logger.info("Clicked on 'Disconnect Shopee' button.");
    	return this;
    }      
    
    public AccountManagement clickDeleteConnectedAccount() {
    	commonAction.click(loc_btnDeleteAccount);
    	logger.info("Clicked on 'Delete Connected Account' button.");
    	return this;
    }   
    
    public AccountManagement clickDeleteSyncedProductCheckbox() {
    	commonAction.click(loc_chkDeleteSyncedProduct);
    	logger.info("Clicked on 'Delete Synced Products' check box.");
    	return this;
    }     
    
    public AccountManagement clickDeleteBtnInConfirmationDialog() {
    	commonAction.click(loc_btnDelete);
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
