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

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.login.Login;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.ProductInformation;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.orders.createquotation.CreateQuotation;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.jsonFileUtility;
import utilities.driver.InitWebdriver;

public class CreateQuotationTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	CreateQuotation createQuotationPage;
	
	List<String> productList;
	List<Integer> productIDList;
	List<List<String>> convUnitProductList;
	
	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String sellerUsername = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String sellerPassword = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String sellerCountry = sellerData.findValue("seller").findValue("mail").findValue("country").asText();

	
	@BeforeClass
	public void getDataByAPI() {
        new Login().loginToDashboardByMail(sellerUsername, sellerPassword);
        productList = new APIAllProducts().getAllProductNames();
        productIDList = new ProductInformation().getProductList();
        convUnitProductList = new ProductInformation().getIdAndNameOfProductWithConversionUnits();
	}		
	
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		createQuotationPage = new CreateQuotation(driver);
		commonAction = new UICommonAction(driver);
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
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
	
	public String randomSearchProduct() {
		return productList.get((new Random().nextInt(0, productList.size())));
	}		

	public String randomSearchProductID() {
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
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);

		/* Check text at management screen */
		createQuotationPage.navigate().verifyTextAtCreateQuotationScreen();
	}

	@Test
	public void CQ_02_CreateQuotationForNoOne() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);
		
		createQuotationPage.navigate();
		
		/* Add products */
		for (int i=0; i<productList.size(); i++) {
			String product = productList.get(i);
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
			if (i==9) break; 
		}
		
		createQuotationPage.inputCustomerSearchTerm("");
		createQuotationPage.clickExportQuotationBtn();
		commonAction.sleepInMiliSecond(5000);
		
		String dbSubTotal = createQuotationPage.getSubTotal().replaceAll("\\D", "");
		String dbVAT = createQuotationPage.getVAT().replaceAll("\\D", "");
		String dbTotal = createQuotationPage.getTotal().replaceAll("\\D", "");
		
		List<List<String>> table = createQuotationPage.readQuotationFile();
		
		int lastTableRowIndex = table.size()-1;
		
		String fileSubTotal = table.get(lastTableRowIndex-2).get(1);
		String fileVAT = table.get(lastTableRowIndex-1).get(1);
		String fileTotal = table.get(lastTableRowIndex).get(1);
		
		Assert.assertEquals(fileSubTotal, dbSubTotal, "Subtotal");
		Assert.assertEquals(fileVAT, dbVAT, "VAT");
		Assert.assertEquals(fileTotal, dbTotal, "Total");
	}	
	
	@Test
	public void CQ_03_CreateQuotationForCustomer() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);

		createQuotationPage.navigate();
		
		/* Add products */
		for (int i=0; i<productList.size(); i++) {
			String product = productList.get(i);
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
			if (i==9) break; 
		}
		
		/* Select customer */
		String customer = "Automation Buyer 7574137150";
		createQuotationPage.inputCustomerSearchTerm(customer);
		createQuotationPage.selectCustomer(customer);
		
		createQuotationPage.clickExportQuotationBtn();
		commonAction.sleepInMiliSecond(5000);
		
		String dbSubTotal = createQuotationPage.getSubTotal().replaceAll("\\D", "");
		String dbVAT = createQuotationPage.getVAT().replaceAll("\\D", "");
		String dbTotal = createQuotationPage.getTotal().replaceAll("\\D", "");
		
		List<List<String>> table = createQuotationPage.readQuotationFile();
		
		int lastTableRowIndex = table.size()-1;
		
		String fileSubTotal = table.get(lastTableRowIndex-2).get(1);
		String fileVAT = table.get(lastTableRowIndex-1).get(1);
		String fileTotal = table.get(lastTableRowIndex).get(1);
		
		Assert.assertEquals(fileSubTotal, dbSubTotal, "Subtotal");
		Assert.assertEquals(fileVAT, dbVAT, "VAT");
		Assert.assertEquals(fileTotal, dbTotal, "Total");
	}
	
	@Test
	public void CQ_04_SearchProductByName() throws Exception {
		
        String randomSearchProduct = randomSearchProduct();
        String searchTerm = randomSearchProduct.substring(0, randomSearchProduct.length()/2);
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);
		
		createQuotationPage.navigate();
		
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
	}
	
	@Test
	public void CQ_05_SearchProductByBarcode() throws Exception {
		
		String randomSearchID = randomSearchProductID();
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);
		
		createQuotationPage.navigate();
		
		createQuotationPage.selectSearchCondition(tranlateSearchCondition("searchProductByBarcode"));
		
		createQuotationPage.inputProductSearchTerm(randomSearchID);
		
		verifyResultMatchIDSearchTerm(createQuotationPage.getSearchResults(), randomSearchID);
	}
	
	@Test
	public void CQ_06_RemoveProductFromQuotation() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(language);

		createQuotationPage.navigate();
		
		List<String> conv = convUnitProductList.get((new Random().nextInt(0, convUnitProductList.size())));
		List<String> conversionUnits = new APIAllProducts().getConversionUnitsOfProduct(Integer.valueOf(conv.get(0)));
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
