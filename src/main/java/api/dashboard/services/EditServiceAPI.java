package api.dashboard.services;

import api.dashboard.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;

import java.util.ArrayList;
import java.util.List;

public class EditServiceAPI {
    API api = new API();
    String EDIT_TRANSLATION_MODEL = "/itemservice/api/item-model-languages/bulk";

    public List<String> editTranslationServiceLocations(String serviceId){
        Response serviceInfoRes = new ServiceInfoAPI().getServiceDetail(serviceId);
        List<Integer> serviceModelIds = serviceInfoRes.jsonPath().getList("models.id");
        List<String> variationNames = serviceInfoRes.jsonPath().getList("models.orgName");
        StringBuilder body = new StringBuilder("[");
        List<String> listNewLocation = new ArrayList<>();
        for(int i =0;i<serviceModelIds.size();i++){
            String currentVariation = variationNames.get(i);
            String newlocation = currentVariation.split("\\|")[0]+" update en";
            String newVariation = String.join("|", newlocation, currentVariation.split("\\|")[1]);
            if(!listNewLocation.contains(newlocation)){
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
                    """.formatted(serviceId,serviceModelIds.get(i),newVariation));
            if(i == serviceModelIds.size() -1){
                body.append("]");
            }else body.append(",");
        }
        Response response = api.put(EDIT_TRANSLATION_MODEL, Login.accessToken,body.toString());
        System.out.println(body);
        response.then().statusCode(200);
        return listNewLocation;
    }
}
