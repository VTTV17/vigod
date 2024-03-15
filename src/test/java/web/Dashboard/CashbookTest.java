package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.cashbook.CashbookAPI;
import api.Seller.cashbook.OthersGroupAPI;
import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.supplier.supplier.SupplierAPI;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StaffManagement;

import web.Dashboard.cashbook.Cashbook;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.pagination.Pagination;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;

public class CashbookTest extends BaseTest {

	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;
	
	String username;
	String password;
	String country;

	List<String> customerList;
	List<String> supplierList;
	List<String> staffList;
	List<String> othersList;
	List<String> branchList;
	List<String> transactionIdList;
	
	LoginInformation loginInformation;

	@BeforeClass
	public void loadTestData() {
//		username = "hoa.phan.hong@mediastep.com";
//		password = "123456a@";
		username = AccountTest.ADMIN_USERNAME_TIEN;
		password = AccountTest.ADMIN_PASSWORD_TIEN;
		country = AccountTest.ADMIN_COUNTRY_TIEN;
        loginInformation = new Login().setLoginInformation(username, password).getLoginInformation();
        customerList = new Customers(loginInformation).getAllCustomerNames();
        supplierList = new SupplierAPI(loginInformation).getAllSupplierNames();
        staffList = new StaffManagement(loginInformation).getAllStaffNames();
        othersList = new OthersGroupAPI(loginInformation).getAllOtherGroupNames();
        branchList = new BranchManagement(loginInformation).getInfo().getActiveBranches();
        transactionIdList = new CashbookAPI(loginInformation).getAllTransactionCodes();
	}	

	@BeforeMethod
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		cashbookPage = new Cashbook(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	

	public void loginDashboard() {
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}    
    
	public String getRandomListElement(List<String> list) {
		return list.get(new Random().nextInt(0, list.size()));
	}
	
	public String randomCustomer() {
        return getRandomListElement(customerList);
	}		
	
	public String randomSupplier() {
		return getRandomListElement(supplierList);
	}		
	
	public String randomStaff() {
		return getRandomListElement(staffList);
	}		
	
	public String randomOthers() {
		return getRandomListElement(othersList);
	}		

	public String randomBranch() {
		return getRandomListElement(branchList);
	}		
	
	public boolean randomAccountingChecked() {
		return Boolean.parseBoolean(getRandomListElement(Arrays.asList("true", "false")));
	}		
	
	public String randomTransactionId() {
		return getRandomListElement(transactionIdList);
	}		

	public String randomAmount() {
		return String.valueOf(Math.round(generate.generatNumberInBound(1, 50))*1000);
	}	
	
	/**
	 * Extract numbers from a string
	 * @param rawAmount
	 * @return
	 */
	public String extractDigits(String rawAmount) {
		return rawAmount.replaceAll("\\D", "");
	}

	/**
	 * 
	 * @param group           customer/supplier/staff/others
	 * @return
	 * @throws Exception
	 */
	public String senderGroup(String group) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.group." + group);
	}
	
	/**
	 * 
	 * @param revenue debtCollectionFromSupplier/debtCollectionFromCustomer/paymentForOrder/saleOfAssets/otherIncome
	 * @return revenue text according to VIE/ENG
	 * @throws Exception
	 */
	public String revenueSource(String revenue) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source." + revenue);
	}

	/**
	 * 
	 * @return a list of revenue sources
	 * @throws Exception
	 */
	public String[] revenueSourceList() throws Exception {
		String[] list = {
				revenueSource("debtCollectionFromSupplier"),
				revenueSource("debtCollectionFromCustomer"),
				revenueSource("paymentForOrder"),
				revenueSource("saleOfAssets"),
				revenueSource("otherIncome"),
		};
		return list;
	}

	/**
	 * 
	 * @param expense paymentToShippingPartner/paymentForGoods/productionCost/costOfRawMaterials/debtPaymentToCustomer/rentalFee/utilities/salaries/sellingExpenses/otherCosts/refund
	 * @return
	 * @throws Exception
	 */
	public String expenseType(String expense) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense." + expense);
	}	
	
	/**
	 * 
	 * @return a list of expense types
	 * @throws Exception
	 */
	public String[] expenseTypeList() throws Exception {
		String[] list = {
				expenseType("paymentToShippingPartner"),
				expenseType("paymentForGoods"),
				expenseType("productionCost"),
				expenseType("costOfRawMaterials"),
				expenseType("debtPaymentToCustomer"),
				expenseType("rentalFee"),
				expenseType("utilities"),
				expenseType("salaries"),
				expenseType("sellingExpenses"),
				expenseType("otherCosts"),
				expenseType("refund"),
		};
		return list;
	}

	/**
	 * 
	 * @param method visa/atm/bankTransfer/cash/zalopay/momo
	 * @return
	 * @throws Exception
	 */
	public String paymentMethod(String method) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod." + method);
	}		

	/**
	 * @return a list of payment methods
	 * @throws Exception
	 */
	public String[] paymentMethodList() throws Exception {
		String[] paymentMethod = { 
				paymentMethod("visa"),
				paymentMethod("atm"),
				paymentMethod("bankTransfer"),
				paymentMethod("cash"),
				paymentMethod("zalopay"),
				paymentMethod("momo"),
		};
		return paymentMethod;
	}	
	
	public String randomPaymentMethod() throws Exception {
		return getRandomListElement(Arrays.asList(paymentMethodList()));
	}

	/**
	 * Allow accounting or not
	 * @param yesOrNo yes/no
	 * @return
	 * @throws Exception
	 */
	public String allowAccounting(String yesOrNo) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.accountChecked." + yesOrNo);
	}	
	
	/**
	 * 
	 * @param transactionType allExpenses/allRevenues
	 * @return
	 * @throws Exception
	 */
	public String transactions(String transactionType) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.transaction." + transactionType);
	}	
	
	/**
	 * 
	 * @param staff system/shopOwner
	 * @return
	 * @throws Exception
	 */
	public String createdBy(String staff) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.createdBy." + staff);
	}	
	
	public void verifySummaryDataAfterReceiptCreated(List<Long> originalSummary, List<Long> laterSummary, String amount, boolean isAccountingChecked) {
//		commonAction.sleepInMiliSecond(3000);
		Long revenue = (isAccountingChecked) ? originalSummary.get(Cashbook.TOTALREVENUE_IDX) + Long.parseLong(amount) : originalSummary.get(Cashbook.TOTALREVENUE_IDX);
		Assert.assertEquals(laterSummary.get(Cashbook.TOTALREVENUE_IDX), revenue, "Revenue");
		Assert.assertEquals(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX), originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX), "Expenditure");
		Assert.assertEquals(laterSummary.get(Cashbook.ENDINGBALANCE_IDX), laterSummary.get(Cashbook.OPENINGBALANCE_IDX) + laterSummary.get(Cashbook.TOTALREVENUE_IDX) - laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX),
				"Ending Opening");
	}

	public void verifySummaryDataAfterPaymentCreated(List<Long> originalSummary, List<Long> laterSummary, String amount, boolean isAccountingChecked) {
		Long expenditure = (isAccountingChecked) ? originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX) + Long.parseLong(amount) : originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX);
		Assert.assertEquals(laterSummary.get(Cashbook.TOTALREVENUE_IDX), originalSummary.get(Cashbook.TOTALREVENUE_IDX), "Revenue");
		Assert.assertEquals(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX), expenditure, "Expenditure");
		Assert.assertEquals(laterSummary.get(Cashbook.ENDINGBALANCE_IDX), laterSummary.get(Cashbook.OPENINGBALANCE_IDX) + laterSummary.get(Cashbook.TOTALREVENUE_IDX) - laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX),
				"Ending Opening");
	}

	public void verifyRecordDataAfterReceiptCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), source, "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), "-", "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_COL), sender, "Sender");
		Assert.assertEquals(extractDigits(record.get(Cashbook.AMOUNT_COL)), amount, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-", "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), source, "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_COL), sender, "Sender");
		Assert.assertEquals(extractDigits(record.get(Cashbook.AMOUNT_COL)), amount, "Amount");
	}

	public void verifyRecordDataOnTransactionIDPopup(String group, String sender, String source, String branch,
			String amount, String paymentMethod, String note, boolean isAccountingChecked) {
		Assert.assertEquals(cashbookPage.getGroup(), group, "Sender/Recipient group");
		Assert.assertEquals(cashbookPage.getName(), sender, "Sender/Recipient name");
		Assert.assertEquals(cashbookPage.getSourceOrExpense(), source, "Revenue/Expense");
		Assert.assertEquals(cashbookPage.getBranch(), branch, "Branch");
		Assert.assertEquals(extractDigits(cashbookPage.getAmount()), amount, "Amount");
		Assert.assertEquals(cashbookPage.getPaymentMethod(), paymentMethod, "Payment method");
		Assert.assertEquals(cashbookPage.getNote(), note, "Note");
		Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked, "Accounting");
	}

	@Test
	public void CB_00_CheckRevenueExpensePaymentDropdownValues() throws Exception {
		
		loginDashboard();
		
		cashbookPage.navigate();
		
		cashbookPage.clickCreateReceiptBtn();
		
		String[] expected = revenueSourceList();
		String[] temp = cashbookPage.getSourceDropdownValues(); //Create a new array by omitting the first element of a particular array
		String[] actual = Arrays.copyOfRange(temp, 1, temp.length);
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Source list");
        
        expected = paymentMethodList();
        actual = cashbookPage.getPaymentMethodDropdownValues();
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
        
        cashbookPage.clickCancelBtn();
        
        cashbookPage.clickCreatePaymentBtn();
        
		expected = expenseTypeList();
		temp = cashbookPage.getSourceDropdownValues(); //Create a new array by omitting the first element of a particular array
		actual = Arrays.copyOfRange(temp, 1, temp.length);
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Expense list");
        
        expected = paymentMethodList();
        actual = cashbookPage.getPaymentMethodDropdownValues();
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
        
	}		
	
	@Test
	public void CB_01_CheckTranslation() throws Exception {
		
		String group = senderGroup("customer");
		String sender = randomCustomer();
		String branch = randomBranch();
		boolean isAccountingChecked = randomAccountingChecked();
		String amount = randomAmount();
		String note = "%s %s".formatted(sender, amount);
		
		/* Log into dashboard */
		loginDashboard();
		
		/* Verify text at cashbook management screen */
		cashbookPage.navigate().verifyTextAtCashbookManagementScreen();
		
		/* Verify text at create receipt screen */
		cashbookPage.clickCreateReceiptBtn().verifyTextAtCreateReceiptScreen();
		cashbookPage.clickCancelBtn();
		cashbookPage.createReceipt(group, revenueSourceList()[0], branch, randomPaymentMethod(), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		List<List<String>> records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
		cashbookPage.verifyTextAtReceiptTransactionIDScreen();
		cashbookPage.clickCancelBtn();
		
		/* Verify text at create payment screen */
		cashbookPage.clickCreatePaymentBtn().verifyTextAtCreatePaymentScreen();
		cashbookPage.clickCancelBtn();
		cashbookPage.createPayment(group, expenseTypeList()[0], branch, randomPaymentMethod(), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
		cashbookPage.verifyTextAtPaymentTransactionIDScreen();
		cashbookPage.clickCancelBtn();
		
		/* Verify text at filter container screen */
		cashbookPage.clickFilterBtn();
		cashbookPage.verifyTextAtFilterContainer();
		cashbookPage.clickFilterDoneBtn();
	}	
	
	@Test
	public void CB_02_CreateReceiptWhenSenderGroupIsCustomer() throws Exception {

		String group = senderGroup("customer");

		/* Log into dashboard */
		loginDashboard();

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomCustomer();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating receipts
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_03_CreateReceiptWhenSenderGroupIsSupplier() throws Exception {

		String group = senderGroup("supplier");

		/* Log into dashboard */
		loginDashboard();

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomSupplier();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating receipts
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_04_CreateReceiptWhenSenderGroupIsStaff() throws Exception {

		String group = senderGroup("staff");

		/* Log into dashboard */
		loginDashboard();

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomStaff();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating receipts
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_05_CreateReceiptWhenSenderGroupIsOthers() throws Exception {

		String group = senderGroup("others");

		/* Log into dashboard */
		loginDashboard();

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomOthers();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating receipts
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_06_CreatePaymentWhenRecipientGroupIsCustomer() throws Exception {

		String group = senderGroup("customer");

		/* Log into dashboard */
		loginDashboard();

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomCustomer();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating payments
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);
			
			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_07_CreatePaymentWhenRecipientGroupIsSupplier() throws Exception {

		String group = senderGroup("supplier");

		/* Log into dashboard */
		loginDashboard();

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomSupplier();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating payments
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_08_CreatePaymentWhenRecipientGroupIsStaff() throws Exception {

		String group = senderGroup("staff");

		/* Log into dashboard */
		loginDashboard();

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomStaff();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating payments
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_09_CreatePaymentWhenRecipientGroupIsOthers() throws Exception {

		String group = senderGroup("others");

		/* Log into dashboard */
		loginDashboard();

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomOthers();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2500);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating payments
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}
	
	@Test
	public void CB_10_SearchRecords() throws Exception {
		
		/* Log into dashboard */
		loginDashboard();
		
		cashbookPage.navigate();
		cashbookPage.clickResetDateRangerPicker();
		
		/* Search random transaction ids */
		for (int i=0; i<3; i++) {
			String transactionId = randomTransactionId();
			
			cashbookPage.inputCashbookSearchTerm(transactionId);
			
			List<List<String>> searchedRecords = cashbookPage.getRecords();
			
			/* Click on the searched record */
			Assert.assertEquals(searchedRecords.size(), 1, "Number of found records");
			Assert.assertEquals(searchedRecords.get(0).get(Cashbook.TRANSACTIONCODE_COL), transactionId, "Transaction Code");	
			cashbookPage.inputCashbookSearchTerm("DFRT");
		}
	}
	
	@Test
	public void CB_11_FilterCashbook() throws Exception {
		
		List<List<String>> records;
		
		/* Log into dashboard */
		loginDashboard();
		
		/* Filter */
		cashbookPage.navigate();
		
		cashbookPage.clickResetDateRangerPicker();
		
		records = cashbookPage.getRecords();
		
		List<String> randomRecord = records.get(new Random().nextInt(0, records.size()));
		
		String recordId = randomRecord.get(Cashbook.TRANSACTIONCODE_COL);
		String branch = randomRecord.get(Cashbook.BRANCH_COL);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_COL);
		String name = randomRecord.get(Cashbook.NAME_COL);
		
		cashbookPage.clickRecord(recordId);
		
		boolean expectedAccounting = cashbookPage.isAccountingChecked();
		String accounting = (expectedAccounting) ? allowAccounting("yes"):allowAccounting("no");
		String group = cashbookPage.getGroup();
		String payment = cashbookPage.getPaymentMethod();
		
		cashbookPage.clickCancelBtn();
		
		/* Filter by Accounting*/
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
			cashbookPage.clickCancelBtn();
		}
		
		/* Filter by branch*/
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch);
		}
		
		/* Filter by transaction */
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allExpenses"));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-");
		}
		
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allRevenues"));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), "-");
		}
		
		/* Filter by Expense type */
		String filteredExpenseType = getRandomListElement(Arrays.asList(expenseTypeList()));
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredExpenseType(filteredExpenseType);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), filteredExpenseType);
		}
		
		/* Filter by Revenue type */
		String filteredRevenueType = getRandomListElement(Arrays.asList(revenueSourceList())); 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredRevenueType(filteredRevenueType);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), filteredRevenueType);
		}
		
		/* Filter by Created by */
		String filteredStaff = createdBy; 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_COL), filteredStaff);
		}
		
		filteredStaff = createdBy("system"); 
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_COL), "system");
		}
		
		/* Filter by Sender/Recipient Group */
		String filteredGroup = group; 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			cashbookPage.clickCancelBtn();
		}
		
		/* Filter by Sender/Recipient Name */
		filteredGroup = group;
		String filteredName = name; 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.NAME_COL), filteredName);
		}
		
		/* Filter by Payment methods */
		String filteredPaymentMethod = payment; 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
	}
	
	@Test
	public void CB_12_CombineFilterConditions() throws Exception {
		
		List<List<String>> records;
		
		/* Log into dashboard */
		loginDashboard();
		
		cashbookPage.navigate();
		cashbookPage.clickResetDateRangerPicker();
		records = cashbookPage.getRecords();
		new Pagination(driver).clickNextBtn();
		commonAction.sleepInMiliSecond(1000);
		records.addAll(cashbookPage.getRecords());
		new Pagination(driver).clickPreviousBtn();
		
		List<String> randomRecord = records.get(new Random().nextInt(0, records.size()));
		
		String recordId = randomRecord.get(Cashbook.TRANSACTIONCODE_COL);
		String branch = randomRecord.get(Cashbook.BRANCH_COL);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_COL);
		String name = randomRecord.get(Cashbook.NAME_COL);
		String revenue = randomRecord.get(Cashbook.REVENUETYPE_COL);
		String expense = randomRecord.get(Cashbook.EXPENSETYPE_COL);
		String transaction = !revenue.contains("-") ? transactions("allRevenues") : transactions("allExpenses");
		cashbookPage.inputCashbookSearchTerm(recordId);
		cashbookPage.clickRecord(recordId);
		boolean expectedAccounting = cashbookPage.isAccountingChecked();
		String accounting = (expectedAccounting) ? allowAccounting("yes"):allowAccounting("no");
		String group = cashbookPage.getGroup();
		String payment = cashbookPage.getPaymentMethod();
		cashbookPage.clickCancelBtn();
		
		/* Combine filter conditions */
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.selectFilteredTransaction(transaction);
		if (!revenue.contains("-")) {
			cashbookPage.selectFilteredRevenueType(revenue);
		} else {
			cashbookPage.selectFilteredExpenseType(expense);
		}
		cashbookPage.selectFilteredCreatedBy(createdBy);
		cashbookPage.selectFilteredGroup(group);
		cashbookPage.selectFilteredName(name);
		cashbookPage.selectFilteredPaymentMethod(payment);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0, "Number of found records");
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch);
			if (!revenue.contains("-")) {
				Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), revenue);
				Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), "-");
			} else {
				Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-");
				Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), expense);
			}
			
			Assert.assertEquals(record.get(Cashbook.NAME_COL), name);
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_COL), createdBy);
			
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getGroup(), group);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), payment);
			cashbookPage.clickCancelBtn();
		}
	}
}
