package pages.storefront.checkout.checkoutstep3;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CheckOutStep3Element {
    WebDriver driver;
    public CheckOutStep3Element(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(xpath = "(//*[@id='checkout-footer-btn-continue' and not (contains(@class,'d-none'))])[1]")
    WebElement NEXT_BUTTON;
    @FindBy(css = ".bi-caret-up-fill")
    WebElement ARROW_ICON_NEXT_TO_TOTAL_AMOUNT;
    @FindBy(xpath = "(//td[contains(@class,'sub-price')])[2]/span")
    WebElement DISCOUNT_AMOUNT ;
    @FindBy(xpath = "//span[@class='text-decoration-line-through']//following-sibling::span")
    WebElement SHIPPING_FEE;
    @FindBy(css = ".d-md-table .product-name")
    List<WebElement> PRODUCT_NAMES;
}
