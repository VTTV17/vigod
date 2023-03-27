package pages.storefront.userprofile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.header.HeaderSF;
import utilities.UICommonAction;

public class MyOrders extends HeaderSF {
	
	final static Logger logger = LogManager.getLogger(MyOrders.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    SoftAssert soft = new SoftAssert();
    
    public MyOrders (WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }
	
	@FindBy(css = ".my-order-container .group-order")
	List<WebElement> ORDERS; 
	
	@FindBy(css = ".order-item__review")
	List<WebElement> REVIEW_LINKTEXT;    
	
	String WRITE_REVIEW_LINKTEXT = "//div[@class='group-order__header__order-id' and contains(.,': #%s')]//ancestor::div[@class='group-order']//*[@class='order-item__review']";
    
	public List<List<String>> getOrderData() {
		List<List<String>> table = new ArrayList<>();
		for (WebElement eachOrder : ORDERS) {
			List<String> orderData = new ArrayList<>();
			Collections.addAll(orderData, commonAction.getText(eachOrder).split("\n"));
			table.add(orderData);
		}
		return table;
	}		

    public boolean isWriteReviewDisplayed(){
    	commonAction.sleepInMiliSecond(1000);
    	boolean isDisplayed = (REVIEW_LINKTEXT.size() > 0) ? true : false;
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
