package pages.dashboard.home;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePageElement {
    WebDriver driver;
    public HomePageElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".header-right .store-detail__name")
    WebElement SHOP_NAME;
    @FindBy(xpath = "//div[@class='title']")
    WebElement HOME_PAGE_TITLE;
    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[1]")
    WebElement GOPOS_LBL;
    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[2]")
    WebElement GOWEB_LBL;
    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[3]")
    WebElement GOAPP_LBL;
    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[2]/following-sibling::span")
    WebElement GOWEB_BUILDING_Txt;
    @FindBy(xpath = "(//div[@class='card-row']//span[@class='capitalize-none'])[3]/following-sibling::span")
    WebElement GOAPP_BUILDING_Txt;
    @FindBy(xpath = "//div[@class='card-row']/div[last()]/div/span")
    WebElement SALE_CHANNELS_LBL;
    @FindBy(xpath = "(//div[@class='statistic-title'])[1]")
    WebElement TO_CONFIRM_ORDERS_TXT;
    @FindBy(xpath = "(//div[@class='statistic-title'])[2]")
    WebElement DELIVERED_ORDERS_TXT;
    @FindBy(xpath = "(//div[@class='statistic-title'])[3]")
    WebElement TO_CONFIRM_RESERVATIONS_TXT;
    @FindBy(xpath = "(//div[@class='statistic-title'])[4]")
    WebElement COMPLETED_RESERVATIONS_TXT;
    @FindBy(xpath = "//h3[@class='title']")
    WebElement WHAT_TO_DO_NEXT_TITLE;
    @FindBy(xpath = "//span[@class='subTitle']")
    WebElement WHAT_TO_DO_NEXT_DESCRIPTION;
    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[1]")
    WebElement ADD_OR_IMPORT_PRODUCTS_TITLE;
    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[2]")
    WebElement CUSTOMIZE_APPEARANCE_TITLE;
    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[3]")
    WebElement ADD_YOUR_DOMAIN_TITLE;
    @FindBy(xpath = "(//div[contains(@class,'uik-widget-title__wrapper')]//h3)[4]")
    WebElement ADD_BANK_ACCOUNT_TITLE;
    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[1]")
    WebElement ADD_OR_IMPORT_PRODUCT_DESCRIPTION;
    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[2]")
    WebElement CUSTOMIZE_APPEARANCE_DESCRIPTION;
    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[3]")
    WebElement ADD_YOUR_DOMAIN_DESCRIPTION;
    @FindBy(xpath = "(//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='descriptions'])[4]")
    WebElement ADD_BANK_ACCOUNT_DESCRIPTION;
    @FindBy(xpath = "//div[contains(@class,'shortcut-card')]//div[contains(@class,'content')]//span[@class='step-hint']")
    WebElement ADD_OR_IMPORT_PRODUCT_HINT_TXT;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[1]")
    WebElement CREATE_PRODUCT_BTN;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[2]")
    WebElement IMPORT_FROM_SHOPEE_BTN;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[1]//button[3]")
    WebElement IMPORT_FROM_LAZADA_BTN;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[2]//button")
    WebElement CHANGE_DESIGN_BTN;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[3]//button")
    WebElement ADD_DOMAIN_BTN;
    @FindBy(xpath = "(//div[contains(@class,'card-btn-group')])[4]//button")
    WebElement BANK_INFORMATION_BTN;

}
