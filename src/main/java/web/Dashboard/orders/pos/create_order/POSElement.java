package web.Dashboard.orders.pos.create_order;

import org.openqa.selenium.By;

public class POSElement {
    By loc_ddvSelectedBranch = By.cssSelector("div[class='uik-select__wrapper pos-selector'] div[class='uik-select__valueWrapper']");

    By loc_lstBranches(String branchName) {
        return By.xpath("(//div[text() = \"%s\"])[last()]".formatted(branchName));
    }

    By loc_dlgConfirmSwitchBranch = By.cssSelector(".confirm-modal");
    By loc_dlgConfirmSwitchBranch_btnOK = By.cssSelector(".confirm-modal .gs-button__green");

    By loc_txtProductSearchBox = By.cssSelector("#dropdownSuggestionProduct input");

    By loc_lstProductResult(String productBarcode) {
        return By.xpath("//code[text() = \"%s\"]".formatted(productBarcode));
    }

    By loc_txtCustomerSearchBox = By.cssSelector("#dropdownSuggestionCustomer input");

    By loc_lstCustomerResult(int customerId) {
        return By.xpath("//*[@class = 'mobile-customer-profile-row__right' and contains(., \"%s\")]".formatted(customerId));
    }
    By loc_lstCustomerResult(String name) {
    	return By.xpath("//*[contains(@class,'search-list__result')]//div[@class='full-name' and .=\"%s\"]".formatted(name));
    }

    By loc_txtProductQuantity(String productName, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%s\"] and td//div[text()=\"%s\"]]//input".formatted(productName, unitName));
    }

    By loc_txtProductQuantity(String productName, String variationValue, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%s\"] and //td//span[text() =\"%s\"] and td//div[text()=\"%s\"]]//input".formatted(productName, variationValue, unitName));
    }

    By loc_btnSelectIMEI(String productName, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%s\"] and td//div[text()=\"%s\"]]//*[@class='select-IMEI errorIMEI']".formatted(productName, unitName));
    }

    By loc_btnSelectIMEI(String productName, String variationValue, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%\"] and td//span[text() =\"%s\"] and td//div[text()=\"%s\"]]//*[@class='select-IMEI errorIMEI']".formatted(productName, variationValue, unitName));
    }

    By loc_btnSelectLot(String productName, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%s\"] and td//div[text()=\"%s\"]]//following-sibling::tr[1]//img".formatted(productName, unitName));
    }

    By loc_btnSelectLot(String productName, String variationValue, String unitName) {
        return By.xpath("//tr[td//div[text() =\"%s\"] and td//span[text() =\"%s\"] and td//div[text()=\"%s\"]]//following-sibling::tr[1]//img".formatted(productName, variationValue, unitName));
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
    By loc_dlgDiscount_btnApply = By.xpath("//div[contains(@class,'order-instore-purchase-discount-modal')]//div[@class='modal-content']//button[2]");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_chkUsePointValue = By.cssSelector(".order-in-store-use-point .form-check-label");
    By loc_chkUsePointAction = By.cssSelector(".order-in-store-use-point .new-ui-checkbox-square");
    By loc_btnPrintOrder = By.cssSelector(".btn-print-order");
    By loc_btnPrintReceiptValue = By.cssSelector(".print-order__toggle input");
    By loc_btnPrintnReceiptAction = By.cssSelector(".modalPrintPos .print-order__toggle");
    By loc_lst_tltTotalPromotionApply = By.cssSelector("#order-discount-detail-popover .align-items-center");
    By loc_icnPromotionInfo = By.cssSelector(".group-promotion-inStore button");
    By loc_lblPromotionValue = By.cssSelector(".value-promotion");
    By loc_lblTaxValue = By.cssSelector(".value-tax");
    By loc_lblShippingFee = By.cssSelector(".value-delivery");
    By loc_lblSubTotalValue = By.cssSelector(".order-in-store-purchase-content__subTotal .align-self-baseline");
    By loc_lst_lblProductName = By.cssSelector(".order-in-store-purchase-cart-product-list__product-name");
    By loc_lblVariationName (String productName){
        return By.xpath("//div[contains(@class,'product-list')]//div[text() = \"%s\"]//following-sibling::span".formatted(productName));
    }
    By loc_lblSellingPriceForOne(int index){
        return By.xpath("((//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//ancestor::tr//div[@class = 'selling-price'])[1]".formatted(index));
    }
    By loc_lblSellingPriceAfterDiscountForOne(int index){
        return By.xpath("(//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//ancestor::tr//div[@class = 'price']".formatted(index));
    }
    By loc_lblSellingPriceTotal(String productName){
        return By.xpath("(//div[contains(@class,'product-list')]//div[text() = \"%s\"]//ancestor::tr//div[@class = 'selling-price'])[2]".formatted(productName));
    }
    By loc_lblPriceTotalAfterDiscount(int index){
        return By.xpath("(//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//ancestor::tr//div[@class='total-price']".formatted(index));
    }
    By loc_lblGift(String productName){
        return By.xpath("//div[contains(@class,'product-list')]//div[text() = \"%s\"]//preceding-sibling::div[@class='text-gift']".formatted(productName));
    }
    By loc_ddlPromotion(int index){
        return By.xpath("(//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//ancestor::tr//span[@class='group-promotion-item']".formatted(index));
    }
    By loc_tltPromotionApplyOnItem = By.cssSelector(".tippy-tooltip-content .align-items-center");
    
    By loc_chkDelivery = By.cssSelector(".delivery-group-info .form-check");
    By loc_ddlDelivery = By.cssSelector(".delivery-select-pos__selected-value");
    By loc_iconLoadingDeliveryProvider = By.cssSelector(".menu-list .loading-icon-wrapper");
    By loc_lblTotalEarningPoint = By.cssSelector(".group-view-loyaltyPoint");
    By loc_lblTotalQuantity = By.cssSelector(".count-product");
    By loc_icnEditDelivery = By.cssSelector(".edit-icon");
    By loc_lblCustomerNameAndPhone = By.cssSelector(".show-profile");
    By loc_lblDebt = By.xpath("//div[contains(@class,'show-profile')]/following-sibling::div//span[@class='color-blue']");
    By loc_ddlShippingPromotion = By.cssSelector(".group-discount-shipping");
    By loc_tltShippingPromotion = By.cssSelector("#popover-promotion-discount .align-items-center");
    By loc_btnComplete = By.cssSelector(".order-pos__btn-create-order");
    By loc_lblSelectedPaymentMethod = By.cssSelector(".payment-method-list-wrapper .selected-item span");
    By loc_lblUnit = By.cssSelector(".order-in-store-purchase-cart-product-list__product-row .unit");
    By loc_lblVariationByProductIndex(int index){
        return By.xpath("(//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//following-sibling::span[contains(@class, 'text-variation')]".formatted(index));
    }
    By loc_txtProductQuantity(int productIndex) {
        return By.xpath("(//div[@class='order-in-store-purchase-cart-product-list__product-name'])[%s]//ancestor::tr//input".formatted(productIndex));
    }
}
