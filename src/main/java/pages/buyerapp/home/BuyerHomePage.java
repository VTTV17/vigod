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
        commonMobile.waitElementVisible(HEADER_STORE_LOGO);
        return this;
    }

    String keywords;

    public BuyerHomePage searchProductByName(ProductInfo productInfo, String language) {
        // click Search icon
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(ExpectedConditions.elementToBeClickable(HEADER_SEARCH_ICON)).click();
        logger.info("Open search screen");

        keywords = productInfo.getDefaultProductNameMap().get(language);

        // input search keywords
        commonMobile.sendKeys(HEADER_SEARCH_BOX, "%s\n".formatted(keywords));
        logger.info("Search with keywords: %s".formatted(keywords));

        return this;
    }

    WebElement getProductElement() {
        // wait list product visible
        List<WebElement> resultList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULT));
        return resultList.stream().filter(element -> StringUtils.capitalize(keywords).equals(element.getText())).findFirst().orElse(null);
    }

    public void navigateToProductDetailPage() {
        // wait list product visible
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(SEARCH_RESULT));

        // check View More button
        boolean hasViewMore = driver.findElements(VIEW_MORE).size() > 0;
        System.out.println("keywords: " + keywords);

        WebElement element = getProductElement();

        if (element == null && hasViewMore) {
            // show all results
            driver.findElement(VIEW_MORE).click();

            // wait list product visible
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(SEARCH_RESULT));

            // getListElementId product element
            while (element == null) {
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(SEARCH_RESULT));
                new UICommonMobile(driver).swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
                element = getProductElement();
            }
        }
        assert element != null;
        element.click();
    }
}
