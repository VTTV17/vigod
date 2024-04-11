package web.Dashboard.orders.pos;

import api.Seller.customers.Customers;
import api.Seller.products.all_products.ProductInformation;
import api.Seller.promotion.ProductDiscountCampaign;
import api.Seller.promotion.ProductDiscountCampaign.BranchDiscountCampaignInfo;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.STORE_CURRENCY;

public class POSPage extends POSElement {

    final static Logger logger = LogManager.getLogger(POSPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    Map<String, BranchDiscountCampaignInfo> discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    AssertCustomize assertCustomize;
    BranchInfo brInfo;
    StoreInfo storeInfo;

    public POSPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public void inputProductSearchTerm(String searchTerm) {
        commons.sendKeys(SEARCH_PRODUCT_BOX, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search Product box.");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToUsePOS(String permission) {
        if (permission.contentEquals("A")) {
            commons.switchToWindow(1);
            new POSPage(driver).inputProductSearchTerm("Test Permission");
            commons.closeTab();
            commons.switchToWindow(0);
        } else if (permission.contentEquals("D")) {
            Assert.assertEquals(commons.getAllWindowHandles().size(), 1);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }
    /*-------------------------------------*/

    /*
        Create POS order with:
        - Normal/IMEI product with/without variation
        - With/without delivery
        - Apply some promotion
     */
    public POSPage navigateToInStorePurchasePage() {
        driver.get(DOMAIN + "/order/instore-purchase");
        logger.info("Open POS page.");
        return this;
    }

    void changeBranch(String brName) {
        if (!commons.getText(CURRENT_BRANCH).equals(brName)) {
            // open branch dropdown
            while (driver.findElements(BRANCH_LIST).isEmpty()) {
                commons.click(BRANCH_DROPDOWN);
            }

            // find and change branch
            commons.getListElement(BRANCH_LIST).stream().filter(brElement -> brElement.getText().equals(brName)).findFirst().ifPresent(WebElement::click);

            // confirm change branch
            commons.click(CONFIRM_CHANGE_BRANCH_BTN);
        }

        // log
        logger.info("Select branch: %s.".formatted(brName));
    }

    void selectProduct(ProductInfo productInfo) {
        // search by product name
        commons.sendKeys(SEARCH_PRODUCT_BOX, productInfo.getMainProductNameMap().get(storeInfo.getDefaultLanguage()));

        // add product to cart
        for (String barcode : productInfo.getBarcodeList()) {
            commons.click(SEARCH_PRODUCT_BOX);

            // select product
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commons.getElement(By.xpath("//code[text() = '%s']".formatted(barcode))));

            // log
            logger.info("Select variation with barcode: %s.".formatted(barcode));
        }
    }

    void selectCustomer(CustomerInfo customerInfo) {
        if (customerInfo.getCustomerId() != 0) {
            // search customer
            String key = customerInfo.getMainEmail() != null ? customerInfo.getMainEmail() : customerInfo.getMainPhoneNumber();
            commons.sendKeys(SEARCH_CUSTOMER_BOX, key);

            // select customer
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()",
                    commons.getElement(By.xpath("//div[contains(., '%s')][@class = 'mobile-customer-profile-row__right']".formatted(customerInfo.getCustomerId()))));

            // log
            logger.info("Select customer with customerId: %s.".formatted(customerInfo.getCustomerId()));
        }
    }

    /**
     * Map: branch name, list of price type
     * <p>Ex: Product has variation var1, var2, var3, var4. And branch A, B</p>
     * <p>This function return list price type of each variation on each branch</p>
     * <p>Branch A = {FLASH SALE, WHOLESALE PRODUCT, WHOLESALE PRODUCT, SELLING PRICE} </p>
     * <p>Branch B = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN} </p>
     * <p>Branch C = {FLASH SALE, DISCOUNT CAMPAIGN, DISCOUNT CAMPAIGN, SELLING PRICE} </p>
     */
    Map<String, List<String>> getSalePriceMap(ProductInfo productInfo) {
        return brInfo.getBranchName()
                .stream()
                .collect(Collectors.toMap(brName -> brName,
                        brName -> IntStream.range(0, productInfo.getVariationModelList().size())
                                .mapToObj(varIndex -> (discountCampaignInfo.get(brName) != null)
                                        ? "DISCOUNT CAMPAIGN"
                                        : (wholesaleProductInfo.getStatusMap().get(brName).get(varIndex) ? "WHOLESALE PRODUCT" : "SELLING PRICE"))
                                .toList(),
                        (a, b) -> b));
    }

    void checkVariationPrice(int varIndex, long sellingPrice, String branchName, String variationName) {
        String varPrice;
        do {
            varPrice = commons.getText(CART_PRODUCT_PRICE, varIndex).split(STORE_CURRENCY)[0];
        } while (varPrice.isEmpty());

        long variationPrice = Long.parseLong(varPrice.replaceAll("\\D+", ""));

        String varName = variationName.isEmpty() ? "" : "[%s]".formatted(variationName);
        assertCustomize.assertEquals(variationPrice, sellingPrice, "[Failed][%s]%s Variation price should be show %s instead of %s".formatted(branchName, varName, sellingPrice, variationPrice));
        logger.info("[%s]%s Check variation price.".formatted(branchName, varName));

        // get variation quantity
        int varStock = Integer.parseInt(commons.getValue(CART_PRODUCT_QUANTITY, varIndex));
        long priceTotal = Long.parseLong(commons.getText(CART_PRICE_TOTAL, varIndex).replaceAll("\\D+", ""));
        assertCustomize.assertEquals(priceTotal, sellingPrice * varStock, "[Failed][%s]%s Variation price total should be show %s instead of %s".formatted(branchName, varName, sellingPrice * varStock, priceTotal));
        logger.info("[%s]%s Check variation total price.".formatted(branchName, varName));
    }

    void changeProductQuantityToAppliesDiscount(String priceType, int varIndex, int minDiscountCampaignStock, int wholesaleProductStock, String branchName, String variationName) {
        // get variation name
        String varName = variationName.isEmpty() ? "" : "[%s]".formatted(variationName);

        // increase quantity to discount campaign minimum requirement
        if (priceType.equals("DISCOUNT CAMPAIGN")) {
            commons.sendKeys(CART_PRODUCT_QUANTITY, varIndex, String.valueOf(minDiscountCampaignStock));
            // log
            logger.info("[%s]%s Increase quantity to match with minimum discount campaign quantity.".formatted(branchName, varName));
        } else // increase quantity to wholesale product minimum requirement
            if (priceType.equals("WHOLESALE PRODUCT")) {
                commons.sendKeys(CART_PRODUCT_QUANTITY, varIndex, String.valueOf(wholesaleProductStock));
                logger.info("[%s]%s Increase quantity to match with minimum wholesale product quantity.".formatted(branchName, varName));
            }
    }

    public void checkProductInformation(LoginInformation loginInformation, ProductInfo productInfo, int customerId) {
        // get the latest branch information
        brInfo = new BranchManagement(loginInformation).getInfo();

        // get store information
        storeInfo = new StoreInformation(loginInformation).getInfo();

        // get list segment of customer
        List<Integer> listSegmentOfCustomer = new Customers(loginInformation).getListSegmentOfCustomer(customerId);

        // get discount campaign information
        discountCampaignInfo = new ProductDiscountCampaign(loginInformation).getAllDiscountCampaignInfo(productInfo, listSegmentOfCustomer);

        // get wholesale product information
        wholesaleProductInfo = new ProductInformation(loginInformation).wholesaleProductInfo(productInfo, listSegmentOfCustomer);

        // get customer information
        CustomerInfo customerInfo = new Customers(loginInformation).getInfo(customerId);

        // get sale price map
        Map<String, List<String>> salePriceMap = getSalePriceMap(productInfo);

        // check product price on each branch
        for (String brName : brInfo.getActiveBranches()) {
            // select branch
            changeBranch(brName);

            // add product to cart
            selectProduct(productInfo);

            // get total variation
            int listProductSize = productInfo.getVariationModelList().size();

            // get minimum requirement of discount campaign
            int minimumOfRequirements = discountCampaignInfo.get(brName) != null
                    ? discountCampaignInfo.get(brName).getListOfMinimumRequirements().get(0) : 1;

            // divided minimum requirement for all variations
            List<Integer> variationMinimumRequirements = new ArrayList<>();
            IntStream.range(0, listProductSize).forEach(varIndex -> variationMinimumRequirements.add(Math.max((minimumOfRequirements - variationMinimumRequirements.stream().mapToInt(Integer::intValue).sum()) / (listProductSize - varIndex), 1)));
            Collections.reverse(variationMinimumRequirements);

            IntStream.range(0, listProductSize).forEachOrdered(quantityIndex -> {
                int modelIndex = listProductSize - quantityIndex - 1;
                String varName = productInfo.isHasModel() ? commons.getText(CART_PRODUCT_VARIATION, quantityIndex) : "";
                changeProductQuantityToAppliesDiscount(salePriceMap.get(brName).get(modelIndex),
                        quantityIndex,
                        variationMinimumRequirements.get(modelIndex),
                        wholesaleProductInfo.getStockList().get(modelIndex),
                        brName,
                        varName);
            });
            selectCustomer(customerInfo);

            commons.sleepInMiliSecond(2000);
            logger.info("Wait api check discount response.");

            IntStream.range(0, listProductSize).forEachOrdered(priceIndex -> {
                int modelIndex = listProductSize - priceIndex - 1;
                String varName = productInfo.isHasModel() ? commons.getText(CART_PRODUCT_VARIATION, priceIndex) : "";
                checkVariationPrice(priceIndex,
                        salePriceMap.get(brName).get(modelIndex).equals("WHOLESALE PRODUCT")
                                ? wholesaleProductInfo.getPriceList().get(modelIndex)
                                : productInfo.getProductSellingPrice().get(modelIndex),
                        brName,
                        varName);
            });

            checkSubtotalAndVAT(salePriceMap, productInfo, brName);
        }
    }

    void checkSubtotalAndVAT(Map<String, List<String>> salePriceMap, ProductInfo productInfo, String branchName) {
        // get subtotal
        long subtotal = Long.parseLong(commons.getText(SUB_TOTAL).replaceAll("\\D+", ""));

        // init expected subtotal
        long expectedSubtotal = 0;

        // list variations size
        int listProductSize = productInfo.getVariationModelList().size();

        // get total variations price
        for (int varIndex = 0; varIndex < listProductSize; varIndex++) {
            // get quantity index
            int quantityIndex = listProductSize - varIndex - 1;

            // get variation price
            long sellingPrice = salePriceMap.get(branchName).get(varIndex).equals("WHOLESALE PRODUCT")
                    ? wholesaleProductInfo.getPriceList().get(varIndex)
                    : productInfo.getProductSellingPrice().get(varIndex);

            // add variation price to subtotal price
            expectedSubtotal += Long.parseLong(commons.getValue(CART_PRODUCT_QUANTITY, quantityIndex)) * sellingPrice;
        }

        // check subtotal
        assertCustomize.assertEquals(subtotal, expectedSubtotal, "[Failed][%s] Subtotal should be show %s instead of %s".formatted(branchName, expectedSubtotal, subtotal));
        logger.info("[%s] Check subtotal.".formatted(branchName));

        // get VAT
        long totalVAT = Long.parseLong(commons.getText(VAT_TOTAL).replaceAll("\\D+", ""));
        long expectedVAT = (long) (expectedSubtotal * productInfo.getTaxRate());

        // check VAT
        assertCustomize.assertEquals(totalVAT, expectedSubtotal * productInfo.getTaxRate(), "[Failed][%s] VAT should be show %s instead of %s".formatted(branchName, expectedVAT, totalVAT));
        logger.info("[%s] Check VAT.".formatted(branchName));

    }

    /**
     * discountType -> 0: discount code, 1: direct discount (amount), 2: direct discount (%)
     */
    void inputDiscountCode(int discountType, String value, String branchName) {
        if (discountCampaignInfo.get(branchName) == null) {
            // open coupon popup
            commons.click(DISCOUNT);
            logger.info("[%s] Open discount popup.".formatted(branchName));

            // select discount type
            commons.click(DISCOUNT_POPUP_DISCOUNT_DROPDOWN);
            logger.info("[%s] Open discount dropdown.".formatted(branchName));

            // select discount type
            commons.click(DISCOUNT_POPUP_DISCOUNT_TYPE, discountType);
            logger.info("[%s] Select discount type: %s.".formatted(branchName, discountType == 0 ? "discount code" : discountType == 1 ? "fix amount discount" : "percentage discount"));

            // input discount value
            commons.sendKeys(DISCOUNT_POPUP_DISCOUNT_VALUE, value);
            logger.info("[%s] Input discount value: %s.".formatted(branchName, discountType == 2 ? "%s%%".formatted(value) : value));

            // apply discount
            commons.click(DISCOUNT_POPUP_APPLY_BTN);
            logger.info("[%s] Apply discount.");
        }
    }

    void selectDelivery() {
        String epoch = new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

        // open delivery popup
        commons.click(DELIVERY_CHECKBOX);
        logger.info("Open delivery popup.");

        // input customer name
        String customerName = "POS customer - %s".formatted(epoch);
        commons.sendKeys(DELIVERY_POPUP_CUSTOMER_NAME, customerName);
        logger.info("Input customer name: %s.".formatted(customerName));

        // input phone number
        commons.sendKeys(DELIVERY_POPUP_PHONE, epoch);
        logger.info("Input customer phone: %s.".formatted(epoch));

        // input email
        String email = "%s@qa.team".formatted(epoch);
        commons.sendKeys(DELIVERY_POPUP_EMAIL, email);
        logger.info("Input customer email: %s.".formatted(email));

        // select country
        String countryCode = new DataGenerator().getCountryCode(new DataGenerator().randomCountry());

    }

}
