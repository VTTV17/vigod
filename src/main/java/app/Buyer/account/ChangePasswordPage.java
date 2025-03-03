package app.Buyer.account;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonMobile;
import utilities.model.dashboard.setting.languages.translation.MobileAndroid;
import utilities.utils.localization.TranslateText;

public class ChangePasswordPage {
    final static Logger logger = LogManager.getLogger(ChangePasswordPage.class);

    WebDriver driver;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public ChangePasswordPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonMobile(driver);
    }
    
    //TODO: Move them to a separate file
    By loc_txtCurrentPassword = By.xpath("//*[ends-with(@resource-id,'current_password_field')]//*[ends-with(@resource-id,'social_layout_limit_edittext')]");
    By loc_txtNewPassword = By.xpath("//*[ends-with(@resource-id,'new_password_field')]//*[ends-with(@resource-id,'social_layout_limit_edittext')]");
    By loc_btnDone = By.xpath("//*[ends-with(@resource-id,'change_password_btn_done')]");
    By loc_lblCurrentPasswordError = By.xpath("//*[ends-with(@resource-id,'change_password_tv_error_current_password')]");
    By loc_lblNewPasswordError = By.xpath("//*[ends-with(@resource-id,'change_password_tv_error_new_password')]");    
    
    public static String localizedInvalidPasswordFormatError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "social_password_condition_2");
    }    
    public static String localizedWrongCurrentPasswordError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "social_change_password_error_current_password");
    }    
    
    public ChangePasswordPage inputCurrentPassword(String password){
        commonAction.inputText(loc_txtCurrentPassword, password);
        logger.info("Input '" + password + "' into Current Password field to change password.");
        return this;
    }    
   
    public ChangePasswordPage clickNewPassword() {
    	commonAction.clickElement(loc_txtNewPassword);
    	logger.info("Clicked on New Password field.");
    	return this;
    }
    
    public ChangePasswordPage inputNewPassword(String password){
    	commonAction.inputText(loc_txtNewPassword, password);
    	logger.info("Input '" + password + "' into New Password field to change password.");
    	return this;
    }
    
    public ChangePasswordPage clickChangePasswordDoneBtn() {
    	commonAction.clickElement(loc_btnDone);
    	logger.info("Clicked on Done button to change password.");
    	return this;
    }     
    
    public String getCurrentPasswordError(){
    	String text = commonAction.getText(loc_lblCurrentPasswordError);
    	logger.info("Retrieved error for Current Password field: " + text);
    	return text;
    }
    
    public String getNewPasswordError(){
    	String text = commonAction.getText(loc_lblNewPasswordError);
    	logger.info("Retrieved error for New Password field: " + text);
    	return text;
    }    
    
}
