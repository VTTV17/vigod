package web.StoreFront.login;

import org.openqa.selenium.By;

public class LoginPageElement {

    By loc_lblScreenText = By.cssSelector("#login-modal .modal-content");
    By loc_ddlCountry = By.id("login-country-code");
	By loc_lstCountry = By.id("login-country-code-menu");
	String loc_ddvCountryByName = ".//span[text()='%s']";
    By loc_txtUsername = By.id("login-username");
    By loc_txtPassword = By.id("login-password"); 
    By loc_btnLogin = By.cssSelector("#frm-login .btn-submit");
    By loc_lnkForgotPassword = By.id("open-forgot-pwd");
    By loc_btnFacebookLogin = By.cssSelector("#login-modal .facebook-login-button"); 
    By loc_lnkCreateAccount = By.cssSelector("#login-modal [data-target='#signup-modal']");
    By loc_lblUsernameError = By.id("login-username-error");
    By loc_lblLoginFailError = By.id("login-fail");
    By loc_lblPasswordError = By.id("login-password-error");
}
