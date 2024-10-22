package api.Seller.sale_channel.lazada;

import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import lombok.SneakyThrows;
import utilities.api.API;
import utilities.enums.ProductThirdPartyStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.inventory.InventoryMapping;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class APILazadaProducts {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    List<LazadaProduct> lazadaProductList;
    String GET_LAZADA_PRODUCT_PATH = "/lazadaservices/api/items/bc-store/%s?keyword=&page=0&size=100&getBcItemName=true";
    public APILazadaProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        Response response = callAPIGetLazadaProduct();
        lazadaProductList = Arrays.asList(response.as(LazadaProduct[].class));
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
//    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @Data
    public static class LazadaProduct{
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
        private long skuId;
        private String sellerSku;
        private String shopSku;
        private String attributes;
        private long bcItemModelId;
        private int stock;
        private long productId;
        private String name;
    }
    public Response callAPIGetLazadaProduct(){
        Response response = api.get(GET_LAZADA_PRODUCT_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    /**
     * Only has data when product has linked/synced
     * */
    @SneakyThrows
    public List<InventoryMapping> getLazadaInventoryMappingInfo(long productId){
        List<InventoryMapping> inventoryMappingList = new ArrayList<>();
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.bcItemId==productId)
                .findAny().orElseThrow(() -> new Exception("ProductId %s not found!".formatted(productId)));
        int branchId = lazadaProduct.getBranchId();
        long itemId = lazadaProduct.getBcItemId();
        long lazdaItemId = lazadaProduct.getLazadaItemId();
        if(lazadaProduct.hasVariation){
            List<Variation> variationList = lazadaProduct.getVariations();
            variationList.forEach(variation -> {
                Long itemModelId = variation.getBcItemModelId();
                String inventoryId = branchId+"-"+itemId+"-"+itemModelId;
                inventoryMappingList.add(new InventoryMapping(branchId,itemId,itemModelId, variation.getStock() ,APIAllOrders.Channel.GOSELL,inventoryId ));
                inventoryMappingList.add(new InventoryMapping(branchId,lazdaItemId,variation.getSkuId(), variation.getStock(), APIAllOrders.Channel.LAZADA,inventoryId));
            });
        }else {
            inventoryMappingList.add(new InventoryMapping(branchId,itemId,lazadaProduct.getStock(), APIAllOrders.Channel.GOSELL,branchId+"-"+itemId));
            inventoryMappingList.add(new InventoryMapping(branchId,lazdaItemId, lazadaProduct.getStock(), APIAllOrders.Channel.GOSELL,branchId+"-"+itemId));
        }
        return inventoryMappingList;
    }
    @SneakyThrows
    public long getProductIdMappingWithLazdaProduct(long lazadaProductId){
        LazadaProduct lazadaProduct = lazadaProductList.stream().filter(i -> i.lazadaItemId==lazadaProductId)
                .findAny().orElseThrow(() -> new Exception("ProductId mapping with lazadaItem %s not found!".formatted(lazadaProductId)));
        return lazadaProduct.getBcItemId();
    }
    public List<Long> getLazadaProductIdWithStatus(ProductThirdPartyStatus status, boolean hasVariation){
        return lazadaProductList.stream().filter(i -> i.gosellStatus.equals(status.name()) && hasVariation == i.hasVariation)
                .map(LazadaProduct::getLazadaItemId)
                .collect(Collectors.toList());
    }
}
