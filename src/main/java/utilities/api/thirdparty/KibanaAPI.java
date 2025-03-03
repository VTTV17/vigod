package utilities.api.thirdparty;

import static java.lang.Thread.sleep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.links.Links;

public class KibanaAPI {
	final static Logger logger = LogManager.getLogger(KibanaAPI.class);
	
    String searchPath = "/elasticsearch/_msearch";
    
    API api;

    public KibanaAPI() {
    	api = new API(Links.KIBANA_URI);
    }

	JsonPath getKeyFromKibanaJsonPath(String username) {
		long endTimestamp = System.currentTimeMillis();
		long startTimestamp = endTimestamp - TimeUnit.SECONDS.toMillis(30);
		
		String body1 = """
				{"index":"logs-*","ignore_unavailable":true,"timeout":0,"preference":1713452407734}
				{"version":true,"size":500,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"_source":{"excludes":[]},"stored_fields":["*"],"script_fields":{},"docvalue_fields":[{"field":"@timestamp","format":"date_time"}],"query":{"bool":{"must":[{"match_all":{}},{"query_string":{"query":"\\"%s\\"","analyze_wildcard":true,"default_field":"*"}},{"range":{"@timestamp":{"gte":%s,"lte":%s,"format":"epoch_millis"}}}],"filter":[],"should":[],"must_not":[]}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"fragment_size":2147483647}}
				{"index":"logs-*","ignore_unavailable":true,"timeout":0,"preference":1713452407734}
				{"version":true,"size":500,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"_source":{"excludes":[]},"stored_fields":["*"],"script_fields":{},"docvalue_fields":[{"field":"@timestamp","format":"date_time"}],"query":{"bool":{"must":[{"match_all":{}},{"query_string":{"query":"\\"%s\\"","analyze_wildcard":true,"default_field":"*"}},{"range":{"@timestamp":{"gte":%s,"lte":%s,"format":"epoch_millis"}}}],"filter":[],"should":[],"must_not":[{"match_phrase":{"logger_name":{"query":"metrics"}}}]}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"fragment_size":2147483647}}
				""";
		
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("kbn-version", "6.4.1");
        
		Response results = api.post(searchPath, "noTokenNeeded", body1.formatted(username, startTimestamp, endTimestamp, username, startTimestamp, endTimestamp), headerMap);
		
		return results.then().statusCode(200).extract().jsonPath();
	}

	String extractKeyFromKibanaJsonPath(JsonPath jsonPath, String keyType) {
		List<String> results = jsonPath.getList("responses[0].hits.hits._source.message");
		
		String correctResult = results.stream().filter(e -> e.matches("(?:sendActivationEmailGoSell|sendPasswordResetEmailGoSell|Send kafka messageDTO).*")).findFirst().orElse("");
		
		Matcher matcher = Pattern.compile("%s='*(\\w+)'*".formatted(keyType)).matcher(correctResult);
		
		return (matcher.find()) ? matcher.group(1):"";
	}	

	/**
	 * Get activationKey/resetKey in Kibana in the last 1 min
	 * @param username Eg. "+84:0703618433" or "tv85@mailnesia.com"
	 * @param keyType either "activationKey" or "resetKey"
	 * @return activationKey/resetKey generated for the username
	 */
	public String getKeyFromKibana(String username, String keyType) {
	    String key = "";
	    int attempts = 0;

	    do {
	        try {
	            sleep(1000);
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        }

	        key = extractKeyFromKibanaJsonPath(getKeyFromKibanaJsonPath(username), keyType);
	        attempts++;
	    } while ((key == null || key.isEmpty()) && attempts < 10);

	    logger.info(String.format("Retrieved %s for username %s: %s", keyType, username, key));
	    return key;
	}
	
}
