package web.Dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

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
