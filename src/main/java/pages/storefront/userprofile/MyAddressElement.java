package pages.storefront.userprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MyAddressElement {
    WebDriver driver;
    public MyAddressElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(id = "countryCode")
    WebElement COUNTRY;
    @FindBy(id = "address")
    WebElement ADDRESS_INPUT;
    @FindBy(id = "locationCode")
    WebElement CITY_DROPDOWN;
    @FindBy(id = "districtCode")
    WebElement DISTRICT_DROPDOWN;
    @FindBy(id = "wardCode")
    WebElement WARD_DROPDOWN;
    @FindBy(id = "address2")
    WebElement ADDRESS2_INPUT;
    @FindBy(id = "stateCode")
    WebElement STATE_DROPDOWN;
    @FindBy(id = "city")
    WebElement CITY_INPUT;
    @FindBy(id = "zipCode")
    WebElement ZIPCODE_INPUT;
    @FindBy(xpath = "//div[@class='box_submit']//button[@type='submit']")
    WebElement SAVE_BTN;
    @FindBy(css = ".btn-cancel")
    WebElement CANCEL_BTN;
    @FindBy(css = "#user-full-info .title span")
    WebElement MY_ADDRESS_TITLE;
    @FindBy(xpath = "//label[@for='countryCode']")
    WebElement COUNTRY_LBL;
    @FindBy(xpath = "//label[@for='address']")
    WebElement ADDRESS_LBL;
    @FindBy(xpath = "//label[@for='locationCode']")
    WebElement CITY_VN_LBL;
    @FindBy(xpath = "//label[@for='districtCode']")
    WebElement DISTRICT_LBL;
    @FindBy(xpath = "//label[@for='wardCode']")
    WebElement WARD_LBL;
    @FindBy(xpath = "//label[@for='address2']")
    WebElement ADDRESS2_LBL;
    @FindBy(xpath = "//label[@for='stateCode']")
    WebElement STATE_CODE_LBL;
    @FindBy(xpath = "//label[@for='city']")
    WebElement CITY_NONVN_LBL;
    @FindBy(xpath = "//label[@for='zipCode']")
    WebElement ZIP_CODE_LBL;
}
