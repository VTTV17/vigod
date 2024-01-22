package web.StoreFront.quicklycheckout;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class QuicklyCheckoutElement {
    @FindBy(xpath = "//div[contains(@class,'product-information')]/following::div[1]")
    WebElement ERROR_MESSAGE;
    @FindBy(xpath = "//div[@class='card-header']//span[2]")
    WebElement CARD_HEADER;

    @FindBy(css = ".product-name")
    List<WebElement> PRODUCT_NAME_LIST;
    @FindBy(xpath = "//div[contains(@class,'card')]//th[1]")
    WebElement PRODUCT_LBL;
    @FindBy(xpath = "//div[contains(@class,'card')]//th[2]")
    WebElement QUANTITY_LBL;
    @FindBy(xpath = "//div[contains(@class,'card')]//th[3]")
    WebElement UNIT_PRICE_LBL;
    @FindBy(xpath = "//div[contains(@class,'card-footer row')]//span")
    WebElement COUPON_CODE_LBL;
    @FindBy(css = ".quick-checkout .btn")
    WebElement HOME_BTN;

}