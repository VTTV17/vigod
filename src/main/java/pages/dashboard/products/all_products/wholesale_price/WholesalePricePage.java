package pages.dashboard.products.all_products.wholesale_price;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class WholesalePricePage extends WholesalePriceElement {
    WebDriverWait wait;

    public WholesalePricePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WholesalePricePage addWholesalePriceForAllVariations(Map<Integer, List<String>> wholesaleMap) throws InterruptedException {
        int id = 0;
        wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
        int max = LIST_VARIATION.size();
        wait.until(ExpectedConditions.elementToBeClickable(LIST_VARIATION.get(id))).click();
        wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
        id++;
        while (id < max) {
            wait.until(ExpectedConditions.elementToBeClickable(ADD_VARIATION_BTN)).click();
            sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", LIST_VARIATION.get(id));
            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            id++;
        }
        for (WebElement element : HAS_VARIATION_LIST_ADD_WHOLESALE_PRICE_BTN) {
            for (int i = 0; i < wholesaleMap.size(); i++) {
                element.click();
            }
        }
        return this;
    }

    public WholesalePricePage addWholesalePriceForNormalProduct(Map<Integer, List<String>> wholesaleMap) {
        for (int i = 0; i < wholesaleMap.size(); i++) {
            wait.until(ExpectedConditions.elementToBeClickable(NO_VARIATION_ADD_WHOLESALE_PRICE_BTN)).click();
        }
        return this;
    }

    public WholesalePricePage configureWholesalePrice(Map<Integer, List<String>> wholesaleMap) {
        int id = -1;
        for (int key : wholesaleMap.keySet()) {
            id++;
            List<String> wholeSaleInfo = wholesaleMap.get(key);
            for (int i = 0; i < TOTAL_WHOLESALE_CONFIG.size(); i++) {
                wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i)))).clear();
                WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i)).sendKeys(wholeSaleInfo.get(0));
                wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i) + 1))).clear();
                WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i) + 1).sendKeys(wholeSaleInfo.get(1));
                wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i) + 2))).clear();
                WHOLESALE_PRICE_CONFIGURE.get(3 * (id + wholesaleMap.size() * i) + 2).sendKeys(wholeSaleInfo.get(2));
            }
        }
        return this;
    }

    public void configureSegment(Map<Integer, List<String>> wholesaleMap) {
        int id = -1;
        for (int key : wholesaleMap.keySet()) {
            id++;
            List<String> wholeSaleInfo = wholesaleMap.get(key);
            for (int i = 0; i < TOTAL_WHOLESALE_CONFIG.size(); i++) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", LIST_SEGMENT_BTN.get(id + wholesaleMap.size() * i));
                for (int segId = 3; segId < wholeSaleInfo.size(); segId++) {
                    System.out.println(wholeSaleInfo.get(segId));
                    for (int segment = 0; segment < LIST_SEGMENT_LABEL.size(); segment++) {
                        if (LIST_SEGMENT_LABEL.get(segment).getText().equals(wholeSaleInfo.get(segId)) && (!LIST_SEGMENT_CHECKBOX.get(segment).isSelected())) {
                            LIST_SEGMENT_LABEL.get(segment).click();
                        }
                    }
                }
            }
        }
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }
}
