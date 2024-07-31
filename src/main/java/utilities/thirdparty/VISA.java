package utilities.thirdparty;

import static utilities.account.AccountTest.VISA_ADDRESS;
import static utilities.account.AccountTest.VISA_CARDHOLDER;
import static utilities.account.AccountTest.VISA_CARDNUMBER;
import static utilities.account.AccountTest.VISA_CCV;
import static utilities.account.AccountTest.VISA_CITY;
import static utilities.account.AccountTest.VISA_COUNTRY;
import static utilities.account.AccountTest.VISA_EMAIL;
import static utilities.account.AccountTest.VISA_EXPIRYDATE;
import static utilities.account.AccountTest.VISA_OTP;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
		commonAction = new UICommonAction(driver);
	}

	//Will move these locators to a separate file
	By loc_btnVISA = By.id("VISA");
	By loc_btnCancel = By.cssSelector("[data-bs-target='#modalCancelPayment'] .ubtn-text");
	By loc_btnConfirmAbortPayment = By.cssSelector("#modalCancelPayment [onclick='cancelConfirm()']");
	By loc_txtCardNumber = By.id("card_number_mask");
	By loc_txtCardDate = By.id("cardDate");
	By loc_txtCCV = By.id("cvcCvv");
	By loc_txtCardHolder = By.id("card_holder_name");
	By loc_txtEmail = By.id("email");
	By loc_ddlCountry = By.id("ddlCountry");
	By loc_txtCity = By.id("city");
	By loc_txtAddress = By.id("address");
	By loc_btnContinue = By.id("btnSubmit");
	By loc_btnAgree = By.cssSelector(".modal-footer #btnAgree");
	By loc_btnNext = By.cssSelector("input[value='NEXT']");
	By loc_txtOTP = By.cssSelector("[name='challengeDataEntry']");
	By loc_btnConfirm = By.cssSelector("input[value='SUBMIT']");

	public VISA clickVISABtn() {
		commonAction.click(loc_btnVISA);
		logger.info("Clicked 'VISA' button.");
		return this;
	}	

	public VISA clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button.");
		return this;
	}		
	public VISA clickConfirmCancelPaymentBtn() {
		commonAction.click(loc_btnConfirmAbortPayment);
		logger.info("Clicked on 'Confirm' button to abort payment process.");
		return this;
	}		
	
	public VISA inputCardNumber(String number) {
		commonAction.inputText(loc_txtCardNumber, number);
		logger.info("Input Card Number: {}", number);
		return this;
	}

	public VISA inputExpiryDate(String date) {
		commonAction.inputText(loc_txtCardDate, date);
		logger.info("Input Expiry Date: {}", date);
		return this;
	}

	public VISA inputCVCCVV(String cvccvv) {
		commonAction.inputText(loc_txtCCV, cvccvv);
		logger.info("Input CVC/CVV: {}", cvccvv);
		return this;
	}

	public VISA inputCardHolder(String name) {
		commonAction.inputText(loc_txtCardHolder, name);
		logger.info("Input Full Name: {}", name);
		return this;
	}

	public VISA inputEmail(String email) {
		commonAction.inputText(loc_txtEmail, email);
		logger.info("Input Email: {}", email);
		return this;
	}

	public VISA selectCountry(String country) {
		commonAction.selectByVisibleText(loc_ddlCountry, country);
		logger.info("Selected country: {}", country);
		return this;
	}

	public VISA inputProvinceCity(String city) {
		commonAction.inputText(loc_txtCity, city);
		logger.info("Input Provice/City: {}", city);
		return this;
	}

	public VISA inputAddress(String address) {
		commonAction.inputText(loc_txtAddress, address);
		logger.info("Input Address: {}", address);
		return this;
	}

	public VISA clickContinue() {
		commonAction.click(loc_btnContinue);
		logger.info("Clicked 'Continue' button.");
		return this;
	}
	
	public VISA clickAgreeToConditionBtn() {
		commonAction.click(loc_btnAgree);
		logger.info("Clicked 'Agree and Continue' button.");
		return this;
	}
	
	public VISA inputOTP(String OTPValue) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("step-up-iframe")));
		commonAction.switchToFrameByElement(iframe);
		commonAction.switchToFrameByIndex(0);
		commonAction.inputText(loc_txtOTP, OTPValue);
		logger.info("Input OTP: {}", OTPValue);
		return this;
	}

	public VISA clickSubmit() {
		commonAction.click(loc_btnConfirm);
		logger.info("Clicked 'Submit' button.");
		return this;
	}

	public VISA completePayment() {
		completePayment(VISA_CARDNUMBER, VISA_EXPIRYDATE, VISA_CCV, VISA_CARDHOLDER, VISA_EMAIL, VISA_COUNTRY, VISA_CITY, VISA_ADDRESS, VISA_OTP);
		return this;
	}
	
	public VISA completePayment(String cardNumber, String expiryDate, String cvccvv, String cardHolder, String email, String country, String city, String address, String otp) {
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
		clickAgreeToConditionBtn();
		inputOTP(otp);
		clickSubmit();
		return this;
	}

	public VISA abandonPayment() {
		clickVISABtn();
		clickCancelBtn();
		clickConfirmCancelPaymentBtn();
		return this;
	}		
	
}
