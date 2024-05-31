package web.Dashboard.sales_channels.tiktok.account_management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.time.Duration;

public class AccountManagementPage extends AccountManagementElement {

	final static Logger logger = LogManager.getLogger(AccountManagementPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public AccountManagementPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    
    public void clickDisconnect() {
    	commonAction.click(loc_btnDisconnect);
    	logger.info("Clicked on 'Disconnect Shopee' button.");
    }
    
    public void clickDeleteConnectedAccount() {
    	commonAction.click(loc_btnDeleteAccount);
    	logger.info("Clicked on 'Delete Connected Account' button.");
    }
    
    public void clickDeleteSyncedProductCheckbox() {
    	commonAction.clickJS(loc_chkDeleteSyncedProduct);
    	logger.info("Clicked on 'Delete Synced Products' check box.");
    }
    
    public void clickDeleteBtnInConfirmationDialog() {
    	commonAction.click(loc_btnDelete);
    	logger.info("Clicked on 'Delete' button in Confirmation Dialog.");
    }
    
    public void deleteAccount() {
    	new HomePage(driver).hideFacebookBubble();
    	clickDisconnect();
    	new ConfirmationDialog(driver).clickOKBtn();
    	clickDeleteConnectedAccount();
    	clickDeleteSyncedProductCheckbox();
    	clickDeleteBtnInConfirmationDialog();
    }
    
}
