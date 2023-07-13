import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.dashboard.cashbook.CashbookAPI;
import api.dashboard.cashbook.OthersGroupAPI;
import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.SupplierAPI;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.StaffManagement;
import io.appium.java_client.AppiumDriver;
import pages.sellerapp.HomePage;
import pages.sellerapp.LoginPage;
import pages.sellerapp.SellerGeneral;
import pages.sellerapp.cashbook.Cashbook;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.screenshot.Screenshot;

public class CashbookApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	LoginPage loginPage;
	SellerGeneral general;
	HomePage homePage;
	Cashbook cashbookPage;
	UICommonMobile commonAction;
	DataGenerator generate;
	
	String language = "VIE";
	String expectedCodeMsg;
	String expectedChangePasswordMsg;

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	List<String> customerList;
	List<String> supplierList;
	List<String> staffList;
	List<String> othersList;
	List<String> branchList;
	List<String> transactionIdList;
	
	String amount = "2000";
	String note = "Simply a note";	
	
	public void getCredentials() {
		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
	}	
	
	@BeforeClass
	public void setUp() throws Exception {
		PropertiesUtil.setEnvironment("STAG");
		PropertiesUtil.setDBLanguage(language);
		getCredentials();
		
        new Login().setDashboardLoginInfo(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
        customerList = new Customers().getAllCustomerNames();
        supplierList = new SupplierAPI().getAllSupplierNames();
        staffList = new StaffManagement().getAllStaffNames();
        othersList = new OthersGroupAPI().getAllOtherGroupNames();
        branchList = new BranchManagement().getInfo().getActiveBranches();
        transactionIdList = new CashbookAPI().getAllTransactionCodes();
	}

	@BeforeMethod
	public void generateData() throws Exception {
		instantiatePageObjects();
	}	

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
		if (driverWeb != null) driverWeb.quit();
	}	
	
	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		loginPage = new LoginPage(driver);
		general = new SellerGeneral(driver);
		homePage = new HomePage(driver);
		cashbookPage = new Cashbook(driver);
		commonAction = new UICommonMobile(driver);

		commonAction.waitSplashScreenLoaded();
//		new NotificationPermission(driver).clickAllowBtn();
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
		return PropertiesUtil.getPropertiesValueByDBLang("seller.cashbook.paymentMethod." + method);
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
	
	public void verifySummaryDataAfterReceiptCreated(List<Long> originalSummary, List<Long> laterSummary, boolean isAccountingChecked) {
		Long revenue = (isAccountingChecked) ? originalSummary.get(pages.dashboard.cashbook.Cashbook.TOTALREVENUE_IDX) + Long.parseLong(amount) : originalSummary.get(pages.dashboard.cashbook.Cashbook.TOTALREVENUE_IDX);
		Assert.assertEquals(laterSummary.get(pages.dashboard.cashbook.Cashbook.TOTALREVENUE_IDX), revenue, "Revenue");
		Assert.assertEquals(laterSummary.get(pages.dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX), originalSummary.get(pages.dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX), "Expenditure");
		Assert.assertEquals(laterSummary.get(pages.dashboard.cashbook.Cashbook.ENDINGBALANCE_IDX), laterSummary.get(pages.dashboard.cashbook.Cashbook.OPENINGBALANCE_IDX) + laterSummary.get(pages.dashboard.cashbook.Cashbook.TOTALREVENUE_IDX) - laterSummary.get(pages.dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX),
				"Ending Opening");
	}

	public void verifySummaryDataAfterPaymentCreated(List<Long> originalSummary, List<Long> laterSummary, boolean isAccountingChecked) {
//		Long expenditure = (isAccountingChecked) ? originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX) + Long.parseLong(amount) : originalSummary.get(Cashbook.TOTALEXPENDITURE_IDX);
//		Assert.assertEquals(laterSummary.get(Cashbook.TOTALREVENUE_IDX), originalSummary.get(Cashbook.TOTALREVENUE_IDX), "Revenue");
//		Assert.assertEquals(laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX), expenditure, "Expenditure");
//		Assert.assertEquals(laterSummary.get(Cashbook.ENDINGBALANCE_IDX), laterSummary.get(Cashbook.OPENINGBALANCE_IDX) + laterSummary.get(Cashbook.TOTALREVENUE_IDX) - laterSummary.get(Cashbook.TOTALEXPENDITURE_IDX),
//				"Ending Opening");
	}

	public void verifyRecordDataAfterReceiptCreated(List<String> record, String branch, String source,
			String sender, String amount) {
		Assert.assertEquals(record.get(pages.dashboard.cashbook.Cashbook.BRANCH_COL), branch, "Branch");
		Assert.assertTrue(record.get(pages.dashboard.cashbook.Cashbook.REVENUETYPE_COL).contains(source), "Revenue type");
		Assert.assertEquals(record.get(pages.dashboard.cashbook.Cashbook.NAME_COL-1), sender, "Sender");
		Assert.assertEquals(extractDigits(record.get(pages.dashboard.cashbook.Cashbook.AMOUNT_COL-1)), amount, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
			String sender, String amount) {
//		Assert.assertEquals(record.get(Cashbook.BRANCH_COL), branch, "Branch");
//		Assert.assertEquals(record.get(Cashbook.REVENUETYPE_COL), "-", "Revenue type");
//		Assert.assertEquals(record.get(Cashbook.EXPENSETYPE_COL), source, "Expense type");
//		Assert.assertEquals(record.get(Cashbook.NAME_COL), sender, "Sender");
//		Assert.assertEquals(extractDigits(record.get(Cashbook.AMOUNT_COL)), amount, "Amount");
	}

	public void verifyDataInRecordDetail(String group, String sender, String source, String branch,
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
	
	public AppiumDriver launchApp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "10.10.2.100:5555"); //192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D 
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
        capabilities.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        capabilities.setCapability("noReset", "false");
        
        String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}	

	@Test
	public void Login_02_CreateReceiptWhenSenderGroupIsCustomer() throws Exception {
		
		String group = senderGroup("customer");
		
    	loginPage.performLogin(STORE_USERNAME, STORE_PASSWORD);
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	homePage.navigateToPage("Cashbook");
    	
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomCustomer();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			List<Long> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
	    	general.getToastMessage();
	    	
	    	List<Long> laterSummary = cashbookPage.getCashbookSummary();
	    	
	    	verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);
	    	
	    	commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.7);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);
	    	
			cashbookPage.clickRecord(record.get(pages.dashboard.cashbook.Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}
	
	@Test
	public void Login_03_CreateReceiptWhenSenderGroupIsSupplier() throws Exception {
		
		String group = senderGroup("supplier");
		
		loginPage.performLogin(STORE_USERNAME, STORE_PASSWORD);
		Assert.assertTrue(homePage.isAccountTabDisplayed());
		homePage.navigateToPage("Cashbook");
		
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomSupplier();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			List<Long> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			general.getToastMessage();
			
			List<Long> laterSummary = cashbookPage.getCashbookSummary();
			
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);
			
			commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.7);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);
			
			cashbookPage.clickRecord(record.get(pages.dashboard.cashbook.Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}
	
	@Test
	public void Login_04_CreateReceiptWhenSenderGroupIsStaff() throws Exception {
		
		String group = senderGroup("staff");
		
		loginPage.performLogin(STORE_USERNAME, STORE_PASSWORD);
		Assert.assertTrue(homePage.isAccountTabDisplayed());
		homePage.navigateToPage("Cashbook");
		
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomStaff();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			List<Long> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			general.getToastMessage();
			
			List<Long> laterSummary = cashbookPage.getCashbookSummary();
			
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);
			
			commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.7);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);
			
			cashbookPage.clickRecord(record.get(pages.dashboard.cashbook.Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}
	
	@Test
	public void Login_05_CreateReceiptWhenSenderGroupIsOthers() throws Exception {
		
		String group = senderGroup("others");
		
		loginPage.performLogin(STORE_USERNAME, STORE_PASSWORD);
		Assert.assertTrue(homePage.isAccountTabDisplayed());
		homePage.navigateToPage("Cashbook");
		
		for (String source : revenueSourceList()) {
			boolean isAccountingChecked = randomAccountingChecked();
			String sender = randomOthers();
			String branch = randomBranch();
			String paymentMethod = randomPaymentMethod();
			List<Long> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			general.getToastMessage();
			
			List<Long> laterSummary = cashbookPage.getCashbookSummary();
			
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, isAccountingChecked);
			
			commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.7);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);
			
			cashbookPage.clickRecord(record.get(pages.dashboard.cashbook.Cashbook.TRANSACTIONCODE_COL)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}
	
}
