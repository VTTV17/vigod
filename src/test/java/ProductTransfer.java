import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.ProductInformation;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.products.inventory.Inventory;
import pages.dashboard.products.inventory.InventoryHistory;
import pages.dashboard.products.transfer.Transfer;
import utilities.UICommonAction;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
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
	LoginInformation loginInformation;

	@BeforeClass
	public void loadTestData() {
		username = "0800111222";
		password = "123456a@A";
		country = AccountTest.ADMIN_COUNTRY_TIEN;
        loginInformation = new Login().setLoginInformation(new DataGenerator().getPhoneCode(country), username, password).getLoginInformation();
//        customerList = new Customers(loginInformation).getAllCustomerNames();
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
    

	@Test
	public void PT_02_TransferBasicProductWithoutVariations() {

		int productId = 1218606;
		String product = "Tien's Baseball Jacket";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "Kho tổng";
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
		
		int inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		int remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock in", "In-Stock Status");
		
		int outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		int remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock out", "Out-Stock Status");
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock incoming", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock outgoing", "Out-Stock Status");
		
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch)+quantity, "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Received transfer", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Delivered transfer", "Out-Stock Status");
		
	}
	
	@Test
	public void PT_03_TransferBasicProductHavingVariations() {
		
		int productId = 1218683;
		String productBarcodeModel = "1218683-1210016";
		String product = "Tien's Hat";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "Kho tổng";
		String destinationBranch = "CN3";
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		
		
//		ProductInfo productInfo = new ProductInformation(loginInformation).getInfo(productId);
		
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
		
		int inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		int remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock in", "In-Stock Status");
		
		int outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		int remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock out", "Out-Stock Status");
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock incoming", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock outgoing", "Out-Stock Status");
		
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStockOfProductHavingVariations(productBarcodeModel);
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch)+quantity, "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch)+quantity, "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Received transfer", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Delivered transfer", "Out-Stock Status");
	}

	@Test
	public void PT_04_TransferProductManagedByIMEIWithoutVariations() {

		int productId = 1218881;
		String product = "Tien's Trousers";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "CN3";
		String destinationBranch = "Kho tổng";
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
		
		int inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		int remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock in", "In-Stock Status");
		
		int outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		int remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock out", "Out-Stock Status");
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalStock = productPage.getStock();
		Assert.assertEquals(finalStock.get(sourceBranch), initialStock.get(sourceBranch)-quantity, "Stock of source branch");
		Assert.assertEquals(finalStock.get(destinationBranch), initialStock.get(destinationBranch), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock incoming", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock outgoing", "Out-Stock Status");
		
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		
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
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialStock.get(destinationBranch)+quantity, "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Received transfer", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialStock.get(sourceBranch)-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Delivered transfer", "Out-Stock Status");
		
	}	
	
	@Test
	public void PT_05_TransferProductManagedByIMEIWithVariations() {
		
		int productId = 1219134;
		String productBarcodeModel = "1219134-1210588";
		String product = "Tien's T-Shirt";
		int quantity = Math.round(generate.generatNumberInBound(1, 5));
		String sourceBranch = "Kho tổng";
		String destinationBranch = "CN3";
		String note = "Transferring %s to branch %s".formatted(product, destinationBranch);
		
		
		loginDashboard();
		
		productPage.navigateToProductDetailById(productId);
		
		Map<String, List<String>> initialIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
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
		Map<String, List<String>> finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch).size(), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		List<String> inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		List<String> outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		int inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		int remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialIMEI.get(destinationBranch).size(), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock in", "In-Stock Status");
		
		int outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		int remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialIMEISource.size()-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock out", "Out-Stock Status");
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickShipGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch).size(), "Stock of destination branch");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialIMEI.get(destinationBranch).size(), "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Transfer stock incoming", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialIMEISource.size()-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Transfer stock outgoing", "Out-Stock Status");
		
		
		
		transferPage.navigate();
		
		transferPage.clickRecord(firstFinalRecordId).clickReceiveGoodsBtn();
		
		homePage.getToastMessage();
		
		productPage.navigateToProductDetailById(productId);
		finalIMEI = productPage.getIMEIOfProductHavingVariations(productBarcodeModel);
		
		Assert.assertEquals(finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault())).size(), initialIMEISource.size()-quantity, "Stock of source branch");
		Assert.assertEquals(finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault())).size(), initialIMEI.get(destinationBranch).size()+quantity, "Stock of destination branch");
		
		List<String> finalIMEISource = finalIMEI.get(sourceBranch.toUpperCase(Locale.getDefault()));
		List<String> finalIMEIDestination = finalIMEI.get(destinationBranch.toUpperCase(Locale.getDefault()));
		
		Assert.assertTrue(transferredIMEI.stream().noneMatch(finalIMEISource::contains), "Source branch no longer contains the transferred IMEIs");
		Assert.assertTrue(finalIMEIDestination.containsAll(transferredIMEI), "Destination branch contain the transferred IMEIs");
		
		inventoryPage.navigate().clickInventoryHistory().inputSearchTerm(product);
		inStockEvent = inventoryHistoryPage.getSpecificRecord(0);
		outStockEvent = inventoryHistoryPage.getSpecificRecord(1);
		
		inStockChangeQuantity = Integer.valueOf(inStockEvent.get(1));
		Assert.assertEquals(inStockChangeQuantity, quantity, "In-Stock Change Quantity");
		remainingInStockQuantity = Integer.valueOf(inStockEvent.get(2));
		Assert.assertEquals(remainingInStockQuantity, initialIMEI.get(destinationBranch).size()+quantity, "Remaining In-Stock Quantity");
		Assert.assertEquals(inStockEvent.get(3), "Received transfer", "In-Stock Status");
		
		outStockChangeQuantity = Integer.valueOf(outStockEvent.get(1));
		Assert.assertEquals(String.valueOf(outStockChangeQuantity), "-" + quantity, "Out-Stock Change Quantity");
		remainingOutStockQuantity = Integer.valueOf(outStockEvent.get(2));
		Assert.assertEquals(remainingOutStockQuantity, initialIMEISource.size()-quantity, "Remaining Out-Stock Quantity");
		Assert.assertEquals(outStockEvent.get(3), "Delivered transfer", "Out-Stock Status");
	}
	
}
