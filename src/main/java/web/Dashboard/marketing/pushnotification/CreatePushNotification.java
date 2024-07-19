package web.Dashboard.marketing.pushnotification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.links.Links;

public class CreatePushNotification {

	final static Logger logger = LogManager.getLogger(CreatePushNotification.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreatePushNotification(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCampaignName = By.id("campaignName");
	By loc_lnkAddSegment = By.cssSelector(".btn-add-segment");
	By loc_ddlLinkTo = By.cssSelector(".choose-link__type");
	By loc_ddvLinkTo = By.cssSelector(".choose-link__type [role='menuitem']");
	By loc_ddlLinkValue = By.cssSelector(".choose-link__value .btn-secondary");
	By loc_ddvLinkValue = By.cssSelector(".choose-link__value .dropdown-item");
	By loc_lnkChooseAProductService = By.cssSelector(".product-item__type span");
	By log_dlgAddSegment_lstSegmentName = By.cssSelector(".segment-name");
	By loc_dlgProductSelection_lstProductName = By.cssSelector(".product-name");
	By loc_dlgAddSegment_chkSelectAll = By.cssSelector("[name='check_all']");
	By loc_dlgAddSegment_btnOK = By.cssSelector(".modal-body .gs-button__green");
	By loc_dlgAddSegment_txtSearchSegmentName = By.cssSelector(".clear-up-down-btn");
	By loc_txtTitle = By.id("title");
	By loc_txtMessage = By.id("message");
	By loc_btnCreate = By.cssSelector(".btn-send-now");
	By loc_lst_rdoSendType = By.xpath("//label[contains(@class,'uik-checkbox__radio')]");
	By loc_ddlEventType = By.xpath("(//input[@name='send-mode'])[3]//following-sibling::div//button");
	By loc_ddvEventType = By.cssSelector(".uik-select__option");

	public CreatePushNotification inputCampaignName(String campaignName) {
		commonAction.sendKeys(loc_txtCampaignName, campaignName);
		logger.info("Input '" + campaignName + "' into Campaign Name field.");
		return this;
	}
	public CreatePushNotification clickOnAddSegment(){
		commonAction.click(loc_lnkAddSegment);
		logger.info("Click on Add segment.");
		return this;
	}
	public CreatePushNotification navigateUrl(){
		String url = Links.DOMAIN + "/marketing/notification/push/create";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		return this;
	}
	public CreatePushNotification selectLinkToType(int index){
		commonAction.click(loc_ddlLinkTo);
		commonAction.click(loc_ddvLinkTo,index);
		switch (index){
			case 0 -> logger.info("Selected None");
			case 1 -> logger.info("Selected Page");
			case 2 -> logger.info("Selected Collection");
			case 3 -> logger.info("Selected Product");
			case 4 -> logger.info("Selected Service");
			case 5 -> logger.info("Selected External Link");
		}
		commonAction.sleepInMiliSecond(500);
		return this;
	}

	public CreatePushNotification addSegment(String...segmentName){
		clickOnAddSegment();
		if(segmentName.length == 0){
			commonAction.click(loc_dlgAddSegment_chkSelectAll);
		}else {
			for (String segment : segmentName){
				commonAction.inputText(loc_dlgAddSegment_txtSearchSegmentName,segment);
				commonAction.click(loc_dlgAddSegment_chkSelectAll);
			}
		}
		commonAction.click(loc_dlgAddSegment_btnOK);
		return this;
	}
	public CreatePushNotification inputTitle(String title){
		commonAction.inputText(loc_txtTitle,title);
		logger.info("Input title: "+title);
		return this;
	}
	public CreatePushNotification inputMessage(String message){
		commonAction.inputText(loc_txtMessage,message);
		logger.info("Input message: "+message);
		return this;
	}
	public CreatePushNotification clickOnCreateBtn(){
		commonAction.click(loc_btnCreate);
		logger.info("Click on Create button");
		return this;
	}
	public CreatePushNotification selectSendNotificationType(int index){
		commonAction.clickJS(loc_lst_rdoSendType,index);
		switch (index){
			case 0 -> logger.info("Select Send now.");
			case 1 -> logger.info("Select Specific time.");
			case 2 -> logger.info("Select Event");
		}
		return this;
	}
	public CreatePushNotification selectEventType(int index){
		commonAction.click(loc_ddlEventType);
		commonAction.click(loc_ddvEventType,index);
		switch (index){
			case 0 -> logger.info("Select Birthday.");
			case 1 -> logger.info("Select Account created.");
			case 2 -> logger.info("Select Order Completed");
			case 3 -> logger.info("Select Order Abandoned checkout");
			case 4 -> logger.info("Select Order Activated partner");
			case 5 -> logger.info("Select Order Rejected partner");
			case 6 -> logger.info("Select Order Deactive partner");
		}
		return this;
	}
	public CreatePushNotification createSimpleCampaign(){
		String randomText = new DataGenerator().generateString(10);
		inputCampaignName("campaign name "+randomText);
		selectSendNotificationType(2);
		selectEventType(1);
		inputTitle("tile "+randomText);
		inputMessage("message "+randomText);
		clickOnCreateBtn();
		return this;
	}

}
