package pages.dashboard.service;

import com.beust.ah.A;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.framework.qual.FromStubFile;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.character_limit.CharacterLimit;
import utilities.data.DataGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateServicePage extends HomePage{
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    DataGenerator generate;
    final static Logger logger = LogManager.getLogger(CreateServicePage.class);
    CreateServiceElement createServiceUI;
    PropertiesUtil propertiesUtil;
    String createSuccessfullyMess = PropertiesUtil.getPropertiesValueByDBLang("services.create.successullyMessage");
    public CreateServicePage(WebDriver driver) throws Exception {
        super(driver);
        this.driver=driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        commons = new UICommonAction(driver);
        generate = new DataGenerator();
        createServiceUI = new CreateServiceElement(driver);
        PageFactory.initElements(driver,this);
    }
    public CreateServicePage inputServiceName(String serviceName){
        commons.inputText(createServiceUI.SERVICE_NAME,serviceName);
        logger.info("Input "+serviceName+ " into Service name field");
        return this;
    }

    public CreateServicePage inputListingPrice (String listingPrice){
        commons.inputText(createServiceUI.LISTING_PRICE,listingPrice);
        logger.info("Input "+ listingPrice + " into Listing price field");
        return this;
    }

    public String inputSellingPrice (String listingPrice, String discountPercent){
        int listingPricePars = Integer.parseInt(listingPrice);
        int sellingPrice = listingPricePars - listingPricePars * Integer.parseInt(discountPercent)/100;
        commons.inputText(createServiceUI.SELLING_PRICE,  String.valueOf(sellingPrice));
        logger.info("Input "+ sellingPrice+ " into Selling price field");
        return String.valueOf(sellingPrice);
    }
    public String inputSellingPrice (String sellingPrice){
        commons.inputText(createServiceUI.SELLING_PRICE, sellingPrice);
        logger.info("Input "+ sellingPrice+ " into Selling price field");
        return sellingPrice;
    }
    public CreateServicePage checkOnShowAsListingService(){
        commons.checkTheCheckBoxOrRadio(createServiceUI.SHOW_AS_LISTING_CBX_VALUE,createServiceUI.SHOW_AS_LISTING_CBX_ACTION);
        logger.info("Check on Show as listing service checkbox");
        return this;
    }
    public  CreateServicePage uncheckOnShowAsListingService(){
        commons.uncheckTheCheckboxOrRadio(createServiceUI.SHOW_AS_LISTING_CBX_VALUE,createServiceUI.SHOW_AS_LISTING_CBX_ACTION);
        logger.info("Uncheck on Show as listing service checkbox");
        return this;
    }
    public CreateServicePage inputServiceDescription(String description){
        commons.inputText(createServiceUI.SERVICE_DESCRIPTION,description);
        logger.info("Input "+description+ " into description field");
        return this;
    }
    public CreateServicePage inputCollections(int quantity ){
        for (int i=0;i<quantity;i++) {
            commons.clickElement(createServiceUI.COLLECTION_FORM);
            logger.info("Click on collection form");
            commons.clickElement(createServiceUI.COLLECTION_SUGGESTION.get(0));
            logger.info("Select collection");
        }
        return this;
    }
    public List<String>  getSelectedCollection(){
        List<String> selectedCollections =   new ArrayList<>();

        for (WebElement element:createServiceUI.SELECTED_COLLECTIONS) {
            selectedCollections.add(element.getText());
        }
        logger.debug("selectedCollections: "+selectedCollections);
        return selectedCollections;
    }
    public CreateServicePage uploadImages(String...fileNames){
        commons.uploadMultipleFile(createServiceUI.IMAGE_INPUT,"serviceimages",fileNames);
        logger.info("Upload multiple file: "+ Arrays.toString(fileNames));
        return this;
    }
    public CreateServicePage inputLocations(String...locations){
        for (String loctaion:locations) {
            commons.inputText(createServiceUI.LOCATION,loctaion+"\n");
            logger.info("Input "+loctaion+ " into Location field");
        }
        return this;
    }
    public CreateServicePage inputTimeSlots(String...timeSlots){
        for (String timeSlot:timeSlots) {
            commons.inputText(createServiceUI.TIME_SLOTS,timeSlot +"\n");
            logger.info("Input %s into TimeSlot field".formatted(timeSlots));
            commons.sleepInMiliSecond(100);
        }
        return this;
    }

    public CreateServicePage inputSEOTitle (String SEOTitle){
    	if (commons.isElementVisiblyDisabled(createServiceUI.SEO_TITLE.findElement(By.xpath("./ancestor::div[contains(@class,'gs-widget  seo-editor')]/descendant::*[1]")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(createServiceUI.SEO_TITLE));
    		return this;
    	}
        commons.inputText(createServiceUI.SEO_TITLE, SEOTitle);
        logger.info("Input "+SEOTitle+" into SEO title field");
        return this;
    }

    public CreateServicePage inputSEODescription (String SEODesciption){
        commons.inputText(createServiceUI.SEO_DESCRIPTION, SEODesciption);
        logger.info("Input "+SEODesciption+ " into SEO description field");
        return this;
    }
    public CreateServicePage inputSEOKeyword (String SEOKeyword){
        commons.inputText(createServiceUI.SEO_KEYWORDS,SEOKeyword);
        logger.info("Input "+SEOKeyword+ " into SEO keyword field");
        return this;
    }
    public CreateServicePage inputSEOUrl (String SEOUrl){
        commons.inputText(createServiceUI.SEO_URL, SEOUrl);
        logger.info("Input "+SEOUrl+ " into SEO url field");
        return this;
    }
    public CreateServicePage clickSaveBtn (){
        commons.clickElement(createServiceUI.SAVE_BTN);;
        logger.info("Click on Save button");
        return this;
    }

	public String getSEOTitle() {
		String title = commons.getElementAttribute(createServiceUI.SEO_TITLE, "value");
		logger.info("Retrieved SEO Title: %s".formatted(title));
		return title;
	}    
    
    public void verifyCreateSeviceSuccessfulMessage() {
        commons.sleepInMiliSecond(2000);//wait loading
        String message= commons.getText(createServiceUI.POPUP_MESSAGE);
        Assert.assertEquals(message,createSuccessfullyMess);
        logger.info("Create service successfully popup is shown");
    }
    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(commons.getText(createServiceUI.CREATE_NEW_SERVICE_TITLE),propertiesUtil.getPropertiesValueByDBLang("services.create.pageTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.SAVE_BTN),propertiesUtil.getPropertiesValueByDBLang("services.create.saveBtn"));
        Assert.assertEquals(commons.getText(createServiceUI.CANCEL_BTN),propertiesUtil.getPropertiesValueByDBLang("services.create.cancelBtn"));
        Assert.assertEquals(commons.getText(createServiceUI.BASIC_INFOMATION_TITLE),propertiesUtil.getPropertiesValueByDBLang("services.create.basicInformationTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.SERVICE_NAME_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.serviceNameLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.LISTING_PRICE_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.listingPriceLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.SELLING_PRICE_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.sellingPriceLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.SHOW_AS_LISTING_CBX_ACTION),propertiesUtil.getPropertiesValueByDBLang("services.create.showAsListingServiceTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.DESCRIPTION_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.descriptionLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.COLLECTIONS_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.collectionLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.SELECT_COLLECTIONS_HINT_TXT),propertiesUtil.getPropertiesValueByDBLang("services.create.selectCollectionsHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.IMAGES_TITLE),propertiesUtil.getPropertiesValueByDBLang("services.create.imagesLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.DRAG_DROP_PHOTO_TXT),propertiesUtil.getPropertiesValueByDBLang("services.create.drapAndDropTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.LOCATIONS_AND_TIME_TITLE_AND_DESCRIPNTION),propertiesUtil.getPropertiesValueByDBLang("services.create.locations&TimesTitleAndDescription"));
        Assert.assertEquals(commons.getText(createServiceUI.LOCATIONS_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.locationsLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.LOCATION,"placeholder"),propertiesUtil.getPropertiesValueByDBLang("services.create.inputLocationHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.TIME_SLOTS_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.timeSlotsLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.TIME_SLOTS_TOOLTIP,"data-original-title"),propertiesUtil.getPropertiesValueByDBLang("services.create.titmeSlotsTooltipTxt"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.TIME_SLOTS,"placeholder"),propertiesUtil.getPropertiesValueByDBLang("services.create.inputTimeSlotsHintTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.LIST_LOCATIONS_TIMESLOTS_TITLE),propertiesUtil.getPropertiesValueByDBLang("services.create.listLocationAndTimeslotsTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.LOCATION_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.locationLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.TIMESLOT_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.timeSlotLbl"));
        Assert.assertEquals(commons.getText(createServiceUI.NO_LOCATION_TIMESLOT_TXT),propertiesUtil.getPropertiesValueByDBLang("services.create.noLocationAndTimeSlotTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.SEO_SETTINGS_TITLE),propertiesUtil.getPropertiesValueByDBLang("services.create.seoSettingsTitle"));
        Assert.assertEquals(commons.getText(createServiceUI.LIVE_PREVIEW_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.livePreviewLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.LIVE_PREVIEW_TOOLTIP,"data-original-title"),propertiesUtil.getPropertiesValueByDBLang("services.create.livePreviewTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.SEO_TITLE_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.seoTitleLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.SEO_TITLE_TOOLTIP,"data-original-title"),propertiesUtil.getPropertiesValueByDBLang("services.create.seoTitleTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.SEO_DESCRIPTION_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.seoDescriptionLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.SEO_DESCRIPTION_TOOLTIP,"data-original-title"),propertiesUtil.getPropertiesValueByDBLang("services.create.seoDescriptionTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.SEO_KEYWORDS_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.seoKeywordsLbl"));
        Assert.assertEquals(commons.getElementAttribute(createServiceUI.SEO_KEYWORD_TOOLTIP,"data-original-title"),propertiesUtil.getPropertiesValueByDBLang("services.create.seoKeywordsTooltipTxt"));
        Assert.assertEquals(commons.getText(createServiceUI.URL_LINK_LBL),propertiesUtil.getPropertiesValueByDBLang("services.create.urlLinkLbl"));
    }
    public CreateServicePage checkErrorSaveWithBlankField() throws Exception {
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_SERVICE_NAME),PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertFalse(commons.isElementDisplay(createServiceUI.ERROR_MESSAGE_LISTING_PRICE));
        Assert.assertFalse(commons.isElementDisplay(createServiceUI.ERROR_MESSAGE_SELLING_PRICE));
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_DESCRIPTION),PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_IMAGES),PropertiesUtil.getPropertiesValueByDBLang("services.create.imagesFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_LOCATIONS),PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_TIMESLOTS),PropertiesUtil.getPropertiesValueByDBLang("services.create.inputFieldEmptyError"));
        return this;
    }
    public CreateServicePage checkErrorWhenInputListingPriceOutOfRange() throws Exception {
        inputListingPrice("-1");
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_LISTING_PRICE),PropertiesUtil.getPropertiesValueByDBLang("services.create.listingPrice.minimumRequiredError"));
        commons.sleepInMiliSecond(500);
        inputListingPrice("100000000000");
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_LISTING_PRICE),PropertiesUtil.getPropertiesValueByDBLang("services.create.listingPrice.maximumRequiredError"));
        return this;
    }
    public CreateServicePage checkErrorWhenInputSellingPriceOutOfRange() throws Exception {
        String listingPrice ="10000";
        inputListingPrice(listingPrice);
        inputSellingPrice("-1");
        clickSaveBtn();
        Assert.assertEquals(commons.getText(createServiceUI.ERROR_MESSAGE_SELLING_PRICE),PropertiesUtil.getPropertiesValueByDBLang("services.create.sellingPrice.minimumRequiredError"));
        commons.sleepInMiliSecond(500);
        inputSellingPrice("1000000");
        String sellingPriceActual=String.join("", commons.getText(createServiceUI.ERROR_MESSAGE_SELLING_PRICE).split(","));
        Assert.assertEquals(sellingPriceActual,PropertiesUtil.getPropertiesValueByDBLang("services.create.sellingPrice.maximumRequiredError").formatted(listingPrice));
        return this;
    }
    public CreateServicePage checkMaximumCharacterForServiceNameField(){
        inputServiceName(Constant.TEXT_101_CHAR);
        String serviceNameDisplay = commons.getElementAttribute(createServiceUI.SERVICE_NAME,"value");
        Assert.assertEquals(serviceNameDisplay.length(), CharacterLimit.MAX_CHAR_SERVICE_NAME);
        return this;
    }
    public CreateServicePage checkMaximumCharacterForSEOTitleField(){
        inputSEOTitle(Constant.TEXT_201_CHAR);
        String SEOTitleDisplay = getSEOTitle();
        Assert.assertEquals(SEOTitleDisplay.length(),CharacterLimit.MAX_CHAR_SEO_TITLE);
        return this;
    }
    public CreateServicePage checkMaximumCharacterForSEODescriptionField(){
        inputSEODescription(Constant.TEXT_326_CHAR);
        String SEODescriptionDisplay = commons.getElementAttribute(createServiceUI.SEO_DESCRIPTION,"value");
        Assert.assertEquals(SEODescriptionDisplay.length(),CharacterLimit.MAX_CHAR_SEO_DESCRIPTION);
        return this;
    }
}
