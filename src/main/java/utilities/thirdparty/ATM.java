package utilities.thirdparty;

import static utilities.account.AccountTest.*;

import java.time.Duration;

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

public class ATM {

	final static Logger logger = LogManager.getLogger(ATM.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public ATM(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "[id='accordionList'] [aria-controls='accordionList2']")
	WebElement DOMESTIC_CARD;
	
	@FindBy(id = "card_number_mask")
	WebElement CARD_NUMBER;
	
	@FindBy(id = "cardHolder")
	WebElement CARD_HOLDER;
	
	@FindBy(id = "cardDate")
	WebElement CARD_DATE;
	
	@FindBy(id = "btnContinue")
	WebElement CONTINUE_BTN;
	
	@FindBy(id = "otpvalue")
	WebElement OTP;
	
	@FindBy(id = "btnConfirm")
	WebElement CONFIRM_BTN;
	
	public ATM selectBank(String bank) {
    	if (bank.contentEquals("NCB")) {
    		commonAction.clickElement(wait.until(ExpectedConditions.presenceOfElementLocated(By.id(bank))));
    	} else if (bank.contentEquals("VIETCOMBANK")) {
    		commonAction.clickElement(driver.findElement(By.id(bank)));
    	} else {
    		logger.error("Unsupported bank: " + bank);
    		return this;
    	}
		logger.info("Selected bank: " + bank);
		return this;
	}
	
	public ATM inputCardNumber(String number) {
		commonAction.inputText(CARD_NUMBER, number);
		logger.info("Input '" + number + "' into Card Number field.");
		return this;
	}
	
	public ATM inputCardHolder(String name) {
		commonAction.inputText(CARD_HOLDER, name);
		logger.info("Input '" + name + "' into Card Holder field.");
		return this;
	}
	
	public ATM inputIssuingDate(String date) {
		commonAction.inputText(CARD_DATE, date);
		logger.info("Input '" + date + "' into Issuing Date field.");
		return this;
	}

	public ATM clickContinue() {
		commonAction.clickElement(CONTINUE_BTN);
		logger.info("Clicked on 'Continue' button.");
		return this;
	}	
	
	public ATM inputOTP(String OTPValue) {
		commonAction.inputText(OTP, OTPValue);
		logger.info("Input '" + OTPValue + "' into OTP field.");
		return this;
	}	
	
	public ATM clickConfirm() {
		commonAction.clickElement(CONFIRM_BTN);
		logger.info("Clicked on 'Confirm' button.");
		return this;
	}	
	
	public ATM completePayment() {
		selectBank(ATM_BANK);
		inputCardNumber(ATM_CARDNUMBER);
		inputCardHolder(ATM_CARDHOLDER);
		inputIssuingDate(ATM_ISSUINGDATE);
		clickContinue();
		inputOTP(ATM_OTP);
		clickConfirm();
		return this;
	}	
	
	public ATM completePayment(String bank, String cardNumber, String cardHolder, String issuingDate, String otp) {
		selectBank(bank);
		inputCardNumber(cardNumber);
		inputCardHolder(cardHolder);
		inputIssuingDate(issuingDate);
		clickContinue();
		inputOTP(otp);
		clickConfirm();
		return this;
	}		
	
}
