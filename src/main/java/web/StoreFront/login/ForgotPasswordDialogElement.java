package web.StoreFront.login;

import org.openqa.selenium.By;

public class ForgotPasswordDialogElement {

    By loc_lblForgotScreen = By.cssSelector("#forgot-pwd-modal .modal-content");
    By loc_ddlCountry = By.id("forgot-pwd-country-code");
	By loc_lstCountry = By.id("forgot-pwd-country-code-menu");
	String loc_ddvCountryByName = ".//span[text()='%s']";
    By loc_txtUsername = By.id("forgot-pwd-username");
    By loc_btnContinue = By.cssSelector("#frm-forgot-pwd .btn-submit"); 
    By loc_lnkBackToLogin = By.cssSelector("#forgot-pwd-modal [data-target='#login-modal']");
    By loc_txtPassword = By.id("verify-password");
    By loc_btnConfirm = By.cssSelector("#frm-verify .btn-submit"); 
    By loc_txtVerificationCode = By.id("verify-code"); 
    By loc_lblWrongVerificationCodeError = By.id("verify-fail");
    By loc_lblUsernameError = By.xpath("//div[@id='forgot-pwd-fail' and @style='display: inline-block;']");
    By loc_lblPasswordError = By.id("verify-password-error");
}
