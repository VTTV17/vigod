package pages.buyerapp;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public SignupPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }
    
    By COUNTRYCODE = By.xpath("//*[ends-with(@resource-id,'country_code')]");
    By MAGNIFIER = By.xpath("//*[ends-with(@resource-id,'btn_search')]");
    By COUNTRY_SEARCHBOX = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By COUNTRY_SEARCHRESULT = By.xpath("//*[ends-with(@resource-id,'country_code_list_tv_title')]");
    
    By MAIL_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[1]");
    By PHONE_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    
    By TEXTBOX = By.xpath("//*[ends-with(@resource-id,'limit_edittext')]");
    
    By USERNAME = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]");
    By DISPLAYNAME = By.xpath("//*[ends-with(@resource-id,'displayName')]");
    By BIRTHDAY = By.xpath("//*[ends-with(@resource-id,'birthday')]");
    
    By TERM_CHK = By.xpath("//*[ends-with(@resource-id,'btn_check_term_and_policy')]");
    By CONTINUE_BTN = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    
    By USERNAME_ERROR = By.xpath("//*[contains(@resource-id,'error')]");
    
    By VERIFICATIONCODE = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]");
    By RESEND_BTN = By.xpath("//*[ends-with(@resource-id,'verify_code_resend_action')]");
    By VERIFY_BTN = By.xpath("//*[ends-with(@resource-id,'verify_code_action')]");

    By TOASTMESSAGE = By.xpath("//*[ends-with(@class,'Toast')]");
    

    public SignupPage clickMailTab() {
    	commonAction.clickElement(MAIL_TAB);
    	logger.info("Clicked on Mail tab.");
    	return this;
    }
    
    public SignupPage clickPhoneTab() {
    	commonAction.clickElement(PHONE_TAB);
    	logger.info("Clicked on Phone tab.");
    	return this;
    }

    public SignupPage clickCountryCodeField() {
    	commonAction.clickElement(COUNTRYCODE);
    	logger.info("Clicked on Country code field.");
        return this;
    }    
    
    public SignupPage clickMagnifierIcon() {
    	commonAction.clickElement(MAGNIFIER);
    	logger.info("Clicked on Magnifier icon.");
    	return this;
    }    
    
    public SignupPage inputCountryCodeToSearchBox(String country) {
    	commonAction.getElement(COUNTRY_SEARCHBOX).sendKeys(country);
    	logger.info("Input Country code: " + country);
    	return this;
    }    
    
    public SignupPage selectCountryCodeFromSearchBox(String country) {
    	clickCountryCodeField();
    	clickMagnifierIcon();
    	inputCountryCodeToSearchBox(country);
    	
    	for (int i=0; i<6; i++) {
    		if (commonAction.getText(COUNTRY_SEARCHRESULT).contentEquals(country)) break;
    		commonAction.sleepInMiliSecond(500);
    	}
    	
    	commonAction.clickElement(COUNTRY_SEARCHRESULT);
    	logger.info("Selected country: " + country);
    	return this;
    }        
    
    public SignupPage clickUsername() {
    	commonAction.clickElement(new ByChained(USERNAME, TEXTBOX));
    	logger.info("Clicked on Username field.");
    	return this;
    }
    
    public SignupPage inputUsername(String username) {
    	commonAction.inputText(new ByChained(USERNAME, TEXTBOX), username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public SignupPage inputPassword(String password) {
    	commonAction.inputText(new ByChained(PASSWORD, TEXTBOX), password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }
    
    public SignupPage inputDisplayName(String name) {
    	commonAction.inputText(new ByChained(DISPLAYNAME, TEXTBOX), name);
    	logger.info("Input '" + name + "' into Name field.");
    	return this;
    }
    
    public SignupPage inputBirthday(String birthday) {
    	commonAction.inputText(new ByChained(BIRTHDAY, TEXTBOX), birthday);
    	logger.info("Input '" + birthday + "' into Birthday field.");
    	return this;
    }
    
    public SignupPage clickAgreeTermBtn() {
    	commonAction.clickElement(TERM_CHK);
    	logger.info("Clicked on 'Term Condition' check box.");
    	return this;
    }
    
    public boolean isContinueBtnEnabled() {
    	boolean isEnabled = commonAction.isElementEnabled(CONTINUE_BTN);
    	logger.info("Is 'Continue' button enabled: " + isEnabled);
    	return isEnabled;
    }

    public SignupPage clickContinueBtn() {
    	commonAction.clickElement(CONTINUE_BTN);
    	logger.info("Clicked on Continue button.");
        return this;
    }

    public String getUsernameError() {
    	String text = commonAction.getText(USERNAME_ERROR);
    	logger.info("Retrieved error for username field: " + text);
    	return text;
    }    
    
    public SignupPage inputVerificationCode(String code) {
    	commonAction.inputText(new ByChained(VERIFICATIONCODE, TEXTBOX), code);
    	logger.info("Input '" + code + "' into Username field.");
        return this;
    }    

    public SignupPage clickResendBtn() {
    	commonAction.clickElement(RESEND_BTN);
    	logger.info("Clicked on Resend button.");
        return this;
    }    
    
    public SignupPage clickVerifyBtn() {
    	commonAction.clickElement(VERIFY_BTN);
    	logger.info("Clicked on Verify button.");
    	return this;
    }    

    public String getVerificationCodeError() {
    	String text = commonAction.getText(new ByChained(VERIFICATIONCODE, By.xpath("//*[contains(@class,'TextView')]")));
    	logger.info("Retrieved error for verification field: " + text);
    	return text;
    }    
    
    public String getToastMessage() {
    	String text = commonAction.getText(TOASTMESSAGE);
    	logger.info("Retrieved toast message: " + text);
    	return text;
    }    
    
}
