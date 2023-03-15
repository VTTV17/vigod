package pages.dashboard.products.all_products.conversion_unit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

class ConversionUnitElement {
    WebDriver driver;

    ConversionUnitElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /* Conversion unit config */
    @FindBy(css = ".uik-checkbox__wrapper > [name='conversionUnitCheckbox']")
    WebElement ADD_CONVERSION_UNIT_CHECKBOX;

    @FindBy(css = "div:nth-child(5)  .bg-light-white > div > button")
    WebElement CONFIGURE_BTN;

    /* Without variation config */
    @FindBy(css = ".btn-header-wrapper > .gs-button__green--outline")
    WebElement WITHOUT_VARIATION_HEADER_SELECT_UNIT_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green")
    WebElement WITHOUT_VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = "#unit-0")
    WebElement WITHOUT_VARIATION_UNIT;

    By WITHOUT_VARIATION_LIST_AVAILABLE_UNIT = By.cssSelector(".expanded > div > div");

    @FindBy(css = ".icon_add_unit")
    WebElement WITHOUT_VARIATION_ADD_BTN;

    @FindBy(css = "[name *= quantity]")
    WebElement WITHOUT_VARIATION_QUANTITY;

    /* Variation config */
    @FindBy(css = ".gs-button__green--outline")
    WebElement VARIATION_HEADER_SELECT_VARIATION_BTN;

    @FindBy(css = ".gs-button__green")
    WebElement VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = ".modal-content")
    WebElement SELECT_VARIATION_POPUP;

    By VARIATION_SELECT_VARIATION_POPUP_LIST_VARIATION_CHECKBOX = By.cssSelector("input[name = variationUnit]");

    @FindBy(css = ".modal-footer > .gs-button__green")
    WebElement VARIATION_SELECT_VARIATION_POPUP_SAVE_BTN;

    @FindBy(css = ".conversion-configure > .gs-button__blue--outline")
    List<WebElement> VARIATION_CONFIGURE_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green--outline")
    WebElement CONFIGURE_FOR_EACH_VARIATION_HEADER_SELECT_UNIT_BTN;

    @FindBy(css = ".btn-header-wrapper > .gs-button__green")
    WebElement CONFIGURE_FOR_EACH_VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = "#unit-0")
    WebElement CONFIGURE_FOR_EACH_VARIATION_UNIT;

    By CONFIGURE_FOR_EACH_VARIATION_LIST_AVAILABLE_UNIT = By.cssSelector(".expanded > div > div");

    @FindBy(css = ".icon_add_unit")
    WebElement CONFIGURE_FOR_EACH_VARIATION_ADD_BTN;

    @FindBy(css = "[name *= quantity]")
    WebElement CONFIGURE_FOR_EACH_VARIATION_QUANTITY;

    /* UI element */
    @FindBy(css = ".gs-widget__content .bg-light-white p")
    WebElement UI_CONVERSION_UNIT_INFORMATION;

    @FindBy(css = ".gs-widget__content .bg-light-white button")
    WebElement UI_CONVERSION_UNIT_CONFIGURE_BTN;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(5) small")
    WebElement UI_CONVERSION_UNIT_FOR_IMEI_NOTICE;

    @FindBy(css = ".gs-fake-link")
    WebElement UI_HEADER_GO_BACK_TO_PRODUCT_DETAIL;

    @FindBy(css = ".container-fluid h6")
    WebElement UI_HEADER_WITHOUT_VARIATION_PAGE_TITLE;

    @FindBy(css = ".container-fluid h6, .container-fluid h5")
    WebElement UI_HEADER_VARIATION_PAGE_TITLE;

    @FindBy(css = ".gs-button__green")
    WebElement UI_HEADER_SAVE_BTN;

    @FindBy(css = ".bg-white > p")
    WebElement UI_NO_CONFIG;

    /* UI without variation */
    @FindBy(css = ".gs-button__green--outline")
    WebElement UI_HEADER_WITHOUT_VARIATION_SELECT_UNIT_BTN;

    @FindBy(css = ".gs-button__gray--outline")
    WebElement UI_HEADER_WITHOUT_VARIATION_CANCEL_BTN;

    /**
     * <p>0: Unit</p>
     * <p>1: Quantity</p>
     */
    @FindBy(css = ".border label")
    List<WebElement> UI_WITHOUT_VARIATION_CONFIG_TABLE_COLUMN;

    @FindBy(css = "#unit-0")
    WebElement UI_WITHOUT_VARIATION_CONFIG_TABLE_INPUT_UNIT_PLACEHOLDER;

    @FindBy(css = ".search-list > .loading")
    WebElement SEARCH_LOADING;
    @FindBy(css = ".search-list > p")
    WebElement UI_WITHOUT_VARIATION_CONFIG_TABLE_SEARCH_UNIT_NO_RESULT;

    @FindBy(css = ".icon_add_unit")
    WebElement UI_WITHOUT_VARIATION_CONFIG_TABLE_ADD_BTN;

    /**
     * <p>0: Conversion unit</p>
     * <p>1: SKU</p>
     * <p>2: Barcode</p>
     * <p>3: Cost price</p>
     * <p>4: Listing price</p>
     * <p>5: Selling price</p>
     * <p>6: Stock</p>
     * <p>7: Weight</p>
     * <p>8: Length</p>
     * <p>9: Width</p>
     * <p>10: Height</p>
     */
    @FindBy(css = ".table-conversion-list > thead th")
    List<WebElement> UI_WITHOUT_VARIATION_CONFIG_ALIAS_TABLE_COLUMN;

    /* UI variation */
    @FindBy(css = ".gs-button__green--outline")
    WebElement UI_HEADER_VARIATION_SELECT_VARIATION_BTN;

    @FindBy(css = ".modal-title")
    WebElement UI_SELECT_VARIATION_POPUP_TITLE;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_SELECT_VARIATION_POPUP_OK_BTN;

    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement UI_SELECT_VARIATION_POPUP_CANCEL_BTN;

    @FindBy(css = ".action-variation > span:nth-child(1)")
    List<WebElement> UI_VARIATION_CONFIG_TABLE_DELETE_BTN;

    @FindBy(css = ".action-variation > span:nth-child(2)")
    List<WebElement> UI_VARIATION_CONFIG_TABLE_EDIT_BTN;

    @FindBy(css = ".gs-button__blue--outline")
    List<WebElement> UI_VARIATION_CONFIG_TABLE_CONFIGURE_BTN;

    @FindBy(css = ".gs-fake-link")
    WebElement UI_VARIATION_CONFIG_PAGE_GO_BACK_TO_SETUP_CONVERSION_UNIT;

    @FindBy(css = ".conversion-unit-wrapper h5")
    WebElement UI_VARIATION_CONFIG_PAGE_TITLE;

    @FindBy(css = ".gs-button__green--outline")
    WebElement UI_VARIATION_CONFIG_PAGE_HEADER_SELECT_UNIT_BTN;

    @FindBy(css = ".gs-button__gray--outline")
    WebElement UI_VARIATION_CONFIG_PAGE_HEADER_CANCEL_BTN;

    @FindBy(css = ".gs-button__green")
    WebElement UI_VARIATION_CONFIG_PAGE_HEADER_SAVE_BTN;

    @FindBy(css = ".border label")
    List<WebElement> UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_COLUMN;

    @FindBy(css = "#unit-0")
    WebElement UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_INPUT_UNIT_PLACEHOLDER;

    @FindBy(css = ".search-list > p")
    WebElement UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_SEARCH_UNIT_NO_RESULT;

    @FindBy(css = ".icon_add_unit")
    WebElement UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_ADD_BTN;

    /**
     * <p>0: Conversion unit</p>
     * <p>1: SKU</p>
     * <p>2: Barcode</p>
     * <p>3: Cost price</p>
     * <p>4: Listing price</p>
     * <p>5: Selling price</p>
     * <p>6: Stock</p>
     * <p>7: Weight</p>
     * <p>8: Length</p>
     * <p>9: Width</p>
     * <p>10: Height</p>
     */
    @FindBy(css = ".table-conversion-list > thead th")
    List<WebElement> UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_ALIAS_TABLE_COLUMN;
}
