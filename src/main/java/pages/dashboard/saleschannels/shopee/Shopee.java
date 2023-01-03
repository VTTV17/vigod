package pages.dashboard.saleschannels.shopee;

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

public class Shopee {

	final static Logger logger = LogManager.getLogger(Shopee.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Shopee(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sp-account .gs-button__green")
    WebElement CONNECT_SHOPEE_BTN;	
    
    public Shopee clickConnectShopee() {
    	commonAction.clickElement(CONNECT_SHOPEE_BTN);
    	logger.info("Clicked on 'Connect Shopee' button.");
    	return this;
    }      
    
}
