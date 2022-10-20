package pages.dashboard.customers.allcustomers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.customers.allcustomers.create_customer.CreateCustomerPopup;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

public class AllCustomers {

	final static Logger logger = LogManager.getLogger(AllCustomers.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public AllCustomers(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".customer-list__filter-container .gs-search-box__wrapper .uik-input__input")
	WebElement SEARCH_BOX;

	@FindBy(id = "phone")
	WebElement PHONE;

    @FindBy (css = "div.modal-content")
    WebElement WARNING_POPUP;
    @FindBy(xpath = "//section[contains(@class,'desktop-flex')]//input[@placeholder='Search keyword']")
    WebElement SEARCH_INPUT;

	@FindBy(css = ".btn-filter-action")
	WebElement FILTER_BTN;

	@FindBy(xpath = "(//div[contains(@class,'filter-title')])[1]/following-sibling::div")
	WebElement BRANCH_FIELD;

	@FindBy (css = ".gs-content-header-right-el > .gs-button__green")
	WebElement CREATE_NEW_CUSTOMER_BTN;

	@FindBy(css = ".dropdown-menu-right .gs-button__green")
	WebElement DONE_BTN;

	public AllCustomers navigate() {
		new HomePage(driver).navigateToPage("Customers");
		return this;
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
		clickFilterIcon();
		clickBranchList();
		commonAction.clickElement(BRANCH_FIELD.findElement(By.xpath("//div[@class='uik-select__label' and text()='%s']".formatted(branch))));
		logger.info("Selected branch: " + branch);
		clickFilterDoneBtn();
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

	public AllCustomers clickUser(String user) {
		String xpath = "//div[@class='full-name' and text()='%s']".formatted(user);
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

}
