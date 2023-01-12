package pages.dashboard.service;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ServiceManagementPage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    public ServiceManagementPage(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(css = ".service-list-page .gss-content-header button")
    WebElement CREATE_SERVICE_BTN;

    public ServiceManagementPage goToCreateServicePage(){
        commons.clickElement(CREATE_SERVICE_BTN);
        new HomePage(driver).waitTillSpinnerDisappear();
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
    
}
