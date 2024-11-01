package web.Dashboard;

import java.sql.Connection;
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
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation.Model;
import api.Seller.sale_channel.shopee.APIShopeeProducts;
import api.Seller.setting.BranchManagement;
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
import utilities.enums.EventAction;
import utilities.model.dashboard.salechanel.shopee.ShopeeProduct;
import utilities.model.dashboard.salechanel.shopee.Variation;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.sales_channels.shopee.account_information.AccountInformationPage;
import web.Dashboard.sales_channels.shopee.link_products.LinkProductsPage;
import web.Dashboard.sales_channels.shopee.products.ProductsPage;

public class ShopeeAutoSyncTest extends BaseTest {
	
	static final int BULK_PRODUCT_COUNT = 2;
	
	LoginInformation credentials;
	BranchInfo branchInfo;

	Connection dbConnection;

	@BeforeClass
	void onetimeLoadedData() {
		credentials = new Login().setLoginInformation("uyen.lai@mediastep.com", "Abc@12345").getLoginInformation();
//		credentials = new Login().setLoginInformation("Bonguyen11397@gmail.com", "Abc@12345").getLoginInformation();
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

	List<ShopeeProduct> getShopeeProductListByCondition(Boolean hasVariation, String status) {
		return new APIShopeeProducts(credentials).getProducts().stream().filter(e -> (e.getHasVariation().equals(hasVariation) && e.getGosellStatus().contentEquals(status))).collect(Collectors.toList());
	}
	List<ShopeeProduct> getShopeeProductListByCondition(Boolean hasVariation, List<String> status) {
		return new APIShopeeProducts(credentials).getProducts().stream().filter(e -> (e.getHasVariation().equals(hasVariation) && status.contains(e.getGosellStatus()))).collect(Collectors.toList());
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
	void verifyEvent(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
		if (product.getHasVariation()) {
			verifyEventVariations(currentTimestamp, product, expectedEvent);
		} else {
			verifyEventNoVariations(currentTimestamp, product, expectedEvent);
		}
	}

	void verifyEventNoVariationsRemoved(String currentTimestamp, ShopeeProduct product) {
		
		var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
		
		List<InventoryEvent> inventoryEvents = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
		
		Assert.assertTrue(inventoryEvents.size() == 0, "No event records");
	}	
	void verifyEventVariationsRemoved(String currentTimestamp, ShopeeProduct product) {
		
		product.getVariations().stream().forEach(var -> {
			
			var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
			
			List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
			
			Assert.assertTrue(inventoryEventList.size() == 0, "No event records");
		});
	}
	void verifyEventRemoved(String currentTimestamp, ShopeeProduct product) {
		if (product.getHasVariation()) {
			verifyEventVariationsRemoved(currentTimestamp, product);
		} else {
			verifyEventNoVariationsRemoved(currentTimestamp, product);
		}
	}
	
	void verifyMappingNoVariations(ShopeeProduct product) {
		
		var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
		
		var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
		
        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
		
        Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
	}
	void verifyMappingVariations(ShopeeProduct product) {
		
		product.getVariations().stream().forEach(var -> {
			var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
			var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
			
			List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
			
			Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
		});
	}
	void verifyMapping(ShopeeProduct product) {
		if (product.getHasVariation()) {
			verifyMappingVariations(product);
		} else {
			verifyMappingNoVariations(product);
		}
	}
	
	void verifyMappingNoVariationsRemoved(ShopeeProduct product) {
		
		var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
		
		var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
		
		List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
		
		Assert.assertTrue(mappingRecordList.size() == 0, "Mapping records removed");
	}
	void verifyMappingVariationsRemoved(ShopeeProduct product) {
		
		product.getVariations().stream().forEach(var -> {
			var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
			var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
			
			List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
			
			Assert.assertTrue(mappingRecordList.size() == 0, "Mapping records removed");
		});
	}

	
	@Test(description = "Import products with no variations to GoSELL")
	public void TC_ImportProductToGosell_NoVar() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		//TODO retrieve shopeeShopId from API
		var shopeeShopId = "751369538";
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
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
		shopeeProductsAfterLink.stream().forEach(product -> verifyEvent(currentTimestamp, product, expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	
	@Test(description = "Import products with variations to GoSELL")
	public void TC_ImportProductToGosell_VarAvailable() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		//TODO retrieve shopeeShopId from API
		var shopeeShopId =  "751369538";
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
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
		shopeeProductsAfterLink.stream().forEach(product -> verifyEvent(currentTimestamp, product, expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}

	@Test(description = "Link a Shopee product with no variations to a GoSELL product")
	public void TC_LinkProduct_NoVar() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
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
		verifyEvent(currentTimestamp, shopeeProductAfterLink, expectedEvent);
		
		//Check database for mapping records
		verifyMapping(shopeeProductAfterLink);
		
		//TODO unlink Shopee-GoSELL product
		
	}
	
	@Test(description = "Link a Shopee product with variations to a GoSELL product")
	public void TC_LinkProduct_VarAvailable() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		//Get a random Shopee product
		var shopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
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
		verifyEvent(currentTimestamp, shopeeProductAfterLink, expectedEvent);
		
		//Check database for mapping records
		verifyMapping(shopeeProductAfterLink);		
		
		//TODO unlink Shopee-GoSELL product
	}
	
	@Test(description = "Unlink a LINKED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_NoVar() {
		
		var unlinkedShopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, "UNLINK"));
		
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		
		new APIShopeeProducts(credentials).linkProductNoVariations(unlinkedShopeeProduct, String.valueOf(gosellProductId));
		
		var shopeeProductAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(unlinkedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		verifyMappingNoVariations(shopeeProductAfterLink);
		
		//UI implementation of unlink Shopee products
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductAfterLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductAfterUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(unlinkedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductAfterUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingNoVariationsRemoved(shopeeProductAfterLink);
		
	}
	@Test(description = "Unlink a LINKED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_VarAvailable() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		var unlinkedShopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		var shopeeVariationList = unlinkedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		var gosellVariationList = gosellProductDetail.getModels();
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(unlinkedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		var mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellVariationList, mappedVariations);
		
		//Get Shopee products after the products are linked
		var shopeeProductAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(unlinkedShopeeProduct.getShopeeItemId()))
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
		verifyMappingVariations(shopeeProductAfterLink);	
		
		
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductAfterLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductAfterUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(unlinkedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductAfterUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingVariationsRemoved(shopeeProductAfterLink);
	}

	@Test(description = "Unlink a SYNCED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_NoVar() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
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
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingNoVariations(product));

		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIds);
		
		//Get Shopee products post unlink process
		var shopeeProductsAfterUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsAfterUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingNoVariationsRemoved(product));
	}	
	
	@Test(description = "Unlink a SYNCED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_VarAvailable() {
		
		String expectedEvent = EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name();
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
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
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingVariations(product));
		
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIds);
		
		//Get Shopee products post unlink process
		var shopeeProductsAfterUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsAfterUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMappingVariationsRemoved(product));
	}	

	@Test(description = "Download a specific Shopee product with no variations to GoSELL system")
	public void TC_DownloadSpecificProduct_NoVar() {
		
		String expectedEvent = EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name();
		
		//Get a random Shopee product that matches a specific condition
		var shopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, List.of("LINK", "SYNC")));
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
        new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProduct.getShopeeItemId());
        
		//Get Shopee products after the products are linked
		var shopeeProductAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
        
        //Check database for inventory events
		verifyEvent(currentTimestamp, shopeeProductAfterDownload, expectedEvent);
		
		//Check database for mapping records
		verifyMapping(shopeeProductAfterDownload);
		
		//TODO decide whether to unlink the products
	}
	
	@Test(description = "Download a specific Shopee product with variations to GoSELL system")
	public void TC_DownloadSpecificProduct_VarAvailable() {
		
		String expectedEvent = EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name();
		
		//Get a random Shopee product that matches a specific condition
		var shopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, List.of("LINK", "SYNC")));
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProduct.getShopeeItemId());
		
		//Get Shopee products after the products are linked
		var shopeeProductAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		UICommonAction.sleepInMiliSecond(3000, "Wait until all records generated in database");
		
		//Check database for inventory events
		verifyEvent(currentTimestamp, shopeeProductAfterDownload, expectedEvent);
		
		//Check database for mapping records
		verifyMapping(shopeeProductAfterDownload);
		
		//TODO decide whether to unlink the products
	}
	
	@Test(description = "Download all Shopee products to GoSELL system")
	public void TC_DownloadAllProduct() {
		
		String expectedEvent = EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name();
		
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> List.of("LINK", "SYNC").contains(e.getGosellStatus()))
				.collect(Collectors.toList());
		
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new AccountInformationPage(driver).navigateToShopeeAccountInformationPage().clickDownloadButton();
		
		//Get Shopee products after the products are linked
		var shopeeProductsAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsAfterLink.stream().forEach(product -> verifyEvent(currentTimestamp, product, expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(product -> verifyMapping(product));
		
		//TODO decide whether to unlink the products
	}	

	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	public void TC_UpdateLinkedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && List.of("LINK").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
        new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
        
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
        
        //Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIds);
		
        //Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));		
		
		//TODO decide whether to unlink the products
	}	
	
	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	public void TC_UpdateLinkedProduct_VarAvailable() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && List.of("LINK").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIds);
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));		
		
		//TODO check product' status is changed to SYNC
		
		//TODO decide whether to unlink the products
	}
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && List.of("SYNC").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIds);
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEventRemoved(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));		
		
		//TODO check product' status is changed to SYNC
		
		//TODO decide whether to unlink the products
	}	
	
	@Test(description = "Incorporate a synceds product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_VarAvailable() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && List.of("SYNC").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId().toString()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId().toString()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIds);
		
		//Check database for inventory events
		shopeeProductsAfterDownload.stream().forEach(product -> verifyEventRemoved(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsAfterDownload.stream().forEach(product -> verifyMapping(product));		
		
		//TODO check product' status is changed to SYNC
		
		//TODO decide whether to unlink the products
	}	
	
    @AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}
}
