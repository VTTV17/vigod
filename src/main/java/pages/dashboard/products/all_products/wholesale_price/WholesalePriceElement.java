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

    @FindBy(css = ".border-bottom span:nth-child(1)")
    List<WebElement> HAS_VARIATION_LIST_ADD_WHOLESALE_PRICE_BTN;

    // 0: wholesale title
    // 1: buy from
    // 2: price per item
    //(3i, 3i + 1, 3i + 2)
    @FindBy(css = "div.wholesale-grid-item input:not([readonly])")
    List<WebElement> WHOLESALE_PRICE_CONFIGURE;

    @FindBy(css = ".gs-page-content--max")
    List<WebElement> TOTAL_WHOLESALE_CONFIG;

    @FindBy(css = ".dropdown-search-checkbox-custom")
    List<WebElement> LIST_SEGMENT_BTN;

    @FindBy(css = "div.label > input")
    List<WebElement> LIST_SEGMENT_CHECKBOX;

    @FindBy(css = "div.label > label")
    List<WebElement> LIST_SEGMENT_LABEL;

    @FindBy(css = ".wholesale-btn-group-header > .gs-button__green")
    WebElement SAVE_BTN;
}
