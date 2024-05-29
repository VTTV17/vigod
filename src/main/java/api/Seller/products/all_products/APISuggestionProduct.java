package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.location.APILocation;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static api.Seller.products.all_products.APIProductDetail.ProductInformationEnum.platform;

public class APISuggestionProduct {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    private record CacheQuery(String staffToken, int branchIds) {
    }
    private static final Cache<CacheQuery, AllSuggestionProductsInfo> cache = CacheBuilder.newBuilder()
            .build();

    public APISuggestionProduct(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    private String suggestProductPath = "/itemservice/api/store/%s/item-model/suggestion?page=%s&size=100&ignoreDeposit=true&branchId=%s&ignoreOutOfStock=true&includeConversion=true";

    @Data
    public static class AllSuggestionProductsInfo {
        private List<Integer> itemIds;
        private List<Integer> modelIds;
        private List<String> itemNames;
        private List<String> modelNames;
        private List<String> barcodes;
        private List<Long> remainingStocks;
        private List<String> inventoryManageTypes;
        private List<Boolean> hasLots;
        private List<Boolean> hasLocations;
        private List<Long> price;
        private List<Long> costPrice;
    }

    @Data
    public static class SuggestionProductsInfo {
        private int branchId;
        private String branchName;
        private int itemId;
        private int modelId;
        private String itemName;
        private String modelName;
        private String barcode;
        private Long remainingStock;
        private String inventoryManageType;
        private Boolean hasLot;
        private Boolean hasLocation;
        private Long price;
        private Long costPrice;
    }

    Response getSuggestionResponse(int pageIndex, int branchId) {
        return api.get(suggestProductPath.formatted(loginInfo.getStoreID(), pageIndex, branchId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllSuggestionProductsInfo getListSuggestionProduct(int branchId) {
        AllSuggestionProductsInfo info = cache.getIfPresent(new CacheQuery(loginInfo.getStaffPermissionToken(), branchId));
        if (Optional.ofNullable(info).isEmpty()) {
            if (!loginInfo.getStaffPermissionToken().isEmpty()) {
                AllSuggestionProductsInfo tempInfo = cache.getIfPresent(new CacheQuery("", branchId));
                cache.invalidateAll();
                if (Optional.ofNullable(tempInfo).isPresent()) {
                    cache.put(new CacheQuery("", branchId), tempInfo);
                }
            }
            // init suggestion model
            info = new AllSuggestionProductsInfo();

            // init temp array
            List<String> itemIds = new ArrayList<>();
            List<String> modelIds = new ArrayList<>();
            List<String> itemNames = new ArrayList<>();
            List<String> modelNames = new ArrayList<>();
            List<String> barcodes = new ArrayList<>();
            List<String> remainingStocks = new ArrayList<>();
            List<String> inventoryManageTypes = new ArrayList<>();
            List<Boolean> hasLots = new ArrayList<>();
            List<Boolean> hasLocations = new ArrayList<>();
            List<Long> price = new ArrayList<>();
            List<Long> costPrice = new ArrayList<>();

            // get total products
            int totalOfProducts = Integer.parseInt(getSuggestionResponse(0, branchId).getHeader("X-Total-Count"));

            // get number of pages
            int numberOfPages = totalOfProducts / 100;

            // get other page data
            for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
                Response response = getSuggestionResponse(pageIndex, branchId);
                itemIds.addAll(response.jsonPath().getList("itemId"));
                modelIds.addAll(response.jsonPath().getList("modelId"));
                itemNames.addAll(response.jsonPath().getList("itemName"));
                modelNames.addAll(response.jsonPath().getList("modelName"));
                barcodes.addAll(response.jsonPath().getList("barcode"));
                remainingStocks.addAll(response.jsonPath().getList("modelStock"));
                inventoryManageTypes.addAll(response.jsonPath().getList("inventoryManageType"));
                hasLots.addAll(response.jsonPath().getList("hasLot"));
                hasLocations.addAll(response.jsonPath().getList("hasLocation"));
                price.addAll(Pattern.compile("price.{4}(\\d+)").matcher(response.asPrettyString())
                        .results()
                        .map(matchResult -> Long.valueOf(matchResult.group(1)))
                        .toList());
                costPrice.addAll(Pattern.compile("costPrice.{4}(\\d+)").matcher(response.asPrettyString())
                        .results()
                        .map(matchResult -> Long.valueOf(matchResult.group(1)))
                        .toList());
            }

            // set suggestion info
            info.setItemIds(itemIds.stream().mapToInt(Integer::parseInt).boxed().toList());
            info.setModelIds(modelIds.stream().mapToInt(model -> model.isEmpty() ? 0 : Integer.parseInt(model)).boxed().toList());
            info.setItemNames(itemNames);
            info.setModelNames(modelNames);
            info.setBarcodes(barcodes);
            info.setRemainingStocks(remainingStocks.stream().map(Long::parseLong).toList());
            info.setInventoryManageTypes(inventoryManageTypes);
            info.setHasLots(hasLots);
            info.setHasLocations(hasLocations);
            info.setPrice(price);
            info.setCostPrice(costPrice);

            // return suggestion model
            cache.put(new CacheQuery(loginInfo.getStaffPermissionToken(), branchId), info);
        }
        return info;
    }

    public AllSuggestionProductsInfo getAllSuggestProductIdInStock(int branchId) {
        // get all suggestions information
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);

        // init suggestion model to get all products in-stock
        AllSuggestionProductsInfo info = new AllSuggestionProductsInfo();

        // init temp array
        List<Integer> itemIds = new ArrayList<>();
        List<Integer> modelIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> modelNames = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<Long> remainingStocks = new ArrayList<>();
        List<String> inventoryManageTypes = new ArrayList<>();

        // filter by in-stock conditions
        IntStream.range(0, suggestionInfo.getItemIds().size())
                .filter(index -> (suggestionInfo.getRemainingStocks().get(index) > 0))
                .forEach(index -> {
                    itemIds.add(suggestionInfo.getItemIds().get(index));
                    itemNames.add(suggestionInfo.getItemNames().get(index));
                    modelNames.add(suggestionInfo.getModelNames().get(index));
                    modelIds.add(suggestionInfo.getModelIds().get(index));
                    barcodes.add(suggestionInfo.getBarcodes().get(index));
                    remainingStocks.add(suggestionInfo.getRemainingStocks().get(index));
                    inventoryManageTypes.add(suggestionInfo.getInventoryManageTypes().get(index));
                });

        // set in-stock all suggestions
        info.setItemIds(itemIds);
        info.setModelIds(modelIds);
        info.setItemNames(itemNames);
        info.setModelNames(modelNames);
        info.setBarcodes(barcodes);
        info.setRemainingStocks(remainingStocks);
        info.setInventoryManageTypes(inventoryManageTypes);

        // return model
        return info;
    }

    public AllSuggestionProductsInfo getAllSuggestProductIdNoManagedByLot(int branchId, boolean hasLot) {
        // get all suggestions information
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);

        // init suggestion model to get all products in-stock
        AllSuggestionProductsInfo info = new AllSuggestionProductsInfo();

        // init temp array
        List<Integer> itemIds = new ArrayList<>();
        List<Integer> modelIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> modelNames = new ArrayList<>();
        List<String> barcodes = new ArrayList<>();
        List<Long> remainingStocks = new ArrayList<>();
        List<String> inventoryManageTypes = new ArrayList<>();
        List<Long> price = new ArrayList<>();
        List<Long> costPrice = new ArrayList<>();

        // filter by in-stock conditions
        IntStream.range(0, suggestionInfo.getItemIds().size())
                .filter(index -> (suggestionInfo.getHasLots().get(index).equals(hasLot)))
                .forEach(index -> {
                    itemIds.add(suggestionInfo.getItemIds().get(index));
                    itemNames.add(suggestionInfo.getItemNames().get(index));
                    modelNames.add(suggestionInfo.getModelNames().get(index));
                    modelIds.add(suggestionInfo.getModelIds().get(index));
                    barcodes.add(suggestionInfo.getBarcodes().get(index));
                    remainingStocks.add(suggestionInfo.getRemainingStocks().get(index));
                    inventoryManageTypes.add(suggestionInfo.getInventoryManageTypes().get(index));
                    price.add(suggestionInfo.getPrice().get(index));
                    costPrice.add(suggestionInfo.getCostPrice().get(index));
                });

        // set in-stock all suggestions
        info.setItemIds(itemIds);
        info.setModelIds(modelIds);
        info.setItemNames(itemNames);
        info.setModelNames(modelNames);
        info.setBarcodes(barcodes);
        info.setRemainingStocks(remainingStocks);
        info.setInventoryManageTypes(inventoryManageTypes);
        info.setPrice(price);
        info.setCostPrice(costPrice);

        // return model
        return info;
    }

    public SuggestionProductsInfo getSuggestProductForImportProductLocationReceipt(int branchId, boolean hasLot) {
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
            if (suggestionInfo.getInventoryManageTypes().get(index).equals("PRODUCT")
                && (suggestionInfo.getHasLots().get(index) == hasLot)) {
                info.setItemId(suggestionInfo.getItemIds().get(index));
                info.setModelId(suggestionInfo.getModelIds().get(index));
                info.setItemName(suggestionInfo.getItemNames().get(index));
                info.setModelName(suggestionInfo.getModelNames().get(index));
                info.setBarcode(suggestionInfo.getBarcodes().get(index));
                info.setHasLot(hasLot);
                break;
            }
        }

        return info;
    }

    public SuggestionProductsInfo findProductInformationMatchesWithAddLocationReceipt(int branchId) {
        // conditions: product must be managed inventory by Product and 0 < location's stocks < remaining stock
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);

        APILocation location = new APILocation(loginInformation);

        SuggestionProductsInfo info = new SuggestionProductsInfo();
        for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
            if (suggestionInfo.getInventoryManageTypes().get(index).equals("PRODUCT") && (suggestionInfo.getRemainingStocks().get(index) > 0)) {
                // get all location of products
                APILocation.AllProductLocationInfo locationInfo = location.getAllProductLocationInfo(suggestionInfo.getItemIds().get(index),
                        suggestionInfo.getModelIds().get(index),
                        branchId,
                        "ADD_PRODUCT_TO_LOCATION");

                // get total location quantity
                int totalLocationQuantity = locationInfo.getQuantity().stream().mapToInt(Integer::intValue).sum();

                // check remaining stock > total location quantity or not
                if (suggestionInfo.getRemainingStocks().get(index) > totalLocationQuantity) {
                    info.setItemId(suggestionInfo.getItemIds().get(index));
                    info.setModelId(suggestionInfo.getModelIds().get(index));
                    info.setItemName(suggestionInfo.getItemNames().get(index));
                    info.setModelName(suggestionInfo.getModelNames().get(index));
                    info.setBarcode(suggestionInfo.getBarcodes().get(index));
                    info.setHasLot(suggestionInfo.getHasLots().get(index));
                    break;
                }
            }
        }

        return info;
    }

    public SuggestionProductsInfo findProductInformationMatchesWithGetLocationReceipt(int branchId) {
        // conditions: product must be managed inventory by Product and has location
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
            if (suggestionInfo.getInventoryManageTypes().get(index).equals("PRODUCT")
                && (suggestionInfo.getRemainingStocks().get(index) > 0)
                && (suggestionInfo.getHasLocations().get(index))) {
                info.setItemId(suggestionInfo.getItemIds().get(index));
                info.setModelId(suggestionInfo.getModelIds().get(index));
                info.setItemName(suggestionInfo.getItemNames().get(index));
                info.setModelName(suggestionInfo.getModelNames().get(index));
                info.setBarcode(suggestionInfo.getBarcodes().get(index));
                info.setHasLot(suggestionInfo.getHasLots().get(index));
                break;
            }
        }

        return info;
    }

    public SuggestionProductsInfo findProductInformationWithItemIdAndModelId(int branchId, int itemId, int modelId) {
        // conditions: product must be managed inventory by Product and has location
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
            if (suggestionInfo.getItemIds().get(index) == itemId
                && suggestionInfo.getModelIds().get(index) == modelId) {
                info.setItemId(suggestionInfo.getItemIds().get(index));
                info.setModelId(suggestionInfo.getModelIds().get(index));
                info.setItemName(suggestionInfo.getItemNames().get(index));
                info.setModelName(suggestionInfo.getModelNames().get(index));
                info.setBarcode(suggestionInfo.getBarcodes().get(index));
                info.setHasLot(suggestionInfo.getHasLots().get(index));
                info.setPrice(suggestionInfo.getPrice().get(index));
                info.setCostPrice(suggestionInfo.getCostPrice().get(index));
                info.setRemainingStock(suggestionInfo.getRemainingStocks().get(index));
                break;
            }
        }

        return info;
    }

    public SuggestionProductsInfo findProductInformationForAddToCartInPOS() {
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        APIProductDetail productDetail = new APIProductDetail(loginInformation);

        // get all suggestions information
        AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(loginInfo.getAssignedBranchesIds().get(0));

        // filter by in-stock conditions
        for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
            boolean isInStore = Optional.ofNullable(productDetail.getInfo(suggestionInfo.getItemIds().get(index), platform).getInStore())
                    .orElse(false);
            if (isInStore) {
                info.setBranchId(loginInfo.getAssignedBranchesIds().get(0));
                info.setBranchName(loginInfo.getAssignedBranchesNames().get(loginInfo.getAssignedBranchesIds().indexOf(loginInfo.getAssignedBranchesIds().get(0))));
                info.setItemId(suggestionInfo.getItemIds().get(index));
                info.setItemName(suggestionInfo.getItemNames().get(index));
                info.setModelName(suggestionInfo.getModelNames().get(index));
                info.setModelId(suggestionInfo.getModelIds().get(index));
                info.setBarcode(suggestionInfo.getBarcodes().get(index));
                info.setRemainingStock(suggestionInfo.getRemainingStocks().get(index));
                info.setInventoryManageType(suggestionInfo.getInventoryManageTypes().get(index));
                break;
            }
        }
        return info;
    }

    public SuggestionProductsInfo findProductInformationForCreatePOSOrder() {
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        APIProductDetail productDetail = new APIProductDetail(loginInformation);
        APICheckItemModel checkItemModel = new APICheckItemModel(loginInformation);

        for (Integer branchId : loginInfo.getAssignedBranchesIds()) {
            // get all suggestions information
            AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);

            // filter by in-stock conditions
            for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
                if (suggestionInfo.getRemainingStocks().get(index) > 0 && suggestionInfo.getPrice().get(index) > 0) {
                    boolean isInStore = Optional.ofNullable(productDetail.getInfo(suggestionInfo.getItemIds().get(index), platform).getInStore())
                            .orElse(false);
                    String modelCode = (suggestionInfo.getModelIds().get(index) != 0)
                            ? "%s-%s".formatted(suggestionInfo.getItemIds().get(index), suggestionInfo.getModelIds().get(index))
                            : "%s".formatted(suggestionInfo.getItemIds().get(index));
                    boolean isAvailable = checkItemModel.itemModelAvailable(modelCode);
                    if (isInStore && isAvailable) {
                        info.setBranchId(branchId);
                        info.setBranchName(loginInfo.getAssignedBranchesNames().get(loginInfo.getAssignedBranchesIds().indexOf(branchId)));
                        info.setItemId(suggestionInfo.getItemIds().get(index));
                        info.setItemName(suggestionInfo.getItemNames().get(index));
                        info.setModelName(suggestionInfo.getModelNames().get(index));
                        info.setModelId(suggestionInfo.getModelIds().get(index));
                        info.setBarcode(suggestionInfo.getBarcodes().get(index));
                        info.setRemainingStock(suggestionInfo.getRemainingStocks().get(index));
                        info.setInventoryManageType(suggestionInfo.getInventoryManageTypes().get(index));
                        break;
                    }
                }
            }
            if (info.getBranchId() != 0) break;
        }
        return info;
    }

    public SuggestionProductsInfo findProductInformationForAddStockOnPOS() {
        SuggestionProductsInfo info = new SuggestionProductsInfo();
        APIProductDetail productDetail = new APIProductDetail(loginInformation);
        APICheckItemModel checkItemModel = new APICheckItemModel(loginInformation);

        for (Integer branchId : loginInfo.getAssignedBranchesIds()) {
            // get all suggestions information
            suggestProductPath = suggestProductPath.replace("&ignoreOutOfStock=true", "");
            AllSuggestionProductsInfo suggestionInfo = getListSuggestionProduct(branchId);

            // filter by in-stock conditions
            for (int index = 0; index < suggestionInfo.getItemIds().size(); index++) {
                if ((suggestionInfo.getRemainingStocks().get(index) == 0) && !suggestionInfo.getHasLots().get(index)) {
                    boolean isInStore = Optional.ofNullable(productDetail.getInfo(suggestionInfo.getItemIds().get(index), platform).getInStore())
                            .orElse(false);
                    String modelCode = (suggestionInfo.getModelIds().get(index) != 0)
                            ? "%s-%s".formatted(suggestionInfo.getItemIds().get(index), suggestionInfo.getModelIds().get(index))
                            : "%s".formatted(suggestionInfo.getItemIds().get(index));
                    boolean isAvailable = checkItemModel.itemModelAvailable(modelCode);
                    if (isInStore && isAvailable) {
                        info.setBranchId(branchId);
                        info.setBranchName(loginInfo.getAssignedBranchesNames().get(loginInfo.getAssignedBranchesIds().indexOf(branchId)));
                        info.setItemId(suggestionInfo.getItemIds().get(index));
                        info.setItemName(suggestionInfo.getItemNames().get(index));
                        info.setModelName(suggestionInfo.getModelNames().get(index));
                        info.setModelId(suggestionInfo.getModelIds().get(index));
                        info.setBarcode(suggestionInfo.getBarcodes().get(index));
                        info.setRemainingStock(suggestionInfo.getRemainingStocks().get(index));
                        info.setInventoryManageType(suggestionInfo.getInventoryManageTypes().get(index));
                        break;
                    }
                }
            }
            if (info.getBranchId() != 0) break;
        }
        return info;
    }
}
