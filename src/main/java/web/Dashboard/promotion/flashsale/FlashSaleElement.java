package web.Dashboard.promotion.flashsale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FlashSaleElement {
    public WebDriver driver;

    public FlashSaleElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".gs-flash-sale-intro .gs-button__green")
    WebElement EXPLORE_NOW_BTN;

    @FindBy(css = ".flash-sale-campaign-management .gs-button__green")
    WebElement MANAGE_FLASH_SALE_TIME_BTN;

    @FindBy(css = ".gs-button__green--outline")
    public static WebElement CREATE_CAMPAIGN_BTN;
    By loc_lst_lblFlashSaleCampaignName = By.xpath("//tr/td[1]/span");
    By loc_ddlCampaignStatus = By.xpath("//div[contains(@class,'flash-sale-campaign-management')]//button[contains(@class,'uik-select__valueRendered')]");
    By loc_lst_ddvStatus = By.cssSelector(".uik-select__label");
    By loc_btnManageFlashSaleTime = By.cssSelector(".flash-sale-campaign-management .gs-button__green");
}
