package pages.gomua.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class SignupGomua {
	final static Logger logger = LogManager.getLogger(SignupGomua.class);

	/* Message headers of mails sent to seller's mailbox */
	public String SUCCESSFUL_SIGNUP_MESSAGE_VI = "Đăng ký thành công tài khoản GoMua";
	public String SUCCESSFUL_SIGNUP_MESSAGE_EN = "Successful GoMua registration";
	
	public String VERIFICATION_CODE_MESSAGE_VI = "là mã xác minh tài khoản GoMua của bạn";
	public String VERIFICATION_CODE_MESSAGE_EN = "is your GoMua's verification code";
	/* ================================================== */
	
	WebDriver driver;
	UICommonAction common;
	SignupGomuaElement elements;

	public SignupGomua(WebDriver driver) {
		this.driver = driver;
		common = new UICommonAction(driver);
		elements = new SignupGomuaElement();
	}

	public SignupGomua inputUsername(String userName) {
		common.inputText(elements.loc_txtUsername, userName);
		logger.info("Input username: %s".formatted(userName));
		return this;
	}

	public SignupGomua inputPassWord(String password) {
		common.inputText(elements.loc_txtPassword, password);
		logger.info("Input password: %s".formatted(password));
		return this;
	}

	public SignupGomua inputDisplayName(String displayName) {
		common.inputText(elements.loc_txtDisplayName, displayName);
		logger.info("Input display name: %s".formatted(displayName));
		return this;
	}
	
	public SignupGomua inputEmail(String email) {
		common.inputText(elements.loc_txtEmail, email);
		logger.info("Input email: %s".formatted(email));
		return this;
	}

	public SignupGomua clickComplete() {
		common.click(elements.loc_btnComplete);
		logger.info("Clicked on 'Complete' button");
		return this;
	}
	
	public SignupGomua clickContinueBtn() {
		common.click(elements.loc_btnContinue);
		logger.info("Clicked on 'Continue' button");
		return this;
	}

	public SignupGomua inputVerificationCode(String verificationCode) {
		common.inputText(elements.loc_txtOTP, verificationCode);
		logger.info("Input '" + verificationCode + "' into Verification Code field.");
		return this;
	}

	public SignupGomua clickVerifyAndLoginBtn() {
		common.click(elements.loc_btnVerifyLogin);
		logger.info("Clicked on 'Verify and Login' button");
		return this;
	}
	
	public SignupGomua clickAgreeAndContiueBtn() {
		common.click(elements.loc_btnAgreeContinue);
		logger.info("Clicked on 'Agree and Continue' button");
		return this;
	}
	
}
