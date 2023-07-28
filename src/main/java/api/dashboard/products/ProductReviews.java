package api.dashboard.products;

import api.dashboard.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class ProductReviews {
    String PRODUCT_REVIEW_CONFIG = "/itemservice/api/store-item-reviews/store/%s";
    String PRODUCT_REVIEW_LIST = "/itemservice/api/item-reviews/store/storeID?langKey=vi&page=0&size=1000&sort=reviewDate,DESC&itemName.contains=";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public ProductReviews(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    public boolean isIsEnableReview() {
        Response productReviewsConfig = api.get(PRODUCT_REVIEW_CONFIG.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        productReviewsConfig.then().statusCode(200);

        return productReviewsConfig.jsonPath().getBoolean("isEnableReview");
    }

    public JsonPath getAllReviewJsonPath() {
        Response productReviews = api.get(PRODUCT_REVIEW_LIST.replace("storeID", String.valueOf(loginInfo.getStoreID())), loginInfo.getAccessToken());
        productReviews.then().statusCode(200);
        return productReviews.jsonPath();
    }

    public List<String> getProductNameList() {
        return getAllReviewJsonPath().getList("itemName");
    }

}
