package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;

import java.util.HashMap;
import java.util.Map;

public class CreateServiceAPI {
    String CREATE_SERVICE_PATH = "/itemservice/api/service/items";
    API api = new API();
    LoginDashboardInfo loginInfo = new Login().getInfo();
    /**
     *
     * @param name
     * @param description
     * @param listingPrice
     * @param sellingPrice
     * @param locations
     * @param times
     * @param isEnableListingService
     * @return map with keys: "serviceId".
     */
    public Map createServiceAPI(String name, String description, int listingPrice, int sellingPrice, String[] locations, String[] times, boolean isEnableListingService) {
        StringBuilder createServiceBody = new StringBuilder("""
                {
                	"name": "%s",
                	"cateId": 1680,
                	"currency": "đ",
                	"description": "%s",
                	"itemType": "SERVICE",
                	"totalComment": 0,
                	"totalLike": 0,
                	"images": [
                		{
                			"imageUUID": "251f1241-51dc-47b4-b203-7f992aa21d35",
                			"urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                			"extension": "png"
                		}
                	],
                	"categories": [
                		{
                			"id": null,
                			"level": 1,
                			"cateId": 1014
                		},
                		{
                			"id": null,
                			"level": 2,
                			"cateId": 1680
                		}
                	],
                	"models": [
                             """.formatted(name, description));
        int discount  = sellingPrice*100/listingPrice;
        for (int i = 0; i < locations.length; i++) {
            for (int j = 0; j < times.length; j++) {
                createServiceBody.append("""
                        {
                            "name": "%s",
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
                        }""".formatted(locations[i]+"|"+times[j],listingPrice,sellingPrice,discount));
                if(i == locations.length -1 && j == times.length -1 ){
                    createServiceBody.append("],");
                }else createServiceBody.append(",");
            }
        }
        createServiceBody.append("""
                    "quantityChanged": true,
                    "bcoin": 0,
                    "isSelfDelivery": true,
                    "enabledListing": %s,
                    "inventoryType": "SET",
                    "inventoryStock": 1000000,
                    "inventoryCurrent": 0,
                    "inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN"
                }""".formatted(isEnableListingService));
        Response response =  api.post(CREATE_SERVICE_PATH,loginInfo.getAccessToken(),createServiceBody.toString());
        response.then().statusCode(201);
        Map result = new HashMap<>();
        result.put("serviceId",response.jsonPath().getInt("id"));
        return result;
    }


}
