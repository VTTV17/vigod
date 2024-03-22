package api.Seller.marketing;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.all_products.APISuggestionProduct.AllSuggestionProductsInfo;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIBuyLink {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIBuyLink(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    final static Logger logger = LogManager.getLogger(APIBuyLink.class);
    public static String GET_BUY_LINK_LIST_PATH = "orderservices2/api/buy-link/%storeId%?page=0&size=100&sort=createdDate,desc";
    public static String DELETE_BUY_LINK_PATH = "orderservices2/api/buy-link/%s/%s";
    public static String CREATE_BUY_LINK_PATH = "/orderservices2/api/buy-link/%s";
    public int getNewestBuyLinkID(){
        Response response = api.get(GET_BUY_LINK_LIST_PATH.replaceAll("%storeId%", String.valueOf(loginInfo.getStoreID())),loginInfo.getAccessToken());
        response.then().statusCode(200);
        System.out.println(response.prettyPrint());
        List<Integer> idList = response.jsonPath().getList("id");
        if(idList.isEmpty()) return 0;
        logger.info("Buy Link ID newest: "+idList.get(0));
        return idList.get(0);
    }
    public void deleteBuyLinkById(int id){
        String storeId = String.valueOf(loginInfo.getStoreID());
        api.delete(DELETE_BUY_LINK_PATH.formatted(storeId,id),loginInfo.getAccessToken());
        logger.info("Delete buy link: "+id);
    }
    public int createBuyLink(){
        int branchId = new Login().getInfo(loginInformation).getAssignedBranchesIds().get(0);
        AllSuggestionProductsInfo productSuggesionInfo = new APISuggestionProduct(loginInformation).getListSuggestionProduct(branchId);
        String body = """
                {
                      "buyLinkItems": [
                          {
                              "itemId": "%s",
                              "modelId": "%s",
                              "quantity": 1,
                              "branchId": %s
                          }
                      ],
                      "storeId": "%s"
                  }
                    """.formatted(productSuggesionInfo.getItemIds().get(0), productSuggesionInfo.getModelIds().get(0),branchId,loginInfo.getStoreID());
        Response response = api.post(CREATE_BUY_LINK_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),body);
        response.then().statusCode(201);
        int id = response.jsonPath().getInt("id");
        return id;
    }
}
