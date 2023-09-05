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

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
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
	@FindBy(css = ".collection-name b")
	List<WebElement> COLLECTION_NAMES;
	@FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]")
	List<WebElement> TYPES;
	@FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]")
	List<WebElement> MODES;
	@FindBy(xpath = "//div[contains(@class,'products')]")
	List<WebElement> ITEMS;

	public CreateServiceCollection clickCreateServiceCollection() {
		commonAction.clickElement(CREATE_SERVICE_COLLECTION_BTN);
		logger.info("Clicked on 'Create Service Collection' button.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return new CreateServiceCollection(driver);
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
	public ServiceCollectionManagement verifyCollectionName(String expected, int index) {
		String actual = commonAction.getText(COLLECTION_NAMES.get(index));
		Assert.assertEquals(actual, expected);
		logger.info("Verify collection name after created");
		return this;
	}

	public ServiceCollectionManagement verifyType(String expected, int index) {
		String actual = commonAction.getText(TYPES.get(index));
		Assert.assertEquals(actual, expected);
		logger.info("Verify type after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyMode(String expected, int index) {
		String actual = commonAction.getText(MODES.get(index));
		Assert.assertEquals(actual, expected);
		logger.info("Verify mode after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyItem(String expected, int index) {
		String actual = commonAction.getText(ITEMS.get(index));
		Assert.assertEquals(actual, expected);
		logger.info("Verify items after collection created");
		return this;
	}
	public ServiceCollectionManagement refreshPage(){
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	public ServiceCollectionManagement verifyCollectionInfoAfterCreated(String collectionName, String type, String mode, String items) {
		verifyCollectionName(collectionName, 0);
		verifyType(type, 0);
		verifyMode(mode, 0);
		verifyItem(items, 0);
		logger.info("Verify collection info after created.");
		return this;
	}
}
