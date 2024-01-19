package pages.storefront.checkout.ordercomplete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.UICommonAction;

import java.time.Duration;

public class OrderComplete {
    final static Logger logger = LogManager.getLogger(OrderComplete.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    OrderCompleteElement orderCompleteUI;
    public OrderComplete (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        orderCompleteUI = new OrderCompleteElement(driver);
        PageFactory.initElements(driver, this);
    }
    public void clickOnBackToMarket(){
        commonAction.click(orderCompleteUI.loc_btnBackToMarket);
        commonAction.sleepInMiliSecond(2000);
        logger.info("Click on Back to market button");
    }
    public OrderComplete verifyProductNames(String...productNames){
        for (int i=0;i<productNames.length;i++) {
            Assert.assertEquals(commonAction.getText(orderCompleteUI.loc_lst_lblProductName,i).toLowerCase(),productNames[i].toLowerCase());
        }
        logger.info("Verify Product name list.");
        return this;
    }
    public OrderComplete verifyDiscountAmount(String expected){
        Assert.assertEquals(String.join("",commonAction.getText(orderCompleteUI.loc_lblDiscountAmount).split(",")),expected);
        logger.info("Verify discount amount.");
        return this;
    }
    public String getShippingFeeAfterDiscount(){
        String shippingFee = commonAction.getText(orderCompleteUI.loc_lblShippingFee);
        logger.info("Get Shipping fee after discount: "+shippingFee);
        return shippingFee;
    }
    public OrderComplete verifyShippingFeeAfterDiscount(String expected){
        Assert.assertEquals(getShippingFeeAfterDiscount(),expected,"Expected shipping fee after discount is %s, but display %s".formatted(expected,getShippingFeeAfterDiscount()));
        logger.info("Verify shipping fee after discount");
        return this;
    }
}
