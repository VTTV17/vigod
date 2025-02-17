package api.catalog;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.catalog.CityTree;

public class APICatalog {
	final static Logger logger = LogManager.getLogger(APICatalog.class);

    static List<CityTree> getVNCityTree(String countryCode) {
    	String vnCityTreePath = "/catalogservices/api/country/<country>/cities/tree";
    	String basePath = vnCityTreePath.replaceAll("<country>", countryCode);
    	String token = "notokenneeded";
    	
    	Response response = new API().get(basePath, token).then().statusCode(200).extract().response();
    	return response.jsonPath().getList(".", CityTree.class);
    }  
    static List<CityTree> getForeignCityTree(String countryCode) {
    	String foreignCityTreePath = "/catalogservices/api/country/<country>/cities";
    	String basePath = foreignCityTreePath.replaceAll("<country>", countryCode);
    	String token = "notokenneeded";
    	
    	Response response = new API().get(basePath, token).then().statusCode(200).extract().response();
    	return response.jsonPath().getList(".", CityTree.class);
    }  	
	
    public static List<CityTree> getCityTree(String countryCode) {
    	if(countryCode.contentEquals("VN")) {
    		return getVNCityTree(countryCode);
    	}
    	return getForeignCityTree(countryCode);
    }  
 
    public static String getCurrentLocation() {
    	String basePath = "/catalogservices/api/ip-info";
    	String token = "notokenneeded";
    	
    	Response response = new API().get(basePath, token).then().statusCode(200).extract().response();
    	return response.jsonPath().getString("country");
    }     
    
}
