package app.Buyer.checkout;

import org.openqa.selenium.By;

public class CheckoutOneStepElement {
    By loc_delivery_icnArrow = By.xpath("//*[contains(@resource-id, 'ivArrow')]");
    By loc_delivery_selectAddress = By.xpath("//*[contains(@resource-id, 'tvLocation')]");
    By loc_delivery_lstcityDistrictWard = By.xpath("//*[contains(@resource-id, 'tvName')]");
    By loc_delivery_txtAddress = By.xpath("//*[contains(@resource-id, 'edtAddress')]");
    By loc_delivery_txtPhone = By.xpath("//*[contains(@resource-id, 'edtPhoneNumber')]");
    By loc_delivery_ddlCountry = By.xpath("//*[contains(@resource-id, 'tvCountry')]");
    By loc_delivery_txtContactName = By.xpath("//*[contains(@resource-id, 'edtName')]");
    By loc_delivery_lstCountry = By.xpath("//*[contains(@resource-id, 'tvValue')]");
    By loc_delivery_ddlPhoneCode = By.xpath("//*[contains(@resource-id, 'tvPhoneCountry')]");
}
