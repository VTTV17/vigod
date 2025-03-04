package app.Buyer.login;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import app.Buyer.signup.SignupPage;
import utilities.commons.UICommonMobile;
import utilities.model.dashboard.setting.languages.translation.MobileAndroid;
import utilities.utils.localization.TranslateText;

public class ForgotPasswordPage {

	final static Logger logger = LogManager.getLogger(ForgotPasswordPage.class);

    WebDriver driver;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public ForgotPasswordPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonMobile(driver);
    }

    public static String localizedEmailNotExistError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "email_not_exist");
    }       
    public static String localizedPhoneNotExistError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "phone_not_exist");
    }       
    
    By loc_txtxUsername = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[1]");
    By loc_txtPassword = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[2]");
    
    public ForgotPasswordPage selectCountry(String country) {
    	//TODO: Temporarily reuse function from SignupPage. For the long run, create its own function
    	new SignupPage(driver).selectCountry(country);
    	return this;
    }        
    
    public ForgotPasswordPage inputUsername(String username) {
    	commonAction.inputText(loc_txtxUsername, username);
    	logger.info("Input Username: {}", username);
        return this;
    }

    public ForgotPasswordPage inputNewPassword(String password) {
        commonAction.inputText(loc_txtPassword, password);
        logger.info("Input Password: {}", password);
        return this;
    }
    
    public ForgotPasswordPage clickContinueBtn() {
    	commonAction.clickElement(SignupPage.loc_btnContinue);
    	logger.info("Clicked Continue button.");
        return this;
    }  

    public String getUsernameError() {
    	String text = commonAction.getText(SignupPage.loc_lblUsernameError);
    	logger.info("Retrieved Username field error: " + text);
    	return text;
    }   
    
}
