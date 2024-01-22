package web.StoreFront.userprofile.MyAccount;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class MyAccountElement {
    WebDriver driver;
    public MyAccountElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    By loc_txtFullName = By.id("displayName");
    By loc_txtCompanyName = By.id("companyName");
    By loc_txtTaxCode = By.id("taxCode");
    By loc_txtEmail = By.id("email");
    By loc_lblCountryCodeValue = By.id("input-country-code");
    By loc_ddlCountryCode = By.id("country-code-menu");
    By loc_txtPhone = By.id("phone");
    By loc_txtBirthday = By.id("dob");
    By loc_chbMale = By.id("male");
    By loc_chbFemale = By.id("female");
    By loc_btnSave = By.cssSelector(".box_submit button[type='submit']");
    By loc_btnBirthday = By.id("dob-picker");
    By loc_lblToastMessage = By.cssSelector(".toast-message");
    By loc_btnAddOtherPhone = By.id("add-new-other-phone");
    By loc_dlgOtherPhone_txtName = By.id("phone-name-modal");
    By loc_dlgOtherPhone_txtPhoneNumber = By.id("phone-number-modal");
    By loc_dlgOtherPhone_btnSave = By.id("btn-save-new-phone");
    By loc_dlgOtherPhone_ddlPhoneCode = By.id("phone-code-modal");
    By loc_btnAddOtherEmail = By.id("add-new-other-email");
    By loc_dlgOtherEmail_txtEmailName = By.id("email-name-modal");
    By loc_dlgOtherEmail_txtEmail = By.id("new-email-modal");
    By loc_dlgOtherEmail_btnSave = By.id("btn-save-new-email");
    By loc_lst_lblOtherPhoneNumber = By.cssSelector(".phone-number");
    By loc_lst_lblOtherEmail = By.cssSelector(".email-item");
    By loc_lblMyAccount = By.cssSelector(".my-account-info");
    By loc_lblFullName = By.xpath("//label[@for='displayName']");
    By loc_lblCompanyName = By.xpath("//label[@for='companyName']");
    By loc_lblTaxCode = By.xpath("//label[@for='taxCode']");
    By loc_lblPhoneNumber = By.xpath("//label[@for='phone']");
    By loc_lblOtherPhoneNumber = By.xpath("//label[@for='other-phone-list']");
    By loc_lblEmail = By.xpath("//label[@for='email']");
    By loc_lblOtherEmail = By.xpath("//label[@for='other-email-list']");
    By loc_lblBirthday = By.xpath("//label[@for='dob']");
    By loc_lblMale = By.xpath("//label[@for='male']");
    By loc_lblFemale = By.xpath("//label[@for='female']");
    By loc_lblGender = By.xpath("//label[@for='male']//ancestor::div[@class='form-group']/label");
    By loc_btnCancel = By.cssSelector(".btn-cancel");
    By loc_dlgOtherPhone_lblPhoneName = By.xpath("//label[@for='phone-name-modal']");
    By loc_dlgOtherPhone_lblPhoneNumber = By.xpath("//label[@for='new-phone-modal']");
    By loc_dlgOtherPhone_btnCancel = By.id("btn-cancel-add-phone");
    By loc_dlgOtherEmail_lblName = By.xpath("//label[@for='email-name-modal']");
    By loc_dlgOtherEmail_lblEmail = By.xpath("//label[@for='new-email-modal']");
    By loc_dlgOtherEmail_btnCancel = By.id("btn-cancel-add-email");
    By loc_lblAddOtherPhoneError = By.cssSelector(".add-new-phone-error");
    By loc_lblAddOtherEmailError = By.cssSelector(".add-new-email-error");
    By loc_lst_lblPhoneName = By.cssSelector(".phone-name");
    By loc_lst_lblOtherEmailName = By.cssSelector(".email-name");
    By loc_lst_btnDeleteOtherPhone = By.cssSelector(".other-phone-item-wrapper .remove-icon-item");
    By loc_lst_btnDeleteOtherEmail = By.cssSelector(".other-email-item-wrapper .remove-icon-item");

//    @FindBy(id = "displayName")
//    WebElement DISPLAY_NAME;
//    @FindBy(id = "companyName")
//    WebElement COMPANY_NAME_INPUT;
//    @FindBy(id = "taxCode")
//    WebElement TAX_CODE_INPUT;
//    @FindBy(id = "email")
//    WebElement EMAIL;
//    @FindBy(id = "input-country-code")
//    WebElement COUNTRY_CODE_INPUT;
//    @FindBy(id = "country-code-menu")
//    WebElement COUNTRY_CODE_SELECT;

//    @FindBy(id = "phone")
//    WebElement PHONE;
//    @FindBy(id = "dob")
//    WebElement birthday;
//    @FindBy(id = "verify-password")
//    WebElement PASSWORD_FORGOT_TXTBOX;
//    @FindBy(id ="male")
//    WebElement MALE_RADIO;
//    @FindBy(id = "female")
//    WebElement FEMALE_RADIO;
//    @FindBy(css = ".box_submit button[type='submit']")
//    WebElement SAVE_BTN;
//    @FindBy(id = "dob-picker")
//    WebElement PICKER;
//    @FindBy(css = ".toast-message")
//    WebElement TOAST_MESSAGE;
//    @FindBy(id = "add-new-other-phone")
//    WebElement ADD_OTHER_PHONE_BTN;
//    @FindBy(id = "phone-name-modal")
//    WebElement PHONE_NAME_ADD_OTHER_PHONE;
//    @FindBy(id = "phone-number-modal")
//    WebElement PHONE_NUMBER_ADD_OTHER_PHONE;
//    @FindBy(id = "btn-save-new-phone")
//    WebElement SAVE_BTN_ADD_OTHER_PHONE;
//    @FindBy(id = "phone-code-modal")
//    WebElement PHONE_CODE_ADD_OTHER_PHONE;
//    @FindBy(id = "add-new-other-email")
//    WebElement ADD_OTHER_EMAIL_BTN;
//    @FindBy(id = "email-name-modal")
//    WebElement EMAIL_NAME_ADD_OTHER_EMAIL;
//    @FindBy(id = "new-email-modal")
//    WebElement EMAIL_ADD_OTHER_EMAIL;
//    @FindBy(id = "btn-save-new-email")
//    WebElement SAVE_BTN_ADD_OTHER_EMAIL;
//    @FindBy(css = ".phone-number")
//    List<WebElement> OTHER_PHONE_LIST;
//    @FindBy(css = ".email-item")
//    List<WebElement> OTHER_EMAIL_LIST;
//    @FindBy(css = ".my-account-info")
//    WebElement MY_ACCOUNT_TITLE;
//    @FindBy(xpath = "//label[@for='displayName']")
//    WebElement FULL_NAME_LBL;
//    @FindBy(xpath = "//label[@for='companyName']")
//    WebElement COMPANY_NAME_LBL;
//    @FindBy(xpath = "//label[@for='taxCode']")
//    WebElement TAX_CODE_LBL;
//    @FindBy(xpath = "//label[@for='phone']")
//    WebElement PHONE_NUMBER_LBL;
//    @FindBy(xpath = "//label[@for='other-phone-list']")
//    WebElement OTHER_PHONE_NUMBER_LBL;
//    @FindBy(xpath = "//label[@for='email']")
//    WebElement EMAIL_LBL;
//    @FindBy(xpath = "//label[@for='other-email-list']")
//    WebElement OTHER_EMAIL_LBL;
//    @FindBy(xpath = "//label[@for='dob']")
//    WebElement BIRTHDAY_LBL;
//    @FindBy(xpath = "//label[@for='male']")
//    WebElement MALE_LBL;
//    @FindBy(xpath = "//label[@for='female']")
//    WebElement FEMALE_LBL;
//    @FindBy(xpath = "//label[@for='male']//ancestor::div[@class='form-group']/label")
//    WebElement GENDER_LBL;
//    @FindBy(css = ".btn-cancel")
//    WebElement CANCEL_BTN;
//    @FindBy(xpath = "//label[@for='phone-name-modal']")
//    WebElement PHONE_NAME_LBL_ADD_OTHER_PHONE;
//    @FindBy(xpath = "//label[@for='new-phone-modal']")
//    WebElement PHONE_NUMBER_LBL_ADD_OTHER_PHONE;
//    @FindBy(id = "btn-cancel-add-phone")
//    WebElement CANCEL_BTN_ADD_OTHER_PHONE;
//    @FindBy(xpath = "//label[@for='email-name-modal']")
//    WebElement NAME_LBL_ADD_OTHER_EMAIL;
//    @FindBy(xpath = "//label[@for='new-email-modal']")
//    WebElement EMAIL_LBL_ADD_OTHER_EMAIL;
//    @FindBy(id = "btn-cancel-add-email")
//    WebElement CANCEL_BTN_ADD_OTHER_EMAIL;
//    @FindBy(css = ".add-new-phone-error")
//    WebElement ADD_OTHER_PHONE_ERROR;
//    @FindBy(css = ".add-new-email-error")
//    WebElement ADD_OTHER_EMAIL_ERROR;
//    @FindBy(css = ".phone-name")
//    List<WebElement> PHONE_NAMES;
//    @FindBy(css = ".email-name")
//    List<WebElement> EMAIL_NAMES;
//    @FindBy(css = ".other-phone-item-wrapper .remove-icon-item")
//    List<WebElement> OTHER_PHONE_DELETE_BTN;
//    @FindBy(css = ".other-email-item-wrapper .remove-icon-item")
//    List<WebElement> OTHER_EMAIL_DELETE_BTN;
}
