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
	
	By loc_ddlSelectedPage = By.cssSelector(".page-selector.dropdown");
	By loc_ddlAssignStaff = By.cssSelector(".assign-staff-container .btn-assign");
	By loc_ddvAssignStaff = By.cssSelector(".dropdown-menu .staff-row div");
	By loc_btnAssignToMe = By.cssSelector(".dropdown-menu button.gs-button__green");
	By loc_btnUnAssign = By.cssSelector(".dropdown-menu button.gs-button__white");
	
	By loc_btnCreateTag = By.cssSelector(".social-tag-container .add-tags");
	By loc_txtTagNameInDialog = By.cssSelector(".modal-body #tagName");
	By loc_btnAddTagInDialog = By.cssSelector(".modal-body .gs-button__green");
	
	String loc_lblTagByName = "//div[@class='modal-body']//main//div[contains(@class,'tagName')]/span[text()='%s']";
}
