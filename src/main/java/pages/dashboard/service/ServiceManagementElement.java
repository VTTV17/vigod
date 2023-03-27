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
    @FindBy(xpath = "//h5[@class='gs-page-title']")
    WebElement PAGE_TITLE;
    @FindBy(css = ".gs-search-box__wrapper input")
    WebElement SEARCH_INPUT;
    @FindBy(css = ".status-filter .uik-select__valueWrapper")
    WebElement STATUS_FILTER_VALUE;
    @FindBy(xpath = "(//div[contains(@class,'uik-select__label')])[1]")
    WebElement ALL_STATUS_OPTION;
    @FindBy(xpath = "(//div[contains(@class,'uik-select__label')])[2]")
    WebElement ACTIVE_OPTION;
    @FindBy(xpath = "(//div[contains(@class,'uik-select__label')])[3]")
    WebElement INACTIVE_OPTION;
    @FindBy(xpath = "(//div[contains(@class,'uik-select__label')])[4]")
    WebElement ERROR_OPTION;
    @FindBy(css = "#dropdownSegmentsButton .uik-select__valueWrapper")
    WebElement COLLECTION_FILTER_VALUE;
    @FindBy(xpath = "//div[@class='segments-item-row'][1]")
    WebElement ALL_COLLECTION_OPTION;
    @FindBy(xpath = "//section[@class='gs-table-header']/section[1]//span")
    WebElement THUMBNAIL_COL_TXT;
    @FindBy(xpath = "//section[@class='gs-table-header']/section[2]//span")
    WebElement SERVICE_NAME_COL_TXT;
    @FindBy(xpath = "//section[@class='gs-table-header']/section[3]//span")
    WebElement STATUS_COL_TXT;
    @FindBy(xpath = "//section[@class='gs-table-header']/section[4]//span")
    WebElement ACTIONS_COL_TXT;
    @FindBy(css = ".modal-title")
    WebElement MODAL_TITLE;
    @FindBy(css = ".modal-body")
    WebElement MODAL_CONTENT;
    @FindBy(css = ".modal-footer .gs-button__gray--outline")
    WebElement MODAL_CANCEL_BTN;
    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement MODAL_OK_BTN;
    @FindBy(css = ".first-button")
    List<WebElement> LIST_EDIT_BTN;
    @FindBy(css = ".product-table__name b")
    List<WebElement> LIST_SERVICE_NAME;
}
