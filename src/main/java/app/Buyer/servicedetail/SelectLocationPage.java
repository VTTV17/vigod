package app.Buyer.servicedetail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class SelectLocationPage extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(SelectLocationPage.class);
    WebDriver driver;
    WebDriverWait wait;
    BuyerServiceDetailElement serviceDetailEl;
    public SelectLocationPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        serviceDetailEl = new BuyerServiceDetailElement(driver);
    }
    By PAGE_TITLE = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_menu_collection_product_action_bar_basic_title')]");

    public SelectLocationPage verifyPageTitle(String expected){
        Assert.assertEquals(getText(PAGE_TITLE).toLowerCase(),expected.toLowerCase());
        logger.info("Verify select location page title.");
        return this;
    }
}
