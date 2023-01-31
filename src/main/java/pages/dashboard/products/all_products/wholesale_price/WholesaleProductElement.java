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

    By VARIATION_ADD_VARIATION_POPUP_LIST_VARIATION_CHECKBOX = By.cssSelector(".product-list input");

    @FindBy(css = ".footer-btn .gs-button__green")
    WebElement VARIATION_ADD_VARIATION_POPUP_OK_BTN;

    By VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN = By.cssSelector(".border-bottom > .wholesale-group-header .gs-fake-link:nth-child(1)");

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
}
