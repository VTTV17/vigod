package pages.storefront.checkout.checkoutstep2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckOutStep2Element {
    WebDriver driver;
    public CheckOutStep2Element(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".summary #checkout-footer-btn-continue")
    WebElement NEXT_BUTTON;
    @FindBy(xpath = "//select[contains(@id,'select-shipping-plan')]")
    WebElement SELECT_SHIPPING_METHOD;
    @FindBy(css = ".bi-caret-up-fill")
    WebElement ARROW_ICON_NEXT_TO_TOTAL_AMOUNT;
    @FindBy(xpath = "(//td[contains(@class,'sub-price')])[2]/span")
    WebElement DISCOUNT_AMOUNT ;
    @FindBy(css = ".delivery-option .color-red")
    WebElement SHIPPING_FEE;

}
