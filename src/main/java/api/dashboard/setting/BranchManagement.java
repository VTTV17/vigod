package api.dashboard.setting;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.*;

public class BranchManagement {
    List<String> apiBranchCode;
    List<String> apiBranchAddress;
    List<String> apiWardCode;
    List<String> apiDistrictCode;
    List<String> apiCityCode;
    List<String> apiPhoneNumberFirst;
    List<String> apiCountryCode;
    List<Boolean> apiIsDefaultBranch;
    public static List<Integer> apiBranchID;
    public static List<String> apiBranchName;
    public static List<Boolean> apiIsHideOnStoreFront;
    public static List<String> apiAllBranchStatus;
    public static List<String> apiActiveBranches;

    String GET_ALL_BRANCH_PATH = "/storeservice/api/store-branch/full?storeId=%s&page=0&size=100";
    String UPDATE_BRANCH_INFORMATION_PATH = "/storeservice/api/store-branch/%s";
    
    JsonPath getBranchInfoResponseJsonPath() {
        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(apiStoreID), accessToken);
        branchInfo.then().statusCode(200);
        return branchInfo.jsonPath();
    }

    public List<Integer> getListBranchID() {
        return getBranchInfoResponseJsonPath().getList("id");
    }

    public void getBranchInformation() {
        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(apiStoreID), accessToken);
        branchInfo.then().statusCode(200);

        apiBranchID = getBranchInfoResponseJsonPath().getList("id");
        apiBranchName = getBranchInfoResponseJsonPath().getList("name");
        apiBranchCode = getBranchInfoResponseJsonPath().getList("code");
        apiBranchAddress = getBranchInfoResponseJsonPath().getList("address");
        apiWardCode = getBranchInfoResponseJsonPath().getList("ward");
        apiDistrictCode = getBranchInfoResponseJsonPath().getList("district");
        apiCityCode = getBranchInfoResponseJsonPath().getList("city");
        apiPhoneNumberFirst = getBranchInfoResponseJsonPath().getList("phoneNumberFirst");
        apiCountryCode = getBranchInfoResponseJsonPath().getList("countryCode");
        apiIsDefaultBranch = getBranchInfoResponseJsonPath().getList("default");
        apiIsHideOnStoreFront = getBranchInfoResponseJsonPath().getList("hideOnStoreFront");
        IntStream.range(0, apiIsHideOnStoreFront.size()).filter(i -> apiIsHideOnStoreFront.get(i) == null).forEachOrdered(i -> apiIsHideOnStoreFront.set(i, false));
        apiAllBranchStatus = getBranchInfoResponseJsonPath().getList("branchStatus");
        apiActiveBranches = new ArrayList<>();
        IntStream.range(0, apiAllBranchStatus.size()).filter(i -> apiAllBranchStatus.get(i).equals("ACTIVE")).forEach(i -> apiActiveBranches.add(apiBranchName.get(i)));

    }

    private void updateBranchInfo(int id, boolean isDefault, boolean hideOnStoreFront, String branchStatus) {
        int index = apiBranchID.indexOf(id);

        String body = """
                {
                      "createdDate": "2022-12-19T03:29:35.655Z",
                      "lastModifiedDate": "2022-12-19T03:29:35.655Z",
                      "id": %s,
                      "name": "%s",
                      "storeId": %s,
                      "code": "%s",
                      "address": "%s",
                      "ward": "%s",
                      "district": "%s",
                      "city": "%s",
                      "phoneNumberFirst": "%s",
                      "email": "",
                      "isDefault": %s,
                      "branchStatus": "%s",
                      "storeName": "%s",
                      "hideOnStoreFront": %s,
                      "countryCode": "%s"
                  }""".formatted(id, apiBranchName.get(index), apiStoreID, apiBranchCode.get(index), apiBranchAddress.get(index), apiWardCode.get(index), apiDistrictCode.get(index), apiCityCode.get(index), apiPhoneNumberFirst.get(index), isDefault, branchStatus, apiStoreName, hideOnStoreFront, apiCountryCode.get(index));
        new API().put(UPDATE_BRANCH_INFORMATION_PATH.formatted(apiStoreID), accessToken, body).then().statusCode(200);
    }

    public void inactiveAllPaidBranches() {
        // get current branch information
        getBranchInformation();

        // inactive all paid branches
        IntStream.range(1, apiBranchID.size()).forEachOrdered(i -> updateBranchInfo(apiBranchID.get(i), false, apiIsHideOnStoreFront.get(i), "INACTIVE"));

        // get branch information after update
        getBranchInformation();
    }

    public void activeAndShowAllPaidBranchesOnShopOnline() {
        // get current branch information
        getBranchInformation();

        // active and show all paid branches on shop online
        IntStream.range(1, apiBranchID.size()).forEachOrdered(i -> updateBranchInfo(apiBranchID.get(i), false, false, "ACTIVE"));

        // get branch information after update
        getBranchInformation();
    }

    public BranchManagement hideFreeBranchOnShopOnline() {
        // get current branch information
        getBranchInformation();

        // hide free branch on shop online
        updateBranchInfo(apiBranchID.get(0), true, true, "ACTIVE");
        return this;
    }
    public BranchManagement showFreeBranchOnShopOnline() {
        // get current branch information
        getBranchInformation();

        // show free branch on shop online
        updateBranchInfo(apiBranchID.get(0), true, false, "ACTIVE");
        return this;
    }
}
