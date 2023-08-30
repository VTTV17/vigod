package pages.buyerapp.account.myorders.orderdetail;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonMobile;

public class OrderDetails {
	final static Logger logger = LogManager.getLogger(OrderDetails.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonMobile commonAction;

	int defaultTimeout = 5;

	public OrderDetails(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonMobile(driver);
	}

	By ORDERID = By.xpath("//*[ends-with(@resource-id,'tv_order_history_header_id')]");
	By WRITEREVIEW_BTN = By.xpath("//*[ends-with(@resource-id,'item_order_history_prod_rl_write_review')]");
	By RATING_STARS = By.xpath("//*[ends-with(@resource-id,'activity_write_review_rating_view')]/*");
	By REVIEW_TITLE = By.xpath("//*[ends-with(@resource-id,'activity_write_review_edt_title')]");
	By REVIEW_DESCRIPTION = By.xpath("//*[ends-with(@resource-id,'activity_write_review_edt_review_content')]");
	By SUBMIT_REVIEW_BTN = By.xpath("//*[ends-with(@resource-id,'activity_write_review_rl_submit')]");
    
	public String getOrderId() {
        String value = commonAction.getText(ORDERID);
        logger.info("Retrieved Order Id: " + value);
        return value;
    }
    
    public boolean isWriteReviewBtnDisplayed() {
    	commonAction.getElement(ORDERID); //Workaround to check if everything is present before going forward
        boolean isDisplayed = !commonAction.isElementNotDisplay(commonAction.getElements(WRITEREVIEW_BTN));
        logger.info("Is 'Write Review' button displayed: " + isDisplayed);
        return isDisplayed;
    }
    
    public OrderDetails clickWriteReviewBtn() {
    	commonAction.clickElement(WRITEREVIEW_BTN);
    	logger.info("Clicked on 'Write Reviews' button.");
    	return this;
    }   	
    
    public OrderDetails inputRating(int rating) {
        commonAction.clickElement(commonAction.getElements(RATING_STARS).get(rating-1));
        logger.info("Rated stars : " + rating);
        return this;
    }

    public OrderDetails inputReviewTitle(String reviewTitle) {
        commonAction.inputText(REVIEW_TITLE, reviewTitle);
        logger.info("Input review title: " + reviewTitle);
        return this;
    }

    public OrderDetails inputReviewDescription(String reviewDescription) {
        commonAction.inputText(REVIEW_DESCRIPTION, reviewDescription);
        logger.info("Input review description: " + reviewDescription);
        return this;
    }

    public OrderDetails clickSubmitReviewBtn() {
        commonAction.clickElement(SUBMIT_REVIEW_BTN);
        logger.info("Clicked on Submit Review button");
        return this;
    }

    public OrderDetails leaveReview(int rating, String reviewTitle, String reviewDescription) {
    	inputReviewTitle(reviewTitle);
    	inputReviewDescription(reviewDescription);
    	inputRating(rating);
        clickSubmitReviewBtn();
        return this;
    }  	
	
}
