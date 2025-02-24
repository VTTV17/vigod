package app.android.GoSeller;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
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
import api.Seller.supplier.supplier.APISupplier;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StaffManagement;
import io.appium.java_client.AppiumDriver;
import app.Buyer.notificationpermission.NotificationPermission;
import app.GoSeller.cashbook.Cashbook;
import app.GoSeller.general.SellerGeneral;
import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.sellerApp.login.LoginInformation;


/**
 * <b>Some differences in the feature between web and mobile versions are: </b>
 * <p>On mobile version, payment method has Paypal whereas web version doesn't. The payment does not appear when filtering records
 * <p>On mobile version, expenditure does not have "Debt payment to customer" whereas web version does => https://mediastep.atlassian.net/browse/BH-21226
 */
public class CashbookApp extends BaseTest {

	LoginPage loginPage;
	SellerGeneral general;
	HomePage homePage;
	Cashbook cashbookPage;
	UICommonMobile commonAction;
	DataGenerator generate;

	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;

	List<String> customerList;
	List<String> supplierList;
	List<String> staffList;
	List<String> othersList;
	List<String> branchList;
	List<String> transactionIdList;

	
	@BeforeClass
	public void setUp() throws Exception {
		PropertiesUtil.setDBLanguage("VIE");
		
		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		
		LoginInformation loginInformation = new Login().setLoginInformation(AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.ADMIN_USERNAME_TIEN, AccountTest.ADMIN_PASSWORD_TIEN).getLoginInformation();
		customerList = new APIAllCustomers(loginInformation).getAllCustomerNames();
		supplierList = new APISupplier(loginInformation).getAllSupplierNames();
		staffList = new StaffManagement(loginInformation).getAllStaffNames();
		othersList = new OthersGroupAPI(loginInformation).getAllOtherGroupNames();
		branchList = new BranchManagement(loginInformation).getInfo().getActiveBranches();
		transactionIdList = new CashbookAPI(loginInformation).getAllTransactionCodes();
	}
	
	@BeforeMethod
	public void beforeEachMethod() throws Exception  {
		instantiatePageObjects();
		loginThenNavigateToCashbook();
	}
	
	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		super.tearDownAndroid();
	}

	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		
		driver = launchApp();
		loginPage = new LoginPage(driver);
		general = new SellerGeneral(driver);
		homePage = new HomePage(driver);
		cashbookPage = new Cashbook(driver);
		commonAction = new UICommonMobile(driver);
		
	}

	public void loginThenNavigateToCashbook() throws Exception  {
		commonAction.waitSplashScreenLoaded();
		
		loginPage.performLogin(STORE_USERNAME, STORE_PASSWORD);
		
		new NotificationPermission(driver).clickAllowBtn();
		
		Assert.assertTrue(homePage.isAccountTabDisplayed());
		
		homePage.navigateToPage("Cashbook");
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
		return RandomUtils.nextBoolean();
	}

	public String randomTransactionId() {
		return getRandomListElement(transactionIdList.subList(0, 200));
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
//				paymentMethod("paypal"),
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


	public AppiumDriver launchApp() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("udid", "RF8N20PY57D"); //192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
		capabilities.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
		capabilities.setCapability("noReset", "false");

		String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}

//	@Test
	public void CB_00_CheckRevenueExpensePaymentDropdownValues() throws Exception {

		String[] expected;
		String[] actual;

		cashbookPage.clickCreateBtn().clickCreateReceiptBtn();

		expected = revenueSourceList();
		actual = cashbookPage.getSourceDropdownValues();
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual), "Source list");
		expected = paymentMethodList();
		actual = cashbookPage.getPaymentMethodDropdownValues();
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
		commonAction.navigateBack();

		cashbookPage.clickCreateBtn().clickCreatePaymentBtn();

		expected = expenseTypeList();
		actual = cashbookPage.getSourceDropdownValues();
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual), "Expense list");
		expected = paymentMethodList();
		actual = cashbookPage.getPaymentMethodDropdownValues();
		Arrays.sort(expected);
		Arrays.sort(actual);
		Assert.assertTrue(Arrays.equals(expected, actual), "Payment method list");
	}
}
