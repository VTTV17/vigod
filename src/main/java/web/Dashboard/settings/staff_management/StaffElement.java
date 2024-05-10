package web.Dashboard.settings.staff_management;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class StaffElement {
    WebDriver driver;

    public StaffElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "li:nth-child(5) > a.nav-link")
    WebElement STAFF_MANAGEMENT_MENU;

    @FindBy (css = "div.staff-management > div > div >button")
    WebElement ADD_STAFF_BTN;

    @FindBy (css = "#name")
    WebElement STAFF_NAME;

    @FindBy (css = "div.modal-body> div > div >input[name = 'email']")
    WebElement STAFF_MAIL;

    @FindBy (css = "tr:nth-child(2)>td>div>div.staff__email.gsa__color--gray")
    WebElement STAFF_MAIL_VALUE;

    @FindBy (css = "div.permission__body > fieldset > div > div > div > div > input")
    List<WebElement> STAFF_PERMISSIONS_CHECKBOX;

    @FindBy (css = "div.permission__body > fieldset > div > div > div > div > label")
    List<WebElement> STAFF_PERMISSIONS_LABEL;

    @FindBy (css = "div.branch__body > fieldset > div > div > div > div > input")
    List<WebElement> STAFF_BRANCH_CHECKBOX;

    @FindBy (css = "div.branch__body > fieldset > div > div > div > div > label")
    List<WebElement> STAFF_BRANCH_LABEL;

    @FindBy (css = "div.staff-modal__footer > button.gs-button__green")
    WebElement DONE_BTN;
    
    @FindBy (css = "div.staff-modal__footer > button.gs-button__white")
    WebElement CANCEL_BTN;

    @FindBy (css = "div.Toastify > div > div > div")
    WebElement TOAST_MESSAGE;

    @FindBy (css = "div.header-right__ele-right > a[href='/logout']")
    WebElement LOGOUT_BTN;

    @FindBy (css = "tr:nth-child(2) > td.gsa-white-space--nowrap > div > i:nth-child(2)")
    WebElement DELETE_ICON;

    @FindBy (css = "div.modal-footer>button.gs-button__green")
    WebElement OK_BTN;

    @FindBy (css = "")
    WebElement STAFF_MANAGEMENT_HEADER;

    @FindBy (css = "")
    WebElement STAFF_NAME_COLUMN;

    @FindBy (css = "")
    WebElement STAFF_PERMISSIONS_COLUMN;

    @FindBy (css = "")
    WebElement STAFF_STATUS_COLUMN;

    @FindBy (css = "")
    WebElement ACTIONS_COLUMN;

    @FindBy (css = "")
    WebElement STAFF_MANAGEMENT_FOOTER;
    
    //It'll take some time to convert all the WebElements above into By locators so we'll leave them unchanged for now.
    By loc_lblStaffEmailTable = By.cssSelector(".staff-list-desktop .staff__email");
    
    By loc_ddlSelectGroupForm = By.cssSelector("[name='permissionGroupIds']");
    By loc_ddvSelectFirstGroupForm = By.xpath("//div[starts-with(@id, 'react-select-') and contains(@id, 'option-0')]");
    
    By loc_btnEditIcon = By.xpath("//div[contains(@class,'staff-list-desktop')]//i[contains(@style, 'icon-edit')]");
    By loc_btnDeleteIcon = By.xpath("//div[contains(@class,'staff-list-desktop')]//i[contains(@style, 'icon-delete')]");
    
    By loc_btnActivateStaffToggle = By.cssSelector(".staff-list-desktop .btn-enable-staff .uik-checkbox__toggle");
    By loc_tmpInputTag = By.xpath("./input");
}
