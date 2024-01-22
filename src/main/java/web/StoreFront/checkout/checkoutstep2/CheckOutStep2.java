package web.StoreFront.checkout.checkoutstep2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutstep3.CheckOutStep3;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

import java.time.Duration;

public class CheckOutStep2 extends GeneralSF {
    final static Logger logger = LogManager.getLogger(CheckOutStep2.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    CheckOutStep2Element checkOutStep2UI;
    public CheckOutStep2 (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        checkOutStep2UI = new CheckOutStep2Element(driver);
        PageFactory.initElements(driver, this);
    }
    public CheckOutStep3 clickOnNextButton(){
        commonAction.click(checkOutStep2UI.loc_btnNext);
        logger.info("Click on Next button.");
        waitTillLoaderDisappear();
        return new CheckOutStep3(driver);
    }

    /**
     *
     * @param shippingOtion:Giao Hang Nhanh, Shop self delivery,..
     * @return CheckOutStep2
     */
    public CheckOutStep2 selectShippingMethod(String shippingOtion) throws Exception {
        switch (shippingOtion){
            case "Self delivery" -> commonAction.selectByVisibleText(checkOutStep2UI.loc_ddlShippingMethod, PropertiesUtil.getPropertiesValueBySFLang("checkoutStep2.shopSelfDeliveryOption"));
            case "GHN" -> commonAction.selectByVisibleText(checkOutStep2UI.loc_ddlShippingMethod, PropertiesUtil.getPropertiesValueBySFLang("checkoutStep2.giaoHangNhanhOption"));
            case "GHTK" -> commonAction.selectByVisibleText(checkOutStep2UI.loc_ddlShippingMethod, PropertiesUtil.getPropertiesValueBySFLang("checkoutStep2.giaoHangTietKiemOption"));
            default -> throw new Exception("Check shipping method.");
        }
        logger.info("Select shipping method: "+shippingOtion);
        return this;
    }
    public CheckOutStep2 clickOnArrowIcon(){
        commonAction.click(checkOutStep2UI.loc_icnArrowShowSummaryPrice);
        logger.info("Click on Arrow icon to show/hide total summary.");
        return this;
    }
    public CheckOutStep2 verifyDicountAmount(String expected){
        Assert.assertEquals(String.join("",commonAction.getText(checkOutStep2UI.loc_blkSummaryPrice_lblDiscountAmount).split(",|-\s")),expected);
        logger.info("Verify discount amount.");
        return this;
    }
    public String getShippingFeeAfterDiscount(){
        String shippingFee = commonAction.getText(checkOutStep2UI.loc_lblShippingFee);
        String shippingFeeAfterDiscount = shippingFee.split("đ\s")[1];
        logger.info("Get Shipping fee after discount: "+shippingFeeAfterDiscount);
        return shippingFeeAfterDiscount;
    }
    public CheckOutStep2 verifyShippingFeeAfterDiscount(String expected){
        Assert.assertEquals(getShippingFeeAfterDiscount(),expected,"Expected shipping fee after discount is %s, but display %s".formatted(expected,getShippingFeeAfterDiscount()));
        logger.info("Verify shipping fee after discount");
        return this;
    }
    public CheckOutStep2 verifyProductName(String...productNamesExpected){
        for (int i=0;i<productNamesExpected.length;i++) {
            Assert.assertEquals(commonAction.getText(checkOutStep2UI.loc_lst_lblProductName,i).toLowerCase().trim(),productNamesExpected[i].toLowerCase().trim());
        }
        logger.info("Verify product name list.");
        return this;
    }
}
