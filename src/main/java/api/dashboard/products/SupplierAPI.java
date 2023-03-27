package api.dashboard.products;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class SupplierAPI {
    String GET_SUPPLIER_LIST = "/itemservice/api/suppliers/store/%s?langKey=vi&page=0&size=100&sort=id,desc&itemNameOrCode=";
    API api = new API();
    
    public JsonPath getAllSupplierJsonPath() {
    	Response response = api.get(GET_SUPPLIER_LIST.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllSupplierNames() {
    	return getAllSupplierJsonPath().getList("name");
    }

}
