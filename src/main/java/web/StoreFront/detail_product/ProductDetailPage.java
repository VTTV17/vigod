package web.StoreFront.detail_product;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.onlineshop.Preferences;
import api.Seller.products.all_products.ProductInformation;
import api.Seller.products.product_reviews.APIProductReviews;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.FlashSale.FlashSaleInfo;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.promotion.ProductDiscountCampaign.BranchDiscountCampaignInfo;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.StoreFront.shoppingcart.ShoppingCart;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;
import static utilities.utils.PropertiesUtil.getPropertiesValueBySFLang;

public class ProductDetailPage extends ProductDetailElement {
    WebDriver driver;
    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction commonAction;
    ProductInfo productInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
    FlashSaleInfo flashSaleInfo;
    Map<String, BranchDiscountCampaignInfo> productDiscountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    List<Boolean> branchStatus;
    LoginInformation loginInformation;
    AssertCustomize assertCustomize;

    public ProductDetailPage(WebDriver driver) {
        // get webDriver
        this.driver = driver;

        // init common method
        commonAction = new UICommonAction(driver);

        // init assert method
        assertCustomize = new AssertCustomize(driver);
    }

    void checkHeader(String language) throws Exception {
        // check store logo
        String sfStoreLogo = commonAction.getAttribute(loc_imgHeaderLogo, "src").replaceAll("\\d+/", "");
        assertCustomize.assertEquals(sfStoreLogo, storeInfo.getStoreLogo(), "[Failed][Header] Store logo should be %s, but found %s.".formatted(storeInfo.getStoreLogo(), sfStoreLogo));
        logger.info("[UI][%s] Check Header - Store Logo".formatted(language));

        // check header menu
        List<String> sfHeaderMenu = IntStream.range(0, commonAction.getListElement(loc_mnuHeaderMenu).size()).mapToObj(index -> commonAction.getText(loc_mnuHeaderMenu, index)).toList();
        List<String> defaultMenu = List.of(getPropertiesValueBySFLang("header.menu.vnStore.0", storeInfo.getDefaultLanguage()), getPropertiesValueBySFLang("header.menu.vnStore.1", storeInfo.getDefaultLanguage()));
        assertCustomize.assertEquals(sfHeaderMenu, defaultMenu, "[Failed][Header] Header menu should be %s, but found %s.".formatted(defaultMenu, sfHeaderMenu));
        logger.info("[UI][%s] Check Header - Menu".formatted(language));

        // check search icon
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnHeaderSearch).isEmpty(), "[Failed][Header] Search icon does not show.");
        logger.info("[UI][%s] Check Header - Search Icon".formatted(language));

        // check cart
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnHeaderCart).isEmpty(), "[Failed][Header] Cart icon does not show.");
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnNumberOfProductsInCart).isEmpty(), "[Failed][Header] Number of products in cart does not show.");
        logger.info("[UI][%s] Check Header - Cart Icon".formatted(language));

        // check profile icon
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnUserProfile).isEmpty(), "[Failed][Header] Profile icon does not show.");
        logger.info("[UI][%s] Check Header - Profile Icon".formatted(language));
    }

    void checkBreadcrumbs(String language) throws Exception {
        // check breadcrumbs
        List<WebElement> breadcrumbsElements = commonAction.getListElement(loc_brcBreadCrumbs);
        List<String> sfBreadCrumbs = IntStream.range(0, breadcrumbsElements.size()).filter(index -> index != 1).mapToObj(index -> commonAction.getText(loc_brcBreadCrumbs, index)).toList();
        List<String> ppBreadCrumbs = List.of(getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), productInfo.getDefaultProductNameMap().get(language));
        assertCustomize.assertTrue(ppBreadCrumbs.toString().equals(sfBreadCrumbs.toString()), "[Failed][Breadcrumbs] Breadcrumbs should be %s, but found %s.".formatted(ppBreadCrumbs, sfBreadCrumbs));
        logger.info("[UI][%s] Check Breadcrumbs".formatted(language));
    }

    void checkProductDetailWhenInStock(String language) throws Exception {
        // quantity
        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            String sfQuantity = commonAction.getText(loc_lblQuantity);
            String quantity = getPropertiesValueBySFLang("productDetail.quantity", language);
            assertCustomize.assertEquals(sfQuantity, quantity, "[Failed][Product Detail] Quantity title should be %s, but found %s.".formatted(quantity, sfQuantity));
            logger.info("[UI][%s] Check Product Detail - Quantity".formatted(language));

            // check buy now
            String sfBuyNow = commonAction.getText(loc_btnBuyNow);
            String buyNow = getPropertiesValueBySFLang("productDetail.cart.buyNow", language);
            assertCustomize.assertEquals(sfBuyNow, buyNow, "[Failed][Product Detail] Buy now title should be %s, but found %s.".formatted(buyNow, sfBuyNow));
            logger.info("[UI][%s] Check Product Detail - Buy Now button".formatted(language));

            // check Add to cart
            String sfAddToCart = commonAction.getText(loc_btnAddToCart);
            String addToCart = getPropertiesValueBySFLang("productDetail.cart.addToCart", language);
            assertCustomize.assertEquals(sfAddToCart, addToCart, "[Failed][Product Detail] Add to cart title should be %s, but found %s.".formatted(addToCart, sfAddToCart));
            logger.info("[UI][%s] Check Product Detail - Add to Cart button".formatted(language));

            // payment
            String sfPayment = commonAction.getText(loc_lblPayment);
            String payment = getPropertiesValueBySFLang("productDetail.payment", language);
            assertCustomize.assertEquals(sfPayment, payment, "[Failed][Product Detail] Payment title should be %s, but found %s.".formatted(payment, sfPayment));
            logger.info("[UI][%s] Check Product Detail - Payment Method".formatted(language));
        }

        if (commonAction.getListElement(loc_lblBranchName).size() > 5) {
            // filter
            String sfFilterBranch = commonAction.getText(loc_lblAllLocations);
            String filterBranch = getPropertiesValueBySFLang("productDetail.branch.filter", language);
            assertCustomize.assertEquals(sfFilterBranch, filterBranch, "[Failed][Filter branch by location] The first filter value should be %s, but found %s.".formatted(filterBranch, sfFilterBranch));
            logger.info("[UI][%s] Check Product Detail - Filter Branch".formatted(language));

            // search
            String sfSearchBranch = commonAction.getText(loc_plhSearchBranchByName);
            String searchBranch = getPropertiesValueBySFLang("productDetail.branch.search", language);
            assertCustomize.assertEquals(sfFilterBranch, filterBranch, "[Failed][Search branch by address] The search placeholder value should be %s, but found %s.".formatted(searchBranch, sfSearchBranch));
            logger.info("[UI][%s] Check Product Detail - Search Branch".formatted(language));
        }

        // check branch
        String sfAvailableBranch = commonAction.getText(loc_lblAvailableBranch).replaceAll("\\d", "");
        String availableBranch = getPropertiesValueBySFLang("productDetail.branch.availableBranch", language).replaceAll("\\d", "");
        assertCustomize.assertEquals(sfAvailableBranch, availableBranch, "[Failed][Product Detail] Available branch title should be %s, but found %s.".formatted(availableBranch, sfAvailableBranch));
        logger.info("[UI][%s] Check Product Detail - Available Branches".formatted(language));

        // check stock
        if (!productInfo.isHideStock()) {
            String sfStockText = commonAction.getText(loc_lblBranchStock, 0).replaceAll("\\d+,*", "");
            String dbStockText = getPropertiesValueBySFLang("productDetail.branch.stock", language).replaceAll("\\d+,*", "");
            assertCustomize.assertEquals(sfStockText, dbStockText, "[Failed][Product Detail] Stock title should be %s, but found %s.".formatted(dbStockText, sfStockText));
            logger.info("[UI][%s] Check Product Detail - Stock in Branch".formatted(language));
        }
    }

    void checkOthersInformation(String language) throws Exception {
        // description tab
        String sfDescriptionTab = commonAction.getText(loc_tabDescription);
        String descriptionTab = getPropertiesValueBySFLang("productDetail.description", language);
        assertCustomize.assertEquals(sfDescriptionTab, descriptionTab, "[Failed][Product Detail] Description tab title should be %s, but found %s.".formatted(descriptionTab, sfDescriptionTab));
        logger.info("[UI][%s] Check Product Detail - Description Tab".formatted(language));

        // review tab
        if (new APIProductReviews(loginInformation).isIsEnableReview()) {
            String sfReviewTab = commonAction.getText(loc_tabReview);
            String reviewTab = getPropertiesValueBySFLang("productDetail.review", language);
            assertCustomize.assertEquals(sfReviewTab, reviewTab, "[Failed][Product Detail] Review tab title should be %s, but found %s.".formatted(reviewTab, sfReviewTab));
            logger.info("[UI][%s] Check Product Detail - Review Tab".formatted(language));
        }

        // similar product
        try {
            if (!commonAction.getListElement(loc_lblSimilarProducts).isEmpty()) {
                String sfSimilarProduct = commonAction.getText(loc_lblSimilarProducts);
                String similarProduct = getPropertiesValueBySFLang("productDetail.similarProduct", language);
                assertCustomize.assertEquals(sfSimilarProduct, similarProduct, "[Failed][Product Detail] Similar Product title should be %s, but found %s.".formatted(similarProduct, sfSimilarProduct));
                logger.info("[UI][%s] Check Product Detail - Similar Product".formatted(language));
            }
        } catch (NoSuchElementException ex) {
            logger.info("No similar product");
        }
    }

    void checkFooter(String language) throws Exception {
        // check store logo
        String sfStoreLogo = commonAction.getAttribute(loc_imgFooterShopLogo, "src").replaceAll("\\d+/", "");
        assertCustomize.assertEquals(sfStoreLogo, storeInfo.getStoreLogo(), "[Failed][Footer] Store logo should be %s, but found %s.".formatted(storeInfo.getStoreLogo(), sfStoreLogo));
        logger.info("[UI][%s] Check Footer - Shop Logo".formatted(language));

        // check company
        String sfCompany = commonAction.getText(loc_lblFooterCompany);
        String company = getPropertiesValueBySFLang("footer.company", language);
        assertCustomize.assertEquals(sfCompany, company, "[Failed][Footer] Company title should be %s, but found %s.".formatted(company, sfCompany));
        logger.info("[UI][%s] Check Footer - Company".formatted(language));

        // check follow us
        String sfFollowUs = commonAction.getText(loc_lblFooterFollowUs);
        String followUs = getPropertiesValueBySFLang("footer.followUs", language);
        assertCustomize.assertEquals(sfFollowUs, followUs, "[Failed][Footer] Follow us title should be %s, but found %s.".formatted(followUs, sfFollowUs));
        logger.info("[UI][%s] Check Footer - Follow Us".formatted(language));

        // check copyright
        String sfCopyright = commonAction.getText(loc_lblFooterCopyright);
        String copyright = getPropertiesValueBySFLang("footer.copyright", language).formatted(new DataGenerator().generateDateTime("yyyy"), new Login().getInfo(loginInformation).getStoreName());
        assertCustomize.assertEquals(sfCopyright, copyright, "[Failed][Footer] Copyright title should be %s, but found %s.".formatted(copyright, sfCopyright));
        logger.info("[UI][%s] Check Footer - Copyright".formatted(language));
    }

    void checkMetaTag(String language) {
        // check SEO title
        if (!productInfo.getSeoMap().get("title").get(language).isEmpty()) {
            String sfSEOTitle = commonAction.getAttribute(loc_seoTitle, "content");
            String dbSEOTitle = productInfo.getSeoMap().get("title").get(language);
            assertCustomize.assertEquals(sfSEOTitle, dbSEOTitle, "[Failed] SEO title should be %s, but found %s.".formatted(dbSEOTitle, sfSEOTitle));
            logger.info("[%s] Check SEO title".formatted(language));
        }

        // check SEO description
        if (!productInfo.getSeoMap().get("description").get(language).isEmpty()) {
            String sfSEODescription = commonAction.getAttribute(loc_seoDescription, "content");
            String dbSEODescription = productInfo.getSeoMap().get("description").get(language);
            assertCustomize.assertEquals(sfSEODescription, dbSEODescription, "[Failed] SEO description should be %s, but found %s.".formatted(dbSEODescription, sfSEODescription));
            logger.info("[%s] Check SEO description".formatted(language));
        }

        // check SEO keywords
        if (!productInfo.getSeoMap().get("keywords").get(language).isEmpty()) {
            String sfSEOKeywords = commonAction.getAttribute(loc_seoKeyword, "content");
            String dbSEOKeywords = productInfo.getSeoMap().get("keywords").get(language);
            assertCustomize.assertEquals(sfSEOKeywords, dbSEOKeywords, "[Failed] SEO keywords should be %s, but found %s.".formatted(dbSEOKeywords, sfSEOKeywords));
            logger.info("[%s] Check SEO keywords".formatted(language));
        }

        // check SEO Url
        if (!productInfo.getSeoMap().get("url").get(language).isEmpty()) {
            String sfSEOUrl = commonAction.getAttribute(loc_seoURL, "content");
            String dbSEOUrl = productInfo.getSeoMap().get("url").get(language);
            assertCustomize.assertTrue(sfSEOUrl.contains(dbSEOUrl), "[Failed] SEO url should be contains %s, but found %s.".formatted(dbSEOUrl, sfSEOUrl));
            logger.info("[%s] Check SEO url".formatted(language));
        }
    }

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
                    (productDiscountCampaignInfo.get(brName) == null) && (wholesaleProductInfo.getStatusMap().get(brName).get(i)) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
            default ->
                    productDiscountCampaignInfo.get(brName) != null ? "DISCOUNT CAMPAIGN" : wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
        }).toList(), (a, b) -> b));
    }

    Map<String, List<String>> getSaleDisplayMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size()).mapToObj(i -> switch (flashSaleInfo.getFlashSaleStatus().get(brName).get(i)) {
            case "IN_PROGRESS", "SCHEDULED" -> "FLASH SALE";
            default ->
                    productDiscountCampaignInfo.get(brName) != null ? "DISCOUNT CAMPAIGN" : wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE";
        }).toList(), (a, b) -> b));
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    void checkProductName(String barcode, String language) {
        // get product name on dashboard
        String dbProductName = productInfo.getProductNameMap().get(barcode).get(language);

        // get product name on shop online
        String sfProductName = commonAction.getText(loc_lblProductName);

        // check product name
        assertCustomize.assertTrue(sfProductName.equals(dbProductName), "[Failed][Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

        logger.info("[Check product name] Check product name show correctly.");
    }

    // BH_8887, BH_8888

    /**
     * Compare product price/currency on the SF with Dashboard
     */
    void checkPriceOnEachBranch(long listingPrice, long sellingPrice, String brName) {
        String branch = brName.isEmpty() ? "" : "[Branch name: %s]".formatted(brName);

        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            if (listingPrice != sellingPrice) {
                String actListingPrice = new UICommonAction(driver).getText(loc_lblListingPrice).replace(",", "");
                assertCustomize.assertEquals(actListingPrice, listingPrice + STORE_CURRENCY, "[Failed]%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
            } else logger.info("No discount product (listing price = selling price)");
            String actSellingPrice = new UICommonAction(driver).getText(loc_lblSellingPrice).replace(",", "");
            long actSellingPriceValue = Long.parseLong(actSellingPrice.replace(STORE_CURRENCY, ""));

            assertCustomize.assertTrue(Math.abs(actSellingPriceValue - sellingPrice) <= 1, "[Failed]%s Selling price should be show %s ±1 instead of %s".formatted(branch, sellingPrice, actSellingPrice));
            logger.info("%s Check product price/ store currency show correctly".formatted(branch));
        } else logger.info("%s Website listing enable, so listing/selling price is hidden".formatted(branch));
    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        assertCustomize.assertFalse(commonAction.getListElement(loc_lblFlashSale).isEmpty(), "%s Flash sale badge does not show".formatted(branch));
        logger.info("%s Check flash sale badge is shown".formatted(branch));
    }


    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        boolean check = true;
        try {
            if (commonAction.getAttribute(loc_chkBuyInBulk, "class").contains("unchecked")) {
                commonAction.clickJS(loc_chkBuyInBulk);
                // wait page loaded
                try {
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply discount campaign.");
                } catch (TimeoutException ex) {
                    logger.info(ex);
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply discount campaign again.");
                }
            } else {
                // uncheck
                commonAction.clickJS(loc_chkBuyInBulk);

                // check again
                commonAction.clickJS(loc_chkBuyInBulk);
                // wait page loaded
                try {
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply discount campaign.");
                } catch (TimeoutException ex) {
                    logger.info(ex);
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply discount campaign again.");
                }
            }
        } catch (NoSuchElementException ex) {
            check = false;
        }

        assertCustomize.assertTrue(check, "%s Discount campaign does not show".formatted(branch));
        logger.info("%s Check discount campaign is shown".formatted(branch));
    }

    // check wholesale product price
    void checkWholesaleProductShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        assertCustomize.assertFalse(commonAction.getListElement(loc_pnlWholesalePricing).isEmpty(), "[Failed]%s Wholesale product information is not shown".formatted(branch));
        logger.info("%s Check wholesale product information is shown".formatted(branch));
    }


    void checkAttribution(List<Boolean> isDisplayAttribute, List<String> attributeGroups, List<String> attributeValues, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        if (isDisplayAttribute.stream().anyMatch(b -> b)) {
            // if product have more than 3 attributes, show all attribute
            if (Collections.frequency(isDisplayAttribute, true) > 3) commonAction.clickJS(loc_btnViewMore);

            // check attribute
            for (int attributeIndex = 0; attributeIndex < attributeGroups.size(); attributeIndex++) {
                if (isDisplayAttribute.get(attributeIndex)) {
                    // check attribute name
                    String sfAttributeName = commonAction.getText(loc_cntAttributeGroup, attributeIndex);
                    assertCustomize.assertEquals(sfAttributeName, attributeGroups.get(attributeIndex),
                            "Attribute name must be '%s', but found '%s'.".formatted(attributeGroups.get(attributeIndex), sfAttributeName));

                    // check attribute value
                    String sfAttributeValue = commonAction.getText(loc_cntAttributeValue, attributeIndex);
                    assertCustomize.assertEquals(sfAttributeValue, attributeValues.get(attributeIndex),
                            "Attribute name must be '%s', but found '%s'.".formatted(attributeGroups.get(attributeIndex), sfAttributeName));
                }
            }
        }

        // log
        logger.info("%s Check product attribute.".formatted(varName));
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(productInfo.getVariationNameMap().get(language).split("\\|")).toList();
        List<WebElement> listElement = commonAction.getListElement(loc_lblVariationName);
        List<String> variationNameListSF = IntStream.range(0, listElement.size()).mapToObj(index -> commonAction.getText(loc_lblVariationName, index)).toList().stream().sorted().toList();

        assertCustomize.assertTrue(variationNameListSF.toString().equalsIgnoreCase(variationNameListDB.toString()), "[Failed][Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListSF));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Filter branch is shown
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnFilterBranch).isEmpty(), "[Failed]%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is displayed.".formatted(varName));

        // check Search branch is shown
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnSearchBranch).isEmpty(), "[Failed]%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("%s Check 'Search box' is displayed.".formatted(varName));
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Filter branch is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_icnFilterBranch).isEmpty(), "[Failed]%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Filter dropdown' is hidden.".formatted(varName));

        // check Search branch is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_icnSearchBranch).isEmpty(), "[Failed]%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Search box' is hidden.".formatted(varName));
    }

    // BH_8616, BH_9536
    void checkBranch(String brElementText, boolean brStatus, int brStock, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch information
        // check branch name
        assertCustomize.assertTrue(brInfo.getBranchName().contains(brElementText) && brStatus && (brStock > 0), "[Failed][Branch name: %s] Branch in-stock but is not shown.".formatted(brElementText));
        logger.info("%s Check branch '%s'".formatted(varName, brElementText));
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    void checkBranchStock(String brElementText, int brElementIndex, boolean brStatus, int brStock, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        if (!productInfo.isHideStock() & brStatus) {
            String brStockElementText = commonAction.getText(loc_lblBranchStock, brElementIndex);
            // check branch stock
            int sfStock = Integer.parseInt(brStockElementText.replaceAll("\\D+", ""));
            assertCustomize.assertEquals(sfStock, brStock, "[Failed]%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, brElementText, brStock, sfStock));
        } else logger.info("Setting hide stock.");
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    void checkProductDescription(String barcode, String language) {
        // get dashboard product description
        String dbDescription = productInfo.getProductDescriptionMap().get(barcode).get(language).replaceAll("<.*?>", "").replaceAll("amp;", "");

        // get SF product description
        String sfDescription = commonAction.getText(loc_pnlDescription).replaceAll("\n", "");

        assertCustomize.assertTrue(sfDescription.equals(dbDescription), "[Failed][Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, sfDescription));
        logger.info("[Check description] Check product description is shown correctly.");
    }

    void checkBuyNowAndAddToCartBtnIsShown(String... variationName) {
        String varName = (variationName.length > 0) ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            assertCustomize.assertFalse(commonAction.getListElement(loc_btnBuyNow).isEmpty(), "[Failed]%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Buy Now' button is displayed.".formatted(varName));

            // check Add to cart button is shown
            assertCustomize.assertFalse(commonAction.getListElement(loc_btnAddToCart).isEmpty(), "[Failed]%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
            logger.info("%s Check 'Add to cart' button is displayed.".formatted(varName));
        } else {
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    void checkBuyNowAndAddToCartBtnIsHidden(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Buy now button is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_btnBuyNow).isEmpty(), "[Failed]%s 'Buy now' button should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Buy Now' button is hidden.".formatted(varName));

        // check Add to cart button is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_btnAddToCart).isEmpty(), "[Failed]%s 'Add to cart' button should be hidden but it is shown.".formatted(varName));
        logger.info("%s Check 'Add to cart' button is hidden.".formatted(varName));
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    void checkSoldOutMark(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0] != null) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        boolean sfSoldOut = commonAction.getText(loc_lblSoldOut).equals("Hết hàng") || commonAction.getText(loc_lblSoldOut).equals("Out of stock");
        assertCustomize.assertTrue(sfSoldOut, "[Failed]%s Sold out mark does not show".formatted(varName));
        logger.info("%s Check 'SOLD OUT' mark is shown".formatted(varName));
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    void check404Page() {
        assertCustomize.assertTrue(driver.getCurrentUrl().contains("404"), "[Failed] 404 is not shown although product out of stock.");
        logger.info("Check 404 page is shown when product out of stock.");
    }

    Long getDiscountCampaignPrice(long sellingPrice, String brName) {
        List<Integer> listOfMinimumRequirements = productDiscountCampaignInfo.get(brName).getListOfMinimumRequirements();
        int minRequirement = Collections.min(listOfMinimumRequirements);
        List<Integer> indexOfAllMinRequirements = IntStream.range(0, listOfMinimumRequirements.size()).filter(index -> listOfMinimumRequirements.get(index).equals(minRequirement)).boxed().toList();

        String couponType = productDiscountCampaignInfo.get(brName).getListOfCouponTypes().get(indexOfAllMinRequirements.get(0));
        long couponValue = productDiscountCampaignInfo.get(brName).getListOfCouponValues().get(indexOfAllMinRequirements.get(0));
        long productDiscountCampaignPrice = couponType.equals("FIXED_AMOUNT")
                ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                : ((sellingPrice * (100 - couponValue)) / 100);

        if (indexOfAllMinRequirements.size() > 1) {
            for (int index = 1; index < indexOfAllMinRequirements.size(); index++) {
                couponType = productDiscountCampaignInfo.get(brName).getListOfCouponTypes().get(index);
                couponValue = productDiscountCampaignInfo.get(brName).getListOfCouponValues().get(index);
                productDiscountCampaignPrice = Math.min(productDiscountCampaignPrice, couponType.equals("FIXED_AMOUNT")
                        ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                        : ((sellingPrice * (100 - couponValue)) / 100));
            }
        }
        return productDiscountCampaignPrice;
    }

    void checkVariationPriceAndDiscount(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, int wholesaleProductStock, long wholesaleProductPrice, String brName) {
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

        // check price
        switch (priceType) {
            // check flash sale price
            case "FLASH SALE" -> checkPriceOnEachBranch(listingPrice, flashSalePrice, brName);
            // check discount campaign price
            case "DISCOUNT CAMPAIGN" ->
                    checkPriceOnEachBranch(listingPrice, getDiscountCampaignPrice(sellingPrice, brName), brName);
            case "WHOLESALE PRODUCT" -> {
                // increase quantity to wholesale product minimum requirement
                commonAction.sendKeys(loc_txtQuantity, String.valueOf(wholesaleProductStock));

                // wait spinner loading if any
                try {
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply wholesale product discount.");
                } catch (TimeoutException ex) {
                    logger.info(ex);
                    commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                    logger.info("Wait page loaded after apply wholesale product discount again.");
                }

                // check wholesale product price
                checkPriceOnEachBranch(listingPrice, wholesaleProductPrice, brName);
            }
            default -> checkPriceOnEachBranch(listingPrice, sellingPrice, brName);
        }
    }

    void checkAllVariationsAndDiscount(int varIndex,
                                       long listingPrice,
                                       long sellingPrice,
                                       long flashSalePrice,
                                       int wholesaleProductStock,
                                       long wholesaleProductPrice,
                                       List<Integer> branchStock,
                                       List<Boolean> isDisplayAttribute,
                                       List<String> attributeGroups,
                                       List<String> attributeValues,
                                       String language,
                                       String... variationName) {
        // get branch info
        brInfo = new BranchManagement(loginInformation).getInfo();

        // check product name
        checkProductName(productInfo.getVariationModelList().get(varIndex), language);

        // check attribute
        checkAttribution(isDisplayAttribute, attributeGroups, attributeValues, variationName);

        // check variation name if any
        if (productInfo.isHasModel()) checkVariationName(language);

        // check description
        checkProductDescription(productInfo.getVariationModelList().get(varIndex), language);

        int numberOfDisplayBranches = IntStream.range(0, brInfo.getAllBranchStatus().size()).filter(i -> !brInfo.getIsHideOnStoreFront().get(i) && brInfo.getAllBranchStatus().get(i).equals("ACTIVE") && (branchStock.get(i) > 0)).mapToObj(i -> true).toList().size();
        if (numberOfDisplayBranches > 0) {
            // check filter/search branch is shown when available branches >= 6
            if (numberOfDisplayBranches >= 6) checkFilterAndSearchBranchIsShown(variationName);
            else checkFilterAndSearchBranchIsHidden(variationName);

            // check Buy Now and Add To Cart button is shown
            checkBuyNowAndAddToCartBtnIsShown(variationName);

            // wait list branch visible
            List<WebElement> branchElementsList = commonAction.getListElement(loc_lblBranchName);

            // check flash sale for each branch
            for (int brElementIndex = 0; brElementIndex < branchElementsList.size(); brElementIndex++) {
                // switch branch
                commonAction.clickJS(loc_lblBranchName, brElementIndex);

                // branch name
                String brName = commonAction.getText(loc_lblBranchName, brElementIndex);

                // check branch stock quantity
                int brIndex = brInfo.getBranchName().indexOf(brName);
                checkBranchStock(brName, brElementIndex, branchStatus.get(brIndex), branchStock.get(brIndex), variationName);
                checkBranch(brName, branchStatus.get(brIndex), branchStock.get(brIndex), variationName);

                // check product price
                checkVariationPriceAndDiscount(varIndex, listingPrice, sellingPrice, flashSalePrice, wholesaleProductStock, wholesaleProductPrice, brName);
            }

        } else {
            checkSoldOutMark(variationName);
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    /**
     * Verify all information on the SF is shown correctly
     */
    void checkProductInformation(String language, int customerId) {
        // get the latest branch information
        brInfo = new BranchManagement(loginInformation).getInfo();
        branchStatus = getBranchStatus();

        // get list segment of customer
        List<Integer> listSegmentOfCustomer = new Customers(loginInformation).getListSegmentOfCustomer(customerId);

        // get flash sale, discount campaign information
        flashSaleInfo = new FlashSale(loginInformation).getFlashSaleInfo(productInfo.getVariationModelList(), productInfo.getProductSellingPrice());
        productDiscountCampaignInfo = new ProductDiscountCampaign(loginInformation).getAllDiscountCampaignInfo(productInfo, listSegmentOfCustomer);

        // get wholesale config
        if (!productInfo.isDeleted())
            wholesaleProductInfo = new ProductInformation(loginInformation).wholesaleProductInfo(productInfo, listSegmentOfCustomer);

        // verify on each variation
        for (String variationValue : productInfo.getVariationListMap().get(language)) {
            // variation index
            int varIndex = productInfo.getVariationListMap().get(language).indexOf(variationValue);

            // ignore if variation inactive
            if (productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) {
                // switch variation if any
                if (productInfo.isHasModel()) {
                    // get variation value
                    List<String> varName = Arrays.stream(variationValue.split("\\|")).toList();
                    logger.info("*** var: %s ***".formatted(variationValue));

                    // select variation
                    for (String var : varName) {
                        int index = varName.indexOf(var);
                        commonAction.clickJS(By.cssSelector(variationDropdownLocator.formatted(index + 1)));
                        logger.info("Open variation dropdown %s.".formatted(index));

                        commonAction.clickJS(By.xpath(variationValueLocator.formatted(var, var)));
                        logger.info("Select variation: %s.".formatted(var));

                        // check variation is selected or not
                        Assert.assertEquals(commonAction.getAttribute(By.cssSelector(selectedLocator.formatted(index + 1)), "title"), var, "[Failed] Can not select variation: %s.".formatted(var));
                    }

                    // wait page loaded
                    try {
                        commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                        logger.info("Wait page loaded after select variation.");
                    } catch (TimeoutException ex) {
                        logger.info(ex);
                        commonAction.waitInvisibilityOfElementLocated(loc_spnLoading);
                        logger.info("Wait page loaded after select variation again.");
                    }
                }

                // get modelCode
                String modelCode = productInfo.getVariationModelList().get(varIndex);

                // check product information
                checkAllVariationsAndDiscount(varIndex,
                        productInfo.getProductListingPrice().get(varIndex),
                        productInfo.getProductSellingPrice().get(varIndex),
                        flashSaleInfo.getFlashSalePrice().get(varIndex),
                        wholesaleProductInfo.getStockList().get(varIndex),
                        wholesaleProductInfo.getPriceList().get(varIndex),
                        productInfo.getProductStockQuantityMap().get(modelCode),
                        productInfo.getIsDisplayVariationAttributes().get(modelCode),
                        productInfo.getVariationAttributeGroups().get(modelCode),
                        productInfo.getVariationAttributeValues().get(modelCode),
                        language,
                        variationValue);
            }
        }
    }

    /**
     * Access to product detail on SF by URL
     */
    public void accessToProductDetailPageByProductIDAndCheckProductInformation(LoginInformation loginInformation, String language, ProductInfo productInfo, int customerId) throws Exception {
        // get login information
        this.loginInformation = loginInformation;

        // get product information
        this.productInfo = productInfo;

        // convert language to languageCode
        String languageCode = language.equals("VIE") ? "vi" : "en";
        // get store language and others information
        storeInfo = new StoreInformation(loginInformation).getInfo();

        // check shop has multiple language or not
        driver.get("https://%s%s/".formatted(storeInfo.getStoreURL(), SF_DOMAIN));

        // get max stock
        int maxStock = productInfo.isDeleted() ? 0 : Collections.max(productInfo.getProductStockQuantityMap().values().stream().map(Collections::max).toList());

        // check product is display or not
        if (!productInfo.isDeleted() && productInfo.isOnWeb() && productInfo.getBhStatus().equals("ACTIVE") && (maxStock > 0 || productInfo.isShowOutOfStock())) {
            // in-case in stock or setting show product when out of stock
            // check language is published or not
            if (storeInfo.getSFLangList().contains(languageCode)) {
                // check all information with language
                if (languageCode.equals(storeInfo.getDefaultLanguage()) || !productInfo.getSeoMap().get("url").get(languageCode).equals(productInfo.getSeoMap().get("url").get(storeInfo.getDefaultLanguage()))) {
                    driver.get("https://%s%s/%s/product/%s".formatted(storeInfo.getStoreURL(), SF_DOMAIN, languageCode, productInfo.getProductID()));
                    driver.navigate().refresh();
                    logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productInfo.getProductID()));

                    // wait product detail page loaded
                    try {
                        commonAction.getElement(loc_lblProductName);
                        logger.info("Wait page loaded.");
                    } catch (TimeoutException ex) {
                        logger.info(ex);
                        driver.get("https://%s%s/%s/product/%s".formatted(storeInfo.getStoreURL(), SF_DOMAIN, languageCode, productInfo.getProductID()));
                        commonAction.getElement(loc_lblProductName);
                        logger.info("Wait page loaded.");
                    }

                    // check UI
                    checkHeader(languageCode);
                    checkBreadcrumbs(languageCode);
                    checkOthersInformation(languageCode);
                    checkFooter(languageCode);
                    checkMetaTag(languageCode);

                    if ((maxStock > 0) && (!commonAction.getListElement(loc_lblBranchName).isEmpty())) {
                        checkProductDetailWhenInStock(languageCode);
                    }
                    checkProductInformation(languageCode, customerId);
                }
            } else logger.info("'%s' language is not published, please publish it and try again.".formatted(language));
        } else {
            // in-case out of stock and setting hide product when out of stock
            // wait 404 page loaded
            driver.get("https://%s%s%s/product/%s".formatted(storeInfo.getStoreURL(), SF_DOMAIN, !storeInfo.getStoreLanguageList().isEmpty() ? "/%s".formatted(storeInfo.getStoreLanguageList().get(0)) : "", productInfo.getProductID()));
            logger.info("Navigate to Product detail page by URL, with productID: %s".formatted(productInfo.getProductID()));

            // wait 404 page loaded
            commonAction.waitURLShouldBeContains("404");

            // check 404 page is shown
            check404Page();
        }

        // complete verify
        if (AssertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(AssertCustomize.getCountFalse()));
        }

    }

    public ShoppingCart clickOnBuyNow() {
        commonAction.click(loc_btnBuyNow);
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

    public String getProductName() {
        String name = commonAction.getText(loc_lblProductName);
        logger.info("Retrieved product name: " + name);
        return name;
    }

    public boolean isReviewTabDisplayed() {
        boolean isDisplayed = !commonAction.getListElement(loc_tabReview).isEmpty();
        logger.info("Is Review tab displayed: " + isDisplayed);
        return isDisplayed;
    }

    public void clickReviewTab() {
        commonAction.click(loc_tabReview);
        logger.info("Clicked on Review tab");
    }

    public List<List<String>> getAllReviews() {
        // Wait until Review tab is present in a period of 10s
        for (int i = 0; i < 10; i++) {
            if (isReviewTabDisplayed()) break;
            commonAction.sleepInMiliSecond(1000);
        }
        List<List<String>> table = new ArrayList<>();
        List<WebElement> reviews = commonAction.getListElement(loc_tblReview);
        for (WebElement eachReview : reviews) {
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

    public void inputRating(int rating) {
        commonAction.clickElement(commonAction.getListElement(loc_icnReviewStar).get(rating - 1));
        logger.info("Rated stars : " + rating);
    }

    public void inputReviewTitle(String reviewTitle) {
        commonAction.sendKeys(loc_lblReviewTitle, reviewTitle);
        logger.info("Input review title: " + reviewTitle);
    }

    public void inputReviewDescription(String reviewDescription) {
        commonAction.sendKeys(loc_lblReviewDescription, reviewDescription);
        logger.info("Input review description: " + reviewDescription);
    }

    public void clickSubmitReviewBtn() {
        commonAction.click(loc_btnSubmitReview);
        logger.info("Clicked on Submit Review button");
    }

    public void leaveReview(int rating, String reviewTitle, String reviewDescription) {
        inputRating(rating);
        inputReviewTitle(reviewTitle);
        inputReviewDescription(reviewDescription);
        clickSubmitReviewBtn();
    }
}
