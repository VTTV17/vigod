package pages.dashboard.products.all_products;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductElement {
    WebDriver driver;

    public ProductElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "div.d-flex > button.gs-button__green > div")
    WebElement CREATE_PRODUCT_BTN;

    @FindBy (css = "input#productName")
    WebElement PRODUCT_NAME;

    @FindBy (css = "div.fr-wrapper > div")
    WebElement PRODUCT_DESCRIPTION;

    @FindBy (css = "section> div > input[type=file]")
    WebElement PRODUCT_IMAGE;

    @FindBy (css = "div[name *= 'Price']")
    List<WebElement> NORMAL_PRODUCT_PRICE;

    @FindBy (css = "div#app-body div.uik-select__valueWrapper")
    WebElement PRODUCT_VAT_DROPDOWN;

    @FindBy (css = "div#app-body button[type='button'] > span > div > div.uik-select__label")
    List<WebElement> VAT_LIST;

    @FindBy (css = "div:nth-child(4) > div.gs-widget__header > span")
    WebElement ADD_VARIATION_BTN;

    @FindBy (css = "div.first-item > div > div > input")
    List<WebElement> VARIATION_NAME;

    @FindBy (css = "div.second-item > div > div > div.css-1hwfws3")
    List<WebElement> VARIATION_VALUE;

    @FindBy (css = "input#input-search")
    WebElement CONVERSION_UNIT_SEARCH_BOX;

    @FindBy (css = "div.mt-3 > label > div")
    WebElement ADD_CONVERSION_UNIT_CHECKBOX;

    @FindBy (css = "div.p-3.bg-light-white > div > button")
    WebElement CONFIGURE_CONVERSION_UNIT_BTN;

    @FindBy (css = "h3 > div > label > div")
    WebElement WHOLESALE_PRICE_CHECK_BOX;

    @FindBy (css = "div.gs-widget__content > div > button")
    WebElement CONFIGURE_WHOLESALE_PRICE_BTN;

    @FindBy (css = "div:nth-child(7) > div > span")
    WebElement ADD_DEPOSIT_BTN;

    @FindBy (css = "div.d-md-block > div > div > div > div.css-1hwfws3")
    WebElement DEPOSIT_VALUE;

    @FindBy (css = "input#seoTitle")
    WebElement SEO_TITLE;

    @FindBy (css = "input#seoDescription")
    WebElement SEO_DESCRIPTION;

    @FindBy (css = "input#seoKeywords")
    WebElement SEO_KEYWORD;

    @FindBy (css = "input#seoUrl")
    WebElement SEO_URL;

    @FindBy (css = "div.page-toolbar > div > div > div > button.btn-save")
    WebElement SAVE_BTN;

    @FindBy (css = "div.page-toolbar > div > div > div > button.gs-button__gray--outline")
    WebElement CANCEL_BTN;

    @FindBy (css = "div.product-form-collection-selector2 > div:nth-child(1) > div > input")
    WebElement COLLECTION_SEARCH_BOX;

    @FindBy (css = "select#manageInventory")
    WebElement MANAGE_INVENTORY_DROPDOWN;

    // 0: Show as listing product on storefront
    // 1: Display if out of stock
    // 3: Hide remaining stock on online store
    @FindBy (css = "label.uik-checkbox__wrapper.custom-check-box > div")
    List<WebElement> CONFIGURE_PRODUCT_DISPLAY;

    @FindBy (css = "div[name='productWeight'] > div >div > input")
    WebElement PRODUCT_WEIGHT;

    @FindBy (css = "div[name='productLength'] > div >div > input")
    WebElement PRODUCT_LENGTH;

    @FindBy (css = "div[name='productWidth'] > div >div > input")
    WebElement PRODUCT_WIDTH;

    @FindBy (css = "div[name='productHeight'] > div >div > input")
    WebElement PRODUCT_HEIGHT;

    @FindBy (css = "div[class *= priority-field] > input")
    WebElement PRODUCT_PRIORITY;

    @FindBy (css = "div.gs-widget__content > label > div")
    List<WebElement> PRODUCT_PLATFORM;
}
