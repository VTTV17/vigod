package api.Seller.marketing;

import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.enums.PushNotiEvent;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIPushNotification {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String CREATE_PUSH_NOTI_PATH = "/beehiveservices/api/marketing-notifications";
    String GET_PUSH_NOTI_PATH = "/beehiveservices/api/marketing-notifications?sort=createdDate,desc&page=-1&size=50&status.equals=%s";
    String DELETE_CAMPAIGN_PATH = "/beehiveservices/api/marketing-notifications/%s/%s";
    public APIPushNotification(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public int createEventPushNotification(PushNotiEvent event){
        String random = new DataGenerator().generateString(10);
        String content = "content "+random;
        String name = "name "+random;
        int storeId = loginInfo.getStoreID();
        String title = "noti title "+random;
        List<PushNotiEvent> needSegmentEvents =  Arrays.asList(new PushNotiEvent[] {PushNotiEvent.BIRTHDAY, PushNotiEvent.ORDER_COMPLETED,PushNotiEvent.ABANDONED_CHECKOUT});
        List<Integer> segmentLists = new ArrayList<>();
        if(needSegmentEvents.contains(event)){
            int getSegmentId = new APISegment(loginInformation).getListSegmentIdInStore().get(0);
            segmentLists.add(getSegmentId);
        }
        String body = """
                {
                	"content": "%s",
                	"event": "%s",
                	"eventOption": 0,
                	"linkToValue": "",
                	"name": "%s",
                	 "segmentIds": %s,
                	"storeId": "%s",
                	"title": "%s",
                	"type": "PUSH"
                }
                """.formatted(content,String.valueOf(event),name,segmentLists,storeId,title);
        Response response = api.post(CREATE_PUSH_NOTI_PATH,loginInfo.getAccessToken(),body);
        response.then().log().all().statusCode(201);
        int id = response.jsonPath().getInt("id");
        return id;
    }
    public int getActivePushNoti(){
        Response response = api.get(GET_PUSH_NOTI_PATH.formatted("ACTIVE"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("id");
        int id = ids.isEmpty() ? 0 : ids.get(0);
        return id;
    }
    /* Get list of newest active campaign from this id */
    public List<Integer> getSomeActiveCampaign(int fromIdUntilNewest){
        Response response = api.get(GET_PUSH_NOTI_PATH.formatted("ACTIVE"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> ids = response.jsonPath().getList("id");
        List<Integer> newestList = new ArrayList<>();
        for (int id:ids) {
            if(id > fromIdUntilNewest) newestList.add(id);
        }
        return newestList;
    }
    public void deletePushNotiCampaign(int id){
        api.delete(DELETE_CAMPAIGN_PATH.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken());
    }
}
