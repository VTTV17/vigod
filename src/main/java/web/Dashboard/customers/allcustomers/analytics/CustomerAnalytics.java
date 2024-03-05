package web.Dashboard.customers.allcustomers.analytics;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;

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
}
