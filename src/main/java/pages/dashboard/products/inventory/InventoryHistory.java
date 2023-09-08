package pages.dashboard.products.inventory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.dashboard.cashbook.Cashbook;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.transfer.Transfer;
import utilities.UICommonAction;

public class InventoryHistory {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(InventoryHistory.class);

	public InventoryHistory(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".uik-input__input")
	WebElement HISTORY_SEARCH_BOX;		

	By HISTORY_RECORDS = By.xpath("//div[contains(@class,'inventory_history')]//table/tbody/tr");
	
	public InventoryHistory inputSearchTerm(String searchTerm) {
		commons.inputText(HISTORY_SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		commons.sleepInMiliSecond(1000); //It takes some time for the search operation to commence after the search term is input
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}		

	public List<WebElement> getRecordElements(By byLocator) {
		return commons.getElements(byLocator);
	}
	
	public List<String> getSpecificRecord(int index) {
		//Wait until records are present
		for (int i=0; i<10; i++) {
			if (!getRecordElements(HISTORY_RECORDS).isEmpty()) {
				break;
			}
			commons.sleepInMiliSecond(500);
		}
		
		/*
		 * Loop through the columns of the specific record
		 * and store data of the column into an array.
		 * Retry the process when StaleElementReferenceException occurs
		 */
		try {
			List<String> rowData = new ArrayList<>();
			List<WebElement> columns = getRecordElements(HISTORY_RECORDS).get(index).findElements(By.xpath("./td"));
			for (int i=0; i<columns.size(); i++) {
				if (i==0) continue; //Skip the first column as it only contains pictures
				rowData.add(columns.get(i).getText());
			}
			return rowData;
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
			List<String> rowData = new ArrayList<>();
			List<WebElement> columns = getRecordElements(HISTORY_RECORDS).get(index).findElements(By.xpath("./td"));
			for (int i=0; i<columns.size(); i++) {
				if (i==0) continue; //Skip the first column as it only contains pictures
				rowData.add(columns.get(i).getText());
			}
			return rowData;
		}
	}

	public List<List<String>> getRecords() {
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<getRecordElements(HISTORY_RECORDS).size(); i++) {
			table.add(getSpecificRecord(i));
		}
		return table;
	}   	
	
}
