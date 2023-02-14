package pages.dashboard.products.all_products.conversion_unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
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

        // clear old conversion unit config
        if ((boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", ADD_CONVERSION_UNIT_CHECKBOX))
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_CONVERSION_UNIT_CHECKBOX);

        // if 'Add Conversion Unit' checkbox is not checked, check and click on 'Configure' button
        if (!(boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", ADD_CONVERSION_UNIT_CHECKBOX))
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_CONVERSION_UNIT_CHECKBOX);

        // check [UI] after check on Add Conversion Unit checkbox
        checkConversionUnitConfig();

        if (uiIsIMEIProduct)
            logger.info("Not support conversion unit for product managed by IMEI/Serial at this time.");
        else {
            // click Configure button
            wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_BTN)).click();

            // wait wholesale product page loaded
            commonAction.verifyPageLoaded("Quay lại chi tiết sản phẩm", "Go back to product detail");

            // hide Facebook bubble
            commonAction.hideElement(driver.findElement(By.cssSelector("#fb-root")));

            // check [UI] header
            checkUIHeader();
        }

        return this;
    }

    /* Without variation config */
    public void addConversionUnitWithoutVariation() throws Exception {
        if (!uiIsIMEIProduct) {
            // click Select Unit button
            wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SELECT_UNIT_BTN)).click();

            // check [UI] config table
            checkWithoutVariationConfigTable();

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
    public void addConversionUnitVariation() throws Exception {
        if (!uiIsIMEIProduct) {
            // number of conversion unit
            int numberOfConversionUnit = nextInt(uiVariationList.size()) + 1;

            // select variation
            for (int i = 0; i < numberOfConversionUnit; i++) {
                // open Select Variation popup
                wait.until(ExpectedConditions.elementToBeClickable(VARIATION_HEADER_SELECT_VARIATION_BTN)).click();

                // wait Select Variation popup visible
                wait.until(visibilityOf(SELECT_VARIATION_POPUP));

                // check [UI] select variation popup
                checkSelectVariationPopup();

                // select variation
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.refreshListElement(VARIATION_SELECT_VARIATION_POPUP_LIST_VARIATION_CHECKBOX).get(i));

                // wait variation is selected
                commonAction.sleepInMiliSecond(100);

                // close Add variation popup
                wait.until(ExpectedConditions.elementToBeClickable(VARIATION_SELECT_VARIATION_POPUP_SAVE_BTN)).click();

                // check [UI] variation config table
                checkVariationConfigTable(i);

                // add conversion unit configuration for variation
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_CONFIGURE_BTN.get(i));

                // wait variation conversion unit page loaded
                commonAction.verifyPageLoaded("Quay lại cài đặt đơn vị quy đổi", "Go back to Set up conversion unit");

                // check [UI] variation config page
                checkVariationConfigPageHeader();

                // click Select Unit button
                wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_FOR_EACH_VARIATION_HEADER_SELECT_UNIT_BTN)).click();

                // check [UI] config and alias table
                checkVariationConfigPageConfigAndAliasTable();

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
            String dbConversionUnitForIMEI = wait.until(visibilityOf(UI_CONVERSION_UNIT_FOR_IMEI_NOTICE)).getText();
            String ppConversionUnitForIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitForIMEI", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitForIMEI, ppConversionUnitForIMEI, "[Failed][Body]Conversion unit for product manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppConversionUnitForIMEI, dbConversionUnitForIMEI));
            logger.info("[UI][%s] Check Body - Conversion unit for product manage inventory by IMEI/Serial number.".formatted(language));
        } else {
            // check conversion unit information
            String dbConversionUnitInformation = wait.until(visibilityOf(UI_CONVERSION_UNIT_INFORMATION)).getText();
            String ppConversionUnitInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitInformation", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitInformation, ppConversionUnitInformation, "[Failed][Body] Conversion unit information should be %s, but found %s.".formatted(ppConversionUnitInformation, dbConversionUnitInformation));
            logger.info("[UI][%s] Check Body - Conversion unit information.".formatted(language));

            // check wholesale product configure button
            String dbConversionUnitConfigBtn = wait.until(visibilityOf(UI_CONVERSION_UNIT_CONFIGURE_BTN)).getText();
            String ppConversionUnitConfigBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitConfigureBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitConfigBtn, ppConversionUnitConfigBtn, "[Failed][Body] Conversion unit configure button should be %s, but found %s.".formatted(ppConversionUnitConfigBtn, dbConversionUnitConfigBtn));
            logger.info("[UI][%s] Check Body - Conversion unit configure button.".formatted(language));
        }
    }

    void checkUIHeader() throws Exception {
        // check go back to product detail link text
        String dbGoBackToProductDetailPage = wait.until(visibilityOf(UI_HEADER_GO_BACK_TO_PRODUCT_DETAIL)).getText();
        String ppGoBackToProductDetailPage = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.goBackToProductDetailPage", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToProductDetailPage, ppGoBackToProductDetailPage, "[Failed][Header] Go back to product detail page link text should be %s, but found %s.".formatted(ppGoBackToProductDetailPage, dbGoBackToProductDetailPage));
        logger.info("[UI][%s] Check Header - Go back to product detail page.".formatted(language));

        // check flex-UI for variation/without variation
        if (uiIsVariation) {
            // check page title
            String dbPageTitle = wait.until(visibilityOf(UI_HEADER_VARIATION_PAGE_TITLE)).getText();
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.pageTitle", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check select variation button
            String dbSelectVariationBtn = wait.until(visibilityOf(UI_HEADER_VARIATION_SELECT_VARIATION_BTN)).getText();
            String ppSelectVariationBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.variation.selectVariationBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectVariationBtn, ppSelectVariationBtn, "[Failed][Header] Select variation button should be %s, but found %s.".formatted(ppSelectVariationBtn, dbSelectVariationBtn));
            logger.info("[UI][%s] Check Header - Select variation button.".formatted(language));
        } else {
            // check page title
            String dbPageTitle = wait.until(visibilityOf(UI_HEADER_WITHOUT_VARIATION_PAGE_TITLE)).getText();
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.pageTitle", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check select unit button
            String dbSelectUnitBtn = wait.until(visibilityOf(UI_HEADER_WITHOUT_VARIATION_SELECT_UNIT_BTN)).getText();
            String ppSelectUnitBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.withoutVariation.selectUnitBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectUnitBtn, ppSelectUnitBtn, "[Failed][Header] Select unit button should be %s, but found %s.".formatted(ppSelectUnitBtn, dbSelectUnitBtn));
            logger.info("[UI][%s] Check Header - Select unit button.".formatted(language));

            // check cancel button
            String dbCancelBtn = wait.until(visibilityOf(UI_HEADER_WITHOUT_VARIATION_CANCEL_BTN)).getText();
            String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.withoutVariation.cancelBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
            logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));
        }

        // check save button
        String dbSaveBtn = wait.until(visibilityOf(UI_HEADER_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check UI when no config
        String dbNoConfig = wait.until(visibilityOf(UI_NO_CONFIG)).getText();
        String ppNoConfig = getPropertiesValueByDBLang("products.allProducts.conversionUnit.noConfig", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNoConfig, ppNoConfig, "[Failed][Body] UI when no config should be %s, but found %s.".formatted(ppNoConfig, dbNoConfig));
        logger.info("[UI][%s] Check UI when no config.".formatted(language));
    }

    void checkWithoutVariationConfigTable() throws Exception {
        // check config table
        List<String> dbConfigTableColumn = UI_WITHOUT_VARIATION_CONFIG_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppConfigTableColumn = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.column.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigTableColumn, ppConfigTableColumn, "[Failed][Config table] List column should be %s, but found %s.".formatted(ppConfigTableColumn, dbConfigTableColumn));
        logger.info("[UI][%s] Check Config table - List column.".formatted(language));

        // check unit text box placeholder
        String dbInputUnitPlaceholder = wait.until(visibilityOf(UI_WITHOUT_VARIATION_CONFIG_TABLE_INPUT_UNIT_PLACEHOLDER)).getAttribute("placeholder");
        String ppInputUnitPlaceholder = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.inputUnitPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInputUnitPlaceholder, ppInputUnitPlaceholder, "[Failed][Config table] Input unit placeholder should be %s, but found %s.".formatted(ppInputUnitPlaceholder, dbInputUnitPlaceholder));
        logger.info("[UI][%s] Check Config table - Input unit placeholder.".formatted(language));

        // input new unit
        WITHOUT_VARIATION_UNIT.sendKeys(String.valueOf(Instant.now().toEpochMilli()));
        WITHOUT_VARIATION_UNIT.click();
        commonAction.waitForElementInvisible(SEARCH_LOADING, 30);

        // check search unit no result
        String dbSearchUnitNoResult = wait.until(visibilityOf(UI_WITHOUT_VARIATION_CONFIG_TABLE_SEARCH_UNIT_NO_RESULT)).getText();
        String ppSearchUnitNoResult = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.searchUnitNoResult", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSearchUnitNoResult, ppSearchUnitNoResult, "[Failed][Config table] Search unit no result should be %s, but found %s.".formatted(ppSearchUnitNoResult, dbSearchUnitNoResult));
        logger.info("[UI][%s] Check Config table - Search unit no result.".formatted(language));

        // check Add button
        String dbAddBtn = wait.until(visibilityOf(UI_WITHOUT_VARIATION_CONFIG_TABLE_ADD_BTN)).getText();
        String ppAddBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.addBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddBtn, ppAddBtn, "[Failed][Config table] Add button should be %s, but found %s.".formatted(ppAddBtn, dbAddBtn));
        logger.info("[UI][%s] Check Config table - Add button.".formatted(language));
        // clear unit text box
        WITHOUT_VARIATION_UNIT.sendKeys(Keys.CONTROL + "a", Keys.DELETE);

        // check alias table
        List<String> dbAliasTableColumn = UI_WITHOUT_VARIATION_CONFIG_ALIAS_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppAliasTableColumn = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.1", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.2", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.3", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.4", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.5", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.6", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.7", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.8", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.9", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.aliasTable.withoutVariation.column.10", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAliasTableColumn, ppAliasTableColumn, "[Failed][Alias table] List column should be %s, but found %s.".formatted(ppAliasTableColumn, dbAliasTableColumn));
        logger.info("[UI][%s] Check Alias table - List column.".formatted(language));
    }

    void checkSelectVariationPopup() throws Exception {
        // check title
        String dbTitle = wait.until(visibilityOf(UI_SELECT_VARIATION_POPUP_TITLE)).getText();
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Select variation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Select variation popup - Title.".formatted(language));

        // check OK button
        String dbOKBtn = wait.until(visibilityOf(UI_SELECT_VARIATION_POPUP_OK_BTN)).getText();
        String ppOKBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.okBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbOKBtn, ppOKBtn, "[Failed][Select variation popup] OK button should be %s, but found %s.".formatted(ppOKBtn, dbOKBtn));
        logger.info("[UI][%s] Check Select variation popup - OK button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = wait.until(visibilityOf(UI_SELECT_VARIATION_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Select variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Select variation popup - Cancel button.".formatted(language));
    }

    void checkVariationConfigTable(int index) throws Exception {
        // check delete button
        String dbDeleteBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_TABLE_DELETE_BTN.get(index))).getText();
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.deleteBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDeleteBtn, ppDeleteBtn, "[Failed][Config table] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Config table - Delete button.".formatted(language));

        // check edit button
        String dbEditBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_TABLE_EDIT_BTN.get(index))).getText();
        String ppEditBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.editBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbEditBtn, ppEditBtn, "[Failed][Config table] Edit button should be %s, but found %s.".formatted(ppEditBtn, dbEditBtn));
        logger.info("[UI][%s] Check Config table - Edit button.".formatted(language));

        // check Configure button
        String dbConfigureBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_TABLE_CONFIGURE_BTN.get(index))).getText();
        String ppConfigureBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.configureBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigureBtn, ppConfigureBtn, "[Failed][Config table] Configure button should be %s, but found %s.".formatted(ppConfigureBtn, dbConfigureBtn));
        logger.info("[UI][%s] Check Config table - Configure button.".formatted(language));
    }

    void checkVariationConfigPageHeader() throws Exception {
        // check header - Go back to setup conversion unit page link text
        String dbGoBackToSetupConversionUnitPage = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_GO_BACK_TO_SETUP_CONVERSION_UNIT)).getText();
        String ppGoBackToSetupConversionUnitPage = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.goBackToSetupConversionUnit", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToSetupConversionUnitPage, ppGoBackToSetupConversionUnitPage, "[Failed][Variation config page][Header] Go back to setup conversion unit page link text should be %s, but found %s.".formatted(ppGoBackToSetupConversionUnitPage, dbGoBackToSetupConversionUnitPage));
        logger.info("[UI][%s][Variation config page] Check Header - Go back to setup conversion unit page link text.".formatted(language));

        // check header - page title
        String dbPageTitle = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_TITLE)).getText();
        String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.pageTitle", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbPageTitle.contains(ppPageTitle), "[Failed][Variation config page][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
        logger.info("[UI][%s][Variation config page] Check Header - Page title.".formatted(language));

        // check header - select unit button
        String dbSelectUnitBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_HEADER_SELECT_UNIT_BTN)).getText();
        String ppSelectUnitBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.selectUnitBtn", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbSelectUnitBtn.equalsIgnoreCase(ppSelectUnitBtn), "[Failed][Variation config page][Header] Select Unit button should be %s, but found %s.".formatted(ppSelectUnitBtn, dbSelectUnitBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Select Unit button.".formatted(language));

        // check header - cancel button
        String dbCancelBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_HEADER_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Variation config page][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Cancel button.".formatted(language));

        // check header - save button
        String dbSaveBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_HEADER_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Variation config page][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Save button.".formatted(language));
    }

    void checkVariationConfigPageConfigAndAliasTable() throws Exception {
        // check config table
        List<String> dbConfigTableColumn = UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppConfigTableColumn = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.column.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigTableColumn, ppConfigTableColumn, "[Failed][Variation config page][Config table] Config table column should be %s, but found %s.".formatted(ppConfigTableColumn, dbConfigTableColumn));
        logger.info("[UI][%s][Variation config page] Check Config table - Config table column.".formatted(language));

        // check unit text box placeholder
        String dbInputUnitPlaceholder = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_INPUT_UNIT_PLACEHOLDER)).getAttribute("placeholder");
        String ppInputUnitPlaceholder = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.inputUnitPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInputUnitPlaceholder, ppInputUnitPlaceholder, "[Failed][Variation config page][Config table] Input unit placeholder should be %s, but found %s.".formatted(ppInputUnitPlaceholder, dbInputUnitPlaceholder));
        logger.info("[UI][%s][Variation config page] Check Config table - Input unit placeholder.".formatted(language));


        // input new unit
        CONFIGURE_FOR_EACH_VARIATION_UNIT.sendKeys(String.valueOf(Instant.now().toEpochMilli()));
        CONFIGURE_FOR_EACH_VARIATION_UNIT.click();
        // check search unit no result
        String dbSearchUnitNoResult = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_SEARCH_UNIT_NO_RESULT)).getText();
        String ppSearchUnitNoResult = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.searchUnitNoResult", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSearchUnitNoResult, ppSearchUnitNoResult, "[Failed][Variation config page][Config table] Search unit no result should be %s, but found %s.".formatted(ppSearchUnitNoResult, dbSearchUnitNoResult));
        logger.info("[UI][%s][Variation config page] Check Config table - Search unit no result.".formatted(language));

        // check Add button
        String dbAddBtn = wait.until(visibilityOf(UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_TABLE_ADD_BTN)).getText();
        String ppAddBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.addBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddBtn, ppAddBtn, "[Failed][Variation config page][Config table] Add button should be %s, but found %s.".formatted(ppAddBtn, dbAddBtn));
        logger.info("[UI][%s][Variation config page] Check Config table - Add button.".formatted(language));
        // clear unit text box
        CONFIGURE_FOR_EACH_VARIATION_UNIT.sendKeys(Keys.CONTROL + "a", Keys.DELETE);

        // check alias table
        List<String> dbAliasTable = UI_VARIATION_CONFIG_PAGE_CONVERSION_UNIT_ALIAS_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppAliasTable = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.1", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.2", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.3", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.4", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.5", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.6", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.7", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.8", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.9", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.aliasTable.column.10", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAliasTable, ppAliasTable, "[Failed][Variation config page][Alias table] List column should be %s, but found %s.".formatted(ppAliasTable, dbAliasTable));
        logger.info("[UI][%s][Variation config page] Check Alias table - List column.".formatted(language));
    }
}
