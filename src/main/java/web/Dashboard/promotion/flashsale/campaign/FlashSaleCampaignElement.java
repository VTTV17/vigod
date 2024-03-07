package web.Dashboard.promotion.flashsale.campaign;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class FlashSaleCampaignElement {
    WebDriver driver;

    public FlashSaleCampaignElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#campaign-name")
    WebElement CAMPAIGN_NAME;

    @FindBy(css = ".date > input")
    WebElement DATE_SET;

    @FindBy(css = ".time > div:nth-child(2) > div:nth-child(4) > button")
    WebElement TIME_SET;

    @FindBy(css = ".time > div:nth-child(2) > div:nth-child(4) > div  button")
    List<WebElement> TIME_DROPDOWN;

    @FindBy(css = ".gs-fake-link")
    WebElement ADD_PRODUCT_BTN;

    @FindBy(css = ".search-input")
    WebElement SEARCH_BOX;

    @FindBy(css = ".gs-table-body")
    WebElement TABLE_BODY;

    @FindBy(css = ".product-list .header")
    WebElement SELECT_ALL_PRODUCT_MATCH_CONDITION;

    @FindBy(css = ".footer-btn .gs-button__green")
    WebElement OK_BTN;

    @FindBy(css = ".row-product-name")
    List<WebElement> PRODUCT_NAME;

    // variation = PRODUCT_VARIATION.getText().split(PRODUCT_NAME.getText())[1]
    @FindBy(css = "tbody > tr > td > div >  div:nth-child(2)")
    List<WebElement> PRODUCT_VARIATION;

    @FindBy(css = "tr > td:nth-child(2) > div > span")
    List<WebElement> LIST_PRICE;

    @FindBy(css = "tr > td:nth-child(3) > div > div > div > input")
    List<WebElement> LIST_FLASH_SALE_PRICE;

    @FindBy(css = "tr > td:nth-child(4) > div > span")
    List<WebElement> LIST_REMAINING_STOCK;

    @FindBy(css = "tr > td:nth-child(5) > div > div > div > div > input")
    List<WebElement> LIST_FLASH_SALE_STOCK;

    @FindBy(css = "tr > td:nth-child(6) > div > div > div > div > input")
    List<WebElement> LIST_MAX_PURCHASE_LIMIT;

    @FindBy(css = ".create-flash-sale-campaign .gs-button__green")
    WebElement SAVE_BTN;

    @FindBy(css = "tr > td:nth-child(6) > div > button > div > img")
    List<WebElement> DELETE_ICON;

    @FindBy(css = ".information h3")
    WebElement UI_PRODUCT_INFORMATION;
    public By loc_txtCampaignName = By.cssSelector("#campaign-name");
    public By loc_btnAddProduct = By.cssSelector(".gs-fake-link");
    By loc_dlgSelectProduct = By.cssSelector(".select-product-modal");
}
