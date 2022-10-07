package pages.storefront.checkout.checkoutstep3;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckOutStep3Element {
    WebDriver driver;
    public CheckOutStep3Element(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".box-icon-paypal #checkout-footer-btn-continue")
    WebElement NEXT_BUTTON;
}
