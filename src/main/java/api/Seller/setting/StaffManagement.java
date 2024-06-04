package api.Seller.setting;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffManagement {
    String GET_STAFF_LIST = "/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=%s&size=100&sort=id,desc";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    private static final Cache<String, AllStaffInformation> staffCache = CacheBuilder.newBuilder().build();

    public StaffManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class AllStaffInformation {
        List<Integer> ids;
        List<Integer> userIds;
        List<String> emails;
        List<String> names;
        List<Boolean> enables;
    }

    public Response getAllStaffResponse(int pageIndex) {
        return api.get(GET_STAFF_LIST.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    AllStaffInformation getAllStaffInformation() {
        AllStaffInformation info = staffCache.getIfPresent(loginInfo.getStaffPermissionToken());
        if (Optional.ofNullable(info).isEmpty()) {
            // init suggestion model
            info = new AllStaffInformation();

            // init temp array
            List<Integer> ids = new ArrayList<>();
            List<Integer> userIds = new ArrayList<>();
            List<String> emails = new ArrayList<>();
            List<String> names = new ArrayList<>();
            List<Boolean> enables = new ArrayList<>();

            // get total products
            int totalOfStaffs = Integer.parseInt(getAllStaffResponse(0).getHeader("X-Total-Count"));

            // get number of pages
            int numberOfPages = totalOfStaffs / 100;

            // get all staff info
            for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
                JsonPath jsonPath = getAllStaffResponse(pageIndex).jsonPath();
                ids.addAll(jsonPath.getList("id"));
                userIds.addAll(jsonPath.getList("userId"));
                emails.addAll(jsonPath.getList("email"));
                names.addAll(jsonPath.getList("name"));
                enables.addAll(jsonPath.getList("enabled"));
            }

            // set permission group info
            info.setIds(ids);
            info.setUserIds(userIds);
            info.setNames(names);
            info.setEmails(emails);
            info.setEnables(enables);

            // save cache
            staffCache.put(loginInfo.getAccessToken(), info);
        }

        // return model
        return info;
    }

    public List<String> getAllStaffNames() {
        return getAllStaffInformation().getNames();
    }

    public int getStaffId(int userId) {
        AllStaffInformation info = getAllStaffInformation();
        return info.getIds().get(info.getUserIds().indexOf(userId));
    }

    public String getStaffName(int userId) {
        AllStaffInformation info = getAllStaffInformation();
        return info.getNames().get(info.getUserIds().indexOf(userId));
    }
}
