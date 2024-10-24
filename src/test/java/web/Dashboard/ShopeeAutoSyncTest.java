package web.Dashboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIShopeeProducts;
import api.Seller.setting.BranchManagement;
import lombok.SneakyThrows;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.sales_channels.shopee.products.ProductsPage;

public class ShopeeAutoSyncTest extends BaseTest {

	LoginInformation credentials;
	BranchInfo branchInfo;

	Connection dbConnection;

	@BeforeClass
	void onetimeLoadedData() {
		credentials = new Login().setLoginInformation("uyen.lai@mediastep.com", "Abc@12345").getLoginInformation();
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

	@SneakyThrows
	void verifyEventOfProductWithoutVariation(String currentTimestamp, Integer gosellBranchId, Integer linkedGosellProductId, String expectedEvent) {
		
		var queryForInventoryEvent = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(gosellBranchId, linkedGosellProductId, currentTimestamp);

		System.out.println(queryForInventoryEvent);
		
		ResultSet resultSet = InitConnection.executeSQL(dbConnection, queryForInventoryEvent);

		List<List<String>> itemAndModelIds = new ArrayList<>();
		while (resultSet.next()) {
			if (resultSet.wasNull()) break;
			List<String> itemAndModelId = new ArrayList<>();
			itemAndModelId.add(resultSet.getString("item_id"));
			itemAndModelId.add(resultSet.getString("model_id"));
			itemAndModelId.add(resultSet.getString("action"));
			itemAndModelIds.add(itemAndModelId);
		}
		
		Assert.assertTrue(itemAndModelIds.size() == 1, "Event records appear");
		
		Assert.assertEquals(itemAndModelIds.get(0).get(0), String.valueOf(linkedGosellProductId));
		Assert.assertEquals(itemAndModelIds.get(0).get(1), null);
		Assert.assertEquals(itemAndModelIds.get(0).get(2), expectedEvent);
	}
	
	@SneakyThrows
	void verifyEventOfProductWithVariation(String currentTimestamp, Integer gosellBranchId, Integer linkedGosellProductId, Integer gosellModelId, String expectedEvent) {
		
		var queryForInventoryEvent = "SELECT x.* FROM \"inventory-services\".inventory_event x WHERE branch_id = '%s' and item_id = '%s' and model_id = '%s' and created_date > '%s' ORDER BY x.id DESC".formatted(gosellBranchId, linkedGosellProductId, gosellModelId, currentTimestamp);
		
		System.out.println(queryForInventoryEvent);
		
		ResultSet resultSet = InitConnection.executeSQL(dbConnection, queryForInventoryEvent);
		
		List<List<String>> itemAndModelIds = new ArrayList<>();
		while (resultSet.next()) {
			if (resultSet.wasNull()) break;
			List<String> itemAndModelId = new ArrayList<>();
			itemAndModelId.add(resultSet.getString("item_id"));
			itemAndModelId.add(resultSet.getString("model_id"));
			itemAndModelId.add(resultSet.getString("action"));
			itemAndModelIds.add(itemAndModelId);
		}
		
		Assert.assertTrue(itemAndModelIds.size() == 1, "Event records appear");
		
		Assert.assertEquals(itemAndModelIds.get(0).get(0), String.valueOf(linkedGosellProductId));
		Assert.assertEquals(itemAndModelIds.get(0).get(1), String.valueOf(gosellModelId));
		Assert.assertEquals(itemAndModelIds.get(0).get(2), expectedEvent);
	}
	
	@SneakyThrows
	void verifyMappingRecords(String currentTimestamp, Integer gosellBranchId, String inventoryId) {
		
		var queryForMappingEvent = "SELECT x.* FROM \"inventory-services\".inventory_mapping x WHERE branch_id = '%s' and inventory_id = '%s' and updated_date > '%s' ORDER BY x.id DESC".formatted(gosellBranchId, inventoryId, currentTimestamp);
		
		System.out.println(queryForMappingEvent);
		
		ResultSet resultSet = InitConnection.executeSQL(dbConnection, queryForMappingEvent);
		
		List<List<String>> channelList = new ArrayList<>();
		while (resultSet.next()) {
			if (resultSet.wasNull()) break;
			List<String> itemAndModelId = new ArrayList<>();
			itemAndModelId.add(resultSet.getString("channel"));
			channelList.add(itemAndModelId);
		}
		
		Assert.assertTrue(channelList.size() == 2, "Mapping records appear");
	}
	
	@Test(description = "Import products with no variations to GoSELL")
	public void TC_ImportProductToGosell_NoVar() throws SQLException {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		var shopeeShopId =  "751369538";
		
		//Select a branch to test
		var gosellBranchId = branchInfo.getBranchID().get(0);
		
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
		shopeeProductsAfterLink.stream().forEach(prod -> verifyEventOfProductWithoutVariation(currentTimestamp, gosellBranchId, prod.getBcItemId(), expectedEvent));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(prod -> {
			var inventoryId = "%s-%s".formatted(gosellBranchId, prod.getBcItemId());
			verifyMappingRecords(currentTimestamp, gosellBranchId, inventoryId);
		});
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	
	@Test(description = "Import products with variations to GoSELL")
	public void TC_ImportProductToGosell_VarAvailable() throws SQLException {
		
		String expectedEvent = "GS_SHOPEE_SYNC_ITEM_EVENT";
		
		var shopeeShopId =  "751369538";
		
		//Select a branch to test
		var gosellBranchId = branchInfo.getBranchID().get(0);
		
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
		
		
		//Check database for inventory events
		shopeeProductsAfterLink.stream().forEach(prod -> prod.getVariations().stream().forEach(var -> verifyEventOfProductWithVariation(currentTimestamp, gosellBranchId, prod.getBcItemId(), var.getBcModelId(), expectedEvent)));
		
		//Check database for mapping records
		shopeeProductsAfterLink.stream().forEach(prod -> {
			prod.getVariations().stream().forEach(var -> {
				var inventoryId = "%s-%s-%s".formatted(gosellBranchId, prod.getBcItemId(), var.getBcModelId());
				verifyMappingRecords(currentTimestamp, gosellBranchId, inventoryId);
			});			
		});
		
		//Unlink products
		new APIShopeeProducts(credentials).unlinkProduct(shopeeShopId, shopeeProductIds);
	}
	

    @AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}
}
