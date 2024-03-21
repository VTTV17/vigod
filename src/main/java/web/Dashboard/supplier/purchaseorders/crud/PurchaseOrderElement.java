package web.Dashboard.supplier.purchaseorders.crud;

import org.openqa.selenium.By;

public class PurchaseOrderElement {
    By loc_icnRemoveSupplier = By.cssSelector(".d-desktop-flex [alt='remove supplier']");
    By loc_txtSupplierSearchBox = By.xpath("(//*[@class = 'search-box']//input)[1]");
    String str_ddvSupplier = "//*[text() = '%s']";
    By loc_icnRemoveProduct = By.cssSelector("[alt='remove product']");
    By loc_txtProductSearchBox = By.cssSelector(".clearfix .product-list-wrapper input[placeholder]");
    String str_ddvProduct = "//*[text() = '%s']";
    By loc_lnkSelectLot = By.cssSelector("td .cursor--pointer");
    By loc_dlgSelectLot = By.cssSelector(".select-lot-date-modal");

    By loc_lnkAddIMEI = By.xpath("//td/div[contains(@class, 'input')]/div/following-sibling::span");
    By loc_dlgAddIMEI = By.cssSelector(".imei-serial-modal");
    By loc_dlgAddIMEI_txtInputIMEI = By.cssSelector(".imei-serial-modal  .desktop-view input");
    By loc_dlgAddIMEI_btnSave = By.cssSelector(".imei-serial-modal  .gs-button__green");
    By loc_txtImportPrice = By.xpath("//*[contains(@id, 'importPrice')]/parent::div/parent::div/preceding-sibling::input");
    By loc_btnChangePaymentMethod = By.xpath("//*[contains(@class, 'payment-method-form-editor')]//div[@class = 'gs-widget__header-right-title ']");
    By loc_dlgChoosePaymentMethod = By.cssSelector(".lot-editor-modal");
    By loc_dlgChoosePaymentMethod_chkPaymentMethod = By.cssSelector(".lot-editor-modal input");
    By loc_dlgChoosePaymentMethod_btnConfirm = By.cssSelector(".lot-editor-modal .gs-button__green");
    By loc_chkPaymentMethod = By.cssSelector(".payment-method-form-editor input:not(#to-accounting)");
    By loc_txtPaymentAmount = By.cssSelector(".cur-input--unit");
    By loc_ddvSelectedBranch = By.cssSelector(".order-information .uik-select__valueRendered");
    String str_ddvBranch = "//*[text() = '%s']";
    By loc_btnCreateOrder = By.cssSelector(".purchase-order-form-editor .gs-button__green.action");
    By loc_btnCreateAndApprove = By.cssSelector(".purchase-order-form-editor .gs-button__green.action");
    /**
     * 0: Create order, 1: Create and Approve
     */
    By loc_btnCreateOptions = By.cssSelector(".uik-dropdown-item__wrapper");
    By loc_icnCreateOptions = By.cssSelector(".purchase-order-form-editor .gs-button__green.icon");
    By loc_btnSave = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    By loc_btnApprove = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    By loc_btnImportGoods = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_dlgConfirmImportGoods = By.cssSelector(".purchase-order-set-avg-cost-price-modal");
    By loc_dlgConfirmImportGoods_textBeforeCost = By.xpath("//*[contains(@name, 'beforePurchaseOrder')]/parent::div/parent::div/preceding-sibling::input");
    By loc_dlgConfirmImportGoods_textAfterCost = By.xpath("//*[contains(@name, 'afterPurchaseOrder')]/parent::div/parent::div/preceding-sibling::input");
    By loc_dlgConfirmImportGoods_btnUpdate = By.cssSelector(".purchase-order-set-avg-cost-price-modal .gs-button__green");
    /**
     * 0: Change
     * 1: Cancel
     * 2: Print
     */
    By loc_ddlActions = By.cssSelector(".gs-dropdown-action > .actions > div");

    By loc_dlgConfirmCancel = By.cssSelector(".confirm-modal");
    By loc_dlgConfirmCancel_btnOK = By.cssSelector(".confirm-modal .gs-button__red");
    By loc_btnPurchaseOrderHistory = By.cssSelector(".purchase-order-history-header");
    By loc_dlgPurchaseOrderHistory = By.cssSelector(".purchase-order-history-modal");
}
