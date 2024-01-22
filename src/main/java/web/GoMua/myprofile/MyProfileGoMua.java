package web.GoMua.myprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;

public class MyProfileGoMua {
	final static Logger logger = LogManager.getLogger(MyProfileGoMua.class);
	WebDriver driver;
	UICommonAction common;
	MyProfileGoMuaElement myProfileUI;

	public MyProfileGoMua(WebDriver driver) {
		this.driver = driver;
		common = new UICommonAction(driver);
		myProfileUI = new MyProfileGoMuaElement();
	}

	public MyProfileGoMua clickOnEditProfile() {
		common.click(myProfileUI.loc_lnkEditProfile);
		logger.info("Click on Edit profile");
		return this;
	}

	public String getDisplayName() {
		String displayname = common.getText(myProfileUI.loc_lblDisplayName);
		logger.info("Get display name: %s".formatted(displayname));
		return displayname;
	}

	/**
	 * <p>
	 * To retrieve email of customers
	 * <p>
	 * @return the customer's email address or "" in case the customers do not obtain an email account
	 */	
	public String getEmail() {
		String email;
		if (common.getElements(myProfileUI.loc_lblEmail).size() <1) { // The element won't be present in some cases
			email = "";
		} else {
			email = common.getText(myProfileUI.loc_lblEmail, 0);
		}
		logger.info("Retrieved email: %s".formatted(email));
		return email;
	}

	/**
	 * <p>
	 * To retrieve phone of customers
	 * <p>
	 * @return a phone number with a country code or "" in case the customers do not obtain one. 
	 */	
	public String getPhoneNumber() {
		String phoneNumber;
		if (common.getElements(myProfileUI.loc_btnPhone).size() <1) { // The element won't be present in some cases
			phoneNumber = "";
		} else {
			phoneNumber = common.getText(myProfileUI.loc_btnPhone, 0);
		}
		logger.info("Get phone number: %s".formatted(phoneNumber));
		return phoneNumber;
	}

	/**
	 * <p>
	 * To retrieve gender of customers
	 * <p>
	 * @return the customer's gender or "" in case the customers have not selected gender when signing up
	 */	
	public String getGender() {
		String gender;
		if (common.getElements(myProfileUI.loc_lblGender).size() <1) { // The element won't be present in some cases
			gender = "";
		} else {
			gender = common.getText(myProfileUI.loc_lblGender, 0);
		}
		logger.info("Get gender: %s".formatted(gender));
		return gender;
	}

	/**
	 * <p>
	 * To retrieve birthday of customers
	 * <p>
	 * @return the customer's birthday or "" in case birthday field is left empty during sign-up procedure
	 */	
	public String getBirthday() {
		String birthday;
		if (common.getElements(myProfileUI.loc_lblBirthday).size() <1) { // The element won't be present in some cases
			birthday = "";
		} else {
			birthday = common.getText(myProfileUI.loc_lblBirthday, 0);
		}
		logger.info("Retrieved birthday: %s".formatted(birthday));
		return birthday;
	}

	public MyProfileGoMua verifyDisplayName(String expected) {
		Assert.assertEquals(getDisplayName(), expected);
		logger.info("Display name is updated");
		return this;
	}

	public MyProfileGoMua verifyPhoneNumber(String expected) {
		String updateFormatPhoneExpected = String.join("", expected.split(":"));
		Assert.assertEquals(getPhoneNumber(), updateFormatPhoneExpected);
		logger.info("Phone number is updated");
		return this;
	}

	public MyProfileGoMua verifyGender(String expected) {
		Assert.assertEquals(getGender(), expected);
		logger.info("Gender is updated");
		return this;
	}

}
