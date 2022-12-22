package pages.dashboard.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.framework.qual.FromStubFile;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;
import utilities.data.DataGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateServicePage {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    DataGenerator generate;
    final static Logger logger = LogManager.getLogger(CreateServicePage.class);

    public CreateServicePage(WebDriver driver){
        this.driver=driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        commons = new UICommonAction(driver);
        generate = new DataGenerator();
        PageFactory.initElements(driver,this);
    }
    @FindBy(xpath = "(//button[contains(@class,'btn-save')])[1]")
    WebElement SAVE_BTN;
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
    @FindBy(xpath = " //div[@name='serviceDescription']//div[@class='fr-wrapper show-placeholder']/div")
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

    public CreateServicePage inputServiceName(String serviceName){
        commons.inputText(SERVICE_NAME,serviceName);
        logger.info("Input "+serviceName+ " into Service name field");
        return this;
    }

    public CreateServicePage inputListingPrice (String listingPrice){
        commons.inputText(LISTING_PRICE,listingPrice);
        logger.info("Input "+ listingPrice + " into Listing price field");
        return this;
    }

    public String inputSellingPrice (String listingPrice, String discountPercent){
        int listingPricePars = Integer.parseInt(listingPrice);
        int sellingPrice = listingPricePars - listingPricePars * Integer.parseInt(discountPercent)/100;
        commons.inputText(SELLING_PRICE,  String.valueOf(sellingPrice));
        logger.info("Input "+ sellingPrice+ " into Selling price field");
        return String.valueOf(sellingPrice+"đ");
    }
    public CreateServicePage checkOnShowAsListingService(){
        commons.checkTheCheckBoxOrRadio(SHOW_AS_LISTING_CBX_VALUE,SHOW_AS_LISTING_CBX_ACTION);
        logger.info("Check on Show as listing service checkbox");
        return this;
    }
    public  CreateServicePage uncheckOnShowAsListingService(){
        commons.uncheckTheCheckboxOrRadio(SHOW_AS_LISTING_CBX_VALUE,SHOW_AS_LISTING_CBX_ACTION);
        logger.info("Uncheck on Show as listing service checkbox");
        return this;
    }
    public CreateServicePage inputServiceDescription(String description){
        commons.inputText(SERVICE_DESCRIPTION,description);
        logger.info("Input "+description+ " into description field");
        return this;
    }
    public CreateServicePage inputCollections(int quantity ){
        for (int i=0;i<quantity;i++) {
            commons.clickElement(COLLECTION_FORM);
            logger.info("Click on collection form");
            commons.clickElement(COLLECTION_SUGGESTION.get(0));
            logger.info("Select collection");
        }
        return this;
    }
    public List<String>  getSelectedCollection(){
        List<String> selectedCollections =   new ArrayList<>();

        for (WebElement element:SELECTED_COLLECTIONS) {
            selectedCollections.add(element.getText());
        }
        logger.debug("selectedCollections: "+selectedCollections);
        return selectedCollections;
    }
    public CreateServicePage uploadImages(String...fileNames){
        commons.uploadMultipleFile(IMAGE_INPUT,"serviceimages",fileNames);
        logger.info("Upload multiple file: "+ Arrays.toString(fileNames));
        return this;
    }
    public CreateServicePage inputLocations(String...locations){
        for (String loctaion:locations) {
            commons.inputText(LOCATION,loctaion+"\n");
            logger.info("Input "+loctaion+ " into Location field");
        }
        return this;
    }
    public CreateServicePage inputTimeSlots(String...timeSlots){
        for (String timeSlot:timeSlots) {
            commons.inputText(TIME_SLOTS,timeSlot +"\n");
            logger.info("Input %s into TimeSlot field".formatted(timeSlots));
        }
        return this;
    }
    public CreateServicePage inputSEOTitle (String SEOTitle){
        commons.inputText(SEO_TITLE, SEOTitle);
        logger.info("Input "+SEOTitle+" into SEO title field");
        return this;
    }
    public CreateServicePage inputSEODescription (String SEODesciption){
        commons.inputText(SEO_DESCRIPTION, SEODesciption);
        logger.info("Input "+SEODesciption+ " into SEO description field");
        return this;
    }
    public CreateServicePage inputSEOKeyword (String SEOKeyword){
        commons.inputText(SEO_KEYWORDS,SEOKeyword);
        logger.info("Input "+SEOKeyword+ " into SEO keyword field");
        return this;
    }
    public CreateServicePage inputSEOUrl (String SEOUrl){
        commons.inputText(SEO_URL, SEOUrl);
        logger.info("Input "+SEOUrl+ " into SEO url field");
        return this;
    }
    public CreateServicePage clickSaveBtn (){
        commons.clickElement(SAVE_BTN);;
        logger.info("Click on Save button");
        return this;
    }

    public void verifyCreateSeviceSuccessfulMessage() {
        String message= commons.getText(POPUP_MESSAGE);
        Assert.assertEquals(message,"Sản phẩm được tạo thành công!");
        logger.info("Create service successfully popup is shown");
    }








}
