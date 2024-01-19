package api.dashboard.setting;

import api.dashboard.login.Login;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.PropertiesUtil;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.CreatePermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionAPI {
    String CREATE_GROUP_PERMISSION_PATH = "/storeservice/api/authorized-group-permissions/store/%s";
    String EDIT_GROUP_PERMISSION_PATH = CREATE_GROUP_PERMISSION_PATH + "/group/%s";
    String GRANT_GROUP_PERMISSION_TO_STAFF_PATH = "/storeservice/api/store_staffs/add-staff-to-permission-group/%s";
    String REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH = "/storeservice/api/store_staffs/remove-staff-from-permission-group/%s/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;

    @Data
    static
    class PermissionModel {
        private String level1;
        private String level2;
        private int binaryString;
    }

    public PermissionAPI(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
    }

    private List<PermissionModel> getPermissionTree(CreatePermission createPermission) {
        List<PermissionModel> permissionModels = new ArrayList<>();
        Map<String, String> permissionTreeMap = new ObjectMapper().convertValue(createPermission, new TypeReference<>() {
        });
        for (String permissionKey : permissionTreeMap.keySet()) {
            PermissionModel model = new PermissionModel();
            model.setLevel1(permissionKey.split("_")[0]);
            model.setLevel2(permissionKey.split("_")[1]);
            model.setBinaryString(Integer.parseInt(permissionTreeMap.get(permissionKey), 2));
            permissionModels.add(model);
        }
        return permissionModels;
    }

    private String getPermission(String firstLevel, String secondLevel, int binaryPermission) {
        return """
                {
                	"firstLevel": "%s",
                	"secondLevel": "%s",
                	"permissionInBinary": %s
                }""".formatted(firstLevel, secondLevel, binaryPermission);
    }

    private List<String> getAllPermission(CreatePermission createPermission) {
        List<PermissionModel> permissionModels = getPermissionTree(createPermission);
        return permissionModels.stream().map(model -> getPermission(model.getLevel1(), model.getLevel2(), model.getBinaryString())).toList();
    }

    public JsonPath createGroupPermission(String name, String description, CreatePermission model) {
        String body = """
                {
                     "name": "%s",
                     "description": "%s",
                     "storeId": "%s",
                     "permissions": %s
                 }""".formatted(name, description, loginInfo.getStoreID(), getAllPermission(model));

        return api.post(CREATE_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath();
    }

    public int createGroupPermissionAndGetID(String name, String description, CreatePermission model) {
        return createGroupPermission(name, description, model).getInt("id");
    }

    public JsonPath editGroupPermission(int id, String name, String description, CreatePermission model) {
        String body = """
                {
                    "id": "%s",
                    "name": "%s",
                    "description": "%s",
                    "storeId": "%s",
                    "permissions": %s
                }""".formatted(id, name, description, loginInfo.getStoreID(), getAllPermission(model));

        return api.put(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), id), loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();
    }

    public int editGroupPermissionAndGetID(int groupID, String name, String description, CreatePermission model) {
        return editGroupPermission(groupID, name, description, model).getInt("id");
    }


    public void deleteGroupPermission(int groupID) {
        Response response = api.delete(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), groupID), loginInfo.getAccessToken());
        response.then().statusCode(204);
    }

    public void grantGroupPermissionToStaff(int staffID, int groupID) {
        String body = """
                      {
                "staffIds": "%s"
                      }""".formatted(staffID);
        Response response = api.post(GRANT_GROUP_PERMISSION_TO_STAFF_PATH.formatted(groupID), loginInfo.getAccessToken(), body);
        response.then().statusCode(200);
    }

    public void removeGroupPermissionFromStaff(int staffID, int groupID) {
        Response response = api.delete(REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH.formatted(groupID, staffID), loginInfo.getAccessToken());
        response.then().statusCode(200);
    }

    public static void main(String[] args) {
        PropertiesUtil.setEnvironment("STAG");

        LoginInformation ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
        LoginInformation staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();

        LoginDashboardInfo staffLoginInfo = new Login().getInfo(staffCredentials);

        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setPromotion_discountCode("1101000000");
        model.setGoWallet_none("010");
        model.setGoChat_smsCampaign("0111111");
        model.setReservation_posService("111111111111111");
        model.setCashbook_none("111111111111");

        int groupPermissionId = new PermissionAPI(ownerCredentials).createGroupPermissionAndGetID("Create Tien's Permission", "Create Description Tien's Permission", model);
        System.out.println(groupPermissionId);

        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Tien's Permission", "Description Tien's Permission", model);

        int staffId = new StaffManagement(ownerCredentials).getStaffId(staffLoginInfo.getSellerID());

        new PermissionAPI(ownerCredentials).grantGroupPermissionToStaff(staffId, groupPermissionId);

        new PermissionAPI(ownerCredentials).removeGroupPermissionFromStaff(staffId, groupPermissionId);

        new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
    }

}
