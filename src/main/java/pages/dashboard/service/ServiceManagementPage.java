package pages.dashboard.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.Constant;
import utilities.UICommonAction;

public class ServiceManagementPage extends ServiceManagementElement {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
	final static Logger logger = LogManager.getLogger(ServiceManagementPage.class);
	public ServiceManagementPage(WebDriver driver){
		super(driver);
		this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }

    public ServiceManagementPage goToCreateServicePage(){
        commons.clickElement(CREATE_SERVICE_BTN);
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
	public CreateServicePage clickOnEditNewestService() throws Exception {
		HomePage homePage = new HomePage(driver);
		homePage.waitTillSpinnerDisappear();
		commons.clickElement(LIST_EDIT_BTN.get(0));
		logger.info("Click on edit newest service.");
		return new CreateServicePage(driver);
	}
	public CreateServicePage goToEditService(String serviceName) throws Exception {
		if (serviceName.equalsIgnoreCase("")){
			commons.clickElement(LIST_SERVICE_NAME.get(0));
			logger.info("Go to edit newest service.");
			return new CreateServicePage(driver);
		}
		boolean clicked = false;
		for (WebElement el: LIST_SERVICE_NAME) {
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
		new HomePage(driver).waitTillSpinnerDisappear();
		commons.sleepInMiliSecond(2000);
		return new CreateServicePage(driver);
	}
	public ServiceManagementPage verifyServiceNotDisplayInList(String serviceName){
		boolean isShow = false;
		for (WebElement el: LIST_SERVICE_NAME) {
			if (commons.getText(el).equalsIgnoreCase(serviceName)){
				isShow = true;
				break;
			}
		}
		Assert.assertFalse(isShow,"Verify service not show after deteled");
		logger.info("Verify service not show after deteled");
		return this;
	}
}
