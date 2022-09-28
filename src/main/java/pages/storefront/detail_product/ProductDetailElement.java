package pages.storefront.detail_product;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductDetailElement {
    WebDriver driver;
    public ProductDetailElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#item-detail .title")
    WebElement PRODUCT_NAME;

    @FindBy (css = ".price-disc")
    WebElement SELLING_PRICE;

    @FindBy (css = ".price-org")
    WebElement LISTING_PRICE;

    @FindBy (css = "span[rv-text='variation.label']")
    List<WebElement> LIST_VARIATION_NAME;

    @FindBy (css = ".type-of-item span")
    List<WebElement> LIST_VARIATION_VALUE;

    @FindBy (css = "#branch-list .stock")
    List<WebElement> STOCK_QUANTITY_IN_BRANCH;

    @FindBy (css = "#product-description")
    WebElement PRODUCT_DESCRIPTION;


    @FindBy (css = ".sold-out")
    WebElement SOLD_OUT_MARK;
}
