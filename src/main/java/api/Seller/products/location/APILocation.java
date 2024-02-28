package api.Seller.products.location;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

public class APILocation {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    API api = new API();

    public APILocation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class LocationManagementInfo {
        List<Integer> locationIds;
        List<Integer> branchIds;
        List<String> branchNames;
        List<String> locationCodes;
        List<Integer> productQuantity;
        List<Integer> lotsQuantity;
        List<Integer> numberOfProductInLocation;
        List<Integer> parentIds;
        List<String> locationPaths;
    }

    String getAllLocationPath = "/itemservice/api/dashboard/store/%s/location";

    public Response getAllLocationResponse() {
        return api.get(getAllLocationPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    }

    public LocationManagementInfo getAllLocationInformation() {
        LocationManagementInfo info = new LocationManagementInfo();

        // get location information json path
        JsonPath jsonPath = getAllLocationResponse().then()
                .statusCode(200)
                .extract()
                .jsonPath();

        // set location information into temp array
        List<Integer> locationIds = new ArrayList<>(jsonPath.getList("id"));
        List<Integer> branchIds = new ArrayList<>(jsonPath.getList("branchId"));
        List<String> branchNames = new ArrayList<>(jsonPath.getList("branchName"));
        List<String> locationCodes = new ArrayList<>(jsonPath.getList("locationCode"));
        List<Integer> productQuantity = new ArrayList<>(jsonPath.getList("quantity"));
        List<Integer> lotsQuantity = new ArrayList<>(jsonPath.getList("quantityLot"));
        List<Integer> numberOfProductInLocation = new ArrayList<>(jsonPath.getList("quantityProduct"));
        List<Integer> parentIds = new ArrayList<>(jsonPath.getList("parentId"));
        List<String> locationPaths = new ArrayList<>(jsonPath.getList("locationPath"));

        // set location information into model
        info.setLocationIds(locationIds);
        info.setLocationCodes(locationCodes);
        info.setBranchIds(branchIds);
        info.setBranchNames(branchNames);
        info.setProductQuantity(productQuantity);
        info.setLotsQuantity(lotsQuantity);
        info.setNumberOfProductInLocation(numberOfProductInLocation);
        info.setParentIds(parentIds);
        info.setLocationPaths(locationPaths);

        return info;
    }


}
