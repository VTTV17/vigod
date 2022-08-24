package pages.dashboard.products.all_products.conversion_unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ConversionUnitPage extends ConversionUnitElement {
    WebDriverWait wait;
    Actions actions;
    String pageLoadedTextVIE = "Thiết lập đơn vị quy đổi";
    String pageLoadedTextENG = "Set up conversion unit";

    public ConversionUnitPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
    }

    Logger logger = LogManager.getLogger(ConversionUnitPage.class);

    public ConversionUnitPage selectVariations() {
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
        return this;
    }

    public void selectConversionUnit(Map<String, Integer> conversionMap) throws InterruptedException {
        for (String conversionName : conversionMap.keySet()) {
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_UNIT_BTN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SELECT_UNIT_BTN);
            logger.info("Click on the Select Unit button");
            waitElementList(CONVERSION_UNIT_NAME);
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_NAME.get(0)));
            actions.moveToElement(CONVERSION_UNIT_NAME.get(0)).click().build().perform();
            CONVERSION_UNIT_NAME.get(0).sendKeys(conversionName);
            sleep(1000);
            if (MATCH_CONVERSION_RESULT.size() > 0) {
                for (WebElement element : MATCH_CONVERSION_RESULT) {
                    if (element.getText().contains(conversionName)) {
                        sleep(500);
                        element.click();
                        break;
                    }
                }
            } else {
                wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_BTN)).click();
            }
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_QUANTITY.get(0))).clear();
            CONVERSION_UNIT_QUANTITY.get(0).sendKeys(Integer.toString(conversionMap.get(conversionName)));
        }

        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }

    public void configureConversionUnitForAllVariations(Map<String, Integer> conversionMap) throws InterruptedException {
        waitElementList(CONFIGURE_BY_VARIATION_BTN);
        int max = CONFIGURE_BY_VARIATION_BTN.size();
        for (int i = 0; i < max; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_BY_VARIATION_BTN.get(i))).click();
            selectConversionUnit(conversionMap);
            wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE));
            waitElementList(CONFIGURE_BY_VARIATION_BTN);
        }
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    public ConversionUnitPage verifyPageLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return driver.getPageSource().contains(pageLoadedTextVIE) || driver.getPageSource().contains(pageLoadedTextENG);
        });
        return this;
    }
}
