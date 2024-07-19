package utilities.thirdparty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utilities.commons.UICommonAction;

public class Mailnesia {

	final static Logger logger = LogManager.getLogger(Mailnesia.class);

	WebDriver driver;
	UICommonAction common;
	
	By loc_tblEmailRows = By.className("emailheader");
	By loc_tblEmailColumns = By.xpath("./td");
	
	String loc_tblSpecificCell = "//tr[starts-with(@class,'emailheader') and position()='%s']/td[%s]";
	
	By loc_tblSubjectVerificationCode = By.xpath("//tr[starts-with(@class,'emailheader') and not(@style='display: none;')]/td[4]/self::*[contains(.,'là mã xác minh tài khoản GoSell của bạn') or contains(.,\"is your GoSell's verification code\")]");
	By loc_tblSubjectSuccessfulRegistration = By.xpath("//tr[starts-with(@class,'emailheader') and not(@style='display: none;')]/td[4]/self::*[.='Đăng ký thành công tài khoản GoSell' or .='Successful GoSell registration']");
	By loc_tblSubjectWelcome = By.xpath("//tr[starts-with(@class,'emailheader') and not(@style='display: none;')]/td[4]/self::*[.='Chào mừng bạn đến với GoSell' or .='Welcome to GoSell']");
	By loc_tblSubjecPaymentConfirmation = By.xpath("//tr[starts-with(@class,'emailheader') and not(@style='display: none;')]/td[4]/self::*[.='Xác nhận thông tin thanh toán' or .='Confirmation of information payment']");
	By loc_tblSubjecSuccessfulPayment = By.xpath("//tr[starts-with(@class,'emailheader') and not(@style='display: none;')]/td[4]/self::*[.='Xác nhận thanh toán thành công' or .='Successful payment confirmation']");
	
	By emailBody = By.xpath("//tr[starts-with(@id,'emailbody') and not(@style='display: none;')]//div[starts-with(@id,'text_plain')]");
	
	public Mailnesia(WebDriver driver) {
		this.driver = driver;
		common = new UICommonAction(driver);
	}

	public enum MailType {
		VERIFICATION_CODE, ACCOUNT_REGISTRATION, WELCOME, PAYMENT_CONFIRMATION, SUCCESSFUL_PAYMENT;
	}
	
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
	//Will soon be superseded by the function right below it
	public String[][] getListOfEmailHeaders() {
		int emailCount = common.getElements(loc_tblEmailRows).size();
		String[][] mailContent = new String[emailCount][4]; //The 5th column displays a symbol so we exclude it.
		for (int row = 0; row < emailCount; row++) {
			List<WebElement> headerSegments = common.getElement(loc_tblEmailRows, row).findElements(By.xpath("./td"));
			for (int column = 0; column < headerSegments.size() - 1; column++) { //The 5th column displays a symbol so we exclude it.
				mailContent[row][column] = headerSegments.get(column).getText();
			}
			logger.info(Arrays.toString(mailContent[row]));
		}
		return mailContent;
	}
	public List<List<String>> getListOfEmailHeadersExp() {
		List<List<String>> tableOfContent = new ArrayList<>();
		for (int row = 1; row <= common.getElements(loc_tblEmailRows).size(); row++) {
			List<String> rowOfcontent = new ArrayList<>();
			for (int column=1; column<=4; column++) {  //The 5th column displays a symbol so we exclude it.
				rowOfcontent.add(common.getText(By.xpath(loc_tblSpecificCell.formatted(row, column))));
			}
			tableOfContent.add(rowOfcontent);
		}
		logger.info(tableOfContent);
		return tableOfContent;
	}
	
	public void clickEmail(MailType mailType) {
		switch (mailType) {
		case VERIFICATION_CODE: {
			common.click(loc_tblSubjectVerificationCode); break;
		}
		case ACCOUNT_REGISTRATION: {
			common.click(loc_tblSubjectSuccessfulRegistration); break;
		}
		case WELCOME: {
			common.click(loc_tblSubjectWelcome); break;
		}
		case PAYMENT_CONFIRMATION: {
			common.click(loc_tblSubjecPaymentConfirmation); break;
		}
		case SUCCESSFUL_PAYMENT: {
			common.click(loc_tblSubjecSuccessfulPayment); break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + mailType);
		}
		common.sleepInMiliSecond(700, "Wait in clickEmail()");
	}
	public String getEmailBody(MailType mailType) {
		
		clickEmail(mailType);
		
		String text = common.getText(emailBody);
		
		clickEmail(mailType);
		
		return text;
	}

}
