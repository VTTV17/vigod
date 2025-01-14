package web.Dashboard.signup;

import org.openqa.selenium.By;

public class SignupPageElement {

	String loc_ddvCountry = "//*[contains(@class, 'select-country__option')]//div[@class='label' and text()=\"%s\"]";
	
	By loc_ddlSelectedCountryValue =  By.cssSelector(".input-field.phone-code .select-country__single-value .option .main-content");
    By loc_ddlCountry = By.cssSelector(".input-field.phone-code .select-country__input-container input");
    By loc_ddlPhoneCode = By.cssSelector(".input-field.phone-code .option .code");
    
    By loc_txtUsername = By.id("username");
    By loc_txtPassword = By.id("password");
    By loc_txtReferralCode = By.id("refCode");
    By loc_btnSignup = By.cssSelector("form button[type='submit']");
    
    By loc_txtVerificationCode = By.id("verifyCode");
    By loc_lnkResendOTP = By.cssSelector(".resend-otp span.send-code");
    By loc_lblInvalidFeedback = By.cssSelector(".invalid-feedback");
    By loc_btnConfirm = By.cssSelector("form.verify-signup-container button[type='submit']");
    
    By loc_lblSignupFailError = By.xpath("//div[starts-with(@class,'alert__wrapper') and not(@hidden)]");
    By loc_lblSignupScreen = By.cssSelector(".step1-page__wrapper");
    By loc_lblVerificationCodeScreen = By.cssSelector(".modal-content");
    By loc_lblWizardScreen = By.cssSelector(".wizard-layout__title");
	
}
