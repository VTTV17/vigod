package pages.sellerapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.buyerapp.BuyerGeneral;
import utilities.UICommonMobile;

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
    By TOASTMESSAGE = By.xpath("//*[ends-with(@class,'Toast')]");
    By LOADING_ICON = By.xpath("//*[ends-with(@class,'ProgressBar')]");

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
        String text = common.getText(TOASTMESSAGE);
        logger.info("Retrieved toast message: " + text);
        return text;
    }
    public SellerGeneral verifyToastMessage(String expected){
        Assert.assertEquals(getToastMessage(),expected);
        logger.info("Verify toast message.");
        return this;
    }
    public SellerGeneral waitLoadingDisapear(){
        common.waitTillElementDisappear(common.getElement(LOADING_ICON,30),30);
        return this;
    }
}
