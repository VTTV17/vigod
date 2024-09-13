package mobile.buyer.android.productDetail;

import api.Buyer.productdetail.APIGetDiscountCampaignInformation;
import api.Buyer.productdetail.APIGetFlashSaleInformation;
import api.Buyer.productdetail.APIGetWholesaleProductInformation;
import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import api.Seller.sale_channel.onlineshop.Preferences;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import mobile.buyer.android.buyergeneral.BuyerGeneral;
import mobile.buyer.android.navigationbar.NavigationBar;
import mobile.buyer.android.shopcart.BuyerShopCartPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ProductDetailScreen extends ProductDetailElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonAndroid;
    Logger logger = LogManager.getLogger();
    ProductInfoV2 productInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
    List<Boolean> branchStatus;
    boolean isEnableListingProduct;
    LoginInformation loginInformation;

    public ProductDetailScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonAndroid = new UICommonAndroid(driver);
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

    void checkProductName(String productName) {
        // Check product name
        String actProductName = commonAndroid.getText(loc_lblProductName);
        assertCustomize.assertEquals(actProductName, productName, "Product/Version name must be %s, but found %s".formatted(productName, actProductName));

        // Log
        logger.info("Check product/version name");
    }

    void checkSoldOutMark() {
        assertCustomize.assertFalse(commonAndroid.getListElement(loc_lblSoldOutMark).isEmpty(), "Sold out mark is not shown");

        // Log
        logger.info("Check sold out mark");
    }

    void checkVariationPrice(int itemId, Integer modelId, int branchId, int customerId, long listingPrice, long sellingPrice) {
        var campaignInfo = new APIGetDiscountCampaignInformation(loginInformation).getDiscountCampaignInformation(itemId, branchId, customerId);
        var wholesaleInfo = new APIGetWholesaleProductInformation(loginInformation).getWholesaleProductInformation(itemId, customerId, modelId);
        var flashSaleInfo = new APIGetFlashSaleInformation(loginInformation).getFlashSaleInformation(itemId, modelId);


        // check badge
        if (flashSaleInfo != null)
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_lblFlashSaleBadge).isEmpty(), "Flash sale badge is not shown");
        else if (campaignInfo != null)
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_lblDiscountCampaignBadge).isEmpty(), "Discount campaign badge is not shown");
        else if (wholesaleInfo != null)
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_lblWholesaleProductBadge).isEmpty(), "Wholesale product badge is not shown");

        // check price
        if (!(isEnableListingProduct && productInfo.isEnabledListing())) {
            // Check listing price
            if (!Objects.equals(sellingPrice, listingPrice)) {
                long actListingPrice = Long.parseLong(commonAndroid.getText(loc_lblListingPrice).replaceAll("\\D+", ""));
                assertCustomize.assertEquals(actListingPrice, listingPrice, "Listing price should be show %s instead of %s".formatted(listingPrice, actListingPrice));
            } else logger.info("No discount product (listing price = selling price)");

            // Open add to cart popup
            commonAndroid.click(loc_icnAddToCart);
            if (flashSaleInfo != null && !flashSaleInfo.getStatus().equals("SCHEDULED")) {
                // Log
                logger.info("PRICE: FLASH SALE");

                // Check flash sale price
                checkSellingPriceOnBranch(flashSaleInfo.getItems().get(0).getNewPrice());
            } else if (campaignInfo != null) {
                // Log
                logger.info("PRICE: DISCOUNT CAMPAIGN");

                // Calculation campaign price
                String couponType = campaignInfo.getWholesales().get(0).getType();
                long couponValue = campaignInfo.getWholesales().get(0).getWholesaleValue();
                long newPrice = couponType.equals("FIXED_AMOUNT")
                        ? ((sellingPrice > couponValue) ? (sellingPrice - couponValue) : 0)
                        : ((sellingPrice * (100 - couponValue)) / 100);

                // Click into Buy in bulk checkbox
                if (!commonAndroid.isChecked(loc_chkBuyInBulk)) {
                    commonAndroid.click(loc_chkBuyInBulk);
                }

                // Check discount campaign price
                checkSellingPriceOnBranch(newPrice);
            } else if (wholesaleInfo != null) {
                // Log
                logger.info("PRICE: WHOLESALE PRODUCT");

                // Increase quantity to wholesale product minimum requirement
                commonAndroid.sendKeys(loc_txtAddToCartQuantity, String.valueOf(wholesaleInfo.getMinQuatity()));

                // Check wholesale product price
                checkSellingPriceOnBranch(wholesaleInfo.getPrice().longValue());
            } else {
                // Log
                logger.info("PRICE: SELLING PRICE");

                // Check selling price
                checkSellingPriceOnBranch(sellingPrice);
            }

            // close add to cart popup
            commonAndroid.click(loc_icnCloseAddToCart);

            // Log
            logger.info("Check product listing/selling price");
        }
    }

    void checkSellingPriceOnBranch(long expSellingPrice) {
        // Get selling price
        long actSellingPrice = Long.parseLong(commonAndroid.getText(loc_lblSellingPriceOnCart).replaceAll("\\D+", ""));

        // Check selling price
        assertCustomize.assertTrue(Math.abs(actSellingPrice - expSellingPrice) <= 1, "Selling price must be %,d but found %,d".formatted( expSellingPrice, actSellingPrice));
    }

    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> expectedVariationNameList = Arrays.stream(productInfo.getVariationGroupNameMap().get(language).split("\\|")).toList();

        // Check all variation names show
        expectedVariationNameList.forEach(variationName -> assertCustomize.assertFalse(commonAndroid.getListElement(loc_lblVariationName(variationName)).isEmpty(), "Variation name %s is not shown".formatted(variationName)));

        // Log
        logger.info("Check variation name");
    }

    void checkFilterAndSearchBranch(boolean isShown) {
        // check Search icon is shown or not
        assertCustomize.assertNotEquals(commonAndroid.getListElement(loc_icnSearchBranch).isEmpty(), isShown, "Search branch icon is not %s".formatted(isShown ? "shown" : "hidden"));

        // check Filter icon
        if (!commonAndroid.getListElement(loc_icnSearchBranch).isEmpty()) {
            commonAndroid.click(loc_icnSearchBranch);
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_icnFilterBranch).isEmpty(), "Filter branch icon is not shown");
        }

        // Log
        logger.info("Check filter and search icon");
    }

    void checkBranchNameAndStock(String brName, int brStock) {
        // Check branch name
        assertCustomize.assertTrue(brInfo.getBranchName().contains(brName), "[Branch name: %s] Branch in-stock but is not shown.".formatted(brName));

        // Check branch stock
        if (!productInfo.isHideStock()) {
            // check branch stock
            int adrBranchStock = Integer.parseInt(commonAndroid.getText(loc_lblBranchAndStock(brName)).split(" - ")[1].replaceAll("\\D+", ""));
            assertCustomize.assertEquals(adrBranchStock, brStock, "[Branch name: %s] Stock quantity should be %s, but found %s".formatted(brName, brStock, adrBranchStock));
        }

        // Log
        logger.info("Check branch name and available stock");
    }

    void checkProductDescription(String description) {
        // Get expected description
        String expDescription = description.replaceAll("<.*?>", "").replaceAll("amp;", "");

        // Check description
        if (!commonAndroid.getListElement(loc_lblProductDescription).isEmpty()) {
            String actDescription = commonAndroid.getText(loc_lblProductDescription);
            assertCustomize.assertEquals(actDescription, expDescription, "Product description must be %s, but found %s".formatted(expDescription, actDescription));
        }

        // Log
        logger.info("Check product/version description");
    }

    void checkBuyNowAddToCartAndContactNowBtn() {
        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_btnBuyNow).isEmpty(), "Buy now button is not shown");

            // check Add to cart button is shown
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_icnAddToCart).isEmpty(), "Add to cart icon is not shown");

        } else {
            // check Contact Now button is shown
            assertCustomize.assertFalse(commonAndroid.getListElement(loc_btnContactNow).isEmpty(), "Contact now button is not shown");
        }

        // Log
        logger.info("Check Buy now, Add to cart, and Contact now button");
    }

    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, int customerId, List<Integer> branchStock, String language) {
        // Check product name
        String dbProductName = productInfo.isHasModel()
                ? productInfo.getVersionNameMap().get(productInfo.getVariationModelList().get(varIndex)).get(language)
                : productInfo.getLanguages().parallelStream().filter(languages -> languages.getLanguage().equals(language)).findAny().orElse(new ProductInfoV2.MainLanguage()).getName();
        checkProductName(dbProductName);

        // Count all branches display
        int numberOfDisplayBranches = Collections.frequency(IntStream.range(0, branchStatus.size()).mapToObj(brIndex -> branchStatus.get(brIndex) && (branchStock.get(brIndex) > 0)).toList(), true);

        // Check branch information
        if (numberOfDisplayBranches > 0) {
            // Check filter/search branch is shown when available branches > 5
            checkFilterAndSearchBranch(numberOfDisplayBranches > 5);

            // Check branch information
            for (String brName : brInfo.getActiveBranches()) {
                // Get branch index in branch information
                int brIndex = brInfo.getBranchName().indexOf(brName);

                // Get stock in branch
                int stockInBranch = branchStock.get(brIndex);

                // If branch in-stock, check that information
                if (stockInBranch > 0 && branchStatus.get(brIndex)) {
                    // Log
                    logger.debug("Branch name: {}", brName);

                    // Switch branch
                    commonAndroid.click(loc_lblBranchAndStock(brName));

                    // Check branch name, branch stock, branch price
                    checkBranchNameAndStock(brName, branchStock.get(brIndex));

                    // Check product price
                    checkVariationPrice(productInfo.getId(),
                            productInfo.getVariationModelList().get(varIndex),
                            brInfo.getBranchID().get(brIndex),
                            customerId,
                            listingPrice,
                            sellingPrice);
                }
            }
        } else checkSoldOutMark();

        // check description
        String dbDescription = productInfo.isHasModel()
                ? productInfo.getVersionDescriptionMap().get(productInfo.getVariationModelList().get(varIndex)).get(language)
                : productInfo.getLanguages().parallelStream().filter(languages -> languages.getLanguage().equals(language)).findAny().orElse(new ProductInfoV2.MainLanguage()).getDescription();
        dbDescription = dbDescription.replaceAll("<.*?>", "").replaceAll("amp;", "");
        checkProductDescription(dbDescription);

        // check Buy Now and Add To Cart button is shown
        checkBuyNowAddToCartAndContactNowBtn();
    }

    void checkProductInformation(String language, int customerId) {
        // get listing price setting
        isEnableListingProduct = new Preferences(loginInformation).isEnabledListingProduct();

        // check variation name if any
        if (productInfo.isHasModel()) {
            checkVariationName(language);
        }

        // verify on each variation
        List<Integer> variationModelList = productInfo.getVariationModelList();
        for (int varIndex = 0; varIndex < variationModelList.size(); varIndex++) {
            Integer modelId = variationModelList.get(varIndex);

            // variation value
            String variationValue = productInfo.isHasModel() ? productInfo.getVariationValuesMap().get(language).get(varIndex) : "";

            // ignore if variation inactive
            if ((productInfo.getVariationStatus() != null && productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) || productInfo.getBhStatus().equals("ACTIVE")) {
                // Log
                if (!variationValue.isEmpty()) logger.debug("Variation value: {}", variationValue);

                // switch variation if any
                if (productInfo.isHasModel())
                    Arrays.stream(variationValue.split("\\|")).forEachOrdered(value -> commonAndroid.click(loc_lblVariationValue(value)));

                // check product information
                checkVariationInformation(varIndex,
                        productInfo.isHasModel() ? productInfo.getProductListingPrice().get(varIndex) : productInfo.getOrgPrice(),
                        productInfo.isHasModel() ? productInfo.getProductSellingPrice().get(varIndex) : productInfo.getNewPrice(),
                        customerId,
                        productInfo.getProductStockQuantityMap().get(productInfo.isHasModel() ? modelId : productInfo.getId()),
                        language);
            }
        }

    }

    boolean searchProductAndNavigateToProductDetailScreen(String language, ProductInfoV2 productInfo) {
        return new NavigationBar(driver).tapOnHomeIcon().waitHomepageLoaded()
                .searchAndNavigateToProductScreenByName(productInfo, language);
    }

    /**
     * Access to product detail on SF by URL
     */
    public void openProductDetailScreenAndCheckProductInformation(LoginInformation loginInformation, String language, ProductInfoV2 productInfo, int customerId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [CheckProductDetail] START... ");

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
        assertCustomize.assertNotEquals((maxStock == 0 && !productInfo.isShowOutOfStock()),
                searchProductAndNavigateToProductDetailScreen(language, productInfo),
                "Product still shows when stock is out and setting hides product out of stock.");

        // check product is display or not
        if (!productInfo.isDeleted() && productInfo.isOnApp() && productInfo.getBhStatus().equals("ACTIVE") && (maxStock > 0 || productInfo.isShowOutOfStock())) {
            if (storeInfo.getSFLangList().contains(languageCode))
                checkProductInformation(languageCode, customerId);
            else logger.info("'{}' language is not published, please publish it and try again.", language);
        }

        // Logger
        LogManager.getLogger().info("===== STEP =====> [CheckProductDetail] DONE!!! ");

        // complete verify
        AssertCustomize.verifyTest();
    }

    public BuyerShopCartPage buyNowProduct(int quantity) {
        commonAndroid.click(loc_btnBuyNow);
        if (!commonAndroid.getText(loc_txtBuyNowQuantity).equals(String.valueOf(quantity))) {
            commonAndroid.sendKeys(loc_txtBuyNowQuantity, String.valueOf(quantity));
        }
        commonAndroid.click(loc_btnBuy);
        return new BuyerShopCartPage(driver).waitLoadingDisapear();
    }

    public void addToCart(int quantity) {
        commonAndroid.click(loc_icnAddToCart);
        if (!commonAndroid.getText(loc_txtAddToCartQuantity).equals(String.valueOf(quantity))) {
            commonAndroid.sendKeys(loc_txtAddToCartQuantity, String.valueOf(quantity));
        }
        commonAndroid.click(loc_btnAdd);
        logger.info("Add product to cart");
    }

    public void tapOnShoppingCart() {
        commonAndroid.click(loc_icnCart);
        logger.info("Tap on Shopping cart icon.");
        new BuyerShopCartPage(driver);
    }

    public BuyerShopCartPage addProductToCartAndGoToShoppingCart(int quantity) {
        addToCart(quantity);
        tapOnShoppingCart();
        return new BuyerShopCartPage(driver).waitLoadingDisapear();
    }

    public BuyerShopCartPage goToShopCartByBackIcon() {
        new BuyerGeneral(driver).clickOnBackIcon().tapCancelSearch();
        new NavigationBar(driver).tapOnCartIcon();
        return new BuyerShopCartPage(driver);
    }

    public boolean isReviewTabDisplayed() {
        commonAndroid.getElement(loc_tabReview);
        boolean isDisplayed = commonAndroid.isShown(loc_tabReview);
        logger.info("Is Review tab displayed: {}", isDisplayed);
        return isDisplayed;
    }

    public String[] getReview() {
        //Needs further updates
        commonAndroid.getElement(loc_lblReviewContent);

        //Temporary
        if (commonAndroid.isShown(loc_lblNoReview)) {
            return null;
        }

        String title = commonAndroid.getText(loc_lblProductReviewTitle);
        String description = commonAndroid.getText(loc_lblProductReviewDescription);

        return new String[]{title, description};
    }
}
