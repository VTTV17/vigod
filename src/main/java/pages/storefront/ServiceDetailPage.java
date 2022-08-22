package pages.storefront;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.UICommonAction;
import utilities.assert_customize.AssertCustomize;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class ServiceDetailPage {
    WebDriverWait wait;
    WebDriver driver;
    UICommonAction commons;
    AssertCustomize assertCustomize;
    int countFalse = 0;
    final static Logger logger = LogManager.getLogger(ServiceDetailPage.class);

    public ServiceDetailPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h3[@rv-text='models.serviceName']")
    WebElement SERVICE_NAME;

    @FindBy(css = ".price-box .old-price")
    List<WebElement> LISTING_PRICE;

    @FindBy(css = ".price-box .price")
    List<WebElement> SELLING_PRICE;

    @FindBy(xpath = "//button[contains(@class,'buynow')]")
    List<WebElement> BOOK_NOW_BTN;
    @FindBy(xpath = "//button[contains(@class,'clicktocart')]")
    List<WebElement> ADD_TO_CART_BTN;
    @FindBy(xpath = "//button[contains(@class,'contact-now')]")
    WebElement CONTACT_NOW_BTN;
    @FindBy(xpath = "//select[@name='location']")
    WebElement LOCATION_DROPDOWN;

    @FindBy(xpath = "//select[@name='timeSlot']")
    WebElement TIMESLOT_DROPDOWN;
    @FindBy(xpath = "//div[contains(@class,'product-description')]")
    WebElement SERVICE_DESCRIPTION;
    @FindBy(xpath = "//div[contains(@class,'breadcrumbs')]//span/a")
    WebElement COLLECTION_LINK;
    @FindBy(xpath = "//meta[@name='title']")
    WebElement META_TITLE;
    @FindBy(xpath = "//meta[@name='description']")
    WebElement META_DESCRIPTION;
    @FindBy(xpath = "//meta[@name='keywords']")
    WebElement META_KEYWORD;


    public ServiceDetailPage verifyServiceName(String nameExpected) throws IOException {
        String nameActual = commons.getText(SERVICE_NAME);
        Assert.assertEquals(nameActual, nameExpected, "Service name display: " + nameActual + " not match with expected " + nameExpected);
        logger.info("Verify service name display on detail page");
        return this;
    }

    public ServiceDetailPage verifyListingPrice(String listingPriceExpected) throws IOException {
        String listingPriceActual = commons.getText(LISTING_PRICE.get(0));
        Assert.assertEquals(String.join("", listingPriceActual.split(",")), listingPriceExpected + "đ");
        logger.info("Verify service listing price on detail page");
        return this;
    }

    public ServiceDetailPage verifySellingPrice(String sellingPriceExpected) throws IOException {
        String sellingPriceActual = commons.getText(SELLING_PRICE.get(0));
        Assert.assertEquals(String.join("", sellingPriceActual.split(",")), sellingPriceExpected);
        logger.info("Verify service selling price on detail page");
        return this;
    }

    public ServiceDetailPage verifyLocations(String... locationsExpected) {
        List<WebElement> locationOptions = commons.getAllOptionInDropDown(LOCATION_DROPDOWN);
        Assert.assertEquals(locationOptions.size(), locationsExpected.length);
        for (int i = 0; i < locationOptions.size(); i++) {
            Assert.assertEquals(commons.getText(locationOptions.get(i)), locationsExpected[i]);
        }
        logger.info("All location are displayed");
        return this;
    }

    public ServiceDetailPage verifyTimeSlots(String... timeSlotsExpected) {
        List<WebElement> timeSlotsOptions = commons.getAllOptionInDropDown(TIMESLOT_DROPDOWN);
        Assert.assertEquals(timeSlotsOptions.size(), timeSlotsExpected.length);
        for (int i = 0; i < timeSlotsOptions.size(); i++) {
            Assert.assertEquals(commons.getText(timeSlotsOptions.get(i)), timeSlotsExpected[i]);
        }
        logger.info("All timeslots are displayed");
        return this;
    }
    public ServiceDetailPage verifyBookNowAndAddToCartButtonDisplay(){
        Assert.assertTrue(BOOK_NOW_BTN.get(0).isDisplayed(),"Book Now button is displayed");
        Assert.assertTrue(ADD_TO_CART_BTN.get(0).isDisplayed(),"Add to cart button is displayed");
        logger.info("Book now and add to cart button are displayed");
        return this;
    }
    public ServiceDetailPage verifyServiceDescription(String expected){
        Assert.assertEquals(commons.getText(SERVICE_DESCRIPTION),expected);
        logger.info("Service description is shown");
        return this;
    }
    public ServiceDetailPage verifyCollectionLink(int collectionQuantity,List<String> expected){
        if(collectionQuantity==1) {
            Assert.assertEquals(commons.getText(COLLECTION_LINK), expected.get(0));
        }else {
            Assert.assertEquals(commons.getText(COLLECTION_LINK), "All Services");
        }
        logger.info("Verify collection link");
        return this;
    }
    public ServiceDetailPage verifyContactNowButtonDisplay(){
        Assert.assertTrue(CONTACT_NOW_BTN.isDisplayed());
        logger.info("Verfy book now button display");
        return this;
    }
    public ServiceDetailPage verifyBookNowAndAddToCartButtonNotDisplay(){
        Assert.assertTrue(commons.isElementNotDisplay(BOOK_NOW_BTN));
        Assert.assertTrue(commons.isElementNotDisplay(ADD_TO_CART_BTN));
        logger.info("Verify book now button and add to cart button not display");
        return this;
    }
    public ServiceDetailPage verifyPriceNotDisplay(){
        Assert.assertTrue(commons.isElementNotDisplay(SELLING_PRICE));
        Assert.assertTrue(commons.isElementNotDisplay(LISTING_PRICE));
        logger.info("Verify selling price and listing price not display");
        return this;
    }
    public ServiceDetailPage verifySEOInfo(String SEOTitle, String SEODescription, String SEOKeyword, String serviceName,String serviceDescription){
        String titleActual = commons.getElementAttribute(META_TITLE,"content");
        if (SEOTitle==""){
            Assert.assertEquals(titleActual,serviceName);
        }else {
            Assert.assertEquals(titleActual,SEOTitle);
        }
        String SEOTitleActual = commons.getElementAttribute(META_DESCRIPTION,"content");
        if (SEODescription == "") {
            Assert.assertEquals(SEOTitleActual,serviceDescription);
        }else {
            Assert.assertEquals(SEOTitleActual,SEODescription);
        }
        String SEOKeywordActual = commons.getElementAttribute(META_KEYWORD,"content");
        if (SEOKeyword==""){
            Assert.assertEquals(SEOKeywordActual,serviceName);
        }else {
            Assert.assertEquals(SEOKeywordActual,SEOKeyword);
        }
        logger.info("Verify SEO info");
        return this;
    }
    public void clickOnCollectionLink(){
        commons.clickElement(COLLECTION_LINK);
        logger.info("Click on collection link to go to collection page");
    }
    public ServiceDetailPage verifyNavigateToServiceDetailBySEOUrl(String domain, String SEOUrl, String seviceName) throws IOException {
        commons.openNewTab();
        commons.switchToWindow(1);
        commons.navigateToURL(domain+SEOUrl);
        verifyServiceName(seviceName);
        commons.closeTab();
        commons.switchToWindow(0);
        return this;
    }
}
