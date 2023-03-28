import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.cashbook.CashbookAPI;
import api.dashboard.cashbook.OthersGroupAPI;
import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.SupplierAPI;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StaffAPI;
import pages.dashboard.cashbook.Cashbook;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import utilities.PropertiesUtil;
import utilities.jsonFileUtility;

public class CashbookTest extends BaseTest {

	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;

	List<String> customerList;
	List<String> supplierList;
	List<String> staffList;
	List<String> othersList;
	List<String> branchList;
	List<String> transactionIdList;
	
	String displayLanguage = "ENG";
	String amount = "2000";
	String note = "Simply a note";

	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String username = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String password = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String country = sellerData.findValue("seller").findValue("mail").findValue("country").asText();

	@BeforeClass
	public void getDataByAPI() throws InterruptedException {
        new Login().loginToDashboardByMail(username, password);
        customerList = new Customers().getAllCustomerNames();
        supplierList = new SupplierAPI().getAllSupplierNames();
        staffList = new StaffAPI().getAllStaffNames();
        othersList = new OthersGroupAPI().getAllOtherGroupNames();
        new BranchManagement().getBranchInformation();
        branchList = BranchManagement.apiActiveBranches;
        transactionIdList = new CashbookAPI().getAllTransactionCodes();
	}	
	
	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginPage = new LoginPage(driver);
		cashbookPage = new Cashbook(driver);
		homePage = new HomePage(driver);
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
	
	public String randomTransactionId() {
		return getRandomListElement(transactionIdList);
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
	 * @param displayLanguage VIE/ENG
	 * @return
	 * @throws Exception
	 */
	public String senderGroup(String group, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.group." + group, displayLanguage);
	}
	
	/**
	 * 
	 * @param revenue debtCollectionFromSupplier/debtCollectionFromCustomer/paymentForOrder/saleOfAssets/otherIncome
	 * @param displayLanguage VIE/ENG
	 * @return revenue text according to VIE/ENG
	 * @throws Exception
	 */
	public String revenueSource(String revenue, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source." + revenue, displayLanguage);
	}

	/**
	 * 
	 * @param displayLanguage
	 * @return a list of revenue sources
	 * @throws Exception
	 */
	public String[] revenueSourceList(String displayLanguage) throws Exception {
		String[] list = {
				revenueSource("debtCollectionFromSupplier", displayLanguage),
				revenueSource("debtCollectionFromCustomer", displayLanguage),
				revenueSource("paymentForOrder", displayLanguage),
				revenueSource("saleOfAssets", displayLanguage),
				revenueSource("otherIncome", displayLanguage),
		};
		return list;
	}

	/**
	 * 
	 * @param expense paymentToShippingPartner/paymentForGoods/productionCost/costOfRawMaterials/debtPaymentToCustomer/rentalFee/utilities/salaries/sellingExpenses/otherCosts/refund
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String expenseType(String expense, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense." + expense, displayLanguage);
	}	
	
	/**
	 * 
	 * @param displayLanguage
	 * @return a list of expense types
	 * @throws Exception
	 */
	public String[] expenseTypeList(String displayLanguage) throws Exception {
		String[] list = {
				expenseType("paymentToShippingPartner",displayLanguage),
				expenseType("paymentForGoods",displayLanguage),
				expenseType("productionCost",displayLanguage),
				expenseType("costOfRawMaterials",displayLanguage),
				expenseType("debtPaymentToCustomer",displayLanguage),
				expenseType("rentalFee",displayLanguage),
				expenseType("utilities",displayLanguage),
				expenseType("salaries",displayLanguage),
				expenseType("sellingExpenses",displayLanguage),
				expenseType("otherCosts",displayLanguage),
				expenseType("refund",displayLanguage),
		};
		return list;
	}

	/**
	 * 
	 * @param method visa/atm/bankTransfer/cash/zalopay/momo
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String paymentMethod(String method, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod." + method, displayLanguage);
	}		
	
	public String randomPaymentMethod(String displayLanguage) throws Exception {
		String[] paymentMethod = { 
				paymentMethod("visa", displayLanguage),
				paymentMethod("atm", displayLanguage),
				paymentMethod("bankTransfer", displayLanguage),
				paymentMethod("cash", displayLanguage),
				paymentMethod("zalopay", displayLanguage),
				paymentMethod("momo", displayLanguage),
		};
		return paymentMethod[new Random().nextInt(0, paymentMethod.length)];
	}

	/**
	 * Allow accounting or not
	 * @param yesOrNo yes/no
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String allowAccounting(String yesOrNo, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.accountChecked." + yesOrNo, displayLanguage);
	}	
	
	/**
	 * 
	 * @param transactionType allExpenses/allRevenues
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String transactions(String transactionType, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.transaction." + transactionType, displayLanguage);
	}	
	
	/**
	 * 
	 * @param staff system/shopOwner
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String createdBy(String staff, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("cashbook.filter.createdBy." + staff, displayLanguage);
	}	
	
	public void verifySummaryDataAfterReceiptCreated(List<Long> originalSummary, List<Long> laterSummary, boolean isAccountingChecked) {
		Long revenue = (isAccountingChecked) ? originalSummary.get(1) + Long.parseLong(amount) : originalSummary.get(1);
		Assert.assertEquals(laterSummary.get(1), revenue, "Revenue");
		Assert.assertEquals(laterSummary.get(2), originalSummary.get(2), "Expenditure");
		Assert.assertEquals(laterSummary.get(3), laterSummary.get(0) + laterSummary.get(1) - laterSummary.get(2),
				"Ending Opening");
	}

	public void verifySummaryDataAfterPaymentCreated(List<Long> originalSummary, List<Long> laterSummary, boolean isAccountingChecked) {
		Long expenditure = (isAccountingChecked) ? originalSummary.get(2) + Long.parseLong(amount) : originalSummary.get(2);
		Assert.assertEquals(laterSummary.get(1), originalSummary.get(1), "Revenue");
		Assert.assertEquals(laterSummary.get(2), expenditure, "Expenditure");
		Assert.assertEquals(laterSummary.get(3), laterSummary.get(0) + laterSummary.get(1) - laterSummary.get(2),
				"Ending Opening");
	}

	public void verifyRecordDataAfterReceiptCreated(List<List<String>> records, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(records.get(0).get(2), branch, "Branch");
		Assert.assertEquals(records.get(0).get(3), source, "Revenue type");
		Assert.assertEquals(records.get(0).get(4), "-", "Expense type");
		Assert.assertEquals(records.get(0).get(5), sender, "Sender");
		Assert.assertEquals(extractDigits(records.get(0).get(7)), amount, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<List<String>> records, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(records.get(0).get(2), branch, "Branch");
		Assert.assertEquals(records.get(0).get(3), "-", "Revenue type");
		Assert.assertEquals(records.get(0).get(4), source, "Expense type");
		Assert.assertEquals(records.get(0).get(5), sender, "Sender");
		Assert.assertEquals(extractDigits(records.get(0).get(7)), amount, "Amount");
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
	public void CB_01_CheckTranslation() throws Exception {
		
		String group = senderGroup("customer", displayLanguage);
		String sender = randomCustomer();
		String branch = randomBranch();
		boolean isAccountingChecked = true;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Verify text at cashbook management screen */
		cashbookPage.navigate().verifyTextAtCashbookManagementScreen(displayLanguage);
		
		/* Verify text at create receipt screen */
		cashbookPage.clickCreateReceiptBtn().verifyTextAtCreateReceiptScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		cashbookPage.createReceipt(group, revenueSourceList(displayLanguage)[0], branch, randomPaymentMethod(displayLanguage), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		List<List<String>> records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
		cashbookPage.verifyTextAtReceiptTransactionIDScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		
		/* Verify text at create payment screen */
		cashbookPage.clickCreatePaymentBtn().verifyTextAtCreatePaymentScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		cashbookPage.createPayment(group, expenseTypeList(displayLanguage)[0], branch, randomPaymentMethod(displayLanguage), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
		cashbookPage.verifyTextAtPaymentTransactionIDScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		
		/* Verify text at filter container screen */
		cashbookPage.clickFilterBtn();
		cashbookPage.verifyTextAtFilterContainer(displayLanguage);
		cashbookPage.clickFilterDoneBtn();
	}	
	
	@Test
	public void CB_02_CreateReceiptWhenSenderGroupIsCustomer() throws Exception {

		String group = senderGroup("customer", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList(displayLanguage)) {
			String sender = randomCustomer();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating receipts
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterReceiptCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_03_CreateReceiptWhenSenderGroupIsSupplier() throws Exception {

		String group = senderGroup("supplier", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList(displayLanguage)) {
			String sender = randomSupplier();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating receipts
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterReceiptCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_04_CreateReceiptWhenSenderGroupIsStaff() throws Exception {

		String group = senderGroup("staff", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList(displayLanguage)) {
			String sender = randomStaff();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating receipts
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterReceiptCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_05_CreateReceiptWhenSenderGroupIsOthers() throws Exception {

		String group = senderGroup("others", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSourceList(displayLanguage)) {
			String sender = randomOthers();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating receipts
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterReceiptCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_06_CreatePaymentWhenRecipientGroupIsCustomer() throws Exception {

		String group = senderGroup("customer", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList(displayLanguage)) {
			String sender = randomCustomer();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating payments
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterPaymentCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_07_CreatePaymentWhenRecipientGroupIsSupplier() throws Exception {

		String group = senderGroup("supplier", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList(displayLanguage)) {
			String sender = randomSupplier();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating payments
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterPaymentCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_08_CreatePaymentWhenRecipientGroupIsStaff() throws Exception {

		String group = senderGroup("staff", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList(displayLanguage)) {
			String sender = randomStaff();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating payments
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterPaymentCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}

	@Test
	public void CB_09_CreatePaymentWhenRecipientGroupIsOthers() throws Exception {

		String group = senderGroup("others", displayLanguage);
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseTypeList(displayLanguage)) {
			String sender = randomOthers();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Long> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Long> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, isAccountingChecked);

			// Check record data after creating payments
			List<List<String>> records = cashbookPage.getRecords();
			verifyRecordDataAfterPaymentCreated(records, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}
	
	@Test
	public void CB_10_SearchRecords() throws Exception {
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		cashbookPage.navigate();
		cashbookPage.clickResetDateRangerPicker();
		
		/* Search random transaction ids */
		for (int i=0; i<3; i++) {
			String transactionId = randomTransactionId();
			
			cashbookPage.inputCashbookSearchTerm(transactionId);
			
			List<List<String>> searchedRecords = cashbookPage.getRecords();
			
			/* Click on the searched record */
			Assert.assertEquals(searchedRecords.size(), 1, "Number of found records");
			Assert.assertEquals(searchedRecords.get(0).get(0), transactionId, "Transaction Code");	
		}
	}
	
	@Test
	public void CB_11_FilterCashbook() throws Exception {
		
		List<List<String>> records;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Filter */
		cashbookPage.navigate();
		
		/* Filter by Accounting*/
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredAccounting(allowAccounting("no", displayLanguage));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.isAccountingChecked(), false);
			cashbookPage.clickCancelBtn();
		}
		
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredAccounting(allowAccounting("yes", displayLanguage));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.isAccountingChecked(), true);
			cashbookPage.clickCancelBtn();
		}
		
		
		/* Filter by branch*/
		String branch = getRandomListElement(new api.dashboard.cashbook.CashbookAPI().getAllRecordJsonPath().getList("branchName"));
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
			Assert.assertEquals(record.get(2), branch);
		}
		
		/* Filter by transaction */
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allExpenses", displayLanguage));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(3), "-");
		}
		
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allRevenues", displayLanguage));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(4), "-");
		}
		
		
		/* Filter by Expense type */
		String filteredExpenseType = expenseType("productionCost", displayLanguage);
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
			Assert.assertEquals(record.get(4), filteredExpenseType);
		}
		
		/* Filter by Revenue type */
		String filteredRevenueType = revenueSource("saleOfAssets", displayLanguage); 
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
			Assert.assertEquals(record.get(3), filteredRevenueType);
		}
		
		/* Filter by Created by */
		String filteredStaff = "Staff D"; 
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
			Assert.assertEquals(record.get(6), filteredStaff);
		}
		
		filteredStaff = createdBy("shopOwner", displayLanguage); 
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(6), filteredStaff);
		}
		
		
		/* Filter by Sender/Recipient Group */
		String filteredGroup = senderGroup("customer", displayLanguage); 
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
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			cashbookPage.clickCancelBtn();
		}
		
		filteredGroup = senderGroup("staff", displayLanguage);
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			cashbookPage.clickCancelBtn();
		}
		
		
		/* Filter by Sender/Recipient Name */
		filteredGroup = senderGroup("customer", displayLanguage);
		String filteredName = "Anh Le"; 
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
			Assert.assertEquals(record.get(5), filteredName);
		}
		
		filteredGroup = senderGroup("staff", displayLanguage);
		filteredName = "Staff A"; 
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		for (List<String> record : records) {
			Assert.assertEquals(record.get(5), filteredName);
		}
		

		/* Filter by Payment methods */
		String filteredPaymentMethod = paymentMethod("visa", displayLanguage); 
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
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
		
	}
	
	@Test
	public void CB_12_CombineFilterConditions() throws Exception {
		
		List<List<String>> records;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Combine filter conditions */
		// 1-st combination of filter conditions
		String branch = "My Shop Vietnam - Thu Duc Location"; 
		String accounting = allowAccounting("no", displayLanguage);
		String filteredTransaction = transactions("allExpenses", displayLanguage);
		String filteredExpenseType = expenseType("rentalFee", displayLanguage); 
		String filteredStaff = "Staff B";
		String filteredGroup = senderGroup("customer", displayLanguage);
		String filteredName = "Anh Le"; 
		String filteredPaymentMethod = paymentMethod("zalopay", displayLanguage); 
		boolean isAccountingChecked = (accounting.contentEquals(allowAccounting("yes", displayLanguage))) ? true : false;
		
		cashbookPage.navigate();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.selectFilteredTransaction(filteredTransaction);
		cashbookPage.selectFilteredExpenseType(filteredExpenseType);
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0, "Number of found records");
		for (List<String> record : records) {
			Assert.assertEquals(record.get(2), branch);
			Assert.assertEquals(record.get(3), "-");
			Assert.assertEquals(record.get(4), filteredExpenseType);
			Assert.assertEquals(record.get(5), filteredName);
			Assert.assertEquals(record.get(6), filteredStaff);
			
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
		
		// 2-nd combination of filter conditions
		filteredTransaction = transactions("allRevenues", displayLanguage);
		filteredExpenseType = revenueSource("debtCollectionFromSupplier", displayLanguage); 
		
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.selectFilteredTransaction(filteredTransaction);
		cashbookPage.selectFilteredRevenueType(filteredExpenseType);
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0, "Number of found records");
		for (List<String> record : records) {
			Assert.assertEquals(record.get(2), branch);
			Assert.assertEquals(record.get(3), filteredExpenseType);
			Assert.assertEquals(record.get(4), "-");
			Assert.assertEquals(record.get(5), filteredName);
			Assert.assertEquals(record.get(6), filteredStaff);
			
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
		
		// 3-rd combination of filter conditions
		branch = "CN3"; 
		accounting = allowAccounting("yes", displayLanguage);
		filteredTransaction = transactions("allExpenses", displayLanguage);
		filteredExpenseType = expenseType("costOfRawMaterials", displayLanguage); 
		filteredStaff = "Staff B";
		filteredGroup = senderGroup("supplier", displayLanguage);
		filteredName = "Tín Cường"; 
		filteredPaymentMethod = paymentMethod("atm", displayLanguage); 
		isAccountingChecked = (accounting.contentEquals(allowAccounting("yes", displayLanguage))) ? true : false;
		
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.selectFilteredTransaction(filteredTransaction);
		cashbookPage.selectFilteredExpenseType(filteredExpenseType);
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0, "Number of found records");
		for (List<String> record : records) {
			Assert.assertEquals(record.get(2), branch);
			Assert.assertEquals(record.get(3), "-");
			Assert.assertEquals(record.get(4), filteredExpenseType);
			Assert.assertEquals(record.get(5), filteredName);
			Assert.assertEquals(record.get(6), filteredStaff);
			
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
		
		// 4-th combination of filter conditions
		filteredTransaction = transactions("allRevenues", displayLanguage);
		filteredExpenseType = revenueSource("saleOfAssets", displayLanguage); 
		isAccountingChecked = (accounting.contentEquals(allowAccounting("yes", displayLanguage))) ? true : false;
		
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredBranch(branch);
		cashbookPage.selectFilteredAccounting(accounting);
		cashbookPage.selectFilteredTransaction(filteredTransaction);
		cashbookPage.selectFilteredRevenueType(filteredExpenseType);
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.selectFilteredGroup(filteredGroup);
		cashbookPage.selectFilteredName(filteredName);
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0, "Number of found records");
		for (List<String> record : records) {
			Assert.assertEquals(record.get(2), branch);
			Assert.assertEquals(record.get(3), filteredExpenseType);
			Assert.assertEquals(record.get(4), "-");
			Assert.assertEquals(record.get(5), filteredName);
			Assert.assertEquals(record.get(6), filteredStaff);
			cashbookPage.clickRecord(record.get(0));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
	}
}
