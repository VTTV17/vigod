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
import api.Seller.customers.APIAllCustomers;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StaffManagement;
import api.Seller.supplier.supplier.APISupplier;
import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.cashbook.CashbookExpense;
import utilities.enums.cashbook.CashbookGroup;
import utilities.enums.cashbook.CashbookPaymentMethod;
import utilities.enums.cashbook.CashbookRevenue;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import web.Dashboard.cashbook.Cashbook;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.pagination.Pagination;

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
		username = AccountTest.ADMIN_USERNAME_TIEN;
		password = AccountTest.ADMIN_PASSWORD_TIEN;
		country = AccountTest.ADMIN_COUNTRY_TIEN;
        loginInformation = new Login().setLoginInformation(username, password).getLoginInformation();
        customerList = new APIAllCustomers(loginInformation).getAllCustomerNames();
        supplierList = new APISupplier(loginInformation).getAllSupplierNames();
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
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	

	public void loginDashboard() {
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}    
	
	public String randomCustomer() {
        return DataGenerator.getRandomListElement(customerList);
	}		
	
	public String randomSupplier() {
		return DataGenerator.getRandomListElement(supplierList);
	}		
	
	public String randomStaff() {
		return DataGenerator.getRandomListElement(staffList);
	}		
	
	public String randomOthers() {
		return DataGenerator.getRandomListElement(othersList);
	}		

	public String randomBranch() {
		return DataGenerator.getRandomListElement(branchList);
	}		
	
	public boolean randomAccountingChecked() {
		return new Random().nextBoolean();
	}		
	
	public String randomTransactionId() {
		return DataGenerator.getRandomListElement(transactionIdList);
	}		

	public String randomAmount() {
		return String.valueOf(Math.round(DataGenerator.generatNumberInBound(1, 50))*1000);
	}	
	
	/**
	 * Extract numbers from a string
	 * @param rawAmount
	 * @return
	 */
	public String extractDigits(String rawAmount) {
		return rawAmount.replaceAll("\\D", "");
	}

	public String[] revenueSourceList() {
		return Arrays.stream(CashbookRevenue.values()).map(name -> CashbookRevenue.getTextByLanguage(name)).toArray(String[]::new);
	}

	public String[] expenseTypeList() {
		return Arrays.stream(CashbookExpense.values()).map(name -> CashbookExpense.getTextByLanguage(name)).toArray(String[]::new);
	}

	public String[] paymentMethodList() {
		return Arrays.stream(CashbookPaymentMethod.values()).map(name -> CashbookPaymentMethod.getTextByLanguage(name)).toArray(String[]::new);
	}	
	
	public String randomPaymentMethod() {
		return DataGenerator.getRandomListElement(Arrays.asList(paymentMethodList()));
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
		Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), source, "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), "-", "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_IDX), sender, "Sender");
		Assert.assertEquals(extractDigits(record.get(Cashbook.AMOUNT_IDX)), amount, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), "-", "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), source, "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_IDX), sender, "Sender");
		Assert.assertEquals(extractDigits(record.get(Cashbook.AMOUNT_IDX)), amount, "Amount");
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

//	@Test
	public void CB_01_CheckTranslation() throws Exception {
		
		String group = CashbookGroup.getLocalizedText(CashbookGroup.CUSTOMER);
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
		cashbookPage.clickRecord(records.get(0).get(Cashbook.ID_IDX)); // Click on the first record on the list.
		cashbookPage.verifyTextAtReceiptTransactionIDScreen();
		cashbookPage.clickCancelBtn();
		
		/* Verify text at create payment screen */
		cashbookPage.clickCreatePaymentBtn().verifyTextAtCreatePaymentScreen();
		cashbookPage.clickCancelBtn();
		cashbookPage.createPayment(group, expenseTypeList()[0], branch, randomPaymentMethod(), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(Cashbook.ID_IDX)); // Click on the first record on the list.
		cashbookPage.verifyTextAtPaymentTransactionIDScreen();
		cashbookPage.clickCancelBtn();
		
		/* Verify text at filter container screen */
		cashbookPage.clickFilterBtn();
		cashbookPage.verifyTextAtFilterContainer();
		cashbookPage.clickFilterDoneBtn();
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
		
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		
		String recordId = randomRecord.get(Cashbook.ID_IDX);
		String branch = randomRecord.get(Cashbook.BRANCH_IDX);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_IDX);
		String name = randomRecord.get(Cashbook.NAME_IDX);
		
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
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
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
			Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch);
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
			Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), "-");
		}
		
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allRevenues"));
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), "-");
		}
		
		/* Filter by Expense type */
		String filteredExpenseType = DataGenerator.getRandomListElement(Arrays.asList(expenseTypeList()));
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
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), filteredExpenseType);
		}
		
		/* Filter by Revenue type */
		String filteredRevenueType = DataGenerator.getRandomListElement(Arrays.asList(revenueSourceList())); 
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
			Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), filteredRevenueType);
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
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), filteredStaff);
		}
		
		filteredStaff = createdBy("system"); 
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.clickFilterDoneBtn();
		
		commonAction.sleepInMiliSecond(1000);
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), "system");
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
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
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
			Assert.assertEquals(record.get(Cashbook.NAME_IDX), filteredName);
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
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
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
		
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		
		String recordId = randomRecord.get(Cashbook.ID_IDX);
		String branch = randomRecord.get(Cashbook.BRANCH_IDX);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_IDX);
		String name = randomRecord.get(Cashbook.NAME_IDX);
		String revenue = randomRecord.get(Cashbook.REVENUETYPE_IDX);
		String expense = randomRecord.get(Cashbook.EXPENSETYPE_IDX);
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
			Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch);
			if (!revenue.contains("-")) {
				Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), revenue);
				Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), "-");
			} else {
				Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), "-");
				Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), expense);
			}
			
			Assert.assertEquals(record.get(Cashbook.NAME_IDX), name);
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), createdBy);
			
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
			Assert.assertEquals(cashbookPage.getGroup(), group);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), payment);
			cashbookPage.clickCancelBtn();
		}
	}
}
