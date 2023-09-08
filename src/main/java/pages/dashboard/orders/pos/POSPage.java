package pages.dashboard.orders.pos;

import api.dashboard.customers.Customers;
import api.dashboard.products.ProductInformation;
import api.dashboard.promotion.ProductDiscountCampaign;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.dashboard.promotion.DiscountCampaignInfo;
import utilities.model.dashboard.promotion.DiscountCodeInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
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
    DiscountCampaignInfo discountCampaignInfo;
    WholesaleProductInfo wholesaleProductInfo;
    AssertCustomize assertCustomize;
    DiscountCodeInfo discountCodeInfo;
    BranchInfo brInfo;
    StoreInfo storeInfo;
    int countFail = 0;

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
        commons.sendKeys(SEARCH_PRODUCT_BOX, productInfo.getDefaultProductNameMap().get(storeInfo.getDefaultLanguage()));

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
    Map<String, List<String>> getSalePriceMap() {
        return brInfo.getBranchName()
                .stream()
                .collect(Collectors.toMap(brName -> brName,
                        brName -> IntStream.range(0, discountCampaignInfo.getDiscountCampaignStatus().get(brName).size())
                                .mapToObj(varIndex -> discountCampaignInfo.getDiscountCampaignStatus().get(brName).get(varIndex).equals("IN_PROGRESS")
                                        ? "DISCOUNT CAMPAIGN"
                                        : wholesaleProductInfo.getStatusMap().get(brName).get(varIndex) ? "WHOLESALE PRODUCT" : "SELLING PRICE")
                                .toList(),
                        (a, b) -> b));
    }

    void checkVariationPrice(int varIndex, long sellingPrice, String branchName, String variationName) {
        String varPrice = commons.getText(CART_PRODUCT_PRICE, varIndex).split(STORE_CURRENCY)[0];
        long variationPrice = Long.parseLong(varPrice.replaceAll("\\D+", ""));

        String varName = variationName.isEmpty() ? "" : "[%s]".formatted(variationName);
        countFail = assertCustomize.assertTrue(countFail, Math.abs(variationPrice - sellingPrice) <= 1, "[Failed][%s]%s Variation price should be show %s ±1 instead of %s".formatted(branchName, varName, sellingPrice, variationPrice));
        logger.info("[%s]%s Check variation price.".formatted(branchName, varName));
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
        discountCampaignInfo = new ProductDiscountCampaign(loginInformation).getDiscountCampaignInfo(productInfo, listSegmentOfCustomer);

        // get wholesale product information
        wholesaleProductInfo = new ProductInformation(loginInformation).wholesaleProductInfo(productInfo, listSegmentOfCustomer);

        // get customer information
        CustomerInfo customerInfo = new Customers(loginInformation).getInfo(customerId);

        // get sale price map
        Map<String, List<String>> salePriceMap = getSalePriceMap();

        // check product price on each branch
        for (String brName : brInfo.getActiveBranches()) {
            changeBranch(brName);
            selectProduct(productInfo);
            int listProductSize = productInfo.getVariationModelList().size();
            IntStream.range(0, listProductSize).forEachOrdered(quantityIndex -> {
                int modelIndex = listProductSize - quantityIndex - 1;
                String varName = productInfo.isHasModel() ? commons.getText(CART_PRODUCT_VARIATION, quantityIndex) : "";
                changeProductQuantityToAppliesDiscount(salePriceMap.get(brName).get(modelIndex),
                        quantityIndex,
                        discountCampaignInfo.getDiscountCampaignMinQuantity(),
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

        }
    }

}
