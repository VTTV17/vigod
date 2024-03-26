package api.Seller.marketing;

import api.Seller.login.Login;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.emailCampaign.EmailCampaignInfo;
import utilities.model.dashboard.marketing.emailCampaign.EmailTemplateInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIEmailCampaign {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIEmailCampaign(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    String GET_EMAIL_TEMPLATE_PATH = "/mailservice/api/mail-templates/by-group?page=0&size=16&langKey=en";
    String CREATE_EMAIL_CAMPAIGN_PATH = "/mailservice/api/store-mails/%s?send=true&langKey=en";
    String GET_EMAIL_CAMPAIGN_PATH = "/mailservice/api/store-mails/storeId/%s?ignoreContent=true&page=0&size=20&sort=lastModifiedDate,desc&filter=%s";
    public EmailTemplateInfo getAnEmailTemplateInfo(){
        Response response = api.get(GET_EMAIL_TEMPLATE_PATH,loginInfo.getAccessToken());
        response.then().statusCode(200);
        EmailTemplateInfo emailTemplateInfo = new EmailTemplateInfo();
        emailTemplateInfo.setId(response.jsonPath().getInt("[0].id"));
        emailTemplateInfo.setContent(response.jsonPath().getString("[0].content"));
        return emailTemplateInfo;
    }
    public EmailCampaignInfo createEmailCampaign(String...receiveEmail){
        EmailTemplateInfo emailTemplateInfo = getAnEmailTemplateInfo();
        String templateContent = emailTemplateInfo.getContent();
        int templateId = emailTemplateInfo.getId();
        EmailCampaignInfo emailCampaignInfo = new EmailCampaignInfo();
        String receiver = receiveEmail.length>0 ? receiveEmail[0] : "";
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("campaignDescription","");
        requestBody.addProperty("campaignEmailTo", "EMAIL_ADDRESS");
        requestBody.addProperty("campaignName",emailCampaignInfo.getName());
        requestBody.addProperty("content",templateContent);
        requestBody.addProperty("contentType","HTML");
        requestBody.addProperty("mailId",templateId);
        requestBody.addProperty("receiver",receiver);
        requestBody.addProperty("storeId",loginInfo.getStoreID());
        requestBody.addProperty("title",emailCampaignInfo.getEmailTitle());
        Response response = api.put(CREATE_EMAIL_CAMPAIGN_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),requestBody.toString());
        response.then().statusCode(201);
        emailCampaignInfo.setId(response.jsonPath().getInt("id"));
        emailCampaignInfo.setContentHtml(templateContent);
        emailCampaignInfo.setMailId(templateId);
        return emailCampaignInfo;
    }
    public int getDraftEmailCampaignId(){
        Response response = api.get(GET_EMAIL_CAMPAIGN_PATH.formatted(loginInfo.getStoreID(),"DRAFT"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("id");
        int id = ids.isEmpty() ? 0 : ids.get(0);
        return id;
    }
}
