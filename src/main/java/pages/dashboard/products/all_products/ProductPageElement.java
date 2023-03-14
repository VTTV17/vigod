package pages.dashboard.products.all_products;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductPageElement {
    WebDriver driver;

    ProductPageElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By HEADER_SELECTED_LANGUAGE = By.cssSelector(".language-selector .uik-select__valueWrapper");

    @FindBy(css = ".language-selector .uik-select__optionList .uik-select__label")
    List<WebElement> HEADER_LANGUAGE_LIST;

    @FindBy(css = "#fb-root")
    WebElement FB_BUBBLE;
    @FindBy(css = ".product-list-page > div > div > div >  button.gs-button__green")
    WebElement CREATE_PRODUCT_BTN;

    @FindBy(xpath = "(//div[contains(@class,'gs-content-header-right-el d-flex')]//button)[4]")
    WebElement PRINT_BARCODE_BTN;

    /* General product information */
    @FindBy(css = "input#productName")
    WebElement PRODUCT_NAME;

    @FindBy(css = "div.fr-wrapper > div")
    WebElement PRODUCT_DESCRIPTION;

    By REMOVE_PRODUCT_IMAGE_BTN = By.cssSelector(".image-widget__btn-remove");

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
    WebElement CR_PRODUCT_SKU_WITHOUT_VARIATION;

    @FindBy(css = "[class *=--n2] > div:nth-child(3) .align-items-center > span")
    WebElement UP_PRODUCT_SKU_WITHOUT_VARIATION;


    @FindBy(css = "#manageInventory")
    WebElement MANAGE_INVENTORY;

    @FindBy(css = "[name = productPriority]")
    WebElement PRIORITY_TEXT_BOX;

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
    WebElement PLATFORM_GOSOCIAL;

    @FindBy(css = ".gss-content-header .btn-save")
    WebElement SAVE_BTN;

    @FindBy(css = ".lds-ellipsis")
    WebElement THREE_POINT_LOADING;
    @FindBy(css = ".lds-dual-ring-grey")
    WebElement SPINNER_LOADING;

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

    @FindBy(css = "[name = productCostPrice] input")
    WebElement PRODUCT_COST_PRICE_WITHOUT_VARIATION;

    // 0: Display if out of stock
    // 1: Hide remaining stock on online store
    @FindBy(css = "[class=' '] > label > input")
    List<WebElement> SF_DISPLAY_SETTING;
    By LIST_BRANCH_STOCK_WITHOUT_VARIATION_PRODUCT = By.cssSelector(".branch-list-stock__wrapper__row  input");

    By ADD_IMEI_POPUP_BRANCH_DROPDOWN = By.cssSelector(".modal-body button.uik-select__valueRendered");

    By REMOVE_IMEI_ICON = By.cssSelector(".fa-times");

    @FindBy(css = ".modal-body .uik-menuDrop__list > button:nth-child(1)  input")
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

    /**
     * <p>0: Update Price</p>
     * <p>1: Update Stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update Image</p>
     */

    By VARIATION_TABLE_LIST_ACTION = By.cssSelector(".uik-menuDrop__list > button");
    @FindBy(css = "td [name *= stock]")
    List<WebElement> CR_VARIATION_TABLE_STOCK_QUANTITY;

    @FindBy(xpath = "//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[2]/span")
    List<WebElement> VARIATION_TABLE_UP_STOCK_LINK_TEXT;

    @FindBy(xpath = "//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span")
    List<WebElement> VARIATION_TABLE_UP_EDIT_SKU_LINK_TEXT;

    @FindBy(css = "td [name *= sku]")
    List<WebElement> CR_VARIATION_TABLE_SKU;

    @FindBy(css = ".justify-content-center input")
    List<WebElement> UPDATE_SKU_POPUP_SKU_TEXT_BOX;

    @FindBy(css = "td > img")
    List<WebElement> VARIATION_TABLE_IMAGE;

    @FindBy(css = ".modal-content [type = file]")
    WebElement UPDATE_IMAGE_POPUP_UPLOAD_BTN;

    By UPDATE_PRICE_POPUP_LISTING_PRICE = By.cssSelector(".wrapper [name *= 'orgPrice']");
    By UPDATE_PRICE_POPUP_SELLING_PRICE = By.cssSelector(".wrapper [name *= 'discountPrice']");

    By UPDATE_PRICE_POPUP_COST_PRICE = By.cssSelector(".wrapper [name *= 'costPrice']");

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement POPUP_UPDATE_BTN;

    @FindBy(css = ".modal-body .uik-select__valueWrapper")
    WebElement UPDATE_STOCK_POPUP_BRANCH_DROPDOWN;

    @FindBy(css = ".modal-body .uik-menuDrop__list > button:nth-child(1)  input")
    WebElement UPDATE_STOCK_POPUP_BRANCH_DROPDOWN_SELECT_ALL;

    @FindBy(css = ".modal-body  div > div > .gs-button:nth-child(2)")
    WebElement UPDATE_STOCK_POPUP_CHANGE_TAB;

    @FindBy(css = ".modal-body  .quantity-input-field > input")
    WebElement UPDATE_STOCK_POPUP_INPUT_STOCK;

    By UPDATE_STOCK_POPUP_LIST_INPUT_STOCK_TEXT_BOX = By.cssSelector(".input-stock  > input");

    /* Product list page */
    @FindBy(css = ".d-mobile-none .uik-input__input")
    WebElement SEARCH_BOX;

    @FindBy(css = "tbody > tr > td:nth-child(1) > span > b")
    WebElement PRODUCT_ID;

    /* Tien */
    @FindBy(xpath = "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/following-sibling::*/button")
    List<WebElement> DELETE_VARIATION_BTN;

    @FindBy(xpath = "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/parent::*/following-sibling::*/button")
    List<WebElement> DELETE_DEPOSIT_BTN;

    By PRINT_BARCODE_MODAL = By.cssSelector(".modal-content.product-list-barcode-printer");

    @FindBy(xpath = "//input[@class='uik-checkbox__checkbox' and @name='enabledListing']/ancestor::div[contains(@class,'uik-widget__wrapper')]/following-sibling::*/div[1]//span")
    WebElement ADD_DEPOSIT_BTN;

    /* UI text element */
    /* Header */
    @FindBy(css = ".gss-content-header > div > div > a")
    WebElement UI_HEADER_GO_BACK_TO_PRODUCT_LIST;

    @FindBy(css = ".gss-content-header .gs-page-title")
    WebElement UI_HEADER_CR_PAGE_TITLE;

    @FindBy(xpath = "//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/preceding-sibling::button")
    WebElement UI_HEADER_UP_EDIT_TRANSLATION_BTN;
    @FindBy(css = ".gss-content-header .gs-button__green")
    WebElement UI_HEADER_SAVE_BTN;

    @FindBy(css = ".gss-content-header .gs-button__yellow--outline")
    WebElement UI_HEADER_UP_DEACTIVATE_BTN;

    @FindBy(css = ".gss-content-header .gs-button__red--outline")
    WebElement UI_HEADER_UP_DELETE_BTN;

    @FindBy(xpath = "//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/following-sibling::button[contains(@class, 'gs-button__gray--outline')]")
    WebElement UI_HEADER_CANCEL_BTN;

    /* Product information */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(1) > .gs-widget__header > h3")
    WebElement UI_PRODUCT_INFORMATION;

    @FindBy(css = "[for = productName]")
    WebElement UI_PRODUCT_NAME;

    @FindBy(xpath = "//*[@name='productName']/following-sibling::div")
    WebElement UI_PRODUCT_NAME_BLANK_ERROR;

    @FindBy(css = "[for = productDescription]")
    WebElement UI_PRODUCT_DESCRIPTION;

    /* Upload images */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(2) > .gs-widget__header > h3")
    WebElement UI_PRODUCT_IMAGE;

    @FindBy(css = ".image-drop-zone")
    WebElement UI_PRODUCT_IMAGE_DRAG_AND_DROP_CONTENT;

    /* Price and VAT */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(3) > .gs-widget__header > h3")
    WebElement UI_PRODUCT_PRICE;

    @FindBy(xpath = "//*[@name='productPrice']/parent::div/label")
    WebElement UI_PRODUCT_LISTING_PRICE;

    @FindBy(css = "[for = productDiscountPrice]")
    WebElement UI_PRODUCT_SELLING_PRICE;

    @FindBy(xpath = "//*[@name='productCostPrice']/parent::div/label")
    WebElement UI_PRODUCT_COST_PRICE;

    @FindBy(css = "label:not([for]).gs-frm-control__title")
    WebElement UI_VAT;

    @FindBy(css = ".uik-select__option .uik-select__label")
    List<WebElement> UI_VAT_LIST;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(3) .uik-checkbox__label")
    WebElement UI_SHOW_AS_LISTING_PRODUCT_ON_STOREFRONT;

    /* Variation */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3")
    WebElement UI_VARIATIONS;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > span")
    WebElement UI_ADD_VARIATION_BTN;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(4) .gs-widget__content > p")
    WebElement UI_VARIATION_DESCRIPTION;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(1) > label")
    WebElement UI_VARIATION_NAME;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(2) > label")
    WebElement UI_VARIATION_VALUE;

    @FindBy(css = ".second-item .css-151xaom-placeholder")
    List<WebElement> UI_VARIATION_VALUE_PLACEHOLDER;

    // Variation table
    @FindBy(xpath = "//span[contains(@class,'gs-fake-link')]/ancestor::th/div")
    WebElement UI_VARIATION_TABLE_NUMBER_OF_SELECTED_VARIATIONS;

    @FindBy(xpath = "//th/div/div/span")
    WebElement UI_VARIATION_TABLE_SELECT_ACTION_LINK_TEXT;

    /**
     * <p>0: Update price</p>
     * <p>1: Update stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update image</p>
     */
    @FindBy(css = ".uik-menuDrop__list span")
    List<WebElement> UI_VARIATION_TABLE_LIST_ACTIONS;

    /**
     * <p>0: Image</p>
     * <p>1: Listing price</p>
     * <p>2: Selling price</p>
     * <p>3: Cost price</p>
     * <p>4: Stock quantity</p>
     * <p>5: SKU</p>
     * <p>6: Barcode</p>
     */
    @FindBy(xpath = "//th[@class=' align-middle'][1] | //th[contains(@class, 'text-center')]")
    List<WebElement> UI_VARIATION_TABLE_COLUMN;

    @FindBy(xpath = "//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span")
    List<WebElement> UI_VARIATION_TABLE_UP_EDIT_SKU;

    // Update variation price popup
    @FindBy(css = ".modal-title")
    WebElement UI_UPDATE_PRICE_POPUP_TITLE;

    @FindBy(css = ".modal-body .uik-select__valueWrapper")
    WebElement UI_UPDATE_PRICE_POPUP_PRICE_DROPDOWN;

    /**
     * <p>0: Listing price</p>
     * <p>1: Selling price</p>
     * <p>2: Cost price</p>
     */
    @FindBy(css = ".uik-select__optionList span")
    List<WebElement> UI_UPDATE_PRICE_POPUP_LIST_PRICE_IN_DROPDOWN;

    @FindBy(css = ".modal-body .gs-button__blue")
    WebElement UI_UPDATE_PRICE_POPUP_APPLY_ALL_BTN;

    /**
     * <p>size() - 3: Listing price</p>
     * <p>size() - 2: Selling price</p>
     * <p>size() - 1: Cost price</p>
     */
    @FindBy(css = ".modal-body tr > th")
    List<WebElement> UI_UPDATE_PRICE_POPUP_LIST_PRICE_COLUMN;

    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement UI_UPDATE_PRICE_POPUP_CANCEL_BTN;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_UPDATE_PRICE_POPUP_UPDATE_BTN;

    // Update normal variation stock popup
    @FindBy(css = ".modal-title")
    WebElement UI_UPDATE_STOCK_POPUP_TITLE;

    @FindBy(css = ".modal-body .uik-select__valueWrapper")
    WebElement UI_UPDATE_STOCK_POPUP_BRANCH_DROPDOWN;

    /**
     * <p>0: Add stock</p>
     * <p>1: Change stock</p>
     */
    @FindBy(css = ".modal-body div > div > .gs-button")
    List<WebElement> UI_UPDATE_STOCK_POPUP_LIST_ACTION;

    @FindBy(css = "[name= quantity]")
    WebElement UI_UPDATE_STOCK_POPUP_INPUT_STOCK_PLACEHOLDER;

    @FindBy(css = ".modal-body strong")
    WebElement UI_UPDATE_STOCK_ACTION_TYPE;

    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement UI_UPDATE_STOCK_POPUP_CANCEL_BTN;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_UPDATE_STOCK_POPUP_UPDATE_BTN;

    // Add IMEI/Serial number popup
    @FindBy(css = ".modal-title")
    WebElement UI_ADD_IMEI_POPUP_TITLE;

    @FindBy(css = ".branch > h3")
    WebElement UI_ADD_IMEI_POPUP_BRANCH;

    By UI_ADD_IMEI_POPUP_BRANCH_DROPDOWN = By.cssSelector(".modal-body .uik-select__valueWrapper");

    @FindBy(css = ".input-code input")
    List<WebElement> UI_ADD_IMEI_POPUP_INPUT_IMEI_PLACEHOLDER;

    @FindBy(css = ".modal-body thead > tr > th:nth-child(1)")
    WebElement UI_ADD_IMEI_POPUP_PRODUCT_NAME_COLUMN;

    @FindBy(css = ".modal-footer .gs-button__white ")
    WebElement UI_ADD_IMEI_POPUP_CANCEL_BTN;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_ADD_IMEI_POPUP_SAVE_BTN;

    // Update variation SKU popup
    @FindBy(css = ".modal-title")
    WebElement UI_UPDATE_SKU_POPUP_TITLE;

    @FindBy(css = ".modal-body .uik-select__valueWrapper")
    WebElement UI_UPDATE_SKU_POPUP_BRANCH_DROPDOWN;

    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement UI_UPDATE_SKU_POPUP_CANCEL_BTN;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_UPDATE_SKU_POPUP_UPDATE_BTN;

    // Update variation image popup
    @FindBy(css = ".modal-title")
    WebElement UI_UPLOAD_IMAGE_POPUP_TITLE;

    @FindBy(css = ".modal-body .image-uploader__text")
    WebElement UI_UPLOAD_IMAGE_POPUP_UPLOAD_IMAGE_BTN_PLACEHOLDER;

    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement UI_UPLOAD_IMAGE_POPUP_CANCEL_BTN;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_UPLOAD_IMAGE_POPUP_SELECT_BTN;

    /* Conversion unit */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(5) > .gs-widget__header > h3 > span")
    WebElement UI_UNIT;

    @FindBy(css = "#input-search")
    WebElement UI_CONVERSION_UNIT_SEARCH_BOX_PLACEHOLDER;

    @FindBy(xpath = "//*[@name='conversionUnitCheckbox']/parent::label/div")
    WebElement UI_ADD_CONVERSION_UNIT_LABEL;

    @FindBy(css = "[aria-describedby='tippy-tooltip-1'],[aria-describedby='tippy-tooltip-13']")
    WebElement UI_CONVERSION_UNIT_TOOLTIPS;

    /* Wholesale product */
    @FindBy(xpath = "//*[@name='enabledListing']/parent::label")
    WebElement UI_ADD_WHOLESALE_PRICING_LABEL;

    /* Deposit */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > h3")
    WebElement UI_DEPOSIT;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > span")
    WebElement UI_ADD_DEPOSIT_BTN;

    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(7)  .gs-widget__content > p")
    WebElement UI_DEPOSIT_DESCRIPTION;

    /* SEO */
    @FindBy(css = "[class $= --n1] > .gs-widget:nth-child(8) > .gs-widget__header > h3")
    WebElement UI_SEO_SETTING;

    @FindBy(xpath = "//div[@aria-describedby='tippy-tooltip-2']/parent::div/preceding-sibling::span")
    WebElement UI_LIVE_PREVIEW;

    @FindBy(css = "[aria-describedby='tippy-tooltip-2']")
    WebElement UI_LIVE_PREVIEW_TOOLTIPS;

    @FindBy(xpath = "//div[@aria-describedby='tippy-tooltip-3']/parent::div/preceding-sibling::span")
    WebElement UI_SEO_TITLE;

    @FindBy(css = "[aria-describedby='tippy-tooltip-3']")
    WebElement UI_SEO_TITLE_TOOLTIPS;

    @FindBy(xpath = "//div[@aria-describedby='tippy-tooltip-4']/parent::div/preceding-sibling::span")
    WebElement UI_SEO_DESCRIPTION;

    @FindBy(css = "[aria-describedby='tippy-tooltip-4']")
    WebElement UI_SEO_DESCRIPTIONS_TOOLTIPS;

    @FindBy(xpath = "//div[@aria-describedby='tippy-tooltip-5']/parent::div/preceding-sibling::span")
    WebElement UI_SEO_KEYWORDS;

    @FindBy(css = "[aria-describedby='tippy-tooltip-5']")
    WebElement UI_SEO_KEYWORDS_TOOLTIPS;

    @FindBy(xpath = "//div[@class='mb-2'][last()]")
    WebElement UI_SEO_URL_LINK;

    /* Sale chanel */
    @FindBy(css = "[class $= --n2] > div:nth-child(1) h3")
    WebElement UI_SALE_CHANEL;

    @FindBy(css = ".store-front")
    WebElement UI_SALE_CHANEL_ONLINE_SHOP;

    @FindBy(css = "#tippy-tooltip-6 .tippy-tooltip-content")
    WebElement UI_SALE_CHANEL_ONLINE_SHOP_TOOLTIPS;

    @FindBy(css = ".beecow")
    WebElement UI_SALE_CHANEL_GOMUA;

    @FindBy(css = "#tippy-tooltip-7 .tippy-tooltip-content")
    WebElement UI_SALE_CHANEL_GOMUA_TOOLTIPS;

    @FindBy(css = ".shopee")
    WebElement UI_SALE_CHANEL_SHOPEE;

    @FindBy(css = "#tippy-tooltip-8 .tippy-tooltip-content")
    WebElement UI_SALE_CHANEL_SHOPEE_TOOLTIPS;

    @FindBy(css = "[aria-describedby='tippy-tooltip-9']")
    WebElement UI_SALE_CHANEL_TIKTOK;

    @FindBy(css = "#tippy-tooltip-9 .tippy-tooltip-content")
    WebElement UI_SALE_CHANEL_TIKTOK_TOOLTIPS;

    /* Collections */
    @FindBy(css = "[class $= --n2] > div:nth-child(2) h3")
    WebElement UI_COLLECTIONS;

    @FindBy(css = ".product-form-collection-selector2 input")
    WebElement UI_COLLECTIONS_SEARCH_BOX_PLACEHOLDER;

    @FindBy(css = ".product-form-collection-selector2 > .no-content")
    WebElement UI_COLLECTION_NO_CREATED_COLLECTION;

    /* Warehousing */
    @FindBy(css = "[class $= --n2] > div:nth-child(3) h3")
    WebElement UI_WAREHOUSING;

    @FindBy(css = "[for = 'productSKU']")
    WebElement UI_CR_WITHOUT_VARIATION_PRODUCT_SKU;

    @FindBy(css = "[class *=--n2] > div:nth-child(3) .align-items-center > label")
    WebElement UI_UP_WITHOUT_VARIATION_PRODUCT_SKU;

    @FindBy(css = "[for = 'barcode']")
    WebElement UI_BARCODE;

    @FindBy(css = "[for = 'manageInventory']")
    WebElement UI_MANAGE_INVENTORY;

    @FindBy(css = "#manageInventory > [value = PRODUCT]")
    WebElement UI_MANAGE_INVENTORY_BY_PRODUCT;

    @FindBy(css = "#manageInventory > [value = IMEI_SERIAL_NUMBER]")
    WebElement UI_MANAGE_INVENTORY_BY_IMEI;

    @FindBy(css = ".Notice-product-quantity")
    WebElement UI_MANAGE_INVENTORY_BY_IMEI_NOTICE;

    @FindBy(css = "[for = 'productQuantity']")
    WebElement UI_STOCK_QUANTITY;

    @FindBy(xpath = "//*[@for = 'productQuantity']/following-sibling::div[@class = 'row']/button/div")
    WebElement UI_STOCK_QUANTITY_APPLY_ALL_BTN;

    @FindBy(css = "[for = remaining]")
    WebElement UI_REMAINING_STOCK_LABEL;

    @FindBy(xpath = "//*[@for='remaining']/following-sibling::span")
    WebElement UI_REMAINING_STOCK_LINK_TEXT;

    @FindBy(css = ".modal-title")
    WebElement UI_VIEW_REMAINING_STOCK_POPUP_TILE;

    @FindBy(css = ".remaining-sold-item-modal__body__dropdown > button")
    WebElement UI_VIEW_REMAINING_STOCK_POPUP_BRANCH_DROPDOWN;

    @FindBy(xpath = "//*[contains(@class,'modal-body')]/descendant::label[1]")
    WebElement UI_VIEW_REMAINING_STOCK_POPUP_ALL_BRANCHES_CHECKBOX;

    @FindBy(css = ".remaining-sold-item-modal__body__error")
    WebElement UI_VIEW_REMAINING_STOCK_POPUP_NO_SELECT_BRANCH_ERROR;

    @FindBy(css = ".modal-header > .close")
    WebElement UI_CLOSE_VIEW_REMAINING_STOCK_POPUP_BTN;
    @FindBy(css = "[for = soldItem]")
    WebElement UI_SOLD_COUNT_LABEL;

    @FindBy(xpath = "//*[@for='soldItem']/following-sibling::span")
    WebElement UI_SOLD_COUNT_LINK_TEXT;

    By UI_VIEW_SOLD_COUNT_POPUP_TILE = By.cssSelector(".modal-title");

    By UI_VIEW_SOLD_COUNT_POPUP_BRANCH_DROPDOWN = By.cssSelector(".remaining-sold-item-modal__body__dropdown > button");

    @FindBy(xpath = "//*[contains(@class,'modal-body')]/descendant::label[1]")
    WebElement UI_VIEW_SOLD_COUNT_POPUP_ALL_BRANCHES_CHECKBOX;

    @FindBy(css = ".remaining-sold-item-modal__body__error")
    WebElement UI_VIEW_SOLD_COUNT_POPUP_NO_SELECT_BRANCH_ERROR;

    @FindBy(css = ".modal-header > .close")
    WebElement UI_CLOSE_VIEW_SOLD_COUNT_POPUP_BTN;

    @FindBy(xpath = "//*[@id='showOutOfStock']/parent::div/preceding-sibling::label/div")
    WebElement UI_DISPLAY_IF_OUT_OF_STOCK;

    @FindBy(xpath = "//*[@id='isHideStock']/parent::div/preceding-sibling::label/div")
    WebElement UI_HIDE_REMAINING_STOCK_ON_ONLINE_STORE;

    /* Package information */
    @FindBy(css = "[class $= --n2] > div:nth-child(4) h3")
    WebElement UI_PACKAGE_INFORMATION;

    @FindBy(css = "[aria-describedby = tippy-tooltip-10]")
    WebElement UI_PACKAGE_INFORMATION_TOOLTIPS;

    @FindBy(css = "[for = productWeight]")
    WebElement UI_WEIGHT;

    @FindBy(css = "[for = productLength]")
    WebElement UI_LENGTH;

    @FindBy(css = "[for = productWidth]")
    WebElement UI_WIDTH;

    @FindBy(css = "[for = productHeight]")
    WebElement UI_HEIGHT;

    @FindBy(css = "[class $= --n2] > div:nth-child(4)  p > em")
    WebElement UI_SHIPPING_FEE_NOTE;

    /* Priority */
    @FindBy(css = "[class $= --n2] > div:nth-child(5) h3")
    WebElement UI_PRIORITY;

    @FindBy(css = "[aria-describedby = tippy-tooltip-11]")
    WebElement UI_PRIORITY_TOOLTIPS;

    @FindBy(css = "[name = productPriority]")
    WebElement UI_PRIORITY_PLACE_HOLDER;

    /* Platform */
    @FindBy(css = "[class $= --n2] > div:nth-child(6) h3")
    WebElement UI_PLATFORM;

    @FindBy(css = "[aria-describedby = tippy-tooltip-12]")
    WebElement UI_PLATFORM_TOOLTIPS;

    @FindBy(xpath = "//*[@name='onApp']/following-sibling::div")
    WebElement UI_APP;

    @FindBy(xpath = "//*[@name='onWeb']/following-sibling::div")
    WebElement UI_WEB;

    @FindBy(xpath = "//*[@name='inStore']/following-sibling::div")
    WebElement UI_IN_STORE;

    @FindBy(xpath = "//*[@name='inGosocial']/following-sibling::div")
    WebElement UI_GOSOCIAL;
}
