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
import java.util.stream.IntStream;

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

    public ProductLocationInfo getProductLocationThatHaveNoChildLocation(List<Integer> assignedBranchIds) {
        ProductLocationInfo locationInfo = new ProductLocationInfo();

        // get all location info
        LocationManagementInfo allLocation = getAllLocationInformation();

        // get locationId
        List<Integer> locationIds = allLocation.getLocationIds();
        List<Integer> branchIds = allLocation.getBranchIds();
        List<String> branchNames = allLocation.getBranchNames();
        List<String> locationCodes = allLocation.getLocationCodes();
        List<Integer> parentIds = allLocation.getParentIds();

        IntStream.range(0, locationIds.size())
                .filter(index -> assignedBranchIds.contains(branchIds.get(index))
                        && !parentIds.contains(locationIds.get(index))).forEach(index -> {
                    locationInfo.setId(locationIds.get(index));
                    locationInfo.setBranchId(branchIds.get(index));
                    locationInfo.setBranchName(branchNames.get(index));
                    locationInfo.setLocationCode(locationCodes.get(index));
                });
        return locationInfo;
    }

    @Data
    public static class AllProductLocationInfo {
        private List<Integer> ids;
        private List<String> locationNames;
        private List<String> locationCodes;
        private List<Integer> quantity;
        private List<String> locationPaths;
        private List<String> locationPathNames;
    }

    @Data
    public static class ProductLocationInfo {
        private Integer id;
        private String locationName;
        private String locationCode;
        private Integer quantity;
        private String locationPath;
        private String locationPathName;
        private String branchName;
        private int branchId;
    }

    String productLocationPath = "/itemservice/api/locations/store/%s/search-receipt?page=%s&size=100";

    Response getProductLocationResponse(int pageIndex, int itemId, int modelId, int branchId, String locationReceiptType) {
        String body = """
                {
                    "itemId": %s,
                    "modelId": %s,
                    "branchId": "%s",
                    "locationReceiptType": "%s"
                }""".formatted(itemId, (modelId == 0) ? "" : modelId, branchId, locationReceiptType);
        return api.post(productLocationPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken(), body);
    }

    public AllProductLocationInfo getAllProductLocationInfo(int itemId, int modelId, int branchId, String locationReceiptType) {
        // init model
        AllProductLocationInfo info = new AllProductLocationInfo();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> locationNames = new ArrayList<>();
        List<String> locationCodes = new ArrayList<>();
        List<Integer> quantity = new ArrayList<>();
        List<String> locationPaths = new ArrayList<>();
        List<String> locationPathNames = new ArrayList<>();

        // get total products
        int totalOfLotDate = Integer.parseInt(getProductLocationResponse(0, itemId, modelId, branchId, locationReceiptType).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfLotDate / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getProductLocationResponse(pageIndex, itemId, modelId, branchId, locationReceiptType)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            locationNames.addAll(jPath.getList("locationName"));
            locationCodes.addAll(jPath.getList("locationCode"));
            quantity.addAll(jPath.getList("quantity"));
            locationPaths.addAll(jPath.getList("locationPath"));
            locationPathNames.addAll(jPath.getList("locationPathName"));
        }

        // set result
        info.setIds(ids);
        info.setLocationNames(locationNames);
        info.setLocationCodes(locationCodes);
        info.setQuantity(quantity);
        info.setLocationPaths(locationPaths);
        info.setLocationPathNames(locationPathNames);

        return info;
    }

    public ProductLocationInfo getLocation(int itemId, int modelId, int branchId, String locationReceiptType) {
        AllProductLocationInfo searchInfo = getAllProductLocationInfo(itemId, modelId, branchId, locationReceiptType);
        ProductLocationInfo info = new ProductLocationInfo();

        if (!searchInfo.getIds().isEmpty()) {
            info.setId(searchInfo.getIds().get(0));
            info.setLocationCode(searchInfo.getLocationCodes().get(0));
            info.setLocationName(searchInfo.getLocationNames().get(0));
            info.setQuantity(searchInfo.getQuantity().get(0));
            info.setLocationPath(searchInfo.getLocationPaths().get(0));
            info.setLocationPathName(searchInfo.getLocationPathNames().get(0));
        }

        return info;
    }

    public ProductLocationInfo getLocationInStock(int itemId, int modelId, int branchId, String locationReceiptType) {
        AllProductLocationInfo searchInfo = getAllProductLocationInfo(itemId, modelId, branchId, locationReceiptType);
        ProductLocationInfo info = new ProductLocationInfo();
        for (int index = 0; index < searchInfo.getIds().size(); index++) {
            if (searchInfo.getQuantity().get(index) > 0) {
                info.setId(searchInfo.getIds().get(index));
                info.setLocationCode(searchInfo.getLocationCodes().get(index));
                info.setLocationName(searchInfo.getLocationNames().get(index));
                info.setQuantity(searchInfo.getQuantity().get(index));
                info.setLocationPath(searchInfo.getLocationPaths().get(index));
                info.setLocationPathName(searchInfo.getLocationPathNames().get(index));

                break;
            }
        }

        return info;
    }
}
