package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceInfoAPI {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String SERVICE_LISH_PATH = "/itemservice/api/store/dashboard/%storeID%/service?langKey=vi&collectionId=%collectionId%&search=&page=0&size=100&sort=lastModifiedDate%2Cdesc&bhStatus=";
    public ServiceInfoAPI (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String SERVICE_DETAIL_PATH = "https://api.beecow.info/itemservice/api/beehive-items/%s?langKey=vi";
    String DELETE_SERVICE_PATH = "/itemservice/api/items/%s";
    public Response getServiceDetail(int serviceId){
        Response response = api.get(SERVICE_DETAIL_PATH.formatted(serviceId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public void deleteService(int serviceId){
        String path = DELETE_SERVICE_PATH.formatted(serviceId);
        api.delete(path,loginInfo.getAccessToken()).then().statusCode(200);
    }
    public Map<String, Date> getServiceLastModifedDateMapByProductName(int collectionID, String productName) throws ParseException {
        Response response = api.get(SERVICE_LISH_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf(collectionID)).replaceAll("%sort%",""),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> lastModifedDateList = response.jsonPath().getList("lastModifiedDate");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        List<String> serviceNameList = response.jsonPath().getList("name");
        Map<String,Date> serviceLastModifedMap = new HashMap<>();
        for(int i = 0; i<serviceNameList.size();i++){
            if(serviceNameList.get(i).equalsIgnoreCase(productName)){
                Date date = formatter.parse(lastModifedDateList.get(i).replaceAll("Z$", "+0000"));
                serviceLastModifedMap.put(serviceNameList.get(i).toLowerCase(),date);
                break;
            }
        }
        return serviceLastModifedMap;
    }
    public List<String> getServiceListCollection_SortNewest(Map serviceCollectionInfo) {
        Map<String, Date> sortedMap = SortData.sortMapByValue(serviceCollectionInfo);
        List<String> serviceSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(serviceSorted);
        return serviceSorted;
    }
}
