package pages.dashboard.products.all_products.wholesale_price;

import api.dashboard.customers.SegmentAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.products.all_products.ProductPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.links.Links.DOMAIN;

public class WholesaleProductPage extends WholesaleProductElement {
    UICommonAction commonAction;
    WebDriver driver;
    WebDriverWait wait;
    Logger logger = LogManager.getLogger(WholesaleProductPage.class);

    private List<Long> wholesaleProductPrice;
    private List<Integer> wholesaleProductStock;
    int productID;
    ProductPage productPage;
    List<String> variationList;
    Map<String, List<Integer>> productStockQuantity;
    String language;
    boolean hasModel;
    AssertCustomize assertCustomize;
    List<Long> productSellingPrice;
    LoginInformation loginInformation;

    public WholesaleProductPage(WebDriver driver, LoginInformation loginInformation) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.loginInformation = loginInformation;
        productPage = new ProductPage(driver, loginInformation);
        commonAction = productPage.getCommonAction();
        productID = ProductPage.getProductID();
        variationList = ProductPage.getVariationList();
        productStockQuantity = ProductPage.getProductStockQuantity();
        language = ProductPage.getLanguage();
        hasModel = ProductPage.isHasModel();
        productSellingPrice = ProductPage.getProductSellingPrice();
        assertCustomize = ProductPage.getAssertCustomize();
    }

    public WholesaleProductPage navigateToWholesaleProductPage() throws Exception {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, productPage.getUpdateProductPath().formatted(productID)));

        // if 'Add Wholesale Pricing' checkbox is not checked, check and click on 'Configure' button
        if (!commonAction.isCheckedJS(productPage.getAddWholesalePricingCheckbox()))
            commonAction.clickJS(productPage.getAddWholesalePricingCheckbox());

        // [UI] check UI after check on Add Wholesale Pricing checkbox
        checkWholesaleProductConfig();

        // click Configure button
        commonAction.click(productPage.getConfigureWholesalePricingBtn());


        // wait wholesale product page loaded
        commonAction.getElement(noConfigText);

        // check [UI] header
        checkUIHeader();

        // hide Facebook bubble
        commonAction.removeFbBubble();

        return this;
    }

    int numOfWholesaleProduct;

    public WholesaleProductPage getWholesaleProductInfo() {
        wholesaleProductPrice = new ArrayList<>(productSellingPrice);
        wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        numOfWholesaleProduct = nextInt(variationList.size()) + 1;
        IntStream.range(0, numOfWholesaleProduct).forEach(varIndex -> {
            wholesaleProductPrice.set(varIndex, nextLong(productSellingPrice.get(varIndex)) + 1);
            wholesaleProductStock.set(varIndex, nextInt(Math.max(Collections.max(productStockQuantity.get(variationList.get(varIndex))), 1)) + 1);
        });
        return this;
    }

    /* Without variation config */
    public void addWholesaleProductWithoutVariation() throws Exception {
        // click add wholesale pricing button
        commonAction.click(withoutVariationAddWholesalePricingBtn);
        logger.info("Open setup wholesale price table.");

        // check [UI] wholesale product page
        checkWithoutVariationConfigTable();

        // wait and input buy from
        commonAction.sendKeys(withoutVariationBuyFrom, String.valueOf(wholesaleProductStock.get(0)));
        logger.info("Input buy from: %s.".formatted(wholesaleProductStock.get(0)));

        // wait and input price per item
        commonAction.sendKeys(withoutVariationWholesalePrice, String.valueOf(wholesaleProductPrice.get(0)));
        logger.info("Input price per item: %s".formatted(String.format("%,d", wholesaleProductPrice.get(0))));

        // open segment dropdown
        commonAction.click(withoutVariationSegmentDropdown);
        logger.info("Open segment dropdown.");

        // check [UI] segment default value and search box placeholder
        checkSegmentInformation();

        // select segment
        List<Integer> listSegmentIdInStore = new SegmentAPI(loginInformation).getListSegmentIdInStore();
        if (listSegmentIdInStore.isEmpty()) {
            // if store do not have any segment, select All customers option
            logger.info("Select segment: %s.".formatted(commonAction.getText(allCustomerTextInDropdown)));
            commonAction.click(allCustomerCheckbox);
        } else {
            // in-case store have some segment, select any segment.
            int segmentId = listSegmentIdInStore.get(nextInt(listSegmentIdInStore.size()));
            logger.info("Select segment: %s.".formatted(commonAction.getText(By.cssSelector(segmentText.formatted(segmentId)))));
            commonAction.click(By.cssSelector(segmentLocator.formatted(segmentId)));
        }

        // close segment dropdown
        commonAction.click(withoutVariationSegmentDropdown);
        logger.info("Close segment dropdown.");

        // complete config wholesale product
        commonAction.click(saveBtn);
    }

    /* Variation config */
    List<String> selectVariation() throws Exception {
        List<String> variationSaleList = new ArrayList<>();
        for (int varIndex = 0; varIndex < numOfWholesaleProduct; varIndex++) {
            // open Add variation popup
            commonAction.click(variationAddVariationBtn);
            logger.info("Open select variation popup on wholesale config page.");

            // wait popup visible
            try {
                commonAction.getElement(addVariationPopup);
                logger.info("Wait select variation popup visible.");
            } catch (TimeoutException ex) {
                logger.info(ex);
                commonAction.click(variationAddVariationBtn);
                logger.info("Open select variation popup on wholesale config page.");
                commonAction.getElement(addVariationPopup);
                logger.info("Wait select variation popup visible again.");
            }

            // check [UI] Add variation popup
            if (varIndex == 0) checkAddVariationPopup();

            // select variation
            commonAction.clickJS(listVariationCheckboxOnAddVariationPopup, varIndex);

            // close Add variation popup
            commonAction.click(okBtnOnAddVariationPopup);

            // add variation to sale list
            variationSaleList.add("%s,".formatted(variationList.get(varIndex).replace(" ", "|")));
        }
        return variationSaleList;
    }

    public void addWholesaleProductVariation() throws Exception {
        // get list variation has wholesale pricing config
        List<String> variationSaleList = selectVariation();

        // add config for each variation
        for (int index = 0; index < variationSaleList.size(); index++) {
            // get variation value
            String value = commonAction.getText(variationValue, index);

            // get variation index
            int varIndex = variationSaleList.indexOf(value);

            // click add wholesale pricing button
            commonAction.clickJS(variationAddWholesalePricingBtn, index);

            // check [UI] after add new config
            if (index == 0) checkVariationConfigTable();

            // wait and input buy from
            commonAction.sendKeys(variationBuyFrom, index, String.valueOf(wholesaleProductStock.get(varIndex)));
            logger.info("[%s] Input buy from: %s.".formatted(value, wholesaleProductStock.get(varIndex)));

            // wait and input price per item
            commonAction.sendKeys(variationWholesalePrice, index, String.valueOf(wholesaleProductPrice.get(varIndex)));
            logger.info("[%s] Input price per item: %s.".formatted(value, String.format("%,d", wholesaleProductPrice.get(varIndex))));

            // open segment dropdown
            commonAction.click(variationSegmentDropdown, index);

            // check [UI] segment default value and search box placeholder
            if (index == 0) checkSegmentInformation();

            // select segment
            List<Integer> listSegmentIdInStore = new SegmentAPI(loginInformation).getListSegmentIdInStore();
            if (listSegmentIdInStore.isEmpty()) {
                // if store do not have any segment, select All customers option
                logger.info("Select segment: %s.".formatted(commonAction.getText(allCustomerTextInDropdown)));
                commonAction.click(allCustomerCheckbox);
            } else {
                // in-case store have some segment, select any segment.
                int segmentId = listSegmentIdInStore.get(nextInt(listSegmentIdInStore.size()));
                logger.info("Select segment: %s.".formatted(commonAction.getText(By.cssSelector(segmentText.formatted(segmentId)))));
                commonAction.click(By.cssSelector(segmentLocator.formatted(segmentId)));
            }

            // close segment dropdown
            commonAction.click(variationSegmentDropdown, index);
        }

        // complete config wholesale product
        commonAction.click(saveBtn);
    }

    /* check UI function */
    void checkWholesaleProductConfig() throws Exception {
        // check wholesale product information
        String dbWholesaleProductInformation = commonAction.getText(productPage.getNoWholesaleProductConfigText());
        String ppWholesaleProductInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.wholesaleProductInformation", language);
        assertCustomize.assertEquals(dbWholesaleProductInformation, ppWholesaleProductInformation, "[Failed][Wholesale config table] Wholesale product information should be %s, but found %s.".formatted(ppWholesaleProductInformation, dbWholesaleProductInformation));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale product information.".formatted(language));

        // check wholesale product configure button
        String dbWholesaleProductConfigBtn = commonAction.getText(productPage.getConfigureText());
        String ppWholesaleProductConfigBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.wholesaleProductConfigureBtn", language);
        assertCustomize.assertEquals(dbWholesaleProductConfigBtn, ppWholesaleProductConfigBtn, "[Failed][Wholesale config table] Wholesale product configure button should be %s, but found %s.".formatted(ppWholesaleProductConfigBtn, dbWholesaleProductConfigBtn));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale product configure button.".formatted(language));
    }

    void checkUIHeader() throws Exception {
        // check go back to product detail link text
        String dbGoBackToProductDetailPage;
        try {
            // get text
            dbGoBackToProductDetailPage = commonAction.getText(goBackToProductDetailLinkText);
        } catch (TimeoutException ex) {
            // log error
            logger.info(ex);

            // check wholesale product page is loaded
            // get text again
            if (driver.getCurrentUrl().contains("wholesale-price"))
                dbGoBackToProductDetailPage = commonAction.getText(goBackToProductDetailLinkText);
            else throw new Exception("Can not navigate to Wholesale product page.");
        }
        String ppGoBackToProductDetailPage = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.goBackToProductDetail", language);
        assertCustomize.assertEquals(dbGoBackToProductDetailPage, ppGoBackToProductDetailPage, "[Failed][Header] Go back to product detail link text should be %s, but found %s.".formatted(ppGoBackToProductDetailPage, dbGoBackToProductDetailPage));
        logger.info("[UI][%s] Check Header - Go back to product detail.".formatted(language));

        if (hasModel) {
            // check page title
            String dbPageTitle = commonAction.getText(pageTitle);
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.pageTitle", language);
            assertCustomize.assertEquals(dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check add variation button
            String dbAddVariationBtn = commonAction.getText(addVariationText);
            String ppAddVariationBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.addVariationBtn", language);
            assertCustomize.assertEquals(dbAddVariationBtn, ppAddVariationBtn, "[Failed][Wholesale config table] Add variation button should be %s, but found %s.".formatted(ppAddVariationBtn, dbAddVariationBtn));
            logger.info("[UI][%s] Check Header - Add variation button.".formatted(language));
        } else {
            // check add wholesale pricing button
            String dbAddWholesalePricingBtn = commonAction.getText(withoutVariationAddWholeSalePricingText);
            while (dbAddWholesalePricingBtn.isEmpty()) {
                dbAddWholesalePricingBtn = commonAction.getText(withoutVariationAddWholeSalePricingText);
            }
            String ppAddWholesalePricingBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.withoutVariation.addWholesalePricingBtn", language);
            assertCustomize.assertEquals(dbAddWholesalePricingBtn, ppAddWholesalePricingBtn, "[Failed][Header] Add wholesale pricing button should be %s, but found %s.".formatted(ppAddWholesalePricingBtn, dbAddWholesalePricingBtn));
            logger.info("[UI][%s] Check Header - Add wholesale pricing button.".formatted(language));
        }

        // check save button
        String dbSaveBtn = commonAction.getText(saveText);
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.saveBtn", language);
        assertCustomize.assertEquals(dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check UI when no wholesale config
        String dbNoConfig = commonAction.getText(noConfigText);
        String ppNoConfig = hasModel ? getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.noConfig.variation", language)
                : getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.noConfig.withoutVariation", language);
        assertCustomize.assertEquals(dbNoConfig, ppNoConfig, "[Failed][Wholesale config table] UI when no wholesale config should be %s, but found %s.".formatted(ppNoConfig, dbNoConfig));
        logger.info("[UI][%s] Check Wholesale config table - UI when no wholesale config.".formatted(language));


    }

    void checkAddVariationPopup() throws Exception {
        // check popup title
        String dbAddVariationPopupTitle = commonAction.getText(titleOfAddVariationPopup);
        String ppAddVariationPopupTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.title", language);
        assertCustomize.assertEquals(dbAddVariationPopupTitle, ppAddVariationPopupTitle, "[Failed][Add variation popup] Title should be %s, but found %s.".formatted(ppAddVariationPopupTitle, dbAddVariationPopupTitle));
        logger.info("[UI][%s] Check Add variation popup - Title.".formatted(language));

        // check select all checkbox
        String dbSelectAllCheckbox = commonAction.getText(selectAllLabelOnAddVariationPopup);
        String ppSelectAllCheckbox = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.selectAllCheckbox", language);
        assertCustomize.assertEquals(dbSelectAllCheckbox, ppSelectAllCheckbox, "[Failed][ Add variation popup] Select all checkbox should be %s, but found %s.".formatted(ppSelectAllCheckbox, dbSelectAllCheckbox));
        logger.info("[UI][%s] Check Add variation popup - Select all checkbox.".formatted(language));

        // check OK button
        String dbOKBtn = commonAction.getText(okTextOnAddVariationPopup);
        String ppOKBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.okBtn", language);
        assertCustomize.assertEquals(dbOKBtn, ppOKBtn, "[Failed][Add variation popup] OK button should be %s, but found %s.".formatted(ppOKBtn, dbOKBtn));
        logger.info("[UI][%s] Check Add variation popup - OK button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = commonAction.getText(cancelTextOnAddVariationPopup);
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.cancelBtn", language);
        assertCustomize.assertEquals(dbCancelBtn, ppCancelBtn, "[Failed][ Add variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Add variation popup - Cancel button.".formatted(language));
    }

    void checkWithoutVariationConfigTable() throws Exception {
        // check page title
        String dbPageTitle = commonAction.getText(pageTitle);
        String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.pageTitle", language);
        assertCustomize.assertEquals(dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
        logger.info("[UI][%s] Check Header - Page title.".formatted(language));

        // check wholesale config table
        List<String> dbWholesaleConfigTable = commonAction.getListElement(withoutVariationSetupWholesalePriceTable).stream().map(WebElement::getText).toList();
        List<String> ppWholesaleConfigTable = List.of(getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.0", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.1", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.2", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.3", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.4", language));
        assertCustomize.assertEquals(dbWholesaleConfigTable, ppWholesaleConfigTable, "[Failed][Wholesale config table] Wholesale config table column should be %s, but found %s.".formatted(ppWholesaleConfigTable, dbWholesaleConfigTable));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale config table.".formatted(language));

        // check customer segment tooltips
        commonAction.click(withoutVariationSetupWholesalePriceTable, 4);
        String dbCustomerSegmentTooltips = commonAction.getText(segmentTooltips);
        String ppCustomerSegmentTooltips = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.segmentTooltips", language);
        assertCustomize.assertEquals(dbCustomerSegmentTooltips, ppCustomerSegmentTooltips, "[Failed][Wholesale config table] Customer segment tooltips should be %s, but found %s.".formatted(ppCustomerSegmentTooltips, dbCustomerSegmentTooltips));
        logger.info("[UI][%s] Check Wholesale config table - Customer segment tooltips.".formatted(language));
    }

    void checkSegmentInformation() throws Exception {
        // check search box placeholder
        String dbSearchBoxPlaceholder = commonAction.getAttribute(segmentSearchPlaceholder, "placeholder");
        String ppSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.segment.searchBoxPlaceholder", language);
        assertCustomize.assertEquals(dbSearchBoxPlaceholder, ppSearchBoxPlaceholder, "[Failed][Wholesale config table] Segment search box placeholder should be %s, but found %s.".formatted(ppSearchBoxPlaceholder, dbSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Wholesale config table - Segment search box placeholder.".formatted(language));

        // check all customer checkbox
        String dbAllCustomers = commonAction.getText(allCustomerTextInDropdown);
        String ppAllCustomers = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.segment.allCustomers", language);
        assertCustomize.assertEquals(dbAllCustomers, ppAllCustomers, "[Failed][Wholesale config table] Segment All customers checkbox should be %s, but found %s.".formatted(ppAllCustomers, dbAllCustomers));
        logger.info("[UI][%s] Check Wholesale config table - Segment All customers checkbox.".formatted(language));
    }

    void checkVariationConfigTable() throws Exception {
        // check Add wholesale pricing button
        String dbAddWholesalePricingBtn = commonAction.getText(variationAddWholesalePricingText, 0);
        String ppAddWholesalePricingBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.addWholesalePricingBtn", language);
        assertCustomize.assertEquals(dbAddWholesalePricingBtn, ppAddWholesalePricingBtn, "[Failed][Wholesale config table] Add wholesale pricing button should be %s, but found %s.".formatted(ppAddWholesalePricingBtn, dbAddWholesalePricingBtn));
        logger.info("[UI][%s] Check Wholesale config table - Add wholesale pricing button.".formatted(language));

        // check Edit button
        String dbEditBtn = commonAction.getText(variationEditText, 0);
        String ppEditBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.editBtn", language);
        assertCustomize.assertEquals(dbEditBtn, ppEditBtn, "[Failed][Wholesale config table] Edit button should be %s, but found %s.".formatted(ppEditBtn, dbEditBtn));
        logger.info("[UI][%s] Check Wholesale config table - Edit button.".formatted(language));

        // check Delete button
        String dbDeleteBtn = commonAction.getText(variationDeleteText, 0);
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.deleteBtn", language);
        assertCustomize.assertEquals(dbDeleteBtn, ppDeleteBtn, "[Failed][Wholesale config table] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Wholesale config table - Delete button.".formatted(language));

        // check variation config table
        List<String> dbConfigTable = commonAction.getListElement(variationSetupWholesalePriceTable).stream().map(WebElement::getText).toList();
        List<String> ppConfigTable = List.of(getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.0", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.1", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.2", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.3", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.4", language));
        assertCustomize.assertEquals(dbConfigTable, ppConfigTable, "[Failed][Wholesale config table] Variation config table column should be %s, but found %s.".formatted(ppConfigTable, dbConfigTable));
        logger.info("[UI][%s] Check Wholesale config table - Variation config table.".formatted(language));

        // check customer segment tooltips
        commonAction.click(variationSetupWholesalePriceTable, 4);
        String dbCustomerSegmentTooltips = commonAction.getText(segmentTooltips);
        String ppCustomerSegmentTooltips = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.segmentTooltips", language);
        assertCustomize.assertEquals(dbCustomerSegmentTooltips, ppCustomerSegmentTooltips, "[Failed][Wholesale config table] Customer segment tooltips should be %s, but found %s.".formatted(ppCustomerSegmentTooltips, dbCustomerSegmentTooltips));
        logger.info("[UI][%s] Check Wholesale config table - Customer segment tooltips.".formatted(language));
    }

}
