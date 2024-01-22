package pages.gomua.logingomua;

import org.openqa.selenium.By;

public class LoginGoMuaElement {

	By loc_txtUsername = By.id("usr");
	By loc_txtPassword = By.xpath("//section[@class='signin-box']//input[@type='password'and @id='pwd']");
	By loc_btnLogin = By.xpath("//button[@beetranslate='beecow.login.login']");
	By loc_lnkForgotPassword = By.cssSelector("[beetranslate='beecow.login.forgotpwd']");
}
