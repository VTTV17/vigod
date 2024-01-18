package pages.storefront.checkout.checkoutstep3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.checkout.ordercomplete.OrderComplete;
import utilities.UICommonAction;

import java.time.Duration;

public class CheckOutStep3 extends GeneralSF {
    final static Logger logger = LogManager.getLogger(CheckOutStep3.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    CheckOutStep3Element checkOutStep3UI;
    public CheckOutStep3 (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        checkOutStep3UI = new CheckOutStep3Element(driver);
        PageFactory.initElements(driver, this);
    }
    public OrderComplete clickOnNextButton(){
        commonAction.click(checkOutStep3UI.loc_btnNext);
        logger.info("Click on Next button.");
        waitTillLoaderDisappear();
        return new OrderComplete(driver);
    }
    public CheckOutStep3 clickOnArrowIcon(){
        commonAction.click(checkOutStep3UI.loc_icnArrowShowSummaryPrice);
        logger.info("Click on Arrow icon to show/hide total summary.");
        return this;
    }
    public CheckOutStep3 verifyDicountAmount(String expected){
        Assert.assertEquals(String.join("",commonAction.getText(checkOutStep3UI.loc_blkSummaryPrice_lblDiscountAmount).split(",|-\s")),expected);
        logger.info("Verify discount amount.");
        return this;
    }
    public String getShippingFeeAfterDiscount(){
        String shippingFee = commonAction.getText(checkOutStep3UI.loc_lblShippingFee);
        logger.info("Get Shipping fee after discount: "+shippingFee);
        return shippingFee;
    }
    public CheckOutStep3 verifyShippingFreeAfterDiscount(String expected){
        Assert.assertEquals(getShippingFeeAfterDiscount(),expected,"Expected shipping fee after discount is %s, but display %s".formatted(expected,getShippingFeeAfterDiscount()));
        logger.info("Verify shipping fee after discount");
        return this;
    }
    public CheckOutStep3 verifyProductName(String...productNamesExpected){
        for (int i=0;i<productNamesExpected.length;i++) {
            Assert.assertEquals(commonAction.getText(checkOutStep3UI.loc_lst_lblProductName,i).toLowerCase().trim(),productNamesExpected[i].toLowerCase().trim());
        }
        logger.info("Verify product name list.");
        return this;
    }
}
