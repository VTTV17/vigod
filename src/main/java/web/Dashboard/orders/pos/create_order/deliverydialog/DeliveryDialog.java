package web.Dashboard.orders.pos.create_order.deliverydialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import app.GoSeller.home.HomePage;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import web.Dashboard.confirmationdialog.ConfirmationDialog;

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
	
	/**
	 * Select province (Vietnamese address) or state (foreign address)
	 * @param province
	 * @return
	 */
	public DeliveryDialog selectProvince(String province) {
		commonAction.selectByVisibleText(elements.ddlProvince, province);
		logger.info("Selected province: {}", province);
		return this;
	}	
	/**
	 * Checks if the Province/State field is not provided with a value.
	 * @return true/false
	 */
	public boolean isProvinceUndefined() {
		boolean isDefined = commonAction.getAttribute(elements.ddlProvince, "class").contains("av-invalid") ? true : false;
		
		logger.info("Is Province/State field undefined: {}", isDefined);
		return isDefined;
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
	
    public DeliveryDialog clickShippingProviderDropdown() {
        commonAction.click(elements.loc_ddlDelivery);

        int maxRetries = 10;
        int sleepDuration = 1000;
        int retries = 0;

        while (retries < maxRetries && !commonAction.getElements(elements.loc_iconLoadingDeliveryProvider).isEmpty()) {
            logger.debug("Loading icon still appears. Retrying after {} ms", sleepDuration);
            commonAction.sleepInMiliSecond(sleepDuration);
            retries++;
        }
        return this;
    }
	public String getSelectedDeliveryName(){
		String selectedDelivery = commonAction.getText(elements.loc_lblSelectedDeliveryName);
		logger.info("Retrieved selected delivery name: {}",selectedDelivery);
		return selectedDelivery;
	}
	
    public DeliveryDialog fillVNAddress(String country, String address, String province, String district, String ward) {
    	selectCountry(country);
    	inputAddress(address);
    	selectProvince(province);
    	selectDistrict(district);
    	selectWard(ward);
    	return this;
    }    
    public DeliveryDialog fillForeignAddress(String country, String address, String address2, String province, String city, String zipCode) {
    	selectCountry(country);
    	inputAddress(address);
    	inputAddress2(address2);
    	selectProvince(province);
    	inputCity(city);
    	inputZipcode(zipCode);
    	return this;
    } 
    
    /**
     * Handles inputing address based on country.
     * Vietnamese address and foreign address are handled differently
     * @param data
     */
    public DeliveryDialog fillAddress(UICreateCustomerData data) {
    	if(data.getCountry().contentEquals("Vietnam")) {
    		return fillVNAddress(data.getCountry(), data.getAddress(), data.getProvince(), data.getDistrict(), data.getWard());
    	}
    	return fillForeignAddress(data.getCountry(), data.getAddress(), data.getAddress2(), data.getProvince(), data.getCity(), data.getZipCode());
    }
    
    public void clickSaveBtn() {
    	new ConfirmationDialog(driver).clickGreenBtn();
    	logger.info("Clicked Save button");
    }
}
