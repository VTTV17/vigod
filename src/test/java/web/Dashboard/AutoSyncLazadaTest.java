package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.orders.OrderAPI;
import api.Seller.orders.order_management.APIGetOrderList;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.orders.pos.APICreateOrderPOS;
import api.Seller.products.all_products.*;
import api.Seller.products.transfer.APIEditTransfer;
import api.Seller.products.transfer.APITransferDetail;
import api.Seller.sale_channel.lazada.APILazadaAccount;
import api.Seller.sale_channel.lazada.APILazadaProducts;
import api.Seller.sale_channel.lazada.APILazadaSetting;
import api.Seller.setting.BranchManagement;
import lombok.SneakyThrows;
//import org.apache.commons.io.function.IOQuadFunction;
import org.apache.commons.lang3.function.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.*;
import sql.SQLGetInventoryEvent;
import sql.SQLGetInventoryMapping;
import utilities.api.API;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.EventAction;
import utilities.enums.ProductThirdPartyStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.orderdetail.ItemOrderInfo;
import utilities.model.dashboard.orders.orderdetail.OrderDetailInfo;
import utilities.model.dashboard.orders.pos.CreatePOSOrderCondition;
import utilities.model.dashboard.products.inventory.InventoryMapping;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.return_orders.create_return_order.CreateReturnOrderPage;
import web.Dashboard.orders.return_orders.return_order_management.ReturnOrdersManagementPage;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.Dashboard.products.all_products.crud.sync_lazada.SyncLazadaPage;
import web.Dashboard.products.all_products.management.ProductManagementPage;
import web.Dashboard.products.transfer.crud.TransferPage;
import web.Dashboard.sales_channels.lazada.lazada_products.LazadaProducts;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import java.util.stream.Collectors;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.enums.EventAction.GS_CHANGE_PRODUCT_STOCK;

public class AutoSyncLazadaTest extends BaseTest {
    String sellerUsername;
    String sellerPass;
    LoginInformation loginInformation;
    Connection connection = null;
    Logger logger = LogManager.getLogger(AutoSyncLazadaTest.class);
    int branchId;
    String lazadaShopId;
    LoginDashboardInfo loginDashboard;
    int transferId_OriginBranchConnectedLazada, transferId_DestinationConnectedLazada;

    @SneakyThrows
    @BeforeClass
    public void beforeClass() {
        sellerUsername = ADMIN_SHOP_VI_USERNAME;
        sellerPass = ADMIN_SHOP_VI_PASSWORD;
        connection = new InitConnection().createConnection();
        loginInformation = new Login().setLoginInformation("+84", sellerUsername, sellerPass).getLoginInformation();
        loginDashboard = new Login().getInfo(loginInformation);
        MAX_PRICE = 999999L;
        //turn on auto sync
        new APILazadaSetting(loginInformation).turnOnAutoSync();
        String branchAndLazadaShop = new APILazadaAccount(loginInformation).getBranchIDAndLazadaShopConnected();
        branchId = Integer.parseInt(branchAndLazadaShop.split("-")[0]);
        lazadaShopId = branchAndLazadaShop.split("-")[1];
        driver = new InitWebdriver().getDriver(browser, "false");
        LoginPage loginPage = new LoginPage(driver, Domain.valueOf(domain));
        loginPage.navigate().changeDisplayLanguage(DisplayLanguage.valueOf(language))
                .performValidLogin("Vietnam", sellerUsername, sellerPass);
    }
    @SneakyThrows
    @AfterClass
    public void afterClass(){
        if (connection != null) connection.close();
        if (driver != null) driver.quit();
    }

    public void waitManyTime(int time){
        new UICommonAction(driver).sleepInMiliSecond(2000*time, "Wait for data insert into database.");
    }
    @FunctionalInterface
    public interface QuadConsumer<T, U, V, W> {
        void accept(T t, U u, V v, W w);
    }
    public void actionThenCheckChangeStockEvent(ProductThirdPartyStatus status, boolean hasVariation, QuadConsumer<Integer, AtomicReference<List<String>>, AtomicReference<ZonedDateTime>,AtomicReference<EventAction>> action){
        //Get productId
        long lazadaProductId = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(hasVariation,1,status).get(0);
        String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        int productId = Integer.parseInt(branchProduct.split("-")[1]);
        AtomicReference<List<String>> modelIdList = new AtomicReference<>(new ArrayList<>());
        AtomicReference<EventAction> event  = new AtomicReference<>(GS_CHANGE_PRODUCT_STOCK);
        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());

        //Excute action
        AtomicReference<ZonedDateTime> currentDate = new AtomicReference<>(ZonedDateTime.now(ZoneOffset.UTC));
        action.accept(productId, modelIdList, currentDate,event);
        System.out.println("modelIdList: "+modelIdList);
        //Verify event
        waitManyTime(1);
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        if(modelIdList.get().isEmpty())
            lazadaProductPage.verifyInventoryEvent(branchId, productId, event.get(), currentDate.toString());
        else for (String modelId: modelIdList.get()){
            lazadaProductPage.verifyInventoryEvent(branchId, productId, modelId, event.get(), currentDate.toString());
        }
        //Verify the all mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of("0"), loginDashboard.getStoreID(), mappingNotChangeBefore);
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
    @Test(dataProvider = "dataCreateToGoSell", priority = 1)
    public void TC01_CreateProductToGoSell(int productNumber, boolean hasVariation) {
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
    public void TC02_LinkProductTest(boolean hasVariation) {
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> allLazadaIdNotUnlink = lazadaProductPage.getUnLinkProduct(hasVariation);
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        long lazadaProductId = allLazadaIdNotUnlink.getFirst();
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());
        long productId = new APICreateProduct(loginInformation).createProductTo3rdPartyThenRetrieveId(apiLazadaProducts.getVariationNumberOfLazadaProduct(lazadaProductId),10);

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
    public void TC03_UpdateProductToGoSell(int productNumber, boolean hasVariation, ProductThirdPartyStatus status) {
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
//                {ProductThirdPartyStatus.SYNC, true},
//                {ProductThirdPartyStatus.LINK, false},
                {ProductThirdPartyStatus.LINK, true},
        };
    }

    @SneakyThrows
    @Test(dataProvider = "dataDownloadTest", priority = 4)
    public void TC04_DownloadOneProduct(ProductThirdPartyStatus status, boolean hasVariation) {
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
                {ProductThirdPartyStatus.SYNC, false, 2},
//                {ProductThirdPartyStatus.SYNC, true, 1},
//                {ProductThirdPartyStatus.LINK, false, 3},
                {ProductThirdPartyStatus.LINK, true, 2},
        };
    }
    @Test(dataProvider = "dataForUnlinkTest", priority = 5)
    @SneakyThrows
    public void TC05_UnlinkProductTest(ProductThirdPartyStatus status, boolean hasVariation, int productNumber) {
        logger.info("Test for case: ProductThirdPartyStatus is %s - hasVariation is %s".formatted(status,hasVariation));
        //Get lazada product
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        List<Long> syncedLinkedProduct = lazadaProductPage.getSyncedLinkedProduct(hasVariation, productNumber, status);
        logger.info("Lazada product id: {}", syncedLinkedProduct);

        //Get branch and productId
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        List<String> branchProductList = syncedLinkedProduct.stream().map(i ->apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(i)).collect(Collectors.toList());
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection)
                .getLazadaInventoryMappingExceptProduct(branchProductList, loginDashboard.getStoreID());

        //call API unlink
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        apiLazadaProducts.unlinkLazadaProduct(syncedLinkedProduct);

        //Verify
        waitManyTime(productNumber);
        branchProductList.forEach(i -> {
            long productId = Long.parseLong(i.split("-")[1]);
            List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
            List<InventoryMapping> inventoryMappingExpected = new ArrayList<>();
            List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
            List<SQLGetInventoryEvent.InventoryEvent> inventoryEventExpected = new ArrayList<>();
            lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingExpected);
            lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventExpected);
        });

        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(branchProductList, loginDashboard.getStoreID(), mappingNotChangeBefore);
    }
    @DataProvider
    public Object[][] dataCreateProductToLazada(){
        return new Object[][]{
                {false},
                {true}
        };
    }
    @Test(dataProvider = "dataCreateProductToLazada")
    public void TC06_CreateProductToLazada(boolean hasVariation){
        long productId;

        if(hasVariation)
            productId = new APICreateProduct(loginInformation).createProductTo3rdPartyThenRetrieveId(4,10);
        else productId = new APICreateProduct(loginInformation).createProductTo3rdPartyThenRetrieveId(0,10);
        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));

        //Action on UI
        new ProductPage(driver).navigateProductDetail(productId).clickLazadaIcon()
                .createProductToLazada().verifyCreateToLazadaSuccess();
        //Verify
        waitManyTime(2);
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        lazadaProductPage.verifyInventoryEvent(branchId, productId, EventAction.GS_LAZADA_SYNC_ITEM_EVENT, currentDate);
        lazadaProductPage.verifyInventoryMapping(branchId, productId);

        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of(branchId+"-"+productId), loginDashboard.getStoreID(), mappingNotChangeBefore);
    }



    @DataProvider
    public Object[][] dataUpdateStock(){
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, true, "ClearStockInBulk", true},
                {ProductThirdPartyStatus.SYNC, true, "UpdateStockInDetail", true},
//                {ProductThirdPartyStatus.SYNC, true, "UpdateStockInBulk", true},
//                {ProductThirdPartyStatus.LINK, true, "ClearStockInBulk", true},
//                {ProductThirdPartyStatus.LINK, true, "UpdateStockInDetail", true},
//                {ProductThirdPartyStatus.LINK, true, "UpdateStockInBulk", true},
//                {ProductThirdPartyStatus.SYNC, false, "ClearStockInBulk", true},
//                {ProductThirdPartyStatus.SYNC, false, "UpdateStockInDetail", true},
//                {ProductThirdPartyStatus.SYNC, false, "UpdateStockInBulk", true},
//                {ProductThirdPartyStatus.LINK, false, "ClearStockInBulk",true},
//                {ProductThirdPartyStatus.LINK, false, "ClearStockInBulk",true},
//                {ProductThirdPartyStatus.LINK, false, "UpdateStockInDetail", true},
//                {ProductThirdPartyStatus.LINK, false, "UpdateStockInBulk", true},
//                {ProductThirdPartyStatus.SYNC, true, "UpdateStockInDetail", false},
//                {ProductThirdPartyStatus.SYNC, false, "UpdateStockInBulk", false},


        };
    }
    @Test(dataProvider = "dataUpdateStock")
    @SneakyThrows
    public void TC07_UpdateStock(ProductThirdPartyStatus status, boolean hasVariation, String action, boolean isChangeStock){
        actionThenCheckChangeStockEvent(status, hasVariation, (productId, modelIdList, currentDate, event)->{
            switch (action){
                case "UpdateStockInDetail"->{
                    new ProductPage(driver).getLoginInformation(loginInformation)
                            .navigateToUpdateProductPage(productId)
                            .updateStock(hasVariation,isChangeStock);
                }
                case "UpdateStockInBulk"
                        ->{
                    String branchName = new BranchManagement(loginInformation).getBranchNameById(List.of(branchId)).get(0);
                    new ProductManagementPage(driver).getLoginInformation(loginInformation)
                            .navigateToProductManagementPage()
                            .excuteSearch(ProductManagementPage.SearchType.BARCODE, productId.toString())
                            .updateStockAction(branchName, isChangeStock);
                }
                case "ClearStockInBulk" ->{
                    new ProductManagementPage(driver).getLoginInformation(loginInformation)
                            .navigateToProductManagementPage()
                            .excuteSearch(ProductManagementPage.SearchType.BARCODE, productId.toString())
                            .clearStockAction();
                }
                default -> {
                    try {
                        throw new Exception("Action not found.");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(isChangeStock|| action.equalsIgnoreCase("ClearStockInBulk")) event.set(EventAction.GS_SET_PRODUCT_STOCK);
            if(hasVariation) waitManyTime(1);
        });
    }
    @DataProvider
    public Object[][] dataCreateCancelPOSOrder(){
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, true},
//                {ProductThirdPartyStatus.LINK, true},
//                {ProductThirdPartyStatus.SYNC, false},
//                {ProductThirdPartyStatus.LINK, false}
        };
    }
    @Test(dataProvider = "dataCreateCancelPOSOrder")
    public void TC08_CreateAndCancelOrderPOS(ProductThirdPartyStatus status, boolean hasVariation){
        //Get productId
        long lazadaProductId = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(hasVariation,1,status).get(0);
        String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        long productId = Long.parseLong(branchProduct.split("-")[1]);

        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());

        //Call API to create pos order
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        APICreateOrderPOS.APICreatePOSCondition condition = new APICreateOrderPOS.APICreatePOSCondition();
        condition.setProductInfoList(List.of(new APIProductDetail(loginInformation).getInfo((int)productId)));
        condition.setBranchId(branchId);
        int orderId = new APICreateOrderPOS(loginInformation).getInfo(condition).createPOSOrder();
        waitManyTime(1);

        //Verify event
        List<String> modelList= new ArrayList<>();
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        if(hasVariation){
            OrderDetailInfo detailInfo = new APIOrderDetail(loginInformation).getOrderDetail(orderId);
            List<ItemOrderInfo> itemOrderInfos = detailInfo.getItems();
            modelList = itemOrderInfos.stream().map(i ->String.valueOf(i.getVariationId())).collect(Collectors.toList());
            for(String modelId: modelList){
                lazadaProductPage.verifyInventoryEvent(branchId, productId, modelId, GS_CHANGE_PRODUCT_STOCK, currentDate);
            }
        }else lazadaProductPage.verifyInventoryEvent(branchId, productId, GS_CHANGE_PRODUCT_STOCK, currentDate);

        //Verify the all mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of("0"), loginDashboard.getStoreID(), mappingNotChangeBefore);

        //Call API cancel order
        currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));

        new OrderAPI(loginInformation).cancelOrder(orderId);
        //Verify event
        waitManyTime(1);
        if(modelList.isEmpty())
            lazadaProductPage.verifyInventoryEvent(branchId, productId, GS_CHANGE_PRODUCT_STOCK, currentDate);
        else for ( String modelId : modelList){
            lazadaProductPage.verifyInventoryEvent(branchId, productId, modelId, GS_CHANGE_PRODUCT_STOCK, currentDate);
        }
        //Verify the all mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of("0"), loginDashboard.getStoreID(), mappingNotChangeBefore);

    }
    @DataProvider
    public Object[][] dataReturnOrder(){
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, true},
//                {ProductThirdPartyStatus.LINK, true},
//                {ProductThirdPartyStatus.SYNC, false},
//                {ProductThirdPartyStatus.LINK, false}
        };
    }
    @Test(dataProvider = "dataReturnOrder")
    public void TC09_ReturnOrder(ProductThirdPartyStatus status, boolean hasVariation){
        actionThenCheckChangeStockEvent(status, hasVariation, (productId,modelIdList, currentDate, event) ->{
            //Call API create order
            APICreateOrderPOS.APICreatePOSCondition condition = new APICreateOrderPOS.APICreatePOSCondition();
            condition.setProductInfoList(List.of(new APIProductDetail(loginInformation).getInfo(productId)));
            condition.setBranchId(branchId);
            condition.setHasDelivery(false);
            int orderId = new APICreateOrderPOS(loginInformation).getInfo(condition).createPOSOrder();
            waitManyTime(1);
            if(hasVariation) {
                OrderDetailInfo detailInfo = new APIOrderDetail(loginInformation).getOrderDetail(orderId);
                List<ItemOrderInfo> itemOrderInfos = detailInfo.getItems();
                modelIdList.set(itemOrderInfos.stream().map(i -> String.valueOf(i.getVariationId())).collect(Collectors.toList()));
            }
            //Action on to update stock
            currentDate.set(ZonedDateTime.now(ZoneOffset.UTC));
            new CreateReturnOrderPage(driver).navigateToCreateOrder(orderId).createReturnOrder();
        });
    }
    @Test(dataProvider = "dataReturnOrder")
    public void TC10_CreateTransferProduct(ProductThirdPartyStatus status, boolean hasVariation){
        //Get productId
        long lazadaProductId = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(hasVariation,1,status).get(0);
        String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        int productId = Integer.parseInt(branchProduct.split("-")[1]);
        List<String> modelIdList = new ArrayList<>();
        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of("0"), loginDashboard.getStoreID());

        //Update stock
        new ProductPage(driver).getLoginInformation(loginInformation)
                .navigateToUpdateProductPage(productId)
                .updateStock(hasVariation, true);

        //Excute action
        List<APIProductDetailV2.ProductInfoV2> productInfoList = getProductInfoList(1);
        if (hasVariation) {
            modelIdList.add(String.valueOf(productInfoList.getFirst().getVariationModelList().getFirst()));
        }

        //Create transfer with origin branch is connected.
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        transferId_OriginBranchConnectedLazada = getTransferIdOrCreateTransfer(true, productInfoList);

        //Verify event
        waitManyTime(1);
        LazadaProducts lazadaProductPage = new LazadaProducts(driver, loginInformation);
        if(modelIdList.isEmpty())
            lazadaProductPage.verifyInventoryEvent(branchId, productId, GS_CHANGE_PRODUCT_STOCK, currentDate.toString());
        else for (String modelId: modelIdList){
            lazadaProductPage.verifyInventoryEvent(branchId, productId, modelId, GS_CHANGE_PRODUCT_STOCK, currentDate.toString());
        }
        //Verify the all mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of("0"), loginDashboard.getStoreID(), mappingNotChangeBefore);

        //Create transfer with destination branch is connected
        currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        transferId_DestinationConnectedLazada = getTransferIdOrCreateTransfer(false, productInfoList);

        //Verify not create event
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate);
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new ArrayList<>();
        lazadaProductPage.verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);


//        //transfer from connected branch
//        actionThenCheckChangeStockEvent(status, hasVariation, (productId, modelIdList, currentDate, event) -> {
//            //Update stock
//            new ProductPage(driver).getLoginInformation(loginInformation)
//                    .navigateToUpdateProductPage(productId)
//                    .updateStock(hasVariation, true);
//            //Get product detail
//            List<APIProductDetailV2.ProductInfoV2> productInfoList = new ArrayList<>();
//            APIProductDetailV2.ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);
//            productInfoList.add(productInfo);
//            if (hasVariation) {
//                modelIdList.set(List.of(String.valueOf(productInfo.getVariationModelList().getFirst())));
//            }
//            String originAndDestinationBranch = new TransferPage(loginInformation).getOriginDestinationBranchName(branchId);
//            System.out.println("originAndDestinationBranch: " + originAndDestinationBranch);
//            // create transfer
//            currentDate.set(ZonedDateTime.now(ZoneOffset.UTC));
//            new TransferPage(driver).createTransfer(originAndDestinationBranch.split("-")[0], originAndDestinationBranch.split("-")[1], productInfoList);
//        });
//        transferId = Integer.parseInt(new TransferPage(driver).getPageTitle());
//        System.out.println("Transfer id: " + transferId);
//        //transfer to connected branch
//        actionThenCheckChangeStockEvent(status, hasVariation, (productId, modelIdList, currentDate, event) -> {
//            List<APIProductDetailV2.ProductInfoV2> productInfoList = new ArrayList<>();
//            //Get product detail
//            APIProductDetailV2.ProductInfoV2 productInfo = new APIProductDetailV2(loginInformation).getInfo(productId);
//            productInfoList.add(productInfo);
//            if (hasVariation) {
//                modelIdList.set(List.of(String.valueOf(productInfo.getVariationModelList().getFirst())));
//            }
//            String originAndDestinationBranch = new TransferPage(loginInformation).getOriginDestinationBranchName(branchId);
//            // create transfer
//            TransferPage transferPage = new TransferPage(driver);
//            transferPage.createTransfer(originAndDestinationBranch.split("-")[1], originAndDestinationBranch.split("-")[0],productInfoList);
//            transferPage.clickShipGoodsBtn();
//            currentDate.set(ZonedDateTime.now(ZoneOffset.UTC));
//            transferPage.clickReceiveGoodsBtn();
//        });
    }
    public List<APIProductDetailV2.ProductInfoV2> getProductInfoList(int quantity){
        List<Long> lazadaProductIdList = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(false,quantity,ProductThirdPartyStatus.SYNC);
        List<APIProductDetailV2.ProductInfoV2> productInfoList = new ArrayList<>();
        lazadaProductIdList.forEach(lazadaProductId ->{
            String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
            int productId = Integer.parseInt(branchProduct.split("-")[1]);
            productInfoList.add(new APIProductDetailV2(loginInformation).getInfo(productId));
        });
        return productInfoList;
    }
    public int createTransfer(boolean originBranchConnectedLazada, List<APIProductDetailV2.ProductInfoV2> productInfoList){
        String originAndDestinationBranch = new TransferPage(loginInformation).getOriginDestinationBranchName(branchId);
        System.out.println("originAndDestinationBranch: " + originAndDestinationBranch);
        //Create transfer with origin branch is connected.
        TransferPage transferPage = new TransferPage(driver);
        if (originBranchConnectedLazada) {
            transferPage.createTransfer(originAndDestinationBranch.split("-")[0],
                    originAndDestinationBranch.split("-")[1], List.of(productInfoList.get(0)));
        } else transferPage.createTransfer(originAndDestinationBranch.split("-")[1],
                originAndDestinationBranch.split("-")[1], List.of(productInfoList.get(0)));
        return Integer.parseInt(new TransferPage(driver).getPageTitle());
    }
    public int getTransferIdOrCreateTransfer(boolean originBranchConnectedLazada, List<APIProductDetailV2.ProductInfoV2> productInfoList){
        int transferId = transferId_OriginBranchConnectedLazada;
        if(!originBranchConnectedLazada) transferId = transferId_DestinationConnectedLazada;
        if(transferId==0) {
            transferId = createTransfer(originBranchConnectedLazada, productInfoList);
        }else {
            APITransferDetail.TransferDetailInfo transferDetailInfo = new APITransferDetail(loginInformation).getTransferDetail(transferId);
            if(!transferDetailInfo.getStatus().equals("READY_FOR_TRANSPORT")){
                transferId = createTransfer(originBranchConnectedLazada, productInfoList);
            }
        }
        return transferId;
    }
    @Test
    public void TC11_EditProductTransfer(String action, boolean originBranchConnectedLazada){
        //Get product to transfer if don't have transferId before
        List<APIProductDetailV2.ProductInfoV2> productInfoList = getProductInfoList(2);

        //Create transfer if don't have transfer before
        int transferId = getTransferIdOrCreateTransfer(originBranchConnectedLazada,productInfoList);

        // dev remove item before call update so need to get event when remove product.
        TransferPage transferPage = new TransferPage(driver);
        List<SQLGetInventoryEvent.InventoryEvent> eventExpected = new ArrayList<>();
        if(originBranchConnectedLazada)
            eventExpected = new APITransferDetail(loginInformation).getEventFromTransferDetail(transferId);
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        //action to update
        switch (action){
            case "updateQuantity"-> transferPage.navigateAndUpdateTransferredQuantity(transferId);
            case "addMoreProduct" ->{
                transferPage.searchAndSelectProduct(List.of(productInfoList.get(1)));
                transferPage.clickSaveBtn();
                new HomePage(driver).verifySuccessToastMessageShow();
            }
            case "removeAProduct" ->{
                new APIEditTransfer(loginInformation, transferId).removeAProductInTransfer();
            }
        }
        // add all event when update to event expected list
        if(originBranchConnectedLazada)
            eventExpected.addAll(new APITransferDetail(loginInformation).getEventFromTransferDetail(transferId));

        //Get inventory event actual.
        List<Integer> productList = new APITransferDetail(loginInformation).getTransferDetail(transferId).getItems().stream().map(APITransferDetail.Item::getItemId).toList();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new ArrayList<>();
        productList.forEach(productId -> {
            inventoryEventsActual.addAll(new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate));
        });

        //Verify event
        new LazadaProducts(driver).verifyInventoryEvent(inventoryEventsActual, eventExpected);

    }
    @SneakyThrows
    @Test
    public void TC12_EditDestinationBranchTransfer(boolean destinationBranchConnectLazada){
        //Get product to transfer if don't have transferId before
        APIProductDetailV2.ProductInfoV2 productInfo = getProductInfoList(1).get(0);

        //Create transfer if don't have transfer before
        int transferId = getTransferIdOrCreateTransfer(false,List.of(productInfo));

        //Get available branch to edit branch
        APITransferDetail.TransferDetailInfo transferDetailInfo = new APITransferDetail(loginInformation).getTransferDetail(transferId);
        BranchManagement branchManagement = new BranchManagement(loginInformation);
        List<String> eliminateBranch = branchManagement.getBranchNameById(List.of(transferDetailInfo.getOriginBranchId(),transferDetailInfo.getDestinationBranchId()));
        List<String> activeBranchList = branchManagement.getInfo().getActiveBranches();
        if(activeBranchList.size()>eliminateBranch.size()) activeBranchList.removeAll(eliminateBranch);
        else throw new Exception("No branch to edit.");

        //edit branch that not connected lazada
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        new TransferPage(driver).navigateAndEditDestinationBranch(transferId,activeBranchList.getFirst());

        //Get inventory event actual.
        List<Integer> productList = transferDetailInfo.getItems().stream().map(APITransferDetail.Item::getItemId).toList();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new ArrayList<>();
        productList.forEach(productId -> {
            inventoryEventsActual.addAll(new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate));
        });
        List<SQLGetInventoryEvent.InventoryEvent> eventExpected = new ArrayList<>();

        //Verify event
        new LazadaProducts(driver).verifyInventoryEvent(inventoryEventsActual, eventExpected);
    }
    @Test
    public void TC13_CompleteTransfer(boolean originBranchConnectedLazada){
        //Get product to transfer if don't have transferId before
        APIProductDetailV2.ProductInfoV2 productInfo = getProductInfoList(1).get(0);

        //Create transfer if don't have transfer before
        int transferId = getTransferIdOrCreateTransfer(originBranchConnectedLazada,List.of(productInfo));

        //get event expected
        List<SQLGetInventoryEvent.InventoryEvent> eventExpected = new ArrayList<>();
        if(!originBranchConnectedLazada)
        eventExpected = new APITransferDetail(loginInformation).getEventFromTransferDetail(transferId);

        //Complete transfer
        TransferPage transferPage = new TransferPage(driver);
        transferPage.navigateToTransferDetailPage(transferId);
        transferPage.clickShipGoodsBtn();
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        transferPage.clickReceiveGoodsBtn();

        //Get inventory event actual.
        List<Integer> productList = new APITransferDetail(loginInformation).getTransferDetail(transferId).getItems().stream().map(APITransferDetail.Item::getItemId).toList();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new ArrayList<>();
        productList.forEach(productId -> {
            inventoryEventsActual.addAll(new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate));
        });

        //Verify event
        new LazadaProducts(driver).verifyInventoryEvent(inventoryEventsActual, eventExpected);
    }
    @Test
    public void TC14_CancelTransfer(boolean originBranchConnectedLazada){
        //Get product to transfer if don't have transferId before
        APIProductDetailV2.ProductInfoV2 productInfo = getProductInfoList(1).get(0);

        //Create transfer if don't have transfer before
        int transferId = getTransferIdOrCreateTransfer(originBranchConnectedLazada,List.of(productInfo));

        //get event expected
        List<SQLGetInventoryEvent.InventoryEvent> eventExpected = new ArrayList<>();
        if(originBranchConnectedLazada)
            eventExpected = new APITransferDetail(loginInformation).getEventFromTransferDetail(transferId);

        //cancel transfer
        String currentDate = String.valueOf(ZonedDateTime.now(ZoneOffset.UTC));
        TransferPage transferPage = new TransferPage(driver);
        transferPage.navigateAndCancelTransfer(transferId);

        //Get inventory event actual.
        List<Integer> productList = new APITransferDetail(loginInformation).getTransferDetail(transferId).getItems().stream().map(APITransferDetail.Item::getItemId).toList();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new ArrayList<>();
        productList.forEach(productId -> {
            inventoryEventsActual.addAll(new SQLGetInventoryEvent(connection).inventoryEventListByItem(branchId, productId, currentDate));
        });

        //Verify event
        new LazadaProducts(driver).verifyInventoryEvent(inventoryEventsActual, eventExpected);
    }
    @DataProvider
    public Object[][] dataDeleteAndAddVariation(){
        return new Object[][]{
//                {ProductThirdPartyStatus.SYNC, "delete"},
//                {ProductThirdPartyStatus.LINK, "delete"},
//                {ProductThirdPartyStatus.SYNC, "add"},
                {ProductThirdPartyStatus.LINK, "add"}
        };
    }
    @Test(dataProvider = "dataDeleteAndAddVariation")
    public void TC_DeleteOrAddVariationToLazada(ProductThirdPartyStatus status, String action){
        //Get productId
        long lazadaProductId = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(true,1,status).get(0);
        String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        long productId = Long.parseLong(branchProduct.split("-")[1]);

        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of(String.valueOf(branchProduct)), loginDashboard.getStoreID());

        //Action on UI
        if(action.equals("add")){
//            APIGetProductDetail.ProductInformation productInfo = new APIGetProductDetail(loginInformation).getProductInformation((int) productId);
//            new APIUpdateProduct(loginInformation).updateProductVariations(productInfo, 4);
//            new ProductPage(driver).navigateProductDetail(productId);
            new ProductPage(driver).navigateToAddMoreVariationValue((int)productId);
        }else {
            new ProductPage(driver).navigateToProductAndDeleteAllVariation((int)productId);
        }
        //Verify mapping after delete or add variation
        LazadaProducts lazadaProductPage = new LazadaProducts(driver,loginInformation);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
        List<InventoryMapping> inventoryMappingExpected = new ArrayList<>();
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingExpected);

        //Action UI: update to Lazada
        new ProductPage(driver).clickLazadaIcon()
                .updateProductToLazada()
                .verifyUpdateToLazadaSuccess();

        //Verify mapping of this item after update to Lazada
        lazadaProductPage.verifyInventoryMapping(branchId, productId);

        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of(branchProduct), loginDashboard.getStoreID(), mappingNotChangeBefore);

    }
    @DataProvider
    public Object[][] dataDeleteProduct(){
        return new Object[][]{
                {ProductThirdPartyStatus.SYNC, true},
//                {ProductThirdPartyStatus.LINK, true},
//                {ProductThirdPartyStatus.SYNC, false},
//                {ProductThirdPartyStatus.LINK, false}
        };
    }
    @Test(dataProvider = "dataDeleteProduct")
    public void deleteLazadaProductFromGS(ProductThirdPartyStatus status, boolean hasVariation){
        //Get productId
        long lazadaProductId = new LazadaProducts(driver, loginInformation).getSyncedLinkedProduct(hasVariation, 1,status).get(0);
        String branchProduct = new APILazadaProducts(loginInformation).getBranchAndProductMappingWithLazadaProduct(lazadaProductId);
        long productId = Long.parseLong(branchProduct.split("-")[1]);

        //Get mapping list of store before create to lazada
        List<InventoryMapping> mappingNotChangeBefore = new SQLGetInventoryMapping(connection).getLazadaInventoryMappingExceptProduct(List.of(branchProduct), loginDashboard.getStoreID());

        //Action on UI
        new APIEditProduct(loginInformation).deleteProduct((int)productId);
        waitManyTime(1);

        //Verify mapping after delete product
        LazadaProducts lazadaProductPage = new LazadaProducts(driver,loginInformation);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(connection).getLazadaInventoryMapping(branchId, productId);
        List<InventoryMapping> inventoryMappingExpected = new ArrayList<>();
        lazadaProductPage.verifyInventoryMapping(inventoryMappingsActual, inventoryMappingExpected);

        //Verify the others mapping not change
        lazadaProductPage.verifyInventoryMappingExceptProductList(List.of(branchProduct), loginDashboard.getStoreID(), mappingNotChangeBefore);
    }

}
