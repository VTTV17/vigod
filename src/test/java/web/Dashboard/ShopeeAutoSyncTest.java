package web.Dashboard;

import static utilities.character_limit.CharacterLimit.MAX_PRICE;

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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.orders.OrderAPI;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.orders.pos.APICreateOrderPOS;
import api.Seller.orders.pos.APICreateOrderPOS.APICreatePOSCondition;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.APIGetProductDetail;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.APIUpdateProduct;
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation.Model;
import api.Seller.products.all_products.APIProductConversionUnit;
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
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.salechanel.shopee.ShopeeProduct;
import utilities.model.dashboard.salechanel.shopee.Variation;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.return_orders.create_return_order.CreateReturnOrderPage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.Dashboard.products.all_products.management.ProductManagementPage;
import web.Dashboard.sales_channels.shopee.account_information.AccountInformationPage;
import web.Dashboard.sales_channels.shopee.link_products.LinkProductsPage;
import web.Dashboard.sales_channels.shopee.products.ProductsPage;
import web.Dashboard.supplier.purchaseorders.crud.PurchaseOrderPage;


/**
 * <p>Some odd behaviors are observed:
 * <p>- SYNCED product -> access synchronization page => Event DOWNLOAD appears 
 *
 */

public class ShopeeAutoSyncTest extends BaseTest {
	
	static final int BULK_PRODUCT_COUNT = 10;
	static final int DELAY_BEFORE_FETCHING_DATA = 3000;
	static final long PRODUCT_MAX_PRICE = 2000000L;
	static final String OVERRDING_PRODUCT_DESCRIPTION = "Chúng tôi đang trong quá trình cập nhật phần mô tả cho sản phầm này - We're working on the description of this product. Please wait a little!";
	
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
//		credentials = new Login().setLoginInformation("uyen.lai@mediastep.com", "Abc@12345").getLoginInformation();
//		credentials = new Login().setLoginInformation("Bonguyen11397@gmail.com", "Abc@12345").getLoginInformation();
		credentials = new Login().setLoginInformation("phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		branchInfo = new BranchManagement(credentials).getInfo();

//		dbConnection = new InitConnection().createConnection();
		
        if (PropertiesUtil.environment.equals("STAG")) {
        	dbConnection = new InitConnection().createConnection();
        } else {
        	dbConnection = new InitConnection().createConnection("db-ca.mediastep.com", "5432", "postgres", "qc_thangnguyen", "tc1t9rQYXyRS3XcK");
        }
		
		driver = new InitWebdriver().getDriver(browser, headless);
//		new LoginPage(driver).navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language)).performValidLogin("Vietnam", credentials.getEmail(), credentials.getPassword());
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
				.filter(e -> (hasVariation.contains(e.getHasVariation()) && status.contains(e.getGosellStatus())) && !e.getHasLinkErrorStatus())
		        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
		            Collections.shuffle(collected);
		            return collected.stream();
		        }))
		        .limit(outputProductCount)
		        .toList();
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

	void verifyEventsAppear(String currentTimestamp, ShopeeProduct product, String expectedEvent) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_VARS.formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).waitUntilEventRecordsAppear(sqlQuery);
	            
	            inventoryEventList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(inventoryEventList.size(), 1, "Event record count");
	            Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	            Assert.assertEquals(inventoryEventList.get(0).getModel_id(), String.valueOf(var.getBcModelId()));
	            Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
	        });
	    } else {
	        var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_NOVARS.formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).waitUntilEventRecordsAppear(sqlQuery);
	        
	        inventoryEventList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(inventoryEventList.size(), 1, "Event record count");
	        Assert.assertEquals(inventoryEventList.get(0).getItem_id(), String.valueOf(product.getBcItemId()));
	        Assert.assertEquals(inventoryEventList.get(0).getModel_id(), null);
	        Assert.assertEquals(inventoryEventList.get(0).getAction(), expectedEvent);
	    }
	}
	
	void verifyEventsAppear(String currentTimestamp, ShopeeProduct product, List<String> expectedEventList) {
		if (product.getHasVariation()) {
			product.getVariations().forEach(var -> {
				var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_VARS.formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
				
				List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).waitUntilEventRecordsAppear(sqlQuery);
				
				inventoryEventList.stream().forEach(System.out::println);
				
				Assert.assertEquals(inventoryEventList.size(), expectedEventList.size(), "Event record count");
				inventoryEventList.stream().forEach(event -> Assert.assertEquals(event.getItem_id(), String.valueOf(product.getBcItemId())));
				inventoryEventList.stream().forEach(event -> Assert.assertEquals(event.getModel_id(), String.valueOf(var.getBcModelId())));
				Assert.assertEquals(inventoryEventList.stream().map(InventoryEvent::getAction).sorted().toList(), expectedEventList.stream().sorted().toList());
			});
		} else {
			var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_NOVARS.formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
			
			List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).waitUntilEventRecordsAppear(sqlQuery);
			
			inventoryEventList.stream().forEach(System.out::println);
			
			Assert.assertEquals(inventoryEventList.size(), expectedEventList.size(), "Event record count");
			inventoryEventList.stream().forEach(event -> Assert.assertEquals(event.getItem_id(), String.valueOf(product.getBcItemId())));
			inventoryEventList.stream().forEach(event -> Assert.assertEquals(event.getModel_id(), null));
			Assert.assertEquals(inventoryEventList.stream().map(InventoryEvent::getAction).sorted().toList(), expectedEventList.stream().sorted().toList());
		}
	}

	void verifyNoEventsAppear(String currentTimestamp, ShopeeProduct product) {
	    if (product.getHasVariation()) {
	        product.getVariations().forEach(var -> {
	            var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_VARS.formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId(), currentTimestamp);
	            
	            List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	            
	            inventoryEventList.stream().forEach(System.out::println);
	            
	            Assert.assertEquals(inventoryEventList.size(), 0, "Event record count");
	        });
	    } else {
	        var sqlQuery = SQLGetInventoryEvent.SQL_FETCHING_EVENTS_NOVARS.formatted(product.getBranchId(), product.getBcItemId(), currentTimestamp);
	        
	        List<InventoryEvent> inventoryEventList = new SQLGetInventoryEvent(dbConnection).getShopeeInventoryEvents(sqlQuery);
	        
	        inventoryEventList.stream().forEach(System.out::println);
	        
	        Assert.assertEquals(inventoryEventList.size(), 0, "Event record count");
	    }
	}

	/**
	 * Retrieves the inventory mapping records for a list of Shopee products
	 * @param shopeeProductList the list of Shopee products
	 * @param waitForRecords    a flag indicating whether to wait for the records to appear
	 * @return a list of inventory mapping records
	 */	
	List<InventoryMapping> getMappingRecordsOfProductList(List<ShopeeProduct> shopeeProductList, boolean waitForRecords) {
		List<InventoryMapping> mappingRecordList = new ArrayList<>();
		shopeeProductList.stream().forEach(product -> {
		    if (product.getHasVariation()) {
		        product.getVariations().forEach(var -> {
		            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
		            var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
		            
		            if (waitForRecords) {
		            	mappingRecordList.addAll(new SQLGetInventoryMapping(dbConnection).waitUntilMappingRecordsAppear(sqlQuery));
		            } else {
		            	mappingRecordList.addAll(new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery));
		            }
		        });
		    } else {
		        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
		        var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
		        
	            if (waitForRecords) {
	            	mappingRecordList.addAll(new SQLGetInventoryMapping(dbConnection).waitUntilMappingRecordsAppear(sqlQuery));
	            } else {
	            	mappingRecordList.addAll(new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery));
	            }
		    }
		});
		return mappingRecordList;
	}
	
	void verifyMappingRecordsAppear(ShopeeProduct product) {
 	    if (product.getHasVariation()) {
 	        product.getVariations().forEach(var -> {
 	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
 	            var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
 	            
 	            List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).waitUntilMappingRecordsAppear(sqlQuery);
 	            
 	            mappingRecordList.stream().forEach(System.out::println);
 	            
 	            Assert.assertEquals(mappingRecordList.size(), 2, "Mapping record count");
 	        });
 	    } else {
 	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
 	        var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
 	        
 	        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).waitUntilMappingRecordsAppear(sqlQuery);
 	        
 	        mappingRecordList.stream().forEach(System.out::println);
 	        
 	        Assert.assertEquals(mappingRecordList.size(), 2, "Mapping record count");
 	    }
	}
	void verifyNoMappingRecordsAppear(ShopeeProduct product) {
 	    if (product.getHasVariation()) {
 	        product.getVariations().forEach(var -> {
 	            var inventoryId = "%s-%s-%s".formatted(product.getBranchId(), product.getBcItemId(), var.getBcModelId());
 	            var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
 	            
 	            List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
 	            
 	            mappingRecordList.stream().forEach(System.out::println);
 	            
 	            Assert.assertEquals(mappingRecordList.size(), 0, "Mapping record count");
 	        });
 	    } else {
 	        var inventoryId = "%s-%s".formatted(product.getBranchId(), product.getBcItemId());
 	        var sqlQuery = SQLGetInventoryMapping.SQL_FETCHING_MAPPING_RECORDS.formatted(product.getBranchId(), inventoryId);
 	        
 	        List<InventoryMapping> mappingRecordList = new SQLGetInventoryMapping(dbConnection).getMappingRecords(sqlQuery);
 	        
 	        mappingRecordList.stream().forEach(System.out::println);
 	        
 	        Assert.assertEquals(mappingRecordList.size(), 0, "Mapping record count");
 	    }
	}

    @DataProvider
    Boolean[][] hasVariations(){
        return new Boolean[][]{
                {Boolean.FALSE},
                {Boolean.TRUE}
        };
    }		
	
    @Test(description = "Import products to GoSELL", dataProvider = "hasVariations")
    void TC_ImportProductToGosell(Boolean hasVariations) {
        
    	//Get initial mapping records
    	var initiallyMappedProductList = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK","SYNC"), 100);
    	var initialMappingRecordsPreAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
    	
        //Select Shopee products matching a specific condition
        var selectedShopeeProductList = filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT);
        
        var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
        
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //UI implementation of importing a Shopee product to GoSELL
        new ProductsPage(driver).navigateByURL().createProductToGosellBtn(shopeeProductIdList);
        
        //Get Shopee products post action
        var shopeeProductListPostAction = new APIShopeeProducts(credentials).getProducts().stream()
                .filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
                .toList();
        
        //Preliminary check for correct mapping process
        Assert.assertEquals(shopeeProductListPostAction.size(), selectedShopeeProductList.size(), "Shopee product count post-action");
        shopeeProductListPostAction.forEach(product -> {
            Assert.assertEquals(product.getGosellStatus(), "SYNC", "Sync status post-action");
            Assert.assertNotNull(product.getBcItemId(), "Shopee product's counterpart in GoSELL post-action");
        });
        
        //Check database for inventory events
        shopeeProductListPostAction.forEach(product -> verifyEventsAppear(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
        
        //Check database for mapping records
        shopeeProductListPostAction.forEach(product -> verifyMappingRecordsAppear(product));
 
        //Check whether initial mapping records remain
        var initialMappingRecordsPostAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
        Assert.assertEquals(initialMappingRecordsPostAction, initialMappingRecordsPreAction, "Original mapping records post-action");        
        
        //Unlink products
        new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
    }

    @Test(description = "Link a Shopee product to a GoSELL product", dataProvider = "hasVariations")
    void TC_LinkProduct(Boolean hasVariations) {
        
    	//Select Shopee products matching a specific condition
        var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
        var shopeeVariationList = selectedShopeeProduct.getVariations();
        
        //Create a GoSELL product to link with the Shopee product
        var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(hasVariations ? shopeeVariationList.size() : 0, new Random().nextInt(1, 101));
        var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
        
        
        //UI implementation
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        List<List<String>> mappedVariationIds = new ArrayList<>();
        if (!hasVariations) {
            new LinkProductsPage(driver).navigateByURL().linkShopeeProductToGosellProduct(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
        } else {
            if (shopeeVariationList.size() > 12) {
                mappedVariationIds = new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
            } else {
                var mappedVariations = new LinkProductsPage(driver).navigateByURL().linkVariationsBetweenShopeeGosell(selectedShopeeProduct.getShopeeItemId(), gosellProductDetail.getName());
                mappedVariationIds = lookupMappedVariationIds(shopeeVariationList, gosellProductDetail.getModels(), mappedVariations);
            }
        }
        
        //Get Shopee products post action
        var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
                .filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
                .findFirst().orElse(null);
        
        //Preliminary check for correct mapping process
        Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
        Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
        if (hasVariations) {
            List<List<String>> mappedVariationIdsPostAction = shopeeProductPostAction.getVariations().stream()
                    .map(product -> List.of(product.getShopeeVariationId(), String.valueOf(product.getBcModelId())))
                    .toList();
            Assert.assertEquals(mappedVariationIdsPostAction, mappedVariationIds, "Mapped variation ids post-action");
        }
        
        //Check database for inventory events
        verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());
        
        //Check database for mapping records
        verifyMappingRecordsAppear(shopeeProductPostAction);
        
        //Unlink products
        new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
        
        //Delete the products on GoSELL
        new APIEditProduct(credentials).deleteProduct(gosellProductId);
    }	
	
	@Test(description = "Unlink a LINKED Shopee product with no variations from a GoSELL product")
	void TC_UnlinkLinkedProduct_NoVar() {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is linked with the GoSELL product
		Assert.assertEquals(shopeeProductPostLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostLink);
		
		//UI implementation
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductPostLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductPostUnlink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductPostUnlink.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyNoMappingRecordsAppear(shopeeProductPostLink);
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}
	
	@Test(description = "Unlink a LINKED Shopee product with variations from a GoSELL product")
	void TC_UnlinkLinkedProduct_VarAvailable() {
		
		/**Precondition**/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		var mappedVariationIds = new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
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
				}).toList();
		Assert.assertEquals(shopeeProductPostLink.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		Assert.assertEquals(mappedVariationIdsPostLink, mappedVariationIds, "Mapping is correct");
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostLink);
		
		/**TC starts here**/
		//UI implementation
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(List.of(shopeeProductPostLink.getShopeeItemId()));
		
		//Get Shopee products post unlink process
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check if the Shopee product is unlinked from the GoSELL product
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), null, "Linked GoSELL product id");
		
		//Check database for mapping records
		verifyNoMappingRecordsAppear(shopeeProductPostLink);
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}

	@Test(description = "Unlink a SYNCED Shopee product with no variations from a GoSELL product")
	void TC_UnlinkSyncedProduct_NoVar() {
		
		//Get Shopee products that match a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
		var shopeeProductRecordIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getId).toList();
		
		//Import Shopee products to GoSELL
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		//Get Shopee products post import
		var shopeeProductsPostImport = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
        
		shopeeProductsPostImport.stream().forEach(product -> {
			Assert.assertEquals(product.getGosellStatus(), "SYNC", "Sync status post-action");
			//TODO check whether Shopee product is mapped correctly with the GoSELL product
		});
		
        //Check database for inventory events
		shopeeProductsPostImport.stream().forEach(product -> verifyEventsAppear(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostImport.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIdList);
		
		//Get Shopee products post unlink process
		var shopeeProductsPostUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsPostUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsPostUnLink.stream().forEach(product -> verifyNoMappingRecordsAppear(product));
	}	
	
	@Test(description = "Unlink a SYNCED Shopee product with variations from a GoSELL product")
	void TC_UnlinkSyncedProduct_VarAvailable() {
		
		//Get Shopee products that match a specific condition
		var selectedShopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		var shopeeProductIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
		var shopeeProductRecordIdList = selectedShopeeProductList.stream().map(ShopeeProduct::getId).toList();
		
		//Import Shopee products to GoSELL
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		//Get Shopee products post import
		var shopeeProductsPostImport = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Check database for inventory events
		shopeeProductsPostImport.stream().forEach(product -> verifyEventsAppear(preProcessTime, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostImport.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		//UI implementation of unlink Shopee products
		new LinkProductsPage(driver).navigateByURL().unlinkShopeeProductFromGosellProduct(shopeeProductIdList);
		
		//Get Shopee products post unlink process
		var shopeeProductsPostUnLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Additionally check if the Shopee product is unlinked from the GoSELL product
		shopeeProductsPostUnLink.stream().forEach(product -> Assert.assertEquals(product.getBcItemId(), null, "Linked GoSELL product id"));
		
		//Check database for mapping records
		shopeeProductsPostUnLink.stream().forEach(product -> verifyNoMappingRecordsAppear(product));
	}	

	@Test(description = "Download a specific Shopee product to GoSELL system", dataProvider = "hasVariations")
	void TC_DownloadSpecificProduct(Boolean hasVariations) {
	    
    	//Get initial mapping records
    	var initiallyMappedProductList = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK","SYNC"), 100);
    	var initialMappingRecordsPreAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
		
	    /** Precondition **/
	    // Select Shopee products matching a specific condition
	    var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
	    
	    // Create a GoSELL product to link with the Shopee product
	    var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(hasVariations ? selectedShopeeProduct.getVariations().size() : 0, new Random().nextInt(1, 101));
	    var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
	    
	    // Link the Shopee product with the GoSELL product
	    new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
	    
	    /** TC starts here **/
	    // Get a random Shopee product that matches a specific condition
	    var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("LINK", "SYNC"), BULK_PRODUCT_COUNT));
	    
	    // UI implementation
	    var productPage = new ProductsPage(driver).navigateByURL();
	    
	    var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	    
	    productPage.downloadSpecificProduct(shopeeProductPreAction.getShopeeItemId());
	    
	    // Get Shopee products post download
	    var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
	            .filter(e -> e.getShopeeItemId().equals(shopeeProductPreAction.getShopeeItemId()))
	            .findFirst().orElse(null);
	    
	    // Check database for inventory events
	    verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name());
	    
	    // Check database for mapping records
	    verifyMappingRecordsAppear(shopeeProductPostAction);
	    
        //Check whether initial mapping records remain
        var initialMappingRecordsPostAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
        Assert.assertEquals(initialMappingRecordsPostAction, initialMappingRecordsPreAction, "Original mapping records post-action");   
	    
	    // Unlink products
	    new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	}
	
	@Test(description = "Download all Shopee products to GoSELL system")
	void TC_DownloadAllProduct() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		//TODO handle the case where the products are unlinked
		//TODO handle the case where the products don't have variations
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeProductPreAction.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(shopeeProductPreAction, gosellProductDetail);
		
		
		/** TC starts here **/
		var shopeeProductListPreAction = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK", "SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIds = shopeeProductListPreAction.stream().map(ShopeeProduct::getShopeeItemId).toList();
		
		//UI implementation
		var accountInfoPage = new AccountInformationPage(driver).navigateToShopeeAccountInformationPage();
		
		var currentTimestamp = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		accountInfoPage.clickDownloadButton();
		
		//Get Shopee products post download
		var shopeeProductsPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIds.contains(e.getShopeeItemId()))
				.toList();
		
		//Check database for inventory events
		shopeeProductsPostAction.stream().forEach(product -> verifyEventsAppear(currentTimestamp, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostAction.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	

	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	void TC_UpdateLinkedProduct_NoVar() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var shopeeProductPreAction = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(0, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(shopeeProductPreAction, gosellProductDetail);

		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductListPreAction = filterProductsByCondition(List.of(Boolean.FALSE), List.of("LINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductListPreAction.stream().map(ShopeeProduct::getShopeeItemId).toList();
        
        var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductListPreAction);
        
		var shopeeProductListPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
        
        //Check database for inventory events
		shopeeProductListPostDownload.stream().forEach(product -> verifyEventsAppear(preProcessTime, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductListPostDownload.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		
		//UI implementation
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
        //Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEventsAppear(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMappingRecordsAppear(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	
	
	@Test(description = "Incorporate a linked product's info on Shopee into GoSELL system")
	void TC_UpdateLinkedProduct_VarAvailable() {
		
		/** Precondition **/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(selectedShopeeProduct.getVariations().size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("LINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
		
		//UI implementation
        
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEventsAppear(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyEventsAppear(timestampBeforeUpdate, product, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name()));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMappingRecordsAppear(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	void TC_UpdateSyncedProduct_NoVar() {
		
		/** Precondition **/
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = filterProductsByCondition(List.of(Boolean.FALSE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductRecordIdList = shopeeProductTemp.stream().map(ShopeeProduct::getId).toList();
		
		//Import Shopee products to GoSELL
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIdList);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.FALSE), List.of("SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
		
		//UI implementation
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEventsAppear(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyNoEventsAppear(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMappingRecordsAppear(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}	
	
	@Test(description = "Incorporate a synced product's info on Shopee into GoSELL system")
	void TC_UpdateSyncedProduct_VarAvailable() {
		
		/** Precondition **/
		//Get Shopee products that match a specific condition
		var shopeeProductTemp = filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT);
		
		var shopeeProductRecordIds = shopeeProductTemp.stream().map(ShopeeProduct::getId).toList();
		
		//Import Shopee products to GoSELL
		new APIShopeeProducts(credentials).importProductToGosell(shopeeProductRecordIds);
		
		
		/** TC starts here **/
		//Get Shopee products that match a specific condition
		var shopeeProductList = filterProductsByCondition(List.of(Boolean.TRUE), List.of("SYNC"), BULK_PRODUCT_COUNT);
		
		var shopeeProductIdList = shopeeProductList.stream().map(ShopeeProduct::getShopeeItemId).toList();
		
		shopeeProductList.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		//UI implementation
        var timestampBeforeDownload = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        
        //Download Shopee products
        new APIShopeeProducts(credentials).downloadSingleProduct(shopeeProductList);
		
		var shopeeProductsPostDownload = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		//Check database for inventory events
		shopeeProductsPostDownload.stream().forEach(product -> verifyEventsAppear(timestampBeforeDownload, product, EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		//Check database for mapping records
		shopeeProductsPostDownload.stream().forEach(product -> verifyMappingRecordsAppear(product));
		
		var timestampBeforeUpdate = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductsPage(driver).navigateByURL().updateProductToGosellBtn(shopeeProductIdList);
		
		var shopeeProductsPostUpdate = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> shopeeProductIdList.contains(e.getShopeeItemId()))
				.toList();
		
		shopeeProductsPostUpdate.stream().forEach(product -> Assert.assertEquals(product.getGosellStatus(), "SYNC"));
		
		//Check database for inventory events
		shopeeProductsPostUpdate.stream().forEach(product -> verifyNoEventsAppear(timestampBeforeUpdate, product));
		
		//Check database for mapping records
		shopeeProductsPostUpdate.stream().forEach(product -> verifyMappingRecordsAppear(product));		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIdList);
	}	

	@Test(description = "Create a GoSELL product and push it to Shopee", dataProvider ="hasVariations")
	void TC_CreateProduct_ThenPushItToShopee(Boolean hasVariations) {
	    
    	//Get initial mapping records
    	var initiallyMappedProductList = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK","SYNC"), 100);
    	var initialMappingRecordsPreAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
		
		// Override max product price when creating products
	    MAX_PRICE = PRODUCT_MAX_PRICE;

	    // Create a GoSELL product to push to Shopee
	    var variationCount = hasVariations ? new Random().nextInt(1, 11) : 0;
	    var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
	    var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);

	    // UI implementation
	    var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

	    var productPage =  new ProductPage(driver).navigateProductDetail(gosellProductId)
	        .selectShopeeToSync()
	        .clickPlusIcon()
	        .inputDescription(OVERRDING_PRODUCT_DESCRIPTION);
	    if (hasVariations) {
	    	productPage.inputPriceForVariations();
	    }
	    productPage.selectCategory()
	        .selectBrand()
	        .inputPackageWeightAndDimensions()
	        .selectLogistics()
	        .clickSaveBtn();

	    // Get Shopee product post-action
	    var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
	            .filter(product -> product.getBcItemId().equals(Integer.valueOf(gosellProductId)))
	            .findFirst().orElse(null);

	    // Preliminary check for correct mapping process
	    Assert.assertNotNull(shopeeProductPostAction, "Shopee product retrieved post-action");
	    Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "SYNC");
	    if (hasVariations) {
	        Assert.assertEquals(shopeeProductPostAction.getVariations().size(), gosellProductDetail.getModels().size(), "Total of Shopee product variations post-action");
	    }

	    // Check database for inventory events
	    verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name());

	    // Check database for mapping records
	    verifyMappingRecordsAppear(shopeeProductPostAction);

        //Check whether initial mapping records remain
        var initialMappingRecordsPostAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
        Assert.assertEquals(initialMappingRecordsPostAction, initialMappingRecordsPreAction, "Original mapping records post-action");  
	    
	    // Unlink products
	    new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
	    
	    //Delete the products on both GoSELL and Shopee
	    new APIShopeeProducts(credentials).deleteProduct(List.of(shopeeProductPostAction));
	}	
	
	@Test(description = "Delete all variations of a product then push it to Shopee")
	void TC_DeleteVariations_ThenPushProductToShopee() {
		
		//Get initial mapping records
		var initiallyMappedProductList = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK","SYNC"), 100);
		var initialMappingRecordsPreAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
		
		/**Precondition**/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to push to Shopee
		//Override max product price when creating products
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//Get Shopee products after the products are linked
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check database for mapping records
		Assert.assertEquals(shopeeProductPostLink.getVariations().size(), gosellProductDetail.getModels().size(), "Total of Shopee product variations post-link");
		verifyMappingRecordsAppear(shopeeProductPostLink);
		
		/**TC starts here**/
		//UI implementation
		//Delete all variations
		var productPage = new ProductPage(driver).navigateProductDetail(gosellProductId).clickDeleteVariationBtn().clickSaveBtn();
		new ConfirmationDialog(driver).clickOKBtn();
		
		//Get Shopee product post-action
		var shopeeProductPostVarDeletion = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(product -> product.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);		
		
		//Check if there's a link error
		Assert.assertTrue(shopeeProductPostVarDeletion.getHasLinkErrorStatus(), "Link error appears after deleting variations");
		
		//Verify old mapping records are deleted once all variations have been deleted
		verifyNoMappingRecordsAppear(shopeeProductPostLink);
		
		//Push the product to Shopee
		var prePushTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		productPage.selectShopeeToSync()
			.clickPlusIcon()
			.clickPlusIcon()
			.inputDescription(OVERRDING_PRODUCT_DESCRIPTION)
			.inputPriceForVariations()
			.selectCategory()
			.inputPackageWeightAndDimensions()
			.selectLogistics()
			.clickSaveBtn();
		
		//Get Shopee product post-action
		var shopeeProductPostPush = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(product -> product.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		// Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostPush.getBcItemId(), gosellProductId);
		Assert.assertEquals(shopeeProductPostPush.getGosellStatus(), "SYNC");
		Assert.assertFalse(shopeeProductPostPush.getHasVariation(), "Availability of variations post-push");
		
		// Check database for inventory events
		verifyEventsAppear(prePushTime, shopeeProductPostPush, List.of(EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name(), EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		// Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostPush);
		
		//Check whether initial mapping records remain
		var initialMappingRecordsPostAction = getMappingRecordsOfProductList(initiallyMappedProductList, true);
		Assert.assertEquals(initialMappingRecordsPostAction, initialMappingRecordsPreAction, "Original mapping records post-action");  
		
		// Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostPush.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	
	
//	@Test(description = "Add more variations to a product then push it to Shopee")
	void TC_AddVariations_ThenPushProductToShopee() {
		
		//Get initial mapping records
		var initiallyMappedProductList = filterProductsByCondition(List.of(Boolean.TRUE, Boolean.FALSE), List.of("LINK","SYNC"), 100);
		var initialMappingRecordsPreAction = getMappingRecordsOfProductList(initiallyMappedProductList, false);
		
		/**Precondition**/
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(Boolean.TRUE), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		var shopeeVariationList = selectedShopeeProduct.getVariations();
		
		//Create a GoSELL product to push to Shopee
		//Override max product price when creating products
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(shopeeVariationList.size(), new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//Get Shopee products after the products are linked
		var shopeeProductPostLink = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Check database for mapping records
		Assert.assertEquals(shopeeProductPostLink.getVariations().size(), gosellProductDetail.getModels().size(), "Total of Shopee product variations post-link");
		verifyMappingRecordsAppear(shopeeProductPostLink);
		
		/**TC starts here**/
		//UI implementation
		//Add more variations
		new APIUpdateProduct(credentials).updateProductVariations(gosellProductDetail, shopeeVariationList.size() +2);
		
		//Get Shopee product post-action
		var shopeeProductPostVarAddition = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(product -> product.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);		
		
		//Check if there's a link error
		Assert.assertTrue(shopeeProductPostVarAddition.getHasLinkErrorStatus(), "Link error appears after adding variations");
		
		//Verify old mapping records are deleted once all variations have been deleted
		verifyNoMappingRecordsAppear(shopeeProductPostLink);
		
		//Push the product to Shopee
		var prePushTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		new ProductPage(driver).navigateProductDetail(gosellProductId)
		.selectShopeeToSync()
		.inputDescription(OVERRDING_PRODUCT_DESCRIPTION)
		.inputPriceForVariations()
		.selectCategory()
		.inputPackageWeightAndDimensions()
		.selectLogistics()
		.clickSaveBtn();
		
		//Get Shopee product post-action
		var shopeeProductPostPush = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(product -> product.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		// Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostPush.getBcItemId(), gosellProductId);
		Assert.assertEquals(shopeeProductPostPush.getGosellStatus(), "SYNC");
		Assert.assertTrue(shopeeProductPostPush.getHasVariation(), "Availability of variations post-push");
		
		// Check database for inventory events
		verifyEventsAppear(prePushTime, shopeeProductPostPush, List.of(EventAction.GS_SHOPEE_SYNC_ITEM_EVENT.name(), EventAction.GS_SHOPEE_DOWNLOAD_PRODUCT.name()));
		
		// Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostPush);
		
		//Check whether initial mapping records remain
		var initialMappingRecordsPostAction = getMappingRecordsOfProductList(initiallyMappedProductList, true);
		Assert.assertEquals(initialMappingRecordsPostAction, initialMappingRecordsPreAction, "Original mapping records post-action");  
		
		// Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostPush.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	
	
	@Test(dataProvider ="hasVariations")
	void TC_ChangeStockFromDetailScreen(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
	    var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
	    var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
	    var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(5000, "Temporarily sleep a little");	
		
		//UI implementation
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductPage(driver).getLoginInformation(credentials)
        	.navigateToUpdateProductPage(gosellProductId)
        	.updateStock(hasVariations, true);
		
        //Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_SET_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	
	
	@Test(dataProvider ="hasVariations")
	void TC_AddStockFromDetailScreen(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(5000, "Temporarily sleep a little");	
		
		//UI implementation
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		new ProductPage(driver).getLoginInformation(credentials)
		.navigateToUpdateProductPage(gosellProductId)
		.updateStock(hasVariations, false);
		
		//Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_CHANGE_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	

	@Test(dataProvider ="hasVariations")
	void TC_ChangeStockFromManagementScreen(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
	    var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
	    var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
	    var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(5000, "Temporarily sleep a little");	
		
		//UI implementation
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
        String branchName = branchInfo.getBranchName().get(0);
        new ProductManagementPage(driver).getLoginInformation(credentials)
                .navigateToProductManagementPage()
                .excuteSearch(ProductManagementPage.SearchType.BARCODE, String.valueOf(gosellProductId))
                .updateStockAction(branchName, true);
		
        //Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_SET_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}		
	
	@Test(dataProvider ="hasVariations")
	void TC_AddStockFromManagementScreen(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(5000, "Temporarily sleep a little");	
		
		//UI implementation
		var preProcessTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		String branchName = branchInfo.getBranchName().get(0);
		new ProductManagementPage(driver).getLoginInformation(credentials)
			.navigateToProductManagementPage()
			.excuteSearch(ProductManagementPage.SearchType.BARCODE, String.valueOf(gosellProductId))
			.updateStockAction(branchName, false);
		
		//Get Shopee products post action
		var shopeeProductPostAction = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		//Preliminary check for correct mapping process
		Assert.assertEquals(shopeeProductPostAction.getGosellStatus(), "LINK", "Sync status post-action");
		Assert.assertEquals(shopeeProductPostAction.getBcItemId(), gosellProductId, "Shopee product's counterpart in GoSELL post-action");
		
		//Check database for inventory events
		verifyEventsAppear(preProcessTime, shopeeProductPostAction, EventAction.GS_CHANGE_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostAction);
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostAction.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}		
	
	@Test(dataProvider ="hasVariations")
	void TC_CreatePOSOrderThenCancelIt(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(5000, "Temporarily sleep a little");	
		
		//UI implementation
		var preOrderTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		
		//Place an order on POS
		APICreatePOSCondition condition = new APICreateOrderPOS.APICreatePOSCondition();
        condition.setProductInfoList(List.of(new APIProductDetail(credentials).getInfo(gosellProductId)));
        condition.setBranchId(branchInfo.getBranchID().get(0));
        
        int orderId = new APICreateOrderPOS(credentials).getInfo(condition).createPOSOrder();

		//Get Shopee products post order
		var shopeeProductPostOrder = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		
		var tempShopeeProductPostOrder = shopeeProductPostOrder;
		if (hasVariations) {
			OrderDetailInfo detailInfo = new APIOrderDetail(credentials).getOrderDetail(orderId);
			//TODO enhance this so it takes a list of products
			var itemVariationIdInOrderDetail = detailInfo.getItems().get(0).getVariationId();
			
			var correctVar = shopeeProductPostOrder.getVariations().stream()
					.filter(var -> String.valueOf(var.getBcModelId()).contentEquals(String.valueOf(itemVariationIdInOrderDetail)))
					.findFirst().orElse(null);
			tempShopeeProductPostOrder.setVariations(List.of(correctVar));
		}
		
		//Check database for inventory events
		verifyEventsAppear(preOrderTime, tempShopeeProductPostOrder, EventAction.GS_CHANGE_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostOrder);
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(2000, "Temporarily sleep a little");
		
		//Cancel order
		var preCancelOrderTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        new OrderAPI(credentials).cancelOrder(orderId);
		
		//Check database for inventory events
		verifyEventsAppear(preCancelOrderTime, tempShopeeProductPostOrder, EventAction.GS_CHANGE_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostOrder);
        
        
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostOrder.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}	
	
	@Test(dataProvider ="hasVariations")
	void TC_ReturnOrder(Boolean hasVariations) {
		
		//Select Shopee products matching a specific condition
		var selectedShopeeProduct = DataGenerator.getRandomListElement(filterProductsByCondition(List.of(hasVariations), List.of("UNLINK"), BULK_PRODUCT_COUNT));
		
		//Create a GoSELL product to link with the Shopee product
		MAX_PRICE = PRODUCT_MAX_PRICE;
		var variationCount = hasVariations ? selectedShopeeProduct.getVariations().size() : 0;
		var gosellProductId = new APICreateProduct(credentials).createProductTo3rdPartyThenRetrieveId(variationCount, new Random().nextInt(1, 101));
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		//Link the Shopee product with the GoSELL product
		new APIShopeeProducts(credentials).linkProduct(selectedShopeeProduct, gosellProductDetail);
		
		//Place an order on POS
		APICreatePOSCondition condition = new APICreateOrderPOS.APICreatePOSCondition();
		condition.setProductInfoList(List.of(new APIProductDetail(credentials).getInfo(gosellProductId)));
		condition.setBranchId(branchInfo.getBranchID().get(0));
		condition.setHasDelivery(false);
		
		int orderId = new APICreateOrderPOS(credentials).getInfo(condition).createPOSOrder();
		
		//Get Shopee products post order
		var shopeeProductPostOrder = new APIShopeeProducts(credentials).getProducts().stream()
				.filter(e -> e.getShopeeItemId().equals(selectedShopeeProduct.getShopeeItemId()))
				.findFirst().orElse(null);
		
		var tempShopeeProductPostOrder = shopeeProductPostOrder;
		if (hasVariations) {
			OrderDetailInfo detailInfo = new APIOrderDetail(credentials).getOrderDetail(orderId);
			//TODO enhance this so it takes a list of products
			var itemVariationIdInOrderDetail = detailInfo.getItems().get(0).getVariationId();
			
			var correctVar = shopeeProductPostOrder.getVariations().stream()
					.filter(var -> String.valueOf(var.getBcModelId()).contentEquals(String.valueOf(itemVariationIdInOrderDetail)))
					.findFirst().orElse(null);
			tempShopeeProductPostOrder.setVariations(List.of(correctVar));
		}
		
		//TODO delete this sleep
		UICommonAction.sleepInMiliSecond(2000, "Temporarily sleep a little");
		
		//UI implementation
		//Create return order
		var preReturnOrderTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		new CreateReturnOrderPage(driver).navigateToCreateOrder(orderId).createReturnOrder();
		
		//Check database for inventory events
		verifyEventsAppear(preReturnOrderTime, tempShopeeProductPostOrder, EventAction.GS_CHANGE_PRODUCT_STOCK.name());
		
		//Check database for mapping records
		verifyMappingRecordsAppear(shopeeProductPostOrder);
		
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, List.of(shopeeProductPostOrder.getShopeeItemId()));
		
		//Delete the products on GoSELL
		new APIEditProduct(credentials).deleteProduct(gosellProductId);
	}		
	
	@Test
	void TC_PurchaseOrderExp() throws Exception {
		
//		var gosellProductId = 1311980; //basic
//		var gosellProductId = 1311983; //vars
		var gosellProductId = 1311987; //units
		var gosellProductDetail = new APIGetProductDetail(credentials).getProductInformation(gosellProductId);
		
		
//		var hasVariations = gosellProductDetail.isHasModel();
//		var barcodeList = hasVariations ? gosellProductDetail.getModels().stream().map(var -> var.getBarcode()).toList() : List.of(gosellProductDetail.getBarcode());
		
		
		var conversionUnitList = new APIProductConversionUnit(credentials).getItemConversionUnit(gosellProductId);
		
		
		var purchaseOrderPage = new PurchaseOrderPage(driver);
//		purchaseOrderPage.navigateToCreatePurchaseOrderPageByURL();
//		purchaseOrderPage.inputPurchaseOrderInfo(credentials);

		System.out.println();
	}	
	
}
