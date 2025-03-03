package web.Dashboard.orders.pos.create_order;

import api.Seller.customers.APICustomerDetail;
import api.Seller.login.Login;
import api.Seller.marketing.LoyaltyPoint;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.products.all_products.APIGetProductDetail;
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation;
import api.Seller.products.all_products.APIProductConversionUnit;
import api.Seller.products.all_products.APIProductConversionUnit.ConversionUnitItem;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.data.GetDataByRegex;
import utilities.enums.Domain;
import utilities.enums.PaymentStatus;
import utilities.enums.pos.ReceivedAmountType;
import utilities.model.dashboard.customer.CustomerInfoFull;
import utilities.model.dashboard.customer.CustomerPhone;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.loyaltyPoint.LoyaltyPointInfo;
import utilities.model.dashboard.orders.orderdetail.*;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.orders.pos.create_order.deliverydialog.DeliveryDialog;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;

public class POSPage extends POSElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger();
    LoginInformation loginInformation;
    Domain domain = Domain.VN;

    public POSPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    public POSPage(WebDriver driver, Domain domain) {
        this(driver);
        this.domain = domain;
    }

    public POSPage getLoginInfo(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        return this;
    }

    public POSPage navigateToPOSPage() {
        String url = domain.equals(Domain.VN) ? DOMAIN : DOMAIN_BIZ;
        // Navigate to POS page
        driver.get("%s/order/instore-purchase".formatted(url));

        // Log
        logger.info("Navigate to POS page by URL");
        return this;
    }

    private String branchName;

    public void selectBranch(String branchName) {
        // Get branchName for calculate stock in branch
        this.branchName = branchName;

        // Open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // Select branch
        commonAction.clickJS(loc_lstBranches(branchName));

        // Confirm switch branch
        if (!commonAction.getListElement(loc_dlgConfirmSwitchBranch).isEmpty()) {
//            commonAction.click(loc_dlgConfirmSwitchBranch_btnOK);
            new ConfirmationDialog(driver).clickOKBtn_V2();
        }

        // Log
        logger.info("Select branch: {}", branchName);
    }

    public void selectProduct(LoginInformation loginInformation, List<Integer> productIds) {

        var branchManagementInfo = new BranchManagement(loginInformation).getInfo();

        int selectedBranchIndex = branchManagementInfo.getBranchName().indexOf(branchName);

        int branchId = branchManagementInfo.getBranchID().get(selectedBranchIndex);

        // Select product
        productIds.forEach(productId -> {
            ProductInformation productDetailInfo = new APIGetProductDetail(loginInformation).getProductInformation(productId);

            // Search product
            commonAction.sendKeys(loc_txtProductSearchBox, productDetailInfo.getName());
            commonAction.click(loc_txtProductSearchBox);

            // Log
            logger.info("Search product, keyword: {}", productDetailInfo.getName());

            // Get list conversion unit
            List<ConversionUnitItem> unitItems = new APIProductConversionUnit(loginInformation).getItemConversionUnit(productId);

            // If product has conversion unit, only add conversion unit
            if (unitItems.isEmpty()) {
                // Select product/variations

                List<Integer> modelIds = productDetailInfo.isHasModel() ? APIGetProductDetail.getVariationModelList(productDetailInfo) : Collections.singletonList(null);

                modelIds.forEach(modelId -> {
                    // Get current stock in branch
                    int currentStock = APIGetProductDetail.getStockByModelAndBranch(productDetailInfo, modelId, branchId);

                    List<String> barCodes = productDetailInfo.isHasModel() ? APIGetProductDetail.getBarcodeList(productDetailInfo) : List.of(productDetailInfo.getBarcode());

                    // Add product/variation to cart
                    addProductToCart(loginInformation, productDetailInfo, barCodes.get(modelIds.indexOf(modelId)), currentStock, modelIds.indexOf(modelId), "-");
                });
            } else {
                // Select conversion unit
                unitItems.forEach(unit -> {
                    // Get current stock
                    int currentStock = APIGetProductDetail.getStockByModelAndBranch(productDetailInfo, unit.getModelId(), branchId)
                                       / unit.getQuantity();

                    // Add conversion unit to cart
                    addProductToCart(loginInformation, productDetailInfo, unit.getBarcode(), currentStock, APIGetProductDetail.getVariationModelList(productDetailInfo).indexOf(unit.getModelId()), unit.getConversionUnitName());
                });
            }
        });
    }

    void addProductToCart(LoginInformation loginInformation, ProductInformation productInfo, String barcode, int currentStock, int varIndex, String unitName) {
        // Return early if the current stock is zero or less
        if (currentStock < 1) {
            logger.info("Stock for Product/Variation is {}. Item will not be added to the cart.", currentStock);
            return;
        }

        // Add conversion unit to cart
        commonAction.clickJS(loc_lstProductResult(barcode));

        // Wait API response
        UICommonAction.sleepInMiliSecond(500, "Wait product/variation/conversion unit is added to cart");

        // Log
        logger.info("Add product/variation/conversion unit to cart, barcode: {}", barcode);

        // Get product name
        String productName = productInfo.getName();

        String langKey = new StoreInformation(loginInformation).getInfo().getDefaultLanguage();

        // Get variation value
        String variationValue = productInfo.isHasModel()
                ? APIGetProductDetail.getVariationValue(productInfo, langKey, varIndex).replace("|", " | ")
                : "";

        // Get quantity
        int quantity = currentStock > 5 ? nextInt(3) + 1 : nextInt(currentStock) + 1; //Restrict the maximum quantity to 3

        // Input quantity
        commonAction.sendKeys(productInfo.isHasModel()
                        ? loc_txtProductQuantity(productName, variationValue, unitName)
                        : loc_txtProductQuantity(productName, unitName),
                String.valueOf(quantity));

        // Select Lot if product quantity is managed by Lot
        if (productInfo.isLotAvailable()) {
            // Open Select Lot popup
            commonAction.click(productInfo.isHasModel()
                    ? loc_btnSelectLot(productName, variationValue, unitName)
                    : loc_btnSelectLot(productName, unitName));

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

        // Select IMEI if product is managed by IMEI
        if (!productInfo.getInventoryManageType().equals("PRODUCT")) {

            // Retry logic to open Select IMEI popup and confirm it's open
            commonAction.attemptWithRetry(5, 1000, () -> {
                // Attempt to open the IMEI popup
                commonAction.click(productInfo.isHasModel()
                        ? loc_btnSelectIMEI(productName, variationValue, unitName)
                        : loc_btnSelectIMEI(productName, unitName));

                // Log attempt
                logger.info("Attempted to open select IMEI popup");

                // Check if the popup is open, throw exception if not to trigger retry
                if (commonAction.getListElement(loc_dlgSelectIMEI).isEmpty()) {
                    throw new IllegalStateException("IMEI popup not opened");
                }
            });

            // Log success
            logger.info("Successfully opened select IMEI popup");

            // Select IMEI
            IntStream.range(0, quantity)
                    .mapToObj(a -> commonAction.getText(loc_dlgSelectIMEI_lstIMEI)) // Get IMEI value
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

        // Log
        logger.info("Add conversion unit to cart, productName: {}, variationValue: {}, unitName: {}, quantity: {}",
                productName,
                variationValue.isEmpty() ? "None" : variationValue,
                unitName.equals("-") ? "None" : unitName,
                quantity);
    }

    void addCustomer(int customerId) {

    }

    public POSPage selectCustomer(String name) {

        //After customer name is input, the text box is still empty. The retry mechanism below helps resolve the issue by retrying to input the customer name a few times
        commonAction.attemptWithRetry(5, 1000, () -> {
            commonAction.inputText(loc_txtCustomerSearchBox, name);
            logger.info("Input customer to search: {}", name);
            UICommonAction.sleepInMiliSecond(500, "Wait for search API to start");

            if (commonAction.getValue(loc_txtCustomerSearchBox).isEmpty()) {
                throw new IllegalStateException("Search customer text box is empty after being filled with data");
            }
        });

        commonAction.click(loc_txtCustomerSearchBox);
        commonAction.click(loc_lstCustomerResult(name));

        new HomePage(driver).waitTillLoadingDotsDisappear();
        return this;
    }

    public DeliveryDialog tickDelivery() {
        commonAction.click(loc_chkDelivery);
        logger.info("Ticked Delivery check box");
        return new DeliveryDialog(driver);
    }

    /**
     * Opt to delivery or not.
     * Self-Delivery is selected by default.
     * Selection of other shipping service providers will be handled later
     *
     * @param isDeliveryOpted if it is true, Delivery option is checked
     */
    public void selectDelivery(boolean isDeliveryOpted) {
        if (!isDeliveryOpted) return;

        tickDelivery().clickShippingProviderDropdown().clickSaveBtn();
        new HomePage(driver).waitTillLoadingDotsDisappear();

        // TODO Handle specific delivery service provider
    }

    /**
     * Opt to delivery or not.
     * This function randomly inputs an phone and an address if they are undefined.
     * Self-Delivery is selected by default.
     * Selection of other shipping service providers will be handled later
     *
     * @param isDeliveryOpted if it is true, Delivery option is checked
     * @param customerData
     */
    public void selectDelivery(boolean isDeliveryOpted, UICreateCustomerData customerData) {
        if (!isDeliveryOpted) return;

        DeliveryDialog deliveryDlg = tickDelivery();

        if (deliveryDlg.getCustomerName().isEmpty()) {
            logger.info("Customer phone is undefined. Randomly filling the name...");
            deliveryDlg.inputCustomerName(customerData.getName());
        }
        if (deliveryDlg.getCustomerPhone().isEmpty()) {
            logger.info("Customer phone is undefined. Randomly filling the phone...");
            deliveryDlg.inputCustomerPhone(customerData.getPhone());
        }

        if (deliveryDlg.getAddress().isEmpty()) {
            logger.info("Customer address is undefined. Randomly filling the address...");
            deliveryDlg.fillAddress(customerData);
        }

        deliveryDlg.clickShippingProviderDropdown().clickSaveBtn();

        new HomePage(driver).waitTillLoadingDotsDisappear();

        // TODO Handle specific delivery service provider
    }

    /**
     * Make an order for a list of products on a specific branch
     * Available discounts are automatically applied
     *
     * @param loginInformation
     * @param branchName
     * @param productIds
     */
    public void createPOSOrder(LoginInformation loginInformation, String branchName, List<Integer> productIds) {
        // Select branch
        selectBranch(branchName);

        // Add product to cart
        selectProduct(loginInformation, productIds);

        // Add customer

        // Apply discount
        applyDiscount();
    }

    /**
     * Make an order for a list of products on a random branch
     * Available discounts are automatically applied
     *
     * @param loginInformation
     * @param branchInfo
     * @param productIds
     */
    public void createPOSOrder(LoginInformation loginInformation, BranchInfo branchInfo, List<Integer> productIds) {
        //Randomly select a branch to order from
        String branchName = branchInfo.getBranchName().get(nextInt(branchInfo.getBranchName().size()));

        createPOSOrder(loginInformation, branchName, productIds);
    }


    public Double getTotalAmount() {
        String total = commonAction.getText(loc_lblTotalAmount);
        logger.info("Total amount: {}", total);
        return GetDataByRegex.getAmountByRegex(total);
    }

    public void inputReceiveAmount(String amount) {
        new HomePage(driver).waitTillLoadingDotsDisappear();
        UICommonAction.sleepInMiliSecond(1000, "Wait a little for better UI stability");
        commonAction.click(loc_txtReceiveAmount);
        Actions actions = new Actions(driver);
        // Select all text (Ctrl + A for Windows/Linux, Command + A for Mac)
        actions.keyDown(Keys.CONTROL) // Or Keys.COMMAND for Mac
                .sendKeys("a")
                .keyUp(Keys.CONTROL)     // Or Keys.COMMAND for Mac
                .perform();

        // Press the Delete key
        actions.sendKeys(Keys.DELETE).perform();
        commonAction.inputText(loc_txtReceiveAmount, amount);
        logger.info("Input receive amount: {}", amount);
    }

    public Double inputReceiveAmount(ReceivedAmountType receivedAmountType) {
        new HomePage(driver).waitTillLoadingDotsDisappear();
        UICommonAction.sleepInMiliSecond(1000, "Wait a little for better UI stability");
        String currency = getCurrencySymbol();
        double randomFrom = currency.equals("đ") ? 1 : 0.01;
        String formatReceiveAmount = currency.equals("đ") ? "%.0f" : "%.2f";
        double receiveAmount = (Objects.requireNonNull(receivedAmountType) == ReceivedAmountType.FULL)
                ? getTotalAmount()
                : ((receivedAmountType == ReceivedAmountType.PARTIAL)
                ? DataGenerator.generatNumberInBound(randomFrom, getTotalAmount())
                : 0);
        UICommonAction.sleepInMiliSecond(3000, "Wait a little for better UI stability");
        if(receivedAmountType != ReceivedAmountType.FULL)
            inputReceiveAmount(String.format(formatReceiveAmount,receiveAmount));
        return Double.parseDouble(String.format(formatReceiveAmount,receiveAmount));
    }

    public enum POSPaymentMethod {
        CASH, BANK_TRANSFER, POS
    }

    public void clickOnViewAllPayment() {
        commonAction.click(loc_lnkViewAllPayment);
        logger.info("Click on View All Payment link");
    }

    public void selectPaymentMethod(POSPaymentMethod paymentMethod) {
        clickOnViewAllPayment();
        //wait popup show
        commonAction.getElements(loc_icnPaymentPOS);

        //TODO: Think of a better way to handle this. Without this delay, Bank-Transfer is assumed selected sometimes on the DOM
        UICommonAction.sleepInMiliSecond(500, "Wait a little for better UI stability");

        switch (paymentMethod) {
            case CASH -> {
                if (!commonAction.getAttribute(loc_icnPaymentCash, "class").contains("selected-item"))
                    commonAction.click(loc_icnPaymentCash);
            }
            case BANK_TRANSFER -> {
                if (!commonAction.getAttribute(loc_icnPaymentBankTransfer, "class").contains("selected-item"))
                    commonAction.click(loc_icnPaymentBankTransfer);
            }
            case POS -> {
                if (!commonAction.getAttribute(loc_icnPaymentPOS, "class").contains("selected-item")) {
                    commonAction.click(loc_icnPaymentPOS);
                    commonAction.inputText(loc_txtPOSReceiptCode, new DataGenerator().generateString(10));
                }
            }
        }
        new ConfirmationDialog(driver).clickBlueBtn();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        logger.info("Select payment method: {}", paymentMethod);
    }

    public void configApplyEarningPoint(boolean isApply) {
        if (!commonAction.getElements(loc_chkNotApplyEarningPoint, 1).isEmpty()) {
            if (!isApply)
                commonAction.checkTheCheckBoxOrRadio(loc_chkNotApplyEarningPoint, loc_lblNotApplyEarningPoint);
            else commonAction.uncheckTheCheckboxOrRadio(loc_chkNotApplyEarningPoint, loc_lblNotApplyEarningPoint);
            logger.info("Config apply earning point: {}", isApply);
        } else try {
            throw new Exception("Not apply earning point checkbox not show.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            Long totalAmount = Math.round(getTotalAmount());
            long amount = nextLong(totalAmount);

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
            long percent = nextInt(99);

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

    public void inputUsePoint(int point) {
        if (!commonAction.getAttribute(loc_chkUsePointValue, "class").contains("checked")) {
            commonAction.clickJS(loc_chkUsePoint);
            logger.info("Click on Use point.");
        }
        commonAction.inputText(loc_txtInputPoint, String.valueOf(point));
        logger.info("Input point = {}", point);
    }

    public enum UsePointType {
        SERVERAL, MAX_ORDER, MAX_AVAILABLE, NONE
    }

    public int redeemPointNeedForTotal() {
        LoyaltyPointInfo loyaltyPointInfo = new LoyaltyPoint(loginInformation).getLoyaltyPointSetting();
        Long exchangeAmount = loyaltyPointInfo.getExchangeAmount();
        int redeemPoint = (int) (getSubTotalAfterDiscount() / exchangeAmount);
        logger.info("Redeem point can use for this order: " + redeemPoint);
        return redeemPoint;
    }

    @SneakyThrows
    public int inputUsePoint(UsePointType usePointType) {
        int point = 0;
        int availablePoint = Integer.parseInt(commonAction.getText(loc_lblAvailablePoint).replaceAll(",", ""));
        if (availablePoint == 0) throw new Exception("Customer don't have any available point");
        int redeemPointMax = Math.min(availablePoint, redeemPointNeedForTotal());
        switch (usePointType) {
            case SERVERAL ->
                    point = redeemPointMax == 0 ? 0 : DataGenerator.generatNumberInBound(1, redeemPointMax + 1);
            case MAX_AVAILABLE, MAX_ORDER -> point = redeemPointMax;
            case NONE -> {
                return 0;
            }
        }
        inputUsePoint(point);
        new HomePage(driver).waitTillSpinnerDisappear1();
        UICommonAction.sleepInMiliSecond(2000, "Wait total amount updated.");
        return point;
    }

    public void clickPrintOrderIcon() {
        commonAction.click(loc_btnPrintOrder);
        logger.info("Click on Print Order icon.");
    }

    public void enableDisablePrint(boolean isEnable) {
        clickPrintOrderIcon();
        if (isEnable) {
            commonAction.checkTheCheckBoxOrRadio(loc_btnPrintReceiptValue, loc_btnPrintnReceiptAction);
        } else commonAction.uncheckTheCheckboxOrRadio(loc_btnPrintReceiptValue, loc_btnPrintnReceiptAction);
        new ConfirmationDialog(driver).clickGreenBtn();
    }

    public double getTotalDiscountAmount() {
        String discountText = commonAction.getText(loc_lblPromotionValue);
        logger.info("Get total discount: {}", discountText);
        return GetDataByRegex.getAmountByRegex(discountText);
    }

    public double getTaxAmount() {
        String taxValue = commonAction.getText(loc_lblTaxValue);
        logger.info("Get tax amount: {}", taxValue);
        return GetDataByRegex.getAmountByRegex(taxValue);
    }

    public double getShippingFee() {
        String shippingFee = commonAction.getText(loc_lblShippingFee);
        logger.info("Get shipping fee: {}", shippingFee);
        return GetDataByRegex.getAmountByRegex(shippingFee);
    }

    /**
     * There are times the element is not rendered with text, this function repeats 5 times until the element has text
     *
     * @param element
     * @return text of the WebElement
     */
    String getElementText(WebElement element) {
        int attempts = 0;
        int maxAttempts = 5;

        while (attempts < maxAttempts) {
            var text = element.getText();
            if (!text.isEmpty()) return text;
            UICommonAction.sleepInMiliSecond(500, "Element's text is empty. Wait a little");
            attempts++;
        }
        return "";
    }

    /**
     * @param actionlocator  : locator that user hover or click on it.
     * @param tooltipLocator : tooltip content
     * @return List<ItemTotalDiscount> with fields: Name and Value
     */
    public Map<String, Double> getPromotionDetailApply(By actionlocator, By tooltipLocator) {
        List<WebElement> infoIcon = commonAction.getElements(actionlocator, 1);
        Map<String, Double> itemDiscountList = new HashMap<>();
        if (!infoIcon.isEmpty()) {
//            commonAction.sleepInMiliSecond(1000);
            commonAction.clickActions(actionlocator);
            List<WebElement> promotionList = commonAction.getElements(tooltipLocator, 2);
            promotionList.forEach(i -> {
                var text = getElementText(i);
                logger.info("Promotion: {}", text);
                String[] promoItem = text.split("\n");
                itemDiscountList.put(promoItem[0].replaceAll("\\.00(?=%)", ""), Double.valueOf(DataGenerator.extractDigits(promoItem[promoItem.length - 1])));
            });
        }
        return itemDiscountList;
    }

    public List<SummaryDiscount> getTotalPromotionDetailApply() {
        Map<String, Double> promotionDetail = getPromotionDetailApply(loc_icnPromotionInfo, loc_lst_tltTotalPromotionApply);
        List<SummaryDiscount> itemTotalDiscountList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : promotionDetail.entrySet()) {
            SummaryDiscount itemTotalDiscount = new SummaryDiscount();
            itemTotalDiscount.setLabel(entry.getKey());
            itemTotalDiscount.setValue(entry.getValue());
            itemTotalDiscountList.add(itemTotalDiscount);
        }
        logger.info("itemTotalDiscountList: {}", itemTotalDiscountList);
        return itemTotalDiscountList;
    }

    public double getSubTotalValue() {
        String subtotalText = commonAction.getText(loc_lblSubTotalValue);
        logger.info("Get Subtotal value: {}", subtotalText);
        return GetDataByRegex.getAmountByRegex(subtotalText);
    }

    public List<ItemOrderInfo> getItemDiscountInfo() {
        List<ItemOrderInfo> itemDiscountList = new ArrayList<>();
        List<WebElement> productList = commonAction.getElements(loc_lst_lblProductName, 1);
        for (int i = 0; i < productList.size(); i++) {
            ItemOrderInfo itemOrderInfo = new ItemOrderInfo();
            String productName = productList.get(i).getText();
            itemOrderInfo.setName(productName);
            //ConversionUnit
            String conversionUnit = commonAction.getElements(loc_lblUnit).get(i).getText();
            if (!conversionUnit.equals("-"))
                itemOrderInfo.setConversionUnitName(conversionUnit);
            //Variation
            if (!commonAction.getElements(loc_lblVariationByProductIndex(i + 1)).isEmpty()) {
                itemOrderInfo.setVariationName(commonAction.getText(loc_lblVariationByProductIndex(i + 1)).replaceAll(" ", ""));
            }
            itemOrderInfo.setQuantity(Integer.parseInt(commonAction.getValue(loc_txtProductQuantity(i + 1))));

            if (!commonAction.getElements(loc_lblGift(productName)).isEmpty()) {
                GsOrderBXGYDTO gsOrderBXGYDTO = new GsOrderBXGYDTO();
                gsOrderBXGYDTO.setGiftType("BUY_X_GET_Y");
                itemOrderInfo.setGsOrderBXGYDTO(gsOrderBXGYDTO);
            }
            if (!commonAction.getElements(loc_lblSellingPriceForOne(i + 1)).isEmpty())
                itemOrderInfo.setPrice(GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblSellingPriceForOne(i + 1))));
            else
                itemOrderInfo.setPrice(GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblSellingPriceAfterDiscountForOne(i + 1))));
            itemOrderInfo.setPriceDiscount(GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblSellingPriceAfterDiscountForOne(i + 1))));
            itemOrderInfo.setTotalAmount(GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblPriceTotalAfterDiscount(i + 1))));

            //Set promotion info of each item
//            commonAction.sleepInMiliSecond(1000);
            List<ItemTotalDiscount> itemTotalDiscountList = new ArrayList<>();
            if (!commonAction.getElements(loc_ddlPromotion(i + 1), 1).isEmpty()) {
                commonAction.click(loc_ddlPromotion(i + 1));
                Map<String, Double> itemTotalDiscountMap = getPromotionDetailApply(loc_ddlPromotion(i + 1), loc_tltPromotionApplyOnItem);
                for (Map.Entry<String, Double> entry : itemTotalDiscountMap.entrySet()) {
                    ItemTotalDiscount itemTotalDiscount = new ItemTotalDiscount();
                    String label = entry.getKey().equals("Giá sỉ") ? "Bán sỉ" : entry.getKey();
                    itemTotalDiscount.setLabel(label.replaceAll(",","."));
                    itemTotalDiscount.setValue(entry.getValue());
                    itemTotalDiscountList.add(itemTotalDiscount);
                }
            }
            itemOrderInfo.setItemTotalDiscounts(itemTotalDiscountList);
            itemDiscountList.add(itemOrderInfo);
        }
        logger.info("itemDiscountList: {}", itemDiscountList);
        return itemDiscountList;
    }


    public double getShippingFeeDiscount() {
        Map<String, Double> shippingDiscountMap = getPromotionDetailApply(loc_ddlShippingPromotion, loc_tltShippingPromotion);
        double discountAmount = shippingDiscountMap.values().stream().mapToDouble(Double::doubleValue).sum();
        logger.info("Shipping fee discount amount: {}", discountAmount);
        return discountAmount;
    }

    public double getReceiveAmount() {
        double receiveAmount = GetDataByRegex.getAmountByRegex(commonAction.getValue(loc_txtReceiveAmount));
        logger.info("Get receive amount: {}", receiveAmount);
        return receiveAmount;
    }

    public EarningPoint getEarnPoint() {
        EarningPoint earningPoint = new EarningPoint();
        LoyaltyPointInfo loyaltyPointInfo = new LoyaltyPoint(loginInformation).getLoyaltyPointSetting();
        Long rateAmount = loyaltyPointInfo.getRateAmount();
        if ((double) rateAmount < getSubTotalAfterDiscount())
            if (!commonAction.getElements(loc_lblTotalEarningPoint).isEmpty()) {
                earningPoint.setValue((int) GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblTotalEarningPoint)));
            } else earningPoint.setValue(0);
        else earningPoint.setValue(0);
        logger.info("earningPoint: {}", earningPoint);
        return earningPoint;
    }

    public int getTotalQuantity() {
        return (int) GetDataByRegex.getAmountByRegex(commonAction.getText(loc_lblTotalQuantity));
    }

    public ShippingInfo getShippingInfo() {
        ShippingInfo shippingInfo = new ShippingInfo();
        if (isDeliveryOpted()) {
//            commonAction.click(loc_icnEditDelivery);
            DeliveryDialog deliveryDialog = new DeliveryDialog(driver);
            shippingInfo.setContactName(deliveryDialog.getCustomerName());
            shippingInfo.setPhone(deliveryDialog.getCustomerPhone());
            shippingInfo.setPhoneCode(deliveryDialog.getPhoneCode());
            shippingInfo.setEmail(deliveryDialog.getCustomerEmail());
            shippingInfo.setCountry(deliveryDialog.getCountry());
            if (shippingInfo.getCountry().equals("Vietnam")) {
                shippingInfo.setFullAddress(deliveryDialog.getAddress() + ", " + deliveryDialog.getWard() + ", " + deliveryDialog.getDistrict()
                                            + ", " + deliveryDialog.getProvince() + ", " + Constant.VIETNAM);
                shippingInfo.setFullAddressEn(deliveryDialog.getAddress() + ", " + deliveryDialog.getWard() + ", " + deliveryDialog.getDistrict()
                                              + ", " + deliveryDialog.getProvince() + ", " + Constant.VIETNAM);
            } else {
                shippingInfo.setFullAddress(deliveryDialog.getAddress() + ", " + deliveryDialog.getAddress2() + ", " + deliveryDialog.getCity() + ", " + deliveryDialog.getProvince()
                                            + ", " + deliveryDialog.getZipcode() + ", " + deliveryDialog.getCountry());
                shippingInfo.setFullAddressEn(deliveryDialog.getAddress() + ", " + deliveryDialog.getAddress2() + ", " + deliveryDialog.getCity() + ", " + deliveryDialog.getProvince()
                                              + ", " + deliveryDialog.getZipcode() + ", " + deliveryDialog.getCountry());
            }
//            new ConfirmationDialog(driver).clickCancelBtn();
        }
        logger.info("shippingInfo: {}", shippingInfo);
        return shippingInfo;
    }

    public POSPaymentMethod getSelectedPaymentMethod() {
        String paymentMethod = commonAction.getText(loc_lblSelectedPaymentMethod);
        return (paymentMethod.equalsIgnoreCase("cash") || paymentMethod.equalsIgnoreCase("tiền mặt")) ? POSPaymentMethod.CASH :
                (paymentMethod.equalsIgnoreCase("bank transfer") || paymentMethod.equalsIgnoreCase("chuyển khoản")) ? POSPaymentMethod.BANK_TRANSFER : POSPaymentMethod.POS;
    }

    /**
     * @param isGuest    don't select customer or select
     * @param customerId = 0 when isGuest = true
     * @return
     */
    public BillingInfo getBillingInfo(boolean isGuest, int customerId) {
        BillingInfo billingInfo = new BillingInfo();

        if (!isGuest && !isDeliveryOpted()) { //isCustomer (Account+Contact) + no delivery : billing get from customer info
            CustomerInfoFull customerInfo = new APICustomerDetail(loginInformation).getFullInfo(customerId);
            billingInfo.setContactName(customerInfo.getFullName());
            Optional<String> mainPhoneNumber = customerInfo.getPhones().stream()
                    .filter(phone -> "main".equalsIgnoreCase(phone.getPhoneType()))
                    .map(CustomerPhone::getPhoneNumber)
                    .findFirst();
            if (mainPhoneNumber.isPresent()) billingInfo.setPhone(mainPhoneNumber.get());
            billingInfo.setCountry(customerInfo.getCustomerAddressFull().getCountry());
            billingInfo.setAddress1(customerInfo.getCustomerAddress().getAddress());
            if (billingInfo.getCountry().equals(Constant.VIETNAM)) {
                billingInfo.setFullAddress(customerInfo.getCustomerAddressFull().getWard() + ", " + customerInfo.getCustomerAddressFull().getDistrict()
                                           + ", " + customerInfo.getCustomerAddressFull().getCity() + ", " + customerInfo.getCustomerAddressFull().getCountry());
                billingInfo.setFullAddressEn(customerInfo.getCustomerAddressFull().getWard() + ", " + customerInfo.getCustomerAddressFull().getDistrict()
                                             + ", " + customerInfo.getCustomerAddressFull().getCity() + ", " + customerInfo.getCustomerAddressFull().getCountry());
            } else {
                billingInfo.setFullAddress(customerInfo.getCustomerAddressFull().getCity() + ", " + customerInfo.getCustomerAddressFull().getCountry());
                billingInfo.setFullAddressEn(customerInfo.getCustomerAddressFull().getCity() + ", " + customerInfo.getCustomerAddressFull().getCountry());
            }
        } else if (isDeliveryOpted()) { //Account or Guest + has delivery : billing get from delivery info
//            commonAction.clickJS(loc_icnEditDelivery);
            DeliveryDialog deliveryDialog = new DeliveryDialog(driver);
            billingInfo.setContactName(deliveryDialog.getCustomerName());
            billingInfo.setPhone(deliveryDialog.getCustomerPhone());
            billingInfo.setEmail(deliveryDialog.getCustomerEmail());
            billingInfo.setCountry(deliveryDialog.getCountry());
            if (billingInfo.getCountry().equals("Vietnam")) {
                billingInfo.setFullAddress(deliveryDialog.getAddress() + ", " + deliveryDialog.getWard() + ", " + deliveryDialog.getDistrict()
                                           + ", " + deliveryDialog.getProvince() + ", " + Constant.VIETNAM);
                billingInfo.setFullAddressEn(deliveryDialog.getAddress() + ", " + deliveryDialog.getWard() + ", " + deliveryDialog.getDistrict()
                                             + ", " + deliveryDialog.getProvince() + ", " + Constant.VIETNAM);
            } else {
                billingInfo.setFullAddress(deliveryDialog.getAddress() + ", " + deliveryDialog.getCity() + ", " + deliveryDialog.getProvince()
                                           + ", " + deliveryDialog.getZipcode() + ", " + deliveryDialog.getCountry());
                billingInfo.setFullAddressEn(deliveryDialog.getAddress() + ", " + deliveryDialog.getCity() + ", " + deliveryDialog.getProvince()
                                             + ", " + deliveryDialog.getZipcode() + ", " + deliveryDialog.getCountry());
            }

//            new ConfirmationDialog(driver).clickCancelBtn();
        }
        logger.info("billingInfo: {}", billingInfo);
        return billingInfo;
    }

    public CustomerOrderInfo getCustomerOderInfo(int customerId) {
        CustomerOrderInfo customerOrderInfo = new CustomerOrderInfo();
        double currentDebt = 0;
        if (!commonAction.getElements(loc_lblCustomerNameAndPhone, 1).isEmpty()) {
            CustomerInfoFull customerDetail = new APICustomerDetail(loginInformation).getFullInfo(customerId);
            customerOrderInfo.setName(customerDetail.getFullName());
            customerOrderInfo.setGuest(customerDetail.getGuest());
            Optional<String> mainPhoneNumber = customerDetail.getPhones().stream()
                    .filter(phone -> "main".equalsIgnoreCase(phone.getPhoneType()))
                    .map(CustomerPhone::getPhoneNumber)
                    .findFirst();
            if (mainPhoneNumber.isPresent()) {
                customerOrderInfo.setMainPhone(mainPhoneNumber.get());
                customerOrderInfo.setPhone(customerDetail.getPhoneNumberWithPhoneCode());
                customerOrderInfo.setPhoneWithoutPhoneCode(customerDetail.getPhone());
                customerOrderInfo.setPhoneWithZero(customerDetail.getPhoneNumberWithZero());
            }
            //debt format: -1111 or 1111
            currentDebt = Double.parseDouble(DataGenerator.extractDigits(commonAction.getText(loc_lblDebt)));
        }
        customerOrderInfo.setDebtAmount(Double.parseDouble(String.format("%.2f", currentDebt + getDebtAmount())));
        logger.info("customerOrderInfo: {}", customerOrderInfo);
        return customerOrderInfo;
    }

    /**
     * @param customerId = 0 when no select customer
     * @return
     */
    public OrderDetailInfo getOrderInfoBeforeCheckOut(int customerId) {
        new HomePage(driver).waitTillLoadingDotsDisappear(); //Wait until Apply Discount API finishes execution

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTotalTaxAmount(getTaxAmount());
        double totalAmount = getTotalAmount();
        double receiveAmount = getReceiveAmount();
        orderInfo.setTotalPrice(totalAmount);
        orderInfo.setSubTotal(getSubTotalValue());
        if (getShippingFee() > 0) {
            orderInfo.setOriginalShippingFee(getShippingFee());
            orderInfo.setShippingFee(getShippingFee() > getShippingFeeDiscount() ? getShippingFee() - Math.abs(getShippingFeeDiscount()) : 0);
        }

        orderInfo.setTotalQuantity(getTotalQuantity());
        orderInfo.setPaymentMethod(getSelectedPaymentMethod().toString());
        orderInfo.setPaid(receiveAmount == totalAmount);
        orderInfo.setUsePoint(getUsePoint());
        orderInfo.setPayType(getPayType(receiveAmount, totalAmount).toString());
        orderInfo.setStatus(getOrderStatusAfterCreated().toString());
        orderInfo.setDebtAmount(getDebtAmount());
        orderInfo.setReceivedAmount(receiveAmount);
        orderInfo.setCreateDate(DataGenerator.getDateByTimeZone(new StoreInformation(loginInformation).getInfo().getTimeZone(),
                String.valueOf(ZonedDateTime.now(ZoneOffset.UTC))));
        orderInfo.setCreatedBy(getCreatedBy());

        OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
        orderDetailInfo.setOrderInfo(orderInfo);
        orderDetailInfo.setItems(getItemDiscountInfo());
        orderDetailInfo.setSummaryDiscounts(getTotalPromotionDetailApply());
        orderDetailInfo.setTotalSummaryDiscounts(-getTotalDiscountAmount());
        CustomerOrderInfo customerOrderInfo = getCustomerOderInfo(customerId);
        if (isDeliveryOpted()) clickEditDelivery();
        orderDetailInfo.setShippingInfo(getShippingInfo());
        /** get customer when guest checkout  */
        if(customerId==0) customerOrderInfo.setGuest(true);
        if(isDeliveryOpted()) {
            if(customerOrderInfo.getMainPhone()==null)customerOrderInfo.setMainPhone(orderDetailInfo.getShippingInfo().getPhone());
            if(customerOrderInfo.getPhone()==null)customerOrderInfo.setPhone(orderDetailInfo.getShippingInfo().getPhoneCode() + orderDetailInfo.getShippingInfo().getPhone().replaceFirst("^0", ""));
            if(customerOrderInfo.getName()==null)customerOrderInfo.setName(orderDetailInfo.getShippingInfo().getContactName());
            String deliveryMethod = new DeliveryDialog(driver).getSelectedDeliveryName();
            orderInfo.setDeliveryName(deliveryMethod.equalsIgnoreCase("tự vận chuyển") || deliveryMethod.equalsIgnoreCase("self delivery") ? "selfdelivery" : GetDataByRegex.normalizeString(deliveryMethod));
            new ConfirmationDialog(driver).clickCancelBtn();
        }
        customerOrderInfo.setCustomerId(customerId);
        orderDetailInfo.setCustomerInfo(customerOrderInfo);
        orderDetailInfo.setTotalSummaryDiscounts(getTotalDiscountAmount());
        orderDetailInfo.setEarningPoint(getEarnPoint());
        StoreBranch storeBranch = new StoreBranch();
        storeBranch.setName(branchName);
        orderDetailInfo.setStoreBranch(storeBranch);
        logger.info("orderDetailInfo: {}", orderDetailInfo);
        return orderDetailInfo;
    }

    public POSPage clickCompleteCheckout() {
        commonAction.click(loc_btnComplete);
        logger.info("Click on Complete button");
        return this;
    }

    public int getUsePoint() {
        int usePoint = commonAction.getElements(loc_txtInputPoint).isEmpty()
                ? 0 : Integer.parseInt(commonAction.getValue(loc_txtInputPoint).replaceAll(",", ""));
        logger.info("Use point: {}", usePoint);
        return usePoint;
    }

    public PaymentStatus getPayType(double receiveAmount, double totalAmount) {
        PaymentStatus paymentStatus = receiveAmount == totalAmount ? PaymentStatus.PAID
                : receiveAmount == 0 ? PaymentStatus.UNPAID : PaymentStatus.PARTIAL;
        logger.info("Payment status: {}", paymentStatus);
        return paymentStatus;
    }

    public APIAllOrders.OrderStatus getOrderStatusAfterCreated() {
        if (isDeliveryOpted()) return APIAllOrders.OrderStatus.TO_SHIP;
        return APIAllOrders.OrderStatus.DELIVERED;
    }

    public double getDebtAmount() {
        if (isDeliveryOpted()) return getReceiveAmount()==0? 0: -getReceiveAmount();
        return getTotalAmount() - getReceiveAmount();
    }

    public boolean isDeliveryOpted() {
        boolean isDisplayed = !commonAction.getElements(loc_icnEditDelivery).isEmpty();
        logger.info("Is Edit Delivery icon present: {}", isDisplayed);
        return isDisplayed;
    }

    public String getCreatedBy() {
        LoginDashboardInfo loginInfo = new Login().getInfo(loginInformation);
        if (loginInfo.getUserRole().contains("ROLE_STORE"))
            return "[shop0wner]";
        return loginInfo.getUserName();
    }

    public DeliveryDialog clickEditDelivery() {
        commonAction.clickJS(loc_icnEditDelivery);
        logger.info("Click on Edit delivery icon.");
        return new DeliveryDialog(driver);
    }

    public String getCurrencySymbol() {
        return new StoreInformation(loginInformation).getInfo().getSymbol();
    }

    public double getSubTotalAfterDiscount() {
        double totalAmountAfterDiscount = getTotalAmount() - getTaxAmount() - getShippingFee();
        logger.info("Get subtotal after discount (total amount exclude: tax and shipping fee): {}", totalAmountAfterDiscount);
        return totalAmountAfterDiscount;
    }

    @SneakyThrows
    public void verifyCreateOrderSuccessMessage() {
        Assert.assertEquals(new HomePage(driver).getToastMessage(), PropertiesUtil.getPropertiesValueByDBLang("order.pos.create.successMessage"),
                "[Failed] Check success message.");
    }
}
