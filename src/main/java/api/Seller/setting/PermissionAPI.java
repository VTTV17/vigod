package api.Seller.setting;

import api.Seller.login.Login;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.CreatePermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PermissionAPI {
	final static Logger logger = LogManager.getLogger(PermissionAPI.class);
	
    String CREATE_GROUP_PERMISSION_PATH = "/storeservice/api/authorized-group-permissions/store/%s";
    String EDIT_GROUP_PERMISSION_PATH = CREATE_GROUP_PERMISSION_PATH + "/group/%s";
    String GRANT_GROUP_PERMISSION_TO_STAFF_PATH = "/storeservice/api/store_staffs/add-staff-to-permission-group/%s";
    String REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH = "/storeservice/api/store_staffs/remove-staff-from-permission-group/%s/%s";
    String GET_GROUP_PERMISSIONS_OF_STAFF_PATH = "/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=0&size=50&sort=id,desc&keyword=";
    API api = new API();
    LoginDashboardInfo loginInfo;
    private static LoginInformation staffCredentials;


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

    public static void setStaffCredentials(LoginInformation staffCredentials1) {
    	staffCredentials = staffCredentials1;
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
     * @param groupID The ID of the permission group to modify.
     * @param name The updated name of the permission group.
     * @param description The updated description of the group.
     * @param model An instance of the `CreatePermission` model containing updated permission assignments.
     * @return The integer ID of the updated permission group.
     */
    public int editGroupPermissionAndGetID(int groupID, String name, String description, CreatePermission model) {
        // clear cache
        Login.getLoginCache().invalidate(staffCredentials);

        return editGroupPermission(groupID, name, description, model).getInt("id");
    }

    /**
     * Deletes an existing permission group.
     * @param groupID The ID of the permission group to delete.
     */
    public void deleteGroupPermission(int groupID) {
        Response response = api.delete(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), groupID), loginInfo.getAccessToken());
        response.then().statusCode(204);
        logger.info("Deleted permission group: " + groupID);
    }
    public void deleteGroupPermission(List<Integer>groupIds){
        for (int groupId:groupIds) {
            deleteGroupPermission(groupId);
        }
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
        logger.info("Granted permission group '%s' to staff '%s'".formatted(groupID, staffID));
    }

    /**
     * Revokes a specific permission group from a designated staff member.
     * @param staffID The ID of the staff member from whom to revoke the permission group.
     * @param groupID The ID of the permission group to revoke from the staff member.
     */
    public void removeGroupPermissionFromStaff(int staffID, int groupID) {
        Response response = api.delete(REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH.formatted(groupID, staffID), loginInfo.getAccessToken());
        response.then().statusCode(200);
        logger.info("Revoked permission group '%s' from staff '%s'".formatted(groupID, staffID));
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
    	return permissionGroups == null ? new ArrayList<>() : Arrays.stream(permissionGroups.split(",")).map(Integer::valueOf).toList();
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

    /**
     * Creates a new permission group with specified permissions, assigns it to the specified staff member,
     * and removes any existing permission groups from the staff member.
     * @param ownerCredentials The login credentials of the owner performing the action.
     * @param staffCredentials The login credentials of the staff member to receive the new permission group.
     * @param model The CreatePermission model defining the specific permissions to include in the group.
     * @return The ID of the newly created permission group.
     */
    public int createPermissionGroupThenGrantItToStaff(LoginInformation ownerCredentials, LoginInformation staffCredentials, CreatePermission model) {
        // add staff credentials
        PermissionAPI.staffCredentials = staffCredentials;

        // get staffId
        int staffId = new StaffManagement(ownerCredentials).getStaffId(new Login().getInfo(staffCredentials).getUserId());

        //Remove all permission groups from the staff
        removeAllGroupPermissionsFromStaff(staffId);
        int groupPermissionId = createGroupPermissionAndGetID("Permission %s for %s".formatted(System.currentTimeMillis(), staffCredentials.getEmail().split("@")[0]), "Description %s".formatted(System.currentTimeMillis()), model);
        //Grant the permission to the staff
        grantGroupPermissionToStaff(staffId, groupPermissionId);

        // delete all permission group that are not assigned to staff
        deleteNoAssignedPermissionGroup();

        // clear loginCache login
        Login.getLoginCache().invalidate(staffCredentials);

        return groupPermissionId;
    } 
    
    /**
     * Creates a new permission group with full permissions, assigns it to the specified staff member,
     * and removes any existing permission groups from the staff member.
     * @param ownerCredentials The login credentials of the owner performing the action.
     * @param staffCredentials The login credentials of the staff member to receive the new permission group.
     * @return The ID of the newly created permission group.
     */
    public int createPermissionGroupThenGrantItToStaff(LoginInformation ownerCredentials, LoginInformation staffCredentials) {
        // add staff credentials
        PermissionAPI.staffCredentials = staffCredentials;

    	return createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials, CreatePermission.getFullPermissionModel());
    }

    public void editPermissionGroupThenGrantItToStaff(LoginInformation ownerCredentials, LoginInformation staffCredentials, int groupId, CreatePermission model) {
        // add staff credentials
        PermissionAPI.staffCredentials = staffCredentials;

        // delete all permission group that are not assigned to staff
        deleteNoAssignedPermissionGroup();

        // get staffId
        int staffId = new StaffManagement(ownerCredentials).getStaffId(new Login().getInfo(staffCredentials).getUserId());

        //Remove all permission groups from the staff
        removeAllGroupPermissionsFromStaff(staffId);

        // update permission group
        int groupPermissionId = editGroupPermissionAndGetID(groupId, "Permission %s for %s".formatted(System.currentTimeMillis(), staffCredentials.getEmail().split("@")[0]), "Description %s".formatted(System.currentTimeMillis()), model);

        //Grant the permission to the staff
        grantGroupPermissionToStaff(staffId, groupPermissionId);

        // clear loginCache login
        Login.getLoginCache().invalidate(staffCredentials);
    }

    @Data
    public static class PermissionInformation {
        List<Integer> permissionIds;
        List<Integer> numberOfAssigned;
    }

    String getAllPermissionGroupPath = "/storeservice/api/authorized-group-permissions/store/%s?page=%s&size=100";

    Response getAllPermissionResponse(int pageIndex) {
        return api.get(getAllPermissionGroupPath.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public PermissionInformation getAllPermissionGroupInformation() {
        // init suggestion model
        PermissionInformation info = new PermissionInformation();

        // init temp array
        List<Integer> permissionIds = new ArrayList<>();
        List<Integer> numberOfAssigned = new ArrayList<>();

        // get total products
        int totalOfPermissions = Integer.parseInt(getAllPermissionResponse(0).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfPermissions / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            Response suggestProducts = getAllPermissionResponse(pageIndex);
            permissionIds.addAll(suggestProducts.jsonPath().getList("id"));
            numberOfAssigned.addAll(suggestProducts.jsonPath().getList("totalStaff"));
        }

        // set permission group info
        info.setPermissionIds(permissionIds);
        info.setNumberOfAssigned(numberOfAssigned);

        // return suggestion model
        return info;
    }

    void deleteNoAssignedPermissionGroup() {
        PermissionInformation info = getAllPermissionGroupInformation();
        IntStream.range(0, info.getPermissionIds().size())
                .filter(index -> info.getNumberOfAssigned().get(index) == 0)
                .map(index -> info.getPermissionIds().get(index))
                .forEach(this::deleteGroupPermission);
    }
}
