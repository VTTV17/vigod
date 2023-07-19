package pages.dashboard.orders.createquotation;

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

import pages.dashboard.confirmationdialog.ConfirmationDialog;
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
	
	@FindBy(css = ".information .name")
	List<WebElement> SELECTED_CUSTOMER_NAME;
	
	@FindBy(css = ".information .phone")
	List<WebElement> SELECTED_CUSTOMER_PHONE;
	
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

	String PRODUCT_NAME_IN_RESULT = ".//*[contains(@class,'product-item-row__product-name') %s]";
	String VARIATION_IN_RESULT = ".//*[contains(@class,'product-item-row__variation-name') %s]";
	String PRODUCT_BARCODE_IN_RESULT = ".//code[' ' %s]";
	String PRICE_IN_RESULT = ".//*[contains(@class,'product-item-row__price') %s]";
	String CONV_UNIT_IN_RESULT = ".//p[' ' %s]";
	
	public CreateQuotation navigate() {
		homePage.navigateToPage("Orders", "Create Quotation");
		homePage.hideFacebookBubble();
		return this;
	}	
	
	public CreateQuotation inputProductSearchTerm(String searchTerm) {
		commonAction.inputText(PRODUCT_SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}
	
	/**
	 * 
	 * @param searchBy Product/Barcode or Sản phẩm/Mã Vạch
	 * @return
	 */
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

	/**
	 * Retrieves the search results data
	 * @return the list of lists containing search result information such as name, variation, barcode, price and conversion unit
	 */
	public List<List<String>> getSearchResults() {
		//Wait till there are results on search results box
		for (int i=0; i<10; i++) {
			if (PRODUCT_SEARCH_RESULTS.size() >0) break; 
			commonAction.sleepInMiliSecond(500);
		}
		
		//Get data result data
		List<List<String>> table = new ArrayList<>();
		for (WebElement row : PRODUCT_SEARCH_RESULTS) {
			List<String> rowData = new ArrayList<>();
			
			WebElement nameElement = row.findElement(By.xpath(PRODUCT_NAME_IN_RESULT.formatted("")));
			rowData.add(nameElement.getText()); //Get name
			
			List<WebElement> variations = row.findElements(By.xpath(VARIATION_IN_RESULT.formatted("")));
			String variationValue = variations.isEmpty() ? "" : variations.get(0).getText();
			rowData.add(variationValue); // Get variation
			
			WebElement barcodeElement = row.findElement(By.xpath(PRODUCT_BARCODE_IN_RESULT.formatted("")));
			rowData.add(barcodeElement.getText()); // Get Barcode
			
			WebElement priceElement = row.findElement(By.xpath(PRICE_IN_RESULT.formatted("")));
			rowData.add(priceElement.getText()); //Get price
			
			List<WebElement> conversionUnits = row.findElements(By.xpath(CONV_UNIT_IN_RESULT.formatted("")));
			String conversionValue = conversionUnits.isEmpty() ? "" : conversionUnits.get(0).getText();
			rowData.add(conversionValue); // Get conversion unit
			
			table.add(rowData);
		}
		return table;
	}		
	
	/**
	 * Selects a product from search results based on the provided data to add to quotation
	 * @param productData The list of strings containing product name, variation, barcode, price and conversion unit
	 * <p>Eg. String[] pr = {"Product with variations", "L|Red", "", "", ""};
	 * <p>selectProduct(Arrays.asList(pr));
	 * @return
	 */
	public CreateQuotation selectProduct(List<String> productData) {
		String name = productData.get(0);
		String variation = productData.get(1);
		String barcode = productData.get(2);
		String price = productData.get(3);
		String convUnit = productData.get(4);
		
		List<List<String>> results = getSearchResults();
		
		for(int i=0; i<results.size(); i++) {
			if (!results.get(i).get(0).contentEquals(name)) continue;
			
			// If variation value is not provided, we proceed to next step
			if (variation.length() >0) {
				if (!results.get(i).get(1).contentEquals(variation)) continue;
			}
			// If barcode value is not provided, we proceed to next step
			if (barcode.length() >0) {
				if (!results.get(i).get(2).contentEquals(barcode)) continue;
			}
			// If price value is not provided, we proceed to next step
			if (price.length() >0) {
				if (!results.get(i).get(3).contentEquals(price)) continue;
			}
			// If conversion unit value is not provided, we proceed to next step
			if (convUnit.length() >0) {
				//Extract conversion unit value from string of "Unit: <conversion_unit>"
				String retrievedConvUnit = results.get(i).get(4);
				
				if (retrievedConvUnit.length()==0) continue;
				
				String extractedConvUnit = retrievedConvUnit.substring(retrievedConvUnit.indexOf(":") + 3);
				
				if (!extractedConvUnit.contentEquals(convUnit)) continue;
			}
			commonAction.clickElement(PRODUCT_SEARCH_RESULTS.get(i));
			
			/**
			 * Code to check if the product is selected goes here
			 */
			
			break;
		}
		logger.info("Selected product: " + name);
		return this;
	}

	public CreateQuotation selectProduct(String name) {
		By productXpath = By.xpath(PRODUCT_NAME_IN_RESULT.formatted("and text()='%s'".formatted(name)));
		commonAction.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(productXpath)));
		logger.info("Selected product: " + name);
		return this;
	}
	
	public CreateQuotation inputCustomerSearchTerm(String searchTerm) {
		homePage.hideFacebookBubble();
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

	/**
	 * Retrieves the selected customer's name and phone number
	 * @return A list of Strings containing the selected customer's name and phone number
	 */
	public List<String> getSelectedCustomerData() {
		List<String> customerData = new ArrayList<>();
		
		String name = SELECTED_CUSTOMER_NAME.isEmpty() ? "" : SELECTED_CUSTOMER_NAME.get(0).getText();
		customerData.add(name); //Get name
		
		String phone = SELECTED_CUSTOMER_PHONE.isEmpty() ? "" : SELECTED_CUSTOMER_PHONE.get(0).getText();
		customerData.add(phone); // Get phone number
		
		return customerData;
	}
	
	public CreateQuotation clickExportQuotationBtn() {
		homePage.hideFacebookBubble();
		commonAction.clickElement(EXPORT_QUO_BTN);
		logger.info("Clicked on 'Export Quotation' button.");
		commonAction.sleepInMiliSecond(5000);
		return this;
	}

	public CreateQuotation inputQuantity(String number) {
		commonAction.inputText(QUANTITY_BOX,number);
		logger.info("Inputted "+number+" into quantity box.");
		return this;
	}

	public CreateQuotation removeItemFromListQuotation() {
		homePage.hideFacebookBubble();
		commonAction.clickElement(DELETE_ITEM_ICON);
		logger.info("Clicked on Delete icon on list product quotation.");
		return this;
	}

	public CreateQuotation confirmProductRemoval() {
		new ConfirmationDialog(driver).clickOKBtn();
		return this;
	}	

	public String getSubTotal() {
		logger.info("Getting subtotal...");
		String text = commonAction.getText(MONEY_SUMMARY.get(0).findElement(By.tagName("b")));
		
		return text;
	}	
	
	public String getVAT() {
		logger.info("Getting VAT...");
		String text = commonAction.getText(MONEY_SUMMARY.get(1).findElement(By.tagName("span")));
		return text;
	}	
	
	public String getTotal() {
		logger.info("Getting Total...");
		String text = commonAction.getText(MONEY_SUMMARY.get(2).findElement(By.tagName("span")));
		return text;
	}	

	public List<List<String>> readQuotationFile() {
	
		int STORE_NAME_ROW = 0;
		int STORE_NAME_COL = 1;
		int STORE_PHONE_ROW = 1;
		int STORE_PHONE_COL = 1;
		int STORE_EMAIL_ROW = 2;
		int STORE_EMAIL_COL = 1;
		int CUSTOMER_INFO_ROW = 4;
		int CUSTOMER_NAME_ROW = 5;
		int CUSTOMER_NAME_COL = 1;
		int CUSTOMER_PHONE_ROW = 6;
		int CUSTOMER_PHONE_COL = 1;
		int CUSTOMER_EMAIL_ROW = 7;
		int CUSTOMER_EMAIL_COL = 1;
		int PRODUCT_START_ROW = 9;
		int HEADER_ROW = 8;
		int NUMBER_COL = 0;
		int IMAGE_COL = 1;
		int PRODUCT_NAME_COL = 2;
		int QUANTITY_COL = 3;
		int UNIT_PRICE_COL = 4;
		int TOTAL_PRICE_COL = 5;		
		
        Excel excel = new Excel();
		
		Sheet dataSheet = null;
		try {
			dataSheet = excel.getSheet(FileUtils.getLastDownloadedFile(FileNameAndPath.downloadFolder), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<List<String>> table = new ArrayList<>();
		
        // Store Information
        String storeNameTitle = dataSheet.getRow(STORE_NAME_ROW).getCell(0).getStringCellValue();
        String storeNameValue = dataSheet.getRow(STORE_NAME_ROW).getCell(STORE_NAME_COL).getStringCellValue();
        table.add(Arrays.asList(storeNameTitle, storeNameValue));
        
        String storePhoneTitle = dataSheet.getRow(STORE_PHONE_ROW).getCell(0).getStringCellValue();
        String storePhoneValue = dataSheet.getRow(STORE_PHONE_ROW).getCell(STORE_PHONE_COL).getStringCellValue();
        table.add(Arrays.asList(storePhoneTitle, storePhoneValue));
        
        String storeEmailTitle = dataSheet.getRow(STORE_EMAIL_ROW).getCell(0).getStringCellValue();
        String storeEmailValue = dataSheet.getRow(STORE_EMAIL_ROW).getCell(STORE_EMAIL_COL).getStringCellValue();
        table.add(Arrays.asList(storeEmailTitle, storeEmailValue));
        
        // Customer Information
        String customerInfoTitle = dataSheet.getRow(CUSTOMER_INFO_ROW).getCell(0).getStringCellValue();
        table.add(Arrays.asList(customerInfoTitle));
        
        String customerNameTitle = dataSheet.getRow(CUSTOMER_NAME_ROW).getCell(0).getStringCellValue();
        String customerNameValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(5).getCell(CUSTOMER_NAME_COL))) {
        	customerNameValue = dataSheet.getRow(CUSTOMER_NAME_ROW).getCell(CUSTOMER_NAME_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerNameTitle, customerNameValue));
        
        String customerPhoneTitle = dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(0).getStringCellValue();
        String customerPhoneValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(CUSTOMER_PHONE_COL))) {
        	customerPhoneValue = dataSheet.getRow(CUSTOMER_PHONE_ROW).getCell(CUSTOMER_PHONE_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerPhoneTitle, customerPhoneValue));
        
        String customerEmailTitle = dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(0).getStringCellValue();
        String customerEmailValue = "";
        if (!excel.isCellBlank(dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(CUSTOMER_EMAIL_COL))) {
        	customerEmailValue = dataSheet.getRow(CUSTOMER_EMAIL_ROW).getCell(CUSTOMER_EMAIL_COL).getStringCellValue();
        }
        table.add(Arrays.asList(customerEmailTitle, customerEmailValue));
        
        // Get Header
        String numberTitle = dataSheet.getRow(HEADER_ROW).getCell(NUMBER_COL).getStringCellValue();
        String imageTitle = dataSheet.getRow(HEADER_ROW).getCell(IMAGE_COL).getStringCellValue();
        String productNameTitle = dataSheet.getRow(HEADER_ROW).getCell(PRODUCT_NAME_COL).getStringCellValue();
        String quantityTitle = dataSheet.getRow(HEADER_ROW).getCell(QUANTITY_COL).getStringCellValue();
        String unitPriceTitle = dataSheet.getRow(HEADER_ROW).getCell(UNIT_PRICE_COL).getStringCellValue();
        String totalPriceTitle = dataSheet.getRow(HEADER_ROW).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(numberTitle, imageTitle, productNameTitle, quantityTitle, unitPriceTitle, totalPriceTitle));
        
        //Get Quotation Data
        int lastRowIndex = dataSheet.getLastRowNum();
        int lastProductRowIndex = lastRowIndex -3;
        
		for (int i=PRODUCT_START_ROW; i<=lastProductRowIndex; i++) {
			List<String> rowData = new ArrayList<>();
			for (int j=NUMBER_COL; j<=TOTAL_PRICE_COL; j++) {
				rowData.add(dataSheet.getRow(i).getCell(j).getStringCellValue());
			}
			table.add(rowData);
		}
        
        String subTotalTitle = dataSheet.getRow(lastRowIndex-2).getCell(UNIT_PRICE_COL).getStringCellValue();
        String subTotalValue = dataSheet.getRow(lastRowIndex-2).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(subTotalTitle, subTotalValue));
        
        String vatTitle = dataSheet.getRow(lastRowIndex-1).getCell(UNIT_PRICE_COL).getStringCellValue();
        String vatValue = dataSheet.getRow(lastRowIndex-1).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(vatTitle, vatValue));
        
        String totalTitle = dataSheet.getRow(lastRowIndex).getCell(UNIT_PRICE_COL).getStringCellValue();
        String totalValue = dataSheet.getRow(lastRowIndex).getCell(TOTAL_PRICE_COL).getStringCellValue();
        table.add(Arrays.asList(totalTitle, totalValue));	
        
		return table;
	}	

    public void verifyTextAtCreateQuotationScreen() throws Exception {
    	
    	String text = commonAction.getText(PAGE_TITLE).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.title"), text);
  
    	text = commonAction.getText(SEARCH_PRODUCT_BY);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName"), text);
    	
    	text = commonAction.getElementAttribute(PRODUCT_SEARCH_BOX, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchProductByName.placeHolder"));
    	
    	text = commonAction.getText(TABLE_HEADER);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.table.header"), text);
    
    	text = commonAction.getElementAttribute(CUSTOMER_SEARCH_BOX, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("quotation.create.searchCustomer.placeHolder"));
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(0).findElement(By.tagName("p")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.subTotal"), text);
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(1).findElement(By.tagName("p")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.vat"), text);
    	
    	text = commonAction.getText(MONEY_SUMMARY.get(2).findElement(By.tagName("b")));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.total"), text);
    	
    	text = commonAction.getText(EXPORT_QUO_BTN);
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.exportBtn"), text);

    	logger.info("verifyTextAtCreateQuotationScreen completed");
    }  		
    
    public void verifyErrorWhenSelectingProductWithConversionUnits() throws Exception {
    	String text = homePage.getToastMessage();
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("quotation.create.error.addConversionUnits"), text);
    	logger.info("verifyErrorWhenSelectingProductWithConversionUnits completed");
    }  		
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateQuotation(String permission, String url) {
		if (permission.contentEquals("A")) {
			inputProductSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/   	
	
}
