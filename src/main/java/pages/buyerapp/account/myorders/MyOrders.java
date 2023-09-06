package pages.buyerapp.account.myorders;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.buyerapp.account.myorders.orderdetail.OrderDetails;
import utilities.UICommonMobile;

public class MyOrders {
	final static Logger logger = LogManager.getLogger(MyOrders.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public MyOrders(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	By SHIPPED_TAB = By.xpath("//*[@content-desc='Đã giao hàng' or @content-desc='Shipped']");
	By ORDER = By.xpath("//*[ends-with(@resource-id,'fragment_order_history_rlv_products_ordered')]/*");
    
    public MyOrders clickShippedTab() {
    	commonAction.clickElement(SHIPPED_TAB, defaultTimeout);
    	logger.info("Clicked on 'Shipped' tab.");
    	return this;
    }   	
    
    public OrderDetails clickOrder() {
    	commonAction.clickElement(ORDER, defaultTimeout);
    	
    	//Sometimes the element is still present. The code below helps handle this intermittent issue
    	boolean isElementPresent = true;
    	for (int i=0; i<3; i++) {
    		if (commonAction.getElements(ORDER).size() == 0) {
    			isElementPresent = false;
    			break;
    		}
    		commonAction.sleepInMiliSecond(500);
    	}
    	if (isElementPresent) {
    		commonAction.clickElement(ORDER, defaultTimeout);
    	}
    	
    	logger.info("Clicked on first order to see its details.");
    	return new OrderDetails(driver);
    }   	
	
}
