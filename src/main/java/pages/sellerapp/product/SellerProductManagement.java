package pages.sellerapp.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.sellerapp.HomePage;
import utilities.UICommonMobile;

import java.time.Duration;

public class SellerProductManagement {
    final static Logger logger = LogManager.getLogger(SellerProductManagement.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerProductManagement (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By PRODUCT_COLLECTION_ICON = By.xpath("//*[ends-with(@resource-id,'fabProductCollection')]");
    public SellerProductCollection tapOnProductColectionIcon(){
        common.clickElement(PRODUCT_COLLECTION_ICON);
        logger.info("Tap on product collection icon to go to Product Collection page.");
        return new SellerProductCollection(driver);
    }
}
