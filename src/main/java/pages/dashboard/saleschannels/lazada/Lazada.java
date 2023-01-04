package pages.dashboard.saleschannels.lazada;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class Lazada {

	final static Logger logger = LogManager.getLogger(Lazada.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Lazada(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".lz-authen .btn-enable")
    WebElement ENABLE_LAZADA_BTN;	
    
    public Lazada clickEnableLazada() {
    	commonAction.clickElement(ENABLE_LAZADA_BTN);
    	logger.info("Clicked on 'Enable Lazada' button.");
    	return this;
    }      
    
}
