package web.Dashboard.marketing.affiliate.partner;

import org.openqa.selenium.By;

public class CreateEditPartnerElement {
    By loc_chkReseller_action = By.xpath("//input[@name='RESELLER']/parent::label");
    By loc_chkReseller_value = By.cssSelector("input[name='RESELLER']");
    By loc_chkDropship_action = By.xpath("//input[@name='DROP_SHIP']/parent::label");
    By loc_chkDropship_value = By.cssSelector("input[name='DROP_SHIP']");
    By loc_txtName = By.id("name");
    By loc_txtEmail = By.id("email");
    By loc_txtPhoneNumber = By.id("phoneNumber");
    By loc_txtAddress = By.id("address");
    By loc_ddlCityProvince = By.id("cityCode");
    By loc_ddlDistrict = By.id("districtCode");
    By loc_ddlWard = By.id("wardCode");
    By loc_btnSave = By.cssSelector(".btn-save");
}
