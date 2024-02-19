package web.Dashboard.products.productreviews;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import web.Dashboard.pagination.Pagination;
import web.StoreFront.detail_product.ProductDetailPage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

public class ProductReviews extends ProductReviewElement {
	WebDriver driver;
	UICommonAction commons;
	HomePage homePage;

	final static Logger logger = LogManager.getLogger(ProductReviews.class);

	public ProductReviews(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		homePage = new HomePage(driver);
	}

	public ProductReviews navigate() {
		new HomePage(driver).navigateToPage("Products", "Product Reviews");
		return this;
	}	
	
	/**
	 * Check whether Product Reviews feature is enabled
	 */
	public boolean isProductReviewsEnabled() {
		return commons.getElement(new ByChained(loc_btnEnableReviewToggle, loc_btnToggleStatus)).isSelected();
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
		homePage.hideFacebookBubble();
		commons.click(loc_btnEnableReviewToggle);
		logger.info("Enabled Product Reviews.");
		homePage.getToastMessage();
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
		homePage.hideFacebookBubble();
		commons.click(loc_btnEnableReviewToggle);
		logger.info("Disabled Product Reviews.");
		homePage.getToastMessage();
		return this;
	}
	
	public ProductReviews inputSearchTerm(String searchTerm) {
		commons.sendKeys(loc_txtSearchProduct, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		homePage.waitTillSpinnerDisappear();
		return this;
	}	

	public ProductReviews selectSortCondition(String condition) {
		homePage.hideFacebookBubble();
		commons.click(loc_btnFilter);
		commons.click(By.xpath(loc_ddlSortCondition.formatted(condition)));
		logger.info("Selected filter condition: %s.".formatted(condition));
		homePage.waitTillSpinnerDisappear1();
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
		for (int i=0; i<commons.getElements(loc_tmpRecords).size(); i++) {
			List<String> rowData = new ArrayList<>();
			rowData.add(commons.getText(loc_tblProductNameColumn, i));
			rowData.add(commons.getText(loc_tblProductRatingColumn, i));
			rowData.add(commons.getText(loc_tblReviewTitleColumn, i));
			rowData.add(commons.getText(loc_tblReviewDescriptionColumn, i));
			rowData.add(commons.getText(loc_tblCustomerNameColumn, i));
			rowData.add(commons.getText(loc_tblCreatedDateColumn, i));
			rowData.add(String.valueOf(commons.getElement(loc_tblStatus, i).isSelected()));
			table.add(rowData);
		}
		return table;
	}	
	
	public List<List<String>> getAllReviewTable() {
		List<List<String>> table = new ArrayList<List<String>>();
		
		Pagination pagi = new Pagination(driver);
		
		while(true) {
			List<List<String>> dbReviews = getReviewTable();
			table.addAll(dbReviews);	
			if (!pagi.isNextBtnDisplayed()) break;
			pagi.clickNextBtn();
		}
		return table;
	}	
	
	public ProductDetailPage clickNavigationIcon(int reviewIndex) {
		homePage.hideFacebookBubble();
		commons.click(loc_btnNavigation, reviewIndex);
		logger.info("Clicked on %s-indexed review to navigate to product detail on SF.".formatted(reviewIndex));
		return new ProductDetailPage(driver);
	}	
	
	/**
	 * Check whether a review with a specific index is approved
	 * @param reviewIndex
	 * @return
	 */
	public boolean isReviewApproved(int reviewIndex) {
		commons.sleepInMiliSecond(1000);
		return commons.getElement(loc_btnEnableSpecificReviewToggle, reviewIndex).findElement(loc_btnToggleStatus).isSelected();
	}		
	
	/**
	 * Allow a review to appear on Storefront.
	 * @param reviewIndex Eg. 0,1,2...
	 */
	public ProductReviews approveReview(int reviewIndex) {
		if (isReviewApproved(reviewIndex)) {
			logger.info("%s-indexed review has already been approved.".formatted(reviewIndex));
			return this;
		}
		homePage.hideFacebookBubble();
		commons.click(loc_btnEnableSpecificReviewToggle, reviewIndex);
		logger.info("Allow %s-indexed review to appear on SF.".formatted(reviewIndex));
		homePage.getToastMessage();
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
		homePage.hideFacebookBubble();
		commons.click(loc_btnEnableSpecificReviewToggle, reviewIndex);
		logger.info("Hide %s-indexed review from buyers on SF.".formatted(reviewIndex));
		homePage.getToastMessage();
		return this;
	}	

    public void verifyTextAtReviewManagementScreen() throws Exception {
    	homePage.hideFacebookBubble();
    	
    	String text = commons.getText(loc_lblPageTitle).split("\n")[0];
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("product.review.management"), text);
    	
    	text = commons.getText(new ByChained(loc_btnEnableReviewToggle, loc_lblEnableProductReview));
    	Assert.assertEquals(PropertiesUtil.getPropertiesValueByDBLang("product.review.management.toggle.enable.product.review"), text);
    	
    	text = commons.getAttribute(loc_txtSearchProduct, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.searchBox"));
    	
    	text = commons.getText(loc_lblTableHeader);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.table.header"));

    	if (isProductReviewsEnabled()) {
    		commons.click(loc_btnEnableReviewToggle);
    		text = homePage.getToastMessage();
        	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.disable.store.review"));
    	}
    	commons.hoverActions(loc_btnEnableReviewToggle);
    	commons.sleepInMiliSecond(500);
    	text = commons.getText(loc_tltEnableReviewToggle);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.disable.store.review"));
    	
    	if (!isProductReviewsEnabled()) {
    		commons.click(loc_btnEnableReviewToggle);
    		text = homePage.getToastMessage();
        	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.enable.store.review"));
    	}
    	commons.hoverActions(loc_btnEnableReviewToggle);
    	commons.sleepInMiliSecond(500);
    	text = commons.getText(loc_tltEnableReviewToggle);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.enable.store.review"));
    	
    	logger.info("verifyTextAtCashbookManagementScreen completed");
    }  		
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToManageReviews(String permission) {
		if (permission.contentEquals("A")) {
			new ProductReviews(driver).inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    	
    
}
