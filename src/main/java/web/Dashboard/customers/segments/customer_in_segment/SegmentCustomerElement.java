package web.Dashboard.customers.segments.customer_in_segment;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class SegmentCustomerElement {
    WebDriver driver;

    public SegmentCustomerElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".full-name")
    List<WebElement> LIST_CUSTOMER_NAME;
}
