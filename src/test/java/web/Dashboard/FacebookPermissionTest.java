package web.Dashboard;

import java.util.Arrays;
import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICustomerDetail;
import api.Seller.customers.APISegment;
import api.Seller.gochat.APIFacebook;
import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.customer.CustomerProfileFB;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.gochat.facebook.ConnectedPages;
import utilities.model.gochat.facebook.ConversationEntity;
import utilities.model.gochat.facebook.CreatedAutomationCampaign;
import utilities.model.gochat.facebook.CreatedBroadcast;
import utilities.model.gochat.facebook.TagManagement;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import utilities.utils.ListUtils;
import web.Dashboard.gochat.Facebook;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;


/**
 * <p>Ticket: https://mediastep.atlassian.net/browse/BH-24619</p>
 * <p>Preconditions: There exists a staff, a Facebook post and a customer segment</p>
 */

public class FacebookPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	StaffManagement staffManagementAPI;
	APIAllCustomers customerAPI;
	APICustomerDetail customerDetailAPI;
	APISegment segmentAPI;
	PermissionAPI permissionAPI;
	APIFacebook fbAPI;
	
	
	LoginPage loginPage;
	HomePage homePage;
	Facebook fbPage;
	
	int permissionGroupId;
	
	int staffUserId;
	int staffId;
	String staffName;
	int linkedCustomerId;
	
	int latestTagId;
	int latestAutomationId;
	int latestBroadcastId;
	
	@BeforeClass
	void precondition() {
		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staffa74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffManagementAPI = new StaffManagement(ownerCredentials);
		customerAPI = new APIAllCustomers(ownerCredentials);
		customerDetailAPI = new APICustomerDetail(ownerCredentials);
		segmentAPI = new APISegment(ownerCredentials);
		permissionAPI = new PermissionAPI(ownerCredentials);
		fbAPI = new APIFacebook(ownerCredentials);
		
		staffUserId = new Login().getInfo(staffCredentials).getUserId();
		staffId = staffManagementAPI.getStaffId(staffUserId);
		staffName = staffManagementAPI.getStaffName(staffUserId);
		
		linkedCustomerId = customerAPI.getAllCustomerIds().parallelStream().filter(id -> customerDetailAPI.getCustomerInfoAtGoSocial(id).getPhones().size()>0).findFirst().orElse(null);
		
		latestTagId = fbAPI.getTagList().get(0).getId();
		latestAutomationId = fbAPI.getAllAutomation().get(0).getId();
		latestBroadcastId = fbAPI.getAllBroadcast().get(0).getId();
		
    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		fbPage = new Facebook(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	
	void deleteTags() {
		fbAPI.getTagList().parallelStream().filter(tag -> tag.getId() > latestTagId).forEach(t -> fbAPI.deleteTag(t.getId()));
	}
	void deleteAutomationCampaigns() {
		fbAPI.getAllAutomation().parallelStream().filter(campaign -> campaign.getId() > latestAutomationId).forEach(t -> fbAPI.deleteAutomation(t.getId()));
	}
	void deleteBroadcastCampaigns() {
		fbAPI.getAllBroadcast().parallelStream().filter(campaign -> campaign.getId() > latestBroadcastId).forEach(t -> fbAPI.deleteBroadcast(t.getId()));
	}
	@AfterClass
	void rollback() {
		deleteTags();
		deleteAutomationCampaigns();
		deleteBroadcastCampaigns();
		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
    }	
    
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_staffManagement(ListUtils.getRandomListElement(Arrays.asList(new String[] {"0", "1"})));
		model.setCustomer_customerManagement("11");
		model.setCustomer_segment("1");
//		model.setCustomer_customerManagement(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"11", "00"})));
		model.setGoChat_facebook(permissionBinary);
		return model;
	}

	@DataProvider
	public Object[][] facebookPermission() {
		return new Object[][] { 
			{"0000000000000000000000000000"},
//			{"0000000000000000000000000001"},
//			{"0000000000000000000000000011"},
//			{"0000000000000000000000000111"}, 
//			{"0000000000000000000000001111"},
			{"0000000000000000000000011111"},
//			{"0000000000000000000000111111"},
//			{"0000000000000000000001111111"},
			{"0000000000000000000011111111"},
//			{"0000000000000000000111111111"},
//			{"0000000000000000001111111111"},
			{"0000000000000000011111111111"},
//			{"0000000000000000111111111111"},
//			{"0000000000000001111111111111"}, 
//			{"0000000000000011111111111111"},
			{"0000000000000111111111111111"},
//			{"0000000000001111111111111111"},//unlink
//			{"0000000000011111111111111111"},//chat
//			{"0000000000111111111111111111"},//order
//			{"0000000001111111111111111111"},//view campaign
			{"0000000011111111111111111111"},//View campaign detail
//			{"0000000111111111111111111111"},//Create automation campaign
//			{"0000001111111111111111111111"},//edit campaign
//			{"0000011111111111111111111111"},//delete campaign
			{"0000111111111111111111111111"},//viewbroadcst
//			{"0001111111111111111111111111"},//view detail
//			{"0011111111111111111111111111"},//create broadcast
//			{"0111111111111111111111111111"},//edit
			{"1111111111111111111111111111"},//delete
		};
	}	

	@Test(dataProvider = "facebookPermission")
	public void CheckFacebookPermission(String permissionBinary) throws JsonProcessingException {
		
		String[] connectedPage = {"101234035989956", "Multiple page Gosell"};
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		PermissionAPI.setStaffCredentials(staffCredentials);
		
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Customer Permission", "Description Customer Permission", setPermissionModel(permissionBinary));
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getGoChat().getFacebook());
		
		commonAction.refreshPage();
		UICommonAction.sleepInMiliSecond(1000, "OMG");
		
		//Check permission on Dashboard
		fbPage.checkPermissionToConnectAccount(allPermissionDTO);
		fbPage.checkPermissionToDisconnectAccount(allPermissionDTO);
		fbPage.checkPermissionToAddRemovePages(allPermissionDTO);
		
		fbAPI.getConnectedPages().stream().forEach(e -> fbAPI.disconnectPages(e.getPageId()));
		fbPage.checkPermissionToConnectPages(allPermissionDTO, connectedPage[1]);
		
		fbAPI.connectPages(connectedPage[0]);
		fbPage.checkPermissionToDisconnectPages(allPermissionDTO, connectedPage[1]);
		
		//Make sure there are at least 2 conversations
		fbAPI.connectPages(connectedPage[0]);
		List<ConversationEntity> convEntity = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId = convEntity.get(0).getSenders().getData().get(0).getId();
		String unassignedConvsId = convEntity.get(1).getSenders().getData().get(0).getId();
		String assignedConvsName = convEntity.get(0).getSenders().getData().get(0).getName();
		String unassignedConvsName = convEntity.get(1).getSenders().getData().get(0).getName();
		fbAPI.assignConversation(assignedConvsId, staffId, staffName);
		fbAPI.unassignConversation(unassignedConvsId);
		fbPage.checkPermissionToViewConversations(allPermissionDTO, assignedConvsName, unassignedConvsName);
		
		List<ConversationEntity> convEntity1 = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId1 = convEntity1.get(0).getSenders().getData().get(0).getId();
		fbAPI.assignConversation(assignedConvsId1, staffId, staffName);
		fbPage.checkPermissionToAssignConversations(allPermissionDTO);	
		
		List<ConversationEntity> convEntity2 = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId2 = convEntity2.get(0).getSenders().getData().get(0).getId();
		fbAPI.assignConversation(assignedConvsId2, staffId, staffName);
		fbPage.checkPermissionToUnassignConversations(allPermissionDTO);			

		List<ConversationEntity> convEntity3 = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId3 = convEntity3.get(0).getSenders().getData().get(0).getId();
		fbAPI.assignConversation(assignedConvsId3, staffId, staffName);
		fbPage.checkPermissionToCreateTag(allPermissionDTO);		

		fbPage.checkPermissionToDeleteTag(allPermissionDTO, fbAPI.createRandomTag().getTagName());		

		TagManagement hiddenTag = fbAPI.createRandomTag();
		fbPage.checkPermissionToHideTag(allPermissionDTO, hiddenTag.getTagName());
		fbAPI.deleteTag(hiddenTag.getId());
		
		TagManagement assignedTag = fbAPI.createRandomTag();
		fbAPI.unassignTag(assignedTag.getId(), assignedConvsId3);
		fbPage.checkPermissionToAssignTag(allPermissionDTO, assignedTag.getTagName());
		fbAPI.deleteTag(assignedTag.getId());		
		
		TagManagement unassignedTag = fbAPI.createRandomTag();
		fbAPI.assignTag(unassignedTag.getId(), assignedConvsId3, connectedPage[0]);
		fbPage.checkPermissionToUnassignTag(allPermissionDTO, unassignedTag.getTagName());
		fbAPI.deleteTag(unassignedTag.getId());		

		fbAPI.unlinkFBUserFromCustomer(assignedConvsId3);
		CustomerProfileFB customerProfile = customerDetailAPI.getCustomerInfoAtGoSocial(linkedCustomerId);
		fbPage.checkPermissionToLinkCustomer(allPermissionDTO, assignedConvsId3, customerProfile.getFullName());		

		fbAPI.linkFBUserToCustomer(customerProfile, assignedConvsId3, connectedPage[0]);
		fbPage.checkPermissionToUnlinkCustomer(allPermissionDTO);
		fbAPI.unlinkFBUserFromCustomer(assignedConvsId3);
		
		//Reply to messages
		//Place orders for customers

		ConnectedPages connectedPage1 = fbAPI.getConnectedPages().get(0);
		CreatedAutomationCampaign createdAutomationCampaign = fbAPI.createAutomation(connectedPage1, fbAPI.getStoreConfig(), fbAPI.getFBPosts(connectedPage1.getPageId()).get(0));
		fbPage.checkPermissionToViewAutomationCampaign(allPermissionDTO);
		fbAPI.deleteAutomation(createdAutomationCampaign.getId());
		
		CreatedAutomationCampaign createdAutomationCampaign2 = fbAPI.createAutomation(connectedPage1, fbAPI.getStoreConfig(), fbAPI.getFBPosts(connectedPage1.getPageId()).get(0));
		fbPage.checkPermissionToViewAutomationCampaignDetail(allPermissionDTO, createdAutomationCampaign2);
		fbAPI.deleteAutomation(createdAutomationCampaign2.getId());		
		
		fbPage.checkPermissionToCreateAutomationCampaign(allPermissionDTO, connectedPage1);		

		CreatedAutomationCampaign createdAutomationCampaign4 = fbAPI.createAutomation(connectedPage1, fbAPI.getStoreConfig(), fbAPI.getFBPosts(connectedPage1.getPageId()).get(0));
		fbPage.checkPermissionToEditAutomationCampaign(allPermissionDTO, createdAutomationCampaign4);
		fbAPI.deleteAutomation(createdAutomationCampaign4.getId());			
		
		CreatedAutomationCampaign createdAutomationCampaign5 = fbAPI.createAutomation(connectedPage1, fbAPI.getStoreConfig(), fbAPI.getFBPosts(connectedPage1.getPageId()).get(0));
		fbPage.checkPermissionToDeleteAutomationCampaign(allPermissionDTO, createdAutomationCampaign5);		
		
		List<SegmentList> segmentList = segmentAPI.getSegmentList();
		CreatedBroadcast broadcast = fbAPI.createBroadcast(connectedPage1, fbAPI.getStoreConfig(), segmentList);
		fbPage.checkPermissionToViewBroadcastList(allPermissionDTO);
		fbAPI.deleteBroadcast(broadcast.getId());		

		CreatedBroadcast broadcast2 = fbAPI.createBroadcast(connectedPage1, fbAPI.getStoreConfig(), segmentList);
		fbPage.checkPermissionToViewBroadcastCampaignDetail(allPermissionDTO, broadcast2);
		fbAPI.deleteBroadcast(broadcast2.getId());		

		fbPage.checkPermissionToCreateBroadcastCampaign(allPermissionDTO, connectedPage1);	
		
		CreatedBroadcast broadcast3 = fbAPI.createBroadcast(connectedPage1, fbAPI.getStoreConfig(), segmentList);
		fbPage.checkPermissionToEditBroadcastCampaign(allPermissionDTO, broadcast3);
		fbAPI.deleteBroadcast(broadcast3.getId());	
		
		CreatedBroadcast broadcast4 = fbAPI.createBroadcast(connectedPage1, fbAPI.getStoreConfig(), segmentList);
		fbPage.checkPermissionToDeleteBroadcastCampaign(allPermissionDTO, broadcast4);
		
	}		
}