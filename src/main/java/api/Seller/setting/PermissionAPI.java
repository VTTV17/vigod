package api.Seller.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.utils.PropertiesUtil;

public class PermissionAPI {
    String CREATE_GROUP_PERMISSION_PATH = "/storeservice/api/authorized-group-permissions/store/%s";
    String EDIT_GROUP_PERMISSION_PATH = CREATE_GROUP_PERMISSION_PATH + "/group/%s";
    String GRANT_GROUP_PERMISSION_TO_STAFF_PATH = "/storeservice/api/store_staffs/add-staff-to-permission-group/%s";
    String REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH = "/storeservice/api/store_staffs/remove-staff-from-permission-group/%s/%s";
    String GET_GROUP_PERMISSIONS_OF_STAFF_PATH = "/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=0&size=50&sort=id,desc&keyword=";
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

    /**
     * Creates a new permission group and retrieves its assigned ID.
     * @param name The name of the new permission group.
     * @param description A brief description of the group.
     * @param model An instance of the `CreatePermission` model containing specific permission assignments.
     * @return The integer ID of the newly created permission group.
     */
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

    /**
     * Modifies an existing permission group and retrieves its updated ID.
     * @param id The ID of the permission group to modify.
     * @param name The updated name of the permission group.
     * @param description The updated description of the group.
     * @param model An instance of the `CreatePermission` model containing updated permission assignments.
     * @return The integer ID of the updated permission group.
     */
    public int editGroupPermissionAndGetID(int groupID, String name, String description, CreatePermission model) {
        return editGroupPermission(groupID, name, description, model).getInt("id");
    }

    /**
     * Deletes an existing permission group.
     * @param groupID The ID of the permission group to delete.
     */
    public void deleteGroupPermission(int groupID) {
        Response response = api.delete(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), groupID), loginInfo.getAccessToken());
        response.then().statusCode(204);
    }

    /**
     * Assigns a specific permission group to a designated staff member.
     * @param staffID The ID of the staff member to receive the permission group.
     * @param groupID The ID of the permission group to grant to the staff member.
     */
    public void grantGroupPermissionToStaff(int staffID, int groupID) {
        String body = """
                      {
                "staffIds": "%s"
                      }""".formatted(staffID);
        Response response = api.post(GRANT_GROUP_PERMISSION_TO_STAFF_PATH.formatted(groupID), loginInfo.getAccessToken(), body);
        response.then().statusCode(200);
    }

    /**
     * Revokes a specific permission group from a designated staff member.
     * @param staffID The ID of the staff member from whom to revoke the permission group.
     * @param groupID The ID of the permission group to revoke from the staff member.
     */
    public void removeGroupPermissionFromStaff(int staffID, int groupID) {
        Response response = api.delete(REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH.formatted(groupID, staffID), loginInfo.getAccessToken());
        response.then().statusCode(200);
    }

    /**
     * Retrieves a list of all permission group IDs assigned to a specific staff member.
     * @param staffID The ID of the staff member whose permission groups to retrieve.
     * @return A list of Integer objects representing the IDs of the staff member's permission groups.
     * An empty list will be returned if the staff member has no permission groups or if an error occurs.
     */
    public List<Integer> getAllPermissionGroupsOfStaff(int staffID) {
    	Response response = api.get(GET_GROUP_PERMISSIONS_OF_STAFF_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	String permissionGroups = response.jsonPath().getString("find { it.id == %s }.permissionGroupIds".formatted(staffID));
    	return permissionGroups == null? new ArrayList<Integer>() : Arrays.asList(permissionGroups.split(",")).stream().map(Integer::valueOf).collect(Collectors.toList());
    }    
    
    /**
     * Revokes all permission groups assigned to a given staff member.
     * @param staffID The ID of the staff member whose permission groups to revoke.
     */
    public void removeAllGroupPermissionsFromStaff(int staffID) {
    	for (int id : getAllPermissionGroupsOfStaff(staffID)) {
    		removeGroupPermissionFromStaff(staffID, id);
    	}
    }    
    
    public static void main(String[] args) {
        PropertiesUtil.setEnvironment("STAG");
        
        //Set login info of seller and staff
        LoginInformation ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
        LoginInformation staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();

        //Get info of staff
        LoginDashboardInfo staffLoginInfo = new Login().getInfo(staffCredentials);
        
        //Get staff id
        int staffId = new StaffManagement(ownerCredentials).getStaffId(staffLoginInfo.getSellerID());
        
        //Remove all permission groups from the staff
        new PermissionAPI(ownerCredentials).removeAllGroupPermissionsFromStaff(staffId);
        
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("01");
        model.setProduct_productManagement("010");
        model.setCashbook_none("111111111111");

        //Create a permisison
        int groupPermissionId = new PermissionAPI(ownerCredentials).createGroupPermissionAndGetID("Create Tien's Permission", "Create Description Tien's Permission", model);

        //Edit the permission
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Tien's Permission", "Description Tien's Permission", model);

        //Grant the permission to the staff
        new PermissionAPI(ownerCredentials).grantGroupPermissionToStaff(staffId, groupPermissionId);
        
        //Get info of the staff after being granted the permission
        LoginDashboardInfo staffLoginInfo1 = new Login().getInfo(staffCredentials);
        
        //See if the staff has permissions to perform some actions
        System.out.println("Is staff able to change language: " + new AllPermissions(staffLoginInfo1.getStaffPermissionToken()).getHome().isChangLanguage());
        System.out.println("Is staff able to see notifications: " + new AllPermissions(staffLoginInfo1.getStaffPermissionToken()).getHome().isNotification());
        
        //Remove the permission group from the staff
        new PermissionAPI(ownerCredentials).removeGroupPermissionFromStaff(staffId, groupPermissionId);

        //Delete the permission group
        new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
    }

}
