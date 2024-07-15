package web.Dashboard.onlineshop.pages;

import api.Seller.sale_channel.onlineshop.APIPages;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.data.DataGenerator;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

import java.util.List;

public class PageManagement {

	final static Logger logger = LogManager.getLogger(PageManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	AddPage addPage;
	LoginInformation staffLoginInfo;
	LoginInformation ownerLoginInfo;
	public PageManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		addPage = new AddPage(driver);
	}
	public PageManagement getLoginInfo(LoginInformation staffLoginInfo, LoginInformation ownerLoginInfo){
		this.staffLoginInfo = staffLoginInfo;
		this.ownerLoginInfo = ownerLoginInfo;
		return this;
	}
	
    By loc_btnAddPage = By.cssSelector(".gss-content-header--undefined .gs-button__green");
    By loc_lst_icnEditPage = By.cssSelector(".icon-edit");
	By loc_lst_icnDeletePage = By.cssSelector(".icon-delete");
    public AddPage clickAddPage() {
    	commonAction.click(loc_btnAddPage);
    	logger.info("Clicked on 'Add Page' button.");
    	return new AddPage(driver);
    }    	
    public PageManagement navigateByUrl(){
		String url = Links.DOMAIN + Links.PAGE_MANAGEMENT_PATH;
		commonAction.navigateToURL(url);
		return this;
	}
	public PageManagement deleteAPage(){
		commonAction.click(loc_lst_icnDeletePage,0);
		commonAction.sleepInMiliSecond(300,"Wait confirm popup show.");
		new ConfirmationDialog(driver).clickGreenBtn();
		return this;
	}
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreatePage(String permission) {
		if (permission.contentEquals("A")) {
			clickAddPage();
			new AddPage(driver).inputPageTitle("Test Permission");
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-----------------Staff Permission--------------------*/
	public boolean hasViewPageList(){
		return allPermissions.getOnlineStore().getPage().isViewPageList();
	}
	public boolean hasAddPage(){
		return allPermissions.getOnlineStore().getPage().isAddPage();
	}
	public boolean hasEditPage(){
		return allPermissions.getOnlineStore().getPage().isEditPage();
	}
	public boolean hasDeletePage(){
		return allPermissions.getOnlineStore().getPage().isDeletePage();
	}
	public boolean hasTranslatePage(){
		return allPermissions.getOnlineStore().getPage().isTranslatePage();
	}
	public void checkViewPageList(){
		List<WebElement> editList = commonAction.getElements(loc_lst_icnEditPage,2);
		if(hasViewPageList()){
			assertCustomize.assertTrue(editList.size()>0,"[Failed] Page list should be shown.");
		}else assertCustomize.assertTrue(editList.isEmpty(),"[Failed] Page list should be empty.");
		logger.info("Verified View page list permission.");
	}
	public void checkAddPage(){
		navigateByUrl();
		if(hasAddPage()){
			clickAddPage().createASimplePage();
			String popupContent = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(popupContent, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.pages.create.success"),
						"[Failed] Create page success message should be shown.");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnAddPage),
					"[Failed] Restricted page should be shown when click Add Page button.");
			Response response = new APIPages(staffLoginInfo).createPageResponse();
			assertCustomize.assertTrue(response.getStatusCode()==403,"[Failed] Call API Create page should response 403 status.");
		}
		logger.info("Verified Add page permission.");
	}
	public void checkEditPage(int id){
		if(hasEditPage()){
			addPage.navigateEditPageByUrl(id).clickOnSave();
			String message = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.pages.update.success"),
						"[Failed] Page updated success message should be shown but '%s' is shown.".formatted(message));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(addPage.urlEdirPage.formatted(id)),
				"[Failed] Restricted page should be shown when navigate to edit page url");
		logger.info("Verified Edit page permission.");
	}
	public int callAPIGetPageId(){
		List<Integer> pageIds = new APIPages(ownerLoginInfo).getPageIdList();
		int id ;
		if(pageIds.isEmpty()){id = new APIPages(ownerLoginInfo).createPage();}
		else id = pageIds.get(0);
		return id;
	}
	public void checkDeletePage(){
		navigateByUrl();
		if(hasViewPageList()){
			if(hasDeletePage()){
				deleteAPage();
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(message,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.pages.delete.success"),
							"[Failed] Delete page success message should be shown, but '%s' is shown".formatted(message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnDeletePage,0),
					"[Failed] Restricted popup should be shown when click delete page.");
			logger.info("Verified Delete page permission on Page management.");
		}else logger.info("Don't have View page list permission, so no need check Delete page permission on Page Management.");
		//Check delete on page detail.
		int id = callAPIGetPageId();
		addPage.navigateEditPageByUrl(id);
		if(hasEditPage()) {
			if (hasDeletePage()) {
				addPage.clickOnDelete();
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.pages.delete.success"),
							"[Failed] Delete page success message should be shown, but '%s' is shown".formatted(message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(addPage.loc_btnDelete),
					"[Failed] Restricted popup should be shown when click delete page on Detail page");
			logger.info("Verified Delete page permission on detail page.");
		}else logger.info("Don't have edit page permission, so no need check delete page permission on detal page.");
	}
	public void checkTranslatePage(int id){
		addPage.navigateEditPageByUrl(id);
		if(hasEditPage()) {
			if (hasTranslatePage()) {
				String urlLink = new DataGenerator().generateString(10);
				addPage.clickOnEditTranslation().inputUrlLinkOnTranslationModal(urlLink).clickSaveOnEditTranslationModal();
				String message = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.update.success"),
							"[Failed] Update success message should be shown when edit translate, but '%s' is shown.".formatted(message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(addPage.loc_btnEditTranslation),
						"[Failed] Restricted popup should be shown when click on Edit translation page.");
			logger.info("Verified translate page permission");
		}else logger.info("Don't have Edit page permission, so no need check Edit translation permission.");
	}
	public PageManagement checkPagePermission(AllPermissions allPermissions, int pageId){
		this.allPermissions = allPermissions;
		checkViewPageList();
		checkAddPage();
		checkEditPage(pageId);
		checkTranslatePage(pageId);
		checkDeletePage();
		AssertCustomize.verifyTest();
		return this;
	}
}
