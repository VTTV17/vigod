package web.Dashboard;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import api.Seller.cashbook.CashbookAPI;
import api.Seller.customers.APICustomerDetail;
import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders.OrderStatus;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.setting.BranchManagement;
import utilities.commons.UICommonAction;
import utilities.data.testdatagenerator.CreateCustomerTDG;
import utilities.driver.InitWebdriver;
import utilities.enums.DebtActionEnum;
import utilities.enums.DisplayLanguage;
import utilities.enums.pos.ReceivedAmountType;
import utilities.model.dashboard.cashbook.CashbookRecord;
import utilities.model.dashboard.customer.CustomerDebtRecord;
import utilities.model.dashboard.customer.CustomerOrder;
import utilities.model.dashboard.customer.CustomerOrderSummary;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.pos.create_order.POSPage;
import web.Dashboard.orders.pos.create_order.POSPage.POSPaymentMethod;

public class POSOrderTest extends BaseTest{

//	LoginInformation credentials = new Login().setLoginInformation("tham1babe@mailnesia.com", "fortesting!1").getLoginInformation();
	LoginInformation credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();

	public void createVNCustomer() {

		driver = new InitWebdriver().getDriver(browser, headless);
		commonAction = new UICommonAction(driver);
		
		LoginPage loginPage = new LoginPage(driver);
		
		loginPage.navigate().selectDisplayLanguage(language).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		
		AllCustomers allCustomer = new AllCustomers(driver).navigateByURL();
		
		commonAction.refreshPage();
		
		UICreateCustomerData data = CreateCustomerTDG.buildVNCustomerUIData(DisplayLanguage.valueOf(language));
		
		allCustomer.clickCreateNewCustomerBtn().createCustomer(data).clickAddBtn();
		new HomePage(driver).getToastMessage();
	}
	
	public void createForeignCustomer() {
		
		driver = new InitWebdriver().getDriver(browser, headless);
		commonAction = new UICommonAction(driver);
		
		LoginPage loginPage = new LoginPage(driver);
		
		loginPage.navigate().selectDisplayLanguage(language).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		
		AllCustomers allCustomer = new AllCustomers(driver).navigateByURL();
		
		commonAction.refreshPage();
		
		UICreateCustomerData data = CreateCustomerTDG.buildForeignCustomerUIData(DisplayLanguage.valueOf(language));
		
		allCustomer.clickCreateNewCustomerBtn().createCustomer(data).clickAddBtn();
		new HomePage(driver).getToastMessage();
	}
	
	int workoutExpectedEarningPoints(int previousEarningPoints, OrderDetailInfo orderDetailsBeforeCheckout) {
		var uiEarningPoints = orderDetailsBeforeCheckout.getEarningPoint().getValue();
		var uiUsedPoints = orderDetailsBeforeCheckout.getOrderInfo().getUsePoint();
		var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP
		
		if (orderStatus.equals(OrderStatus.DELIVERED)) {
			return (previousEarningPoints - uiUsedPoints) + uiEarningPoints;
		} else {
			return (previousEarningPoints - uiUsedPoints);
		}
	}
	public Integer workoutExpectedTotalOrderCount(Integer previousTotalOrderCount, OrderDetailInfo orderDetailsBeforeCheckout) {
		
		var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP
		
		if (orderStatus.equals(OrderStatus.DELIVERED)) {
			return previousTotalOrderCount +1;
		}
		return previousTotalOrderCount;
	}
	public BigDecimal workoutExpectedTotalPurchase(BigDecimal previousTotalPurchase, OrderDetailInfo orderDetailsBeforeCheckout) {
		
		double uiTotalOrderAmount = orderDetailsBeforeCheckout.getOrderInfo().getTotalAmount();
		
		var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP
		
		if (orderStatus.equals(OrderStatus.DELIVERED)) {
			return previousTotalPurchase.add(BigDecimal.valueOf(uiTotalOrderAmount));
		}
		return previousTotalPurchase;
	}
	public BigDecimal workoutExpectedTotalPurchaseLast3Months(BigDecimal previousTotalPurchaseLast3Months, OrderDetailInfo orderDetailsBeforeCheckout) {
		double uiTotalOrderAmount = orderDetailsBeforeCheckout.getOrderInfo().getTotalAmount();
		var orderStatus = OrderStatus.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getStatus()); //DELIVERED/TO_SHIP
		
		if (orderStatus.equals(OrderStatus.DELIVERED)) {
			return previousTotalPurchaseLast3Months.add(BigDecimal.valueOf(uiTotalOrderAmount));
		}
		return previousTotalPurchaseLast3Months;
	}
	public BigDecimal workoutExpectedAverageOrderValue(BigDecimal totalOrderCount, BigDecimal totalPurchase) {
		return totalPurchase.divide(totalOrderCount, 10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
	}
	public String workoutExpectedCashbookRecordSourceType(boolean isDeliveryOpted, BigDecimal debt) {
		if (isDeliveryOpted) return "DEBT_COLLECTION_FROM_CUSTOMER";
		if (debt.compareTo(BigDecimal.ZERO) ==0) return "PAYMENT_FOR_ORDER";
		return "DEBT_COLLECTION_FROM_CUSTOMER";
	}
	
    @DataProvider
    public static Object[][] receivedAmountAndDeliveryOptions() {
        return new Object[][] {
            { ReceivedAmountType.FULL, true, POSPaymentMethod.CASH },
            { ReceivedAmountType.FULL, false, POSPaymentMethod.CASH },
            { ReceivedAmountType.FULL, true, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.FULL, false, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.FULL, true, POSPaymentMethod.POS },
            { ReceivedAmountType.FULL, false, POSPaymentMethod.POS },
            { ReceivedAmountType.PARTIAL, true, POSPaymentMethod.CASH },
            { ReceivedAmountType.PARTIAL, false, POSPaymentMethod.CASH },
            { ReceivedAmountType.PARTIAL, true, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.PARTIAL, false, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.PARTIAL, true, POSPaymentMethod.POS },
            { ReceivedAmountType.PARTIAL, false, POSPaymentMethod.POS },
            { ReceivedAmountType.NONE, true, POSPaymentMethod.CASH },
            { ReceivedAmountType.NONE, false, POSPaymentMethod.CASH },
            { ReceivedAmountType.NONE, true, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.NONE, false, POSPaymentMethod.BANK_TRANSFER },
            { ReceivedAmountType.NONE, true, POSPaymentMethod.POS },
            { ReceivedAmountType.NONE, false, POSPaymentMethod.POS }
        };
    }
	@Test(dataProvider = "receivedAmountAndDeliveryOptions")
	public void TC_CheckCustomerInfoPostOrder(ReceivedAmountType receivedAmountType, boolean isDeliveryOpted, POSPaymentMethod paymentMethod) throws JsonMappingException, JsonProcessingException {
		
		/** Test case input **/
		
		LoginInformation credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();
		
		String customerName = "Auto Buyer 8053241775";
		int customerId = 3951003;
		int userId = 44213179;
		
		BranchInfo branchInfo = new BranchManagement(credentials).getInfo();
		APIAllProducts allProduct = new APIAllProducts(credentials);
		String branchName = branchInfo.getBranchName().get(nextInt(branchInfo.getBranchName().size()));
		
		System.out.println("Payment type: " + receivedAmountType);
		System.out.println("Delivery selected: " + isDeliveryOpted);
		System.out.println("Payment method: " + paymentMethod);
		
		
		/** Retrieve pre-order data **/
		APICustomerDetail customerDetailAPI = new APICustomerDetail(credentials);
		
		//Earning point
		int previousEarningPoints = customerDetailAPI.getEarningPoint(userId);
		
		//Order summary
		CustomerOrderSummary previousOrderSummary = customerDetailAPI.getOrderSummary(customerId);
		Integer previousTotalOrderCount = previousOrderSummary.getTotalOrder();
		BigDecimal previousTotalPurchase = previousOrderSummary.getTotalPurchase();
		BigDecimal previousTotalPurchaseLast3Months = previousOrderSummary.getTotalPurchaseLast3Month();
		BigDecimal previousDebtAmount = previousOrderSummary.getDebtAmount();
		
		//Order tab
		List<CustomerOrder> previousOrderList = customerDetailAPI.getOrders(customerId, userId);
		String firstOrderId = previousOrderList.stream().findFirst().map(CustomerOrder::getId).orElse("");
		
		//Debt tab
		List<CustomerDebtRecord> previousDebtList = customerDetailAPI.getDebtRecords(customerId);
		Integer firstDebtRecordId = previousDebtList.stream().findFirst().map(CustomerDebtRecord::getId).orElse(-1);
		
		//Cashbook summary
		CashbookAPI cashbookAPI = new CashbookAPI(credentials);
		List<BigDecimal> previousSummary = cashbookAPI.getCasbookSummary();
		BigDecimal previousTotalRevenue = previousSummary.get(1);
		BigDecimal previousEndingBalance = previousSummary.get(3);
		
		//Cashbook record
		List<CashbookRecord> cashbookRecords = cashbookAPI.getAllRecords();
		String firstTransactionCodeId = cashbookRecords.stream().findFirst().map(CashbookRecord::getTransactionCode).orElse("RN");
		
		
		/** Place an order in POS **/
		driver = new InitWebdriver().getDriver(browser, headless);
		commonAction = new UICommonAction(driver);
		
		LoginPage loginPage = new LoginPage(driver);
		loginPage.navigate().selectDisplayLanguage(language).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		
		POSPage posPage = new POSPage(driver).getLoginInfo(credentials).navigateToPOSPage();
//		POSPage posPage = new POSPage(driver).getLoginInfo(credentials);
		
		posPage.createPOSOrder(credentials, branchName, allProduct.getListProductId());
		posPage.selectCustomer(customerName);
		posPage.selectPaymentMethod(paymentMethod);
		posPage.selectDelivery(isDeliveryOpted);
		Double receivedAmount =  posPage.inputReceiveAmount(receivedAmountType);
		
		
		/** Organize expected results **/
		//Order details
		OrderDetailInfo orderDetailsBeforeCheckout = posPage.getOrderInfoBeforeCheckOut(customerId);
		Boolean expectedPaidStatus = orderDetailsBeforeCheckout.getOrderInfo().getPaid();
		String expectedDeliveryStatus = orderDetailsBeforeCheckout.getOrderInfo().getStatus();
		Integer expectedProductCount = orderDetailsBeforeCheckout.getItems().size();
		
		//Earning points
		int expectedEarningPoints = workoutExpectedEarningPoints(previousEarningPoints, orderDetailsBeforeCheckout);
		
		//Order summary
		Integer expectedTotalOrderCount = workoutExpectedTotalOrderCount(previousTotalOrderCount, orderDetailsBeforeCheckout);
		BigDecimal expectedTotalPurchase = workoutExpectedTotalPurchase(previousTotalPurchase, orderDetailsBeforeCheckout);
		BigDecimal expectedTotalPurchaseLast3Months = workoutExpectedTotalPurchaseLast3Months(previousTotalPurchaseLast3Months, orderDetailsBeforeCheckout);
		BigDecimal expectedAverageOrderValue = workoutExpectedAverageOrderValue(new BigDecimal(expectedTotalOrderCount), expectedTotalPurchase);
		BigDecimal expectedDebtAmount = previousDebtAmount.add(BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
		
		//Order tab
		BigDecimal expectedOrderTotalAmount = BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getTotalAmount());
		
		//Debt tab
		DebtActionEnum expectedDebtAction = posPage.isDeliveryOpted() ? DebtActionEnum.POS_DELIVERY_ORDER : DebtActionEnum.POS_NOW_ORDER;
		BigDecimal expectedDebtRecordAmount = BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount());
		
		posPage.clickCompleteCheckout();
		if (!receivedAmountType.equals(ReceivedAmountType.FULL)) new ConfirmationDialog(driver).clickOKBtn();
		new HomePage(driver).getToastMessage();
		
		//Order date is the moment the order is placed successfully
		String expectedOrderDate = LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
		//Cashbook summary
		BigDecimal expectedTotalRevenue = previousTotalRevenue.add(BigDecimal.valueOf(receivedAmount));
		BigDecimal expectedEndingBalance = previousEndingBalance.add(BigDecimal.valueOf(receivedAmount));
		
		//Cashbook record
		String expectedCashbookRecordCreatedDate = expectedOrderDate;
		String expectedCashbookRecordGroupType = "CUSTOMER";
		String expectedCashbookRecordCustomerName = customerName;
		String expectedCashbookSourceType = workoutExpectedCashbookRecordSourceType(isDeliveryOpted, BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
		String expectedCashbookRecordBranch = branchName;
		BigDecimal expectedCashbookRecordAmount = BigDecimal.valueOf(receivedAmount);
		String expectedCashbookRecordPaymentMethod = paymentMethod.name();
		
		
		/** Retrieve post-order data **/
		//Earning points
		int postEarningPoints = customerDetailAPI.getEarningPoint(userId);
		
		//Order summary
		CustomerOrderSummary postOrderSummary = customerDetailAPI.getOrderSummary(customerId);
		Integer postTotalOrderCount = postOrderSummary.getTotalOrder();
		BigDecimal postTotalPurchase = postOrderSummary.getTotalPurchase();
		BigDecimal postTotalPurchaseLast3Months = postOrderSummary.getTotalPurchaseLast3Month();
		BigDecimal postAverageOrderValue = postOrderSummary.getAverangePurchase();
		BigDecimal postDebtAmount = postOrderSummary.getDebtAmount();
		
		//Order tab
		List<CustomerOrder> postOrderList = customerDetailAPI.getOrders(customerId, userId);
		String postFirstOrderId = postOrderList.get(0).getId(); // 13339499
		String postOrderChannel = postOrderList.get(0).getChannel(); // GOSELL
		String postOrderDate = postOrderList.get(0).getCreatedDate().replaceAll("T.*", ""); // 2024-08-26T08:42:29.427377Z
		Boolean postOrderPaymentStatus = postOrderList.get(0).getIsPaid(); // true
		String postOrderStatus = postOrderList.get(0).getStatus(); // DELIVERED
		Integer postOrderItemCount = postOrderList.get(0).getItemsCount(); // 1
		BigDecimal postOrderTotalAmount = postOrderList.get(0).getTotal(); // 221000
		
		//Debt tab
		List<CustomerDebtRecord> postDebtList = customerDetailAPI.getDebtRecords(customerId);
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
		String postCashbookSourceType = workoutExpectedCashbookRecordSourceType(isDeliveryOpted, BigDecimal.valueOf(orderDetailsBeforeCheckout.getOrderInfo().getDebtAmount()));
		String postCashbookRecordBranch = postCashbookRecords.get(0).getBranchName();
		BigDecimal postCashbookRecordAmount = postCashbookRecords.get(0).getAmount();
		String postCashbookRecordPaymentMethod = postCashbookRecords.get(0).getPaymentMethod();
		
		
		/** Assertions **/
		//Earning points
		Assert.assertEquals(postEarningPoints, expectedEarningPoints, "Earning points");
		
		//Order summary
		Assert.assertEquals(postTotalOrderCount, expectedTotalOrderCount, "Total order count");
		Assert.assertTrue(postTotalPurchase.compareTo(expectedTotalPurchase) == 0, "Total purchase expected: " + expectedTotalPurchase + ", but got: " + postTotalPurchase);
		Assert.assertTrue(postTotalPurchaseLast3Months.compareTo(expectedTotalPurchaseLast3Months) == 0, "Total purchase last 3 months expected: " + expectedTotalPurchaseLast3Months + ", but got: " + postTotalPurchaseLast3Months);
		Assert.assertTrue(postAverageOrderValue.compareTo(expectedAverageOrderValue) == 0, "Average order value: " + expectedAverageOrderValue + ", but got: " + postAverageOrderValue);
		Assert.assertTrue(postDebtAmount.compareTo(expectedDebtAmount) == 0, "Debt amount expected: " + expectedDebtAmount + ", but got: " + postDebtAmount);
		
		//Order tab
		Assert.assertNotEquals(postFirstOrderId, firstOrderId, "Latest order record id");
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
			Assert.assertTrue(postDebtRecordAmount.compareTo(expectedDebtRecordAmount) == 0, "Debt record amount: " + expectedDebtRecordAmount + ", but got: " + postDebtRecordAmount);
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
		if (receivedAmount >0) {
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
