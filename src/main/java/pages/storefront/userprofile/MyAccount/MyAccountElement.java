package pages.storefront.userprofile.MyAccount;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

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
    WebElement COUNTRY_CODE;

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

}
