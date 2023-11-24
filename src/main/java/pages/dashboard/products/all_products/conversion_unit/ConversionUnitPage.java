package pages.dashboard.products.all_products.conversion_unit;

import api.dashboard.products.ConversionUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.products.all_products.ProductPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;

public class ConversionUnitPage extends ConversionUnitElement {
    WebDriverWait wait;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(ConversionUnitPage.class);
    ProductPage productPage;
    int countFail;
    String language;
    ConversionUnit unit;
    List<String> variationList;

    public ConversionUnitPage(WebDriver driver, LoginInformation loginInformation) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        productPage = new ProductPage(driver, loginInformation);
        language = ProductPage.getLanguage();
        countFail = ProductPage.getCountFail();
        unit = new ConversionUnit(loginInformation);
        variationList = ProductPage.getVariationList();
    }

    public ConversionUnitPage navigateToConversionUnitPage() throws Exception {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, productPage.getUpdateProductPath().formatted(ProductPage.getProductID())));
        logger.info("Navigate to product detail page by URL, productId: %s".formatted(ProductPage.getProductID()));

        // if 'Add Conversion Unit' checkbox is not checked, check and click on 'Configure' button
        if (!commonAction.isCheckedJS(productPage.getAddConversionUnitCheckBox()))
            commonAction.clickJS(productPage.getAddConversionUnitCheckBox());

        // check [UI] after check on Add Conversion Unit checkbox
        checkConversionUnitConfig();

        if (ProductPage.isManageByIMEI())
            logger.info("Not support conversion unit for product managed by IMEI/Serial at this time.");
        else {
            // click Configure button
            commonAction.click(productPage.getConfigureConversionUnitBtn());

            // hide Facebook bubble
            commonAction.removeFbBubble();

            // check [UI] header
            checkUIHeader();
        }
        return this;
    }

    /* Without variation config */
    public void addConversionUnitWithoutVariation() throws Exception {
        if (!ProductPage.isManageByIMEI()) {
            // click Select Unit button
            commonAction.click(withoutVariationSelectUnitBtn);

            // check [UI] config table
            checkWithoutVariationConfigTable();

            // select conversion unit
            commonAction.click(withoutVariationUnitTextBox);

            // get all conversion unit name on store
            List<String> unitNameList = unit.getListConversionUnitName();

            // get conversion name to assign to this product
            String unitName = unitNameList.isEmpty() ? unit.createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));
            commonAction.sendKeys(unitTextBoxOnSetupVariationConversionUnitPage, unitName);
            commonAction.click(By.xpath(unitLocator.formatted(unitName)));

            // input conversion unit quantity
            long quantity = Math.min(Math.max(Collections.max(ProductPage.getProductStockQuantity().get(null)), 1), MAX_PRICE / ProductPage.getProductListingPrice().get(0));
            commonAction.sendKeys(withoutVariationQuantity, String.valueOf(quantity));

            // click Save button
            commonAction.click(withoutVariationSaveBtn);
        }
    }

    /* Variation config */
    public void addConversionUnitVariation() throws Exception {
        if (!ProductPage.isManageByIMEI()) {
            // number of conversion unit
            int numberOfConversionUnit = nextInt(ProductPage.getVariationList().size()) + 1;

            // select variation
            for (int varIndex = 0; varIndex < numberOfConversionUnit; varIndex++) {
                // open Select Variation popup
                commonAction.click(selectVariationBtn);
                logger.info("Open select variation popup.");

                // wait Select Variation popup visible
                commonAction.sleepInMiliSecond(2000);
                commonAction.getElement(selectVariationPopup);

                // get variation
                String variation = variationList.get(varIndex);

                // select variation
                commonAction.clickJS(variationOptions, varIndex);
                commonAction.sleepInMiliSecond(100);

                logger.info("Select variation: %s.".formatted(variation));

                // check [UI] select variation popup
                if (varIndex == 0) checkSelectVariationPopup();

                // close Add variation popup
                commonAction.click(saveBtnOnSelectVariationPopup);

                // wait until select variation popup invisible
                commonAction.invisibilityOfElementLocated(selectVariationPopup);

                // check [UI] variation config table
                if (varIndex == 0) checkVariationConfigTable();

                // add conversion unit configuration for variation
                commonAction.clickJS(variationConfigureBtn, varIndex);

                // check [UI] variation config page
                if (varIndex == 0) checkVariationConfigPageHeader();

                // click Select Unit button
                commonAction.clickJS(selectUnitBtnOnSetupVariationConversionUnitPage);

                // check [UI] config and alias table
                if (varIndex == 0) checkVariationConfigPageConfigAndAliasTable();

                // get all conversion unit name on store
                List<String> unitNameList = unit.getListConversionUnitName();

                // get conversion name to assign to this product
                String unitName = unitNameList.isEmpty() ? unit.createConversionUnitAndGetName() : unitNameList.get(nextInt(unitNameList.size()));

                // select conversion unit
                commonAction.sendKeys(withoutVariationUnitTextBox, unitName);
                commonAction.click(By.xpath(unitLocator.formatted(unitName)));

                // input conversion unit quantity
                long quantity = MAX_PRICE / ProductPage.getProductListingPrice().get(varIndex);
                commonAction.sendKeys(quantityOnSetupVariationConversionUnitPage, String.valueOf(quantity));

                // click Save button on variation config
                commonAction.click(saveBtnOnSetupVariationConversionUnitPage);

                logger.info("[%s] Complete configure conversion unit.".formatted(variation));

                // wait conversion unit page loaded
                commonAction.waitURLShouldBeContains("/conversion-unit/variation/edit/");
                logger.info("[%s] Wait setup conversion unit page loaded.".formatted(variation));

            }

            // click Save button on setup conversion unit page
            commonAction.click(variationSaveBtn);
        }

    }

    /* check UI function */
    void checkConversionUnitConfig() throws Exception {
        // check IMEI product
        if (ProductPage.isManageByIMEI()) {
            // check conversion unit for product manage inventory by IMEI/Serial number
            String dbConversionUnitForIMEI = commonAction.getText(productPage.getNotSupportConversionUnitForProductManagedByIMEI());
            String ppConversionUnitForIMEI = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitForIMEI", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitForIMEI, ppConversionUnitForIMEI, "[Failed][Body]Conversion unit for product manage inventory by IMEI/Serial number should be %s, but found %s.".formatted(ppConversionUnitForIMEI, dbConversionUnitForIMEI));
            logger.info("[UI][%s] Check Body - Conversion unit for product manage inventory by IMEI/Serial number.".formatted(language));
        } else {
            // check conversion unit information
            String dbConversionUnitInformation = commonAction.getText(productPage.getNoConversionUnitConfig());
            String ppConversionUnitInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitInformation", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitInformation, ppConversionUnitInformation, "[Failed][Body] Conversion unit information should be %s, but found %s.".formatted(ppConversionUnitInformation, dbConversionUnitInformation));
            logger.info("[UI][%s] Check Body - Conversion unit information.".formatted(language));

            // check wholesale product configure button
            String dbConversionUnitConfigBtn = commonAction.getText(configureText);
            String ppConversionUnitConfigBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.conversionUnit.conversionUnitConfigureBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbConversionUnitConfigBtn, ppConversionUnitConfigBtn, "[Failed][Body] Conversion unit configure button should be %s, but found %s.".formatted(ppConversionUnitConfigBtn, dbConversionUnitConfigBtn));
            logger.info("[UI][%s] Check Body - Conversion unit configure button.".formatted(language));
        }
    }

    void checkUIHeader() throws Exception {
        // check go back to product detail link text
        String dbGoBackToProductDetailPage = commonAction.getText(goBackToProductDetailLinkText);
        String ppGoBackToProductDetailPage = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.goBackToProductDetailPage", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToProductDetailPage, ppGoBackToProductDetailPage, "[Failed][Header] Go back to product detail page link text should be %s, but found %s.".formatted(ppGoBackToProductDetailPage, dbGoBackToProductDetailPage));
        logger.info("[UI][%s] Check Header - Go back to product detail page.".formatted(language));

        // check flex-UI for variation/without variation
        if (ProductPage.isHasModel()) {
            // check page title
            String dbPageTitle = commonAction.getText(variationPageTitle);
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.pageTitle", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check select variation button
            String dbSelectVariationBtn = commonAction.getText(variationSelectVariationText);
            String ppSelectVariationBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.variation.selectVariationBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectVariationBtn, ppSelectVariationBtn, "[Failed][Header] Select variation button should be %s, but found %s.".formatted(ppSelectVariationBtn, dbSelectVariationBtn));
            logger.info("[UI][%s] Check Header - Select variation button.".formatted(language));
        } else {
            // check page title
            String dbPageTitle = commonAction.getText(withoutVariationPageTitle);
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.pageTitle", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check select unit button
            String dbSelectUnitBtn = commonAction.getText(withoutVariationSelectUnitText);
            String ppSelectUnitBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.withoutVariation.selectUnitBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectUnitBtn, ppSelectUnitBtn, "[Failed][Header] Select unit button should be %s, but found %s.".formatted(ppSelectUnitBtn, dbSelectUnitBtn));
            logger.info("[UI][%s] Check Header - Select unit button.".formatted(language));

            // check cancel button
            String dbCancelBtn = commonAction.getText(withoutVariationCancelText);
            String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.withoutVariation.cancelBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
            logger.info("[UI][%s] Check Header - Cancel button.".formatted(language));
        }

        // check save button
        String dbSaveBtn = commonAction.getText(saveText);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check UI when no config
        String dbNoConfig = commonAction.getText(noConfig);
        String ppNoConfig = getPropertiesValueByDBLang("products.allProducts.conversionUnit.noConfig", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNoConfig, ppNoConfig, "[Failed][Body] UI when no config should be %s, but found %s.".formatted(ppNoConfig, dbNoConfig));
        logger.info("[UI][%s] Check UI when no config.".formatted(language));
    }

    void checkWithoutVariationConfigTable() throws Exception {
        // check config table
        List<String> dbConfigTableColumn = commonAction.getListElement(withoutVariationSetupConversionUnitTable).stream().map(WebElement::getText).toList();
        List<String> ppConfigTableColumn = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.column.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigTableColumn, ppConfigTableColumn, "[Failed][Config table] List column should be %s, but found %s.".formatted(ppConfigTableColumn, dbConfigTableColumn));
        logger.info("[UI][%s] Check Config table - List column.".formatted(language));

        // check unit text box placeholder
        String dbInputUnitPlaceholder = commonAction.getAttribute(withoutVariationUnitPlaceholder, "placeholder");
        String ppInputUnitPlaceholder = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.inputUnitPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInputUnitPlaceholder, ppInputUnitPlaceholder, "[Failed][Config table] Input unit placeholder should be %s, but found %s.".formatted(ppInputUnitPlaceholder, dbInputUnitPlaceholder));
        logger.info("[UI][%s] Check Config table - Input unit placeholder.".formatted(language));

        // input new unit
        commonAction.sendKeys(withoutVariationUnitTextBox, String.valueOf(Instant.now().toEpochMilli()));
        commonAction.click(withoutVariationUnitTextBox);

        // check search unit no result
        String dbSearchUnitNoResult = commonAction.getText(withoutVariationNoResult);
        String ppSearchUnitNoResult = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.searchUnitNoResult", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSearchUnitNoResult, ppSearchUnitNoResult, "[Failed][Config table] Search unit no result should be %s, but found %s.".formatted(ppSearchUnitNoResult, dbSearchUnitNoResult));
        logger.info("[UI][%s] Check Config table - Search unit no result.".formatted(language));

        // check Add button
        String dbAddBtn = commonAction.getText(withoutVariationAddText);
        String ppAddBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.withoutVariation.addBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddBtn, ppAddBtn, "[Failed][Config table] Add button should be %s, but found %s.".formatted(ppAddBtn, dbAddBtn));
        logger.info("[UI][%s] Check Config table - Add button.".formatted(language));

        // clear unit text box
        commonAction.clear(withoutVariationUnitTextBox);

        // check alias table
        List<String> dbAliasTableColumn = commonAction.getListElement(withoutVariationAliasTable).stream().map(WebElement::getText).toList();
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
        String dbTitle = commonAction.getText(titleOfSelectVariationPopup);
        String ppTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbTitle, ppTitle, "[Failed][Select variation popup] Title should be %s, but found %s.".formatted(ppTitle, dbTitle));
        logger.info("[UI][%s] Check Select variation popup - Title.".formatted(language));

        // check OK button
        String dbOKBtn = commonAction.getText(okTextOnSelectVariationPopup);
        String ppOKBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.okBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbOKBtn, ppOKBtn, "[Failed][Select variation popup] OK button should be %s, but found %s.".formatted(ppOKBtn, dbOKBtn));
        logger.info("[UI][%s] Check Select variation popup - OK button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnSelectVariationPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.selectVariationPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Select variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Select variation popup - Cancel button.".formatted(language));
    }

    void checkVariationConfigTable() throws Exception {
        // check delete button
        String dbDeleteBtn = commonAction.getText(deleteTextOnSetupConversionUnitTable, 0);
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.deleteBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDeleteBtn, ppDeleteBtn, "[Failed][Config table] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Config table - Delete button.".formatted(language));

        // check edit button
        String dbEditBtn = commonAction.getText(editTextOnSetupConversionUnitTable, 0);
        String ppEditBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.editBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbEditBtn, ppEditBtn, "[Failed][Config table] Edit button should be %s, but found %s.".formatted(ppEditBtn, dbEditBtn));
        logger.info("[UI][%s] Check Config table - Edit button.".formatted(language));

        // check Configure button
        String dbConfigureBtn = commonAction.getText(configureTextOnSetupConversionUnitTable, 0);
        String ppConfigureBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configTable.variation.configureBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigureBtn, ppConfigureBtn, "[Failed][Config table] Configure button should be %s, but found %s.".formatted(ppConfigureBtn, dbConfigureBtn));
        logger.info("[UI][%s] Check Config table - Configure button.".formatted(language));
    }

    void checkVariationConfigPageHeader() throws Exception {
        // check header - Go back to set up conversion unit page link text
        String dbGoBackToSetupConversionUnitPage = commonAction.getText(goBackToSetupConversionUnitLinkText);
        String ppGoBackToSetupConversionUnitPage = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.goBackToSetupConversionUnit", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToSetupConversionUnitPage, ppGoBackToSetupConversionUnitPage, "[Failed][Variation config page][Header] Go back to setup conversion unit page link text should be %s, but found %s.".formatted(ppGoBackToSetupConversionUnitPage, dbGoBackToSetupConversionUnitPage));
        logger.info("[UI][%s][Variation config page] Check Header - Go back to setup conversion unit page link text.".formatted(language));

        // check header - page title
        String dbPageTitle = commonAction.getText(setupVariationConversionUnitPageTitle);
        String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.pageTitle", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbPageTitle.contains(ppPageTitle), "[Failed][Variation config page][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
        logger.info("[UI][%s][Variation config page] Check Header - Page title.".formatted(language));

        // check header - select unit button
        String dbSelectUnitBtn = commonAction.getText(selectUnitTextOnSetupVariationConversionUnit);
        String ppSelectUnitBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.selectUnitBtn", language);
        countFail = new AssertCustomize(driver).assertTrue(countFail, dbSelectUnitBtn.equalsIgnoreCase(ppSelectUnitBtn), "[Failed][Variation config page][Header] Select Unit button should be %s, but found %s.".formatted(ppSelectUnitBtn, dbSelectUnitBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Select Unit button.".formatted(language));

        // check header - cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnSetupVariationConversionUnit);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][Variation config page][Header] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Cancel button.".formatted(language));

        // check header - save button
        String dbSaveBtn = commonAction.getText(saveTextOnSetupVariationConversionUnit);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Variation config page][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s][Variation config page] Check Header - Save button.".formatted(language));
    }

    void checkVariationConfigPageConfigAndAliasTable() throws Exception {
        // check config table
        List<String> dbConfigTableColumn = commonAction.getListElement(setupVariationConversionUnitTable).stream().map(WebElement::getText).toList();
        List<String> ppConfigTableColumn = List.of(getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.column.0", language),
                getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.column.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigTableColumn, ppConfigTableColumn, "[Failed][Variation config page][Config table] Config table column should be %s, but found %s.".formatted(ppConfigTableColumn, dbConfigTableColumn));
        logger.info("[UI][%s][Variation config page] Check Config table - Config table column.".formatted(language));

        // check unit text box placeholder
        String dbInputUnitPlaceholder = commonAction.getAttribute(unitPlaceholderOnSetupVariationConversionUnitTable, "placeholder");
        String ppInputUnitPlaceholder = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.inputUnitPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbInputUnitPlaceholder, ppInputUnitPlaceholder, "[Failed][Variation config page][Config table] Input unit placeholder should be %s, but found %s.".formatted(ppInputUnitPlaceholder, dbInputUnitPlaceholder));
        logger.info("[UI][%s][Variation config page] Check Config table - Input unit placeholder.".formatted(language));


        // input new unit
        commonAction.sendKeys(unitTextBoxOnSetupVariationConversionUnitPage, String.valueOf(Instant.now().toEpochMilli()));
        commonAction.click(unitTextBoxOnSetupVariationConversionUnitPage);
        // check search unit no result
        String dbSearchUnitNoResult = commonAction.getText(noResultTextOnSetupVariationConversionUnitTable);
        String ppSearchUnitNoResult = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.searchUnitNoResult", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSearchUnitNoResult, ppSearchUnitNoResult, "[Failed][Variation config page][Config table] Search unit no result should be %s, but found %s.".formatted(ppSearchUnitNoResult, dbSearchUnitNoResult));
        logger.info("[UI][%s][Variation config page] Check Config table - Search unit no result.".formatted(language));

        // check Add button
        String dbAddBtn = commonAction.getText(addTextOnSetupVariationConversionUnitTable);
        String ppAddBtn = getPropertiesValueByDBLang("products.allProducts.conversionUnit.configPage.variation.configTable.addBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddBtn, ppAddBtn, "[Failed][Variation config page][Config table] Add button should be %s, but found %s.".formatted(ppAddBtn, dbAddBtn));
        logger.info("[UI][%s][Variation config page] Check Config table - Add button.".formatted(language));

        // clear unit text box
        commonAction.clear(unitTextBoxOnSetupVariationConversionUnitPage);

        // check alias table
        List<String> dbAliasTable = commonAction.getListElement(variationAliasTable).stream().map(WebElement::getText).toList();
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
