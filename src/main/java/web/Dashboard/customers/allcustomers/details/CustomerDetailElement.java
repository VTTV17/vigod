package web.Dashboard.customers.allcustomers.details;

import org.openqa.selenium.By;

public class CustomerDetailElement {
    By loc_txtEmail = By.id("email");
    By loc_txtPhone = By.id("phone");
    By loc_txtPhoneCode = By.cssSelector(".row.phone-email .phone-code");
    By loc_txtBirthday = By.cssSelector(".birthday-date input");
    By loc_ddlCountry = By.id("country");
    By loc_txtAddress = By.id("address");
    By loc_ddlProvince = By.id("province");
    By loc_ddlDistrict = By.id("district");
    By loc_ddlWard = By.id("ward");
    By loc_txtAddress2 = By.id("address2");
    By loc_txtCity = By.id("city");
    By loc_txtZipcode = By.id("zipCode");
    By loc_btnCancel = By.cssSelector(".btn-cancel");
    By loc_btnSave = By.xpath("//button[contains(@class,'btn-save') and not(contains(@class,'gs-atm--disable'))]");
    By loc_btnStatus = By.cssSelector(".button-user-status button");
    By loc_ddlStatus = By.cssSelector(".dropdown_item_select");
    By loc_btnAssignPartner = By.cssSelector("#dropdownPartnerButton");
    By loc_ddlAssignPartner = By.cssSelector(".partner-item-list");
    By loc_ddlStaff = By.cssSelector("#responsibleStaffUserId");
    By loc_ddlStaffOption = By.cssSelector("#responsibleStaffUserId option");
    By loc_btnConfirmPayment = By.id("btn-print");
    By loc_dlgConfirmPayment = By.cssSelector(".confirmation-payment-modal");
    By loc_txtNote = By.id("note");
    By loc_txtCompany = By.id("companyName");
    By loc_txtBankBranch = By.id("bankBranchName");
    By loc_tabGeneral = By.xpath("//img[contains(@src, 'ico-general.svg')]/parent::*");
    By loc_tabBank = By.xpath("//img[contains(@src, 'credit-card.svg')]/parent::*");
}
