package mobile.buyer.iOS.product_detail;

import api.Buyer.productdetail.APIGetDiscountCampaignInformation;
import api.Buyer.productdetail.APIGetFlashSaleInformation;
import api.Buyer.productdetail.APIGetWholesaleProductInformation;
import api.Seller.products.all_products.APIProductDetailV2.ProductInfoV2;
import api.Seller.sale_channel.onlineshop.Preferences;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import mobile.buyer.iOS.search.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
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
    UICommonIOS commonIOS;
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
        commonIOS = new UICommonIOS(driver);
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
        String actProductName = commonIOS.getText(loc_lblProductName);
        assertCustomize.assertEquals(actProductName, productName, "Product/Version name must be %s, but found %s".formatted(productName, actProductName));
    }

    void checkSoldOutMark() {
        assertCustomize.assertFalse(commonIOS.getListElement(loc_lblSoldOutMark).isEmpty(), "Sold out mark is not shown");
    }

    void checkVariationPrice(int itemId, Integer modelId, int branchId, int customerId, long listingPrice, long sellingPrice) {
        var campaignInfo = new APIGetDiscountCampaignInformation(loginInformation).getDiscountCampaignInformation(itemId, branchId, customerId);
        var wholesaleInfo = new APIGetWholesaleProductInformation(loginInformation).getWholesaleProductInformation(itemId, customerId, modelId);
        var flashSaleInfo = new APIGetFlashSaleInformation(loginInformation).getFlashSaleInformation(itemId, modelId);


        // check badge
        if (flashSaleInfo != null)
            assertCustomize.assertFalse(commonIOS.getListElement(loc_lblFlashSaleBadge).isEmpty(), "Flash sale badge is not shown");
        else if (campaignInfo != null)
            assertCustomize.assertFalse(commonIOS.getListElement(loc_lblDiscountCampaignBadge).isEmpty(), "Discount campaign badge is not shown");
        else if (wholesaleInfo != null)
            assertCustomize.assertFalse(commonIOS.getListElement(loc_lblWholesaleProductBadge).isEmpty(), "Wholesale product badge is not shown");

        // check price
        if (!(isEnableListingProduct && productInfo.isEnabledListing())) {
            // Check listing price
            if (!Objects.equals(sellingPrice, listingPrice)) {
                assertCustomize.assertFalse(commonIOS.getListElement(loc_lblListingPrice(listingPrice)).isEmpty(), "Listing price must be '%,d đ' but it is not shown".formatted(listingPrice));
            } else logger.info("No discount product (listing price = selling price)");

            // Open add to cart popup
            commonIOS.click(loc_icnAddToCart);
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
                if (!commonIOS.isChecked(commonIOS.getElement(loc_chkBuyInBulk))) {
                    commonIOS.click(loc_chkBuyInBulk);
                }

                // Check discount campaign price
                checkSellingPriceOnBranch(newPrice);
            } else if (wholesaleInfo != null) {
                // Log
                logger.info("PRICE: WHOLESALE PRODUCT");

                // Increase quantity to wholesale product minimum requirement
                commonIOS.sendKeys(loc_txtAddToCartQuantity, String.valueOf(wholesaleInfo.getMinQuatity()));

                // Check wholesale product price
                checkSellingPriceOnBranch(wholesaleInfo.getPrice().longValue());
            } else {
                // Log
                logger.info("PRICE: SELLING PRICE");

                // Check selling price
                checkSellingPriceOnBranch(sellingPrice);
            }

            // close add to cart popup
            commonIOS.click(loc_icnCloseAddToCart);
        }
    }

    void checkSellingPriceOnBranch(long sellingPrice) {
        // Check selling price
        assertCustomize.assertFalse(commonIOS.getListElement(loc_lblSellingPriceOnCart(sellingPrice)).isEmpty(), "Selling price must be '%,d đ' but it not shown".formatted(sellingPrice));
    }

    void checkVariationName(String language) {
        // get variation name list on dashboard
        List<String> expectedVariationNameList = Arrays.stream(productInfo.getVariationGroupNameMap().get(language).split("\\|")).toList();

        // Check all variation names show
        expectedVariationNameList.forEach(variationName -> assertCustomize.assertFalse(commonIOS.getListElement(loc_lblVariationName(variationName)).isEmpty(), "Variation name %s is not shown".formatted(variationName)));
    }

    void checkFilterAndSearchBranch(boolean isShown) {
        // check Search icon is shown or not
        assertCustomize.assertNotEquals(commonIOS.getListElement(loc_icnSearchBranch).isEmpty(), isShown, "Search branch icon is not %s".formatted(isShown ? "shown" : "hidden"));
    }

    void checkBranchNameAndStock(String brName, int brStock) {
        // Check branch name
        assertCustomize.assertTrue(brInfo.getBranchName().contains(brName), "[Branch name: %s] Branch in-stock but is not shown.".formatted(brName));

        // Check branch stock
        if (!productInfo.isHideStock()) {
            // check branch stock
            int adrBranchStock = Integer.parseInt(commonIOS.getText(loc_lblBranchAndStock(brName)).split(" - ")[1].replaceAll("\\D+", ""));
            assertCustomize.assertEquals(adrBranchStock, brStock, "[Branch name: %s] Stock quantity should be %s, but found %s".formatted(brName, brStock, adrBranchStock));
        }
    }

    void checkProductDescription(String description) {
        // Get expected description
        String expDescription = description.replaceAll("<.*?>", "").replaceAll("amp;", "");

        // Check description
        if (!commonIOS.getListElement(loc_lblProductDescription).isEmpty()) {
            String actDescription = commonIOS.getText(loc_lblProductDescription);
            assertCustomize.assertEquals(actDescription, expDescription, "Product description must be %s, but found %s".formatted(expDescription, actDescription));
        }
    }

    void checkBuyNowAddToCartAndContactNowBtn() {
        if (!(new Preferences(loginInformation).isEnabledListingProduct() && productInfo.isEnabledListing())) {
            // check Buy now button is shown
            assertCustomize.assertFalse(commonIOS.getListElement(loc_btnBuyNow).isEmpty(), "Buy now button is not shown");

            // check Add to cart button is shown
            assertCustomize.assertFalse(commonIOS.getListElement(loc_icnAddToCart).isEmpty(), "Add to cart icon is not shown");

        } else {
            // check Contact Now button is shown
            assertCustomize.assertFalse(commonIOS.getListElement(loc_btnContactNow).isEmpty(), "Contact now button is not shown");
        }
    }

    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, int customerId, List<Integer> branchStock, String language) {
        // Check product name
        checkProductName(productInfo.getVersionNameMap().get(productInfo.getVariationModelList().get(varIndex)).get(language));

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
                    commonIOS.click(loc_lblBranchAndStock(brName));

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
        checkProductDescription(productInfo.getVersionDescriptionMap().get(productInfo.getVariationModelList().get(varIndex)).get(language));

        // check Buy Now and Add To Cart button is shown
        checkBuyNowAddToCartAndContactNowBtn();
    }

    void checkProductInformation(String language, int customerId) {
        // get listing price setting
        isEnableListingProduct = new Preferences(loginInformation).isEnabledListingProduct();

        // check variation name if any
        if (productInfo.isHasModel()) checkVariationName(language);

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
                    //temp
                    Arrays.stream(variationValue.split("\\|")).forEachOrdered(value -> commonIOS.click(loc_lblVariationValue(value)));

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

    void searchProductAndNavigateToProductDetailScreen(String language, ProductInfoV2 productInfo) {
        // Get product name
        String keywords = productInfo.getMainProductNameMap().get(language.equals("VIE") ? "vi" : "en");

        // Search and navigate to product detail screen
        new SearchScreen(driver).searchAndNavigateProductDetailScreen(keywords);
    }

    /**
     * Access to product detail on SF by URL
     */
    public void openProductDetailScreenAndCheckProductInformation(LoginInformation loginInformation, String language, ProductInfoV2 productInfo, int customerId) {
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
            searchProductAndNavigateToProductDetailScreen(language, productInfo);
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
            else logger.info("'{}' language is not published, please publish it and try again.",language);
        }

        // complete verify
        AssertCustomize.verifyTest();
    }
}
