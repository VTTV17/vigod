package pages.dashboard.products.supplier.function.crud;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.util.List;

class CRUDSupplierElement {
    WebDriver driver;
    CRUDSupplierElement(WebDriver driver) {
      this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 5), this);
    }

    @FindBy(css = ".language-selector .uik-select__valueWrapper")
    WebElement HEADER_SELECTED_LANGUAGE;
    @FindBy(css = ".language-selector .uik-select__optionList .uik-select__label")
    List<WebElement> HEADER_LANGUAGE_LIST;

    @FindBy(css = ".btn-save")
    WebElement HEADER_SAVE_BTN;

    @FindBy(css = ".gs-button__red--outline")
    WebElement HEADER_DELETE_BTN;

    @FindBy(css = "#name")
    WebElement SUPPLIER_NAME;

    @FindBy(css = "#code")
    WebElement SUPPLIER_CODE;

    @FindBy(css = "#phone-number")
    WebElement PHONE_NUMBER;

    @FindBy(css = "#email")
    WebElement EMAIL;

    @FindBy(css = "#countryCode")
    WebElement COUNTRY_DROPDOWN;

    @FindBy(css = "#countryCode option")
    List<WebElement> COUNTRY_LIST;

    @FindBy(css = "#address")
    WebElement VN_ADDRESS;

    @FindBy(css = "#province")
    WebElement VN_CITY_DROPDOWN;

    @FindBy(css = "#province option")
    List<WebElement> VN_CITY_LIST;

    @FindBy(css = "#district")
    WebElement VN_DISTRICT_DROPDOWN;

    @FindBy(css = "#district option")
    List<WebElement> VN_DISTRICT_LIST;

    @FindBy(css = "#ward")
    WebElement VN_WARD_DROPDOWN;

    @FindBy(css = "#ward option")
    List<WebElement> VN_WARD_LIST;

    @FindBy(css = "#address")
    WebElement NON_VN_STREET_ADDRESS;

    @FindBy(css = "#address2")
    WebElement NON_VN_ADDRESS2;

    @FindBy(css = "#cityName")
    WebElement NON_VN_CITY;

    @FindBy(css = "#province")
    WebElement NON_VN_PROVINCE_DROPDOWN;

    @FindBy(css = "#province option")
    List<WebElement> NON_VN_PROVINCE_LIST;

    @FindBy(css = "#zipCode")
    WebElement NON_VN_ZIP_CODE;

    @FindBy(css = "#staff")
    WebElement RESPONSIBLE_STAFF_DROPDOWN;

    @FindBy(css = "#staff option")
    List<WebElement> RESPONSIBLE_STAFF_LIST;

    @FindBy(css = "#description")
    WebElement DESCRIPTION;

    @FindBy(css = ".gs-search-box__wrapper .uik-input__input")
    WebElement SEARCH_BOX;

    @FindBy(css = ".gs-table-body-items .id")
    List<WebElement> LIST_RESULT;

    @FindBy(css = ".modal-content")
    WebElement CONFIRM_DELETE_SUPPLIER_POPUP;

    @FindBy(css = ".gs-button__red")
    WebElement CONFIRM_DELETE_SUPPLIER_POPUP_DELETE_BTN;
}
