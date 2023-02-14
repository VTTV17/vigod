package api.dashboard.products;

import api.dashboard.onlineshop.APIMenus;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;

import static api.dashboard.login.Login.accessToken;

public class APIEditProduct {
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIEditProduct.class);

    public static String ITEM_LANGUAGES_PATH = "itemservice/api/item-languages";
    public void ediTranslation(String itemID, String description,String productName,String language) throws Exception {
        String lang;
        switch (language){
            case "ENG"->lang = "en";
            case "VIE" -> lang = "vi";
            default -> throw new Exception("Language not found!");
        }
        String body = """
                "description":"%s",
                "itemId":"%s",
                "language":"%s",
                "name":"%s",
                "seoDescription":"",
                "seoKeywords":"",
                "seoTitle":"",
                "seoUrl":""
                """.formatted(description,itemID,lang,productName);
        Response menuItemRespone = api.putRequest(ITEM_LANGUAGES_PATH,accessToken,body);
        menuItemRespone.then().statusCode(200);
        logger.info("Update translation successful.");
    }
}
