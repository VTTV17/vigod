package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.products.APIAllProducts;
import api.Seller.products.ProductInformation;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.createquotation.CreateQuotation;
import web.Dashboard.settings.storeinformation.StoreInformation;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.account.AccountTest;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;

public class CreateQuotationTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	CreateQuotation createQuotationPage;
	
	String username;
	String password;
	String country;
	
	List<String> customerList;
	List<String> productList;
	List<Integer> productIDList;
	List<List<String>> convUnitProductList;
	LoginInformation loginInformation;

	@BeforeClass
	public void loadTestData() {
		getLoginInfo();
		getDataByAPI();
	}	
	
	public void getLoginInfo() {
		username = AccountTest.ADMIN_USERNAME_TIEN;
		password = AccountTest.ADMIN_PASSWORD_TIEN;
		country = AccountTest.ADMIN_COUNTRY_TIEN;
	}	
	
	public void getDataByAPI() {
        loginInformation = new Login().setLoginInformation(username, password).getLoginInformation();
        customerList = new Customers(loginInformation).getAllAccountCustomer();
        productList = new APIAllProducts(loginInformation).getAllProductNames();
        productIDList = new ProductInformation(loginInformation).getProductList();
        convUnitProductList = new ProductInformation(loginInformation).getIdAndNameOfProductWithConversionUnits();
	}		
	
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		createQuotationPage = new CreateQuotation(driver);
		commonAction = new UICommonAction(driver);
	}
	
	public void logIntoDashboard() {
		dbLoginPage.navigate().performLogin(country, username, password);
		homePage.selectLanguage(language);
	}

	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
	}
	
	/**
	 * 
	 * @param condition searchProductByName/searchProductByBarcode
	 * @return
	 * @throws Exception
	 */
	public String tranlateSearchCondition(String condition) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("quotation.create." + condition);
	}
	
	public String randomCustomer() {
		return customerList.get((new Random().nextInt(0, customerList.size())));
	}		
	
	public String randomProduct() {
		return productList.get((new Random().nextInt(0, productList.size())));
	}		

	public String randomProductID() {
        return String.valueOf(productIDList.get((new Random().nextInt(0, productIDList.size()))));
	}	

	public List<String> extractElementAtIndex(List<List<String>> results, int index) {
	    return results.stream().map(list -> list.get(index)).collect(Collectors.toList());
	}
	
	public List<String> extractProductNameFromSearchResults(List<List<String>> results) {
		return extractElementAtIndex(results, 0);
	} 	
	
	public List<String> extractProductIDFromSearchResults(List<List<String>> results) {
		return extractElementAtIndex(results, 2);
	} 	
	
    public void verifyResultMatchNameSearchTerm(List<List<String>> results, String searchTerm){
		Assert.assertNotEquals(results.size(), 0, "Number of found records");
    	List<String> names = extractProductNameFromSearchResults(results);
    	for (String name : names) {
    		Assert.assertTrue(name.toLowerCase().contains(searchTerm.toLowerCase()));
    	}
    }
    
    public void verifyResultMatchIDSearchTerm(List<List<String>> results, String searchTerm){
    	Assert.assertNotEquals(results.size(), 0, "Number of found records");
    	List<String> ids = extractProductIDFromSearchResults(results);
    	for (String id : ids) {
    		Assert.assertTrue(id.toLowerCase().contains(searchTerm.toLowerCase()));
    	}
    }

	@Test
	public void CQ_01_CheckTranslation() throws Exception {

		/* Log into dashboard */
		logIntoDashboard();

		/* Check text at management screen */
		createQuotationPage.navigate().verifyTextAtCreateQuotationScreen();
	}

	@Test
	public void CQ_02_CreateQuotationForNoOne() throws Exception {
		
		/* Log into dashboard */
		logIntoDashboard();
		
		homePage.navigateToPage("Settings");
		
		StoreInformation storeInfo = new StoreInformation(driver);
		storeInfo.navigate();
		
		String storeName = storeInfo.getShopName();
		String storePhone = storeInfo.getHoline();
		String storeEmail = storeInfo.getEmail();
		
		createQuotationPage.navigate();
		
		/* Add products */
		for (int i=0; i<productList.size(); i++) {
			String randomSearchProduct = randomProduct();
			createQuotationPage.inputProductSearchTerm(randomSearchProduct);
			createQuotationPage.selectProduct(randomSearchProduct);
			if (i==9) break; 
		}
		
		commonAction.sleepInMiliSecond(2000);
		
		createQuotationPage.clickExportQuotationBtn();
		
		String dbSubTotal = createQuotationPage.getSubTotal().replaceAll("\\D", "");
		String dbVAT = createQuotationPage.getVAT().replaceAll("\\D", "");
		String dbTotal = createQuotationPage.getTotal().replaceAll("\\D", "");
		
		List<List<String>> table = createQuotationPage.readQuotationFile();
		
		String fileStoreName = table.get(0).get(1);
		String fileStorePhone = table.get(1).get(1);
		String fileStoreEmail = table.get(2).get(1);
		
		int lastTableRowIndex = table.size()-1;
		
		String fileSubTotal = table.get(lastTableRowIndex-2).get(1);
		String fileVAT = table.get(lastTableRowIndex-1).get(1);
		String fileTotal = table.get(lastTableRowIndex).get(1);
		
		Assert.assertEquals(fileSubTotal, dbSubTotal, "Subtotal");
		Assert.assertEquals(fileVAT, dbVAT, "VAT");
		Assert.assertEquals(fileTotal, dbTotal, "Total");
		Assert.assertEquals(fileStoreName, storeName, "Store name");
		Assert.assertEquals(fileStorePhone, "'"+storePhone, "Store phone");
		Assert.assertEquals(fileStoreEmail, storeEmail, "Store email");
	}	
	
	@Test
	public void CQ_03_CreateQuotationForCustomer() throws Exception {
		
		/* Log into dashboard */
		logIntoDashboard();

		homePage.navigateToPage("Settings");
		
		StoreInformation storeInfo = new StoreInformation(driver);
		storeInfo.navigate();
		
		String storeName = storeInfo.getShopName();
		String storePhone = storeInfo.getHoline();
		String storeEmail = storeInfo.getEmail();
		
		createQuotationPage.navigate();

		/* Select customer */
		String customer = randomCustomer();
		createQuotationPage.inputCustomerSearchTerm(customer);
		createQuotationPage.selectCustomer(customer);		
		
		/* Add products */
		for (int i=0; i<productList.size(); i++) {
			String randomSearchProduct = randomProduct();
			createQuotationPage.inputProductSearchTerm(randomSearchProduct);
			createQuotationPage.selectProduct(randomSearchProduct);
			if (i==9) break; 
		}
		
		commonAction.sleepInMiliSecond(2000);
		
		createQuotationPage.clickExportQuotationBtn();
		
		String dbSubTotal = createQuotationPage.getSubTotal().replaceAll("\\D", "");
		String dbVAT = createQuotationPage.getVAT().replaceAll("\\D", "");
		String dbTotal = createQuotationPage.getTotal().replaceAll("\\D", "");
		
		List<String> customerInfo = createQuotationPage.getSelectedCustomerData();
		
		List<List<String>> table = createQuotationPage.readQuotationFile();
		
		String fileStoreName = table.get(0).get(1);
		String fileStorePhone = table.get(1).get(1);
		String fileStoreEmail = table.get(2).get(1);
		
		int lastTableRowIndex = table.size()-1;
		
		String fileSubTotal = table.get(lastTableRowIndex-2).get(1);
		String fileVAT = table.get(lastTableRowIndex-1).get(1);
		String fileTotal = table.get(lastTableRowIndex).get(1);
		
		Assert.assertEquals(table.get(4).get(1), customerInfo.get(0), "Customer name");
		if(customerInfo.get(1).isEmpty()) {
			Assert.assertTrue(table.get(5).get(1).contentEquals("'") || table.get(5).get(1).contentEquals("'undefined"));
		} else {
			Assert.assertEquals(table.get(5).get(1), "'"+customerInfo.get(1), "Customer phone");
		}
		Assert.assertEquals(fileStoreName, storeName, "Store name");
		Assert.assertEquals(fileStorePhone, "'"+storePhone, "Store phone");
		Assert.assertEquals(fileStoreEmail, storeEmail, "Store email");
		Assert.assertEquals(fileSubTotal, dbSubTotal, "Subtotal");
		Assert.assertEquals(fileVAT, dbVAT, "VAT");
		Assert.assertEquals(fileTotal, dbTotal, "Total");
	}	
	
	@Test
	public void CQ_04_SearchProductByName() throws Exception {
		
		/* Log into dashboard */
		logIntoDashboard();
		
		createQuotationPage.navigate();
		
		for (int i=0; i<productList.size(); i++) {
	        String randomSearchProduct = randomProduct();
	        String searchTerm = randomSearchProduct.substring(0, randomSearchProduct.length()/2);
			
			/* Absolute match */
			createQuotationPage.inputProductSearchTerm(randomSearchProduct);
			verifyResultMatchNameSearchTerm(createQuotationPage.getSearchResults(), randomSearchProduct);
			
			/* Partly match */
			createQuotationPage.emptyProductSearchBox();
			createQuotationPage.inputProductSearchTerm(searchTerm);
			verifyResultMatchNameSearchTerm(createQuotationPage.getSearchResults(), searchTerm);
			
			/* Ignore case */
			createQuotationPage.emptyProductSearchBox();
			createQuotationPage.inputProductSearchTerm(randomSearchProduct.toLowerCase());
			verifyResultMatchNameSearchTerm(createQuotationPage.getSearchResults(), randomSearchProduct.toLowerCase());
			
			createQuotationPage.emptyProductSearchBox();
			createQuotationPage.inputProductSearchTerm(randomSearchProduct.toUpperCase());
			verifyResultMatchNameSearchTerm(createQuotationPage.getSearchResults(), randomSearchProduct.toUpperCase());
			
			createQuotationPage.emptyProductSearchBox();
			if (i==2) break;
		}
	}
	
	@Test
	public void CQ_05_SearchProductByBarcode() throws Exception {
		
		/* Log into dashboard */
		logIntoDashboard();
		
		createQuotationPage.navigate();
		
		createQuotationPage.selectSearchCondition(tranlateSearchCondition("searchProductByBarcode"));
		
		for (int i=0; i<productList.size(); i++) {
			String randomSearchID = randomProductID();
			createQuotationPage.inputProductSearchTerm(randomSearchID);
			
			verifyResultMatchIDSearchTerm(createQuotationPage.getSearchResults(), randomSearchID);
			
			createQuotationPage.emptyProductSearchBox();
			if (i==2) break;
		}
		

	}
	
	@Test
	public void CQ_06_RemoveProductFromQuotation() throws Exception {
		
		/* Log into dashboard */
		logIntoDashboard();

		createQuotationPage.navigate();
		
		List<String> conv = convUnitProductList.get((new Random().nextInt(0, convUnitProductList.size())));
		List<String> conversionUnits = new APIAllProducts(loginInformation).getConversionUnitsOfProduct(Integer.valueOf(conv.get(0)));
		createQuotationPage.inputProductSearchTerm(conv.get(1));
		createQuotationPage.selectProduct(Arrays.asList(conv.get(1),"", "", "", conversionUnits.get(0)));
		createQuotationPage.verifyErrorWhenSelectingProductWithConversionUnits();
		
		for (int i=0; i<productList.size(); i++) {
			String product = productList.get(i);
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
			if (i==9) break; 
		}
		
		for (int i=0; i<productList.size(); i++) {
			createQuotationPage.removeItemFromListQuotation();
			createQuotationPage.confirmProductRemoval();
			if (i==9) break; 
		}
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }
	
}
