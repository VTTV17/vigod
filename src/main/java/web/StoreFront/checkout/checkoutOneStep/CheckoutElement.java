package web.StoreFront.checkout.checkoutOneStep;

import org.openqa.selenium.By;

public class CheckoutElement {
    By loc_lblAddress = By.cssSelector(".checkout-right [rv-text='models.deliveryInfo.fullShippingAddress']");
    By loc_icnEditShippingInfo = By.cssSelector(".edit-shipping-info--desktop");
    By loc_dlgShippingAddress_btnUpdate = By.cssSelector("#customer-address-wrapper .text-primary");
    By loc_dlgUpdateAddress_txtAddress = By.cssSelector("#address");
    By loc_dlgUpdateAddress_ddlCountry = By.cssSelector("#countryCode");

    By loc_dlgUpdateAddress_txtFullName = By.cssSelector("#contactName");
    By loc_dlgUpdateAddress_txtPhoneNumber = By.cssSelector("#phoneNumber");
    By loc_dlgUpdateAddress_txtEmail = By.cssSelector("#email");
    By loc_dlgUpdateAddress_ddlCityProvince = By.cssSelector("#cityCode");
    By loc_dlgUpdateAddress_ddlDistrict = By.cssSelector("#districtCode");
    By loc_dlgUpdateAddress_ddlWardTown = By.cssSelector("#wardCode");
    By loc_dlgUpdateAddress_btnConfirm = By.cssSelector("#form-new-shipping-address .btn-submit-shipping-address");
    By loc_dlgShippingAddress_btnConfirm = By.cssSelector("#form-default-shipping-address .btn-submit-shipping-address");
    By loc_dlgUpdateAddress_txtAddress2 = By.cssSelector("#address2");
    By loc_dlgUpdateAddress_ddlState = By.cssSelector("#stateCode");
    By loc_dlgUpdateAddress_txtZipCode  = By.cssSelector("#zipCode");
    By loc_dlgUpdateAddress_txtCity = By.cssSelector("#city");
    By loc_dlgUpdateAddress_lstOtherPhone = By.cssSelector("#phone-list .other-item-value");
    By loc_dlUpdateAddress_lstOtherPhoneName = By.cssSelector("#phone-list .other-item-title");
    By loc_dlgUpdateAddress_lstOtherEmail = By.cssSelector("#email-list .other-item-value");
    By loc_dlgUpdateAddress_lstOtherEmailName = By.cssSelector("#email-list .other-item-title");
    By loc_lblDiscountAmount= By.cssSelector(".payment-info .payment-info__value--discount");
    By loc_btnComplete = By.cssSelector(".btn-checkout--desktop");
    By loc_lstProductName = By.cssSelector(".prod-name");
    By loc_lblShippingFee = By.xpath("//div[contains(@rv-text,'models.shippingFeeAmount')]");

}
