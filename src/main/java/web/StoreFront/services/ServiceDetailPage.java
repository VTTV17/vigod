package web.StoreFront.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
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

    public ServiceDetailPage(WebDriver driver) throws Exception {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
        PageFactory.initElements(driver, this);
    }
    By loc_lblServiceName = By.xpath("//h1[@rv-text='models.serviceName']");
    By loc_lblListingPrice = By.cssSelector(".price-box .old-price");
    By loc_lblSellingPrice = By.cssSelector(".price-box .price");
    By loc_btnBookNow = By.xpath("//button[contains(@class,'buynow')]");
    By loc_btnAddToCart = By.xpath("//button[contains(@class,'clicktocart')]");
    By loc_btnContactNow = By.xpath("//button[contains(@class,'contact-now')]");
    By loc_ddlLocation = By.xpath("//select[@name='location']");
    By loc_ddlTimeSlot = By.xpath("//select[@name='timeSlot']");
    By loc_cntServiceDescription = By.xpath("//div[contains(@class,'product-description')]");
    By loc_lnkCollection = By.xpath("//div[contains(@class,'breadcrumbs')]//span/a");
    By loc_lblMetaTitle = By.xpath("//meta[@name='title']");
    By loc_lblMetaDescription = By.xpath("//meta[@name='description']");
    By loc_lblMetaKeyword = By.xpath("//meta[@name='keywords']");
    By loc_lst_imgService = By.cssSelector(".slider-detail-img img");
    By loc_lst_imgServiceThumb = By.cssSelector(".slider-detail-thumb img");
    String sfAllServicesTxt = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.allServicesTxt");

    public ServiceDetailPage verifyServiceName(String nameExpected) throws IOException {
        String nameActual = commons.getText(loc_lblServiceName);
        Assert.assertEquals(nameActual, nameExpected, "Service name display: " + nameActual + " not match with expected " + nameExpected);
        logger.info("Verify service name display on detail page");
        return this;
    }

    public ServiceDetailPage verifyListingPrice(String listingPriceExpected) throws IOException {
        String listingPriceActual = commons.getText(loc_lblListingPrice,0);
        listingPriceActual=String.join("", listingPriceActual.split(","));
        Assert.assertEquals(listingPriceActual.subSequence(0,listingPriceActual.length()-1), listingPriceExpected);
        logger.info("Verify service listing price on detail page");
        return this;
    }

    public ServiceDetailPage verifySellingPrice(String sellingPriceExpected) throws IOException {
        String sellingPriceActual = commons.getText(loc_lblSellingPrice,0);
        sellingPriceActual=String.join("", sellingPriceActual.split(","));
        Assert.assertEquals(sellingPriceActual.subSequence(0,sellingPriceActual.length()-1), sellingPriceExpected);
        logger.info("Verify service selling price on detail page");
        return this;
    }

    public ServiceDetailPage verifyLocations(String... locationsExpected) {
        List<WebElement> locationOptions = commons.getAllOptionInDropDown(loc_ddlLocation,0);
        Assert.assertEquals(locationOptions.size(), locationsExpected.length);
        for (int i = 0; i < locationOptions.size(); i++) {
            Assert.assertEquals(commons.getText(locationOptions.get(i)), locationsExpected[i]);
        }
        logger.info("All location are displayed");
        return this;
    }

    public ServiceDetailPage verifyTimeSlots(String... timeSlotsExpected) {
        List<WebElement> timeSlotsOptions = commons.getAllOptionInDropDown(loc_ddlTimeSlot,0);
        Assert.assertEquals(timeSlotsOptions.size(), timeSlotsExpected.length);
        for (int i = 0; i < timeSlotsOptions.size(); i++) {
            Assert.assertEquals(commons.getText(timeSlotsOptions.get(i)), timeSlotsExpected[i]);
        }
        logger.info("All timeslots are displayed");
        return this;
    }
    public ServiceDetailPage verifyBookNowAndAddToCartButtonDisplay(){
        Assert.assertTrue(commons.getElements(loc_btnBookNow).get(0).isDisplayed(),"Book Now button is displayed");
        Assert.assertTrue(commons.getElements(loc_btnAddToCart).get(0).isDisplayed(),"Add to cart button is displayed");
        logger.info("Book now and add to cart button are displayed");
        return this;
    }
    public ServiceDetailPage verifyServiceDescription(String expected){
        Assert.assertEquals(commons.getText(loc_cntServiceDescription),expected);
        logger.info("Service description is shown");
        return this;
    }
    public ServiceDetailPage verifyCollectionLink(int collectionQuantity,List<String> expected){
        if(collectionQuantity==1) {
            Assert.assertEquals(commons.getText(loc_lnkCollection), expected.get(0));
        }else {
            Assert.assertEquals(commons.getText(loc_lnkCollection), sfAllServicesTxt);
        }
        logger.info("Verify collection link");
        return this;
    }
    public ServiceDetailPage verifyContactNowButtonDisplay(){
        Assert.assertTrue(commons.isElementDisplay(loc_btnContactNow));
        logger.info("Verfy book now button display");
        return this;
    }
    public ServiceDetailPage verifyBookNowAndAddToCartButtonNotDisplay(){
        Assert.assertTrue(commons.isElementNotDisplay(loc_btnBookNow));
        Assert.assertTrue(commons.isElementNotDisplay(loc_btnAddToCart));
        logger.info("Verify book now button and add to cart button not display");
        return this;
    }
    public ServiceDetailPage verifyPriceNotDisplay(){
        Assert.assertTrue(commons.isElementNotDisplay(loc_lblSellingPrice));
        Assert.assertTrue(commons.isElementNotDisplay(loc_lblListingPrice));
        logger.info("Verify selling price and listing price not display");
        return this;
    }
    public ServiceDetailPage verifySEOInfo(String SEOTitle, String SEODescription, String SEOKeyword, String serviceName,String serviceDescription){
        String titleActual = commons.getAttribute(loc_lblMetaTitle,"content");
        if (SEOTitle==""){
            Assert.assertEquals(titleActual,serviceName);
        }else {
            Assert.assertEquals(titleActual,SEOTitle);
        }
        String SEODescActual = commons.getAttribute(loc_lblMetaDescription,"content");
        if (SEODescription == "") {
            Assert.assertEquals(SEODescActual,serviceDescription);
        }else {
            Assert.assertEquals(SEODescActual,SEODescription);
        }
        String SEOKeywordActual = commons.getAttribute(loc_lblMetaKeyword,"content");
        if (SEOKeyword==""){
            Assert.assertEquals(SEOKeywordActual,serviceName);
        }else {
            Assert.assertEquals(SEOKeywordActual,SEOKeyword);
        }
        logger.info("Verify SEO info");
        return this;
    }
    public void clickOnCollectionLink(){
        commons.click(loc_lnkCollection);
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
    public ServiceDetailPage verifyServiceImagesDisplay(){
        commons.sleepInMiliSecond(3000);
        List<WebElement> serviceImgElements = commons.getElements(loc_lst_imgService);
        if(serviceImgElements.size()>0){
            Assert.assertTrue(commons.isElementDisplay(serviceImgElements.get(0)),"Image not display.");
        }else Assert.assertTrue(false,"Image element not found");
        List<WebElement> serviceImgThumbElements = commons.getElements(loc_lst_imgServiceThumb);
        if(serviceImgThumbElements.size()>0){
            for (WebElement el:serviceImgThumbElements) {
                Assert.assertTrue(commons.isElementDisplay(el),"Image thumbnail not display.");
            }
        }else Assert.assertTrue(false,"Image thumbnail element not found");
        return this;
    }
    public ServiceDetailPage verifyDescriptionAsHTMLFormat(String expected){
        Assert.assertEquals(commons.getAttribute(loc_cntServiceDescription,"innerHTML"),expected);
        logger.info("Verify description has html as inputted");
        return this;
    }
    public ServiceDetailPage verifyServiceListSize(int expected){
        commons.sleepInMiliSecond(1000);
        Assert.assertEquals(commons.getElements(loc_lst_imgServiceThumb).size(),expected);
        logger.info("Verify service image thumbnail list size");
        return this;
    }
}
