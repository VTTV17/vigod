package web.Dashboard.customers.allcustomers.create_customer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CreateCustomerElement {
    WebDriver driver;

    public CreateCustomerElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#fullName")
    WebElement CUSTOMER_NAME;

    @FindBy (css = "#phone")
    WebElement CUSTOMER_PHONE;

    @FindBy (css = ".phone-country")
    WebElement CUSTOMER_PHONE_CODE;

    @FindBy (css = "div.css-1hwfws3 input")
    WebElement CUSTOMER_TAGS;

    @FindBy (css = ".modal-footer > .gs-button__green")
    WebElement ADD_BTN;
}
