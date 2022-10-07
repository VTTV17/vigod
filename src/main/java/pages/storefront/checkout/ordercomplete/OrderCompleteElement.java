package pages.storefront.checkout.ordercomplete;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OrderCompleteElement {
    WebDriver driver;
    public OrderCompleteElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".position-sticky .btn")
    WebElement BACK_TO_MARKET_BTN;
}
