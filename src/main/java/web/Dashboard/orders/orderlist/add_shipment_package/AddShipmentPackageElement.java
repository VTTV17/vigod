package web.Dashboard.orders.orderlist.add_shipment_package;

import org.openqa.selenium.By;

public class AddShipmentPackageElement {
    By loc_lblIMEIItemQuantity = By.xpath("//*[contains(@class,'select-imei-number')]/preceding-sibling::*[@class = 'quantity']");
    By loc_txtLotQuantity = By.cssSelector("//*[contains(@class, 'section-lot-location__last-tr')]/preceding-sibling::*//*[@class = 'quantity']//input");
    By lnkSelectLot = By.cssSelector(".section-lot-location .gs-fake-link");
    By loc_icnRemoveSelectedLot = By.cssSelector(".section-lot-location__last-tr img");
    By loc_dlgSelectLot = By.cssSelector(".modal-lot-select");
    By loc_dlgSelectLot_txtConfirmQuantity = By.cssSelector(".modal-lot-select .get-quantity input");
    By loc_dlgSelectLot_btnConfirm = By.cssSelector(".modal-lot-select .gs-button__green");
    By loc_lnkSelectIMEI = By.cssSelector(".select-imei-number img");
    By loc_dlgSelectIMEI = By.cssSelector(".select-imei-modal");
    By loc_dlgSelectIMEI_lstRemoveIMEI = By.cssSelector(".select-imei-modal .d-desktop-flex .code__multi-value__remove");
    By loc_dlgSelectIMEI_lstIMEI = By.cssSelector(".select-imei-modal .d-desktop-flex .code div");
    By loc_dlgSelectIMEI_btnSave = By.cssSelector(".select-imei-modal .gs-button__green");
    By loc_ddlDeliveryMethod = By.cssSelector("#deliveryMethod");
    By loc_icnUnfortunately = By.cssSelector(".icon--warning");
    By loc_chkAsSameAsSenderInformation = By.cssSelector("input[name='areReturnAndSenderTheSame']");
    By loc_txtPackageInformationWeight = By.xpath("//*[@name='weight']/parent::div/parent::div/preceding-sibling::input");
    By loc_txtPackageInformationLength = By.xpath("//*[@name='length']/parent::div/parent::div/preceding-sibling::input");
    By loc_txtPackageInformationWidth = By.xpath("//*[@name='width']/parent::div/parent::div/preceding-sibling::input");
    By loc_txtPackageInformationHeight = By.xpath("//*[@name='height']/parent::div/parent::div/preceding-sibling::input");
    By loc_ddlAhamoveSenderProvince = By.cssSelector("[name = 'senderProvince']");
    By loc_ddlAhamoveSenderDistrict = By.cssSelector("[name = 'senderDistrict']");
    By loc_ddlAhamoveSenderCommune = By.cssSelector("[name = 'senderCommune']");
    By loc_ddlAhamoveReceiverProvince = By.cssSelector("[name = 'receiverProvince']");
    By loc_ddlAhamoveReceiverDistrict = By.cssSelector("[name = 'receiverDistrict']");
    By loc_ddlAhamoveReceiverCommune = By.cssSelector("[name = 'receiverCommune']");
    String str_ddvAhamoveReceiverProvince = "//*[@name='receiverProvince']//*[@value = '%s']";
    String str_ddvAhamoveReceiverDistrict = "//*[@name='receiverDistrict']//*[@value = '%s']";
    String str_ddvAhamoveReceiverCommune = "//*[@name='receiverCommune']//*[@value = '%s']";
    By loc_btnReEstimateDeliveryFee = By.cssSelector(".btn-estimate-delivery-fee, [data-testid='btn-estimate-delivery-fee']");
    By loc_btnConfirm = By.cssSelector(".gss-content-header--action-btn .gs-button__green");
    By loc_dlgConfirm = By.cssSelector(".confirm-modal");
    By loc_dlgConfirm_btnYes = By.cssSelector(".confirm-modal .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
