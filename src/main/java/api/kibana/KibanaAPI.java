package api.kibana;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.links.Links;
import utilities.utils.PropertiesUtil;

public class KibanaAPI {
    String searchPath = "/elasticsearch/_msearch";
    
    API api;

    public KibanaAPI() {
    	api = new API(Links.KIBANA_URI);
    }

	JsonPath getKeyFromKibanaJsonPath(String username) {
		long endTimestamp = System.currentTimeMillis();
		long startTimestamp = endTimestamp - TimeUnit.MINUTES.toMillis(5);
		
		String body1 = """
				{"index":"logs-*","ignore_unavailable":true,"timeout":0,"preference":1713452407734}
				{"version":true,"size":500,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"_source":{"excludes":[]},"stored_fields":["*"],"script_fields":{},"docvalue_fields":[{"field":"@timestamp","format":"date_time"}],"query":{"bool":{"must":[{"match_all":{}},{"query_string":{"query":"\\"%s\\"","analyze_wildcard":true,"default_field":"*"}},{"range":{"@timestamp":{"gte":%s,"lte":%s,"format":"epoch_millis"}}}],"filter":[],"should":[],"must_not":[]}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"fragment_size":2147483647}}
				{"index":"logs-*","ignore_unavailable":true,"timeout":0,"preference":1713452407734}
				{"version":true,"size":500,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"_source":{"excludes":[]},"stored_fields":["*"],"script_fields":{},"docvalue_fields":[{"field":"@timestamp","format":"date_time"}],"query":{"bool":{"must":[{"match_all":{}},{"query_string":{"query":"\\"%s\\"","analyze_wildcard":true,"default_field":"*"}},{"range":{"@timestamp":{"gte":%s,"lte":%s,"format":"epoch_millis"}}}],"filter":[],"should":[],"must_not":[{"match_phrase":{"logger_name":{"query":"metrics"}}}]}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"fragment_size":2147483647}}
				""";
		Response results = api.postKibana(searchPath, body1.formatted(username, startTimestamp, endTimestamp, username, startTimestamp, endTimestamp));
		
		return results.then().statusCode(200).extract().jsonPath();
	}

	String extractKeyFromKibanaJsonPath(JsonPath jsonPath, String keyType) {
		String message = jsonPath.getString("responses[0].hits.hits._source.message");
		
		Matcher matcher = Pattern.compile("(?:sendActivationEmailGoSell|sendPasswordResetEmailGoSell|Send kafka messageDTO).*" + keyType + "='*(\\w+)'*").matcher(message);
		
		return (matcher.find()) ? matcher.group(1):"";
	}	

	/**
	 * Get activationKey/resetKey in Kibana in the last 5 mins
	 * @param username Eg. "+84:0703618433" or "tv85@mailnesia.com"
	 * @param keyType either "activationKey" or "resetKey"
	 * @return activationKey/resetKey generated for the username
	 */
	public String getKeyFromKibana(String username, String keyType) {
		return extractKeyFromKibanaJsonPath(getKeyFromKibanaJsonPath(username), keyType);
	}
	
	//Usage example. Will be deleted later
	public static void main(String[] args) {
		PropertiesUtil.setEnvironment("STAG");
		
		String key = new KibanaAPI().getKeyFromKibana("+84:0703618433", "resetKey");
		System.out.println(key);
	}
	
}
