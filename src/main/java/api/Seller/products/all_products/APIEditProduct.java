package api.Seller.products.all_products;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;


public class APIEditProduct {
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIEditProduct.class);
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIEditProduct(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }


    public static String ITEM_LANGUAGES_PATH = "itemservice/api/item-languages";
    public static String DELETE_ITEM_PATH = "itemservice/api/items/%s";
    public void ediTranslation(int itemID, String description,String productName,String language) throws Exception {
        String lang;
        switch (language){
            case "ENG"->lang = "en";
            case "VIE" -> lang = "vi";
            default -> throw new Exception("Language not found!");
        }
        String body = """
                {"description":"%s",
                "itemId": %s,
                "language":"%s",
                "name":"%s",
                "seoDescription":"",
                "seoKeywords":"",
                "seoTitle":"",
                "seoUrl":""
                }""".formatted(description,itemID,lang,productName);
        Response menuItemRespone = api.put(ITEM_LANGUAGES_PATH,loginInfo.getAccessToken(),body);
        menuItemRespone.then().statusCode(200);
        logger.info("Update translation successful.");
    }
    public void deleteProduct(int productId){
        String path = DELETE_ITEM_PATH.formatted(productId);
        Response response = api.delete(path,loginInfo.getAccessToken());
        System.out.println(response.prettyPrint());
//        response.then().statusCode(200);
        logger.info("Delete product: "+productId);
    }
}
