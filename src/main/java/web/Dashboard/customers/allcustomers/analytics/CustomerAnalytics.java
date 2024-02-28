package web.Dashboard.customers.allcustomers.analytics;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.customers.allcustomers.AllCustomers;

public class CustomerAnalytics {
	
	final static Logger logger = LogManager.getLogger(CustomerAnalytics.class);

    WebDriver driver;
    UICommonAction commonAction;
    CustomerAnalyticsElement elements;
    
    public CustomerAnalytics (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        elements = new CustomerAnalyticsElement();
    }

	public CustomerAnalytics navigateByURL() {
		String url = DOMAIN + "/customers/overview";
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		return this;
	}
    
    public void clickUpdate(){
    	commonAction.click(elements.loc_lnkUpdate);
        logger.info("Clicked on 'Update' link text");
    }    
    
    public boolean isPaymentConfirmationDialogDisplayed(){
    	commonAction.sleepInMiliSecond(1000, "Wait for payment confirmation dialog to appear");
    	boolean isDisplayed = !commonAction.getElements(elements.loc_dlgConfirmPayment).isEmpty();
    	logger.info("isPaymentConfirmationDialogDisplayed: " + isDisplayed);
    	return isDisplayed;
    }    
}
