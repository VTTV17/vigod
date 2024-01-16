package pages.dashboard.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ServiceManagementElement {
    WebDriver driver;
    public ServiceManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(css = ".service-list-page .gss-content-header button")
    WebElement CREATE_SERVICE_BTN;
    @FindBy(css = ".first-button")
    List<WebElement> LIST_EDIT_BTN;
    @FindBy(css = ".product-table__name b")
    List<WebElement> LIST_SERVICE_NAME;
}
