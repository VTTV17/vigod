package pages.buyerapp.productDetail;

import api.dashboard.onlineshop.Preferences;
import api.dashboard.products.ProductInformation;
import api.dashboard.promotion.CreatePromotion;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.shopcart.BuyerShopCartPage;
import pages.dashboard.products.all_products.ProductPage;
import utilities.UICommonMobile;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.promotion.DiscountCampaignInfo;
import utilities.model.dashboard.promotion.FlashSaleInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BuyerProductDetailPage extends BuyerProductDetailElement {
    WebDriver driver;
    WebDriverWait wait;
    Logger logger = LogManager.getLogger(BuyerProductDetailPage.class);

    UICommonMobile commonMobile;
    ProductInfo productInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
    FlashSaleInfo flashSaleInfo;
    DiscountCampaignInfo discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    int countFail;
    List<Boolean> branchStatus;
    boolean isEnableListingProduct;
    Map<String, List<String>> salePriceMap;
    Map<String, List<String>> saleDisplayMap;

    public BuyerProductDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        commonMobile = new UICommonMobile(driver);
    }

    /**
     * <p>Branch status:</p>
     * <p>status = true: show branch on storefront/buyer app if in-stock</p>
     * <p>status = false: hide branch on storefront/buyer app</p>
     */
    List<Boolean> getBranchStatus() {
        // return branch status
        return IntStream.range(0, brInfo.getAllBranchStatus().size()).mapToObj(i -> !brInfo.getIsHideOnStoreFront().get(i) && brInfo.getAllBranchStatus().get(i).equals("ACTIVE")).toList();
    }

    /**
     * Map: branch name, list of price type
     * <p>Ex: Product has variation var1, var2, var3, var4. And branch A, B</p>
     * <p>This function return list price type of each variation on each branch</p>
     * <p>Branch A = {FLASH SALE, WHOLESALE PRODUCT, WHOLESALE PRODUCT, SELLING PRICE} </p>
     * <p>Branch B = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN} </p>
     * <p>Branch C = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, SELLING PRICE} </p>
     */
    Map<String, List<String>> getSalePriceMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size()).mapToObj(i -> switch (flashSaleInfo.getFlashSaleStatus().get(brName).get(i)) {
            case "IN_PROGRESS" -> "FLASH SALE";
            case "SCHEDULED" ->
                    (!discountCampaignInfo.getDiscountCampaignStatus().get(brName).get(i).equals("IN_PROGRESS")) && (wholesaleProductInfo.getStatusMap().get(brName).get(i)) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
            default ->
                    discountCampaignInfo.getDiscountCampaignStatus().get(brName).get(i).equals("IN_PROGRESS") ? "DISCOUNT CAMPAIGN" : wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
        }).toList(), (a, b) -> b));
    }

    Map<String, List<String>> getSaleDisplayMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size()).mapToObj(i -> switch (flashSaleInfo.getFlashSaleStatus().get(brName).get(i)) {
            case "IN_PROGRESS", "SCHEDULED" -> "FLASH SALE";
            default ->
                    discountCampaignInfo.getDiscountCampaignStatus().get(brName).get(i).equals("IN_PROGRESS") ? "DISCOUNT CAMPAIGN" : wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
        }).toList(), (a, b) -> b));
    }

    void checkProductName(String barcode, String language) {
        // get product name on dashboard
        String dbProductName = StringUtils.capitalize(productInfo.getProductNameMap().get(barcode).get(language));

        // get product name on shop online
        String sfProductName = commonMobile.moveAndGetElement(PRODUCT_NAME, PRODUCT_NAME).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertTrue(countFail, sfProductName.equals(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    void checkPriceOnBranch(long listingPrice, long sellingPrice, String brName) throws IOException {
        String branch = brName.equals("") ? "" : "[Branch name: %s]".formatted(brName);

        if (listingPrice != sellingPrice) {
            String adrListingPrice = driver.findElement(ADD_TO_CART_POPUP_LISTING_PRICE).getText().replaceAll("\\D+", "");
            long adrListingPriceValue = Long.parseLong(adrListingPrice);
            countFail = new AssertCustomize(driver).assertEquals(countFail, adrListingPriceValue, listingPrice, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, adrListingPriceValue));
        } else logger.info("No discount product (listing price = selling price)");
        String adrSellingPrice = driver.findElement(ADD_TO_CART_POPUP_SELLING_PRICE).getText().replaceAll("\\D+", "");
        long adrSellingPriceValue = Long.parseLong(adrSellingPrice);

        countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(adrSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, adrSellingPrice));
        logger.info("%s Check product price/ store currency show correctly".formatted(branch));

    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasFlashSale = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, FLASH_SALE_BADGE);
        } catch (NoSuchElementException ex) {
            hasFlashSale = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, hasFlashSale, "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }

    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasDiscountCampaign = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, DISCOUNT_CAMPAIGN_BADGE);
        } catch (NoSuchElementException ex) {
            hasDiscountCampaign = false;
        }

        countFail = new AssertCustomize(driver).assertTrue(countFail, hasDiscountCampaign, "%s Discount campaign does not show".formatted(branch));
        logger.info("%s Check discount campaign is shown".formatted(branch));
    }

    // check wholesale product price
    void checkWholesaleProductShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean check = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, WHOLE_SALE_PRODUCT_BADGE);
        } catch (NoSuchElementException ex) {
            check = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(productInfo.getVariationNameMap().get(language).split("\\|")).toList();
        List<String> variationNameListAndroid = commonMobile.getListElementText(PRODUCT_NAME, VARIATION_NAME_LIST);

        countFail = new AssertCustomize(driver).assertTrue(countFail, variationNameListAndroid.toString().equals(variationNameListDB.toString()), "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListAndroid));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check Search icon is shown or not
        boolean checkSearchBox = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, SEARCH_BRANCH_ICON).click();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkSearchBox, "[Failed]%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Search box' is displayed.".formatted(varName));

        // check Filter icon
        boolean checkFilter = true;
        try {
            // check filter branch icon is shown or not
            commonMobile.moveAndGetElement(PRODUCT_NAME, FILTER_BRANCH_ICON);
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is displayed.".formatted(varName));
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check Search icon is shown or not
        boolean checkSearchBox = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, SEARCH_BRANCH_ICON).click();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, checkSearchBox, "[Failed]%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Search box' is hidden.".formatted(varName));

        // check Filter icon
        boolean checkFilter = true;
        try {
            commonMobile.moveAndGetElement(PRODUCT_NAME, FILTER_BRANCH_ICON);
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is hidden.".formatted(varName));
    }

    void checkBranchNameAndStock(String brElementText, boolean brStatus, int brStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch information
        // check branch name
        String adrBranchName = brElementText.split(" - ")[0];
        countFail = new AssertCustomize(driver).assertTrue(countFail, brInfo.getBranchName().contains(adrBranchName) && brStatus && (brStock > 0), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(adrBranchName));

        if (!productInfo.isHideStock() & brStatus) {
            // check branch stock
            int adrBranchStock = Integer.parseInt(brElementText.split(" - ")[1].replaceAll("\\D+", ""));
            countFail = new AssertCustomize(driver).assertEquals(countFail, adrBranchStock, brStock, "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, adrBranchName, brStock, adrBranchStock));
        } else logger.info("Setting hide stock.");
        logger.info("%s Check branch '%s'".formatted(varName, adrBranchName));
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    void checkProductDescription(String barcode, String language) {
        // get dashboard product description
        String dbDescription = productInfo.getProductDescriptionMap().get(barcode).get(language).replaceAll("<.*?>", "").replaceAll("amp;", "");

        // get SF product description
        if (dbDescription.length() > 1) {
            String adrDescription = commonMobile.moveAndGetElement(PRODUCT_NAME, PRODUCT_DESCRIPTION_CONTENT).getText();
            countFail = new AssertCustomize(driver).assertTrue(countFail, adrDescription.equals(dbDescription), "[Failed][Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, adrDescription));
        }
        logger.info("[Check description] Check product description is shown correctly.");
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    void checkSoldOutMark(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        boolean isSoldOut = commonMobile.moveAndGetElement(PRODUCT_NAME, SOLD_OUT_MARK) != null;
        countFail = new AssertCustomize(driver).assertTrue(countFail, isSoldOut, "[Failed]%s Sold out mark does not show".formatted(varName));
        logger.info("%s Check 'SOLD OUT' mark is shown".formatted(varName));
    }

    void checkBuyNowAddToCartAndContactNowBtn(String... variationName) {
        String varName = (variationName.length > 0) ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(new Preferences().isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            boolean checkBuyNow = commonMobile.moveAndGetElement(PRODUCT_NAME, BUY_NOW_BTN) != null;

            countFail = new AssertCustomize(driver).assertTrue(countFail, checkBuyNow, "[Failed]%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Buy Now' button is displayed.".formatted(varName));

            // check Add to cart button is shown
            boolean checkAddToCart = commonMobile.moveAndGetElement(PRODUCT_NAME, ADD_TO_CART_ICON) != null;
            countFail = new AssertCustomize(driver).assertTrue(countFail, checkAddToCart, "[Failed]%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Add to cart' button is displayed.".formatted(varName));

        } else {
            // check Contact Now button is shown
            boolean isContactNow = commonMobile.moveAndGetElement(PRODUCT_NAME, CONTACT_NOW_BTN) != null;

            countFail = new AssertCustomize(driver).assertTrue(countFail, isContactNow, "[Failed]%s 'Contact Now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Contact Now' button is shown.".formatted(varName));
        }
    }

    void checkVariationPrice(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, String brName) throws IOException {
        String priceType = salePriceMap.get(brName).get(varIndex);
        System.out.println("price: " + priceType);
        String displayType = saleDisplayMap.get(brName).get(varIndex);
        System.out.println("display: " + displayType);

        // check badge
        switch (displayType) {
            // check flash sale badge is shown
            case "FLASH SALE" -> checkFlashSaleShouldBeShown(brName);
            // check discount campaign is shown
            case "DISCOUNT CAMPAIGN" -> checkDiscountCampaignShouldBeShown(brName);
            // check wholesale product is shown
            case "WHOLESALE PRODUCT" -> checkWholesaleProductShouldBeShown(brName);
        }

        if (!(isEnableListingProduct && productInfo.isEnabledListing())) {
            // open add to cart popup
            commonMobile.moveAndGetElement(PRODUCT_NAME, ADD_TO_CART_ICON).click();

            // check price
            switch (priceType) {
                // check flash sale price
                case "FLASH SALE" -> checkPriceOnBranch(sellingPrice, flashSalePrice, brName);
                // check discount campaign price
                case "DISCOUNT CAMPAIGN" -> {
                    driver.findElement(ADD_TO_CART_POPUP_BUY_IN_BULK_CHECKBOX).click();
                    checkPriceOnBranch(sellingPrice, productDiscountCampaignPrice, brName);
                }
                case "WHOLESALE PRODUCT" -> {
                    // increase quantity to wholesale product minimum requirement
                    driver.findElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).clear();
                    driver.findElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).sendKeys(String.valueOf(wholesaleProductStock));

                    // check wholesale product price
                    checkPriceOnBranch(sellingPrice, wholesaleProductPrice, brName);
                }
                default -> checkPriceOnBranch(listingPrice, sellingPrice, brName);
            }

            // close add to cart popup
            commonMobile.tapByCoordinatesInPercent(0.5, 0.5);
        }
    }

    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String language, String... variationName) throws IOException {
        // log
        if (variationName.length > 0)
            if (variationName[0] != null) logger.info("*** var: %s ***".formatted(variationName[0]));

        // check product name
        checkProductName(productInfo.getBarcodeList().get(varIndex), language);

        // count all branches display
        int numberOfDisplayBranches = Collections.frequency(IntStream.range(0, branchStatus.size()).mapToObj(brIndex -> branchStatus.get(brIndex) && (branchStock.get(brIndex) > 0)).toList(), true);

        // check branch information
        if (numberOfDisplayBranches > 0) {
            // check filter/search branch is shown when available branches >= 6
            if (numberOfDisplayBranches >= 6) checkFilterAndSearchBranchIsShown(variationName);
            else checkFilterAndSearchBranchIsHidden(variationName);

            // get current branch name list
            List<String> currentBranchNameList = commonMobile.getListElementText(PRODUCT_NAME, BRANCH_LIST);
            System.out.println(currentBranchNameList);
            for (String brName : currentBranchNameList) {
                // switch branch
                int index = commonMobile.moveAndGetElement(PRODUCT_NAME, BRANCH_LIST, brName);
                driver.findElements(BRANCH_LIST).get(index).click();

                // check branch name, branch stock, branch price
                // get branch index in branch information
                int brIndex = brInfo.getBranchName().indexOf(brName.split(" - ")[0]);
                checkBranchNameAndStock(brName, branchStatus.get(brIndex), branchStock.get(brIndex), variationName);

                // check product price
                checkVariationPrice(varIndex, listingPrice, sellingPrice, flashSalePrice, productDiscountCampaignPrice, wholesaleProductStock, wholesaleProductPrice, brName.split(" - ")[0]);
            }

        } else checkSoldOutMark(variationName);

        // check description
        checkProductDescription(productInfo.getBarcodeList().get(varIndex), language);

        // check Buy Now and Add To Cart button is shown
        checkBuyNowAddToCartAndContactNowBtn(variationName);
    }

    /**
     * Verify all information on the SF is shown correctly
     */
    void checkProductInformation(String language) throws IOException {
        // get branch information
        brInfo = new BranchManagement().getInfo();

        // get wholesale config
        if (!productInfo.isDeleted()) wholesaleProductInfo = new ProductInformation().wholesaleProductInfo(productInfo);

        // get flash sale, discount campaign information
        CreatePromotion promotion = new CreatePromotion();
        flashSaleInfo = promotion.getFlashSaleInfo(productInfo.getBarcodeList(), productInfo.getProductSellingPrice());
        discountCampaignInfo = promotion.getDiscountCampaignInfo(productInfo.getBarcodeList(), productInfo.getProductSellingPrice());
        // get sale price map and display
        salePriceMap = getSalePriceMap();
        saleDisplayMap = getSaleDisplayMap();

        // get listing price setting
        isEnableListingProduct = new Preferences().isEnabledListingProduct();

        // get branch status
        branchStatus = getBranchStatus();

        // check variation name if any
        if (productInfo.isHasModel()) checkVariationName(language);

        // verify on each variation
        for (String barcode : productInfo.getBarcodeList()) {
            // variation index
            int varIndex = productInfo.getBarcodeList().indexOf(barcode);

            // variation value
            String variationValue = productInfo.getVariationListMap().get(language).get(varIndex);

            // ignore if variation inactive
            if (productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) {
                // switch variation if any
                if (productInfo.isHasModel()) {
                    for (String var : variationValue.split("\\|")) {
                        int index = commonMobile.moveAndGetElement(PRODUCT_NAME, VARIATION_VALUE_LIST, var);
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(VARIATION_VALUE_LIST)).get(index).click();
                    }
                }

                // check product information
                checkVariationInformation(varIndex,
                        productInfo.getProductListingPrice().get(varIndex),
                        productInfo.getProductSellingPrice().get(varIndex),
                        flashSaleInfo.getFlashSalePrice().get(varIndex),
                        discountCampaignInfo.getDiscountCampaignPrice().get(varIndex),
                        wholesaleProductInfo.getStockList().get(varIndex),
                        wholesaleProductInfo.getPriceList().get(varIndex),
                        productInfo.getProductStockQuantityMap().get(barcode),
                        language,
                        variationValue);
            }
        }

    }

    void searchAndNavigateToProductDetail(String language, ProductInfo productInfo) {
        new NavigationBar(driver).tapOnHomeIcon()
                .waitHomepageLoaded()
                .searchProductByName(productInfo, language)
                .navigateToProductDetailPage();
    }

    /**
     * Access to product detail on SF by URL
     */
    public void openProductDetailScreenAndCheckProductInformation(String language, ProductInfo productInfo) throws Exception {
        // get product information
        this.productInfo = productInfo;

        // open product detail screen
        searchAndNavigateToProductDetail(language, productInfo);

        // convert language to languageCode
        String languageCode = language.equals("VIE") || language.equals("vi") ? "vi" : "en";

        // get max stock
        int maxStock = productInfo.isDeleted() ? 0 : Collections.max(productInfo.getProductStockQuantityMap().values().stream().map(Collections::max).toList());
        storeInfo = new StoreInformation().getInfo();

        // check product is display or not
        if (!productInfo.isDeleted() && productInfo.isOnApp() && productInfo.getBhStatus().equals("ACTIVE") && (maxStock > 0 || productInfo.isShowOutOfStock())) {
            if (storeInfo.getSFLangList().contains(languageCode))
                checkProductInformation(languageCode);
            else logger.info("'%s' language is not published, please publish it and try again.".formatted(language));
        }

        // complete verify
        if (countFail + new ProductPage(driver).getCountFail() > 0) {
            int count = countFail + new ProductPage(driver).getCountFail();
            countFail = 0;
            new ProductPage(driver).setCountFail();
            Assert.fail("[Failed] Fail %d cases".formatted(count));
        }
    }

    public BuyerShopCartPage buyNowProduct(int quantity) {
        commonMobile.clickElement(BUY_NOW_BTN);
        if (!commonMobile.getText(BUY_NOW_POPUP_QUANTITY_TEXT_BOX).equals(String.valueOf(quantity))) {
            commonMobile.inputText(BUY_NOW_POPUP_QUANTITY_TEXT_BOX, String.valueOf(quantity));
        }
        commonMobile.clickElement(BUY_NOW_POPUP_BUY_BTN);
        return new BuyerShopCartPage(driver).waitLoadingDisapear();
    }

    public BuyerProductDetailPage addToCart(int quantity) {
        commonMobile.clickElement(ADD_TO_CART_ICON);
        if (!commonMobile.getText(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).equals(String.valueOf(quantity))) {
            commonMobile.inputText(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX, String.valueOf(quantity));
        }
        commonMobile.clickElement(ADD_TO_CART_POPUP_ADD_BTN);
        logger.info("Add product to cart");
        return this;
    }

    public BuyerShopCartPage tapOnShoppingCart() {
        commonMobile.clickElement(CART_ICON);
        logger.info("Tap on Shopping cart icon.");
        return new BuyerShopCartPage(driver);
    }

    public BuyerShopCartPage addProductToCartAndGoToShoppingCart(int quantity) {
        addToCart(quantity);
        commonMobile.sleepInMiliSecond(3000);
        tapOnShoppingCart();
        return new BuyerShopCartPage(driver).waitLoadingDisapear();
    }

    public BuyerShopCartPage goToShopCartByBackIcon() {
        new BuyerGeneral(driver).clickOnBackIcon().tapCancelSearch();
        new NavigationBar(driver).tapOnCartIcon();
        return new BuyerShopCartPage(driver);
    }
}
