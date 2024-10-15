package web.Dashboard;

import static utilities.account.AccountTest.ADMIN_PHONE_BIZ_COUNTRY;
import static utilities.account.AccountTest.ADMIN_PHONE_BIZ_PASSWORD;
import static utilities.account.AccountTest.ADMIN_PHONE_BIZ_USERNAME;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.STAFF_SHOP_VI_USERNAME;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import api.Seller.products.inventory.APIInventoryHistoryV2;
import api.Seller.products.inventory.APIInventoryV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import api.Seller.analytics.APIOrdersAnalytics;
import api.Seller.cashbook.CashbookAPI;
import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICustomerDetail;
import api.Seller.customers.APIUpdatePoint;
import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.orders.order_management.APIAllOrders.OrderStatus;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.products.all_products.APIAddConversionUnit;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.all_products.WholesaleProduct;
import api.Seller.products.lot_date.APICreateLotDate;
import api.Seller.products.lot_date.APIEditLotDate;
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

public class POSOrderTest extends BaseTest {
    String country;
    String phoneCode;
    String username = ADMIN_SHOP_VI_USERNAME;
    String pass = ADMIN_SHOP_VI_PASSWORD;
    LoginInformation credentials;
    Logger logger = LogManager.getLogger(POSOrderTest.class);

    
    private List<Integer> createProductForPOSCart(LoginInformation loginInformation, BranchInfo branchInfo, int stockQuantity) {

        // Create lot
        int lotId = new APICreateLotDate(loginInformation).createLotDateAndGetLotId();

        // Init cart items
        List<Integer> items = new ArrayList<>();

        // Init stock
        int[] stock = new int[branchInfo.getBranchID().size()];
        Arrays.fill(stock, stockQuantity);

        // Init API create product
        APICreateProduct apiCreateProduct = new APICreateProduct(loginInformation);

        // Create lot product
        int withoutVariationProductIdWithLot = apiCreateProduct.setLotAvailable(true).createWithoutVariationProduct(false, stock).getProductID();
        int withVariationProductIdWithLot = apiCreateProduct.setLotAvailable(true).createVariationProduct(false, 0, stock).getProductID();

        // Create conversion unit
        int withoutVariationProductIdWithConversionUnit = apiCreateProduct.createWithoutVariationProduct(false, stock).getProductID();
        int withVariationProductIdWithConversionUnit = apiCreateProduct.createVariationProduct(false, 0, stock).getProductID();

        // Create without variation product
        int productWholesaleId = apiCreateProduct.createWithoutVariationProduct(false, stock).getProductID();
        items.add(productWholesaleId);
        items.add(apiCreateProduct.createWithoutVariationProduct(true, stock).getProductID());
        new WholesaleProduct(loginInformation).addWholesalePriceProduct(new APIProductDetailV2(loginInformation).getInfo(productWholesaleId));

        // Create with variation product
        items.add(apiCreateProduct.createVariationProduct(false, 0, stock).getProductID());
        items.add(apiCreateProduct.createVariationProduct(true, 0, stock).getProductID());

        // Add product to lot and update stock
        new APIEditLotDate(loginInformation).addProductIntoLot(lotId, withoutVariationProductIdWithLot, 5);
        new APIEditLotDate(loginInformation).addProductIntoLot(lotId, withVariationProductIdWithLot, 5);
        items.add(withoutVariationProductIdWithLot);
        items.add(withVariationProductIdWithLot);

        // Add conversion unit to product
        new APIAddConversionUnit(loginInformation).addConversionUnitToProduct(withoutVariationProductIdWithConversionUnit);
        new APIAddConversionUnit(loginInformation).addConversionUnitToProduct(withVariationProductIdWithConversionUnit);
        items.add(withoutVariationProductIdWithConversionUnit);
        items.add(withVariationProductIdWithConversionUnit);

        return items;
    }    

    int getRandomCustomerId(APIAllCustomers allCustomerAPI) {
        //Get a list of profile ids whose saleChannel is GOSELL
        var profileIdPool = allCustomerAPI.getProfileRecords().stream().filter(pro -> pro.getSaleChannel().contentEquals("GOSELL")).map(id -> id.getId()).toList();

        //Get a random profileId from the pool
        return DataGenerator.getRandomListElement(profileIdPool);
    }

    /**
     * Retrieves detail of a customer
     * @param customerDetailAPI
     * @param profileId id of the customer - 0 for walk-in guest
     * @return POJO object representing the customer's detail,
     * or null if that's a walk-in guest
     */
    CustomerInfoFull getCustomerDetail(APICustomerDetail customerDetailAPI, int profileId) {
    	if (profileId == 0) {
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

    /**
     * Sets earning point for a customer so that he can redeem it when buying products.
     * When the customer already has some points, we don't give him points
     * @param isWalkInGuest
     * @param customerDetail
     * @param existingEarningPoints
     * @return the number of points given to the customer. 1000 by default
     */
    int setEarningPointsWhenNeeded(boolean isWalkInGuest, CustomerInfoFull customerDetail, int existingEarningPoints) {
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
    	if (profileId == 0) {
            return null;
        }
        return customerDetailAPI.getOrderSummary(profileId);
    }

    int getLatestDebtRecordId(APICustomerDetail customerDetailAPI, CustomerInfoFull customerDetail) throws JsonProcessingException {
        if (customerDetail == null) {
            return -1;
        }

        List<CustomerDebtRecord> previousDebtList = customerDetailAPI.getDebtRecords(customerDetail.getId());
        return previousDebtList.stream()
                .findFirst()
                .map(CustomerDebtRecord::getId)
                .orElse(-1);
    }
    
    List<CustomerDebtRecord> getDebtRecordList(APICustomerDetail customerDetailAPI, int customerId) throws JsonProcessingException {
    	if (customerId == 0) {
    		return Collections.emptyList();
    	}
    	return customerDetailAPI.getDebtRecords(customerId);
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

    int workoutExpectedTotalOrderCount(Integer previousTotalOrderCount, OrderDetailInfo orderDetailsBeforeCheckout) {
        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalOrderCount + 1;
        }
        return previousTotalOrderCount;
    }

    BigDecimal workoutExpectedTotalPurchase(BigDecimal previousTotalPurchase, OrderDetailInfo orderDetailsBeforeCheckout) {
        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalPurchase.add(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice()));
        }
        return previousTotalPurchase;
    }

    BigDecimal workoutExpectedTotalPurchaseLast3Months(BigDecimal previousTotalPurchaseLast3Months, OrderDetailInfo orderDetailsBeforeCheckout) {
        var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            return previousTotalPurchaseLast3Months.add(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice()));
        }
        return previousTotalPurchaseLast3Months;
    }

    BigDecimal workoutExpectedAverageOrderValue(BigDecimal totalOrderCount, BigDecimal totalPurchase) {
        if (totalOrderCount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalPurchase.divide(totalOrderCount, 10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }

    String workoutExpectedCashbookRecordSourceType(boolean isDeliveryOpted, BigDecimal debt) {
        if (isDeliveryOpted)
            return CashbookRevenue.DEBT_COLLECTION_FROM_CUSTOMER.name();

        if (debt.compareTo(BigDecimal.ZERO) == 0)
            return CashbookRevenue.PAYMENT_FOR_ORDER.name();

        return CashbookRevenue.DEBT_COLLECTION_FROM_CUSTOMER.name();
    }

    /**
     * Verifies customer's order summary data post-order
     * @param postOrderSummary
     * @param expectedTotalOrderCount
     * @param expectedTotalPurchase
     * @param expectedTotalPurchaseLast3Months
     * @param expectedAverageOrderValue
     * @param expectedDebtAmount
     */
    void verifyCustomerOrderSummary(CustomerOrderSummary postOrderSummary, int expectedTotalOrderCount, BigDecimal expectedTotalPurchase, BigDecimal expectedTotalPurchaseLast3Months, BigDecimal expectedAverageOrderValue, BigDecimal expectedDebtAmount) {
        int postTotalOrderCount = postOrderSummary.getTotalOrder();
        var postTotalPurchase = postOrderSummary.getTotalPurchase();
        var postTotalPurchaseLast3Months = postOrderSummary.getTotalPurchaseLast3Month();
        var postAverageOrderValue = postOrderSummary.getAverangePurchase();
        var postDebtAmount = postOrderSummary.getDebtAmount();
        
        Assert.assertEquals(postTotalOrderCount, expectedTotalOrderCount, "Total order count");
        Assert.assertEquals(postTotalPurchase.compareTo(expectedTotalPurchase), 0, "Total purchase expected: " + expectedTotalPurchase + ", but got: " + postTotalPurchase);
        Assert.assertEquals(postTotalPurchaseLast3Months.compareTo(expectedTotalPurchaseLast3Months), 0, "Total purchase last 3 months expected: " + expectedTotalPurchaseLast3Months + ", but got: " + postTotalPurchaseLast3Months);
        Assert.assertEquals(postAverageOrderValue.compareTo(expectedAverageOrderValue), 0, "Average order value: " + expectedAverageOrderValue + ", but got: " + postAverageOrderValue);
        Assert.assertEquals(postDebtAmount.compareTo(expectedDebtAmount), 0, "Debt amount expected: " + expectedDebtAmount + ", but got: " + postDebtAmount);
    }   
    
    void verifyCustomerOrderTab(CustomerOrder postLastestOrder,  CustomerOrder expectedCustomerOrder) {
        var postFirstOrderId = postLastestOrder.getId(); // 13339499
        var postOrderChannel = postLastestOrder.getChannel(); // GOSELL
        var postOrderDate = postLastestOrder.getCreatedDate().replaceAll("T.*", ""); // 2024-08-26T08:42:29.427377Z
        var postOrderPaymentStatus = postLastestOrder.getIsPaid(); // true
        var postOrderStatus = postLastestOrder.getStatus(); // DELIVERED
        var postOrderItemCount = postLastestOrder.getItemsCount(); // 1
        var postOrderTotalAmount = postLastestOrder.getTotal(); // 221000
        
        Assert.assertEquals(postFirstOrderId, expectedCustomerOrder.getId(), "Latest order record id");
        Assert.assertEquals(postOrderChannel, expectedCustomerOrder.getChannel(), "Sale channel");
        Assert.assertEquals(postOrderDate, expectedCustomerOrder.getCreatedDate(), "Order date");
        Assert.assertEquals(postOrderPaymentStatus, expectedCustomerOrder.getIsPaid(), "Payment status");
        Assert.assertEquals(postOrderStatus, expectedCustomerOrder.getStatus(), "Delivery status");
        Assert.assertEquals(postOrderItemCount, expectedCustomerOrder.getItemsCount(), "Product count");
        Assert.assertEquals(postOrderTotalAmount.compareTo(expectedCustomerOrder.getTotal()), 0, "Order total amount: " + expectedCustomerOrder.getTotal() + ", but got: " + postOrderTotalAmount);
    }     
 
    void verifyNoDebtRecordsGeneratedPostOrder(List<CustomerDebtRecord> previousRecordList, List<CustomerDebtRecord> postRecordList) {
    	Assert.assertEquals(postRecordList, previousRecordList);
    }    
    void verifyNewDebtRecordsGeneratedPostOrder(List<CustomerDebtRecord> previousRecordList, List<CustomerDebtRecord> postRecordList, CustomerDebtRecord expectedRecord) {
    	var postRecord = postRecordList.get(0);
    	
    	if (!previousRecordList.isEmpty()) {
    		//There exists records pre-order
    		Assert.assertNotEquals(previousRecordList.get(0), postRecord, "Latest Debt record");
    	}
    	
    	Assert.assertEquals(postRecord.getAction(), expectedRecord.getAction(), "Debt record action");
    	Assert.assertEquals(postRecord.getAmount().compareTo(expectedRecord.getAmount()), 0, "Debt record amount: " + expectedRecord.getAmount() + ", but got: " + postRecord.getAmount());
    	Assert.assertEquals(postRecord.getRefId(), expectedRecord.getRefId(), "Debt reference id");
    	Assert.assertEquals(postRecord.getCreatedDate().replaceAll("T.*", ""), expectedRecord.getCreatedDate(), "Debt record date");
    	Assert.assertEquals(postRecord.getDebt().compareTo(expectedRecord.getDebt()), 0, "Debt record accumulated amount: " + expectedRecord.getDebt() + ", but got: " + postRecord.getDebt());
    }
    
    /**
     * Verifies Debt records post-order.
     * When debtAmount =0, there won't be new records
     * @param debtAmount
     * @param previousRecordList
     * @param postRecordList
     * @param expectedRecord
     */
    void verifyDebtRecord(double debtAmount, List<CustomerDebtRecord> previousRecordList, List<CustomerDebtRecord> postRecordList, CustomerDebtRecord expectedRecord) {
    	if (debtAmount ==0) {
    		verifyNoDebtRecordsGeneratedPostOrder(previousRecordList, postRecordList);
    	} else {
    		verifyNewDebtRecordsGeneratedPostOrder(previousRecordList, postRecordList, expectedRecord);
    	}
    }    
    
    /**
     * Verifies Cashbook summary after orders are successfully made on POS.
     * When receivedAmount =0, there won't be any changes in the summary data
     * @param previousSummary
     * @param postSummary
     * @param receivedAmount
     */
    void verifyCashbookSummary(List<BigDecimal> previousSummary, List<BigDecimal> postSummary, BigDecimal receivedAmount) {
        var expectedTotalRevenue = previousSummary.get(1).add(receivedAmount);
        var expectedEndingBalance = previousSummary.get(3).add(receivedAmount);
    	
        var postTotalRevenue = postSummary.get(1);
        var postEndingBalance = postSummary.get(3);

        Assert.assertEquals(postTotalRevenue.compareTo(expectedTotalRevenue), 0, "Total revenue: " + expectedTotalRevenue + ", but got: " + postTotalRevenue);
        Assert.assertEquals(postEndingBalance.compareTo(expectedEndingBalance), 0, "Ending balance: " + expectedEndingBalance + ", but got: " + postEndingBalance);
    } 
    
    void verifyNoCashbookRecordsGeneratedPostOrder(List<CashbookRecord> previousRecordList, List<CashbookRecord> postRecordList) {
    	Assert.assertEquals(postRecordList, previousRecordList);
    }
    void verifyNewCashbookRecordsGeneratedPostOrder(List<CashbookRecord> previousRecordList, List<CashbookRecord> postRecordList, CashbookRecord expectedRecord) {
    	var postRecord = postRecordList.get(0);
    	
    	if (!previousRecordList.isEmpty()) {
    		//There exists records pre-order
    		Assert.assertNotEquals(previousRecordList.get(0), postRecord, "Latest Cashbook record");
    	}
    	
    	Assert.assertEquals(postRecord.getCreatedDate().replaceAll("T.*", ""), expectedRecord.getCreatedDate(), "Cashbook record created date");
    	Assert.assertEquals(postRecord.getGroupType(), expectedRecord.getGroupType(), "Cashbook record group type");
    	Assert.assertEquals(postRecord.getCustomerName(), expectedRecord.getCustomerName(), "Cashbook record customer name");
    	Assert.assertEquals(postRecord.getSourceType(), expectedRecord.getSourceType(), "Cashbook record source type");
    	Assert.assertEquals(postRecord.getBranchName(), expectedRecord.getBranchName(), "Cashbook record branch");
        Assert.assertEquals(postRecord.getAmount().compareTo(expectedRecord.getAmount()), 0, "Cashbook record amount: " + expectedRecord.getAmount() + ", but got: " + postRecord.getAmount());
    	Assert.assertEquals(postRecord.getPaymentMethod(), expectedRecord.getPaymentMethod(), "Cashbook record payment method");    	
    }
    /**
     * Verifies whether Cashbook records are generated after orders are successfully made on POS.
     * When receivedAmount =0, there won't be any records.
     * @param receivedAmount
     * @param previousRecordList
     * @param postRecordList
     * @param expectedRecord
     */
    void verifyCashbookRecord(BigDecimal receivedAmount, List<CashbookRecord> previousRecordList, List<CashbookRecord> postRecordList, CashbookRecord expectedRecord) {
    	if (receivedAmount.compareTo(BigDecimal.ZERO) ==0) {
    		verifyNoCashbookRecordsGeneratedPostOrder(previousRecordList, postRecordList);
    	} else {
    		verifyNewCashbookRecordsGeneratedPostOrder(previousRecordList, postRecordList, expectedRecord);
    	}
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
    public void TC_CheckCustomerInfoPostOrder(CreatePOSOrderCondition condition, TimeFrame timeFrame) throws JsonProcessingException {
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

        //Get branch info
        BranchInfo branchInfo = new BranchManagement(credentials).getInfo();
        String branchName = DataGenerator.getRandomListElement(branchInfo.getBranchName());

        // Set stock quantity
        int stockQuantity = 5;
        // Create products for test
        List<Integer> productIds = createProductForPOSCart(credentials, branchInfo, stockQuantity);
        
        APIAllCustomers allCustomerAPI = new APIAllCustomers(credentials);
        APICustomerDetail customerDetailAPI = new APICustomerDetail(credentials);

        CashbookAPI cashbookAPI = new CashbookAPI(credentials);

        //Walk-in guest is 0
        int selectedCustomerId = 0;

        if (!condition.isWalkInGuest()) {
        	//Existing customers
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
        previousEarningPoints = setEarningPointsWhenNeeded(condition.isWalkInGuest(), selectedProfile, previousEarningPoints);

        CustomerOrderSummary previousOrderSummary = getCustomerOrderSummary(customerDetailAPI, selectedCustomerId);
        int previousTotalOrderCount = (previousOrderSummary != null) ? previousOrderSummary.getTotalOrder() : 0;
        BigDecimal previousTotalPurchase = (previousOrderSummary != null) ? previousOrderSummary.getTotalPurchase() : BigDecimal.ZERO;
        BigDecimal previousTotalPurchaseLast3Months = (previousOrderSummary != null) ? previousOrderSummary.getTotalPurchaseLast3Month() : BigDecimal.ZERO;
        BigDecimal previousDebtAmount = (previousOrderSummary != null) ? previousOrderSummary.getDebtAmount() : BigDecimal.ZERO;

        //Debt tab
        List<CustomerDebtRecord> previousDebtList = getDebtRecordList(customerDetailAPI, selectedCustomerId);

        //Cashbook summary
        List<BigDecimal> previousSummary = cashbookAPI.getCasbookSummary();

        //Cashbook record
        List<CashbookRecord> previousCashbookRecords = cashbookAPI.getAllRecords();


        /** Place an order in POS **/
        driver = new InitWebdriver().getDriver(browser, headless);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin(country, credentials.getPhoneNumber(), credentials.getPassword());

        POSPage posPage = new POSPage(driver, Domain.valueOf(domain)).getLoginInfo(credentials).navigateToPOSPage();

        // Select branch
        posPage.selectBranch(branchName);

        // Add product to cart
        posPage.selectProduct(credentials, productIds);
        
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
        int expectedTotalOrderCount = workoutExpectedTotalOrderCount(previousTotalOrderCount, orderDetailsBeforeCheckout);
        BigDecimal expectedTotalPurchase = workoutExpectedTotalPurchase(previousTotalPurchase, orderDetailsBeforeCheckout);
        BigDecimal expectedTotalPurchaseLast3Months = workoutExpectedTotalPurchaseLast3Months(previousTotalPurchaseLast3Months, orderDetailsBeforeCheckout);
        BigDecimal expectedAverageOrderValue = workoutExpectedAverageOrderValue(new BigDecimal(expectedTotalOrderCount), expectedTotalPurchase);
        BigDecimal expectedDebtAmount = previousDebtAmount.add(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount())).setScale(2, RoundingMode.HALF_UP);


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

        //Order tab
        CustomerOrder expectedCustomerOrder = new CustomerOrder();
        expectedCustomerOrder.setId(String.valueOf(orderId));
        expectedCustomerOrder.setChannel("GOSELL");
        expectedCustomerOrder.setCreatedDate(expectedOrderDate);
        expectedCustomerOrder.setIsPaid(expectedPaidStatus);
        expectedCustomerOrder.setStatus(expectedDeliveryStatus);
        expectedCustomerOrder.setItemsCount(expectedProductCount);
        expectedCustomerOrder.setTotal(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getTotalPrice()));
        
        //Debt tab
        CustomerDebtRecord expectedDebtRecord = new CustomerDebtRecord();
        expectedDebtRecord.setAction(condition.isHasDelivery() ? DebtActionEnum.POS_DELIVERY_ORDER : DebtActionEnum.POS_NOW_ORDER);
        expectedDebtRecord.setAmount(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
        expectedDebtRecord.setRefId(String.valueOf(orderId));
        expectedDebtRecord.setCreatedDate(expectedOrderDate);
        expectedDebtRecord.setDebt(expectedDebtAmount);
        
        // Check inventory
        new APIInventoryV2(credentials).checkInventoryAfterOrder(stockQuantity, orderId);
        new APIInventoryHistoryV2(credentials).checkInventoryAfterOrder(stockQuantity, orderId);
        
        //After an order is place, an userId is given to the customer if it's previously undefined
        userId = (userId == null) ? String.valueOf(orderDetailInfo.getCustomerInfo().getUserId()) : userId;

        //Cashbook record
        CashbookRecord expectedRecord = new CashbookRecord();
        expectedRecord.setCreatedDate(expectedOrderDate);
        expectedRecord.setGroupType("CUSTOMER");
        expectedRecord.setCustomerName(customerName);
        expectedRecord.setSourceType(workoutExpectedCashbookRecordSourceType(condition.isHasDelivery(), BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount())));
        expectedRecord.setBranchName(branchName);
        expectedRecord.setAmount(BigDecimal.valueOf(receivedAmount));
        expectedRecord.setPaymentMethod(condition.getPaymentMethod().name());


        /** Retrieve post-order data **/
        //Earning points
        int postEarningPoints = customerDetailAPI.getEarningPoint(userId);

        //Order summary
        CustomerOrderSummary postOrderSummary = customerDetailAPI.getOrderSummary(selectedCustomerId);

        //Order tab
        CustomerOrder postCustomerLastestOrder = customerDetailAPI.getOrders(selectedCustomerId, userId).get(0);

        //Debt tab
        List<CustomerDebtRecord> postDebtList = customerDetailAPI.getDebtRecords(selectedCustomerId);

        //Cashbook summary
        List<BigDecimal> postSummary = cashbookAPI.getCasbookSummary();

        //Cashbook record
        List<CashbookRecord> postCashbookRecords = cashbookAPI.getAllRecords();
        
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
        verifyCustomerOrderSummary(postOrderSummary, expectedTotalOrderCount, expectedTotalPurchase, expectedTotalPurchaseLast3Months, expectedAverageOrderValue, expectedDebtAmount);
        
        //Order tab
        verifyCustomerOrderTab(postCustomerLastestOrder, expectedCustomerOrder);
        
        //Debt tab
        verifyDebtRecord(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount(), previousDebtList, postDebtList, expectedDebtRecord);

        //Cashbook summary
        verifyCashbookSummary(previousSummary, postSummary, BigDecimal.valueOf(receivedAmount));
        
        //Cashbook record
        verifyCashbookRecord(BigDecimal.valueOf(receivedAmount), previousCashbookRecords, postCashbookRecords, expectedRecord);
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }
}
