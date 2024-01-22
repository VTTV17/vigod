package web.GoMua.signup;

import org.openqa.selenium.By;

public class SignupGomuaElement {

	By loc_txtUsername = By.id("usr");
	By loc_txtPassword = By.xpath("//section[@class='signin-box']//input[@type='password'and @id='pwd']");
	By loc_txtDisplayName = By.id("displayName");
	By loc_txtEmail = By.id("email");
	By loc_btnComplete = By.cssSelector("#get-email [beetranslate='beecow.getemail.button.done']");
	By loc_btnContinue = By.cssSelector("button[beetranslate='beecow.signup.continue']");
	By loc_txtOTP = By.id("code");
	By loc_btnVerifyLogin = By.cssSelector("[beetranslate='beecow.activate.button']");
	By loc_btnAgreeContinue = By.cssSelector(".modal-dialog [beetranslate='beecow.redirectgosell.button']");
}
