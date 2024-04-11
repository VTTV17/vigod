package web.Dashboard.marketing.loyaltyprogram;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;

public class CreateLoyaltyProgram {

	final static Logger logger = LogManager.getLogger(CreateLoyaltyProgram.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateLoyaltyProgram(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtMembershipName = By.id("name");
	By loc_lnkAddSegment = By.cssSelector(".segment-add .gs-fake-link");
	By loc_btnUploadImage = By.cssSelector(".image-upload input");
	By loc_txtDescription = By.cssSelector("div.fr-wrapper > div");
	By loc_chkBenefit = By.cssSelector(".benefit .custom-check-box");
	By loc_txtPercentAmount = By.id("discountPercent");
	By loc_txtMaxDiscountAmount = By.id("discountMaxAmount");
	By loc_btnSave = By.cssSelector(".group-btn .btn-save");
	By loc_btnCancel = By.cssSelector(".group-btn .gs-button__gray--outline");
	By loc_btnClose = By.cssSelector("[data-sherpherd='tour-guide-alert-button-close']");

	public CreateLoyaltyProgram inputTierName(String tierName) {
		commonAction.sendKeys(loc_txtMembershipName, tierName);
		logger.info("Input '" + tierName + "' into Tier Name field.");
		return this;
	}

	public CreateLoyaltyProgram clickAddSegment() {
		commonAction.click(loc_lnkAddSegment);
		logger.info("Clicked on 'Add Segment'");
		return this;
	}

	public CreateLoyaltyProgram uploadImages(String imgFile) {
        Path filePath = Paths.get("%s%s".formatted(System.getProperty("user.dir"), "/src/main/resources/uploadfile/membership_images/%s".formatted(imgFile)));
		commonAction.uploads(loc_btnUploadImage, filePath.toString());
		logger.info("Upload file: " + imgFile);
		return this;
	}

	public CreateLoyaltyProgram inputDescription(String membershipDescription) {
		commonAction.sendKeys(loc_txtDescription, membershipDescription);
		logger.info("Input '" + membershipDescription + "' into Description field.");
		return this;
	}

	public CreateLoyaltyProgram checkBenefitCheckBox(boolean checked) {
		WebElement benefitCheckBox = commonAction.getElement(loc_chkBenefit);
		if (checked) {
			commonAction.checkTheCheckBoxOrRadio(benefitCheckBox);
			logger.info("Checked 'Membership Level Benefits' checkbox.");
		} else {
			commonAction.uncheckTheCheckboxOrRadio(benefitCheckBox);
			logger.info("Un-checked 'Membership Level Benefits' checkbox.");
		}
		return this;
	}

	public CreateLoyaltyProgram inputMembershipDiscount(String membershipDiscount) {
		commonAction.sendKeys(loc_txtPercentAmount, membershipDiscount);
		logger.info("Input '" + membershipDiscount + "' into Membership Discount field.");
		return this;
	}

	public CreateLoyaltyProgram inputMaximumDiscount(String maximumDiscount) {
		commonAction.sendKeys(loc_txtMaxDiscountAmount, maximumDiscount);
		logger.info("Input '" + maximumDiscount + "' into Maximum Discount field.");
		return this;
	}

	public CreateLoyaltyProgram clickSaveBtn() {
		commonAction.click(loc_btnSave);
		logger.info("Clicked on 'Save' button");
		return this;
	}
	
	public CreateLoyaltyProgram clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}
	
	public CreateLoyaltyProgram clickCloseBtn() {
		commonAction.click(loc_btnClose);
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
	
		new AddSegmentDialog(driver)
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
	public CreateLoyaltyProgram createRandomMembership(){
		String random = new DataGenerator().generateString(10);
		inputTierName("Membership "+random);
		clickAddSegment();
		new AddSegmentDialog(driver)
				.selectCustomerSegment()
				.clickOKBtn();
		inputDescription("Description "+random);
		return this;
	}
	public void navigateByUrl(){
		String url = Links.DOMAIN + "/marketing/loyalty/create";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
	}
}
