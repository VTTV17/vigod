import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.dashboard.login.Login;
import api.dashboard.products.ProductInformation;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.orders.createquotation.CreateQuotation;
import utilities.PropertiesUtil;
import utilities.jsonFileUtility;

public class CreateQuotationTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	CreateQuotation createQuotationPage;

	String displayLanguage = "VIE";

	String[] products = { "Product with wholesale pricing", "Product with IMEIs",
			"Product with conversion units + wholesale pricing", "Product with variations + wholesale pricing",
			"Product with variations + conversion units + wholesale pricing", "Product with variations",
			"Product with SKU", "product2 with conversion units", "Product with IMEIs + variations" };

	JsonNode sellerData = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String sellerUsername = sellerData.findValue("seller").findValue("mail").findValue("username").asText();
	String sellerPassword = sellerData.findValue("seller").findValue("mail").findValue("password").asText();
	String sellerCountry = sellerData.findValue("seller").findValue("mail").findValue("country").asText();

	
	public void instantiatePageObjects() {
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		createQuotationPage = new CreateQuotation(driver);
	}

	@BeforeMethod
	public void setup() throws InterruptedException {
		super.setup();
		instantiatePageObjects();
	}
	
	/**
	 * 
	 * @param condition searchProductByName/searchProductByBarcode
	 * @param displayLanguage
	 * @return
	 * @throws Exception
	 */
	public String tranlateSearchCondition(String condition, String displayLanguage) throws Exception {
		return PropertiesUtil.getPropertiesValueByDBLang("quotation.create." + condition, displayLanguage);
	}
	
	public String randomSearchProduct() {
		return products[(new Random().nextInt(0, products.length))];
	}		

	public List<Integer> getProductIDListByAPI() {
		new Login().loginToDashboardByMail(sellerUsername, sellerPassword);
		return new ProductInformation().getProductList();
	}		
	
	public String randomSearchProductID() {
		List<Integer> id = getProductIDListByAPI();
        return String.valueOf(id.get((new Random().nextInt(0, id.size()))));
	}	
	
	public List<String> extractProductNameFromSearchResults(List<List<String>> results) {
		List<String> extractedNames = new ArrayList<>();
		for (List<String> review : results) {
			extractedNames.add(review.get(0));
		}
		return extractedNames;
	} 	
	
	public List<String> extractProductIDFromSearchResults(List<List<String>> results) {
		List<String> extractedIDs = new ArrayList<>();
		for (List<String> review : results) {
			extractedIDs.add(review.get(2));
		}
		return extractedIDs;
	} 	
	
    public void verifyResultMatchNameSearchTerm(List<List<String>> resutls, String searchTerm){
		Assert.assertNotEquals(resutls.size(), 0, "Number of found records");
    	List<String> names = extractProductNameFromSearchResults(resutls);
    	for (String name : names) {
    		Assert.assertTrue(name.toLowerCase().contains(searchTerm.toLowerCase()));
    	}
    }
    
    public void verifyResultMatchIDSearchTerm(List<List<String>> resutls, String searchTerm){
    	Assert.assertNotEquals(resutls.size(), 0, "Number of found records");
    	List<String> names = extractProductIDFromSearchResults(resutls);
    	for (String name : names) {
    		Assert.assertTrue(name.toLowerCase().contains(searchTerm.toLowerCase()));
    	}
    }

	@Test
	public void CQ_01_CheckTranslation() throws Exception {

		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		/* Check text at management screen */
		createQuotationPage.navigate().verifyTextAtCreateQuotationScreen(displayLanguage);
	}

	@Test
	public void CQ_02_CreateQuotationForNoOne() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		createQuotationPage.navigate();
		
		/* Add products */
		for (String product : products) {
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
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
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		createQuotationPage.navigate();
		
		/* Add products */
		for (String product : products) {
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
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
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
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
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);
		
		createQuotationPage.navigate();
		
		createQuotationPage.selectSearchCondition(tranlateSearchCondition("searchProductByBarcode", displayLanguage));
		
		createQuotationPage.inputProductSearchTerm(randomSearchID);
		
		verifyResultMatchIDSearchTerm(createQuotationPage.getSearchResults(), randomSearchID);
	}
	
	@Test
	public void CQ_06_RemoveProductFromQuotation() throws Exception {
		
		/* Log into dashboard */
		dbLoginPage.navigate().performLogin(sellerCountry, sellerUsername, sellerPassword);
		homePage.waitTillSpinnerDisappear().selectLanguage(displayLanguage);

		createQuotationPage.navigate();
		
		for (String product : products) {
			createQuotationPage.inputProductSearchTerm(product);
			createQuotationPage.selectProduct(product);
		}
		
		for (String product : products) {
			createQuotationPage.removeItemFromListQuotation();
			createQuotationPage.confirmProductRemoval();
		}
	}

}
