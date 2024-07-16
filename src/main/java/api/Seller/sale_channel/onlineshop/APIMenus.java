package api.Seller.sale_channel.onlineshop;
import api.Seller.login.Login;
import api.Buyer.header.APIHeader;
import api.Seller.products.product_collections.APIProductCollection;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.enums.MenuItemType;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIMenus {
    public static String ADD_MENU_ITEM_PATH = "itemservice/api/menu-items/items";
    public static String GET_ALL_MENU_ITEM_PATH = "itemservice/api/menus/%s/menu-items?type=ALL";
    public static String DELETE_MENU_ITEM_PATH = "itemservice/api/menu-items?sellerId=%s&ids=%s";
    public static String GET_ALL_MENU_PATH = "/itemservice/api/menus?sellerId=%s&sort=id,asc&page=0&size=20";
    public static String ADD_MENU_PATH = "/itemservice/api/menus";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIMenus.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public APIMenus(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public String getMenuItemBody(int collectionID, String menuItemName, MenuItemType type,int menuID){
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
        return body;
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
        String body =getMenuItemBody(collectionID,menuItemName,type,menuID);
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
    public Response getMenuListResponse(){
        Response response = api.get(GET_ALL_MENU_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public int createMenu(){
        String menuName = "Menu "+ new DataGenerator().generateString(5);
        int collectionId = new APIProductCollection(loginInformation).getNewestCollectionID();
        String menuItemName = "menuitem "+new DataGenerator().generateString(5);
        String menuItemBody = getMenuItemBody(collectionId,menuItemName,MenuItemType.COLLECTION_PRODUCT,0);
        String body = """
                {
                    "menuDto": {
                        "name": "%s",
                        "sellerId": "%s",
                        "actionList": "EDIT,REMOVE"
                      },
                    "menuItemDto": %s
                }
                """.formatted(menuName,loginInfo.getStoreID(),menuItemBody);
        Response response = api.post(ADD_MENU_PATH,loginInfo.getAccessToken(),body);
        response.then().statusCode(201);
        return response.jsonPath().getInt("menuDto.id");
    }
    public int getMenuIdCanEdit(){
        Response response = getMenuListResponse();
        List<Integer> ids = response.jsonPath().getList("findAll{it.actionList != null}.id");
        if(ids.isEmpty()){
            return createMenu();
        }
        return ids.get(0);
    }

}
