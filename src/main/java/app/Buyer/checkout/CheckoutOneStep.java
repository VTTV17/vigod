package app.Buyer.checkout;

import app.Buyer.buyergeneral.BuyerGeneral;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonMobile;
import utilities.constant.Constant;
import utilities.model.dashboard.storefront.AddressInfo;
import web.StoreFront.checkout.ordercomplete.OrderComplete;

import java.time.Duration;

public class CheckoutOneStep extends CheckoutOneStepElement{
    final static Logger logger = LogManager.getLogger(CheckoutOneStep.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;


    public CheckoutOneStep(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
    }
    public DeliveryAddress goToDeliveryAddress(){
        common.click(loc_delivery_icnArrow);
        logger.info("CLick on arrow icon on Delivery address component");
        return new DeliveryAddress(driver);
    }
    public String getFullAddress(){
        String fullAddress = common.getText(loc_tvFullAddress);
        logger.info("Get full address: "+fullAddress);
        return fullAddress;
    }
    public CheckoutOneStep verifyAddress(AddressInfo expectedAddressInfo) {
        boolean isVietnam = expectedAddressInfo.getCountry().equals(Constant.VIETNAM);
        String fullAddressActual = getFullAddress();
        String fullAddressExpected;
        if(isVietnam){
            fullAddressExpected = expectedAddressInfo.getAddress()+ ", " +expectedAddressInfo.getWard()+ ", " + expectedAddressInfo.getDistrict()
                    + ", " + expectedAddressInfo.getCityProvince()+ ", "+ expectedAddressInfo.getCountry();
        }else fullAddressExpected = expectedAddressInfo.getStreetAddress()+ ", " +expectedAddressInfo.getAddress2()+ ", " +expectedAddressInfo.getCity()
                + ", " +expectedAddressInfo.getStateRegionProvince()+ ", " +expectedAddressInfo.getZipCode()+ ", " +expectedAddressInfo.getCountry();
        Assert.assertEquals(fullAddressActual,fullAddressExpected,"[Failed] Actual: %s \nExpected: %s".formatted(fullAddressActual,fullAddressExpected));
        logger.info("Verified full address on Checkout One step page.");
        return this;
    }
    public CompletedOrder tapOnCheckoutBtn(){
        common.click(loc_btnCheckout);
        logger.info("Tap on Checkout button");
        new BuyerGeneral(driver).waitLoadingDisapear();
        return new CompletedOrder(driver);
    }

}
