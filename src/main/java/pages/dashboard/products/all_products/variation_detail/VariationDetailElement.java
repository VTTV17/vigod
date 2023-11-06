package pages.dashboard.products.all_products.variation_detail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class VariationDetailElement {
    WebDriver driver;
    VariationDetailElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#variationName")
    WebElement PRODUCT_VERSION_NAME;

    @FindBy(css = ".des-header > div > label > input")
    WebElement REUSE_DESCRIPTION_CHECKBOX;

    @FindBy(css = ".fr-element")
    WebElement DESCRIPTION;

    @FindBy(css = ".gss-content-header .gs-button__green")
    WebElement SAVE_BTN;

    By EDIT_TRANSLATION_BTN = By.cssSelector(".gss-content-header span > .gs-button__gray--outline");

    @FindBy(css = ".gss-content-header .gs-button__yellow--outline, .gs-button__cyan--outline")
    WebElement DEACTIVATE_BTN;

    By POPUP = By.cssSelector(".modal-content");

    @FindBy(css = ".uik-select__valueRenderedWrapper .text-truncate")
    WebElement EDIT_TRANSLATION_POPUP_SELECTED_LANGUAGE;

    @FindBy(css = ".uik-select__optionList .text-truncate")
    List<WebElement> EDIT_TRANSLATION_POPUP_LIST_LANGUAGE;

    @FindBy(css = "#informationName")
    WebElement EDIT_TRANSLATION_POPUP_PRODUCT_NAME;

    @FindBy(css = ".modal-body .fr-element")
    WebElement EDIT_TRANSLATION_POPUP_DESCRIPTION;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement EDIT_TRANSLATION_POPUP_SAVE_BTN;

    @FindBy(css = ".modal-header > button")
    WebElement EDIT_TRANSLATION_POPUP_CLOSE_BTN;

    /* UI */
    @FindBy(css = ".toolbar__product-status")
    WebElement UI_HEADER_VARIATION_STATUS;

    @FindBy(css = ".gss-content-header span > .gs-button__gray--outline")
    WebElement UI_HEADER_EDIT_TRANSLATION_BTN;

    @FindBy(css = ".gss-content-header .gs-button__green")
    WebElement UI_HEADER_SAVE_BTN;

    @FindBy(css = ".gss-content-header .gs-button__green ~.gs-button__gray--outline")
    WebElement UI_HEADER_CANCEL_BTN;

    @FindBy(css = ".gss-content-header .gs-button__yellow--outline, .gs-button__cyan--outline")
    WebElement UI_HEADER_DEACTIVATE_BTN;

    @FindBy(xpath = "//*[@for='productName']/parent::div/parent::div/preceding-sibling::div")
    WebElement UI_PRODUCT_VERSION;

    @FindBy(css = "[for='productName']")
    WebElement UI_PRODUCT_VERSION_NAME;

    @FindBy(css = "[for='productDescription']")
    WebElement UI_DESCRIPTION;

    @FindBy(xpath = "//*[@for='productDescription']/following-sibling::div")
    WebElement UI_REUSE_DESCRIPTION;

    @FindBy(xpath = "//*[@for='productName']/parent::div/parent::div/parent::div/following-sibling::div[1]//h3")
    WebElement UI_IMAGES;

    @FindBy(xpath = "//*[@for='productName']/parent::div/parent::div/parent::div/following-sibling::div[2]//h3")
    WebElement UI_PRICING;

    @FindBy(css = "[for='productPrice']")
    WebElement UI_LISTING_PRICE;

    @FindBy(css = "[for='productDiscountPrice']")
    WebElement UI_SELLING_PRICE;

    @FindBy(css = ".variation-number")
    WebElement UI_NUM_OF_VARIATIONS;

    @FindBy(xpath = "//*[contains(@class, 'branch-list')]/parent::div/preceding-sibling::div")
    WebElement UI_BRANCH;

    @FindBy(xpath = "//*[contains(@class, 'variation-list')]/parent::div/parent::div/preceding-sibling::div")
    WebElement UI_VARIATION;

    @FindBy(xpath = "//*[@for='remaining']/parent::div/parent::div/preceding-sibling::div/h3")
    WebElement UI_WAREHOUSING;

    @FindBy(xpath = "//*[@for='remaining']/parent::div/parent::div/preceding-sibling::div/span")
    WebElement UI_UPDATE_STOCK;

    @FindBy(css = "[for='variationSKU']")
    WebElement UI_SKU;

    @FindBy(css = "[for='barcode']")
    WebElement UI_BARCODE;

    @FindBy(css = "[for='remaining']")
    WebElement UI_REMAINING_STOCK;

    @FindBy(css = "[for='soldItem']")
    WebElement UI_SOLD_COUNT;

    @FindBy(css = ".product-translate__titleHeader > p")
    WebElement UI_EDIT_TRANSLATION_POPUP_TITLE;

    @FindBy(css = ".product-translate__titleBody > h3")
    WebElement UI_EDIT_TRANSLATION_POPUP_INFORMATION;

    @FindBy(css = "[for='informationName']")
    WebElement UI_EDIT_TRANSLATION_POPUP_NAME;

    @FindBy(xpath = "//*[@for='informationName']/parent::div/following-sibling::div/label")
    WebElement UI_EDIT_TRANSLATION_POPUP_DESCRIPTION;

    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement UI_EDIT_TRANSLATION_POPUP_SAVE_BTN;

    @FindBy(css = ".modal-footer .gs-button__white")
    WebElement UI_EDIT_TRANSLATION_POPUP_CANCEL_BTN;
}
