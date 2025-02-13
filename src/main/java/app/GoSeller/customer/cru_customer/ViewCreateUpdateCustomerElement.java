package app.GoSeller.customer.cru_customer;

import org.openqa.selenium.By;

public class ViewCreateUpdateCustomerElement {
    By loc_txtFullName = By.xpath("//*[contains(@resource-id, 'edtCustomerFullName')]");
    By loc_txtPhoneNumber = By.xpath("//*[contains(@resource-id, 'edtPhoneNumber')]");
    By loc_txtEmail = By.xpath("//*[contains(@resource-id, 'edtCustomerEmail')]");
    By loc_ddlPhoneCode = By.xpath("//*[contains(@resource-id, 'tvLabelCustomerPhoneCode')]");
    By loc_ddlCountry = By.xpath("//*[contains(@resource-id, 'edtCustomerCountry')]");
    By loc_Address = By.xpath("//*[contains(@resource-id, 'edtCustomerAddress')]");
    By loc_Birthday = By.xpath("//*[contains(@resource-id, 'edtCustomerBirthday')]");
    By loc_Gender = By.xpath("//*[contains(@resource-id, 'edtCustomerGender')]");
    By loc_txtTag = By.xpath("//*[contains(@resource-id, 'edtCustomerTag')]");
    By loc_txtNote = By.xpath("//*[contains(@resource-id, 'edtNote')]");
    // address screen
    By loc_address_txtAddress = By.xpath("//*[contains(@resource-id, 'etAddress')]");
    By loc_address_ddlCityProvince = By.xpath("//*[contains(@resource-id, 'etCity')]");
    By loc_address_ddlDistrict = By.xpath("//*[contains(@resource-id, 'etDistrict')]");
    By loc_address_ddlWard = By.xpath("//*[contains(@resource-id, 'etWard')]");
    By loc_lstCountry_cityProvice_district_ward = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By loc_address_txtAddress2 = By.xpath("//*[contains(@resource-id, 'etAddress2')]");
    By loc_address_ddlState = By.xpath("//*[contains(@resource-id, 'etState')]");
    By loc_address_txtCity = By.xpath("//*[contains(@resource-id, 'etCityOutsideVietnam')]");
    By loc_address_txtZipCode = By.xpath("//*[contains(@resource-id, 'etZipCode')]");
}
