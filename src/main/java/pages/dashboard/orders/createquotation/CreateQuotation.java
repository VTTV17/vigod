package pages.dashboard.orders.createquotation;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.FileUtils;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;

public class CreateQuotation {

	final static Logger logger = LogManager.getLogger(CreateQuotation.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;
	HomePage homePage;

	SoftAssert soft = new SoftAssert();

	public CreateQuotation(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".gs-page-title")
	WebElement PAGE_TITLE;	
	
	@FindBy(css = ".quotation-in-store-purchase-cart-product-list__table-header")
	WebElement TABLE_HEADER;
	
	@FindBy(css = ".quotation-instore-purchase .uik-btn__base")
	WebElement SEARCH_PRODUCT_BY;
	
	@FindBy(css = "#dropdownSuggestionProduct input.uik-input__input")
	WebElement PRODUCT_SEARCH_BOX;
	
	@FindBy(css = ".search-list__result .product-item-row")
	List<WebElement> PRODUCT_SEARCH_RESULTS;
	
	@FindBy(css = ".order-in-store-purchase-customer input.uik-input__input")
	WebElement CUSTOMER_SEARCH_BOX;
	
	@FindBy(css = ".search-result .mobile-customer-profile-row__info")
	List<WebElement> CUSTOMER_SEARCH_RESULTS;
	
	@FindBy(css = ".quotation-in-store-purchase-complete .align-items-center")
	List<WebElement> MONEY_SUMMARY;

	@FindBy(css = ".quotation-in-store-purchase-complete button.gs-button__green")
	WebElement EXPORT_QUO_BTN;

	@FindBy(css = ".quotation-in-store-purchase-cart-product-list__stock-input")
	WebElement QUANTITY_BOX;

	@FindBy(xpath = "//i[@class='gs-action-button  ']")
	WebElement DELETE_ITEM_ICON;

	@FindBy(css = "div.modal-footer button.gs-button.gs-button__green.gs-button--undefined")
	WebElement OK_BTN;

	@FindBy(css = "div.modal-footer button.gs-button.gs-button__gray--outline.gs-button--undefined")
	WebElement CANCEL_BTN;	

	public CreateQuotation navigate() {
		new HomePage(driver).navigateToPage("Orders", "Create Quotation");
		return this;
	}	
	
	public CreateQuotation inputProductSearchTerm(String searchTerm) {
		commonAction.inputText(PRODUCT_SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}
	
	public CreateQuotation selectSearchCondition(String searchBy) {
		commonAction.clickElement(SEARCH_PRODUCT_BY);
		By conditionXpath = By.xpath("./following-sibling::*//div[@class='uik-select__label' and text()='%s']".formatted(searchBy));
		commonAction.clickElement(SEARCH_PRODUCT_BY.findElement(conditionXpath));
		logger.info("Selected search condition: " + searchBy);
		return this;
	}
	
	/**
	 * Temporary function, will be deleted soon
	 * @return
	 */
	public CreateQuotation emptyProductSearchBox() {
		commonAction.inputText(PRODUCT_SEARCH_BOX, "<>?@#$%^");
		
		for (int i=0; i<6; i++) {
			if (PRODUCT_SEARCH_RESULTS.size() == 0) break; 
			commonAction.sleepInMiliSecond(500);
		}
		
		logger.info("Emptied search box");
		return this;
	}

	public List<List<String>> getSearchResults() {
		for (int i=0; i<10; i++) {
			if (PRODUCT_SEARCH_RESULTS.size() >0) break; 
			commonAction.sleepInMiliSecond(500);
		}
		
		List<List<String>> table = new ArrayList<>();
		for (WebElement row : PRODUCT_SEARCH_RESULTS) {
			List<String> rowData = new ArrayList<>();
			rowData.add(row.findElement(By.xpath(".//*[contains(@class,'product-item-row__product-name')]")).getText()); //Get name
			List<WebElement> variationList = row.findElements(By.xpath(".//*[contains(@class,'product-item-row__variation-name')]"));
			String variationValue = variationList.size() >0 ? variationList.get(0).getText() : "";
			rowData.add(variationValue); // Get variation
			rowData.add(row.findElement(By.xpath(".//code")).getText()); // Get Barcode
			rowData.add(row.findElement(By.xpath(".//*[contains(@class,'product-item-row__price')]")).getText()); //Get price
			List<WebElement> conversionList = row.findElements(By.xpath(".//p"));
			String conversionValue = conversionList.size() >0 ? conversionList.get(0).getText() : "";
			rowData.add(conversionValue); // Get conversion unit
			
			table.add(rowData);
		}
		return table;
	}		

	public CreateQuotation selectProduct(String name) {
		By productXpath = By.xpath("//*[contains(@class,'product-item-row__product-name') and text()='%s']".formatted(name));
		commonAction.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(productXpath)));
		logger.info("Selected product: " + name);
		return this;
	}
	
	public CreateQuotation inputCustomerSearchTerm(String searchTerm) {
		new HomePage(driver).hideFacebookBubble();
		commonAction.inputText(CUSTOMER_SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Customer Search box.");
		return this;
	}	

	public CreateQuotation selectCustomer(String name) {
		By customerXpath = By.xpath("//div[@class='full-name' and text()='%s']".formatted(name));
		commonAction.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(customerXpath)));
		logger.info("Selected customer: " + name);
		return this;
	}	
	
	public CreateQuotation clickExportQuotationBtn() {
		new HomePage(driver).hideFacebookBubble();
		commonAction.clickElement(EXPORT_QUO_BTN);
		logger.info("Clicked on 'Export Quotation' button.");
		return this;
	}

	public CreateQuotation inputQuantity(String number) {
		commonAction.inputText(QUANTITY_BOX,number);
		logger.info("Inputted "+number+" into quantity box.");
		return this;
	}

	public CreateQuotation removeItemFromListQuotation() {
		new HomePage(driver).hideFacebookBubble();
		commonAction.clickElement(DELETE_ITEM_ICON);
		logger.info("Clicked on Delete icon on list product quotation.");
		return this;
	}

	public CreateQuotation confirmProductRemoval() {
		new ConfirmationDialog(driver).clickOKBtn();
		return this;
	}	

	public String getSubTotal() {
		String text = commonAction.getText(MONEY_SUMMARY.get(0).findElement(By.tagName("b")));
		logger.info("Retrieved subtotal: " + text);
		return text;
	}	
	
	public String getVAT() {
		String text = commonAction.getText(MONEY_SUMMARY.get(1).findElement(By.tagName("span")));
		logger.info("Retrieved VAT: " + text);
		return text;
	}	
	
	public String getTotal() {
		String text = commonAction.getText(MONEY_SUMMARY.get(2).findElement(By.tagName("span")));
		logger.info("Retrieved Total: " + text);
		return text;
	}	
	
	public List<List<String>> readQuotationFile() {
		File file = FileUtils.getLastDownloadedFile(FileNameAndPath.downloadFolder);
		
        Sheet dataSheet = null;
		try {
			dataSheet = new Excel().getSheet(file, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<List<String>> table = new ArrayList<>();
		
        int lastRowIndex = dataSheet.getLastRowNum();
        
        String storeNameTitle = dataSheet.getRow(0).getCell(0).getStringCellValue();
        String storeNameValue = dataSheet.getRow(0).getCell(1).getStringCellValue();
        table.add(Arrays.asList(storeNameTitle, storeNameValue));
        
        String storePhoneTitle = dataSheet.getRow(1).getCell(0).getStringCellValue();
        String storePhoneValue = dataSheet.getRow(1).getCell(1).getStringCellValue();
        table.add(Arrays.asList(storePhoneTitle, storePhoneValue));
        
        String storeEmailTitle = dataSheet.getRow(2).getCell(0).getStringCellValue();
        String storeEmailValue = dataSheet.getRow(2).getCell(1).getStringCellValue();
        table.add(Arrays.asList(storeEmailTitle, storeEmailValue));
        
        String customerInfoTitle = dataSheet.getRow(4).getCell(0).getStringCellValue();
        table.add(Arrays.asList(customerInfoTitle));
        
        String customerNameTitle = dataSheet.getRow(5).getCell(0).getStringCellValue();
        String customerNameValue = "";
        if (!new Excel().isCellBlank(dataSheet.getRow(5).getCell(1))) {
        	customerNameValue = dataSheet.getRow(5).getCell(1).getStringCellValue();
        }
        
        table.add(Arrays.asList(customerNameTitle, customerNameValue));
        
        String customerPhoneTitle = dataSheet.getRow(6).getCell(0).getStringCellValue();
        String customerPhoneValue = "";
        if (!new Excel().isCellBlank(dataSheet.getRow(6).getCell(1))) {
        	customerPhoneValue = dataSheet.getRow(6).getCell(1).getStringCellValue();
        }
        table.add(Arrays.asList(customerPhoneTitle, customerPhoneValue));
        
        String customerEmailTitle = dataSheet.getRow(7).getCell(0).getStringCellValue();
        String customerEmailValue = "";
        if (!new Excel().isCellBlank(dataSheet.getRow(7).getCell(1))) {
        	customerEmailValue = dataSheet.getRow(7).getCell(1).getStringCellValue();
        }
        table.add(Arrays.asList(customerEmailTitle, customerEmailValue));
        
        String numberTitle = dataSheet.getRow(8).getCell(0).getStringCellValue();
        String imageTitle = dataSheet.getRow(8).getCell(1).getStringCellValue();
        String productNameTitle = dataSheet.getRow(8).getCell(2).getStringCellValue();
        String quantityTitle = dataSheet.getRow(8).getCell(3).getStringCellValue();
        String unitPriceTitle = dataSheet.getRow(8).getCell(4).getStringCellValue();
        String totalPriceTitle = dataSheet.getRow(8).getCell(5).getStringCellValue();
        table.add(Arrays.asList(numberTitle, imageTitle, productNameTitle, quantityTitle, unitPriceTitle, totalPriceTitle));
        
        int startProductRowIndex = 9;
        int lastProductRowIndex = lastRowIndex -3;
        
        int startProductColumnIndex = 0;
        int lastProductColumnIndex = 5;
		
		for (int i=startProductRowIndex; i<=lastProductRowIndex; i++) {
			List<String> rowData = new ArrayList<>();
			for (int j=startProductColumnIndex; j<=lastProductColumnIndex; j++) {
				rowData.add(dataSheet.getRow(i).getCell(j).getStringCellValue());
			}
			table.add(rowData);
		}
        
        String subTotalTitle = dataSheet.getRow(lastRowIndex-2).getCell(4).getStringCellValue();
        String subTotalValue = dataSheet.getRow(lastRowIndex-2).getCell(5).getStringCellValue();
        table.add(Arrays.asList(subTotalTitle, subTotalValue));
        
        String vatTitle = dataSheet.getRow(lastRowIndex-1).getCell(4).getStringCellValue();
        String vatValue = dataSheet.getRow(lastRowIndex-1).getCell(5).getStringCellValue();
        table.add(Arrays.asList(vatTitle, vatValue));
        
        String totalTitle = dataSheet.getRow(lastRowIndex).getCell(4).getStringCellValue();
        String totalValue = dataSheet.getRow(lastRowIndex).getCell(5).getStringCellValue();
        table.add(Arrays.asList(totalTitle, totalValue));	
        
		return table;
	}	

    public void verifyTextAtCreateQuotationScreen(String signupLanguage) throws Exception {
    	
    	String text = commonAction.getText(PAGE_TITLE).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.title", signupLanguage), text);
  
    	text = commonAction.getText(SEARCH_PRODUCT_BY);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName", signupLanguage), text);
    	
    	text = commonAction.getElementAttribute(PRODUCT_SEARCH_BOX, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName.placeHolder", signupLanguage));
    	
    	text = commonAction.getText(TABLE_HEADER);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.table.header", signupLanguage), text);
    
    	text = commonAction.getElementAttribute(CUSTOMER_SEARCH_BOX, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchCustomer.placeHolder", signupLanguage));
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(0).findElement(By.tagName("p")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.subTotal", signupLanguage), text);
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(1).findElement(By.tagName("p")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.vat", signupLanguage), text);
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(2).findElement(By.tagName("b")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.total", signupLanguage), text);
    	
    	text = commonAction.getText(EXPORT_QUO_BTN);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.exportBtn", signupLanguage), text);

    	logger.info("verifyTextAtCreateQuotationScreen completed");
    }  		
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateQuotation(String permission, String url) {
		if (permission.contentEquals("A")) {
			inputProductSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/   	
	
}
