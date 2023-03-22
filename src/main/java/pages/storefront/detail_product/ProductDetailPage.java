package pages.storefront.detail_product;

import api.dashboard.onlineshop.Preferences;
import api.dashboard.products.ProductInformation;
import api.dashboard.products.ProductReviews;
import api.dashboard.promotion.CreatePromotion;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.shoppingcart.ShoppingCart;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.apiStoreName;
import static api.dashboard.onlineshop.Preferences.enabledProduct;
import static api.dashboard.products.CreateProduct.*;
import static api.dashboard.products.ProductInformation.*;
import static api.dashboard.products.ProductReviews.isEnableReview;
import static api.dashboard.promotion.CreatePromotion.*;
import static api.dashboard.setting.BranchManagement.*;
import static api.dashboard.setting.StoreInformation.*;
import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static pages.dashboard.products.all_products.ProductPage.uiProductID;
import static pages.dashboard.products.all_products.wholesale_price.WholesaleProductPage.*;
import static utilities.PropertiesUtil.getPropertiesValueBySFLang;
import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;

public class ProductDetailPage extends ProductDetailElement {
    WebDriverWait wait;

    int countFail = 0;

    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction commonAction;
    boolean isMultipleLanguage;

    List<Integer> wholesaleProductStock = new ArrayList<>();
    List<Long> wholesaleProductPrice = new ArrayList<>();
    Map<String, List<Boolean>> wholesaleProductStatus = new HashMap<>();

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    public String getProductName() {
        String name = commonAction.getText(PRODUCT_NAME);
        logger.info("Retrieved product name: " + name);
        return name;
    }

    void getProductDiscountInformation() {
        // wholesale product config
        wholesaleProductStock.addAll(uiWholesaleProductStock != null ? uiWholesaleProductStock : apiWholesaleProductStock);

        wholesaleProductPrice.addAll(uiWholesaleProductPrice != null ? uiWholesaleProductPrice : apiWholesaleProductPrice);

        wholesaleProductStatus.putAll(uiWholesaleProductStatus != null ? uiWholesaleProductStatus : apiWholesaleProductStatus);
    }

    /**
     * Access to product detail on SF by URL
     */
    public void accessToProductDetailPageByProductIDAndCheckProductInformation() throws Exception {
        // get store language and others information
        if (apiStoreLanguageList == null) new StoreInformation().getStoreInformation();

        // get product information
        new ProductInformation().get(uiProductID != 0 ? uiProductID : apiProductID);
        System.out.println(uiProductID != 0 ? uiProductID : apiProductID);
        getProductDiscountInformation();

        // check shop has multiple language or not
        driver.get("https://%s%s/".formatted(apiStoreURL, SF_DOMAIN));

        // get max stock
        int maxStock = Collections.max(productStockQuantityMap.values().stream().map(Collections::max).toList());

        // check product is display or not
        if (((maxStock != 0) || (showOutOfStock)) && bhStatus.equals("ACTIVE") && !deleted && onWeb) {
            // in-case in stock or setting show product when out of stock
            // check if shop have multiple language, check all language should be shown exactly
            for (String languageCode : apiSFLangList) {
                // check all information with language
                if (languageCode.equals(apiDefaultLanguage) || !seoMap.get("url").get(languageCode).equals(seoMap.get("url").get(apiDefaultLanguage))) {
                    driver.get("https://%s%s/%s/product/%s".formatted(apiStoreURL, SF_DOMAIN, languageCode, uiProductID != 0 ? uiProductID : apiProductID));
                    logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(uiProductID != 0 ? uiProductID : apiProductID));

                    //wait spinner loaded
                    commonAction.waitForElementInvisible(SPINNER, 30);

                    // wait product detail page loaded
                    commonAction.verifyPageLoaded(defaultProductNameMap.get(languageCode), defaultProductNameMap.get(languageCode));

                    if ((maxStock > 0) && (BRANCH_NAME_LIST.size() > 0)) {
                        checkUIInStock(languageCode);
                        checkProductInformation(languageCode);
                    } else {
                        checkUIOutOfStock(languageCode);

                    }
                }

            }

        } else {
            // in-case out of stock and setting hide product when out of stock
            // wait 404 page loaded
            driver.get("https://%s%s%s/product/%s".formatted(apiStoreURL, SF_DOMAIN, isMultipleLanguage ? "/%s".formatted(apiStoreLanguageList.get(0)) : "", uiProductID != 0 ? uiProductID : apiProductID));
            logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(uiProductID != 0 ? uiProductID : apiProductID));

            // sleep 1s
            commonAction.sleepInMiliSecond(1000);

            // wait spinner loaded if any
            commonAction.waitForElementInvisible(SPINNER, 15);

            // check 404 page is shown
            check404Page();
        }

        // complete verify
        if (countFail + ProductPage.countFail > 0) {
            int count = countFail + ProductPage.countFail;
            countFail = 0;
            ProductPage.countFail = 0;
            Assert.fail("[Failed] Fail %d cases".formatted(count));
        }

    }

    void checkHeader(String language) throws Exception {
        // check store logo
        String sfStoreLogo = wait.until(ExpectedConditions.visibilityOf(HEADER_SHOP_LOGO)).getAttribute("src").replace("/200/", "/");
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfStoreLogo, apiStoreLogo, "[Failed][Header] Store logo should be %s, but found %s.".formatted(apiStoreLogo, sfStoreLogo));
        logger.info("[UI][%s] Check Header - Store Logo".formatted(language));

        // check header menu
        commonAction.waitElementList(HEADER_MENU, 2);
        List<String> sfHeaderMenu = HEADER_MENU.stream().map(WebElement::getText).toList();
        List<String> defaultMenu = List.of(getPropertiesValueBySFLang("header.menu.vnStore.0", apiDefaultLanguage), getPropertiesValueBySFLang("header.menu.vnStore.1", apiDefaultLanguage));
        countFail = new AssertCustomize(driver).assertEquals(countFail, sfHeaderMenu, defaultMenu, "[Failed][Header] Header menu should be %s, but found %s.".formatted(defaultMenu, sfHeaderMenu));
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
        List<String> sfBreadCrumbs = BREAD_CRUMBS.stream().map(webElement -> webElement.getText().trim()).toList();
        List<List<String>> ppBreadCrumbs = new ArrayList<>();
        ppBreadCrumbs.add(List.of(getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), getPropertiesValueBySFLang("productDetail.breadCrumbs.1", language), defaultProductNameMap.get(language)));
        if (collectionNameMap.keySet().size() > 0)
            ppBreadCrumbs.add(List.of(getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), collectionNameMap.get(collectionNameMap.keySet().stream().toList().get(0)).get(language).trim(), defaultProductNameMap.get(language)));
        countFail = new AssertCustomize(driver).assertTrue(countFail, ppBreadCrumbs.stream().anyMatch(breadCrumbs -> breadCrumbs.toString().equals(sfBreadCrumbs.toString())), "[Failed][Breadcrumbs] Breadcrumbs should be %s, but found %s.".formatted(ppBreadCrumbs, sfBreadCrumbs));
        logger.info("[UI][%s] Check Breadcrumbs".formatted(language));
    }

    void checkProductDetailWhenInStock(String language) throws Exception {
        new Preferences().getListingWebsiteConfig();
        // quantity
        if (!(enabledProduct && enabledListing)) {
            String sfQuantity = wait.until(ExpectedConditions.visibilityOf(QUANTITY_TITLE)).getText();
            String quantity = getPropertiesValueBySFLang("productDetail.quantity", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfQuantity, quantity, "[Failed][Product Detail] Quantity title should be %s, but found %s.".formatted(quantity, sfQuantity));
            logger.info("[UI][%s] Check Product Detail - Quantity".formatted(language));

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
        new ProductReviews().getProductReviewsConfig();
        if (isEnableReview) {
            String sfReviewTab = wait.until(ExpectedConditions.visibilityOf(REVIEW_TAB)).getText();
            String reviewTab = getPropertiesValueBySFLang("productDetail.review", language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfReviewTab, reviewTab, "[Failed][Product Detail] Review tab title should be %s, but found %s.".formatted(reviewTab, sfReviewTab));
            logger.info("[UI][%s] Check Product Detail - Review Tab".formatted(language));
        }

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

    void checkMetaTag(String language) throws IOException {
        // check SEO title
        if (!seoMap.get("title").get(language).equals("")) {
            String sfSEOTitle = META_TITLE.getAttribute("content");
            String dbSEOTitle = seoMap.get("title").get(language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfSEOTitle, dbSEOTitle, "[Failed] SEO title should be %s, but found %s.".formatted(dbSEOTitle, sfSEOTitle));
            logger.info("[%s] Check SEO title".formatted(language));
        }

        // check SEO description
        if (!seoMap.get("description").get(language).equals("")) {
            String sfSEODescription = META_DESCRIPTION.getAttribute("content");
            String dbSEODescription = seoMap.get("description").get(language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfSEODescription, dbSEODescription, "[Failed] SEO description should be %s, but found %s.".formatted(dbSEODescription, sfSEODescription));
            logger.info("[%s] Check SEO description".formatted(language));
        }

        // check SEO keywords
        if (!seoMap.get("keywords").get(language).equals("")) {
            String sfSEOKeywords = META_KEYWORD.getAttribute("content");
            String dbSEOKeywords = seoMap.get("keywords").get(language);
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfSEOKeywords, dbSEOKeywords, "[Failed] SEO keywords should be %s, but found %s.".formatted(dbSEOKeywords, sfSEOKeywords));
            logger.info("[%s] Check SEO keywords".formatted(language));
        }

        // check SEO Url
        if (!seoMap.get("url").get(language).equals("")) {
            String sfSEOUrl = META_URL.getAttribute("content");
            String dbSEOUrl = seoMap.get("url").get(language);
            countFail = new AssertCustomize(driver).assertTrue(countFail, sfSEOUrl.contains(dbSEOUrl), "[Failed] SEO url should be contains %s, but found %s.".formatted(dbSEOUrl, sfSEOUrl));
            logger.info("[%s] Check SEO url".formatted(language));
        }
    }

    void checkUIInStock(String language) throws Exception {
        checkHeader(language);
        checkBreadcrumbs(language);
        checkProductDetailWhenInStock(language);
        checkOthersInformation(language);
        checkFooter(language);
        checkMetaTag(language);
    }

    void checkUIOutOfStock(String language) throws Exception {
        checkHeader(language);
        checkBreadcrumbs(language);
        checkProductDetailWhenOutOfStock(language);
        checkOthersInformation(language);
        checkFooter(language);
        checkMetaTag(language);
    }

    /**
     * <p> countFail: The number of failure cases in this test</p>
     * <p> If countFail > 0, some cases have been failed</p>
     * <p> Reset countFail for the next test</p>
     */
    public void completeVerify() {
        if (countFail + ProductPage.countFail > 0) {
            int count = countFail + ProductPage.countFail;
            countFail = 0;
            ProductPage.countFail = 0;
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
    void checkPriceOnEachBranch(long listingPrice, long sellingPrice, String apiBranchName) throws IOException {
        String branch = apiBranchName.equals("") ? "" : "[Branch name: %s]".formatted(apiBranchName);

        if (!(enabledProduct && enabledListing)) {
            if (listingPrice != sellingPrice) {
                String actListingPrice = new UICommonAction(driver).getText(LISTING_PRICE).replace(",", "");
                countFail = new AssertCustomize(driver).assertEquals(countFail, actListingPrice, listingPrice + STORE_CURRENCY, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
            }

            String actSellingPrice = new UICommonAction(driver).getText(SELLING_PRICE).replace(",", "");
            long actSellingPriceValue = Long.parseLong(actSellingPrice.replace(STORE_CURRENCY, ""));

            countFail = new AssertCustomize(driver).assertTrue(countFail, Math.abs(actSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, actSellingPrice));
            logger.info("%s Check product price/ store currency show correctly".formatted(branch));
        } else logger.info("%s Website listing enable, so listing/selling price is hidden".formatted(branch));
    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String apiBranchName) {
        String branch = "[Branch name: %s]".formatted(apiBranchName);
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
    void checkDiscountCampaignShouldBeShown(String apiBranchName) {
        String branch = "[Branch name: %s]".formatted(apiBranchName);
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
    void checkWholesaleProductShouldBeShown(String apiBranchName) {
        String branch = "[Branch name: %s]".formatted(apiBranchName);
        boolean check = true;
        try {
            WHOLESALE_PRODUCT_INFO.getText();
        } catch (NoSuchElementException ex) {
            check = false;
        }
        countFail = new AssertCustomize(driver).assertTrue(countFail, check, "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }

    public void checkVariationPriceAndDiscount(int indexOfVariation, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, String apiBranchName) throws IOException {
        String priceType = getSalePriceMap().get(apiBranchName).get(indexOfVariation);
        String displayType = getSaleDisplayMap().get(apiBranchName).get(indexOfVariation);

        // check badge
        switch (displayType) {
            // check flash sale badge is shown
            case "FLASH SALE" -> checkFlashSaleShouldBeShown(apiBranchName);
            // check discount campaign is shown
            case "DISCOUNT CAMPAIGN" -> checkDiscountCampaignShouldBeShown(apiBranchName);
            // check wholesale product is shown
            case "WHOLESALE PRODUCT" -> checkWholesaleProductShouldBeShown(apiBranchName);
        }

        // check price
        switch (priceType) {
            // check flash sale price
            case "FLASH SALE" -> checkPriceOnEachBranch(listingPrice, flashSalePrice, apiBranchName);
            // check discount campaign price
            case "DISCOUNT CAMPAIGN" ->
                    checkPriceOnEachBranch(listingPrice, productDiscountCampaignPrice, apiBranchName);
            case "WHOLESALE PRODUCT" -> {
                // increase quantity to wholesale product minimum requirement
                wait.until(elementToBeClickable(QUANTITY)).click();
                QUANTITY.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
                QUANTITY.sendKeys(String.valueOf(wholesaleProductStock));

                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);

                // check wholesale product price
                checkPriceOnEachBranch(listingPrice, wholesaleProductPrice, apiBranchName);
            }
            default -> checkPriceOnEachBranch(listingPrice, sellingPrice, apiBranchName);
        }
    }

    // BH_8616, BH_9536
    void checkBranch(List<Integer> branchStock, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch on online shop
        // if branch stock > 0 and branch is hidden on sf => sold out is shown
        // else branch stock > 0 and branch is shown => check branch and stock
        int numberOfDisplayBranches = IntStream.range(0, apiAllBranchStatus.size()).filter(i -> !apiIsHideOnStoreFront.get(i) && apiAllBranchStatus.get(i).equals("ACTIVE") && (branchStock.get(i) > 0)).mapToObj(i -> true).toList().size();
        if (numberOfDisplayBranches > 0) {
            if (numberOfDisplayBranches > 5) {
                checkFilterAndSearchBranchIsShown(variationName);
            } else {
                checkFilterAndSearchBranchIsHidden(variationName);
            }

            // wait list branch visible
            commonAction.waitElementList(BRANCH_NAME_LIST);

            // SF branch list:
            List<String> sfBranchList = BRANCH_NAME_LIST.stream().map(WebElement::getText).toList();

            // Call API and get Branch setting info
            new BranchManagement().getBranchInformation();
            List<String> branchStatus = apiBranchID.stream().mapToInt(brID -> brID).mapToObj(id -> (!apiIsHideOnStoreFront.get(apiBranchID.indexOf(id)) && apiAllBranchStatus.get(apiBranchID.indexOf(id)).equals("ACTIVE")) ? "SHOW" : "HIDE").toList();

            for (int i = 0; i < branchStock.size(); i++) {
                String branch = apiBranchName.get(i);
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
    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(variationNameMap.get(language).replace("|", " ").split(" ")).toList();
        List<String> variationNameListSF = LIST_VARIATION_NAME.stream().map(element -> element.getText().toLowerCase()).toList().stream().sorted().toList();

        countFail = new AssertCustomize(driver).assertTrue(countFail, variationNameListSF.toString().equals(variationNameListDB.toString()), "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListSF));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    void checkProductName(String barcode, String language) {
        // get product name on dashboard
        String dbProductName = productNameMap.get(barcode).get(language);

        // get product name on shop online
        String sfProductName = wait.until(visibilityOf(PRODUCT_NAME)).getText();

        // check product name
        countFail = new AssertCustomize(driver).assertTrue(countFail, sfProductName.equals(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    void checkStock(boolean isHideStock, int index, int stockQuantity, String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        if (!isHideStock) {
            // wait list branch stock visible
            commonAction.waitElementList(STOCK_QUANTITY_IN_BRANCH);

            // get stock on shop online
            String sfStock = wait.until(visibilityOf(STOCK_QUANTITY_IN_BRANCH.get(index))).getText().replace("Còn hàng ", "").replace(" in stock", "").replace(",", "");
            countFail = new AssertCustomize(driver).assertEquals(countFail, sfStock, String.valueOf(stockQuantity), "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, BRANCH_NAME_LIST.get(index).getText(), stockQuantity, sfStock));

            logger.info("%s[Branch name: %s] Check current stock quantity".formatted(varName, BRANCH_NAME_LIST.get(index).getText()));
        } else logger.info("%s[Check stock] Setting hide stock on StoreFront.".formatted(varName));
    }

    void checkBuyNowAndAddToCartBtnIsShown(String... variationName) throws IOException {
        String varName = (variationName.length > 0) ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(enabledProduct && enabledListing)) {
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
        } else {
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    void checkBuyNowAndAddToCartBtnIsHidden(String... variationName) throws IOException {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
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
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
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
    void checkProductDescription(String barcode, String language) {
        // get dashboard product description
        String dbDescription = productDescriptionMap.get(barcode).get(language);

        // get SF product description
        String sfDescription = wait.until(visibilityOf(PRODUCT_DESCRIPTION)).getText();

        countFail = new AssertCustomize(driver).assertTrue(countFail, sfDescription.equals(dbDescription), "[Failed][Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, sfDescription));
        logger.info("[Check description] Check product description is shown correctly.");
    }

    void checkAllVariationsAndDiscount(int index, long listingPrice, long sellingPrice, long flashSalePrice, long productDiscountCampaignPrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String language, String... variationName) throws IOException {
        // log
        if (variationName.length > 0)
            if (variationName[0] != null) logger.info("*** var: %s ***".formatted(variationName[0]));

        // check product name
        checkProductName(barcodeList.get(index), language);

        // check variation name if any
        if (hasModel) checkVariationName(language);

        // check description
        checkProductDescription(barcodeList.get(index), language);

        int numberOfDisplayBranches = IntStream.range(0, apiAllBranchStatus.size()).filter(i -> !apiIsHideOnStoreFront.get(i) && apiAllBranchStatus.get(i).equals("ACTIVE") && (branchStock.get(i) > 0)).mapToObj(i -> true).toList().size();
        if (variationStatus.get(index).equals("ACTIVE")) {
            if (numberOfDisplayBranches > 0) {

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
                    String branch = ((JavascriptExecutor) driver).executeScript("return arguments[0].textContent", element).toString();

                    // check branch stock quantity
                    int id = BRANCH_NAME_LIST.indexOf(element);
                    checkStock(isHideStock, id, branchStock.get(apiBranchName.indexOf(branch)));

                    // check product price
                    checkVariationPriceAndDiscount(index, listingPrice, sellingPrice, flashSalePrice, productDiscountCampaignPrice, wholesaleProductStock, wholesaleProductPrice, element.getText());
                }
                checkBranch(branchStock, variationName);
            } else {
                checkSoldOutMark(variationName);
                checkBuyNowAndAddToCartBtnIsHidden(variationName);
            }
        } else {

        }
    }

    /**
     * Verify all information on the SF is shown correctly
     */
    void checkProductInformation(String language) throws InterruptedException, IOException {
        new CreatePromotion().getCurrentFlashSaleInformation(barcodeList, productSellingPrice)
                .getCurrentDiscountCampaignInformation(barcodeList, productSellingPrice);

        // verify on each variation
        for (String variationValue : variationListMap.get(language)) {
            // variation index
            int varIndex = variationListMap.get(language).indexOf(variationValue);

            // switch variation if any
            if (hasModel) {
                // get variation value
                String[] varName = variationValue.replace("|", " ").split(" ");

                // wait list variation value is visible
                sleep(3000);

                // select variation
                for (String var : varName) {
                    for (WebElement webElement : LIST_VARIATION_VALUE) {
                        System.out.println(((JavascriptExecutor) driver).executeScript("return arguments[0].textContent", webElement).toString());
                        System.out.println(var);
                        if (((JavascriptExecutor) driver).executeScript("return arguments[0].textContent", webElement).toString().contains(var)) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", webElement);
                            break;
                        }
                    }
                }


                // wait spinner loading if any
                commonAction.waitForElementInvisible(SPINNER, 15);
            }

            // check product information
            checkAllVariationsAndDiscount(varIndex, productListingPrice.get(varIndex), productSellingPrice.get(varIndex), apiFlashSalePrice.get(varIndex), apiDiscountCampaignPrice.get(varIndex), wholesaleProductStock.get(varIndex), wholesaleProductPrice.get(varIndex), productStockQuantityMap.get(barcodeList.get(varIndex)), language, variationValue);
        }
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
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
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
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
        return apiBranchName.stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, apiFlashSaleStatus.get(brName).size()).mapToObj(i -> apiFlashSaleStatus.get(brName).get(i).equals("IN_PROGRESS") ? "FLASH SALE" : (apiFlashSaleStatus.get(brName).get(i).equals("SCHEDULE") ? (apiDiscountCampaignStatus.get(brName).get(i).equals("IN_PROGRESS") ? (!hasModel ? "DISCOUNT CAMPAIGN" : "SELLING PRICE") : (wholesaleProductStatus.get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE")) : (apiDiscountCampaignStatus.get(brName).get(i).equals("IN_PROGRESS") ? "DISCOUNT CAMPAIGN" : (wholesaleProductStatus.get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE")))).toList(), (a, b) -> b));
    }

    public Map<String, List<String>> getSaleDisplayMap() {
        return apiBranchName.stream().collect(Collectors.toMap(s1 -> s1, s1 -> IntStream.range(0, apiFlashSaleStatus.get(s1).size()).mapToObj(i -> !apiFlashSaleStatus.get(s1).get(i).equals("EXPIRED") ? "FLASH SALE" : apiDiscountCampaignStatus.get(s1).get(i).equals("IN_PROGRESS") ? "DISCOUNT CAMPAIGN" : wholesaleProductStatus.get(s1).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE").toList(), (a, b) -> b));
    }

    public boolean isReviewTabDisplayed() {
        boolean isDisplayed = REVIEW_TAB.isDisplayed();
        logger.info("Is Review tab displayed: " + isDisplayed);
        return isDisplayed;
    }

    public ProductDetailPage clickReviewTab() {
        commonAction.clickElement(REVIEW_TAB);
        logger.info("Clicked on Review tab");
        return this;
    }

    public List<List<String>> getAllReviews() {
        commonAction.sleepInMiliSecond(1000);
        List<List<String>> table = new ArrayList<>();
        for (WebElement eachReview : REVIEWS) {
            List<String> reviewData = new ArrayList<>();
            reviewData.add(String.valueOf(eachReview.findElements(By.xpath(".//span[@style='color: rgb(255, 176, 0);']")).size())); //Rating
            reviewData.add(eachReview.findElement(By.xpath(".//span[@rv-text='review.userName']")).getText()); //Reviewer
            reviewData.add(eachReview.findElement(By.xpath(".//span[contains(@rv-text,'review.reviewDate')]")).getText()); //Review date
            reviewData.add(eachReview.findElement(By.xpath(".//div[@class='title']")).getText()); //Review title
            reviewData.add(eachReview.findElement(By.xpath(".//div[@class='description']")).getText()); //Review description
            table.add(reviewData);
        }
        return table;
    }

    public ProductDetailPage inputRating(int rating) {
        commonAction.clickElement(RATING_STARS.get(rating - 1));
        logger.info("Rated stars : " + rating);
        return this;
    }

    public ProductDetailPage inputReviewTitle(String reviewTitle) {
        commonAction.inputText(REVIEW_TITLE, reviewTitle);
        logger.info("Input review title: " + reviewTitle);
        return this;
    }

    public ProductDetailPage inputReviewDescription(String reviewDescription) {
        commonAction.inputText(REVIEW_DESCRIPTION, reviewDescription);
        logger.info("Input review description: " + reviewDescription);
        return this;
    }

    public ProductDetailPage clickSubmitReviewBtn() {
        commonAction.clickElement(SUBMIT_REVIEW_BTN);
        logger.info("Clicked on Submit Review button");
        return this;
    }

    public ProductDetailPage leaveReview(int rating, String reviewTitle, String reviewDescription) {
        inputRating(rating);
        inputReviewTitle(reviewTitle);
        inputReviewDescription(reviewDescription);
        clickSubmitReviewBtn();
        return this;
    }


}
