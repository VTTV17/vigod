package web.Dashboard.sales_channels.lazada.lazada_products;

import api.Seller.products.all_products.APICreateProduct;
import api.Seller.sale_channel.lazada.APICreateUpdateToGoSell;
import api.Seller.sale_channel.lazada.APILazadaAccount;
import api.Seller.sale_channel.lazada.APILazadaProducts;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import sql.SQLGetInventoryEvent;
import sql.SQLGetInventoryMapping;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.database.InitConnection;
import utilities.enums.EventAction;
import utilities.enums.ProductThirdPartyStatus;
import utilities.model.dashboard.products.inventory.InventoryMapping;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static utilities.links.Links.DOMAIN;

public class LazadaProducts extends LazadaProductElements {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LazadaProducts.class);
    LoginInformation loginInformation;

    public LazadaProducts(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    public LazadaProducts(WebDriver driver, LoginInformation loginInformation) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.loginInformation = loginInformation;
    }

    public LazadaProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    }

    public LazadaProducts navigateByURL() {
        String url = DOMAIN + "/channel/lazada/product";
        commonAction.navigateToURL(url);
        return this;
    }

    public LazadaProducts selectProduct(List<Long> lazadaProductId) {
        lazadaProductId.forEach(i -> {
            commonAction.click(loc_ckbSelectProduct(i));
            logger.info("Select lazada product id: " + i);
        });
        return this;
    }

    public LazadaProducts clickOnSelectAction() {
        commonAction.click(loc_btnSelectAction);
        logger.info("Click on Select Action button.");
        return this;
    }

    public LazadaProducts clickOnCreateToGoSell() {
        commonAction.click(loc_btnCreateProductToGoSell);
        logger.info("Click on Create product to GoSell");
        return this;
    }

    public LazadaProducts clickOnUpdateToGoSell() {
        commonAction.click(loc_btnUpdateProductToGoSell);
        logger.info("Click on Update product to GoSell.");
        return this;
    }

    public LazadaProducts waitToFetchProduct() {
//        commonAction.refreshPage();
        commonAction.waitElementVisible(loc_lblFetchProductStatus);
        try {
            commonAction.waitInvisibilityOfElementLocated(loc_lblFetchProductStatus);
        } catch (Exception e) {
            logger.info(e.getMessage());
            commonAction.refreshPage();
            commonAction.waitInvisibilityOfElementLocated(loc_lblFetchProductStatus);
        }
        return this;
    }

    public LazadaProducts createProductToGoSell(List<Long> lazadaProductId) {
        selectProduct(lazadaProductId);
        clickOnSelectAction();
        clickOnCreateToGoSell();
        new ConfirmationDialog(driver).clickOKBtn_V2();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        waitToFetchProduct();
        return this;
    }

    public LazadaProducts updateProductToGoSell(List<Long> lazadaProductId) {
        selectProduct(lazadaProductId);
        clickOnSelectAction();
        clickOnUpdateToGoSell();
        new ConfirmationDialog(driver).clickOKBtn_V2();
        new HomePage(driver).waitTillLoadingDotsDisappear();
        waitToFetchProduct();
        return this;
    }

    public LazadaProducts clickDownloadProduct(String lazadaProductId) {
        commonAction.click(loc_icnDownloadProduct(lazadaProductId));
        new HomePage(driver).waitTillLoadingDotsDisappear();
        return this;
    }

    @SneakyThrows
    public LazadaProducts verifyInventoryMapping(int branchId, long productId) {
        List<InventoryMapping> inventoryMappingsExpected = new APILazadaProducts(loginInformation).getLazadaInventoryMappingInfo(branchId, productId);
        List<InventoryMapping> inventoryMappingsActual = new SQLGetInventoryMapping(new InitConnection().createConnection()).getLazadaInventoryMapping(branchId, productId);
        verifyInventoryMapping(inventoryMappingsActual, inventoryMappingsExpected);
        logger.info("Verify inventory mapping for branch: %s, productId: %s".formatted(branchId, productId));
        return this;
    }

    @SneakyThrows
    public LazadaProducts verifyInventoryEvent(int branchId, long productId, EventAction eventAction, String startTime) {
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new APILazadaProducts(loginInformation).getInventoryEventInfo(branchId, productId, eventAction);
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(new InitConnection().createConnection()).inventoryEventListByItem(branchId, productId, startTime);
        verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
        logger.info("Vefify inventory event for branch: %s, produc: %s, event action: %s, start time: %s".formatted(branchId, productId, eventAction, startTime));
        return this;
    }
    @SneakyThrows
    public LazadaProducts verifyInventoryEvent(int branchId, long productId, String modelId, EventAction eventAction, String startTime) {
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected = new APILazadaProducts(loginInformation).getInventoryEventInfo(branchId, productId, eventAction)
                .stream().filter(i ->i.getModel_id().equals(modelId)).collect(Collectors.toList());
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual = new SQLGetInventoryEvent(new InitConnection().createConnection()).inventoryEventListByItem(branchId, productId, startTime);
        verifyInventoryEvent(inventoryEventsActual, inventoryEventsExpected);
        logger.info("Vefify inventory event for branch: %s, produc: %s, model: %s, event action: %s, start time: %s".formatted(branchId, productId, modelId, eventAction, startTime));
        return this;
    }
    @SneakyThrows
    public LazadaProducts verifyInventoryMapping(List<InventoryMapping> inventoryMappingsActual, List<InventoryMapping> inventoryMappingsExpected) {
        inventoryMappingsExpected.sort(Comparator.comparing(InventoryMapping::getInventoryId)
                .thenComparing(InventoryMapping::getChannel));
        inventoryMappingsActual.sort(Comparator.comparing(InventoryMapping::getInventoryId)
                .thenComparing(InventoryMapping::getChannel));
        logger.info("inventoryMappingsActual: " + inventoryMappingsActual);
        logger.info("inventoryMappingsExpected: " + inventoryMappingsExpected);
        Assert.assertEquals(inventoryMappingsActual, inventoryMappingsExpected, "[Failed] Check inventory mapping.");
        logger.info("Verify inventory mapping.");
        return this;
    }

    @SneakyThrows
    public LazadaProducts verifyInventoryEvent(List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsActual, List<SQLGetInventoryEvent.InventoryEvent> inventoryEventsExpected) {
        inventoryEventsExpected.sort(Comparator.comparing(SQLGetInventoryEvent.InventoryEvent::getBranch_id)
                .thenComparing(SQLGetInventoryEvent.InventoryEvent::getItem_id)
                .thenComparing(SQLGetInventoryEvent.InventoryEvent::getModel_id));
        inventoryEventsActual.sort(Comparator.comparing(SQLGetInventoryEvent.InventoryEvent::getBranch_id)
                .thenComparing(SQLGetInventoryEvent.InventoryEvent::getItem_id)
                .thenComparing(SQLGetInventoryEvent.InventoryEvent::getModel_id));
        Assert.assertEquals(inventoryEventsActual, inventoryEventsExpected, "[Failed] Check inventory event. inventoryEventsActual: %s \ninventoryEventsExpected: %s".formatted(inventoryEventsActual, inventoryEventsExpected));
        logger.info("Vefify inventory event");
        return this;
    }

    public List<Long> getUnLinkProduct(boolean hasVariation) {
        List<Long> allLazadaIdUnlink = new APILazadaProducts(loginInformation).getLazadaProductIdWithStatus(ProductThirdPartyStatus.UNLINK, hasVariation);
        if (allLazadaIdUnlink.size() < 1) {
            logger.info("Wait to unlink all product");
            new APILazadaProducts(loginInformation).unlinkAllLazadaProduct();
            logger.info("Unlinked all product.");
        }
        commonAction.sleepInMiliSecond(2000,"Wait update database");
        allLazadaIdUnlink = new APILazadaProducts(loginInformation).getLazadaProductIdWithStatus(ProductThirdPartyStatus.UNLINK, hasVariation);
        return allLazadaIdUnlink;
    }

    public List<Long> getSyncedLinkedProduct(boolean hasVariation, int numberOfProduct, ProductThirdPartyStatus status) {
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        List<Long> allLazadaId = apiLazadaProducts.getLazadaProductIdWithStatus(status, hasVariation);
        String branchAndLazadaConnected = new APILazadaAccount(loginInformation).getBranchIDAndLazadaShopConnected();
        int branchId = Integer.parseInt(branchAndLazadaConnected.split("-")[0]);
        String lazadShopId = branchAndLazadaConnected.split("-")[1];
        if (allLazadaId.size() < numberOfProduct) {
            List<Long> unlinkProductId = apiLazadaProducts.getLazadaProductIdWithStatus(ProductThirdPartyStatus.UNLINK, hasVariation).stream().limit(numberOfProduct).collect(Collectors.toList());
            if(unlinkProductId.isEmpty()){
                List<Long> lazadaIdNotHasStatus = apiLazadaProducts.getLazadaProductIdNotStatus(status, hasVariation).stream().limit(numberOfProduct).collect(Collectors.toList());
                apiLazadaProducts.unlinkLazadaProduct(lazadaIdNotHasStatus);
                unlinkProductId = new APILazadaProducts(loginInformation).getLazadaProductIdWithStatus(ProductThirdPartyStatus.UNLINK, hasVariation).stream().limit(numberOfProduct).collect(Collectors.toList());
            }
            if (status.equals(ProductThirdPartyStatus.SYNC))
                //if status = SYNC, then create to gosell
                new APICreateUpdateToGoSell(loginInformation).createLazadaToGoSell(lazadShopId, unlinkProductId);
            else {
                //else to link product to Gosell
                unlinkProductId.forEach(i -> {
                    long productId = new APICreateProduct(loginInformation).createProductTo3rdPartyThenRetrieveId(apiLazadaProducts.getVariationNumberOfLazadaProduct(i),10);
                    new APILazadaProducts(loginInformation).linkProduct(branchId, productId, i);
                });
            }
            commonAction.sleepInMiliSecond(5000, "Hard wait for data insert into database.");
            return unlinkProductId;
        } else return allLazadaId.stream().limit(numberOfProduct).collect(Collectors.toList());
    }

    @SneakyThrows
    public void verifyInventoryMappingExceptProductList(List<String> branchProductExcept, int storeId, List<InventoryMapping> mappingBefore) {
        logger.info("Check inventory mapping of remain product (that are not impacted)");
        List<InventoryMapping> mappingAfter = new SQLGetInventoryMapping(new InitConnection().createConnection()).getLazadaInventoryMappingExceptProduct(branchProductExcept, storeId);
        verifyInventoryMapping(mappingAfter,mappingBefore);
    }

    public List<String> getBranchProductRelevantLazadaProduct(List<Long> lazadaProduct) {
        List<String> branchProductList = new ArrayList<>();
        APILazadaProducts apiLazadaProducts = new APILazadaProducts(loginInformation);
        lazadaProduct.forEach(i -> {
            logger.info("Lazada product id: {}", i);
            String branchProduct = apiLazadaProducts.getBranchAndProductMappingWithLazadaProduct(i);
            branchProductList.add(branchProduct);
        });
        return branchProductList;
    }
}
