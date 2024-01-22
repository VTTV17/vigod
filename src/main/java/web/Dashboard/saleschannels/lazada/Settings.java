package web.Dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

public class Settings {

	final static Logger logger = LogManager.getLogger(Settings.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public Settings(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_btnSave = By.cssSelector(".shopeeSettings button.gs-button__green");

    public Settings clickSave() {
    	commonAction.click(loc_btnSave);
    	logger.info("Clicked on 'Save' button.");
    	return this;
    }      
}
