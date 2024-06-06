package api.Seller.sale_channel.onlineshop;
import api.Seller.login.Login;
import api.Buyer.header.APIHeader;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.enums.MenuItemType;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIMenus {
    public static String ADD_MENU_ITEM_PATH = "itemservice/api/menu-items/items";
    public static String GET_ALL_MENU_ITEM_PATH = "itemservice/api/menus/%s/menu-items?type=ALL";
    public static String DELETE_MENU_ITEM_PATH = "itemservice/api/menu-items?sellerId=%s&ids=%s";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIMenus.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public APIMenus(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    /**
     * Call API login to set account before call this api
     * Create menuitem with level = 0
     * @param collectionID
     * @param menuItemName
     * @type COLLECTION_PRODUCT, COLLECTION_SERVICE, BLOG...
     */
    public void CreateMenuItemParent(int collectionID, String menuItemName, MenuItemType type){
        int menuID = new APIHeader(loginInformation).getCurrentMenuId();
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
                "dataType":"%s",
                "dataValue":%s,
                "collectionId":%s}]
                """.formatted(menuID,menuItemName,type,collectionID,collectionID);
        System.out.println(body);
        Response menuItemRespone = api.put(ADD_MENU_ITEM_PATH,loginInfo.getAccessToken(),body);
        menuItemRespone.then().statusCode(200);
        logger.info("Create menuItem successful.");
    }
    public int getMenuItemIDByName( int menuId, String menuItemName) throws Exception {
        Response response = api.get(GET_ALL_MENU_ITEM_PATH.formatted(menuId),loginInfo.getAccessToken());
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
     * Call API login to set account before call this api
     * Delete menu item to clear data
     * @param menuId: Id of current menu
     * @param menuItemName
     * @throws Exception
     */
    public void deleteMenuItem(String menuItemName) throws Exception {
        int menuId = new APIHeader(loginInformation).getCurrentMenuId();
        int menuItemId = getMenuItemIDByName(menuId,menuItemName);
        String body = """
                {}
                """;
        Response response = api.deleteRequest(DELETE_MENU_ITEM_PATH.formatted(loginInfo.getStoreID(),menuItemId),loginInfo.getAccessToken(),body);
//        response.then().statusCode(200);
    }
}
