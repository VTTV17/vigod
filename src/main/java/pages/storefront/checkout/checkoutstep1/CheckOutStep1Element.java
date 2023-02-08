package pages.storefront.checkout.checkoutstep1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

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
    @FindBy(xpath = "//div[contains(@class,'item-phone')]/span[1]")
    List<WebElement> PHONE_NAMES;
    @FindBy(xpath = "//div[contains(@class,'item-phone')]/span[2]")
    List<WebElement> OTHER_PHONE_LIST;
    @FindBy(css = "#input-email")
    WebElement EMAIL_INPUT;
    @FindBy(xpath = "//div[contains(@class,'item-email')]/span[1]")
    List<WebElement> EMAIL_NAMES;
    @FindBy(xpath = "//div[contains(@class,'item-email')]/span[2]")
    List<WebElement> OTHER_EMAIL_LIST;
    @FindBy(css = ".bi-caret-up-fill")
    WebElement ARROW_ICON_NEXT_TO_TOTAL_AMOUNT;
    @FindBy(xpath = "(//td[contains(@class,'sub-price')])[2]/span")
    WebElement DISCOUNT_AMOUNT ;
}
