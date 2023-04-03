package api.dashboard.customers;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class SegmentAPI {
	final static Logger logger = LogManager.getLogger(SegmentAPI.class);
	
    String GET_SEGMENT_LIST = "/beehiveservices/api/segments/store/%s?page=0&size=100&name.contains=&sort=id,desc";
    String DELETE_SEGMENT_PATH = "/beehiveservices/api/segments/delete/%s/%s";
    API api = new API();

    
    
    public JsonPath getAllSegmentJsonPath() {
    	Response response = api.get(GET_SEGMENT_LIST.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    /**
     * This method retrieves the ID of a segment by its name.
     * @param segmentName The name of the segment whose ID is to be retrieved.
     * @return The ID of the specified segment.
     */
    public int getSegmentIdByName(String segmentName) {
    	return getAllSegmentJsonPath().get("find { it.name == '%s' }.id".formatted(segmentName));
    }  

    /**
     * This method deletes a customer segment with a specific ID.
     * @param segmentId The ID of the segment to be deleted.
     */
    public void deleteSegment(int segmentId) {
    	new API().delete(DELETE_SEGMENT_PATH.formatted(apiStoreID, segmentId), accessToken).then().statusCode(200);
    	logger.info("Deleted customer segment with id: " + segmentId);
    }     
    
}
