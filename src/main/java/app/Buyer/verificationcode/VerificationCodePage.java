package app.Buyer.verificationcode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonMobile;

public class VerificationCodePage {

	final static Logger logger = LogManager.getLogger(VerificationCodePage.class);

    WebDriver driver;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public VerificationCodePage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonMobile(driver);
    }

//    By loc_txtVerificationCode = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]");
    By loc_txtVerificationCode = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]//*[ends-with(@resource-id,'id/social_layout_limit_edittext')]");
    By loc_btnResend = By.xpath("//*[ends-with(@resource-id,'verify_code_resend_action')]");
    By loc_btnVerify = By.xpath("//*[ends-with(@resource-id,'verify_code_action')]");
    By loc_lblVerificationCodeError = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]//*[contains(@class,'TextView')]");
    
    public VerificationCodePage inputVerificationCode(String password) {
        commonAction.inputText(loc_txtVerificationCode, password);
        logger.info("Input Password: {}", password);
        return this;
    }
    
    public VerificationCodePage clickResendBtn() {
    	commonAction.clickElement(loc_btnResend);
    	logger.info("Clicked Resend button.");
        return this;
    }    
    
    public VerificationCodePage clickVerifyBtn() {
    	commonAction.clickElement(loc_btnVerify);
    	logger.info("Clicked Verify button.");
    	return this;
    }      
    
    public String getVerificationCodeError() {
    	UICommonMobile.sleepInMiliSecond(1500); //Sometimes it takes longer for the error to appear
    	String text = commonAction.getText(loc_lblVerificationCodeError);
    	logger.info("Retrieved verification field error: " + text);
    	return text;
    }     
    
}
