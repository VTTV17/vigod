package pages.buyerapp.servicedetail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.REQUIRE_LOGIN_POPUP)),"Require login popup not show.");
        logger.info("Verify require login popup show.");
        return this;
    }
    public BuyerServiceDetail verifyContactPopUpShow(){
        Assert.assertTrue(isElementDisplay(getElement(serviceDetailEl.CONTACT_POPUP)),"Contact popup not show.");
        logger.info("Verify contact popup show.");
        return this;
    }
}
