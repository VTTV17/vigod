package api.dashboard.setting;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static api.dashboard.login.Login.*;

public class BranchManagement {
    Logger logger = LogManager.getLogger(BranchManagement.class);

    List<String> branchCode;
    List<String> branchAddress;
    List<String> wardCode;
    List<String> districtCode;
    List<String> cityCode;
    List<String> phoneNumberFirst;
    List<String> countryCode;
    List<Boolean> isDefaultBranch;
    public static List<Integer> branchID;
    public static List<String> branchName;
    public static List<Boolean> isHideOnStoreFront;
    public static List<String> allBranchStatus;

    String GET_ALL_BRANCH_PATH = "/storeservice/api/store-branch/full?storeId=%s&page=0&size=100";
    String UPDATE_BRANCH_INFORMATION_PATH = "/storeservice/api/store-branch/%s";

    public void getBranchInformation() {
        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(storeID), accessToken);
        branchInfo.then().statusCode(200);

        branchID = branchInfo.jsonPath().getList("id");
        branchName = branchInfo.jsonPath().getList("name");
        branchCode = branchInfo.jsonPath().getList("code");
        branchAddress = branchInfo.jsonPath().getList("address");
        wardCode = branchInfo.jsonPath().getList("ward");
        districtCode = branchInfo.jsonPath().getList("district");
        cityCode = branchInfo.jsonPath().getList("city");
        phoneNumberFirst = branchInfo.jsonPath().getList("phoneNumberFirst");
        countryCode = branchInfo.jsonPath().getList("countryCode");
        isDefaultBranch = branchInfo.jsonPath().getList("default");
        isHideOnStoreFront = branchInfo.jsonPath().getList("hideOnStoreFront");
        IntStream.range(0, isHideOnStoreFront.size()).filter(i -> isHideOnStoreFront.get(i) == null).forEachOrdered(i -> isHideOnStoreFront.set(i, false));
        allBranchStatus = branchInfo.jsonPath().getList("branchStatus");
    }

    private void updateBranchInfo(int id, boolean isDefault, boolean hideOnStoreFront, String branchStatus) {
        int index = branchID.indexOf(id);

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
                  }""".formatted(id, branchName.get(index), storeID, branchCode.get(index), branchAddress.get(index), wardCode.get(index), districtCode.get(index), cityCode.get(index), phoneNumberFirst.get(index), isDefault, branchStatus, storeName, hideOnStoreFront, countryCode.get(index));
        new API().put(UPDATE_BRANCH_INFORMATION_PATH.formatted(storeID), accessToken, body).then().statusCode(200);
    }

    public void inactiveAllPaidBranches() {
        IntStream.range(1, branchID.size()).forEachOrdered(i -> updateBranchInfo(branchID.get(i), isDefaultBranch.get(i), isHideOnStoreFront.get(i), "INACTIVE"));
    }

    public void activeAndShowAllPaidBranchesOnShopOnline() {
        IntStream.range(1, branchID.size()).forEachOrdered(i -> updateBranchInfo(branchID.get(i), isDefaultBranch.get(i), false, "ACTIVE"));
    }

    public BranchManagement hideFreeBranchOnShopOnline() {
        updateBranchInfo(branchID.get(0), true, true, "ACTIVE");
        return this;
    }
    public BranchManagement showFreeBranchOnShopOnline() {
        updateBranchInfo(branchID.get(0), true, false, "ACTIVE");
        return this;
    }
}
