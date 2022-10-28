package pages.dashboard.products.productcollection.productcollectionmanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import pages.dashboard.products.productcollection.createproductcollection.CreateProductCollection;

import java.util.List;

public class ProductCollectionManagementElement {
    WebDriver driver;
    public ProductCollectionManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".gs-content-header-right-el button")
    WebElement CREATE_PRODUCT_COLLECTION_BTN;
    @FindBy(css = ".collection-name b")
    List<WebElement> COLLECTION_NAMES;
    @FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]")
    List<WebElement> TYPES;
    @FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]")
    List<WebElement> MODES;
    @FindBy(xpath = "//div[contains(@class,'products')]")
    List<WebElement> ITEMS;
    @FindBy(css = ".actions .first-button")
    List<WebElement> EDIT_BTN;
    @FindBy(css = ".actions .lastest-button")
    List<WebElement> DELETE_BTN;
}
