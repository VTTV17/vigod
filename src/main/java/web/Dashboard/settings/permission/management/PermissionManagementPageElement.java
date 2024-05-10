package web.Dashboard.settings.permission.management;

import org.openqa.selenium.By;

public class PermissionManagementPageElement {

	By loc_txtName = By.id("id");
	By loc_txtDescription = By.id("description");
	By loc_chkFullPermission = By.id("full-permission");
	
	By loc_btnSave = By.cssSelector(".setting-create-group-permission button.gs-button__green");
	
	String loc_btnSpecificDeleteIcon = "//div[contains(@class,'setting-permissions-management')]//tbody/tr/td[text()='%s']/following-sibling::*//img[@src='assets/icons/trash.svg']";
	
	By loc_btnDeleteIcon = By.cssSelector("button [src*='/icons/trash.svg']");
	
}
