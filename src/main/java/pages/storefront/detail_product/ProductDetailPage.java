package pages.storefront.detail_product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.all_products.ProductPage;
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
import static pages.dashboard.products.all_products.ProductVerify.branchInfo;
import static pages.dashboard.products.all_products.ProductVerify.productID;

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

    public ProductDetailPage accessToProductDetailPageByURL() throws InterruptedException {
        driver.get(driver.getCurrentUrl() + "/product/" + productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        sleep(3000);
        return this;
    }

    /**
     * <p> Without variation product</p>
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public ProductDetailPage checkProductIsDisplayOrHideWithoutVariationProduct(Boolean isDisplay, String productName, int stockQuantity) throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean inStock = stockQuantity > 0;
        boolean actDisplay = LIST_SEARCH_RESULT.size() > 0;
        countFail = new AssertCustomize(driver).assertTrue(countFail, actDisplay == isDisplay || inStock, "[Failed] Product display should be %s but it is %s".formatted(isDisplay, actDisplay));
        return this;
    }

    /**
     * <p> Variation product</p>
     * <p> If have product match condition, hasResult = false</p>
     * <p> Else hasResult = true</p>
     */
    public ProductDetailPage checkProductIsDisplayOrHideVariationProduct(Boolean isDisplay, String productName, int startQuantity, int increaseStockForNextVariation) throws IOException, InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_ICON)).click();
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BOX)).sendKeys(productName);

        // If have product match condition, actDisplay = false
        // Else actDisplay = true
        sleep(1000);
        boolean inStock = !((startQuantity == 0) && (increaseStockForNextVariation == 0));
        boolean actDisplay = LIST_SEARCH_RESULT.size() > 0;
        countFail = new AssertCustomize(driver).assertTrue(countFail, actDisplay == isDisplay || inStock, "[Failed] Product display should be %s but it is %s".formatted(isDisplay, actDisplay));
        return this;
    }


    /**
     * <p> Without variation product</p>
     * <p> actHide = true when remaining stock has been hidden</p>
     */
    public ProductDetailPage checkRemainingStockIsDisplayOrHideWithoutVariationProduct(boolean isHide, int stockQuantity) throws IOException {
        if (stockQuantity > 0) {
            boolean actHide = !(STOCK_QUANTITY_IN_BRANCH.size() > 0);
            countFail = new AssertCustomize(driver).assertTrue(countFail, actHide == isHide, "[Failed] Remaining stock is hidden: %s but it is %s".formatted(isHide, actHide));
        } else {
            logger.info("SKIPPED - Out of stock");
        }
        return this;
    }

    /**
     * <p> Without variation product</p>
     * <p> actHide = true when remaining stock has been hidden</p>
     */
    public ProductDetailPage checkRemainingStockIsDisplayOrHideVariationProduct(boolean isHide, int startQuantity, int increaseStockForNextVariation) throws IOException {
        if (!((startQuantity == 0) && (increaseStockForNextVariation == 0))) {
            boolean actHide = !(STOCK_QUANTITY_IN_BRANCH.size() > 0);
            countFail = new AssertCustomize(driver).assertTrue(countFail, actHide == isHide, "[Failed] Remaining stock is hidden: %s but it is %s".formatted(isHide, actHide));
            logger.info("isHide: " + isHide);
            logger.info("actHide: " + actHide);
        } else {
            logger.info("SKIPPED - Out of stock");
        }
        return this;
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    public ProductDetailPage check404PageShouldBeShownWhenProductOutOfStock() throws IOException, InterruptedException {
        accessToProductDetailPageByURL();
        countFail = new AssertCustomize(driver).assertTrue(countFail, driver.getCurrentUrl().contains("404"), "[Failed] Can access to product detail page by URL although product has been hidden");
        logger.info(driver.getCurrentUrl());
        logger.info("404 page should be shown when product out of stock");
        return this;
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    private void checkProductName(String productName) throws IOException {
        String actName = wait.until(ExpectedConditions.visibilityOf(PRODUCT_NAME)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail, actName, productName, "[Failed] Product Name show %s instead of %s".formatted(actName, productName));
        logger.info("Verify product name show correctly");
    }

    /**
     * Compare product price/currency on the SF with Dashboard
     */
    private void checkProductPrice(int listingPrice, int sellingPrice, String currencySymbol) throws IOException {
        String actListingPrice = wait.until(ExpectedConditions.visibilityOf(LISTING_PRICE)).getText().replace(",", "");
        String actSellingPrice = wait.until(ExpectedConditions.visibilityOf(SELLING_PRICE)).getText().replace(",", "");
        countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + currencySymbol, "[Failed] Listing price should be show %s instead of %s".formatted(listingPrice, actListingPrice));
        countFail = new AssertCustomize(driver).assertEquals(countFail, actSellingPrice, sellingPrice + currencySymbol, "[Failed] Selling price should be show %s instead of %s".formatted(listingPrice, actListingPrice));
        logger.info("Verify product price/ store currency show correctly");
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    private void checkVariation(Map<String, List<String>> variation) throws IOException {
        variation = new ProductPage(driver).getVariationsMap(variation);
        int variationNameID = 0;
        int variationValueID = 0;
        for (String variationName : variation.keySet()) {
            String actVariationName = wait.until(ExpectedConditions.visibilityOf(LIST_VARIATION_NAME.get(variationNameID))).getText();
            countFail = new AssertCustomize(driver).assertEquals(countFail, actVariationName, variationName.toUpperCase(), "[Failed] Variation name should be show %s instead of %s".formatted(variationName, actVariationName));
            for (String variationValue : variation.get(variationName)) {
                String actVariationValue = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent;", LIST_VARIATION_VALUE.get(variationValueID)).toString();
                countFail = new AssertCustomize(driver).assertEquals(countFail, actVariationValue, variationValue, "[Failed] Variation name should be show %s instead of %s".formatted(variationValue, actVariationValue));
                variationValueID++;
            }
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
        waitElementList(BRANCH_NAME_LIST);
        Map<String, String> actBranchInfo = new HashMap<>();
        for (int i = 0; i < BRANCH_NAME_LIST.size(); i++) {
            actBranchInfo.put(wait.until(ExpectedConditions.visibilityOf(BRANCH_NAME_LIST.get(i))).getText(),
                    wait.until(ExpectedConditions.visibilityOf(BRANCH_ADDRESS_LIST.get(i))).getText());
        }
        System.out.println(actBranchInfo);
        System.out.println(branchInfo);
        countFail = new AssertCustomize(driver).assertTrue(countFail, actBranchInfo.equals(branchInfo), "[Failed] Branch information does not match.");
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
     * convert list string to index
     */
    private List<List<Integer>> convert(List<List<String>> stringList) {
        List<List<Integer>> newList = new ArrayList<>();
        int index = 0;
        for (List<String> strList : stringList) {
            List<Integer> integerList = new ArrayList<>();
            for (int i = 0; i < strList.size(); i++) {
                integerList.add(index);
                index++;
            }
            newList.add(integerList);
        }
        return newList;
    }

    /**
     * mix variation value
     */
    private List<List<Integer>> merge(List<Integer> list1, List<Integer> list2) {
        List<List<Integer>> merge = new ArrayList<>();
        for (Integer ax : list1) {
            for (Integer bx : list2) {
                List<Integer> ab = List.of(ax, bx);
                merge.add(ab);
            }
        }
        return merge;
    }


    /**
     * Compare product stock quantity per branch on the SF with Dashboard (variation product)
     */
    private void checkStockQuantityForVariationProduct(Map<String, List<String>> variation, int startQuantity, int increaseStockForNextVariation) throws IOException {
        // if variation size > 2, only keep 2 variations
        variation = new ProductPage(driver).getVariationsMap(variation);

        // get list variation value
        List<List<String>> listVariation = new ArrayList<>();
        for (String variationName : variation.keySet()) {
            listVariation.add(variation.get(variationName));
        }

        // convert variation value to index (element list)
        List<List<Integer>> variationValueIndexList = convert(listVariation);

        // mix variation index
        List<List<Integer>> variationValueIndexListMerge = new ArrayList<>();
        List<Integer> orgList = variationValueIndexList.get(0);
        if (variationValueIndexList.size() > 1) {
            for (int i = 1; i < variationValueIndexList.size(); i++) {
                variationValueIndexListMerge = merge(orgList, variationValueIndexList.get(i));
            }
        } else {
            variationValueIndexList.add(orgList);
        }

        // check stock quantity for each variation
        for (int i = 0; i < variationValueIndexListMerge.size(); i++) {
            List<Integer> variationIndex = variationValueIndexListMerge.get(i);
            for (Integer index : variationIndex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LIST_VARIATION_VALUE.get(index));
            }
            checkStockQuantity(startQuantity + i * increaseStockForNextVariation);
        }
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    private void checkProductDescription(String productDescription) throws IOException {
        String actDescriptions = wait.until(ExpectedConditions.visibilityOf(PRODUCT_DESCRIPTION)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail, actDescriptions, productDescription, "[Failed] Product Description does not match");
    }

    /**
     * Verify all information on the SF is shown correctly (without variation product)
     */
    public ProductDetailPage checkAllInformationIsDisplayedProperlyWithoutVariationProduct(String productName, int listingPrice, int sellingPrice, String currencySymbol, int stockQuantity, String productDescription) throws IOException {
        checkProductName(productName);
        checkProductPrice(listingPrice, sellingPrice, currencySymbol);
        if (stockQuantity > 0) {
            checkBranchInformation();
        }
        checkStockQuantity(stockQuantity);
        checkProductDescription(productDescription);
        return this;
    }

    /**
     * Verify all information on the SF is shown correctly (variation product)
     */
    public ProductDetailPage checkAllInformationIsDisplayedProperlyVariationProduct(String productName, int listingPrice, int sellingPrice, String currencySymbol, Map<String, List<String>> variation, int startQuantity, int increaseStockForNextVariation, String productDescription) throws IOException {
        checkProductName(productName);
        checkProductPrice(listingPrice, sellingPrice, currencySymbol);
        if (!((startQuantity == 0) && (increaseStockForNextVariation == 0))) {
            checkVariation(variation);
        }
        checkStockQuantityForVariationProduct(variation, startQuantity, increaseStockForNextVariation);
        checkProductDescription(productDescription);
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

    /**
     * Wait until list element loading successfully
     */
    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
    public ShoppingCart clickOnBuyNow(){
        common.clickElement(BUY_NOW_BTN);
        logger.info("CLick on Buy Now button");
        return new ShoppingCart(driver);
    }
    public ProductDetailPage accessToProductDetailPageByURL(String domain, String productID) {
        common.navigateToURL(domain+"product/"+productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        common.sleepInMiliSecond(3000);
        return this;
    }
}
