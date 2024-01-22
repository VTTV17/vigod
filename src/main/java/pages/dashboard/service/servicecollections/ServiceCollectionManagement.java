package pages.dashboard.service.servicecollections;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
	By loc_btnCreateServiceCollection = By.cssSelector(".collection-list-page button");
	By loc_lst_lblServiceCollectionName = By.cssSelector(".collection-name b");
	By loc_lst_icnEdit = By.cssSelector(".d-desktop-block .first-button");
	By loc_lst_lblType = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]");
	By loc_lst_lblMode = By.xpath("//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]");
	By loc_lst_lblItems = By.xpath("//div[contains(@class,'products')]");
	By loc_txtSearch = By.xpath("//span[contains(@class,'gs-search-box')]//input");
	By loc_lst_icnDelete = By.cssSelector(".actions .lastest-button");
	By loc_dlgConfirmation_btnOK = By.cssSelector(".modal-footer .gs-button__green");


//	@FindBy(css = ".collection-list-page button")
//	WebElement CREATE_SERVICE_COLLECTION_BTN;
//	@FindBy(css = ".collection-name")
//	List<WebElement> LIST_SERVICE_COLLECTION_NAME;
//	@FindBy(css = ".d-desktop-block .first-button")
//	List<WebElement> EDIT_ICON_LIST;
//	@FindBy(css = ".collection-name b")
//	List<WebElement> COLLECTION_NAMES;
//	@FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][1]")
//	List<WebElement> TYPES;
//	@FindBy(xpath = "//div[contains(@class,'collection-name')]/following-sibling::div[contains(@class,'collection-type')][2]")
//	List<WebElement> MODES;
//	@FindBy(xpath = "//div[contains(@class,'products')]")
//	List<WebElement> ITEMS;
//	@FindBy(xpath = "//span[contains(@class,'gs-search-box')]//input")
//	WebElement SEARCH_INPUT;
//	@FindBy(css = ".actions .lastest-button")
//	List<WebElement> DELETE_BTN;
//	@FindBy(css = ".modal-footer .gs-button__green")
//	WebElement OK_BTN_ON_MODAL;

	public CreateServiceCollection clickCreateServiceCollection() {
		commonAction.click(loc_btnCreateServiceCollection);
		logger.info("Clicked on 'Create Service Collection' button.");
		new HomePage(driver).waitTillSpinnerDisappear1();
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
	public EditServiceCollection goToEditServiceCollection(String collectionName) throws Exception {
		boolean clicked = false;
		for (int i=0;i< commonAction.getElements(loc_lst_lblServiceCollectionName).size();i++) {
			if (commonAction.getText(loc_lst_lblServiceCollectionName,i).equalsIgnoreCase(collectionName)){
				commonAction.click(loc_lst_icnEdit,i);
				clicked = true;
				break;
			}
		}
		if (!clicked){
			throw new Exception("Service collection %s not found".formatted(collectionName));
		}
		new HomePage(driver).waitTillSpinnerDisappear1();
//		commonAction.sleepInMiliSecond(1000);
		logger.info("Go to collection: "+collectionName);
		return new EditServiceCollection(driver);
	}
	public ServiceCollectionManagement verifyCollectionName(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblServiceCollectionName,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify collection name after created");
		return this;
	}

	public ServiceCollectionManagement verifyType(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblType,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify type after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyMode(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblMode,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify mode after collection created");
		return this;
	}

	public ServiceCollectionManagement verifyItem(String expected, int index) {
		String actual = commonAction.getText(loc_lst_lblItems,index);
		Assert.assertEquals(actual, expected);
		logger.info("Verify items after collection created");
		return this;
	}
	public ServiceCollectionManagement refreshPage(){
		commonAction.sleepInMiliSecond(1000);
		commonAction.refreshPage();
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}
	public ServiceCollectionManagement refreshPageUtilCollectUpdate(String expectItemNumber){
		for (int i=0;i<10;i++){
			refreshPage();
			String itemNumber = commonAction.getText(loc_lst_lblItems,0);
			if(itemNumber.equals(String.valueOf(expectItemNumber))){
				break;
			}
		}
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
	public ServiceCollectionManagement searchCollection(String collectionName){
		commonAction.inputText(loc_txtSearch,collectionName);
		logger.info("Input to search collection: "+collectionName);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	public String getTheFirstCollectionName() {
		String name = commonAction.getText(loc_lst_lblServiceCollectionName,0);
		logger.info("Get the first collection name in list: " + name);
		return name;
	}
	public ServiceCollectionManagement navigate(){
		HomePage home = new HomePage(driver);
		home.navigateToPage("Services", "Service Collections");
		return this;
	}
	public void deleteTheFirstCollection() {
		commonAction.click(loc_lst_icnDelete,0);
		commonAction.click(loc_dlgConfirmation_btnOK);
		commonAction.sleepInMiliSecond(1000);
		commonAction.click(loc_dlgConfirmation_btnOK);
		new HomePage(driver).waitTillSpinnerDisappear();
		logger.info("Delete the first collection");
	}
	public void verifyCollectNameNotDisplayInList(String collectionName) {
		for (int i = 0; i < commonAction.getElements(loc_lst_lblServiceCollectionName).size(); i++) {
			String collectionNameInList = commonAction.getText(loc_lst_lblServiceCollectionName,i);
			if (collectionNameInList.equalsIgnoreCase(collectionName)) {
				Assert.assertTrue(false, collectionName + ": still display in position " + i);
			}
		}
		logger.info("Verify collection: %s - not show in list after deleted".formatted(collectionName));
		Assert.assertTrue(true, collectionName + ": not show in collection list");
	}
	public void checkSalePitchWhenNoPermision(){
		HomePage homePage = new HomePage(driver);
		int countFail = homePage.verifySalePitchPopupDisplay();
		Assert.assertEquals(countFail,0,"Verify sale-pitch has %s error".formatted(countFail));
	}
}
