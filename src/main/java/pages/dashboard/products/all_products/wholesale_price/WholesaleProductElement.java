package pages.dashboard.products.all_products.wholesale_price;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class WholesaleProductElement {
    WebDriver driver;

    WholesaleProductElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /* Wholesale product config */
    @FindBy(css = ".uik-checkbox__wrapper > [name='enabledListing']")
    WebElement ADD_WHOLESALE_PRICING_CHECKBOX;

    @FindBy(css = "div:nth-child(6)  .bg-light-white > div > button")
    WebElement CONFIGURE_BTN;

    @FindBy(css = ".loading .lds-dual-ring-grey")
    WebElement SPINNER;

    @FindBy(css = ".modal-content")
    WebElement CONFIRM_POPUP;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement CONFIRM_POPUP_OK_BTN;

    /* Without variation config*/
    @FindBy(css = ".wholesale-btn-group-header .gs-button__gray--outline")
    WebElement WITHOUT_VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN;

    @FindBy(css = ".wholesale-btn-group-header > .gs-button__green")
    WebElement WITHOUT_VARIATION_HEADER_SAVE_BTN;

    @FindBy(css = "[name ^= 'buyFrom']")
    WebElement WITHOUT_VARIATION_BUY_FROM;

    @FindBy(css = "[name ^= 'pricePerItem'] input")
    WebElement WITHOUT_VARIATION_PRICE_PER_ITEM;

    @FindBy(css = ".dropdown-search-checkbox-custom")
    WebElement WITHOUT_VARIATION_CUSTOMER_SEGMENT_DROPDOWN;

    /* Variation config */
    @FindBy(css = ".wholesale-btn-group-header > .gs-button__gray--outline")
    WebElement VARIATION_HEADER_ADD_VARIATION_BTN;

    @FindBy(css = ".modal-content")
    WebElement ADD_VARIATION_POPUP;

    @FindBy(css = ".product-list input")
    List<WebElement> VARIATION_ADD_VARIATION_POPUP_LIST_VARIATION_CHECKBOX;

    @FindBy(css = ".footer-btn .gs-button__green")
    WebElement VARIATION_ADD_VARIATION_POPUP_OK_BTN;

    @FindBy(css = ".border-bottom > .wholesale-group-header .gs-fake-link:nth-child(1)")
    List<WebElement> VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN;

    By VARIATION_HEADER_VARIATION_VALUE = By.cssSelector(".border-bottom > .wholesale-group-header > div > div");

    @FindBy(css = "[name^='buyFrom-']")
    List<WebElement> VARIATION_BUY_FROM;

    @FindBy(css = "[name^='pricePerItem-'] input")
    List<WebElement> VARIATION_PRICE_PER_ITEM;

    By VARIATION_CUSTOMER_SEGMENT_DROPDOWN = By.cssSelector(".dropdown-search-checkbox-custom");

    /* reuse element */
    @FindBy(css = ".search-box")
    WebElement CUSTOMER_SEGMENT_SEARCH_BOX;

    @FindBy(css = ".label-list > .label:nth-child(2)")
    WebElement CUSTOMER_SEGMENT_CHECKBOX;

    /* UI element */
    @FindBy(css = ".gs-widget__content.bg-light-white p")
    WebElement UI_WHOLESALE_PRODUCT_INFORMATION;

    @FindBy(css = ".gs-widget__content.bg-light-white .gs-button__green")
    WebElement UI_WHOLESALE_PRODUCT_CONFIGURE_BTN;

    /* Header */
    @FindBy(css = ".wholesale-group-header > span")
    WebElement UI_HEADER_GO_BACK_TO_PRODUCT_DETAIL;

    @FindBy(css = ".container-fluid h5")
    WebElement UI_HEADER_PAGE_TITLE;

    @FindBy(css = ".wholesale-group-header .gs-button__gray--outline")
    WebElement UI_WITHOUT_VARIATION_ADD_WHOLESALE_PRICING_BTN;

    @FindBy(css = ".container-fluid p")
    WebElement UI_NO_WHOLESALE_CONFIG;

    @FindBy(css = ".wholesale-group-header .gs-button__gray--outline")
    WebElement UI_VARIATION_HEADER_ADD_VARIATION_BTN;

    @FindBy(css = ".wholesale-group-header .gs-button__green")
    WebElement UI_HEADER_SAVE_BTN;

    /**
     * <p>0: wholesale title</p>
     * <p>1: buy from</p>
     * <p>2: price per item</p>
     * <p>3: discount</p>
     * <p>4: customer segment</p>
     * <p></p>
     */
    @FindBy(css = ".wholesale-grid-item label")
    List<WebElement> UI_WITHOUT_VARIATION_WHOLESALE_CONFIG_TABLE_COLUMN;

    @FindBy(css = ".search-box input")
    WebElement UI_SEGMENT_SEARCH_BOX_PLACEHOLDER;

    @FindBy(css = "[for = ALL]")
    WebElement UI_SEGMENT_ALL_CUSTOMERS;

    @FindBy(xpath = "//div[contains(@class,'gs-widget')]/descendant::span[1]")
    List<WebElement> UI_VARIATION_ADD_WHOLESALE_PRICING_BTN;

    @FindBy(xpath = "//div[contains(@class,'gs-widget')]/descendant::span[2]")
    List<WebElement> UI_VARIATION_DELETE_BTN;

    @FindBy(xpath = "//div[contains(@class,'gs-widget')]/descendant::span[3]")
    List<WebElement> UI_VARIATION_EDIT_BTN;

    @FindBy(css = ".wholesale-grid-item:nth-child(1) label")
    List<WebElement> UI_VARIATION_WHOLESALE_TITLE_COLUMN;

    @FindBy(css = ".wholesale-grid-item:nth-child(2) label")
    List<WebElement> UI_VARIATION_BUY_FROM_COLUMN;

    @FindBy(css = ".wholesale-grid-item:nth-child(3) label")
    List<WebElement> UI_VARIATION_PRICE_PER_ITEM_COLUMN;

    @FindBy(css = ".wholesale-grid-item:nth-child(4) label")
    List<WebElement> UI_VARIATION_DISCOUNT_COLUMN;

    @FindBy(css = ".wholesale-grid-item:nth-child(5) label")
    List<WebElement> UI_VARIATION_CUSTOMER_SEGMENT_COLUMN;

    @FindBy(css = ".tippy-tooltip-content")
    WebElement UI_SEGMENT_TOOLTIPS;

    /* Add variation popup */
    @FindBy(css = ".modal-title")
    WebElement UI_ADD_VARIATION_POPUP_TITLE;

    @FindBy(css = ".modal-body > section > div > label")
    WebElement UI_ADD_VARIATION_POPUP_SELECT_ALL_CHECKBOX;

    @FindBy(css = ".footer-btn > .gs-button__green")
    WebElement UI_ADD_VARIATION_POPUP_OK_BTN;

    @FindBy(css = ".footer-btn > .gs-button__gray--outline")
    WebElement UI_ADD_VARIATION_POPUP_CANCEL_BTN;
}