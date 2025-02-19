package app.android.GoSeller;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.RandomUtils;
import org.testng.Assert;
import org.testng.ITestResult;
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
import api.catalog.APICatalog;
import app.GoSeller.cashbook.Cashbook;
import app.GoSeller.general.SellerGeneral;
import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import utilities.account.AccountTest;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.driver.InitAndroidDriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.cashbook.CashbookExpense;
import utilities.enums.cashbook.CashbookGroup;
import utilities.enums.cashbook.CashbookRevenue;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.ListUtils;
import utilities.utils.PropertiesUtil;

/**
 * <b>Some differences in the feature between web and mobile versions are: </b>
 * <p>On mobile version, payment method has Paypal whereas web version doesn't. The payment does not appear when filtering records
 * <p>On mobile version, expenditure does not have "Debt payment to customer" whereas web version does => https://mediastep.atlassian.net/browse/BH-21226
 */
public class CashbookGoSeller extends BaseTest {

	String country, username, password;
	String storeCurrencySymbol;
	List<String> customerList, supplierList, staffList, othersList, branchList, transactionIdList;
	
	LoginPage loginPage;
	SellerGeneral general;
	HomePage homePage;
	Cashbook cashbookPage;
	UICommonMobile commonAction;

	CashbookAPI cashbookAPI;
	
	@BeforeClass
	public void loadTestData() {
		
		//Override display language based on IP location
		if (APICatalog.getCurrentLocation().contentEquals("VN")) {
			PropertiesUtil.setDBLanguage(DisplayLanguage.VIE.name());
		} else {
			PropertiesUtil.setDBLanguage(DisplayLanguage.ENG.name());
		}
		
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
	}
	
	@BeforeMethod
	public void beforeMethod() {
    	driver = new InitAndroidDriver().getSellerDriver(PropertiesUtil.getEnvironmentData("udidAndroidVi"));
    	new UICommonMobile(driver).waitSplashScreenLoaded();
    	
		loginPage = new LoginPage(driver);
		general = new SellerGeneral(driver);
		homePage = new HomePage(driver);
		cashbookPage = new Cashbook(driver);
		commonAction = new UICommonMobile(driver);
		
		loginPage.performLogin(country, username, password);
		Assert.assertTrue(homePage.isAccountTabDisplayed());
		homePage.navigateToPage("Cashbook");
	}
	
	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		super.tearDownAndroid();
	}

	public String randomSender(CashbookGroup group) {
		return switch (group) {
			case CUSTOMER: yield ListUtils.getRandomListElement(customerList);
			case SUPPLIER: yield ListUtils.getRandomListElement(supplierList);
			case STAFF: yield ListUtils.getRandomListElement(staffList);
			default: yield ListUtils.getRandomListElement(othersList);
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
	
	public List<String> revenueSources() {
		return Arrays.stream(CashbookRevenue.values()).map(name -> CashbookRevenue.getTextByLanguage(name)).collect(Collectors.toList());
	}	

	public List<String> expenseSources(CashbookGroup group) {
		//Reason for this is not documented
		if (group.equals(CashbookGroup.CUSTOMER)) {
			return Arrays.stream(CashbookExpense.values()).map(name -> CashbookExpense.getTextByLanguage(name)).collect(Collectors.toList());
		}
		//https://mediastep.atlassian.net/browse/BH-27152
		return Arrays.stream(CashbookExpense.values()).filter(name -> name!=CashbookExpense.DEBT_COLLECTION_FROM_SELLER).map(name -> CashbookExpense.getTextByLanguage(name)).collect(Collectors.toList());
	}	
	
	public String localizePaymentMethod(String method) {
		try {
			return PropertiesUtil.getPropertiesValueByDBLang("seller.cashbook.paymentMethod." + method);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * Domain .biz has less payment methods
	 * @param domain
	 */
	public List<String> paymentMethodList(Domain domain) {
		List<String> paymentMethods = new ArrayList<>();
		paymentMethods.add(localizePaymentMethod("bankTransfer"));
		paymentMethods.add(localizePaymentMethod("cash"));
		
		if (domain.equals(Domain.VN)) {
			paymentMethods.add(localizePaymentMethod("visa"));
			paymentMethods.add(localizePaymentMethod("atm"));
			paymentMethods.add(localizePaymentMethod("zalopay"));
			paymentMethods.add(localizePaymentMethod("momo"));
		}
		return paymentMethods;
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
	    BigDecimal revenue = (isAccountingChecked) ? originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX).add(new BigDecimal(amount)) : originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX);
	    
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX).compareTo(revenue) == 0, 
	        "Revenue mismatch: expected " + revenue + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX));
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX).compareTo(originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX)) == 0, 
	        "Expenditure mismatch: expected " + originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX) + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX));
	    
	    BigDecimal expectedEndingBalance = laterSummary.get(web.Dashboard.cashbook.Cashbook.OPENINGBALANCE_IDX)
	        .add(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX))
	        .subtract(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX));
	    
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.ENDINGBALANCE_IDX).compareTo(expectedEndingBalance) == 0, 
	        "Ending Balance mismatch: expected " + expectedEndingBalance + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.ENDINGBALANCE_IDX));
	}		
	
	public void verifySummaryDataAfterPaymentCreated(List<BigDecimal> originalSummary, List<BigDecimal> laterSummary, String amount, boolean isAccountingChecked) {
	    BigDecimal expenditure = (isAccountingChecked) ? originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX).add(new BigDecimal(amount)) : originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX);
	    
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX).compareTo(originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX)) == 0, 
	        "Revenue mismatch: expected " + originalSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX) + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX));
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX).compareTo(expenditure) == 0, 
	        "Expenditure mismatch: expected " + expenditure + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX));
	    
	    BigDecimal expectedEndingBalance = laterSummary.get(web.Dashboard.cashbook.Cashbook.OPENINGBALANCE_IDX)
	        .add(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALREVENUE_IDX))
	        .subtract(laterSummary.get(web.Dashboard.cashbook.Cashbook.TOTALEXPENDITURE_IDX));
	    
	    Assert.assertTrue(laterSummary.get(web.Dashboard.cashbook.Cashbook.ENDINGBALANCE_IDX).compareTo(expectedEndingBalance) == 0, 
	        "Ending Balance mismatch: expected " + expectedEndingBalance + " but found " + laterSummary.get(web.Dashboard.cashbook.Cashbook.ENDINGBALANCE_IDX));
	}	
	
	public void verifyRecordDataAfterReceiptCreated(List<String> record, String branch, String source,
													String sender, String amount) {
		Assert.assertEquals(record.get(web.Dashboard.cashbook.Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertTrue(record.get(web.Dashboard.cashbook.Cashbook.REVENUETYPE_IDX).contains(source), "Revenue type");
		Assert.assertEquals(record.get(web.Dashboard.cashbook.Cashbook.NAME_IDX-1), sender, "Sender");
		Assert.assertEquals(DataGenerator.extractDigits(record.get(web.Dashboard.cashbook.Cashbook.AMOUNT_IDX-1)), amount, "Amount");
	}

	public void verifyRecordDataAfterPaymentCreated(List<String> record, String branch, String source,
													String sender, String amount) {
		Assert.assertEquals(record.get(web.Dashboard.cashbook.Cashbook.BRANCH_IDX), branch, "Branch");
		Assert.assertTrue(record.get(web.Dashboard.cashbook.Cashbook.REVENUETYPE_IDX).contains(source), "Expense type");
		Assert.assertEquals(record.get(web.Dashboard.cashbook.Cashbook.NAME_IDX-1), sender, "Sender");
		Assert.assertEquals(DataGenerator.extractDigits(record.get(web.Dashboard.cashbook.Cashbook.AMOUNT_IDX-1)), amount, "Amount");
	}

	public void verifyDataInRecordDetail(String group, String sender, String source, String branch,
										 String amount, String paymentMethod, String note, boolean isAccountingChecked) {
		Assert.assertEquals(cashbookPage.getGroup(), group, "Sender/Recipient group");
		Assert.assertEquals(cashbookPage.getName(), sender, "Sender/Recipient name");
		Assert.assertEquals(cashbookPage.getSourceOrExpense(), source, "Revenue/Expense");
		Assert.assertEquals(cashbookPage.getBranch(), branch, "Branch");
		Assert.assertTrue(new BigDecimal(DataGenerator.extractDigits(cashbookPage.getAmount())).compareTo(new BigDecimal(amount))==0, "Amount");
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
	public void CBA_01_CreateReceipts(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		for (String source : revenueSources()) {
			boolean isAccountingChecked = RandomUtils.nextBoolean();
			String sender = randomSender(groupEnum);
			String branch = ListUtils.getRandomListElement(branchList);
			String paymentMethod = ListUtils.getRandomListElement(paymentMethodList(Domain.valueOf(domain)));
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			List<BigDecimal> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			
			//Bug https://mediastep.atlassian.net/browse/BH-48658
			//general.getToastMessage();
			
			List<BigDecimal> laterSummary = cashbookPage.getCashbookSummary();
			
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary, amount, isAccountingChecked);
			
			commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.8);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterReceiptCreated(record, branch, source, sender, amount);
			
			cashbookPage.clickRecord(record.get(web.Dashboard.cashbook.Cashbook.ID_IDX)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}
	
	@Test(dataProvider = "groups")
	public void CBA_02_CreatePayments(CashbookGroup groupEnum) {
		
		String group = CashbookGroup.getLocalizedText(groupEnum);
		
		for (String source : expenseSources(groupEnum)) {
			boolean isAccountingChecked = RandomUtils.nextBoolean();
			String sender = randomSender(groupEnum);
			String branch = ListUtils.getRandomListElement(branchList);
			String paymentMethod = ListUtils.getRandomListElement(paymentMethodList(Domain.valueOf(domain)));
			String amount = randomAmount();
			String note = "%s %s".formatted(sender, paymentMethod);
			
			List<BigDecimal> originalSummary = cashbookPage.getCashbookSummary();
			
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			
			//Bug https://mediastep.atlassian.net/browse/BH-48658
			//general.getToastMessage();
			
			List<BigDecimal> laterSummary = cashbookPage.getCashbookSummary();
			
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary, amount, isAccountingChecked);
			
			commonAction.swipeByCoordinatesInPercent(0.5, 0.5, 0.5, 0.8);
			List<String> record = cashbookPage.getSpecificRecord(0);
			verifyRecordDataAfterPaymentCreated(record, branch, source, sender, amount);
			
			cashbookPage.clickRecord(record.get(web.Dashboard.cashbook.Cashbook.ID_IDX)); // Click on the first record on the list.
			verifyDataInRecordDetail(group, sender, source, branch, amount, paymentMethod, note, isAccountingChecked);
			
			commonAction.navigateBack();
		}
	}

}
