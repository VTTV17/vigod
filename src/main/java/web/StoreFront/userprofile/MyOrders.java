package web.StoreFront.userprofile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.header.HeaderSF;
import utilities.commons.UICommonAction;

public class MyOrders extends HeaderSF {
	
	final static Logger logger = LogManager.getLogger(MyOrders.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public MyOrders (WebDriver driver) {
        super(driver);
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
	
    By loc_tmpOrders = By.cssSelector(".my-order-container .group-order");
    By loc_lnkReview = By.cssSelector(".order-item__review");
	
	String WRITE_REVIEW_LINKTEXT = "//div[@class='group-order__header__order-id' and contains(.,': #%s')]//ancestor::div[@class='group-order']//*[@class='order-item__review']";
    
	public List<List<String>> getOrderData() {
		List<List<String>> table = new ArrayList<>();
		for (WebElement eachOrder : commonAction.getElements(loc_tmpOrders)) {
			List<String> orderData = new ArrayList<>();
			Collections.addAll(orderData, commonAction.getText(eachOrder).split("\n"));
			table.add(orderData);
		}
		return table;
	}		

    public boolean isWriteReviewDisplayed(){
    	commonAction.sleepInMiliSecond(1000);
    	boolean isDisplayed = (commonAction.getElements(loc_lnkReview).size() > 0) ? true : false;
    	logger.info("Is Write Review displayed: " + isDisplayed);
    	return isDisplayed;
    }
    
    public boolean isWriteReviewDisplayed(String orderId){
    	commonAction.sleepInMiliSecond(1000);
    	List<WebElement> el = driver.findElements(By.xpath(WRITE_REVIEW_LINKTEXT.formatted(orderId)));
    	boolean isDisplayed = (el.size() > 0) ? true : false;
    	logger.info("Is Write Review displayed: " + isDisplayed);
    	return isDisplayed;
    }	
	
    public ProductDetailPage clickWriteReview(String orderId){
    	WebElement el = driver.findElement(By.xpath(WRITE_REVIEW_LINKTEXT.formatted(orderId)));
    	commonAction.clickElement(el);
    	logger.info("Click on Write Review for order: " + orderId);
    	return new ProductDetailPage(driver);
    }

    
}
