package pages.dashboard.service.servicecollections;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import pages.dashboard.service.CreateServicePage;
import pages.dashboard.service.ServiceManagementPage;
import utilities.UICommonAction;

public class ServiceCollectionManagement {

	final static Logger logger = LogManager.getLogger(ServiceCollectionManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public ServiceCollectionManagement(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".collection-list-page button")
	WebElement CREATE_SERVICE_COLLECTION_BTN;
	@FindBy(css = ".collection-name")
	List<WebElement> LIST_SERVICE_COLLECTION_NAME;
	@FindBy(css = ".d-desktop-block .first-button")
	List<WebElement> EDIT_ICON_LIST;

	public ServiceCollectionManagement clickCreateServiceCollection() {
		commonAction.clickElement(CREATE_SERVICE_COLLECTION_BTN);
		logger.info("Clicked on 'Create Service Collection' button.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
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
	public CreateServiceCollection goToEditServiceCollection(String collectionName) throws Exception {
		boolean clicked = false;
		for (int i=0;i< LIST_SERVICE_COLLECTION_NAME.size();i++) {
			if (commonAction.getText(LIST_SERVICE_COLLECTION_NAME.get(i)).equalsIgnoreCase(collectionName)){
				commonAction.clickElement(EDIT_ICON_LIST.get(i));
				clicked = true;
				break;
			}
		}
		if (!clicked){
			throw new Exception("Service %s not found".formatted(collectionName));
		}
		new HomePage(driver).waitTillSpinnerDisappear();
		commonAction.sleepInMiliSecond(1000);
		logger.info("Go to collection: "+collectionName);
		return new CreateServiceCollection(driver);
	}

}
