package web.StoreFront.userprofile.myorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utilities.commons.UICommonAction;
import web.StoreFront.detail_product.ProductDetailPage;

public class MyOrders {
	
	final static Logger logger = LogManager.getLogger(MyOrders.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    MyOrderElement elements;
    
    public MyOrders (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        elements = new MyOrderElement();
    }
    
	public List<List<String>> getOrderData() {
		List<List<String>> table = new ArrayList<>();
		for (WebElement eachOrder : commonAction.getElements(elements.loc_blkOrders)) {
			List<String> orderData = new ArrayList<>();
			Collections.addAll(orderData, commonAction.getText(eachOrder).split("\n"));
			table.add(orderData);
		}
		return table;
	}		

    public boolean isWriteReviewDisplayed(){
    	commonAction.sleepInMiliSecond(1000);
    	boolean isDisplayed = commonAction.getElements(elements.loc_lnkReview).size() > 0;
    	logger.info("Is Write Review displayed: " + isDisplayed);
    	return isDisplayed;
    }
    
    public boolean isWriteReviewDisplayed(String orderId){
    	commonAction.sleepInMiliSecond(1000);
    	boolean isDisplayed = driver.findElements(By.xpath(elements.loc_lnkSpecificReview.formatted(orderId))).size() > 0;
    	logger.info("Is Write Review displayed: " + isDisplayed);
    	return isDisplayed;
    }	
	
    public ProductDetailPage clickWriteReview(String orderId){
    	WebElement el = driver.findElement(By.xpath(elements.loc_lnkSpecificReview.formatted(orderId)));
    	commonAction.clickElement(el);
    	logger.info("Click on Write Review for order: " + orderId);
    	return new ProductDetailPage(driver);
    }

    
}
