package web.Dashboard.orders.pos.create_order;

import api.Seller.orders.pos.APIPOSApplyDiscount;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.pos.ReceivedAmountType;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.DOMAIN;

public class POSPage extends POSElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger();

    public POSPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    public POSPage navigateToPOSPage() {
        // Navigate to POS page
        driver.get("%s/order/instore-purchase".formatted(DOMAIN));

        // Log
        logger.info("Navigate to POS page by URL");
        return this;
    }

    void selectBranch(String branchName) {
        // Open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // Select branch
        commonAction.clickJS(loc_lstBranches(branchName));

        // Confirm switch branch
        if (!commonAction.getListElement(loc_dlgConfirmSwitchBranch).isEmpty()) {
            commonAction.click(loc_dlgConfirmSwitchBranch_btnOK);
        }

        // Log
        logger.info("Select branch: {}", branchName);
    }

    void selectProduct(LoginInformation loginInformation, List<Integer> productIds, int quantity) {
        // Select product
        productIds.forEach(productId -> {
            // Get product information
            APIProductDetailV2.ProductInfoV2 infoV2 = new APIProductDetailV2(loginInformation).getInfo(productId);

            // Search product
            commonAction.sendKeys(loc_txtProductSearchBox, infoV2.getName());

            // Log
            logger.info("Search product, keyword: {}", infoV2.getName());

            // Select product/variations
            infoV2.getBarcodeList().forEach(barcode -> {
                // Add product/variation to cart
                commonAction.clickJS(loc_lstProductResult(barcode));

                // Wait API response
                commonAction.sleepInMiliSecond(500, "Wait product/variation is added to cart");

                // Log
                logger.info("Add product/variation to cart, barcode: {}", barcode);

                // Get product name
                String productName = infoV2.getName();

                // Get variation value
                String variationValue = infoV2.isHasModel()
                        ? infoV2.getVariationValuesMap()
                        .get(new StoreInformation(loginInformation).getInfo().getDefaultLanguage())
                        .get(infoV2.getBarcodeList().indexOf(barcode)).replace("|", " | ")
                        : "";

                // Input quantity
                commonAction.sendKeys(infoV2.isHasModel()
                                ? loc_txtProductQuantity(productName, variationValue)
                                : loc_txtProductQuantity(productName),
                        String.valueOf(quantity));

                // Select IMEI if product is managed by IMEI
                if (!infoV2.getInventoryManageType().equals("PRODUCT")) {
                    // Open Select IMEI popup
                    commonAction.click(infoV2.isHasModel()
                            ? loc_btnSelectIMEI(productName, variationValue)
                            : loc_btnSelectIMEI(productName));

                    // Log
                    logger.info("Open select IMEI popup");

                    // Select IMEI
                    IntStream.range(0, quantity)
                            .mapToObj(imeiIndex -> commonAction.getText(loc_dlgSelectIMEI_lstIMEI)) // Get IMEI value
                            .forEach(imeiValue -> {
                                // Select IMEI
                                commonAction.click(loc_dlgSelectIMEI_lstIMEI);
                                // Log
                                logger.info("Select IMEI: {}", imeiValue);
                            });

                    // Save changes
                    commonAction.click(loc_dlgSelectIMEI_btnSave);

                    // Log
                    logger.info("Close Select IMEI popup");
                }

                // Select Lot if product quantity is managed by Lot
                if (infoV2.isLotAvailable()) {
                    // Open Select Lot popup
                    commonAction.click(infoV2.isHasModel()
                            ? loc_btnSelectLot(productName, variationValue)
                            : loc_btnSelectLot(productName));

                    // Log
                    logger.info("Open Select Lot popup");

                    // Add lot quantity
                    commonAction.sendKeys(loc_dlgSelectLot_txtQuantity, String.valueOf(quantity));

                    // Log
                    logger.info("Select lot quantity: {}", quantity);

                    // Save changes
                    commonAction.click(loc_dlgSelectLot_btnSave);

                    // Log
                    logger.info("Close Select Lot popup");
                }
            });
        });
    }

    void addCustomer(int customerId) {

    }

    public void createPOSOrder(LoginInformation loginInformation, BranchInfo branchInfo, List<Integer> productIds, int stockQuantity) {
        // Get cart quantity
        int cartQuantity = nextInt(stockQuantity) + 1;

        // Select branch
        String branchName = branchInfo.getBranchName().get(nextInt(branchInfo.getBranchName().size()));
        selectBranch(branchName);

        // Add product to cart
        selectProduct(loginInformation, productIds, cartQuantity);

        // Add customer

        // Apply discount
        applyDiscount();
    }

    public Double getTotalAmount() {
        String total = commonAction.getText(loc_lblTotalAmount);
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(total);
        String matchNumber = "";
        while (matcher.find()) {
            matchNumber = matchNumber + matcher.group();
        }
        return Double.parseDouble(matchNumber);
    }

    public void inputReceiveAmount(double amount) {
        commonAction.inputText(loc_txtReceiveAmount, String.valueOf(amount));
        logger.info("Input receive amount: {}", amount);
    }

    public Double inputReceiveAmount(ReceivedAmountType receivedAmountType) {
        double receiveAmount = 0;
        switch (receivedAmountType) {
            case NONE -> inputReceiveAmount(receiveAmount);
            case FULL -> {
                double total = getTotalAmount();
                inputReceiveAmount(total);
                receiveAmount = total;
            }
            case PARTIAL -> {
                double random = DataGenerator.generatNumberInBound(1000, getTotalAmount());
                inputReceiveAmount(random);
                receiveAmount = random;
            }
        }
        commonAction.inputText(loc_txtReceiveAmount, String.valueOf(receiveAmount));
        return receiveAmount;
    }

    public enum POSPaymentMethod {
        CASH, BANKTRANSFER, POS
    }

    public void clickOnViewAllPayment() {
        commonAction.click(loc_lnkViewAllPayment);
        logger.info("Click on View All Payment link");
    }

    public void selectPaymentMethod(POSPaymentMethod paymentMethod) {
        clickOnViewAllPayment();
        //wait popup show
        commonAction.getElements(loc_lstPaymentMethod, 2);
        switch (paymentMethod) {
            case CASH -> {
                if (!commonAction.getAttribute(loc_lstPaymentMethod, 0, "class").contains("selected-item"))
                    commonAction.click(loc_lstPaymentMethod, 0);
                else return;
            }
            case BANKTRANSFER -> {
                if (!commonAction.getAttribute(loc_lstPaymentMethod, 1, "class").contains("selected-item"))
                    commonAction.click(loc_lstPaymentMethod, 1);
                else return;
            }
            case POS -> {
                if (!commonAction.getAttribute(loc_lstPaymentMethod, 2, "class").contains("selected-item")) {
                    commonAction.click(loc_lstPaymentMethod, 2);
                    commonAction.inputText(loc_txtPOSReceiptCode, new DataGenerator().generateString(10));
                } else return;
            }
        }
        new ConfirmationDialog(driver).clickBlueBtn();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        logger.info("Select payment method: {}", paymentMethod);
    }

    public void configApplyEarningPoint(boolean isApply) {
        if (!isApply)
            commonAction.checkTheCheckBoxOrRadio(loc_chkNotApplyEarningPoint, loc_lblNotApplyEarningPoint);
        else commonAction.uncheckTheCheckboxOrRadio(loc_chkNotApplyEarningPoint, loc_lblNotApplyEarningPoint);
        logger.info("Config apply earning point: {}", isApply);
    }

    public void applyDiscount() {
        // Open Discount popup
        commonAction.click(loc_btnPromotion);

        // Log
        logger.info("Open Discount popup");

        // Select discount code if any
        if (!commonAction.getListElement(loc_dlgDiscount_lstDiscountCode, 1000).isEmpty()) {
            // Get discount code value
            String code = commonAction.getText(loc_dlgDiscount_lstDiscountCodeValue);

            // Apply first discount
            commonAction.clickJS(loc_dlgDiscount_lstDiscountCode);

            // Check discount is applied or not
            logger.info("Apply discount code: {}, status: {}", code, commonAction.getListElement(loc_dlgToast).isEmpty() ? "FAILED" : "SUCCESSFULLY");
        }

        // Apply discount amount/percentage
        if (nextBoolean()) {
            // Switch to discount amount tab
            commonAction.click(loc_dlgDiscount_tabDiscountAmount);

            // Log
            logger.info("Switch to Discount amount tab");

            // Get discount amount value
            long amount = nextLong(MAX_PRICE);

            // Input discount amount
            commonAction.sendKeys(loc_dlgDiscount_txtDiscountAmountValue, String.valueOf(amount));

            // Log
            logger.info("Discount amount: {}đ", amount);
        } else {
            // Switch to discount percent tab
            commonAction.click(loc_dlgDiscount_tabDiscountPercentage);

            // Log
            logger.info("Switch to Discount percentage tab");

            // Get discount percent value
            long percent = nextInt(100);

            // Input percent amount
            commonAction.sendKeys(loc_dlgDiscount_txtDiscountPercentValue, String.valueOf(percent));

            // Log
            logger.info("Discount percent: {}%", percent);
        }

        // Apply discount
        commonAction.click(loc_dlgDiscount_btnApply);

        // Log
        logger.info("Close Discount popup");
    }
}
