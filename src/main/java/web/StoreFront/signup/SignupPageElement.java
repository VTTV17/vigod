package web.StoreFront.signup;

import org.openqa.selenium.By;

public class SignupPageElement {

    By loc_lblSignupScreen = By.cssSelector("#signup-modal .modal-content");
    By loc_lblVerificationCodeScreen = By.cssSelector("#activate-modal .modal-content");
    By loc_txtUsername = By.cssSelector("#signup-username");
    By loc_txtDisplayUsername = By.cssSelector("#signup-displayName");
    By loc_txtBirthday = By.cssSelector("#signup-dob");
    By loc_txtOptionalEmail = By.cssSelector("#get-email");
    By loc_txtPassword = By.cssSelector("#signup-password");
    By loc_lnkLoginNow = By.cssSelector("#signup-modal [data-target='#login-modal']");
    By loc_lnkSkipEmail = By.id("get-email-skip");
    By loc_btnCompleteEmail = By.cssSelector("#frm-get-email .btn-submit");
    By loc_btnSignup = By.cssSelector("#frm-signup .btn-submit");
    By loc_ddlCountry = By.cssSelector("#signup-country-code");
    By loc_lstCountry = By.cssSelector("#signup-country-code-menu .dropdown-item");
    By loc_txtVerificationCode = By.cssSelector("#activate-code");
    By loc_btnConfirmOTP = By.cssSelector("#frm-activate .btn-submit");
    By loc_lnkResendOTP = By.id("open-activate-resend-code");
    By loc_lblSignupFailError = By.id("signup-fail");
    By loc_lblUsernameError = By.id("signup-username-error");
    By loc_lblPasswordError = By.id("signup-password-error");
    By loc_lblDisplayNameError = By.id("signup-displayName-error");
    By loc_lblWrongCodeError = By.id("activate-fail");	
	
}
