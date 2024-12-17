package api.Seller.sale_channel.lazada;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import api.Seller.products.all_products.APIProductDetailV2;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.response.Response;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.SQLGetInventoryEvent;
import utilities.api.API;
import utilities.enums.EventAction;
import utilities.enums.ProductThirdPartyStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.inventory.InventoryMapping;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.stream.Collectors;

public class APILazadaProducts {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    List<LazadaProduct> lazadaProductList;
    String GET_LAZADA_PRODUCT_PATH = "/lazadaservices/api/items/bc-store/%s?keyword=&page=0&size=100&getBcItemName=true";
    String LINK_LAZADA_PATH = "/lazadaservices/api/items/link";
    String UNLINK_LAZADA_PATH = "/lazadaservices/api/items/%s/unlink/%s?ids=%s";
    Logger logger = LogManager.getLogger(APILazadaProducts.class);

    public APILazadaProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        Response response = callAPIGetLazadaProduct();
        lazadaProductList = Arrays.asList(response.as(LazadaProduct[].class));
    }

    //    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class LazadaProduct {
        private int id;
        private long lazadaItemId;
        private int branchId;
        private long lazadaShopId;
        private String lazadaShopName;
        private String thumbnail;
        private String lazadaItemName;
        private String gosellStatus;
        private String bcItemName;
        private long bcItemId;
        private String lastSyncDate;
        private String updateTime;
        private List<Variation> variations;
        private boolean hasVariation;
        private int stock;
        private int bcStoreId;
        private boolean hasLinkErrorStatus;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @Data
    public static class Variation {
        private long id;
        private String skuId;
        private String sellerSku;
        private String shopSku;
        private String attributes;
        private String bcItemModelId;
        private int stock;
        private long productId;
        private String name;
    }

    public Response callAPIGetLazadaProduct() {
        Response response = api.get(GET_LAZADA_PRODUCT_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }

    /**
     * Only has data when product has linked/synced
     */
    @SneakyThrows
    public List<InventoryMapping> getLazadaInventoryMappingInfo(int branchId, long productId) {
        List<InventoryMapping> inventoryMappingList = new ArrayList<>();
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.bcItemId == productId && i.branchId == branchId)
                .findAny().orElseThrow(() -> new Exception("ProductId %s not found!".formatted(productId)));
        long itemId = lazadaProduct.getBcItemId();
        long lazdaItemId = lazadaProduct.getLazadaItemId();
        if (lazadaProduct.hasVariation) {
            List<Variation> variationList = lazadaProduct.getVariations();
            variationList.forEach(variation -> {
                String itemModelId = variation.getBcItemModelId();
                String inventoryId = branchId + "-" + itemId + "-" + itemModelId;
                inventoryMappingList.add(new InventoryMapping(branchId, itemId, itemModelId, APIAllOrders.Channel.GOSELL, inventoryId));
                inventoryMappingList.add(new InventoryMapping(branchId, lazdaItemId, variation.getSkuId(), APIAllOrders.Channel.LAZADA, inventoryId));
            });
        } else {
            inventoryMappingList.add(new InventoryMapping(branchId, itemId, APIAllOrders.Channel.GOSELL, branchId + "-" + itemId));
            inventoryMappingList.add(new InventoryMapping(branchId, lazdaItemId, APIAllOrders.Channel.LAZADA, branchId + "-" + itemId));
        }
        return inventoryMappingList;
    }

    /**
     * To get branchId and productId mapping with Lazada product.
     *
     * @param lazadaProductId
     * @return Format: branchId-productId
     */
    @SneakyThrows
    public String getBranchAndProductMappingWithLazadaProduct(long lazadaProductId) {
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId == lazadaProductId)
                .findAny().orElseThrow(() -> new Exception("ProductId mapping with lazadaItem %s not found!".formatted(lazadaProductId)));
        System.out.println("lazadaProduct: " + lazadaProduct);
        System.out.println(lazadaProduct.getBranchId() + "-" + lazadaProduct.getBcItemId());
        return lazadaProduct.getBranchId() + "-" + lazadaProduct.getBcItemId();
    }

    public List<Long> getLazadaProductIdWithStatus(ProductThirdPartyStatus status, boolean hasVariation) {
        return lazadaProductList.stream().filter(i -> i.gosellStatus.equals(status.name()) && hasVariation == i.hasVariation)
                .map(LazadaProduct::getLazadaItemId)
                .collect(Collectors.toList());
    }

    public List<Long> getLazadaProductIdNotStatus(ProductThirdPartyStatus status) {
        return lazadaProductList.stream().filter(i -> !i.gosellStatus.equals(status.name()))
                .map(LazadaProduct::getLazadaItemId)
                .collect(Collectors.toList());
    }
    public List<Long> getLazadaProductIdNotStatus(ProductThirdPartyStatus status, boolean hasVaiation) {
        return lazadaProductList.stream().filter(i ->i.hasVariation==hasVaiation && !i.gosellStatus.equals(status.name()))
                .map(LazadaProduct::getLazadaItemId)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public List<SQLGetInventoryEvent.InventoryEvent> getInventoryEventInfo(int branchId, long productId, EventAction action) {
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventList = new ArrayList<>();
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.bcItemId == productId && i.branchId == branchId)
                .findAny().orElseThrow(() -> new Exception("ProductId %s not found!".formatted(productId)));
        if (lazadaProduct.hasVariation) {
            List<Variation> variationList = lazadaProduct.getVariations();
            variationList.forEach(variation -> {
                SQLGetInventoryEvent.InventoryEvent inventoryEvent = new SQLGetInventoryEvent.InventoryEvent();
                String itemModelId = variation.getBcItemModelId();
                inventoryEvent.setBranch_id(String.valueOf(branchId));
                inventoryEvent.setItem_id(String.valueOf(productId));
                inventoryEvent.setModel_id(itemModelId == null ? "0" : itemModelId);
                inventoryEvent.setAction(action.name());
                inventoryEventList.add(inventoryEvent);
            });
        } else {
            SQLGetInventoryEvent.InventoryEvent inventoryEvent = new SQLGetInventoryEvent.InventoryEvent();
            inventoryEvent.setBranch_id(String.valueOf(branchId));
            inventoryEvent.setItem_id(String.valueOf(productId));
            inventoryEvent.setModel_id("0");
            inventoryEvent.setAction(action.name());
            inventoryEventList.add(inventoryEvent);
        }
        logger.info("inventoryEventList from API: " + inventoryEventList);
        return inventoryEventList;
    }

    @SneakyThrows
    public boolean hasLinkErrorStatus(int branchId, long productId) {
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.bcItemId == productId && i.branchId == branchId)
                .findAny().orElseThrow(() -> new Exception("ProductId %s not found!".formatted(productId)));
        return lazadaProduct.isHasLinkErrorStatus();
    }

    @Data
    private static class LazadaLinkPayload {
        private int bcItemId;
        private int itemId;
        private long lazadaShopId;
        private int branchId;
        private List<VariationLinkPayLoad> lazadaItemVariations;
        private List<VariationLinkPayLoad> bcItemVariations;
        private String bcStoreId;
        @JsonProperty("isManual")
        private boolean manual;

        @Data
        private static class VariationLinkPayLoad {
            private long id;
            private String value;
        }
    }

    @SneakyThrows
    public LazadaLinkPayload getLinkProductPayLoad(int branchId, long productId, long lazadaProductId) {
        LazadaLinkPayload payload = new LazadaLinkPayload();
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId == lazadaProductId && i.branchId == branchId && i.gosellStatus.equals(ProductThirdPartyStatus.UNLINK.name()))
                .findAny().orElseThrow(() -> new Exception("Lazada product id: %s not found or has not status  UNLINK!".formatted(lazadaProductId)));
        payload.setItemId(lazadaProduct.getId());
        payload.setBcItemId((int) productId);
        payload.setLazadaShopId(lazadaProduct.getLazadaShopId());
        payload.setBranchId(branchId);
        List<LazadaLinkPayload.VariationLinkPayLoad> listVariationLazada = new ArrayList<>();
        List<LazadaLinkPayload.VariationLinkPayLoad> variationListGS = new ArrayList<>();

        if (lazadaProduct.hasVariation) {
            List<Variation> variationList = lazadaProduct.getVariations();
            variationList.forEach(variation -> {
                LazadaLinkPayload.VariationLinkPayLoad variationLink = new LazadaLinkPayload.VariationLinkPayLoad();
                variationLink.setId(variation.getId());
                variationLink.setValue(variation.getName());
                listVariationLazada.add(variationLink);
            });
            APIProductDetailV2.ProductInfoV2 productDetail = new APIProductDetailV2(loginInformation).getInfo((int) productId);
            List<APIProductDetailV2.ProductInfoV2.Model> modelList = productDetail.getModels();
            modelList.forEach(model -> {
                LazadaLinkPayload.VariationLinkPayLoad variationLink = new LazadaLinkPayload.VariationLinkPayLoad();
                variationLink.setId(model.getId());
                variationLink.setValue(model.getName());
                variationListGS.add(variationLink);
            });
        }
        payload.setLazadaItemVariations(listVariationLazada);
        payload.setBcItemVariations(variationListGS);
        payload.setBcStoreId(String.valueOf(loginInfo.getStoreID()));
        payload.setManual(true);
        return payload;
    }

    public LazadaLinkPayload linkProduct(int branchId, long productId, long lazadaProductId) {
        LazadaLinkPayload payload = getLinkProductPayLoad(branchId, productId, lazadaProductId);
        Response response = api.put(LINK_LAZADA_PATH, loginInfo.getAccessToken(), payload);
        response.then().statusCode(200);
        logger.info("Link product lazada: %s to gosell product: %s".formatted(lazadaProductId,productId));
        return payload;
    }

    /**
     * To link product Lazada with Gosell
     * @param branchId
     * @param productId
     * @param lazadaProductId
     * @return Map<String, List> has keys: inventoryEvent, inventoryMapping
     */
    public Map<String, List> linkProductThenGetInventoryInfo(int branchId, long productId, long lazadaProductId) {
        LazadaLinkPayload payload = linkProduct(branchId, productId, lazadaProductId);
        //Get InventoryMapping and Inventory Event
        Map<String, List> eventAndMapping = new HashMap<>();
        List<InventoryMapping> inventoryMappingList = new ArrayList<>();
        List<SQLGetInventoryEvent.InventoryEvent> inventoryEventList = new ArrayList<>();
        List<LazadaLinkPayload.VariationLinkPayLoad> variationLazadaPL = payload.getLazadaItemVariations();
        List<LazadaLinkPayload.VariationLinkPayLoad> variationGoSellPL = payload.getBcItemVariations();
        if (!variationLazadaPL.isEmpty()) {
            variationLazadaPL.forEach(variation -> {
                //Mapping
                String itemModelGoSellId = String.valueOf(variationGoSellPL.get(variationLazadaPL.indexOf(variation)).getId());
                String itemModelLazId = getLazadVariationProductId(lazadaProductId, variation.getId());
                String inventoryId = branchId + "-" + productId + "-" + itemModelGoSellId;
                inventoryMappingList.add(new InventoryMapping(branchId, productId, itemModelGoSellId, APIAllOrders.Channel.GOSELL, inventoryId));
                inventoryMappingList.add(new InventoryMapping(branchId, lazadaProductId, itemModelLazId, APIAllOrders.Channel.LAZADA, inventoryId));
                //Event
                SQLGetInventoryEvent.InventoryEvent inventoryEvent = new SQLGetInventoryEvent.InventoryEvent();
                inventoryEvent.setBranch_id(String.valueOf(branchId));
                inventoryEvent.setItem_id(String.valueOf(productId));
                inventoryEvent.setModel_id(itemModelGoSellId == null ? "0" : itemModelGoSellId);
                inventoryEvent.setAction(EventAction.GS_LAZADA_SYNC_ITEM_EVENT.name());
                inventoryEventList.add(inventoryEvent);
            });
        } else {
            //Mapping
            inventoryMappingList.add(new InventoryMapping(branchId, productId, APIAllOrders.Channel.GOSELL, branchId + "-" + productId));
            inventoryMappingList.add(new InventoryMapping(branchId, lazadaProductId, APIAllOrders.Channel.LAZADA, branchId + "-" + productId));
            //Event
            SQLGetInventoryEvent.InventoryEvent inventoryEvent = new SQLGetInventoryEvent.InventoryEvent();
            inventoryEvent.setBranch_id(String.valueOf(branchId));
            inventoryEvent.setItem_id(String.valueOf(productId));
            inventoryEvent.setModel_id("0");
            inventoryEvent.setAction(EventAction.GS_LAZADA_SYNC_ITEM_EVENT.name());
            inventoryEventList.add(inventoryEvent);
        }
        eventAndMapping.put("inventoryEvent", inventoryEventList);
        eventAndMapping.put("inventoryMapping", inventoryMappingList);
        return eventAndMapping;
    }

    @SneakyThrows
    public void unlinkLazadaProduct(long lazadaProductId) {
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId == lazadaProductId && !i.gosellStatus.equals(ProductThirdPartyStatus.UNLINK.name()))
                .findAny().orElseThrow(() -> new Exception("Lazada product id: %s not found or has status LINKED/SYNCE!".formatted(lazadaProductId)));
        long lazadaShop = lazadaProduct.getLazadaShopId();
        Response response = api.get(UNLINK_LAZADA_PATH.formatted(loginInfo.getStoreID(), lazadaShop, lazadaProductId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Unlink product: "+lazadaProductId);
    }
    @SneakyThrows
    public void unlinkLazadaProduct(List<Long> lazadaProductIdList) {
        lazadaProductIdList.forEach(lazadaProductId -> {
            unlinkLazadaProduct(lazadaProductId);
        });
    }
    @SneakyThrows
    public void unlinkAllLazadaProduct() {
        List<LazadaProduct> productCanUnlinkList = lazadaProductList.stream().filter(i -> !i.gosellStatus.equals(ProductThirdPartyStatus.UNLINK.name())).collect(Collectors.toList());
        productCanUnlinkList.forEach(lazadaProduct -> {
            long lazadaProductId = lazadaProduct.getLazadaItemId();
            long lazadaShop = lazadaProduct.getLazadaShopId();
            Response response = api.get(UNLINK_LAZADA_PATH.formatted(loginInfo.getStoreID(), lazadaShop, lazadaProductId), loginInfo.getAccessToken());
            response.then().statusCode(200);
        });
    }

    @SneakyThrows
    public String getLazadVariationProductId(long lazadaProductId, long variationId) {
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId == lazadaProductId)
                .findAny().orElseThrow(() -> new Exception("Lazada product id: %s not found!".formatted(lazadaProductId)));
        List<Variation> variationList = lazadaProduct.getVariations();
        Variation variation = variationList.stream().filter(i -> i.id == variationId)
                .findAny().orElseThrow(() -> new Exception("Product: %s don't have variation: %s".formatted(lazadaProductId, variationId)));
        return variation.getSkuId();
    }

    @SneakyThrows
    public int getVariationNumberOfLazadaProduct(long lazadaProductId) {
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId == lazadaProductId)
                .findAny().orElseThrow(() -> new Exception("Lazada product id: %s not found!".formatted(lazadaProductId)));
        return lazadaProduct.getVariations().size();
    }
    public List<Long> getProductListHasLinkedSyncedLazada(){
        List<Long> productList = new ArrayList<>();
        lazadaProductList.forEach(lazadaProductInfo -> {
            if (lazadaProductInfo.getBcItemId()!=0){
                productList.add(lazadaProductInfo.getBcItemId());
            }
        });
        return productList;
    }
}
