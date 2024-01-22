package web.Dashboard.settings.plans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class ForceLogOutDialog {

	final static Logger logger = LogManager.getLogger(ForceLogOutDialog.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public ForceLogOutDialog(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    
	By loc_btnLogout = By.cssSelector("[data-sherpherd='tour-guide-alert-button-close']");
    
	public ForceLogOutDialog clickLogOutBtn() {
		commonAction.click(loc_btnLogout);
		logger.info("Clicked on 'Logout' button in Force Logout dialog");
		return this;
	}
    
}
