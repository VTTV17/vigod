package web.Dashboard.settings.permission.permissionassignment;

import org.openqa.selenium.By;

public class AssignPermissionPageElement {

	By loc_btnAddStaff = By.cssSelector(".setting-group-staff-management button.gs-button__green");
	
	By loc_txtSelectStaffSearch = By.cssSelector(".staff-group-permission-list .pro-select-variant-search input");
	
	By loc_chkStaff = By.cssSelector(".staff-group-permission--inner .uik-checkbox__label.green");
	
	By loc_btnDeleteIcon = By.cssSelector("button [src*='/icons/trash.svg']");
	
	By loc_btnLastDeleteIcon = By.xpath("(//img[contains(@src, 'icons/trash.svg')])[last()]");
	
	String loc_btnSpecificDeleteIcon = "//div[contains(@class,'setting-group-staff-management')]//tbody/tr/td[position()=2 and text()='%s']/following-sibling::*//img[@src='assets/icons/trash.svg']";	
}
