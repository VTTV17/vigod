package pages.dashboard.service;

import org.openqa.selenium.By;
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
    By loc_btnCreateService = By.cssSelector(".service-list-page .gss-content-header button");
    By loc_lst_btnEdit = By.cssSelector(".first-button");
    By loc_lst_lblServiceName = By.cssSelector(".product-table__name b");
}
