package pages.storefront.detail_product;

import api.dashboard.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.storefront.shoppingcart.ShoppingCart;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.apiStoreName;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.*;
import static api.dashboard.setting.StoreInformation.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static pages.dashboard.products.all_products.ProductPage.*;
import static pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage.*;
import static utilities.PropertiesUtil.getPropertiesValueBySFLang;
import static utilities.api_body.product.CreateProductBody.apiIsDisplayOutOfStock;
import static utilities.api_body.product.CreateProductBody.apiIsHideStock;
import static utilities.links.Links.*;

public class ProductDetailPage extends ProductDetailElement {
    WebDriverWait wait;

    int countFail = 0;

    List<String> branchStatus;

    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction commonAction;
    boolean isMultipleLanguage;
    Map<String, List<Integer>> productStockQuantity = new HashMap<>();
    boolean isDisplayOutOfStock;
    boolean isHideStock;
    int productID;

    String productName;
    String productDescription;
    Instant flashSaleStartTime;
    Instant flashSaleEndTime;
    List<String> branchName = new ArrayList<>();
    Map<String, List<String>> flashSaleStatus = new HashMap<>();
    boolean isVariation;
    List<String> variationList = new ArrayList<>();
    Map<String, List<String>> variationMap = new HashMap<>();
    Instant discountCampaignStartTime;
    Instant discountCampaignEndTime;
    Map<String, List<String>> discountCampaignStatus = new HashMap<>();
    List<Integer> productListingPrice = new ArrayList<>();
    List<Integer> productSellingPrice = new ArrayList<>();
    List<Integer> flashSalePrice = new ArrayList<>();
    List<Integer> discountCampaignPrice = new ArrayList<>();
    List<Integer> wholesaleProductStock = new ArrayList<>();
    List<Integer> wholesaleProductPrice = new ArrayList<>();
    List<Integer> branchID = new ArrayList<>();
    Map<String, List<Boolean>> wholesaleProductStatus = new HashMap<>();
    int discountCampaignStock;
    String collectionName;

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);

        //get product information
        getProductInformation();
    }

    void getProductInformation() {
        if (isCreateByUI) {
            // has variation or not, get variation info if any
            isVariation = uiIsVariation;

            variationList.addAll(uiVariationList);

            if (isVariation) variationMap.putAll(uiVariationMap);

            // product name
            productName = uiProductName;

            // product description
            productDescription = uiProductDescription;

            // product collection
            collectionName = uiCollectionName;

            // product id
            productID = uiProductID;

            // stock quantity
            productStockQuantity.putAll(uiProductStockQuantity);

            // product price
            productListingPrice.addAll(uiProductListingPrice);

            productSellingPrice.addAll(uiProductSellingPrice);

            // setting is display on SF
            isDisplayOutOfStock = uiIsDisplayOutOfStock;

            isHideStock = uiIsHideStock;

            // flash sale config
            flashSaleStartTime = apiFlashSaleStartTime;

            flashSaleEndTime = apiFlashSaleEndTime;

            flashSaleStatus.putAll(apiFlashSaleStatus);

            flashSalePrice.addAll(apiFlashSalePrice);

            // discount campaign config
            discountCampaignStartTime = apiDiscountCampaignStartTime;

            discountCampaignEndTime = apiDiscountCampaignEndTime;

            discountCampaignStatus.putAll(apiDiscountCampaignStatus);

            discountCampaignPrice.addAll(apiDiscountCampaignPrice);

            discountCampaignStock = apiDiscountCampaignStock;

            // wholesale product config
            wholesaleProductStock.addAll(uiWholesaleProductStock);

            wholesaleProductPrice.addAll(uiWholesaleProductPrice);

            wholesaleProductStatus.putAll(uiWholesaleProductStatus);

            // branch config
            branchID.addAll(apiBranchID);

            branchName.addAll(uiBranchName);

        } else {
            // has variation or not, get variation info if any
            isVariation = apiIsVariation;

            variationList.addAll(apiVariationList);

            if (isVariation) variationMap.putAll(apiVariationMap);

            // product name
            productName = apiProductName;

            // product description
            productDescription = apiProductDescription;

            // product collection
            collectionName = apiCollectionName;

            // product id
            productID = apiProductID;

            // stock quantity
            productStockQuantity.putAll(apiProductStockQuantity);

            // product price
            productListingPrice.addAll(apiProductListingPrice);

            productSellingPrice.addAll(apiProductSellingPrice);

            // setting is display on SF
            isDisplayOutOfStock = apiIsDisplayOutOfStock;

            isHideStock = apiIsHideStock;

            // flash sale config
            flashSaleStartTime = apiFlashSaleStartTime;

            flashSaleEndTime = apiFlashSaleEndTime;

            flashSaleStatus.putAll(apiFlashSaleStatus);

            flashSalePrice.addAll(apiFlashSalePrice);

            // discount campaign config
            discountCampaignStartTime = apiDiscountCampaignStartTime;

            discountCampaignEndTime = apiDiscountCampaignEndTime;

            discountCampaignStatus.putAll(apiDiscountCampaignStatus);

            discountCampaignPrice.addAll(apiDiscountCampaignPrice);

            discountCampaignStock = apiDiscountCampaignStock;

            // wholesale product config
            wholesaleProductStock.addAll(apiWholesaleProductStock);

            wholesaleProductPrice.addAll(apiWholesaleProductPrice);

            wholesaleProductStatus.putAll(apiWholesaleProductStatus);

            // branch config
            branchID.addAll(apiBranchID);

            branchName.addAll(apiBranchName);
        }
    }

    /**
     * Access to product detail on SF by URL
     */
    public ProductDetailPage accessToProductDetailPageByProductID() throws Exception {
        // check shop has multiple language or not
        driver.get("https://%s%s/".formatted(apiStoreURL, SF_DOMAIN));

        // get max stock
        int maxStock = Collections.max(productStockQuantity.values().stream().map(Collections::max).toList());

        // check product is display or not
        if ((maxStock == 0) && (!isDisplayOutOfStock)) {
            // in-case out of stock and setting hide product when out of stock
            // wait 404 page loaded
            driver.get("https://%s%s%s/product/%s".formatted(apiStoreURL, SF_DOMAIN, isMultipleLanguage ? "/%s".formatted(apiStoreLanguageList.get(0)) : "", productID));

            // sleep 1s
            commonAction.sleepInMiliSecond(1000);

            // wait spinner loaded if any
            commonAction.waitForElementInvisible(SPINNER, 15);

        } else {
            // in-case in stock or setting show product when out of stock
            // check if shop have multiple language, check all language should be shown exactly
            for (String languageCode : apiStoreLanguageList) {
                // check all information with VIE language
                driver.get("https://%s%s/%s/product/%s".formatted(apiStoreURL, SF_DOMAIN, languageCode, productID));

                //wait spinner loaded
                commonAction.waitForElementInvisible(SPINNER, 30);

                // wait product detail page loaded
                commonAction.verifyPageLoaded(productName, productName);
                if ((maxStock > 0) && (BRANCH_NAME_LIST.size() > 0)) {
                    checkUIInStock(languageCode.equals("vi") ? "VIE" : "ENG");
                } else checkUIOutOfStock(languageCode.equals("vi") ? "VIE" : "ENG");
            }
        }

        logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productID));
        return this;
    }

    void checkHeader(String language) throws Exception {
        // check store logo
        String sfStoreLogo = wait.until(ExpectedConditions.visibilityOf(HEADER_SHOP_LOGO)).getAttribute("src").replace("/200/", "/");
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfStoreLogo, apiStoreLogo, "[Failed][Header] Store logo should be %s, but found %s.".formatted(apiStoreLogo, sfStoreLogo));
        logger.info("[UI][%s] Check Header - Store Logo".formatted(language));

        // check header menu
        commonAction.waitElementList(HEADER_MENU, 2);
        List<String> sfHeaderMenu = HEADER_MENU.stream().map(WebElement::getText).toList();
        List<String> defaultMenu = apiDefaultLanguage.equals("VIE")
                ? List.of(getPropertiesValueBySFLang("header.menu.vnStore.0", language), getPropertiesValueBySFLang("header.menu.vnStore.1", language))
                : List.of(getPropertiesValueBySFLang("header.menu.nonVNStore.0", language), getPropertiesValueBySFLang("header.menu.nonVNStore.1", language));
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfHeaderMenu, defaultMenu,
                "[Failed][Header] Header menu should be %s, but found %s.".formatted(sfHeaderMenu, defaultMenu));
        logger.info("[UI][%s] Check Header - Menu".formatted(language));

        // check search icon
        countFail = new AssertCustomize(driver).assertTrue(countFail, HEADER_SEARCH_ICON.isDisplayed(), "[Failed][Header] Search icon does not show.");
        logger.info("[UI][%s] Check Header - Search Icon".formatted(language));

        // check cart
        countFail = new AssertCustomize(driver).assertTrue(countFail, HEADER_CART_ICON.isDisplayed(), "[Failed][Header] Cart icon does not show.");
        countFail = new AssertCustomize(driver).assertTrue(countFail, HEADER_NUMBER_PRODUCT_IN_CART.isDisplayed(), "[Failed][Header] Number of products in cart does not show.");
        logger.info("[UI][%s] Check Header - Cart Icon".formatted(language));

        // check profile icon
        countFail = new AssertCustomize(driver).assertTrue(countFail, HEADER_PROFILE_ICON.isDisplayed(), "[Failed][Header] Profile icon does not show.");
        logger.info("[UI][%s] Check Header - Profile Icon".formatted(language));
    }

    void checkBreadcrumbs(String language) throws Exception {
        // check breadcrumbs
        String[] sfBreadCrumbs = wait.until(ExpectedConditions.visibilityOf(BREAD_CRUMBS)).getText().split(" {4}/ {5}");
        String[] breadCrumbsCollection = {getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), collectionName, productName};
        String[] breadCrumbsAllProduct = {getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), getPropertiesValueBySFLang("productDetail.breadCrumbs.1", language), productName};
        countFail = new AssertCustomize(driver).assertTrue(countFail,
                Arrays.toString(sfBreadCrumbs).equals(Arrays.toString(breadCrumbsAllProduct)) || Arrays.toString(sfBreadCrumbs).equals(Arrays.toString(breadCrumbsCollection)),
                "[Failed][Breadcrumbs] Breadcrumbs should be %s or %s, but found %s.".formatted(Arrays.toString(breadCrumbsAllProduct),
                        Arrays.toString(breadCrumbsCollection),
                        Arrays.toString(sfBreadCrumbs)));
        logger.info("[UI][%s] Check Breadcrumbs".formatted(language));
    }

    void checkProductDetailWhenInStock(String language) throws Exception {
        // quantity
        String sfQuantity = wait.until(ExpectedConditions.visibilityOf(QUANTITY_TITLE)).getText();
        String quantity = getPropertiesValueBySFLang("productDetail.quantity", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfQuantity, quantity, "[Failed][Product Detail] Quantity title should be %s, but found %s.".formatted(quantity, sfQuantity));
        logger.info("[UI][%s] Check Product Detail - Quantity".formatted(language));

        if (BRANCH_NAME_LIST.size() > 5) {
            // filter
            String sfFilterBranch = wait.until(ExpectedConditions.visibilityOf(FILTER_BRANCH_BY_LOCATION)).getText();
            String filterBranch = getPropertiesValueBySFLang("productDetail.branch.filter", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfFilterBranch, filterBranch, "[Failed][Filter branch by location] The first filter value should be %s, but found %s.".formatted(filterBranch, sfFilterBranch));
            logger.info("[UI][%s] Check Product Detail - Filter Branch".formatted(language));

            // search
            String sfSearchBranch = wait.until(ExpectedConditions.visibilityOf(SEARCH_BRANCH_BY_ADDRESS)).getText();
            String searchBranch = getPropertiesValueBySFLang("productDetail.branch.search", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfFilterBranch, filterBranch, "[Failed][Search branch by address] The search placeholder value should be %s, but found %s.".formatted(searchBranch, sfSearchBranch));
            logger.info("[UI][%s] Check Product Detail - Search Branch".formatted(language));
        }

        // check branch
        String[] sfAvailableBranch = wait.until(ExpectedConditions.visibilityOf(AVAILABLE_BRANCH)).getText().split("\\d");
        String[] availableBranch = getPropertiesValueBySFLang("productDetail.branch.availableBranch", language).split("\\d");
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfAvailableBranch, availableBranch, "[Failed][Product Detail] Available branch title should be %s, but found %s.".formatted(Arrays.toString(availableBranch), Arrays.toString(sfAvailableBranch)));
        logger.info("[UI][%s] Check Product Detail - Available Branches".formatted(language));

        // check stock
        if (!isHideStock) {
            commonAction.waitElementList(STOCK_QUANTITY_IN_BRANCH);
            String[] sfStock = wait.until(ExpectedConditions.visibilityOf(STOCK_QUANTITY_IN_BRANCH.get(0))).getText().split("\\d+");
            String[] stock = getPropertiesValueBySFLang("productDetail.branch.stock", language).split("\\d+");
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfStock, stock, "[Failed][Product Detail] Stock title should be %s, but found %s.".formatted(Arrays.toString(stock), Arrays.toString(sfStock)));
            logger.info("[UI][%s] Check Product Detail - Stock in Branch".formatted(language));
        }

        // check buy now
        String sfBuyNow = wait.until(ExpectedConditions.visibilityOf(BUY_NOW_BTN)).getText();
        String buyNow = getPropertiesValueBySFLang("productDetail.cart.buyNow", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfBuyNow, buyNow, "[Failed][Product Detail] Buy now title should be %s, but found %s.".formatted(buyNow, sfBuyNow));
        logger.info("[UI][%s] Check Product Detail - Buy Now button".formatted(language));

        // check Add to cart
        String sfAddToCart = wait.until(ExpectedConditions.visibilityOf(ADD_TO_CART_BTN)).getText();
        String addToCart = getPropertiesValueBySFLang("productDetail.cart.addToCart", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfAddToCart, addToCart, "[Failed][Product Detail] Add to cart title should be %s, but found %s.".formatted(addToCart, sfAddToCart));
        logger.info("[UI][%s] Check Product Detail - Add to Cart button".formatted(language));

        // payment
        String sfPayment = wait.until(ExpectedConditions.visibilityOf(PAYMENT)).getText();
        String payment = getPropertiesValueBySFLang("productDetail.payment", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfPayment, payment, "[Failed][Product Detail] Payment title should be %s, but found %s.".formatted(payment, sfPayment));
        logger.info("[UI][%s] Check Product Detail - Payment Method".formatted(language));

    }

    void checkProductDetailWhenOutOfStock(String language) throws Exception {
        // check sold out mark
        String sfSoldOut = wait.until(ExpectedConditions.visibilityOf(SOLD_OUT_MARK)).getText();
        String soldOut = getPropertiesValueBySFLang("productDetail.soldOut", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfSoldOut, soldOut, "[Failed][Product Detail] Sold out title should be %s, but found %s.".formatted(soldOut, sfSoldOut));
        logger.info("[UI][%s] Check Product Detail - Sold Out mark".formatted(language));
    }

    void checkOthersInformation(String language) throws Exception {
        // description tab
        String sfDescriptionTab = wait.until(ExpectedConditions.visibilityOf(DESCRIPTION_TAB)).getText();
        String descriptionTab = getPropertiesValueBySFLang("productDetail.description", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfDescriptionTab, descriptionTab, "[Failed][Product Detail] Description tab title should be %s, but found %s.".formatted(descriptionTab, sfDescriptionTab));
        logger.info("[UI][%s] Check Product Detail - Description Tab".formatted(language));

        // review tab
        String sfReviewTab = wait.until(ExpectedConditions.visibilityOf(REVIEW_TAB)).getText();
        String reviewTab = getPropertiesValueBySFLang("productDetail.review", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfReviewTab, reviewTab, "[Failed][Product Detail] Review tab title should be %s, but found %s.".formatted(reviewTab, sfReviewTab));
        logger.info("[UI][%s] Check Product Detail - Review Tab".formatted(language));

        // similar product
        if (SIMILAR_PRODUCT.isDisplayed()) {
            String sfSimilarProduct = wait.until(ExpectedConditions.visibilityOf(SIMILAR_PRODUCT)).getText();
            String similarProduct = getPropertiesValueBySFLang("productDetail.similarProduct", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfSimilarProduct, similarProduct, "[Failed][Product Detail] Similar Product title should be %s, but found %s.".formatted(similarProduct, sfSimilarProduct));
            logger.info("[UI][%s] Check Product Detail - Similar Product".formatted(language));
        }
    }

    void checkFooter(String language) throws Exception {
        // check store logo
        String sfStoreLogo = wait.until(ExpectedConditions.visibilityOf(FOOTER_SHOP_LOGO)).getAttribute("src").replace("/200/", "/");
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfStoreLogo, apiStoreLogo, "[Failed][Footer] Store logo should be %s, but found %s.".formatted(apiStoreLogo, sfStoreLogo));
        logger.info("[UI][%s] Check Footer - Shop Logo".formatted(language));

        // check company
        String sfCompany = wait.until(ExpectedConditions.visibilityOf(FOOTER_COMPANY)).getText();
        String company = getPropertiesValueBySFLang("footer.company", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfCompany, company, "[Failed][Footer] Company title should be %s, but found %s.".formatted(company, sfCompany));
        logger.info("[UI][%s] Check Footer - Company".formatted(language));

        // check follow us
        String sfFollowUs = wait.until(ExpectedConditions.visibilityOf(FOOTER_FOLLOW_US)).getText();
        String followUs = getPropertiesValueBySFLang("footer.followUs", language);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfFollowUs, followUs, "[Failed][Footer] Follow us title should be %s, but found %s.".formatted(followUs, sfFollowUs));
        logger.info("[UI][%s] Check Footer - Follow Us".formatted(language));

        // check copyright
        String sfCopyright = wait.until(ExpectedConditions.visibilityOf(FOOTER_COPYRIGHT)).getText();
        String copyright = getPropertiesValueBySFLang("footer.copyright", language).formatted(new DataGenerator().generateDateTime("yyyy"), apiStoreName);
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfCopyright, copyright, "[Failed][Footer] Copyright title should be %s, but found %s.".formatted(copyright, sfCopyright));
        logger.info("[UI][%s] Check Footer - Copyright".formatted(language));
    }


    void checkUIInStock(String language) throws Exception {
        checkHeader(language);
        checkBreadcrumbs(language);
        checkProductDetailWhenInStock(language);
        checkOthersInformation(language);
        checkFooter(language);
    }

    void checkUIOutOfStock(String language) throws Exception {
        checkHeader(language);
        checkBreadcrumbs(language);
        checkProductDetailWhenOutOfStock(language);
        checkOthersInformation(language);
        checkFooter(language);
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
    void checkPriceOnEachBranch(int listingPrice, int sellingPrice, String branchName) throws IOException {
        String branch = branchName.equals("") ? "" : "[Branch name: %s]".formatted(branchName);
        if (listingPrice != sellingPrice) {
            String actListingPrice = new UICommonAction(driver).getText(LISTING_PRICE).replace(",", "");
            countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + STORE_CURRENCY, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
        }

        String actSellingPrice = new UICommonAction(driver).getText(SELLING_PRICE).replace(",", "");
        int actSellingPriceValue = Integer.parseInt(actSellingPrice.replace(STORE_CURRENCY, ""));

        countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(actSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, actSellingPrice));
        logger.info("%s Check product price/ store currency show correctly".formatted(branch));
    }

    public void getFlashSaleStatusMap() {
        boolean checkStart = flashSaleStartTime != null && Instant.now().getEpochSecond() >= flashSaleStartTime.getEpochSecond();
        boolean checkEnd = flashSaleEndTime != null && Instant.now().getEpochSecond() < flashSaleEndTime.getEpochSecond();
        branchName.stream().filter(branch -> flashSaleStatus.get(branch).get(0).equals("SCHEDULE")).filter(branch -> checkStart && checkEnd).forEachOrdered(branch -> flashSaleStatus.put(branch, IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> flashSaleStatus.get(branch).get(i).equals("SCHEDULE") ? "IN-PROGRESS" : "EXPIRED").collect(Collectors.toList())));
    }

    void getProductDiscountCampaignStatus() {
        boolean checkStart = discountCampaignStartTime != null && Instant.now().getEpochSecond() >= discountCampaignStartTime.getEpochSecond();
        boolean checkEnd = discountCampaignEndTime != null && Instant.now().getEpochSecond() < discountCampaignEndTime.getEpochSecond();

        // get discount campaign status
        branchName.stream().filter(branch -> discountCampaignStatus.get(branch).get(0).equals("SCHEDULE")).filter(branch -> checkStart && checkEnd).forEachOrdered(branch -> discountCampaignStatus.put(branch, IntStream.range(0, isVariation ? variationList.size() : 1).mapToObj(i -> "IN-PROGRESS").collect(Collectors.toList())));
    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String branchName) {
        String branch = "[Branch name: %s]".formatted(branchName);
        boolean check = true;
        try {
            wait.until(elementToBeClickable(FLASH_SALE_BADGE));
        } catch (TimeoutException ex) {
            check = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }


    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String branchName) {
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
    void checkWholesaleProductShouldBeShown(String branchName) {
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

    public void checkPrice(int indexOfVariation, int listingPrice, int sellingPrice, int flashSalePrice, int productDiscountCampaignPrice, int wholesaleProductStock, int wholesaleProductPrice, String branchName) throws IOException {
        String priceType = getSalePriceMap().get(branchName).get(indexOfVariation);
        switch (priceType) {
            case "FLASH SALE" -> {
                // check flash sale badge is shown
                checkFlashSaleShouldBeShown(branchName);

                // check flash sale price
                checkPriceOnEachBranch(listingPrice, flashSalePrice, branchName);
            }
            case "DISCOUNT CAMPAIGN" -> {
                // check discount campaign is shown
                checkDiscountCampaignShouldBeShown(branchName);

                // check discount campaign price
                checkPriceOnEachBranch(listingPrice, productDiscountCampaignPrice, branchName);
            }
            case "WHOLESALE PRODUCT" -> {
                // check wholesale product is shown
                checkWholesaleProductShouldBeShown(branchName);

                // increase quantity to wholesale product minimum requirement
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(wholesaleProductStock));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check wholesale product price
                checkPriceOnEachBranch(listingPrice, wholesaleProductPrice, branchName);
            }
            default -> checkPriceOnEachBranch(listingPrice, sellingPrice, branchName);
        }
    }

    // BH_8616, BH_9536
    void checkBranch(List<Integer> branchStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";

        // check branch on online shop
        if ((Collections.max(branchStock) > 0) && branchListIsShownOnSF(branchStock)) {
            System.out.println(apiAllBranchStatus);
            System.out.println(apiIsHideOnStoreFront);
            int count = IntStream.range(0, apiAllBranchStatus.size())
                    .filter(i -> !apiIsHideOnStoreFront.get(i) && apiAllBranchStatus.get(i).equals("ACTIVE") && (branchStock.get(i) > 0))
                    .mapToObj(i -> true).toList().size();

            if (count > 5) {
                checkFilterAndSearchBranchIsShown(variationName);
            } else {
                checkFilterAndSearchBranchIsHidden(variationName);
            }

            // wait list branch visible
            commonAction.waitElementList(BRANCH_NAME_LIST);

            // SF branch list:
            List<String> sfBranchList = BRANCH_NAME_LIST.stream().map(WebElement::getText).toList();

            for (int i = 0; i < branchStock.size(); i++) {
                String branch = branchName.get(i);
                if ((branchStock.get(i) > 0) && branchStatus.get(i).equals("SHOW")) {
                    countFail = new AssertCustomize(driver).assertTrue(countFail, sfBranchList.contains(branch), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(branch));
                    logger.info("%s Check branch '%s' is shown.".formatted(varName, branch));
                } else {
                    countFail = new AssertCustomize(driver).assertFalse(countFail, sfBranchList.contains(branch), "[Failed][Branch name: %s] Branch out of stock but is not hidden.".formatted(branch));
                    logger.info("%s Check branch '%s' is hidden.".formatted(varName, branch));
                }
            }
        } else logger.info("[Check branch] All branches out of stock");
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(Map<String, List<String>> variationMap) {
        List<String> variationNameList = LIST_VARIATION_NAME.stream().map(element -> element.getText().toLowerCase()).toList().stream().sorted().toList();

        countFail = new AssertCustomize(driver).assertTrue(countFail,
                variationNameList.toString().equals(variationMap.keySet().stream().sorted().toList().toString()),
                "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationMap.keySet().stream().sorted().toList(), variationNameList));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    void checkProductName(String productName) throws IOException {
        // get product name on shop online
        String sfProductName = wait.until(visibilityOf(PRODUCT_NAME)).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfProductName, productName, "[Failed][Check product name] Product name should be %s but found %s.".formatted(productName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    void checkStock(boolean isHideStock, int index, int stockQuantity, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
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
                    "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s"
                            .formatted(varName, BRANCH_NAME_LIST.get(index).getText(), stockQuantity, sfStock));

            logger.info("%s[Branch name: %s] Check current stock quantity".formatted(varName, BRANCH_NAME_LIST.get(index).getText()));
        } else logger.info("%s[Check stock] Setting hide stock on StoreFront.".formatted(varName));
    }

    void checkBuyNowAndAddToCartBtnIsShown(String... variationName) {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        // check Buy now button is shown
        boolean checkBuyNow = true;
        try {
            BUY_NOW_BTN.getText();
        } catch (NoSuchElementException ex) {
            checkBuyNow = false;
        }

        countFail = new AssertCustomize(driver).assertTrue(countFail, checkBuyNow, "[Failed]%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Buy Now' button is displayed.".formatted(varName));

        // check Add to cart button is shown
        boolean checkAddToCart = true;
        try {
            ADD_TO_CART_BTN.getText();
        } catch (NoSuchElementException ex) {
            checkAddToCart = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkAddToCart, "[Failed]%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Add to cart' button is displayed.".formatted(varName));
    }

    void checkBuyNowAndAddToCartBtnIsHidden(String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        // check Buy now button is shown
        boolean checkBuyNow = true;
        try {
            BUY_NOW_BTN.getText();
        } catch (NoSuchElementException ex) {
            checkBuyNow = false;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, checkBuyNow, "[Failed]%s 'Buy now' button should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Buy Now' button is hidden.".formatted(varName));

        // check Add to cart button is shown
        boolean checkAddToCart = true;
        try {
            ADD_TO_CART_BTN.getText();
        } catch (NoSuchElementException ex) {
            checkAddToCart = false;
        }
        countFail = new AssertCustomize(driver).assertFalse(countFail, checkAddToCart, "[Failed]%s 'Add to cart' button should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Add to cart' button is hidden.".formatted(varName));
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    void checkSoldOutMark(String... variationName) {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        boolean sfSoldOut = SOLD_OUT_MARK.getText().equals("Hết hàng") || SOLD_OUT_MARK.getText().equals("Out of stock");
        countFail = new AssertCustomize(driver).assertTrue(countFail, sfSoldOut, "[Failed]%s Sold out mark does not show".formatted(varName));
        logger.info("%s Check 'SOLD OUT' mark is shown".formatted(varName));
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    void check404Page() {
        countFail = new AssertCustomize(driver).assertTrue(countFail, driver.getCurrentUrl().contains("404"), "[Failed] 404 is not shown although product out of stock.");
        logger.info("Check 404 page is shown when product out of stock.");
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    void checkProductDescription(String productDescription) throws IOException {
        String sfDescription = wait.until(visibilityOf(PRODUCT_DESCRIPTION)).getText();
        countFail = new AssertCustomize(driver).assertEquals(countFail,
                sfDescription,
                productDescription,
                "[Failed][Check description] Product description should be '%s', but found '%s'"
                        .formatted(productDescription, sfDescription));
        logger.info("[Check description] Check product description is shown correctly.");
    }

    void checkProductInformation(int index, int listingPrice, int sellingPrice, int flashSalePrice, int productDiscountCampaignPrice, int wholesaleProductStock, int wholesaleProductPrice, List<Integer> branchStock, String... variationName) throws IOException {
        // log
        if (variationName.length > 0) logger.info("*** var: %s ***".formatted(variationName[0]));

        // check product name
        checkProductName(productName);

        // check variation name if any
        if (isVariation) checkVariationName(variationMap);

        // check description
        checkProductDescription(productDescription);

        if ((Collections.max(branchStock) > 0) && branchListIsShownOnSF(branchStock)) {

            // check Buy Now and Add To Cart button is shown
            checkBuyNowAndAddToCartBtnIsShown(variationName);

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

                // check branch stock quantity
                int id = BRANCH_NAME_LIST.indexOf(element);
                checkStock(isHideStock, id, branchStock.get(branchName.indexOf(branch)));

                // check product price
                checkPrice(index,
                        listingPrice,
                        sellingPrice,
                        flashSalePrice,
                        productDiscountCampaignPrice,
                        wholesaleProductStock,
                        wholesaleProductPrice,
                        element.getText());
            }
            checkBranch(branchStock, variationName);
        } else {
            checkSoldOutMark(variationName);
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    /**
     * Verify all information on the SF is shown correctly (without variation product)
     */
    public ProductDetailPage checkWithoutVariationProductInformation() throws IOException {
        int maxStock = Collections.max(productStockQuantity.get(null));
        if ((maxStock > 0) || isDisplayOutOfStock) {
            int index = 0;
            checkProductInformation(index,
                    productListingPrice.get(index),
                    productSellingPrice.get(index),
                    flashSalePrice.get(index),
                    discountCampaignPrice.get(index),
                    wholesaleProductStock.get(index),
                    wholesaleProductPrice.get(index),
                    productStockQuantity.get(null));
        } else check404Page();
        return this;
    }

    /**
     * Verify all information on the SF is shown correctly (variation product)
     */
    public ProductDetailPage checkVariationProductInformation() throws IOException {
        int maxStock = Collections.max(productStockQuantity.values().stream().map(Collections::max).toList());

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

                // check product information
                int index = variationList.indexOf(variationValue);
                checkProductInformation(index,
                        productListingPrice.get(index),
                        productSellingPrice.get(index),
                        flashSalePrice.get(index),
                        discountCampaignPrice.get(index),
                        wholesaleProductStock.get(index),
                        wholesaleProductPrice.get(index),
                        productStockQuantity.get(variationValue),
                        variationValue);
            }
        } else check404Page();
        return this;
    }

    public boolean branchListIsShownOnSF(List<Integer> branchStock) {
        // Call API and get Branch setting info
        new BranchManagement().getBranchInformation();

        // get branch status
        branchStatus = branchID.stream().mapToInt(brID -> brID)
                .mapToObj(id -> (!apiIsHideOnStoreFront.get(branchID.indexOf(id))
                        && apiAllBranchStatus.get(branchID.indexOf(id)).equals("ACTIVE")) ? "SHOW" : "HIDE").toList();

        return IntStream.range(0, branchStock.size()).anyMatch(i -> (branchStock.get(i) > 0) && branchStatus.get(i).equals("SHOW"));
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        // check Filter branch is shown
        boolean checkFilter = true;
        try {
            FILTER_BRANCH.getText();
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }

        countFail = new AssertCustomize(driver).assertTrue(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is displayed.".formatted(varName));

        // check Add to cart button is shown
        boolean checkSearchBox = true;
        try {
            SEARCH_BRANCH.getText();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, checkSearchBox, "[Failed]%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Search box' is displayed.".formatted(varName));
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) throws IOException {
        String varName = variationName.length > 0 ? "[Variation: %s]".formatted(variationName[0]) : "";
        // check Buy now button is shown
        boolean checkFilter = true;
        try {
            FILTER_BRANCH.getText();
        } catch (NoSuchElementException ex) {
            checkFilter = false;
        }

        countFail = new AssertCustomize(driver).assertFalse(countFail, checkFilter, "[Failed]%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is hidden.".formatted(varName));

        // check Add to cart button is shown
        boolean checkSearchBox = true;
        try {
            SEARCH_BRANCH.getText();
        } catch (NoSuchElementException ex) {
            checkSearchBox = false;
        }
        countFail = new AssertCustomize(driver).assertFalse(countFail, checkSearchBox, "[Failed]%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Search box' is hidden.".formatted(varName));
    }

    /**
     * Map: branch name, list of price type
     * <p>Ex: Product has variation var1, var2, var3, var4. And branch A, B</p>
     * <p>This function return list price type of each variation on each branch</p>
     * <p>Branch A = {FLASH SALE, WHOLESALE PRODUCT, WHOLESALE PRODUCT, SELLING PRICE} </p>
     * <p>Branch B = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN} </p>
     * <p>Branch C = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, SELLING PRICE} </p>
     */
    public Map<String, List<String>> getSalePriceMap() {
        getFlashSaleStatusMap();
        getProductDiscountCampaignStatus();
        return branchName.stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleStatus.get(brName).size()).mapToObj(i -> flashSaleStatus.get(brName).get(i).equals("IN-PROGRESS") ? "FLASH SALE" : discountCampaignStatus.get(brName).get(i).equals("IN-PROGRESS") ? "DISCOUNT CAMPAIGN" : wholesaleProductStatus.get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE").toList(), (a, b) -> b));
    }

    void addToCartForAllBranches(List<Integer> branchStock, String variationName) throws IOException {
        if ((Collections.max(branchStock) > 0) && branchListIsShownOnSF(branchStock)) {
            // wait list branch visible
            commonAction.waitElementList(BRANCH_NAME_LIST);

            // check flash sale for each branch
            for (WebElement element : BRANCH_NAME_LIST) {
                // switch branch
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 30);

                int varIndex = variationList.indexOf(variationName);

                // Add product to cart
                clickAddToCart(varIndex, discountCampaignStock, wholesaleProductStock.get(varIndex), element.getText());

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 30);
            }
        } else {
            checkSoldOutMark(variationName);
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    public void addProductToCart() throws IOException {
        int maxStock = Collections.max(productStockQuantity.values().stream().map(Collections::max).toList());

        if ((maxStock > 0) || isDisplayOutOfStock) {
            if (isVariation) {
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
                    commonAction.waitForElementInvisible(SPINNER, 30);

                    // Add product to cart
                    addToCartForAllBranches(productStockQuantity.get(variationValue), variationValue);
                }
            } else {
                // Add product to cart
                addToCartForAllBranches(productStockQuantity.get(null), null);
            }
        } else check404Page();
    }


    void clickAddToCart(int indexOfVariation, int discountCampaignStock, int wholesaleProductStock, String branchName) {
        String priceType = getSalePriceMap().get(branchName).get(indexOfVariation);
        switch (priceType) {
            case "DISCOUNT CAMPAIGN" -> {
                // set minimum discount campaign stock
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(discountCampaignStock));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // Add product to cart
                wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART_BTN)).click();

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 30);
            }
            case "WHOLESALE PRODUCT" -> {
                // set minimum wholesale product stock
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(wholesaleProductStock));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // Add product to cart
                wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART_BTN)).click();

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 30);
            }
            default -> {
                // set stock quantity = 1 for flash sale or no promotion product
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(1));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // Add product to cart
                wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART_BTN)).click();

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 30);
            }
        }
    }
}
