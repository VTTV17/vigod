package pages.buyerapp.productDetail;

import api.dashboard.onlineshop.Preferences;
import api.dashboard.products.ProductInformation;
import api.dashboard.promotion.CreatePromotion;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
    DiscountCampaignInfo discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    int countFail;

    public BuyerProductDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        commonMobile = new UICommonMobile(driver);
    }

    String url;

    WebElement getElement(By locator) {
        return commonMobile.moveAndGetElement(PRODUCT_NAME, locator);
    }

    List<WebElement> getListElements(By locator) {
        return commonMobile.moveAndGetListElements(PRODUCT_NAME, locator);
    }

    List<WebElement> getNextElementOfList(By locator) {
        return commonMobile.nextElementOfList(locator);
    }

    /**
     * <p>Branch status:</p>
     * <p>status = true: show branch on storefront/buyer app if in-stock</p>
     * <p>status = false: hide branch on storefront/buyer app</p>
     */
    List<Boolean> getBranchStatus() {
        // getListElementId branch info
        brInfo = new BranchManagement().getInfo();

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
        System.out.printf("flash sale status%s%n", flashSaleInfo.getFlashSaleStatus());
        System.out.printf("discount campaign status%s%n", discountCampaignInfo.getDiscountCampaignStatus());
        System.out.printf("wholesale product status%s%n", wholesaleProductInfo.getStatusMap());
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
        // getListElementId product name on dashboard
        String dbProductName = StringUtils.capitalize(productInfo.getProductNameMap().get(barcode).get(language));

        // getListElementId product name on shop online
        String sfProductName = getElement(PRODUCT_NAME).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertTrue(countFail, sfProductName.equals(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    void checkPriceOnBranch(long listingPrice, long sellingPrice, String brName) throws IOException {
        String branch = brName.equals("") ? "" : "[Branch name: %s]".formatted(brName);

        if (!(new Preferences().isEnabledListingProduct() && productInfo.isEnabledListing())) {
//            if (listingPrice != sellingPrice) {
//                String adrListingPrice = driver.findElement(ADD_TO_CART_POPUP_LISTING_PRICE).getText().replaceAll("\\D+", "");
//                long adrListingPriceValue = Long.parseLong(adrListingPrice);
//                countFail = new AssertCustomize(driver).assertEquals(countFail, adrListingPriceValue, listingPrice, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, adrListingPriceValue));
//            } else logger.info("No discount product (listing price = selling price)");
            String adrSellingPrice = driver.findElement(ADD_TO_CART_POPUP_SELLING_PRICE).getText().replaceAll("\\D+", "");
            long adrSellingPriceValue = Long.parseLong(adrSellingPrice);

            countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(adrSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, adrSellingPrice));
            logger.info("%s Check product price/ store currency show correctly".formatted(branch));
        } else logger.info("%s Website listing enable, so listing/selling price is hidden".formatted(branch));
    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasFlashSale = getElement(FLASH_SALE_BADGE).isDisplayed();
        countFail = new AssertCustomize(driver).assertTrue(countFail, hasFlashSale, "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }

    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean hasDiscountCampaign = getElement(DISCOUNT_CAMPAIGN_BADGE).isDisplayed();

        countFail = new AssertCustomize(driver).assertTrue(countFail, hasDiscountCampaign, "%s Discount campaign does not show".formatted(branch));
        logger.info("%s Check discount campaign is shown".formatted(branch));
    }

    // check wholesale product price
    void checkWholesaleProductShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean check = getElement(WHOLE_SALE_PRODUCT_BADGE).isDisplayed();
        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(String language) {
        // getListElementId variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(productInfo.getVariationNameMap().get(language).split("\\|")).toList();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(VARIATION_NAME_LIST));
        List<String> variationNameListSF = driver.findElements(VARIATION_NAME_LIST).stream().map(WebElement::getText).toList().stream().sorted().toList();
        countFail = new AssertCustomize(driver).assertTrue(countFail, variationNameListSF.toString().equals(variationNameListDB.toString()), "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListSF));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // getListElementId search icon element
        WebElement searchIcon = getElement(SEARCH_BRANCH_ICON);
        // check Search icon is shown or not
        boolean checkSearchBox = searchIcon != null;
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkSearchBox, "[Failed]%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Search box' is displayed.".formatted(varName));

        // check Filter icon
        boolean checkFilter = false;
        if (checkSearchBox) {
            // open search box (filter icon is a part of search box)
            searchIcon.click();

            // check filter branch icon is shown or not
            checkFilter = getElement(FILTER_BRANCH_ICON) != null;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is displayed.".formatted(varName));
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // getListElementId search icon element
        WebElement searchIcon = getElement(SEARCH_BRANCH_ICON);
        // check Search icon is shown or not
        boolean checkSearchBox = searchIcon != null;
        countFail = new AssertCustomize(driver).assertFalse(countFail, checkSearchBox, "[Failed]%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Search box' is hidden.".formatted(varName));

        // check Filter icon
        boolean checkFilter = false;
        if (checkSearchBox) {
            // open search box (filter icon is a part of search box)
            searchIcon.click();

            // check filter branch icon is shown or not
            checkFilter = getElement(FILTER_BRANCH_ICON) != null;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is hidden.".formatted(varName));
    }

    void checkBranchNameAndStock(WebElement brElement, boolean brStatus, int brStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch information
        // getListElementId branch name
        String adrBranchName = brElement.getText().split(" - ")[0];
        // check branch name
        countFail = new AssertCustomize(driver).assertTrue(countFail, brInfo.getBranchName().contains(adrBranchName) && brStatus && (brStock > 0), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(adrBranchName));

        if (!productInfo.isHideStock()) {
            // check branch stock
            int adrBranchStock = Integer.parseInt(brElement.getText().split(" - ")[1].replaceAll("\\D+", ""));
            countFail = new AssertCustomize(driver).assertEquals(countFail, adrBranchStock, brStock, "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, adrBranchName, brStock, adrBranchStock));
        } else logger.info("Setting hide stock.");
        logger.info("%s Check branch '%s'".formatted(varName, adrBranchName));
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    void checkProductDescription(String barcode, String language) {
        // getListElementId dashboard product description
        String dbDescription = productInfo.getProductDescriptionMap().get(barcode).get(language).replaceAll("<.*?>", "");

        // getListElementId SF product description
        String adrDescription = (dbDescription.length() > 1) ? getElement(PRODUCT_DESCRIPTION_CONTENT).getText() : "";

        countFail = new AssertCustomize(driver).assertTrue(countFail, adrDescription.equals(dbDescription), "[Failed][Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, adrDescription));
        logger.info("[Check description] Check product description is shown correctly.");
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    void checkSoldOutMark(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        boolean isSoldOut = getElement(SOLD_OUT_MARK) != null;
        countFail = new AssertCustomize(driver).assertTrue(countFail, isSoldOut, "[Failed]%s Sold out mark does not show".formatted(varName));
        logger.info("%s Check 'SOLD OUT' mark is shown".formatted(varName));
    }

    void checkBuyNowAddToCartAndContactNowBtn(String... variationName) {
        String varName = (variationName.length > 0) ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(new Preferences().isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            boolean checkBuyNow = getElement(BUY_NOW_BTN).isDisplayed();

            countFail = new AssertCustomize(driver).assertTrue(countFail, checkBuyNow, "[Failed]%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Buy Now' button is displayed.".formatted(varName));

            // check Add to cart button is shown
            boolean checkAddToCart = getElement(ADD_TO_CART_ICON).isDisplayed();
            countFail = new AssertCustomize(driver).assertTrue(countFail, checkAddToCart, "[Failed]%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Add to cart' button is displayed.".formatted(varName));

        } else {
            // check Contact Now button is shown
            boolean isContactNow = getElement(CONTACT_NOW_BTN).isDisplayed();

            countFail = new AssertCustomize(driver).assertTrue(countFail, isContactNow, "[Failed]%s 'Contact Now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Contact Now' button is shown.".formatted(varName));
        }
    }

    void checkVariationPrice(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, String brName) throws IOException {
        String priceType = getSalePriceMap().get(brName).get(varIndex);
        String displayType = getSaleDisplayMap().get(brName).get(varIndex);
        System.out.printf("price type: %s%n", priceType);
        System.out.printf("display type: %s%n", displayType);

        // check badge
        switch (displayType) {
            // check flash sale badge is shown
            case "FLASH SALE" -> checkFlashSaleShouldBeShown(brName);
            // check discount campaign is shown
            case "DISCOUNT CAMPAIGN" -> checkDiscountCampaignShouldBeShown(brName);
            // check wholesale product is shown
            case "WHOLESALE PRODUCT" -> checkWholesaleProductShouldBeShown(brName);
        }

        // open add to cart popup
        getElement(ADD_TO_CART_ICON).click();

        // check price
        switch (priceType) {
            // check flash sale price
            case "FLASH SALE" -> checkPriceOnBranch(listingPrice, flashSalePrice, brName);
            // check discount campaign price
            case "DISCOUNT CAMPAIGN" -> {
                getElement(ADD_TO_CART_POPUP_BUY_IN_BULK_CHECKBOX).click();
                checkPriceOnBranch(listingPrice, productDiscountCampaignPrice, brName);
            }
            case "WHOLESALE PRODUCT" -> {
                // increase quantity to wholesale product minimum requirement
                getElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).clear();
                driver.findElement(ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX).sendKeys(String.valueOf(wholesaleProductStock));

                // check wholesale product price
                checkPriceOnBranch(listingPrice, wholesaleProductPrice, brName);
            }
            default -> checkPriceOnBranch(listingPrice, sellingPrice, brName);
        }

        // close add to cart popup
        getElement(ADD_TO_CART_POPUP_CLOSE_ICON).click();
    }

//    void checkCurrentBranchList(List<Boolean> branchStatus, By locator, int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String... variationName) throws IOException {
//        // getListElementId branch element list
//        List<WebElement> branchElementList = driver.findElements(locator);
//
//        // check current branch information
//        for (int brElementIndex = 0; brElementIndex < branchElementList.size(); brElementIndex++) {
//            WebElement brElement = branchElementList.getListElementId(brElementIndex);
//            // switch branch
//            brElement.click();
//
//            // branch name
//            String brName;
//            try {
//                brName = brElement.getText().split(" - ")[0];
//            } catch (StaleElementReferenceException ex) {
//                branchElementList = driver.findElements(locator);
//                brElement = branchElementList.getListElementId(brElementIndex);
//                brName = brElement.getText().split(" - ")[0];
//            }
//
//            // check branch name, branch stock, branch price
//            if (!listCheckedBranches.contains(brName)) {
//                // getListElementId branch index in branch information
//                int brIndex = brInfo.getBranchName().indexOf(brName);
//
//                // add branch to checked branch list
//                listCheckedBranches.add(brName);
//
//                checkBranchNameAndStock(brElement, branchStatus.getListElementId(brIndex), branchStock.getListElementId(brIndex), variationName);
//
//                // check product price
//                checkVariationPrice(varIndex, listingPrice, sellingPrice, flashSalePrice, productDiscountCampaignPrice, wholesaleProductStock, wholesaleProductPrice, brName);
//            }
//        }
//    }


    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String language, String... variationName) throws IOException {
        // getListElementId branch status
        List<Boolean> branchStatus = getBranchStatus();

        // log
        if (variationName.length > 0)
            if (variationName[0] != null) logger.info("*** var: %s ***".formatted(variationName[0]));

        // check product name
        checkProductName(productInfo.getBarcodeList().get(varIndex), language);

        // check variation name if any
        if (productInfo.isHasModel()) checkVariationName(language);

        // count all branches display
        int numberOfDisplayBranches = Collections.frequency(IntStream.range(0, branchStatus.size()).mapToObj(brIndex -> branchStatus.get(brIndex) && (branchStock.get(brIndex) > 0)).toList(), true);

        // check branch information
        if (numberOfDisplayBranches > 0) {
            // check filter/search branch is shown when available branches >= 6
            if (numberOfDisplayBranches >= 6) checkFilterAndSearchBranchIsShown(variationName);
            else checkFilterAndSearchBranchIsHidden(variationName);

            // getListElementId current branch list
            List<WebElement> branchElementList = getListElements(BRANCH_LIST);
            List<String> branchElementIdList = commonMobile.getListElementId(BRANCH_LIST);

            // init list checked branch
            List<String> listCheckedBranches = new ArrayList<>();

            do {
                // check current branch information
                for (int brElementIndex = 0; brElementIndex < branchElementList.size(); brElementIndex++) {
                    if (!listCheckedBranches.contains(branchElementIdList.get(brElementIndex))) {
                        WebElement brElement = branchElementList.get(brElementIndex);
                        // switch branch
                        brElement.click();

                        // branch name
                        String brName;
                        try {
                            brName = brElement.getText().split(" - ")[0];
                        } catch (StaleElementReferenceException ex) {
                            branchElementList = driver.findElements(BRANCH_LIST);
                            brElement = branchElementList.get(brElementIndex);
                            brName = brElement.getText().split(" - ")[0];
                        }

                        // check branch name, branch stock, branch price
                        // getListElementId branch index in branch information
                        int brIndex = brInfo.getBranchName().indexOf(brName);

                        // add branch to checked branch list
                        listCheckedBranches.add(brName);

                        checkBranchNameAndStock(brElement, branchStatus.get(brIndex), branchStock.get(brIndex), variationName);

                        // check product price
                        checkVariationPrice(varIndex, listingPrice, sellingPrice, flashSalePrice, productDiscountCampaignPrice, wholesaleProductStock, wholesaleProductPrice, brName);

                    }

                }
                branchElementList = getNextElementOfList(BRANCH_LIST);
                branchElementIdList = commonMobile.getListElementId(BRANCH_LIST);
                System.out.println(branchElementList.size());
            }
            // check another branch information
            while (branchElementList.size() > 0);

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
        // getListElementId flash sale, discount campaign information
        CreatePromotion promotion = new CreatePromotion();
        flashSaleInfo = promotion.getFlashSaleInfo(productInfo.getBarcodeList(), productInfo.getProductSellingPrice());
        discountCampaignInfo = promotion.getDiscountCampaignInfo(productInfo.getBarcodeList(), productInfo.getProductSellingPrice());

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
                    // getListElementId variation value
                    String[] varName = variationValue.split("\\|");

                    wait.until(ExpectedConditions.visibilityOfElementLocated(VARIATION_VALUE_LIST));
                    List<WebElement> variationValueList = driver.findElements(VARIATION_VALUE_LIST);

                    // select variation
                    Arrays.stream(varName).forEachOrdered(var -> variationValueList.stream().filter(webElement -> ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent", webElement).toString().contains(var)).findFirst().ifPresent(WebElement::click));
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

    /**
     * Access to product detail on SF by URL
     */
    public void accessToProductDetailPageByProductIDAndCheckProductInformation(String language, ProductInfo productInfo) throws Exception {
        // getListElementId product information
        this.productInfo = productInfo;

        // convert language to languageCode
        String languageCode = language.equals("VIE") || language.equals("vi") ? "vi" : "en";

        // getListElementId wholesale config
        if (!productInfo.isDeleted()) wholesaleProductInfo = new ProductInformation().wholesaleProductInfo(productInfo);

        // getListElementId max stock
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
}
