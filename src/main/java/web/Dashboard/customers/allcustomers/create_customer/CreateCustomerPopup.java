package web.Dashboard.customers.allcustomers.create_customer;

import static utilities.character_limit.CharacterLimit.MAX_CUSTOMER_NAME;
import static utilities.character_limit.CharacterLimit.MAX_CUSTOMER_TAG_LENGTH;
import static utilities.character_limit.CharacterLimit.MAX_CUSTOMER_TAG_NUM;
import static utilities.character_limit.CharacterLimit.MAX_PHONE_NUMBER;
import static utilities.character_limit.CharacterLimit.MIN_PHONE_NUMBER;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import utilities.model.dashboard.customer.create.UICreateCustomerData;

public class CreateCustomerPopup {
    public String customerName;
    public String customerPhone;
    public static String[] customerTags;
    
    Logger logger = LogManager.getLogger(CreateCustomerPopup.class);
    
    UICommonAction commons;
    CreateCustomerElement elements;

    public CreateCustomerPopup(WebDriver driver) {
        commons = new UICommonAction(driver);
        elements = new CreateCustomerElement();
    }

    public CreateCustomerPopup inputCustomerName(String... name) {
        // get customer name
        customerName = name.length == 0
                ? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_CUSTOMER_NAME) + 1)
                : name[0];

        // input customer name
        commons.inputText(elements.loc_txtFullName, customerName);
        logger.info("Input Customer name: {}", customerName);
        return this;
    }

    public CreateCustomerPopup inputCustomerPhone(String... phoneNumber) {
        // get customer phone number
        int phoneLength = RandomUtils.nextInt(MAX_PHONE_NUMBER - MIN_PHONE_NUMBER + 1) + MIN_PHONE_NUMBER;
        customerPhone = phoneNumber.length == 0 ? RandomStringUtils.random(phoneLength, false, true) : phoneNumber[0];

        // input customer phone number
        commons.inputText(elements.loc_txtPhone, customerPhone);
        logger.info("Input Phone: {}", customerPhone);

        return this;
    }
    
    public CreateCustomerPopup inputEmail(String email) {
    	commons.inputText(elements.loc_txtEmail, email);
    	logger.info("Input Email: {}", email);
    	return this;
    }
    public CreateCustomerPopup inputBirthday(String birthday) {
    	commons.inputText(elements.loc_txtBirthday, birthday);
    	logger.info("Input Birthday: {}", birthday);
    	return this;
    }
    
    public CreateCustomerPopup selectCountry(String country) {
    	commons.selectByVisibleText(elements.loc_ddlCountry, country);
    	logger.info("Selected Country: {}", country);
    	return this;
    }
    public CreateCustomerPopup inputAddress1(String address) {
    	commons.inputText(elements.loc_txtAddress1, address);
    	logger.info("Input Address 1: {}", address);
    	return this;
    }
    public CreateCustomerPopup inputAddress2(String address) {
    	commons.inputText(elements.loc_txtAddress2, address);
    	logger.info("Input Address 2: {}", address);
    	return this;
    }
    public CreateCustomerPopup selectProvinceState(String province) {
    	commons.selectByVisibleText(elements.loc_ddlProvince, province);
    	logger.info("Selected Province/State: {}", province);
    	return this;
    }
    public CreateCustomerPopup selectDistrict(String district) {
    	commons.selectByVisibleText(elements.loc_ddlDistrict, district);
    	logger.info("Selected District: {}", district);
    	return this;
    }
    public CreateCustomerPopup selectWard(String ward) {
    	commons.selectByVisibleText(elements.loc_ddlWard, ward);
    	logger.info("Selected Ward: {}", ward);
    	return this;
    }
    public CreateCustomerPopup inputCity(String city) {
    	commons.inputText(elements.loc_txtCity, city);
    	logger.info("Input City: {}", city);
    	return this;
    }
    public CreateCustomerPopup inputZipCode(String code) {
    	commons.inputText(elements.loc_txtZipCode, code);
    	logger.info("Input Zip Code: {}", code);
    	return this;
    }
    public CreateCustomerPopup fillVNAddress(String country, String address, String province, String district, String ward) {
    	selectCountry(country);
    	inputAddress1(address);
    	selectProvinceState(province);
    	selectDistrict(district);
    	selectWard(ward);
    	return this;
    }    
    public CreateCustomerPopup fillForeignAddress(String country, String address, String address2, String province, String city, String zipCode) {
    	selectCountry(country);
    	inputAddress1(address);
    	inputAddress2(address2);
    	selectProvinceState(province);
    	inputCity(city);
    	inputZipCode(zipCode);
    	return this;
    }     
    public CreateCustomerPopup clickCustomerCreationCheckbox() {
    	commons.click(elements.loc_chkCustomerCreation);
    	logger.info("Clicked Customer Creation checkbox");
    	return this;
    }
    
    private String[] generateTagList() {
        String[] tags = new String[RandomUtils.nextInt(MAX_CUSTOMER_TAG_NUM) + 1];
        for (int i = 0; i < tags.length; i++) {
            tags[i] = RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_CUSTOMER_TAG_LENGTH) + 1);
        }
        return tags;
    }

    public CreateCustomerPopup inputCustomerTags(String... tags) {
        // get customer tags
        customerTags = tags.length == 0 ? generateTagList() : tags;

        // input customer tags
        for (String tag : customerTags) {
            commons.inputText(elements.loc_txtTags, tag + "\n");
            logger.info("Input Tags: {}", tag);
        }

        return this;
    }
    public CreateCustomerPopup inputTags(List<String> tags) {
    	tags.stream().forEach(tag -> {
    		commons.inputText(elements.loc_txtTags, tag + "\n");
    		logger.info("Input Tags: {}", tag);
    	});
    	
    	return this;
    }
    
    /**
     * Handles inputing address based on country.
     * Vietnamese address and foreign address are handled differently
     * @param data
     */
    public void fillAddress(UICreateCustomerData data) {
    	if(data.getCountry().contentEquals("Vietnam")) {
    		fillVNAddress(data.getCountry(), data.getAddress(), data.getProvince(), data.getDistrict(), data.getWard());
    	}
    	fillForeignAddress(data.getCountry(), data.getAddress(), data.getAddress2(), data.getProvince(), data.getCity(), data.getZipCode());
    }
    
    public CreateCustomerPopup createCustomer(UICreateCustomerData data) {
    	inputCustomerName(data.getName());
    	inputCustomerPhone(data.getPhone());
    	inputEmail(data.getEmail());
    	
    	//TODO: Define gender and birthday
    	
    	fillAddress(data);
    	
    	inputTags(data.getTags());

    	//TODO: Input customer note
    	
    	//TODO: Check status before the click
    	clickCustomerCreationCheckbox();
    	return this;
    }    
    
    public void clickAddBtn() {
        commons.click(elements.loc_btnAdd);
        logger.info("Clicked Add button");
    }
}
