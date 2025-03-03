package web.StoreFront.checkout.ordercomplete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.enums.Domain;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutOneStep.Checkout;

import java.time.Duration;

public class OrderComplete {
    final static Logger logger = LogManager.getLogger(OrderComplete.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    OrderCompleteElement orderCompleteUI;
    Domain domain;
    public OrderComplete (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        orderCompleteUI = new OrderCompleteElement(driver);
    }
    public OrderComplete (WebDriver driver, Domain domain) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        orderCompleteUI = new OrderCompleteElement(driver);
        this.domain = domain;
    }
    public void clickOnBackToMarket(){
        commonAction.click(orderCompleteUI.loc_btnBackToHome);
        new GeneralSF(driver).waitTillLoaderDisappear();
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
        if(domain.equals(Domain.BIZ)){
            if (!expected.contains(".")) expected = expected+".00";
            Assert.assertEquals(commonAction.getText(orderCompleteUI.loc_lblDiscountAmount).replaceAll("[^\\d.]", ""),expected);

        }else
            Assert.assertEquals(commonAction.getText(orderCompleteUI.loc_lblDiscountAmount).replaceAll("[^\\d]", ""),expected);
        logger.info("Verify discount amount.");
        return this;
    }
    public OrderComplete verifyDiscountAmount(double expected){
        System.out.println("Discount: "+commonAction.getText(orderCompleteUI.loc_lblDiscountAmount));
        if(domain.equals(Domain.BIZ)){
            Assert.assertEquals(commonAction.getText(orderCompleteUI.loc_lblDiscountAmount).replaceAll("[^\\d.]", ""),String.format("%.2f",expected));
        }else
            Assert.assertEquals(commonAction.getText(orderCompleteUI.loc_lblDiscountAmount).replaceAll("[^\\d.]", ""),String.format("%.0f",expected));
        logger.info("Verify discount amount.");
        return this;
    }
    public String getShippingFee(){
        String shippingFee = commonAction.getText(orderCompleteUI.loc_lblShippingFee).replaceAll("[^\\d.]", "");
        logger.info("Shipping free: "+shippingFee);
        return shippingFee;
    }
    public OrderComplete verifyShippingFee(String expected){
        if(domain.equals(Domain.BIZ)){
            Assert.assertEquals(getShippingFee(),expected,"Expected shipping fee after discount is %s, but display %s".formatted(expected,getShippingFee()));
        }else Assert.assertEquals(getShippingFee() +".00",expected,"Expected shipping fee after discount is %s, but display %s".formatted(expected,getShippingFee()));

        logger.info("Verify shipping fee after discount");
        return this;
    }
}
