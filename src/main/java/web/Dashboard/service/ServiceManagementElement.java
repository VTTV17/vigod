package web.Dashboard.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class ServiceManagementElement {
    WebDriver driver;
    public ServiceManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By loc_btnCreateService = By.cssSelector(".service-list-page button[type='submit']");
    By loc_lst_icnEdit = By.cssSelector(".first-button");
    By loc_lst_lblServiceName = By.cssSelector(".product-table__name b");
    By loc_lst_icnDelete = By.cssSelector(".lastest-button");
    By loc_dlgNotification_btnOK = By.cssSelector(".modal-footer button.gs-button__green");

}
