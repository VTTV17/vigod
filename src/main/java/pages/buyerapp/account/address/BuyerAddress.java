package pages.buyerapp.account.address;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class BuyerAddress {
	final static Logger logger = LogManager.getLogger(BuyerAddress.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public BuyerAddress(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	By COUNTRY_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'activity_edit_address_et_country')]");

    public String getCountry() {
        String value = commonAction.getText(COUNTRY_DROPDOWN);
        logger.info("Retrieved Country: " + value);
        return value;
    }	

}
