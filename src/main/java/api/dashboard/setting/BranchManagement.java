package api.dashboard.setting;

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

    public void getBranchInformation() {
        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(apiStoreID), accessToken);
        branchInfo.then().statusCode(200);

        apiBranchID = branchInfo.jsonPath().getList("id");
        apiBranchName = branchInfo.jsonPath().getList("name");
        apiBranchCode = branchInfo.jsonPath().getList("code");
        apiBranchAddress = branchInfo.jsonPath().getList("address");
        apiWardCode = branchInfo.jsonPath().getList("ward");
        apiDistrictCode = branchInfo.jsonPath().getList("district");
        apiCityCode = branchInfo.jsonPath().getList("city");
        apiPhoneNumberFirst = branchInfo.jsonPath().getList("phoneNumberFirst");
        apiCountryCode = branchInfo.jsonPath().getList("countryCode");
        apiIsDefaultBranch = branchInfo.jsonPath().getList("default");
        apiIsHideOnStoreFront = branchInfo.jsonPath().getList("hideOnStoreFront");
        IntStream.range(0, apiIsHideOnStoreFront.size()).filter(i -> apiIsHideOnStoreFront.get(i) == null).forEachOrdered(i -> apiIsHideOnStoreFront.set(i, false));
        apiAllBranchStatus = branchInfo.jsonPath().getList("branchStatus");
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
        getBranchInformation();
        IntStream.range(1, apiBranchID.size()).forEachOrdered(i -> updateBranchInfo(apiBranchID.get(i), apiIsDefaultBranch.get(i), apiIsHideOnStoreFront.get(i), "INACTIVE"));
    }

    public void activeAndShowAllPaidBranchesOnShopOnline() {
        getBranchInformation();
        IntStream.range(1, apiBranchID.size()).forEachOrdered(i -> updateBranchInfo(apiBranchID.get(i), apiIsDefaultBranch.get(i), false, "ACTIVE"));
    }

    public BranchManagement hideFreeBranchOnShopOnline() {
        getBranchInformation();
        updateBranchInfo(apiBranchID.get(0), true, true, "ACTIVE");
        return this;
    }
    public BranchManagement showFreeBranchOnShopOnline() {
        getBranchInformation();
        updateBranchInfo(apiBranchID.get(0), true, false, "ACTIVE");
        return this;
    }
}
