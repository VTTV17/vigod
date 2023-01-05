package pages.dashboard.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

public class ServiceManagementPage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    public ServiceManagementPage(WebDriver driver){
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(css = ".service-list-page .gss-content-header button")
    WebElement CREATE_SERVICE_BTN;

    public ServiceManagementPage goToCreateServicePage(){
        commons.clickElement(CREATE_SERVICE_BTN);
        new HomePage(driver).waitTillSpinnerDisappear();
        return this;
    }
}
