package web.Dashboard;

import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.STAFF_SHOP_VI_USERNAME;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import api.Seller.analytics.APIOrdersAnalytics;
import api.Seller.cashbook.CashbookAPI;
import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICustomerDetail;
import api.Seller.customers.APIUpdatePoint;
import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.orders.order_management.APIAllOrders.OrderStatus;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.setting.BranchManagement;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.driver.InitWebdriver;
import utilities.enums.DebtActionEnum;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.analytics.TimeFrame;
import utilities.enums.cashbook.CashbookRevenue;
import utilities.enums.pos.ReceivedAmountType;
import utilities.model.dashboard.analytics.AnalyticsOrderSummaryInfo;
import utilities.model.dashboard.cashbook.CashbookRecord;
import utilities.model.dashboard.customer.CustomerDebtRecord;
import utilities.model.dashboard.customer.CustomerInfoFull;
import utilities.model.dashboard.customer.CustomerOrder;
import utilities.model.dashboard.customer.CustomerOrderSummary;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.orders.ordermanagement.OrderListSummaryVM;
import utilities.model.dashboard.orders.pos.CreatePOSOrderCondition;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.pos.create_order.POSPage;

import static utilities.account.AccountTest.*;

public class POSOrderTest extends BaseTest {
    String country;
    String phoneCode;
    String username = ADMIN_SHOP_VI_USERNAME;
    String pass = ADMIN_SHOP_VI_PASSWORD;
    LoginInformation credentials;
    Logger logger = LogManager.getLogger(POSOrderTest.class);


    int getRandomCustomerId(APIAllCustomers allCustomerAPI) {
        //Get a list of profile ids whose saleChannel is GOSELL
        var profileIdPool = allCustomerAPI.getProfileRecords().stream().filter(pro -> pro.getSaleChannel().contentEquals("GOSELL")).map(id -> id.getId()).toList();

        //Get a random profileId from the pool
        return DataGenerator.getRandomListElement(profileIdPool);
    }

    /**
     * Retrieves detail of a customer
     *
     * @param customerDetailAPI
     * @param profileId
     * @return POJO object representing the customer's detail,
     * or null if the input profileId is less than 1
     */
    CustomerInfoFull getCustomerDetail(APICustomerDetail customerDetailAPI, int profileId) {
        if (profileId < 1) {
            return null;
        }
        return customerDetailAPI.getFullInfo(profileId);
    }

    int calculateEarningPoints(APICustomerDetail customerDetailAPI, CustomerInfoFull customerDetail) {
        if (customerDetail == null || customerDetail.getGuest() || customerDetail.getUserId() == null) {
            return 0;
        }
        return customerDetailAPI.getEarningPoint(customerDetail.getUserId());
    }

    int setEarningPoints(boolean isWalkInGuest, CustomerInfoFull customerDetail, int existingEarningPoints) {
        if (isWalkInGuest) {
            return existingEarningPoints;
        }
        if (customerDetail == null) {
            return existingEarningPoints;
        }
        if (customerDetail.getGuest()) {
            return existingEarningPoints;
        }
        if (existingEarningPoints > 0) {
            return existingEarningPoints;
        }
        if (customerDetail.getUserId() == null) {
            return existingEarningPoints;
        }

        int point = 1000;
        new APIUpdatePoint(credentials).addMorePoint(Integer.parseInt(customerDetail.getUserId()), point);
        return point;
    }

    CustomerOrderSummary getCustomerOrderSummary(APICustomerDetail customerDetailAPI, int profileId) {
        if (profileId < 1) {
            return null;
        }
        return customerDetailAPI.getOrderSummary(profileId);
    }

    String getLatestOrderId(APICustomerDetail customerDetailAPI, CustomerInfoFull customerDetail) {
        if (customerDetail.getUserId() == null) {
            return "";
        }

        List<CustomerOrder> previousOrderList = customerDetailAPI.getOrders(customerDetail.getId(), customerDetail.getUserId());
        return previousOrderList.stream()
                .findFirst()
                .map(CustomerOrder::getId)
                .orElse("");
    }

    int getLatestDebtRecordId(APICustomerDetail customerDetailAPI, CustomerInfoFull customerDetail) throws JsonMappingException, JsonProcessingException {
        if (customerDetail == null) {
            return -1;
        }

        List<CustomerDebtRecord> previousDebtList = customerDetailAPI.getDebtRecords(customerDetail.getId());
        return previousDebtList.stream()
                .findFirst()
                .map(CustomerDebtRecord::getId)
                .orElse(-1);
    }

    String getLatestCashbookRecordId(CashbookAPI cashbookAPI) {
        return cashbookAPI.getAllRecords().stream()
                .findFirst()
                .map(CashbookRecord::getTransactionCode)
                .orElse("RN");
    }

    int calculateExpectedEarningPoints(int previousPoints, OrderDetailInfo orderDetails) {
        int earnedPoints = orderDetails.getEarningPoint().getValue();
        int usedPoints = orderDetails.getOrderInfo().getUsePoint();
        OrderStatus status = OrderStatus.valueOf(orderDetails.getOrderInfo().getStatus());

        int netPoints = previousPoints - usedPoints;
        if (status == OrderStatus.DELIVERED) {
            netPoints += earnedPoints;
        }
        return netPoints;
    }

    public int workoutExpectedTotalOrderCount(Integer previousTotalOrderCount, OrderDetailInfo orderDetailsBeforeCheckout) {

        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalOrderCount + 1;
        }
        return previousTotalOrderCount;
    }

    public BigDecimal workoutExpectedTotalPurchase(BigDecimal previousTotalPurchase, OrderDetailInfo orderDetailsBeforeCheckout) {

        double uiTotalOrderAmount = orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice();

        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalPurchase.add(BigDecimal.valueOf(uiTotalOrderAmount));
        }
        return previousTotalPurchase;
    }

    public BigDecimal workoutExpectedTotalPurchaseLast3Months(BigDecimal previousTotalPurchaseLast3Months, OrderDetailInfo orderDetailsBeforeCheckout) {
        double uiTotalOrderAmount = orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice();
        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalPurchaseLast3Months.add(BigDecimal.valueOf(uiTotalOrderAmount));
        }
        return previousTotalPurchaseLast3Months;
    }

    public BigDecimal workoutExpectedAverageOrderValue(BigDecimal totalOrderCount, BigDecimal totalPurchase) {
        if (totalOrderCount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalPurchase.divide(totalOrderCount, 10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }

    public String workoutExpectedCashbookRecordSourceType(boolean isDeliveryOpted, BigDecimal debt) {
        if (isDeliveryOpted)
            return CashbookRevenue.DEBT_COLLECTION_FROM_SUPPLIER.name();

        if (debt.compareTo(BigDecimal.ZERO) == 0)
            return CashbookRevenue.PAYMENT_FOR_ORDER.name();

        return CashbookRevenue.DEBT_COLLECTION_FROM_CUSTOMER.name();
    }

    @BeforeClass
    public void beforeClass() {
        if (Domain.valueOf(domain).equals(Domain.VN)) {
            country = AccountTest.ADMIN_COUNTRY_TIEN;
            username = ADMIN_SHOP_VI_USERNAME;
            pass = ADMIN_SHOP_VI_PASSWORD;
        } else {
            country = ADMIN_PHONE_BIZ_COUNTRY;
            username = ADMIN_PHONE_BIZ_USERNAME;
            pass = ADMIN_PHONE_BIZ_PASSWORD;
        }
        phoneCode = DataGenerator.getPhoneCode(country);
        credentials = new Login().setLoginInformation(phoneCode, username, pass).getLoginInformation();
    }

    @DataProvider
    public Object[][] dataTest() {
        return new Object[][]{
                // Seller create order
                {new CreatePOSOrderCondition(true, false, false, POSPage.UsePointType.NONE, ReceivedAmountType.NONE, true, false, POSPage.POSPaymentMethod.CASH), TimeFrame.TODAY},   //guest checkout, no delivery
//				{new CreatePOSOrderCondition(true,true,false,POSPage.UsePointType.NONE,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},   //guest checkout, has delivery
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, // checkout with customer, no delivery.
//				{new CreatePOSOrderCondition(false,true,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},  //checkout customer, has delivery
//				{new CreatePOSOrderCondition(false,true,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,false,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, // no apply earn point
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.NONE,ReceivedAmountType.NONE,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},    // apply promotion, no delivery
//				{new CreatePOSOrderCondition(false,true,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,true,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},    // apply promotion, has delivery
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.NONE,ReceivedAmountType.PARTIAL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},   //receive amount = partial
//				{new CreatePOSOrderCondition(false,true,false,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, //receive amount = full
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.BANK_TRANSFER),TimeFrame.TODAY}, //Payment method = bank transfer
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.MAX_ORDER,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.BANK_TRANSFER),TimeFrame.TODAY}, //POSPage.UsePointType.MAX_ORDER
//				{new CreatePOSOrderCondition(true,false,false,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,true,true, POSPage.POSPaymentMethod.CASH),TimeFrame.YESTERDAY},   //guest checkout, no delivery, apply direct discount
//				{new CreatePOSOrderCondition(false,false ,false,POSPage.UsePointType.MAX_AVAILABLE,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, //POSPage.UsePointType.MAX_AVAILABLE
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.THIS_WEEK}, //TimeFrame.THIS_WEEK
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.THIS_MONTH},   //TimeFrame.THIS_MONTH
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.LAST_7_DAYS},   //TimeFrame.LAST_7_DAYS
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.LAST_30_DAYS},   //TimeFrame.LAST_30_DAYS
//				{new CreatePOSOrderCondition(false,false,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.LAST_WEEK},   //TimeFrame.LAST_WEEK
//				{new CreatePOSOrderCondition(false,true,false,POSPage.UsePointType.SERVERAL,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.LAST_MONTH},   //TimeFrame.LAST_MONTH

                /**Staff create order*/
//				{new CreatePOSOrderCondition(true,false,true,POSPage.UsePointType.NONE,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},   //guest checkout, no delivery
//				{new CreatePOSOrderCondition(false,false,true,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, // checkout with customer, no delivery.
//				{new CreatePOSOrderCondition(false,true,true,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,true,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},  //checkout customer, has delivery
//				{new CreatePOSOrderCondition(false,true,true,POSPage.UsePointType.SERVERAL,ReceivedAmountType.NONE,false,false, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, // no apply earn point
//				{new CreatePOSOrderCondition(false,false,true,POSPage.UsePointType.NONE,ReceivedAmountType.NONE,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},    // apply promotion, no delivery
//				{new CreatePOSOrderCondition(false,true,true,POSPage.UsePointType.NONE,ReceivedAmountType.NONE,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},    // apply promotion, has delivery
//				{new CreatePOSOrderCondition(false,true,true,POSPage.UsePointType.NONE,ReceivedAmountType.PARTIAL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},   //receive amount = partial
//				{new CreatePOSOrderCondition(false,false,true,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY}, //receive amount = full
//				{new CreatePOSOrderCondition(false,false,true,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.BANK_TRANSFER),TimeFrame.TODAY}, //Payment method = bank transfer
//				{new CreatePOSOrderCondition(false,false,true,POSPage.UsePointType.MAX_ORDER,ReceivedAmountType.FULL,false,true, POSPage.POSPaymentMethod.BANK_TRANSFER), TimeFrame.TODAY}, //POSPage.UsePointType.MAX_ORDER
//				{new CreatePOSOrderCondition(true,false,true,POSPage.UsePointType.NONE,ReceivedAmountType.FULL,true,true, POSPage.POSPaymentMethod.CASH),TimeFrame.TODAY},   //guest checkout, no delivery, apply direct discount

        };
    }

    @Test(dataProvider = "dataTest")
    public void TC_CheckCustomerInfoPostOrder(CreatePOSOrderCondition condition, TimeFrame timeFrame) throws JsonMappingException, JsonProcessingException {
        logger.info("Combination being executed: " + condition);
        logger.info("Run with timeframe: " + timeFrame);
        /** Test case input **/
        //login
        if (condition.isStaffCreateOrder()) {
            credentials = new Login().setLoginInformation("+84", STAFF_SHOP_VI_USERNAME, STAFF_SHOP_VI_PASSWORD).getLoginInformation();
        }
        long newestOrderbefore = new APIAllOrders(credentials).getNewestOrderId();
        logger.info("Newest order before: " + newestOrderbefore);
        AnalyticsOrderSummaryInfo ordersAnalyticsSummaryBefore = new APIOrdersAnalytics(credentials).getOrderAnalyticsSummary(timeFrame);
        OrderListSummaryVM orderListSummaryBefore = new APIAllOrders(credentials).getOrderListSummary(APIAllOrders.Channel.GOSELL);

        BranchInfo branchInfo = new BranchManagement(credentials).getInfo();
        String branchName = DataGenerator.getRandomListElement(branchInfo.getBranchName());

        APIAllCustomers allCustomerAPI = new APIAllCustomers(credentials);
        APICustomerDetail customerDetailAPI = new APICustomerDetail(credentials);

        CashbookAPI cashbookAPI = new CashbookAPI(credentials);

        int selectedCustomerId = 0;

        if (!condition.isWalkInGuest()) {
            selectedCustomerId = getRandomCustomerId(allCustomerAPI);
        }

        CustomerInfoFull selectedProfile = getCustomerDetail(customerDetailAPI, selectedCustomerId);
        String customerName = (selectedProfile != null) ? selectedProfile.getFullName() : null;
        String userId = (selectedProfile != null) ? selectedProfile.getUserId() : null;
        boolean isGuestFromProfile = (selectedProfile != null) ? selectedProfile.getGuest() : true;


        /** Retrieve pre-order data **/
        //Earning point
        int previousEarningPoints = calculateEarningPoints(customerDetailAPI, selectedProfile);

        //Set earning points for later use
        previousEarningPoints = setEarningPoints(condition.isWalkInGuest(), selectedProfile, previousEarningPoints);

        CustomerOrderSummary previousOrderSummary = getCustomerOrderSummary(customerDetailAPI, selectedCustomerId);
        int previousTotalOrderCount = (previousOrderSummary != null) ? previousOrderSummary.getTotalOrder() : 0;
        BigDecimal previousTotalPurchase = (previousOrderSummary != null) ? previousOrderSummary.getTotalPurchase() : BigDecimal.ZERO;
        BigDecimal previousTotalPurchaseLast3Months = (previousOrderSummary != null) ? previousOrderSummary.getTotalPurchaseLast3Month() : BigDecimal.ZERO;
        BigDecimal previousDebtAmount = (previousOrderSummary != null) ? previousOrderSummary.getDebtAmount() : BigDecimal.ZERO;

        //Debt tab
        int firstDebtRecordId = getLatestDebtRecordId(customerDetailAPI, selectedProfile);

        //Cashbook summary
        List<BigDecimal> previousSummary = cashbookAPI.getCasbookSummary();
        BigDecimal previousTotalRevenue = previousSummary.get(1);
        BigDecimal previousEndingBalance = previousSummary.get(3);

        //Cashbook record
        String firstTransactionCodeId = getLatestCashbookRecordId(cashbookAPI);


        /** Place an order in POS **/
        driver = new InitWebdriver().getDriver(browser, headless);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin(country, credentials.getPhoneNumber(), credentials.getPassword());


        POSPage posPage = new POSPage(driver, Domain.valueOf(domain)).getLoginInfo(credentials).navigateToPOSPage();

        // Select branch
        posPage.selectBranch(branchName);

        // Add product to cart
        posPage.selectProduct(credentials, List.of(1284713));// 1058837
        //Select customer
        if (!condition.isWalkInGuest()) posPage.selectCustomer(customerName);
        posPage.selectPaymentMethod(condition.getPaymentMethod());
        posPage.selectDelivery(condition.isHasDelivery(), CreateCustomerTDG.buildVNCustomerUIData(DisplayLanguage.valueOf(language)));
        //apply discount
        if (condition.isApplyPromotion()) {
            posPage.applyDiscount();
            new HomePage(driver).waitTillLoadingDotsDisappear();
            if (condition.isHasDelivery()) {
                posPage.clickEditDelivery().clickShippingProviderDropdown();
                new ConfirmationDialog(driver).clickGreenBtn();
                new HomePage(driver).waitTillLoadingDotsDisappear();
            }
        }
        //input use point
        if (!isGuestFromProfile) {
            posPage.inputUsePoint(condition.getUsePointType());
            //config not apply earnPoint
            posPage.configApplyEarningPoint(condition.isHasEarnPoint());
        }
        //get receive amount
        Double receivedAmount = posPage.inputReceiveAmount(condition.getReceivedAmountType());


        /** Organize expected results **/
        //Order details
        OrderDetailInfo orderDetailsBeforeCheckout = posPage.getOrderInfoBeforeCheckOut(selectedCustomerId);
        Boolean expectedPaidStatus = orderDetailsBeforeCheckout.getOrderInfo().getPaid();
        String expectedDeliveryStatus = orderDetailsBeforeCheckout.getOrderInfo().getStatus();
        Integer expectedProductCount = orderDetailsBeforeCheckout.getItems().size();

        //Earning points
        int expectedEarningPoints = calculateExpectedEarningPoints(previousEarningPoints, orderDetailsBeforeCheckout);

        //Order summary
        Integer expectedTotalOrderCount = workoutExpectedTotalOrderCount(previousTotalOrderCount, orderDetailsBeforeCheckout);
        BigDecimal expectedTotalPurchase = workoutExpectedTotalPurchase(previousTotalPurchase, orderDetailsBeforeCheckout);
        BigDecimal expectedTotalPurchaseLast3Months = workoutExpectedTotalPurchaseLast3Months(previousTotalPurchaseLast3Months, orderDetailsBeforeCheckout);
        BigDecimal expectedAverageOrderValue = workoutExpectedAverageOrderValue(new BigDecimal(expectedTotalOrderCount), expectedTotalPurchase);
        BigDecimal expectedDebtAmount = previousDebtAmount.add(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount())).setScale(2, RoundingMode.HALF_UP);

        //Order tab
        BigDecimal expectedOrderTotalAmount = BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice());

        //Debt tab
        DebtActionEnum expectedDebtAction = condition.isHasDelivery() ? DebtActionEnum.POS_DELIVERY_ORDER : DebtActionEnum.POS_NOW_ORDER;
        BigDecimal expectedDebtRecordAmount = BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount());

        posPage.clickCompleteCheckout();
        if (!condition.getReceivedAmountType().equals(ReceivedAmountType.FULL))
            new ConfirmationDialog(driver).clickOKBtn();
        posPage.verifyCreateOrderSuccessMessage();

        //Order date is the moment the order is placed successfully
        String expectedOrderDate = LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //Get order detail
        long orderId = new APIAllOrders(credentials).waitAndGetUntilNewOrderCreated(newestOrderbefore, APIAllOrders.Channel.GOSELL);
        OrderDetailInfo orderDetailInfo = new APIOrderDetail(credentials).getOrderDetail(orderId);
        if (condition.isWalkInGuest()) {
            selectedCustomerId = orderDetailInfo.getCustomerInfo().getCustomerId();
            customerName = orderDetailInfo.getCustomerInfo().getName();
        }
        //After an order is place, an userId is given to the customer if it's previously undefined
        userId = (userId == null) ? String.valueOf(orderDetailInfo.getCustomerInfo().getUserId()) : userId;

        //Cashbook summary
        BigDecimal expectedTotalRevenue = previousTotalRevenue.add(BigDecimal.valueOf(receivedAmount));
        BigDecimal expectedEndingBalance = previousEndingBalance.add(BigDecimal.valueOf(receivedAmount));

        //Cashbook record
        String expectedCashbookRecordCreatedDate = expectedOrderDate;
        String expectedCashbookRecordGroupType = "CUSTOMER";
        String expectedCashbookRecordCustomerName = customerName;
        String expectedCashbookSourceType = workoutExpectedCashbookRecordSourceType(condition.isHasDelivery(), BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
        String expectedCashbookRecordBranch = branchName;
        BigDecimal expectedCashbookRecordAmount = BigDecimal.valueOf(receivedAmount);
        String expectedCashbookRecordPaymentMethod = condition.getPaymentMethod().name();


        /** Retrieve post-order data **/
        //Earning points
        int postEarningPoints = customerDetailAPI.getEarningPoint(userId);

        //Order summary
        CustomerOrderSummary postOrderSummary = customerDetailAPI.getOrderSummary(selectedCustomerId);
        Integer postTotalOrderCount = postOrderSummary.getTotalOrder();
        BigDecimal postTotalPurchase = postOrderSummary.getTotalPurchase();
        BigDecimal postTotalPurchaseLast3Months = postOrderSummary.getTotalPurchaseLast3Month();
        BigDecimal postAverageOrderValue = postOrderSummary.getAverangePurchase();
        BigDecimal postDebtAmount = postOrderSummary.getDebtAmount();

        //Order tab
        List<CustomerOrder> postOrderList = customerDetailAPI.getOrders(selectedCustomerId, userId);
        String postFirstOrderId = postOrderList.get(0).getId(); // 13339499
        String postOrderChannel = postOrderList.get(0).getChannel(); // GOSELL
        String postOrderDate = postOrderList.get(0).getCreatedDate().replaceAll("T.*", ""); // 2024-08-26T08:42:29.427377Z
        Boolean postOrderPaymentStatus = postOrderList.get(0).getIsPaid(); // true
        String postOrderStatus = postOrderList.get(0).getStatus(); // DELIVERED
        Integer postOrderItemCount = postOrderList.get(0).getItemsCount(); // 1
        BigDecimal postOrderTotalAmount = postOrderList.get(0).getTotal(); // 221000

        //Debt tab
        List<CustomerDebtRecord> postDebtList = customerDetailAPI.getDebtRecords(selectedCustomerId);
        Integer postFirstDebtRecordId = postDebtList.stream().findFirst().map(CustomerDebtRecord::getId).orElse(-1);
        DebtActionEnum postDebtAction = postDebtList.stream().findFirst().map(CustomerDebtRecord::getAction).orElse(DebtActionEnum.POS_NOW_ORDER);
        BigDecimal postDebtRecordAmount = postDebtList.stream().findFirst().map(CustomerDebtRecord::getAmount).orElse(BigDecimal.ZERO);
        String postDebtReferenceId = postDebtList.stream().findFirst().map(CustomerDebtRecord::getRefId).orElse("");
        String postDebtDate = postDebtList.stream().findFirst().map(CustomerDebtRecord::getCreatedDate).orElse("").replaceAll("T.*", "");
        BigDecimal postAccumulatedDebt = postDebtList.stream().findFirst().map(CustomerDebtRecord::getDebt).orElse(BigDecimal.ZERO);

        //Cashbook summary
        List<BigDecimal> postSummary = cashbookAPI.getCasbookSummary();
        BigDecimal postTotalRevenue = postSummary.get(1);
        BigDecimal postEndingBalance = postSummary.get(3);

        //Cashbook record
        List<CashbookRecord> postCashbookRecords = cashbookAPI.getAllRecords();
        String firstPostTransactionCodeId = postCashbookRecords.stream().findFirst().map(CashbookRecord::getTransactionCode).orElse("RN");
        String postCashbookRecordCreatedDate = postCashbookRecords.get(0).getCreatedDate().replaceAll("T.*", "");
        String postCashbookRecordGroupType = postCashbookRecords.get(0).getGroupType();
        String postCashbookRecordCustomerName = postCashbookRecords.get(0).getCustomerName();
        String postCashbookSourceType = workoutExpectedCashbookRecordSourceType(condition.isHasDelivery(), BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
        String postCashbookRecordBranch = postCashbookRecords.get(0).getBranchName();
        BigDecimal postCashbookRecordAmount = postCashbookRecords.get(0).getAmount();
        String postCashbookRecordPaymentMethod = postCashbookRecords.get(0).getPaymentMethod();
        //Product cost
        Double productCost = new APIAllOrders(credentials).getProuctCostOfOrder(orderId);

        /** Assertions **/
        //Verify Order detail
        new APIOrderDetail(credentials, language).verifyOrderDetailAPI(orderDetailsBeforeCheckout, orderId)
                .verifyPaymentHistoryAfterCreateOrder(orderId, orderDetailsBeforeCheckout.getOrderInfo().getReceivedAmount(), condition.getReceivedAmountType());
        //Verify order in management
        new APIAllOrders(credentials, language).verifyOrderInManagement(orderDetailsBeforeCheckout, orderId);
        new APIAllOrders(credentials, language).verifyOrderListSummary(orderListSummaryBefore, orderDetailsBeforeCheckout);
        //Verify order analytic
        new APIOrdersAnalytics(credentials).waitOrderAnalyticsUpdateData(ordersAnalyticsSummaryBefore.getTotalOrders(), timeFrame);
        new APIOrdersAnalytics(credentials).verifyOrderAnalyticAfterCreateOrder(ordersAnalyticsSummaryBefore, orderDetailInfo, timeFrame, productCost);

        //Earning points
        Assert.assertEquals(postEarningPoints, expectedEarningPoints, "Earning points");

        //Order summary
        Assert.assertEquals(postTotalOrderCount, expectedTotalOrderCount, "Total order count");
        Assert.assertTrue(postTotalPurchase.compareTo(expectedTotalPurchase) == 0, "Total purchase expected: " + expectedTotalPurchase + ", but got: " + postTotalPurchase);
        Assert.assertTrue(postTotalPurchaseLast3Months.compareTo(expectedTotalPurchaseLast3Months) == 0, "Total purchase last 3 months expected: " + expectedTotalPurchaseLast3Months + ", but got: " + postTotalPurchaseLast3Months);
        Assert.assertTrue(postAverageOrderValue.compareTo(expectedAverageOrderValue) == 0, "Average order value: " + expectedAverageOrderValue + ", but got: " + postAverageOrderValue);
        Assert.assertTrue(postDebtAmount.compareTo(expectedDebtAmount) == 0, "Debt amount expected: " + expectedDebtAmount + ", but got: " + postDebtAmount);

        //Order tab
        Assert.assertEquals(postFirstOrderId, String.valueOf(orderId), "Latest order record id");
        Assert.assertEquals(postOrderChannel, "GOSELL", "Sale channel");
        Assert.assertEquals(postOrderDate, expectedOrderDate, "Order date");
        Assert.assertEquals(postOrderPaymentStatus, expectedPaidStatus, "Payment status");
        Assert.assertEquals(postOrderStatus, expectedDeliveryStatus, "Delivery status");
        Assert.assertEquals(postOrderItemCount, expectedProductCount, "Product count");
        Assert.assertTrue(postOrderTotalAmount.compareTo(expectedOrderTotalAmount) == 0, "Order total amount: " + expectedOrderTotalAmount + ", but got: " + postOrderTotalAmount);

        //Debt tab
        if (orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount() != 0) {
            Assert.assertNotEquals(postFirstDebtRecordId, firstDebtRecordId, "Latest debt record id");
            Assert.assertEquals(postDebtAction, expectedDebtAction, "Debt record action");
            Assert.assertTrue(postDebtRecordAmount.compareTo(expectedDebtRecordAmount.setScale(2, RoundingMode.HALF_UP)) == 0, "Debt record amount: " + expectedDebtRecordAmount + ", but got: " + postDebtRecordAmount);
            Assert.assertEquals(postDebtReferenceId, postFirstOrderId, "Debt reference id");
            Assert.assertEquals(postDebtDate, expectedOrderDate, "Debt record date");
            Assert.assertTrue(postAccumulatedDebt.compareTo(expectedDebtAmount) == 0, "Debt record accumulated amount: " + expectedDebtAmount + ", but got: " + postAccumulatedDebt);
        } else {
            Assert.assertEquals(postFirstDebtRecordId, firstDebtRecordId, "Latest debt record id");
        }

        //Cashbook summary
        Assert.assertTrue(postTotalRevenue.compareTo(expectedTotalRevenue) == 0, "Total revenue: " + expectedTotalRevenue + ", but got: " + postTotalRevenue);
        Assert.assertTrue(postEndingBalance.compareTo(expectedEndingBalance) == 0, "Ending balance: " + expectedEndingBalance + ", but got: " + postEndingBalance);

        //Cashbook record
        if (receivedAmount > 0) {
            Assert.assertNotEquals(firstPostTransactionCodeId, firstTransactionCodeId, "Latest transaction code id");
            Assert.assertEquals(postCashbookRecordCreatedDate, expectedCashbookRecordCreatedDate, "Cashbook record created date");
            Assert.assertEquals(postCashbookRecordGroupType, expectedCashbookRecordGroupType, "Cashbook record group type");
            Assert.assertEquals(postCashbookRecordCustomerName, expectedCashbookRecordCustomerName, "Cashbook record customer name");
            Assert.assertEquals(postCashbookSourceType, expectedCashbookSourceType, "Cashbook record source type");
            Assert.assertEquals(postCashbookRecordBranch, expectedCashbookRecordBranch, "Cashbook record branch");
            Assert.assertTrue(postCashbookRecordAmount.compareTo(expectedCashbookRecordAmount) == 0, "Cashbook record amount: " + expectedCashbookRecordAmount + ", but got: " + postCashbookRecordAmount);
            Assert.assertEquals(postCashbookRecordPaymentMethod, expectedCashbookRecordPaymentMethod, "Cashbook record payment method");
        } else {
            Assert.assertEquals(firstPostTransactionCodeId, firstTransactionCodeId, "Latest transaction code id");
        }

        driver.quit();
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }
}
