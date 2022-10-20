package pages.dashboard.customers.allcustomers.create_customer;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import static utilities.character_limit.CharacterLimit.*;

public class CreateCustomerPopup extends CreateCustomerElement {
    public static String customerName;
    public static String customerPhone;
    public static String customerPhoneCode;
    public static String[] customerTags;
    WebDriverWait wait;
    Logger logger = LogManager.getLogger(CreateCustomerPopup.class);

    public CreateCustomerPopup(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CreateCustomerPopup inputCustomerName(String... name) {
        // get customer name
        customerName = name.length == 0
                ? RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(MAX_CUSTOMER_NAME) + 1)
                : name[0];

        // input customer name
        wait.until(ExpectedConditions.elementToBeClickable(CUSTOMER_NAME)).sendKeys(customerName);

        // log
        logger.info("Customer name: %s".formatted(customerName));

        return this;
    }

    public CreateCustomerPopup inputCustomerPhone(String... phoneNumber) {
        // get customer phone number
        int phoneLength = RandomUtils.nextInt(MAX_PHONE_NUMBER - MIN_PHONE_NUMBER + 1) + MIN_PHONE_NUMBER;
        customerPhone = phoneNumber.length == 0 ? RandomStringUtils.random(phoneLength, false, true) : phoneNumber[0];

        // get phone code
        customerPhoneCode = wait.until(ExpectedConditions.elementToBeClickable(CUSTOMER_PHONE_CODE))
                .getText().replace("(", "").replace(")", "");

        // input customer phone number
        wait.until(ExpectedConditions.elementToBeClickable(CUSTOMER_PHONE)).sendKeys(customerPhone);

        //log
        logger.info("Customer phone: %s%s".formatted(customerPhoneCode, customerPhone));

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
            wait.until(ExpectedConditions.elementToBeClickable(CUSTOMER_TAGS)).sendKeys(tag + "\n");
            logger.info("Customer tag: %s".formatted(tag));
        }

        return this;
    }

    public void clickAddBtn() {
        // click add button to complete create a new customer
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BTN)).click();

        //log
        logger.info("Create customer successfully");
    }
}
