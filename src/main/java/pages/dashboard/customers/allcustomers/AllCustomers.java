package pages.dashboard.customers.allcustomers;

import java.time.Duration;

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
import org.testng.asserts.SoftAssert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.customers.allcustomers.create_customer.CreateCustomerPopup;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class AllCustomers extends HomePage{

	final static Logger logger = LogManager.getLogger(AllCustomers.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public AllCustomers(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[2]")
	WebElement EXPORT_BTN;

	@FindBy(xpath = "(//div[contains(@class,'uik-menuDrop__list')]//button)[1]")
	WebElement EXPORT_CUSTOMER_BTN;	
	
	@FindBy(xpath = "(//div[contains(@class,'customer-list')]//button[contains(@class,'gs-button__green')])[3]")
	WebElement IMPORT_CUSTOMER_BTN;	

    @FindBy(xpath = "(//div[contains(@class,'customer-list')]//div[contains(@class,'buttons-row')]//button)[last()]")
    WebElement PRINT_BARCODE_BTN;	
	
	@FindBy(css = ".customer-list__filter-container .gs-search-box__wrapper .uik-input__input")
	WebElement SEARCH_BOX;

	@FindBy(id = "phone")
	WebElement PHONE;

    @FindBy (css = "div.modal-content")
    WebElement WARNING_POPUP;

	@FindBy(css = ".btn-filter-action")
	WebElement FILTER_BTN;

	@FindBy(xpath = "(//div[contains(@class,'filter-title')])[1]/following-sibling::div")
	WebElement BRANCH_FIELD;

	@FindBy (css = ".gs-content-header-right-el > .gs-button__green")
	WebElement CREATE_NEW_CUSTOMER_BTN;

	@FindBy(css = ".dropdown-menu-right .gs-button__green")
	WebElement DONE_BTN;

	By IMPORT_CUSTOMER_MODAL = By.cssSelector(".customer-list-import-modal");	
	By PRINT_BARCODE_MODAL = By.cssSelector(".customer-list-barcode-printer");
	
	public AllCustomers navigate() {
		new HomePage(driver).navigateToPage("Customers");
		commonAction.sleepInMiliSecond(2000);
		return this;
	}

	public AllCustomers clickExport() {
		commonAction.clickElement(EXPORT_BTN);
		logger.info("Clicked on 'Export' button.");
		return this;
	}

	public AllCustomers clickExportCustomer() {
		if (commonAction.isElementVisiblyDisabled(EXPORT_CUSTOMER_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(EXPORT_CUSTOMER_BTN);
			return this;
		}
		commonAction.clickElement(EXPORT_CUSTOMER_BTN);
		logger.info("Clicked on 'Export Customer' button.");
		return this;
	}	
	
	public AllCustomers clickImportCustomer() {
		if (commonAction.isElementVisiblyDisabled(IMPORT_CUSTOMER_BTN.findElement(By.xpath("./parent::*")))) {
			new HomePage(driver).isMenuClicked(IMPORT_CUSTOMER_BTN);
			return this;
		}
		commonAction.clickElement(IMPORT_CUSTOMER_BTN);
		logger.info("Clicked on 'Import Customer' button.");
		return this;
	}	

    public AllCustomers clickPrintBarcode() {
    	if (commonAction.isElementVisiblyDisabled(PRINT_BARCODE_BTN.findElement(By.xpath("./parent::*")))) {
    		new HomePage(driver).isMenuClicked(PRINT_BARCODE_BTN);
    		return this;
    	}
    	commonAction.clickElement(PRINT_BARCODE_BTN);
    	logger.info("Clicked on 'Print Barcode' button.");
    	return this;
    }	
	
    public boolean isImportCustomerDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(IMPORT_CUSTOMER_MODAL));
    }   	

    public boolean isPrintBarcodeDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(PRINT_BARCODE_MODAL));
    }       
    
	public AllCustomers inputSearchTerm(String searchTerm) {
		commonAction.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickFilterIcon() {
		commonAction.clickElement(FILTER_BTN);
		logger.info("Clicked on Filter icon.");
		return this;
	}

	public AllCustomers clickFilterDoneBtn() {
		commonAction.clickElement(DONE_BTN);
		logger.info("Clicked on Filter Done button.");
		return this;
	}

	public AllCustomers clickBranchList() {
		commonAction.clickElement(BRANCH_FIELD);
		logger.info("Clicked on Branch list.");
		return this;
	}

	public AllCustomers selectBranch(String branch) {
		new HomePage(driver).hideFacebookBubble();
		clickFilterIcon();
		clickBranchList();
		commonAction.clickElement(BRANCH_FIELD.findElement(By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(branch))));
		logger.info("Selected branch: " + branch);
		clickFilterDoneBtn();
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickUser(String user) {
		new HomePage(driver).hideFacebookBubble();
		String xpath = "//div[@class='text-truncate' and text()='%s']".formatted(user);
		commonAction.clickElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))));
		logger.info("Clicked on user: " + user);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public String getPhoneNumber(String user) {
		String xpath = "//div[@class='full-name' and text()='%s']/ancestor::*/following-sibling::td[2]".formatted(user);
		String value = commonAction.getText(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))));
		logger.info("Retrieved phone number: " + value);
		return value;
	}

	public CreateCustomerPopup clickCreateNewCustomerBtn() {
		// wait and click Create New Customer button
		wait.until(ExpectedConditions.elementToBeClickable(CREATE_NEW_CUSTOMER_BTN)).click();
		return new CreateCustomerPopup(driver);
	}

	public CustomerDetails searchAndGoToCustomerDetailByName(String fullName){
		inputSearchTerm(fullName);
		clickUser(fullName);
		return new CustomerDetails(driver);
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToExportCustomer(String permission) {
		if (permission.contentEquals("A")) {
			clickExport().clickExportCustomer();
			new ConfirmationDialog(driver).clickCancelBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToImportCustomer(String permission) {
		clickImportCustomer();
		boolean flag = isImportCustomerDialogDisplayed();
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    public void verifyPermissionToPrintBarCode(String permission) {
		clickPrintBarcode();
		boolean flag = isPrintBarcodeDialogDisplayed();
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(flag);
    	} else if (permission.contentEquals("D")) {
    		Assert.assertFalse(flag);
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    
    /*-------------------------------------*/ 
	
}
