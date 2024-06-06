package api.Seller.gochat;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerProfileFB;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.gochat.facebook.GeneralAutomationCampaign;
import utilities.model.gochat.facebook.GeneralBroadcastCampaign;
import utilities.model.gochat.facebook.AllConversation;
import utilities.model.gochat.facebook.ConnectedPages;
import utilities.model.gochat.facebook.CreatedAutomationCampaign;
import utilities.model.gochat.facebook.CreatedBroadcast;
import utilities.model.gochat.facebook.FBPost;
import utilities.model.gochat.facebook.StoreConfig;
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

	String assignTagPath = "/beehiveservices/api/fb-user-tag/assign/%s";
	String unassignTagPath = "/beehiveservices/api/fb-user-tag/revoke/%s";
	
	String linkFBUserPath = "/beehiveservices/api/customer-profiles/social-chat/store/%s/type/FACEBOOK";
	String unlinkFBUserPath = linkFBUserPath + "/user/%s/un-pin";
	
	String getFBPostPath = "/facebookservices/api/fb-posts/store/%s/page/%s?after=&limit=50";
	String getStoreConfigPath = "/facebookservices/api/store-configs/store/%s";
	
	String getAllAutomationPath = "/facebookservices/api/automated-campaigns/search?searchName=&page=0&size=1000&storeId=%s";
	String createAutomationPath = "/facebookservices/api/automated-campaigns";
	String deleteAutomationPath = createAutomationPath + "/%s";
	
	String getAllBroadcastPath = "/facebookservices/api/broadcasts/search?campaignName=&page=0&size=1000&storeId=%s";
	String createBroadcastPath = "/facebookservices/api/broadcasts";
	String deleteBroadcastPath = createBroadcastPath + "/%s/%s";

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
		String[] color = DataGenerator.getRandomListElement(List.of("Red #D93635", "Yellow #FECF2F", "Green #23A762", "Blue #136DFB", "Orange #F3833B")).split("\\s");
		return createTag(color[0] + System.currentTimeMillis(), color[1], true);
	}
	public void deleteTag(int tagId) {
		api.delete(deleteTagInfoPath.formatted(loginInfo.getStoreID(), tagId), loginInfo.getAccessToken()).then().statusCode(204).extract().response();
		logger.info("Deleted tag: " + tagId);
	}
	public void assignTag(int tagId, String conversationId, String pageId) {
		String body = """
				{
				"fbUser": "%s",
				"fbTagInfoId": %s,
				"fbPageId": "%s",
				"storeId": "%s"
				}""".formatted(conversationId, tagId, pageId, loginInfo.getStoreID());
		api.put(assignTagPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(201).extract().response();
		logger.info("Assigned tag '%s' to conversation '%s'".formatted(tagId, conversationId));
	}
	public void unassignTag(int tagId, String conversationId) {
		String body = """
				{
				"fbUser": "%s",
				"fbTagInfoId": %s,
				"storeId": "%s"
				}""".formatted(conversationId, tagId, loginInfo.getStoreID());
		api.put(unassignTagPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(200).extract().response();
		logger.info("Unassigned tag '%s' from conversation '%s'".formatted(tagId, conversationId));
	}
	public void linkFBUserToCustomer(CustomerProfileFB customerProfile, String conversationId, String pageId) {
		String body = """
				{
				"id": %s,
				"fullName": "%s",
				"phone": "%s",
				"email": "",
				"addressId": %s,
				"address": "",
				"locationCode": "",
				"districtCode": "",
				"wardCode": "",
				"birthday": null,
				"socialUserId": "%s",
				"countryCode": "VN",
				"city": "",
				"fbPageId": "%s",
				"phones": [{
				"phoneCode": "%s",
				"phoneName": "%s",
				"phoneNumber": "%s",
				"phoneType": "%s"
				}],
				"backupEmails": [],
				"backupPhones": [],
				"geoLocation": null
				}""".formatted(customerProfile.getId(), 
						customerProfile.getFullName(), 
						customerProfile.getPhones().get(0).getPhoneNumber(),
						customerProfile.getCustomerAddress().getId(),
						//address -> birthday
						conversationId,
						pageId,
						customerProfile.getPhones().get(0).getPhoneCode(),
						customerProfile.getPhones().get(0).getPhoneName(),
						customerProfile.getPhones().get(0).getPhoneNumber(),
						customerProfile.getPhones().get(0).getPhoneType()
						);
		api.post(linkFBUserPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().statusCode(200).extract().response();
		logger.info("Linked '%s' to conversation '%s'".formatted(customerProfile.getFullName(), conversationId));
	}
	public void unlinkFBUserFromCustomer(String conversationId) {
		api.delete(unlinkFBUserPath.formatted(loginInfo.getStoreID(), conversationId), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		logger.info("Un-linked customers from conversation '%s'".formatted(conversationId));
	}
	public StoreConfig getStoreConfig() {
		Response response = api.get(getStoreConfigPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.as(StoreConfig.class);
	} 
	public List<FBPost> getFBPosts(String pageId) {
		Response response = api.get(getFBPostPath.formatted(loginInfo.getStoreID(), pageId), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList("data", FBPost.class);
	} 
	public List<GeneralAutomationCampaign> getAllAutomation() {
		Response response = api.get(getAllAutomationPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList("lstAutomatedCampaign", GeneralAutomationCampaign.class);
	} 
	public CreatedAutomationCampaign createAutomation(ConnectedPages page, StoreConfig config, FBPost post) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String body = """
				{
				"storeId": "%s",
				"pageId": "%s",
				"campaignName": "%s",
				"timeValue": null,
				"timeType": "IMMEDIATELY",
				"status": "DRAFT",
				"automatedCampaignKeyword": [{
				"keywordContent": "how much"
				}
				],
				"storeChatId": %s,
				"campaignPosts": [%s
				],
				"automatedCampaignComponent": [{
				"componentType": "TEXT",
				"error": false,
				"textComponent": {
				"textContent": "Thanks for reaching out to us. A staff will contact you shortly!",
				"buttons": [{
				"title": "Button #1",
				"url": "https://www.gosell.vn/"
				}
				]
				},
				"imageComponent": null,
				"products": null,
				"isButton": true,
				"index": 0
				}
				],
				"isResponseInComment": true,
				"storeConfig": %s,
				"commentContent": "Please check your inbox!"
				}""".formatted(loginInfo.getStoreID(), page.getPageId(), "Automation " + System.currentTimeMillis(),
				page.getId(), mapper.writeValueAsString(post), mapper.writeValueAsString(config));
		
		Response response = api.post(createAutomationPath, loginInfo.getAccessToken(), body).then().statusCode(201).extract().response();
		logger.info("Created automation campaign: " + response.jsonPath().getString("campaignName"));
		return response.as(CreatedAutomationCampaign.class);
	} 
	
	public void deleteAutomation(int campaignId) {
		Response response = api.delete(deleteAutomationPath.formatted(campaignId), loginInfo.getAccessToken()).then().statusCode(204).extract().response();
		logger.info("Deleted automation campaign: " + campaignId);
	} 
	public List<GeneralBroadcastCampaign> getAllBroadcast() {
		Response response = api.get(getAllBroadcastPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList("lstBroadcast", GeneralBroadcastCampaign.class);
	} 
	public CreatedBroadcast createBroadcast(ConnectedPages page, StoreConfig config, List<SegmentList> segmentList) {
		String body = """
				{
				"storeId": "%s",
				"status": "DRAFT",
				"storeChatId": %s,
				"pageId": "%s",
				"campaignName": "%s",
				"lstSegmentId": %s,
				"scheduleTime": null,
				"lstBroadcastComponent": [{
				"componentType": "TEXT",
				"error": false,
				"textComponent": {
				"textContent": "We're delighted to share with you our favorite playlist of songs. Check this out! https://www.youtube.com/watch?v=axcwU3BwLZQ&list=PLcvNQSi-n12CfJHhm36LFsuxZ0SHNFe2Q",
				"buttons": [{
				"title": "Button #1",
				"url": "https://www.gosell.vn/"
				}]
				},
				"imageComponent": null,
				"products": null,
				"isButton": true,
				"index": 0
				}]
				}""".formatted(loginInfo.getStoreID(), page.getId(), page.getPageId(), "Broadcast " + System.currentTimeMillis(), segmentList.stream().map(e -> e.getId()).toList());
		
		Response response = api.post(createBroadcastPath, loginInfo.getAccessToken(), body).then().statusCode(201).extract().response();
		logger.info("Created automation campaign: " + response.jsonPath().getString("campaignName"));
		return response.as(CreatedBroadcast.class);
	} 	
	public void deleteBroadcast(int broadcastId) {
		Response response = api.delete(deleteBroadcastPath.formatted(loginInfo.getStoreID(), broadcastId), loginInfo.getAccessToken()).then().statusCode(204).extract().response();
		logger.info("Deleted broadcast: " + broadcastId);
	} 	
}
