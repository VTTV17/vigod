package web.Dashboard.sales_channels.shopee.account_management;

import org.openqa.selenium.By;

public class AccountManagementElement {
    By loc_btnDisconnect = By.xpath("//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'broken-link.svg')]");
    By loc_btnDeleteAccount = By.xpath("//div[contains(@class,'ShopeeAccountManagement')]//i[contains(@style,'icon-delete')]");
    By loc_chkDeleteSyncedProduct = By.cssSelector(".boxCheckedDelete .uik-checkbox__label.green");
    By loc_btnDelete = By.cssSelector(".modal-footer .gs-button__red");
}
