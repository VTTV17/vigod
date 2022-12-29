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
}
