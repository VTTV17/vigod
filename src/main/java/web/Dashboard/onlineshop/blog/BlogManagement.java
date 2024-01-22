package web.Dashboard.onlineshop.blog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.onlineshop.blog.categorymanagement.CategoryManagement;
import web.Dashboard.onlineshop.blog.categorymanagement.CreateCategory;
import utilities.commons.UICommonAction;

public class BlogManagement {

	final static Logger logger = LogManager.getLogger(BlogManagement.class);

	WebDriver driver;
	UICommonAction commonAction;

	public BlogManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}
	
    By loc_btnCreateArticle = By.cssSelector(".gss-content-header--undefined .gs-button__green");
    By loc_btnCategoryManagement = By.cssSelector(".gss-content-header--undefined .gs-button__green--outline");	
    
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
    /*-------------------------------------*/       
    
}
