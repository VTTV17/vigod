package pages.storefront.detail_product;

import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.products.all_products.ProductVerify;
import pages.storefront.shoppingcart.ShoppingCart;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.dashboard.login.Login.storeURL;
import static api.dashboard.products.CreateProduct.variationListingPrice;
import static api.dashboard.products.CreateProduct.withoutVariationListingPrice;
import static api.dashboard.products.CreateProduct.withoutVariationSellingPrice;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.promotion.CreatePromotion.*;
import static java.lang.Thread.sleep;
import static pages.dashboard.products.all_products.ProductPage.*;
import static pages.dashboard.products.all_products.ProductVerify.branchInfo;
import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;

public class ProductDetailPage extends ProductDetailElement {
    WebDriverWait wait;

    private int countFail = 0;

    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction commonAction;

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    /**
     * Access to product detail on SF by URL
     */
    public ProductDetailPage accessToProductDetailPageByURL() {
        // productID get from dashboard
        driver.get(driver.getCurrentUrl() + "/product/" + ProductVerify.productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(ProductVerify.productID));

        return this;
    }

    public ProductDetailPage accessToProductDetailPageByProductID() {
        driver.get("https://%s%s/vi/product/%s".formatted(storeURL, SF_DOMAIN, CreateProduct.productID));
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(CreateProduct.productID));
        verifyPageLoaded();
        return this;
    }

    public ProductDetailPage verifyPageLoaded() {
        String name = isCreateByUI ? ProductPage.productName : CreateProduct.productName;

        // wait page loaded successfully
        new UICommonAction(driver).verifyPageLoaded(name, name);

        return this;
    }

    public boolean checkElementVisible(WebElement element) {
        boolean check = true;
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException ex) {
            check = false;
        }
        return check;
    }


    // check flash sale
    private void checkFlashSaleShouldBeShown(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkElementVisible(FLASH_SALE_BADGE), "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }

    private void checkFlashSaleShouldBeHidden(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        countFail = new AssertCustomize(driver).assertFalse(countFail, checkElementVisible(FLASH_SALE_BADGE), "%s Flash sale badge does not hide".formatted(branch));
        logger.info("%s Check flash sale badge is hidden".formatted(branch));
    }

    public ProductDetailPage checkFlashSalePriceWithoutVariationProduct() throws IOException {
        // wait list branch visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check flash sale for each branch
        for (WebElement element : BRANCH_NAME_LIST) {
            // switch branch
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

            // check flash sale badge is shown
            checkFlashSaleShouldBeShown(element.getText());

            // check flash sale price
            checkProductPrice(withoutVariationListingPrice,
                    CreatePromotion.flashSaleWithoutVariationPrice,
                    element.getText());
        }
        return this;
    }

    public ProductDetailPage checkProductPriceWhenFlashSaleIsScheduleWithoutVariationProduct() throws IOException {
        // wait list branch visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check flash sale for each branch
        for (WebElement element : BRANCH_NAME_LIST) {
            // switch branch
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

            // check flash sale badge is shown
            checkFlashSaleShouldBeShown(element.getText());

            // check selling price should be shown
            checkWholesaleProductPrice(withoutVariationListingPrice,
                    withoutVariationSellingPrice,
                    withoutVariationWholesaleProductStock,
                    withoutVariationWholesaleProductPrice,
                    element.getText());
        }
        return this;
    }

    public ProductDetailPage checkFlashSalePriceVariationProduct() throws IOException {
        // get variation coordinates
        Map<String, List<Integer>> variationValueCoordinates = getVariationValueCoordinates();

        // wait branch list visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check product price for each variation
        for (String variationValue : variationValueCoordinates.keySet()) {
            // check flash sale price if variation in flash sale campaign
            if (flashSaleVariationList.contains(variationValue)) {

                // select variation
                for (int index : variationValueCoordinates.get(variationValue)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
                }

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check for each branch
                for (WebElement element : BRANCH_NAME_LIST) {
                    // check flash sale badge is shown
                    checkFlashSaleShouldBeShown(element.getText());

                    // switch branch
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                    // wait spinner loading if any
                    commonAction.waitForElementInvisible(SPINNER, 15);

                    // check flash sale price for each branch
                        checkProductPrice(variationListingPrice.get(variationList.indexOf(variationValue)),
                                flashSaleVariationPrice.get(flashSaleVariationList.indexOf(variationValue)),
                                element.getText());
                }
            } else { // if variation is not in flash sale campaign
                // check selling price for each variation
                for (int index : variationValueCoordinates.get(variationValue)) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
                }

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check selling price for each branch
                for (WebElement element : BRANCH_NAME_LIST) {

                    // check flash sale badge is not shown
                    checkFlashSaleShouldBeHidden(element.getText());

                    // switch branch
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                    // wait spinner loading if any
                    commonAction.waitForElementInvisible(SPINNER, 15);

                    // check selling price
                    checkProductPrice(variationListingPrice.get(variationList.indexOf(variationValue)),
                            CreateProduct.variationSellingPrice.get(variationList.indexOf(variationValue)),
                            element.getText());
                }
            }
        }
        return this;
    }

    public ProductDetailPage checkProductPriceWhenFlashSaleIsScheduleVariationProduct() throws IOException {
        // get variation coordinates
        Map<String, List<Integer>> variationValueCoordinates = getVariationValueCoordinates();

        // wait list branch is visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check flash sale badge
        for (String variationValue : variationValueCoordinates.keySet()) {
            // select variation
            for (int index : variationValueCoordinates.get(variationValue)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
            }

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check selling price for each branch
            for (WebElement element : BRANCH_NAME_LIST) {
                // switch branch
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check flash sale for each variation
                if (flashSaleVariationList.contains(variationValue)) {
                    // check flash sale badge is shown if in variation in flash sale campaign
                    checkFlashSaleShouldBeShown(element.getText());

                } else {
                    // if flash sale is not available, check flash sale badge should be hidden
                    checkFlashSaleShouldBeHidden(element.getText());
                }

                // NOTE: discount logic when combined flash sale/ discount campaign / wholesale product
                // discount campaign is ignored when flash sale is schedule
                // wholesale product is shown when flash sale is schedule => check wholesale product price if any
                int varIndex = variationList.indexOf(variationValue);
                checkWholesaleProductPrice(variationListingPrice.get(varIndex),
                        CreateProduct.variationSellingPrice.get(varIndex),
                        variationWholesaleProductStock.get(varIndex),
                        variationWholesaleProductPrice.get(varIndex),
                        element.getText());
            }
        }
        return this;
    }


    // check discount campaign
    private void checkDiscountCampaignShouldBeShown(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        boolean check = true;
        try {
            if (WHOLESALE_CAMPAIGN_CHECKBOX.getAttribute("class").contains("unchecked")) {
                WHOLESALE_CAMPAIGN_CHECKBOX.click();
            } else {
                // uncheck
                WHOLESALE_CAMPAIGN_CHECKBOX.click();

                // check again
                WHOLESALE_CAMPAIGN_CHECKBOX.click();
            }
        } catch (NoSuchElementException ex) {
            check = false;
        }

        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "%s Discount campaign does not show".formatted(branch));
        logger.info("%s Check discount campaign is shown".formatted(branch));
    }

    private void checkDiscountCampaignShouldBeHidden(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        boolean check = true;
        try {
            if (WHOLESALE_CAMPAIGN_CHECKBOX.getAttribute("class").contains("unchecked")) {
                WHOLESALE_CAMPAIGN_CHECKBOX.click();
            }
        } catch (NoSuchElementException ex) {
            check = false;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, check, "%s Discount campaign does not hide".formatted(branch));
        logger.info("%s Check discount campaign is hidden".formatted(branch));
    }

    private void checkDiscountCampaignPrice(int listingPrice, int sellingPrice, int discountCampaignCouponValue, int wholesaleProductStock, int wholesaleProductPrice) throws IOException {
        // check product price for each branch
        for (WebElement element : BRANCH_NAME_LIST) {
            // switch branch
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check branch has discount campaign or not
            // check product price
            if (productDiscountCampaignApplicableBranch.contains(element.getText())) {
                // check Buy in bulk checkbox is shown
                checkDiscountCampaignShouldBeShown(element.getText());

                // get campaign price
                int salePrice = (productWholesaleCouponType == 0)
                        ? Math.round(sellingPrice * (1 - ((float) discountCampaignCouponValue / 100)))
                        : (Math.max(0, sellingPrice - discountCampaignCouponValue));

                // check product price
                checkProductPrice(listingPrice,
                        salePrice,
                        element.getText());
            } else {
                // check Buy in bulk checkbox is hidden
                checkDiscountCampaignShouldBeHidden(element.getText());

                // check wholesale product price if any
                checkWholesaleProductPrice(listingPrice,
                        sellingPrice,
                        wholesaleProductStock,
                        wholesaleProductPrice,
                        element.getText());
            }
        }
    }

    public ProductDetailPage checkDiscountCampaignPriceWithoutVariationProduct() throws IOException {
        // wait branch list visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check discount campaign price
        checkDiscountCampaignPrice(withoutVariationListingPrice,
                withoutVariationSellingPrice,
                productWholesaleCouponValue,
                withoutVariationWholesaleProductStock,
                withoutVariationWholesaleProductPrice);
        return this;
    }

    public ProductDetailPage checkDiscountCampaignPriceVariationProduct() throws IOException {
        // wait branch list visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // get variation coordinates
        Map<String, List<Integer>> variationValueCoordinates = getVariationValueCoordinates();

        for (String variationValue : variationValueCoordinates.keySet()) {
            // select variation
            for (int index : variationValueCoordinates.get(variationValue)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
            }

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // get variation index
            int varIndex = variationList.indexOf(variationValue);

            // check discount campaign price
            checkDiscountCampaignPrice(variationListingPrice.get(varIndex),
                    CreateProduct.variationSellingPrice.get(varIndex),
                    productWholesaleCouponValue,
                    variationWholesaleProductStock.get(varIndex),
                    variationWholesaleProductPrice.get(varIndex));
        }
        return this;
    }

    // check wholesale product price
    private void checkWholesaleProductShouldBeShown(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        boolean check = true;
        try {
            WHOLESALE_PRODUCT_INFO.getText();
        } catch (NoSuchElementException ex) {
            check = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }

    private void checkWholesaleProductPrice(int listingPrice, int sellingPrice, int wholesaleProductStock, int wholesaleProductPrice, String branchName) throws IOException {
        // check wholesale product price if any
        if ((wholesaleProductStock > 0) && (!productDiscountCampaignApplicableBranch.contains(branchName))) {
            // increase quantity to minimum requirement
            wait.until(ExpectedConditions.elementToBeClickable(QUANTITY)).click();
            QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
            QUANTITY.sendKeys(String.valueOf(wholesaleProductStock));

//            // click around
//            PRODUCT_NAME.click();

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check product wholesale product price
            checkProductPrice(listingPrice,
                    wholesaleProductPrice,
                    branchName);
        } else {
            // check product selling price
            checkProductPrice(listingPrice,
                    sellingPrice,
                    branchName);
        }
    }

    public ProductDetailPage checkWholesaleProductPriceWithoutVariationProduct() throws IOException {
        // wait branch list visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // check product price for each branch
        for (WebElement element : BRANCH_NAME_LIST) {

            // switch branch
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check wholesale product price is shown
            checkWholesaleProductShouldBeShown(element.getText());

            // check wholesale product price
            checkWholesaleProductPrice(withoutVariationListingPrice,
                    withoutVariationSellingPrice,
                    withoutVariationWholesaleProductStock,
                    withoutVariationWholesaleProductPrice,
                    element.getText());
        }
        return this;
    }

    public ProductDetailPage checkWholesaleProductPriceVariationProduct() throws IOException {
        // get variation coordinates
        Map<String, List<Integer>> variationValueCoordinates = getVariationValueCoordinates();

        for (String variationValue : variationValueCoordinates.keySet()) {
            // select variation
            for (int index : variationValueCoordinates.get(variationValue)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
            }

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check product price for each branch
            for (WebElement element : BRANCH_NAME_LIST) {

                // switch branch
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check wholesale product price is shown
                checkWholesaleProductShouldBeShown(element.getText());

                // get variation index
                int varIndex = variationList.indexOf(variationValue);

                // check wholesale product price, if
                checkWholesaleProductPrice(variationListingPrice.get(varIndex),
                        CreateProduct.variationSellingPrice.get(varIndex),
                        variationWholesaleProductStock.get(varIndex),
                        variationWholesaleProductPrice.get(varIndex),
                        element.getText());
            }
        }

        return this;
    }

    /**
     * <p> Without variation product</p>
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public ProductDetailPage checkProductIsDisplayOrHideWithoutVariationProduct() throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(ProductPage.productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean inStock = withoutVariationStockQuantity > 0;
        boolean actDisplay = LIST_SEARCH_RESULT.size() > 0;
        countFail = new AssertCustomize(driver).assertTrue(countFail, actDisplay == isDisplayIfOutOfStock || inStock, "[Failed] Product display should be %s but it is %s".formatted(isDisplayIfOutOfStock, actDisplay));
        return this;
    }

    /**
     * <p> Variation product</p>
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public ProductDetailPage checkProductIsDisplayOrHideVariationProduct() throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(ProductPage.productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean inStock = !((ProductPage.variationStockQuantity.get(0) == 0) && (increaseStockForNextVariation == 0));
        boolean actDisplay = LIST_SEARCH_RESULT.size() > 0;
        countFail = new AssertCustomize(driver).assertTrue(countFail, actDisplay == isDisplayIfOutOfStock || inStock, "[Failed] Product display should be %s but it is %s".formatted(isDisplayIfOutOfStock, actDisplay));
        return this;
    }


    /**
     * <p> Without variation product</p>
     * <p> actHide = true when remaining stock has been hidden</p>
     */
    public ProductDetailPage checkRemainingStockIsDisplayOrHideWithoutVariationProduct() throws IOException {
        // if in stock, check show/hide as setting
        if (withoutVariationStockQuantity > 0) {
            // actHide = true when stock quantity does not show
            boolean actHide = !(STOCK_QUANTITY_IN_BRANCH.size() > 0);

            // compare setting as actual result
            // isHideRemainingStock: setting on ProductPage.java
            countFail = new AssertCustomize(driver).assertTrue(countFail, actHide == isHideRemainingStock, "[Failed] Remaining stock is hidden: %s but it is %s".formatted(isDisplayIfOutOfStock, actHide));
        } else {
            // if out of stock, skip this test
            logger.info("SKIPPED - Out of stock");
        }
        return this;
    }

    /**
     * <p> Without variation product</p>
     * <p> actHide = true when remaining stock has been hidden</p>
     */
    public ProductDetailPage checkRemainingStockIsDisplayOrHideVariationProduct() throws IOException {
        if (!((ProductPage.variationStockQuantity.get(0) == 0) && (increaseStockForNextVariation == 0))) {
            boolean actHide = !(STOCK_QUANTITY_IN_BRANCH.size() > 0);
            countFail = new AssertCustomize(driver).assertTrue(countFail, actHide == isHideRemainingStock, "[Failed] Remaining stock is hidden: %s but it is %s".formatted(isHideRemainingStock, actHide));
        } else {
            logger.info("SKIPPED - Out of stock");
        }
        return this;
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    public ProductDetailPage check404PageShouldBeShownWhenProductOutOfStock() throws IOException {
        accessToProductDetailPageByURL();
        countFail = new AssertCustomize(driver).assertTrue(countFail, driver.getCurrentUrl().contains("404"), "[Failed] Can access to product detail page by URL although product has been hidden");
        logger.info(driver.getCurrentUrl());
        logger.info("404 page should be shown when product out of stock");
        return this;
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    private void checkProductName() throws IOException {
        // get actual product name
        String actName = wait.until(ExpectedConditions.visibilityOf(PRODUCT_NAME)).getText();

        // compare actual product name with product name has been setting at ProductPage.java
        countFail = new AssertCustomize(driver).assertEquals(countFail, actName, ProductPage.productName, "[Failed] Product Name show %s instead of %s".formatted(actName, ProductPage.productName));
        logger.info("Verify product name show correctly");
    }

    /**
     * Compare product price/currency on the SF with Dashboard
     */
    private void checkProductPrice(int listingPrice, int sellingPrice, String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        if (listingPrice != sellingPrice) {
            String actListingPrice = new UICommonAction(driver).getText(LISTING_PRICE).replace(",", "");
            countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + STORE_CURRENCY, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
        }

        String actSellingPrice = new UICommonAction(driver).getText(SELLING_PRICE).replace(",", "");
        int actSellingPriceValue = Integer.parseInt(actSellingPrice.replace(STORE_CURRENCY, ""));

        countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(actSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, actSellingPrice));
        logger.info("%s Verify product price/ store currency show correctly".formatted(branch));
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    private void checkVariationName() throws IOException {
        int variationNameID = 0;
        for (String variationName : variation.keySet()) {
            String actVariationName = wait.until(ExpectedConditions.visibilityOf(LIST_VARIATION_NAME.get(variationNameID))).getText();
            countFail = new AssertCustomize(driver).assertEquals(countFail, actVariationName, variationName.toUpperCase(), "[Failed] Variation name should be show %s instead of %s".formatted(variationName, actVariationName));
            variationNameID++;
        }
        logger.info("Verify product variation show correctly");
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    private void checkSoldOutMarkHasBeenShown() throws IOException {
        boolean isSoldOut = SOLD_OUT_MARK.getText().equals("Hết hàng") || SOLD_OUT_MARK.getText().equals("Out of stock");
        countFail = new AssertCustomize(driver).assertTrue(countFail, isSoldOut, "[Failed] Sold out mark does not show");
        logger.info("Verify Sold out mark should be shown when product out of stock");
    }

    private void checkBranchInformation() throws IOException {
        new UICommonAction(driver).waitElementList(BRANCH_NAME_LIST);
        Map<String, String> actBranchInfo = new HashMap<>();
        for (int i = 0; i < BRANCH_NAME_LIST.size(); i++) {
            actBranchInfo.put(wait.until(ExpectedConditions.visibilityOf(BRANCH_NAME_LIST.get(i))).getText(),
                    wait.until(ExpectedConditions.visibilityOf(BRANCH_ADDRESS_LIST.get(i))).getText());
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, actBranchInfo.equals(branchInfo), "[Failed] Branch information does not match.");
        logger.info("Storefront branch information: " + actBranchInfo);
        logger.info("Dashboard branch information: " + branchInfo);
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    private void checkStockQuantity(int stockQuantity) throws IOException {
        if (stockQuantity > 0) {
            for (WebElement element : STOCK_QUANTITY_IN_BRANCH) {
                String actStock = wait.until(ExpectedConditions.visibilityOf(element)).getText()
                        .replace("Còn hàng ", "").replace(" in stock", "").replace(",", "");
                countFail = new AssertCustomize(driver).assertEquals(countFail, actStock, String.valueOf(stockQuantity), "[Failed] Stock quantity should be %s instead of %s".formatted(stockQuantity, actStock));
            }
            logger.info("Check current stock quantity");
        } else {
            checkSoldOutMarkHasBeenShown();
        }
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    private void checkProductDescription() throws IOException {
        String actDescriptions = wait.until(ExpectedConditions.visibilityOf(PRODUCT_DESCRIPTION)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail, actDescriptions, ProductPage.productDescription, "[Failed] Product Description does not match");
    }

    /**
     * Verify all information on the SF is shown correctly (without variation product)
     */
    public ProductDetailPage checkWithoutVariationProductInformation() throws IOException {
        checkProductName();
        checkProductPrice(ProductPage.withoutVariationListingPrice, ProductPage.withoutVariationSellingPrice, "");
        if (withoutVariationStockQuantity > 0) {
            checkBranchInformation();
        }
        checkStockQuantity(ProductPage.withoutVariationStockQuantity);
        checkProductDescription();
        return this;
    }


    public Map<String, List<Integer>> getVariationValueCoordinates() {

        Map<String, List<String>> variation = isCreateByUI ? ProductPage.variation : variationMap;
        List<String> variationValueList = isCreateByUI ? ProductPage.variationValueList : variationList;

        // generate coordinates variation in product detail page
        // example: variationMap = {Size =[S, M, L], Color = [Red]}
        // coordinates map = { S Red = [0, 3], M Red = [1, 3], L Red =[2, 3]}
        // on product detail page, corresponding to 2 variations we will have 2 dropdowns are {S, M, L} and {Red}
        // to select variation S Red, we have to select S on dropdown1 and Red on dropdown2 with its coordinates
        Map<String, List<Integer>> coordinates = new HashMap<>();

        // init variation value index
        // example: variationMap = {Size =[S, M, L], Color = [Red]}
        // variationValueIndex = {S = [0], M = [1], L = [2], Red = [3]}
        Map<String, Integer> variationValueIndex = new HashMap<>();

        // currentIndex is current variation coordinates
        int currentIndex = 0;

        // index all variations value
        for (String variationName: variation.keySet()) {
            for (String variationValue: variation.get(variationName)) {
                variationValueIndex.put(variationValue, currentIndex);
                currentIndex ++;
            }
        }

        // convert variation map from variation value to variation coordinates(index)
        for (String variationValue: variationValueList) {
            // if we have multiple variation, they will be separated by space character
            String[] varValue = variationValue.replace("|", " ").split(" ");

            // get list variation coordinates
            // with S Red variation, we have listIndex = [0, 3]
            List<Integer> listIndex = new ArrayList<>();
            for (String value: varValue) {
                listIndex.add(variationValueIndex.get(value));
            }

            // add variation and its coordinates
            coordinates.put(variationValue, listIndex);
        }

        return coordinates;
    }


    /**
     * Verify all information on the SF is shown correctly (variation product)
     */
    public ProductDetailPage checkVariationProductInformation() throws IOException {
        // get variation coordinates
        Map<String, List<Integer>> variationValueCoordinates = getVariationValueCoordinates();

        // check all variation information
        for (String variationValue: variationValueCoordinates.keySet()) {

            // check product name
            checkProductName();

            // select variation
            for (int index : variationValueCoordinates.get(variationValue)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
            }

            // get variation value index
            int varIndex = variationValueList.indexOf(variationValue);

            // check stock quantity
            if (!isHideRemainingStock) {
                checkStockQuantity(ProductPage.variationStockQuantity.get(varIndex));
            }

            // check price
            checkProductPrice(ProductPage.variationListingPrice.get(varIndex), ProductPage.variationSellingPrice.get(varIndex), "");

            // check branch name
            if (ProductPage.variationStockQuantity.get(varIndex) > 0) {
                checkBranchInformation();
            }

            // check variation name
            checkVariationName();

            // check product description
            checkProductDescription();
        }

        return this;
    }

    /**
     * <p> countFail: The number of failure cases in this test</p>
     * <p> If countFail > 0, some cases have been failed</p>
     * <p> Reset countFail for the next test</p>
     */
    public void completeVerify() {
        if (countFail > 0) {
            int count = countFail;
            countFail = 0;
            Assert.fail("[Failed] Fail %d cases".formatted(count));
        }
    }

    public ShoppingCart clickOnBuyNow(){
        commonAction.clickElement(BUY_NOW_BTN);
        logger.info("CLick on Buy Now button");
        commonAction.sleepInMiliSecond(2000);
        return new ShoppingCart(driver);
    }
    public ProductDetailPage accessToProductDetailPageByURL(String domain, String productID) {
        commonAction.navigateToURL(domain+"product/"+productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        commonAction.sleepInMiliSecond(3000);
        return this;
    }

    public String getFlashSaleStatus() {
        if (flashSaleStatus.equals("SCHEDULE")) {
            boolean checkStart = flashSaleStartTime.getEpochSecond() - Instant.now().getEpochSecond() >= 0;
            boolean checkEnd = flashSaleEndTime.getEpochSecond() - Instant.now().getEpochSecond() > 0;
            if (checkStart && checkEnd) {
                return flashSaleStatus = "IN-PROGRESS";
            } else {
                return flashSaleStatus = "SCHEDULE";
            }
        }
        return flashSaleStatus = "EXPIRED";
    }

    public String getProductDiscountCampaignStatus() {
        if (productDiscountCampaignStatus.equals("SCHEDULE")) {
            boolean checkStart = productDiscountCampaignStartTime.getEpochSecond() - Instant.now().getEpochSecond() >= 0;
            boolean checkEnd = productDiscountCampaignEndTime.getEpochSecond() - Instant.now().getEpochSecond() > 0;
            if (checkStart && checkEnd) {
                return productDiscountCampaignStatus = "IN-PROGRESS";
            } else {
                return productDiscountCampaignStatus = "SCHEDULE";
            }
        }
        return productDiscountCampaignStatus = "EXPIRED";
    }
    public void checkPrice() {

    }
}
