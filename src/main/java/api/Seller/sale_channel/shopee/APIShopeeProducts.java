package api.Seller.sale_channel.shopee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIGetProductDetail.ProductInformation;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.salechanel.shopee.ShopeeProduct;
import utilities.model.dashboard.salechanel.shopee.TierVariation;
import utilities.model.dashboard.salechanel.shopee.Variation;
import utilities.model.sellerApp.login.LoginInformation;

public class APIShopeeProducts {
    Logger logger = LogManager.getLogger(APIShopeeProducts.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIShopeeProducts(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum GoSELLStatus {
        LINK, UNLINK
    }

    @Data
    public static class ShopeeProductsInformation {
        List<Integer> ids = new ArrayList<>();
        List<Long> shopeeItemIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Integer> shopeeIds = new ArrayList<>();
        List<String> shopeeShopNames = new ArrayList<>();
        List<GoSELLStatus> gosellStatues = new ArrayList<>();
        List<Boolean> hasVariations = new ArrayList<>();
        List<Integer> variationNum = new ArrayList<>();
    }

    String allShopeeProductPath = "/shopeeservices/api/items/bc-store/%s?page=%s&size=100&getBcItemName=true&sort=update_time,DESC";
    String linkProductPath = "/shopeeservices/api/items/link";
    String unlinkProductPath = "/shopeeservices/api/items/<storeId>/unlink/<shopeeShopId>?ids=<shopeeItemId>";
    String importProductPath = "/shopeeservices/api/items/syn_to_gosell/<storeId>";
    String syncStatusPath = "/shopeeservices/api/item-synch-informations/get-status/<storeId>";
    String downloadSingleProductPath = "/shopeeservices/api/item-download-informations/product/<storeId>/<shopeeShopId>/<shopeeItemId>";

    Response getShopeeProductResponse(int pageIndex) {
        return api.get(allShopeeProductPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public ShopeeProductsInformation getShopeeProductInformation() {
        // init temp
        ShopeeProductsInformation info = new ShopeeProductsInformation();
        List<Integer> ids = new ArrayList<>();
        List<Long> shopeeItemIds = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<Integer> shopeeIds = new ArrayList<>();
        List<String> shopeeShopNames = new ArrayList<>();
        List<String> gosellStatues = new ArrayList<>();
        List<Boolean> hasVariations = new ArrayList<>();
        List<Integer> variationNum = new ArrayList<>();

        Response response = getShopeeProductResponse(0);
        if (response.getStatusCode() != 403) {
            int numOfPages = Integer.parseInt(response.getHeader("X-Total-Count")) / 100;

            List<JsonPath> jsonPaths = IntStream.rangeClosed(0, numOfPages).parallel()
                    .mapToObj(pageIndex -> getShopeeProductResponse(pageIndex).then().statusCode(200).extract().jsonPath())
                    .toList();

            jsonPaths.forEach(jsonPath -> {
                ids.addAll(jsonPath.getList("id"));
                shopeeItemIds.addAll(jsonPath.getList("shopeeItemId"));
                branchIds.addAll(jsonPath.getList("branchId"));
                shopeeIds.addAll(jsonPath.getList("shopeeShopId"));
                shopeeShopNames.addAll(jsonPath.getList("shopeeShopName"));
                gosellStatues.addAll(jsonPath.getList("gosellStatus"));
                hasVariations.addAll(jsonPath.getList("hasVariation"));

                // get variation number
                List<ArrayList<Integer>> variationIds = jsonPath.getList("variations.id");
                AtomicInteger productIndex = new AtomicInteger(0);
                List<Integer> varNum = hasVariations.stream().map(hasVariation -> hasVariation ? variationIds.get(productIndex.getAndIncrement()).size() : 0).toList();
                variationNum.addAll(varNum);
            });
        }

        // return model
        info.setIds(ids);
        info.setShopeeItemIds(shopeeItemIds);
        info.setBranchIds(branchIds);
        info.setShopeeIds(shopeeIds);
        info.setShopeeShopNames(shopeeShopNames);
        info.setGosellStatues(gosellStatues.stream().map(GoSELLStatus::valueOf).toList());
        info.setHasVariations(hasVariations);
        info.setVariationNum(variationNum);

        return info;
    }

    /**
     * Getting the first 100 products on Shopee
     * @return a list of ShopeeProduct object
     */
    public List<ShopeeProduct> getProducts() {
    	return getShopeeProductResponse(0).jsonPath().getList(".", ShopeeProduct.class);
    }
    
    /**
     * Unlinks Shopee products from GoSELL products
     * @param shopeeShopId
     * @param shopeeItemIdList
     */
    public void unlinkProduct(String shopeeShopId, List<String> shopeeItemIdList) {
        if (shopeeItemIdList.size() == 0) {
            logger.info("Shopee product id list input is empty. Skipping unlinkProduct");
            return;
        }
    	
        String shopeeItemIdsString = shopeeItemIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        
    	String basePath = unlinkProductPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()))
    			.replaceAll("<shopeeShopId>", shopeeShopId)
    			.replaceAll("<shopeeItemId>", shopeeItemIdsString);
    	
    	api.get(basePath, loginInfo.getAccessToken()).then().statusCode(200);
    	
    	logger.info("Unlinked Shopee product ids: {}", shopeeItemIdList);
    }    
    
    public void linkProductNoVariations(ShopeeProduct product, int gosellProductId) {
    	String body = """
				{
					"bcItemId": %s,
					"itemId": %s,
					"shopeeShopId": %s,
					"branchId": %s,
					"bcStoreId": "%s"
				}    			
    			""".formatted(gosellProductId, product.getId(), product.getShopeeShopId(), product.getBranchId(), product.getBcStoreId());
    	
    	api.put(linkProductPath, loginInfo.getAccessToken(), body).then().statusCode(204);	
    	
    	logger.info("Linked Shopee product '{}' with GoSELL product '{}'", product.getShopeeItemId(), gosellProductId);
    } 
    
    //TODO update description for this function
    public List<List<String>> linkProductHavingVariations(ShopeeProduct product, ProductInformation gosellProductInfo) {

    	var shopeeVariations = product.getVariations().stream().map(var -> {
    		var variation = new JSONObject();
    		variation.put("id", var.getId());
    		variation.put("value", var.getName());
    		return variation;
    	}).collect(Collectors.toList());

    	var shopeeVariationIds = product.getVariations().stream()
    			.map(Variation::getShopeeVariationId)
    			.collect(Collectors.toList());
    	
    	var gosellVariations = gosellProductInfo.getModels().stream().map(var -> {
    		var variation = new JSONObject();
    		variation.put("id", var.getId());
    		variation.put("value", var.getOrgName());
    		return variation;
    	}).collect(Collectors.toList());
    	
    	JSONObject linkProductPayload = new JSONObject();
    	linkProductPayload.put("bcItemId", gosellProductInfo.getId());
    	linkProductPayload.put("itemId", product.getId());
    	linkProductPayload.put("shopeeShopId", product.getShopeeShopId());
    	linkProductPayload.put("branchId", product.getBranchId());
    	linkProductPayload.put("bcStoreId", String.valueOf(product.getBcStoreId()));
    	linkProductPayload.put("bcTierVariations", Arrays.asList(gosellProductInfo.getModels().get(0).getLabel().split("\\|")));
    	linkProductPayload.put("shopeeTierVariations", product.getTierVariations().stream().map(TierVariation::getName).collect(Collectors.toList()));
    	linkProductPayload.put("shopeeItemVariations", shopeeVariations);
    	linkProductPayload.put("isManual", true);
    	linkProductPayload.put("bcItemVariations", gosellVariations);
    	
    	api.put(linkProductPath, loginInfo.getAccessToken(), linkProductPayload.toString()).then().statusCode(204);	
    	
    	logger.info("Linked Shopee product '{}' with GoSELL product '{}'", product.getShopeeItemId(), gosellProductInfo.getId());
    	
    	List<List<String>> mappedVarIds = new ArrayList<>();
    	for (int i=0; i < Math.max(shopeeVariationIds.size(), gosellVariations.size()); i++) {
    		mappedVarIds.add(List.of(String.valueOf(shopeeVariationIds.get(i)), String.valueOf(gosellVariations.get(i).getInt("id"))));
    	}
    	
    	return mappedVarIds;
    }    

    /**
     * After importing Shopee products to GoSELL, it's essential to wait for the sync process to complete.
     * This function is responsible for that
     */
    public void waitUnTilSyncingComplete() {
    	
    	String basePath = syncStatusPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()));
    	
    	int maxAttempts = 30;
    	int sleepDuration = 6000;
    	
    	for (int i=0; i<maxAttempts; i++) {
    		Response response = api.get(basePath, loginInfo.getAccessToken());
    		response.then().statusCode(200);
    		
    		if (!response.jsonPath().getBoolean("isInProgress")) break;
    		
    		try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	logger.info("Sync process completed. Go ahead!");
    }  
    
    /**
     * Pulls products from Shopee then pushes the products to GoSELL (Create products to GoSELL)
     * @param shopeeItemRecordIdList list of Shopee product record id stored in database
     */
    public void importProductToGosell(List<Integer> shopeeItemRecordIdList) {
        if (shopeeItemRecordIdList.size() == 0) {
            logger.info("Shopee product id list input is empty. Skipping importProductToGosell");
            return;
        }
    	
        String payload = """
				{
					"shopeeItemIds": %s,
					"shouldCreateCollection": false,
					"createToGoSell": true
				}        		
        		""".formatted(shopeeItemRecordIdList);
        
    	String basePath = importProductPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()));
    	
    	api.post(basePath, loginInfo.getAccessToken(), payload).then().statusCode(200);
    	
    	logger.info("Pulled Shopee product ids into GoSEll: {}", shopeeItemRecordIdList);
    	
    	waitUnTilSyncingComplete();
    }   
    
	/**
	 * Downloads Shopee products one by one from a Shopee shop
	 * @param productList list of ShopeeProduct objects
	 */
    public void downloadSingleProduct(List<ShopeeProduct> productList) {
    	
    	productList.stream().forEach(product -> {
    		String basePath = downloadSingleProductPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()))
    				.replaceAll("<shopeeShopId>", String.valueOf(product.getShopeeShopId()))
    				.replaceAll("<shopeeItemId>", product.getShopeeItemId());
    		
    		api.post(basePath, loginInfo.getAccessToken()).then().statusCode(200);
    		
    		logger.info("Downloaded Shopee product id '{}' from Shopee shop id '{}'", product.getShopeeItemId(), product.getShopeeShopId());
    	});
    }        
}
