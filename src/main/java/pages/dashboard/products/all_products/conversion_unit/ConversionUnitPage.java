package pages.dashboard.products.all_products.conversion_unit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static utilities.character_limit.CharacterLimit.*;
import static utilities.page_loaded_text.PageLoadedText.DB_CONFIGURE_CONVERSION_UNIT_PAGE_LOADED_TEXT_ENG;
import static utilities.page_loaded_text.PageLoadedText.DB_CONFIGURE_CONVERSION_UNIT_PAGE_LOADED_TEXT_VIE;

public class ConversionUnitPage extends ConversionUnitElement {
    WebDriverWait wait;
    Actions actions;
    String pageLoadedTextVIE = "Thiết lập đơn vị quy đổi";
    String pageLoadedTextENG = "Set up conversion unit";

    public Map<String, Integer> conversionMap = new HashMap<>();

    public ConversionUnitPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
    }

    Logger logger = LogManager.getLogger(ConversionUnitPage.class);

    /**
     * Select all variation
     */
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

    /**
     * generate conversion map (Conversion name, Quantity)
     */
    private Map<String, Integer> generateMapStringInteger(int size, int stringLength, int maxInt) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(stringLength) + 1), maxInt);
        }
        return map;
    }

    /**
     * Configure conversion unit
     */
    @SafeVarargs
    public final void selectConversionUnit(Map<String, Integer>... conversionMap) throws InterruptedException {
        // get conversion map
        this.conversionMap = conversionMap.length == 0 ? generateMapStringInteger(RandomUtils.nextInt(MAX_DEPOSIT_QUANTITY), RandomUtils.nextInt(MAX_DEPOSIT_NAME), RandomUtils.nextInt(MAX_STOCK_QUANTITY + 1)) : conversionMap[0];

        // set conversion unit
        for (String conversionName : this.conversionMap.keySet()) {

            // add new conversion unit
            wait.until(ExpectedConditions.elementToBeClickable(SELECT_UNIT_BTN));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", SELECT_UNIT_BTN);
            logger.info("Add new conversion unit");

            // wait and input conversion unit name
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_NAME));
            actions.moveToElement(CONVERSION_UNIT_NAME).click().build().perform();

            // input conversion name
            CONVERSION_UNIT_NAME.sendKeys(conversionName);

            // wait api return search result
            sleep(1000);

            // have conversion unit match with keyword, select the first result
            if (MATCH_CONVERSION_RESULT.size() > 0) {
                for (WebElement element : MATCH_CONVERSION_RESULT) {
                    if (element.getText().contains(conversionName)) {
                        //sleep(500);
                        wait.until(ExpectedConditions.elementToBeClickable(element));
                        logger.info("Conversion: %s".formatted(element.getText()));
                        element.click();
                        break;
                    }
                }
            } else {
                // else add new conversion unit
                wait.until(ExpectedConditions.elementToBeClickable(ADD_CONVERSION_UNIT_BTN)).click();
                logger.info("Conversion %s has been created".formatted(conversionName));
            }

            // input conversion unit quantity
            wait.until(ExpectedConditions.elementToBeClickable(CONVERSION_UNIT_QUANTITY)).clear();
            CONVERSION_UNIT_QUANTITY.sendKeys(Integer.toString(this.conversionMap.get(conversionName)));
        }

        // complete configure conversion unit
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    /**
     * Configure conversion unit for all variations
     */
    @SafeVarargs
    public final void configureConversionUnitForAllVariations(Map<String, Integer>... conversionMap) throws InterruptedException {
        // wait configure visible on each variation
        new UICommonAction(driver).waitElementList(CONFIGURE_BY_VARIATION_BTN);
        int max = CONFIGURE_BY_VARIATION_BTN.size();

        // configure conversion unit for each variation
        for (int i = 0; i < max; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_BY_VARIATION_BTN.get(i))).click();
            selectConversionUnit(conversionMap);
            wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE));
            new UICommonAction(driver).waitElementList(CONFIGURE_BY_VARIATION_BTN);
        }

        // complete configure conversion unit
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
    }

    public ConversionUnitPage verifyPageLoaded() {
        new UICommonAction(driver).verifyPageLoaded(DB_CONFIGURE_CONVERSION_UNIT_PAGE_LOADED_TEXT_VIE, DB_CONFIGURE_CONVERSION_UNIT_PAGE_LOADED_TEXT_ENG);
        return this;
    }
}
