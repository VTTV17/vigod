package pages.storefront.detail_product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.storefront.shoppingcart.ShoppingCart;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static pages.dashboard.products.all_products.ProductPage.*;
import static pages.dashboard.products.all_products.ProductVerify.branchInfo;
import static pages.dashboard.products.all_products.ProductVerify.productID;
import static utilities.links.Links.STORE_CURRENCY;

public class ProductDetailPage extends ProductDetailElement {
    WebDriverWait wait;

    static int countFail = 0;

    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction common;
    public ProductDetailPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
    }

    /**
     * Access to product detail on SF by URL
     */
    public ProductDetailPage accessToProductDetailPageByURL() {
        // productID get from dashboard
        driver.get(driver.getCurrentUrl() + "/product/" + productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));

        return this;
    }

    public ProductDetailPage verifyPageLoaded() {
        // wait page loaded successfully
        new UICommonAction(driver).verifyPageLoaded(productName, productName);

        return this;
    }

    /**
     * <p> Without variation product</p>
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public ProductDetailPage checkProductIsDisplayOrHideWithoutVariationProduct() throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(productName);

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
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean inStock = !((variationStockQuantity.get(0) == 0) && (increaseStockForNextVariation == 0));
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
        if (!((variationStockQuantity.get(0) == 0) && (increaseStockForNextVariation == 0))) {
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
        countFail = new AssertCustomize(driver).assertEquals(countFail, actName, productName, "[Failed] Product Name show %s instead of %s".formatted(actName, productName));
        logger.info("Verify product name show correctly");
    }

    /**
     * Compare product price/currency on the SF with Dashboard
     */
    private void checkProductPrice(int listingPrice, int sellingPrice) throws IOException {
        String actListingPrice = wait.until(ExpectedConditions.visibilityOf(LISTING_PRICE)).getText().replace(",", "");
        String actSellingPrice = wait.until(ExpectedConditions.visibilityOf(SELLING_PRICE)).getText().replace(",", "");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + STORE_CURRENCY, "[Failed] Listing price should be show %s instead of %s".formatted(listingPrice, actListingPrice));
        countFail = new AssertCustomize(driver).assertEquals(countFail, actSellingPrice, sellingPrice + STORE_CURRENCY, "[Failed] Selling price should be show %s instead of %s".formatted(sellingPrice, actSellingPrice));
        logger.info("Verify product price/ store currency show correctly");
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
        countFail = new AssertCustomize(driver).assertEquals(countFail, actDescriptions, productDescription, "[Failed] Product Description does not match");
    }

    /**
     * Verify all information on the SF is shown correctly (without variation product)
     */
    public ProductDetailPage checkWithoutVariationProductInformation() throws IOException {
        checkProductName();
        checkProductPrice(withoutVariationListingPrice, withoutVariationSellingPrice);
        if (withoutVariationStockQuantity > 0) {
            checkBranchInformation();
        }
        checkStockQuantity(withoutVariationStockQuantity);
        checkProductDescription();
        return this;
    }

    /**
     * Verify all information on the SF is shown correctly (variation product)
     */
    private Map<String, List<Integer>> getVariationValueCoordinates() {
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
            String[] variation = variationValue.split(" ");

            // get list variation coordinates
            // with S Red variation, we have listIndex = [0, 3]
            List<Integer> listIndex = new ArrayList<>();
            for (String value: variation) {
                listIndex.add(variationValueIndex.get(value));
            }

            // add variation and its coordinates
            coordinates.put(variationValue, listIndex);
        }
        logger.debug(variationValueList);
        logger.debug(coordinates);

        return coordinates;
    }


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
                checkStockQuantity(variationStockQuantity.get(varIndex));
            }

            // check price
            checkProductPrice(variationListingPrice.get(varIndex), variationSellingPrice.get(varIndex));

            // check branch name
            if (variationStockQuantity.get(varIndex) > 0) {
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
        common.clickElement(BUY_NOW_BTN);
        logger.info("CLick on Buy Now button");
        common.sleepInMiliSecond(2000);
        return new ShoppingCart(driver);
    }
    public ProductDetailPage accessToProductDetailPageByURL(String domain, String productID) {
        common.navigateToURL(domain+"product/"+productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        common.sleepInMiliSecond(3000);
        return this;
    }
}
