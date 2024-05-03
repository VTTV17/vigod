package api;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectBuilder {
	protected JSONObject jsonData = new JSONObject();
	
    public static JSONObject mergeJSONObjects(JSONObject ...jsonObjects) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(jsonObjects[0], JSONObject.getNames(jsonObjects[0]));
            for (int i=1; i<jsonObjects.length; i++) {
                for (String keys : JSONObject.getNames(jsonObjects[i])) {
                    mergedJSON.put(keys, jsonObjects[i].get(keys));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("JSON Exception" + e);
        }
        return mergedJSON;
    }
    
    public JSONObject build() {
        return jsonData;
    }
}
