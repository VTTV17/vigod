package pages.storefront.checkout.checkoutstep2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1Element;
import pages.storefront.checkout.checkoutstep3.CheckOutStep3;
import utilities.UICommonAction;

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
        commonAction.clickElement(checkOutStep2UI.NEXT_BUTTON);
        logger.info("Click on Next button.");
        waitTillLoaderDisappear();
        return new CheckOutStep3(driver);
    }

    /**
     *
     * @param shippingOtion:Giao Hang Nhanh, Shop self delivery,..
     * @return CheckOutStep2
     */
    public CheckOutStep2 selectShippingMethod(String shippingOtion){
        commonAction.selectByVisibleText(checkOutStep2UI.SELECT_SHIPPING_METHOD,shippingOtion);
        logger.info("Select shipping method: "+shippingOtion);
        return this;
    }
}
