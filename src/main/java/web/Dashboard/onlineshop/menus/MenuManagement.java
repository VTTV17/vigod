package web.Dashboard.onlineshop.menus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.enums.MenuItemType;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

import java.util.List;

public class MenuManagement {

	final static Logger logger = LogManager.getLogger(MenuManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	AddMenu addMenu;
	public MenuManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		addMenu = new AddMenu(driver);
	}
	
    By loc_btnAddMenu = By.cssSelector(".gss-content-header--undefined .gs-button__green");	
    By loc_lstMenuTitle = By.cssSelector(".gs-table-body-items div:nth-child(1)");
	By loc_lst_icnEdit = By.cssSelector(".icon-edit");

    public MenuManagement clickAddMenu() {
    	commonAction.click(loc_btnAddMenu);
    	logger.info("Clicked on 'Add Menu' button.");
    	return this;
    }    	
	public MenuManagement navigateByUrl(){
		String url = Links.DOMAIN + Links.MENUS_PATH;
		commonAction.navigateToURL(url);
		return this;
	}
	public AddMenu clickEditMenu(int index){
		commonAction.click(loc_lst_icnEdit,index);
		logger.info("Click on edit menu icon, index = "+index);
		return new AddMenu(driver);
	}
    public void verifyPermissionToAddMenu(String permission) {
    	if (permission.contentEquals("A")) {
    		clickAddMenu();
    		new AddMenu(driver).inputMenuTitle("Test Permission");
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
	/*-------------------Staff permission-----------------*/
	public boolean hasViewListMenu(){
		return allPermissions.getOnlineStore().getMenu().isViewListMenu();
	}
	public boolean hasCreateMenu(){
		return allPermissions.getOnlineStore().getMenu().isCreateMenu();
	}
	public boolean hasEditMenu(){
		return allPermissions.getOnlineStore().getMenu().isEditMenu();
	}
	public boolean hasTranslateMenu(){
		return allPermissions.getOnlineStore().getMenu().isTranslateMenu();
	}
	public boolean hasViewProductCollectionList(){
		return allPermissions.getProduct().getCollection().isViewCollectionList();
	}
	public boolean hasViewServiceCollectionList(){
		return allPermissions.getService().getServiceCollection().isViewCollectionList();
	}
	public boolean hasViewPageList(){
		return allPermissions.getOnlineStore().getPage().isViewPageList();
	}
	public boolean hasViewBlogList(){
		return allPermissions.getOnlineStore().getBlog().isViewBlogCategoryList();
	}
	public boolean hasViewArticleList(){
		return allPermissions.getOnlineStore().getBlog().isViewArticleList();
	}
	public void checkViewMenuList(){
		List<WebElement> menuTitleList = commonAction.getElements(loc_lstMenuTitle,1);
		if(hasViewListMenu()){
			assertCustomize.assertTrue(menuTitleList.size()>0,"[Failed] Menu list should be shown.");
		}else assertCustomize.assertTrue(menuTitleList.isEmpty(),"[Failed] Menu list should be empty.");
		logger.info("Verified View menu list permission.");
	}
	public void checkViewURLLink(MenuItemType menuItemType){
		boolean permission = false;
		switch (menuItemType){
			case COLLECTION_PRODUCT -> permission = hasViewProductCollectionList();
			case COLLECTION_SERVICE -> permission = hasViewServiceCollectionList();
			case PAGE -> permission = hasViewPageList();
			case BLOG -> permission = hasViewBlogList();
			case ARTICLE -> permission = hasViewArticleList();
		}
		addMenu.selectUrlLinkType(menuItemType);
		commonAction.click(addMenu.loc_ddlUrlLinkValue);
		List<WebElement> linkValueList = commonAction.getElements(addMenu.loc_ddvUrlLinkValue,2);
		if(permission){
			assertCustomize.assertTrue(linkValueList.size()>0,"[Failed] %s list not show".formatted(menuItemType));
			logger.info("Verified view list %s permission.".formatted(menuItemType));
		}else assertCustomize.assertTrue(linkValueList.isEmpty(),"[Failed] %s list should be not shown".formatted(menuItemType));
		commonAction.click(addMenu.loc_ddlUrlLinkValue);
	}
	public void checkCreateMenu(){
		navigateByUrl();
		if(hasCreateMenu()){
			clickAddMenu();
			addMenu.clickOnAddMenuItem();
			checkViewURLLink(MenuItemType.COLLECTION_PRODUCT);
			checkViewURLLink(MenuItemType.COLLECTION_SERVICE);
			checkViewURLLink(MenuItemType.PAGE);
			checkViewURLLink(MenuItemType.BLOG);
			checkViewURLLink(MenuItemType.ARTICLE);
			addMenu.clickCancelAddMenuItem();
			addMenu.createSimpleAMenu();
			String message = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.menus.update.success"),
						"[Failed] Success message should be shown, but '%s' is shown".formatted(message));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnAddMenu),
					"[Failed] Restricted popup should be shown");
		}
		logger.info("Verified create menu permission.");
	}
	public void checkEditMenu(){
		if(hasViewListMenu()) {
			navigateByUrl();
			if (hasEditMenu()) {
				clickEditMenu(0);
				addMenu.clickOnSaveBtn();
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.menus.update.success"),
							"[Failed] Success message should be shown, but '%s' is shown".formatted(message));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit, 0),
					"[Failed] Restricted page should be shown.");
			logger.info("Verified edit menu permission.");
		}else logger.info("Don't have View menu list permission, so no need check edit menu permission.");
	}
	public void checkTranslateMenu(){
		if(hasViewListMenu()) {
			navigateByUrl();
			if (hasEditMenu()) {
				clickEditMenu(0);
				if (hasTranslateMenu()) {
					addMenu.clickOnEditTranslation().clickSaveOnEditTranslationModal();
					String message = new HomePage(driver).getToastMessage();
					try {
						assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.update.success"),
								"[Failed] Update success message should be shown when edit translate, but '%s' is shown.".formatted(message));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(addMenu.loc_btnEditTranslation),
							"[Failed] Restricted popup should be shown when click on Edit translation button.");
				logger.info("Verified Translate menu permission.");
			} else logger.info("Don't have edit menu permission, so no need check Translate menu permission.");
		}else logger.info("Don't have view menu list permission, so no need check Translate menu permission.");
	}
	public MenuManagement checkMenuPermission(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		checkViewMenuList();
		checkCreateMenu();
		checkEditMenu();
		checkTranslateMenu();
		AssertCustomize.verifyTest();
		return this;
	}
}
