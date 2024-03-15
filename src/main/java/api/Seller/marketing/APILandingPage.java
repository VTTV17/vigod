package api.Seller.marketing;

import api.Seller.login.Login;
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
        System.out.println("________Response Template______");
        response.prettyPrint();
        LandingPageTemplateInfo landingPageTemplateInfo = new LandingPageTemplateInfo();
        landingPageTemplateInfo.setId((int)response.jsonPath().getList("id").get(0));
        landingPageTemplateInfo.setContent(response.jsonPath().getList("content").get(0).toString());
        return landingPageTemplateInfo;
    }
    public LandingPageInfo createLandingPage(){
        LandingPageInfo landingPageInfo = new LandingPageInfo();
        LandingPageTemplateInfo landingPageTemplateInfo = getALandingPageTemplateInfo();
        String contentHtml  = landingPageTemplateInfo.getContent();
        int templateId = landingPageTemplateInfo.getId();
        String body = """
                {
                    "id": null,
                    "contentHtml":"%s"
                    "customerTag": "",
                    "description": "",
                    "status": "DRAFT",
                    "storeId": "%s",
                    "templateId": %s,
                    "title": "%s",
                    "domainType": "FREE",
                    "primaryColor": "#ffa500",
                    "fbPixelId": "",
                    "ggAnalyticsId": "",
                    "seoThumbnail": "",
                    "seoTitle": "",
                    "seoDescription": "",
                    "seoKeywords": "",
                    "popupMainShow": true,
                    "popupMainTime": 3,
                    "fbChatId": "",
                    "zlChatId": "",
                    "slug": "%s",
                    "freeDomainType": "GOSELL"
                }
                """.formatted(contentHtml,loginInfo.getStoreID(),templateId,landingPageInfo.getName(),landingPageInfo.getDomainName());
        Response response = api.post(CREATE_LANDING_PAGE_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),body);
        response.then().statusCode(201);
        landingPageInfo.setId(response.jsonPath().getInt("id"));
        landingPageInfo.setContentHtml(contentHtml);
        landingPageInfo.setTemplateId(templateId);
        System.out.println("________Response______");
        response.prettyPrint();
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
}
