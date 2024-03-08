package web.Dashboard.promotion.flashsale.time;

import org.openqa.selenium.By;
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
    By loc_lblPopUpMessage = By.cssSelector(".modal-body");
    public By loc_lst_lblStartTime = By.xpath("//tr/td[1]/span");
    By loc_dlgAddTime_ddlStartAtHour = By.xpath("//label[@for='start-at-hour']/following-sibling::div[1]//button");
    By loc_dlgAddTime_ddvStartAtHour = By.xpath("//label[@for='start-at-hour']/following-sibling::div[1]//div[@class='uik-select__label']");
    By loc_dlgAddTime_btnSave = By.cssSelector(".add-flash-sale-time-modal__footer button");
    public By loc_btnAddTime = By.cssSelector(".flash-sale-time-management button");
    By loc_dlgAddTime_ddlEndAtHour = By.xpath("//label[@for='end-at-hour']/following-sibling::div[1]//button");
    By loc_dlgAddTime_ddvEndAtHour = By.xpath("//label[@for='end-at-hour']/following-sibling::div[1]//div[@class='uik-select__label']");

}
