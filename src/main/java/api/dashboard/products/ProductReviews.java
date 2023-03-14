package api.dashboard.products;

import io.restassured.response.Response;
import utilities.api.API;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class ProductReviews {
    String PRODUCT_REVIEW_CONFIG = "/itemservice/api/store-item-reviews/store/%s";
    API api = new API();
    public static boolean isEnableReview;

    public void getProductReviewsConfig() {
        Response productReviewsConfig = api.get(PRODUCT_REVIEW_CONFIG.formatted(apiStoreID), accessToken);
        productReviewsConfig.then().statusCode(200);

        isEnableReview = productReviewsConfig.jsonPath().getBoolean("isEnableReview");
    }
}
