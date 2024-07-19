package app.Buyer.checkout;

import org.openqa.selenium.By;

public class DeliveryAddressElement {
    By loc_selectAddress = By.xpath("//*[contains(@resource-id, 'tvLocation')]");
    By loc_lstcityDistrictWard = By.xpath("//*[contains(@resource-id, 'tvName')]");
    By loc_txtAddress = By.xpath("//*[contains(@resource-id, 'edtAddress1')]");
    By loc_txtAddress2 = By.xpath("//*[contains(@resource-id, 'edtAddress2')]");
    By loc_txtPhone = By.xpath("//*[contains(@resource-id, 'edtPhoneNumber')]");
    By loc_tvCountry = By.xpath("//*[contains(@resource-id, 'tvCountry')]");
    By loc_txtContactName = By.xpath("//*[contains(@resource-id, 'edtName')]");
    By loc_lstCountry = By.xpath("//*[contains(@resource-id, 'tvValue')]");
    By loc_tvPhoneCode = By.xpath("//*[contains(@resource-id, 'tvPhoneNumber')]");
    By loc_myAddress_lst_btnEdit = By.xpath("//*[contains(@resource-id, 'tvEditAddress')]");
    By loc_txtEmail = By.xpath("//*[contains(@resource-id, 'edtEmail')]");
    By loc_chkUpdateInMyProfile  = By.xpath("//*[contains(@resource-id, 'cbxUpdateAddressInCustomerProfile')]");
    By loc_txtCity = By.xpath("//*[ends-with(@resource-id, 'edtCityOutsideVN')]");
    By loc_txtZipCode = By.xpath("//*[ends-with(@resource-id, 'edtZipCode')]");
    By loc_myAddress_tvFullAddress = By.xpath("//*[ends-with(@resource-id, 'tvAddress')]");
    By loc_btnReset = By.xpath("//*[ends-with(@resource-id, 'tvReset')]");
}
