package pages.storefront.checkout.ordercomplete;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class OrderCompleteElement {
    WebDriver driver;
    public OrderCompleteElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_btnBackToMarket = By.cssSelector(".position-sticky .btn");
    By loc_lst_lblProductName = By.cssSelector(".col-product-info >div .product-name");
    By loc_lblDiscountAmount = By.xpath("(//span[contains(@class,'font-weight-bold')])[3]");
    By loc_lblShippingFee = By.xpath("//span[@class='text-decoration-line-through']//following-sibling::span");
//    @FindBy(css = ".position-sticky .btn")
//    WebElement BACK_TO_MARKET_BTN;
//    @FindBy(css = ".col-product-info >div .product-name")
//    List<WebElement> PRODUCT_NAME_LIST;
//    @FindBy(xpath = "(//span[contains(@class,'font-weight-bold')])[3]")
//    WebElement DISCOUNT_AMOUNT;
//    @FindBy(xpath = "//span[@class='text-decoration-line-through']//following-sibling::span")
//    WebElement SHIPPING_FEE;
}
