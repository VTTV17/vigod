package pages.dashboard.service.servicecollections;

import org.openqa.selenium.By;
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
    By loc_txtCollectionName = By.id("collectionName");
    By loc_lst_lblServiceName = By.cssSelector(".collection-form-editor-row-name b");
    By loc_lst_btnDelete = By.cssSelector(".btn-remove__row");
    By loc_txtUploadImage = By.xpath("//input[@type='file' and @style ='display: none;']");
    By loc_lblDrapAndDropPhoto = By.xpath("//input[@type='file']/following-sibling::div[2]");
    By loc_btnSave = By.xpath("(//button[contains(@class,'btn-save')])[1]");
    By loc_btnCancel = By.xpath("(//div[contains(@class,'action-btn--group')])[1]//button[2]");
    By loc_dlgNotification_btnClose = By.cssSelector(".modal-footer button");
    By loc_chbManualValue = By.id("radio-collectionType-MANUAL");
    By loc_chbManualAction = By.xpath("//input[@id='radio-collectionType-MANUAL']/following-sibling::label");
    By loc_chbAutomatedValue = By.id("radio-collectionType-AUTOMATED");
    By loc_chbAutomedAction = By.xpath("//input[@id='radio-collectionType-AUTOMATED']/following-sibling::label");
    By loc_chbAllConditionValue = By.id("radio-conditionType-ALL");
    By loc_chbAllConditionAction = By.xpath("//input[@id='radio-conditionType-ALL']/parent::div");
    By loc_chbAnyConditionValue = By.id("radio-conditionType-ANY");
    By loc_chbAnyConditionAction = By.xpath("//input[@id='radio-conditionType-ANY']/parent::div");
    By loc_ddlCondition = By.id("conditionField");
    By loc_ddlOperator = By.id("conditionOperand");
    By loc_lst_txtConditionValue = By.cssSelector(".uik-input__input");
    By loc_btnSelectService = By.cssSelector(".btn-addproduct");
    By loc_lst_txtPriorities = By.xpath("//td[contains(@class,'gs-table-body-item')]//input[@precision='0']");
    By loc_dlgSelectService_cbxSelectAllValue  = By.xpath("//input[@name='check_all']");
    By loc_dlgSelectService_cbxSelectAllAction = By.xpath("//input[@name='check_all']//following-sibling::div");
    By loc_dlgSelectService_txtSearchForService = By.cssSelector(".search-input");
    By loc_dlgSelectService_btnOK = By.cssSelector(".footer-btn .gs-button__green");
    By loc_btnAddMoreCondition = By.cssSelector(".automated-config__header button > div");
    By loc_txtSEOTitle = By.id("seoTitle");
    By loc_txtSEODescription = By.id("seoDescription");
    By loc_txtSEOKeyword = By.id("seoKeywords");
    By loc_txtSEOUrl = By.id("seoUrl");
    By loc_lblPageTitle =  By.cssSelector(".gs-page-title");
    By loc_lblGeneralInfomation = By.xpath("//label[@for='collectionName']//parent::div/preceding-sibling::div//h3");
    By loc_lblCollectionName = By.xpath("//label[@for='collectionName']");
    By loc_lblImages = By.xpath("//label[@for='collectionName']/following-sibling::label");
    By loc_lblDragAndDrop = By.xpath("//input[@type='file']//following-sibling::div[2]");
    By loc_lblCollectionType = By.xpath("//fieldset[@name='collectionType']//parent::div//preceding-sibling::div//h3");
    By loc_lblManualDescription = By.xpath("//div[@class='collection-description'][1]");
    By loc_lblAutomatedDescription = By.xpath("//div[@class='collection-description'][2]");
    By loc_lblConditions = By.xpath(".automated-config__header h3");
    By loc_lblServiceMustBeMatch = By.xpath("//div[@class='automated-config__condition-type']/span[1]");
    By loc_lblServiceList = By.xpath("//div[@class='bnt-group_add_product']//preceding-sibling::h3");
    By loc_lblNoService = By.xpath(".product-list__group span");
    By loc_lblSEOSetting = By.xpath("//div[contains(@class,'seo-editor')]//div[contains(@class,'gs-widget__header')]//h3");
    By loc_lblLivePreview = By.xpath("//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span");
    By loc_tltLivePreview = By.xpath("//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span/following-sibling::div//div");
    By loc_lblSEOTitle  = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]/span");
    By loc_tltSEOTitle = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]//div/div");
    By loc_lblSEODescription = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]/span");
    By loc_tltSEODescription = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]//div/div");
    By loc_lblSEOKeyword = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]/span");
    By loc_tltSEOKeyword = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]//div/div");
    By loc_lblUrlLink = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][4]/span");

    /*--------------Edit translation-------------*/

    By loc_btnEditTranslation = By.xpath("//button[contains(@class,'gs-button__gray')][1]");
    By loc_dlgTranslate_txtName = By.id("name");
    By loc_dlgTranslate_btnSave = By.cssSelector("button[name='submit-translate']");
    By loc_dlgTranslate_txtSEOTitle = By.cssSelector(".product-translate #seoTitle");
    By loc_dlgTranslate_txtSEODescription = By.cssSelector(".product-translate #seoDescription");
    By loc_dlgTranslate_txtSEOKeyword = By.cssSelector(".product-translate #seoKeywords");
    By loc_dlgTranslate_txtSEOUrl = By.cssSelector(".product-translate #seoUrl");


//    @FindBy(id = "collectionName")
//    WebElement COLLECTION_NAME;
//    @FindBy(css = ".collection-form-editor-row-name b")
//    List<WebElement> SERVICE_NAME_LIST;
//    @FindBy(css = ".btn-remove__row")
//    List<WebElement> DELETE_BTN_LIST;
//    @FindBy(xpath = "//input[@type='file' and @style ='display: none;']")
//    WebElement IMAGE_INPUT;
//    @FindBy(xpath = "//input[@type='file']/following-sibling::div[2]")
//    WebElement DRAG_DROP_PHOTO_TXT;
//    @FindBy(xpath = "(//button[contains(@class,'btn-save')])[1]")
//    WebElement SAVE_BTN;
//    @FindBy(xpath = "(//div[contains(@class,'action-btn--group')])[1]//button[2]")
//    WebElement CANCEL_BTN;
//    @FindBy(css = ".modal-footer button")
//    WebElement CLOSE_BTN;
//    @FindBy(id = "radio-collectionType-MANUAL")
//    WebElement MANUAL_RADIO_VALUE;
//    @FindBy(xpath = "//input[@id='radio-collectionType-MANUAL']/following-sibling::label")
//    WebElement MANUAL_RADIO_ACTION;
//    @FindBy(id = "radio-collectionType-AUTOMATED")
//    WebElement AUTOMATED_RADIO_VALUE;
//    @FindBy(xpath = "//input[@id='radio-collectionType-AUTOMATED']/following-sibling::label")
//    WebElement AUTOMATED_RADIO_ACTION;
//    @FindBy(id = "radio-conditionType-ALL")
//    WebElement ALL_CONDITION_RADIO_VALUE;
//    @FindBy(xpath = "//input[@id='radio-conditionType-ALL']/parent::div")
//    WebElement ALL_CONDITION_RADIO_ACTION;
//    @FindBy(id = "radio-conditionType-ANY")
//    WebElement ANY_CONDITION_RADIO_VALUE;
//    @FindBy(xpath = "//input[@id='radio-conditionType-ANY']/parent::div")
//    WebElement ANY_CONDITION_RADIO_ACTION;
//    @FindBy(id = "conditionField")
//    List<WebElement> CONDITION_DROPDOWN;
//    @FindBy(id = "conditionOperand")
//    List<WebElement> OPERATOR_DROPDOWN;
//    @FindBy(css = ".uik-input__input")
//    List<WebElement> CONDITION_VALUE_INPUT;
//    @FindBy(css = ".btn-addproduct")
//    WebElement SELECT_SERVICE_BTN;
//    @FindBy(xpath = "//td[contains(@class,'gs-table-body-item')]//input[@precision='0']")
//    List<WebElement> PRIORITIES_INPUT;

//    @FindBy(xpath = "//input[@name='check_all']")
//    WebElement SELECT_ALL_CBX_VALUE;
//    @FindBy(xpath = "//input[@name='check_all']//following-sibling::div")
//    WebElement SELECT_ALL_CBX_ACTION;
//    @FindBy(css = ".search-input")
//    WebElement SEARCH_FOR_PRODUCT_INPUT;
//    @FindBy(css = ".footer-btn .gs-button__green")
//    WebElement OK_BTN;
//    @FindBy(css = ".automated-config__header button > div")
//    WebElement ADD_MORE_CONDITION_BTN;
//    @FindBy(id = "seoTitle")
//    WebElement SEO_TITLE;
//    @FindBy(id = "seoDescription")
//    WebElement SEO_DESCRIPTION;
//    @FindBy(id = "seoKeywords")
//    WebElement SEO_KEYWORD;
//    @FindBy(id = "seoUrl")
//    WebElement SEO_URL;
//    @FindBy(css = ".gs-page-title")
//    WebElement PAGE_TITLE;
//    @FindBy(xpath = "//label[@for='collectionName']//parent::div/preceding-sibling::div//h3")
//    WebElement GENERAL_INFORMATION_LBL;
//    @FindBy(xpath = "//label[@for='collectionName']")
//    WebElement COLLECTION_NAME_LBL;
//    @FindBy(xpath = "//label[@for='collectionName']/following-sibling::label")
//    WebElement IMAGES_LBL;
//    @FindBy(xpath = "//input[@type='file']//following-sibling::div[2]")
//    WebElement DRAG_AND_DROP_LBL;
//    @FindBy(xpath = "//fieldset[@name='collectionType']//parent::div//preceding-sibling::div//h3")
//    WebElement COLLECTION_TYPE_LBL;
//    @FindBy(xpath = "//div[@class='collection-description'][1]")
//    WebElement MANUAL_DESCRIPTION;
//    @FindBy(xpath = "//div[@class='collection-description'][2]")
//    WebElement AUTOMATED_DESCRIPTION;
//    @FindBy(css = ".automated-config__header h3")
//    WebElement CONDITIONS_LBL;
//    @FindBy(xpath = "//div[@class='automated-config__condition-type']/span[1]")
//    WebElement SERVICE_MUST_MATCH_LBL;
//    @FindBy(xpath = "//div[@class='bnt-group_add_product']//preceding-sibling::h3")
//    WebElement SERVICE_LIST_LBL;
//    @FindBy(css = ".product-list__group span")
//    WebElement NO_SERVICES_LBL;
//    @FindBy(xpath = "//div[contains(@class,'seo-editor')]//div[contains(@class,'gs-widget__header')]//h3")
//    WebElement SEO_SETTINGS_LBL;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span")
//    WebElement LIVE_PREVIEW_LBL;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span/following-sibling::div//div")
//    WebElement LIVE_PREVIEW_TOOLTIP;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]/span")
//    WebElement SEO_TITLE_LBL;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]//div/div")
//    WebElement SEO_TITLE_TOOLTIP;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]/span")
//    WebElement SEO_DESCRIPTION_LBL;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]//div/div")
//    WebElement SEO_DESCRIPTION_TOOLTIP;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]/span")
//    WebElement SEO_KEYWORDS_LBL;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]//div/div")
//    WebElement SEO_KEYWORD_TOOLTIP;
//    @FindBy(xpath = "//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][4]/span")
//    WebElement URL_LINK_LBL;

    /*--------------Edit translation-------------*/
//    @FindBy(xpath = "//button[contains(@class,'gs-button__gray')][1]")
//    WebElement EDIT_TRANSLATION_BTN;
//    @FindBy(id="name")
//    WebElement NAME_TRANSLATION;
//    @FindBy(css = "button[name='submit-translate']")
//    WebElement SAVE_TRANSLATE_BTN;
//    @FindBy(css = ".product-translate #seoTitle")
//    WebElement SEO_TITLE_TRANSLATE;
//    @FindBy(css = ".product-translate #seoDescription")
//    WebElement SEO_DESCRIPTION_TRANSLATE;
//    @FindBy(css = ".product-translate #seoKeywords")
//    WebElement SEO_KEYWORDS_TRANSLATE;
//    @FindBy(css = ".product-translate #seoUrl")
//    WebElement SEO_URL_TRANSLATE;
}
