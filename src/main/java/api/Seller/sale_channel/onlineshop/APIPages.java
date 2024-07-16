package api.Seller.sale_channel.onlineshop;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIPages {
    API api = new API();
    final static Logger logger = LogManager.getLogger(APIPages.class);
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    String CREATE_PAGE_PATH = "/ssrstorefront/api/custom-pages/%s";
    String GET_PAGE_LIST_PATH = "/ssrstorefront/api/custom-pages/%s?size=10&page=0&sort=lastModifiedDate,desc";
    public APIPages(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response createPageResponse(){
        String title = new DataGenerator().generateString(10);
        String body = """
                {    "title":"%s",
                    "url":"%s",
                    "rawContent":"<p>%s</p>",
                    "storeId":"%s",
                    "status":"PUBLISH"
                }
                """.formatted(title,title,"Description "+title,loginInfo.getStoreID());
        Response response = api.post(CREATE_PAGE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),body);
        return response;
    }
    public int createPage(){
        Response response = createPageResponse();
        response.then().statusCode(201);
        return response.jsonPath().getInt("id");
    }
    public List<Integer> getPageIdList(){
        Response response = api.get(GET_PAGE_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("id");
        return ids;
    }
}
