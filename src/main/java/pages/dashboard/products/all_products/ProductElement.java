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

    @FindBy(css = ".product-list-page > div > div > div >  button.gs-button__green")
    WebElement CREATE_PRODUCT_BTN;

    @FindBy(css = "input#productName")
    WebElement PRODUCT_NAME;

    @FindBy(css = "div.fr-wrapper > div")
    WebElement PRODUCT_DESCRIPTION;

    @FindBy(css = "section> div > input[type=file]")
    WebElement PRODUCT_IMAGE;

    @FindBy(css = "div[name *= 'Price'] input")
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

    @FindBy(css = "div:nth-child(4) > div > div > table > thead > tr > th:nth-child(1) > label > input")
    WebElement SELECT_ALL_VARIATIONS_CHECKBOX;
    @FindBy(css = "div:nth-child(4) > div > div > table > thead > tr > th:nth-child(1) > label")
    WebElement SELECT_ALL_VARIATIONS_LABEL;

    @FindBy(css = "div:nth-child(4) > div > div > table > thead > tr:nth-child(1) > th > div > div > span")
    WebElement SELECT_ACTIONS_IN_VARIATION_TABLE;

    // 0: Update Price
    // 1: Update Stock
    // 3: Update SKU
    // 4: Update Image
    @FindBy(css = "div#app-body th > div > div > div > button")
    List<WebElement> LIST_ACTIONS;

    // 0: Listing price
    // 1: Selling price
    // 2: Cost price
    // 3: Stock quantity
    // 4: SKU
    @FindBy(css = "div:nth-child(4) > div > div > table > tbody > tr > td > div.cursor--pointer")
    List<WebElement> VARIATION_TABLE;

    @FindBy(css = "div:nth-child(4) > div > div > table > tbody > tr > td > img")
    List<WebElement> IMAGE_LIST_IN_VARIATION_TABLE;

    @FindBy(css = "div.modal-footer > button.gs-button__green")
    WebElement UPDATE_BTN;

    @FindBy(css = "div.modal-content div.image-uploader-wrapper>input")
    WebElement ADD_IMAGE;

    @FindBy(css = "div.product-variation-price-editor-modal__apply-price > div > div > input")
    WebElement PRICE_VALUE_IN_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__apply-all-wrapper > button > div")
    WebElement APPLY_ALL_IN_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__select-price-type > button > span > div > div:nth-child(1)")
    WebElement PRICE_DROPDOWN_IN_VARIATION_TABLE;

    @FindBy(css = "div.product-variation-price-editor-modal__select-price-type > div > div > button > span > div > div:nth-child(1)")
    List<WebElement> PRICE_TYPE_IN_VARIATION_TABLE;

    @FindBy(css = "div.modal-body  .d-flex > button:nth-child(1)")
    WebElement ADD_STOCK;

    @FindBy(css = "div.modal-body  .d-flex > button:nth-child(2)")
    WebElement CHANGE_STOCK;

    @FindBy(css = ".modal-body .form-group >  div > input")
    WebElement STOCK_VALUE_IN_STOCK_QUANTITY_TABLE;

    @FindBy(css = "div:nth-child(4) > div > div > table > tbody > tr")
    List<WebElement> VARIATION_TEXT;

    @FindBy(css = "div:nth-child(7) > div > div > table > tbody > tr")
    List<WebElement> DEPOSIT_TEXT;

    @FindBy(css = "[class ^= 'branch-list-stock'] > .font-weight-500")
    List<WebElement> BRANCH_NAME_LIST;

    @FindBy(css = ".input-code input")
    List<WebElement> INPUT_IMEI_VALUE;

    @FindBy(css = ".d-flex > input")
    List<WebElement> SKU_LIST_IN_SKU_TABLE;

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

    @FindBy(css = "div:nth-child(7) > div > div > table > thead > tr > th:nth-child(1) > label > input")
    WebElement SELECT_ALL_DEPOSIT_CHECKBOX;
    @FindBy(css = "div:nth-child(7) > div > div > table > thead > tr > th:nth-child(1) > label")
    WebElement SELECT_ALL_DEPOSIT_LABEL;

    @FindBy(css = "div:nth-child(7) > div > div > table > thead > tr:nth-child(1) > th > div > div > span")
    WebElement SELECT_ACTIONS_IN_DEPOSIT_TABLE;

    // 0: Price
    // 1: Stock quantity
    // 2: SKU
    @FindBy(css = "div:nth-child(7) > div > div > table > tbody > tr > td > div.cursor--pointer")
    List<WebElement> DEPOSIT_TABLE;

    @FindBy(css = "div:nth-child(7) > div > div > table > tbody > tr > td > img")
    List<WebElement> IMAGE_LIST_IN_DEPOSIT_TABLE;

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

    @FindBy(css = "div.product-form-collection-selector2 > div:nth-child(1) > div > input")
    WebElement COLLECTION_SEARCH_BOX;

    @FindBy(css = "div.product-form-collection-selector2__search-item")
    List<WebElement> COLLECTION_LIST;

    @FindBy(css = "select#manageInventory > option:nth-child(2)")
    WebElement MANAGE_INVENTORY_BY_IMEI;

    @FindBy(css = "div.mb-0 > div > input")
    List<WebElement> IMEI_STOCK;

    @FindBy(css = "input[name='serial']")
    WebElement IMEI_INPUT;

    @FindBy(css = ".branch-list-stock__wrapper__row > div:nth-child(1)")
    List<WebElement> LIST_BRANCH_NAME_IN_STOCK_INVENTORY;

    @FindBy(css = ".modal-footer button.gs-button__green")
    WebElement SAVE_BTN_IN_IMEI_STOCK_TABLE;

    @FindBy(css = "div[name = productQuantity] input")
    WebElement NORMAL_PRODUCT_STOCK_QUANTITY;

    @FindBy(css = "div.row > button")
    WebElement APPLY_ALL_STOCK_QUANTITY;

    // 0: Display if out of stock
    // 1: Hide remaining stock on online store
    @FindBy(css = "div[class = ' '] > label > input")
    List<WebElement> CONFIGURE_DISPLAY_IN_SF_CHECKBOX;

    @FindBy(css = "div[class = ' '] > label > div")
    List<WebElement> CONFIGURE_DISPLAY_IN_SF_LABEL;

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

    @FindBy(css = ".modal-footer > button")
    WebElement CLOSE_BTN;

    @FindBy(css = "tr:nth-child(1) > td:nth-child(1) > span > b")
    WebElement NEWEST_PRODUCT_ID;

    @FindBy (css = "div.product-form-collection-selector2__selected-container > div > span")
    List<WebElement> SELECTED_COLLECTION_LIST;

    @FindBy (css = " div.branch-list-stock__wrapper > div > div> div > div")
    List<WebElement> QUANTITY_STOCK_BY_BRANCH;

    @FindBy (css = "div.header-right__ele-left > div > span.store-detail__url > a")
    WebElement SF_URL;

    @FindBy (css = ".Toastify__toast-body")
    WebElement TOAST_MESSAGE;
}
