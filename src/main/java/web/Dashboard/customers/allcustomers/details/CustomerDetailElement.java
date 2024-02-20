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
}
