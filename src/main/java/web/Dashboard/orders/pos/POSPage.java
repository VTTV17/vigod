package web.Dashboard.orders.pos;

import api.Seller.customers.APIAllCustomers;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.all_products.APISuggestionProduct.SuggestionProductsInfo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.orders.pos.POSElement.DiscountType.*;
import static web.Dashboard.orders.pos.POSElement.SearchType.barcode;
import static web.Dashboard.orders.pos.POSElement.SearchType.getAllSearchType;

public class POSPage extends POSElement {

    final static Logger logger = LogManager.getLogger(POSPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    AssertCustomize assertCustomize;


    public POSPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public void inputProductSearchTerm(String searchTerm) {
        commonAction.sendKeys(loc_txtSearchProduct, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search Product box.");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToUsePOS(String permission) {
        if (permission.contentEquals("A")) {
            commonAction.switchToWindow(1);
            new POSPage(driver).inputProductSearchTerm("Test Permission");
            commonAction.closeTab();
            commonAction.switchToWindow(0);
        } else if (permission.contentEquals("D")) {
            Assert.assertEquals(commonAction.getAllWindowHandles().size(), 1);
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
    void navigateToInStorePurchasePage() {
        driver.get(DOMAIN + "/order/instore-purchase");
        driver.navigate().refresh();
        logger.info("Open POS page.");
    }

    void selectBranch(SuggestionProductsInfo productsInfo) {
        // open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // select branch
        commonAction.clickJS(By.xpath(str_ddvBranches.formatted(productsInfo.getBranchName())));

        // confirm switch branch
        if (!commonAction.getListElement(loc_dlgConfirmChangeBranch).isEmpty()) {
            commonAction.click(loc_dlgConfirmChangeBranch_btnOK);
        }

        // log
        logger.info("Select branch: %s.".formatted(productsInfo.getBranchName()));
    }

    void searchAndSelectProduct(SuggestionProductsInfo productsInfo) {
        // open search type list
        commonAction.clickJS(loc_ddvSelectedSearchType);

        // change search type to search by barcode
        commonAction.clickJS(loc_ddlSearchType, getAllSearchType().indexOf(barcode));

        // search product
        String keyword = (productsInfo.getModelId() == 0)
                ? "%s".formatted(productsInfo.getItemId())
                : "%s-%s".formatted(productsInfo.getItemId(), productsInfo.getModelId());
        commonAction.sendKeys(loc_txtSearchProduct, keyword);
        logger.info("Search keyword: %s.".formatted(keyword));

        // select product
        commonAction.clickJS(By.xpath(str_ddvProduct.formatted(keyword)));
    }

    void addProductToCart(SuggestionProductsInfo productsInfo) {
        // search and select product
        searchAndSelectProduct(productsInfo);

        // input quantity
        long quantity = nextLong(Math.max(Optional.ofNullable(productsInfo.getRemainingStock()).orElse(0L), 1L)) + 1L;
        commonAction.sendKeys(loc_txtItemQuantity, String.valueOf(quantity));
        logger.info("Input cart quantity: %,d.".formatted(quantity));

        // select lot if any
        if (!commonAction.getListElement(loc_tblCart_lnkSelectLot).isEmpty()) {
            // open select lot popup
            commonAction.clickJS(loc_tblCart_lnkSelectLot, 0);

            // select lot
            if (!commonAction.getListElement(loc_dlgSelectLot).isEmpty()) {
                if (!commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).isEmpty()) {
                    int bound = commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).size();
                    for (int lotIndex = 0; lotIndex < bound; lotIndex++) {
                        if (quantity > 0) {
                            // input quantity
                            long availableQuantity = Long.parseLong(commonAction.getText(loc_dlgSelectLot_lblAvailableQuantity));
                            commonAction.sendKeys(loc_dlgSelectLot_txtConfirmQuantity, String.valueOf(Math.min(quantity, availableQuantity)));
                            logger.info("Input lot quantity: %,d.".formatted(quantity));

                            // get current quantity
                            quantity -= Math.min(quantity, availableQuantity);
                        }
                    }

                    // save changes
                    commonAction.click(loc_dlgSelectLot_btnConfirm);
                } else logger.warn("Can not found lot.");
            }
        }
    }

    void selectCustomer(CustomerInfo customerInfo) {
        commonAction.sendKeys(txtSearchCustomer, customerInfo.getMainEmailName());
        logger.info("Input keywords: %s.".formatted(customerInfo.getMainEmailName()));
        commonAction.clickJS(By.xpath(str_ddvCustomer.formatted(customerInfo.getCustomerId())));
    }


    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24814
    LoginInformation staffLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    APISuggestionProduct suggestionProductWithStaffToken;

    public POSPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public POSPage getLoginInformation(LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        suggestionProductWithStaffToken = new APISuggestionProduct(staffLoginInformation);
        return this;
    }

    public void checkPOSPermission() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isCreateOrder()) {
            // check can access to POS page by URL
            assertCustomize.assertTrue(driver.getCurrentUrl().contains("/order/instore-purchase"), "Can not access to POS page.");

            // check others permission
            checkAddStock();
            checkAddCustomer();
            checkApplyDiscount();
            checkCreateDebtOrder();
            checkNotApplyEarningPoint();
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted page is not shown.");
        }

        // log
        logger.info("Check permission: Orders >> POS >> Create order.");
    }

    void checkAddStock() {
        // get product for add stock on POS
        SuggestionProductsInfo productsInfo = suggestionProductWithStaffToken.findProductInformationForAddStockOnPOS();

        if (productsInfo.getItemId() != 0) {
            // select branch
            selectBranch(productsInfo);

            // add product to cart
            addProductToCart(productsInfo);

            // check icon add stock show or not
            assertCustomize.assertFalse(commonAction.getListElement(loc_icnAddStock).isEmpty(), "Product out of stock, but add stock label does not show.");

            // check permission
            if (!commonAction.getListElement(loc_icnAddStock).isEmpty()) {
                if (permissions.getProduct().getInventory().isUpdateStock()) {
                    // get current quantity
                    long quantity = Long.parseLong(commonAction.getValue(loc_txtItemQuantity));

                    // open add stock/imei popup
                    commonAction.clickJS(loc_icnAddStock);

                    // add stock
                    if (!commonAction.getListElement(loc_dlgAddStock).isEmpty()) {
                        commonAction.sendKeys(loc_dlgAddStock_txtStock, String.valueOf(quantity));
                        logger.info("Add stock quantity: %,d.".formatted(quantity));
                        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgAddStock_btnApply, loc_dlgToastSuccess),
                                "Can not add stock.");
                    }

                    // add imei
                    if (!commonAction.getListElement(loc_dlgAddIMEI).isEmpty()) {
                        IntStream.iterate(0, index -> index < quantity, index -> index + 1)
                                .mapToObj(index -> "%s\n".formatted(Instant.now().toEpochMilli()))
                                .forEach(imei -> {
                                    commonAction.sendKeys(loc_dlgAddIMEI_txtIMEI, imei);
                                    logger.info("Add imei: %s.".formatted(imei.replace("\n", "")));
                                });
                        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgAddIMEI_btnSave, loc_dlgToastSuccess),
                                "Can not add imei.");
                    }
                } else {
                    assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnAddStock),
                            "Restricted popup is not shown.");
                }
            }
        }

        // log
        logger.info("Check permission: Product >> Inventory >> Update stock.");
    }

    void checkAddCustomer() {
        if (permissions.getCustomer().getCustomerManagement().isAddCustomer()) {
            // open add customer popup
            commonAction.clickJS(loc_icnAddCustomer);

            // check add customer popup is shown or not
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgAddCustomer).isEmpty(),
                    "Can not open add customer popup.");
            if (!commonAction.getListElement(loc_dlgAddCustomer).isEmpty()) {
                commonAction.sendKeys(loc_dlgAddCustomer_txtFullName, RandomStringUtils.randomAlphabetic(5));
                commonAction.sendKeys(loc_dlgAddCustomer_txtPhoneNumber, String.valueOf(Instant.now().toEpochMilli()));
                commonAction.click(loc_dlgAddCustomer_btnAdd);

                assertCustomize.assertTrue(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not create customer.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnAddCustomer), "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Customer >> Customer management >> Add customer.");
    }

    void applyDiscount(DiscountType discountType) {
        // open discount popup
        commonAction.clickJS(loc_btnPromotion);

        // check discount popup is shown or not
        if (!commonAction.getListElement(loc_dlgDiscount).isEmpty()) {
            // switch to tab
            commonAction.click(loc_dlgDiscount_tabDiscountType, getAllDiscountType().indexOf(discountType));

            // input discount value
            switch (discountType) {
                case discountCode -> {
                    String discountValue = commonAction.getText(loc_dlgDiscount_tabDiscountCode_lblDiscountCode);
                    commonAction.sendKeys(loc_dlgDiscount_tabDiscountCode_txtEnterCouponCode, discountValue);
                    commonAction.click(loc_dlgDiscount_tabDiscountCode_btnApply);
                    commonAction.clickJS(loc_dlgDiscount_tabDiscountCode_btnSave);
                }
                case discountAmount -> {
                    String discountValue = String.valueOf(nextLong(MAX_PRICE));
                    commonAction.sendKeys(loc_dlgDiscount_tabDiscountAmount_txtAmount, discountValue);
                    commonAction.click(loc_dlgDiscount_tabDiscountAmount_btnApply);
                }
                case discountPercent -> {
                    String discountValue = String.valueOf(nextInt(100));
                    commonAction.sendKeys(loc_dlgDiscount_tabDiscountPercent_txtPercent, discountValue);
                    commonAction.click(loc_dlgDiscount_tabDiscountPercent_btnApply);
                }
            }
        }
    }

    void checkApplyDiscount() {
        // get item for create pos
        SuggestionProductsInfo productsInfo = suggestionProductWithStaffToken.findProductInformationForAddToCartInPOS();

        if (productsInfo.getItemId() != 0) {
            // navigate to POS page by URL
            navigateToInStorePurchasePage();

            // add product to cart
            searchAndSelectProduct(productsInfo);

            // check permission
            if (permissions.getOrders().getOrderManagement().isApplyDiscount()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnPromotion, loc_dlgDiscount), "Can not open discount popup.");
                checkAddDiscountAmount(productsInfo);
                checkAddDiscountPercent(productsInfo);
                checkApplyDiscountCode(productsInfo);
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPromotion), "Restricted popup is not shown.");
            }
        } logger.warn("Can not found product for check apply discount.");

        // log
        logger.info("Check permission: Orders >> Order management >> Apply discount.");
    }

    void checkAddDiscountAmount(SuggestionProductsInfo productsInfo) {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // add product to cart
        searchAndSelectProduct(productsInfo);

        // apply discount
        applyDiscount(discountAmount);

        if (permissions.getOrders().getPOSInstorePurchase().isAddDirectDiscount()) {
            assertCustomize.assertFalse(checkPermission.isAccessRestrictedPresent(), "Can not apply discount amount.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Orders >> POS >> Apply direct discount (amount).");
    }

    void checkAddDiscountPercent(SuggestionProductsInfo productsInfo) {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // add product to cart
        searchAndSelectProduct(productsInfo);

        // apply discount
        applyDiscount(discountPercent);

        if (permissions.getOrders().getPOSInstorePurchase().isAddDirectDiscount()) {
            assertCustomize.assertFalse(checkPermission.isAccessRestrictedPresent(), "Can not apply discount percent.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Orders >> POS >> Apply direct discount (percentage).");
    }

    void checkApplyDiscountCode(SuggestionProductsInfo productsInfo) {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // add product to cart
        searchAndSelectProduct(productsInfo);

        // apply discount
        applyDiscount(discountCode);

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isApplyDiscountCode()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not apply discount code.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Orders >> POS >> Apply discount code.");
    }

    void checkCreateDebtOrder() {
        // get item for create pos
        SuggestionProductsInfo productsInfo = suggestionProductWithStaffToken.findProductInformationForCreatePOSOrder();

        // check product
        if (productsInfo.getItemId() != 0) {
            // navigate to POS page by URL
            navigateToInStorePurchasePage();

            // select branch
            selectBranch(productsInfo);

            // add product to cart
            addProductToCart(productsInfo);

            // get total amount
            long totalAmount = Long.parseLong(commonAction.getText(loc_lblTotalAmount).replaceAll("\\D+", ""));
            logger.info("Total amount: %,d.".formatted(totalAmount));

            // input received amount
            long receivedAmount = nextLong(totalAmount);
            commonAction.sendKeys(loc_txtReceivedAmount, String.valueOf(receivedAmount));
            logger.info("Input received amount: %,d.".formatted(receivedAmount));

            // complete order
            commonAction.click(loc_btnComplete);

            // check received not enough popup is shown or not
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgReceivedNotEnough).isEmpty(),
                    "The received amount is not enough popup is not shown.");

            if (!commonAction.getListElement(loc_dlgReceivedNotEnough).isEmpty()) {
                // click apply
                commonAction.click(loc_dlgReceivedNotEnough_btnApply);

                // check permission
                if (permissions.getOrders().getPOSInstorePurchase().isCreateDebtOrder()) {
                    assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not complete order.");
                } else {
                    assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
                }
            }
        } else logger.warn("Can not find product for create debt order.");

        // log
        logger.info("Check permission: Orders >> POS >> Create debt order.");
    }

    void checkNotApplyEarningPoint() {
        SuggestionProductsInfo productsInfo = suggestionProductWithStaffToken.findProductInformationForCreatePOSOrder();
        if (productsInfo.getItemId() != 0) {
            CustomerInfo customerInfo = new APIAllCustomers(staffLoginInformation).getAccountCustomerForCreatePOS();
            if (customerInfo.getCustomerId() != 0) {
                // navigate to POS page by URL
                navigateToInStorePurchasePage();

                // check permission
                if (permissions.getMarketing().getLoyaltyPoint().isViewPointProgramInformation()) {
                    // select branch
                    selectBranch(productsInfo);

                    // add product to cart
                    addProductToCart(productsInfo);

                    // select customer
                    selectCustomer(customerInfo);

                    // check not apply earning point checkbox is shown or not
                    assertCustomize.assertFalse(commonAction.getListElement(loc_chkNotApplyEarningPoint).isEmpty(), "Not apply earning point is not shown.");

                    if (!commonAction.getListElement(loc_chkNotApplyEarningPoint).isEmpty()) {
                        // check permission
                        if (permissions.getOrders().getPOSInstorePurchase().isNotApplyEarningPoint()) {
                            assertCustomize.assertFalse(commonAction.isDisabledJS(loc_chkNotApplyEarningPoint), "Not apply earning point is disabled.");
                        } else {
                            assertCustomize.assertTrue(commonAction.isDisabledJS(loc_chkNotApplyEarningPoint), "Not apply earning point is enabled.");
                        }
                    }
                } else logger.info("Not apply earning point is hidden, ignored.");
            } else logger.warn("Can not find customer for check not apply earning point.");
        } else logger.warn("Can not find product for check not apply earning point.");

        // log
        logger.info("Check permission: Orders >> POS >> Not apply earning point.");
    }
}
