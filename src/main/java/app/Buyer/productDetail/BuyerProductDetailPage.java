package app.Buyer.productDetail;

import api.Seller.customers.Customers;
import api.Seller.onlineshop.Preferences;
import api.Seller.products.all_products.ProductInformation;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.FlashSale.FlashSaleInfo;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.promotion.ProductDiscountCampaign.BranchDiscountCampaignInfo;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.shopcart.BuyerShopCartPage;
import utilities.commons.UICommonMobile;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.util.*;
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
    Map<String, BranchDiscountCampaignInfo> discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    List<Boolean> branchStatus;
    boolean isEnableListingProduct;
    Map<String, List<String>> salePriceMap;
    Map<String, List<String>> saleDisplayMap;
    LoginInformation loginInformation;
    AssertCustomize assertCustomize;

    public BuyerProductDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        commonMobile = new UICommonMobile(driver);
        assertCustomize = new AssertCustomize(driver);
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
                    ((discountCampaignInfo.get(brName) == null) && (wholesaleProductInfo.getStatusMap().get(brName).get(i))) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
            default ->
                    (discountCampaignInfo.get(brName) != null) ? "DISCOUNT CAMPAIGN" : (wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE");
        }).toList(), (a, b) -> b));
    }

    Map<String, List<String>> getSaleDisplayMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size()).mapToObj(i -> switch (flashSaleInfo.getFlashSaleStatus().get(brName).get(i)) {
            case "IN_PROGRESS", "SCHEDULED" -> "FLASH SALE";
            default ->
                    (discountCampaignInfo.get(brName) != null) ? "DISCOUNT CAMPAIGN" : (wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE");
        }).toList(), (a, b) -> b));
    }

    void checkProductName(String barcode, String language) {
        // get product name on dashboard
        String dbProductName = StringUtils.capitalize(productInfo.getProductNameMap().get(barcode).get(language));

        // get product name on shop online
        String adrProductName = commonMobile.moveAndGetElement(PRODUCT_NAME).getText();

        // check product name
        assertCustomize.assertTrue(adrProductName.equalsIgnoreCase(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, adrProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    void checkSellingPriceOnBranch(long sellingPrice, String brName){
        String branch = "[Branch name: %s]".formatted(brName);
        String adrSellingPrice = wait.until(ExpectedConditions.presenceOfElementLocated(ADD_TO_CART_POPUP_SELLING_PRICE)).getText().replaceAll("\\D+", "");
        long adrSellingPriceValue = Long.parseLong(adrSellingPrice);

        assertCustomize.assertTrue(Math.abs(adrSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, adrSellingPrice));
        logger.info("%s Check product price/ store currency show correctly".formatted(branch));

    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasFlashSale = true;
        try {
            commonMobile.moveAndGetElement(FLASH_SALE_BADGE);
        } catch (NoSuchElementException ex) {
            hasFlashSale = false;
        }
        assertCustomize.assertTrue(hasFlashSale, "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }

    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasDiscountCampaign = true;
        try {
            commonMobile.moveAndGetElement(DISCOUNT_CAMPAIGN_BADGE);
        } catch (NoSuchElementException ex) {
            hasDiscountCampaign = false;
        }

        assertCustomize.assertTrue(hasDiscountCampaign, "%s Discount campaign does not show".formatted(branch));
        logger.info("%s Check discount campaign is shown".formatted(branch));
    }

    // check wholesale product price
    void checkWholesaleProductShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean check = true;
        try {
            commonMobile.moveAndGetElement(WHOLE_SALE_PRODUCT_BADGE);
        } catch (NoSuchElementException ex) {
            check = false;
        }
        assertCustomize.assertTrue(check, "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(productInfo.getVariationNameMap().get(language).split("\\|")).toList();
        List<String> variationNameListAndroid = commonMobile.getListElementText(VARIATION_NAME_LIST);

        assertCustomize.assertTrue(variationNameListAndroid.toString().equals(variationNameListDB.toString()), "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListAndroid));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check Search icon is shown or not
        boolean checkSearchBox = true;
        try {
            commonMobile.moveAndGetElement(SEARCH_BRANCH_ICON).click();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }
        assertCustomize.assertTrue(checkSearchBox, "[Failed]%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Search box' is displayed.".formatted(varName));

        // check Filter icon
        boolean checkFilter = true;
        try {
            // check filter branch icon is shown or not
            commonMobile.moveAndGetElement(FILTER_BRANCH_ICON);
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }
        assertCustomize.assertTrue(checkFilter, "[Failed]%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is displayed.".formatted(varName));
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check Search icon is shown or not
        boolean checkSearchBox = true;
        try {
            commonMobile.moveAndGetElement(SEARCH_BRANCH_ICON).click();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }

        assertCustomize.assertFalse(checkSearchBox, "[Failed]%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Search box' is hidden.".formatted(varName));

        // check Filter icon
        boolean checkFilter = true;
        try {
            commonMobile.moveAndGetElement(FILTER_BRANCH_ICON);
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }

        assertCustomize.assertFalse(checkFilter, "[Failed]%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is hidden.".formatted(varName));
    }

    void checkBranchNameAndStock(String brElementText, boolean brStatus, int brStock, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch information
        // check branch name
        String adrBranchName = brElementText.split(" - ")[0];
        assertCustomize.assertTrue(brInfo.getBranchName().contains(adrBranchName) && brStatus && (brStock > 0), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(adrBranchName));

        if (!productInfo.isHideStock() & brStatus) {
            // check branch stock
            int adrBranchStock = Integer.parseInt(brElementText.split(" - ")[1].replaceAll("\\D+", ""));
            assertCustomize.assertEquals(adrBranchStock, brStock, "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, adrBranchName, brStock, adrBranchStock));
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
            String adrDescription = commonMobile.moveAndGetElement(PRODUCT_DESCRIPTION_CONTENT).getText();
            assertCustomize.assertTrue(adrDescription.equals(dbDescription), "[Failed][Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, adrDescription));
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
        boolean isSoldOut = commonMobile.moveAndGetElement(SOLD_OUT_MARK) != null;
        assertCustomize.assertTrue(isSoldOut, "[Failed]%s Sold out mark does not show".formatted(varName));
        logger.info("%s Check 'SOLD OUT' mark is shown".formatted(varName));
    }

    void checkBuyNowAddToCartAndContactNowBtn(String... variationName) {
        String varName = (variationName.length > 0) ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            boolean checkBuyNow = commonMobile.moveAndGetElement(BUY_NOW_BTN) != null;

            assertCustomize.assertTrue(checkBuyNow, "[Failed]%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Buy Now' button is displayed.".formatted(varName));

            // check Add to cart button is shown
            boolean checkAddToCart = commonMobile.moveAndGetElement(ADD_TO_CART_ICON) != null;
            assertCustomize.assertTrue(checkAddToCart, "[Failed]%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Add to cart' button is displayed.".formatted(varName));

        } else {
            // check Contact Now button is shown
            boolean isContactNow = commonMobile.moveAndGetElement(CONTACT_NOW_BTN) != null;

            assertCustomize.assertTrue(isContactNow, "[Failed]%s 'Contact Now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Contact Now' button is shown.".formatted(varName));
        }
    }

    Long getDiscountCampaignPrice(long sellingPrice, String brName) {
        List<Integer> listOfMinimumRequirements = discountCampaignInfo.get(brName).getListOfMinimumRequirements();
        int minRequirement = Collections.min(listOfMinimumRequirements);
        List<Integer> indexOfAllMinRequirements = IntStream.range(0, listOfMinimumRequirements.size()).filter(index -> listOfMinimumRequirements.get(index).equals(minRequirement)).boxed().toList();

        String couponType = discountCampaignInfo.get(brName).getListOfCouponTypes().get(indexOfAllMinRequirements.get(0));
        long couponValue = discountCampaignInfo.get(brName).getListOfCouponValues().get(indexOfAllMinRequirements.get(0));
        long productDiscountCampaignPrice = couponType.equals("FIXED_AMOUNT")
                ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                : ((sellingPrice * (100 - couponValue)) / 100);

        if (indexOfAllMinRequirements.size() > 1) {
            for (int index = 1; index < indexOfAllMinRequirements.size(); index++) {
                couponType = discountCampaignInfo.get(brName).getListOfCouponTypes().get(index);
                couponValue = discountCampaignInfo.get(brName).getListOfCouponValues().get(index);
                productDiscountCampaignPrice = Math.min(productDiscountCampaignPrice, couponType.equals("FIXED_AMOUNT")
                        ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                        : ((sellingPrice * (100 - couponValue)) / 100));
            }
        }
        return productDiscountCampaignPrice;
    }

    void checkVariationPrice(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, int wholesaleProductStock, long wholesaleProductPrice, String brName) {
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
            // check listing price
            if (!Objects.equals(sellingPrice, listingPrice)) {
                String adrListingPrice = commonMobile.moveAndGetElement(LISTING_PRICE).getText().replaceAll("\\D+", "");
                long adrListingPriceValue = Long.parseLong(adrListingPrice);
                assertCustomize.assertEquals(adrListingPriceValue, listingPrice, "[Failed]%s Listing price should be show %s instead of %s".formatted(brName, listingPrice, adrListingPriceValue));
            } else logger.info("No discount product (listing price = selling price)");

            // open add to cart popup
            commonMobile.moveAndGetElement(ADD_TO_CART_ICON).click();

            // check selling price
            switch (priceType) {
                // check flash sale price
                case "FLASH SALE" -> checkSellingPriceOnBranch(flashSalePrice, brName);
                // check discount campaign price
                case "DISCOUNT CAMPAIGN" -> {
                    driver.findElement(ADD_TO_CART_POPUP_BUY_IN_BULK_CHECKBOX).click();
                    checkSellingPriceOnBranch(getDiscountCampaignPrice(sellingPrice, brName), brName);
                }
                case "WHOLESALE PRODUCT" -> {
                    // increase quantity to wholesale product minimum requirement
                    driver.findElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).clear();
                    driver.findElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).sendKeys(String.valueOf(wholesaleProductStock));

                    // check wholesale product price
                    checkSellingPriceOnBranch(wholesaleProductPrice, brName);
                }
                default -> checkSellingPriceOnBranch(sellingPrice, brName);
            }

            // close add to cart popup
            commonMobile.tapByCoordinatesInPercent(0.5, 0.5);
        }
    }

    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String language, String... variationName) {
        // log
        if (variationName.length > 0)
            if (variationName[0] != null) logger.info("*** var: %s ***".formatted(variationName[0]));

        // check product name
        checkProductName(productInfo.getVariationModelList().get(varIndex), language);

        // count all branches display
        int numberOfDisplayBranches = Collections.frequency(IntStream.range(0, branchStatus.size()).mapToObj(brIndex -> branchStatus.get(brIndex) && (branchStock.get(brIndex) > 0)).toList(), true);

        // check branch information
        if (numberOfDisplayBranches > 0) {
            // check filter/search branch is shown when available branches >= 6
            if (numberOfDisplayBranches >= 6) checkFilterAndSearchBranchIsShown(variationName);
            else checkFilterAndSearchBranchIsHidden(variationName);

            // get current branch name list
            logger.info("Get list branches.");
            List<String> currentBranchNameList = commonMobile.getListElementText(BRANCH_LIST);

            for (String brName : currentBranchNameList) {
                // switch branch
                commonMobile.moveAndGetOverlappedElementByText(brName, ITEM_DETAIL_FOOTER).click();

                // check branch name, branch stock, branch price
                // get branch index in branch information
                int brIndex = brInfo.getBranchName().indexOf(brName.split(" - ")[0]);
                checkBranchNameAndStock(brName, branchStatus.get(brIndex), branchStock.get(brIndex), variationName);

                // check product price
                checkVariationPrice(varIndex, listingPrice, sellingPrice, flashSalePrice, wholesaleProductStock, wholesaleProductPrice, brName.split(" - ")[0]);
            }

        } else checkSoldOutMark(variationName);

        // check description
        checkProductDescription(productInfo.getVariationModelList().get(varIndex), language);

        // check Buy Now and Add To Cart button is shown
        checkBuyNowAddToCartAndContactNowBtn(variationName);
    }

    /**
     * Verify all information on the SF is shown correctly
     */
    void checkProductInformation(String language, int customerId) {
        // get list segment of customer
        List<Integer> listSegmentOfCustomer = new Customers(loginInformation).getListSegmentOfCustomer(customerId);

        // get wholesale config
        if (!productInfo.isDeleted()) wholesaleProductInfo = new ProductInformation(loginInformation).wholesaleProductInfo(productInfo, listSegmentOfCustomer);

        // get flash sale, discount campaign information
        flashSaleInfo = new FlashSale(loginInformation).getFlashSaleInfo(productInfo.getVariationModelList(), productInfo.getProductSellingPrice());
        discountCampaignInfo = new ProductDiscountCampaign(loginInformation).getAllDiscountCampaignInfo(productInfo, listSegmentOfCustomer);

        // get sale price map and display
        salePriceMap = getSalePriceMap();
        saleDisplayMap = getSaleDisplayMap();

        // get listing price setting
        isEnableListingProduct = new Preferences(loginInformation).isEnabledListingProduct();

        // check variation name if any
        if (productInfo.isHasModel()) checkVariationName(language);

        // verify on each variation
        List<String> variationModelList = productInfo.getVariationModelList();
        for (int varIndex = 0; varIndex < variationModelList.size(); varIndex++) {
            String modelId = variationModelList.get(varIndex);

            // variation value
            String variationValue = productInfo.getVariationListMap().get(language).get(varIndex);

            // ignore if variation inactive
            if (productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) {
                // switch variation if any
                if (productInfo.isHasModel())
                    Arrays.stream(variationValue.split("\\|")).forEachOrdered(var -> commonMobile.moveAndGetOverlappedElementByText(var, ITEM_DETAIL_FOOTER).click());

                // check product information
                checkVariationInformation(varIndex,
                        productInfo.getProductListingPrice().get(varIndex),
                        productInfo.getProductSellingPrice().get(varIndex),
                        flashSaleInfo.getFlashSalePrice().get(varIndex),
                        wholesaleProductInfo.getStockList().get(varIndex),
                        wholesaleProductInfo.getPriceList().get(varIndex),
                        productInfo.getProductStockQuantityMap().get(modelId),
                        language,
                        variationValue);
            }
        }

    }

    void searchAndNavigateToProductDetail(String language, ProductInfo productInfo) {
        NavigationBar nav = new NavigationBar(driver);
        nav.tapOnAccountIcon().changeLanguage(language);
        nav.tapOnHomeIcon().waitHomepageLoaded()
                .searchProductByName(productInfo, language)
                .navigateToProductDetailPage();
    }

    /**
     * Access to product detail on SF by URL
     */
    public void openProductDetailScreenAndCheckProductInformation(LoginInformation loginInformation, String language, ProductInfo productInfo, int customerId) {
        // get login information
        this.loginInformation = loginInformation;

        // get product information
        this.productInfo = productInfo;

        // get branch information
        brInfo = new BranchManagement(loginInformation).getInfo();

        // get branch status
        branchStatus = getBranchStatus();

        // convert language to languageCode
        String languageCode = language.equals("VIE") || language.equals("vi") ? "vi" : "en";

        // get max stock
        int maxStock = productInfo.isDeleted() ? 0 : Collections.max(productInfo.getProductStockQuantityMap().values().stream().map(Collections::max).toList());
        storeInfo = new StoreInformation(loginInformation).getInfo();

        // open product detail screen
        boolean isShowOnApp = true;
        try {
            searchAndNavigateToProductDetail(language, productInfo);
        } catch (NoSuchElementException ex) {
            isShowOnApp = false;
        }

        // check out of stock
        if ((maxStock == 0 && !productInfo.isShowOutOfStock())) {
            assertCustomize.assertFalse(isShowOnApp, "[Failed] Product still shows when stock is out and setting hides product out of stock.");
        }

        // check product is display or not
        if (!productInfo.isDeleted() && productInfo.isOnApp() && productInfo.getBhStatus().equals("ACTIVE") && (maxStock > 0 || productInfo.isShowOutOfStock())) {
            if (storeInfo.getSFLangList().contains(languageCode))
                checkProductInformation(languageCode, customerId);
            else logger.info("'%s' language is not published, please publish it and try again.".formatted(language));
        }

        // complete verify
        if (assertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
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
    
    public boolean isReviewTabDisplayed() {
    	commonMobile.sleepInMiliSecond(1000); //Sometimes it does not scroll down to the element
    	commonMobile.moveAndGetElement(PRODUCT_DESCRIPTION_TAB);
    	commonMobile.sleepInMiliSecond(1000); //Sometimes it does not scroll down to the element
        boolean isDisplayed = !commonMobile.isElementNotDisplay(commonMobile.getElements(PRODUCT_REVIEW_TAB));
        logger.info("Is Review tab displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    public String[] getReview() {
    	//Needs further updates
    	commonMobile.moveAndGetElement(REVIEWCONTENT);
    	
    	//Temporary
    	if (!commonMobile.getElements(EMPTYREVIEW).isEmpty()) {
    		return null;
    	}
    	
    	String title = commonMobile.getText(PRODUCT_REVIEW_TITLE);
    	String description = commonMobile.getText(PRODUCT_REVIEW_DESCRIPTION);

        return new String[]{title, description};
    }
    
}
