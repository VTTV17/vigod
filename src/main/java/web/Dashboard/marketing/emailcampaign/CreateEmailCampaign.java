package web.Dashboard.marketing.emailcampaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;

public class CreateEmailCampaign {

	final static Logger logger = LogManager.getLogger(CreateEmailCampaign.class);

	WebDriver driver;
	UICommonAction commonAction;

	public CreateEmailCampaign(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCampaignName = By.id("campaignName");
	By loc_txtEmailAddress = By.id("receiver");
	By loc_txtSubject = By.id("title");
	By loc_btnChooseEmailTemplate = By.cssSelector(".marketing_email_campaign_editor_setting button");
	By loc_btnSave = By.xpath("//button[string()='Save' or string()='Lưu']");
	By loc_lstEmailTemplate = By.cssSelector(".marketing_email_campaign_editor_templates_modal__template-item");
	By loc_ddlSendTo = By.id("campaignEmailTo");
	By loc_lnkSelectSegment = By.cssSelector(".marketing_email_campaign_editor_setting__email-to-segment span span");
	By loc_dlgAddSegment_lstSegmentName = By.cssSelector(".segment-name span");

	public CreateEmailCampaign inputCampaignName(String campaignName) {
		commonAction.sendKeys(loc_txtCampaignName, campaignName);
		logger.info("Input '" + campaignName + "' into Campaign Name field.");
		return this;
	}
	public  CreateEmailCampaign inputEmailAddress(String email){
		commonAction.sendKeys(loc_txtEmailAddress,email);
		logger.info("Input email: "+email);
		return this;
	}
	public CreateEmailCampaign inputSubject(String emailTitle){
		commonAction.sendKeys(loc_txtSubject,emailTitle);
		logger.info("Input email subject: "+emailTitle);
		return this;
	}
	public CreateEmailCampaign clickOnChooseEmailTemplate(){
		commonAction.click(loc_btnChooseEmailTemplate);
		logger.info("Click on Choose template button.");
		return this;
	}
	public CreateEmailCampaign selectATemplate(int index){
		commonAction.getElements(loc_lstEmailTemplate,3).get(index).click();
		logger.info("Select email template: "+index);
		return this;
	}
	public CreateEmailCampaign clickOnSaveBtn(){
		commonAction.click(loc_btnSave);
		logger.info("Click on Save button.");
		return this;
	}
	/*
	0: email address
	1: customer segment
	 */
	public CreateEmailCampaign selectSendTo(int index){
		commonAction.selectByIndex(loc_ddlSendTo,index);
		String selected = commonAction.getDropDownSelectedValue(loc_ddlSendTo);
		logger.info("Selected value: "+selected);
		return this;
	}
	public CreateEmailCampaign createAnEmailCampaign(){
		String random = new DataGenerator().generateString(10);
		inputCampaignName("Email campaign "+random);
		inputEmailAddress("qcgosell01@gmail.com");
		inputSubject("GoSel Campaign");
		clickOnChooseEmailTemplate();
		selectATemplate(0);
		commonAction.sleepInMiliSecond(1000);
		clickOnSaveBtn();
		return this;
	}
	public CreateEmailCampaign clickOnSelectSegment(){
		commonAction.click(loc_lnkSelectSegment);
		logger.info("Click on Select segmennt link");
		return this;
	}
}
