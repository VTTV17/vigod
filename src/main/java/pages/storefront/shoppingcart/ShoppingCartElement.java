package pages.storefront.shoppingcart;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ShoppingCartElement {
    WebDriver driver;
    public ShoppingCartElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(xpath = "//div[contains(@class,'shop-cart-footer')]/button")
    WebElement CONTINUE_BTN;

    By BRANCH_INFO = By.cssSelector(".branch-listing-item");

    By BRANCH_NAME = By.cssSelector(".branch-name");

    By PRODUCT_INFO = By.cssSelector(".prod-row");

    By PRODUCT_NAME = By.cssSelector(".prod-detail .prod-name");

    By VARIATION_VALUE = By.cssSelector(".prod-detail .icon-variation > span");

    By UNIT_PRICE = By.cssSelector("div.desktop-display .unit-price");
    By COUPON_CODE = By.cssSelector("div.desktop-display .coupon-code");
    By CONVERSION_UNIT = By.cssSelector(".prod-cell > .conversion-unit");
    By TOTAL_PRICE = By.cssSelector(".prod-cell > .total-price");
}
