package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.onlineshop.ThemeInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.Map;

public class APIThemes {
    private String GET_MY_THEME_LIST_PATH = "/ssrstorefront/api/store-themes/store/%s/management";
    private String PUBLISH_THEME_PATH = "/ssrstorefront/api/store-themes/store/%s/publish";
    private String GET_THEME_STORE_LIST = "/ssrstorefront/api/master-themes?page=0&size=15&sort=lastModifiedDate,desc";
    private String ADD_NEW_THEME_PATH = "/ssrstorefront/api/preview/store-pages/store/%s";
    private String GET_CONTENT_OF_THEME_PATH = "/ssrstorefront/api/store-themes/store/%s/load-theme?lang=vi&masterThemeId=%s";
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIThemes.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public APIThemes(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response getMyThemeList(){
        Response response = api.get(GET_MY_THEME_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public int getThemeStoreId(){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("langKey", "vi");
        Response response = api.get(GET_THEME_STORE_LIST,loginInfo.getAccessToken(),headerMap);
        response.then().statusCode(200);
        return response.jsonPath().getInt("[0].id");
    }
    public ThemeInfo getActiveThemeId(){
        Response response =  getMyThemeList();
        int id =response.jsonPath().getInt("find {it.published==true}.id");
        String name = response.jsonPath().getString("find {it.published==true}.customName");
        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.setId(id);
        themeInfo.setName(name);
        return themeInfo;
    }
    public void publishATheme(int id){
        Response response = api.put(PUBLISH_THEME_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),String.valueOf(id));
        response.then().statusCode(204);
    }
    public void addANewTheme(){
        int id = getThemeStoreId();
        Response contentRes = api.get(GET_CONTENT_OF_THEME_PATH.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken());
        String homePageContent = contentRes.then().statusCode(200).extract().jsonPath().getString("pages.find {it.type == 'HOME'}.content");
        int pageContentId = contentRes.jsonPath().getInt("pages.find {it.type == 'HOME'}.id");
        String customName = "Themes "+ new DataGenerator().generateString(5);
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("customName",customName);
        requestBody.addProperty("masterThemeId",id);
        JsonObject page = new JsonObject();
        page.addProperty("content",homePageContent);
        page.addProperty("id",pageContentId);
        JsonArray pageArray = new JsonArray();
        pageArray.add(page);
        requestBody.add("pages",pageArray);
        Response response = api.post(ADD_NEW_THEME_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(), requestBody.toString());
        response.then().statusCode(200);
    }

}
