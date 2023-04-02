package pages.dashboard.customers.segments;

import static utilities.character_limit.CharacterLimit.MAX_SEGMENT_NAME_LENGTH;
import static utilities.character_limit.CharacterLimit.MIN_SEGMENT_NAME_LENGTH;

import java.time.Duration;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class CreateSegment {

	final static Logger logger = LogManager.getLogger(CreateSegment.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public static String segmentName;

	public CreateSegment(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".btn-save")
	WebElement SAVE_BTN;
	
	@FindBy(css = ".btn-cancel")
	WebElement CANCEL_BTN;
	
	@FindBy(id = "segmentName")
	WebElement SEGMENT_NAME;
	
	/*
	 * 0-indexed element: Data group
	 * 1-indexed element: Data or Select product linktext
	 * 2-indexed element: Comparison operator
	 * 3-indexed element: Compared value
	 * 5-indexed element: Search time period
	 */
	@FindBy(xpath = "//div[@class='segment-condition-row row']//div[@class='form-group']")
	List<WebElement> CONDITION_FRAGMENTS;

	
	public CreateSegment inputSegmentName(String... segmentName) {
		CreateSegment.segmentName = segmentName.length == 0
				? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_SEGMENT_NAME_LENGTH - MIN_SEGMENT_NAME_LENGTH + 1) + MIN_SEGMENT_NAME_LENGTH)
				: segmentName[0];
		commonAction.inputText(SEGMENT_NAME, CreateSegment.segmentName);
		logger.info("Input '" + CreateSegment.segmentName + "' into Segment Name field.");
		return this;
	}

	public CreateSegment selectDataGroupCondition(String dataGroup) {
		String selectedOption = commonAction.selectByVisibleText(CONDITION_FRAGMENTS.get(0).findElement(By.xpath("//select")), dataGroup);
		logger.info("Selected Data Group: " + selectedOption);
		return this;
	}

	public CreateSegment selectDataCondition(String data) {	
		String selectedOption = commonAction.selectByVisibleText(CONDITION_FRAGMENTS.get(1).findElement(By.xpath(".//select")), data);
		logger.info("Selected Data: " + selectedOption);
		return this;
	}
	public String getSelectedtDataCondition() {	
		WebElement selectedOption = new Select(CONDITION_FRAGMENTS.get(1).findElement(By.xpath(".//select"))).getFirstSelectedOption();
		return selectedOption.getText();
	}	
	
	public CreateSegment selectComparisonOperatorCondition(String operator) {
		String selectedOption = commonAction.selectByVisibleText(CONDITION_FRAGMENTS.get(2).findElement(By.xpath(".//select")), operator);
		logger.info("Selected Operator: " + selectedOption);
		return this;
	}
	
	public CreateSegment selectComparedValueCondition(String comparedValue) {
		String selectedOption = commonAction.selectByVisibleText(CONDITION_FRAGMENTS.get(3).findElement(By.xpath(".//select")), comparedValue);
		logger.info("Selected compared value: " + selectedOption);
		return this;
	}
	
	public CreateSegment inputComparedValueCondition(String comparedValue) {
		commonAction.inputText(CONDITION_FRAGMENTS.get(3).findElement(By.xpath(".//input")), comparedValue);
		logger.info("Input '" + comparedValue + "' into Compared value field.");
		return this;
	}

	public CreateSegment selectSearchTimePeriodCondition(String searchTimePeriod) {
		String selectedOption = commonAction.selectByVisibleText(CONDITION_FRAGMENTS.get(5).findElement(By.xpath(".//select")), searchTimePeriod);
		logger.info("Selected search time period: " + selectedOption);
		return this;
	}
	
	public CreateSegment clickSelectProduct() {
		commonAction.clickElement(CONDITION_FRAGMENTS.get(1).findElement(By.xpath("./preceding-sibling::span")));
		logger.info("Clicked on 'Create Segment' button.");
		return this;
	}	

	public CreateSegment clickSaveBtn() {
		commonAction.clickElement(SAVE_BTN);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		logger.info("Clicked on 'Save' button");
		return this;
	}
	
	public CreateSegment clickCancelBtn() {
		commonAction.clickElement(SAVE_BTN);
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
