package web.Dashboard.settings.permission.create;

import org.openqa.selenium.By;

public class CreatePermissionPageElement {

	By loc_txtName = By.id("name");
	By loc_txtDescription = By.id("description");
	By loc_chkFullPermission = By.id("full-permission");
	
	By loc_btnSave = By.cssSelector(".setting-create-group-permission button.gs-button__green");
	
	By loc_btnDeleteIcon = By.cssSelector("button [src*='/icons/trash.svg']");
	
}
