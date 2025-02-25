package app.GoSeller.cashbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.pagefactory.ByChained;

import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public Cashbook(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonMobile(driver);
	}

	//TODO: Move these locators to a separate file
	String dropdownOption = "//*[ends-with(@resource-id,'tvAction') %s]";

	By loc_btnPlusIcon = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
	By loc_btnCreateReceipt = By.xpath(dropdownOption.formatted("and @index='0'"));
	By loc_BtnCreatePayment = By.xpath(dropdownOption.formatted("and @index='1'"));

	By loc_txtRecordSearchBox = By.xpath("//*[ends-with(@resource-id,'edtCashbookSearch')]");

	By loc_txtDateRange = By.xpath("//*[ends-with(@resource-id,'tvFilterFromDateToDate')]");
	By loc_lblMonthTitle = By.xpath("//*[ends-with(@resource-id,'title') and @index='0']");
	By loc_calWholeMonth = By.xpath("(//*[ends-with(@resource-id,'title')]/parent::*)[1]");

	By loc_ddlGroup = By.xpath("//*[ends-with(@resource-id,'tvSenderGroup')]");
	By loc_ddlSenderName = By.xpath("//*[ends-with(@resource-id,'tvSelectSenderName')]");
	By loc_txtSenderSearchBox = By.xpath("//*[ends-with(@resource-id,'edtSearchSenderRecipient')]");
	By loc_lblSenderSearchResult(String senderName) {
		return By.xpath("//*[ends-with(@resource-id,'tvFilterText') and @text=\"%s\"]".formatted(senderName));
	}

	By loc_ddlRevenue = By.xpath("//*[ends-with(@resource-id,'tvSelectRevenue')]");

	By loc_ddlBranch = By.xpath("//*[ends-with(@resource-id,'tvSelectBranch')]");

	By loc_txtAmount = By.xpath("//*[ends-with(@resource-id,'edtPriceCustom')]");

	By loc_ddlPaymentMethod = By.xpath("//*[ends-with(@resource-id,'tvSelectPaymentMethod')]");

	By loc_txtNote = By.xpath("//*[ends-with(@resource-id,'edtNote')]");

	By loc_chkAccounting = By.xpath("//*[ends-with(@resource-id,'cbxAccounting')]");

	By loc_btnSaveRecord = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");

	By loc_lblOpeningBalance = By.xpath("//*[ends-with(@resource-id,'tvOpening')]");
	By loc_lblTotalRevenue = By.xpath("//*[ends-with(@resource-id,'tvTotalRev')]");
	By loc_lblTotalExpenditure = By.xpath("//*[ends-with(@resource-id,'tvExpenditure')]");
	By loc_lblEndingBalance = By.xpath("//*[ends-with(@resource-id,'tvEnding')]");
	
	By loc_lblRecordId = By.xpath("//*[ends-with(@resource-id,'tvCashbookId')]");
	By loc_lblRecordDate = By.xpath("//*[ends-with(@resource-id,'tvDate')]");
	By loc_lblRecordBranch = By.xpath("//*[ends-with(@resource-id,'tvAddress')]");
	By loc_lblRecordType = By.xpath("//*[ends-with(@resource-id,'tvType')]");
	By loc_lblRecordSender = By.xpath("//*[ends-with(@resource-id,'tvName')]");
	By loc_lblRecordCreatedBy = By.xpath("//*[ends-with(@resource-id,'tvOwner')]");
	By loc_lblRecordAmount = By.xpath("//*[ends-with(@resource-id,'tvPrice')]");

	public List<BigDecimal> getCashbookSummary() {
		UICommonMobile.sleepInMiliSecond(500, "Wait in getCashbookSummary()"); // Sometimes it takes longer for the element to change its data
		By[] cashbookSummaryLocator = { loc_lblOpeningBalance, loc_lblTotalRevenue, loc_lblTotalExpenditure, loc_lblEndingBalance };
		
		List<BigDecimal> summary = new ArrayList<>();
		for (By bySelector : cashbookSummaryLocator) {
			// Sometimes element is present but the data it contains is not yet rendered
			String text = "";
			for (int i = 0; i < 5; i++) {
				text = commonAction.getText(bySelector);
				if (!text.isEmpty()) break;
				UICommonMobile.sleepInMiliSecond(1000);
			}
			summary.add(new BigDecimal(DataGenerator.extractDigits(text)));
		}
		
		logger.info("Cashbook summary: {}", summary);
		return summary;
	}

	public List<String> getSpecificRecord(int index) {
		List<By> columns = Arrays.asList(loc_lblRecordId, loc_lblRecordDate, loc_lblRecordBranch, loc_lblRecordType, loc_lblRecordSender, loc_lblRecordCreatedBy, loc_lblRecordAmount);
		
		List<String> recordInfo = columns.stream().map(e -> commonAction.getText(e)).toList();
		
		logger.info("Record info: {}", recordInfo);
		
		return recordInfo;
	}

	public Cashbook swipeThroughRecords() {

		int recordCount = commonAction.getElements(loc_lblRecordType).size();
		if (recordCount<2) {
			logger.info("Record count is {}. Stopping swipeThroughRecords()", recordCount);
			return this;
		}

		Dimension deviceScreenSize = driver.manage().window().getSize();

		String rawRecordTypeBounds = commonAction.getElement(loc_lblRecordType).getAttribute("bounds");
		List<String> recordTypeBounds = new ArrayList<>();
		Matcher matchedResult = Pattern.compile("\\d+").matcher(rawRecordTypeBounds);
		while (matchedResult.find()) {
			recordTypeBounds.add(matchedResult.group());
		}

		String rawExpenditureSummaryBounds = commonAction.getElement(loc_lblTotalExpenditure).getAttribute("bounds");
		List<String> expenditureSummaryBounds = new ArrayList<>();
		Matcher matchedResult1 = Pattern.compile("\\d+").matcher(rawExpenditureSummaryBounds);
		while (matchedResult1.find()) {
			expenditureSummaryBounds.add(matchedResult1.group());
		}
		
		double startY = (double) Double.valueOf(recordTypeBounds.get(3)) / deviceScreenSize.height;
		double endY = (double) Double.sum(Double.valueOf(expenditureSummaryBounds.get(1)), Double.valueOf(expenditureSummaryBounds.get(3))) /2 / deviceScreenSize.height;
		double swipeDiff = Math.abs(startY-endY);
		
		if (String.valueOf(swipeDiff).matches("0\\.00\\d*")) {
			logger.info("A swipe diff of {} is too small. This will act as a tap instead. Stopping swipeThroughRecords()", swipeDiff);
			return this;
		}
		
		commonAction.swipeByCoordinatesInPercent(0.5, startY, 0.5, endY, 1800);
		return this;
	}
	
	public Cashbook inputCashbookSearchTerm(String searchTerm) {
		commonAction.inputText(loc_txtRecordSearchBox, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}

	public Cashbook clickTimeRangeFilter() {
		commonAction.clickElement(loc_txtDateRange);
		logger.info("Clicked on time range filter.");
		return this;
	}

	public String getCurrentTimeRangeFilter() {
		String timeRange = commonAction.getText(loc_txtDateRange);
		logger.info("Retrived current date range: " + timeRange);
		return timeRange;
	}

	public Cashbook setDateFilter(int currentDay, int currentMonth, int currentYear, int desiredDay, int desiredMonth, int desiredYear) {
		int[] swipeCount = calculateMonthDiff(currentDay, currentMonth, currentYear, desiredDay, desiredMonth, desiredYear);
		travelTime(swipeCount[0], swipeCount[1]);

		By DAY = By.xpath("//*[ends-with(@resource-id,'calendar_grid')]/*/*[@enabled='true']//*[@text='%s']".formatted(desiredDay));
		commonAction.clickElement(new ByChained(loc_calWholeMonth, DAY));

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
			logger.debug("Month Title: " + commonAction.getText(loc_lblMonthTitle));

			String monthTitleBounds = commonAction.getElement(loc_lblMonthTitle).getAttribute("bounds");
			String monthBounds = commonAction.getElement(loc_calWholeMonth).getAttribute("bounds");

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
		logger.info("Clicked 'Apply' button to filter records according to time.");
		return this;
	}
	
	public Cashbook clickCancelDateBtn() {
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'id/tvCalendarCancel')]"));
		logger.info("Clicked 'Cancel' button to abort filtering according to time.");
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
		commonAction.clickElement(By.xpath("//*[@text=\"%s\"]".formatted(branch)));
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
		commonAction.clickElement(By.xpath("//*[@text=\"%\"]".formatted(createdBy)));
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
		commonAction.clickElement(loc_lblRecordId);
		UICommonMobile.sleepInMiliSecond(500, "In clickRecord()"); //Sometimes it takes longer for the detail screen to load. Temporary
		logger.info("Clicked record: {}", recordID);
		return this;
	}

	//TODO: This function is temporary and will be deleted soon
	public void waitUntilPlusIconAppears() {
		commonAction.getElement(loc_btnPlusIcon, defaultTimeout);
	}
	
	public Cashbook clickCreateBtn() {
		commonAction.clickElement(loc_btnPlusIcon, defaultTimeout);
		logger.info("Clicked create (+) button.");
		return this;
	}

	public Cashbook clickCreateReceiptBtn() {
		commonAction.clickElement(loc_btnCreateReceipt, defaultTimeout);
		logger.info("Clicked 'Create Receipt'.");
		return this;
	}

	public Cashbook clickCreatePaymentBtn() {
		commonAction.clickElement(loc_BtnCreatePayment, defaultTimeout);
		logger.info("Clicked on 'Create Payment' button.");
		return this;
	}

	public Cashbook selectGroup(String group) {
		commonAction.clickElement(loc_ddlGroup);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text='%s'".formatted(group))));
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name) {
		commonAction.clickElement(loc_ddlSenderName);

		//The search box element gets stale sometimes and more frequent on CI env. The exception is vague so it's hard to apply try catch mechanism in function inputText. See #issue1
		try {
			commonAction.inputText(loc_txtSenderSearchBox, name);
		} catch (WebDriverException e) {
			commonAction.inputText(loc_txtSenderSearchBox, name);
		}
		
		try {
			commonAction.clickElement(loc_lblSenderSearchResult(name));
		} catch (TimeoutException exception) {
			logger.info("Can't find sender '{}'. It's likely to be obstructed. Trying swiping down a little", name);
			commonAction.swipeByCoordinatesInPercent(0.5, 0.2, 0.5, 0.8, 200); //Some times search results are obstructed
			commonAction.clickElement(loc_lblSenderSearchResult(name));
		}
		
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}

	public List<String> getDropdownValues() {
		// Sometimes it takes longer for the values to display
		int elementCount = 0;
		for (int i = 0; i < 3; i++) {
			elementCount = commonAction.getElements(By.xpath(dropdownOption.formatted(""))).size();
			if (elementCount > 0) break;
		}

		// Store all option values into an array then return the array
		List<String> values = new ArrayList<>();
		for (int i = 0; i < elementCount; i++) {
			values.add(commonAction.getText(By.xpath(dropdownOption.formatted("and @index='%s'".formatted(i)))));
		}
		return values;
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown
	 * list for revenue source.
	 *
	 * @return An array of strings representing the dropdown values
	 */
	public List<String> getSourceDropdownValues() {
		commonAction.clickElement(loc_ddlRevenue);
		List<String> values = getDropdownValues();
		commonAction.navigateBack();
		return values;
	}

	public Cashbook selectRevenueExpense(String revenueExpense) {
		commonAction.clickElement(loc_ddlRevenue);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text=\"%s\"".formatted(revenueExpense))));
		logger.info("Selected Revenue Source/Expense Type: %s.".formatted(revenueExpense));
		return this;
	}

	public Cashbook selectBranch(String branch) {
		commonAction.clickElement(loc_ddlBranch);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text=\"%s\"".formatted(branch))));
		logger.info("Selected Branch: %s.".formatted(branch));
		return this;
	}

	public Cashbook inputAmount(String amount) {
		commonAction.inputText(loc_txtAmount, amount);
		logger.info("Input amount: %s.".formatted(amount));
		return this;
	}

	/**
	 * Returns a list of payment method drop-down options. Eg. ["PAYPAL", "MOMO"]
	 */
	public List<String> getPaymentMethodDropdownValues() {
		commonAction.clickElement(loc_ddlPaymentMethod);
		List<String> values = getDropdownValues();
		commonAction.navigateBack();
		return values;
	}

	public Cashbook selectPaymentMethod(String paymentMethod) {
		commonAction.clickElement(loc_ddlPaymentMethod);
		commonAction.clickElement(By.xpath(dropdownOption.formatted("and @text=\"%s\"".formatted(paymentMethod))));
		logger.info("Selected Payment Method: %s.".formatted(paymentMethod));
		return this;
	}

	public Cashbook inputNote(String note) {
		commonAction.inputText(loc_txtNote, note);
		logger.info("Input note: %s.".formatted(note));
		return this;
	}

	public boolean isAccountingChecked() {
		boolean isChecked = commonAction.isElementChecked(loc_chkAccounting);
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
			commonAction.clickElement(loc_chkAccounting);
			logger.info("Checked Account checkbox");
		} else {
			if (!isAccountingChecked())
				return this;
			commonAction.clickElement(loc_chkAccounting);
			logger.info("Un-checked Account checkbox");
		}
		return this;
	}

	public Cashbook clickSaveBtn() {
		commonAction.clickElement(loc_btnSaveRecord);
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
	
	/**
	 * Use this before interacting with any records
	 */
	public void waitUntilLoadingIconDisappear() {
		commonAction.waitInvisibilityOfElementLocated(By.xpath("//*[ends-with(@resource-id, 'id/srlRefresh')]//*[ends-with(@class, 'widget.ImageView')]"));
		logger.info("Loading Icon has disappeared.");
	}

	public String getGroup() {
		String text = commonAction.getText(loc_ddlGroup);
		logger.info("Retrieved Group value from record details: " + text);
		return text;
	}

	public String getName() {
		String text = commonAction.getText(loc_ddlSenderName);
		logger.info("Retrieved name value from record details: " + text);
		return text;
	}

	public String getSourceOrExpense() {
		String text = commonAction.getText(loc_ddlRevenue);
		logger.info("Retrieved Source/Expense value from record details: " + text);
		return text;
	}

	public String getBranch() {
		String text = commonAction.getText(loc_ddlBranch);
		logger.info("Retrieved Branch value from record details: " + text);
		return text;
	}

	public String getPaymentMethod() {
		String text = commonAction.getText(loc_ddlPaymentMethod);
		logger.info("Retrieved Payment method value from record details: " + text);
		return text;
	}

	public String getAmount() {
		String text = commonAction.getText(loc_txtAmount);
		logger.info("Retrieved Amount value from record details: " + text);
		return text;
	}

	public String getNote() {
		String text = commonAction.getText(loc_txtNote);
		logger.info("Retrieved Note value from record details: " + text);
		return text;
	}
	
	/**
	 * #issue1: org.openqa.selenium.WebDriverException: An unknown server-side error occurred while processing the command. Original error: Cannot invoke method private android.view.accessibility.AccessibilityNodeInfo androidx.test.uiautomator.UiObject2.getAccessibilityNodeInfo() on object androidx.test.uiautomator.UiObject2@16193e with parameters []
	 */
}
