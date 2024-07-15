package web.Dashboard.onlineshop.blog;

import api.Seller.sale_channel.onlineshop.APIBlog;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.onlineshop.blog.categorymanagement.CategoryManagement;
import web.Dashboard.onlineshop.blog.categorymanagement.CreateCategory;
import utilities.commons.UICommonAction;

import java.util.List;

public class BlogManagement {

	final static Logger logger = LogManager.getLogger(BlogManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreateArticle createArticle;
	CategoryManagement categoryManagement;
	LoginInformation loginInformation;
	public BlogManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createArticle = new CreateArticle(driver);
		categoryManagement = new CategoryManagement(driver);
	}
	public BlogManagement getLoginInfo(LoginInformation staffLoginInfo){
		loginInformation = staffLoginInfo;
		return this;
	}
	
    By loc_btnCreateArticle = By.cssSelector(".gss-content-header--undefined .gs-button__green");
    By loc_btnCategoryManagement = By.cssSelector(".gss-content-header--undefined .gs-button__green--outline");
	By loc_lstArticle = By.xpath("//tbody//tr");
    
    public BlogManagement clickCreateArticle() {
    	commonAction.click(loc_btnCreateArticle);
    	logger.info("Clicked on 'Create Article' button.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	return this;
    }    	
    
    public BlogManagement clickCategoryManagement() {
    	commonAction.click(loc_btnCategoryManagement);
    	logger.info("Clicked on 'Category Management' button.");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	return this;
    }    	
	public BlogManagement navigateByUrl(){
		String url = Links.DOMAIN + Links.BLOG_MANAGEMENT_PATH;
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		return this;
	}
    /*Verify permission for certain feature*/
    public void verifyPermissionToAddArticle(String permission) {
		if (permission.contentEquals("A")) {
			clickCreateArticle();
			new CreateArticle(driver).inputTitleName("Test Permission");
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    public void verifyPermissionToCreateCategory(String permission) {
    	if (permission.contentEquals("A")) {
    		clickCategoryManagement();
    		new CategoryManagement(driver).clickCreateCategory();
    		new CreateCategory(driver).inputCategoryName("Test Permission");
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else if (permission.contentEquals("D")) {
    		// Not reproducible
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    }
    /*---------------Staff permission----------------------*/
    public boolean hasViewArticleList(){
		return allPermissions.getOnlineStore().getBlog().isViewArticleList();
	}
	public boolean hasViewArticleDetail(){
		return allPermissions.getOnlineStore().getBlog().isViewArticleDetail();
	}
	public boolean hasAddArticle(){
		return allPermissions.getOnlineStore().getBlog().isAddArticle();
	}
	public boolean hasEditArticle(){
		return allPermissions.getOnlineStore().getBlog().isEditArticle();
	}
	public boolean hasViewBlogCategoryList(){
		return allPermissions.getOnlineStore().getBlog().isViewBlogCategoryList();
	}
	public boolean hasAddBogCategory(){
		return allPermissions.getOnlineStore().getBlog().isAddBlogCategory();
	}
	public boolean hasEditBlogCategory(){
		return allPermissions.getOnlineStore().getBlog().isEditBlogCategory();
	}
	public boolean hasDeleteBlogCategory(){
		return allPermissions.getOnlineStore().getBlog().isDeleteBlogCategory();
	}
	public boolean hasTranslateArticle(){
		return allPermissions.getOnlineStore().getBlog().isTranslateArticle();
	}
	public boolean hasTranslateCategory(){
		return allPermissions.getOnlineStore().getBlog().isTranslateCategory();
	}
	public void checkViewArticleList(){
		List<WebElement> articlList = commonAction.getElements(loc_lstArticle,2);
		if(hasViewArticleList()){
			assertCustomize.assertTrue(articlList.size()>0,
					"[Failed] Article list should be shown.");
		}else assertCustomize.assertTrue(articlList.isEmpty(),"[Failed] Article list should be empty");
		logger.info("Verified View Article list permission.");
	}
	public void checkViewArticleDetailAndEdit(int id){
		String url = Links.DOMAIN + "/channel/storefront/blog/article/edit/"+id;
		if (hasViewArticleDetail()&&hasEditArticle()){
			assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(url,createArticle.loc_txtArticleName),
					"[Failed] Article title should be shown when navigate to edit url: "+url);
			checkViewBlogOnCreateEditArticlePage();
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
				"[Failed] Restricted page should be shown when navigate to edit blog url: "+url);
		logger.info("Verified View Article and Edit Article permission.");
	}
	public void checkViewBlogOnCreateEditArticlePage(){
		createArticle.clickOnCategory();
		List<WebElement> categoryList = commonAction.getElements(createArticle.loc_ddlCategory_lstOption,1);
		if(hasViewBlogCategoryList()){
			assertCustomize.assertTrue(categoryList.size()>0,
					"[Failed] Category list should be shown.");
		}else assertCustomize.assertTrue(categoryList.isEmpty(),
				"[Failed] Category list should be empty.");
	}
	public void checkAddArticle(){
		navigateByUrl();
		if(hasAddArticle()){
			clickCreateArticle();
			checkViewBlogOnCreateEditArticlePage();
			createArticle.createSimpleArticle();
			String toastMessage = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.create.success"),
						"[Failed] Create Article success message should be shown, but '%s' is shown".formatted(toastMessage));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else{ assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateArticle),
				"Restricted page should be shown when click on Create Article button.");
			Response response = new APIBlog(loginInformation).callCreateArticle();
			assertCustomize.assertTrue(response.getStatusCode()==403,
					"[Failed] Call API create article should be response 403, but status '%s' is response".formatted(response.getStatusCode()));
		}
		logger.info("Verified Add Article permission.");
	}
	public void checkViewBlogCategoryList(){
		navigateByUrl();
		clickCategoryManagement();
		List<WebElement> categoryList = commonAction.getElements(categoryManagement.loc_lst_icnEdit,2);
		if(hasViewBlogCategoryList()){
			assertCustomize.assertTrue(categoryList.size()>0,"[Failed] Category list should be shown.");
		}else assertCustomize.assertTrue(categoryList.isEmpty(),"[Failed] Category list should be empty");
		logger.info("Verified View blog category list permission.");
	}
	public void checkAddBlogCategory(){
		navigateByUrl();
		clickCategoryManagement();
		if(hasAddBogCategory()){
			categoryManagement.clickCreateCategory();
			new CreateCategory(driver).createSimpleCategory();
			String toast = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toast,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.create.success"),
						"[Failed] Create Catogory success message should be shown, but '%s' is shown".formatted(toast));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(categoryManagement.loc_btnCreateCategory),
					"[Failed] Restricted page should be shown when click on Create Category button.");
			Response response = new APIBlog(loginInformation).callCreateBlogCategory();
			assertCustomize.assertTrue(response.getStatusCode()==403,
					"[Failed] Call API create category should be response 403, but status '%s' is response".formatted(response.getStatusCode()));

		}
		logger.info("Verified Add blog category permission.");
	}
	public void checkEditBlogCategory(int id){
		String url = Links.DOMAIN + "/channel/storefront/blog/article/category/edit/"+id;
		if(hasEditBlogCategory()){
			assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(url,new CreateCategory(driver).loc_txtCategoryName),
					"[Failed] Category name should be shown when navigate to edit category url: "+url);
			new CreateCategory(driver).clickOnSaveBtn();
			String toast = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toast,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.update.success"),
						"[Failed] Edit category success message should be shown, but '%s' is shown.".formatted(toast));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(url),
				"[Failed] Restricted page should be shown when navigate to edit category url: "+url);
		logger.info("Verified Edit blog category.");
	}
	public void checkDeleteCategory(){
		navigateByUrl();
		clickCategoryManagement();
		if(hasViewBlogCategoryList()){
			if(hasDeleteBlogCategory()){
				categoryManagement.deleteACatogory();
				String toast = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toast,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.category.delete.success"),
							"[Failed] Delete category success should be shown, but '%s' is shown".formatted(toast));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else{
				commonAction.sleepInMiliSecond(500);
				commonAction.click(categoryManagement.loc_lst_icnDelete,0);
				assertCustomize.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(),
					"[Failed] Restricted pop up should be shown when click on delete icon");
			}
			logger.info("Verified View blog catogory permission.");
		}else logger.info("Don't have View blog category list permission, so no need check Delete category permission");
	}
	public void checkTranslateArticle(int id){
		if(hasViewArticleDetail()&& hasEditArticle()){
			createArticle.navigateByUrl(id);
			if(hasTranslateArticle()){
				createArticle.clickOnEditTranslation();
				createArticle.clickSaveOnEditTranslationModal();
				String toast = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toast,PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.update.success"),
							"[Failed] Edit translation success message should be shown, but '%s' is shown.".formatted(toast));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createArticle.loc_btnEditTranslation),
						"[Failed] Restricted popup should be shown when click on Edit translation button.");
			}
			logger.info("Verified Translate Article permission.");
		}else logger.info("Don't have View article detail and edit article permission, so no need check Translate article permission.");
	}
	public void checkTranslateCategory(int id){
		if(hasEditBlogCategory()){
			new CreateCategory(driver).navigateByUrl(id);
			if(hasTranslateCategory()) {
				new CreateCategory(driver).clickOnEditTranslation()
						.clickSaveOnEditTranslationModal();
				String toast = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toast, PropertiesUtil.getPropertiesValueByDBLang("onlineshop.blog.update.success"),
							"[Failed] Edit translation for category success message should be shown, bu '%s' is shown.".formatted(toast));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(new CreateCategory(driver).loc_btnEditTranslation),
					"[Failed] Restricted popup should be shown when click on Edit Translation button.");
			logger.info("Verified Translate category permission.");
		}else logger.info("Don't have Edit blog category permission, so no need check Translate category permission.");
	}
	public BlogManagement checkBlogPermission(AllPermissions allPermissions, int articleId, int categoryId){
		this.allPermissions = allPermissions;
		checkViewArticleList();
		checkViewArticleDetailAndEdit(articleId);
		checkAddArticle();
		checkViewBlogCategoryList();
		checkAddBlogCategory();
		checkEditBlogCategory(categoryId);
		checkTranslateArticle(articleId);
		checkTranslateCategory(categoryId);
		checkDeleteCategory();
		AssertCustomize.verifyTest();
		return this;
	}
}
