package web.Dashboard.cashbook;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.Domain;
import utilities.enums.cashbook.CashbookGroup;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	CashbookElement elements;

	Domain domain;
	
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
	
	public Cashbook(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new CashbookElement();
	}
	public Cashbook(WebDriver driver, Domain domain) {
		this(driver);
		this.domain = domain;
	}

	public Cashbook navigate() {
		new HomePage(driver).navigateToPage("Cashbook");
		commonAction.sleepInMiliSecond(3000);
		commonAction.removeFbBubble();
		return this;
	}

	Cashbook navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		
	
	public Cashbook navigateByURL() {
		driver.get(DOMAIN + "/cashbook/management");
//		commonAction.sleepInMiliSecond(3000);
		commonAction.removeFbBubble();
		return this;
	}

	public Cashbook navigateUsingURL() {
		if (domain.equals(Domain.VN)) {
			navigateByURL(DOMAIN + "/cashbook/management");
		} else {
			navigateByURL(DOMAIN_BIZ + "/cashbook/management");
		}
		
    	commonAction.sleepInMiliSecond(500, "Wait a little after navigation");
		return this;
	}		
	
	public List<Long> getCashbookSummary() {
		List<Long> summary = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String rawAmount = commonAction.getText(elements.loc_lblCashbookSummary, i);
			Matcher m = Pattern.compile("\\d+").matcher(rawAmount);
			ArrayList<String> sub = new ArrayList<String>();
			while (m.find()) {
				sub.add(m.group());
			}
			summary.add(Long.parseLong(String.join("", sub)));
		}
		System.out.println(summary.toString());
		return summary;
	}
	
	public List<BigDecimal> getCashbookSummaryBig() {
		List<BigDecimal> summary = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String rawAmount = commonAction.getText(elements.loc_lblCashbookSummary, i);
			summary.add(new BigDecimal(DataGenerator.extractDigits(rawAmount)));
		}
		return summary;
	}
	
	public int getTotalRecordCount() {
		int recordCount = Integer.valueOf(commonAction.getText(elements.loc_lblTotalRecordCount));
		
		logger.debug("Retrieved total record count: {}", recordCount);
		return recordCount;
	}
	
	
	/**
	 * Waits until the newly created record appears on Cash Book table
	 * @param preTotalRecordCount the number of records initially
	 */
	public void waitTillRecordCountIncrease(int preTotalRecordCount) {
	    int maxRetries = 10;
	    int sleepDuration = 1000;
	    int retries = 0;

	    while (retries < maxRetries && getTotalRecordCount()-preTotalRecordCount <1) {
	        commonAction.sleepInMiliSecond(sleepDuration, "Wait until total record count increases");
	        retries++;
	    }
	}   	
	
	public void waitTillRecordsAppear() {
	    int maxRetries = 10;
	    int sleepDuration = 500;
	    int retries = 0;

	    while (retries < maxRetries && commonAction.getElements(elements.loc_tblCashbookRecord).isEmpty()) {
	    	logger.debug("Table empty. Retrying after {} ms", sleepDuration);
	        commonAction.sleepInMiliSecond(sleepDuration);
	        retries++;
	    }
	}	
	public void waitTillTableEmpty() {
	    int maxRetries = 10;
	    int sleepDuration = 500;
	    int retries = 0;

	    while (retries < maxRetries && commonAction.getElements(elements.loc_icnEmptyWallet).isEmpty()) {
	    	logger.debug("Table not empty. Retrying after {} ms", sleepDuration);
	        commonAction.sleepInMiliSecond(sleepDuration);
	        retries++;
	    }
	}	
	
	public List<String> getSpecificRecord(int index) {
		
		waitTillRecordsAppear();
		
		/*
		 * Loop through the columns of the specific record
		 * and store data of the column into an array.
		 * Retry the process when StaleElementReferenceException occurs
		 */
		try {
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commonAction.getElement(elements.loc_tblCashbookRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			logger.debug("Table row {}: {}", index, rowData); //Debug flaky tests on CI env
			return rowData;
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commonAction.getElement(elements.loc_tblCashbookRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			logger.debug("Table row {}: {}", index, rowData); //Debug flaky tests on CI env
			return rowData;
		}
	}

	public List<List<String>> getRecords() {
		waitTillRecordsAppear();
		
		int recordCount = commonAction.getElements(elements.loc_tblCashbookRecord).size();
		logger.debug("{} cashbook record(s) found", recordCount);
		
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<recordCount; i++) {
			table.add(getSpecificRecord(i));
		}
		
		return table;
	}

	public Cashbook clickCreateReceiptBtn() {
		commonAction.click(elements.loc_btnCreateReceipt);
		logger.info("Clicked on Create Receipt button.");
		return this;
	}

	public Cashbook clickCreatePaymentBtn() {
		commonAction.click(elements.loc_btnCreatePayment);
		logger.info("Clicked on Create Payment button.");
		return this;
	}	

	public Cashbook inputCashbookSearchTerm(String searchTerm) {
		commonAction.sendKeys(elements.loc_txtSearchRecord, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}	
	
	public Cashbook selectGroup(String group) {
		commonAction.click(elements.loc_ddlSenderGroup);
		By groupLocator = By.xpath("//div[contains(@class,'uik-select__label') and text()='%s']".formatted(group));
		commonAction.click(groupLocator);
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name, boolean inputSearchTerm) {
		commonAction.click(elements.loc_ddlSenderName);
		// Open dropdown if necessary
	    if (inputSearchTerm) {
//	    	commonAction.sleepInMiliSecond(500, "Wait a little before inputing text or ElementNotInteractableException occurs");
			commonAction.inputText(elements.loc_txtSearchSenderName, name);
			new HomePage(driver).waitTillSpinnerDisappear1();
	    }
		By customerLocator = By.xpath(elements.searchResultXpath.formatted("and text()=\"%s\"".formatted(name)));
		commonAction.waitVisibilityOfElementLocated(customerLocator);
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
		commonAction.click(elements.loc_ddlSource);
		String text = commonAction.getText(By.cssSelector(".uik-select__optionListWrapper"));
		String[] values = text.split("\n");
		commonAction.click(elements.loc_ddlSource);
		return values;
	}

	public Cashbook selectRevenueExpense(String revenueExpense) {
		commonAction.click(elements.loc_ddlSource);
		By locator = By.xpath("//div[contains(@class,'uik-select__label') and text()='%s']".formatted(revenueExpense));
		commonAction.click(locator);
		logger.info("Selected Revenue Source/Expense Type: %s.".formatted(revenueExpense));
		return this;
	}

	public Cashbook selectBranch(String branch) {
		commonAction.click(elements.loc_ddlBranch);
		By locator = By.xpath("//div[contains(@class,'uik-select__label') and text()=\"%s\"]".formatted(branch));
		commonAction.click(locator);
		logger.info("Selected Branch: %s.".formatted(branch));
		return this;
	}

	/**
	 * This method returns an array of strings representing the values of a dropdown list for payment methods.
	 * @return An array of strings representing the dropdown values
	 */
	public String[] getPaymentMethodDropdownValues() {
		commonAction.click(elements.loc_ddlPaymentMethod);
		String text = commonAction.getText(By.cssSelector(".uik-select__optionListWrapper"));
		String[] values = text.split("\n");
		commonAction.click(elements.loc_ddlPaymentMethod);
		return values;
	}	
	
	public Cashbook selectPaymentMethod(String paymentMethod) {
		commonAction.click(elements.loc_ddlPaymentMethod);
		By locator = By.xpath(".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(paymentMethod));
		commonAction.click(locator);
		logger.info("Selected Payment Method: %s.".formatted(paymentMethod));
		return this;
	}

	public Cashbook inputAmount(String amount) {
		commonAction.sendKeys(new ByChained(elements.loc_txtAmount, By.xpath("./parent::*/parent::*/preceding-sibling::input")), amount);
		logger.info("Input amount: %s.".formatted(amount));
		return this;
	}

	public Cashbook inputNote(String note) {
		commonAction.sendKeys(elements.loc_txtNote, note);
		logger.info("Input note: %s.".formatted(note));
		return this;
	}
	
	/**
	 * @param isChecked If true => check the box, if false => un-check the box
	 */
	public Cashbook checkAccountingCheckbox(boolean isChecked) {
		By selector = new ByChained(elements.loc_chkAccounting, By.xpath("./parent::*"));
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
		if (senderGroup.contentEquals(CashbookGroup.getTextByLanguage(CashbookGroup.OTHERS))) {
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
		return commonAction.getText(elements.loc_ddlSenderGroup);
	}

	public String getName() {
		String text = commonAction.getAttribute(new ByChained(elements.loc_ddlSenderName, By.xpath(".//div[@class='form-group']/input")), "value");
		logger.info("Retrieved name from Transaction Id Popup: " + text);
		return text;
	}

	public String getSourceOrExpense() {
		logger.info("Getting Source/Expense value from Transaction Id Popup");
		return commonAction.getText(elements.loc_ddlSource);
	}

	public String getBranch() {
		logger.info("Getting Branch value from Transaction Id Popup");
		return commonAction.getText(elements.loc_ddlBranch);
	}

	public String getPaymentMethod() {
		logger.info("Getting Payment method value from Transaction Id Popup");
		return commonAction.getText(elements.loc_ddlPaymentMethod);
	}

	public String getAmount() {
		String text = commonAction.getAttribute(elements.loc_txtAmount, "value");
		logger.info("Retrieved Amount from Transaction Id Popup: " + text);
		return text;
	}

	public String getNote() {
		String text = commonAction.getAttribute(elements.loc_txtNote, "value");
		logger.info("Retrieved Note from Transaction Id Popup: " + text);
		return text;
	}

	public boolean isAccountingChecked() {
		boolean text = commonAction.getElement(elements.loc_chkAccounting).isSelected();
		logger.info("Is accounting checked: " + text);
		return text;
	}

    public void verifyTextAtCashbookManagementScreen() throws Exception {
    	commonAction.removeFbBubble();
    	String text = commonAction.getText(elements.loc_lblPageTitle).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.title"), text);
    	text = commonAction.getText(elements.loc_btnCreateReceipt);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createReceiptBtn"));
    	text = commonAction.getText(elements.loc_btnCreatePayment);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.createPaymentBtn"));
    	text = commonAction.getText(elements.loc_btnFilter);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.filterBtn"));
    	
    	text = commonAction.getText(commonAction.getElement(elements.loc_lblCashbookSummary, 0).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.openingBalance"));
    	commonAction.hoverOverElement(commonAction.getElement(elements.loc_lblCashbookSummary, 0).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(elements.loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.openingBalance"));
    	
    	text = commonAction.getText(commonAction.getElement(elements.loc_lblCashbookSummary, 1).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalRevenue"));
    	commonAction.hoverOverElement(commonAction.getElement(elements.loc_lblCashbookSummary, 1).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(elements.loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalRevenue"));
    	
    	text = commonAction.getText(commonAction.getElement(elements.loc_lblCashbookSummary, 2).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.totalExpenditure"));
    	commonAction.hoverOverElement(commonAction.getElement(elements.loc_lblCashbookSummary, 2).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(elements.loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.totalExpenditure"));
    	    	
    	text = commonAction.getText(commonAction.getElement(elements.loc_lblCashbookSummary, 3).findElement(By.xpath("./parent::*/preceding-sibling::*")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.endingBalance"));
    	commonAction.hoverOverElement(commonAction.getElement(elements.loc_lblCashbookSummary, 3).findElement(By.xpath("./parent::*/preceding-sibling::*/div[contains(@class,'help__wrapper')]")));
    	commonAction.sleepInMiliSecond(500);
    	text = commonAction.getText(elements.loc_tltCashbookSummary);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tooltip.endingBalance"));
    	
    	text = commonAction.getAttribute(elements.loc_txtSearchRecord, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.searchBox"));
    	
    	text = commonAction.getText(elements.loc_tblTableTitle);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.management.tableHeader"));
    	
    	logger.info("verifyTextAtCashbookManagementScreen completed");
    }  	
	
    public void verifyTextAtCreateReceiptScreen() throws Exception {
    	String text = commonAction.getText(elements.loc_lblCreateReceiptModalDialog);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.title"), text);
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	text = commonAction.getAttribute(new ByChained(elements.loc_ddlSenderName, By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getAttribute(elements.loc_txtNote, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_chkAccounting, By.xpath("./following-sibling::div")));
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
    	
    	String text = commonAction.getText(new ByChained(elements.loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.sourceLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtReceiptTransactionIDScreen completed");
    }  	
    
    public void verifyTextAtCreatePaymentScreen() throws Exception {
    	String text = commonAction.getText(elements.loc_lblCreateReceiptModalDialog);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.title"));
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getAttribute(new ByChained(elements.loc_ddlSenderName, By.xpath(".//input[contains(@id,'gs-dropdown-search')]")), "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createReceipt.nameLbl.customer.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath(".//div[@class='uik-select__valueWrapper']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	text = commonAction.getAttribute(elements.loc_txtNote, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl.placeHolder"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_chkAccounting, By.xpath("./following-sibling::div")));
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
    	
    	String text = commonAction.getText(new ByChained(elements.loc_ddlSenderGroup, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.groupLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlSenderName, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.nameLbl"));
    	text = commonAction.getText(new ByChained(elements.loc_ddlSource, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.createPayment.sourceLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlBranch, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.branchLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtAmount, By.xpath("./ancestor::div/preceding-sibling::span[contains(@class,'label')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.amountLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_ddlPaymentMethod, By.xpath("./preceding-sibling::span")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.paymentMethodLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_txtNote, By.xpath("./ancestor::div/preceding-sibling::span[@class='label']")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.noteLbl"));
    	
    	text = commonAction.getText(new ByChained(elements.loc_chkAccounting, By.xpath("./following-sibling::div")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.accountingLbl"));
    	
    	text = new ConfirmationDialog(driver).getGrayBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.cancelBtn"));
    	text = new ConfirmationDialog(driver).getGreenBtnText();
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("common.saveBtn"));
    	
    	logger.info("verifyTextAtCreateReceiptScreen completed");
    }  	
	
	public Cashbook clickResetDateRangerPicker() {
		commonAction.click(elements.loc_dtpPrincipleTimeRange);
		commonAction.click(elements.loc_btnResetDatePicker);
		logger.info("Clicked on Reset Time ranger picker button.");
		commonAction.sleepInMiliSecond(1000);
		return this;
	}    
	
	public Cashbook clickFilterBtn() {
		commonAction.click(elements.loc_btnFilter);
		logger.info("Clicked on Filter button.");
		return this;
	}    

	public Cashbook selectFilteredBranch(String branch) {
		commonAction.click(elements.loc_frmFilterCondition,0);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(branch)));
		logger.info("Selected filtered branch: %s.".formatted(branch));
		return this;
	}	
	
	public Cashbook selectFilteredAccounting(String yesOrNo) {
		commonAction.click(elements.loc_frmFilterCondition, 1);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(yesOrNo)));
		logger.info("Selected filtered Accounting: %s.".formatted(yesOrNo));
		return this;
	}	
	
	public Cashbook selectFilteredTransaction(String transaction) {
		commonAction.click(elements.loc_frmFilterCondition, 2);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(transaction)));
		logger.info("Selected filtered Transaction: %s.".formatted(transaction));
		return this;
	}	
	
	public Cashbook selectFilteredExpenseType(String expenseType) {
		commonAction.click(elements.loc_frmFilterCondition, 3);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(expenseType)));
		logger.info("Selected filtered Expense type: %s.".formatted(expenseType));
		return this;
	}	
	
	public Cashbook selectFilteredRevenueType(String revenueType) {
		commonAction.click(elements.loc_frmFilterCondition,4);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(revenueType)));
		logger.info("Selected filtered Revenue type: %s.".formatted(revenueType));
		return this;
	}	
	
	public Cashbook selectFilteredCreatedBy(String createdBy) {
		commonAction.click(elements.loc_frmFilterCondition,5);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(createdBy)));
		logger.info("Selected filtered Created by: %s.".formatted(createdBy));
		return this;
	}	
	
	public Cashbook selectFilteredGroup(String group) {
		commonAction.click(elements.loc_frmFilterCondition, 6);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(group)));
		logger.info("Selected filtered Sender/Recipient group: %s.".formatted(group));
		return this;
	}	
	
	public Cashbook selectFilteredName(String name) {
		commonAction.click(elements.loc_frmFilterCondition, 7);
		
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
		commonAction.click(elements.loc_frmFilterCondition, 8);
		commonAction.click(By.xpath(elements.conditionFilterDropdownXpath.formatted(method)));
		logger.info("Selected filtered payment method: %s.".formatted(method));
		return this;
	}	
	
	public Cashbook clickFilterDoneBtn() {
		commonAction.click(elements.loc_btnFilterDone);
		logger.info("Clicked on Filter Done button.");
		commonAction.sleepInMiliSecond(3000, "Wait after clicking Done button"); //This is needed on CI env
		return this;
	}

	public Cashbook deleteRecord(String recordID) {
		commonAction.click(By.xpath("//td[text()='%s']/following-sibling::td[7]//img".formatted(recordID)));
		logger.info("Clicked on record '%s' to delete it.".formatted(recordID));
		new ConfirmationDialog(driver).clickOKBtn();
		return this;
	}	
	
    public void verifyTextAtFilterContainer() throws Exception {
    	String text = commonAction.getText(new ByChained(elements.loc_btnFilterDone, By.xpath("./ancestor::div[contains(@class,'dropdown-menu-right')]")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("cashbook.filterContainer"));
    	logger.info("verifyTextAtFilterContainer completed");
    }  		

	boolean isCashbookPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				staffPermission.getCashbook().isViewReceiptTransactionList(),
				staffPermission.getCashbook().isViewPaymentTransactionList(),
				staffPermission.getCashbook().isViewReceiptTransactionDetail(),
				staffPermission.getCashbook().isViewPaymentTransactionDetail(),
				staffPermission.getCashbook().isCreateReceiptTransaction(),
				staffPermission.getCashbook().isCreatePaymentTransaction(),
				staffPermission.getCashbook().isEditReceiptTransaction(),
				staffPermission.getCashbook().isEditPaymentTransaction(),
				staffPermission.getCashbook().isDeleteReceiptTransaction(),
				staffPermission.getCashbook().isDeletePaymentTransaction(),
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}	    
    
    public void checkPermissionToViewReceiptPaymentList(AllPermissions staffPermission) {
    	navigateByURL(); 
    	
    	if (isCashbookPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Cashbook permission. Skipping checkPermissionToViewReceiptPaymentList");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	List<List<String>> records = getRecords();
    	commonAction.sleepInMiliSecond(2000, "Waiting for summary to load");
    	List<BigDecimal> originalSummary = getCashbookSummaryBig();
    	
    	for (int i=0; i<2; i++) {
    		boolean type = false;
    		List<String> source = null;
    		BigDecimal amount = BigDecimal.valueOf(0);
    		if (i==0) {
    			type = staffPermission.getCashbook().isViewReceiptTransactionList();
    			source = records.stream().filter(record -> records.get(TRANSACTIONCODE_COL).contains("RN")).map(record -> record.get(REVENUETYPE_COL)).collect(Collectors.toList());
    			amount = originalSummary.get(TOTALREVENUE_IDX);
    		} else {
    			type = staffPermission.getCashbook().isViewPaymentTransactionList();
    			source = records.stream().filter(record -> records.get(TRANSACTIONCODE_COL).contains("PN")).map(record -> record.get(EXPENSETYPE_COL)).collect(Collectors.toList());
    			amount = originalSummary.get(TOTALEXPENDITURE_IDX);
    		}
    		
    		if (type) {
        		Assert.assertNotEquals(amount, BigDecimal.valueOf(0));
        		Assert.assertFalse(source.contains("-"));
    		} else {
        		Assert.assertEquals(amount, BigDecimal.valueOf(0));
        		Assert.assertTrue(source.isEmpty());
    		}
    	}
    	logger.info("Finished checking permission to view receipt/payment list");
    }
    
    public void checkPermissionToViewReceiptPaymentDetail(AllPermissions staffPermission) {
    	navigateByURL(); 
    	
    	if (isCashbookPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Cashbook permission. Skipping checkPermissionToViewReceiptPaymentDetail");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	for (int i=0; i<2; i++) {
    		boolean flag = false;
    		By loc1_tmpTransactionType = null;
    		if (i==0) {
    			loc1_tmpTransactionType = elements.loc_tmpRandomReceipt;
    			flag = staffPermission.getCashbook().isViewReceiptTransactionDetail();
    		} else {
    			loc1_tmpTransactionType = elements.loc_tmpRandomPayment;
    			flag = staffPermission.getCashbook().isViewPaymentTransactionDetail();
    		}  
    		
    		if (commonAction.getElements(loc1_tmpTransactionType).isEmpty()) continue;
    		
    		commonAction.click(loc1_tmpTransactionType);
    		if (flag) {
        		new ConfirmationDialog(driver).clickGrayBtn();
    		} else {
        		Assert.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc1_tmpTransactionType));
    		}
    	}
    	logger.info("Finished checking permission to view receipt/payment details");
    }
    
    public void checkPermissionToCreateReceiptPayment(AllPermissions staffPermission, String nonAssignedCustomer, String assignedCustomer, String supplier, String staff) {
    	navigateByURL(); 
    	
    	if (isCashbookPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Cashbook permission. Skipping checkPermissionToCreateReceiptPayment");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	for (int i=0; i<2; i++) {
    		boolean flag = false;
    		if (i==0) {
    			clickCreateReceiptBtn();
    			flag = staffPermission.getCashbook().isCreateReceiptTransaction();
    		} else {
    			clickCreatePaymentBtn();
    			flag = staffPermission.getCashbook().isCreatePaymentTransaction();
    		}
    		
        	if (flag) {
        		String group = CashbookGroup.getTextByLanguage(CashbookGroup.CUSTOMER);
        		if (staffPermission.getCustomer().getCustomerManagement().isViewAllCustomerList() && staffPermission.getCustomer().getCustomerManagement().isViewAssignedCustomerList()) {
        			selectGroup(group);
        			selectName(nonAssignedCustomer, true);
        			selectName(assignedCustomer, true);
        		} else if (staffPermission.getCustomer().getCustomerManagement().isViewAllCustomerList()) {
        			selectGroup(group);
        			selectName(nonAssignedCustomer, true);
        		} else if (staffPermission.getCustomer().getCustomerManagement().isViewAssignedCustomerList()) {
        			selectGroup(group);
        			selectName(assignedCustomer, true);
        		} else {
        			selectGroup(group);
        			commonAction.click(elements.loc_ddlSenderName);
        			commonAction.sendKeys(elements.loc_txtSearchSenderName, nonAssignedCustomer);
        			new HomePage(driver).waitTillSpinnerDisappear1();
        			By customerLocator = By.xpath(elements.searchResultXpath.formatted(""));
        			Assert.assertEquals(commonAction.getListElement(customerLocator).size(), 0);
        		}
        		group = CashbookGroup.getTextByLanguage(CashbookGroup.SUPPLIER);
        		if (staffPermission.getSuppliers().getSupplier().isViewSupplierList()) {
        			selectGroup(group);
        			selectName(supplier, true);
        		} else {
        			selectGroup(group);
        			commonAction.click(elements.loc_ddlSenderName);
        			commonAction.inputText(elements.loc_txtSearchSenderName, supplier);
        			new HomePage(driver).waitTillSpinnerDisappear1();
        			By supplierLocator = By.xpath(elements.searchResultXpath.formatted(""));
        			Assert.assertEquals(commonAction.getListElement(supplierLocator).size(), 0);
        		}
        		group = CashbookGroup.getTextByLanguage(CashbookGroup.STAFF);
        		if (staffPermission.getSetting().getStaffManagement().isViewStaffList()) {
        			commonAction.sleepInMiliSecond(1000, "This is weird!!!");
        			selectGroup(group);
        			selectName(staff, true);
        		} else {
        			selectGroup(group);
        			commonAction.click(elements.loc_ddlSenderName);
        			commonAction.inputText(elements.loc_txtSearchSenderName, staff);
        			new HomePage(driver).waitTillSpinnerDisappear1();
        			By staffLocator = By.xpath(elements.searchResultXpath.formatted(""));
        			Assert.assertEquals(commonAction.getListElement(staffLocator).size(), 0);
        		}
        		commonAction.refreshPage();
        	} else {
        		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
        	}
    	}
    	logger.info("Finished checking permission to create receipt/payment");
    }
    
    public void checkPermissionToEditReceiptPayment(AllPermissions staffPermission) {
    	navigateByURL(); 
    	
    	if (isCashbookPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Cashbook permission. Skipping checkPermissionToEditReceiptPayment");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	for (int i=0; i<2; i++) {
    		boolean isViewDetail = false;
    		boolean isEdit = false;
    		By loc1_tmpTransactionType = null;
    		if (i==0) {
    			loc1_tmpTransactionType = elements.loc_tmpRandomReceipt;
    			isViewDetail = staffPermission.getCashbook().isViewReceiptTransactionDetail();
    			isEdit = staffPermission.getCashbook().isEditReceiptTransaction();
    		} else {
    			loc1_tmpTransactionType = elements.loc_tmpRandomPayment;
    			
    			isViewDetail = staffPermission.getCashbook().isViewPaymentTransactionDetail();
    			isEdit = staffPermission.getCashbook().isEditPaymentTransaction();
    		}
    		
    		if (commonAction.getElements(loc1_tmpTransactionType).isEmpty()) continue;
    		
    		commonAction.click(loc1_tmpTransactionType);
    		
    		if (isViewDetail) {
    			checkAccountingCheckbox(false);
    			checkAccountingCheckbox(true);
    			clickSaveBtn();
        		if (isEdit) {
        			new HomePage(driver).getToastMessage();
        		} else {
        			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
        			commonAction.refreshPage();
        		}
    		} else {
    			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		}
    	}
    	logger.info("Finished checking permission to edit receipt/payment");
    }
    
    public void checkPermissionToDeleteReceiptPayment(AllPermissions staffPermission) {
    	navigateByURL(); 
    	
    	if (isCashbookPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Cashbook permission. Skipping checkPermissionToDeleteReceiptPayment");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	List<List<String>> records = getRecords();
    	
    	String randomReceipt = null;
    	String randomPayment = null;
    	for (List<String> record : records) {
    		if (record.get(CREATEDBY_COL).equals("system")) continue;
    		if (randomReceipt != null && randomPayment != null) break; 
    		if (randomReceipt == null && record.get(TRANSACTIONCODE_COL).startsWith("RN")) randomReceipt = record.get(TRANSACTIONCODE_COL);
    		if (randomPayment == null && record.get(TRANSACTIONCODE_COL).startsWith("PN")) randomPayment = record.get(TRANSACTIONCODE_COL);
    	}
    	
    	for (int i=0; i<2; i++) {
    		boolean flag = false;
    		String transactionId = null;
    		if (i==0) {
    			transactionId = randomReceipt;
    			flag = staffPermission.getCashbook().isDeleteReceiptTransaction();
    		} else {
    			transactionId = randomPayment;
    			flag = staffPermission.getCashbook().isDeletePaymentTransaction();
    		} 
    		
    		if (transactionId == null) continue;
    		
    		deleteRecord(transactionId);
    		
    		if (flag) {
        		new HomePage(driver).getToastMessage();
    		} else {
        		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		}
    	}
    	logger.info("Finished checking permission to delete receipt/payment");
    }
 		
    public void checkCashbookPermission(AllPermissions staffPermission, String unassignedCustomer, String assignedCustomer, String supplier, String staff) {
    	checkPermissionToViewReceiptPaymentList(staffPermission);
    	checkPermissionToViewReceiptPaymentDetail(staffPermission);
    	checkPermissionToCreateReceiptPayment(staffPermission, unassignedCustomer, assignedCustomer, supplier, staff);
    	checkPermissionToEditReceiptPayment(staffPermission);
    	checkPermissionToDeleteReceiptPayment(staffPermission);
    }  		
	
}
