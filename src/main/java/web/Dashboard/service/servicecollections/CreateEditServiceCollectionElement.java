package web.Dashboard.service.servicecollections;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

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
    By loc_btnSave = By.xpath("//button[string()='Lưu' or string()='Save']");
    By loc_btnCancel = By.cssSelector(".gss-content-header--action-btn button:nth-child(2)");
    By loc_btnDelete = By.xpath("//div[@id='app-body']//button[contains(@class,'red')]");
    By loc_dlgNotification_btnClose = By.cssSelector(".modal-footer button");
    By loc_dlgNotification_lblMessage = By.cssSelector(".modal-body");
    By loc_dlgConfirmation_btnOK = By.cssSelector("[data-testid='confirmBtn']");
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
    By loc_lblConditions = By.cssSelector(".automated-config__header h3");
    By loc_lblServiceMustBeMatch = By.xpath("//div[@class='automated-config__condition-type']/span[1]");
    By loc_lblServiceList = By.xpath("//div[@class='bnt-group_add_product']//preceding-sibling::h3");
    By loc_lblNoService = By.cssSelector(".product-list__group span");
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

    By loc_btnEditTranslation = By.xpath("//button[string()='Sửa bản dịch' or string()='Edit Translation']");
    By loc_dlgTranslate_txtName = By.id("name");
    By loc_dlgTranslate_btnSave = By.cssSelector("button[name='submit-translate']");
    By loc_dlgTranslate_txtSEOTitle = By.cssSelector(".product-translate #seoTitle");
    By loc_dlgTranslate_txtSEODescription = By.cssSelector(".product-translate #seoDescription");
    By loc_dlgTranslate_txtSEOKeyword = By.cssSelector(".product-translate #seoKeywords");
    By loc_dlgTranslate_txtSEOUrl = By.cssSelector(".product-translate #seoUrl");
}
