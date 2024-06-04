package web.Dashboard.sales_channels.shopee.account_management;

import org.openqa.selenium.By;

public class AccountManagementElement {
    By loc_btnAddAccount = By.cssSelector(".gs-content-header-right-el > button:nth-child(1)");
    By loc_btnUpgrade = By.cssSelector(".gs-content-header-right-el > button:nth-child(2)");
    By loc_icnDisconnect = By.cssSelector("[style *= 'broken-link']");
    By loc_dlgConfirmDisconnectShopee = By.cssSelector(".confirm-modal");
    By loc_icnConnect = By.cssSelector("[style *= 'broken-unLink']");
    By loc_icnRemoveAccount = By.cssSelector("[style *= delete]");
    By loc_dlgConfirmRemoveAccount = By.cssSelector(".modalDeleteAccount");
    By loc_chkDeleteSyncedProduct = By.cssSelector(".boxCheckedDelete .uik-checkbox__label.green");
    By loc_btnDelete = By.cssSelector(".modal-footer .gs-button__red");
}
