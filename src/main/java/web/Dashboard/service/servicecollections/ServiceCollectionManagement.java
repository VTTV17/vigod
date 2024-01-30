package web.Dashboard.service.servicecollections;

import java.time.Duration;

import api.Seller.services.ServiceCollectionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class ServiceCollectionManagement {

	final static Logger logger = LogManager.getLogger(ServiceCollectionManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreateEditServiceCollectionElement createServiceCollectionUI;
	LoginInformation loginInformation;

	public ServiceCollectionManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
	}
	public ServiceCollectionManagement(WebDriver driver, LoginInformation loginInformation) {
		this.driver = driver;
		this.loginInformation = loginInformation;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createServiceCollectionUI = new CreateEditServiceCollectionElement(driver);
	}
	By loc_btnCreateServiceCollection = By.cssSelector(".collection-list-page button");
	By loc_lst_lblServiceCollectionName = By.cssSelector(".collection-name b");
	By loc_lst_icnEdit = By.cssSelector(".d-desktop-block .first-button");
	By loc_lst_lblType = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]");
	By loc_lst_lblMode = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]");
	By loc_lst_lblItems = By.xpath("//div[contains(@class,'products')]");
	By loc_txtSearch = By.xpath("//span[contains(@class,'gs-search-box')]//input");
	By loc_lst_icnDelete = By.cssSelector(".actions .lastest-button");
	By loc_dlgConfirmation_btnOK = By.cssSelector(".modal-footer .gs-button__green");

	public CreateServiceCollection clickCreateServiceCollection() {
		commonAction.click(loc_btnCreateServiceCollection);
		logger.info("Clicked on 'Create Service Collection' button.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return new CreateServiceCollection(driver);
	}

	/*Verify permission for certain feature*/
	public void verifyPermissionToManageServiceCollection(String permission) {
		if (permission.contentEquals("A")) {
			clickCreateServiceCollection();
			new CreateServiceCollection(driver).inputCollectionName("Test Permission");
			commonAction.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}
	/*-------------------------------------*/
	public EditServiceCollection goToEditServiceCollection(String collectionName) throws Exception {
		boolean clicked = false;
		for (int i=0;i< commonAction.getElements(loc_lst_lblServiceCollectionName).size();i++) {
			if (commonAction.getText(loc_lst_lblServiceCollectionName,i).equalsIgnoreCase(collectionName)){
				commonAction.click(loc_lst_icnEdit,i);
				clicked = true;
				break;
			}
		}
		if (!clicked){
			throw new Exception("Service collection %s not found".formatted(collectionName));
		}
		new HomePage(driver).waitTillSpinnerDisappear1();
//		commonAction.sleepInMiliSecond(1000);
		logger.info("Go to collection: "+collectionName);
		return new EditServiceCollection(driver);
	}
	public ServiceCollectionManagement verifyCollectionName(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblServiceCollectionName,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify collection name after created");
		return this;
	}

	public ServiceCollectionManagement verifyType(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblType,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify type after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyMode(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblMode,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify mode after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyItem(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblItems,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify items after collection created");
		return this;
	}
	public ServiceCollectionManagement refreshPage(){
		commonAction.sleepInMiliSecond(1000);
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}
	public ServiceCollectionManagement refreshPageUtilCollectUpdate(String expectItemNumber){
		for (int i=0;i<10;i++){
			refreshPage();
			String itemNumber = commonAction.getText(loc_lst_lblItems,0);
			if(itemNumber.equals(String.valueOf(expectItemNumber))){
				break;
			}
		}
		return this;
	}
	public ServiceCollectionManagement verifyCollectionInfoAfterCreated(String collectionName, String type, String mode, String items) {
		verifyCollectionName(collectionName, 0);
		verifyType(type, 0);
		verifyMode(mode, 0);
		verifyItem(items, 0);
		logger.info("Verify collection info after created.");
		return this;
	}
	public ServiceCollectionManagement searchCollection(String collectionName){
		commonAction.inputText(loc_txtSearch,collectionName);
		logger.info("Input to search collection: "+collectionName);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	public String getTheFirstCollectionName() {
		String name = commonAction.getText(loc_lst_lblServiceCollectionName,0);
		logger.info("Get the first collection name in list: " + name);
		return name;
	}
	public ServiceCollectionManagement navigate(){
		HomePage home = new HomePage(driver);
		home.navigateToPage("Services", "Service Collections");
		return this;
	}
	public void navigateToServiceCollectUrl(){
		commonAction.navigateToURL(Links.DOMAIN+"/collection_service/list");
		commonAction.sleepInMiliSecond(200);
		logger.info("Navigate to service list.");
	}
	public void deleteTheFirstCollection() {
		commonAction.click(loc_lst_icnDelete,0);
		commonAction.click(loc_dlgConfirmation_btnOK);
		commonAction.sleepInMiliSecond(1000);
		commonAction.click(loc_dlgConfirmation_btnOK);
		new HomePage(driver).waitTillSpinnerDisappear();
		logger.info("Delete the first collection");
	}
	public void verifyCollectNameNotDisplayInList(String collectionName) {
		for (int i = 0; i < commonAction.getElements(loc_lst_lblServiceCollectionName).size(); i++) {
			String collectionNameInList = commonAction.getText(loc_lst_lblServiceCollectionName,i);
			if (collectionNameInList.equalsIgnoreCase(collectionName)) {
				Assert.assertTrue(false, collectionName + ": still display in position " + i);
			}
		}
		logger.info("Verify collection: %s - not show in list after deleted".formatted(collectionName));
		Assert.assertTrue(true, collectionName + ": not show in collection list");
	}
	public void checkSalePitchWhenNoPermision(){
		HomePage homePage = new HomePage(driver);
		int countFail = homePage.verifySalePitchPopupDisplay();
		Assert.assertEquals(countFail,0,"Verify sale-pitch has %s error".formatted(countFail));
	}
	public void checkPermissionViewCollectionList(){
		navigateToServiceCollectUrl();
		int collectionListSize = commonAction.getElements(loc_lst_lblServiceCollectionName).size();
		if(allPermissions.getService().getServiceCollection().isViewCollectionList()){
			assertCustomize.assertTrue(collectionListSize>0,"[Failed] Verify collection list show. Currently Service collection list not show (size is %s)".formatted(collectionListSize));
		}else
			assertCustomize.assertTrue(collectionListSize == 0,"[Failed] Verify collection list not show. Currently, Service collection list still show (size is %s)".formatted(collectionListSize));
		logger.info("Verified permission View collection list.");
	}
	public void checkPermissionViewCollectionDetail(int collectionId){
		navigateToServiceCollectUrl();
		String viewDetailCollectionUrl = Links.DOMAIN+"/collection_service/edit/service/"+collectionId;
		if(allPermissions.getService().getServiceCollection().isViewCollectionDetail()){
			//check if has "View collection list" permission
			if(allPermissions.getService().getServiceCollection().isViewCollectionList())
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(loc_lst_icnEdit,0,createServiceCollectionUI.loc_txtCollectionName),
						"[Failed] Collection name value not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(viewDetailCollectionUrl,createServiceCollectionUI.loc_txtCollectionName),
						"[Failed] Collection name not show.");
		}else
			if(allPermissions.getService().getServiceCollection().isViewCollectionList())
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),"[Failed] Restricted page or modal not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(viewDetailCollectionUrl),"[Failed] Restricted page or modal not show when click edit button.");
		logger.info("Verified permission View collection detail");
	}
	public void checkPermissionCreateCollection(){
		navigateToServiceCollectUrl();
		if(allPermissions.getService().getServiceCollection().isCreateCollection())
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnCreateServiceCollection, "/collection_service/create/SERVICE"), "[Failed] Create service collection page not show.");
		else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateServiceCollection), "[Failed] Restricted page or modal not show when click create service collection.");
		logger.info("Verified permission Create collection.");
	}
	public void checkPermissionEditCollection(int collectionId){
		String viewDetailServiceUrl = Links.DOMAIN+"/collection_service/edit/service/"+collectionId;
		if(allPermissions.getService().getServiceManagement().isViewServiceDetail()){
			commonAction.navigateToURL(viewDetailServiceUrl);
			commonAction.sleepInMiliSecond(200);
			if(allPermissions.getService().getServiceCollection().isEditCollection()){
				commonAction.click(createServiceCollectionUI.loc_btnSave);
				try {
					String createSuccessfullyMess = PropertiesUtil.getPropertiesValueByDBLang("services.serviceCollections.update.successfullyMessage");
					commonAction.waitForElementVisible(commonAction.getElement(createServiceCollectionUI.loc_dlgNotification_lblMessage));
					String message= commonAction.getText(createServiceCollectionUI.loc_dlgNotification_lblMessage);
					assertCustomize.assertEquals(message,createSuccessfullyMess,"[Failed] Message '%s' should be show, but message '%s' show.".formatted(createSuccessfullyMess,message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceCollectionUI.loc_btnSave),"[Failed] Restricted page or modal not show when click save button.");
		}
		logger.info("Verified permission Edit collection.");
	}
	public void checkPermissionDeleteServiceCollection(int collectionId){
		boolean hasPermissionViewServiceCollectionList = allPermissions.getService().getServiceCollection().isViewCollectionList();
		boolean hasPermissionViewCollectionDetail = allPermissions.getService().getServiceCollection().isViewCollectionDetail();
		String viewDetailServiceCollectionUrl = Links.DOMAIN+"/collection_service/edit/service"+collectionId;
		if(hasPermissionViewCollectionDetail){
			commonAction.navigateToURL(viewDetailServiceCollectionUrl);
			if(allPermissions.getService().getServiceManagement().isDeleteService()) {
				commonAction.click(createServiceCollectionUI.loc_btnDelete);
				commonAction.click(createServiceCollectionUI.loc_dlgConfirmation_btnOK);
				try {
					assertCustomize.assertEquals(commonAction.getText(createServiceCollectionUI.loc_dlgNotification_lblMessage),
							PropertiesUtil.getPropertiesValueByDBLang("services.serviceCollections.deleteSuccessfully"),
							"[Failed] Delete successfull message should be shown, but '%s' is shown".formatted(commonAction.getText(createServiceCollectionUI.loc_dlgNotification_lblMessage)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				commonAction.click(createServiceCollectionUI.loc_btnDelete);
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceCollectionUI.loc_dlgConfirmation_btnOK),
						"[Failed] Restricted page or modal not show when click Delete button.");
				}
			}
		if(hasPermissionViewServiceCollectionList){
			navigateToServiceCollectUrl();
			if(allPermissions.getService().getServiceManagement().isDeleteService()){
				commonAction.click(loc_lst_icnDelete,0);
				try {
					assertCustomize.assertEquals(commonAction.getText(createServiceCollectionUI.loc_dlgNotification_lblMessage),
							PropertiesUtil.getPropertiesValueByDBLang("services.delete.confirmMessage"),
							"[Failed] Delete popup should be shown, but '%s' is shown".formatted(commonAction.getText(createServiceCollectionUI.loc_dlgNotification_lblMessage)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnDelete,0),
						"[Failed] Restricted page or modal not show when click delete icon.");
			}
		}
		logger.info("Verified permission Delete collection.");
	}
	public ServiceCollectionManagement checkPermissionServiceCollection(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		int collectionId = new ServiceCollectionAPI(loginInformation).getNewestCollectionID();
		checkPermissionViewCollectionList();
		checkPermissionViewCollectionDetail(collectionId);
		checkPermissionCreateCollection();
		checkPermissionEditCollection(collectionId);
		checkPermissionDeleteServiceCollection(collectionId);
		return this;
	}
}
