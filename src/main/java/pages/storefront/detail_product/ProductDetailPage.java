package pages.storefront.detail_product;

import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.storefront.shoppingcart.ShoppingCart;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.storeURL;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.branchID;
import static api.dashboard.setting.BranchManagement.isHideOnStoreFront;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static utilities.api_body.product.CreateProductBody.isDisplayOutOfStock;
import static utilities.api_body.product.CreateProductBody.isHideStock;
import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;

public class ProductDetailPage extends ProductDetailElement {
    WebDriverWait wait;

    private int countFail = 0;

    List<String> branchStatus;

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
    public ProductDetailPage accessToProductDetailPageByProductID() {
        driver.get("https://%s%s/vi/product/%s".formatted(storeURL, SF_DOMAIN, CreateProduct.productID));
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(CreateProduct.productID));
        commonAction.waitForElementInvisible(SPINNER, 15);

        // get max stock
        int maxStock = isVariation
                ? Collections.max(variationStockQuantity.values().stream().map(Collections::max).toList())
                : Collections.max(withoutVariationStock);

        // check product is display or not
        if ((maxStock == 0) && (!isDisplayOutOfStock)) commonAction.sleepInMiliSecond(1000);
        else commonAction.verifyPageLoaded(productName, productName);

        return this;
    }

    public boolean checkElementVisible(WebElement element) {
        boolean check = true;
        try {
            wait.until(elementToBeClickable(element));
        } catch (TimeoutException ex) {
            check = false;
        }
        return check;
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

    public ShoppingCart clickOnBuyNow() {
        commonAction.clickElement(BUY_NOW_BTN);
        logger.info("CLick on Buy Now button");
        commonAction.sleepInMiliSecond(2000);
        return new ShoppingCart(driver);
    }

    public ProductDetailPage accessToProductDetailPageByURL(String domain, String productID) {
        commonAction.navigateToURL(domain + "product/" + productID);
        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        commonAction.sleepInMiliSecond(3000);
        return this;
    }

    // BH_8887, BH_8888

    /**
     * Compare product price/currency on the SF with Dashboard
     */
    private void checkPrice(int listingPrice, int sellingPrice, String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        if (listingPrice != sellingPrice) {
            String actListingPrice = new UICommonAction(driver).getText(LISTING_PRICE).replace(",", "");
            countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + STORE_CURRENCY, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
        }

        String actSellingPrice = new UICommonAction(driver).getText(SELLING_PRICE).replace(",", "");
        int actSellingPriceValue = Integer.parseInt(actSellingPrice.replace(STORE_CURRENCY, ""));

        countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(actSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, actSellingPrice));
        logger.info("%s Check product price/ store currency show correctly".formatted(branch));
    }

    private List<String> getFlashSaleStatus() {
        // get flash sale status
        if (flashSaleStatus.get(0).equals("SCHEDULE")) {
            boolean checkStart = flashSaleStartTime.getEpochSecond() - Instant.now().getEpochSecond() <= 0;
            boolean checkEnd = flashSaleEndTime.getEpochSecond() - Instant.now().getEpochSecond() > 0;
            if (checkStart && checkEnd) if (isVariation)
                IntStream.range(0, flashSaleVariationList.size()).forEach(i -> flashSaleStatus.set(i, "IN-PROGRESS"));
            else Collections.fill(flashSaleStatus, "IN-PROGRESS");
        }
        return flashSaleStatus;
    }

    private Map<String, List<String>> getProductDiscountCampaignStatus() {
        // get flash sale status
        for (String branch : activeBranchName) {
            if (productDiscountCampaignStatus.get(branch).get(0).equals("SCHEDULE")) {
                boolean checkStart = productDiscountCampaignStartTime.getEpochSecond() - Instant.now().getEpochSecond() <= 0;
                boolean checkEnd = productDiscountCampaignEndTime.getEpochSecond() - Instant.now().getEpochSecond() > 0;
                if (checkStart && checkEnd)
                    productDiscountCampaignStatus.put(branch, IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> "IN-PROGRESS").collect(Collectors.toList()));
            }
        }
        return productDiscountCampaignStatus;
    }

    // check flash sale
    private void checkFlashSaleShouldBeShown(String branchName) throws IOException {
        String branch = "[Branch name: %s]".formatted(branchName);
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkElementVisible(FLASH_SALE_BADGE), "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
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

    public void checkProductDetailWhenAppliedPromotion(String flashSaleStatus, String productDiscountCampaignStatus, boolean wholesaleProductStatus, int listingPrice, int sellingPrice, int flashSalePrice, int discountCampaignCouponValue, int wholesaleProductStock, int wholesaleProductPrice, String branchName) throws IOException {
        if (flashSaleStatus.equals("IN-PROGRESS")) {
            // check flash sale badge is shown
            checkFlashSaleShouldBeShown(branchName);

            // check flash sale price
            checkPrice(listingPrice,
                    flashSalePrice,
                    branchName);
        }
        // when flash sale expired or schedule, check discount campaign if any
        else if (productDiscountCampaignStatus.equals("IN-PROGRESS")) {

            // check discount campaign is shown
            checkDiscountCampaignShouldBeShown(branchName);

            // get campaign price
            int salePrice = (productDiscountCouponType == 0)
                    ? Math.round(sellingPrice * (1 - ((float) discountCampaignCouponValue / 100)))
                    : (Math.max(0, sellingPrice - discountCampaignCouponValue));

            // check discount campaign price
            checkPrice(listingPrice,
                    salePrice,
                    branchName);
        }
        // in case, no discount campaign is in-progress
        else {
            // check wholesale product if any
            if (wholesaleProductStatus) {
                // check wholesale product is shown
                checkWholesaleProductShouldBeShown(branchName);

                // increase quantity to wholesale product minimum requirement
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(wholesaleProductStock));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check wholesale product price
                checkPrice(listingPrice,
                        wholesaleProductPrice,
                        branchName);
            }
            // in case no promotion applied
            else {
                // verify listing/selling price is shown as dashboard setting
                checkPrice(listingPrice, sellingPrice, branchName);
            }
        }
    }

    public ProductDetailPage checkPriceWithoutVariationProduct() throws IOException {
        // wait list branch visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // get promotion status
        List<String> flashSaleStatus = getFlashSaleStatus();
        Map<String, List<String>> productDiscountCampaignStatus = getProductDiscountCampaignStatus();

        // check flash sale for each branch
        for (WebElement element : BRANCH_NAME_LIST) {
            // switch branch
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // branch name
            String branchName = element.getText();

            // check product price
            checkProductDetailWhenAppliedPromotion(flashSaleStatus.get(0),
                    productDiscountCampaignStatus.get(branchName).get(0),
                    wholesaleProductStatus.get(0),
                    withoutVariationListingPrice,
                    withoutVariationSellingPrice,
                    flashSaleWithoutVariationPrice,
                    productDiscountCouponValue,
                    withoutVariationWholesaleProductStock,
                    withoutVariationWholesaleProductPrice,
                    branchName);
        }
        return this;
    }

    public ProductDetailPage checkPriceVariationProduct() throws IOException {
        // wait list branch visible
        commonAction.waitElementList(BRANCH_NAME_LIST);

        // get promotion status
        List<String> flashSaleStatus = getFlashSaleStatus();
        Map<String, List<String>> productDiscountCampaignStatus = getProductDiscountCampaignStatus();

        // verify on each variation
        for (String variationValue : variationList) {
            // get variation value
            String[] varName = variationValue.replace("|", " ").split(" ");

            // select variation
            Arrays.stream(varName).forEachOrdered(var -> LIST_VARIATION_VALUE.stream()
                    .filter(element -> ((JavascriptExecutor) driver)
                            .executeScript("return arguments[0].textContent", element)
                            .toString().equals(var))
                    .findFirst().ifPresent(element -> ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click()", element)));

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check product price for each branch
            for (WebElement element : BRANCH_NAME_LIST) {

                // switch branch
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // branch name
                String branchName = element.getText();

                // variation index
                int varIndex = variationList.indexOf(variationValue);

                // check product price
                checkProductDetailWhenAppliedPromotion(flashSaleStatus.get(varIndex),
                        productDiscountCampaignStatus.get(branchName).get(varIndex),
                        wholesaleProductStatus.get(varIndex),
                        variationListingPrice.get(varIndex),
                        variationSellingPrice.get(varIndex),
                        flashSaleVariationPrice.get(varIndex),
                        productDiscountCouponValue,
                        variationWholesaleProductStock.get(varIndex),
                        variationWholesaleProductPrice.get(varIndex),
                        branchName);
            }
        }
        return this;
    }

    // BH_8616

    /**
     * <p> Without variation product</p>
     * <p> actHide = true when remaining stock has been hidden</p>
     */
    private void checkStock(List<Integer> branchStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        // if in stock, check show/hide as setting
        if (Collections.max(branchStock) > 0 && branchListIsShownOnSF()) {
            // actHide = true when stock quantity does not show
            boolean isHideOnSF = !(STOCK_QUANTITY_IN_BRANCH.size() > 0);

            // isHideStock: setting on product information
            countFail = new AssertCustomize(driver).assertTrue(countFail, isHideOnSF == isHideStock, "[Failed] Remaining stock is hidden: %s but it is %s".formatted(isHideStock, isHideOnSF));
            logger.info("%s Check remaining stock is %s.".formatted(varName, isHideStock ? "hidden" : "shown"));
        } else logger.info("[Check stock] All branch out of stock");
    }

    private void checkBranch(List<Integer> branchStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";

        // check branch on online shop
        if ((Collections.max(branchStock) > 0) && branchListIsShownOnSF()) {

            // wait list branch visible
            commonAction.waitElementList(BRANCH_NAME_LIST);

            // SF branch list:
            List<String> sfBranchList = BRANCH_NAME_LIST.stream().map(WebElement::getText).toList();

            for (int i = 0; i < branchStock.size(); i++) {
                String branch = activeBranchName.get(i);
                if ((branchStock.get(i) > 0) && branchStatus.get(i).equals("SHOW")) {
                    countFail = new AssertCustomize(driver).assertTrue(countFail, sfBranchList.contains(branch), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(branch));
                    logger.info("%s Check branch '%s' is shown.".formatted(varName, branch));
                } else {
                    countFail = new AssertCustomize(driver).assertFalse(countFail, sfBranchList.contains(branch), "[Failed][Branch name: %s] Branch out of stock but is not hidden.".formatted(branch));
                    logger.info("%s Check branch '%s' is hidden.".formatted(varName, branch));
                }
            }
        } else logger.info("[Check branch] All branch out of stock");
    }

    public ProductDetailPage checkStockIsHiddenWithoutVariationProduct() throws IOException {
        checkStock(withoutVariationStock);
        checkBranch(withoutVariationStock);
        return this;
    }

    public ProductDetailPage checkStockIsHiddenVariationProduct() throws IOException {
        // wait list variation value visible
        commonAction.waitElementList(LIST_VARIATION_VALUE);

        // verify on each variation
        for (String variationValue : variationList) {
            // get variation value
            String[] varName = variationValue.replace("|", " ").split(" ");

            // select variation
            Arrays.stream(varName).forEachOrdered(var -> LIST_VARIATION_VALUE.stream()
                    .filter(element -> ((JavascriptExecutor) driver)
                            .executeScript("return arguments[0].textContent", element)
                            .toString().equals(var))
                    .findFirst().ifPresent(element -> ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].click()", element)));

            // wait spinner loading if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check stock and branch
            checkStock(variationStockQuantity.get(variationValue), variationValue);
            checkBranch(variationStockQuantity.get(variationValue), variationValue);
        }
        return this;
    }

    // BH_9536

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    private void checkVariationName(String branchName) throws IOException {
        List<String> variationNameList = LIST_VARIATION_NAME.stream().map(element -> element.getText().toLowerCase()).toList();

        countFail = new AssertCustomize(driver).assertEquals(countFail,
                variationNameList.toString(),
                variationMap.keySet().toString(),
                "[Failed][Branch name: %s] Variation name should be %s, but found %s.".formatted(branchName, variationMap.keySet(), variationNameList));
        logger.info("[Branch name: %s] Check product variation show correctly".formatted(branchName));
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    private void checkProductName(String branchName) throws IOException {
        // get product name on shop online
        String sfProductName = wait.until(visibilityOf(PRODUCT_NAME)).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfProductName, productName, "[Failed][Branch name: %s] Product name should be %s but found %s.".formatted(branchName, productName, sfProductName));

        logger.info("[Branch name: %s] Check product name.".formatted(branchName));
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    private void checkStockQuantity(int index, int stockQuantity) throws IOException {
        if (!isHideStock) {
            // wait list branch stock visible
            commonAction.waitElementList(STOCK_QUANTITY_IN_BRANCH);

            // get stock on shop online
            String sfStock = wait.until(visibilityOf(STOCK_QUANTITY_IN_BRANCH.get(index)))
                    .getText()
                    .replace("Còn hàng ", "")
                    .replace(" in stock", "").replace(",", "");
            countFail = new AssertCustomize(driver).assertEquals(countFail,
                    sfStock,
                    String.valueOf(stockQuantity),
                    "[Failed][Branch name: %s] Stock quantity should be %s, but found %s"
                            .formatted(BRANCH_NAME_LIST.get(index).getText(), stockQuantity, sfStock));

            logger.info("[Branch name: %s] Check current stock quantity".formatted(BRANCH_NAME_LIST.get(index).getText()));
        } else logger.info("[Check stock] Setting hide stock on StoreFront.");
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    private void checkSoldOutMark() throws IOException {
        boolean isSoldOut = SOLD_OUT_MARK.getText().equals("Hết hàng") || SOLD_OUT_MARK.getText().equals("Out of stock");
        countFail = new AssertCustomize(driver).assertTrue(countFail, isSoldOut, "[Failed] Sold out mark does not show");
        logger.info("Check Sold out mark should is shown");
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    private void check404Page() throws IOException {
        countFail = new AssertCustomize(driver).assertTrue(countFail, driver.getCurrentUrl().contains("404"), "[Failed] 404 is not shown although product out of stock.");
        logger.info("Check 404 page is shown when product out of stock.");
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    private void checkProductDescription(String branchName) throws IOException {
        String sfDescription = wait.until(visibilityOf(PRODUCT_DESCRIPTION)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail,
                sfDescription,
                productDescription,
                "[Failed][Branch name: %s] Product description should be '%s', but found '%s'"
                        .formatted(branchName, productDescription, sfDescription));
        logger.info("[Branch name: %s] Check product description is shown correctly.".formatted(branchName));
    }

    private void checkProductInformation(List<Integer> branchStock, int listingPrice, int sellingPrice) throws IOException {
        if ((Collections.max(branchStock) > 0) && branchListIsShownOnSF()) {
            // wait list branch visible
            commonAction.waitElementList(BRANCH_NAME_LIST);

            // check flash sale for each branch
            for (WebElement element : BRANCH_NAME_LIST) {
                // switch branch
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // branch name
                String branch = ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].textContent", element)
                        .toString();

                // check product name
                checkProductName(branch);

                // check product price
                checkPrice(listingPrice, sellingPrice, branch);

                // check variation name if any
                if (isVariation) checkVariationName(branch);

                // check branch stock quantity
                int index = BRANCH_NAME_LIST.indexOf(element);
                checkStockQuantity(index, branchStock.get(activeBranchName.indexOf(branch)));

                // check description
                checkProductDescription(branch);
            }
            checkBranch(branchStock);
        } else checkSoldOutMark();
    }

    /**
     * Verify all information on the SF is shown correctly (without variation product)
     */
    public ProductDetailPage checkWithoutVariationProductInformation() throws IOException {
        int maxStock = Collections.max(withoutVariationStock);
        if ((maxStock > 0) || isDisplayOutOfStock) {
            checkProductInformation(withoutVariationStock,
                    withoutVariationListingPrice,
                    withoutVariationSellingPrice);
        } else check404Page();
        return this;
    }

    /**
     * Verify all information on the SF is shown correctly (variation product)
     */
    public ProductDetailPage checkVariationProductInformation() throws IOException {
        int maxStock = Collections.max(variationStockQuantity.values().stream().map(Collections::max).toList());

        if ((maxStock > 0) || isDisplayOutOfStock) {
            // verify on each variation
            for (String variationValue : variationList) {
                // get variation value
                String[] varName = variationValue.replace("|", " ").split(" ");

                // wait list variation value is visible
                int varNum = variationMap.values().stream().map(List::size).toList()
                        .stream().mapToInt(Integer::intValue).sum();
                commonAction.waitElementList(LIST_VARIATION_VALUE, varNum);

                // select variation
                Arrays.stream(varName).forEachOrdered(var -> LIST_VARIATION_VALUE.stream()
                        .filter(element -> ((JavascriptExecutor) driver)
                                .executeScript("return arguments[0].textContent", element)
                                .toString().equals(var))
                        .findFirst().ifPresent(element -> ((JavascriptExecutor) driver)
                                .executeScript("arguments[0].click()", element)));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                int varIndex = variationList.indexOf(variationValue);

                // check product information
                checkProductInformation(variationStockQuantity.get(variationValue),
                        variationListingPrice.get(varIndex),
                        variationSellingPrice.get(varIndex));
            }
        } else check404Page();
        return this;
    }

    public boolean branchListIsShownOnSF() {
        // Call API and get Branch setting info
        new BranchManagement().getBranchInformation();

        // get branch status
        branchStatus = activeBranchIDList.stream().mapToInt(brID -> brID).mapToObj(id -> (isHideOnStoreFront.get(branchID.indexOf(id)) != null)
                && isHideOnStoreFront.get(branchID.indexOf(id)) ? "HIDE" : "SHOW").collect(Collectors.toList());

        // check = false: hide all branches
        // check = true: show at least 1 branch
        boolean check = false;
        if (isVariation) {
            for (String var : variationStockQuantity.keySet()) {
                List<Integer> branchStock = variationStockQuantity.get(var);
                check = IntStream.range(0, branchStock.size()).anyMatch(i -> (branchStock.get(i) > 0) && branchStatus.get(i).equals("SHOW"));
                break;
            }
        } else {
            check = IntStream.range(0, withoutVariationStock.size()).anyMatch(i -> (withoutVariationStock.get(i) > 0) && branchStatus.get(i).equals("SHOW"));
        }
        return check;
    }
}
