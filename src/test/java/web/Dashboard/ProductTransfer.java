package web.Dashboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.products.APIAllProducts;
import api.Seller.products.ProductInformation;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.Dashboard.products.inventory.Inventory;
import web.Dashboard.products.inventory.InventoryHistory;
import web.Dashboard.products.transfer.Transfer;
import utilities.commons.UICommonAction;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ProductTransfer extends BaseTest {

	LoginPage loginPage;
	HomePage homePage;
	Transfer transferPage;
	ProductPage productPage;
	Inventory inventoryPage;
	InventoryHistory inventoryHistoryPage;
	
	String username;
	String password;
	String country;

	List<String> customerList;
	String storeDefaultLanguage;
	LoginInformation loginInformation;
	BranchInfo branchInfo;
	List<String> activeBranches;

	@BeforeClass
	public void loadTestData() {
		username = AccountTest.ADMIN_USERNAME_TIEN;
		password = AccountTest.ADMIN_PASSWORD_TIEN;
		country = AccountTest.ADMIN_COUNTRY_TIEN;
        loginInformation = new Login().setLoginInformation(new DataGenerator().getPhoneCode(country), username, password).getLoginInformation();
        storeDefaultLanguage = new StoreInformation(loginInformation).getInfo().getDefaultLanguage();
        branchInfo = new BranchManagement(loginInformation).getInfo();
        activeBranches = branchInfo.getActiveBranches();
        Locale.setDefault(new Locale("vi", "VN"));
	}	

	@BeforeMethod
	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		transferPage = new Transfer(driver);
		productPage = new ProductPage(driver, loginInformation);
		inventoryPage = new Inventory(driver);
		inventoryHistoryPage = new InventoryHistory(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }	

	public void loginDashboard() {
		loginPage.navigate().performLogin(country, username, password);
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}    
	
	public Map<String, String> pickSourceBranchAndDestinationBranch(Map<String, Integer> apiStockByBranch) {
		String highestStockBranch = "";
		int highestStock = 0;
		String lowestStockBranch = "";
		int lowestStock = Integer.MAX_VALUE;

		for (String branch : apiStockByBranch.keySet()) {
			int stock = apiStockByBranch.get(branch);
			if (stock > highestStock) {
				highestStock = stock;
				highestStockBranch = branch;
			}

			if (stock < lowestStock) {
				lowestStock = stock;
				lowestStockBranch = branch;
			}
		}
		Map<String, String> branch = new HashMap<>();
		branch.put("sourceBranch", highestStockBranch);
		branch.put("destinationBranch", lowestStockBranch);
		return branch;
	}    
	
	public void verifyStockInInventoryHistoryBeforeTransferCompleted(List<String> inStockEvent, int transferredQuantity, String status, Map<String, Integer> initialStock, String destinationBranch) {
		Assert.assertEquals(Integer.valueOf(inStockEvent.get(1)), transferredQuantity, "In-Stock Change Quantity");
		Assert.assertEquals(Integer.valueOf(inStockEvent.get(2)), initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), status, "In-Stock Status");
	}   
	
	public void verifyStockOutInventoryHistoryBeforeTransferCompleted(List<String> outStockEvent, int transferredQuantity, String status, Map<String, Integer> initialStock, String sourceBranch) {
		Assert.assertEquals(outStockEvent.get(1), "-" + transferredQuantity, "Out-Stock Change Quantity");
		Assert.assertEquals(Integer.valueOf(outStockEvent.get(2)), initialStock.get(sourceBranch)-transferredQuantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), status, "Out-Stock Status");
	}    
	
	public void verifyStockInInventoryHistoryAfterTransferCompleted(List<String> inStockEvent, int transferredQuantity, String status, Map<String, Integer> initialStock, String destinationBranch) {
		Assert.assertEquals(Integer.valueOf(inStockEvent.get(1)), transferredQuantity, "In-Stock Change Quantity");
		Assert.assertEquals(Integer.valueOf(inStockEvent.get(2)), initialStock.get(destinationBranch)+transferredQuantity, "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), status, "In-Stock Status");
	}   
	
	public void verifyStockOutInventoryHistoryAfterTransferCompleted(List<String> outStockEvent, int transferredQuantity, String status, Map<String, Integer> initialStock, String sourceBranch) {
		Assert.assertEquals(outStockEvent.get(1), "-" + transferredQuantity, "Out-Stock Change Quantity");
		Assert.assertEquals(Integer.valueOf(outStockEvent.get(2)), initialStock.get(sourceBranch)-transferredQuantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), status, "Out-Stock Status");
	}   

	@Test
	public void PT_02_TransferBasicProductWithoutVariations() {

		int productId = 1015539;
		String product = "Bird Food";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "My Shop Vietnam";
		String destinationBranch = "CN3";
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);

		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> initialStock = productPage.getStock();
		
		transferPage.navigate();
		
		List<String> initialRecord = transferPage.getSpecificRecord(0);
		int firstInitialRecordId = Integer.valueOf(initialRecord.get(0));
		
		transferPage.clickAddTransferBtn()
		.selectSourceBranch(sourceBranch)
		.selectDestinationBranch(destinationBranch)
		.inputProductSearchTerm(product)
		.selectProduct(product)
		.inputTransferredQuantity(quantity)
		.inputNote(note)
		.clickSaveBtn();
		homePage.getToastMessage();
		
		commonAction.navigateBack();
		commonAction.navigateBack();
		
		List<String> finalRecord = transferPage.getSpecificRecord(0);
		int firstFinalRecordId = Integer.valueOf(finalRecord.get(0));
		
		Assert.assertNotEquals(firstFinalRecordId, firstInitialRecordId, "New Transfer Record ID");
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock in", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock out", initialStock, sourceBranch);
		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock incoming", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock outgoing", initialStock, sourceBranch);
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryAfterTransferCompleted(inStockEvent, quantity, "Received transfer", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryAfterTransferCompleted(outStockEvent, quantity, "Delivered transfer", initialStock, sourceBranch);
	}
	
	@Test
	public void PT_03_TransferBasicProductHavingVariations() {
		
		int productId = 1033401;
		
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		
		ProductInfo productInfo = new ProductInformation(loginInformation).getInfo(productId);
		
		
		String productBarcodeModel = productInfo.getVariationModelList().get(0);
		String product = productInfo.getDefaultProductNameMap().get(storeDefaultLanguage);
		
		Map<String, List<Integer>> apiStock = productInfo.getProductStockQuantityMap();
		
		Map<String, Integer> apiStockByBranch = new HashMap<String, Integer>();
		
		for (int i=0; i<activeBranches.size(); i++) {
			apiStockByBranch.put(activeBranches.get(i), apiStock.get(productBarcodeModel).get(i));
		}
		
		Map<String, String> branch = pickSourceBranchAndDestinationBranch(apiStockByBranch);
		String sourceBranch = branch.get("sourceBranch");
		String destinationBranch = branch.get("destinationBranch");
		
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		
		
		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> initialStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		
		transferPage.navigate();
		
		List<String> initialRecord = transferPage.getSpecificRecord(0);
		int firstInitialRecordId = Integer.valueOf(initialRecord.get(0));
		
		transferPage.clickAddTransferBtn()
		.selectSourceBranch(sourceBranch)
		.selectDestinationBranch(destinationBranch)
		.inputProductSearchTerm(product)
		.selectProduct(product)
		.inputTransferredQuantity(quantity)
		.inputNote(note)
		.clickSaveBtn();
		homePage.getToastMessage();
		
		commonAction.navigateBack();
		commonAction.navigateBack();
		
		List<String> finalRecord = transferPage.getSpecificRecord(0);
		int firstFinalRecordId = Integer.valueOf(finalRecord.get(0));
		
		Assert.assertNotEquals(firstFinalRecordId, firstInitialRecordId, "New Transfer Record ID");
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> finalStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock in", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock out", initialStock, sourceBranch);		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock incoming", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock outgoing", initialStock, sourceBranch);
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryAfterTransferCompleted(inStockEvent, quantity, "Received transfer", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryAfterTransferCompleted(outStockEvent, quantity, "Delivered transfer", initialStock, sourceBranch);	
	}

	@Test
	public void PT_04_TransferProductManagedByIMEIWithoutVariations() {

		int productId = 1243125;
		String product = "Potted Plant";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "My Shop Vietnam";
		String destinationBranch = "CN3";
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);

		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> initialStock = productPage.getStock();
		
		Map<String, List<String>> initialIMEI = productPage.getIMEI();
		List<String> initialIMEISource = initialIMEI.get(sourceBranch.toUpperCase(Locale.getDefault()));
		List<String> transferredIMEI = initialIMEISource.subList(0, quantity);
		
		transferPage.navigate();
		
		List<String> initialRecord = transferPage.getSpecificRecord(0);
		int firstInitialRecordId = Integer.valueOf(initialRecord.get(0));
		
		transferPage.clickAddTransferBtn()
		.selectSourceBranch(sourceBranch)
		.selectDestinationBranch(destinationBranch)
		.inputProductSearchTerm(product)
		.selectProduct(product)
		.inputTransferredQuantity(quantity)
		.inputNote(note)
		.clickSaveBtn();
		homePage.getToastMessage();
		
		transferPage.selectIMEI(transferredIMEI.toArray(new String[0])).clickSaveBtn();
		homePage.getToastMessage();
		
		commonAction.navigateBack();
		commonAction.navigateBack();
		
		List<String> finalRecord = transferPage.getSpecificRecord(0);
		int firstFinalRecordId = Integer.valueOf(finalRecord.get(0));
		
		Assert.assertNotEquals(firstFinalRecordId, firstInitialRecordId, "New Transfer Record ID");
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock in", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock out", initialStock, sourceBranch);		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock incoming", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock outgoing", initialStock, sourceBranch);		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		Map<String, List<String>> finalIMEI = productPage.getIMEI();
		List<String> finalIMEISource = finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault()));
		List<String> finalIMEIDestination = finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault()));
		
		Assert.assertTrue(transferredIMEI.stream().noneMatch(finalIMEISource::contains), "Source branch no longer contains the transferred IMEIs");
		Assert.assertTrue(finalIMEIDestination.containsAll(transferredIMEI), "Destination branch contain the transferred IMEIs");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryAfterTransferCompleted(inStockEvent, quantity, "Received transfer", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryAfterTransferCompleted(outStockEvent, quantity, "Delivered transfer", initialStock, sourceBranch);
	}	
	
	@Test
	public void PT_05_TransferProductManagedByIMEIWithVariations() {
		
//		int productId = 1243383;
//		String productBarcodeModel = "1243383-1232862";
//		String product = "Trousers";
//		int quantity = Math.round(generate.generatNumberInBound(1, 5));
//		String sourceBranch = "My Shop Vietnam";
//		String destinationBranch = "CN3";
//		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		
		
		int productId = 1243383;
		
		int quantity = Math.round(generate.generatNumberInBound(1, 20));
		
		ProductInfo productInfo = new ProductInformation(loginInformation).getInfo(productId);
		
		
		String productBarcodeModel = productInfo.getVariationModelList().get(0);
		String product = productInfo.getDefaultProductNameMap().get(storeDefaultLanguage);
		
		Map<String, List<Integer>> apiStock = productInfo.getProductStockQuantityMap();
		
		Map<String, Integer> apiStockByBranch = new HashMap<String, Integer>();
		
		for (int i=0; i<activeBranches.size(); i++) {
			apiStockByBranch.put(activeBranches.get(i), apiStock.get(productBarcodeModel).get(i));
		}
		
		Map<String, String> branches = pickSourceBranchAndDestinationBranch(apiStockByBranch);
		String sourceBranch = branches.get("sourceBranch");
		String destinationBranch = branches.get("destinationBranch");
		
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		

		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		
		Map<String, List<String>> initialIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		List<String> initialIMEISource = initialIMEI.get(sourceBranch.toUpperCase(Locale.getDefault()));
		List<String> transferredIMEI = initialIMEISource.subList(0, quantity);
		
		Map<String, Integer> initialStock = new HashMap<>();
		for (String branch : initialIMEI.keySet()) {
			  initialStock.put(branch.toUpperCase(Locale.getDefault()), initialIMEI.get(branch.toUpperCase(Locale.getDefault())).size());
		}
		
		transferPage.navigate();
		
		List<String> initialRecord = transferPage.getSpecificRecord(0);
		int firstInitialRecordId = Integer.valueOf(initialRecord.get(0));
		
		transferPage.clickAddTransferBtn()
		.selectSourceBranch(sourceBranch)
		.selectDestinationBranch(destinationBranch)
		.inputProductSearchTerm(product)
		.selectProduct(product)
		.inputTransferredQuantity(quantity)
		.inputNote(note)
		.clickSaveBtn();
		homePage.getToastMessage();
		
		transferPage.selectIMEI(transferredIMEI.toArray(new String[0])).clickSaveBtn();
		homePage.getToastMessage();
		
		commonAction.navigateBack();
		commonAction.navigateBack();
		
		List<String> finalRecord = transferPage.getSpecificRecord(0);
		int firstFinalRecordId = Integer.valueOf(finalRecord.get(0));
		
		Assert.assertNotEquals(firstFinalRecordId, firstInitialRecordId, "New Transfer Record ID");
		
		productPage.navigateToProductDetailById(productId);
		Map<String, List<String>> finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock in", initialStock, destinationBranch.toUpperCase(Locale.getDefault()));
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock out", initialStock, sourceBranch.toUpperCase(Locale.getDefault()));		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock incoming", initialStock, destinationBranch.toUpperCase(Locale.getDefault()));
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock outgoing", initialStock, sourceBranch.toUpperCase(Locale.getDefault()));		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size()+quantity, "Stock of destination branch");
		
		List<String> finalIMEISource = finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault()));
		List<String> finalIMEIDestination = finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault()));
		
		Assert.assertTrue(transferredIMEI.stream().noneMatch(finalIMEISource::contains), "Source branch no longer contains the transferred IMEIs");
		Assert.assertTrue(finalIMEIDestination.containsAll(transferredIMEI), "Destination branch contain the transferred IMEIs");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryAfterTransferCompleted(inStockEvent, quantity, "Received transfer", initialStock, destinationBranch.toUpperCase(Locale.getDefault()));
		verifyStockOutInventoryHistoryAfterTransferCompleted(outStockEvent, quantity, "Delivered transfer", initialStock, sourceBranch.toUpperCase(Locale.getDefault()));		
	}
	
	@Test
	public void PT_06_TransferBasicProductWithConversionUnits() {

		int productId = 1015484;
//		String product = "Cat Food";
		int quantity = Math.round(generate.generatNumberInBound(1, 99));
		
		
		ProductInfo productInfo = new ProductInformation(loginInformation).getInfo(productId);
		
		
//		String productBarcodeModel = productInfo.getVariationModelList().get(0);
		String product = productInfo.getDefaultProductNameMap().get(storeDefaultLanguage);
		
		Map<String, List<Integer>> apiStock = productInfo.getProductStockQuantityMap();
		
		Map<String, Integer> apiStockByBranch = new HashMap<String, Integer>();
		
		for (int i=0; i<activeBranches.size(); i++) {
			apiStockByBranch.put(activeBranches.get(i), apiStock.get(String.valueOf(productId)).get(i));
		}
		
		Map<String, String> branches = pickSourceBranchAndDestinationBranch(apiStockByBranch);
		String sourceBranch = branches.get("sourceBranch");
		String destinationBranch = branches.get("destinationBranch");
		
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		
		
		APIAllProducts apiAllProduct = new APIAllProducts(loginInformation);
		List<String> conversionUnits = apiAllProduct.getConversionUnitsOfProduct(productId);
		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> initialStock = productPage.getStock();
		
		transferPage.navigate();
		
		List<String> initialRecord = transferPage.getSpecificRecord(0);
		int firstInitialRecordId = Integer.valueOf(initialRecord.get(0));
		
		transferPage.clickAddTransferBtn()
		.selectSourceBranch(sourceBranch)
		.selectDestinationBranch(destinationBranch)
		.inputProductSearchTerm(product)
		.selectProduct(product)
		.inputTransferredQuantity(quantity)
		.inputNote(note)
		.clickSaveBtn();
		homePage.getToastMessage();
		
		commonAction.navigateBack();
		commonAction.navigateBack();
		
		List<String> finalRecord = transferPage.getSpecificRecord(0);
		int firstFinalRecordId = Integer.valueOf(finalRecord.get(0));
		
		Assert.assertNotEquals(firstFinalRecordId, firstInitialRecordId, "New Transfer Record ID");
		
		productPage.navigateToProductDetailById(productId);
		Map<String, Integer> finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock in", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock out", initialStock, sourceBranch);
		
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryBeforeTransferCompleted(inStockEvent, quantity, "Transfer stock incoming", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryBeforeTransferCompleted(outStockEvent, quantity, "Transfer stock outgoing", initialStock, sourceBranch);
		
		transferPage.navigate().clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		verifyStockInInventoryHistoryAfterTransferCompleted(inStockEvent, quantity, "Received transfer", initialStock, destinationBranch);
		verifyStockOutInventoryHistoryAfterTransferCompleted(outStockEvent, quantity, "Delivered transfer", initialStock, sourceBranch);
	}	
	
	@Test
	public void PT_07_TransferBasicProductWithConversionUnits() {
		
//		String product = "Flan Caramel";
//		String product = "Áo khoác gió thể thao đa năng cản gió và chống UV | Coolmate";
//		String product = "Áo Sơ Mi Nam Trơn Tay Dài";
		String product = "new ios 4.1 normal variation";
		
		
		transferPage
		.inputProductSearchTerm(product)
		.getSearchResults();

		
	}
	
	
}
