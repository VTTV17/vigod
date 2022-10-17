package pages.storefront;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.storefront.login.LoginPage;
import utilities.UICommonAction;

import java.time.Duration;

public class GeneralSF extends HeaderSF {
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commons;
    final static Logger logger = LogManager.getLogger(GeneralSF.class);

    public GeneralSF(WebDriver driver){
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        commons = new UICommonAction(driver);
        PageFactory.initElements(driver,this);
    }
    @FindBy(css = ".loader")
    WebElement SPINNER;
    public GeneralSF waitTillLoaderDisappear() {
        commons.waitForElementInvisible(SPINNER, 15);
        logger.info("Loader has finished loading");
        return this;
    }
}
