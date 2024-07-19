package mobile.seller.android.general;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAndroid;

import java.time.Duration;

public class SellerGeneral {
    final static Logger logger = LogManager.getLogger(SellerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAndroid commonMobile;

    public SellerGeneral (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonMobile = new UICommonAndroid(driver);
    }
    By HEADER_PAGE_TITLE = By.xpath("//*[ends-with(@resource-id,'tvActionBarTitle')]");
    By HEADER_RIGHT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconRight')]");
    By HEADER_LEFT_ICON = By.xpath("//*[ends-with(@resource-id,'ivActionBarIconLeft')]");
    By SELECT_IMAGE_BTN = By.xpath("//*[ends-with(@resource-id,'rlSelectImages')]");
    By SELECT_IMAGE_ICON = By.xpath("//*[ends-with(@resource-id,'tvSelectIndex')]");
    By SAVE_ICON_SELECT_IMAGE = By.xpath("//*[ends-with(@resource-id,'fragment_choose_photo_dialog_btn_choose')]");
    By TOAST_MESSAGE = By.xpath("//*[ends-with(@resource-id,'tvContent')]");
    By LOADING_ICON = By.xpath("//*[ends-with(@class,'ProgressBar')]");
    By POPUP_MESSAGE = By.xpath("//*[ends-with(@resource-id,'tvDescription')]");
    By POPUP_LEFT_BTN = By.xpath("//*[ends-with(@resource-id,'tvLeftButton')]");
    By POPUP_RIGHT_BTN = By.xpath("//*[ends-with(@resource-id,'tvRightButton')]");

    public String getHeaderTitle(){
        String headerTitle = commonMobile.getText(HEADER_PAGE_TITLE);
        logger.info("Get page title: "+headerTitle);
        return headerTitle;
    }
    public SellerGeneral tapHeaderRightIcon(){
        commonMobile.click(HEADER_RIGHT_ICON);
        return this;
    }
    public SellerGeneral tapHeaderLeftIcon(){
        commonMobile.click(HEADER_LEFT_ICON);
        return this;
    }
    public SellerGeneral selectImage(){
        commonMobile.click(SELECT_IMAGE_BTN);
        commonMobile.click(SELECT_IMAGE_ICON);
        commonMobile.click(SAVE_ICON_SELECT_IMAGE);
        return this;
    }
    public String getToastMessage() {
        String text = commonMobile.getText(TOAST_MESSAGE);
        logger.info("Retrieved toast message: {}", text);
        return text;
    }
    public SellerGeneral verifyToastMessage(String expected){
        Assert.assertEquals(getToastMessage(),expected);
        logger.info("Verify toast message.");
        return this;
    }
    public String getPopupMessage(){
        String popupMessage = commonMobile.getText(POPUP_MESSAGE);
        logger.info("Get popup message: {}", popupMessage);
        return popupMessage;
    }
    public void tapLeftBtnOnPopup(){
        commonMobile.click(POPUP_LEFT_BTN);
    }
    public void tapRightBtnOnPopup(){
        commonMobile.click(POPUP_RIGHT_BTN);
    }
}
