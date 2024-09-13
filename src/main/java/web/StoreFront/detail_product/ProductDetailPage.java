package web.StoreFront.detail_product;

import api.Buyer.productdetail.APIGetDiscountCampaignInformation;
import api.Buyer.productdetail.APIGetFlashSaleInformation;
import api.Buyer.productdetail.APIGetWholesaleProductInformation;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import api.Seller.products.product_reviews.APIProductReviews;
import api.Seller.sale_channel.onlineshop.Preferences;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.StoreFront.shoppingcart.ShoppingCart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;
import static utilities.utils.PropertiesUtil.getPropertiesValueBySFLang;

public class ProductDetailPage extends ProductDetailElement {
    WebDriver driver;
    Logger logger = LogManager.getLogger(ProductDetailPage.class);
    UICommonAction commonAction;
    ProductInfoV2 productInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
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
        assertCustomize.assertEquals(sfStoreLogo, storeInfo.getStoreLogo(), "[Header] Store logo should be %s, but found %s.".formatted(storeInfo.getStoreLogo(), sfStoreLogo));
        logger.info("[UI][{}] Check Header - Store Logo", language);

        // check header menu
        List<String> sfHeaderMenu = IntStream.range(0, commonAction.getListElement(loc_mnuHeaderMenu).size()).mapToObj(index -> commonAction.getText(loc_mnuHeaderMenu, index)).toList();
        List<String> defaultMenu = List.of(getPropertiesValueBySFLang("header.menu.vnStore.0", storeInfo.getDefaultLanguage()), getPropertiesValueBySFLang("header.menu.vnStore.1", storeInfo.getDefaultLanguage()));
        assertCustomize.assertEquals(sfHeaderMenu, defaultMenu, "[Header] Header menu should be %s, but found %s.".formatted(defaultMenu, sfHeaderMenu));
        logger.info("[UI][{}] Check Header - Menu", language);

        // check search icon
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnHeaderSearch).isEmpty(), "[Header] Search icon does not show.");
        logger.info("[UI][{}] Check Header - Search Icon", language);

        // check cart
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnHeaderCart).isEmpty(), "[Header] Cart icon does not show.");
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnNumberOfProductsInCart).isEmpty(), "[Header] Number of products in cart does not show.");
        logger.info("[UI][{}] Check Header - Cart Icon", language);

        // check profile icon
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnUserProfile).isEmpty(), "[Header] Profile icon does not show.");
        logger.info("[UI][{}] Check Header - Profile Icon", language);
    }

    void checkBreadcrumbs(String language) throws Exception {
        // check breadcrumbs
        List<WebElement> breadcrumbsElements = commonAction.getListElement(loc_brcBreadCrumbs);
        List<String> sfBreadCrumbs = IntStream.range(0, breadcrumbsElements.size()).filter(index -> index != 1).mapToObj(index -> commonAction.getText(loc_brcBreadCrumbs, index)).toList();
        List<String> ppBreadCrumbs = List.of(getPropertiesValueBySFLang("productDetail.breadCrumbs.0", language), productInfo.getMainProductNameMap().get(language));
        assertCustomize.assertTrue(ppBreadCrumbs.toString().equals(sfBreadCrumbs.toString()), "[Breadcrumbs] Breadcrumbs should be %s, but found %s.".formatted(ppBreadCrumbs, sfBreadCrumbs));
        logger.info("[UI][{}] Check Breadcrumbs", language);
    }

    void checkProductDetailWhenInStock(String language) throws Exception {
        // quantity
        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            String sfQuantity = commonAction.getText(loc_lblQuantity);
            String quantity = getPropertiesValueBySFLang("productDetail.quantity", language);
            assertCustomize.assertEquals(sfQuantity, quantity, "[Product Detail] Quantity title should be %s, but found %s.".formatted(quantity, sfQuantity));
            logger.info("[UI][{}] Check Product Detail - Quantity", language);

            // check buy now
            String sfBuyNow = commonAction.getText(loc_btnBuyNow);
            String buyNow = getPropertiesValueBySFLang("productDetail.cart.buyNow", language);
            assertCustomize.assertEquals(sfBuyNow, buyNow, "[Product Detail] Buy now title should be %s, but found %s.".formatted(buyNow, sfBuyNow));
            logger.info("[UI][{}] Check Product Detail - Buy Now button", language);

            // check Add to cart
            String sfAddToCart = commonAction.getText(loc_btnAddToCart);
            String addToCart = getPropertiesValueBySFLang("productDetail.cart.addToCart", language);
            assertCustomize.assertEquals(sfAddToCart, addToCart, "[Product Detail] Add to cart title should be %s, but found %s.".formatted(addToCart, sfAddToCart));
            logger.info("[UI][{}] Check Product Detail - Add to Cart button", language);

            // payment
            String sfPayment = commonAction.getText(loc_lblPayment);
            String payment = getPropertiesValueBySFLang("productDetail.payment", language);
            assertCustomize.assertEquals(sfPayment, payment, "[Product Detail] Payment title should be %s, but found %s.".formatted(payment, sfPayment));
            logger.info("[UI][{}] Check Product Detail - Payment Method", language);
        }

        if (commonAction.getListElement(loc_lblBranchName).size() > 5) {
            // filter
            String sfFilterBranch = commonAction.getText(loc_lblAllLocations);
            String filterBranch = getPropertiesValueBySFLang("productDetail.branch.filter", language);
            assertCustomize.assertEquals(sfFilterBranch, filterBranch, "[Filter branch by location] The first filter value should be %s, but found %s.".formatted(filterBranch, sfFilterBranch));
            logger.info("[UI][{}] Check Product Detail - Filter Branch", language);

            // search
            String sfSearchBranch = commonAction.getText(loc_plhSearchBranchByName);
            String searchBranch = getPropertiesValueBySFLang("productDetail.branch.search", language);
            assertCustomize.assertEquals(sfFilterBranch, filterBranch, "[Search branch by address] The search placeholder value should be %s, but found %s.".formatted(searchBranch, sfSearchBranch));
            logger.info("[UI][{}] Check Product Detail - Search Branch", language);
        }

        // check branch
        String sfAvailableBranch = commonAction.getText(loc_lblAvailableBranch).replaceAll("\\d", "");
        String availableBranch = getPropertiesValueBySFLang("productDetail.branch.availableBranch", language).replaceAll("\\d", "");
        assertCustomize.assertEquals(sfAvailableBranch, availableBranch, "[Product Detail] Available branch title should be %s, but found %s.".formatted(availableBranch, sfAvailableBranch));
        logger.info("[UI][{}] Check Product Detail - Available Branches", language);

        // check stock
        if (!productInfo.isHideStock()) {
            String sfStockText = commonAction.getText(loc_lblBranchStock, 0).replaceAll("\\d+,*", "");
            String dbStockText = getPropertiesValueBySFLang("productDetail.branch.stock", language).replaceAll("\\d+,*", "");
            assertCustomize.assertEquals(sfStockText, dbStockText, "[Product Detail] Stock title should be %s, but found %s.".formatted(dbStockText, sfStockText));
            logger.info("[UI][{}] Check Product Detail - Stock in Branch", language);
        }
    }

    void checkOthersInformation(String language) throws Exception {
        // description tab
        String sfDescriptionTab = commonAction.getText(loc_tabDescription);
        String descriptionTab = getPropertiesValueBySFLang("productDetail.description", language);
        assertCustomize.assertEquals(sfDescriptionTab, descriptionTab, "[Product Detail] Description tab title should be %s, but found %s.".formatted(descriptionTab, sfDescriptionTab));
        logger.info("[UI][{}] Check Product Detail - Description Tab", language);

        // review tab
        if (new APIProductReviews(loginInformation).isIsEnableReview()) {
            String sfReviewTab = commonAction.getText(loc_tabReview);
            String reviewTab = getPropertiesValueBySFLang("productDetail.review", language);
            assertCustomize.assertEquals(sfReviewTab, reviewTab, "[Product Detail] Review tab title should be %s, but found %s.".formatted(reviewTab, sfReviewTab));
            logger.info("[UI][{}] Check Product Detail - Review Tab", language);
        }

        // similar product
        try {
            if (!commonAction.getListElement(loc_lblSimilarProducts).isEmpty()) {
                String sfSimilarProduct = commonAction.getText(loc_lblSimilarProducts);
                String similarProduct = getPropertiesValueBySFLang("productDetail.similarProduct", language);
                assertCustomize.assertEquals(sfSimilarProduct, similarProduct, "[Product Detail] Similar Product title should be %s, but found %s.".formatted(similarProduct, sfSimilarProduct));
                logger.info("[UI][{}] Check Product Detail - Similar Product", language);
            }
        } catch (NoSuchElementException ex) {
            logger.info("No similar product");
        }
    }

    void checkFooter(String language) throws Exception {
        // check store logo
        String sfStoreLogo = commonAction.getAttribute(loc_imgFooterShopLogo, "src").replaceAll("\\d+/", "");
        assertCustomize.assertEquals(sfStoreLogo, storeInfo.getStoreLogo(), "[Footer] Store logo should be %s, but found %s.".formatted(storeInfo.getStoreLogo(), sfStoreLogo));
        logger.info("[UI][{}] Check Footer - Shop Logo", language);

        // check company
        String sfCompany = commonAction.getText(loc_lblFooterCompany);
        String company = getPropertiesValueBySFLang("footer.company", language);
        assertCustomize.assertEquals(sfCompany, company, "[Footer] Company title should be %s, but found %s.".formatted(company, sfCompany));
        logger.info("[UI][{}] Check Footer - Company", language);

        // check follow us
        String sfFollowUs = commonAction.getText(loc_lblFooterFollowUs);
        String followUs = getPropertiesValueBySFLang("footer.followUs", language);
        assertCustomize.assertEquals(sfFollowUs, followUs, "[Footer] Follow us title should be %s, but found %s.".formatted(followUs, sfFollowUs));
        logger.info("[UI][{}] Check Footer - Follow Us", language);

        // check copyright
        String sfCopyright = commonAction.getText(loc_lblFooterCopyright);
        String copyright = getPropertiesValueBySFLang("footer.copyright", language).formatted(new DataGenerator().generateDateTime("yyyy"), new Login().getInfo(loginInformation).getStoreName());
        assertCustomize.assertEquals(sfCopyright, copyright, "[Footer] Copyright title should be %s, but found %s.".formatted(copyright, sfCopyright));
        logger.info("[UI][{}] Check Footer - Copyright", language);
    }

    void checkMetaTag(String language) {
        // Get main language
        var languages = productInfo.getLanguages().parallelStream().filter(mainLanguage -> mainLanguage.getLanguage().equals(language)).findAny().orElse(null);

        // check SEO title
        if ((languages != null) && (languages.getSeoTitle() != null)) {
            String sfSEOTitle = commonAction.getAttribute(loc_seoTitle, "content");
            String dbSEOTitle = languages.getSeoTitle();
            assertCustomize.assertEquals(sfSEOTitle, dbSEOTitle, " SEO title should be %s, but found %s.".formatted(dbSEOTitle, sfSEOTitle));
            logger.info("[{}] Check SEO title", language);
        }

        // check SEO description
        if ((languages != null) && (languages.getSeoDescription() != null)) {
            String sfSEODescription = commonAction.getAttribute(loc_seoDescription, "content");
            String dbSEODescription = languages.getSeoDescription();
            assertCustomize.assertEquals(sfSEODescription, dbSEODescription, " SEO description should be %s, but found %s.".formatted(dbSEODescription, sfSEODescription));
            logger.info("[{}] Check SEO description", language);
        }

        // check SEO keywords
        if ((languages != null) && (languages.getSeoKeywords() != null)) {
            String sfSEOKeywords = commonAction.getAttribute(loc_seoKeyword, "content");
            String dbSEOKeywords = languages.getSeoKeywords();
            assertCustomize.assertEquals(sfSEOKeywords, dbSEOKeywords, " SEO keywords should be %s, but found %s.".formatted(dbSEOKeywords, sfSEOKeywords));
            logger.info("[{}] Check SEO keywords", language);
        }

        // check SEO Url
        if ((languages != null) && (languages.getSeoUrl() != null)) {
            String sfSEOUrl = commonAction.getAttribute(loc_seoURL, "content");
            String dbSEOUrl = languages.getSeoUrl();
            assertCustomize.assertTrue(sfSEOUrl.contains(dbSEOUrl), " SEO url should be contains %s, but found %s.".formatted(dbSEOUrl, sfSEOUrl));
            logger.info("[{}] Check SEO url", language);
        }
    }

    List<Boolean> getBranchStatus() {
        // return branch status
        return IntStream.range(0, brInfo.getAllBranchStatus().size()).mapToObj(i -> !brInfo.getIsHideOnStoreFront().get(i) && brInfo.getAllBranchStatus().get(i).equals("ACTIVE")).toList();
    }

    /**
     * Compare product name on the SF with Dashboard
     */
    void checkProductName(Integer modelId, String language) {
        // get product name on dashboard
        String dbProductName = productInfo.isHasModel()
                ? productInfo.getVersionNameMap().get(modelId).get(language)
                : productInfo.getLanguages().parallelStream().filter(languages -> languages.getLanguage().equals(language)).findAny().orElse(new ProductInfoV2.MainLanguage()).getName();

        // get product name on shop online
        String sfProductName = commonAction.getText(loc_lblProductName);

        // check product name
        assertCustomize.assertTrue(sfProductName.equals(dbProductName), "[Check product name] Product name should be %s but found %s.".formatted(dbProductName, sfProductName));

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
                assertCustomize.assertEquals(actListingPrice, listingPrice + STORE_CURRENCY, "%s Listing price should be show %s instead of %s".formatted(branch, listingPrice, actListingPrice));
            } else logger.info("No discount product (listing price = selling price)");
            String actSellingPrice = new UICommonAction(driver).getText(loc_lblSellingPrice).replace(",", "");
            long actSellingPriceValue = Long.parseLong(actSellingPrice.replace(STORE_CURRENCY, ""));

            assertCustomize.assertTrue(Math.abs(actSellingPriceValue - sellingPrice) <= 1, "%s Selling price should be show %,d ±1 instead of %,d".formatted(branch, sellingPrice, actSellingPriceValue));
            logger.info("{}Check product price/ store currency show correctly", branch);
        } else logger.info("{}Website listing enable, so listing/selling price is hidden", branch);
    }

    // check flash sale
    void checkFlashSaleShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        assertCustomize.assertFalse(commonAction.getListElement(loc_lblFlashSale).isEmpty(), "%s Flash sale badge does not show".formatted(branch));
        logger.info("{}Check flash sale badge is shown", branch);
    }


    // check discount campaign
    void checkDiscountCampaignShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);

        if (!commonAction.getListElement(loc_chkBuyInBulk).isEmpty()) {
            // check into buy in bulk checkbox
            if (commonAction.getAttribute(loc_chkBuyInBulk, "class").contains("unchecked"))
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

        assertCustomize.assertFalse(commonAction.getListElement(loc_chkBuyInBulk).isEmpty(), "%s Discount campaign does not show".formatted(branch));
        logger.info("{}Check discount campaign is shown", branch);
    }

    // check wholesale product price
    void checkWholesaleProductShouldBeShown(String brName) {
        String branch = "[Branch name: %s]".formatted(brName);
        assertCustomize.assertFalse(commonAction.getListElement(loc_pnlWholesalePricing).isEmpty(), "%s Wholesale product information is not shown".formatted(branch));
        logger.info("{}Check wholesale product information is shown", branch);
    }


    void checkAttribution(List<Boolean> isDisplayAttribute, List<String> attributeGroups, List<String> attributeValues, String... variationName) {
        String varName = (variationName.length > 0) ? (variationName[0].isEmpty() ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        if (isDisplayAttribute.stream().anyMatch(b -> b)) {
            // if product have more than 3 attributes, show all attribute
            if (Collections.frequency(isDisplayAttribute, true) > 3) commonAction.clickJS(loc_btnViewMore);

            // check attribute
            int sfAttributeIndex = 0;
            for (int attributeIndex = 0; attributeIndex < attributeGroups.size(); attributeIndex++) {
                if (isDisplayAttribute.get(attributeIndex)) {
                    // check attribute name
                    String sfAttributeName = commonAction.getText(loc_cntAttributeGroup, sfAttributeIndex);
                    assertCustomize.assertEquals(sfAttributeName, attributeGroups.get(attributeIndex),
                            "Attribute name must be '%s', but found '%s'.".formatted(attributeGroups.get(attributeIndex), sfAttributeName));

                    // check attribute value
                    String sfAttributeValue = commonAction.getText(loc_cntAttributeValue, sfAttributeIndex);
                    assertCustomize.assertEquals(sfAttributeValue, attributeValues.get(attributeIndex),
                            "Attribute value must be '%s', but found '%s'.".formatted(attributeGroups.get(attributeIndex), sfAttributeName));

                    sfAttributeIndex++;
                }
            }
        }

        // log
        logger.info("{}Check product attribute.", varName);
    }

    /**
     * Compare variation name/value on the SF with Dashboard
     */
    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> variationNameListDB = Arrays.stream(productInfo.getVariationGroupNameMap().get(language).split("\\|")).toList();
        List<WebElement> listElement = commonAction.getListElement(loc_lblVariationName);
        List<String> variationNameListSF = IntStream.range(0, listElement.size()).mapToObj(index -> commonAction.getText(loc_lblVariationName, index)).toList().stream().sorted().toList();

        assertCustomize.assertTrue(variationNameListSF.toString().equalsIgnoreCase(variationNameListDB.toString()), "[Check variation name] Variation name should be %s, but found %s.".formatted(variationNameListDB, variationNameListSF));
        logger.info("[Check variation name] Check product variation show correctly");
    }

    void checkFilterAndSearchBranchIsShown(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Filter branch is shown
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnFilterBranch).isEmpty(), "%s 'Filter dropdown' should be shown but it is hidden.".formatted(varName));
        logger.info("{}Check 'Filter dropdown' is displayed.", varName);

        // check Search branch is shown
        assertCustomize.assertFalse(commonAction.getListElement(loc_icnSearchBranch).isEmpty(), "%s 'Search box' should be shown but it is hidden.".formatted(varName));
        logger.info("{}Check 'Search box' is displayed.", varName);
    }

    void checkFilterAndSearchBranchIsHidden(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Filter branch is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_icnFilterBranch).isEmpty(), "%s 'Filter dropdown' should be hidden but it is shown.".formatted(varName));
        logger.info("{}Check 'Filter dropdown' is hidden.", varName);

        // check Search branch is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_icnSearchBranch).isEmpty(), "%s 'Search box' should be hidden but it is shown.".formatted(varName));
        logger.info("{}Check 'Search box' is hidden.", varName);
    }

    // BH_8616, BH_9536
    void checkBranch(String brElementText, boolean brStatus, int brStock, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        // check branch information
        // check branch name
        assertCustomize.assertTrue(brInfo.getBranchName().contains(brElementText) && brStatus && (brStock > 0), "[Branch name: %s] Branch in-stock but is not shown.".formatted(brElementText));
        logger.info("{}Check branch '{}'", varName, brElementText);
    }

    /**
     * Compare product stock quantity per branch on the SF with Dashboard (without variation product)
     */
    void checkBranchStock(String brElementText, int brElementIndex, boolean brStatus, int brStock, String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        if (!productInfo.isHideStock() & brStatus) {
            String brStockElementText = commonAction.getText(loc_lblBranchStock, brElementIndex);
            // check branch stock
            int sfStock = Integer.parseInt(brStockElementText.replaceAll("\\D+", ""));
            assertCustomize.assertEquals(sfStock, brStock, "%s[Branch name: %s] Stock quantity should be %s, but found %s".formatted(varName, brElementText, brStock, sfStock));
        } else logger.info("Setting hide stock.");
    }

    /**
     * Compare product description on the SF with Dashboard
     */
    void checkProductDescription(Integer modelId, String language) {
        // get dashboard product description
        String dbDescription = productInfo.isHasModel()
                ? productInfo.getVersionDescriptionMap().get(modelId).get(language)
                : productInfo.getLanguages().parallelStream().filter(languages -> languages.getLanguage().equals(language)).findAny().orElse(new ProductInfoV2.MainLanguage()).getDescription();
        dbDescription = dbDescription.replaceAll("<.*?>", "").replaceAll("amp;", "");

        // get SF product description
        String sfDescription = commonAction.getText(loc_pnlDescription).replaceAll("\n", "");

        assertCustomize.assertTrue(sfDescription.equals(dbDescription), "[Check description] Product description should be '%s', but found '%s'".formatted(dbDescription, sfDescription));
        logger.info("[Check description] Check product description is shown correctly.");
    }

    void checkBuyNowAndAddToCartBtnIsShown(String... variationName) {
        String varName = (variationName.length > 0) ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";

        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            assertCustomize.assertFalse(commonAction.getListElement(loc_btnBuyNow).isEmpty(), "%s 'Buy now' button should be shown but it is hidden.".formatted(varName));
            logger.info("{}Check 'Buy Now' button is displayed.", varName);

            // check Add to cart button is shown
            assertCustomize.assertFalse(commonAction.getListElement(loc_btnAddToCart).isEmpty(), "%s 'Add to cart' button should be shown but it is hidden.".formatted(varName));
            logger.info("{}Check 'Add to cart' button is displayed.", varName);
        } else {
            checkBuyNowAndAddToCartBtnIsHidden(variationName);
        }
    }

    void checkBuyNowAndAddToCartBtnIsHidden(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        // check Buy now button is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_btnBuyNow).isEmpty(), "%s 'Buy now' button should be hidden but it is shown.".formatted(varName));
        logger.info("{}Check 'Buy Now' button is hidden.", varName);

        // check Add to cart button is hidden
        assertCustomize.assertTrue(commonAction.getListElement(loc_btnAddToCart).isEmpty(), "%s 'Add to cart' button should be hidden but it is shown.".formatted(varName));
        logger.info("{}Check 'Add to cart' button is hidden.", varName);
    }

    /**
     * <p> In case, setting show on SF/Buyer when out of stock</p>
     * <p> Check can access to product detail page by URL</p>
     * <p> And verify that SoldOut mark has been shown</p>
     */
    void checkSoldOutMark(String... variationName) {
        String varName = variationName.length > 0 ? ((variationName[0].isEmpty()) ? "[Variation: %s]".formatted(variationName[0]) : "") : "";
        boolean sfSoldOut = commonAction.getText(loc_lblSoldOut).equals("Hết hàng") || commonAction.getText(loc_lblSoldOut).equals("Out of stock");
        assertCustomize.assertTrue(sfSoldOut, "%s Sold out mark does not show".formatted(varName));
        logger.info("{}Check 'SOLD OUT' mark is shown", varName);
    }

    /**
     * <p> In case, setting does not show on SF/Buyer when out of stock</p>
     * <p> Check can not access to product detail page by URL</p>
     */
    void check404Page() {
        assertCustomize.assertTrue(driver.getCurrentUrl().contains("404"), " 404 is not shown although product out of stock.");
        logger.info("Check 404 page is shown when product out of stock.");
    }

    void checkVariationPriceAndDiscount(int itemId, Integer modelId, int branchId, int customerId, long listingPrice, long sellingPrice, String brName) {
        var campaignInfo = new APIGetDiscountCampaignInformation(loginInformation).getDiscountCampaignInformation(itemId, branchId, customerId);
        var wholesaleInfo = new APIGetWholesaleProductInformation(loginInformation).getWholesaleProductInformation(itemId, customerId, modelId);
        var flashSaleInfo = new APIGetFlashSaleInformation(loginInformation).getFlashSaleInformation(itemId, modelId);


        // check badge
        if (flashSaleInfo != null) checkFlashSaleShouldBeShown(brName);
        else if (campaignInfo != null) checkDiscountCampaignShouldBeShown(brName);
        else if (wholesaleInfo != null) checkWholesaleProductShouldBeShown(brName);

        // check price
        if (flashSaleInfo != null && !flashSaleInfo.getStatus().equals("SCHEDULED")) {
            // Log
            logger.info("PRICE: FLASH SALE");

            // Check flash sale price
            checkPriceOnEachBranch(listingPrice, flashSaleInfo.getItems().get(0).getNewPrice(), brName);
        } else if (campaignInfo != null) {
            // Log
            logger.info("PRICE: DISCOUNT CAMPAIGN");

            // Calculation campaign price
            String couponType = campaignInfo.getWholesales().get(0).getType();
            long couponValue = campaignInfo.getWholesales().get(0).getWholesaleValue();
            long newPrice = couponType.equals("FIXED_AMOUNT")
                    ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                    : ((sellingPrice * (100 - couponValue)) / 100);

            // Check discount campaign price
            checkPriceOnEachBranch(listingPrice, newPrice, brName);
        } else if (wholesaleInfo != null) {
            // Log
            logger.info("PRICE: WHOLESALE PRODUCT");

            // increase quantity to wholesale product minimum requirement
            do {
                commonAction.sendKeys(loc_txtQuantity, String.valueOf(wholesaleInfo.getMinQuatity()));
            } while (!commonAction.getValue(loc_txtQuantity).equals(String.valueOf(wholesaleInfo.getMinQuatity())));

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
            checkPriceOnEachBranch(listingPrice, wholesaleInfo.getPrice().longValue(), brName);
        } else {
            // Log
            logger.info("PRICE: SELLING PRICE");

            // Check selling price
            checkPriceOnEachBranch(listingPrice, sellingPrice, brName);
        }
    }

    void checkAllVariationsAndDiscount(int varIndex,
                                       long listingPrice,
                                       long sellingPrice,
                                       int customerId,
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

        int numberOfDisplayBranches = IntStream.range(0, brInfo.getAllBranchStatus().size()).filter(i -> !brInfo.getIsHideOnStoreFront().get(i) && brInfo.getAllBranchStatus().get(i).equals("ACTIVE") && (branchStock.get(i) > 0)).mapToObj(ignored -> true).toList().size();
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
                checkVariationPriceAndDiscount(productInfo.getId(),
                        productInfo.getVariationModelList().get(varIndex),
                        brInfo.getBranchID().get(brIndex),
                        customerId,
                        listingPrice,
                        sellingPrice,
                        brName);
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

        // verify on each variation
        for (Integer modelId : productInfo.getVariationModelList()) {
            // variation index
            int varIndex = productInfo.getVariationModelList().indexOf(modelId);

            // ignore if variation inactive
            if ((productInfo.isHasModel() && productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) || productInfo.getBhStatus().equals("ACTIVE")) {
                // In case without variation, variation is empty
                String variationValue = "";

                // switch variation if any
                if (productInfo.isHasModel()) {
                    // get variation value
                    variationValue = productInfo.getVariationValuesMap().get(language).get(varIndex);
                    List<String> varName = Arrays.stream(variationValue.split("\\|")).toList();
                    if (!variationValue.isEmpty())logger.info("*** var: {} ***", variationValue);

                    // select variation
                    for (String var : varName) {
                        int index = varName.indexOf(var);
                        commonAction.clickJS(By.cssSelector(variationDropdownLocator.formatted(index + 1)));
                        logger.info("Open variation dropdown {}.", index);

                        commonAction.clickJS(By.xpath(variationValueLocator.formatted(var, var)));
                        logger.info("Select variation: {}.", var);

                        // check variation is selected or not
                        Assert.assertEquals(commonAction.getAttribute(By.cssSelector(selectedLocator.formatted(index + 1)), "title"), var, " Can not select variation: %s.".formatted(var));
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

                // Get attribute configs
                List<Boolean> isDisplayAttribute = productInfo.isHasModel()
                        ? productInfo.getModels().get(varIndex).getModelAttributes().stream().map(ProductInfoV2.ItemAttribute::isDisplay).toList()
                        : productInfo.getItemAttributes().stream().map(ProductInfoV2.ItemAttribute::isDisplay).toList();
                List<String> attributeGroups = productInfo.isHasModel()
                        ? productInfo.getModels().get(varIndex).getModelAttributes().stream().map(ProductInfoV2.ItemAttribute::getAttributeName).toList()
                        : productInfo.getItemAttributes().stream().map(ProductInfoV2.ItemAttribute::getAttributeName).toList();

                List<String> attributeValues = productInfo.isHasModel()
                        ? productInfo.getModels().get(varIndex).getModelAttributes().stream().map(ProductInfoV2.ItemAttribute::getAttributeValue).toList()
                        : productInfo.getItemAttributes().stream().map(ProductInfoV2.ItemAttribute::getAttributeValue).toList();

                // check product information
                checkAllVariationsAndDiscount(varIndex,
                        productInfo.isHasModel() ? productInfo.getProductListingPrice().get(varIndex) : productInfo.getOrgPrice(),
                        productInfo.isHasModel() ? productInfo.getProductSellingPrice().get(varIndex) : productInfo.getNewPrice(),
                        customerId,
                        productInfo.getProductStockQuantityMap().get(productInfo.isHasModel() ? modelId : productInfo.getId()),
                        isDisplayAttribute,
                        attributeGroups,
                        attributeValues,
                        language, variationValue);
            }

            // refresh page before check next variation
            driver.navigate().refresh();
        }
    }

    /**
     * Access to product detail on SF by URL
     */
    public void accessToProductDetailPageByProductIDAndCheckProductInformation(LoginInformation loginInformation, String language, ProductInfoV2 productInfo, int customerId) throws Exception {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CheckProductDetail] START... ");

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
                // Get product URL
                var languages = productInfo.getLanguages().parallelStream().filter(mainLanguage -> mainLanguage.getLanguage().equals(languageCode)).findAny().orElse(null);
                String sfURL = ((languages != null) && (languages.getSeoUrl() != null))
                        ? "https://%s%s/%s".formatted(storeInfo.getStoreURL(), SF_DOMAIN, languages.getSeoUrl())
                        : "https://%s%s/%s/product/%d".formatted(storeInfo.getStoreURL(), SF_DOMAIN, languageCode, productInfo.getId());

                // check all information with language
                driver.get(sfURL);
                driver.navigate().refresh();
                logger.info("Navigate to Product detail page by URL, with id: {}", productInfo.getId());

                // wait product detail page loaded
                commonAction.getElement(loc_lblProductName);

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
            } else logger.info("'%s' language is not published, please publish it and try again.", language);
        } else {
            // in-case out of stock and setting hide product when out of stock
            // wait 404 page loaded
            driver.get("https://%s%s%s/product/%s".formatted(storeInfo.getStoreURL(), SF_DOMAIN, !storeInfo.getStoreLanguageList().isEmpty() ? "/%s".formatted(storeInfo.getStoreLanguageList().get(0)) : "", productInfo.getId()));
            logger.info("Navigate to Product detail page by URL, id: {}", productInfo.getId());

            // wait 404 page loaded
            commonAction.waitURLShouldBeContains("404");

            // check 404 page is shown
            check404Page();
        }

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CheckProductDetail] DONE!!! ");

        // complete verify
        AssertCustomize.verifyTest();
    }

    public ShoppingCart clickOnBuyNow() {
        commonAction.click(loc_btnBuyNow);
        logger.info("CLick on Buy Now button");
        commonAction.sleepInMiliSecond(2000);
        return new ShoppingCart(driver);
    }

    public ProductDetailPage accessToProductDetailPageByURL(String domain, String productID) {
        commonAction.navigateToURL(domain + "product/" + productID);
        logger.info("Navigate to Product detail page by URL, with productID: {}", productID);
        commonAction.sleepInMiliSecond(3000);
        return this;
    }

    public String getProductName() {
        String name = commonAction.getText(loc_lblProductName);
        logger.info("Retrieved product name: {}", name);
        return name;
    }

    public boolean isReviewTabDisplayed() {
        boolean isDisplayed = !commonAction.getListElement(loc_tabReview).isEmpty();
        logger.info("Is Review tab displayed: {}", isDisplayed);
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
        logger.info("Rated stars : {}", rating);
    }

    public void inputReviewTitle(String reviewTitle) {
        commonAction.sendKeys(loc_lblReviewTitle, reviewTitle);
        logger.info("Input review title: {}", reviewTitle);
    }

    public void inputReviewDescription(String reviewDescription) {
        commonAction.sendKeys(loc_lblReviewDescription, reviewDescription);
        logger.info("Input review description: {}", reviewDescription);
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
