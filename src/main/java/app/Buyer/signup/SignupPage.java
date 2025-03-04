package app.Buyer.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;

import utilities.commons.UICommonMobile;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);

    WebDriver driver;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public SignupPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonMobile(driver);
    }
    
    By COUNTRYCODE = By.xpath("//*[ends-with(@resource-id,'country_code')]");
    By loc_lblSelectedCountry = By.xpath("//*[ends-with(@resource-id,':id/fragment_social_login_phone_choose_country_name')]");
    By MAGNIFIER = By.xpath("//*[ends-with(@resource-id,'btn_search')]");
    By COUNTRY_SEARCHBOX = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    
    By countrySearchResultByName(String country) {
    	return By.xpath("//*[ends-with(@resource-id,'country_code_list_tv_title') and @text=\"%s\"]".formatted(country));
    }
    
    By MAIL_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[1]");
    By PHONE_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    
    By TEXTBOX = By.xpath("//*[ends-with(@resource-id,'limit_edittext')]");
    
    By USERNAME = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]");
    By DISPLAYNAME = By.xpath("//*[ends-with(@resource-id,'displayName')]");
    By BIRTHDAY = By.xpath("//*[ends-with(@resource-id,'birthday')]");
    By BIRTHDAY_OK_BTN = By.xpath("//*[ends-with(@resource-id,'ok')]");
    
    By TERM_CHK = By.xpath("//*[ends-with(@resource-id,'btn_check_term_and_policy')]");
    public static By loc_btnContinue = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    
    public static By loc_lblUsernameError = By.xpath("//*[contains(@resource-id,'error')]");
    
    By VERIFICATIONCODE = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]");
    By RESEND_BTN = By.xpath("//*[ends-with(@resource-id,'verify_code_resend_action')]");
    By VERIFY_BTN = By.xpath("//*[ends-with(@resource-id,'verify_code_action')]");

    public SignupPage clickMailTab() {
    	commonAction.clickElement(MAIL_TAB);
    	logger.info("Clicked Mail tab.");
    	return this;
    }
    
    public SignupPage clickPhoneTab() {
    	commonAction.clickElement(PHONE_TAB);
    	logger.info("Clicked Phone tab.");
    	return this;
    }

    String getCurrentlySelectedCountry(){
    	var country = commonAction.getText(loc_lblSelectedCountry);
    	logger.info("Retrieved selected country: {}", country);
    	return country;
    }    
    
    public SignupPage clickCountryCodeField() {
    	commonAction.clickElement(COUNTRYCODE);
    	logger.info("Clicked Country code field.");
        return this;
    }    
    public SignupPage clickMagnifierIcon() {
    	commonAction.clickElement(MAGNIFIER);
    	logger.info("Clicked on Magnifier icon.");
    	return this;
    }    
    public SignupPage inputCountryCodeToSearchBox(String country) {
    	commonAction.inputText(COUNTRY_SEARCHBOX, country);
    	logger.info("Input Country code: " + country);
    	//TODO: There's a delay of approximately 2s after inputting country name. The devs deliberately made it. Will do something about it
    	UICommonMobile.sleepInMiliSecond(2000, "after inputing country name just like a real user would");
    	return this;
    }    
    public SignupPage selectCountryCodeFromSearchBox(String country) {
    	commonAction.clickElement(countrySearchResultByName(country));
    	logger.info("Selected country: " + country);
    	return this;
    }        
    public SignupPage selectCountry(String country) {
    	if (getCurrentlySelectedCountry().contentEquals(country)) return this;
    	
    	clickCountryCodeField().clickMagnifierIcon().inputCountryCodeToSearchBox(country).selectCountryCodeFromSearchBox(country);
    	commonAction.hideKeyboard();
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
    
    public String getUsernameFieldValue() {
    	String text = commonAction.getText(new ByChained(USERNAME, TEXTBOX));
    	logger.info("Retrieved Username field value: " + text);
    	return text;
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
    
    /**
     * Simply clicks on birthday field
     * @return
     */
    public SignupPage clickBirthday() {
    	commonAction.clickElement(BIRTHDAY);
    	logger.info("Clicked on Birthday field.");
    	return this;
    }
    
    /**
     * Clicks on the OK button on birthday date picker dialog
     * @return current object
     */
    public SignupPage clickBirthdayOKBtn() {
    	commonAction.clickElement(BIRTHDAY_OK_BTN);
    	logger.info("Clicked on OK button on Birthday date picker dialog.");
    	return this;
    }
    
    public SignupPage inputBirthday(String birthday) {
    	commonAction.inputText(new ByChained(BIRTHDAY, TEXTBOX), birthday);
    	logger.info("Input '" + birthday + "' into Birthday field.");
    	return this;
    }

	public String getBirthday() {
		String value = commonAction.getText(new ByChained(BIRTHDAY, TEXTBOX));
		logger.info("Retrieved Birthday: " + value);
		return value;
	}   
    
    public SignupPage clickAgreeTermBtn() {
    	commonAction.clickElement(TERM_CHK);
    	logger.info("Clicked on 'Term Condition' check box.");
    	return this;
    }
    
    public boolean isContinueBtnEnabled() {
    	boolean isEnabled = commonAction.isElementEnabled(loc_btnContinue);
    	logger.info("Is 'Continue' button enabled: " + isEnabled);
    	return isEnabled;
    }

    public SignupPage clickContinueBtn() {
    	commonAction.clickElement(loc_btnContinue);
    	logger.info("Clicked on Continue button.");
        return this;
    }

    public String getUsernameError() {
    	String text = commonAction.getText(loc_lblUsernameError);
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
    	UICommonMobile.sleepInMiliSecond(1500); // Sometimes it takes longer for the error to appear
    	String text = commonAction.getText(new ByChained(VERIFICATIONCODE, By.xpath("//*[contains(@class,'TextView')]")));
    	logger.info("Retrieved error for verification field: " + text);
    	return text;
    }    
    
}
