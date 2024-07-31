package mobile.buyer.iOS.product_detail;

import api.Seller.customers.APIAllCustomers;
import api.Seller.products.all_products.WholesaleProduct;
import api.Seller.promotion.FlashSale;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.promotion.ProductDiscountCampaign.BranchDiscountCampaignInfo;
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
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductDetailScreen extends ProductDetailElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    ProductInfo productInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
    FlashSale.FlashSaleInfo flashSaleInfo;
    Map<String, BranchDiscountCampaignInfo> discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    List<Boolean> branchStatus;
    boolean isEnableListingProduct;
    Map<String, List<String>> salePriceMap;
    Map<String, List<String>> saleDisplayMap;
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

    /**
     * Map: branch name, list of price type
     * <p>Ex: Product has variation var1, var2, var3, var4. And branch A, B</p>
     * <p>This function return list price type of each variation on each branch</p>
     * <p>Branch A = {FLASH SALE, WHOLESALE PRODUCT, WHOLESALE PRODUCT, SELLING PRICE} </p>
     * <p>Branch B = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN} </p>
     * <p>Branch C = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, SELLING PRICE} </p>
     */
    Map<String, List<String>> getSalePriceMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName,
                brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size())
                        .mapToObj(i -> flashSaleInfo.getFlashSaleStatus().get(brName).get(i).equals("IN_PROGRESS")
                                ? "FLASH SALE"
                                : ((discountCampaignInfo.get(brName) != null)
                                    ? "DISCOUNT CAMPAIGN"
                                    : (wholesaleProductInfo.getStatusMap().get(brName).get(i)
                                        ? "WHOLESALE PRODUCT"
                                        : "SELLING PRICE"))).toList(),
                (a, b) -> b));
    }

    Map<String, List<String>> getSaleDisplayMap() {
        return brInfo.getBranchName().stream().collect(Collectors.toMap(brName -> brName, brName -> IntStream.range(0, flashSaleInfo.getFlashSaleStatus().get(brName).size()).mapToObj(i -> switch (flashSaleInfo.getFlashSaleStatus().get(brName).get(i)) {
            case "IN_PROGRESS", "SCHEDULED" -> "FLASH SALE";
            default ->
                    (discountCampaignInfo.get(brName) != null) ? "DISCOUNT CAMPAIGN" : (wholesaleProductInfo.getStatusMap().get(brName).get(i) ? "WHOLESALE PRODUCT" : "SELLING PRICE");
        }).toList(), (a, b) -> b));
    }

    void checkProductName(String productName) {
        // Check product name
        String actProductName = commonIOS.getText(loc_lblProductName);
        assertCustomize.assertEquals(actProductName, productName, "Product/Version name must be %s, but found %s".formatted(productName, actProductName));
    }

    void checkSoldOutMark() {
        assertCustomize.assertFalse(commonIOS.getListElement(loc_lblSoldOutMark).isEmpty(), "Sold out mark is not shown");
    }

    long getDiscountCampaignPrice(long sellingPrice, String brName) {
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
        logger.debug("Price: {}", priceType);
        String displayType = saleDisplayMap.get(brName).get(varIndex);
        logger.debug("Display: {}", displayType);

        // check badge
        switch (displayType) {
            // check flash sale badge is shown
            case "FLASH SALE" ->
                    assertCustomize.assertFalse(commonIOS.getListElement(loc_lblFlashSaleBadge).isEmpty(), "Flash sale badge is not shown");
            // check discount campaign is shown
            case "DISCOUNT CAMPAIGN" ->
                    assertCustomize.assertFalse(commonIOS.getListElement(loc_lblDiscountCampaignBadge).isEmpty(), "Discount campaign badge is not shown");
            // check wholesale product is shown
            case "WHOLESALE PRODUCT" ->
                    assertCustomize.assertFalse(commonIOS.getListElement(loc_lblWholesaleProductBadge).isEmpty(), "Wholesale product badge is not shown");
        }

        if (!(isEnableListingProduct && productInfo.isEnabledListing())) {
            // Check listing price
            if (!Objects.equals(sellingPrice, listingPrice)) {
                assertCustomize.assertFalse(commonIOS.getListElement(loc_lblListingPrice(listingPrice)).isEmpty(), "Listing price must be '%,d đ' but it is not shown".formatted(listingPrice));
            } else logger.info("No discount product (listing price = selling price)");

            // Open add to cart popup
            commonIOS.click(loc_icnAddToCart);

            // Check selling price
            switch (priceType) {
                case "FLASH SALE" -> checkSellingPriceOnBranch(flashSalePrice);// Check flash sale price
                case "DISCOUNT CAMPAIGN" -> {
                    // Click into Buy in bulk checkbox
                    if (!commonIOS.isChecked(commonIOS.getElement(loc_chkBuyInBulk))) {
                        commonIOS.click(loc_chkBuyInBulk);
                    }

                    // Check discount campaign price
                    checkSellingPriceOnBranch(getDiscountCampaignPrice(sellingPrice, brName));
                }
                case "WHOLESALE PRODUCT" -> {
                    // Increase quantity to wholesale product minimum requirement
                    commonIOS.sendKeys(loc_txtAddToCartQuantity, String.valueOf(wholesaleProductStock));

                    // Check wholesale product price
                    checkSellingPriceOnBranch(wholesaleProductPrice);
                }
                default -> checkSellingPriceOnBranch(sellingPrice);
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

    void checkVariationInformation(int varIndex, long listingPrice, long sellingPrice, long flashSalePrice, int wholesaleProductStock, long wholesaleProductPrice, List<Integer> branchStock, String language) {
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

                // Get variation stock
                List<Integer> variationStock = productInfo.getProductStockQuantityMap().get(productInfo.getVariationModelList().get(varIndex));

                // Get stock in branch
                int stockInBranch = variationStock.get(brIndex);

                // If branch in-stock, check that information
                if (stockInBranch > 0 && branchStatus.get(brIndex)) {
                    // Log
                    logger.debug("Branch name: {}", brName);

                    // Switch branch
                    commonIOS.click(loc_lblBranchAndStock(brName));

                    // Check branch name, branch stock, branch price
                    checkBranchNameAndStock(brName, branchStock.get(brIndex));

                    // Check product price
                    checkVariationPrice(varIndex, listingPrice, sellingPrice, flashSalePrice, wholesaleProductStock, wholesaleProductPrice, brName.split(" - ")[0]);
                }
            }
        } else checkSoldOutMark();

        // check description
        checkProductDescription(productInfo.getVersionDescriptionMap().get(productInfo.getVariationModelList().get(varIndex)).get(language));

        // check Buy Now and Add To Cart button is shown
        checkBuyNowAddToCartAndContactNowBtn();
    }

    void checkProductInformation(String language, int customerId) {
        // get list segment of customer
        List<Integer> listSegmentOfCustomer = new APIAllCustomers(loginInformation).getListSegmentOfCustomer(customerId);

        // get wholesale config
        if (!productInfo.isDeleted())
            wholesaleProductInfo = new WholesaleProduct(loginInformation).wholesaleProductInfo(productInfo, listSegmentOfCustomer);

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
            String variationValue = productInfo.getVariationValuesMap().get(language).get(varIndex);

            // ignore if variation inactive
            if (productInfo.getVariationStatus().get(varIndex).equals("ACTIVE")) {
                // Log
                if (!variationValue.isEmpty()) logger.debug("Variation value: {}", variationValue);

                // switch variation if any
                if (productInfo.isHasModel())
                    //temp
                    Arrays.stream(variationValue.split("\\|")).forEachOrdered(value -> commonIOS.click(loc_lblVariationValue(value)));

                // check product information
                checkVariationInformation(varIndex,
                        productInfo.getProductListingPrice().get(varIndex),
                        productInfo.getProductSellingPrice().get(varIndex),
                        flashSaleInfo.getFlashSalePrice().get(varIndex),
                        wholesaleProductInfo.getStockList().get(varIndex),
                        wholesaleProductInfo.getPriceList().get(varIndex),
                        productInfo.getProductStockQuantityMap().get(modelId),
                        language);
            }
        }

    }

    void searchProductAndNavigateToProductDetailScreen(String language, ProductInfo productInfo) {
        // Get product name
        String keywords = productInfo.getMainProductNameMap().get(language.equals("VIE") ? "vi" : "en");

        // Search and navigate to product detail screen
        new SearchScreen(driver).searchAndNavigateProductDetailScreen(keywords);
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
            searchProductAndNavigateToProductDetailScreen(language, productInfo);
        } catch (NoSuchElementException ex) {
            isShowOnApp = false;
        }

        // check out of stock
        if ((maxStock == 0 && !productInfo.getShowOutOfStock())) {
            assertCustomize.assertFalse(isShowOnApp, "[Failed] Product still shows when stock is out and setting hides product out of stock.");
        }

        // check product is display or not
        if (!productInfo.isDeleted() && productInfo.getOnApp() && productInfo.getBhStatus().equals("ACTIVE") && (maxStock > 0 || productInfo.getShowOutOfStock())) {
            if (storeInfo.getSFLangList().contains(languageCode))
                checkProductInformation(languageCode, customerId);
            else logger.info("'%s' language is not published, please publish it and try again.".formatted(language));
        }

        // complete verify
        AssertCustomize.verifyTest();
    }
}
