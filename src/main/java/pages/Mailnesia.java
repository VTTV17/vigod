package pages;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mailnesia {
	
	final static Logger logger = LogManager.getLogger(Mailnesia.class);
	
    WebDriver driver;
    WebDriverWait wait;
    
    SoftAssert soft = new SoftAssert();
    UICommonAction common;
    public static String MAILNESIA_DOMAIN= "mailnesia.com";
    public Mailnesia (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//tr[@class=\"emailheader\"][1]/td[4]")
    WebElement EMAIL;
    
    public Mailnesia navigate(String mail) {
    	String name = mail.split("@")[0];
        driver.get("https://mailnesia.com/mailbox/" + name);
        return this;
    }

    public String getVerificationCode() {
    	String code = null;
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(EMAIL.getText());
		if (m.find()) {
			code = m.group();
		}
		logger.info("Verification Code retrieved: " + code);
		return code;
    }    
    public String navigateToMailAndGetVerifyCode(String userName){
        common.sleepInMiliSecond(10000);
        common.openNewTab();
        common.switchToWindow(1);
        String verificationCode = navigate(userName).getVerificationCode();
        common.closeTab();
        common.switchToWindow(0);
        return verificationCode;
    }
}
