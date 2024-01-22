package utilities.thirdparty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import utilities.commons.UICommonAction;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mailnesia {

	final static Logger logger = LogManager.getLogger(Mailnesia.class);

	WebDriver driver;
	WebDriverWait wait;

	SoftAssert soft = new SoftAssert();
	UICommonAction common;
	public static String MAILNESIA_DOMAIN = "mailnesia.com";

	public Mailnesia(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		common = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".emailheader")
	List<WebElement> EMAILS;

	public Mailnesia navigate(String mail) {
		String name = mail.split("@")[0];
		driver.get("https://mailnesia.com/mailbox/" + name);
		return this;
	}

	public String getVerificationCode() {
		String code = null;
		// Refresh page till a code is found
		String [][] mailContent;
		for (int i = 0; i < 30; i++) {
			mailContent = getListOfEmailHeaders();
			if (mailContent.length >0 && (mailContent[0][0].contains("a few seconds"))) {
				Matcher m = Pattern.compile("\\d+").matcher(mailContent[0][3]); //Element at [0][3] contains the verification code.
				if (m.find()) {
					code = m.group();
				}				
			}
			if (code != null) break;
			common.sleepInMiliSecond(3000);
			common.refreshPage();
		}
		logger.info("Verification Code retrieved: " + code);
		return code;
	}

	public String navigateToMailAndGetVerifyCode(String userName) {
		common.openNewTab();
		common.switchToWindow(1);
		String verificationCode = navigate(userName).getVerificationCode();
		common.closeTab();
		common.switchToWindow(0);
		return verificationCode;
	}

	/**
	 * <p>
	 * To retrieve headers of mails in mailbox and return them in form of a
	 * 2-dimensional array
	 * <p>
	 * The headers are Date, From, To, Subject
	 * 
	 * @return a 2-dimensional array containing headers of mails in mailbox
	 */

	public String[][] getListOfEmailHeaders() {
		int emailCount = EMAILS.size();
		String[][] mailContent = new String[emailCount][4]; //The 5th column displays a symbol so we exclude it.
		for (int row = 0; row < emailCount; row++) {
			List<WebElement> headerSegments = EMAILS.get(row).findElements(By.xpath("./td"));
			for (int column = 0; column < headerSegments.size() - 1; column++) { //The 5th column displays a symbol so we exclude it.
				mailContent[row][column] = headerSegments.get(column).getText();
			}
			logger.info(Arrays.toString(mailContent[row]));
		}
		return mailContent;
	}

}
