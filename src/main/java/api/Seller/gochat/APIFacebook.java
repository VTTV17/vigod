package api.Seller.gochat;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.gochat.facebook.AllConversation;
import utilities.model.gochat.facebook.ConnectedPages;
import utilities.model.gochat.facebook.TagManagement;
import utilities.model.sellerApp.login.LoginInformation;

public class APIFacebook {
	final static Logger logger = LogManager.getLogger(APIFacebook.class);

	String generalPath = "/facebookservices/api/store-chats/store/%s";
	String getConnectedPagePath = generalPath + "?usingStatus=APPROVED";
	String connectPagePath = generalPath + "/claim";
	String disconnectPagePath = generalPath + "/unclaim";

	String getConversationPath = "/facebookservices/api/fb-chat/find-conversation-v2/%s?sellerPage=%s";
	String assignConversationPath = "/facebookservices/api/customer-fb-infos/store/%s/fb-user/%s/assign";
	String unassignConversationPath = "/facebookservices/api/customer-fb-infos/store/%s/fb-user/%s/un-assign";

	String customerFBInfoPath = "/facebookservices/api/customer-fb-infos/store/%s/fb-user/%s";

	String tagInfoPath = "/beehiveservices/api/fb-tag-infos/%s";
	String getTagListInfoPath = "/beehiveservices/api/fb-tag-infos/store/%s";
	String deleteTagInfoPath = tagInfoPath + "/%s";

	API api = new API();
	LoginDashboardInfo loginInfo;

	LoginInformation loginInformation;
	public APIFacebook(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}

	public List<ConnectedPages> getConnectedPages() {
		Response response = api.get(getConnectedPagePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList(".", ConnectedPages.class);
	} 
	public void connectPages(String pageId) {
		api.put(connectPagePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), "[\"%s\"]".formatted(pageId)).then().statusCode(200).extract().response();
		logger.info("Connected page: " + pageId);
	}    
	public void disconnectPages(String pageId) {
		api.put(disconnectPagePath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), "[\"%s\"]".formatted(pageId)).then().statusCode(200).extract().response();
		logger.info("Disconnected page: " + pageId);
	}
	public AllConversation getConversations(String pageId) {
		Response response = api.get(getConversationPath.formatted(loginInfo.getStoreID(), pageId), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.as(AllConversation.class);
	}  
	public List<String> getFBUserFromConversation(String pageId) {
		List<String> fbUsers = getConversations(pageId).getData().stream().map(entity -> entity.getSenders().getData().get(0).getName()).toList();
		logger.info("Retrieved FB users from conversation: " + fbUsers);
		return fbUsers;
	}    
	public void assignConversation(String conversationId, int staffId, String staffName) {
		String body = """
				{
				"staffId": %s,
				"staffIsSeller": false,
				"staffName": "%s"
				}""".formatted(staffId, staffName);
		api.put(assignConversationPath.formatted(loginInfo.getStoreID(), conversationId), loginInfo.getAccessToken(), body).then().statusCode(200).extract().response();

		logger.info("Assigned conversationId %s to staff '%s'".formatted(conversationId, staffName));
	}
	public void unassignConversation(String conversationId) {
		api.put(unassignConversationPath.formatted(loginInfo.getStoreID(), conversationId), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		logger.info("Unassigned conversationId %s from staff".formatted(conversationId));
	}
	public Response createTagResponse(String name, String color, boolean isShown) {
		String body = """
				{
				"tagName": "%s",
				"tagColor": "%s",
				"isShow": %s,
				"storeId": "%s"
				}""".formatted(name, color, isShown, loginInfo.getStoreID());
		Response response = api.post(tagInfoPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(201).extract().response();
		logger.info("Added tag: " + name);
		return response;
	}
	public List<TagManagement> getTagList() {
		Response response = api.get(getTagListInfoPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList(".", TagManagement.class);
	}
	public TagManagement createTag(String name, String color, boolean isShown) {
		return createTagResponse(name, color, isShown).as(TagManagement.class);
	}
	public TagManagement createRandomTag() {
		return createTag("Auto " + System.currentTimeMillis(), "#4051B6", true);
	}
	public void deleteTag(int tagId) {
		api.delete(deleteTagInfoPath.formatted(loginInfo.getStoreID(), tagId), loginInfo.getAccessToken()).then().statusCode(204).extract().response();
		logger.info("Deleted tag: " + tagId);
	}

}
