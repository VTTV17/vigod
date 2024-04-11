package app.Buyer.home;

import app.Buyer.collection.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.time.Duration;
import java.util.List;

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
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER_STORE_LOGO));
        return this;
    }

    String keywords;

    public BuyerHomePage searchProductByName(ProductInfo productInfo, String language) {
        // click Search icon
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER_SEARCH_ICON)).click();
        logger.info("Open search screen");

        keywords = productInfo.getMainProductNameMap().get(language.equals("VIE") ? "vi" : "en");

        // input search keywords
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER_SEARCH_BOX)).sendKeys(keywords);
        logger.info("Search with keywords: %s".formatted(keywords));

        return this;
    }

    WebElement getProductElement() {
        // wait list product visible
        List<WebElement> listElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));
        for (int index = 0; index < listElement.size(); index++)
            if (driver.findElements(SEARCH_RESULT).get(index).getText().equalsIgnoreCase(keywords))
                return driver.findElements(SEARCH_RESULT).get(index);
        return null;
    }

    public void navigateToProductDetailPage() {
        // wait list product visible
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));

        // check View More button
        boolean hasViewMore = !driver.findElements(VIEW_MORE).isEmpty();
        if (hasViewMore) driver.findElement(VIEW_MORE).click();

        WebElement productElement;
        String currentPageSource;
        String nextPageSource;
        do {
            // wait list product visible
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));
            // get product element
            productElement = getProductElement();
            if (productElement != null) {
                productElement.click();
                break;
            }
            currentPageSource = driver.getPageSource();
            commonMobile.swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
            nextPageSource = driver.getPageSource();
        } while (!currentPageSource.equals(nextPageSource));
        throw new NoSuchElementException("No element is found!!!");
    }

    public BuyerHomePage clickOnMenuItemByText(String menuItemByText) {
        String menuItemNewXpath = MENU_ITEM_XPATH.formatted(menuItemByText.toUpperCase());
        commonMobile.clickElement(commonMobile.getElementByXpath(menuItemNewXpath));
        logger.info("Click on menu: " + menuItemByText);
        return this;
    }

    public BuyerHomePage clickOnMenuIcon(){
        commonMobile.sleepInMiliSecond(2000);
        commonMobile.clickElement(MENU_ICON);
        logger.info("Click on menu icon");
        return this;
    }
    public Collection goToCollectionByMenuText(String text){
        clickOnMenuIcon();
        if(!commonMobile.isElementDisplay(MENU_ITEMS)){
            clickOnMenuIcon();
        }
        clickOnMenuItemByText(text);
        return new Collection(driver);
    }
    public BuyerHomePage verifyMenuItemNotShow(String item){
        clickOnMenuIcon();
        Assert.assertFalse(commonMobile.isElementDisplayInList(MENU_ITEMS,item));
        logger.info("Verify %s not display in list".formatted(item));
        return this;
    }
}
