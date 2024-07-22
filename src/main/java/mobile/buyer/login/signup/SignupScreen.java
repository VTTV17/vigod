package mobile.buyer.login.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAndroid;

import java.time.Duration;

public class SignupScreen extends SignupElement{

	final static Logger logger = LogManager.getLogger(SignupScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAndroid commonAndroid;

    public SignupScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAndroid = new UICommonAndroid(driver);
    }

    public SignupScreen clickMailTab() {
    	commonAndroid.click(loc_tabMail);
    	logger.info("Clicked on Mail tab.");
    	return this;
    }
    
    public SignupScreen clickPhoneTab() {
    	commonAndroid.click(loc_tabPhone);
    	logger.info("Clicked on Phone tab.");
    	return this;
    }

    public SignupScreen clickCountryCodeField() {
    	commonAndroid.click(loc_ddvCountryCode);
    	logger.info("Clicked on Country code field.");
        return this;
    }    
    
    public SignupScreen clickMagnifierIcon() {
    	commonAndroid.click(loc_icnSearch);
    	logger.info("Clicked on Magnifier icon.");
    	return this;
    }    
    
    public SignupScreen inputCountryCodeToSearchBox(String country) {
    	commonAndroid.getElement(loc_txtCountrySearchBox).sendKeys(country);
        logger.info("Input Country code: {}", country);
    	return this;
    }    
    
    public SignupScreen selectCountryCodeFromSearchBox(String country) {
    	clickCountryCodeField();
    	clickMagnifierIcon();
    	inputCountryCodeToSearchBox(country);
    	
    	for (int i=0; i<6; i++) {
    		if (commonAndroid.getText(loc_lstSearchResult).contentEquals(country)) break;
    	}
    	
    	commonAndroid.click(loc_lstSearchResult);
    	
    	//Sometimes the element is still present. The code below helps handle this intermittent issue
    	boolean isElementPresent = true;
    	for (int i=0; i<3; i++) {
    		if (commonAndroid.getListElement(loc_lstSearchResult).isEmpty()) {
    			isElementPresent = false;
    			break;
    		}
    	}
    	if (isElementPresent) {
    		commonAndroid.click(loc_lstSearchResult);
    	}

        logger.info("Selected country: {}", country);
    	return this;
    }        
    
    public SignupScreen clickUsername() {
    	commonAndroid.click(new ByChained(loc_txtUserName, loc_txt));
    	logger.info("Clicked on Username field.");
    	return this;
    }
    
    public SignupScreen inputUsername(String username) {
    	commonAndroid.sendKeys(new ByChained(loc_txtUserName, loc_txt), username);
        logger.info("Input '{}' into Username field.", username);
        return this;
    }
    
    public String getUsernameFieldValue() {
    	String text = commonAndroid.getText(new ByChained(loc_txtUserName, loc_txt));
        logger.info("Retrieved Username field value: {}", text);
    	return text;
    }    
    
    public SignupScreen inputPassword(String password) {
    	commonAndroid.sendKeys(new ByChained(loc_txtPassword, loc_txt), password);
        logger.info("Input '{}' into Password field.", password);
        return this;
    }
    
    public SignupScreen inputDisplayName(String name) {
    	commonAndroid.sendKeys(new ByChained(loc_txtDisplayName, loc_txt), name);
        logger.info("Input '{}' into Name field.", name);
    	return this;
    }
    
    /**
     * Simply clicks on birthday field
     * @return
     */
    public SignupScreen clickBirthday() {
    	commonAndroid.click(loc_txtBirthday);
    	logger.info("Clicked on Birthday field.");
    	return this;
    }
    
    /**
     * Clicks on the OK button on birthday date picker dialog
     * @return current object
     */
    public SignupScreen clickBirthdayOKBtn() {
    	commonAndroid.click(loc_btnBirthdayOK);
    	logger.info("Clicked on OK button on Birthday date picker dialog.");
    	return this;
    }
    
    public SignupScreen inputBirthday(String birthday) {
    	commonAndroid.sendKeys(new ByChained(loc_txtBirthday, loc_txt), birthday);
        logger.info("Input '{}' into Birthday field.", birthday);
    	return this;
    }

	public String getBirthday() {
		String value = commonAndroid.getText(new ByChained(loc_txtBirthday, loc_txt));
        logger.info("Retrieved Birthday: {}", value);
		return value;
	}   
    
    public SignupScreen clickAgreeTermBtn() {
    	commonAndroid.click(loc_chkTermOfUse);
    	logger.info("Clicked on 'Term Condition' check box.");
    	return this;
    }
    
    public boolean isContinueBtnEnabled() {
    	boolean isEnabled = commonAndroid.isEnabled(btnContinue);
        logger.info("Is 'Continue' button enabled: {}", isEnabled);
    	return isEnabled;
    }

    public SignupScreen clickContinueBtn() {
    	commonAndroid.click(btnContinue);
    	logger.info("Clicked on Continue button.");
        return this;
    }

    public String getUsernameError() {
    	String text = commonAndroid.getText(loc_lblError);
        logger.info("Retrieved error for username field: {}", text);
    	return text;
    }    
    
    public SignupScreen inputVerificationCode(String code) {
    	commonAndroid.sendKeys(new ByChained(loc_txtVerificationCode, loc_txt), code);
        logger.info("Input '{}' into Verification field.", code);
        return this;
    }    

    public SignupScreen clickResendBtn() {
    	commonAndroid.click(loc_btnResend);
    	logger.info("Clicked on Resend button.");
        return this;
    }    
    
    public SignupScreen clickVerifyBtn() {
    	commonAndroid.click(loc_btnVerify);
    	logger.info("Clicked on Verify button.");
    	return this;
    }    

    public String getVerificationCodeError() {
		// Sometimes it takes longer for the error to appear
    	String text = commonAndroid.getText(new ByChained(loc_txtVerificationCode, By.xpath("//*[contains(@class,'TextView')]")));
        logger.info("Retrieved error for verification field: {}", text);
    	return text;
    }    
    
}
