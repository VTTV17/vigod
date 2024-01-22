package api.Seller.services;

import api.Seller.login.Login;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;

public class EditServiceAPI {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public EditServiceAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String EDIT_TRANSLATION_MODEL = "/itemservice/api/item-model-languages/bulk";
    String EDIT_SERVICE_PATH = "/itemservice/api/service/items";
    private String serviceName;
    private String serviceDescription;
    private int listingPrice;
    private int sellingPrice;
    private String[] locations;
    private String[] times;
    private boolean isActive = true;
    private boolean isEnableListing = false;
    public String getServiceNameEdit(){
        return serviceName;
    }
    public void setServiceNameEdit(String serviceName){
        this.serviceName = serviceName;
    }
    public String getServiceDescriptionEdit(){
        return serviceDescription;
    }
    public void setServiceDescriptionEdit(String serviceDescription){
        this.serviceDescription = serviceDescription;
    }
    public Integer getListingPriceEdit(){
        return listingPrice;
    }
    public void setListingPriceEdit(int listingPriceEdit){
        this.listingPrice = listingPriceEdit;
    }
    public Integer getSellingPriceEdit(){
        return sellingPrice;
    }
    public void setSellingPriceEdit(int sellingPrice){
        this.sellingPrice = sellingPrice;
    }
    public String[] getLocations(){
        return locations;
    }
    public void setLocations(String[] locationEdit){
        this.locations = locationEdit;
    }
    public String[] getTimes(){
        return times;
    }
    public void setTimes(String[] timesEdit){
        this.times = timesEdit;
    }
    public Boolean getActiveStatus(){
        return isActive;
    }
    public void setActiveStatus(boolean isActive){
        this.isActive = isActive;
    }
    public Boolean getEnableListingStatus(){
        return isEnableListing;
    }
    public void setEnableListingStatus(boolean isEnableListing){
        this.isEnableListing = isEnableListing;
    }
    public List<String> editTranslationServiceLocations(int serviceId) {

        Response serviceInfoRes = new ServiceInfoAPI(loginInformation).getServiceDetail(serviceId);
        List<Integer> serviceModelIds = serviceInfoRes.jsonPath().getList("models.id");
        List<String> variationNames = serviceInfoRes.jsonPath().getList("models.orgName");
        StringBuilder body = new StringBuilder("[");
        List<String> listNewLocation = new ArrayList<>();
        for (int i = 0; i < serviceModelIds.size(); i++) {
            String currentVariation = variationNames.get(i);
            String newlocation = currentVariation.split("\\|")[0] + " update en";
            String newVariation = String.join("|", newlocation, currentVariation.split("\\|")[1]);
            if (!listNewLocation.contains(newlocation)) {
                listNewLocation.add(newlocation);
            }
            body.append("""
                                        
                        {
                            "id": %s,
                            "modelId": %s,
                            "language": "en",
                            "name": "%s",
                            "label": "location|timeslot",
                            "description": "",
                            "versionName": null
                        }
                    """.formatted(serviceId, serviceModelIds.get(i), newVariation));
            if (i == serviceModelIds.size() - 1) {
                body.append("]");
            } else body.append(",");
        }
        Response response = api.put(EDIT_TRANSLATION_MODEL, loginInfo.getAccessToken(), body.toString());
        System.out.println("Edit translation body: "+body);
        response.then().statusCode(200);
        return listNewLocation;
    }

    /**
     * Need to set value service info before call function (Example: setServiceNameEdit(), setServiceDescriptionEdit()...)
     * @param serviceId
     * @throws JsonProcessingException
     */
    public void updateService(int serviceId) throws JsonProcessingException {
        Response serviceInfoRes = new ServiceInfoAPI(loginInformation).getServiceDetail(serviceId);
        Map<String, Object> serviceInfoMapping = new ObjectMapper().readValue(serviceInfoRes.body().asString(), HashMap.class);
        String serviceNamEdit = (String) serviceInfoMapping.get("name");
        if(serviceName!= null) serviceNamEdit = serviceName;
        String descriptionEdit = (String) serviceInfoMapping.get("description");
        if(serviceDescription!= null) descriptionEdit = serviceDescription;
        StringBuilder editServiceBody = new StringBuilder("""
                {
                    "id": %s,
                    "name": "%s",
                    "currency": "đ",
                    "description": "%s",
                    "cateId": 1680,
                    "categories": [
                        {
                            "id": 3136881,
                            "cateId": 1014,
                            "level": 1
                        },
                        {
                            "id": 3136882,
                            "cateId": 1680,
                            "level": 2
                        }
                    ],
                """.formatted(serviceId, serviceNamEdit, descriptionEdit));
        editServiceBody.append("""
                 "author": %s,
                """.formatted(new ObjectMapper().writeValueAsString(serviceInfoMapping.get("author"))));
        editServiceBody.append("""
                "itemType": "SERVICE",
                    "bcoin": 0,
                    "images": [
                        {
                            "imageUUID": "989384e3-5ab9-4353-84ef-61a22ed46d4b",
                            "urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                            "extension": "png"
                        }
                    ],
                    "itemDetails": [],
                    "promotion": {
                        "beecowSuggest": false
                    },
                """);
        String models = """
                {   "name": "%s",
                    "orgPrice": "%s",
                    "costPrice": 0,
                    "position": 0,
                    "newPrice": "%s",
                    "discount": %s,
                    "totalItem": 1000000,
                    "soldItem": 0,
                    "label": "location|timeslot",
                    "inventoryType": "SET",
                    "inventoryStock": 1000000,
                    "inventoryCurrent": 0,
                    "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN"
                }""";
        editServiceBody.append("""
                    "models": [""");
        if(locations == null && times == null){ // no update model: get old data
            List<String> names = serviceInfoRes.jsonPath().getList("models.orgName");
            List<String> orgPrices = serviceInfoRes.jsonPath().getList("models.orgPrice");
            List<String> newPrices = serviceInfoRes.jsonPath().getList("models.newPrice");
            List<String> discounts = serviceInfoRes.jsonPath().getList("models.discount");
            for (int i =0;i< names.size();i++){
                editServiceBody.append(models.formatted(names.get(i),orgPrices.get(i),newPrices.get(i),discounts.get(i)));
                if (i == names.size() - 1) {
                    editServiceBody.append("],");
                } else editServiceBody.append(",");
            }
        }else { //has update model: get new data
            int discount = sellingPrice*100/ listingPrice;
            for (int i = 0; i < locations.length; i++) {
                for (int j = 0; j < times.length; j++) {
                    editServiceBody.append(models.formatted(locations[i] + "|" + times[j], listingPrice,sellingPrice,discount));
                    if (i == locations.length - 1 && j == times.length - 1) {
                        editServiceBody.append("],");
                    } else editServiceBody.append(",");
                }
            }
        }
        editServiceBody.append("""
                "isSelfDelivery": false,
                    "showOutOfStock": true,
                    "barcode": "%s",
                    "lstInventory": [],
                    "branches": %s,
                    "taxSettings": %s,
                """.formatted(serviceInfoMapping.get("barcode"), new ObjectMapper().writeValueAsString(serviceInfoMapping.get("branches")),new ObjectMapper().writeValueAsString(serviceInfoMapping.get("taxSettings"))));
        String activeStatus = "ACTIVE";
        if (!isActive) activeStatus = "INACTIVE";
        editServiceBody.append("""
                      "onApp": true,
                      "onWeb": true,
                      "inStore": true,
                      "inGosocial": true,
                      "enabledListing": %s,
                      "itemModelCodeDTOS": [],
                      "inventoryManageType": "PRODUCT",
                      "bhStatus": "%s",
                      "saleChannels": [
                          {
                              "name": "BEECOW",
                              "status": "LATEST"
                          }
                      ],
                      "selfDelivery": false,
                      "quantityChanged": true,
                      "inventoryType": "CHANGE",
                      "inventoryStock": 0,
                      "inventoryActionType": "FROM_UPDATE_AT_ITEM_SCREEN"
                  }
                """.formatted(isEnableListing,activeStatus));
        System.out.println("body edit: "+editServiceBody);
        Response response = api.put(EDIT_SERVICE_PATH,loginInfo.getAccessToken(),editServiceBody.toString());
        System.out.println(response.prettyPrint());
        response.then().statusCode(200);
    }
}
