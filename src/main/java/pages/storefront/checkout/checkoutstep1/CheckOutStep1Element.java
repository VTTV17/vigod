package pages.storefront.checkout.checkoutstep1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckOutStep1Element {
    WebDriver driver;
    public CheckOutStep1Element(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = "#select-country-code")
    WebElement COUNTRY_DROPDOWN;
    @FindBy(css = "#input-name")
    WebElement FULL_NAME_INPUT;
    @FindBy(css = "#input-phone")
    WebElement PHONE_NUMBER_INPUT;
    @FindBy(css = "#input-address")
    WebElement ADDRESS_INPUT;
    @FindBy(css = "#select-city-code")
    WebElement CITY_PROVINCE_DROPDOWN;
    @FindBy(css = "#select-district-code")
    WebElement DISTRICT_DROPDOWN;
    @FindBy(css = "#select-ward-code")
    WebElement WARD_DROPDOWN;
    @FindBy(css = "#cod")
    WebElement COD;
    @FindBy(css = "bank-transfer")
    WebElement BANK_TRANSFER;
    @FindBy(css = "#paypal")
    WebElement PAYPAL;
    @FindBy(css = "#debt")
    WebElement DEBT;
    @FindBy(css = "#momo")
    WebElement MOMO;
    @FindBy(css = ".summary #checkout-footer-btn-continue")
    WebElement NEXT_BUTTON;
    @FindBy(css = "#input-address2")
    WebElement ADDRESS_2_INPUT;
    @FindBy(css = "#input-city")
    WebElement CITY_INPUT;
    @FindBy(css = "#select-state-code")
    WebElement STATE_REGION_PROVICE_DROPDOWN;
    @FindBy(css = "#input-zipCode")
    WebElement ZIP_CODE_INPUT;

}
