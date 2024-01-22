package utilities.thirdparty;

import java.time.Duration;

import static utilities.account.AccountTest.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class VISA {

	final static Logger logger = LogManager.getLogger(VISA.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public VISA(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "VISA")
	WebElement VISA_BTN;
	
	@FindBy(id = "card_number_mask")
	WebElement CARD_NUMBER;

	@FindBy(id = "cardDate")
	WebElement CARD_DATE;

	@FindBy(id = "cvcCvv")
	WebElement CVC_CVV;

	@FindBy(id = "card_holder_name")
	WebElement CARD_HOLDER;

	@FindBy(id = "email")
	WebElement EMAIL;

	@FindBy(id = "ddlCountry")
	WebElement COUNTRY;

	@FindBy(id = "city")
	WebElement CITY;

	@FindBy(id = "address")
	WebElement ADDRESS;

	@FindBy(id = "btnSubmit")
	WebElement CONTINUE_BTN;
	
	@FindBy(css = "input[value='NEXT']")
	WebElement NEXT_BTN;

	@FindBy(css = "[name='challengeDataEntry']")
	WebElement OTP;

	@FindBy(css = "input[value='SUBMIT']")
	WebElement CONFIRM_BTN;

	public VISA clickVISABtn() {
		commonAction.clickElement(VISA_BTN);
		logger.info("Clicked on 'VISA' button.");
		return this;
	}	
	
	public VISA inputCardNumber(String number) {
		commonAction.inputText(CARD_NUMBER, number);
		logger.info("Input '" + number + "' into Card Number field.");
		return this;
	}

	public VISA inputExpiryDate(String date) {
		commonAction.inputText(CARD_DATE, date);
		logger.info("Input '" + date + "' into Expiry Date field.");
		return this;
	}

	public VISA inputCVCCVV(String cvccvv) {
		commonAction.inputText(CVC_CVV, cvccvv);
		logger.info("Input '" + cvccvv + "' into CVC/CVV field.");
		return this;
	}

	public VISA inputCardHolder(String name) {
		commonAction.inputText(CARD_HOLDER, name);
		logger.info("Input '" + name + "' into Full Name field.");
		return this;
	}

	public VISA inputEmail(String email) {
		commonAction.inputText(EMAIL, email);
		logger.info("Input '" + email + "' into Email field.");
		return this;
	}

	public VISA selectCountry(String country) {
		String selectedOption = commonAction.selectByVisibleText(COUNTRY, country);
		logger.info("Selected country: " + selectedOption);
		return this;
	}

	public VISA inputProvinceCity(String city) {
		commonAction.inputText(CITY, city);
		logger.info("Input '" + city + "' into Provice/City field.");
		return this;
	}

	public VISA inputAddress(String address) {
		commonAction.inputText(ADDRESS, address);
		logger.info("Input '" + address + "' into Address field.");
		return this;
	}

	public VISA clickContinue() {
		commonAction.clickElement(CONTINUE_BTN);
		logger.info("Clicked on 'Continue' button.");
		return this;
	}
	
	public VISA clickNext() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("step-up-iframe")));
		commonAction.switchToFrameByElement(iframe);
		commonAction.switchToFrameByIndex(0);
		commonAction.clickElement(NEXT_BTN);
		logger.info("Clicked on 'Next' button.");
		return this;
	}

	public VISA inputOTP(String OTPValue) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("step-up-iframe")));
		commonAction.switchToFrameByElement(iframe);
		commonAction.switchToFrameByIndex(0);
		commonAction.inputText(OTP, OTPValue);
		logger.info("Input '" + OTPValue + "' into OTP field.");
		return this;
	}

	public VISA clickSubmit() {
		commonAction.clickElement(CONFIRM_BTN);
		logger.info("Clicked on 'Submit' button.");
		return this;
	}

	public VISA completePayment() {
		clickVISABtn();
		inputCardNumber(VISA_CARDNUMBER);
		inputExpiryDate(VISA_EXPIRYDATE);
		inputCVCCVV(VISA_CCV);
		inputCardHolder(VISA_CARDHOLDER);
		inputEmail(VISA_EMAIL);
		selectCountry(VISA_COUNTRY);
		inputProvinceCity(VISA_CITY);
		inputAddress(VISA_ADDRESS);
		clickContinue();
//		clickNext();
		inputOTP(VISA_OTP);
		clickSubmit();
		return this;
	}
	
	public VISA completePayment(String cardNumber, String expiryDate, String cvccvv, String cardHolder, String email,
			String country, String city, String address, String otp) {
		clickVISABtn();
		inputCardNumber(cardNumber);
		inputExpiryDate(expiryDate);
		inputCVCCVV(cvccvv);
		inputCardHolder(cardHolder);
		inputEmail(email);
		selectCountry(country);
		inputProvinceCity(city);
		inputAddress(address);
		clickContinue();
//		clickNext();
		inputOTP(otp);
		clickSubmit();
		return this;
	}

}
