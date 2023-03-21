package api.dashboard.products;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class ProductReviews {
    String PRODUCT_REVIEW_CONFIG = "/itemservice/api/store-item-reviews/store/%s";
    String PRODUCT_REVIEW_LIST = "/itemservice/api/item-reviews/store/storeID?langKey=vi&page=0&size=1000&sort=reviewDate,DESC&itemName.contains=";
    API api = new API();
    public static boolean isEnableReview;

    public void getProductReviewsConfig() {
        Response productReviewsConfig = api.get(PRODUCT_REVIEW_CONFIG.formatted(apiStoreID), accessToken);
        productReviewsConfig.then().statusCode(200);

        isEnableReview = productReviewsConfig.jsonPath().getBoolean("isEnableReview");
    }
    
    public JsonPath getAllReviewJsonPath() {
    	Response productReviews = api.get(PRODUCT_REVIEW_LIST.replace("storeID", String.valueOf(apiStoreID)), accessToken);
    	productReviews.then().statusCode(200);
    	return productReviews.jsonPath();
    }
    
    public List<String> getProductNameList() {
    	return getAllReviewJsonPath().getList("itemName");
    }

}
