package pages.dashboard.products.transfer;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Transfer {
	WebDriver driver;
	UICommonAction commons;

	final static Logger logger = LogManager.getLogger(Transfer.class);

	public Transfer(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_tmpRecord = By.xpath("//div[contains(@class,'transfer-management')]//table/tbody/tr");
	By loc_txtSearchRecord = By.cssSelector(".transfer-management .uik-input__input");
	By loc_btnCreateTransfer = By.cssSelector(".transfer-management .gs-button__green");
	By loc_txtNote = By.cssSelector(".transfer-form-editor #text-note");
	By loc_btnSave = By.cssSelector(".transfer-form-editor .gs-button__green");
	By loc_txtSearchProduct = By.cssSelector(".search-box .uik-input__input");
	By loc_btnTransferredQuantity = By.xpath("//*[contains(@class,'transfer-form-editor')]//tbody/tr[1]//input[@inputmode='numeric']");
	By loc_ddlBranches = By.cssSelector(".information [type='button'] .uik-select__valueWrapper");
	By loc_btnShipGoods = By.cssSelector(".transfer-toolbar .btn-save");
	
	String PRODUCT_NAME_IN_RESULT = "//div[contains(@class,'search-item')]/div/span[position()=1 %s]";
	
    public Transfer navigate() {
    	new HomePage(driver).navigateToPage("Products", "Transfer");
    	commons.waitVisibilityOfElementLocated(loc_btnCreateTransfer);
        return this;
    }	
    
    public Transfer waitTillPageStable() {
    	commons.waitVisibilityOfElementLocated(loc_btnCreateTransfer);
    	new HomePage(driver).waitTillLoadingDotsDisappear().waitTillSpinnerDisappear1();
    	return this;
    }	

	public Transfer clickAddTransferBtn() {
		commons.click(loc_btnCreateTransfer);
		commons.waitVisibilityOfElementLocated(loc_txtNote);
		logger.info("Clicked on 'Add Transfer' button.");
		commons.sleepInMiliSecond(1000);
		return this;
	}    
    
	public Transfer inputSearchTerm(String searchTerm) {
		commons.sendKeys(loc_txtSearchRecord, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	
	public Transfer inputProductSearchTerm(String searchTerm) {
		commons.sendKeys(loc_txtSearchProduct, searchTerm);
		logger.info("Input '" + searchTerm + "' into Product Search box.");
		commons.sleepInMiliSecond(500); //There's a delay of 500ms before search operation commences
		By searchLoadingIcon = By.xpath("//div[contains(@class,'search-result')]/div[contains(@class,'loading')]");
		commons.waitInvisibilityOfElementLocated(searchLoadingIcon);
		return this;
	}

	public Transfer selectProduct(String name) {
		By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()=\"%s\"".formatted(name)));
		commons.click(productXpath);
		logger.info("Selected product: " + name);
		return this;
	}	
	
	public Transfer selectSourceBranch(String name) {
		commons.click(loc_ddlBranches, 0);
		By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
		commons.click(branchName);
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
		commons.click(loc_ddlBranches, 1);
		By branchName = By.xpath("//div[contains(@class,'information')]//div[contains(@class,'uik-select__label') and text()='%s']".formatted(name));
		commons.click(branchName);
		logger.info("Selected destination branch: " + name);
		return this;
	}	

	public Transfer inputTransferredQuantity(int quantity) {
		commons.sendKeys(loc_btnTransferredQuantity, String.valueOf(quantity));
		logger.info("Input '" + quantity + "' into Transferred Quantity field.");
		return this;
	}		
	
	public Transfer selectIMEI(String imei) {
		By imeiLocator = By.xpath("(//div[@class='code in-purchase'])[2]/div[@class='content']/p[text()='%s']".formatted(imei));
		commons.click(imeiLocator);
		logger.info("Selected IMEI: " + imei);
		return this;
	}	
	
	public Transfer selectIMEI(String[] imei) {
		commons.click(new ByChained(loc_btnTransferredQuantity, By.xpath(".//ancestor::div[@class='number']//following-sibling::span")));
		for (String value : imei) {
			selectIMEI(value);
		}
		new ConfirmationDialog(driver).clickOKBtn();
		return this;
	}	
	
	public Transfer inputNote(String note) {
		commons.sendKeys(loc_txtNote, note);
		logger.info("Input '" + note + "' into Note field.");
		return this;
	}	

	public Transfer clickSaveBtn() {
		commons.click(loc_btnSave);
		logger.info("Clicked on 'Save' button to add a product transfer.");
		return this;
	}    
	
	public Transfer clickShipGoodsBtn() {
		commons.click(loc_btnShipGoods);
		logger.info("Clicked on 'Ship Goods' or 'Receive Goods' button.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}    
	
	public Transfer clickReceiveGoodsBtn() {
		return clickShipGoodsBtn();
	}    

	public Transfer clickRecord(int recordID) {
		By record = By.cssSelector("[href='/product/transfer/wizard/%s']".formatted(recordID));
		commons.click(record);
		logger.info("Clicked on transfer record '%s'.".formatted(recordID));
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}	
	
	public List<String> getSpecificRecord(int index) {
		//Wait until records are present
		for (int i=0; i<6; i++) {
			if (commons.getElements(loc_tmpRecord).size() >0) break;
			commons.sleepInMiliSecond(500);
		}
		
		/*
		 * Loop through the columns of the specific record
		 * and store data of the column into an array.
		 * Retry the process when StaleElementReferenceException occurs
		 */
		try {
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commons.getElement(loc_tmpRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		} catch (StaleElementReferenceException ex) {
			logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
			List<String> rowData = new ArrayList<>();
			for (WebElement column : commons.getElement(loc_tmpRecord, index).findElements(By.xpath("./td"))) {
				rowData.add(column.getText());
			}
			return rowData;
		}
	}

	public List<List<String>> getRecords() {
		waitTillPageStable();
		List<List<String>> table = new ArrayList<>();
		for (int i=0; i<commons.getElements(loc_tmpRecord).size(); i++) {
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
