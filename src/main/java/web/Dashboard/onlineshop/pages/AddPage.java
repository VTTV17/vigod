package web.Dashboard.onlineshop.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class AddPage {

	final static Logger logger = LogManager.getLogger(AddPage.class);

	WebDriver driver;
	UICommonAction commonAction;
	String urlEdirPage = Links.DOMAIN + Links.EDIT_PAGE_PATH.formatted("%s");
	public AddPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_btnPageTitle = By.id("title");
	By loc_btnSave = By.xpath("//button[string()='Lưu' or string()='Save']");
	By loc_btnEditTranslation =  By.xpath("//button[string()='Sửa bản dịch' or string()='Edit Translation']");
	By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
	By loc_btnDelete = By.xpath("//button[string()='Xóa' or string()='Delete']");
	By loc_dlgEditTranslation_txtUrlLink = By.cssSelector(".product-translate #seoUrl");
	public AddPage inputPageTitle(String pageTitle) {
		commonAction.sendKeys(loc_btnPageTitle, pageTitle);
		logger.info("Input '" + pageTitle + "' into Page Title field.");
		return this;
	}
	public AddPage navigateEditPageByUrl(int id){
		String url = urlEdirPage.formatted(id);
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		new HomePage(driver).waitTillSpinnerDisappear1();
		commonAction.sleepInMiliSecond(500);
		return this;
	}
	public void clickOnSave(){
		commonAction.click(loc_btnSave);
		logger.info("Click on Save button.");
	}
	public void createASimplePage(){
		inputPageTitle("Page "+new DataGenerator().generateString(10));
		clickOnSave();
	}
	public AddPage clickOnEditTranslation(){
		commonAction.click(loc_btnEditTranslation);
		logger.info("Click on Edit Translation button");
		return this;
	}
	public AddPage clickSaveOnEditTranslationModal(){
		commonAction.click(loc_dlgEditTranslation_btnSave);
		logger.info("Click on Save button on Edit Translation modal.");
		return this;
	}
	public void clickOnDelete(){
		commonAction.click(loc_btnDelete);
		new ConfirmationDialog(driver).clickGreenBtn();
	}
	public AddPage inputUrlLinkOnTranslationModal(String urlLink){
		commonAction.inputText(loc_dlgEditTranslation_txtUrlLink,urlLink);
		logger.info("Input url link on edit translation popup"+urlLink);
		commonAction.sleepInMiliSecond(1000);
		return this;
	}
}
