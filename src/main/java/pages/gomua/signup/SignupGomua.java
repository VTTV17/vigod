package pages.gomua.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonAction;

import java.time.Duration;

public class SignupGomua {
	final static Logger logger = LogManager.getLogger(SignupGomua.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction common;
	SignupGomuaElement elements;

	public SignupGomua(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		common = new UICommonAction(driver);
		elements = new SignupGomuaElement(driver);
		PageFactory.initElements(driver, this);
	}

	public SignupGomua inputUsername(String userName) {
		common.inputText(elements.USERNAME_INPUT, userName);
		logger.info("Input username: %s".formatted(userName));
		return this;
	}

	public SignupGomua inputPassWord(String password) {
		common.inputText(elements.PASSWORD_INPUT, password);
		logger.info("Input password: %s".formatted(password));
		return this;
	}

	public SignupGomua inputDisplayName(String displayName) {
		common.inputText(elements.DISPLAY_NAME, displayName);
		logger.info("Input display name: %s".formatted(displayName));
		return this;
	}
	
	public SignupGomua inputEmail(String email) {
		common.inputText(elements.EMAIL, email);
		logger.info("Input email: %s".formatted(email));
		return this;
	}

	public SignupGomua clickComplete() {
		common.clickElement(elements.COMPLETE_BTN);
		logger.info("Clicked on 'Complete' button");
		return this;
	}
	
	public SignupGomua clickContinueBtn() {
		common.clickElement(elements.CONTINUE_BTN);
		logger.info("Clicked on 'Continue' button");
		return this;
	}

	public SignupGomua inputVerificationCode(String verificationCode) {
		common.inputText(elements.OTP_INPUT, verificationCode);
		logger.info("Input '" + verificationCode + "' into Verification Code field.");
		return this;
	}

	public SignupGomua clickVerifyAndLoginBtn() {
		common.clickElement(elements.VERIFY_LOGIN_BTN);
		logger.info("Clicked on 'Verify and Login' button");
		return this;
	}
	
	public SignupGomua clickAgreeAndContiueBtn() {
		common.clickElement(elements.AGREE_CONTINUE_BTN);
		logger.info("Clicked on 'Agree and Continue' button");
		return this;
	}
	
}
