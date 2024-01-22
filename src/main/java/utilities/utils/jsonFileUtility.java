package utilities.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

public class jsonFileUtility {
	public static JsonNode readJsonFile(String fileName) {
		URL url = Resources.getResource(fileName);
		String jsonString = null;
		JsonNode rootNode = null;
		
		try {
			jsonString = FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		try {
			rootNode = new ObjectMapper().readTree(jsonString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return rootNode;
	}
}
