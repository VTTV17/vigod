package pages.dashboard.onlineshop.pages;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class PageManagement {

	final static Logger logger = LogManager.getLogger(PageManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public PageManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	
    @FindBy(css = ".gss-content-header--undefined .gs-button__green")
    WebElement ADD_PAGE_BTN;	
    
    public PageManagement clickAddPage() {
    	commonAction.clickElement(ADD_PAGE_BTN);
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
