package web.Dashboard.service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

import api.Seller.login.Login;
import api.Seller.services.CreateServiceAPI;
import api.Seller.services.ServiceInfoAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class ServiceManagementPage extends ServiceManagementElement {
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commons;
	LoginInformation loginInformation;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreateServiceElement createServiceUI;
	final static Logger logger = LogManager.getLogger(ServiceManagementPage.class);
	public ServiceManagementPage(WebDriver driver){
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		commons = new UICommonAction(driver);
	}
	public ServiceManagementPage(WebDriver driver, LoginInformation loginInformation){
		super(driver);
		this.driver = driver;
		this.loginInformation = loginInformation;
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		commons = new UICommonAction(driver);
		createServiceUI = new CreateServiceElement(driver);
		assertCustomize = new AssertCustomize(driver);
	}
	public ServiceManagementPage goToCreateServicePage(){
		commons.click(loc_btnCreateService);
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	/*Verify permission for certain feature*/
	public void verifyPermissionToManageServices(String permission) {
		if (permission.contentEquals("A")) {
			goToCreateServicePage();
			CreateServicePage service =null;
			try {
				service = new CreateServicePage(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			service.inputServiceName("Test Permission");
			commons.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	public void verifyPermissionToCreateServiceSEO(String permission) {
		if (permission.contentEquals("A")) {
			goToCreateServicePage();
			CreateServicePage service =null;
			try {
				service = new CreateServicePage(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			service.inputSEOTitle("Test SEO");
			String flag = service.getSEOTitle();
			commons.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
			Assert.assertEquals(flag, "Test SEO");
		} else if (permission.contentEquals("D")) {
			goToCreateServicePage();
			CreateServicePage service =null;
			try {
				service = new CreateServicePage(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			service.inputSEOTitle("Test SEO");
			String flag = service.getSEOTitle();
			commons.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
			Assert.assertEquals(flag, "");
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}

	}
	/*-------------------------------------*/
	public void checkSalePitchWhenNoPermision(){
		HomePage homePage = new HomePage(driver);
		int countFail = homePage.verifySalePitchPopupDisplay();
		Assert.assertEquals(countFail,0,"Verify sale-pitch has %s error".formatted(countFail));
	}
	public String clickOnEditNewestService() {
		HomePage homePage = new HomePage(driver);
		homePage.waitTillSpinnerDisappear1();
		commons.click(loc_lst_icnEdit,0);
		logger.info("Click on edit newest service.");
		return commons.getText(loc_lst_lblServiceName,0);
	}
	public CreateServicePage goToEditService(String serviceName) throws Exception {
		if (serviceName.equalsIgnoreCase("")){
			commons.click(loc_lst_lblServiceName,0);
			logger.info("Go to edit newest service.");
			return new CreateServicePage(driver);
		}
		boolean clicked = false;
		List<WebElement> serviceNameElements = commons.getElements(loc_lst_lblServiceName);
		for (WebElement el: serviceNameElements) {
			if (commons.getText(el).equalsIgnoreCase(serviceName)){
				commons.clickElement(el);
				clicked = true;
				break;
			}
		}
		if (!clicked){
			throw new Exception("Service not found: "+serviceName);
		}
		logger.info("Go to service detail: "+serviceName);
//		new HomePage(driver).waitTillSpinnerDisappear1();
		return new CreateServicePage(driver);
	}
	public ServiceManagementPage verifyServiceNotDisplayInList(String serviceName){
		boolean isShow = false;
		for (WebElement el:  commons.getElements(loc_lst_lblServiceName)) {
			if (commons.getText(el).equalsIgnoreCase(serviceName)){
				isShow = true;
				break;
			}
		}
		Assert.assertFalse(isShow,"Verify service not show after deteled");
		logger.info("Verify service not show after deteled");
		return this;
	}
	public void navigateToServiceManagementUrl(){
		commons.navigateToURL(Links.DOMAIN+"/service/list");
		logger.info("Navigate to service list.");
	}
	public void checkPermissionViewListService(int createdServiceId, int noCreatedServiceId){
		navigateToServiceManagementUrl();
		List<Integer> allServiceIdList = new ServiceInfoAPI(loginInformation).getServiceIdList();
		if (allPermissions.getService().getServiceManagement().isViewListService()) {
			List<Integer> checkData = List.of(createdServiceId, noCreatedServiceId);
			assertCustomize.assertTrue(new HashSet<>(allServiceIdList).containsAll(checkData), "[Failed] List service must be contains: %s, but found list service: %s.".formatted(checkData.toString(), allServiceIdList.toString()));
		} else
			if (allPermissions.getService().getServiceManagement().isViewListCreatedService()) {
				assertCustomize.assertTrue(new HashSet<>(allServiceIdList).contains(createdServiceId), "[Failed] List service must be contains: %s".formatted(createdServiceId));
				assertCustomize.assertFalse(new HashSet<>(allServiceIdList).contains(noCreatedServiceId), "[Failed] List service must not be contains: %s".formatted(noCreatedServiceId));
			}
			else {
				assertCustomize.assertTrue(allServiceIdList.isEmpty(), "[Failed] All products must be hidden, but found: %s.".formatted(allServiceIdList.toString()));
			}
			logger.info("Verify permission View list service");
	}
	public void checkPermissionViewServiceDetail(int serviceId){
		navigateToServiceManagementUrl();
		boolean hasPermissionViewServiceList = allPermissions.getService().getServiceManagement().isViewListService();
		boolean hasPermissionViewCreatedServiceList = allPermissions.getService().getServiceManagement().isViewListCreatedService();
		String viewDetailServiceUrl = Links.DOMAIN+"/service/edit/"+serviceId;
		if(allPermissions.getService().getServiceManagement().isViewServiceDetail()){
			// if has permission view list then click to edit button, else navigate to url
			if(hasPermissionViewServiceList||hasPermissionViewCreatedServiceList)
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(loc_lst_icnEdit,0,createServiceUI.loc_txtServiceName),"[Failed] Service name not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(viewDetailServiceUrl,createServiceUI.loc_txtServiceName),"[Failed] Service name not show.");
		}else {
			// if has permission view list then click to edit button, else navigate to url
			if(hasPermissionViewServiceList||hasPermissionViewCreatedServiceList)
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),"[Failed] Restricted page or modal not show.");
			else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(viewDetailServiceUrl),"[Failed] Restricted page or modal not show when click edit button.");
		}
		logger.info("Verify permission View service detail.");
	}
	public void checkPermissionCreateService() {
		navigateToServiceManagementUrl();
		if (allPermissions.getService().getServiceManagement().isCreateService()){
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnCreateService, "/service/create"), "[Failed] Service page not show.");
		checkPermissionViewCollection();
	}
		else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateService), "[Failed] Restricted page or modal not show when click create service.");
	logger.info("Verify permission Create service.");
	}
	public void checkPermissionViewCollection(){
		commons.click(createServiceUI.loc_frmCollection);
		commons.sleepInMiliSecond(5000);
		boolean isSuggestionListShow = commons.getListElement(createServiceUI.loc_lstCollectionSuggestion).isEmpty();
		if (allPermissions.getService().getServiceCollection().isViewCollectionList())
			assertCustomize.assertTrue(!isSuggestionListShow,"[Failed] Collection list not show.");
//			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(createServiceUI.loc_frmCollection,createServiceUI.loc_lstCollectionSuggestion),"[Failed] Collection list not show.");
		else assertCustomize.assertTrue(isSuggestionListShow,"[Failed] Collection should be hidden, but it show now.");
	logger.info("Check permission View collection list");
	}
	public void checkPermissionEditService(int serviceId) {
		String viewDetailServiceUrl = Links.DOMAIN+"/service/edit/"+serviceId;
		if(allPermissions.getService().getServiceManagement().isViewServiceDetail()){
			commons.navigateToURL(viewDetailServiceUrl);
			if(allPermissions.getService().getServiceManagement().isEditService()){
				commons.click(createServiceUI.loc_btnSave);
				try {
					String createSuccessfullyMess = PropertiesUtil.getPropertiesValueByDBLang("services.update.successfullyMessage");
					commons.waitForElementVisible(commons.getElement(createServiceUI.loc_dlgNotification_lblMessage));
					String message= commons.getText(createServiceUI.loc_dlgNotification_lblMessage);
					assertCustomize.assertEquals(message,createSuccessfullyMess,"[Failed] Message '%s' should be show, but message '%s' show.".formatted(createSuccessfullyMess,message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceUI.loc_btnSave),"[Failed] Restricted page or modal not show when click save button.");
			checkPermissionViewCollection();
		}
		logger.info("Check permission Edit service");
	}
	public void checkPermissionActiveService(int serviceId){
		String viewDetailServiceUrl = Links.DOMAIN+"/service/edit/"+serviceId;
		if(allPermissions.getService().getServiceManagement().isViewServiceDetail()) {
			commons.navigateToURL(viewDetailServiceUrl);
			if(allPermissions.getService().getServiceManagement().isActivateService()){
				commons.click(createServiceUI.loc_btnActiveDeactive);
				try {
					assertCustomize.assertEquals(commons.getText(createServiceUI.loc_lblStatus),
							PropertiesUtil.getPropertiesValueByDBLang("services.create.inactiveStatus"),
							"[Failed] Inactive status should be shown, but '%s' is shown".formatted(commons.getText(createServiceUI.loc_lblStatus)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceUI.loc_btnActiveDeactive),"[Failed] Restricted page or modal not show when click Active button.");
		}
	}
	public void checkPermissionDeactivateService(){
		int serviceId = new ServiceInfoAPI(loginInformation).getInactiveServiceId();
		String viewDetailServiceUrl = Links.DOMAIN+"/service/edit/"+serviceId;
		if(allPermissions.getService().getServiceManagement().isViewServiceDetail()) {
			commons.navigateToURL(viewDetailServiceUrl);
			if(allPermissions.getService().getServiceManagement().isDeactivateService()){
				commons.click(createServiceUI.loc_btnActiveDeactive);
				try {
					assertCustomize.assertEquals(commons.getText(createServiceUI.loc_lblStatus),
							PropertiesUtil.getPropertiesValueByDBLang("services.create.activeStatus"),
							"[Failed] Active status should be shown, but '%s' is shown".formatted(commons.getText(createServiceUI.loc_lblStatus)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceUI.loc_btnActiveDeactive),"[Failed] Restricted page or modal not show when click Deactivate button.");
		}
	}
	public void checkPermissionDeleteService(int serviceId){
		boolean hasPermissionViewServiceList = allPermissions.getService().getServiceManagement().isViewListService();
		boolean hasPermissionViewCreatedServiceList = allPermissions.getService().getServiceManagement().isViewListCreatedService();
		String viewDetailServiceUrl = Links.DOMAIN+"/service/edit/"+serviceId;
		if(allPermissions.getService().getServiceManagement().isViewListService()){
			commons.navigateToURL(viewDetailServiceUrl);
			if(allPermissions.getService().getServiceManagement().isDeleteService()) {
				commons.click(createServiceUI.loc_btnDeleteService);
				try {
					assertCustomize.assertEquals(commons.getText(createServiceUI.loc_dlgNotification_lblMessage),
							PropertiesUtil.getPropertiesValueByDBLang("services.delete.confirmMessage"),
							"[Failed] Delete successfull message should be shown, but '%s' is shown".formatted(commons.getText(createServiceUI.loc_dlgNotification_lblMessage)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createServiceUI.loc_btnDeleteService),
					"[Failed] Restricted page or modal not show when click Delete button.");
		}
		if(hasPermissionViewServiceList||hasPermissionViewCreatedServiceList){
			navigateToServiceManagementUrl();
			if(allPermissions.getService().getServiceManagement().isDeleteService()){
				commons.click(loc_lst_icnDelete,0);
				commons.click(loc_dlgNotification_btnOK);
				try {
					assertCustomize.assertEquals(commons.getText(createServiceUI.loc_dlgNotification_lblMessage),
							PropertiesUtil.getPropertiesValueByDBLang("services.delete.confirmMessage"),
							"[Failed] Delete successfull message should be shown, but '%s' is shown".formatted(commons.getText(createServiceUI.loc_dlgNotification_lblMessage)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				commons.click(loc_lst_icnDelete,0);
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_dlgNotification_btnOK),
						"[Failed] Restricted page or modal not show when click OK button on Confirmation delete popup.");
			}
		}
	}
	public void checkPermissionServiceManagement(AllPermissions allPermissions, int createdServiceId, int noCreatedServiceId){
		this.allPermissions = allPermissions;
		checkPermissionViewListService(createdServiceId,noCreatedServiceId);
		checkPermissionViewServiceDetail(createdServiceId);
		checkPermissionCreateService();
		checkPermissionEditService(createdServiceId);
		checkPermissionActiveService(createdServiceId);
		checkPermissionDeactivateService();
		checkPermissionDeleteService(createdServiceId);
	}
}
