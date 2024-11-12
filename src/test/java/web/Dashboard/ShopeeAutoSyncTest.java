package web.Dashboard;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
	
	static final int BULK_PRODUCT_COUNT = 10;
	static final int DELAY_BEFORE_FETCHING_DATA = 3000;
	
	//TODO retrieve shopeeShopId from API
//	static String shopeeShopId ="42524401"; //cTrang's
//	static String shopeeShopId ="736002841"; //Chico's
	static String shopeeShopId ="751369538"; //Uyen's
	
	LoginInformation credentials;
	BranchInfo branchInfo;

	Connection dbConnection;

	@BeforeClass
	void onetimeLoadedData() throws SQLException {
//		credentials = new Login().setLoginInformation("tienvan-staging-vn@mailnesia.com", "fortesting!1").getLoginInformation();
//		credentials = new Login().setLoginInformation("trangthuy9662@gmail.com", "Password9@").getLoginInformation();
		credentials = new Login().setLoginInformation("uyen.lai@mediastep.com", "Abc@12345").getLoginInformation();
		branchInfo = new BranchManagement(credentials).getInfo();

		dbConnection = new InitConnection().createConnection();
		
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
	}

	@AfterClass(alwaysRun = true)
	void closeDBConnection() throws SQLException {
		if (dbConnection != null) dbConnection.close();
	}

	/**
	 * Filters the list of Shopee products based on the specified conditions and returns a limited number of products.
	 * @param hasVariation a list of Boolean values indicating whether the product has variations.
	 * @param status a list of status strings to filter the products by their GoSell status.
	 * @param outputProductCount the maximum number of products to return after filtering and shuffling.
	 * @return a list of Shopee products that match the specified conditions, limited to the specified number of products.
	 */	
	List<ShopeeProduct> filterProductsByCondition(List<Boolean> hasVariation, List<String> status, int outputProductCount) {
		return new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (hasVariation.contains(e.getHasVariation()) && status.contains(e.getGosellStatus())))
		        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
		            Collections.shuffle(collected);
		            return collected.stream();
		        }))
		        .limit(outputProductCount)
		        .collect(Collectors.toList());
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

	List<InventoryEvent> waitUntilEventRecordsAppear(String sqlQuery){
		
		final int maxAttempts = 5;
		
		List<InventoryEvent> inventoryEventList = new ArrayList<>();
		for (int i=0; i<maxAttempts; i++) {
			inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
			
			if (!inventoryEventList.isEmpty()) return inventoryEventList;
			
			UICommonAction.sleepInMiliSecond(DELAY_BEFORE_FETCHING_DATA, "Events are not present. Wait a little");
		}
		
		return inventoryEventList;
	}
	
	List<InventoryMapping> waitUntilMappingRecordsAppear(String sqlQuery){
		
		final int maxAttempts = 5;
		
		List<InventoryMapping> mappingRecordList = new ArrayList<>();
		
		for (int i=0; i<maxAttempts; i++) {
			mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
			
			if (!mappingRecordList.isEmpty()) return mappingRecordList;
			
			UICommonAction.sleepInMiliSecond(DELAY_BEFORE_FETCHING_DATA, "Mapping records are not present. Wait a little");
		}
		
		return mappingRecordList;
	}
	
	void verifyEvent(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = waitUntilEventRecordsAppear(sqlQuery);
	            
	            inventoryEventList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(inventoryEventList.size(), 1, "Event record count");
	            Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	            Assert.assertEquals(inventoryEventList.get(0).getModel_id(), String.valueOf(var.getBcModelId()));
	            Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
	        });
	    } else {
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEventList = waitUntilEventRecordsAppear(sqlQuery);
	        
	        inventoryEventList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(inventoryEventList.size(), 1, "Event record count");
	        Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	        Assert.assertEquals(inventoryEventList.get(0).getModel_id(), null);
	        Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
	    }
	}

	void verifyEventRemoved(String currentTimestamp, ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	            
	            inventoryEventList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(inventoryEventList.size(), 0, "Event record count");
	        });
	    } else {
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	        
	        inventoryEventList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(inventoryEventList.size(), 0, "Event record count");
	    }
	}
	
	void verifyMapping(ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	            
	            List<InventoryMapping> mappingRecordList = waitUntilMappingRecordsAppear(sqlQuery);
	            
	            mappingRecordList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(mappingRecordList.size(), 2, "Mapping record count");
	        });
	    } else {
	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	        
	        List<InventoryMapping> mappingRecordList = waitUntilMappingRecordsAppear(sqlQuery);
	        
	        mappingRecordList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(mappingRecordList.size(), 2, "Mapping record count");
	    }
	}

	void verifyMappingRemoved(ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	            
	            List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	            
	            mappingRecordList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(mappingRecordList.size(), 0, "Mapping record count");
	        });
	    } else {
	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	        
	        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	        
	        mappingRecordList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(mappingRecordList.size(), 0, "Mapping record count");
	    }
	}
	
	@Test(description = "Import products with no variations to GoSELL")
	public void TC_ImportProductToGosell_NoVar() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIdList);
        
		//Get Shopee products post action
		var shopeeProductListPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
        
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductListPostAction.size(), selectedShopeeProductList.size(), "Shopee product count post-action");
		shopeeProductListPostAction.stream().forEach(product -> {
			Assert.assertEquals(product.getGosellStatus(), "SYNC", "Sync status post-action");
			Assert.assertNotNull(product.getBcItemId(), "Shopee product's counterpart in GoSELL post-action");
		});
		
        //Check database for inventory events
		shopeeProductListPostAction.stream().forEach(product -> verifyEvent(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductListPostAction.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}
	
	@Test(description = "Import products with variations to GoSELL")
	public void TC_ImportProductToGosell_VarAvailable() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIdList);
        
        //Get Shopee products post action
		var shopeeProductListPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductListPostAction.size(), selectedShopeeProductList.size(), "Shopee product count post-action");
		shopeeProductListPostAction.stream().forEach(product -> {
			Assert.assertEquals(product.getGosellStatus(), "SYNC", "Sync status post-action");
			Assert.assertNotNull(product.getBcItemId(), "Shopee product's counterpart in GoSELL post-action");
		});	
		
		//Check database for inventory events
		shopeeProductListPostAction.stream().forEach(product -> verifyEvent(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductListPostAction.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}

	@Test(description = "Link a Shopee product with no variations to a GoSELL product")
	public void TC_LinkProduct_NoVar() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation
		new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		
        //Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEvent(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Link a Shopee product with variations to a GoSELL product")
	public void TC_LinkProduct_VarAvailable() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		List<List<String>> mappedVariationIds = new ArrayList<>();
		if (shopeeVariationList.size() > 12) {
			mappedVariationIds = new APIShopeeProducts(credentials).linkProductHavingVariations(selectedShopeeProduct, gosellProductDetail);
		} else {
			var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
			mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellProductDetail.getModels(), mappedVariations);
		}
		
		//Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
	    List<List<String>> mappedVariationIdsPostAction = shopeeProductPostAction.getVariations().stream()
	            .map(product -> List.of(product.getShopeeVariationId(), String.valueOf(product.getBcModelId())))
	            .collect(Collectors.toList());
		
	    //Preliminary check for correct mapping process
	    Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
	    Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		Assert.assertEquals(mappedVariationIdsPostAction, mappedVariationIds, "Mapped variation ids post-action");
		
		//Check database for inventory events
		verifyEvent(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Unlink a LINKED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_NoVar() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductNoVariations(selectedShopeeProduct, gosellProductId);
		
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is linked with the GoSELL product
		Assert.assertEquals(shopeeProductPostLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostLink);
		
		//UI implementation
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductPostLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductPostUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductPostUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingRemoved(shopeeProductPostLink);
		
	}
	
	@Test(description = "Unlink a LINKED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_VarAvailable() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		var mappedVariationIds = new APIShopeeProducts(credentials).linkProductHavingVariations(selectedShopeeProduct, gosellProductDetail);
		
		//Get Shopee products after the products are linked
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is linked with the GoSELL product
		List<List<String>> mappedVariationIdsPostLink = shopeeProductPostLink.getVariations().stream()
				.map(product -> {
					List<String> mappedVarIds = new ArrayList<>();
					mappedVarIds.add(product.getShopeeVariationId());
					mappedVarIds.add(String.valueOf(product.getBcModelId()));
					return mappedVarIds;
				}).collect(Collectors.toList());
		Assert.assertEquals(shopeeProductPostLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		Assert.assertEquals(mappedVariationIdsPostLink, mappedVariationIds, "Mapping is correct");
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostLink);
		
		
		//UI implementation
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductPostLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingRemoved(shopeeProductPostLink);
	}

	@Test(description = "Unlink a SYNCED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		var shopeeProductRecordIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getId).collect(Collectors.toList());
		
		//Import Shopee products to GoSELL
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		//Get Shopee products post import
		var shopeeProductsPostImport = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
        
		shopeeProductsPostImport.stream().forEach(product -> {
			Assert.assertEquals(product.getGosellStatus(), "SYNC", "Sync status post-action");
			//TODO check whether Shopee product is mapped correctly with the GoSELL product
		});
		
        //Check database for inventory events
		shopeeProductsPostImport.stream().forEach(product -> verifyEvent(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostImport.stream().forEach(product -> verifyMapping(product));
		
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIdList);
		
		//Get Shopee products post unlink process
		var shopeeProductsPostUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsPostUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsPostUnLink.stream().forEach(product -> verifyMappingRemoved(product));
	}	
	
	@Test(description = "Unlink a SYNCED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_VarAvailable() {
		
		//Get Shopee products that match a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		var shopeeProductRecordIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getId).collect(Collectors.toList());
		
		//Import Shopee products to GoSELL
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		//Get Shopee products post import
		var shopeeProductsPostImport = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsPostImport.stream().forEach(product -> verifyEvent(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostImport.stream().forEach(product -> verifyMapping(product));
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIdList);
		
		//Get Shopee products post unlink process
		var shopeeProductsPostUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsPostUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsPostUnLink.stream().forEach(product -> verifyMappingRemoved(product));
	}	

	@Test(description = "Download a specific Shopee product with no variations to GoSELL system")
	public void TC_DownloadSpecificProduct_NoVar() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductNoVariations(selectedShopeeProduct, gosellProductId);		
		
		
		/** TC starts here **/
		//Get a random Shopee product that matches a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("LINK", "SYNC"), BULK_PRODUCT_COUNT));
		
		//UI implementation
        var productPage = new ProductsPage(driver).navigateByURL();
        
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        productPage.downloadSpecificProduct(shopeeProductPreAction.getShopeeItemId());
        
		//Get Shopee products post download
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProductPreAction.getShopeeItemId()))
				.findFirst().orElse(null);
        
        //Check database for inventory events
		verifyEvent(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Download a specific Shopee product with variations to GoSELL system")
	public void TC_DownloadSpecificProduct_VarAvailable() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(selectedShopeeProduct.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductHavingVariations(selectedShopeeProduct, gosellProductDetail);
		
		
		/** TC starts here **/
		//Get a random Shopee product that matches a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("LINK", "SYNC"), BULK_PRODUCT_COUNT));
		
		//UI implementation
        var productPage = new ProductsPage(driver).navigateByURL();
        
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        productPage.downloadSpecificProduct(shopeeProductPreAction.getShopeeItemId());
		
		//Get Shopee products after the products are linked
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProductPreAction.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check database for inventory events
		verifyEvent(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Download all Shopee products to GoSELL system")
	public void TC_DownloadAllProduct() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		//TODO handle the case where the products are unlinked
		//TODO handle the case where the products don't have variations
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeProductPreAction.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductHavingVariations(shopeeProductPreAction, gosellProductDetail);
		
		
		/** TC starts here **/
		var shopeeProductListPreAction = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK", "SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIds = shopeeProductListPreAction.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		//UI implementation
		var accountInfoPage = new AccountInformationPage(driver).navigateToShopeeAccountInformationPage();
		
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		accountInfoPage.clickDownloadButton();
		
		//Get Shopee products post download
		var shopeeProductsPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsPostAction.stream().forEach(product -> verifyEvent(currentTimestamp, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostAction.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}	

	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	public void TC_UpdateLinkedProduct_NoVar() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductNoVariations(shopeeProductPreAction, gosellProductId);

		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductListPreAction = filterProductsByCondition(List.of(Boolean.FALSE), List.of("LINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductListPreAction.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
        
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductListPreAction);
        
		var shopeeProductListPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
        
        //Check database for inventory events
		shopeeProductListPostDownload.stream().forEach(product -> verifyEvent(preProcessTime, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductListPostDownload.stream().forEach(product -> verifyMapping(product));
		
		
		//UI implementation
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
        //Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEvent(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMapping(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}	
	
	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	public void TC_UpdateLinkedProduct_VarAvailable() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(selectedShopeeProduct.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductHavingVariations(selectedShopeeProduct, gosellProductDetail);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("LINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		//UI implementation
        
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEvent(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMapping(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_NoVar() {
		
		/** Precondition **/
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductRecordIdList = shopeeProductTemp.stream().map(ShopeeProduct::getId).collect(Collectors.toList());
		
		//Import Shopee products to GoSELL
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.FALSE), List.of("SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		//UI implementation
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEventRemoved(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMapping(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}	
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_VarAvailable() {
		
		/** Precondition **/
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductRecordIds = shopeeProductTemp.stream().map(ShopeeProduct::getId).collect(Collectors.toList());
		
		//Import Shopee products to GoSELL
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIds);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).collect(Collectors.toList());
		
		shopeeProductList.stream().forEach(product -> verifyMapping(product));
		
		//UI implementation
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEvent(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMapping(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEventRemoved(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMapping(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}	
	
}
