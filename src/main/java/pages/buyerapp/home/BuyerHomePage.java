package pages.buyerapp.home;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonMobile;
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

        keywords = productInfo.getDefaultProductNameMap().get(language);

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

            // getListElementId product element
            while (productElement == null) {
                commonMobile.swipeByCoordinatesInPercent(0.5, 0.75, 0.5, 0.5);
                productElement = getProductElement();
            }
        }
        assert productElement != null;
        productElement.click();
    }
}
