package pages.dashboard.saleschannels.lazada;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
    UICommonAction commonAction;
    
    public Lazada(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnEnableLazada = By.cssSelector(".lz-authen .btn-enable");
    
    public Lazada clickEnableLazada() {
    	commonAction.click(loc_btnEnableLazada);
    	logger.info("Clicked on 'Enable Lazada' button.");
    	return this;
    }      
    
}
