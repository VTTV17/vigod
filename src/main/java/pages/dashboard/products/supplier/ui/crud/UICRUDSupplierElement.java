package pages.dashboard.products.supplier.ui.crud;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

class UICRUDSupplierElement {
    WebDriver driver;
    UICRUDSupplierElement(WebDriver driver) {
      this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    /* UI */
    @FindBy(css = "[class *=gss-content-header] a")
    WebElement UI_BACK_TO_SUPPLIER_MANAGEMENT_PAGE;

    @FindBy(css = "[class *=gss-content-header] a +*")
    WebElement UI_PAGE_TITLE;

    @FindBy(css = ".btn-save")
    WebElement UI_SAVE_BTN;

    @FindBy(css = ".btn-cancel")
    WebElement UI_CANCEL_BTN;

    @FindBy(css = ".gs-button__red--outline")
    WebElement UI_DELETE_BTN;

    @FindBy(css = "[for = name]")
    WebElement UI_SUPPLIER_NAME;

    @FindBy(css = "[for = code]")
    WebElement UI_SUPPLIER_CODE;

    @FindBy(css = "[for = phone-number]")
    WebElement UI_PHONE_NUMBER;

    @FindBy(css = "#phone-number")
    WebElement UI_PHONE_NUMBER_PLACEHOLDER;

    @FindBy(css = "[for = email]")
    WebElement UI_EMAIL;

    @FindBy(css = ".supplier-page__address h5")
    WebElement UI_ADDRESS_INFORMATION;

    @FindBy(css = "[for = countryCode]")
    WebElement UI_COUNTRY;

    @FindBy(xpath = "//*[@id = 'address']/parent::div/parent::div/preceding-sibling::div")
    WebElement UI_VN_ADDRESS;

    @FindBy(css = "#address")
    WebElement UI_VN_ADDRESS_PLACEHOLDER;

    @FindBy(css = "[for = province]")
    WebElement UI_VN_CITY;

    @FindBy(css = "#province option:nth-child(1)")
    WebElement UI_VN_CITY_DEFAULT_OPTION;

    @FindBy(css = "[for = district]")
    WebElement UI_VN_DISTRICT;

    @FindBy(css = "#district option:nth-child(1)")
    WebElement UI_VN_DISTRICT_DEFAULT_OPTION;

    @FindBy(css = "[for = ward]")
    WebElement UI_VN_WARD;

    @FindBy(css = "#ward option:nth-child(1)")
    WebElement UI_WARD_DEFAULT_OPTION;

    @FindBy(xpath = "//*[@id = 'address']/parent::div/parent::div/preceding-sibling::div")
    WebElement UI_NON_VN_STREET_ADDRESS;

    @FindBy(css = "#address")
    WebElement UI_NON_VN_STREET_ADDRESS_PLACEHOLDER;

    @FindBy(css = "[for = address2]")
    WebElement UI_NON_VN_ADDRESS2;

    @FindBy(css = "#address2")
    WebElement UI_NON_VN_ADDRESS2_PLACEHOLDER;

    @FindBy(css = "[for = cityName]")
    WebElement UI_NON_VN_CITY;

    @FindBy(css = "#cityName")
    WebElement UI_NON_VN_CITY_PLACEHOLDER;

    @FindBy(css = "[for = province]")
    WebElement UI_NON_VN_STATE;

    @FindBy(css = "#province option:nth-child(1)")
    WebElement UI_NON_VN_STATE_DEFAULT_OPTION;

    @FindBy(css = "[for = zipCode]")
    WebElement UI_NON_VN_ZIPCODE;

    @FindBy(css = "#zipCode")
    WebElement UI_NON_VN_ZIPCODE_PLACEHOLDER;

    @FindBy(css = ".supplier-page__other-info h5")
    WebElement UI_OTHER_INFORMATION;

    @FindBy(css = "[for = staff]")
    WebElement UI_RESPONSIBLE_STAFF;

    @FindBy(css = "#staff option:nth-child(1)")
    WebElement UI_RESPONSIBLE_STAFF_DEFAULT_OPTION;

    @FindBy(xpath = "//*[@id = 'staff']/parent::div/following-sibling::div[1]")
    WebElement UI_DESCRIPTION;

    @FindBy(css = ".gs-search-box__wrapper h5")
    WebElement UI_ORDER_HISTORY;

    @FindBy(css = ".gs-search-box__wrapper .uik-input__input")
    WebElement UI_SEARCH_ORDER_HISTORY_PLACEHOLDER;

    @FindBy(css = ".gs-table-header-item")
    List<WebElement> UI_ORDER_HISTORY_TABLE_COLUMN;

    @FindBy(css = ".gs-table +* span")
    WebElement UI_NO_ORDER_HISTORY;

    @FindBy(css = ".suplier-page__supplier-status h5")
    WebElement UI_SUPPLIER_SUMMARY;

    @FindBy(css = ".suplier-page__supplier-status .title-label")
    WebElement UI_SUPPLIER_STATUS_TITLE;

    @FindBy(css = ".suplier-page__supplier-status .label-status")
    WebElement UI_SUPPLIER_STATUS_LABEL;

    @FindBy(css = ".modal-title")
    WebElement UI_CONFIRM_DELETE_SUPPLIER_POPUP_TITLE;
    @FindBy(css = ".modal-body")
    WebElement UI_CONFIRM_DELETE_SUPPLIER_POPUP_CONTENT;
    @FindBy(css = ".gs-button__red")
    WebElement UI_CONFIRM_DELETE_SUPPLIER_POPUP_DELETE_BTN;
    @FindBy(css = ".modal-footer .gs-button__red--outline")
    WebElement UI_CONFIRM_DELETE_SUPPLIER_POPUP_CANCEL_BTN;

    @FindBy(css = "#name +*")
    WebElement UI_SUPPLIER_NAME_ERROR;

    @FindBy(css = "#code +*")
    WebElement UI_SUPPLIER_CODE_ERROR;
}
