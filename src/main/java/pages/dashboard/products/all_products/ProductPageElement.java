package pages.dashboard.products.all_products;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductPageElement {
    WebDriver driver;
    ProductPageElement(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".product-list-page > div > div > div >  button.gs-button__green")
    WebElement CREATE_PRODUCT_BTN;

    @FindBy(xpath = "(//div[@class='gs-content-header-right-el d-flex']//button)[4]")
    WebElement PRINT_BARCODE_BTN;

    /* General product information */
    @FindBy(css = "input#productName")
    WebElement PRODUCT_NAME;

    @FindBy(css = "div.fr-wrapper > div")
    WebElement PRODUCT_DESCRIPTION;

    @FindBy(css = ".image-drop-zone input")
    WebElement PRODUCT_IMAGE;

    @FindBy(css = ".form-group .uik-select__valueWrapper")
    WebElement VAT_DROPDOWN;

    @FindBy(css = ".uik-select__option")
    List<WebElement> VAT_LIST;

    @FindBy(css = ".clear-up-down-btn")
    WebElement COLLECTION_SEARCH_BOX;

    @FindBy(css = ".product-form-collection-selector2__search-item")
    List<WebElement> LIST_MANUAL_COLLECTION;

    @FindBy(css = "#productSKU")
    WebElement PRODUCT_SKU_WITHOUT_VARIATION;

    @FindBy(css = "#manageInventory")
    WebElement MANAGE_INVENTORY;

    @FindBy(css = "div[name='productWeight'] > div >div > input")
    WebElement PRODUCT_WEIGHT;

    @FindBy(css = "div[name='productLength'] > div >div > input")
    WebElement PRODUCT_LENGTH;

    @FindBy(css = "div[name='productWidth'] > div >div > input")
    WebElement PRODUCT_WIDTH;

    @FindBy(css = "div[name='productHeight'] > div >div > input")
    WebElement PRODUCT_HEIGHT;

    @FindBy(css = "[name = onApp]")
    WebElement PLATFORM_APP;

    @FindBy(css = "[name = onWeb]")
    WebElement PLATFORM_WEB;

    @FindBy(css = "[name = inStore]")
    WebElement PLATFORM_IN_STORE;

    @FindBy(css = "[name = inGosocial]")
    WebElement PLATFORM_GO_SOCIAL;
    By LIST_BRANCH_NAME = By.cssSelector(".branch-list-stock__wrapper__row > div:nth-child(1)");

    @FindBy(css = ".gss-content-header .btn-save")
    WebElement SAVE_BTN;

    @FindBy(css = ".modal-content")
    WebElement POPUP;

    @FindBy(css = ".modal-footer > button")
    WebElement NOTIFICATION_POPUP_CLOSE_BTN;

    @FindBy(css = "input#seoTitle")
    WebElement SEO_TITLE;

    @FindBy(css = "input#seoDescription")
    WebElement SEO_DESCRIPTION;

    @FindBy(css = "input#seoKeywords")
    WebElement SEO_KEYWORD;

    @FindBy(css = "input#seoUrl")
    WebElement SEO_URL;

    /* Without variation product */
    @FindBy(css = "[name = productPrice] input")
    WebElement PRODUCT_LISTING_PRICE_WITHOUT_VARIATION;

    @FindBy(css = "#productDiscountPrice input")
    WebElement PRODUCT_SELLING_PRICE_WITHOUT_VARIATION;

    // 0: Display if out of stock
    // 1: Hide remaining stock on online store
    @FindBy(css = "[class=' '] > label > input")
    List<WebElement> SF_DISPLAY_SETTING;
    By LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT = By.cssSelector(".branch-list-stock__wrapper__row  input");

    @FindBy(css = ".branch > div button.uik-select__valueRendered")
    WebElement ADD_IMEI_POPUP_BRANCH_DROPDOWN;

    @FindBy(css = ".branch .uik-menuDrop__list > button:nth-child(1)  input")
    WebElement ADD_IMEI_POPUP_BRANCH_DROPDOWN_SELECT_ALL;

    @FindBy(css = ".input-code input")
    List<WebElement> ADD_IMEI_POPUP_IMEI_TEXT_BOX;

    @FindBy(css = ".modal-footer > .gs-button__green")
    WebElement ADD_IMEI_POPUP_SAVE_BTN;

    /* Variation product */
    @FindBy(css = "div:nth-child(4) > div.gs-widget__header > span")
    WebElement ADD_VARIATION_BTN;

    @FindBy(css = "div.first-item > div > div > input")
    List<WebElement> VARIATION_NAME;

    @FindBy(css = "div.second-item > div > div > div.css-1hwfws3")
    List<WebElement> VARIATION_VALUE;

    @FindBy(css = ".product-form-variation-selector__table  th:nth-child(1) input")
    WebElement VARIATION_TABLE_SELECT_ALL_CHECKBOX;

    @FindBy(css = "th .gs-fake-link")
    WebElement VARIATION_TABLE_SELECT_ACTION;

    // 0: Update Price
    // 1: Update Stock
    // 3: Update SKU
    // 4: Update Image
    By VARIATION_TABLE_LIST_ACTION = By.cssSelector(".uik-menuDrop__list > button");
    @FindBy(css = "td [name *= stock]")
    List<WebElement> VARIATION_TABLE_STOCK_QUANTITY;

    By UPDATE_PRICE_POPUP_LISTING_PRICE = By.cssSelector(".wrapper [name *= 'orgPrice']");
    By UPDATE_PRICE_POPUP_SELLING_PRICE = By.cssSelector(".wrapper [name *= 'discountPrice']");

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement POPUP_UPDATE_BTN;

    @FindBy(css = ".modal-body  div > div > .gs-button:nth-child(2)")
    WebElement UPDATE_STOCK_POPUP_CHANGE_TAB;

    @FindBy(css = ".modal-body  .quantity-input-field > input")
    WebElement UPDATE_STOCK_POPUP_INPUT_STOCK;

    By UPDATE_STOCK_POPUP_NORMAL_VARIATION_STOCK = By.cssSelector(".input-stock  > input");

    /* Product list page */
    @FindBy(css = ".d-mobile-none .uik-input__input")
    WebElement SEARCH_BOX;

    @FindBy(css = "tbody > tr > td:nth-child(1) > span > b")
    WebElement PRODUCT_ID;

    /* Tien */
    @FindBy (xpath = "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/following-sibling::*/button")
    List<WebElement> DELETE_VARIATION_BTN;

    @FindBy (xpath = "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/parent::*/following-sibling::*/button")
    List<WebElement> DELETE_DEPOSIT_BTN;

    By PRINT_BARCODE_MODAL = By.cssSelector(".modal-content.product-list-barcode-printer");

    @FindBy(xpath = "//input[@class='uik-checkbox__checkbox' and @name='enabledListing']/ancestor::div[contains(@class,'uik-widget__wrapper')]/following-sibling::*/div[1]//span")
    WebElement ADD_DEPOSIT_BTN;
}
