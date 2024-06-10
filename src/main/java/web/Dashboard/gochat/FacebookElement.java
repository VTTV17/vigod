package web.Dashboard.gochat;

import org.openqa.selenium.By;

public class FacebookElement {

	By loc_icnLoadMoreLoading = By.cssSelector(".fb-chat-list__load-more .lds-ellipsis--grey");
	
	By loc_btnConnectFacebook = By.cssSelector(".btn-connect");
	By loc_btnAddPage = By.cssSelector(".fb-page-configuration__page-list-section .gss-content-header button.gs-button__green");
	
	By loc_lnkAddPage = By.cssSelector(".fb-page-configuration__page-list-section .align-items-center .gs-fake-link");
	
	String loc_lblPageByName = "//div[contains(@class,'b-page-configuration__page-row')]//div//strong[text()='%s']";
	String loc_lblPageStatusByName = loc_lblPageByName + "/parent::*/span";
	String loc_rdoConnectPageByName = loc_lblPageByName + "/parent::*/following-sibling::label";
	
	By loc_btnConnectPage = By.cssSelector(".fb-page-configuration__page-list-container .widget__footer button.gs-button__blue");
	By loc_btnDisconnectPage = By.cssSelector(".fb-page-configuration__page-list-container .widget__footer button.gs-button__red--outline");
	
	By loc_lblFBUserName = By.cssSelector(".list_chat .name");
	//This locator is temporarily used as there is a bug
	By loc_lblNoResultFound = By.cssSelector(".list_chat .text-center");
	
	By loc_ddlSelectedPage = By.cssSelector(".page-selector.dropdown");
	By loc_ddlAssignStaff = By.cssSelector(".assign-staff-container .btn-assign");
	By loc_ddvAssignStaff = By.cssSelector(".dropdown-menu .staff-row div");
	By loc_btnAssignToMe = By.cssSelector(".dropdown-menu button.gs-button__green");
	By loc_btnUnAssign = By.cssSelector(".dropdown-menu button.gs-button__white");
	
	By loc_btnCreateTag = By.cssSelector(".social-tag-container .add-tags");
	By loc_txtTagNameInDialog = By.cssSelector(".modal-body #tagName");
	By loc_btnAddTagInDialog = By.cssSelector(".modal-body .gs-button__green");
	
	String loc_lblTagByName = "//div[@class='modal-body']//main//div[contains(@class,'tagName')]/span[text()='%s']";
	String loc_icnDeleteTagByName = loc_lblTagByName + "/parent::*/following-sibling::*//*[@class='icon-delete']";
	String loc_icnHideTagByName = loc_lblTagByName + "/parent::*/following-sibling::*//*[starts-with(@class, 'icon-view')]";
	
	String loc_btnTagByName = "//div[@class='tags-line']//div[starts-with(@class,'social-tag-element') and @data-tooltip=\"%s\"]";

	By loc_icnEditProfile = By.cssSelector(".box_info .profile .profile_edit img");
	By loc_icnUnlinkCustomer = By.cssSelector(".fb-chat-customer-profile .cursor--pointer img");
	By loc_txtSearchCustomer = By.cssSelector("#dropdownSuggestionCustomer input");
	String loc_lblCustomerResultByName = "//div[contains(@class,'search-list__result')]//div[contains(@class,'full-name') and .=\"%s\"]";
	By loc_lblLinkedCustomer = By.cssSelector(".fb-chat-customer-profile .selected-customer-name");
	By loc_btnSaveProfile = By.cssSelector(".fb-chat-customer-profile form .gs-button__green");
	
	By loc_btnCreateAutomationCampaign = By.cssSelector(".automation-list-page button.gs-button__green");
	By loc_ddlSelectPage = By.xpath("//*[contains(@class,'page-selector dropdown')]");
	By loc_txtAutomationCampaignName = By.id("campaignName");
	By loc_btnSelectPost = By.cssSelector(".select-post button");
	By loc_chkPost = By.cssSelector(".modal-content td label.select-collection-row");
	By loc_btnSelect = By.cssSelector(".modal-content button.gs-button__green");
	By loc_btnAddResponse = By.cssSelector(".add-button-auto-response button");
	By loc_btnSaveAutomationCampaign = By.cssSelector(".automation-form-editor button.gs-button__green");
	String loc_ddvSelectPageByName = "//button[contains(@class,'dropdown-item') and .='%s']";
	String loc_lblAutomationCampaign = "//*[contains(@class,'automation-order-list')]//section[contains(@class,'mobile-none')]//div[contains(@class,'gs-table-body-item') and position()='1']";
	String loc_lblAutomationCampaignByName = loc_lblAutomationCampaign + "/self::*[.='%s']";
	String loc_icnEditAutomationCampaignByName = loc_lblAutomationCampaignByName + "/following-sibling::div/i[contains(@class,'icon-edit')]";
	String loc_icnDeleteAutomationCampaignByName = loc_lblAutomationCampaignByName + "/following-sibling::div/i[contains(@class,'icon-delete')]";
	
	By loc_btnCreateBroadcastCampaign = By.cssSelector(".broadcast-list-page button.gs-button__green");
	By loc_lnkAddSegment = By.cssSelector(".customer-segment");
	By loc_chkSegment = By.cssSelector(".modal-content label.select-segment-row__discount");
	By loc_btnSaveBroadcastCampaign = By.cssSelector(".broadcast-form-editor button.gs-button__green");
	String loc_lblBroadcastCampaign = "//*[contains(@class,'broadcast-list-page')]//section[contains(@class,'mobile-none')]//div[contains(@class,'gs-table-body-item') and position()='1']";
	String loc_lblBroadcastCampaignByName = loc_lblBroadcastCampaign + "/self::*[.='%s']";
	String loc_icnEditBroadcastCampaignByName = loc_lblBroadcastCampaignByName + "/following-sibling::div/i[contains(@class,'icon-edit')]";
	String loc_icnDeleteBroadcastCampaignByName = loc_lblBroadcastCampaignByName + "/following-sibling::div/i[contains(@class,'icon-delete')]";
}
