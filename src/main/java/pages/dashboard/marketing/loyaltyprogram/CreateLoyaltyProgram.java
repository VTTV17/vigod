package pages.dashboard.marketing.loyaltyprogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

import java.time.Duration;
import java.util.Arrays;

public class CreateLoyaltyProgram {

	final static Logger logger = LogManager.getLogger(CreateLoyaltyProgram.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateLoyaltyProgram(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "name")
	WebElement TIER_NAME;

	@FindBy(css = ".segment-add .gs-fake-link")
	WebElement ADD_SEGMENT_LNKTXT;

	@FindBy(css = ".image-upload input")
	WebElement UPLOAD_BTN;

	@FindBy(css = "div.fr-wrapper > div")
	WebElement DESCRIPTION;

	@FindBy(css = ".benefit .custom-check-box")
	WebElement BENEFIT_CHECKBOX;

	@FindBy(id = "discountPercent")
	WebElement MEMBERSHIP_DISCOUNT;
	
	@FindBy(id = "discountMaxAmount")
	WebElement MAXIMUM_DISCOUNT;

	@FindBy(css = ".group-btn .btn-save")
	WebElement SAVE_BTN;
	
	@FindBy(css = ".group-btn .gs-button__gray--outline")
	WebElement CANCEL_BTN;
	
	@FindBy(css = "[data-sherpherd=\"tour-guide-alert-button-close\"]")
	WebElement CLOSE_BTN;
	
	public CreateLoyaltyProgram inputTierName(String tierName) {
		commonAction.inputText(TIER_NAME, tierName);
		logger.info("Input '" + tierName + "' into Tier Name field.");
		return this;
	}

	public CreateLoyaltyProgram clickAddSegment() {
		commonAction.clickElement(ADD_SEGMENT_LNKTXT);
		logger.info("Clicked on 'Add Segment'");
		return this;
	}

	public CreateLoyaltyProgram uploadImages(String... fileNames) {
		commonAction.uploadMultipleFile(UPLOAD_BTN, "membership_images", fileNames);
		logger.info("Upload multiple file: " + Arrays.toString(fileNames));
		return this;
	}

	public CreateLoyaltyProgram inputDescription(String membershipDescription) {
		commonAction.inputText(DESCRIPTION, membershipDescription);
		logger.info("Input '" + membershipDescription + "' into Description field.");
		return this;
	}

	public CreateLoyaltyProgram checkBenefitCheckBox(boolean checked) {
		if (checked) {
			commonAction.checkTheCheckBoxOrRadio(BENEFIT_CHECKBOX);
			logger.info("Checked 'Membership Level Benefits' checkbox.");
		} else {
			commonAction.uncheckTheCheckboxOrRadio(BENEFIT_CHECKBOX);
			logger.info("Un-checked 'Membership Level Benefits' checkbox.");
		}
		return this;
	}

	public CreateLoyaltyProgram inputMembershipDiscount(String membershipDiscount) {
		commonAction.inputText(MEMBERSHIP_DISCOUNT, membershipDiscount);
		logger.info("Input '" + membershipDiscount + "' into Membership Discount field.");
		return this;
	}

	public CreateLoyaltyProgram inputMaximumDiscount(String maximumDiscount) {
		commonAction.inputText(MAXIMUM_DISCOUNT, maximumDiscount);
		logger.info("Input '" + maximumDiscount + "' into Maximum Discount field.");
		return this;
	}

	public CreateLoyaltyProgram clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on 'Save' button");
		return this;
	}
	
	public CreateLoyaltyProgram clickCancelBtn() {
		commonAction.clickElement(SAVE_BTN);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}
	
	public CreateLoyaltyProgram clickCloseBtn() {
		commonAction.clickElement(CLOSE_BTN);
		logger.info("Clicked on 'Close' button");
		return this;
	}

	/**
	Create a membership level
	*/
	public CreateLoyaltyProgram createMembershipLevel(String tierName, String avatar, String customerSegment, String description, String discountPercent, String maximumDiscount) {
		inputTierName(tierName);
		uploadImages(avatar);
		clickAddSegment();
	
		new AddCollectionDialog(driver)
		.inputSearchTerm(customerSegment)
		.selectCustomerSegment(customerSegment)
		.clickOKBtn();
		
		inputDescription(description);
		checkBenefitCheckBox(true);
		inputMembershipDiscount(discountPercent);
		inputMaximumDiscount(maximumDiscount);
		clickSaveBtn();
		clickCloseBtn();
		return this;
	}	
	
}
