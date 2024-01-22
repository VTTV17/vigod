package app.GoSeller.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import app.GoSeller.general.SellerGeneral;
import app.GoSeller.home.HomePage;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class SellerAccount {
    final static Logger logger = LogManager.getLogger(SellerAccount.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;
    private static String language;

    public SellerAccount (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By LANGUAGE = By.xpath("//*[ends-with(@resource-id,'llLanguage')]");
    By SELECTED_VIETNAMES_ICON = By.xpath("//*[ends-with(@resource-id,'ivSelectedVietnamese')]");
    By ENGLISH = By.xpath("//*[ends-with(@resource-id,'llEnglish')]");
    By VIETNAMESE = By.xpath("//*[ends-with(@resource-id,'llVietnamese')]");
    By SELECTED_ENGLISH_ICON = By.xpath("//*[ends-with(@resource-id,'ivSelectedEnglish')]");
    By OK_BTN_CONFIRM_POPUP = By.xpath("//*[ends-with(@resource-id,'tvRightButton')]");
    By CANCEL_BTN_CONFIRM_POPUP = By.xpath("//*[ends-with(@resource-id,'tvLeftButton')]");
    By HOME_TAB = By.xpath("//*[ends-with(@resource-id,'bottom_navigation_tab_home')]");
    public HomePage changeLanguage(String language){
        SellerAccount.language = language;
        new HomePage(driver).clickAccountTab();
        tapLanguage();
        common.sleepInMiliSecond(1000);
        switch (language){
            case "VIE" -> {
                if(common.isElementDisplay(SELECTED_VIETNAMES_ICON)){
                    new SellerGeneral(driver).tapHeaderLeftIcon();
                    return tapHomeTab();
                }else {
                    common.clickElement(VIETNAMESE);
                    common.clickElement(OK_BTN_CONFIRM_POPUP);
                }
            }
            case "ENG" ->{
                if(common.isElementDisplay(SELECTED_ENGLISH_ICON)){
                    new SellerGeneral(driver).tapHeaderLeftIcon();
                    return tapHomeTab();
                }else {
                    common.clickElement(ENGLISH);
                    common.clickElement(OK_BTN_CONFIRM_POPUP);
                }
            }
        }
        return new HomePage(driver);
    }
    public SellerAccount tapLanguage(){
        common.clickElement(LANGUAGE);
        logger.info("Tap on Language");
        return this;
    }
    public HomePage tapHomeTab(){
        common.clickElement(HOME_TAB);
        logger.info("Tap on Home tab.");
        return new HomePage(driver);
    }
    public String getLanguage() {
        return SellerAccount.language;
    }
}
