package pages.storefront.productcollection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ProductCollectionSFElement {
    WebDriver driver;
    public ProductCollectionSFElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".product-name")
    List<WebElement> PRODUCT_NAMES;
    @FindBy(xpath = "//meta[@name='title']")
    WebElement META_TITLE;
    @FindBy(xpath = "//meta[@name='description']")
    WebElement META_DESCRIPTION;
    @FindBy(xpath = "//meta[@name='keywords']")
    WebElement META_KEYWORD;
    @FindBy(xpath = "//div[@class='title-view']//div[contains(@class,'title')]//a[last()]")
    WebElement PRODUCT_COLLECTION_NAME;
    @FindBy(css = ".list-item .page-item")
    List<WebElement> PAGING_PAGE_LIST;
    @FindBy(xpath = "//li[@class='page-item active']")
    WebElement PAGE_ACTIVE;
    String PAGE_IN_PAGINATION_DYNAMIC_XP = "//li[@class='page-item']/a[text()='%s']";
}
