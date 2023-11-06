package pages.dashboard.products.transfer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Transfer {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(Transfer.class);

	public Transfer(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//div[contains(@class,'transfer-management')]//table/tbody/tr")
	List<WebElement> TRANSFER_RECORDS;	
	
	@FindBy(css = ".transfer-management .uik-input__input")
	WebElement SEARCH_BOX;
	
//	String PRODUCT_NAME_IN_RESULT = "//*[contains(@class,'product-item')]//div[contains(@class, 'search-item')]";
	String PRODUCT_NAME_IN_RESULT = "//div[contains(@class,'search-item')]/div/span[position()=1 %s]";

	By ADDTRANSFER_BTN = By.cssSelector(".transfer-management .gs-button__green");
	
	By NOTE_TEXTBOX = By.cssSelector(".transfer-form-editor #text-note");
	
	@FindBy(css = ".transfer-form-editor .gs-button__green")
	WebElement SAVE_BTN;
	
	@FindBy(css = ".search-box .uik-input__input")
	WebElement PRODUCTSEARCH_BOX;
	
	@FindBy(xpath = "//*[contains(@class,'transfer-form-editor')]//tbody/tr[1]//input[@inputmode='numeric']")
	WebElement TRANSFERREDQUANTITY_BOX;
	
	@FindBy(css = ".information [type='button'] .uik-select__valueWrapper")
	List<WebElement> TRANSFER_BRANCHES;
	
	@FindBy(css = ".transfer-toolbar .btn-save")
	WebElement SHIPGOODS_BTN;
	
    public Transfer navigate() {
    	new HomePage(driver).navigateToPage("Products", "Transfer");
    	wait.until(ExpectedConditions.presenceOfElementLocated(ADDTRANSFER_BTN));
        return this;
    }	
    
    public Transfer waitTillPageStable() {
    	wait.until(ExpectedConditions.presenceOfElementLocated(ADDTRANSFER_BTN));
    	new HomePage(driver).waitTillLoadingDotsDisappear().waitTillSpinnerDisappear1();
    	return this;
    }	

	public Transfer clickAddTransferBtn() {
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(ADDTRANSFER_BTN)));
		wait.until(ExpectedConditions.presenceOfElementLocated(NOTE_TEXTBOX));
		logger.info("Clicked on 'Add Transfer' button.");
		commons.sleepInMiliSecond(1000);
		return this;
	}    
    
	public Transfer inputSearchTerm(String searchTerm) {
		commons.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	
	public Transfer inputProductSearchTerm(String searchTerm) {
		commons.inputText(PRODUCTSEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Product Search box.");
		commons.sleepInMiliSecond(500); //There's a delay of 500ms before search operation commences
		By searchLoadingIcon = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'loading')]");
		commons.waitForElementInvisible(commons.getElement(searchLoadingIcon), 30);
		return this;
	}

	public Transfer selectProduct(String name) {
		By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()=\"%s\"".formatted(name)));
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(productXpath)));
		logger.info("Selected product: " + name);
		return this;
	}	
	
	public Transfer selectSourceBranch(String name) {
		commons.clickElement(TRANSFER_BRANCHES.get(0));
		By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(branchName)));
		logger.info("Selected source branch: " + name);
		return this;
	}	
	
	public List<List<String>> getSearchResults() {
		By results = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'product-item')]");
		
		By name = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=1 %s]".formatted(""));
		By barcode = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=2 %s]".formatted(""));
		By variation = By.xpath(".//div[contains(@class,'search-item')]/div/span[position()=3 %s]".formatted(""));
		By inventory = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=1 %s]".formatted(""));
		By unit = By.xpath(".//div[contains(@class,'search-item')]/span/p[position()=2 %s]".formatted(""));
		 
		List<List<String>> resultList = new ArrayList<>();
		
		for (int i=0; i<commons.getElements(results).size(); i++) {
			List<String> temp = new ArrayList<>();
			
			// Get name
			temp.add(commons.getElements(results).get(i).findElement(name).getText());
			
			// Get barcode
			temp.add(commons.getElements(results).get(i).findElement(barcode).getText());
			
			// Get variation value if the product has variations
			if (commons.getElements(results).get(i).findElements(variation).size() > 0) {
				temp.add(commons.getElements(results).get(i).findElement(variation).getText());
			} else {
				temp.add("");
			}
			
			// Get inventory
			temp.add(commons.getElements(results).get(i).findElement(inventory).getText());
			
			// Get conversion units if the product has conversion units
			if (commons.getElements(results).get(i).findElements(unit).size() > 0) {
				temp.add(commons.getElements(results).get(i).findElement(unit).getText());
			} else {
				temp.add("");
			}
			
			resultList.add(temp);
		}
		return resultList;
	}	
	
	public Transfer selectDestinationBranch(String name) {
		commons.clickElement(TRANSFER_BRANCHES.get(1));
		By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(branchName)));
		logger.info("Selected destination branch: " + name);
		return this;
	}	

	public Transfer inputTransferredQuantity(int quantity) {
		commons.inputText(TRANSFERREDQUANTITY_BOX, String.valueOf(quantity));
		logger.info("Input '" + quantity + "' into Transferred Quantity field.");
		return this;
	}		
	
	public Transfer selectIMEI(String imei) {
		By imeiLocator = By.xpath("(//div[@class='code in-purchase'])[2]/div[@class='content']/p[text()='%s']".formatted(imei));
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(imeiLocator)));
		logger.info("Selected IMEI: " + imei);
		return this;
	}	
	
	public Transfer selectIMEI(String[] imei) {
		commons.clickElement(TRANSFERREDQUANTITY_BOX.findElement(By.xpath(".//ancestor::div[@class='number']//following-sibling::span")));
		for (String value : imei) {
			selectIMEI(value);
		}
		new ConfirmationDialog(driver).clickOKBtn();
		return this;
	}	
	
	public Transfer inputNote(String note) {
		commons.inputText(commons.getElement(NOTE_TEXTBOX), note);
		logger.info("Input '" + note + "' into Note field.");
		return this;
	}	

	public Transfer clickSaveBtn() {
		commons.clickElement(SAVE_BTN);
		logger.info("Clicked on 'Save' button to add a product transfer.");
		return this;
	}    
	
	public Transfer clickShipGoodsBtn() {
		commons.clickElement(SHIPGOODS_BTN);
		logger.info("Clicked on 'Ship Goods' or 'Receive Goods' button.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}    
	
	public Transfer clickReceiveGoodsBtn() {
		return clickShipGoodsBtn();
	}    

	public Transfer clickRecord(int recordID) {
		By record = By.cssSelector("[href='/product/transfer/wizard/%s']".formatted(recordID));
		commons.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(record)));
		logger.info("Clicked on transfer record '%s'.".formatted(recordID));
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	
	
	public List<String> getSpecificRecord(int index) {
		//Wait until records are present
		for (int i=0; i<6; i++) {
			if (!TRANSFER_RECORDS.isEmpty()) break;
			commons.sleepInMiliSecond(500);
		}
		
		/*
		 * Loop through the columns of the specific record
		 * and store data of the column into an array.
		 * Retry the process when StaleElementReferenceException occurs
		 */
		try {
			List<String> rowData = new ArrayList<>();
			for (WebElement column : TRANSFER_RECORDS.get(index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
			List<String> rowData = new ArrayList<>();
			for (WebElement column : TRANSFER_RECORDS.get(index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		}
	}

	public List<List<String>> getRecords() {
		waitTillPageStable();
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<TRANSFER_RECORDS.size(); i++) {
			table.add(getSpecificRecord(i));
		}
		return table;
	}   
    	
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToTransferProduct(String permission, String url) {
		if (permission.contentEquals("A")) {
			Assert.assertTrue(commons.getCurrentURL().contains(url));
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commons.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }


    /*-------------------------------------*/   	
	
}
