package web.Dashboard.reservation.POSReservation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class POSReservationElement {
    WebDriver driver;
    public POSReservationElement(WebDriver driver) {
        this.driver = driver;
    }
    By loc_txtSearchService = By.cssSelector("#dropdownBoxSuggestionService input");
    By loc_blkSearchService = By.cssSelector(".reservation-instore-purchase");
    By loc_txtSearchCustomer = By.cssSelector("#dropdownSuggestionCustomer input");
    By loc_icnAddCustomer = By.cssSelector(".button-add-new");
    By loc_lst_lblServiceSuggestion = By.cssSelector(".product-item-row h6");
    By loc_ddlSelectLocator = By.cssSelector("#reservation-select-location");
    By loc_ddlSelectTime = By.cssSelector("#reservation-select-time-slot");
    By loc_txtQuantity = By.cssSelector("#reservation-input-quantity");
    By loc_dlgServiceInformatiion_btnAdd = By.cssSelector(".modal-footer .gs-button__green");
    By loc_btnDiscount = By.cssSelector(".color-blue span");
    By loc_lblServiceSuggestionFirst = By.cssSelector(".product-item-row:first-of-type h6");
    By loc_lst_lblCustomerName = By.cssSelector(".full-name");
    By loc_dlgCreateCustomer_txtFullName = By.cssSelector("#fullName");
    By loc_dlgCreateCustomer_txtPhoneNumber = By.cssSelector("#phone");
    By loc_dlgCreateCustomer_lstPhoneCode = By.cssSelector(".phone-code button div");
    By loc_dlgCreateCustomer_btnPhoneCode = By.cssSelector(".phone-code");
    By loc_dlgCreateCustomer_btnAdd = By.cssSelector(".modal-footer .gs-button__green");
    By loc_ddvDiscountCode = By.xpath("(//div[@class='uik-select__label'])[1]");
    By loc_ddvDiscountAmount = By.xpath("(//div[@class='uik-select__label'])[2]");
    By loc_ddvDiscountPercent = By.xpath("(//div[@class='uik-select__label'])[3]");
    By loc_ddlDiscountType = By.xpath("//div[@class='uik-select__valueWrapper']");
    By loc_txtDiscountAmount = By.xpath("//input[@name='discount']");
    By loc_txtDiscountCode = By.cssSelector("#input-discount");
    By loc_txtDiscountPercent = By.cssSelector("#discount");
    By loc_btnApply = By.cssSelector(".action button");
    By loc_lblError = By.cssSelector(".error");
    By loc_btnCreateReservation = By.cssSelector(".gs-button__green");
    By loc_dlgDiscount = By.cssSelector(".reservation-instore-purchase-discount-modal");

}
