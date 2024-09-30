package app.GoSeller.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.commons.UICommonMobile;

import java.time.Duration;

public class SellerGeneral {
    final static Logger logger = LogManager.getLogger(SellerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;

    public SellerGeneral (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    By HEADER_PAGE_TITLE = By.xpath("//*[ends-with(@resource-id,'tvActionBarTitle')]");
    By HEADER_RIGHT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
    By HEADER_LEFT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconLeft')]");
    By SELECT_IMAGE_BTN = By.xpath("//*[ends-with(@resource-id,'rlSelectImages')]");
    By SELECT_IMAGE_ICON = By.xpath("//*[ends-with(@resource-id,'tvSelectIndex')]");
    By SAVE_ICON_SELECT_IMAGE = By.xpath("//*[ends-with(@resource-id,'fragment_choose_photo_dialog_btn_choose')]");
    By TOASTMESSAGE = By.xpath("//*[ends-with(@resource-id,'tvContent')]");
    By LOADING_ICON = By.xpath("//*[ends-with(@class,'ProgressBar')]");
    By POPUP_MESSAGE = By.xpath("//*[ends-with(@resource-id,'tvDescription')]");
    By POPUP_LEFT_BTN = By.xpath("//*[ends-with(@resource-id,'tvLeftButton')]");
    By POPUP_RIGHT_BTN = By.xpath("//*[ends-with(@resource-id,'tvRightButton')]");

    public String getHeaderTitle(){
        String headerTitle = common.getText(HEADER_PAGE_TITLE);
        logger.info("Get page title: "+headerTitle);
        return headerTitle;
    }
    public SellerGeneral tapHeaderRightIcon(){
        common.clickElement(HEADER_RIGHT_ICON);
        return this;
    }
    public SellerGeneral tapHeaderLeftIcon(){
        common.clickElement(HEADER_LEFT_ICON);
        return this;
    }
    public SellerGeneral selectImage(){
        common.clickElement(SELECT_IMAGE_BTN);
        common.clickElement(SELECT_IMAGE_ICON);
        common.clickElement(SAVE_ICON_SELECT_IMAGE);
        return this;
    }
    public String getToastMessage() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        String text = common.getText(TOASTMESSAGE);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        logger.info("Retrieved toast message: " + text);
        return text;
    }
    public SellerGeneral verifyToastMessage(String expected){
        Assert.assertEquals(getToastMessage(),expected);
        logger.info("Verify toast message.");
        return this;
    }
    public String getPopupMessage(){
        String popupMessage = common.getText(POPUP_MESSAGE);
        logger.info("Get popup message: "+popupMessage);
        return popupMessage;
    }
    public void tapLeftBtnOnPopup(){
        common.clickElement(POPUP_LEFT_BTN);
    }
    public void tapRightBtnOnPopup(){
        common.clickElement(POPUP_RIGHT_BTN);
    }
}
