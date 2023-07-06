package pages.sellerapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonMobile;

import java.time.Duration;

public class SellerGeneral {
    final static Logger logger = LogManager.getLogger(SellerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerGeneral (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By HEADER_PAGE_TITLE = By.xpath("//*[ends-with(@resource-id,'tvActionBarTitle')]");
    By HEADER_RIGHT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
    By HEADER_LEFT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconLeft')]");
    public String getHeaderTitle(){
        String headerTitle = common.getText(HEADER_PAGE_TITLE);
        logger.info("Get page title: "+headerTitle);
        return headerTitle;
    }
    public SellerGeneral tapHeaderRightIcon(){
        common.clickElement(HEADER_RIGHT_ICON);
        return this;
    }
    public SellerGeneral tapHeaderLeftIcon(){
        common.clickElement(HEADER_LEFT_ICON);
        return this;
    }
}
