package web.StoreFront.checkout.ordercomplete;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class OrderCompleteElement {
    WebDriver driver;
    public OrderCompleteElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_btnBackToHome = By.cssSelector(".checkout-right button");
    By loc_lst_lblProductName = By.cssSelector(".prod-name");
    By loc_lblDiscountAmount = By.cssSelector(".payment-info .payment-info__value--discount span");
    By loc_lblShippingFee = By.cssSelector(".payment-info .text-right");

}
