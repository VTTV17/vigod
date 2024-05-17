package web.Dashboard.orders.orderlist.order_list;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

public class OrderManagementElement {
    By btnPrintOrder = By.cssSelector(".button-print-order");
    By icnSetting = By.cssSelector(".icon-setting");
    By btnSaveOnEditOrderDisplaySetting = By.cssSelector(".color--primary");
    By loc_tmpRecords = By.cssSelector(".transaction-row");
    By loc_btnExport = By.cssSelector(".order-list-management .button-v2");
    By loc_btnExportOrder = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[1]");
    By loc_btnExportOrderByProduct = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[2]");
    By loc_btnExportHistory = By.xpath("(//div[contains(@class,'uik-menuDrop__list')]//button)[last()]");
    By loc_btnConfirmOrder = By.id("btn-readyToShip");
    By loc_btnShipmentOK = By.cssSelector(".ready-to-ship-confirm__btn-wrapper .gs-button__green");
    By loc_btnDelivered = By.cssSelector(".gs-button__green");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_chkOrder = By.cssSelector("tbody tr input");
    enum BulkActions {
        printReceipt, printShippingLabel, markAsDelivered, addTags, removeTags;
        static List<BulkActions> getAllActions() {
            return Arrays.asList(BulkActions.values());
        }
    }
    By loc_ddlActions = By.cssSelector(".actions div");
    By loc_dlgSelectPrintSize = By.cssSelector(".gs-select-print-size-modal");
    By loc_dlgAddTags = By.cssSelector(".add-tag-modal");
    By loc_dlgAddTags_txtTag = By.cssSelector(".add-tag-modal input");
    By loc_dlgAddTags_lstTag = By.cssSelector(".item");
    By loc_dlgAddTags_btnAdd = By.cssSelector(".add-tag-modal .gs-button__green");
    By loc_dlgAddTags_icnClose = By.cssSelector(".add-tag-modal .close");
    By loc_dlgRemoveTags = By.cssSelector(".remove-tag-modal");
    By loc_dlgRemoveTags_lstTags = By.cssSelector(".tag");
    By loc_dlgRemoveTags_btnRemoveTags = By.cssSelector(".remove-tag-modal .gs-button__green");
    By loc_dlgRemoveTags_btnCancel = By.cssSelector(".remove-tag-modal .gs-button__gray--outline");
    By loc_dlgConfirmExportOrder = By.cssSelector(".confirm-modal-v2");
    By loc_dlgConfirmExportOrder_btnOK = By.cssSelector(".confirm-modal-v2 .color--primary");
    By loc_dlgExportOrderByProduct = By.cssSelector(".select-product-modal");
    By loc_dlgExportOrderByProduct_chkProduct = By.cssSelector(".select-product-modal label:not(.header) input");
    By loc_dlgExportOrderByProduct_btnExportByProduct = By.cssSelector(".select-product-modal .gs-button__green");
    By loc_dlgExportOrderByProduct_btnCancel = By.cssSelector(".select-product-modal .gs-button__gray--outline");
    By loc_icnDownloadExportFile = By.xpath("//*[contains(@class, 'd-desktop-flex')]//*[contains(text(), 'EXPORT_ORDER')]//following-sibling::div/div/img[@alt='download-file-blue']");
    By loc_btnCancel = By.cssSelector(".modal-body button.gs-button__gray--outline");
    By loc_btnExportByProduct = By.cssSelector(".modal-body button.gs-button__green");
}
