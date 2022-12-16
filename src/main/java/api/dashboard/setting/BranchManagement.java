package api.dashboard.setting;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import java.util.List;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;

public class BranchManagement {
    Logger logger = LogManager.getLogger(BranchManagement.class);

    List<String> branchCode;
    List<String> branchAddress;
    List<String> wardCode;
    List<String> districtCode;
    List<String> cityCode;
    List<String> countryCode;
    List<Boolean> isDefaultBranch;
    public static List<Integer> branchID;
    public static List<Boolean> isHideOnStoreFront;
    public static List<String> branchStatus;

    String GET_ALL_BRANCH_PATH = "/storeservice/api/store-branch/full?storeId=%s&page=0&size=100";

    public void getBranchInformation() {
        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(storeID), accessToken);
        branchInfo.then().statusCode(200);

        branchID = branchInfo.jsonPath().getList("id");
        branchCode = branchInfo.jsonPath().getList("code");
        branchAddress = branchInfo.jsonPath().getList("address");
        wardCode = branchInfo.jsonPath().getList("ward");
        districtCode = branchInfo.jsonPath().getList("district");
        cityCode = branchInfo.jsonPath().getList("city");
        countryCode = branchInfo.jsonPath().getList("countryCode");
        isDefaultBranch = branchInfo.jsonPath().getList("default");
        isHideOnStoreFront = branchInfo.jsonPath().getList("hideOnStoreFront");
        branchStatus = branchInfo.jsonPath().getList("branchStatus");
    }

    public void updateBranchInfo() {
        String body = """
                {
                     "id": %s,
                     "name": "%s",
                     "code": "%s",
                     "address": "%s",
                     "ward": "%s",
                     "district": "%s",
                     "city": "%s",
                     "phoneNumberFirst": "%s",
                     "isDefault": %s,
                     "hideOnStoreFront": %s,
                     "countryCode": "%s",
                     "branchStatus": "%s"
                 }""";

        Response branchInfo = new API().get(GET_ALL_BRANCH_PATH.formatted(storeID), accessToken);
        branchInfo.then().statusCode(200);
        List<Object> list = branchInfo.jsonPath().getList("");
        Object o = list.stream().filter(branch -> branch.toString().contains("131120")).findFirst();

        logger.info(new API().put("/storeservice/api/store-branch/129149", accessToken, o).asPrettyString());
    }
}
