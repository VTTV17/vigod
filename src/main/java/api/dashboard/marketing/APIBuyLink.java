package api.dashboard.marketing;

import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import java.util.List;

public class APIBuyLink {
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIBuyLink.class);
    public static String GET_BUY_LINK_LIST_PATH = "orderservices2/api/buy-link/%storeId%?page=0&size=100&sort=createdDate,desc";
    public static String DELETE_BUY_LINK_PATH = "orderservices2/api/buy-link/%s/%s";
    public int getNewestBuyLinkID(){
        Response response = api.get(GET_BUY_LINK_LIST_PATH.replaceAll("%storeId%", String.valueOf(Login.apiStoreID)), Login.accessToken);
        response.then().statusCode(200);
        List<Integer> idList = response.jsonPath().getList("id");
        logger.info("Buy Link ID newest: "+idList.get(0));
        return idList.get(0);
    }
    public void deleteBuyLinkById(int id){
        String storeId = String.valueOf(Login.apiStoreID);
        Response response = api.delete(DELETE_BUY_LINK_PATH.formatted(storeId,id),Login.accessToken);
        response.then().statusCode(204);
        logger.info("Delete buy link: "+id);
    }
}
