package web.Dashboard.promotion.flashsale.time;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class TimeManagementElement {
    WebDriver driver;

    public TimeManagementElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".flash-sale-time-management .gs-button__green")
    WebElement ADD_TIME_BTN;

    @FindBy(css = "#start-at div:nth-child(2) > button")
    WebElement START_HOUR;

    @FindBy(css = "#start-at > div:nth-child(4) > button")
    WebElement START_MIN;

    @FindBy(css = "#end-at > div:nth-child(2) > button")
    WebElement END_HOUR;

    @FindBy(css = "#end-at > div:nth-child(4) > button")
    WebElement END_MIN;

    @FindBy(css = ".uik-select__label")
    List<WebElement> TIME_DROPDOWN;

    @FindBy(css = ".add-flash-sale-time-modal .gs-button__green")
    WebElement SAVE_BTN;

    @FindBy(css = ".modal-dialog-centered .gs-button__green")
    WebElement CLOSE_BTN;
}
