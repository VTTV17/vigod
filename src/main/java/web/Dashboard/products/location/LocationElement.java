package web.Dashboard.products.location;

import org.openqa.selenium.By;

public class LocationElement {
    By loc_btnAddLocation = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    By loc_dlgAddLocation = By.cssSelector(".add-edit-location-modal");
    By loc_dlgAddLocation_ddvSelectedBranch = By.xpath("(//*[contains(@class, 'dropdown-toggle')])[1]");
    String str_dlgAddLocation_ddvBranches = "(//*[contains(@class, 'dropdown-toggle')])[1]/following-sibling::div/button[text() = '%s']";
    By loc_dlgAddLocation_ddvSelectedParent = By.xpath("(//*[contains(@class, 'dropdown-toggle')])[2]");
    String str_dlgAddLocation_ddvParents = "(//*[contains(@class, 'dropdown-toggle')])[2]/following-sibling::div/button[text() = '%s']";
    By loc_dlgAddLocation_txtName = By.cssSelector("[id= 'value']");
    By loc_dlgAddLocation_txtCode = By.cssSelector("[id= 'code']");
    By loc_dlgAddLocation_txtLength = By.cssSelector("[id= 'length']");
    By loc_dlgAddLocation_txtWidth = By.cssSelector("[id= 'width']");
    By loc_dlgAddLocation_txtHeight = By.cssSelector("[id= 'height']");
    By loc_dlgAddLocation_icnAdd = By.cssSelector("[alt= 'add']");
    By loc_dlgAddLocation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgEditLocation = By.cssSelector(".add-edit-location-modal");
    By loc_dlgEditLocation_ddvSelectedBranch = By.xpath("(//*[contains(@class, 'dropdown-toggle')])[1]");
    String str_dlgEditLocation_ddvBranches = "(//*[contains(@class, 'dropdown-toggle')])[1]/following-sibling::div/button[text() = '%s']";
    By loc_dlgEditLocation_ddvSelectedParent = By.xpath("(//*[contains(@class, 'dropdown-toggle')])[2]");
    String str_dlgEditLocation_ddvParents = "(//*[contains(@class, 'dropdown-toggle')])[2]/following-sibling::*/button[text() = '%s']";
    By loc_dlgEditLocation_txtName = By.cssSelector("[id*= 'name']");
    By loc_dlgEditLocation_txtCode = By.cssSelector("[id*= 'code']");
    By loc_dlgEditLocation_txtLength = By.cssSelector("[id*= 'length']");
    By loc_dlgEditLocation_txtWidth = By.cssSelector("[id*= 'width']");
    By loc_dlgEditLocation_txtHeight = By.cssSelector("[id*= 'height']");
    By loc_dlgEditLocation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_imgActions = By.xpath("//tr[not(contains(@class, 'child'))]//*[contains(@class, 'location-name')]/following-sibling::td[string()= '0'][1]/preceding-sibling::td[3]//img[@alt='icon-setting']");
    /**
     * 0: Edit location
     * 1: Add location
     * 2: Delete location
     */
    By loc_ddlListActions = By.cssSelector(".popover-setting  p");
    By loc_dlgConfirmDeleteLocation = By.cssSelector(".delete-location");
    By loc_dlgConfirmDeleteLocation_btnYes = By.cssSelector(".delete-location .gs-button__green");

}
