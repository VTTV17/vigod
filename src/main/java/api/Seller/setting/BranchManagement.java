package api.Seller.setting;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class BranchManagement {

    String GET_ALL_BRANCH_PATH = "/storeservice/api/store-branch/full?storeId=%s&page=0&size=100";
    String UPDATE_BRANCH_INFORMATION_PATH = "/storeservice/api/store-branch/%s";
    String changeBranchStatusPath = "/storeservice/api/store-branch/setting-status/%s/%s?status=%s";
    String getDestinationBranchesPath = "/storeservice/api/store/branches/%s";
    String GET_BRANCH_FREE = "/storeservice/api/store-branch/list/%s?page=0&size=20&branchType=FREE";
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    BranchInfo brInfo;
    Logger logger = LogManager.getLogger(BranchManagement.class);
    @Getter
    private static final Cache<String, BranchInfo> branchCache = CacheBuilder.newBuilder().build();
    API api = new API();

    public BranchManagement(LoginInformation loginInformation, LoginDashboardInfo... loginInfo) {
        this.loginInformation = loginInformation;
        this.loginInfo = loginInfo.length == 0
                ? new Login().getInfo(loginInformation)
                : loginInfo[0];
    }

    JsonPath getBranchInfoResponseJsonPath() {
        // get all branches response
        return api.get(GET_ALL_BRANCH_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();
    }

    public BranchInfo getInfo() {
        // init branch info model
        BranchInfo brInfo = branchCache.getIfPresent(loginInfo.getStaffPermissionToken());

        if (brInfo == null) {
            // if staff token is changed, clear cache
            if (!loginInfo.getStaffPermissionToken().isEmpty()) {
                BranchInfo tempInfo = branchCache.getIfPresent("");
                branchCache.invalidateAll();
                if (Optional.ofNullable(tempInfo).isPresent()) {
                    branchCache.put("", tempInfo);
                }
            }
            // init branch info model
            brInfo = new BranchInfo();

            // using API to get branch information
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
            // set hide branch
            brInfo.setIsHideOnStoreFront(isHideOnSF.stream().map(hideOnSF -> Optional.ofNullable(hideOnSF).orElse(false)).toList());

            // set all branches status
            brInfo.setAllBranchStatus(resPath.getList("branchStatus"));

            // init temp arr
            List<String> brNames = brInfo.getBranchName();
            List<String> brStatus = brInfo.getAllBranchStatus();

            // set active branches list
            brInfo.setActiveBranches(brNames.stream().filter(brName -> brStatus.get(brNames.indexOf(brName)).equals("ACTIVE")).toList());

            // save cache
            branchCache.put(loginInfo.getStaffPermissionToken(), brInfo);
        }
        // return branch info
        return brInfo;
    }

    private void updateBranchInfo(int brID, boolean isDefault, boolean hideOnStoreFront, String branchStatus) {
        int index = brInfo.getBranchID().indexOf(brID);
        String branchName = brInfo.getBranchName().get(index);

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
                brInfo.getAllBranchStatus().get(index),
                loginInfo.getStoreName(),
                hideOnStoreFront,
                brInfo.getCountryCode().get(index));
        api.put(UPDATE_BRANCH_INFORMATION_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(200);
        logger.info("[API]Change '%s' setting hide on store online: %s.".formatted(branchName, hideOnStoreFront));

        api.put(changeBranchStatusPath.formatted(loginInfo.getStoreID(), brID, branchStatus), loginInfo.getAccessToken());
        logger.info("[API]Change '%s' status: %s.".formatted(branchName, branchStatus));

        // clear cache to get new info
        branchCache.invalidateAll();

        // get latest branch info
        brInfo = getInfo();
    }

    public void inactiveAllPaidBranches() {
        // get current branch info
        brInfo = getInfo();

        // inactive all paid branches
        IntStream.range(1, brInfo.getBranchID().size()).forEachOrdered(i -> updateBranchInfo(brInfo.getBranchID().get(i), false, brInfo.getIsHideOnStoreFront().get(i), "INACTIVE"));
    }

    public void activeAndShowAllPaidBranchesOnShopOnline() {
        // get current branch info
        brInfo = getInfo();

        // active and show all paid branches on shop online
        IntStream.range(1, brInfo.getBranchID().size()).forEachOrdered(i -> updateBranchInfo(brInfo.getBranchID().get(i), false, false, "ACTIVE"));
    }

    public BranchManagement hideFreeBranchOnShopOnline() {
        // get current branch info
        brInfo = getInfo();

        // hide free branch on shop online
        updateBranchInfo(brInfo.getBranchID().get(0), true, true, "ACTIVE");
        return this;
    }

    public BranchManagement showFreeBranchOnShopOnline() {
        // get current branch info
        brInfo = getInfo();

        // show free branch on shop online
        updateBranchInfo(brInfo.getBranchID().get(0), true, false, "ACTIVE");
        return this;
    }

    public BranchInfo getDestinationBranchesInfo() {
        // get current branch info
        brInfo = getInfo();

        // init branch info model
        BranchInfo brInfo = new BranchInfo();

        // using API to get branch information
        JsonPath resPath = api.get(getDestinationBranchesPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        // set branch index
        brInfo.setBranchID(resPath.getList("id"));

        // set branch name
        brInfo.setBranchName(resPath.getList("name"));

        // return branch info
        return brInfo;
    }

    public List<String> getBranchNameById(List<Integer> branchIds) {
        JsonPath resPath = getBranchInfoResponseJsonPath();
        List<String> branchNames = new ArrayList<>();
        for (int branchId : branchIds) {
            String branchName = resPath.getString("find {it.id == %s}.name".formatted(branchId));
            branchNames.add(branchName);
        }
        Collections.sort(branchNames);
        return branchNames;
    }

    public int getFreeBranch() {
        Response response = api.get(GET_BRANCH_FREE.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return (int) response.jsonPath().getList("id").get(0);
    }
}
