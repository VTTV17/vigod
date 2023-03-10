import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.cashbook.Cashbook;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import utilities.PropertiesUtil;
import utilities.jsonFileUtility;

public class CashbookTest extends BaseTest {

	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;

	String displayLanguage = "ENG";
	String amount = "2000";
	String branch = "CN3";
	String note = "Simply a note";

	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String username = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String password = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String country = sellerData.findValue("seller").findValue("mail").findValue("country").asText();

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		loginPage = new LoginPage(driver);
		cashbookPage = new Cashbook(driver);
		homePage = new HomePage(driver);
	}

	public String extractDigits(String rawAmount) {
		Matcher m = Pattern.compile("\\d+").matcher(rawAmount);
		ArrayList<String> sub = new ArrayList<String>();
		while (m.find()) {
			sub.add(m.group());
		}
		return String.join("", sub);
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
	 * @param displayLanguage
	 * @return a list of revenue sources
	 * @throws Exception
	 */
	public String[] revenueSource(String displayLanguage) throws Exception {
		String[] list = {
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source.debtCollectionFromSupplier",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source.debtCollectionFromCustomer",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source.paymentForOrder",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source.saleOfAssets",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.source.otherIncome",displayLanguage),
		};
		return list;
	}
	
	/**
	 * 
	 * @param displayLanguage
	 * @return a list of expense types
	 * @throws Exception
	 */
	public String[] expenseType(String displayLanguage) throws Exception {
		String[] list = {
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.paymentToShippingPartner",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.paymentForGoods",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.productionCost",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.costOfRawMaterials",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.debtPaymentToCustomer",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.rentalFee",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.utilites",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.salaries",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.sellingExpenses",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.otherCosts",displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.expense.refund",displayLanguage),
		};
		return list;
	}

	public String randomPaymentMethod(String displayLanguage) throws Exception {
		String[] paymentMethod = { 
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.visa", displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.atm", displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.bankTransfer", displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.cash", displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.zalopay", displayLanguage),
				PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethod.momo", displayLanguage),
		};
		return paymentMethod[new Random().nextInt(0, paymentMethod.length)];
	}

	public void verifySummaryDataAfterReceiptCreated(List<Integer> originalSummary, List<Integer> laterSummary, boolean isAccountingChecked) {
		Integer revenue = (isAccountingChecked) ? originalSummary.get(1) + Integer.parseInt(amount) : originalSummary.get(1);
		Assert.assertEquals(laterSummary.get(1), revenue, "Revenue");
		Assert.assertEquals(laterSummary.get(2), originalSummary.get(2), "Expenditure");
		Assert.assertEquals(laterSummary.get(3), laterSummary.get(0) + laterSummary.get(1) - laterSummary.get(2),
				"Ending Opening");
	}

	public void verifySummaryDataAfterPaymentCreated(List<Integer> originalSummary, List<Integer> laterSummary, boolean isAccountingChecked) {
		Integer expenditure = (isAccountingChecked) ? originalSummary.get(2) + Integer.parseInt(amount) : originalSummary.get(2);
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
	public void Cashbook_01_CheckTranslation() throws Exception {
		
		String group = senderGroup("customer", displayLanguage);
		String sender = "Anh Le";
		boolean isAccountingChecked = true;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Verify text at cashbook management screen */
		cashbookPage.navigate().verifyTextAtCashbookManagementScreen(displayLanguage);
		
		/* Verify text at create receipt screen */
		cashbookPage.clickCreateReceiptBtn().verifyTextAtCreateReceiptScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		cashbookPage.createReceipt(group, revenueSource(displayLanguage)[0], branch, randomPaymentMethod(displayLanguage), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		List<List<String>> records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
		cashbookPage.verifyTextAtReceiptTransactionIDScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		
		/* Verify text at create payment screen */
		cashbookPage.clickCreatePaymentBtn().verifyTextAtCreatePaymentScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
		cashbookPage.createPayment(group, expenseType(displayLanguage)[0], branch, randomPaymentMethod(displayLanguage), sender, amount, note, isAccountingChecked);
		homePage.getToastMessage();
		records = cashbookPage.getRecords();
		cashbookPage.clickRecord(records.get(0).get(0)); // Click on the first record on the list.
		cashbookPage.verifyTextAtPaymentTransactionIDScreen(displayLanguage);
		cashbookPage.clickCancelBtn();
	}	
	
	@Test
	public void Cashbook_02_CreateReceiptWhenSenderGroupIsCustomer() throws Exception {

		String group = senderGroup("customer", displayLanguage);
		String sender = "Anh Le";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_03_CreateReceiptWhenSenderGroupIsSupplier() throws Exception {

		String group = senderGroup("supplier", displayLanguage);
		String sender = "Kim Ma 1";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_04_CreateReceiptWhenSenderGroupIsStaff() throws Exception {

		String group = senderGroup("staff", displayLanguage);
		String sender = "Staff A";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_05_CreateReceiptWhenSenderGroupIsOthers() throws Exception {

		String group = senderGroup("others", displayLanguage);
		String sender = "nocus";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_06_CreatePaymentWhenRecipientGroupIsCustomer() throws Exception {

		String group = senderGroup("customer", displayLanguage);
		String sender = "aaa";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_07_CreatePaymentWhenRecipientGroupIsSupplỉer() throws Exception {

		String group = senderGroup("supplier", displayLanguage);
		String sender = "Kim Ma 2";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_08_CreatePaymentWhenRecipientGroupIsStaff() throws Exception {

		String group = senderGroup("staff", displayLanguage);
		String sender = "Staff B";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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
	public void Cashbook_09_CreatePaymentWhenRecipientGroupIsOthers() throws Exception {

		String group = senderGroup("others", displayLanguage);
		String sender = "fgh";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType(displayLanguage)) {
			String paymentMethod = randomPaymentMethod(displayLanguage);

			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note, isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

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

}
