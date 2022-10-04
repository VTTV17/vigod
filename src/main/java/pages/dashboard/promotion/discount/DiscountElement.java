package pages.dashboard.promotion.discount;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DiscountElement {
    WebDriver driver;

    public DiscountElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "div.second-button-group > button > div")
    WebElement WHOLESALE_PRICING_BTN;

    @FindBy (css = "div.second-button-group > div > button:nth-child(1)")
    WebElement PRODUCT_WHOLESALE_PRICING;
}
