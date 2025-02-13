package app.GoSeller.customer.customer_list;

import app.GoSeller.customer.cru_customer.ViewCreateUpdateCustomer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Duration;

public class CustomerListScreen extends CustomerListElement{
    final static Logger logger = LogManager.getLogger(CustomerListScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;
    DataGenerator generator;

    public CustomerListScreen (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
        generator = new DataGenerator();
    }
    public ViewCreateUpdateCustomer clickOnCreateCustomer(){
        common.click(loc_icnCreateCustomer);
        return new ViewCreateUpdateCustomer(driver);
    }
}
