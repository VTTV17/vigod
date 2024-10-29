package web.Dashboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIGetProductDetail;
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation;
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation.Model;
import api.Seller.sale_channel.shopee.APIShopeeProducts;
import api.Seller.setting.BranchManagement;
import lombok.SneakyThrows;
import sql.SQLGetInventoryEvent;
import sql.SQLGetInventoryEvent.InventoryEvent;
import sql.SQLGetInventoryMapping;
import sql.SQLGetInventoryMapping.InventoryMapping;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.salechanel.shopee.ShopeeProduct;
import utilities.model.dashboard.salechanel.shopee.Variation;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.sales_channels.shopee.link_products.LinkProductsPage;
import web.Dashboard.sales_channels.shopee.products.ProductsPage;

public class ShopeeAutoSyncTest extends BaseTest {

	LoginInformation credentials;
	BranchInfo branchInfo;

	Connection dbConnection;

	@BeforeClass
	void onetimeLoadedData() {
//		credentials = new Login().setLoginInformation("uyen.lai@mediastep.com", "Abc@12345").getLoginInformation();
		credentials = new Login().setLoginInformation("Bonguyen11397@gmail.com", "Abc@12345").getLoginInformation();
		branchInfo = new BranchManagement(credentials).getInfo();

		try {
			dbConnection = new InitConnection().createConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@AfterClass(alwaysRun = true)
	void closeDBConnection() {
		if (dbConnection == null) return;

		try {
			dbConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//TODO add more description for this function
	/**
	 * Convert mapped variation names to variation Ids
	 * @param shopeeVariationList
	 * @param gosellVariationList
	 * @param mappedVariationNames
	 * @return
	 */
	List<List<String>> lookupMappedVariationIds(List<Variation> shopeeVariationList, List<Model> gosellVariationList, List<List<String>> mappedVariationNames) {
		List<List<String>> mappedVariationIds = new ArrayList<>();
		
		Assert.assertEquals(shopeeVariationList.size(), gosellVariationList.size(), "Size of Shopee vars and GoSELL vars");
		
		for (int i = 0; i<gosellVariationList.size(); i++) {
			
			var shopeeVarNam = shopeeVariationList.get(i).getName();
			var shopeeVariationId = shopeeVariationList.stream().filter(var -> var.getName().contentEquals(shopeeVarNam)).findFirst().orElse(null).getShopeeVariationId();
			
			var gosellVarNam = gosellVariationList.get(i).getOrgName();
			var gosellVariationId = gosellVariationList.stream().filter(var -> var.getOrgName().contentEquals(gosellVarNam)).findFirst().orElse(null).getId();
			
			mappedVariationIds.add(List.of(shopeeVariationId, String.valueOf(gosellVariationId)));
		}
		
		return mappedVariationIds;
	}	
	
	void verifyEventNoVariations(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
		
		var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
		System.out.println(sqlQuery);
		
		List<InventoryEvent> inventoryEvents = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
		
		Assert.assertTrue(inventoryEvents.size() == 1, "Event records appear");
		Assert.assertEquals(inventoryEvents.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
		Assert.assertEquals(inventoryEvents.get(0).getModel_id(), null);
		Assert.assertEquals(inventoryEvents.get(0).getAction(), expectedEvent);
	}
	void verifyEventVariations(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
		
		product.getVariations().stream().forEach(var -> {
			
			var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
			
			List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
			
			Assert.assertTrue(inventoryEventList.size() == 1, "Event records appear");
			Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
			Assert.assertEquals(inventoryEventList.get(0).getModel_id(), String.valueOf(var.getBcModelId()));
			Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
		});

	}

	void verifyMappingNoVariations(String currentTimestamp, ShopeeProduct product) {
		
		var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
		
		var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' and updated_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId, currentTimestamp);
		
        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
		
        Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
	}
	void verifyMappingVariations(String currentTimestamp, ShopeeProduct product) {
		
		product.getVariations().stream().forEach(var -> {
			var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
			var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' and updated_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId, currentTimestamp);
			
			List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
			
			Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
		});
	}
	
	@Test(description = "Import products with no variations to GoSELL")
	public void TC_ImportProductToGosell_NoVar() {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		var shopeeShopId =  "751369538";
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(20).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
        
		//Get Shopee products after the products are linked
		var shopeeProductsAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
        
        //Check database for inventory events
		shopeeProductsAfterLink.stream().forEach(product -> verifyEventNoVariations(currentTimestamp, product, expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingNoVariations(currentTimestamp, product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	
	@Test(description = "Import products with variations to GoSELL")
	public void TC_ImportProductToGosell_VarAvailable() {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		var shopeeShopId =  "751369538";
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(20).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
		
		//Get Shopee products after the products are linked
		var shopeeProductsAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		
		UICommonAction.sleepInMiliSecond(3000, "Wait until all records generated in database");
		
		//Check database for inventory events
		shopeeProductsAfterLink.stream().forEach(product -> verifyEventVariations(currentTimestamp, product, expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingVariations(currentTimestamp, product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}

	@Test(description = "Link a Shopee product with no variations to a GoSELL product")
	public void TC_LinkProductToGosell_NoVar() {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		//Get a random Shopee product
		var shopeeProduct = DataGenerator.getRandomListElement(new APIShopeeProducts(credentials).getProducts().stream().filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK"))).collect(Collectors.toList()));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(shopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		
		//Get Shopee products after the products are linked
		var shopeeProductAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		UICommonAction.sleepInMiliSecond(3000, "Wait until all records generated in database");
		
		Assert.assertEquals(shopeeProductAfterLink.getBcItemId(), Integer.valueOf(gosellProductId), "Mapping process is correct");
		
		//Check database for inventory events
		verifyEventNoVariations(currentTimestamp, shopeeProductAfterLink, expectedEvent);
		
		//Check database for mapping records
		verifyMappingNoVariations(currentTimestamp, shopeeProductAfterLink);
		
		//TODO unlink Shopee-GoSELL product
		
	}
	
	@Test(description = "Link a Shopee product with variations to a GoSELL product")
	public void TC_LinkProductToGosell_VarAvailable() {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		//Get a random Shopee product
		var shopeeProduct = DataGenerator.getRandomListElement(new APIShopeeProducts(credentials).getProducts().stream().filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK"))).collect(Collectors.toList()));
		var shopeeVariationList = shopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		var gosellVariationList = gosellProductDetail.getModels();
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(shopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		var mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellVariationList, mappedVariations);
		
		//Get Shopee products after the products are linked
		var shopeeProductAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		List<List<String>> mappedVariationIdsAfterLink = shopeeProductAfterLink.getVariations().stream()
				.map(product -> {
					List<String> mappedVarIds = new ArrayList<>();
					mappedVarIds.add(product.getShopeeVariationId());
					mappedVarIds.add(String.valueOf(product.getBcModelId()));
					return mappedVarIds;
				}).collect(Collectors.toList());
		
		Assert.assertEquals(mappedVariationIdsAfterLink, mappedVariationIds, "Mapping is correct");
		
		UICommonAction.sleepInMiliSecond(3000, "Wait until all records generated in database");
		
		//Check database for inventory events
		verifyEventVariations(currentTimestamp, shopeeProductAfterLink, expectedEvent);
		
		//Check database for mapping records
		verifyMappingVariations(currentTimestamp, shopeeProductAfterLink);		
		
		//TODO unlink Shopee-GoSELL product
	}

    @AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}
}
