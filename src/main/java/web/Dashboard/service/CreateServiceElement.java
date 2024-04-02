package web.Dashboard.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CreateServiceElement {
    WebDriver driver;
    public CreateServiceElement(WebDriver driver) {
        this.driver = driver;
    }
    By loc_btnSave = By.xpath("(//button[contains(@class,'btn-save')])[1]");
    By loc_btnCancel= By.xpath("(//div[contains(@class,'action-btn--group')])[1]//button[2]");
    By loc_txtServiceName = By.xpath( "//input[@name='serviceName']");
    By loc_txtListingPrice = By.xpath("(//input[@inputmode='numeric'])[1]");
    By loc_txtSellingPrice = By.xpath( "(//input[@inputmode='numeric'])[2]");
    By loc_chbShowAsListingValue = By.xpath("(//input[@type='checkbox'])[1]");
    By loc_chbShowAsListingAction = By.xpath("(//input[@type='checkbox'])[1]/following-sibling::div");
    By loc_txaServiceDescription = By.xpath("//label[@for='serviceDescription']//following::div[@class='fr-element fr-view']");
    By loc_frmCollection = By.cssSelector(".product-form-collection-selector");
    By loc_lstCollectionSuggestion = By.xpath("//div[contains(@id,'react-select-4-option')]");
    By loc_lblSelectedCollection = By.xpath("//div[@class='product-form-collection-selector']//div[contains(@class,'multiValue')]/div[1]");
    By loc_txtUploadImage = By.xpath("//input[@type='file' and @style ='display: none;']");
    By loc_txtLocation = By.cssSelector("#locations");
    By loc_txtTimeSlot = By.cssSelector("#timeSlots");
    By loc_txtSEOTitle = By.cssSelector("#seoTitle");
    By loc_txtSEODescription = By.cssSelector("#seoDescription");
    By loc_txtSEOKeyWords = By.cssSelector("#seoKeywords");
    By loc_txtSEOUrl = By.cssSelector("#seoUrl");
    By loc_lblBasicInformationTitle = By.xpath("//button[@type='submit']//following-sibling::div[1]//h3");
    By loc_lblServiceName = By.xpath("//input[@id='serviceName']/preceding-sibling::label");
    By loc_lblListingPrice = By.xpath("(//section[@class='service-basic-info']//label)[1]");
    By loc_lblSellingPrice = By.xpath("(//section[@class='service-basic-info']//label)[2]");
    By loc_lblDescription = By.xpath("//label[@for='serviceDescription']");
    By loc_lblCollection = By.xpath("//label[@for='productCollection']");
    By loc_lblImages = By.xpath("//button[@type='submit']//following-sibling::div[2]//h3");
    By loc_lblDrapAndDrop = By.xpath("//input[@type='file']/following-sibling::div[2]");
    By loc_lblLocationsAndTimesTitleAndDescription = By.xpath("//button[@type='submit']//following-sibling::div[3]/child::div/h3");
    By loc_lblLocations = By.xpath("//input[@id='locations']/ancestor::div[@class='variation-item']/preceding-sibling::label");
    By loc_lblLocation = By.xpath("(//section[contains(@class, 'gs-table-header-item')])[1]//span");
    By loc_lblTimeSlot = By.xpath("(//section[contains(@class, 'gs-table-header-item')])[2]//span");
    By loc_lblTimeSlots = By.xpath("//label[@class='has-tooltip']");
    By loc_tltTimeSlots = By.xpath("//label[@class='has-tooltip']/span/div");
    By loc_lblListOfLocationsAndTimeslots = By.cssSelector(".location-timeslots h3");
    By loc_lblNoLocationTimeSlot = By.xpath( "//div[@class='empty']//span");
    By loc_lblSEOSettings = By.xpath("//button[@type='submit']//following-sibling::div[4]/child::div/h3");
    By loc_lblLivePreview = By.xpath("//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span");
    By loc_tltLivePreview = By.xpath("//span[@class='gs-fake-link ']//parent::div//preceding-sibling::div//span/following-sibling::div//div");
    By loc_lblSEOTitle = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]/span");
    By loc_tltSEOTitle = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][1]//div/div");
    By loc_lblSEODescription = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]/span");
    By loc_tltSEODescription = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][2]//div/div");
    By loc_lblSEOKeywords = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]/span");
    By loc_tltSEOKeywords = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][3]//div/div");
    By loc_lblURLLink = By.xpath("//span[@class='gs-fake-link ']//parent::div/following-sibling::div[@class='mb-2'][4]/span");
    By loc_lblCreateNewService = By.xpath("//h5[@class='gs-page-title']");
    By loc_plhSelectCollections = By.xpath("//label[@for='productCollection']//following::div[contains(@class,'placeholder')]");
    By loc_lblErrorMessageServiceName = By.xpath("//label[@for='serviceName']//following-sibling::div[@class='invalid-feedback']");
    By loc_lblErrorMessageListingPrice = By.xpath("//input[@id='orgPrice']/following-sibling::div[@class='invalid-feedback']");
    By loc_lblErrorMessageSellingPrice = By.xpath("//input[@id='newPrice']/following-sibling::div[@class='invalid-feedback']");
    By loc_lblErrorMessageDescription = By.xpath("//input[@id='serviceDescription']/following-sibling::div[@class='invalid-feedback']");
    By loc_lblErrorMessageImages = By.cssSelector(".image-widget__error-wrapper div");
    By loc_lblErrorMessageLocations = By.xpath("//input[@id='locations']/following-sibling::div[@class='invalid-feedback']");
    By loc_lblErrorMessageTimeSlots = By.xpath("//input[@id='timeSlots']/following-sibling::div[@class='invalid-feedback']");
    By loc_txaDescription_btnCodeView = By.cssSelector("#html-1");
    By loc_txaCodeViewDescription = By.xpath("//textarea");
    By loc_lblMaximumErrorLocation = By.xpath("//div[@class='variation-item'][1]/div[2]");
    By loc_lblMaximumErrorTimeSlot = By.xpath("//div[@class='variation-item'][2]/div[2]");
    By loc_dlgNotification_btnClose = By.cssSelector( ".modal-footer button");
    By loc_dlgNotification_btnOK = By.cssSelector(".modal-footer button.gs-button__green");
    By loc_dlgNotification_lblMessage = By.cssSelector(".modal-body");

    /*--------------Edit service-------------*/
    By loc_btnEditTranslation = By.xpath("//h5[contains(@class,'product-name')]/following-sibling::div//button[contains(@class,'gs-button__gray--outline')][1]");
    By loc_dlgTranslate_txtName = By.cssSelector("input[name='informationName']");
    By loc_dlgTranslate_txaDescription = By.xpath("//label[@for='serviceDescription']//following::div[@class='fr-element fr-view'][2]");
    By loc_dlgTranslate_txtLocations = By.xpath("//div[@class='modal-body']//div[@class='row']//input");
    By loc_dlgTranslate_btnSave = By.cssSelector("button[name='submit-translate']");
    By loc_dlgTranslate_txtSEOTitle = By.cssSelector(".product-translate-modal #seoTitle");
    By loc_dlgTranslate_txtSEODescription = By.cssSelector(".product-translate-modal #seoDescription");
    By loc_dlgTranslate_txtSEOKeywords = By.cssSelector(".product-translate-modal #seoKeywords");
    By loc_dlgTranslate_txtURLLink = By.cssSelector(".product-translate-modal #seoUrl");
    By loc_btnActiveDeactive = By.xpath("(//button[contains(@class,'yellow')])[1]");
    By loc_lblStatus = By.xpath("//h5[contains(@class,'product-name')]/preceding-sibling::span");
    By loc_urlLivePreview = By.cssSelector(".seo-editor__live-preview-url a");
    By loc_lstImage = By.cssSelector(".image-view");
    By loc_lstIconRemoveImage = By.cssSelector(".image-widget__btn-remove");
    By loc_lstIconDeleteCollection = By.cssSelector(".product-form-collection-selector svg:not([role = 'img'])");
    By loc_btnDeleteService = By.xpath("(//button[contains(@class,'button__red')])[1]");
    By loc_txaDescription_icnViewMore = By.cssSelector("#moreMisc-1");
}
