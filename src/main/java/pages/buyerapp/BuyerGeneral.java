package pages.buyerapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.UICommonAction;

import java.time.Duration;

public class BuyerGeneral extends UICommonAction{
    final static Logger logger = LogManager.getLogger(BuyerGeneral.class);

    WebDriver driver;
    WebDriverWait wait;


    public BuyerGeneral(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }
    By el_loading_icon = By.xpath("//*[contains(@resource-id,'fragment_loading_progress_bar')]");
    public BuyerGeneral waitLoadingDisappear(){
        waitTillElementDisappear(getElement(el_loading_icon),30);
        return this;
    }
}
