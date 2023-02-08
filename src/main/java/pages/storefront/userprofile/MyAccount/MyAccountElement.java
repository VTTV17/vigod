package pages.storefront.userprofile.MyAccount;

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
    @FindBy(id = "displayName")
    WebElement DISPLAY_NAME;
    @FindBy(id = "companyName")
    WebElement COMPANY_NAME_INPUT;
    @FindBy(id = "taxCode")
    WebElement TAX_CODE_INPUT;
    @FindBy(id = "email")
    WebElement EMAIL;

    @FindBy(id = "input-country-code")
    WebElement COUNTRY_CODE_INPUT;
    @FindBy(id = "country-code-menu")
    WebElement COUNTRY_CODE_SELECT;

    @FindBy(id = "phone")
    WebElement PHONE;
    @FindBy(id = "dob")
    WebElement birthday;
    @FindBy(id = "verify-password")
    WebElement PASSWORD_FORGOT_TXTBOX;
    @FindBy(id ="male")
    WebElement MALE_RADIO;
    @FindBy(id = "female")
    WebElement FEMALE_RADIO;
    @FindBy(css = ".box_submit button[type='submit']")
    WebElement SAVE_BTN;
    @FindBy(id = "dob-picker")
    WebElement PICKER;
    @FindBy(css = ".toast-message")
    WebElement TOAST_MESSAGE;
    @FindBy(id = "add-new-other-phone")
    WebElement ADD_OTHER_PHONE_BTN;
    @FindBy(id = "phone-name-modal")
    WebElement PHONE_NAME_ADD_OTHER_PHONE;
    @FindBy(id = "phone-number-modal")
    WebElement PHONE_NUMBER_ADD_OTHER_PHONE;
    @FindBy(id = "btn-save-new-phone")
    WebElement SAVE_BTN_ADD_OTHER_PHONE;
    @FindBy(id = "phone-code-modal")
    WebElement PHONE_CODE_ADD_OTHER_PHONE;
    @FindBy(id = "add-new-other-email")
    WebElement ADD_OTHER_EMAIL_BTN;
    @FindBy(id = "email-name-modal")
    WebElement EMAIL_NAME_ADD_OTHER_EMAIL;
    @FindBy(id = "new-email-modal")
    WebElement EMAIL_ADD_OTHER_EMAIL;
    @FindBy(id = "btn-save-new-email")
    WebElement SAVE_BTN_ADD_OTHER_EMAIL;
    @FindBy(css = ".phone-number")
    List<WebElement> OTHER_PHONE_LIST;
    @FindBy(css = ".email-item")
    List<WebElement> OTHER_EMAIL_LIST;
    @FindBy(css = ".my-account-info")
    WebElement MY_ACCOUNT_TITLE;
    @FindBy(xpath = "//label[@for='displayName']")
    WebElement FULL_NAME_LBL;
    @FindBy(xpath = "//label[@for='companyName']")
    WebElement COMPANY_NAME_LBL;
    @FindBy(xpath = "//label[@for='taxCode']")
    WebElement TAX_CODE_LBL;
    @FindBy(xpath = "//label[@for='phone']")
    WebElement PHONE_NUMBER_LBL;
    @FindBy(xpath = "//label[@for='other-phone-list']")
    WebElement OTHER_PHONE_NUMBER_LBL;
    @FindBy(xpath = "//label[@for='email']")
    WebElement EMAIL_LBL;
    @FindBy(xpath = "//label[@for='other-email-list']")
    WebElement OTHER_EMAIL_LBL;
    @FindBy(xpath = "//label[@for='dob']")
    WebElement BIRTHDAY_LBL;
    @FindBy(xpath = "//label[@for='male']")
    WebElement MALE_LBL;
    @FindBy(xpath = "//label[@for='female']")
    WebElement FEMALE_LBL;
    @FindBy(xpath = "//label[@for='male']//ancestor::div[@class='form-group']/label")
    WebElement GENDER_LBL;
    @FindBy(css = ".btn-cancel")
    WebElement CANCEL_BTN;
    @FindBy(xpath = "//label[@for='phone-name-modal']")
    WebElement PHONE_NAME_LBL_ADD_OTHER_PHONE;
    @FindBy(xpath = "//label[@for='new-phone-modal']")
    WebElement PHONE_NUMBER_LBL_ADD_OTHER_PHONE;
    @FindBy(id = "btn-cancel-add-phone")
    WebElement CANCEL_BTN_ADD_OTHER_PHONE;
    @FindBy(xpath = "//label[@for='email-name-modal']")
    WebElement NAME_LBL_ADD_OTHER_EMAIL;
    @FindBy(xpath = "//label[@for='new-email-modal']")
    WebElement EMAIL_LBL_ADD_OTHER_EMAIL;
    @FindBy(id = "btn-cancel-add-email")
    WebElement CANCEL_BTN_ADD_OTHER_EMAIL;
    @FindBy(css = ".add-new-phone-error")
    WebElement ADD_OTHER_PHONE_ERROR;
    @FindBy(css = ".add-new-email-error")
    WebElement ADD_OTHER_EMAIL_ERROR;
    @FindBy(css = ".phone-name")
    List<WebElement> PHONE_NAMES;
    @FindBy(css = ".email-name")
    List<WebElement> EMAIL_NAMES;
    @FindBy(css = ".other-phone-item-wrapper .remove-icon-item")
    List<WebElement> OTHER_PHONE_DELETE_BTN;
    @FindBy(css = ".other-email-item-wrapper .remove-icon-item")
    List<WebElement> OTHER_EMAIL_DELETE_BTN;


}
