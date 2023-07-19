package pages.sellerapp.cashbook;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class Cashbook {

    final static Logger logger = LogManager.getLogger(Cashbook.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;

    public Cashbook (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    String dropdownOption = "//*[ends-with(@resource-id,'tvAction') %s]";
    
    By CREATE_BTN = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
    By CREATE_RECEIPT_BTN = By.xpath(dropdownOption.formatted("and @index='0'"));
    By CREATE_PAYMENT_BTN = By.xpath(dropdownOption.formatted("and @index='1'"));
    
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
    
	By[] COLUMN = {
			By.xpath("//*[ends-with(@resource-id,'tvCashbookId')]"),
			By.xpath("//*[ends-with(@resource-id,'tvDate')]"),
			By.xpath("//*[ends-with(@resource-id,'tvAddress')]"),
			By.xpath("//*[ends-with(@resource-id,'tvType')]"),
			By.xpath("//*[ends-with(@resource-id,'tvName')]"),
			By.xpath("//*[ends-with(@resource-id,'tvOwner')]"),
			By.xpath("//*[ends-with(@resource-id,'tvPrice')]"),
	};

	public List<Long> getCashbookSummary() {
		commonAction.sleepInMiliSecond(1000); //Sometimes it takes longer for the element to change its data
		By[] CASHBOOKSUMMARY = {
			By.xpath("//*[ends-with(@resource-id,'tvOpening')]"),
			By.xpath("//*[ends-with(@resource-id,'tvTotalRev')]"),
			By.xpath("//*[ends-with(@resource-id,'tvExpenditure')]"),
			By.xpath("//*[ends-with(@resource-id,'tvEnding')]"),
		};
		
		List<Long> summary = new ArrayList<>();
		for (By bySelector: CASHBOOKSUMMARY) {
			
			//Sometimes element is present but the data it contains is not yet rendered
			String text ="";
			for (int i=0; i<3; i++) {
				text = commonAction.getText(bySelector);
				if (!text.isEmpty()) break;
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

	public Cashbook clickRecord(String recordID) {
		//More code needed
		commonAction.clickElement(COLUMN[0]);
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
		commonAction.sleepInMiliSecond(1000); //Sometimes the element representing the search box gets stale
		commonAction.inputText(SEARCH_BOX, name);
		commonAction.clickElement(By.xpath("//*[ends-with(@resource-id,'tvFilterText') and @text='%s']".formatted(name)));
		logger.info("Selected Sender Name: %s.".formatted(name));
		return this;
	}	

	public String[] getDropdownValues() {
		//Sometimes it takes longer for the values to display
		int elementCount = 0;
		for (int i=0; i<3; i++) {
			elementCount = commonAction.getElements(By.xpath(dropdownOption.formatted(""))).size();
			if (elementCount > 0) break; 
		}
		
		//Store all option values into an array then return the array
		List<String> values = new ArrayList<>();
		for (int i=0; i<elementCount; i++) {
			values.add(commonAction.getText(By.xpath(dropdownOption.formatted("and @index='%s'".formatted(i)))));
		}
		return values.toArray(new String[0]);
	}  		
	
	/**
	 * This method returns an array of strings representing the values of a dropdown list for revenue source.
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
	 * This method returns an array of strings representing the values of a dropdown list for payment methods.
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
			if (isAccountingChecked()) return this;
			commonAction.clickElement(ACCOUNTING_CHECKBOX);
			logger.info("Checked Account checkbox");
		} else {
			if (!isAccountingChecked()) return this;
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

	public Cashbook createReceiptPaymentOverlap(String senderGroup, String revenue, String branch, String payment, String senderName,
			String amount, String note, boolean isChecked) {
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
