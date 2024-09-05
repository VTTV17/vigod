package web.Dashboard;

import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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
import utilities.enums.Domain;
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

/**
 * Some test scenarios are commented out in this file due to their complex nature:
 * - Check payment dropdown values in Create Receipt/Payment dialog
 * - Filter records by Sender/Receiver name
 * Before running this test, make sure the preconditions below are met:
 * - The branch is active
 * - There exist some customers and suppliers and staff and some Others values
 */

public class RefactoredCashbookTest extends BaseTest {

	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;

	List<String> customerList;
	List<String> supplierList;
	List<String> staffList;
	List<String> othersList;
	List<String> branchList;
	List<String> transactionIdList;
	
	CashbookAPI cashbookAPI;

	@BeforeClass
	public void loadTestData() {
		
		String country, username, password;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			country = ADMIN_COUNTRY_TIEN;
			username = ADMIN_USERNAME_TIEN;
			password = ADMIN_PASSWORD_TIEN;
		} else {
			country = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			username = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			password = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		
		LoginInformation loginInformation = new Login().setLoginInformation(username, password).getLoginInformation();
        customerList = new APIAllCustomers(loginInformation).getAllCustomerNames();
        supplierList = new APISupplier(loginInformation).getAllSupplierNames();
        staffList = new StaffManagement(loginInformation).getAllStaffNames();
        othersList = new OthersGroupAPI(loginInformation).getAllOtherGroupNames();
        branchList = new BranchManagement(loginInformation).getInfo().getActiveBranches();
        
        cashbookAPI = new CashbookAPI(loginInformation);
        transactionIdList = cashbookAPI.getAllTransactionCodes();
        
        driver = new InitWebdriver().getDriver(browser, headless);
        loginPage = new LoginPage(driver);
        cashbookPage = new Cashbook(driver, Domain.valueOf(domain));
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();

		navigateToPage(Domain.valueOf(domain));
		loginPage.performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear1().hideFacebookBubble();
		homePage.verifyPageLoaded();
		cashbookPage.navigateUsingURL();
	}	

	void navigateToPage(Domain domain) {
		switch (domain) {
			case VN -> loginPage.navigate().selectDisplayLanguage(language);
			case BIZ -> loginPage.navigateBiz();
			default -> throw new IllegalArgumentException("Unexpected value: " + domain);
		}
	}		
	
	@BeforeMethod
	public void navigateToPage() {
		commonAction.refreshPage();
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	

    @AfterClass
    public void afterClass() {
        tearDownWeb();
    }    
	
	public List<BigDecimal> waitTillOK() {
		
		List<BigDecimal> apiSummary = cashbookAPI.getCasbookSummary();
		
		for (int i=0; i<10; i++) {
			List<BigDecimal> uiSummary = cashbookPage.getCashbookSummaryBig();
			
			boolean matched = false;
			for (int j=0; j<apiSummary.size(); j++) {
				if (apiSummary.get(j).compareTo(uiSummary.get(j)) !=0) {
					matched = false;
					break;
				};
				matched = true;
			}
			
			if (matched) return uiSummary;
			commonAction.sleepInMiliSecond(500, "Wait OMG");
		}
		return cashbookPage.getCashbookSummaryBig();
	}  
	
	public String randomSender(CashbookGroup group) {
		return switch (group) {
			case CUSTOMER: yield DataGenerator.getRandomListElement(customerList);
			case SUPPLIER: yield DataGenerator.getRandomListElement(supplierList);
			case STAFF: yield DataGenerator.getRandomListElement(staffList);
			default: yield DataGenerator.getRandomListElement(othersList);
		};
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
		return String.valueOf(Math.round(DataGenerator.generatNumberInBound(1, 50))*10);
	}	
	
	public String[] revenueSources(CashbookGroup group) {
		if (group != CashbookGroup.SUPPLIER) {
			return Arrays.stream(CashbookRevenue.values()).map(name -> CashbookRevenue.getTextByLanguage(name)).toArray(String[]::new);
		}
		return Arrays.stream(CashbookRevenue.values()).filter(name -> name!=CashbookRevenue.DEBT_COLLECTION_FROM_SUPPLIER).map(name -> CashbookRevenue.getTextByLanguage(name)).toArray(String[]::new);
	}
	
	public String[] expenseSources(CashbookGroup group) {
		if (group != CashbookGroup.SUPPLIER) {
			return Arrays.stream(CashbookExpense.values()).map(name -> CashbookExpense.getTextByLanguage(name)).toArray(String[]::new);
		}
		return Arrays.stream(CashbookExpense.values()).filter(name -> name!=CashbookExpense.DEBT_COLLECTION_FROM_SELLER).map(name -> CashbookExpense.getTextByLanguage(name)).toArray(String[]::new);
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
	
	public void verifySummaryDataAfterReceiptCreated(List<BigDecimal> originalSummary, List<BigDecimal> laterSummary, String amount, boolean isAccountingChecked) {
	    BigDecimal revenue = (isAccountingChecked) ? originalSummary.get(Cashbook.TOTALREVENUE_IDX).add(new BigDecimal(amount)) : originalSummary.get(Cashbook.TOTALREVENUE_IDX);
	    
	    Assert.assertTrue(laterSummary.get(Cashbook.TOTALREVENUE_IDX).compareTo(revenue) == 0, 
	        "Revenue mismatch: expected " + revenue + " but found " + laterSummary.get(Cashbook.TOTALREVENUE_IDX));
	    Assert.assertTrue(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX).compareTo(originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX)) == 0, 
	        "Expenditure mismatch: expected " + originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX) + " but found " + laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX));
	    
	    BigDecimal expectedEndingBalance = laterSummary.get(Cashbook.OPENINGBALANCE_IDX)
	        .add(laterSummary.get(Cashbook.TOTALREVENUE_IDX))
	        .subtract(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX));
	    
	    Assert.assertTrue(laterSummary.get(Cashbook.ENDINGBALANCE_IDX).compareTo(expectedEndingBalance) == 0, 
	        "Ending Balance mismatch: expected " + expectedEndingBalance + " but found " + laterSummary.get(Cashbook.ENDINGBALANCE_IDX));
	}	
	
	public void verifySummaryDataAfterPaymentCreated(List<BigDecimal> originalSummary, List<BigDecimal> laterSummary, String amount, boolean isAccountingChecked) {
	    BigDecimal expenditure = (isAccountingChecked) ? originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX).add(new BigDecimal(amount)) : originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX);
	    
	    Assert.assertTrue(laterSummary.get(Cashbook.TOTALREVENUE_IDX).compareTo(originalSummary.get(Cashbook.TOTALREVENUE_IDX)) == 0, 
	        "Revenue mismatch: expected " + originalSummary.get(Cashbook.TOTALREVENUE_IDX) + " but found " + laterSummary.get(Cashbook.TOTALREVENUE_IDX));
	    Assert.assertTrue(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX).compareTo(expenditure) == 0, 
	        "Expenditure mismatch: expected " + expenditure + " but found " + laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX));
	    
	    BigDecimal expectedEndingBalance = laterSummary.get(Cashbook.OPENINGBALANCE_IDX)
	        .add(laterSummary.get(Cashbook.TOTALREVENUE_IDX))
	        .subtract(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX));
	    
	    Assert.assertTrue(laterSummary.get(Cashbook.ENDINGBALANCE_IDX).compareTo(expectedEndingBalance) == 0, 
	        "Ending Balance mismatch: expected " + expectedEndingBalance + " but found " + laterSummary.get(Cashbook.ENDINGBALANCE_IDX));
	}

	public void verifyRecordDataAfterReceiptCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), source, "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), "-", "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_COL), sender, "Sender");
		Assert.assertTrue(new BigDecimal(DataGenerator.extractDigits(record.get(Cashbook.AMOUNT_COL))).compareTo(new BigDecimal(amount))==0, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-", "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), source, "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_COL), sender, "Sender");
		Assert.assertTrue(new BigDecimal(DataGenerator.extractDigits(record.get(Cashbook.AMOUNT_COL))).compareTo(new BigDecimal(amount))==0, "Amount");
	}

	public void verifyRecordDataOnTransactionIDPopup(String group, String sender, String source, String branch,
			String amount, String paymentMethod, String note, boolean isAccountingChecked) {
		Assert.assertEquals(cashbookPage.getGroup(), group, "Sender/Recipient group");
		Assert.assertEquals(cashbookPage.getName(), sender, "Sender/Recipient name");
		Assert.assertEquals(cashbookPage.getSourceOrExpense(), source, "Revenue/Expense");
		Assert.assertEquals(cashbookPage.getBranch(), branch, "Branch");
		Assert.assertEquals(DataGenerator.extractDigits(cashbookPage.getAmount()), amount, "Amount");
		Assert.assertEquals(cashbookPage.getPaymentMethod(), paymentMethod, "Payment method");
		Assert.assertEquals(cashbookPage.getNote(), note, "Note");
		Assert.assertEquals(cashbookPage.isAccountingChecked(), isAccountingChecked, "Accounting");
	}

	
	
	@DataProvider
	public Object[][] groups() {
		return new Object[][] { 
			{CashbookGroup.CUSTOMER},
			{CashbookGroup.SUPPLIER},
			{CashbookGroup.STAFF},
			{CashbookGroup.OTHERS},
		};
	}	

	@Test(dataProvider = "groups")
	public void CB_01_CheckRevenueDropdown(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getTextByLanguage(groupEnum);
		
		cashbookPage.clickCreateReceiptBtn().selectGroup(group);
		
		String[] expected = revenueSources(groupEnum);
		String[] temp = cashbookPage.getSourceDropdownValues(); //Create a new array by omitting the first element of a particular array
		String[] actual = Arrays.copyOfRange(temp, 1, temp.length);
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Source list");
        
        //Temporarily commented out until a solution is found
        /*
        expected = paymentMethodList();
        actual = cashbookPage.getPaymentMethodDropdownValues();
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
        */
	}	
	
	@Test(dataProvider = "groups")
	public void CB_02_CheckExpenseDropdown(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getTextByLanguage(groupEnum);
		
		cashbookPage.clickCreatePaymentBtn().selectGroup(group);
		
		String[] expected = expenseSources(groupEnum);
		String[] temp = cashbookPage.getSourceDropdownValues(); //Create a new array by omitting the first element of a particular array
		String[] actual = Arrays.copyOfRange(temp, 1, temp.length);
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual), "Source list");
		
		//Temporarily commented out until a solution is found
		/*
        expected = paymentMethodList();
        actual = cashbookPage.getPaymentMethodDropdownValues();
        Arrays.sort(expected);
        Arrays.sort(actual);
        Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
		 */
	}		
	
	@Test(dataProvider = "groups")
	public void CB_03_CreateReceipts(CashbookGroup groupEnum) {

		String group = CashbookGroup.getTextByLanguage(groupEnum);
		
		cashbookPage.getCashbookSummaryBig();
		
		/* Create receipts */
		for (String source : revenueSources(groupEnum)) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomSender(groupEnum);
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating receipts
			List<BigDecimal> originalSummary = waitTillOK();
			int preTotalRecord = cashbookPage.getTotalRecordCount();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			List<BigDecimal> laterSummary = waitTillOK();
			cashbookPage.waitTillRecordCountIncrease(preTotalRecord);
			
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
	
	@Test(dataProvider = "groups")
	public void CB_04_CreatePayments(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getTextByLanguage(groupEnum);
		
		/* Create payments */
		for (String source : expenseSources(groupEnum)) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomSender(groupEnum);
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<BigDecimal> originalSummary = waitTillOK();
			int preTotalRecord = cashbookPage.getTotalRecordCount();
			
			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();
			
			// Get cashbook summary after creating payments
			List<BigDecimal> laterSummary = waitTillOK();
			cashbookPage.waitTillRecordCountIncrease(preTotalRecord);
			
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
	public void CB_05_SearchRecords() {
		
		cashbookPage.clickResetDateRangerPicker();
		
		/* Search random transaction ids */
		for (int i=0; i<3; i++) {
			String transactionId = randomTransactionId();
			
			//Deliberately input a random search term to empty the table
			cashbookPage.inputCashbookSearchTerm("DFRT").waitTillTableEmpty();
			
			cashbookPage.inputCashbookSearchTerm(transactionId);
			
			List<List<String>> searchedRecords = cashbookPage.getRecords();
			
			/* Click on the searched record */
			Assert.assertEquals(searchedRecords.size(), 1, "Number of found records");
			Assert.assertEquals(searchedRecords.get(0).get(Cashbook.TRANSACTIONCODE_COL), transactionId, "Transaction Code");	
			
		}
	}
	
	@Test
	public void CB_06_SingleFilterCondition() throws Exception {
		
		List<List<String>> records;
		
		/* Filter */
		cashbookPage.clickResetDateRangerPicker();
		
		records = cashbookPage.getRecords();
		
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		
		String recordId = randomRecord.get(Cashbook.TRANSACTIONCODE_COL);
		String branch = randomRecord.get(Cashbook.BRANCH_COL);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_COL);
		//Temporarily commented out until a solution is found
		//String name = randomRecord.get(Cashbook.NAME_COL);
		
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
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-");
		}
		
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredTransaction(transactions("allRevenues"));
		cashbookPage.clickFilterDoneBtn();
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), "-");
		}
		
		/* Filter by Expense type */
		String filteredExpenseType = DataGenerator.getRandomListElement(Arrays.asList(expenseSources(CashbookGroup.CUSTOMER)));
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredExpenseType(filteredExpenseType);
		cashbookPage.clickFilterDoneBtn();
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), filteredExpenseType);
		}
		
		/* Filter by Revenue type */
		String filteredRevenueType = DataGenerator.getRandomListElement(Arrays.asList(revenueSources(CashbookGroup.CUSTOMER))); 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredRevenueType(filteredRevenueType);
		cashbookPage.clickFilterDoneBtn();
		
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
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_COL), filteredStaff);
		}
		
		filteredStaff = createdBy("system"); 
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredCreatedBy(filteredStaff);
		cashbookPage.clickFilterDoneBtn();
		
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
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getGroup(), filteredGroup);
			cashbookPage.clickCancelBtn();
		}
		
		/* Filter by Sender/Recipient Name */
		/*
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
		*/
		
		/* Filter by Payment methods */
		String filteredPaymentMethod = payment; 
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker();
		cashbookPage.clickFilterBtn();
		cashbookPage.selectFilteredPaymentMethod(filteredPaymentMethod);
		cashbookPage.clickFilterDoneBtn();
		
		records = cashbookPage.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		for (List<String> record : records) {
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getPaymentMethod(), filteredPaymentMethod);
			cashbookPage.clickCancelBtn();
		}
	}	
	
	@Test
	public void CB_07_MultipleFilterConditions() throws Exception {
		
		List<List<String>> records;
		
		cashbookPage.clickResetDateRangerPicker();
		records = cashbookPage.getRecords();
		new Pagination(driver).clickNextBtn();
		commonAction.sleepInMiliSecond(1000);
		records.addAll(cashbookPage.getRecords());
		new Pagination(driver).clickPreviousBtn();
		
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		
		String recordId = randomRecord.get(Cashbook.TRANSACTIONCODE_COL);
		String branch = randomRecord.get(Cashbook.BRANCH_COL);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_COL);
		//Temporarily commented out until a solution is found
		//String name = randomRecord.get(Cashbook.NAME_COL);
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
		//Temporarily commented out until a solution is found
		//cashbookPage.selectFilteredName(name);
		cashbookPage.selectFilteredPaymentMethod(payment);
		cashbookPage.clickFilterDoneBtn();
//		commonAction.sleepInMiliSecond(4000, "Wait till the table is regenerated after the filter conditions are input");

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
			
			//Temporarily commented out until a solution is found
			//Assert.assertEquals(record.get(Cashbook.NAME_COL), name);
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_COL), createdBy);
			
			cashbookPage.clickRecord(record.get(Cashbook.TRANSACTIONCODE_COL));
			Assert.assertEquals(cashbookPage.getGroup(), group);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), payment);
			cashbookPage.clickCancelBtn();
		}
	}	
	
}
