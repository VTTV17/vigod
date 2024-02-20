package web.Dashboard.customers.segments.createsegment;

import static utilities.character_limit.CharacterLimit.MAX_SEGMENT_NAME_LENGTH;
import static utilities.character_limit.CharacterLimit.MIN_SEGMENT_NAME_LENGTH;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import utilities.commons.UICommonAction;
import web.Dashboard.home.HomePage;

public class CreateSegment {

	final static Logger logger = LogManager.getLogger(CreateSegment.class);

	WebDriver driver;
	UICommonAction commonAction;
	CreateSegmentElement elements;

	public static String segmentName;

	public CreateSegment(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		elements = new CreateSegmentElement();
	}

	public CreateSegment inputSegmentName(String... segmentName) {
		CreateSegment.segmentName = segmentName.length == 0
				? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_SEGMENT_NAME_LENGTH - MIN_SEGMENT_NAME_LENGTH + 1) + MIN_SEGMENT_NAME_LENGTH)
				: segmentName[0];
		commonAction.sendKeys(elements.loc_txtSegmentName, CreateSegment.segmentName);
		logger.info("Input '" + CreateSegment.segmentName + "' into Segment Name field.");
		return this;
	}

	public CreateSegment selectDataGroupCondition(String dataGroup) {
		commonAction.selectByVisibleText(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("1")), dataGroup);
		logger.info("Selected Data Group: " + dataGroup);
		return this;
	}

	public CreateSegment selectDataCondition(String data) {	
		commonAction.selectByVisibleText(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("2")), data);
		logger.info("Selected Data: " + data);
		return this;
	}
	public String getSelectedtDataCondition() {	
		return new Select(commonAction.getElement(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("2")))).getFirstSelectedOption().getText();
	}	
	
	public CreateSegment selectComparisonOperatorCondition(String operator) {
		commonAction.selectByVisibleText(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("3")), operator);
		logger.info("Selected Operator: " + operator);
		return this;
	}
	
	public CreateSegment selectComparedValueCondition(String comparedValue) {
		commonAction.selectByVisibleText(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("4")), comparedValue);
		logger.info("Selected compared value: " + comparedValue);
		return this;
	}
	
	public CreateSegment inputComparedValueCondition(String comparedValue) {
		commonAction.inputText(By.xpath(elements.loc_frmConditionFragmentsInputTag.formatted("4")), comparedValue);
		logger.info("Input '" + comparedValue + "' into Compared value field.");
		return this;
	}

	public CreateSegment selectSearchTimePeriodCondition(String searchTimePeriod) {
		commonAction.selectByVisibleText(By.xpath(elements.loc_frmConditionFragmentsSelectTag.formatted("6")), searchTimePeriod);
		logger.info("Selected search time period: " + searchTimePeriod);
		return this;
	}
	
	public CreateSegment clickSelectProduct() {
		commonAction.click(By.xpath(elements.loc_frmConditionFragmentsSpanTag.formatted("2")));
		logger.info("Clicked on 'Select product' button.");
		return this;
	}	

	public CreateSegment clickSaveBtn() {
		commonAction.click(elements.loc_btnSave);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		logger.info("Clicked on 'Save' button");
		return this;
	}
	
	public CreateSegment clickCancelBtn() {
		commonAction.click(elements.loc_btnSave);
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
