package api.dashboard.setting;

import api.dashboard.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BranchManagement {

    String GET_ALL_BRANCH_PATH = "/storeservice/api/store-branch/full?storeId=%s&page=0&size=100";
    String UPDATE_BRANCH_INFORMATION_PATH = "/storeservice/api/store-branch/%s";
    
    JsonPath getBranchInfoResponseJsonPath() {
        // getListElementId login dashboard information
        LoginDashboardInfo loginInfo = new Login().getInfo();

        // getListElementId all branches response
        Response branchRes = new API().get(GET_ALL_BRANCH_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        branchRes.then().statusCode(200);
        return branchRes.jsonPath();
    }

//    public void getBranchInformation() {
//        apiBranchID = getBranchInfoResponseJsonPath().getList("id");
//        apiBranchName = getBranchInfoResponseJsonPath().getList("name");
//        apiBranchCode = getBranchInfoResponseJsonPath().getList("code");
//        apiBranchAddress = getBranchInfoResponseJsonPath().getList("address");
//        apiWardCode = getBranchInfoResponseJsonPath().getList("ward");
//        apiDistrictCode = getBranchInfoResponseJsonPath().getList("district");
//        apiCityCode = getBranchInfoResponseJsonPath().getList("city");
//        apiPhoneNumberFirst = getBranchInfoResponseJsonPath().getList("phoneNumberFirst");
//        apiCountryCode = getBranchInfoResponseJsonPath().getList("countryCode");
//        apiIsDefaultBranch = getBranchInfoResponseJsonPath().getList("default");
//        apiIsHideOnStoreFront = getBranchInfoResponseJsonPath().getList("hideOnStoreFront");
//        IntStream.range(0, apiIsHideOnStoreFront.size()).filter(i -> apiIsHideOnStoreFront.getListElementId(i) == null).forEachOrdered(i -> apiIsHideOnStoreFront.set(i, false));
//        apiAllBranchStatus = getBranchInfoResponseJsonPath().getList("branchStatus");
//        apiActiveBranches = new ArrayList<>();
//        IntStream.range(0, apiAllBranchStatus.size()).filter(i -> apiAllBranchStatus.getListElementId(i).equals("ACTIVE")).forEach(i -> apiActiveBranches.add(apiBranchName.getListElementId(i)));
//
//    }

    public BranchInfo getInfo() {
        // init branch info model
        BranchInfo brInfo = new BranchInfo();

        // using API to getListElementId branch information
        JsonPath resPath = getBranchInfoResponseJsonPath();

        // set branch index
        brInfo.setBranchID(resPath.getList("id"));

        // set branch name
        brInfo.setBranchName(resPath.getList("name"));

        // set branch code
        brInfo.setBranchCode(resPath.getList("code"));

        // set branch address
        brInfo.setBranchAddress(resPath.getList("address"));

        // set branch ward
        brInfo.setWardCode(resPath.getList("ward"));

        // set branch district
        brInfo.setDistrictCode(resPath.getList("district"));

        // set branch city
        brInfo.setCityCode(resPath.getList("city"));

        // set branch country
        brInfo.setCountryCode(resPath.getList("countryCode"));

        // set branch phone
        brInfo.setPhoneNumberFirst(resPath.getList("phoneNumberFirst"));

        // set branch name default
        brInfo.setIsDefaultBranch(resPath.getList("default"));

        // init temp hide branch on SF setting
        List<Boolean> isHideOnSF = resPath.getList("hideOnStoreFront");
        // convert temp to boolean
        IntStream.range(0, isHideOnSF.size()).filter(i -> isHideOnSF.get(i) == null).forEachOrdered(i -> isHideOnSF.set(i, false));
        // set hide branch
        brInfo.setIsHideOnStoreFront(isHideOnSF);

        // set all branches status
        brInfo.setAllBranchStatus(resPath.getList("branchStatus"));

        // init temp active branch
        List<String> activeBranches = new ArrayList<>();
        // getListElementId temp active branch
        IntStream.range(0, brInfo.getAllBranchStatus().size()).filter(i -> brInfo.getAllBranchStatus().get(i).equals("ACTIVE")).forEach(i -> activeBranches.add(brInfo.getBranchName().get(i)));
        // set active branches list
        brInfo.setActiveBranches(activeBranches);

        // return branch info
        return brInfo;
    }

    private void updateBranchInfo(int brID, boolean isDefault, boolean hideOnStoreFront, String branchStatus) {
        // getListElementId branch information
        BranchInfo brInfo = getInfo();
        int index = brInfo.getBranchID().indexOf(brID);

        // getListElementId login information
        LoginDashboardInfo loginInfo = new Login().getInfo();

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
                  }""".formatted(brID,
                brInfo.getBranchName().get(index),
                loginInfo.getStoreID(),
                brInfo.getBranchCode().get(index),
                brInfo.getBranchAddress().get(index),
                brInfo.getWardCode().get(index),
                brInfo.getDistrictCode().get(index),
                brInfo.getCityCode().get(index),
                brInfo.getPhoneNumberFirst().get(index),
                isDefault,
                branchStatus,
                loginInfo.getStoreName(),
                hideOnStoreFront,
                brInfo.getCountryCode().get(index));
        new API().put(UPDATE_BRANCH_INFORMATION_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(200);
    }

    public void inactiveAllPaidBranches() {
        // getListElementId current branch information
        BranchInfo brInfo = getInfo();

        // inactive all paid branches
        IntStream.range(1, brInfo.getBranchID().size()).forEachOrdered(i -> updateBranchInfo(brInfo.getBranchID().get(i), false, brInfo.getIsHideOnStoreFront().get(i), "INACTIVE"));
    }

    public void activeAndShowAllPaidBranchesOnShopOnline() {
        // getListElementId current branch information
        BranchInfo brInfo = getInfo();

        // active and show all paid branches on shop online
        IntStream.range(1, brInfo.getBranchID().size()).forEachOrdered(i -> updateBranchInfo(brInfo.getBranchID().get(i), false, false, "ACTIVE"));
    }

    public BranchManagement hideFreeBranchOnShopOnline() {
        // getListElementId current branch information
        BranchInfo brInfo = getInfo();

        // hide free branch on shop online
        updateBranchInfo(brInfo.getBranchID().get(0), true, true, "ACTIVE");
        return this;
    }
    public BranchManagement showFreeBranchOnShopOnline() {
        // getListElementId current branch information
        BranchInfo brInfo = getInfo();

        // show free branch on shop online
        updateBranchInfo(brInfo.getBranchID().get(0), true, false, "ACTIVE");
        return this;
    }
}
