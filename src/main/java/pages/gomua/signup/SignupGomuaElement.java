package pages.gomua.signup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupGomuaElement {
	WebDriver driver;

	public SignupGomuaElement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "usr")
	WebElement USERNAME_INPUT;
	
	@FindBy(xpath = "//section[@class='signin-box']//input[@type='password'and @id='pwd']")
	WebElement PASSWORD_INPUT;
	
	@FindBy(id = "displayName")
	WebElement DISPLAY_NAME;
	
	@FindBy(id = "email")
	WebElement EMAIL;

	@FindBy(css = "#get-email [beetranslate='beecow.getemail.button.done']")
	WebElement COMPLETE_BTN;
	
	@FindBy(css = "button[beetranslate='beecow.signup.continue']")
	WebElement CONTINUE_BTN;
	
	@FindBy(id = "code")
	WebElement OTP_INPUT;
	
	@FindBy(css = "[beetranslate='beecow.activate.button']")
	WebElement VERIFY_LOGIN_BTN;
	
	@FindBy(css = ".modal-dialog [beetranslate='beecow.redirectgosell.button']")
	WebElement AGREE_CONTINUE_BTN;
	
}
