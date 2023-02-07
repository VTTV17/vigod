package pages.dashboard.products.all_products.conversion_unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static pages.dashboard.products.all_products.ProductPage.*;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.character_limit.CharacterLimit.MAX_CONVERSION_UNIT_NAME;
import static utilities.links.Links.DOMAIN;

public class ConversionUnitPage extends ConversionUnitElement {
    String PRODUCT_DETAIL_PAGE_PATH = "/product/edit/%s";
    WebDriverWait wait;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(ConversionUnitPage.class);

    public ConversionUnitPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    public ConversionUnitPage navigateToConversionUnitPage() throws Exception {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, PRODUCT_DETAIL_PAGE_PATH.formatted(uiProductID)));

        // wait page loaded
        commonAction.verifyPageLoaded("Thêm đơn vị quy đổi", "Add conversion unit");

        // if 'Add Conversion Unit' checkbox is not checked, check and click on 'Configure' button
        if (!(boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", ADD_CONVERSION_UNIT_CHECKBOX))
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_CONVERSION_UNIT_CHECKBOX);

        // check [UI] after check on Add Conversion Unit checkbox
        checkConversionUnitConfig();

        if (uiIsIMEIProduct) logger.info("Not support conversion unit for product managed by IMEI/Serial at this time.");
        else {
            // click Configure button
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_BTN)).click();

            // wait wholesale product page loaded
            commonAction.verifyPageLoaded("Quay lại chi tiết sản phẩm", "Go back to product detail");

            // hide Facebook bubble
            commonAction.hideElement(driver.findElement(By.cssSelector("#fb-root")));
        }

        return this;
    }

    /* Without variation config */
    public void addConversionUnitWithoutVariation() {
        if (!uiIsIMEIProduct) {
            // click Select Unit button
            wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SELECT_UNIT_BTN)).click();

            // select conversion unit
            wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_UNIT)).click();
            commonAction.sleepInMiliSecond(1000);
            List<WebElement> availableConversionUnit = driver.findElements(WITHOUT_VARIATION_LIST_AVAILABLE_UNIT);
            if (availableConversionUnit.size() > 0) try {
                availableConversionUnit.get(nextInt(availableConversionUnit.size())).click();
            } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
                logger.info(ex);
                availableConversionUnit = driver.findElements(WITHOUT_VARIATION_LIST_AVAILABLE_UNIT);
                availableConversionUnit.get(nextInt(availableConversionUnit.size())).click();
            }
            else {
                WITHOUT_VARIATION_UNIT.sendKeys(randomAlphabetic(MAX_CONVERSION_UNIT_NAME));
                wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_ADD_BTN)).click();
            }

            // input conversion unit quantity
            wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_QUANTITY)).clear();
            WITHOUT_VARIATION_QUANTITY.sendKeys(String.valueOf(Math.max(Collections.max(uiProductStockQuantity.get(null)), 1)));

            // click Save button
            wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SAVE_BTN)).click();
        }
    }

    /* Variation config */
    public void addConversionUnitVariation() {
        if (!uiIsIMEIProduct) {
            // number of conversion unit
            int numberOfConversionUnit = nextInt(uiVariationList.size()) + 1;

            // select variation
            for (int i = 0; i < numberOfConversionUnit; i++) {
                // open Select Variation popup
                wait.until(ExpectedConditions.elementToBeClickable(VARIATION_HEADER_SELECT_VARIATION_BTN)).click();

                // wait Select Variation popup visible
                wait.until(ExpectedConditions.visibilityOf(SELECT_VARIATION_POPUP));

                // select variation
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.refreshListElement(VARIATION_SELECT_VARIATION_POPUP_LIST_VARIATION_CHECKBOX).get(i));

                // wait variation is selected
                commonAction.sleepInMiliSecond(100);

                // close Add variation popup
                wait.until(ExpectedConditions.elementToBeClickable(VARIATION_SELECT_VARIATION_POPUP_SAVE_BTN)).click();

                // add conversion unit configuration for variation
                wait.until(ExpectedConditions.elementToBeClickable(VARIATION_CONFIGURE_BTN.get(i))).click();

                // wait variation conversion unit page loaded
                commonAction.verifyPageLoaded("Quay lại cài đặt đơn vị quy đổi", "Go back to Set up conversion unit");

                // click Select Unit button
                wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_HEADER_SELECT_UNIT_BTN)).click();

                // select conversion unit
                wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_UNIT)).click();
                commonAction.sleepInMiliSecond(1000);
                List<WebElement> availableConversionUnit = driver.findElements(CONFIGURE_FOR_EACH_VARIATION_LIST_AVAILABLE_UNIT);
                if (availableConversionUnit.size() > 0) try {
                    availableConversionUnit.get(nextInt(availableConversionUnit.size())).click();
                } catch (StaleElementReferenceException | ElementNotInteractableException ex) {
                    logger.info(ex);
                    availableConversionUnit = driver.findElements(CONFIGURE_FOR_EACH_VARIATION_LIST_AVAILABLE_UNIT);
                    availableConversionUnit.get(nextInt(availableConversionUnit.size())).click();
                }
                else {
                    CONFIGURE_FOR_EACH_VARIATION_UNIT.sendKeys(randomAlphabetic(MAX_CONVERSION_UNIT_NAME));
                    wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_ADD_BTN)).click();
                }

                // input conversion unit quantity
                wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_QUANTITY)).clear();
                CONFIGURE_FOR_EACH_VARIATION_QUANTITY.sendKeys(String.valueOf(Math.max(Collections.max(uiProductStockQuantity.get(uiVariationList.get(i))), 1)));

                // click Save button
                wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_HEADER_SAVE_BTN)).click();

                // wait wholesale product page loaded
                commonAction.verifyPageLoaded("Quay lại chi tiết sản phẩm", "Go back to product detail");
            }

            // click Save button
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_HEADER_SAVE_BTN)).click();
        }

    }

    /* check UI function */
    void checkConversionUnitConfig() throws Exception {
        // check IMEI product
        if (uiIsIMEIProduct) {
            // check conversion unit for product manage inventory by IMEI/Serial number
            String dbConversionUnitForIMEI = wait.until(ExpectedConditions.visibilityOf(UI_CONVERSION_UNIT_FOR_IMEI_NOTICE)).getText();
            String ppConversionUnitForIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitForIMEI", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitForIMEI, ppConversionUnitForIMEI, "[Failed][Body]Conversion unit for product manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppConversionUnitForIMEI, dbConversionUnitForIMEI));
            logger.info("[UI][%s] Check Body - Conversion unit for product manage inventory by IMEI/Serial number.".formatted(language));
        } else {
            // check conversion unit information
            String dbConversionUnitInformation = wait.until(ExpectedConditions.visibilityOf(UI_CONVERSION_UNIT_INFORMATION)).getText();
            String ppConversionUnitInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitInformation", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitInformation, ppConversionUnitInformation, "[Failed][Body] Conversion unit information should be %s, but found %s.".formatted(ppConversionUnitInformation, dbConversionUnitInformation));
            logger.info("[UI][%s] Check Body - Conversion unit information.".formatted(language));

            // check wholesale product configure button
            String dbConversionUnitConfigBtn = wait.until(ExpectedConditions.visibilityOf(UI_CONVERSION_UNIT_CONFIGURE_BTN)).getText();
            String ppConversionUnitConfigBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitConfigureBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitConfigBtn, ppConversionUnitConfigBtn, "[Failed][Body] Conversion unit configure button should be %s, but found %s.".formatted(ppConversionUnitConfigBtn, dbConversionUnitConfigBtn));
            logger.info("[UI][%s] Check Body - Conversion unit configure button.".formatted(language));
        }
    }

}
