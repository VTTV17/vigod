package web.Dashboard.promotion.flashsale;

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
}
