package pages.dashboard.products.all_products.wholesale_price;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class WholesalePriceElement {
    WebDriver driver;

    public WholesalePriceElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    
    @FindBy(css = ".wholesale-btn-group-header > .gs-button__gray--outline")
    WebElement NO_VARIATION_ADD_WHOLESALE_PRICE_BTN;

    @FindBy(css = ".wholesale-btn-group-header > .gs-button__gray--outline")
    WebElement ADD_VARIATION_BTN;
    
    @FindBy(css = ".product-list label")
    List<WebElement> LIST_VARIATION;

    @FindBy(css = ".modal-body .gs-button__green")
    WebElement OK_BTN;

    @FindBy(css = ".wholesale-group-header > div > .gs-fake-link:nth-child(1)")
    List<WebElement> HAS_VARIATION_LIST_ADD_WHOLESALE_PRICE_BTN;

    @FindBy (css = "[name^='wholesaleTitle']")
    List<WebElement> WHOLESALE_TITLE;

    @FindBy (css = "[name^='buyFrom-']")
    List<WebElement> WHOLESALE_BUY_FROM;

    @FindBy (css = "[name^='pricePerItem-'] input")
    List<WebElement> WHOLE_SALE_PRICE_PER_ITEM;

    @FindBy(css = ".dropdown-search-checkbox-custom")
    List<WebElement> SEGMENT_DROPDOWN;

    @FindBy(css = ".select-customer-options .label > input")
    List<WebElement> LIST_SEGMENT_CHECKBOX;

    @FindBy(css = ".select-customer-options .label > label")
    List<WebElement> LIST_SEGMENT_LABEL;

    @FindBy(css = ".wholesale-btn-group-header > .gs-button__green")
    WebElement SAVE_BTN;
}
