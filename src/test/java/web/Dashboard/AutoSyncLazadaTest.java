package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.sale_channel.lazada.APILazadaAccount;
import api.Seller.sale_channel.lazada.APILazadaProducts;
import api.Seller.sale_channel.lazada.APILazadaSetting;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import sql.SQLGetInventoryEvent;
import sql.SQLGetInventoryMapping;
import utilities.commons.UICommonAction;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.EventAction;
import utilities.enums.ProductThirdPartyStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.inventory.InventoryMapping;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.sales_channels.lazada.lazada_products.LazadaProducts;

import java.sql.Connection;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class AutoSyncLazadaTest extends BaseTest {
    String sellerUsername;
    String sellerPass;
    LoginInformation loginInformation;
    Connection connection = null;
    Logger logger = LogManager.getLogger(AutoSyncLazadaTest.class);
    int branchId;
    String lazadaShopId;
    LoginDashboardInfo loginDashboard;

    @SneakyThrows
    @BeforeClass
    public void beforeClass() {
        sellerUsername = ADMIN_SHOP_VI_USERNAME;
        sellerPass = ADMIN_SHOP_VI_PASSWORD;
        connection = new InitConnection().createConnection();
        loginInformation = new Login().setLoginInformation("+84", sellerUsername, sellerPass).getLoginInformation();
        loginDashboard = new Login().getInfo(loginInformation);
        //turn on auto sync
        new APILazadaSetting(loginInformation).turnOnAutoSync();
        String branchAndLazadaShop = new APILazadaAccount(loginInformation).getBranchIDAndLazadaShopConnected();
        branchId = Integer.parseInt(branchAndLazadaShop.split("-")[0]);
        lazadaShopId = branchAndLazadaShop.split("-")[1];
        driver = new InitWebdriver().getDriver(browser, "false");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language))
                .performValidLogin("Vietnam", sellerUsername, sellerPass);
    }
    @SneakyThrows
    @AfterClass
    public void afterClass(){
        if (connection != null) connection.close();
        if (driver != null) driver.quit();
    }
    @DataProvider
    public Object[][] dataCreateToGoSell() {
        return new Object[][]{
                {1, false},
                {3, false},
                {1, true},
                {3, true}
        };
    }
    public void waitManyTime(int time){
        new UICommonAction(driver).sleepInMiliSecond(2000*time, "Wait for data insert into database.");
    }
    @Test(dataProvider = "dataCreateToGoSell", priority = 1)
    public void CreateProductToGoSell(int productNumber, boolean hasVariation) {
        LazadaProducts lazadaProducts = new LazadaProducts(driver, loginInformation);
        List<Long> allLazadaIdUnlink = lazadaProducts.getUnLinkProduct(hasVariation);
        List<Long> lazadaProductNeedCreateToGoSell = allLazadaIdUnlink.stream().limit(productNumber).collect(Collectors.toList());
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());
        System.out.println("mappingNotChangeBefore SIZE: "+mappingNotChangeBefore.size());
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        new LazadaProducts(driver).navigateByURL()
                .createProductToGoSell(lazadaProductNeedCreateToGoSell);
        //Verify
        waitManyTime(productNumber);
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        lazadaProductNeedCreateToGoSell.forEach(i -> {
            logger.info("Lazada product id: {}", i);
            String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(i);
            int branchId = Integer.parseInt(branchProduct.split("-")[0]);
            long productId = Long.parseLong(branchProduct.split("-")[1]);
            LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
            lazadaProductPage.verifyInventoryEvent(branchId, productId, EventAction.GS_LAZADA_SYNC_ITEM_EVENT, currentDate);
            lazadaProductPage.verifyInventoryMapping(branchId, productId);
        });
        //Verify the others mapping not change
        List<String> branchProductList = lazadaProducts.getBranchProductRelevantLazadaProduct(lazadaProductNeedCreateToGoSell);
        lazadaProducts.verifyInventoryMappingExceptProductList(branchProductList, loginDashboard.getStoreID(), mappingNotChangeBefore);
    }

    @DataProvider
    public Object[][] dataForLinkProductTest() {
        return new Object[][]{
                {true},
                {false}
        };
    }

    @Test(dataProvider = "dataForLinkProductTest", priority = 2)
    public void LinkProductTest(boolean hasVariation) {
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> allLazadaIdNotUnlink = lazadaProductPage.getUnLinkProduct(hasVariation);
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        long lazadaProductId = allLazadaIdNotUnlink.get(0);
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());
        long productId = new APICreateProduct(loginInformation).createAndLinkProductTo3rdPartyThenRetrieveId(apiLazadaProducts.getVariationNumberOfLazadaProduct(lazadaProductId),10);

        //Call api to link product
        Map<String, List> inventoryMappingsEventsExpected = apiLazadaProducts.linkProductThenGetInventoryInfo(branchId, productId, lazadaProductId);
        List<InventoryMapping> inventoryMappingsExpected = inventoryMappingsEventsExpected.get("inventoryMapping");
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = inventoryMappingsEventsExpected.get("inventoryEvent");
        //Verify
        waitManyTime(1);
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
        lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingsExpected);
        //Verify the others mapping not change
        List<String> branchProductList = lazadaProductPage.getBranchProductRelevantLazadaProduct(List.of(lazadaProductId));
        lazadaProductPage.verifyInventoryMappingExceptProductList(branchProductList, loginDashboard.getStoreID(), mappingNotChangeBefore);
    }

    @DataProvider
    public Object[][] dataUpdateToGoSell() {
        return new Object[][]{
                {1, false, ProductThirdPartyStatus.SYNC},
                {3, false, ProductThirdPartyStatus.LINK},
                {1, true, ProductThirdPartyStatus.LINK},
                {3, true, ProductThirdPartyStatus.SYNC}
        };
    }

    @Test(dataProvider = "dataUpdateToGoSell", priority = 3)
    @SneakyThrows
    public void UpdateProductToGoSell(int productNumber, boolean hasVariation, ProductThirdPartyStatus status) {
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> allLazadaIdNotUnlink = lazadaProductPage.getSyncedLinkedProduct(hasVariation, productNumber, status);
        //Get inventoryMappings before update to gosell
        List<Long> lazadaProductNeedUpdateToGoSell = allLazadaIdNotUnlink.stream().limit(productNumber).collect(Collectors.toList());
        List<String> branchProductList = lazadaProductPage.getBranchProductRelevantLazadaProduct(lazadaProductNeedUpdateToGoSell);
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(branchProductList, loginDashboard.getStoreID());
        List<InventoryMapping> inventoryMappingsExpected = new ArrayList<>();
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        lazadaProductNeedUpdateToGoSell.forEach(i -> {
            String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(i);
            int branchId = Integer.parseInt(branchProduct.split("-")[0]);
            long productId = Long.parseLong(branchProduct.split("-")[1]);
            inventoryMappingsExpected.addAll(new APILazadaProducts(loginInformation).getLazadaInventoryMappingInfo(branchId, productId));
        });
        //Action: update to goSell on UI
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        lazadaProductPage.navigateByURL()
                .updateProductToGoSell(lazadaProductNeedUpdateToGoSell);
        //Verify event not create and keep mapping
        waitManyTime(productNumber);
        List<InventoryMapping> inventoryMappingsActual = new ArrayList<>();
        lazadaProductNeedUpdateToGoSell.forEach(i -> {
            logger.info("Lazada product id: {}", i);
            String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(i);
            int branchId = Integer.parseInt(branchProduct.split("-")[0]);
            long productId = Long.parseLong(branchProduct.split("-")[1]);
            //Verify no inventory event created when status = SYNC,
            if(status.equals(ProductThirdPartyStatus.SYNC)){
                List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
                List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new ArrayList<>();
                lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
            }else //Status = LINKED, has event
                lazadaProductPage.verifyInventoryEvent(branchId,productId, EventAction.GS_LAZADA_SYNC_ITEM_EVENT, currentDate);
            inventoryMappingsActual.addAll(new SQLGetInventoryMapping(connection)
                    .getLazadaInventoryMapping(branchId, productId));
        });
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingsExpected);
        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(branchProductList, loginDashboard.getStoreID(), mappingNotChangeBefore);
    }

    @DataProvider
    public Object[][] dataDownloadTest() {
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, false},
                {ProductThirdPartyStatus.SYNC, true},
                {ProductThirdPartyStatus.LINK, false},
                {ProductThirdPartyStatus.LINK, true},
        };
    }

    @SneakyThrows
    @Test(dataProvider = "dataDownloadTest", priority = 4)
    public void DownloadOneProduct(ProductThirdPartyStatus status, boolean hasVariation) {
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> allLazadaIdByStatus = lazadaProductPage.getSyncedLinkedProduct(hasVariation, 1, status);
        long lazadaProductId = allLazadaIdByStatus.get(0);
        logger.info("Lazada product id: {}", lazadaProductId);

        //Get inventoryMappings and event before update to gosell
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        int branchId = Integer.parseInt(branchProduct.split("-")[0]);
        long productId = Long.parseLong(branchProduct.split("-")[1]);
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of(branchProduct), loginDashboard.getStoreID());
        boolean isHasLinkError = apiLazadaProducts.hasLinkErrorStatus(branchId, productId);
        List<InventoryMapping> inventoryMappingsExpected = new ArrayList<>();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new ArrayList<>();
        if (!isHasLinkError) {
            inventoryEventsExpected = new APILazadaProducts(loginInformation).getInventoryEventInfo(branchId, productId, EventAction.GS_LAZADA_DOWNLOAD_PRODUCT);
            inventoryMappingsExpected.addAll(new APILazadaProducts(loginInformation).getLazadaInventoryMappingInfo(branchId, productId));
        }
        //Action download on UI
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        new LazadaProducts(driver).navigateByURL()
                .clickDownloadProduct(String.valueOf(lazadaProductId));
        new UICommonAction(driver).sleepInMiliSecond(2000, "Wait for data insert into database.");

        //Verify event and mapping
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
        lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingsExpected);
        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of(branchProduct), loginDashboard.getStoreID(), mappingNotChangeBefore);
    }

    public void DownloadAllProduct() {
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        List<Long> allLazadaIdNotUnlink = apiLazadaProducts.getLazadaProductIdNotStatus(ProductThirdPartyStatus.UNLINK);

        //Get inventoryMappings and event before update to gosell
        List<InventoryMapping> inventoryMappingsExpected = new ArrayList<>();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new ArrayList<>();
        List<String> branchProductList = new ArrayList<>();
        allLazadaIdNotUnlink.forEach(lazadaProductId -> {
            String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
            branchProductList.add(branchProduct);
            int branchId = Integer.parseInt(branchProduct.split("-")[0]);
            long productId = Long.parseLong(branchProduct.split("-")[1]);
            boolean isHasLinkError = apiLazadaProducts.hasLinkErrorStatus(branchId, productId);
            if (!isHasLinkError) {
                inventoryEventsExpected.addAll(new APILazadaProducts(loginInformation).getInventoryEventInfo(branchId, productId, EventAction.GS_LAZADA_DOWNLOAD_PRODUCT));
                inventoryMappingsExpected.addAll(new APILazadaProducts(loginInformation).getLazadaInventoryMappingInfo(branchId, productId));
            }
        });
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        //Action download on UI
        new APILazadaAccount(loginInformation).downloadLazadaProductByAPI();
        new UICommonAction(driver).sleepInMiliSecond(5000, "Wait for data insert into database.");

        //Verify event and mapping
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new ArrayList<>();
        List<InventoryMapping> inventoryMappingsActual = new ArrayList<>();
        branchProductList.forEach(i -> {
            int branchId = Integer.parseInt(i.split("-")[0]);
            long productId = Long.parseLong(i.split("-")[1]);
            inventoryEventsActual.addAll(new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate));
            inventoryMappingsActual.addAll(new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId));
        });
        lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingsExpected);
    }
    @DataProvider
    public Object[][] dataForUnlinkTest() {
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, false},
                {ProductThirdPartyStatus.SYNC, true},
                {ProductThirdPartyStatus.LINK, false},
                {ProductThirdPartyStatus.LINK, true},
        };
    }
    @Test(dataProvider = "dataForUnlinkTest", priority = 5)
    @SneakyThrows
    public void UnlinkProductTest(ProductThirdPartyStatus status, boolean hasVariation) {
        logger.info("Test for case: ProductThirdPartyStatus is %s - hasVariation is %s".formatted(status,hasVariation));
        //Get lazada product
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> syncedLinkedProduct = lazadaProductPage.getSyncedLinkedProduct(hasVariation, 1, status);
        long lazadaProductId = syncedLinkedProduct.get(0);
        logger.info("Lazada product id: {}", lazadaProductId);

        //Get branch and productId
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        int branchId = Integer.parseInt(branchProduct.split("-")[0]);
        long productId = Long.parseLong(branchProduct.split("-")[1]);
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection)
                .getLazadaInventoryMappingExceptProduct(List.of(branchProduct), loginDashboard.getStoreID());

        //call API unlink
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        apiLazadaProducts.unlinkLazadaProduct(lazadaProductId);

        //Verify
        waitManyTime(2);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
        List<InventoryMapping> inventoryMappingExpected = new ArrayList<>();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventExpected = new ArrayList<>();
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingExpected);
        lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventExpected);
        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of(branchProduct), loginDashboard.getStoreID(), mappingNotChangeBefore);
    }
}
