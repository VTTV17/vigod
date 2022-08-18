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

    @FindBy(css = "iframe[data-testid='dialog_iframe']")
    WebElement FACEBOOK_BUBBLE;

    @FindBy(css = "div.d-flex > button.gs-button__green > div")
    WebElement CREATE_PRODUCT_BTN;

    @FindBy(css = "input#productName")
    WebElement PRODUCT_NAME;

    @FindBy(css = "div.fr-wrapper > div")
    WebElement PRODUCT_DESCRIPTION;

    @FindBy(css = "section> div > input[type=file]")
    WebElement PRODUCT_IMAGE;

    @FindBy(css = "div[name *= 'Price']")
    List<WebElement> NORMAL_PRODUCT_PRICE;

    @FindBy(css = "div#app-body div.uik-select__valueWrapper")
    WebElement PRODUCT_VAT_DROPDOWN;

    @FindBy(css = "div#app-body button[type='button'] > span > div > div.uik-select__label")
    List<WebElement> VAT_LIST;

    @FindBy(css = "div:nth-child(4) > div.gs-widget__header > span")
    WebElement ADD_VARIATION_BTN;

    @FindBy(css = "div.first-item > div > div > input")
    List<WebElement> VARIATION_NAME;

    @FindBy(css = "div.second-item > div > div > div.css-1hwfws3")
    List<WebElement> VARIATION_VALUE;

    @FindBy(css = "div:nth-child(4) > div > div > table > thead > tr > th:nth-child(1) > label")
    WebElement SELECT_ALL_VARIATIONS_CHECKBOX;

    @FindBy(css = "div#app-body th > div > div > span")
    WebElement SELECT_ACTIONS_IN_VARIATION_TABLE;

    // 0: Update Price
    // 1: Update Stock
    // 3: Update SKU
    // 4: Update Image
    @FindBy(css = "div#app-body th > div > div > div > button")
    List<WebElement> LIST_ACTIONS_IN_VARIATION_TABLE;

    // 0: Listing price
    // 1: Selling price
    // 2: Cost price
    // 3: Stock quantity
    // 4: SKU
    @FindBy(css = "div:nth-child(4) > div > div > table > tbody > tr > td > div.cursor--pointer")
    List<WebElement> OPEN_TABLE;

    @FindBy(css = "div:nth-child(4) > div > div > table > tbody > tr > td > img")
    List<WebElement> IMAGE_LIST_VARIATION_TABLE;

    @FindBy(css = "div.modal-footer > button.gs-button__green")
    WebElement UPDATE_BTN;

    @FindBy(css = "div.modal-content div.image-uploader-wrapper>input")
    WebElement VARIATION_IMAGE;

    @FindBy(css = "div.product-variation-price-editor-modal__apply-price > div > div > input")
    WebElement PRICE_VALUE_IN_VARIATION_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__apply-all-wrapper > button > div")
    WebElement APPLY_ALL_IN_VARIATION_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__select-price-type > button > span > div > div:nth-child(1)")
    WebElement PRICE_DROPDOWN_IN_VARIATION_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__select-price-type > div > div > button > span > div > div:nth-child(1)")
    List<WebElement> PRICE_TYPE_IN_VARIATION_TABLE;

    @FindBy(css = "div.modal-body  .d-flex > button:nth-child(1)")
    WebElement ADD_STOCK_IN_STOCK_QUANTITY_TABLE;

    @FindBy(css = "div.modal-body  .d-flex > button:nth-child(2)")
    WebElement CHANGE_STOCK_IN_STOCK_QUANTITY_TABLE;

    @FindBy(css = ".d-flex > div > input")
    WebElement STOCK_VALUE_IN_STOCK_QUANTITY_TABLE;

    @FindBy(css = ".d-flex > input")
    List<WebElement> SKU_LIST_IN_SKU_TABLE;

    @FindBy(css = "input#input-search")
    WebElement CONVERSION_UNIT_SEARCH_BOX;

    @FindBy(css = "div.mt-3 > label > div")
    WebElement ADD_CONVERSION_UNIT_CHECKBOX;

    @FindBy(css = "div.p-3.bg-light-white > div > button")
    WebElement CONFIGURE_CONVERSION_UNIT_BTN;

    @FindBy(css = "h3 > div > label > div")
    WebElement WHOLESALE_PRICE_CHECK_BOX;

    @FindBy(css = "div.gs-widget__content > div > button")
    WebElement CONFIGURE_WHOLESALE_PRICE_BTN;

    @FindBy(css = "div:nth-child(7) > div > span")
    WebElement ADD_DEPOSIT_BTN;

    @FindBy(css = "div.d-md-block > div > div > div > div.css-1hwfws3")
    WebElement DEPOSIT_VALUE;

    @FindBy(css = "input#seoTitle")
    WebElement SEO_TITLE;

    @FindBy(css = "input#seoDescription")
    WebElement SEO_DESCRIPTION;

    @FindBy(css = "input#seoKeywords")
    WebElement SEO_KEYWORD;

    @FindBy(css = "input#seoUrl")
    WebElement SEO_URL;

    @FindBy(css = "div.page-toolbar > div > div > div > button.btn-save")
    WebElement SAVE_BTN;

    @FindBy(css = "div.page-toolbar > div > div > div > button.gs-button__gray--outline")
    WebElement CANCEL_BTN;

    @FindBy(css = "div.product-form-collection-selector2 > div:nth-child(1) > div > input")
    WebElement COLLECTION_SEARCH_BOX;

    @FindBy(css = "div.product-form-collection-selector2__search-item")
    List<WebElement> COLLECTION_LIST;

    @FindBy(css = "select#manageInventory > option:nth-child(2)")
    WebElement MANAGE_INVENTORY_BY_IMEI;

    @FindBy(css = "div[name = productQuantity] input")
    WebElement NORMAL_PRODUCT_STOCK_QUANTITY;

    @FindBy(css = "div.row > button")
    WebElement APPLY_ALL_STOCK_QUANTITY;

    // 0: Show as listing product on storefront
    // 1: Display if out of stock
    // 3: Hide remaining stock on online store
    @FindBy(css = "label.uik-checkbox__wrapper.custom-check-box > div")
    List<WebElement> CONFIGURE_PRODUCT_DISPLAY;

    @FindBy(css = "div[name='productWeight'] > div >div > input")
    WebElement PRODUCT_WEIGHT;

    @FindBy(css = "div[name='productLength'] > div >div > input")
    WebElement PRODUCT_LENGTH;

    @FindBy(css = "div[name='productWidth'] > div >div > input")
    WebElement PRODUCT_WIDTH;

    @FindBy(css = "div[name='productHeight'] > div >div > input")
    WebElement PRODUCT_HEIGHT;

    @FindBy(css = "div[class *= priority-field] > input")
    WebElement PRODUCT_PRIORITY;

    @FindBy(css = "div.gs-widget__content > label > div")
    List<WebElement> PRODUCT_PLATFORM_LABEL;

    @FindBy(css = "div.gs-widget__content > label > input")
    List<WebElement> PRODUCT_PLATFORM_CHECKBOX;
}
