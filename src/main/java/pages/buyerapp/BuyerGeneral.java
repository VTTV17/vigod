package pages.buyerapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonMobile;

import java.time.Duration;

public class BuyerGeneral extends UICommonMobile {
    final static Logger logger = LogManager.getLogger(BuyerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;


    public BuyerGeneral(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    By BACK_ICON = By.xpath("//android.widget.ImageView[contains(@resource-id,'action_bar_basic_img_back')]");
//    By el_loading_icon = By.xpath("//*[contains(@resource-id,'fragment_loading_progress_bar')]");
//    By el_loading = By.id("com.mediastep.shop0003:id/fragment_loading_progress_bar");
//
//    public BuyerGeneral waitLoadingDisappear(){
//        waitTillElementDisappear(getElement(el_loading,30),30);
//        return this;
//    }
    public BuyerGeneral waitInMiliSecond(long milisecond){
        sleepInMiliSecond(milisecond);
        return this;
    }
    public BuyerGeneral clickOnBackIcon(){
        clickElement(BACK_ICON);
        logger.info("Click on back icon");
        return this;
    }

}
