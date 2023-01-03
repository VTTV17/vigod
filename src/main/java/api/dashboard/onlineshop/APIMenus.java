package api.dashboard.onlineshop;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import utilities.api.API;

import java.util.List;

public class APIMenus {
    public static String ADD_MENU_ITEM_PATH = "itemservice/api/menu-items/items";
    public static String GET_ALL_MENU_ITEM_PATH = "itemservice/api/menus/%s/menu-items?type=ALL";
    public static String DELETE_MENU_ITEM_PATH = "itemservice/api/menu-items?sellerId=%s&ids=%s";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIMenus.class);

    /**
     * Create menuitem with level = 0
     * @param token
     * @param menuID: ID of current menu
     * @param collectionID
     * @param menuItemName
     */
    public void CreateMenuItemParent(String token, String menuID, int collectionID, String menuItemName){
        String body = """
               [{
                "hasChildren":false,
                "isDeleted":false,
                "menuId":"%s",
                "name":"%s",
                "level":0,
                "parentId":0,
                "order":0,
                "actionList":"EDIT,REMOVE",
                "dataType":"COLLECTION_PRODUCT",
                "dataValue":%s,
                "collectionId":%s}]
                """.formatted(menuID,menuItemName,collectionID,collectionID);
        Response menuItemRespone = api.putRequest(ADD_MENU_ITEM_PATH,token,body);
        menuItemRespone.then().statusCode(200);
        logger.info("Create menuItem successful.");
    }
    public int getMenuItemIDByName(String token, String menuId, String menuItemName) throws Exception {
        Response response = api.get(GET_ALL_MENU_ITEM_PATH.formatted(menuId),token);
        response.then().statusCode(200);
        List<String> nameList = response.jsonPath().getList("name");
        System.out.println("getMenuItemIDByName: "+nameList);
        List<Integer> idList = response.jsonPath().getList("id");
        for(int i=0; i<nameList.size();i++){
            if (nameList.get(i).equalsIgnoreCase(menuItemName)){
                return idList.get(i);
            }
        }
        throw new Exception("Menu item name not found");
    }

    /**
     * Delete menu item to clear data
     * @param storeID
     * @param token
     * @param menuId: Id of current menu
     * @param menuItemName
     * @throws Exception
     */
    public void deleteMenuItem(String storeID, String token, String menuId, String menuItemName) throws Exception {
        int menuItemId = getMenuItemIDByName(token,menuId,menuItemName);
        String body = """
                {}
                """;
        Response response = api.deleteRequest(DELETE_MENU_ITEM_PATH.formatted(storeID,menuItemId),token,body);
        response.then().statusCode(200);
    }
}
