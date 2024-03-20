package api.Seller.marketing;

import api.Seller.login.Login;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.landingPage.LandingPageInfo;
import utilities.model.dashboard.marketing.landingPage.LandingPageTemplateInfo;
import utilities.model.sellerApp.login.LoginInformation;

import javax.swing.text.html.HTML;
import java.util.List;

public class APILandingPage {
    String GET_LIST_LANDING_PAGE_PATH = "/themeservices/api/landing-pages?storeId.equals=%s&sort=lastModifiedDate,desc&page=0&size=20";
    String GET_LANDING_PAGE_TEMPLATE = "/themeservices/api/landing-page-templates?page=0&size=9999";
    String CREATE_LANDING_PAGE_PATH = "/themeservices/api/landing-pages/%s";
    String PUBLISH_LANDING_PAGE_PATH = "/themeservices/api/landing-pages/publish/%s/%s";
    String DELETE_LANDING_PAGE = "/themeservices/api/landing-pages/store/%s/delete/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APILandingPage(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public int getADraftLandingPageId(){
        Response response = api.get(GET_LIST_LANDING_PAGE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("findAll {it.status=='DRAFT'}.id");
        if(ids.isEmpty()) return 0;
        return ids.get(0);
    }
    public LandingPageTemplateInfo getALandingPageTemplateInfo(){
        Response response = api.get(GET_LANDING_PAGE_TEMPLATE,loginInfo.getAccessToken());
        response.then().statusCode(200);
        LandingPageTemplateInfo landingPageTemplateInfo = new LandingPageTemplateInfo();
        landingPageTemplateInfo.setId((int)response.jsonPath().getList("id").get(0));
        landingPageTemplateInfo.setContent((String)response.jsonPath().getList("content").get(0));
        return landingPageTemplateInfo;
    }
    public LandingPageInfo createLandingPage(){
        LandingPageInfo landingPageInfo = new LandingPageInfo();
        LandingPageTemplateInfo landingPageTemplateInfo = getALandingPageTemplateInfo();
        String contentHtml  = landingPageTemplateInfo.getContent();
        int templateId = landingPageTemplateInfo.getId();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("contentHtml",contentHtml);
        requestBody.addProperty("customerTag","");
        requestBody.addProperty("description","");
        requestBody.addProperty("status","DRAFT");
        requestBody.addProperty("storeId",loginInfo.getStoreID());
        requestBody.addProperty("templateId",templateId);
        requestBody.addProperty("title",landingPageInfo.getName());
        requestBody.addProperty("domainType","FREE");
        requestBody.addProperty("primaryColor","#ffa500");
        requestBody.addProperty("fbPixelId","");
        requestBody.addProperty("ggAnalyticsId","");
        requestBody.addProperty("seoThumbnail","");
        requestBody.addProperty("seoTitle","");
        requestBody.addProperty("seoKeywords","");
        requestBody.addProperty("popupMainShow",true);
        requestBody.addProperty("popupMainTime",3);
        requestBody.addProperty("fbChatId","");
        requestBody.addProperty("zlChatId","");
        requestBody.addProperty("slug",landingPageInfo.getDomainName());
        requestBody.addProperty("freeDomainType","GOSELL");
        Response response = api.post(CREATE_LANDING_PAGE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),requestBody.toString());
        System.out.println(requestBody.toString());
        response.then().log().all().statusCode(201);
        landingPageInfo.setId(response.jsonPath().getInt("id"));
        landingPageInfo.setContentHtml(contentHtml);
        landingPageInfo.setTemplateId(templateId);
        return landingPageInfo;
    }
    public void publishLandingPage(int draftLandingPageId){
        Response response = api.post(PUBLISH_LANDING_PAGE_PATH.formatted(loginInfo.getStoreID(),draftLandingPageId),loginInfo.getAccessToken());
        response.then().statusCode(200);
    }
    public int getAPublishLandingPageId(){
        Response response = api.get(GET_LIST_LANDING_PAGE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("findAll {it.status=='PUBLISH'}.id");
        if(ids.isEmpty()) return 0;
        return ids.get(0);
    }
    public void deleteLandingPage(int id){
        api.delete(DELETE_LANDING_PAGE.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken())
                .then();
    }
}
