package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.gochat.APIFacebook;
import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.gochat.Facebook;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;


/**
 * <p>Ticket: https://mediastep.atlassian.net/browse/BH-24619</p>
 * <p>Preconditions: There exists a staff</p>
 */

public class FacebookPermissionTest extends BaseTest {

	LoginInformation ownerCredentials;
	LoginInformation staffCredentials;
	StaffManagement staffManagementAPI;
	PermissionAPI permissionAPI;
	APIFacebook fbAPI;
	
	
	LoginPage loginPage;
	HomePage homePage;
	Facebook fbPage;
	
	int permissionGroupId;
	
	int staffUserId;
	int staffId;
	String staffName;
	
	int latestTagId;
	
	@BeforeClass
	void precondition() {
		ownerCredentials = new Login().setLoginInformation("+84", "automation0-shop74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffCredentials = new Login().setLoginInformation("+84", "staff74053@mailnesia.com", "fortesting!1").getLoginInformation();
		staffManagementAPI = new StaffManagement(ownerCredentials);
		permissionAPI = new PermissionAPI(ownerCredentials);
		fbAPI = new APIFacebook(ownerCredentials);
		
		staffUserId = new Login().getInfo(staffCredentials).getUserId();
		staffId = staffManagementAPI.getStaffId(staffUserId);
		staffName = staffManagementAPI.getStaffName(staffUserId);
		
		latestTagId = fbAPI.getTagList().get(0).getId();
		
//    	permissionGroupId = permissionAPI.createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    	permissionGroupId = 6284;
    	
    	
		driver = new InitWebdriver().getDriver(browser, headless);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		fbPage = new Facebook(driver);
		
		loginPage.staffLogin(staffCredentials.getEmail(), staffCredentials.getPassword());
		homePage.waitTillSpinnerDisappear1().selectLanguage(language).hideFacebookBubble();
	}	

	@AfterClass
	void rollback() {
		fbAPI.getTagList().parallelStream().filter(tag -> tag.getId() > latestTagId).forEach(t -> fbAPI.deleteTag(t.getId()));
//		permissionAPI.deleteGroupPermission(permissionGroupId);
		driver.quit();
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
    }	
    
	CreatePermission setPermissionModel(String permissionBinary) {
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setSetting_staffManagement(DataGenerator.getRandomListElement(Arrays.asList(new String[] {"0", "1"})));
		model.setGoChat_facebook(permissionBinary);
		return model;
	}

	@DataProvider
	public Object[][] facebookPermission() {
		return new Object[][] { 
//			{"0000000000000000000000000000"},
//			{"0000000000000000000000000001"},
//			{"0000000000000000000000000011"},
//			{"0000000000000000000000000111"}, 
//			{"0000000000000000000000001111"},
//			{"0000000000000000000000011111"},
//			{"0000000000000000000000111111"},
//			{"0000000000000000000001111111"},
//			{"0000000000000000000011111111"}, //
			
			{"0000000000000000000000011111"},
//			{"0000000000000000000000111111"}, 
//			{"0000000000000000000001011111"}, 
//			{"0000000000000000000001111111"}, 
//			{"0000000000000000000010011111"}, 
//			{"0000000000000000000010111111"}, 
//			{"0000000000000000000011011111"}, 
//			{"0000000000000000000011111111"}, 
//			{"0000000000000000000100011111"},
//			{"0000000000000000000100111111"}, 
//			{"0000000000000000000101011111"}, 
//			{"0000000000000000000101111111"}, 
//			{"0000000000000000000110011111"}, 
//			{"0000000000000000000110111111"}, 
//			{"0000000000000000000111011111"}, 
//			{"0000000000000000000111111111"},	
			
//			{"0000000000000000000111111111"},
//			{"0000000000000000001111111111"},
//			{"0000000000000000011111111111"},
//			{"0000000000000000111111111111"},
//			{"0000000000000001111111111111"},
//			{"0000000000000011111111111111"},
//			{"0000000000000111111111111111"},
//			{"0000000000001111111111111111"},
//			{"0000000000011111111111111111"},
//			{"0000000000111111111111111111"},
//			{"0000000001111111111111111111"},
//			{"0000000011111111111111111111"},
//			{"0000000111111111111111111111"},
//			{"0000001111111111111111111111"},
//			{"0000011111111111111111111111"},
//			{"0000111111111111111111111111"},
//			{"0001111111111111111111111111"},
//			{"0011111111111111111111111111"},
//			{"0111111111111111111111111111"},
//			{"1111111111111111111111111111"},
		};
	}	

	@Test(dataProvider = "facebookPermission")
	public void CheckFacebookPermission(String permissionBinary) {
		
		String[] connectedPage = {"101234035989956", "Multiple page Gosell"};
		String[] disconnectedPage = {"105994502156884", "Testing SELL PAGE"};
		
		String staffOldPermissionToken = new Login().getInfo(staffCredentials).getStaffPermissionToken();
		
		PermissionAPI.setStaffCredentials(staffCredentials);
		
		permissionAPI.editGroupPermissionAndGetID(permissionGroupId, "Tien's Permission", "Description Tien's Permission", setPermissionModel(permissionBinary));
		
		String staffNewPermissionToken = new CheckPermission(driver).waitUntilPermissionUpdated(staffOldPermissionToken, staffCredentials);
		
		AllPermissions allPermissionDTO = new AllPermissions(staffNewPermissionToken);
		
		System.out.println(allPermissionDTO.getGoChat().getFacebook());
		System.out.println(allPermissionDTO.getSetting().getStaffManagement());
		
		commonAction.refreshPage();
		commonAction.sleepInMiliSecond(1000, "OMG");
		
		//Check permission on Dashboard
		/*
		fbPage.checkPermissionToConnectAccount(allPermissionDTO);
		fbPage.checkPermissionToDisconnectAccount(allPermissionDTO);
		fbPage.checkPermissionToAddRemovePages(allPermissionDTO);
		
		fbAPI.getConnectedPages().stream().forEach(e -> fbAPI.disconnectPages(e.getPageId()));
		fbPage.checkPermissionToConnectPages(allPermissionDTO, connectedPage[1]);
		
		fbAPI.connectPages(disconnectedPage[0]);
		fbPage.checkPermissionToDisconnectPages(allPermissionDTO, disconnectedPage[1]);
		
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
		
		fbAPI.connectPages(connectedPage[0]);
		List<ConversationEntity> convEntity1 = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId1 = convEntity1.get(0).getSenders().getData().get(0).getId();
		fbAPI.assignConversation(assignedConvsId1, staffId, staffName);
		fbPage.checkPermissionToAssignConversations(allPermissionDTO);	
		
		fbAPI.connectPages(connectedPage[0]);
		List<ConversationEntity> convEntity2 = fbAPI.getConversations(connectedPage[0]).getData();
		String assignedConvsId2 = convEntity2.get(0).getSenders().getData().get(0).getId();
		fbAPI.assignConversation(assignedConvsId2, staffId, staffName);
		fbPage.checkPermissionToUnassignConversations(allPermissionDTO);			
		
		*/
		
//		fbAPI.connectPages(connectedPage[0]);
//		List<ConversationEntity> convEntity3 = fbAPI.getConversations(connectedPage[0]).getData();
//		String assignedConvsId3 = convEntity3.get(0).getSenders().getData().get(0).getId();
//		fbAPI.assignConversation(assignedConvsId3, staffId, staffName);
//		fbPage.checkPermissionToCreateTag(allPermissionDTO);
		
		for (int i=0; i<10; i++) {
			fbAPI.createRandomTag();
		}
		
	}		
}
