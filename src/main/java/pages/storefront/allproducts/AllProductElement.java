package pages.storefront.allproducts;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class AllProductElement {
    WebDriver driver;
    public AllProductElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(xpath = "//div[contains(@class,'price-group')]")
    List<WebElement> PRODUCTS_PRICE;
    String OutOfStockXpath="(//div[contains(@class,'price-group')])[%s]//parent::div//preceding-sibling::div//div[contains(@class,'out-of-stock-label-product')]";
}
