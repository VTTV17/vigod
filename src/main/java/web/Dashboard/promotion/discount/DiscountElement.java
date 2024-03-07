package web.Dashboard.promotion.discount;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DiscountElement {
    public WebDriver driver;

    public DiscountElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = ".discount-header .d-flex > div:not(.second-button-group)")
    WebElement CREATE_PROMOTION_BTN;

    @FindBy (xpath = "(//div[contains(@class,'discount-header')]//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
    WebElement PRODUCT_DISCOUNT_CODE;
    
    @FindBy (xpath = "(//div[contains(@class,'discount-header')]//div[contains(@class,'uik-menuDrop__list')]//button)[2]")
    WebElement SERVICE_DISCOUNT_CODE;

    @FindBy(css = "div.second-button-group > button")
    public WebElement WHOLESALE_PRICING_BTN;

    @FindBy (css = "div.second-button-group > div > button:nth-child(1)")
    public WebElement PRODUCT_WHOLESALE_PRICING;

    @FindBy(xpath = ".search-input")
    WebElement UI_SEARCH_PLACEHOLDER;
    By loc_ddlDiscountType = By.xpath("(//button[contains(@class,'btn-secondary')])[1]");
    By loc_ddlStatus = By.xpath("(//button[contains(@class,'btn-secondary')])[2]");
    /*
    0: All Types
    1: Product Discount Code
    2: Service Discount Code
    3: Product Discount Campaign
    4: Service Discount Campaign
     */
    By loc_ddvDiscountType = By.xpath("(//button[contains(@class,'btn-secondary')])[1]/following-sibling::div//button");
    By loc_lstPromotionName = By.cssSelector(".discount-name");
    By loc_lstPromotionType = By.cssSelector(".type");
    By loc_lstPromotionActiveDate = By.cssSelector(".active-date");
    By loc_lstPromotionStatus = By.cssSelector(".gs-status-tag");
    By loc_lst_icnEdit = By.cssSelector(".icon-edit");
    By loc_ddvDiscountStatus = By.xpath("(//button[contains(@class,'btn-secondary')])[2]/following-sibling::div//button");
    public By loc_lst_icnClone = By.cssSelector("[title='Clone']");
    By loc_lst_icnEnd = By.xpath("//div[contains(@class,'action')]//span[@class='gs-component-tooltip'][1]");
    By loc_lst_lblStatus = By.xpath("//div[contains(@class,'status')]//span");

}
