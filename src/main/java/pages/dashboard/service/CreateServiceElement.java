package pages.dashboard.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CreateServiceElement {
    WebDriver driver;
    public CreateServiceElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy(xpath = "(//button[contains(@class,'btn-save')])[1]")
    WebElement SAVE_BTN;
    @FindBy(xpath = "(//div[contains(@class,'action-btn--group')])[1]//button[2]")
    WebElement CANCEL_BTN;
    @FindBy(xpath = "//input[@name='serviceName']")
    WebElement SERVICE_NAME;
    @FindBy(xpath = "(//input[@inputmode='numeric'])[1]")
    WebElement LISTING_PRICE;
    @FindBy(xpath = "(//input[@inputmode='numeric'])[2]")
    WebElement SELLING_PRICE;
    @FindBy(xpath = "(//input[@type='checkbox'])[1]")
    WebElement SHOW_AS_LISTING_CBX_VALUE;
    @FindBy(xpath = "(//input[@type='checkbox'])[1]/following-sibling::div")
    WebElement SHOW_AS_LISTING_CBX_ACTION;
    @FindBy(xpath = "//label[@for='serviceDescription']//following::div[@class='fr-element fr-view']")
    WebElement SERVICE_DESCRIPTION;
    @FindBy(css = ".product-form-collection-selector")
    WebElement COLLECTION_FORM;
    @FindBy(xpath = "//div[contains(@id,'react-select-2-option')]")
    List<WebElement> COLLECTION_SUGGESTION;
    @FindBy(xpath = "//div[@class='product-form-collection-selector']//div[contains(@class,'multiValue')]/div[1]")
    List<WebElement> SELECTED_COLLECTIONS;
    @FindBy(xpath = "//input[@type='file' and @style ='display: none;']")
    WebElement IMAGE_INPUT;
    @FindBy(css = "#locations")
    WebElement LOCATION;
    @FindBy(css = "#timeSlots")
    WebElement TIME_SLOTS;
    @FindBy (css ="#seoTitle")
    WebElement SEO_TITLE;
    @FindBy(css = "#seoDescription")
    WebElement SEO_DESCRIPTION;
    @FindBy(css = "#seoKeywords")
    WebElement SEO_KEYWORDS;
    @FindBy(css = "#seoUrl")
    WebElement SEO_URL;
    @FindBy(css = ".modal-body")
    WebElement POPUP_MESSAGE;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[1]//h3")
    WebElement BASIC_INFOMATION_TITLE;
    @FindBy(xpath = "//input[@id='serviceName']/preceding-sibling::label")
    WebElement SERVICE_NAME_LBL;
    @FindBy(xpath = "(//section[@class='service-basic-info']//label)[1]")
    WebElement LISTING_PRICE_LBL;
    @FindBy(xpath = "(//section[@class='service-basic-info']//label)[2]")
    WebElement SELLING_PRICE_LBL;
    @FindBy(xpath = "//label[@for='serviceDescription']")
    WebElement DESCRIPTION_LBL;
    @FindBy(xpath = "//label[@for='productCollection']")
    WebElement COLLECTIONS_LBL;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[2]//h3")
    WebElement IMAGES_TITLE;
    @FindBy(xpath = "//input[@type='file']/following-sibling::p[2]")
    WebElement DRAG_DROP_PHOTO_TXT;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[3]/child::div/h3")
    WebElement LOCATIONS_AND_TIME_TITLE_AND_DESCRIPNTION;
    @FindBy(xpath = "//input[@id='locations']/ancestor::div[@class='variation-item']/preceding-sibling::label")
    WebElement LOCATIONS_LBL;
    @FindBy(xpath = "//label[@class='has-tooltip']")
    WebElement TIME_SLOTS_LBL;
    @FindBy(xpath = "//label[@class='has-tooltip']/span/div")
    WebElement TIME_SLOTS_TOOLTIP;
    @FindBy(xpath = "//section[@class='location-timeslots']//h3")
    WebElement LIST_LOCATIONS_TIMESLOTS_TITLE;
    @FindBy(xpath = "(//section[@class='gs-table-header']//span)[1]")
    WebElement LOCATION_LBL;
    @FindBy(xpath = "(//section[@class='gs-table-header']//span)[2]")
    WebElement TIMESLOT_LBL;
    @FindBy(xpath = "//div[@class='empty']//span")
    WebElement NO_LOCATION_TIMESLOT_TXT;
    @FindBy(xpath = "//button[@type='submit']//following-sibling::div[4]/child::div/h3")
    WebElement SEO_SETTINGS_TITLE;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span")
    WebElement LIVE_PREVIEW_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span/following-sibling::div//div")
    WebElement LIVE_PREVIEW_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]/span")
    WebElement SEO_TITLE_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]//div/div")
    WebElement SEO_TITLE_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]/span")
    WebElement SEO_DESCRIPTION_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]//div/div")
    WebElement SEO_DESCRIPTION_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]/span")
    WebElement SEO_KEYWORDS_LBL;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]//div/div")
    WebElement SEO_KEYWORD_TOOLTIP;
    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][4]/span")
    WebElement URL_LINK_LBL;
    @FindBy(xpath = "//h5[@class='gs-page-title']")
    WebElement CREATE_NEW_SERVICE_TITLE;
    @FindBy(xpath = "//label[@for='productCollection']//following::div[contains(@class,'placeholder')]")
    WebElement SELECT_COLLECTIONS_HINT_TXT;
    @FindBy(xpath = "//label[@for='serviceName']//following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_SERVICE_NAME;
    @FindBy(xpath = "//input[@id='orgPrice']/following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_LISTING_PRICE;
    @FindBy(xpath = "//input[@id='newPrice']/following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_SELLING_PRICE;
    @FindBy(xpath = "//input[@id='serviceDescription']/following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_DESCRIPTION;
    @FindBy(css = ".image-widget__error-wrapper div")
    WebElement ERROR_MESSAGE_IMAGES;
    @FindBy(xpath = "//input[@id='locations']/following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_LOCATIONS;
    @FindBy(xpath = "//input[@id='timeSlots']/following-sibling::div[@class='invalid-feedback']")
    WebElement ERROR_MESSAGE_TIMESLOTS;
    @FindBy(css = "#html-1")
    WebElement CODE_VIEW_BTN;
    @FindBy(xpath = "//textarea")
    WebElement CODE_VIEW_DES;
    @FindBy(xpath = "//div[@class='variation-item'][1]/div[2]")
    WebElement MAXIMUM_ERROR_LOCATION;
    @FindBy(xpath = "//div[@class='variation-item'][2]/div[2]")
    WebElement MAXIMUM_ERROR_TIMESLOT;
    @FindBy(css = ".modal-footer button")
    WebElement CLOSE_BTN_NOTIFICATION_POPUP;
    /*--------------Edit service-------------*/
    @FindBy(xpath = "//h5[contains(@class,'product-name')]/following-sibling::div//button[contains(@class,'gs-button__gray--outline')][1]")
    WebElement EDIT_TRANSLATION_BTN;
    @FindBy(css = "input[name='informationName']")
    WebElement NAME_INPUT_TRANSLATE;
    @FindBy(xpath = "//label[@for='serviceDescription']//following::div[@class=\"fr-element fr-view\"][2]")
    WebElement DESCRIPTION_TRANSLATE;
    @FindBy(xpath = "//div[@class='modal-body']//div[@class='row']//input")
    List<WebElement> LIST_LOCATION_INPUT;
    @FindBy(css = "button[name='submit-translate']")
    WebElement SAVE_TRANSLATE_BTN;
    @FindBy(css = ".product-translate-modal #seoTitle")
    WebElement SEO_TITLE_TRANSLATE;
    @FindBy(css = ".product-translate-modal #seoDescription")
    WebElement SEO_DESCRIPTION_TRANSLATE;
    @FindBy(css = ".product-translate-modal #seoKeywords")
    WebElement SEO_KEYWORDS_TRANSLATE;
    @FindBy(css = ".product-translate-modal #seoUrl")
    WebElement SEO_URL_TRANSLAE;
    @FindBy(xpath = "(//button[contains(@class,'yellow')])[1]")
    WebElement ACTIVE_DEACTIVE_BTN;
    @FindBy(xpath = "//h5[contains(@class,'product-name')]/preceding-sibling::span")
    WebElement STATUS;
    @FindBy(css = ".seo-editor__live-preview-url a")
    WebElement LIVE_PREVIEW_URL;
    @FindBy(css = ".image-view")
    List<WebElement> IMAGE_LIST;
    @FindBy(css = ".image-widget__btn-remove")
    List<WebElement> REMOVE_IMAGE_LIST;
    @FindBy(css = ".product-form-collection-selector svg:not([role = 'img'])")
    List<WebElement> DELETE_COLLECTION_ICON_LIST;
    @FindBy(xpath = "(//button[contains(@class,'button__red')])[1]")
    WebElement DELETE_BTN;
    @FindBy(css = ".modal-footer button.gs-button__green")
    WebElement OK_BTN_CONFIRM_POPUP;
}
