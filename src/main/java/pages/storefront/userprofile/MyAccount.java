package pages.storefront.userprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

import java.time.Duration;

public class MyAccount {
	
	final static Logger logger = LogManager.getLogger(MyAccount.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public MyAccount (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "displayName")
    WebElement DISPLAY_NAME; 
    
    @FindBy(id = "email")
    WebElement EMAIL;
    
    @FindBy(id = "country-code")
    WebElement COUNTRY_CODE;
    
    @FindBy(id = "phone")
    WebElement PHONE;
    
    @FindBy(id = "dob")
    WebElement birthday; 
    
    @FindBy(id = "verify-password")
    WebElement PASSWORD_FORGOT_TXTBOX;

    
    public String getDisplayName() {
    	String value = commonAction.getElementAttribute(DISPLAY_NAME, "value");
    	logger.info("Retrieved Display Name: " + value);
        return value;
    }
    
    public String getEmail() {
    	String value = commonAction.getElementAttribute(EMAIL, "value");
    	logger.info("Retrieved Mail: " + value);
    	return value;
    }
    
    public String getPhoneNumber() {
    	String countryCode = commonAction.getElementAttribute(COUNTRY_CODE, "value");
    	String phoneNumber = commonAction.getElementAttribute(PHONE, "value");
    	String value = countryCode + ":" + phoneNumber;
    	logger.info("Retrieved phone number prefixed with country code: " + value);
    	return value;
    }

    public String getBirthday() {
    	String value = commonAction.getElementAttribute(birthday, "value");
    	logger.info("Retrieved Birthday: " + value);
    	return value;
    }    
    
}
