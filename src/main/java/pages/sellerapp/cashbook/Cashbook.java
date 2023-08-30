package pages.sellerapp.cashbook;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public Cashbook(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	String dropdownOption = "//*[ends-with(@resource-id,'tvAction') %s]";

	By CREATE_BTN = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
	By CREATE_RECEIPT_BTN = By.xpath(dropdownOption.formatted("and @index='0'"));
	By CREATE_PAYMENT_BTN = By.xpath(dropdownOption.formatted("and @index='1'"));

	By CASHBOOK_SEARCHBOX = By.xpath("//*[ends-with(@resource-id,'edtCashbookSearch')]");

	By DATEFILTER = By.xpath("//*[ends-with(@resource-id,'tvFilterFromDateToDate')]");
	By MONTH_TITLE = By.xpath("//*[ends-with(@resource-id,'title') and @index='0']");
	By WHOLE_MONTH = By.xpath("(//*[ends-with(@resource-id,'title')]/parent::*)[1]");

	By GROUP_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'tvSenderGroup')]");
	By NAME_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'tvSelectSenderName')]");
	By SEARCH_BOX = By.xpath("//*[ends-with(@resource-id,'edtSearchSenderRecipient')]");

	By REVENUE_SOURCE_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'tvSelectRevenue')]");

	By BRANCH_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'tvSelectBranch')]");

	By AMOUNT = By.xpath("//*[ends-with(@resource-id,'edtPriceCustom')]");

	By PAYMENTMETHOD_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'tvSelectPaymentMethod')]");

	By NOTE = By.xpath("//*[ends-with(@resource-id,'edtNote')]");

	By ACCOUNTING_CHECKBOX = By.xpath("//*[ends-with(@resource-id,'cbxAccounting')]");

	By SAVE_BTN = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");

	By[] COLUMN = { By.xpath("//*[ends-with(@resource-id,'tvCashbookId')]"),
			By.xpath("//*[ends-with(@resource-id,'tvDate')]"), By.xpath("//*[ends-with(@resource-id,'tvAddress')]"),
			By.xpath("//*[ends-with(@resource-id,'tvType')]"), By.xpath("//*[ends-with(@resource-id,'tvName')]"),
			By.xpath("//*[ends-with(@resource-id,'tvOwner')]"), By.xpath("//*[ends-with(@resource-id,'tvPrice')]"), };

	public List<Long> getCashbookSummary() {
		commonAction.sleepInMiliSecond(1000); // Sometimes it takes longer for the element to change its data
		By[] CASHBOOKSUMMARY = { By.xpath("//*[ends-with(@resource-id,'tvOpening')]"),
				By.xpath("//*[ends-with(@resource-id,'tvTotalRev')]"),
				By.xpath("//*[ends-with(@resource-id,'tvExpenditure')]"),
				By.xpath("//*[ends-with(@resource-id,'tvEnding')]"), };

		List<Long> summary = new ArrayList<>();
		for (By bySelector : CASHBOOKSUMMARY) {

			// Sometimes element is present but the data it contains is not yet rendered
			String text = "";
			for (int i = 0; i < 3; i++) {
				text = commonAction.getText(bySelector);
				if (!text.isEmpty())
					break;
				commonAction.sleepInMiliSecond(1000);
			}

			Matcher m = Pattern.compile("\\d+").matcher(text);
			ArrayList<String> sub = new ArrayList<String>();
			while (m.find()) {
				sub.add(m.group());
			}
			summary.add(Long.parseLong(String.join("", sub)));
		}
		return summary;
	}

	public List<String> getSpecificRecord(int index) {
		List<String> rowData = new ArrayList<>();
		for (By column : COLUMN) {
			rowData.add(commonAction.getText(column));
		}
		return rowData;
	}

	public Cashbook swipeThroughRecords() {

		if (commonAction.getElements(COLUMN[3]).size()<2) return this;

		Dimension size = driver.manage().window().getSize();

		String monthBounds = commonAction.getElement(COLUMN[3]).getAttribute("bounds");

		List<Integer> bounds = new ArrayList<>();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(monthBounds);
		while (m.find()) {
			bounds.add(Integer.valueOf(m.group()));
		}

		double startY = (double) bounds.get(3) / size.height;
		double endY = 0.41;

		commonAction.swipeByCoordinatesInPercent(0.5, startY, 0.5, endY, 1800);
		return this;
	}

	public Cashbook inputCashbookSearchTerm(String searchTerm) {
		commonAction.inputText(CASHBOOK_SEARCHBOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}

	public Cashbook clickTimeRangeFilter() {
		commonAction.clickElement(DATEFILTER);
		logger.info("Clicked on time range filter.");
		return this;
	}

	public String getCurrentTimeRangeFilter() {
		String timeRange = commonAction.getText(DATEFILTER);
		logger.info("Retrived current date range: " + timeRange);
		return timeRange;
	}

	public Cashbook setDateFilter(int currentDay, int currentMonth, int currentYear, int desiredDay, int desiredMonth, int desiredYear) {
		int[] swipeCount = calculateMonthDiff(currentDay, currentMonth, currentYear, desiredDay, desiredMonth, desiredYear);
		travelTime(swipeCount[0], swipeCount[1]);

		By DAY = By.xpath("//*[ends-with(@resource-id,'calendar_grid')]/*/*[@enabled='true']//*[@text='%s']".formatted(desiredDay));
		commonAction.clickElement(new ByChained(WHOLE_MONTH, DAY));

		return this;
	}

	public int[] calculateMonthDiff(int currentDay, int currentMonth, int currentYear, int desiredDay, int desiredMonth, int desiredYear) {
		int forwardSwipeCount = 0;
		int backwardSwipeCount = 0;
		int monthDiffCount = 0;
		if (desiredYear == currentYear) {
			if (desiredMonth >= currentMonth) {
				forwardSwipeCount = desiredMonth - currentMonth;
			} else {
				backwardSwipeCount = currentMonth - desiredMonth;
			}
		} else if (desiredYear > currentYear) {
			monthDiffCount = (desiredYear - currentYear) * 12;
			if (desiredMonth >= currentMonth) {
				forwardSwipeCount = monthDiffCount + (desiredMonth - currentMonth);
			} else {
				forwardSwipeCount = monthDiffCount - (currentMonth - desiredMonth);
			}
		} else {
			monthDiffCount = (currentYear - desiredYear) * 12;
			if (desiredMonth >= currentMonth) {
				backwardSwipeCount = monthDiffCount - (desiredMonth - currentMonth);
			} else {
				backwardSwipeCount = monthDiffCount + (currentMonth - desiredMonth);
			}
		}

		int[] swipeCount = {forwardSwipeCount, backwardSwipeCount};
		return swipeCount;
	}

	public Cashbook travelTime(int forwardSwipeCount, int backwardSwipeCount) {

		Dimension size = driver.manage().window().getSize();

		int swipeCount = (forwardSwipeCount>0) ? forwardSwipeCount : backwardSwipeCount;

		for (int i=1; i<=swipeCount; i++) {
			logger.debug("Month Title: " + commonAction.getText(MONTH_TITLE));

			String monthTitleBounds = commonAction.getElement(MONTH_TITLE).getAttribute("bounds");
			String monthBounds = commonAction.getElement(WHOLE_MONTH).getAttribute("bounds");

			String rawBounds = (forwardSwipeCount>0) ? monthBounds : monthTitleBounds;

			List<Integer> bounds = new ArrayList<>();
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(rawBounds);
			while (m.find()) {
				bounds.add(Integer.valueOf(m.group()));
			}

			double startY = (double) bounds.get(3) / size.height;
			double endY = (forwardSwipeCount>0) ? 0.4182736455463728 : 0.9366391184573003;

			commonAction.swipeByCoordinatesInPercent(0.5, startY, 0.5, endY, 2000);
		}
		return this;
	}

	public Cashbook clickApplyDateBtn() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'tvCalendarApply')]"));
		logger.info("Clicked on 'Apply' button to filter records according to time.");
		return this;
	}

	public Cashbook clickFilterIcon() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'ivFilterButton')]"));
		logger.info("Clicked on Filter icon.");
		return this;
	}

	public Cashbook clickResetFilterBtn() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'btnReset')]"));
		logger.info("Clicked on Reset Filter icon.");
		return this;
	}

	public Cashbook clickSeeAllBranches() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'btnSeeAllBranches')]"));
		logger.info("Clicked on 'See All Branches'.");
		return this;
	}

	public Cashbook selectFilteredBranch(String branch) {
		clickSeeAllBranches();
		commonAction.clickElement(By.xpath("//*[@text='%s']".formatted(branch)));
		logger.info("Selected filtered branch: %s.".formatted(branch));
		return this;
	}

	public Cashbook selectFilteredAccounting(String yesOrNo) {
		By ACCOUNTING = By.xpath("//*[ends-with(@resource-id,'htvAccountForBusiness')]");
		commonAction.clickElement(new ByChained(ACCOUNTING, By.xpath("//*[@text='%s']".formatted(yesOrNo))));
		logger.info("Selected filtered Accounting: %s.".formatted(yesOrNo));
		return this;
	}

	public Cashbook selectFilteredTransaction(String transaction) {
		By TRANSACTION = By.xpath("//*[ends-with(@resource-id,'htvTransactions')]");
		commonAction.clickElement(new ByChained(TRANSACTION, By.xpath("//*[@text='%s']".formatted(transaction))));
		logger.info("Selected filtered Transaction: %s.".formatted(transaction));
		return this;
	}

	public Cashbook selectFilteredExpenseType(String expenseType) {
		By SEEALL = By.xpath("//*[ends-with(@resource-id,'btnSeeAllExpenseType')]");
		commonAction.clickElement(SEEALL);
		commonAction.clickElement(By.xpath("//*[@text='%s']".formatted(expenseType)));
		logger.info("Selected filtered Expense type: %s.".formatted(expenseType));
		return this;
	}

	public Cashbook selectFilteredRevenueType(String revenueType) {
		By SEEALL = By.xpath("//*[ends-with(@resource-id,'btnSeeAllRevenueType')]");
		commonAction.clickElement(SEEALL);
		commonAction.clickElement(By.xpath("//*[@text='%s']".formatted(revenueType)));
		logger.info("Selected filtered Revenue type: %s.".formatted(revenueType));
		return this;
	}

	public Cashbook selectFilteredCreatedBy(String createdBy) {
		By SEEALL = By.xpath("//*[ends-with(@resource-id,'btnSeeAllCreatedBy')]");
		commonAction.clickElement(SEEALL);
		commonAction.clickElement(By.xpath("//*[@text='%s']".formatted(createdBy)));
		logger.info("Selected filtered Created by: %s.".formatted(createdBy));
		return this;
	}

	public Cashbook selectFilteredGroup(String group) {
		By GROUP = By.xpath("//*[ends-with(@resource-id,'htvSenderRecipientGroup')]");
		commonAction.clickElement(commonAction.moveAndGetElement(new ByChained(GROUP, By.xpath("//*[@text='%s']".formatted(group)))));
		logger.info("Selected filtered Sender/Recipient group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectFilteredName(String name) {
		By SEEALL = By.xpath("//*[ends-with(@resource-id,'btnSeeAllSenderRecipient')]");
		commonAction.clickElement(SEEALL);
		commonAction.clickElement(commonAction.moveAndGetElementByText(name));
		logger.info("Selected filtered Sender/Recipient name: %s.".formatted(name));
		return this;
	}

	public Cashbook selectFilteredPaymentMethod(String method) {
		By PAYMENT = By.xpath("//*[ends-with(@resource-id,'htvPaymentMethod')]");
		commonAction.clickElement(commonAction.moveAndGetElement(new ByChained(PAYMENT, By.xpath("//*[@text='%s']".formatted(method)))));
		logger.info("Selected filtered payment method: %s.".formatted(method));
		return this;
	}

	public Cashbook clickApplyBtn() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'btnApply')]"));
		logger.info("Clicked on 'Apply' button.");
		return this;
	}

	public Cashbook clickRecord(String recordID) {
		// More code needed
		commonAction.clickElement(COLUMN[0]);
		commonAction.sleepInMiliSecond(500); //Sometimes it takes longer for the detail screen to load. Temporary
		logger.info("Clicked on cashbook record '%s'.".formatted(recordID));
		return this;
	}

	public Cashbook clickCreateBtn() {
		commonAction.clickElement(CREATE_BTN, defaultTimeout);
		logger.info("Clicked on 'Create' button.");
		return this;
	}

	public Cashbook clickCreateReceiptBtn() {
		commonAction.clickElement(CREATE_RECEIPT_BTN, defaultTimeout);
		logger.info("Clicked on 'Create Receipt'.");
		return this;
	}

	public Cashbook clickCreatePaymentBtn() {
		commonAction.clickElement(CREATE_PAYMENT_BTN, defaultTimeout);
		logger.info("Clicked on 'Create Payment' button.");
		return this;
	}

	public Cashbook selectGroup(String group) {
		commonAction.clickElement(GROUP_DROPDOWN);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text='%s'".formatted(group))));
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name) {
		commonAction.clickElement(NAME_DROPDOWN);
//		commonAction.sleepInMiliSecond(1000); // Sometimes the element representing the search box gets stale
		commonAction.inputText(SEARCH_BOX, name);
		commonAction
				.clickElement(By.xpath("//*[ends-with(@resource-id,'tvFilterText') and @text='%s']".formatted(name)));
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}

	public String[] getDropdownValues() {
		// Sometimes it takes longer for the values to display
		int elementCount = 0;
		for (int i = 0; i < 3; i++) {
			elementCount = commonAction.getElements(By.xpath(dropdownOption.formatted(""))).size();
			if (elementCount > 0)
				break;
		}

		// Store all option values into an array then return the array
		List<String> values = new ArrayList<>();
		for (int i = 0; i < elementCount; i++) {
			values.add(commonAction.getText(By.xpath(dropdownOption.formatted("and @index='%s'".formatted(i)))));
		}
		return values.toArray(new String[0]);
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown
	 * list for revenue source.
	 *
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getSourceDropdownValues() {
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN);
		String[] values = getDropdownValues();
		commonAction.navigateBack();
		return values;
	}

	public Cashbook selectRevenueExpense(String revenueExpense) {
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text='%s'".formatted(revenueExpense))));
		logger.info("Selected Revenue Source/Expense Type: %s.".formatted(revenueExpense));
		return this;
	}

	public Cashbook selectBranch(String branch) {
		commonAction.clickElement(BRANCH_DROPDOWN);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text='%s'".formatted(branch))));
		logger.info("Selected Branch: %s.".formatted(branch));
		return this;
	}

	public Cashbook inputAmount(String amount) {
		commonAction.inputText(AMOUNT, amount);
		logger.info("Input amount: %s.".formatted(amount));
		return this;
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown
	 * list for payment methods.
	 *
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getPaymentMethodDropdownValues() {
		commonAction.clickElement(PAYMENTMETHOD_DROPDOWN);
		String[] values = getDropdownValues();
		commonAction.navigateBack();
		return values;
	}

	public Cashbook selectPaymentMethod(String paymentMethod) {
		commonAction.clickElement(PAYMENTMETHOD_DROPDOWN);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text='%s'".formatted(paymentMethod))));
		logger.info("Selected Payment Method: %s.".formatted(paymentMethod));
		return this;
	}

	public Cashbook inputNote(String note) {
		commonAction.inputText(NOTE, note);
		logger.info("Input note: %s.".formatted(note));
		return this;
	}

	public boolean isAccountingChecked() {
		boolean isChecked = commonAction.isElementChecked(ACCOUNTING_CHECKBOX);
		logger.info("Is accounting checked: " + isChecked);
		return isChecked;
	}

	/**
	 * @param isChecked If true => check the box, if false => un-check the box
	 */
	public Cashbook checkAccountingCheckbox(boolean isChecked) {
		if (isChecked) {
			if (isAccountingChecked())
				return this;
			commonAction.clickElement(ACCOUNTING_CHECKBOX);
			logger.info("Checked Account checkbox");
		} else {
			if (!isAccountingChecked())
				return this;
			commonAction.clickElement(ACCOUNTING_CHECKBOX);
			logger.info("Un-checked Account checkbox");
		}
		return this;
	}

	public Cashbook clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on Save button.");
		return this;
	}

	public Cashbook createReceiptPaymentOverlap(String senderGroup, String revenue, String branch, String payment,
												String senderName, String amount, String note, boolean isChecked) {
		selectGroup(senderGroup);
		selectRevenueExpense(revenue);
		selectBranch(branch);
		selectPaymentMethod(payment);
		selectName(senderName);
		inputAmount(amount);
		inputNote(note);
		checkAccountingCheckbox(isChecked);
		clickSaveBtn();
		return this;
	}

	public Cashbook createReceipt(String senderGroup, String revenue, String branch, String payment, String senderName,
								  String amount, String note, boolean isChecked) {
		clickCreateBtn();
		clickCreateReceiptBtn();
		createReceiptPaymentOverlap(senderGroup, revenue, branch, payment, senderName, amount, note, isChecked);
		return this;
	}

	public Cashbook createPayment(String senderGroup, String revenue, String branch, String payment, String senderName,
								  String amount, String note, boolean isChecked) {
		clickCreateBtn();
		clickCreatePaymentBtn();
		createReceiptPaymentOverlap(senderGroup, revenue, branch, payment, senderName, amount, note, isChecked);
		return this;
	}

	public String getGroup() {
		String text = commonAction.getText(GROUP_DROPDOWN);
		logger.info("Retrieved Group value from record details: " + text);
		return text;
	}

	public String getName() {
		String text = commonAction.getText(NAME_DROPDOWN);
		logger.info("Retrieved name value from record details: " + text);
		return text;
	}

	public String getSourceOrExpense() {
		String text = commonAction.getText(REVENUE_SOURCE_DROPDOWN);
		logger.info("Retrieved Source/Expense value from record details: " + text);
		return text;
	}

	public String getBranch() {
		String text = commonAction.getText(BRANCH_DROPDOWN);
		logger.info("Retrieved Branch value from record details: " + text);
		return text;
	}

	public String getPaymentMethod() {
		String text = commonAction.getText(PAYMENTMETHOD_DROPDOWN);
		logger.info("Retrieved Payment method value from record details: " + text);
		return text;
	}

	public String getAmount() {
		String text = commonAction.getText(AMOUNT);
		logger.info("Retrieved Amount value from record details: " + text);
		return text;
	}

	public String getNote() {
		String text = commonAction.getText(NOTE);
		logger.info("Retrieved Note value from record details: " + text);
		return text;
	}

}
