package pages.dashboard.promotion.discount;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DiscountElement {
    public WebDriver driver;

    public DiscountElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".d-flex > div:not(.second-button-group)")
    WebElement CREATE_PROMOTION_BTN;

    @FindBy (xpath = "(//div[contains(@class,'discount-header')]//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
    WebElement PRODUCT_DISCOUNT_CODE;
    
    @FindBy (xpath = "(//div[contains(@class,'discount-header')]//div[contains(@class,'uik-menuDrop__list')]//button)[2]")
    WebElement SERVICE_DISCOUNT_CODE;

    @FindBy(css = "div.second-button-group > button")
    public WebElement WHOLESALE_PRICING_BTN;

    @FindBy (css = "div.second-button-group > div > button:nth-child(1)")
    public WebElement PRODUCT_WHOLESALE_PRICING;

    @FindBy(xpath = ".search-input")
    WebElement UI_SEARCH_PLACEHOLDER;
}
