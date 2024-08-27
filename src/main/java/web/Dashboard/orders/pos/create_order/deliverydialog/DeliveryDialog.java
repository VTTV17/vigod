package web.Dashboard.orders.pos.create_order.deliverydialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class DeliveryDialog {

	final static Logger logger = LogManager.getLogger(DeliveryDialog.class);

	WebDriver driver;
	UICommonAction commonAction;
	DeliveryDialogElement elements;
	
	public DeliveryDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		elements = new DeliveryDialogElement();
	}

	public DeliveryDialog inputCustomerName(String name) {
		commonAction.inputText(elements.txtCustomerName, name);
		logger.info("Input customer name: {}", name);
		return this;
	}
	public String getCustomerName() {
		String name = commonAction.getAttribute(elements.txtCustomerName, "value");
		logger.info("Retrieved customer name: {}", name);
		return name;
	}
	public DeliveryDialog inputCustomerPhone(String phone) {
		commonAction.inputText(elements.txtCustomerPhone, phone);
		logger.info("Input customer phone: {}", phone);
		return this;
	}
	public String getCustomerPhone() {
		String phone = commonAction.getAttribute(elements.txtCustomerPhone, "value");
		logger.info("Retrieved customer phone: {}", phone);
		return phone;
	}
	public DeliveryDialog inputCustomerEmail(String email) {
		commonAction.inputText(elements.txtCustomerEmail, email);
		logger.info("Input customer email: {}", email);
		return this;
	}
	public String getCustomerEmail() {
		String email = commonAction.getAttribute(elements.txtCustomerEmail, "value");
		logger.info("Retrieved customer email: {}", email);
		return email;
	}
	public DeliveryDialog selectCountry(String country) {
		commonAction.selectByVisibleText(elements.ddlCountry, country);
		logger.info("Selected country: {}", country);
		return this;
	}
	public String getCountry() {
		logger.info("Retrieving country...");
		return commonAction.getDropDownSelectedValue(elements.ddlCountry);
	}	
	public DeliveryDialog inputAddress(String address) {
		commonAction.inputText(elements.txtCustomerAddress, address);
		logger.info("Input address: {}", address);
		return this;
	}
	public String getAddress() {
		String address = commonAction.getAttribute(elements.txtCustomerAddress, "value");
		logger.info("Retrieved address: {}", address);
		return address;
	}
	public DeliveryDialog selectProvince(String province) {
		commonAction.selectByVisibleText(elements.ddlProvince, province);
		logger.info("Selected province: {}", province);
		return this;
	}	
	public String getProvince() {
		logger.info("Retrieving province/state...");
		return commonAction.getDropDownSelectedValue(elements.ddlProvince);
	}
	public DeliveryDialog selectDistrict(String district) {
		commonAction.selectByVisibleText(elements.ddlDistrict, district);
		logger.info("Selected district: {}", district);
		return this;
	}	
	public String getDistrict() {
		logger.info("Retrieving district...");
		return commonAction.getDropDownSelectedValue(elements.ddlDistrict);
	}
	public DeliveryDialog selectWard(String ward) {
		commonAction.selectByVisibleText(elements.ddlWard, ward);
		logger.info("Selected ward: {}", ward);
		return this;
	}	
	public String getWard() {
		logger.info("Retrieving ward...");
		String ward = commonAction.getDropDownSelectedValue(elements.ddlWard);
		return ward;
	}

	public DeliveryDialog inputAddress2(String address2) {
		commonAction.inputText(elements.txtCustomerAddress2, address2);
		logger.info("Input address 2: {}", address2);
		return this;
	}
	public String getAddress2() {
		String address = commonAction.getAttribute(elements.txtCustomerAddress2, "value");
		logger.info("Retrieved address 2: {}", address);
		return address;
	}
	public DeliveryDialog inputCity(String city) {
		commonAction.inputText(elements.txtCity, city);
		logger.info("Input city: {}", city);
		return this;
	}	
	public String getCity() {
		String city = commonAction.getAttribute(elements.txtCity, "value");
		logger.info("Retrieved city: {}", city);
		return city;
	}	
	public DeliveryDialog inputZipcode(String zipcode) {
		commonAction.inputText(elements.txtZipcode, zipcode);
		logger.info("Input Zipcode: {}", zipcode);
		return this;
	}
	public String getZipcode() {
		String city = commonAction.getAttribute(elements.txtZipcode, "value");
		logger.info("Retrieved customer Zipcode: {}", city);
		return city;
	}
	public String getPhoneCode(){
		String phoneCode = commonAction.getText(elements.ddlPhoneCodeValue);
		logger.info("Retrieved phone code: {}",phoneCode);
		return phoneCode;
	}
}
