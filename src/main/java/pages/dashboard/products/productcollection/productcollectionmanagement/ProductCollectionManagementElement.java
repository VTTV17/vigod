package pages.dashboard.products.productcollection.productcollectionmanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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
    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement OK_BTN_ON_MODAL;
    @FindBy(xpath = "//h5[@class='gs-page-title']/div")
    WebElement PAGE_TITLE_AND_TOTAL_NUMBER;
    @FindBy(css = ".gs-search-box__wrapper input")
    WebElement SEARCH_COLLECTION_INPUT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[1]")
    WebElement THUMBNAIL_COL_TXT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[2]")
    WebElement COLLECTION_NAME_COL_TXT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[3]")
    WebElement TYPE_COL_TXT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[4]")
    WebElement MODE_COL_TXT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[5]")
    WebElement ITEMS_COL_TXT;
    @FindBy(xpath = "(//section[@class='gs-table-header-item']/span)[6]")
    WebElement ACTIONS_COL_TXT;
    @FindBy(css = ".modal-body")
    WebElement MODAL_CONTENT;
    @FindBy(css = ".modal-title")
    WebElement MODAL_TITLE;
    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement MODAL_CANCEL_BTN;

}
