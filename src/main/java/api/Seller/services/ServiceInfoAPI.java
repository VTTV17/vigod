package api.Seller.services;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceInfoAPI {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String SERVICE_LIST_PATH = "/itemservice/api/store/dashboard/%storeID%/service?langKey=vi&collectionId=%collectionId%&search=&page=0&size=1000&sort=%sort%&bhStatus=";
    public ServiceInfoAPI (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String SERVICE_DETAIL_PATH = "/itemservice/api/beehive-items/%s?langKey=vi";
    String DELETE_SERVICE_PATH = "/itemservice/api/items/%s";
    public Response getServiceDetail(int serviceId){
        Response response = api.get(SERVICE_DETAIL_PATH.formatted(serviceId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public void deleteService(int serviceId){
        String path = DELETE_SERVICE_PATH.formatted(serviceId);
        api.delete(path,loginInfo.getAccessToken()).then();
    }
    public Map<String, Date> getServiceLastModifiedDateMapByServiceName(int collectionID, String serviceName) throws ParseException {
        Response response = api.get(SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf(collectionID)).replaceAll("%sort%","lastModifiedDate,desc"),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> lastModifiedDateList = response.jsonPath().getList("lastModifiedDate");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
        List<String> serviceNameList = response.jsonPath().getList("name");
        Map<String,Date> serviceLastModifedMap = new HashMap<>();
        for(int i = 0; i<serviceNameList.size();i++){
            if(serviceNameList.get(i).equalsIgnoreCase(serviceName)){
                System.out.println(serviceName+"---"+lastModifiedDateList.get(i));
                String formatMilisecond = fortmatIfDeleteMiliSecond(lastModifiedDateList.get(i));
                Date date = formatter.parse(formatMilisecond.replaceAll("Z$", "+0000"));
                serviceLastModifedMap.put(serviceNameList.get(i).toLowerCase(),date);
                break;
            }
        }
        System.out.println("HHH: "+serviceLastModifedMap);
        return serviceLastModifedMap;
    }
    public List<String> getServiceListCollection_SortNewest(Map serviceCollectionInfo) {
        Map<String, Date> sortedMap = SortData.sortMapByValue(serviceCollectionInfo);
        List<String> serviceSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(serviceSorted);
        return serviceSorted;
    }
    public Map getMapOfServiceLastModifiedDateMatchTitleCondition(String operator, String value) throws Exception {
        String path = SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%","").replaceAll("%sort%","lastModifiedDate,desc");
        Response response = api.get(path,loginInfo.getAccessToken());
        System.out.println("path: "+path);
        response.then().statusCode(200);
        List<String> productNameList = response.jsonPath().getList("name");
        List<String> createdDateList = response.jsonPath().getList("lastModifiedDate");
        SimpleDateFormat formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
        Map<String, Date> serviceLastModifiedDateMap = new HashMap<>();
        for (int i=0; i<productNameList.size();i++){
            String createDate = fortmatIfDeleteMiliSecond(createdDateList.get(i));
            Date date = formatter.parse(createDate.replaceAll("Z$", "+0000"));
            switch (operator){
                case "contains","bao gồm":
                    if (productNameList.get(i).contains(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        serviceLastModifiedDateMap.put(productName,date);
                    }
                    break;
                case "is equal to","tương đương":
                    if (productNameList.get(i).equals(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        serviceLastModifiedDateMap.put(productName,date);}
                    break;
                case "starts with","bắt đầu bằng":
                    if (productNameList.get(i).startsWith(value)){
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+"," ").trim();
                        serviceLastModifiedDateMap.put(productName,date);
                    }
                    break;
                case "ends with","kết thúc bằng":
                    if (productNameList.get(i).endsWith(value)) {
                        String productName = productNameList.get(i).toLowerCase().replaceAll("\\s+", " ").trim();
                        serviceLastModifiedDateMap.put(productName, date);
                    }
                    break;
                default: throw new Exception("Operator not match");
            }
        }
        return serviceLastModifiedDateMap;
    }
    public String fortmatIfDeleteMiliSecond(String time) {
        //handle when missing milisecond
        Matcher m = Pattern.compile("\\d+").matcher(time);
        List<String> aa = new ArrayList<>();
        while (m.find()) {
            aa.add(m.group());
        }
        if (aa.size() < 7) {
            time = time.replaceAll("Z", ".0000Z");
        }
        time = time.split("\\.|Z")[0]+ "Z";
        return time;
    }
    public List<String> getServiceListInCollectionByLastModifeDate(int collectionID) throws ParseException {
        String path = SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf(collectionID)).replaceAll("%sort%","lastModifiedDate,desc");
        Response response = api.get(path,loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<String> modifiedDateList = response.jsonPath().getList("lastModifiedDate");
        SimpleDateFormat  formatter =  new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
        List<String> serviceNameList = response.jsonPath().getList("name");
        Map<String,Date> serviceModifiedMap = new HashMap<>();
        for(int i = 0; i<modifiedDateList.size();i++){
            String modifiedDate = fortmatIfDeleteMiliSecond(modifiedDateList.get(i));
            Date date = formatter.parse(modifiedDate.replaceAll("Z$", "+0000"));
            serviceModifiedMap.put(serviceNameList.get(i).toLowerCase(),date);
        }
        Map<String, Date> sortedMap = SortData.sortMapByValue(serviceModifiedMap);
        List<String> productSorted = new ArrayList<>(sortedMap.keySet().stream().toList());
        Collections.reverse(productSorted);
        return productSorted;
    }
    public List<Integer> getServiceIdList(){
        String path = SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf("")).replaceAll("%sort%","createdDate,desc");
        Response response = api.get(path,loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }

    /**
     *
     * @return serviceID has status = ACTIVE or throw exception if no service active
     */
    public int getActiveServiceId(){
        String path = SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%",String.valueOf("")).replaceAll("%sort%","");
        Response response = api.get(path,loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> serviceIDList = response.jsonPath().getList("id");
        List<String> serviceStatusList = response.jsonPath().getList("bhStatus");
        for (String serviceStatus:serviceStatusList) {
            if(serviceStatus.equals("ACTIVE")){
                return serviceIDList.get(serviceStatusList.indexOf(serviceStatus));
            }
        }
        try {
            throw new Exception("Not found Active service.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public int getInactiveServiceId(){
        String path = SERVICE_LIST_PATH.replaceAll("%storeID%",String.valueOf(loginInfo.getStoreID())).replaceAll("%collectionId%","").replaceAll("%sort%","");
        Response response = api.get(path,loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<Integer> serviceIDList = response.jsonPath().getList("id");
        List<String> serviceStatusList = response.jsonPath().getList("bhStatus");
        for (String serviceStatus:serviceStatusList) {
            if(serviceStatus.equals("INACTIVE")){
                return serviceIDList.get(serviceStatusList.indexOf(serviceStatus));
            }
        }
        try {
            throw new Exception("Not found Inactive service.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ServiceInfo getServiceInfo(int id){
        Response response = getServiceDetail(id);
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceId(id);
        serviceInfo.setServiceModelId(response.jsonPath().getInt("models[0].id"));
        serviceInfo.setServiceName(response.jsonPath().getString("name"));
        serviceInfo.setServiceDescription(response.jsonPath().getString("description"));
        serviceInfo.setSellingPrice((int)response.jsonPath().getDouble("newPrice"));
        serviceInfo.setListingPrice((int)response.jsonPath().getDouble("orgPrice"));
        return serviceInfo;
    }
}
