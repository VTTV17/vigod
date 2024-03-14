package api.Seller.products.product_reviews;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class APIProductReviews {
    String PRODUCT_REVIEW_CONFIG = "/itemservice/api/store-item-reviews/store/%s";
    String PRODUCT_REVIEW_LIST = "/itemservice/api/item-reviews/store/%s?langKey=vi&page=%s&size=100&sort=reviewDate,DESC&itemName.contains=";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIProductReviews(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    public boolean isIsEnableReview() {
        return api.get(PRODUCT_REVIEW_CONFIG.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getBoolean("isEnableReview");
    }

    public JsonPath getAllReviewJsonPath() {
        Response productReviews = api.get(PRODUCT_REVIEW_LIST.formatted(loginInfo.getStoreID(), 0), loginInfo.getAccessToken());
        productReviews.then().statusCode(200);
        return productReviews.jsonPath();
    }

    public List<String> getProductNameList() {
        return getReviewManagementInfo().getItemNames();
    }

    @Data
    public static class ReviewManagementInfo {
        List<Integer> reviewIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> reviewTitles = new ArrayList<>();
        List<String> reviewDescriptions = new ArrayList<>();
        List<Integer> itemIds = new ArrayList<>();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> userIds = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        List<Boolean> enableReviews = new ArrayList<>();
    }

    public Response getReviewListResponse(int pageIndex) {
        return api.get(PRODUCT_REVIEW_LIST.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken());
    }

    public ReviewManagementInfo getReviewManagementInfo() {
        ReviewManagementInfo info = new ReviewManagementInfo();

        // if staff do not have permission, end.
        if (getReviewListResponse(0).getStatusCode() == 403) return info;

        int totalOfProducts = Integer.parseInt(getReviewListResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        List<Integer> reviewIds = new ArrayList<>();
        List<String> itemNames = new ArrayList<>();
        List<String> reviewTitles = new ArrayList<>();
        List<String> reviewDescriptions = new ArrayList<>();
        List<Integer> itemIds = new ArrayList<>();
        List<Integer> orderIds = new ArrayList<>();
        List<Integer> userIds = new ArrayList<>();
        List<String> userNames = new ArrayList<>();
        List<Boolean> enableReviews = new ArrayList<>();

        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getReviewListResponse(pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            reviewIds.addAll(jsonPath.getList("id"));
            itemNames.addAll(jsonPath.getList("itemName"));
            reviewTitles.addAll(jsonPath.getList("title"));
            reviewDescriptions.addAll(jsonPath.getList("description"));
            itemIds.addAll(jsonPath.getList("itemId"));
            orderIds.addAll(jsonPath.getList("orderId"));
            userIds.addAll(jsonPath.getList("userId"));
            userNames.addAll(jsonPath.getList("userName"));
            enableReviews.addAll(jsonPath.getList("enableReview"));
        }

        info.setReviewIds(reviewIds);
        info.setItemNames(itemNames);
        info.setItemIds(itemIds);
        info.setReviewTitles(reviewTitles);
        info.setReviewDescriptions(reviewDescriptions);
        info.setUserIds(userIds);
        info.setUserNames(userNames);
        info.setOrderIds(orderIds);
        info.setEnableReviews(enableReviews);
        return info;
    }

    public int getHideOnOnlineStoreReviewId() {
        ReviewManagementInfo info = getReviewManagementInfo();
        List<Integer> reviewIds = info.getReviewIds();
        List<Boolean> enableReviews = info.getEnableReviews();
        return reviewIds.stream()
                .filter(reviewId -> !enableReviews.get(reviewIds.indexOf(reviewId)))
                .findFirst()
                .orElse(0);
    }

    public int getShowOnOnlineStoreReviewId() {
        ReviewManagementInfo info = getReviewManagementInfo();
        List<Integer> reviewIds = info.getReviewIds();
        List<Boolean> enableReviews = info.getEnableReviews();
        return reviewIds.stream()
                .filter(reviewID -> enableReviews.get(reviewIds.indexOf(reviewID)))
                .findFirst()
                .orElse(0);
    }
}
