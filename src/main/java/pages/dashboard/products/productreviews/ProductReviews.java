package pages.dashboard.products.productreviews;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import pages.storefront.detail_product.ProductDetailPage;
import utilities.UICommonAction;

public class ProductReviews {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(ProductReviews.class);

	public ProductReviews(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(css = ".gss-content-header .uik-checkbox__label.green")
	WebElement ENABLE_PRODUCT_REVIEW_TOGGLE;

	@FindBy(css = ".d-desktop-flex .gs-search-box__wrapper input")
	WebElement SEARCH_BOX;
	
	@FindBy(css = ".d-desktop-block .gs-table-body .shortest-row")
	List<WebElement> TABLE_ROWS;

	public ProductReviews navigate() {
		new HomePage(driver).navigateToPage("Products", "Product Reviews");
		return this;
	}	
	
	public boolean isProductReviewsEnabled() {
		return ENABLE_PRODUCT_REVIEW_TOGGLE.findElement(By.xpath("./preceding-sibling::input")).isSelected();
	}	
	
	/**
	 * Enable Product Reviews feature.
	 * @return
	 */
	public ProductReviews enableProductReviews() {
		if (isProductReviewsEnabled()) {
			logger.info("Product Reviews have already been enabled.");
			return this;
		}
		new HomePage(driver).hideFacebookBubble();
		commons.clickElement(ENABLE_PRODUCT_REVIEW_TOGGLE);
		logger.info("Enabled Product Reviews.");
		new HomePage(driver).getToastMessage();
		return this;
	}
	
	/**
	 * Disable Product Reviews feature.
	 * @return
	 */
	public ProductReviews disableProductReviews() {
		if (!isProductReviewsEnabled()) {
			logger.info("Product Reviews have already been disabled.");
			return this;
		}
		new HomePage(driver).hideFacebookBubble();
		commons.clickElement(ENABLE_PRODUCT_REVIEW_TOGGLE);
		logger.info("Disabled Product Reviews.");
		new HomePage(driver).getToastMessage();
		return this;
	}
	
	public ProductReviews inputSearchTerm(String searchTerm) {
		commons.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}	
	
	/**
	 * Get a table of product reviews. Each row contains a list of data with the 0-indexed element representing product name,
	 * the 1-indexed element representing product rating, the 2-indexed element representing product review title,
	 * the 3-indexed element representing product description, the 4-indexed element representing customer name, 
	 * the 5-indexed element representing created date, the 6-indexed element representing the review's status,
	 * @return a list of lists of strings displaying review data in form of a table.
	 */
	public List<List<String>> getReviewTable() {
		List<List<String>> table = new ArrayList<>();
		for (WebElement row : TABLE_ROWS) {
			List<String> rowData = new ArrayList<>();
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'product-name')]")).getText());
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'product-rating')]")).getText());
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'product-review')]/span[@class='title']")).getText());
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'product-review')]/span[contains(@class,'description')]")).getText());
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'customer-name')]")).getText());
			rowData.add(row.findElement(By.xpath("./div[contains(@class,'created-date')]")).getText());
			rowData.add(String.valueOf(row.findElement(By.xpath(".//label[contains(@class,'lastest-button')]/input")).isSelected()));
			table.add(rowData);
		}
		return table;
	}	
	
	public ProductDetailPage clickNavigationIcon(int reviewIndex) {
		new HomePage(driver).hideFacebookBubble();
		commons.clickElement(TABLE_ROWS.get(reviewIndex).findElement(By.xpath(".//*[contains(@class,'first-button')]")));
		logger.info("Clicked on %s-indexed review to navigate to product detail on SF.".formatted(reviewIndex));
		return new ProductDetailPage(driver);
	}	
	
	public WebElement approveToggleBtn(int reviewIndex) {
		return TABLE_ROWS.get(reviewIndex).findElement(By.xpath(".//*[contains(@class,'lastest-button')]"));
	}		
	
	public boolean isReviewApproved(int reviewIndex) {
		commons.sleepInMiliSecond(1000);
		return approveToggleBtn(reviewIndex).findElement(By.xpath("./input")).isSelected();
	}		
	
	/**
	 * Allow a review to appear on Storefront.
	 * @param reviewIndex Eg. 0,1,2...
	 * @return
	 */
	public ProductReviews approveReview(int reviewIndex) {
		if (isReviewApproved(reviewIndex)) {
			logger.info("%s-indexed review has already been approved.".formatted(reviewIndex));
			return this;
		}
		new HomePage(driver).hideFacebookBubble();
		commons.clickElement(approveToggleBtn(reviewIndex));
		logger.info("Allow %s-indexed review to appear on SF.".formatted(reviewIndex));
		new HomePage(driver).getToastMessage();
		return this;
	}	
	
	/**
	 * Hide a review on Storefront.
	 * @param reviewIndex Eg. 0,1,2...
	 * @return
	 */
	public ProductReviews disapproveReview(int reviewIndex) {
		if (!isReviewApproved(reviewIndex)) {
			logger.info("%s-indexed review has already been hidden.".formatted(reviewIndex));
			return this;
		}
		new HomePage(driver).hideFacebookBubble();
		commons.clickElement(approveToggleBtn(reviewIndex));
		logger.info("Hide %s-indexed review from buyers on SF.".formatted(reviewIndex));
		new HomePage(driver).getToastMessage();
		return this;
	}	
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToManageReviews(String permission) {
		if (permission.contentEquals("A")) {
			new ProductReviews(driver).inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    	
    
}
