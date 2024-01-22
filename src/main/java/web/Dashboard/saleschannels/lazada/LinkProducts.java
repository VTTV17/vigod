package web.Dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class LinkProducts {

	final static Logger logger = LogManager.getLogger(LinkProducts.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public LinkProducts(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_txtSearch = By.xpath("//div[@class='ml-auto']/preceding-sibling::*//input");
    
	public LinkProducts inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearch, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
    
}
