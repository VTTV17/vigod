package pages.dashboard.settings.plans;

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

public class ForceLogOutDialog {

	final static Logger logger = LogManager.getLogger(ForceLogOutDialog.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public ForceLogOutDialog(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
    
	@FindBy(css = "[data-sherpherd='tour-guide-alert-button-close']")
	WebElement LOGOUT_BTN;
    
	public ForceLogOutDialog clickLogOutBtn() {
		commonAction.clickElement(LOGOUT_BTN);
		logger.info("Clicked on 'Logout' button in Force Logout dialog");
		return this;
	}
    

    

    
    
}
