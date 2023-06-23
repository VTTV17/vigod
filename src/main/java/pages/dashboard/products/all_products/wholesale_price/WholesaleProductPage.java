package pages.dashboard.products.all_products.wholesale_price;

import api.dashboard.customers.Customers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.products.all_products.ProductPage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static utilities.PropertiesUtil.getPropertiesValueByDBLang;
import static utilities.account.AccountTest.BUYER_ACCOUNT_THANG;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;
import static utilities.links.Links.DOMAIN;

public class WholesaleProductPage extends WholesaleProductElement {
    String PRODUCT_DETAIL_PAGE_PATH = "/product/edit/%s";
    UICommonAction commonAction;
    WebDriverWait wait;
    Actions act;
    Logger logger = LogManager.getLogger(WholesaleProductPage.class);

    private List<Long> wholesaleProductPrice;
    private List<Integer> wholesaleProductStock;
    int productID;
    ProductPage productPage;
    List<String> variationList;
    Map<String, List<Integer>> productStockQuantity;
    int countFail;
    String language;
    boolean hasModel;
    List<Long> productSellingPrice;

    public WholesaleProductPage(WebDriver driver) {
        super(driver);
        commonAction = new UICommonAction(driver);
        act = new Actions(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        productPage = new ProductPage(driver);
        productID = productPage.getProductID();
        variationList = productPage.getVariationList();
        productStockQuantity = productPage.getProductStockQuantity();
        countFail = productPage.getCountFail();
        language = productPage.getLanguage();
        hasModel = productPage.isHasModel();
        productSellingPrice = productPage.getProductSellingPrice();
    }
    public WholesaleProductPage navigateToWholesaleProductPage() throws Exception {
        // navigate to product detail page by URL
        driver.get("%s%s".formatted(DOMAIN, PRODUCT_DETAIL_PAGE_PATH.formatted(productID)));

        // wait page loaded
        commonAction.waitElementVisible(ADD_WHOLESALE_PRICING_CHECKBOX);

        // if 'Add Wholesale Pricing' checkbox is not checked, check and click on 'Configure' button
        if (!(boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checked", ADD_WHOLESALE_PRICING_CHECKBOX))
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", ADD_WHOLESALE_PRICING_CHECKBOX);

        // [UI] check UI after check on Add Wholesale Pricing checkbox
        checkWholesaleProductConfig();

        // click Configure button
        wait.until(ExpectedConditions.elementToBeClickable(CONFIGURE_BTN)).click();

        // wait spinner loading hidden
        commonAction.waitForElementInvisible(SPINNER, 60);

        // wait wholesale product page loaded
        commonAction.waitElementVisible(UI_NO_WHOLESALE_CONFIG);

        // check [UI] header
        checkUIHeader();

        // hide Facebook bubble
        ((JavascriptExecutor) driver).executeScript("arguments[0].remove()", FB_BUBBLE);

        return this;
    }

    int numOfWholesaleProduct;

    public WholesaleProductPage getWholesaleProductInfo() {
        wholesaleProductPrice = new ArrayList<>(productSellingPrice);
        wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, wholesaleProductPrice.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        numOfWholesaleProduct = nextInt(variationList.size()) + 1;
        for (int i = 0; i < numOfWholesaleProduct; i++) {
            wholesaleProductPrice.set(i, nextLong(productSellingPrice.get(i)) + 1);
            wholesaleProductStock.set(i, nextInt(Math.max(Collections.max(productStockQuantity.get(variationList.get(i))), 1)) + 1);
        }
        return this;
    }

    /* Without variation config */
    public void addWholesaleProductWithoutVariation() throws Exception {
        // click add wholesale pricing button
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN)).click();

        // check [UI] wholesale product page
        checkWithoutVariationConfigTable();

        // wait and input buy from
        wait.until(visibilityOf(WITHOUT_VARIATION_BUY_FROM)).click();
        WITHOUT_VARIATION_BUY_FROM.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        WITHOUT_VARIATION_BUY_FROM.sendKeys(String.valueOf(wholesaleProductStock.get(0)));

        // wait and input price per item
        wait.until(visibilityOf(WITHOUT_VARIATION_PRICE_PER_ITEM)).click();
        WITHOUT_VARIATION_PRICE_PER_ITEM.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        WITHOUT_VARIATION_PRICE_PER_ITEM.sendKeys(String.valueOf(wholesaleProductPrice.get(0)));

        // open segment dropdown
        wait.until(visibilityOf(WITHOUT_VARIATION_CUSTOMER_SEGMENT_DROPDOWN)).click();

        // wait segment dropdown show
        commonAction.sleepInMiliSecond(1000);

        // check [UI] segment default value and search box placeholder
        checkSegmentInformation();

        // search segment
        if (new Customers().getSegmentName() == null) new Customers().createSegmentByAPI(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84");
        wait.until(visibilityOf(CUSTOMER_SEGMENT_SEARCH_BOX));
        act.moveToElement(CUSTOMER_SEGMENT_SEARCH_BOX).doubleClick().sendKeys("%s\n".formatted(new Customers().getSegmentName()));

        // select segment
        wait.until(visibilityOf(CUSTOMER_SEGMENT_CHECKBOX)).click();

        // close segment dropdown
        wait.until(visibilityOf(WITHOUT_VARIATION_CUSTOMER_SEGMENT_DROPDOWN)).click();

        // complete config wholesale product
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SAVE_BTN)).click();
    }

    /* Variation config */
    List<String> variationSaleList = new ArrayList<>();

    void selectVariation() throws Exception {
        for (int i = 0; i < numOfWholesaleProduct; i++) {
            // open Add variation popup
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_HEADER_ADD_VARIATION_BTN);

            // wait popup visible
            wait.until(visibilityOf(ADD_VARIATION_POPUP));

            // check [UI] Add variation popup
            if (i == 0) checkAddVariationPopup();

            // wait list variation visible
            commonAction.sleepInMiliSecond(500);

            // select variation
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_ADD_VARIATION_POPUP_LIST_VARIATION_CHECKBOX.get(i));

            // close Add variation popup
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_ADD_VARIATION_POPUP_OK_BTN)).click();

            variationSaleList.add("%s,".formatted(variationList.get(i).replace("|", " ")));
        }
    }

    public void addWholesaleProductVariation() throws Exception {
        selectVariation();
        for (int i = 0; i < variationSaleList.size(); i++) {
            // get variation value
            String varValue = commonAction.refreshListElement(VARIATION_HEADER_VARIATION_VALUE).get(i).getText();

            // get variation index
            int varIndex = variationSaleList.indexOf(varValue);

            // wait list add wholesale pricing button visible
            commonAction.waitElementList(VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN, variationSaleList.size());

            // click add wholesale pricing button
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", VARIATION_HEADER_ADD_WHOLESALE_PRICING_BTN.get(i));

            // wait wholesale product page loaded
            commonAction.waitForElementInvisible(SPINNER, 60);

            // check [UI] after add new config
            checkVariationConfigTable(i);

            // wait and input buy from
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_BUY_FROM.get(i)));
            VARIATION_BUY_FROM.get(i).sendKeys(Keys.CONTROL + "a", Keys.DELETE);
            VARIATION_BUY_FROM.get(i).sendKeys(String.valueOf(wholesaleProductStock.get(varIndex)));

            // wait and input price per item
            wait.until(ExpectedConditions.elementToBeClickable(VARIATION_PRICE_PER_ITEM.get(i)));
            VARIATION_PRICE_PER_ITEM.get(i).sendKeys(Keys.CONTROL + "a", Keys.DELETE);
            VARIATION_PRICE_PER_ITEM.get(i).sendKeys(String.valueOf(wholesaleProductPrice.get(varIndex)));

            // open segment dropdown
            wait.until(visibilityOf(commonAction.refreshListElement(VARIATION_CUSTOMER_SEGMENT_DROPDOWN).get(i))).click();

            // wait segment dropdown show
            commonAction.sleepInMiliSecond(1000);

            // check [UI] segment default value and search box placeholder
            checkSegmentInformation();

            // search segment
            commonAction.sleepInMiliSecond(1000);
            wait.until(visibilityOf(CUSTOMER_SEGMENT_SEARCH_BOX));
            act.moveToElement(CUSTOMER_SEGMENT_SEARCH_BOX).doubleClick().sendKeys("%s\n".formatted(new Customers().getSegmentName()));

            // select segment
            wait.until(visibilityOf(CUSTOMER_SEGMENT_CHECKBOX)).click();

            // close segment dropdown
            wait.until(visibilityOf(commonAction.refreshListElement(VARIATION_CUSTOMER_SEGMENT_DROPDOWN).get(i))).click();
        }

        // complete config wholesale product
        wait.until(ExpectedConditions.elementToBeClickable(WITHOUT_VARIATION_HEADER_SAVE_BTN)).click();
    }

    /* check UI function */
    void checkWholesaleProductConfig() throws Exception {
        // check wholesale product information
        String dbWholesaleProductInformation = wait.until(visibilityOf(UI_WHOLESALE_PRODUCT_INFORMATION)).getText();
        String ppWholesaleProductInformation = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.wholesaleProductInformation", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWholesaleProductInformation, ppWholesaleProductInformation, "[Failed][Wholesale config table] Wholesale product information should be %s, but found %s.".formatted(ppWholesaleProductInformation, dbWholesaleProductInformation));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale product information.".formatted(language));

        // check wholesale product configure button
        String dbWholesaleProductConfigBtn = wait.until(visibilityOf(UI_WHOLESALE_PRODUCT_CONFIGURE_BTN)).getText();
        String ppWholesaleProductConfigBtn = getPropertiesValueByDBLang("products.allProducts.createProduct.wholesaleProduct.wholesaleProductConfigureBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWholesaleProductConfigBtn, ppWholesaleProductConfigBtn, "[Failed][Wholesale config table] Wholesale product configure button should be %s, but found %s.".formatted(ppWholesaleProductConfigBtn, dbWholesaleProductConfigBtn));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale product configure button.".formatted(language));
    }

    void checkUIHeader() throws Exception {
        // check go back to product detail link text
        String dbGoBackToProductDetailPage;
        try {
            // get text
            dbGoBackToProductDetailPage = wait.until(visibilityOf(UI_HEADER_GO_BACK_TO_PRODUCT_DETAIL)).getText();
        } catch (TimeoutException ex) {
            // log error
            logger.info(ex);

            // check wholesale product page is loaded
            if (driver.getCurrentUrl().contains("wholesale-price")) {
                // get text again
                dbGoBackToProductDetailPage = wait.until(visibilityOf(UI_HEADER_GO_BACK_TO_PRODUCT_DETAIL)).getText();
            } else throw new Exception("Can not navigate to Wholesale product page.");
        }
        String ppGoBackToProductDetailPage = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.goBackToProductDetail", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbGoBackToProductDetailPage, ppGoBackToProductDetailPage, "[Failed][Header] Go back to product detail link text should be %s, but found %s.".formatted(ppGoBackToProductDetailPage, dbGoBackToProductDetailPage));
        logger.info("[UI][%s] Check Header - Go back to product detail.".formatted(language));

        if (hasModel) {
            // check page title
            String dbPageTitle = wait.until(visibilityOf(UI_HEADER_PAGE_TITLE)).getText();
            String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.pageTitle", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
            logger.info("[UI][%s] Check Header - Page title.".formatted(language));

            // check add variation button
            String dbAddVariationBtn = wait.until(visibilityOf(UI_VARIATION_HEADER_ADD_VARIATION_BTN)).getText();
            String ppAddVariationBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.addVariationBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddVariationBtn, ppAddVariationBtn, "[Failed][Wholesale config table] Add variation button should be %s, but found %s.".formatted(ppAddVariationBtn, dbAddVariationBtn));
            logger.info("[UI][%s] Check Header - Add variation button.".formatted(language));
        } else {
            // check add wholesale pricing button
            String dbAddWholesalePricingBtn = wait.until(visibilityOf(UI_WITHOUT_VARIATION_ADD_WHOLESALE_PRICING_BTN)).getText();
            while (dbAddWholesalePricingBtn.equals("")) {
                dbAddWholesalePricingBtn = UI_WITHOUT_VARIATION_ADD_WHOLESALE_PRICING_BTN.getText();
            }
            String ppAddWholesalePricingBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.withoutVariation.addWholesalePricingBtn", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddWholesalePricingBtn, ppAddWholesalePricingBtn, "[Failed][Header] Add wholesale pricing button should be %s, but found %s.".formatted(ppAddWholesalePricingBtn, dbAddWholesalePricingBtn));
            logger.info("[UI][%s] Check Header - Add wholesale pricing button.".formatted(language));
        }

        // check save button
        String dbSaveBtn = wait.until(visibilityOf(UI_HEADER_SAVE_BTN)).getText();
        String ppSaveBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.saveBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSaveBtn, ppSaveBtn, "[Failed][Header] Save button should be %s, but found %s.".formatted(ppSaveBtn, dbSaveBtn));
        logger.info("[UI][%s] Check Header - Save button.".formatted(language));

        // check UI when no wholesale config
        String dbNoConfig = wait.until(visibilityOf(UI_NO_WHOLESALE_CONFIG)).getText();
        String ppNoConfig = hasModel ? getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.noConfig.variation", language)
                : getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.noConfig.withoutVariation", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbNoConfig, ppNoConfig, "[Failed][Wholesale config table] UI when no wholesale config should be %s, but found %s.".formatted(ppNoConfig, dbNoConfig));
        logger.info("[UI][%s] Check Wholesale config table - UI when no wholesale config.".formatted(language));


    }

    void checkAddVariationPopup() throws Exception {
        // check popup title
        String dbAddVariationPopupTitle = wait.until(visibilityOf(UI_ADD_VARIATION_POPUP_TITLE)).getText();
        String ppAddVariationPopupTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.title", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddVariationPopupTitle, ppAddVariationPopupTitle, "[Failed][Add variation popup] Title should be %s, but found %s.".formatted(ppAddVariationPopupTitle, dbAddVariationPopupTitle));
        logger.info("[UI][%s] Check Add variation popup - Title.".formatted(language));

        // check select all checkbox
        String dbSelectAllCheckbox = wait.until(visibilityOf(UI_ADD_VARIATION_POPUP_SELECT_ALL_CHECKBOX)).getText();
        String ppSelectAllCheckbox = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.selectAllCheckbox", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSelectAllCheckbox, ppSelectAllCheckbox, "[Failed][ Add variation popup] Select all checkbox should be %s, but found %s.".formatted(ppSelectAllCheckbox, dbSelectAllCheckbox));
        logger.info("[UI][%s] Check Add variation popup - Select all checkbox.".formatted(language));

        // check OK button
        String dbOKBtn = wait.until(visibilityOf(UI_ADD_VARIATION_POPUP_OK_BTN)).getText();
        String ppOKBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.okBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbOKBtn, ppOKBtn, "[Failed][Add variation popup] OK button should be %s, but found %s.".formatted(ppOKBtn, dbOKBtn));
        logger.info("[UI][%s] Check Add variation popup - OK button.".formatted(language));

        // check Cancel button
        String dbCancelBtn = wait.until(visibilityOf(UI_ADD_VARIATION_POPUP_CANCEL_BTN)).getText();
        String ppCancelBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.variation.addVariationPopup.cancelBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCancelBtn, ppCancelBtn, "[Failed][ Add variation popup] Cancel button should be %s, but found %s.".formatted(ppCancelBtn, dbCancelBtn));
        logger.info("[UI][%s] Check Add variation popup - Cancel button.".formatted(language));
    }

    void checkWithoutVariationConfigTable() throws Exception {
        // check page title
        String dbPageTitle = wait.until(visibilityOf(UI_HEADER_PAGE_TITLE)).getText();
        String ppPageTitle = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.pageTitle", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbPageTitle, ppPageTitle, "[Failed][Header] Page title should be %s, but found %s.".formatted(ppPageTitle, dbPageTitle));
        logger.info("[UI][%s] Check Header - Page title.".formatted(language));

        // check wholesale config table
        List<String> dbWholesaleConfigTable = UI_WITHOUT_VARIATION_WHOLESALE_CONFIG_TABLE_COLUMN.stream().map(WebElement::getText).toList();
        List<String> ppWholesaleConfigTable = List.of(getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.0", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.1", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.2", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.3", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.4", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbWholesaleConfigTable, ppWholesaleConfigTable, "[Failed][Wholesale config table] Wholesale config table column should be %s, but found %s.".formatted(ppWholesaleConfigTable, dbWholesaleConfigTable));
        logger.info("[UI][%s] Check Wholesale config table - Wholesale config table.".formatted(language));

        // check customer segment tooltips
        act.moveToElement(UI_WITHOUT_VARIATION_WHOLESALE_CONFIG_TABLE_COLUMN.get(4)).build().perform();
        String dbCustomerSegmentTooltips = wait.until(visibilityOf(UI_SEGMENT_TOOLTIPS)).getText();
        String ppCustomerSegmentTooltips = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.segmentTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCustomerSegmentTooltips, ppCustomerSegmentTooltips, "[Failed][Wholesale config table] Customer segment tooltips should be %s, but found %s.".formatted(ppCustomerSegmentTooltips, dbCustomerSegmentTooltips));
        logger.info("[UI][%s] Check Wholesale config table - Customer segment tooltips.".formatted(language));
    }

    void checkSegmentInformation() throws Exception {
        // check search box placeholder
        String dbSearchBoxPlaceholder = wait.until(visibilityOf(UI_SEGMENT_SEARCH_BOX_PLACEHOLDER)).getAttribute("placeholder");
        String ppSearchBoxPlaceholder = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.segment.searchBoxPlaceholder", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbSearchBoxPlaceholder, ppSearchBoxPlaceholder, "[Failed][Wholesale config table] Segment search box placeholder should be %s, but found %s.".formatted(ppSearchBoxPlaceholder, dbSearchBoxPlaceholder));
        logger.info("[UI][%s] Check Wholesale config table - Segment search box placeholder.".formatted(language));

        // check all customer checkbox
        String dbAllCustomers = wait.until(visibilityOf(UI_SEGMENT_ALL_CUSTOMERS)).getText();
        String ppAllCustomers = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.segment.allCustomers", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAllCustomers, ppAllCustomers, "[Failed][Wholesale config table] Segment All customers checkbox should be %s, but found %s.".formatted(ppAllCustomers, dbAllCustomers));
        logger.info("[UI][%s] Check Wholesale config table - Segment All customers checkbox.".formatted(language));
    }

    void checkVariationConfigTable(int index) throws Exception {
        // check Add wholesale pricing button
        String dbAddWholesalePricingBtn = wait.until(visibilityOf(UI_VARIATION_ADD_WHOLESALE_PRICING_BTN.get(index))).getText();
        String ppAddWholesalePricingBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.addWholesalePricingBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbAddWholesalePricingBtn, ppAddWholesalePricingBtn, "[Failed][Wholesale config table] Add wholesale pricing button should be %s, but found %s.".formatted(ppAddWholesalePricingBtn, dbAddWholesalePricingBtn));
        logger.info("[UI][%s] Check Wholesale config table - Add wholesale pricing button.".formatted(language));

        // check Edit button
        String dbEditBtn = wait.until(visibilityOf(UI_VARIATION_EDIT_BTN.get(index))).getText();
        String ppEditBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.editBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbEditBtn, ppEditBtn, "[Failed][Wholesale config table] Edit button should be %s, but found %s.".formatted(ppEditBtn, dbEditBtn));
        logger.info("[UI][%s] Check Wholesale config table - Edit button.".formatted(language));

        // check Delete button
        String dbDeleteBtn = wait.until(visibilityOf(UI_VARIATION_DELETE_BTN.get(index))).getText();
        String ppDeleteBtn = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.header.variation.deleteBtn", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbDeleteBtn, ppDeleteBtn, "[Failed][Wholesale config table] Delete button should be %s, but found %s.".formatted(ppDeleteBtn, dbDeleteBtn));
        logger.info("[UI][%s] Check Wholesale config table - Delete button.".formatted(language));

        // check variation config table
        List<String> dbConfigTable = new ArrayList<>();
        dbConfigTable.add(wait.until(visibilityOf(UI_VARIATION_WHOLESALE_TITLE_COLUMN.get(index))).getText());
        dbConfigTable.add(wait.until(visibilityOf(UI_VARIATION_BUY_FROM_COLUMN.get(index))).getText());
        dbConfigTable.add(wait.until(visibilityOf(UI_VARIATION_PRICE_PER_ITEM_COLUMN.get(index))).getText());
        dbConfigTable.add(wait.until(visibilityOf(UI_VARIATION_DISCOUNT_COLUMN.get(index))).getText());
        dbConfigTable.add(wait.until(visibilityOf(UI_VARIATION_CUSTOMER_SEGMENT_COLUMN.get(index))).getText());
        List<String> ppConfigTable = List.of(getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.0", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.1", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.2", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.3", language),
                getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.tableColumn.4", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbConfigTable, ppConfigTable, "[Failed][Wholesale config table] Variation config table column should be %s, but found %s.".formatted(ppConfigTable, dbConfigTable));
        logger.info("[UI][%s] Check Wholesale config table - Variation config table.".formatted(language));

        // check customer segment tooltips
        UI_VARIATION_CUSTOMER_SEGMENT_COLUMN.get(index).click();
        String dbCustomerSegmentTooltips = wait.until(visibilityOf(UI_SEGMENT_TOOLTIPS)).getText();
        String ppCustomerSegmentTooltips = getPropertiesValueByDBLang("products.allProducts.wholesaleProduct.wholesaleConfig.segmentTooltips", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, dbCustomerSegmentTooltips, ppCustomerSegmentTooltips, "[Failed][Wholesale config table] Customer segment tooltips should be %s, but found %s.".formatted(ppCustomerSegmentTooltips, dbCustomerSegmentTooltips));
        logger.info("[UI][%s] Check Wholesale config table - Customer segment tooltips.".formatted(language));
    }

}
