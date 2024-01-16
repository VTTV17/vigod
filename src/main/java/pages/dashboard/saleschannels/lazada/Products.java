package pages.dashboard.saleschannels.lazada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Products {

	final static Logger logger = LogManager.getLogger(Products.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public Products(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_txtSearch = By.cssSelector(".shopee-product-management .uik-input__input");
	
	public Products inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearch, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
    
}
