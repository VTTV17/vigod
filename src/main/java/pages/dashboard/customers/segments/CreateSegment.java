package pages.dashboard.customers.segments;

import static utilities.character_limit.CharacterLimit.MAX_SEGMENT_NAME_LENGTH;
import static utilities.character_limit.CharacterLimit.MIN_SEGMENT_NAME_LENGTH;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class CreateSegment {

	final static Logger logger = LogManager.getLogger(CreateSegment.class);

	WebDriver driver;
	UICommonAction commonAction;

	public static String segmentName;

	public CreateSegment(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnSave = By.cssSelector(".btn-save");
	By loc_btnCancel = By.cssSelector(".btn-cancel");
	By loc_txtSegmentName = By.id("segmentName");
	/*
	 * 0-indexed element: Data group
	 * 1-indexed element: Data or Select product linktext
	 * 2-indexed element: Comparison operator
	 * 3-indexed element: Compared value
	 * 5-indexed element: Search time period
	 */
	By loc_frmConditionFragments = By.xpath("//div[@class='segment-condition-row row']//div[@class='form-group']");

	public CreateSegment inputSegmentName(String... segmentName) {
		CreateSegment.segmentName = segmentName.length == 0
				? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_SEGMENT_NAME_LENGTH - MIN_SEGMENT_NAME_LENGTH + 1) + MIN_SEGMENT_NAME_LENGTH)
				: segmentName[0];
		commonAction.sendKeys(loc_txtSegmentName, CreateSegment.segmentName);
		logger.info("Input '" + CreateSegment.segmentName + "' into Segment Name field.");
		return this;
	}

	public CreateSegment selectDataGroupCondition(String dataGroup) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_frmConditionFragments, 0).findElement(By.xpath("//select")), dataGroup);
		logger.info("Selected Data Group: " + selectedOption);
		return this;
	}

	public CreateSegment selectDataCondition(String data) {	
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_frmConditionFragments, 1).findElement(By.xpath(".//select")), data);
		logger.info("Selected Data: " + selectedOption);
		return this;
	}
	public String getSelectedtDataCondition() {	
		WebElement selectedOption = new Select(commonAction.getElement(loc_frmConditionFragments, 1).findElement(By.xpath(".//select"))).getFirstSelectedOption();
		return selectedOption.getText();
	}	
	
	public CreateSegment selectComparisonOperatorCondition(String operator) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_frmConditionFragments, 2).findElement(By.xpath(".//select")), operator);
		logger.info("Selected Operator: " + selectedOption);
		return this;
	}
	
	public CreateSegment selectComparedValueCondition(String comparedValue) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_frmConditionFragments, 3).findElement(By.xpath(".//select")), comparedValue);
		logger.info("Selected compared value: " + selectedOption);
		return this;
	}
	
	public CreateSegment inputComparedValueCondition(String comparedValue) {
		commonAction.inputText(commonAction.getElement(loc_frmConditionFragments, 3).findElement(By.xpath(".//input")), comparedValue);
		logger.info("Input '" + comparedValue + "' into Compared value field.");
		return this;
	}

	public CreateSegment selectSearchTimePeriodCondition(String searchTimePeriod) {
		String selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_frmConditionFragments, 5).findElement(By.xpath(".//select")), searchTimePeriod);
		logger.info("Selected search time period: " + selectedOption);
		return this;
	}
	
	public CreateSegment clickSelectProduct() {
		commonAction.clickElement(commonAction.getElement(loc_frmConditionFragments, 1).findElement(By.xpath("./preceding-sibling::span")));
		logger.info("Clicked on 'Create Segment' button.");
		return this;
	}	

	public CreateSegment clickSaveBtn() {
		commonAction.click(loc_btnSave);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		logger.info("Clicked on 'Save' button");
		return this;
	}
	
	public CreateSegment clickCancelBtn() {
		commonAction.click(loc_btnSave);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}
	
	/**
	* <p>
	* Create a customer segment
	* <p>
	* Example: createSegment("Rich Customer", "Customers data", "Registration date", "is", "29/09/2022", "")
	*/
	public CreateSegment createSegment(String segmentName, String dataGroup, String data, String operator, String comparedValue, String searchTimePeriod) {
		inputSegmentName(segmentName);
		// Update later
		return this;
	}		
	
}
