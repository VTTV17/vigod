package pages.buyerapp.home;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.header.HeaderSF;
import utilities.UICommonMobile;

import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class BuyerHomePage extends BuyerHomeElement {
    final static Logger logger = LogManager.getLogger(BuyerHomePage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonMobile;

    public BuyerHomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonMobile = new UICommonMobile(driver);
    }

    public BuyerHomePage waitHomepageLoaded() {
        commonMobile.waitElementVisible(HEADER_STORE_LOGO);
        return this;
    }

    public BuyerHomePage searchProductByName(String keywords) {
        // click Search icon
        commonMobile.click(HEADER_SEARCH_ICON);
        logger.info("Open search screen");

        // input search keywords
        commonMobile.sendKeys(HEADER_SEARCH_BOX, keywords);
        logger.info("Search with keywords: %s".formatted(keywords.split("\n")[0]));

        return this;
    }

    public void navigateToProductDetailPage() {
        // wait list product visible
        commonMobile.waitListElementVisible(SEARCH_RESULT);

        // click on the first result
        List<WebElement> resultList =  driver.findElements(SEARCH_RESULT);
        resultList.get(0).click();
    }
    public BuyerHomePage clickOnMenuItemByText(String menuItemByText) {
        String menuItemNewXpath = MENU_ITEM_XPATH.formatted(menuItemByText);
        commonMobile.clickElement(commonMobile.getElementByXpath(menuItemNewXpath));
        logger.info("Click on menu: " + menuItemByText);
        return this;
    }
    public BuyerHomePage clickOnMenuIcon(){
        commonMobile.clickElement(MENU_ICON);
        logger.info("Click on menu icon");
        return this;
    }
}
