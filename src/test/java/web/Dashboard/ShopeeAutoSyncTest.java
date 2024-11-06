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
	
	static final int BULK_PRODUCT_COUNT = 20;
	
	//TODO retrieve shopeeShopId from API
//	static String shopeeShopId ="736002841"; //me
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
//		credentials = new Login().setLoginInformation("Bonguyen11397@gmail.com", "Abc@12345").getLoginInformation();
		branchInfo = new BranchManagement(credentials).getInfo();

		dbConnection = new InitConnection().createConnection();
		
		driver = new InitWebdriver().getDriver(browser, headless);
		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
	}

	@AfterClass(alwaysRun = true)
	void closeDBConnection() throws SQLException {
		if (dbConnection == null) return;

		dbConnection.close();
	}

	List<ShopeeProduct> getShopeeProductListByCondition(Boolean hasVariation, String status) {
		return new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(hasVariation) && e.getGosellStatus().contentEquals(status)))
				.collect(Collectors.toList());
	}
	List<ShopeeProduct> getShopeeProductListByCondition(Boolean hasVariation, List<String> status) {
		return new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(hasVariation) && status.contains(e.getGosellStatus())))
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
	
	void verifyEvent(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	            
	            Assert.assertTrue(inventoryEventList.size() == 1, "Event records appear");
	            Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	            Assert.assertEquals(inventoryEventList.get(0).getModel_id(), String.valueOf(var.getBcModelId()));
	            Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
	        });
	    } else {
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEvents = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	        
	        Assert.assertTrue(inventoryEvents.size() == 1, "Event records appear");
	        Assert.assertEquals(inventoryEvents.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	        Assert.assertEquals(inventoryEvents.get(0).getModel_id(), null);
	        Assert.assertEquals(inventoryEvents.get(0).getAction(), expectedEvent);
	    }
	}

	void verifyEventRemoved(String currentTimestamp, ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	            
	            Assert.assertTrue(inventoryEventList.size() == 0, "No event records");
	        });
	    } else {
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEvents = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	        
	        Assert.assertTrue(inventoryEvents.size() == 0, "No event records");
	    }
	}
	
	void verifyMapping(ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	            
	            List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	            
	            Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
	        });
	    } else {
	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	        
	        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	        
	        Assert.assertTrue(mappingRecordList.size() == 2, "Mapping records appear");
	    }
	}

	void verifyMappingRemoved(ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
	            var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	            
	            List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	            
	            Assert.assertTrue(mappingRecordList.size() == 0, "Mapping records removed");
	        });
	    } else {
	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
	        var sqlQuery = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' ORDER BY x.id DESC".formatted(product.getBranchId(), inventoryId);
	        
	        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
	        
	        Assert.assertTrue(mappingRecordList.size() == 0, "Mapping records removed");
	    }
	}

	
	@Test(description = "Import products with no variations to GoSELL")
	public void TC_ImportProductToGosell_NoVar() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductsPreAction = new APIShopeeProducts(credentials).getProducts().stream()
		        .filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
		        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
		            Collections.shuffle(collected);
		            return collected.stream();
		        }))
		        .limit(BULK_PRODUCT_COUNT)
		        .collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProductsPreAction.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
        
		//Get Shopee products after the products are linked
		var shopeeProductsPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
        
		Assert.assertEquals(shopeeProductsPostAction.size(), shopeeProductsPreAction.size(), "Shopee product count post-action");
		shopeeProductsPostAction.stream().forEach(prod -> {
			Assert.assertEquals(prod.getGosellStatus(), "SYNC", "Sync status post-action");
			Assert.assertNotNull(prod.getBcItemId(), "Shopee product's counterpart in GoSELL post-action");
		});
		
        //Check database for inventory events
		shopeeProductsPostAction.stream().forEach(product -> verifyEvent(timestampPreAction, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostAction.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	
	@Test(description = "Import products with variations to GoSELL")
	public void TC_ImportProductToGosell_VarAvailable() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductsPreAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
		        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
		            Collections.shuffle(collected);
		            return collected.stream();
		        }))
		        .limit(BULK_PRODUCT_COUNT)
		        .collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProductsPreAction.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
        
		var shopeeProductsPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		Assert.assertEquals(shopeeProductsPostAction.size(), shopeeProductsPreAction.size(), "Shopee product count post-action");
		shopeeProductsPostAction.stream().forEach(prod -> {
			Assert.assertEquals(prod.getGosellStatus(), "SYNC", "Sync status post-action");
			Assert.assertNotNull(prod.getBcItemId(), "Shopee product's counterpart in GoSELL post-action");
		});	
		
		//Check database for inventory events
		shopeeProductsPostAction.stream().forEach(product -> verifyEvent(timestampPreAction, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostAction.stream().forEach(product -> verifyMapping(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}

	@Test(description = "Link a Shopee product with no variations to a GoSELL product")
	public void TC_LinkProduct_NoVar() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());
		
		//Get Shopee products after the products are linked
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProductPreAction.getShopeeItemId()))
				.findFirst().orElse(null);
		
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEvent(timestampPreAction, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Link a Shopee product with variations to a GoSELL product")
	public void TC_LinkProduct_VarAvailable() {
		
		//Select a Shopee product matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		var mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellProductDetail.getModels(), mappedVariations);
		
		//Get Shopee products after the products are linked
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
	    List<List<String>> mappedVariationIdsPostAction = shopeeProductPostAction.getVariations().stream()
	            .map(product -> List.of(product.getShopeeVariationId(), String.valueOf(product.getBcModelId())))
	            .collect(Collectors.toList());
		
	    Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
	    Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		Assert.assertEquals(mappedVariationIdsPostAction, mappedVariationIds, "Mapped variation ids post-action");
		
		//Check database for inventory events
		verifyEvent(timestampPreAction, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Unlink a LINKED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_NoVar() {
		
		//Select a Shopee product matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, "UNLINK"));
		
		//Create a GoSELL product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProductNoVariations(selectedShopeeProduct, String.valueOf(gosellProductId));
		
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		Assert.assertEquals(shopeeProductPostLink.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		verifyMapping(shopeeProductPostLink);
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductPostLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductPostUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductPostUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingRemoved(shopeeProductPostLink);
		
	}
	
	@Test(description = "Unlink a LINKED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkLinkedProduct_VarAvailable() {
		
		var selectedShopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
		var mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellProductDetail.getModels(), mappedVariations);
		
		//Get Shopee products after the products are linked
		var shopeeProductAfterLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		List<List<String>> mappedVariationIdsAfterLink = shopeeProductAfterLink.getVariations().stream()
				.map(product -> {
					List<String> mappedVarIds = new ArrayList<>();
					mappedVarIds.add(product.getShopeeVariationId());
					mappedVarIds.add(String.valueOf(product.getBcModelId()));
					return mappedVarIds;
				}).collect(Collectors.toList());
		
		Assert.assertEquals(shopeeProductAfterLink.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductAfterLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		Assert.assertEquals(mappedVariationIdsAfterLink, mappedVariationIds, "Mapping is correct");
		
		//Check database for inventory events
		verifyEvent(timestampPreAction, shopeeProductAfterLink, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductAfterLink);	
		
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductAfterLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductAfterUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductAfterUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyMappingRemoved(shopeeProductAfterLink);
	}

	@Test(description = "Unlink a SYNCED Shopee product with no variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
        
		//Get Shopee products after the products are linked
		var shopeeProductsAfterCreateToGosell = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
        
		shopeeProductsAfterCreateToGosell.stream().forEach(prod -> {
			Assert.assertEquals(prod.getGosellStatus(), "SYNC", "Sync status post-action");
			//TODO check whether Shopee product is mapped correctly with the GoSELL product
		});
		
        //Check database for inventory events
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyEvent(timestampPreAction, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyMapping(product));
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIds);
		
		//Get Shopee products post unlink process
		var shopeeProductsAfterUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsAfterUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyMappingRemoved(product));
	}	
	
	@Test(description = "Unlink a SYNCED Shopee product with variations from a GoSELL product")
	public void TC_UnlinkSyncedProduct_VarAvailable() {
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIds);
		
		//Get Shopee products after the products are linked
		var shopeeProductsAfterCreateToGosell = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Check database for inventory events
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyEvent(timestampPreAction, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyMapping(product));
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIds);
		
		//Get Shopee products post unlink process
		var shopeeProductsAfterUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsAfterUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsAfterCreateToGosell.stream().forEach(product -> verifyMappingRemoved(product));
	}	

	@Test(description = "Download a specific Shopee product with no variations to GoSELL system")
	public void TC_DownloadSpecificProduct_NoVar() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());				
		
		
		//Get a random Shopee product that matches a specific condition
		var shopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, List.of("LINK", "SYNC")));
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
        new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProduct.getShopeeItemId());
        
		//Get Shopee products after the products are linked
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
        
        //Check database for inventory events
		verifyEvent(timestampPreAction, shopeeProductPostAction, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Download a specific Shopee product with variations to GoSELL system")
	public void TC_DownloadSpecificProduct_VarAvailable() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeProductPreAction.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());
		
		
		//Get a random Shopee product that matches a specific condition
		var shopeeProduct = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, List.of("LINK", "SYNC")));
		
		//UI implementation of importing a Shopee product to GoSELL
		var timestampPreAction = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProduct.getShopeeItemId());
		
		//Get Shopee products after the products are linked
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(shopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check database for inventory events
		verifyEvent(timestampPreAction, shopeeProductPostAction, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name());
		
		//Check database for mapping records
		verifyMapping(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Download all Shopee products to GoSELL system")
	public void TC_DownloadAllProduct() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeProductPreAction.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());
		
		
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> List.of("LINK", "SYNC").contains(e.getGosellStatus()))
				.collect(Collectors.toList());
		
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//Get the current timestamp
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		new AccountInformationPage(driver).navigateToShopeeAccountInformationPage().clickDownloadButton();
		
		//Get Shopee products after the products are linked
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
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.FALSE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());		
		
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && List.of("LINK").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
        new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
        
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
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
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}	
	
	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	public void TC_UpdateLinkedProduct_VarAvailable() {
		
		//Select a Shopee product matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(getShopeeProductListByCondition(Boolean.TRUE, "UNLINK"));
		
		//Create a GoSELL product to link with the Shopee product
		Integer gosellProductId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(shopeeProductPreAction.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(shopeeProductPreAction.getShopeeItemId(), gosellProductDetail.getName());
		
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && List.of("LINK").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
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
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIdTemp = shopeeProductTemp.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIdTemp);		
		
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.FALSE) && List.of("SYNC").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
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
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}	
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	public void TC_UpdateSyncedProduct_VarAvailable() {
		
		
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && e.getGosellStatus().contentEquals("UNLINK")))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIdTemp = shopeeProductTemp.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		//UI implementation of importing a Shopee product to GoSELL
		
		new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIdTemp);		
		
		
		//Get Shopee products that match a specific condition
		var shopeeProducts = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> (e.getHasVariation().equals(Boolean.TRUE) && List.of("SYNC").contains(e.getGosellStatus())))
				.limit(BULK_PRODUCT_COUNT).collect(Collectors.toList());
		
		//Get Ids of the Shopee products
		var shopeeProductIds = shopeeProducts.stream().map(e -> e.getShopeeItemId()).collect(Collectors.toList());
		
		var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//UI implementation of importing a Shopee product to GoSELL
		new ProductsPage(driver).navigateByURL().downloadSpecificProduct(shopeeProductIds);
		
		var shopeeProductsAfterDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
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
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.collect(Collectors.toList());
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}	
	
}
