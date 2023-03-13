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
import utilities.jsonFileUtility;

public class CashbookTest extends BaseTest {

	LoginPage loginPage;
	Cashbook cashbookPage;
	HomePage homePage;

	String displayLanguage = "ENG";
	String[] revenueSource = { "Debt collection from supplier", "Debt collection from customer", "Payment for order",
			"Sale of assets", "Other income" };
	String[] expenseType = { "Payment to shipping partner", "Payment for goods", "Production cost",
			"Cost of raw materials", "Debt payment to customer", "Rental fee", "Utilities", "Salaries",
			"Selling expenses", "Other costs", "Refund" };
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

	public String randomPaymentMethod() {
		String[] paymentMethod = { "VISA", "ATM", "Bank transfer", "CASH", "Zalopay", "MOMO" };
		return paymentMethod[new Random().nextInt(0, paymentMethod.length)];
	}

	public void verifySummaryDataAfterReceiptCreated(List<Integer> originalSummary, List<Integer> laterSummary) {
		Assert.assertEquals(laterSummary.get(1), originalSummary.get(1) + Integer.parseInt(amount), "Revenue");
		Assert.assertEquals(laterSummary.get(2), originalSummary.get(2), "Expenditure");
		Assert.assertEquals(laterSummary.get(3), laterSummary.get(0) + laterSummary.get(1) - laterSummary.get(2),
				"Ending Opening");
	}
	
	public void verifySummaryDataAfterPaymentCreated(List<Integer> originalSummary, List<Integer> laterSummary) {
		Assert.assertEquals(laterSummary.get(1), originalSummary.get(1), "Revenue");
		Assert.assertEquals(laterSummary.get(2), originalSummary.get(2) + Integer.parseInt(amount), "Expenditure");
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
		Assert.assertEquals(cashbookPage.getGroupOnTransactionIdPopup(), group, "Sender/Recipient group");
		Assert.assertEquals(cashbookPage.getNameOnTransactionIdPopup(), sender, "Sender/Recipient name");
		Assert.assertEquals(cashbookPage.getSourceOrExpenseOnTransactionIdPopup(), source, "Revenue/Expense");
		Assert.assertEquals(cashbookPage.getBranchOnTransactionIdPopup(), branch, "Branch");
		Assert.assertEquals(extractDigits(cashbookPage.getAmountOnTransactionIdPopup()), amount, "Amount");
		Assert.assertEquals(cashbookPage.getPaymentMethodOnTransactionIdPopup(), paymentMethod, "Payment method");
		Assert.assertEquals(cashbookPage.getNoteOnTransactionIdPopup(), note, "Note");
		Assert.assertEquals(cashbookPage.isAccountingCheckedOnTransactionIdPopup(), isAccountingChecked, "Accounting");
	}

	@Test
	public void Cashbook_02_CreateReceiptWhenSenderGroupIsCustomer() throws Exception {

		String group = "Customer";
		String sender = "Anh Le";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource) {
			String paymentMethod = randomPaymentMethod();

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary);

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

		String group = "Supplier";
		String sender = "Kim Ma 1";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource) {
			String paymentMethod = randomPaymentMethod();

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary);

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

		String group = "Staff";
		String sender = "Staff A";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource) {
			String paymentMethod = randomPaymentMethod();

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary);

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

		String group = "Others";
		String sender = "nocus";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create receipts */
		cashbookPage.navigate();
		for (String source : revenueSource) {
			String paymentMethod = randomPaymentMethod();

			// Get cashbook summary before creating receipts
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create receipt
			homePage.hideFacebookBubble();
			cashbookPage.createReceipt(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating receipts
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating receipts
			verifySummaryDataAfterReceiptCreated(originalSummary, laterSummary);

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

		String group = "Customer";
		String sender = "aaa";
		boolean isAccountingChecked = true;

		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType) {
			String paymentMethod = randomPaymentMethod();

			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();

			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();

			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();

			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary);

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
		
		String group = "Supplier";
		String sender = "Kim Ma 2";
		boolean isAccountingChecked = true;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType) {
			String paymentMethod = randomPaymentMethod();
			
			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();
			
			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();
			
			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();
			
			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary);
			
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
		
		String group = "Staff";
		String sender = "Staff B";
		boolean isAccountingChecked = true;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType) {
			String paymentMethod = randomPaymentMethod();
			
			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();
			
			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();
			
			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();
			
			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary);
			
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
		
		String group = "Others";
		String sender = "fgh";
		boolean isAccountingChecked = true;
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		/* Create payments */
		cashbookPage.navigate();
		for (String source : expenseType) {
			String paymentMethod = randomPaymentMethod();
			
			// Get cashbook summary before creating payments
			List<Integer> originalSummary = cashbookPage.getCashbookSummary();
			
			// Create payments
			homePage.hideFacebookBubble();
			cashbookPage.createPayment(group, source, branch, paymentMethod, sender, amount, note,
					isAccountingChecked);
			homePage.getToastMessage();
			
			// Get cashbook summary after creating payments
			commonAction.sleepInMiliSecond(2000);
			List<Integer> laterSummary = cashbookPage.getCashbookSummary();
			
			// Check data summary after creating payments
			verifySummaryDataAfterPaymentCreated(originalSummary, laterSummary);
			
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
