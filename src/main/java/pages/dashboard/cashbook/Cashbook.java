package pages.dashboard.cashbook;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public static final int OPENINGBALANCE_IDX = 0;
	public static final int TOTALREVENUE_IDX = 1;
	public static final int TOTALEXPENDITURE_IDX = 2;
	public static final int ENDINGBALANCE_IDX = 3;
	
	public static final int TRANSACTIONCODE_COL = 0;
	public static final int CREATEDDATE_COL = 1;
	public static final int BRANCH_COL = 2;
	public static final int REVENUETYPE_COL = 3;
	public static final int EXPENSETYPE_COL = 4;
	public static final int NAME_COL = 5;
	public static final int CREATEDBY_COL = 6;
	public static final int AMOUNT_COL = 7;
	
	String otherVIE = null;
	String otherENG = null;
	
	public void translateOthers() {
		try {
			otherVIE = PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.group.others", "VIE");
			otherENG = PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.group.others", "ENG");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Cashbook(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		translateOthers();
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".gs-page-title")
	WebElement PAGE_TITLE;
	
	@FindBy(css = ".cashbook-summary .number")
	List<WebElement> CASHBOOKSUMMARY;

	@FindBy(css = ".tippy-tooltip-content")
	WebElement TOOLTIP;	
	
	@FindBy(xpath = "//div[contains(@class,'cashbook-list')]//table/tbody/tr")
	List<WebElement> CASHBOOK_RECORDS;

	@FindBy(xpath = "//div[contains(@class,'cashbook-list')]//table/thead/tr")
	WebElement TABLEHEADER;		
	
	@FindBy(css = ".uik-input__input")
	WebElement CASHBOOK_SEARCH_BOX;		
	
	@FindBy(css = ".gs-content-header-right-el .gs-button__green:nth-of-type(1)")
	WebElement CREATE_RECEIPT_BTN;
	
	@FindBy(css = ".gs-content-header-right-el .gs-button__green:nth-of-type(2)")
	WebElement CREATE_PAYMENT_BTN;
	
	@FindBy(css = ".date-ranger-picker")
	WebElement DATE_RANGER_PICKER;
	
	@FindBy(css = ".daterangepicker .btn-default")
	WebElement DATE_RANGER_PICKER_RESET_BTN;
	
	@FindBy(css = ".btn-filter-action")
	WebElement FILTER_BTN;
	
	@FindBy(css = ".mega-filter-container .dropdown-menu-right")
	WebElement FILTER_CONTAINER;
	
	@FindBy(xpath = "//div[contains(@class,'gs-mega-filter-row-select')]")
	List<WebElement> FILTER_CONDITION;
	
	@FindBy(css = ".gs-button__green.gs-button--small")
	WebElement FILTER_DONE_BTN;
	
	@FindBy(css = ".modal-title")
	WebElement CREATE_RECEIPT_PAYMENT_MODAL_TITLE;

	@FindBy(xpath = "(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[1]")
	WebElement SENDERGROUP_DROPDOWN;

	@FindBy(xpath = "(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[2]")
	WebElement REVENUE_SOURCE_DROPDOWN;

	@FindBy(xpath = "(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[3]")
	WebElement BRANCH_DROPDOWN;

	@FindBy(xpath = "(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[4]")
	WebElement PAYMENT_METHOD_DROPDOWN;

	@FindBy(css = "[class*=gs-dropdown-search]")
	WebElement SENDER_NAME_DROPDOWN;
	
	@FindBy(css = ".search-box input")
	WebElement SEARCH_BOX;

	By AMOUNT = By.id("amount");

	@FindBy(id = "note")
	WebElement NOTE;

	@FindBy(name = "accounting")
	WebElement ACCOUNTING_CHECKBOX;

	@FindBy(css = ".footer.modal-footer .gs-button__white")
	WebElement CANCEL_BTN;

	@FindBy(css = ".footer.modal-footer .gs-button__green")
	WebElement SAVE_BTN;

	public Cashbook navigate() {
		new HomePage(driver).navigateToPage("Cashbook");
		commonAction.sleepInMiliSecond(5000);
		new HomePage(driver).hideFacebookBubble();
		return this;
	}

	public List<Long> getCashbookSummary() {
		List<Long> summary = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String rawAmount = commonAction.getText(CASHBOOKSUMMARY.get(i));
			Matcher m = Pattern.compile("\\d+").matcher(rawAmount);
			ArrayList<String> sub = new ArrayList<String>();
			while (m.find()) {
				sub.add(m.group());
			}
			summary.add(Long.parseLong(String.join("", sub)));
		}
		return summary;
	}
	
	public List<String> getSpecificRecord(int index) {
		for (int i=0; i<6; i++) {
			if (!CASHBOOK_RECORDS.isEmpty()) break;
			commonAction.sleepInMiliSecond(500);
		}
		
		List<String> rowData = new ArrayList<>();
		for (WebElement column : CASHBOOK_RECORDS.get(index).findElements(By.xpath("./td"))) {
			rowData.add(column.getText());
		}
		return rowData;
	}

	public List<List<String>> getRecords() {
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<CASHBOOK_RECORDS.size(); i++) {
			table.add(getSpecificRecord(i));
		}
		return table;
	}

	public Cashbook clickCreateReceiptBtn() {
		commonAction.clickElement(CREATE_RECEIPT_BTN);
		commonAction.sleepInMiliSecond(1000);
		logger.info("Clicked on Create Receipt button.");
		return this;
	}

	public Cashbook clickCreatePaymentBtn() {
		commonAction.clickElement(CREATE_PAYMENT_BTN);
		commonAction.sleepInMiliSecond(1000);
		logger.info("Clicked on Create Payment button.");
		return this;
	}	

	public Cashbook inputCashbookSearchTerm(String searchTerm) {
		commonAction.inputText(CASHBOOK_SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}	
	
	public Cashbook selectGroup(String group) {
		commonAction.clickElement(SENDERGROUP_DROPDOWN);
		By groupLocator = By.xpath(".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(group));
		commonAction.clickElement(SENDERGROUP_DROPDOWN.findElement(groupLocator));
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name) {
		commonAction.clickElement(SENDER_NAME_DROPDOWN);
		commonAction.inputText(SEARCH_BOX, name);
		new HomePage(driver).waitTillSpinnerDisappear1();
		By customerLocator = By.xpath("//div[contains(@class,'search-item') and text()='%s']".formatted(name));
		WebElement sender = wait.until(ExpectedConditions.visibilityOfElementLocated(customerLocator));
		
		commonAction.sleepInMiliSecond(500); //There's something wrong here. Without this delay, names are not selected
		
		//The element will go stale after the delay, so we fetch the element again
		sender = wait.until(ExpectedConditions.visibilityOfElementLocated(customerLocator));
		commonAction.clickElement(sender);
		
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}
	
	public Cashbook selectOthersName(String name) {
		commonAction.clickElement(SENDER_NAME_DROPDOWN);
		By customerLocator = By.xpath("//div[contains(@class,'search-item') and text()='%s']".formatted(name));
		WebElement sender = wait.until(ExpectedConditions.visibilityOfElementLocated(customerLocator));
		commonAction.clickElement(sender);
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}
	
	/**
	 * This method returns an array of strings representing the values of a dropdown list for revenue source.
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getSourceDropdownValues() {
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN);
		String text = commonAction.getText(driver.findElement(By.cssSelector(".uik-select__optionListWrapper")));
		String[] values = text.split("\n");
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN);
		return values;
	}

	public Cashbook selectRevenueExpense(String revenueExpense) {
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN);
		String xpath = ".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(revenueExpense);
		commonAction.clickElement(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath(xpath)));
		logger.info("Selected Revenue Source/Expense Type: %s.".formatted(revenueExpense));
		return this;
	}

	public Cashbook selectBranch(String branch) {
		commonAction.clickElement(BRANCH_DROPDOWN);
		String xpath = ".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(branch);
		commonAction.clickElement(BRANCH_DROPDOWN.findElement(By.xpath(xpath)));
		logger.info("Selected Branch: %s.".formatted(branch));
		return this;
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown list for payment methods.
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getPaymentMethodDropdownValues() {
		commonAction.clickElement(PAYMENT_METHOD_DROPDOWN);
		String text = commonAction.getText(driver.findElement(By.cssSelector(".uik-select__optionListWrapper")));
		String[] values = text.split("\n");
		commonAction.clickElement(PAYMENT_METHOD_DROPDOWN);
		return values;
	}	
	
	public Cashbook selectPaymentMethod(String paymentMethod) {
		commonAction.clickElement(PAYMENT_METHOD_DROPDOWN);
		String xpath = ".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(paymentMethod);
		commonAction.clickElement(PAYMENT_METHOD_DROPDOWN.findElement(By.xpath(xpath)));
		logger.info("Selected Payment Method: %s.".formatted(paymentMethod));
		return this;
	}

	public Cashbook inputAmount(String amount) {
		WebElement el = driver.findElement(AMOUNT).findElement(By.xpath("./parent::*/parent::*/preceding-sibling::input"));
		el.clear();
		el = driver.findElement(AMOUNT).findElement(By.xpath("./parent::*/parent::*/preceding-sibling::input"));
		el.sendKeys(amount);
		logger.info("Input amount: %s.".formatted(amount));
		return this;
	}

	public Cashbook inputNote(String note) {
		commonAction.inputText(NOTE, note);
		logger.info("Input note: %s.".formatted(note));
		return this;
	}
	
	/**
	 * @param isChecked If true => check the box, if false => un-check the box
	 */
	public Cashbook checkAccountingCheckbox(boolean isChecked) {
		if (isChecked) {
			if (isAccountingChecked()) return this;
			commonAction.clickElement(ACCOUNTING_CHECKBOX.findElement(By.xpath("./parent::*")));
			logger.info("Checked Account checkbox");
		} else {
			if (!isAccountingChecked()) return this;
			commonAction.clickElement(ACCOUNTING_CHECKBOX.findElement(By.xpath("./parent::*")));
			logger.info("Un-checked Account checkbox");
		}
		return this;
	}
	
	public Cashbook clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on Save button.");
		return this;
	}

	public Cashbook clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on Cancel button.");
		return this;
	}

	public Cashbook createReceiptPaymentOverlap(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
		selectGroup(senderGroup);
		selectRevenueExpense(revenue);
		selectBranch(branch);
		selectPaymentMethod(payment);
		if (senderGroup.contentEquals(otherENG) || senderGroup.contentEquals(otherVIE)) {
			selectOthersName(senderName);
		} else {
			selectName(senderName);
		}
		inputAmount(amount);
		inputNote(note);
		checkAccountingCheckbox(isChecked);
		clickSaveBtn();
		return this;
	}	
	
	public Cashbook createReceipt(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
		clickCreateReceiptBtn();
		createReceiptPaymentOverlap(senderGroup, revenue, branch, payment, senderName, amount, note, isChecked);
		return this;
	}
	
	public Cashbook createPayment(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
		clickCreatePaymentBtn();
		createReceiptPaymentOverlap(senderGroup, revenue, branch, payment, senderName, amount, note, isChecked);
		return this;
	}

	public Cashbook clickRecord(String recordID) {
		WebElement record = driver.findElement(By.xpath("//td[text()='%s']".formatted(recordID)));
		commonAction.clickElement(record);
		logger.info("Clicked on cashbook record '%s'.".formatted(recordID));
		return this;
	}

	public String getGroup() {
		logger.info("Getting Group value from Transaction Id Popup");
		return commonAction.getText(SENDERGROUP_DROPDOWN);
	}

	public String getName() {
		String text = commonAction.getElementAttribute(
				SENDER_NAME_DROPDOWN.findElement(By.xpath(".//div[@class='form-group']/input")), "value");
		logger.info("Retrieved name from Transaction Id Popup: " + text);
		return text;
	}

	public String getSourceOrExpense() {
		logger.info("Getting Source/Expense value from Transaction Id Popup");
		return commonAction.getText(REVENUE_SOURCE_DROPDOWN);
	}

	public String getBranch() {
		logger.info("Getting Branch value from Transaction Id Popup");
		return commonAction.getText(BRANCH_DROPDOWN);
	}

	public String getPaymentMethod() {
		logger.info("Getting Payment method value from Transaction Id Popup");
		return commonAction.getText(PAYMENT_METHOD_DROPDOWN);
	}

	public String getAmount() {
		String text = commonAction.getElementAttribute(driver.findElement(AMOUNT), "value");
		logger.info("Retrieved Amount from Transaction Id Popup: " + text);
		return text;
	}

	public String getNote() {
		String text = commonAction.getElementAttribute(NOTE, "value");
		logger.info("Retrieved Note from Transaction Id Popup: " + text);
		return text;
	}

	public boolean isAccountingChecked() {
		boolean text = ACCOUNTING_CHECKBOX.isSelected();
		logger.info("Is accounting checked: " + text);
		return text;
	}

    public void verifyTextAtCashbookManagementScreen() throws Exception {
    	new HomePage(driver).hideFacebookBubble();
    	String text = commonAction.getText(PAGE_TITLE).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.title"), text);
    	text = commonAction.getText(CREATE_RECEIPT_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createReceiptBtn"));
    	text = commonAction.getText(CREATE_PAYMENT_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createPaymentBtn"));
    	text = commonAction.getText(FILTER_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.filterBtn"));
    	
    	text = commonAction.getText(CASHBOOKSUMMARY.get(0).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.openingBalance"));
    	commonAction.hoverOverElement(CASHBOOKSUMMARY.get(0).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(TOOLTIP);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.openingBalance"));
    	
    	text = commonAction.getText(CASHBOOKSUMMARY.get(1).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalRevenue"));
    	commonAction.hoverOverElement(CASHBOOKSUMMARY.get(1).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(TOOLTIP);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalRevenue"));
    	
    	text = commonAction.getText(CASHBOOKSUMMARY.get(2).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalExpenditure"));
    	commonAction.hoverOverElement(CASHBOOKSUMMARY.get(2).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(TOOLTIP);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalExpenditure"));
    	    	
    	text = commonAction.getText(CASHBOOKSUMMARY.get(3).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.endingBalance"));
    	commonAction.hoverOverElement(CASHBOOKSUMMARY.get(3).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(TOOLTIP);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.endingBalance"));
    	
    	text = commonAction.getElementAttribute(CASHBOOK_SEARCH_BOX, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.searchBox"));
    	
    	text = commonAction.getText(TABLEHEADER);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tableHeader"));
    	
    	logger.info("verifyTextAtCashbookManagementScreen completed");
    }  	
	
    public void verifyTextAtCreateReceiptScreen() throws Exception {
    	String text = commonAction.getText(CREATE_RECEIPT_PAYMENT_MODAL_TITLE);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.title"), text);
    	
    	text = commonAction.getText(SENDERGROUP_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(SENDER_NAME_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	text = commonAction.getElementAttribute(SENDER_NAME_DROPDOWN.findElement(By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(BRANCH_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(driver.findElement(AMOUNT).findElement(By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(PAYMENT_METHOD_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(NOTE.findElement(By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getElementAttribute(NOTE, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(ACCOUNTING_CHECKBOX.findElement(By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = commonAction.getText(CANCEL_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = commonAction.getText(SAVE_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
    
    public void verifyTextAtReceiptTransactionIDScreen() throws Exception {
    	/*
    	 * Remember to add code to verify pop-up title.
    	 */
    	
    	String text = commonAction.getText(SENDERGROUP_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(SENDER_NAME_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	
    	text = commonAction.getText(BRANCH_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(driver.findElement(AMOUNT).findElement(By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(PAYMENT_METHOD_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(NOTE.findElement(By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(ACCOUNTING_CHECKBOX.findElement(By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = commonAction.getText(CANCEL_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = commonAction.getText(SAVE_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtReceiptTransactionIDScreen completed");
    }  	
    
    public void verifyTextAtCreatePaymentScreen() throws Exception {
    	String text = commonAction.getText(CREATE_RECEIPT_PAYMENT_MODAL_TITLE);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.title"));
    	text = commonAction.getText(SENDERGROUP_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(SENDER_NAME_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getElementAttribute(SENDER_NAME_DROPDOWN.findElement(By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(BRANCH_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(driver.findElement(AMOUNT).findElement(By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(PAYMENT_METHOD_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(NOTE.findElement(By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getElementAttribute(NOTE, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(ACCOUNTING_CHECKBOX.findElement(By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = commonAction.getText(CANCEL_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = commonAction.getText(SAVE_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
    
    public void verifyTextAtPaymentTransactionIDScreen() throws Exception {
    	/*
    	 * Remember to add code to verify pop-up title.
    	 */
    	
    	String text = commonAction.getText(SENDERGROUP_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(SENDER_NAME_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getText(REVENUE_SOURCE_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	
    	text = commonAction.getText(BRANCH_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(driver.findElement(AMOUNT).findElement(By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(PAYMENT_METHOD_DROPDOWN.findElement(By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(NOTE.findElement(By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(ACCOUNTING_CHECKBOX.findElement(By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = commonAction.getText(CANCEL_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = commonAction.getText(SAVE_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
	
	public Cashbook clickResetDateRangerPicker() {
		commonAction.clickElement(DATE_RANGER_PICKER);
		commonAction.clickElement(DATE_RANGER_PICKER_RESET_BTN);
		logger.info("Clicked on Reset Time ranger picker button.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}    
	
	public Cashbook clickFilterBtn() {
		commonAction.clickElement(FILTER_BTN);
		logger.info("Clicked on Filter button.");
		return this;
	}    

	public Cashbook selectFilteredBranch(String branch) {
		commonAction.clickElement(FILTER_CONDITION.get(0));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(branch);
		commonAction.clickElement(FILTER_CONDITION.get(0).findElement(By.xpath(xpath)));
		logger.info("Selected filtered branch: %s.".formatted(branch));
		return this;
	}	
	
	public Cashbook selectFilteredAccounting(String yesOrNo) {
		commonAction.clickElement(FILTER_CONDITION.get(1));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(yesOrNo);
		commonAction.clickElement(FILTER_CONDITION.get(1).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Accounting: %s.".formatted(yesOrNo));
		return this;
	}	
	
	public Cashbook selectFilteredTransaction(String transaction) {
		commonAction.clickElement(FILTER_CONDITION.get(2));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(transaction);
		commonAction.clickElement(FILTER_CONDITION.get(2).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Transaction: %s.".formatted(transaction));
		return this;
	}	
	
	public Cashbook selectFilteredExpenseType(String expenseType) {
		commonAction.clickElement(FILTER_CONDITION.get(3));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(expenseType);
		commonAction.clickElement(FILTER_CONDITION.get(3).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Expense type: %s.".formatted(expenseType));
		return this;
	}	
	
	public Cashbook selectFilteredRevenueType(String revenueType) {
		commonAction.clickElement(FILTER_CONDITION.get(4));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(revenueType);
		commonAction.clickElement(FILTER_CONDITION.get(4).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Revenue type: %s.".formatted(revenueType));
		return this;
	}	
	
	public Cashbook selectFilteredCreatedBy(String createdBy) {
		commonAction.clickElement(FILTER_CONDITION.get(5));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(createdBy);
		commonAction.clickElement(FILTER_CONDITION.get(5).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Created by: %s.".formatted(createdBy));
		return this;
	}	
	
	public Cashbook selectFilteredGroup(String group) {
		commonAction.clickElement(FILTER_CONDITION.get(6));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(group);
		commonAction.clickElement(FILTER_CONDITION.get(6).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Sender/Recipient group: %s.".formatted(group));
		return this;
	}	
	
	public Cashbook selectFilteredName1(String name) {
		commonAction.clickElement(FILTER_CONDITION.get(7));
		String xpath = ".//div[@class='option-item ' and text()='%s']".formatted(name);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		commonAction.clickElement(FILTER_CONDITION.get(7).findElement(By.xpath(xpath)));
		logger.info("Selected filtered Sender/Recipient name: %s.".formatted(name));
		return this;
	}	
	
	public Cashbook selectFilteredName(String name) {
		commonAction.clickElement(FILTER_CONDITION.get(7));
		
		By optionLocator = By.xpath("//div[@class='option-item ']");
		By targetedOption = By.xpath(".//div[@class='option-item ' and text()='%s']".formatted(name));
		
		int previousSize = -1;
		int currentSize = 0;
		
		for (int i=0; i<5; i++) {
			commonAction.sleepInMiliSecond(1000);
			if (!driver.findElements(optionLocator).isEmpty()) break;
		}

		while (previousSize != currentSize) {
			
			List<WebElement> options = driver.findElements(optionLocator);
			commonAction.scrollToElement(options.get(options.size()-1));
			commonAction.sleepInMiliSecond(1500);
	
			previousSize = currentSize;
			currentSize = driver.findElements(optionLocator).size();
			
			if (!driver.findElements(targetedOption).isEmpty()) {
				commonAction.clickElement(FILTER_CONDITION.get(7).findElement(targetedOption));
				break;
			}
		}

		logger.info("Selected filtered Sender/Recipient name: %s.".formatted(name));
		return this;
	}	
	
	public Cashbook selectFilteredPaymentMethod(String method) {
		commonAction.clickElement(FILTER_CONDITION.get(8));
		String xpath = ".//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']".formatted(method);
		commonAction.clickElement(FILTER_CONDITION.get(8).findElement(By.xpath(xpath)));
		logger.info("Selected filtered payment method: %s.".formatted(method));
		return this;
	}	
	
	
	public Cashbook clickFilterDoneBtn() {
		commonAction.clickElement(FILTER_DONE_BTN);
		logger.info("Clicked on Filter Done button.");
		return this;
	}    

    public void verifyTextAtFilterContainer() throws Exception {
    	String text = commonAction.getText(FILTER_DONE_BTN.findElement(By.xpath("./ancestor::div[contains(@class,'dropdown-menu-right')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.filterContainer"));
    	logger.info("verifyTextAtFilterContainer completed");
    }  		
	
}
