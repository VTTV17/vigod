package pages.buyerapp.servicedetail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyerServiceDetail extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerServiceDetail.class);
    WebDriver driver;
    WebDriverWait wait;
    BuyerServiceDetailElement serviceDetailEl;
    public BuyerServiceDetail(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        serviceDetailEl = new BuyerServiceDetailElement(driver);
    }
    public BuyerServiceDetail verifyServiceName(String expected){
        Assert.assertEquals(getText(serviceDetailEl.NAME).toLowerCase(),expected.toLowerCase());
        logger.info("Verify service name");
        return this;
    }
    public BuyerServiceDetail verifyServicePrice(String expected){
        String actual = getText(serviceDetailEl.PRICE);
        Assert.assertEquals(String.join("",actual.split(",")),expected);
        logger.info("Verify service price.");
        return this;
    }
    public BuyerServiceDetail verifyLocations(String... locationsExpected) {
        tapLocationsTab();
        List<WebElement> locationEls= getElements(serviceDetailEl.LOCATIONS);
        List<String> locationsActual = new ArrayList<>();
        List<String> locationsExList = new ArrayList<>();
        Assert.assertEquals(locationEls.size(), locationsExpected.length);
        for (int i = 0; i < locationEls.size(); i++) {
            locationsActual.add(getText(locationEls.get(i)));
        }
        for (int i = 0; i < locationsExpected.length; i++) {
            locationsExList.add(locationsExpected[i]);
        }
        Collections.sort(locationsActual);
        Collections.sort(locationsExList);
        Assert.assertEquals(locationsActual,locationsExList);
        logger.info("All location are displayed");
        return this;
    }
    public BuyerServiceDetail verifyServiceDescription(String expected){
        Assert.assertEquals(getText(serviceDetailEl.DESCRIPTION),expected);
        logger.info("Verify service description.");
        return this;
    }
    public BuyerServiceDetail verifyPriceNotDisplay(){
        Assert.assertTrue(isElementNotDisplay(getElements(serviceDetailEl.PRICE)));
        logger.info("Verify service price not show.");
        return this;
    }
    public BuyerServiceDetail verifyContactNowBtnDisplay(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.CONTACT_NOW_BTN)));
        logger.info("Verify contact now button show.");
        return this;
    }
    public BuyerServiceDetail verifyBookNowBtnDisplay(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.BOOK_NOW_BTN)));
        logger.info("Verify book now button show.");
        return this;
    }
    public BuyerServiceDetail tapOnBookingNow(){
        clickElement(serviceDetailEl.BOOK_NOW_BTN);
        logger.info("Tap on booking now button.");
        return this;
    }
    public BuyerServiceDetail tapOnContactNow(){
        clickElement(serviceDetailEl.CONTACT_NOW_BTN);
        logger.info("Tap on contact now button.");
        return this;
    }
    public BuyerServiceDetail verifyRequireLoginPopUpShow(){
        sleepInMiliSecond(1000);
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.REQUIRE_LOGIN_POPUP)),"Require login popup not show.");
        logger.info("Verify require login popup show.");
        return this;
    }
    public BuyerServiceDetail verifyContactPopUpShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.CONTACT_POPUP)),"Contact popup not show.");
        logger.info("Verify contact popup show.");
        return this;
    }
    public BuyerServiceDetail verifyAddToCartBtnNotShow(){
        Assert.assertTrue(isElementNotDisplay(getElements(serviceDetailEl.ADD_TO_CART)));
        logger.info("Verify add to cart not show.");
        return this;
    }
    public BuyerServiceDetail verifyAddToCartBtnShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.ADD_TO_CART)));
        logger.info("Verify add to cart show.");
        return this;
    }
    public BuyerServiceDetail verifyTextByLanguage() throws Exception {
        Assert.assertEquals(getText(serviceDetailEl.DESCRIPTION_TAB), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.descriptionMobile"));
        Assert.assertEquals(getText(serviceDetailEl.LOCATION_TAB), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.locationsTab"));
        Assert.assertEquals(getText(serviceDetailEl.SIMILAR_TAB), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.similarServices"));
        Assert.assertEquals(getText(serviceDetailEl.DESCRIPTION_TITLE), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.descriptionMobile"));
        swipeByCoordinatesInPercent(0.75,0.75,0.25,0.25);
        Assert.assertEquals(getText(serviceDetailEl.LOCATION_TITLE), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.locationTitle"));
        Assert.assertEquals(getText(serviceDetailEl.SIMILAR_TITLE), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.similarServices"));
        return this;
    }
    public BuyerServiceDetail verifyTextBookNowOrContactNowBtn(boolean isEnableListing) throws Exception {
        if(isEnableListing){
            Assert.assertEquals(getText(serviceDetailEl.CONTACT_NOW_BTN), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.contactNowBtn"));
        }else Assert.assertEquals(getText(serviceDetailEl.BOOK_NOW_BTN), PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.bookNowBtn"));
        return this;
    }
    public BuyerServiceDetail verifyLocationNumber(int quantityExpected) throws Exception {
        Assert.assertEquals(getText(serviceDetailEl.LOCATION_NUMBER),PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.locationNumber").formatted(quantityExpected));
        return this;
    }
    public BuyerServiceDetail verifySimilarSectionDisplay(){
        sleepInMiliSecond(1000);
        swipeByCoordinatesInPercent(0.75,0.75,0.75,0.25);
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.SIMILAR_ITEM_LIST)));
        logger.info("Check similar item list show.");
        return this;
    }
    public BuyerServiceDetail tapDescriptionTab(){
        clickElement(serviceDetailEl.DESCRIPTION_TAB);
        logger.info("Tap on description tab.");
        return this;
    }
    public BuyerServiceDetail tapLocationsTab(){
        clickElement(serviceDetailEl.LOCATION_TAB);
        logger.info("Tap on locations tab.");
        return this;
    }
    public BuyerServiceDetail tapSimilarTab(){
        clickElement(serviceDetailEl.SIMILAR_TAB);
        logger.info("Tap on services similar tab.");
        return this;
    }
    public BuyerServiceDetail verifyDescriptionSectionShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.DESCRIPTION_SECTION)));
        logger.info("Verify description section is shown");
        return this;
    }
    public BuyerServiceDetail verifyLocationSectionShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.LOCATION_SECTION)));
        logger.info("Verify location section is shown");
        return this;
    }
    public BuyerServiceDetail verifySimilarSectionShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.SIMILAR_ITEM_LIST)));
        logger.info("Verify similar section is shown");
        return this;
    }
    public BuyerServiceDetail tapAddToCart(){
        clickElement(serviceDetailEl.ADD_TO_CART);
        logger.info("Tap on Add to cart button.");
        return this;
    }
}
