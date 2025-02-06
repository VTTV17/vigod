package web.Dashboard;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert; 
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import api.Seller.cashbook.CashbookAPI;
import api.Seller.cashbook.OthersGroupAPI;
import api.Seller.customers.APIAllCustomers;
import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StaffManagement;
import api.Seller.setting.StoreInformation;
import api.Seller.supplier.supplier.APISupplier;
import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
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

public class CashbookTest extends BaseTest {

	List<String> customerList, supplierList, staffList, othersList, branchList, transactionIdList;
	String storeCurrencySymbol;
	
	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;
	
	CashbookAPI cashbookAPI;

	@BeforeClass
	public void loadTestData() {
		
		String country, username, password;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			country = AccountTest.ADMIN_COUNTRY_TIEN;
			username = AccountTest.ADMIN_USERNAME_TIEN;
			password = AccountTest.ADMIN_PASSWORD_TIEN;
		} else {
			country = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			username = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			password = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
		}
		
		LoginInformation loginInformation = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, password).getLoginInformation();
        customerList = new APIAllCustomers(loginInformation).getAllCustomerNames().stream().filter(e -> !e.matches(".*\\*{2}.*")).collect(Collectors.toList());
        supplierList = new APISupplier(loginInformation).getAllSupplierNames();
        staffList = new StaffManagement(loginInformation).getAllStaffNames();
        othersList = new OthersGroupAPI(loginInformation).getAllOtherGroupNames();
        branchList = new BranchManagement(loginInformation).getInfo().getActiveBranches();
        storeCurrencySymbol = new StoreInformation(loginInformation).getInfo().getSymbol();
        
        cashbookAPI = new CashbookAPI(loginInformation);
        transactionIdList = cashbookAPI.getAllTransactionCodes();
        
        driver = new InitWebdriver().getDriver(browser, headless);
        loginPage = new LoginPage(driver, Domain.valueOf(domain));
        cashbookPage = new Cashbook(driver, Domain.valueOf(domain));
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);

		loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language)).performValidLogin(country, username, password);
	}	

	@BeforeMethod
	public void navigateToPage() {
		//Not sure why the page is sometimes blank after navigation on CI env => We'll temporarily use the loop below
		for (int i=0; i<4; i++) {
			cashbookPage.navigate();
			if (cashbookPage.isPageTitlePresent()) break;
		}
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	

    @AfterClass
    public void afterClass() {
        tearDownWeb();
    }    
	
	public String randomSender(CashbookGroup group) {
		return switch (group) {
			case CUSTOMER: yield DataGenerator.getRandomListElement(customerList);
			case SUPPLIER: yield DataGenerator.getRandomListElement(supplierList);
			case STAFF: yield DataGenerator.getRandomListElement(staffList);
			default: yield DataGenerator.getRandomListElement(othersList);
		};
	}	

	/**
	 * <p>Generate a random amount based on the store's currency symbol. Eg. 34000 or 34.34
	 */
	public String randomAmount() {
		if (storeCurrencySymbol.contentEquals("đ")) {
			return new Generex("[1-9]\\d{2,5}").random();
		}
		return new Generex("[1-9]\\d{0,2}\\.\\d{2}").random();
	}	
	
	public List<String> revenueSources(CashbookGroup group) {
		if (group != CashbookGroup.SUPPLIER) {
			return Arrays.stream(CashbookRevenue.values()).map(name -> CashbookRevenue.getTextByLanguage(name)).collect(Collectors.toList());
		}
		//https://mediastep.atlassian.net/browse/BH-27152
		return Arrays.stream(CashbookRevenue.values()).filter(name -> name!=CashbookRevenue.DEBT_COLLECTION_FROM_SUPPLIER).map(name -> CashbookRevenue.getTextByLanguage(name)).collect(Collectors.toList());
	}
	
	public List<String> expenseSources(CashbookGroup group) {
		if (group != CashbookGroup.SUPPLIER) {
			return Arrays.stream(CashbookExpense.values()).map(name -> CashbookExpense.getTextByLanguage(name)).collect(Collectors.toList());
		}
		//https://mediastep.atlassian.net/browse/BH-27152
		return Arrays.stream(CashbookExpense.values()).filter(name -> name!=CashbookExpense.DEBT_COLLECTION_FROM_SELLER).map(name -> CashbookExpense.getTextByLanguage(name)).collect(Collectors.toList());
	}

	public List<String> paymentMethodList() {
		return CashbookPaymentMethod.availablePaymentListByDomain(Domain.valueOf(domain))
				.stream()
				.map(name -> CashbookPaymentMethod.getTextByLanguage(name))
				.collect(Collectors.toList());
	}	
	public String randomPaymentMethod() {
		return DataGenerator.getRandomListElement(paymentMethodList());
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
		Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), source, "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), "-", "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_IDX), sender, "Sender");
		Assert.assertTrue(new BigDecimal(DataGenerator.extractDigits(record.get(Cashbook.AMOUNT_IDX))).compareTo(new BigDecimal(amount))==0, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), "-", "Revenue type");
		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), source, "Expense type");
		Assert.assertEquals(record.get(Cashbook.NAME_IDX), sender, "Sender");
		Assert.assertTrue(new BigDecimal(DataGenerator.extractDigits(record.get(Cashbook.AMOUNT_IDX))).compareTo(new BigDecimal(amount))==0, "Amount");
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

	@Test(dataProvider = "groups", description = "Verify revenue and payment dropdown values are shown as expected")
	public void CB_01_CheckRevenueAndPaymentDropdown(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		List<String> expected = revenueSources(groupEnum);
		Collections.sort(expected);
		
		cashbookPage.clickCreateReceiptBtn().selectGroup(group);
		
		List<String> actual = Arrays.stream(cashbookPage.getSourceDropdownValues())
		                            .skip(1) // Skip the first element
		                            .sorted().collect(Collectors.toList());

		Assert.assertEquals(actual, expected, "Source list");
        
        expected = paymentMethodList();
        Collections.sort(expected);
        
        actual = Arrays.stream(cashbookPage.getPaymentMethodDropdownValues()).sorted().collect(Collectors.toList());
        
        Assert.assertEquals(actual, expected, "Payment method list");
	}	
	
//	@Test(dataProvider = "groups", description = "Verify expense and payment dropdown values are shown as expected")
	public void CB_02_CheckExpenseAndPaymentDropdown(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		cashbookPage.clickCreatePaymentBtn().selectGroup(group);
		
		List<String> expected = expenseSources(groupEnum);
		Collections.sort(expected);
		
		List<String> actual = Arrays.stream(cashbookPage.getSourceDropdownValues())
		                            .skip(1) // Skip the first element
		                            .sorted().collect(Collectors.toList());

		Assert.assertEquals(actual, expected, "Source list");
		
        expected = paymentMethodList();
        Collections.sort(expected);
        
        actual = Arrays.stream(cashbookPage.getPaymentMethodDropdownValues()).sorted().collect(Collectors.toList());
        
        Assert.assertEquals(actual, expected, "Payment method list");
	}		
	
	@Test(dataProvider = "groups", description = "Manually create receipts with different revenue and group values")
	public void CB_03_CreateReceipts(CashbookGroup groupEnum) {

		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		/* Create receipts with different revenue values */
		for (String source : revenueSources(groupEnum)) {
			boolean isAccountingChecked = new Random().nextBoolean();
			String sender = randomSender(groupEnum);
			String branch = DataGenerator.getRandomListElement(branchList);
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get summary before creating receipts
			List<BigDecimal> originalSummary = cashbookPage.getCashbookSummaryBig();

			// Create receipt
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get summary after creating receipts
			List<BigDecimal> laterSummary = cashbookPage.getCashbookSummaryBig();
			
			// Check summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);

			// Check record data after creating receipts
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);

			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);

			cashbookPage.clickCancelBtn();
		}
	}	
	
	@Test(dataProvider = "groups", description = "Manually create payments with different revenue and group values")
	public void CB_04_CreatePayments(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		/* Create payments with different expense sources */
		for (String source : expenseSources(groupEnum)) {
			boolean isAccountingChecked = new Random().nextBoolean();
			String sender = randomSender(groupEnum);
			String branch = DataGenerator.getRandomListElement(branchList);
			String paymentMethod = randomPaymentMethod();
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			// Get cashbook summary before creating payments
			List<BigDecimal> originalSummary = cashbookPage.getCashbookSummaryBig();
			
			// Create payments
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();
			
			// Get cashbook summary after creating payments
			List<BigDecimal> laterSummary = cashbookPage.getCashbookSummaryBig();
			
			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);
			
			// Check record data after creating payments
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);
			
			// Check record data on transaction Id pop-up
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX)); // Click on the first record on the list.
			verifyRecordDataOnTransactionIDPopup(group, sender, source, branch, amount, paymentMethod, note,
					isAccountingChecked);
			
			cashbookPage.clickCancelBtn();
		}
	}
	
	@Test
	public void CB_05_SearchRecords() {
		
		cashbookPage.clickResetDateRangerPicker();
		
		for (int i=0; i<3; i++) {
			String transactionId = DataGenerator.getRandomListElement(transactionIdList);
			cashbookPage.inputCashbookSearchTerm(transactionId);
			
			List<List<String>> searchedRecords = cashbookPage.getRecords();
			Assert.assertEquals(searchedRecords.size(), 1, "Number of found records");
			Assert.assertEquals(searchedRecords.get(0).get(Cashbook.ID_IDX), transactionId, "Transaction Code");	
		}
	}
	
	@Test
	public void CB_06_SingleFilterCondition() throws Exception {
		
		cashbookPage.clickResetDateRangerPicker();
		
		List<List<String>> records = cashbookPage.getRecords();
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		String recordId = randomRecord.get(Cashbook.ID_IDX);
		String branch = randomRecord.get(Cashbook.BRANCH_IDX);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_IDX);
//		String name = randomRecord.get(Cashbook.NAME_IDX); //Scrolling through customer list is complicated. Commented out for now
		cashbookPage.clickRecord(recordId);
		boolean expectedAccounting = cashbookPage.isAccountingChecked();
		String accounting = (expectedAccounting) ? allowAccounting("yes"):allowAccounting("no");
		String group = cashbookPage.getGroup();
		String payment = cashbookPage.getPaymentMethod();
		cashbookPage.clickCancelBtn();
		
		/* Filter by Accounting*/
		records = cashbookPage.clickFilterBtn()
				.selectFilteredAccounting(accounting)
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream()
	       .forEach(record -> {
	           cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
	           Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
	           cashbookPage.clickCancelBtn();
	       });

		
		/* Filter by branch*/
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
				.clickFilterBtn()
				.selectFilteredBranch(branch)
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.BRANCH_IDX), branch));
		
		/* Filter by transaction */
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
				.clickFilterBtn()
				.selectFilteredTransaction(transactions("allExpenses"))
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), "-"));
		
		records = cashbookPage.clickFilterBtn()
				.selectFilteredTransaction(transactions("allRevenues"))
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), "-"));

		
		/* Filter by Expense type */
		String filteredExpenseType = DataGenerator.getRandomListElement(expenseSources(CashbookGroup.CUSTOMER));
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
				.clickFilterBtn()
				.selectFilteredExpenseType(filteredExpenseType)
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_IDX), filteredExpenseType));
		
		/* Filter by Revenue type */
		String filteredRevenueType = DataGenerator.getRandomListElement(revenueSources(CashbookGroup.CUSTOMER)); 
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
                .clickFilterBtn()
                .selectFilteredRevenueType(filteredRevenueType)
                .clickFilterDoneBtn()
                .getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.REVENUETYPE_IDX), filteredRevenueType));

		
		/* Filter by Created by */
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
                .clickFilterBtn()
                .selectFilteredCreatedBy(createdBy)
                .clickFilterDoneBtn()
                .getRecords();

		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), createdBy));
		
		records = cashbookPage.clickFilterBtn()
		        .selectFilteredCreatedBy(createdBy("system"))
		        .clickFilterDoneBtn().getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), "system"));
		
		/* Filter by Sender/Recipient Group */
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
				.clickFilterBtn()
				.selectFilteredGroup(group)
				.clickFilterDoneBtn()
				.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream()
	       .forEach(record -> {
	           cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
	           Assert.assertEquals(cashbookPage.getGroup(), group);
	           cashbookPage.clickCancelBtn();
	       });
		
		/* Filter by Sender/Recipient Name */
		/*
		commonAction.refreshPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
			.clickFilterBtn()
			.selectFilteredGroup(group)
			.selectFilteredName(name)
			.clickFilterDoneBtn()
			.getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream().forEach(record -> Assert.assertEquals(record.get(Cashbook.NAME_IDX), name));
		*/
		
		/* Filter by Payment methods */
		navigateToPage();
		homePage.hideFacebookBubble();
		records = cashbookPage.clickResetDateRangerPicker()
			.clickFilterBtn()
			.selectFilteredPaymentMethod(payment)
			.clickFilterDoneBtn().getRecords();
		
		Assert.assertNotEquals(records.size(), 0);
		records.stream()
	       .forEach(record -> {
	           cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
	           Assert.assertEquals(cashbookPage.getPaymentMethod(), payment);
	           cashbookPage.clickCancelBtn();
	       });
	}	
	
	@Test
	public void CB_07_MultipleFilterConditions() throws Exception {
		
		cashbookPage.clickResetDateRangerPicker();
		
		List<List<String>> records = cashbookPage.getRecords();
		
		Pagination paginationSection = new Pagination(driver);
		paginationSection.clickNextBtn();
		records.addAll(cashbookPage.getRecords());
		paginationSection.clickPreviousBtn();
		
		List<String> randomRecord = DataGenerator.getRandomListElement(records);
		
		String recordId = randomRecord.get(Cashbook.ID_IDX);
		String branch = randomRecord.get(Cashbook.BRANCH_IDX);
		String createdBy = randomRecord.get(Cashbook.CREATEDBY_IDX);
//		String name = randomRecord.get(Cashbook.NAME_IDX); //Scrolling through customer list is complicated. Commented out for now
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
		navigateToPage();
		homePage.hideFacebookBubble();
		cashbookPage.clickResetDateRangerPicker()
			.clickFilterBtn()
			.selectFilteredBranch(branch)
			.selectFilteredAccounting(accounting)
			.selectFilteredTransaction(transaction);
		if (!revenue.contains("-")) {
			cashbookPage.selectFilteredRevenueType(revenue);
		} else {
			cashbookPage.selectFilteredExpenseType(expense);
		}
		cashbookPage.selectFilteredCreatedBy(createdBy)
			.selectFilteredGroup(group);
//		cashbookPage.selectFilteredName(name); //Scrolling through customer list is complicated. Commented out for now
		cashbookPage.selectFilteredPaymentMethod(payment)
			.clickFilterDoneBtn();
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
			
//			Assert.assertEquals(record.get(Cashbook.NAME_IDX), name); //Scrolling through customer list is complicated. Commented out for now
			Assert.assertEquals(record.get(Cashbook.CREATEDBY_IDX), createdBy);
			
			cashbookPage.clickRecord(record.get(Cashbook.ID_IDX));
			Assert.assertEquals(cashbookPage.getGroup(), group);
			Assert.assertEquals(cashbookPage.isAccountingChecked(), expectedAccounting);
			Assert.assertEquals(cashbookPage.getPaymentMethod(), payment);
			cashbookPage.clickCancelBtn();
		}
	}	
	
}
