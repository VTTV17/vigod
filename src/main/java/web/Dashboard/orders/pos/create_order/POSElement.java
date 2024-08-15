package web.Dashboard.orders.pos.create_order;

import org.openqa.selenium.By;

public class POSElement {
    By loc_ddvSelectedBranch = By.cssSelector("div[class='uik-select__wrapper pos-selector'] div[class='uik-select__valueWrapper']");

    By loc_lstBranches(String branchName) {
        return By.xpath("(//div[text() = '%s'])[last()]".formatted(branchName));
    }

    By loc_dlgConfirmSwitchBranch = By.cssSelector(".confirm-modal");
    By loc_dlgConfirmSwitchBranch_btnOK = By.cssSelector(".confirm-modal .gs-button__green");

    By loc_txtProductSearchBox = By.cssSelector("#dropdownSuggestionProduct input");

    By loc_lstProductResult(String productBarcode) {
        return By.xpath("//code[text() = '%s']".formatted(productBarcode));
    }

    By loc_txtCustomerSearchBox = By.cssSelector("#dropdownSuggestionCustomer input");

    By loc_lstCustomerResult(int customerId) {
        return By.xpath("//*[@class = 'mobile-customer-profile-row__right' and contains(., '%s')]".formatted(customerId));
    }
    By loc_lstCustomerResult(String name) {
    	return By.xpath("//*[contains(@class,'search-list__result')]//div[@class='full-name' and .=\"%s\"]".formatted(name));
    }

    By loc_txtProductQuantity(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//input".formatted(productName));
    }

    By loc_txtProductQuantity(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//input".formatted(productName, variationValue));
    }

    By loc_btnSelectIMEI(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//*[@class='select-IMEI errorIMEI']".formatted(productName));
    }

    By loc_btnSelectIMEI(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//*[@class='select-IMEI errorIMEI']".formatted(productName, variationValue));
    }

    By loc_btnSelectLot(String productName) {
        return By.xpath("//tr[td//div[text() ='%s']]//following-sibling::tr[1]//img".formatted(productName));
    }

    By loc_btnSelectLot(String productName, String variationValue) {
        return By.xpath("//tr[td//div[text() ='%s'] and td//span[text() ='%s']]//following-sibling::tr[1]//img".formatted(productName, variationValue));
    }

    By loc_dlgSelectIMEI_lstIMEI = By.cssSelector(".content:not(.selected)");
    By loc_dlgSelectIMEI_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgSelectLot_txtQuantity = By.cssSelector(".modal-lot-select .get-quantity input");
    By loc_dlgSelectLot_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_lblTotalAmount = By.cssSelector(".value-total");
    By loc_txtReceiveAmount = By.cssSelector(".order-pos__received-input");
    By loc_lnkViewAllPayment = By.cssSelector(".view-all-payment");
    By loc_lstPaymentMethod = By.cssSelector(".modal-content .payment-item");
    By loc_txtPOSReceiptCode = By.cssSelector(".payment-method-mpos-list-wraper input");
    By loc_chkNotApplyEarningPoint = By.cssSelector(".custom-check-box input");
    By loc_lblNotApplyEarningPoint = By.cssSelector(".custom-check-box div span");
    By loc_lblAvailablePoint = By.cssSelector(".text-availablePoint");
    By loc_txtInputPoint = By.cssSelector(".clear-up-down-btn");
    By loc_chkUsePoint = By.cssSelector(".order-in-store-use-point .form-check-label");
    By loc_btnPromotion = By.cssSelector(".title-promotion span");
    By loc_dlgDiscount_tabDiscountCode = By.cssSelector(".box-promotion-title:nth-child(1)");
    By loc_dlgDiscount_lstDiscountCode = By.cssSelector(".container-discount-code-item label");
    By loc_dlgDiscount_lstDiscountCodeValue = By.cssSelector(".container-discount-code-item .discount-code-type");
    By loc_dlgDiscount_tabDiscountAmount = By.cssSelector(".box-promotion-title:nth-child(2)");
    By loc_dlgDiscount_txtDiscountAmountValue = By.cssSelector("[name='fixAmount']");
    By loc_dlgDiscount_tabDiscountPercentage = By.cssSelector(".box-promotion-title:nth-child(3)");
    By loc_dlgDiscount_txtDiscountPercentValue = By.cssSelector("[name='percentage']");
    By loc_dlgDiscount_btnApply = By.cssSelector(".modal-content .color--gradient-blue");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_chkUsePointValue = By.cssSelector(".order-in-store-use-point .form-check-label");
    By loc_chkUsePointAction = By.cssSelector(".order-in-store-use-point .new-ui-checkbox-square");
    By loc_btnPrintOrder = By.cssSelector(".btn-print-order");
    By loc_btnPrintReceiptValue = By.cssSelector(".print-order__toggle input");
    By loc_btnPrintnReceiptAction = By.cssSelector(".modalPrintPos .print-order__toggle");
}
