package pages.dashboard.onlineshop;

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

public class Domains {

	final static Logger logger = LogManager.getLogger(Domains.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public Domains(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy (id = "subDomain")
    WebElement SUB_DOMAIN;
    
    @FindBy (css = ".btn-next")
    WebElement COMPLETE_BTN;
    
    public Domains inputSubDomain(String domain) {
    	commonAction.inputText(SUB_DOMAIN, domain);
    	logger.info("Input '" + domain + "' into Sub Domain field.");
    	return this;
    }
    
}
