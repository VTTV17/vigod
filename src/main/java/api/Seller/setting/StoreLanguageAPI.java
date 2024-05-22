package api.Seller.setting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.setting.languages.CreatedLanguage;
import utilities.model.dashboard.setting.languages.DefaultLanguage;
import utilities.model.dashboard.setting.languages.LanguageCatalog;
import utilities.model.sellerApp.login.LoginInformation;

public class StoreLanguageAPI {
	final static Logger logger = LogManager.getLogger(StoreLanguageAPI.class);

	String packageStatusPath = "/storeservice/api/order-packages/stores/%s?channel=MULTI_LANGUAGE";
	String allAdditionalLanguagesPath = "/storeservice/api/store-language/store/%s/all";
	String defaultLanguagePath = "/storeservice/api/store-language/store/%s/default";
	String languageCatalogPath = "/catalogservices/api/languages";
	String addLanguagePath = "/storeservice/api/store-language/create?defaultLangKey=%s";
	String publishLanguagePath = "/storeservice/api/store-language/publish";
	String removeLanguagePath = "/storeservice/api/store-language/store/%s/%s";

	API api = new API();
	LoginDashboardInfo loginInfo;

	LoginInformation loginInformation;
	public StoreLanguageAPI(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}

	public Response getPackageStatusResponse() {
		return  api.get(packageStatusPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
	}    

	public int getPackageId() {
		String responseAsString = getPackageStatusResponse().asPrettyString();

		Matcher matcher = Pattern.compile("\"id\": (\\d+)").matcher(responseAsString);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		return -1;
	}

	public Response getAllAdditionalLanguagesResponse() {
		return api.get(allAdditionalLanguagesPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
	}    

	public DefaultLanguage getDefaultLanguage() {
		return api.get(defaultLanguagePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response().as(DefaultLanguage.class);
	} 
	
	public AdditionalLanguages[] getAdditionalLanguages() {
		return api.get(allAdditionalLanguagesPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response().as(AdditionalLanguages[].class);
	} 
	
	public LanguageCatalog[] getLanguageCatalog() {
		return api.get(languageCatalogPath, loginInfo.getAccessToken()).then().statusCode(200).extract().response().as(LanguageCatalog[].class);
	} 
	
	public Response addLanguageResponse(String langName, String langCode, String langIcon, String defaultLangKey) {
        String body = """
        		{
        		"storeId": %s,
        		"langCode": "%s",
        		"langName": "%s",
        		"langIcon": "%s"
        		}""".formatted(loginInfo.getStoreID(), langCode, langName, langIcon);
        
        Response response = api.post(addLanguagePath.formatted(defaultLangKey), loginInfo.getAccessToken(), body).then().statusCode(200).extract().response();
        logger.info("Added language %s : %s".formatted(langName, response.jsonPath().getInt("id")));
		return response;
	} 
	
	String publishBody = """
			{
			"storeId": %s,
			"storeLangId": %s,
			"publish": %s
			}""";
	public Response publishLanguageResponse(int storeLangId) {
		String body = publishBody.formatted(loginInfo.getStoreID(), storeLangId, true);
		return api.put(publishLanguagePath, loginInfo.getAccessToken(), body).then().statusCode(204).extract().response();
	} 
	public Response unpublishLanguageResponse(int storeLangId) {
		String body = publishBody.formatted(loginInfo.getStoreID(), storeLangId, false);
		return api.put(publishLanguagePath, loginInfo.getAccessToken(), body).then().statusCode(204).extract().response();
	} 
	
	public CreatedLanguage addLanguageThenReturnClass(String langName, String langCode, String langIcon, String defaultLangKey) {
		return addLanguageResponse(langName, langCode, langIcon, defaultLangKey).as(CreatedLanguage.class);
	} 
	
	public int addLanguage(String langName, String langCode, String langIcon, String defaultLangKey) {
		int addedLanguageId = addLanguageResponse(langName, langCode, langIcon, defaultLangKey).jsonPath().getInt("id");
		return addedLanguageId;
	} 	

	public void publishLanguage(int storeLangId) {
		publishLanguageResponse(storeLangId);
		logger.info("Published storeLangId: " + storeLangId);
	} 	
	
	public void unpublishLanguage(int storeLangId) {
		publishLanguageResponse(storeLangId);
		logger.info("Unpublished storeLangId: " + storeLangId);
	} 	

	Response selectDefaultLanguageResponse(int storeLangId) {
		Response response = api.put(defaultLanguagePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), String.valueOf(storeLangId)).then().statusCode(204).extract().response();
		logger.info("Selected default language: " + storeLangId);
		return response;
	} 	
	
	public void removeLanguage(int storeLangId) {
		Response response = api.delete(removeLanguagePath.formatted(loginInfo.getStoreID(), storeLangId), loginInfo.getAccessToken());
		response.then().statusCode(204);
		logger.info("Deleted storeLangId: " + storeLangId);
	} 	
	
	public void selectDefaultLanguage(int storeLangId) {
		selectDefaultLanguageResponse(storeLangId);
	} 	
}
