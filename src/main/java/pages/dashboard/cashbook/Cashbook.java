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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Cashbook {

	final static Logger logger = LogManager.getLogger(Cashbook.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public Cashbook(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".cashbook-summary .number")
	List<WebElement> CASHBOOKSUMMARY;

	@FindBy(xpath = "//div[contains(@class,'cashbook-list')]//table/tbody/tr")
	List<WebElement> CASHBOOK_RECORDS;

	@FindBy(css = ".gs-content-header-right-el .gs-button__green:nth-of-type(1)")
	WebElement CREATE_RECEIPT_BTN;
	
	@FindBy(css = ".gs-content-header-right-el .gs-button__green:nth-of-type(2)")
	WebElement CREATE_PAYMENT_BTN;

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
		return this;
	}

	public List<Integer> getCashbookSummary() {
		List<Integer> summary = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String rawAmount = commonAction.getText(CASHBOOKSUMMARY.get(i));
			Matcher m = Pattern.compile("\\d+").matcher(rawAmount);
			ArrayList<String> sub = new ArrayList<String>();
			while (m.find()) {
				sub.add(m.group());
			}
			summary.add(Integer.parseInt(String.join("", sub)));
		}
		return summary;
	}

	public List<List<String>> getRecords() {
		List<List<String>> table = new ArrayList<>();
		for (WebElement row : CASHBOOK_RECORDS) {
			List<String> rowData = new ArrayList<>();
			for (WebElement column : row.findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			table.add(rowData);
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
	
	public Cashbook selectGroup(String group) {
		commonAction.clickElement(SENDERGROUP_DROPDOWN);
		String xpath = ".//div[contains(@class,'uik-select__label') and text()='%s']".formatted(group);
		commonAction.clickElement(SENDERGROUP_DROPDOWN.findElement(By.xpath(xpath)));
		logger.info("Selected Sender/Recipient Group: %s.".formatted(group));
		return this;
	}

	public Cashbook selectName(String name) {
		commonAction.clickElement(SENDER_NAME_DROPDOWN);
		String xpath = "//div[contains(@class,'search-item') and text()='%s']".formatted(name);
		commonAction.clickElement(SENDER_NAME_DROPDOWN.findElement(By.xpath(xpath)));
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
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

	public Cashbook createReceipt(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
		clickCreateReceiptBtn();
		selectGroup(senderGroup);
		selectRevenueExpense(revenue);
		selectBranch(branch);
		selectPaymentMethod(payment);
		selectName(senderName);
		inputAmount(amount);
		inputNote(note);
		clickSaveBtn();
		return this;
	}
	
	public Cashbook createPayment(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
		clickCreatePaymentBtn();
		selectGroup(senderGroup);
		selectRevenueExpense(revenue);
		selectBranch(branch);
		selectPaymentMethod(payment);
		selectName(senderName);
		inputAmount(amount);
		inputNote(note);
		clickSaveBtn();
		return this;
	}

	public Cashbook clickRecord(String recordID) {
		WebElement record = driver.findElement(By.xpath("//td[text()='%s']".formatted(recordID)));
		commonAction.clickElement(record);
		logger.info("Clicked on cashbook record '%s'.".formatted(recordID));
		return this;
	}

	public String getGroupOnTransactionIdPopup() {
		logger.info("Getting Group value from Transaction Id Popup");
		return commonAction.getText(SENDERGROUP_DROPDOWN);
	}

	public String getNameOnTransactionIdPopup() {
		String text = commonAction.getElementAttribute(
				SENDER_NAME_DROPDOWN.findElement(By.xpath(".//div[@class='form-group']/input")), "value");
		logger.info("Retrieved name from Transaction Id Popup: " + text);
		return text;
	}

	public String getSourceOrExpenseOnTransactionIdPopup() {
		logger.info("Getting Source/Expense value from Transaction Id Popup");
		return commonAction.getText(REVENUE_SOURCE_DROPDOWN);
	}

	public String getBranchOnTransactionIdPopup() {
		logger.info("Getting Branch value from Transaction Id Popup");
		return commonAction.getText(BRANCH_DROPDOWN);
	}

	public String getPaymentMethodOnTransactionIdPopup() {
		logger.info("Getting Payment method value from Transaction Id Popup");
		return commonAction.getText(PAYMENT_METHOD_DROPDOWN);
	}

	public String getAmountOnTransactionIdPopup() {
		String text = commonAction.getElementAttribute(driver.findElement(AMOUNT), "value");
		logger.info("Retrieved Amount from Transaction Id Popup: " + text);
		return text;
	}

	public String getNoteOnTransactionIdPopup() {
		String text = commonAction.getElementAttribute(NOTE, "value");
		logger.info("Retrieved Note from Transaction Id Popup: " + text);
		return text;
	}

	public boolean isAccountingCheckedOnTransactionIdPopup() {
		boolean text = ACCOUNTING_CHECKBOX.isSelected();
		logger.info("Is accounting checked: " + text);
		return text;
	}

}
