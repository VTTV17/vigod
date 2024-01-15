package pages.dashboard.cashbook;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
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
		commonAction = new UICommonAction(driver);
		translateOthers();
	}

	By loc_lblPageTitle = By.cssSelector(".gs-page-title");
	By loc_lblCashbookSummary = By.cssSelector(".cashbook-summary .number");
	By loc_tltCashbookSummary = By.cssSelector(".tippy-tooltip-content");
	By loc_tblCashbookRecord = By.xpath("//div[contains(@class,'cashbook-list')]//table/tbody/tr");
	By loc_tblTableTitle = By.xpath("//div[contains(@class,'cashbook-list')]//table/thead/tr");
	By loc_txtSearchRecord = By.cssSelector(".uik-input__input");
	By loc_btnCreateReceipt = By.cssSelector(".gs-content-header-right-el .gs-button__green:nth-of-type(1)");
	By loc_btnCreatePayment = By.cssSelector(".gs-content-header-right-el .gs-button__green:nth-of-type(2)");
	By loc_dtpPrincipleTimeRange = By.cssSelector(".date-ranger-picker");
	By loc_btnResetDatePicker = By.cssSelector(".daterangepicker .btn-default");
	By loc_btnFilter = By.cssSelector(".btn-filter-action");
	By loc_frmFilterContainer = By.cssSelector(".mega-filter-container .dropdown-menu-right");
	By loc_frmFilterCondition = By.xpath("//div[contains(@class,'gs-mega-filter-row-select')]");
	By loc_btnFilterDone = By.cssSelector(".gs-button__green.gs-button--small");
	By loc_lblCreateReceiptModalDialog = By.cssSelector(".modal-title");
	By loc_ddlSenderGroup = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[1]");
	By loc_ddlSource = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[2]");
	By loc_ddlBranch = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[3]");
	By loc_ddlPaymentMethod = By.xpath("(//div[contains(@class,'cashbook-receipt-payment-modal')]//form//div[contains(@class,'uik-select__wrapper')])[4]");
	By loc_ddlSenderName = By.cssSelector("[class*=gs-dropdown-search]");
	By loc_txtSearchSenderName = By.cssSelector(".search-box input");
	By loc_txtAmount = By.id("amount");
	By loc_txtNote = By.id("note");
	By loc_chkAccounting = By.name("accounting");

	String conditionFilterDropdownXpath = "//div[contains(@class,'undefined')]//div[@class='uik-select__label' and text()='%s']";
	
	public Cashbook navigate() {
		new HomePage(driver).navigateToPage("Cashbook");
		commonAction.sleepInMiliSecond(5000);
		commonAction.removeFbBubble();
		return this;
	}

	public List<Long> getCashbookSummary() {
		List<Long> summary = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String rawAmount = commonAction.getText(loc_lblCashbookSummary, i);
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
		//Wait until records are present
		for (int i=0; i<6; i++) {
			if (!commonAction.getElements(loc_tblCashbookRecord).isEmpty()) break;
			commonAction.sleepInMiliSecond(500);
		}
		
		/*
		 * Loop through the columns of the specific record
		 * and store data of the column into an array.
		 * Retry the process when StaleElementReferenceException occurs
		 */
		try {
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commonAction.getElement(loc_tblCashbookRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commonAction.getElement(loc_tblCashbookRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		}
	}

	public List<List<String>> getRecords() {
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<commonAction.getElements(loc_tblCashbookRecord).size(); i++) {
			table.add(getSpecificRecord(i));
		}
		return table;
	}

	public Cashbook clickCreateReceiptBtn() {
		commonAction.click(loc_btnCreateReceipt);
		commonAction.sleepInMiliSecond(1000);
		logger.info("Clicked on Create Receipt button.");
		return this;
	}

	public Cashbook clickCreatePaymentBtn() {
		commonAction.click(loc_btnCreatePayment);
		commonAction.sleepInMiliSecond(1000);
		logger.info("Clicked on Create Payment button.");
		return this;
	}	

	public Cashbook inputCashbookSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearchRecord, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}	
	
	public Cashbook selectGroup(String group) {
		commonAction.click(loc_ddlSenderGroup);
		By groupLocator = By.xpath("//div[contains(@class,'uik-select__label') and text()='%s']".formatted(group));
		commonAction.click(groupLocator);
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name, boolean inputSearchTerm) {
		commonAction.click(loc_ddlSenderName);
		// Open dropdown if necessary
	    if (inputSearchTerm) {
			commonAction.sendKeys(loc_txtSearchSenderName, name);
			new HomePage(driver).waitTillSpinnerDisappear1();
	    }
		By customerLocator = By.xpath("//div[contains(@class,'search-item') and text()='%s']".formatted(name));
		commonAction.visibilityOfElementLocated(customerLocator);
		commonAction.sleepInMiliSecond(500); //There's something wrong here. Without this delay, names are not selected
		//The element will go stale after the delay, so we fetch the element again
		commonAction.click(customerLocator);
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}
	
	/**
	 * This method returns an array of strings representing the values of a dropdown list for revenue source.
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getSourceDropdownValues() {
		commonAction.click(loc_ddlSource);
		String text = commonAction.getText(By.cssSelector(".uik-select__optionListWrapper"));
		String[] values = text.split("\n");
		commonAction.click(loc_ddlSource);
		return values;
	}

	public Cashbook selectRevenueExpense(String revenueExpense) {
		commonAction.click(loc_ddlSource);
		By locator = By.xpath("//div[contains(@class,'uik-select__label') and text()='%s']".formatted(revenueExpense));
		commonAction.click(locator);
		logger.info("Selected Revenue Source/Expense Type: %s.".formatted(revenueExpense));
		return this;
	}

	public Cashbook selectBranch(String branch) {
		commonAction.click(loc_ddlBranch);
		By locator = By.xpath("//div[contains(@class,'uik-select__label') and text()='%s']".formatted(branch));
		commonAction.click(locator);
		logger.info("Selected Branch: %s.".formatted(branch));
		return this;
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown list for payment methods.
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getPaymentMethodDropdownValues() {
		commonAction.click(loc_ddlPaymentMethod);
		String text = commonAction.getText(By.cssSelector(".uik-select__optionListWrapper"));
		String[] values = text.split("\n");
		commonAction.click(loc_ddlPaymentMethod);
		return values;
	}	
	
	public Cashbook selectPaymentMethod(String paymentMethod) {
		commonAction.click(loc_ddlPaymentMethod);
		By locator = By.xpath(".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(paymentMethod));
		commonAction.click(locator);
		logger.info("Selected Payment Method: %s.".formatted(paymentMethod));
		return this;
	}

	public Cashbook inputAmount(String amount) {
		commonAction.sendKeys(new ByChained(loc_txtAmount, By.xpath("./parent::*/parent::*/preceding-sibling::input")), amount);
		logger.info("Input amount: %s.".formatted(amount));
		return this;
	}

	public Cashbook inputNote(String note) {
		commonAction.sendKeys(loc_txtNote, note);
		logger.info("Input note: %s.".formatted(note));
		return this;
	}
	
	/**
	 * @param isChecked If true => check the box, if false => un-check the box
	 */
	public Cashbook checkAccountingCheckbox(boolean isChecked) {
		By selector = new ByChained(loc_chkAccounting, By.xpath("./parent::*"));
		if (isChecked) {
			if (isAccountingChecked()) return this;
			commonAction.click(selector);
			logger.info("Checked Account checkbox");
		} else {
			if (!isAccountingChecked()) return this;
			commonAction.click(selector);
			logger.info("Un-checked Account checkbox");
		}
		return this;
	}
	
	public Cashbook clickSaveBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked on Save button.");
		return this;
	}

	public Cashbook clickCancelBtn() {
		new ConfirmationDialog(driver).clickGrayBtn();
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
			selectName(senderName, false);
		} else {
			selectName(senderName, true);
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
		commonAction.click(By.xpath("//td[text()='%s']".formatted(recordID)));
		logger.info("Clicked on cashbook record '%s'.".formatted(recordID));
		return this;
	}

	public String getGroup() {
		logger.info("Getting Group value from Transaction Id Popup");
		return commonAction.getText(loc_ddlSenderGroup);
	}

	public String getName() {
		String text = commonAction.getAttribute(new ByChained(loc_ddlSenderName, By.xpath(".//div[@class='form-group']/input")), "value");
		logger.info("Retrieved name from Transaction Id Popup: " + text);
		return text;
	}

	public String getSourceOrExpense() {
		logger.info("Getting Source/Expense value from Transaction Id Popup");
		return commonAction.getText(loc_ddlSource);
	}

	public String getBranch() {
		logger.info("Getting Branch value from Transaction Id Popup");
		return commonAction.getText(loc_ddlBranch);
	}

	public String getPaymentMethod() {
		logger.info("Getting Payment method value from Transaction Id Popup");
		return commonAction.getText(loc_ddlPaymentMethod);
	}

	public String getAmount() {
		String text = commonAction.getAttribute(loc_txtAmount, "value");
		logger.info("Retrieved Amount from Transaction Id Popup: " + text);
		return text;
	}

	public String getNote() {
		String text = commonAction.getAttribute(loc_txtNote, "value");
		logger.info("Retrieved Note from Transaction Id Popup: " + text);
		return text;
	}

	public boolean isAccountingChecked() {
		boolean text = commonAction.getElement(loc_chkAccounting).isSelected();
		logger.info("Is accounting checked: " + text);
		return text;
	}

    public void verifyTextAtCashbookManagementScreen() throws Exception {
    	commonAction.removeFbBubble();
    	String text = commonAction.getText(loc_lblPageTitle).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.title"), text);
    	text = commonAction.getText(loc_btnCreateReceipt);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createReceiptBtn"));
    	text = commonAction.getText(loc_btnCreatePayment);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createPaymentBtn"));
    	text = commonAction.getText(loc_btnFilter);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.filterBtn"));
    	
    	text = commonAction.getText(commonAction.getElement(loc_lblCashbookSummary, 0).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.openingBalance"));
    	commonAction.hoverOverElement(commonAction.getElement(loc_lblCashbookSummary, 0).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.openingBalance"));
    	
    	text = commonAction.getText(commonAction.getElement(loc_lblCashbookSummary, 1).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalRevenue"));
    	commonAction.hoverOverElement(commonAction.getElement(loc_lblCashbookSummary, 1).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalRevenue"));
    	
    	text = commonAction.getText(commonAction.getElement(loc_lblCashbookSummary, 2).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalExpenditure"));
    	commonAction.hoverOverElement(commonAction.getElement(loc_lblCashbookSummary, 2).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalExpenditure"));
    	    	
    	text = commonAction.getText(commonAction.getElement(loc_lblCashbookSummary, 3).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.endingBalance"));
    	commonAction.hoverOverElement(commonAction.getElement(loc_lblCashbookSummary, 3).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.endingBalance"));
    	
    	text = commonAction.getAttribute(loc_txtSearchRecord, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.searchBox"));
    	
    	text = commonAction.getText(loc_tblTableTitle);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tableHeader"));
    	
    	logger.info("verifyTextAtCashbookManagementScreen completed");
    }  	
	
    public void verifyTextAtCreateReceiptScreen() throws Exception {
    	String text = commonAction.getText(loc_lblCreateReceiptModalDialog);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.title"), text);
    	
    	text = commonAction.getText(new ByChained(loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	text = commonAction.getAttribute(new ByChained(loc_ddlSenderName, By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getAttribute(loc_txtNote, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
    
    public void verifyTextAtReceiptTransactionIDScreen() throws Exception {
    	/*
    	 * Remember to add code to verify pop-up title.
    	 */
    	
    	String text = commonAction.getText(new ByChained(loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtReceiptTransactionIDScreen completed");
    }  	
    
    public void verifyTextAtCreatePaymentScreen() throws Exception {
    	String text = commonAction.getText(loc_lblCreateReceiptModalDialog);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.title"));
    	text = commonAction.getText(new ByChained(loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getAttribute(new ByChained(loc_ddlSenderName, By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getAttribute(loc_txtNote, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
    
    public void verifyTextAtPaymentTransactionIDScreen() throws Exception {
    	/*
    	 * Remember to add code to verify pop-up title.
    	 */
    	
    	String text = commonAction.getText(new ByChained(loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getText(new ByChained(loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(new ByChained(loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
	
	public Cashbook clickResetDateRangerPicker() {
		commonAction.click(loc_dtpPrincipleTimeRange);
		commonAction.click(loc_btnResetDatePicker);
		logger.info("Clicked on Reset Time ranger picker button.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}    
	
	public Cashbook clickFilterBtn() {
		commonAction.click(loc_btnFilter);
		logger.info("Clicked on Filter button.");
		return this;
	}    

	public Cashbook selectFilteredBranch(String branch) {
		commonAction.click(loc_frmFilterCondition,0);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(branch)));
		logger.info("Selected filtered branch: %s.".formatted(branch));
		return this;
	}	
	
	public Cashbook selectFilteredAccounting(String yesOrNo) {
		commonAction.click(loc_frmFilterCondition, 1);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(yesOrNo)));
		logger.info("Selected filtered Accounting: %s.".formatted(yesOrNo));
		return this;
	}	
	
	public Cashbook selectFilteredTransaction(String transaction) {
		commonAction.click(loc_frmFilterCondition, 2);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(transaction)));
		logger.info("Selected filtered Transaction: %s.".formatted(transaction));
		return this;
	}	
	
	public Cashbook selectFilteredExpenseType(String expenseType) {
		commonAction.click(loc_frmFilterCondition, 3);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(expenseType)));
		logger.info("Selected filtered Expense type: %s.".formatted(expenseType));
		return this;
	}	
	
	public Cashbook selectFilteredRevenueType(String revenueType) {
		commonAction.click(loc_frmFilterCondition,4);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(revenueType)));
		logger.info("Selected filtered Revenue type: %s.".formatted(revenueType));
		return this;
	}	
	
	public Cashbook selectFilteredCreatedBy(String createdBy) {
		commonAction.click(loc_frmFilterCondition,5);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(createdBy)));
		logger.info("Selected filtered Created by: %s.".formatted(createdBy));
		return this;
	}	
	
	public Cashbook selectFilteredGroup(String group) {
		commonAction.click(loc_frmFilterCondition, 6);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(group)));
		logger.info("Selected filtered Sender/Recipient group: %s.".formatted(group));
		return this;
	}	
	
	public Cashbook selectFilteredName(String name) {
		commonAction.click(loc_frmFilterCondition, 7);
		
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
				commonAction.click(targetedOption);
				break;
			}
		}

		logger.info("Selected filtered Sender/Recipient name: %s.".formatted(name));
		return this;
	}	
	
	public Cashbook selectFilteredPaymentMethod(String method) {
		commonAction.click(loc_frmFilterCondition, 8);
		commonAction.click(By.xpath(conditionFilterDropdownXpath.formatted(method)));
		logger.info("Selected filtered payment method: %s.".formatted(method));
		return this;
	}	
	
	
	public Cashbook clickFilterDoneBtn() {
		commonAction.click(loc_btnFilterDone);
		logger.info("Clicked on Filter Done button.");
		return this;
	}    

    public void verifyTextAtFilterContainer() throws Exception {
    	String text = commonAction.getText(new ByChained(loc_btnFilterDone, By.xpath("./ancestor::div[contains(@class,'dropdown-menu-right')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.filterContainer"));
    	logger.info("verifyTextAtFilterContainer completed");
    }  		
	
}
