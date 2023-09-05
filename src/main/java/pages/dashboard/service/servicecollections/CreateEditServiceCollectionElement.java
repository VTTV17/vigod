package pages.dashboard.service.servicecollections;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CreateEditServiceCollectionElement {
    WebDriver driver;
    public CreateEditServiceCollectionElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(id = "collectionName")
    WebElement COLLECTION_NAME;
    @FindBy(css = ".collection-form-editor-row-name b")
    List<WebElement> SERVICE_NAME_LIST;
    @FindBy(css = ".btn-remove__row")
    List<WebElement> DELETE_BTN_LIST;
    @FindBy(xpath = "//input[@type='file' and @style ='display: none;']")
    WebElement IMAGE_INPUT;
    @FindBy(xpath = "//input[@type='file']/following-sibling::div[2]")
    WebElement DRAG_DROP_PHOTO_TXT;
    @FindBy(xpath = "(//button[contains(@class,'btn-save')])[1]")
    WebElement SAVE_BTN;
    @FindBy(xpath = "(//div[contains(@class,'action-btn--group')])[1]//button[2]")
    WebElement CANCEL_BTN;
    @FindBy(css = ".modal-footer button")
    WebElement CLOSE_BTN;
    @FindBy(id = "radio-collectionType-MANUAL")
    WebElement MANUAL_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-collectionType-MANUAL']/following-sibling::label")
    WebElement MANUAL_RADIO_ACTION;
    @FindBy(id = "radio-collectionType-AUTOMATED")
    WebElement AUTOMATED_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-collectionType-AUTOMATED']/following-sibling::label")
    WebElement AUTOMATED_RADIO_ACTION;
    @FindBy(id = "radio-conditionType-ALL")
    WebElement ALL_CONDITION_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-conditionType-ALL']/parent::div")
    WebElement ALL_CONDITION_RADIO_ACTION;
    @FindBy(id = "radio-conditionType-ANY")
    WebElement ANY_CONDITION_RADIO_VALUE;
    @FindBy(xpath = "//input[@id='radio-conditionType-ANY']/parent::div")
    WebElement ANY_CONDITION_RADIO_ACTION;
    @FindBy(id = "conditionField")
    List<WebElement> CONDITION_DROPDOWN;
    @FindBy(id = "conditionOperand")
    List<WebElement> OPERATOR_DROPDOWN;

    @FindBy(css = ".uik-input__input")
    List<WebElement> CONDITION_VALUE_INPUT;
    @FindBy(css = ".btn-addproduct")
    WebElement SELECT_SERVICE_BTN;
    @FindBy(xpath = "//td[contains(@class,'gs-table-body-item')]//input[@precision='0']")
    List<WebElement> PRIORITIES_INPUT;

    @FindBy(xpath = "//input[@name='check_all']")
    WebElement SELECT_ALL_CBX_VALUE;
    @FindBy(xpath = "//input[@name='check_all']//following-sibling::div")
    WebElement SELECT_ALL_CBX_ACTION;
    @FindBy(css = ".search-input")
    WebElement SEARCH_FOR_PRODUCT_INPUT;
    @FindBy(css = ".footer-btn .gs-button__green")
    WebElement OK_BTN;
    @FindBy(css = ".automated-config__header button > div")
    WebElement ADD_MORE_CONDITION_BTN;
    @FindBy(id = "seoTitle")
    WebElement SEO_TITLE;
    @FindBy(id = "seoDescription")
    WebElement SEO_DESCRIPTION;
    @FindBy(id = "seoKeywords")
    WebElement SEO_KEYWORD;
    @FindBy(id = "seoUrl")
    WebElement SEO_URL;

}
