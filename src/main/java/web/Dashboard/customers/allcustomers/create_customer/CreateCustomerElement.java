package web.Dashboard.customers.allcustomers.create_customer;

import org.openqa.selenium.By;

public class CreateCustomerElement {

    By loc_txtFullName = By.id("fullName");
    By loc_txtPhone = By.id("phone");
    By loc_ddlPhoneCode = By.cssSelector(".phone-code");
    By loc_txtEmail = By.id("email");
    By loc_txtBirthday = By.cssSelector(".birthday-date input");
    By loc_ddlCountry = By.id("country");
    By loc_txtAddress1 = By.id("address");
    By loc_txtAddress2 = By.id("address2");
    By loc_ddlProvince = By.id("city");
    By loc_ddlDistrict = By.id("district");
    By loc_ddlWard = By.id("ward");
    By loc_txtCity = By.id("cityName");
    By loc_txtZipCode = By.id("zipCode");
    By loc_chkCustomerCreation = By.cssSelector(".custom-check-box");
    
    By loc_txtTags = By.cssSelector(".tags input");
    By loc_btnAdd = By.cssSelector(".modal-footer > .gs-button__green");
}
