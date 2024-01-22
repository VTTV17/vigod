package web.Dashboard.onlineshop.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class PageManagement {

	final static Logger logger = LogManager.getLogger(PageManagement.class);

	WebDriver driver;
	UICommonAction commonAction;


	public PageManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}
	
    By loc_btnAddPage = By.cssSelector(".gss-content-header--undefined .gs-button__green");
    
    public PageManagement clickAddPage() {
    	commonAction.click(loc_btnAddPage);
    	logger.info("Clicked on 'Add Page' button.");
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
    /*-------------------------------------*/    	
}
