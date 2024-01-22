package web.Dashboard.pagination;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Pagination {

	final static Logger logger = LogManager.getLogger(Pagination.class);

	WebDriver driver;
	UICommonAction commonAction;

	public Pagination(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnNext = By.cssSelector(".pagination [aria-label='Next']");
	By loc_btnPrevious = By.cssSelector(".pagination [aria-label='Previous']");

	public Pagination clickNextBtn() {
		commonAction.click(loc_btnNext, 0);
		logger.info("Clicked on 'Next' button at pagination.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public Pagination clickPreviousBtn() {
		commonAction.click(loc_btnPrevious);
		logger.info("Clicked on 'Previous' button at pagination");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public boolean isNextBtnDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return commonAction.getElements(loc_btnNext).size() >0;
	}

}
