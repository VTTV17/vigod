package pages.dashboard.products.all_products.conversion_unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ConversionUnitPage extends ConversionUnitElement {
    WebDriverWait wait;

    public ConversionUnitPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    Logger logger = LogManager.getLogger(ConversionUnitPage.class);

    public void selectVariations() {
        int id = 0;
        wait.until(ExpectedConditions.elementToBeClickable(SELECT_VARIATION_BTN)).click();
        int max = VARIATION_LIST_IN_SELECT_VARIATION_POPUP.size();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", VARIATION_LIST_IN_SELECT_VARIATION_POPUP.get(id));
        wait.until(ExpectedConditions.elementToBeClickable(OK_BTN_IN_SELECT_VARIATION_POPUP)).click();
        id++;

        while (id < max) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_VARIATION_BTN)).click();
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", VARIATION_LIST_IN_SELECT_VARIATION_POPUP.get(id));
            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN_IN_SELECT_VARIATION_POPUP)).click();
            id++;
        }
    }

    public void selectConversionUnit(Map<String, Integer> conversionMap) throws InterruptedException {
        for (String conversionName:conversionMap.keySet()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_UNIT_BTN)).click();
            logger.info("Click on the Select Unit button");
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_NAME.get(0))).click();
            CONVERSION_UNIT_NAME.get(0).sendKeys(conversionName);
            sleep(1000);
            if (MATCH_CONVERSION_RESULT.size() > 0) {
               MATCH_CONVERSION_RESULT.get(0).click();
            } else {
                wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_BTN)).click();
            }
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_QUANTITY.get(0))).clear();
            CONVERSION_UNIT_QUANTITY.get(0).sendKeys(Integer.toString(conversionMap.get(conversionName)));
        }

        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    public void configureConversionUnitForAllVariations(Map<String, Integer> conversionMap) throws InterruptedException {
        for (int i = 0; i < CONFIGURE_BY_VARIATION_BTN.size(); i ++) {
            CONFIGURE_BY_VARIATION_BTN.get(i).click();
            selectConversionUnit(conversionMap);
            wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE));
            wait.until(ExpectedConditions.invisibilityOf(TOAST_MESSAGE));
        }
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }
}
