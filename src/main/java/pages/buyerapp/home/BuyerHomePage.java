package pages.buyerapp.home;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.buyerapp.collection.Collection;
import pages.storefront.header.HeaderSF;
import utilities.UICommonMobile;
import utilities.model.dashboard.products.productInfomation.ProductInfo;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

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
        System.out.println(productInfo);
        // click Search icon
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER_SEARCH_ICON)).click();
        logger.info("Open search screen");

        keywords = productInfo.getDefaultProductNameMap().get(language.equals("VIE") ? "vi" : "en");

        // input search keywords
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER_SEARCH_BOX)).sendKeys(keywords);
        logger.info("Search with keywords: %s".formatted(keywords));

        return this;
    }

    WebElement getProductElement() {
        // wait list product visible
        List<WebElement> listElement = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));
        for (int index = 0; index < listElement.size(); index++)
            if (driver.findElements(SEARCH_RESULT).get(index).getText().equals(StringUtils.capitalize(keywords)))
                return driver.findElements(SEARCH_RESULT).get(index);
        return null;
    }

    public void navigateToProductDetailPage() {
        // wait list product visible
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));

        // check View More button
        boolean hasViewMore = driver.findElements(VIEW_MORE).size() > 0;

        WebElement productElement = getProductElement();

        if (productElement == null && hasViewMore) {
            // show all results
            driver.findElement(VIEW_MORE).click();

            // wait list product visible
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));

            // get product element
            while (productElement == null) {
                commonMobile.swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
                productElement = getProductElement();
            }
        }
        if (productElement == null) throw new NoSuchElementException("No element is found!!!");
        productElement.click();
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
        clickOnMenuItemByText(text);
        return new Collection(driver);
    }
}
