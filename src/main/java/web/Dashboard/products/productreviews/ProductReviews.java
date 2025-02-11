package web.Dashboard.products.productreviews;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import api.Seller.products.product_reviews.APIProductReviews;
import api.Seller.products.product_reviews.APIProductReviews.ReviewManagementInfo;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.enums.Domain;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.pagination.Pagination;
import web.StoreFront.detail_product.ProductDetailPage;

public class ProductReviews extends ProductReviewElement {
    WebDriver driver;
    UICommonAction commons;
    HomePage homePage;

    /**
     * Domain defaults to VN. Use the object's constructor to override it when necessary
     */
    Domain domain = Domain.VN;        
    
    final static Logger logger = LogManager.getLogger(ProductReviews.class);

    public ProductReviews(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
        homePage = new HomePage(driver);
    }
    public ProductReviews(WebDriver driver, Domain domain) {
		this(driver);
		this.domain = domain;
    }

    public ProductReviews navigate() {
        new HomePage(driver).navigateToPage("Products", "Product Reviews");
        return this;
    }

    /**
     * Navigates to Sign-in screen by URL
     */
    public ProductReviews navigateByURL() {
    	
    	var subURL = "/review_product/list";
    	
    	var url = switch (domain) {
	        case VN -> DOMAIN + subURL;
	        case BIZ -> DOMAIN_BIZ + subURL;
	        default -> throw new IllegalArgumentException("Unexpected value: " + domain);
    	};
    	
    	driver.get(url);
    	logger.info("Navigated to: {}", url);
        return this;
    }    
    
    /**
     * Check whether Product Reviews feature is enabled
     */
    public boolean isProductReviewsEnabled() {
        return commons.isCheckedJS(new ByChained(loc_btnEnableReviewToggle, loc_btnToggleStatus));
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
        for (int i = 0; i < commons.getElements(loc_tmpRecords).size(); i++) {
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

        while (true) {
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
        UICommonAction.sleepInMiliSecond(1000);
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
        UICommonAction.sleepInMiliSecond(500);
        text = commons.getText(loc_tltEnableReviewToggle);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.disable.store.review"));

        if (!isProductReviewsEnabled()) {
            commons.click(loc_btnEnableReviewToggle);
            text = homePage.getToastMessage();
            Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("product.review.management.enable.store.review"));
        }
        commons.hoverActions(loc_btnEnableReviewToggle);
        UICommonAction.sleepInMiliSecond(500);
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

    void navigateToProductReviewListPage() {
        if (!driver.getCurrentUrl().contains("/review_product/list")) {
            driver.get("%s/review_product/list".formatted(DOMAIN));
            logger.info("Navigate to product reviews list page by URL.");
        }
    }

    /*-------------------------------------*/
    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-24654
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    LoginInformation staffLoginInformation;
    APIProductReviews productReviews;

    public ProductReviews getLoginInformation(LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        return this;
    }

    public void checkProductReviewPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // init product reviews API
        productReviews = new APIProductReviews(staffLoginInformation);

        // check view review
        checkViewReview();

        // check enable/disable review feature
        checkEnableDisableReviewFeature();

        // check hide a review
        checkHideAReview();

        // check show a review
        checkShowAReview();

    }

    void checkViewReview() {
        int statusCode = productReviews.getReviewListResponse(0).statusCode();
        if (permissions.getProduct().getReview().isViewReview()) {
            assertCustomize.assertTrue(statusCode == 200, "No product review shows.");
        } else {
            assertCustomize.assertTrue(statusCode == 403, "All product reviews still showing when no view review permission.");
        }

        // If staff don’t have permission “View review”
        // => don’t see any review when access product review page
        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/review_product/list".formatted(DOMAIN), "/review_product/list"),
                "Product review page must be shown instead of %s.".formatted(driver.getCurrentUrl()));

        logger.info("Check permission: Product >> Review >> View review.");
    }

    void checkEnableReviewFeature() {
        // navigate to product review list page
        navigateToProductReviewListPage();

        // check permission
        if (permissions.getProduct().getReview().isEnableReviewFeature()
                && permissions.getProduct().getReview().isShowAReview()) {
            // check can enable review feature
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnEnableReviewToggle,
                            loc_dlgToastSuccess),
                    "Can not enable review feature.");
        } else {
            // show restricted popup
            // if staff don’t have permission “Enable review feature” and
            // click to Enable review feature in Product >> Review page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEnableReviewToggle),
                    "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Review >> Enable review feature.");
    }

    void checkDisableReviewFeature() {
        // navigate to product review list page
        navigateToProductReviewListPage();

        // check permission
        if (permissions.getProduct().getReview().isDisableReviewFeature()
                && permissions.getProduct().getReview().isHideAReview()) {
            // check can disable review feature
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnEnableReviewToggle,
                            loc_dlgToastSuccess),
                    "Can not disable review feature.");
        } else {
            // show restricted popup
            // if staff don’t have permission “Disable review feature” and
            // click to Disable review feature in Product >> Review page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnEnableReviewToggle),
                    "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Review >> Disable review feature.");
    }

    void checkEnableDisableReviewFeature() {
        if (productReviews.isIsEnableReview()) {
            checkDisableReviewFeature();
            if (!productReviews.isIsEnableReview()) {
                checkEnableReviewFeature();
            }
        } else {
            checkEnableReviewFeature();
            if (productReviews.isIsEnableReview()) {
                checkDisableReviewFeature();
            }
        }
    }

    void checkHideAReview() {
        int showReviewId = productReviews.getShowOnOnlineStoreReviewId();
        if ((showReviewId != 0) && productReviews.isIsEnableReview()) {
            By showHideLocator = By.xpath(str_showHideReviews.formatted(showReviewId));

            // navigate to product review list page
            navigateToProductReviewListPage();

            // check permission
            if (permissions.getProduct().getReview().isHideAReview()) {
                ReviewManagementInfo info = productReviews.getReviewManagementInfo();

                String productName = info.getItemNames().get(info.getReviewIds().indexOf(showReviewId));

                // search review by product
                commons.sendKeys(loc_txtSearchProduct, productName);

                // check can show review on online store
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(showHideLocator,
                                loc_dlgToastSuccess),
                        "Can not hide a review on online store.");
            } else {
                // show restricted popup
                // if staff don’t have permission “Hide a review” and
                // click to Hide a review of product in Product >> Review page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(showHideLocator),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Review >> Hide a review.");
    }

    void checkShowAReview() {
        int hiddenReviewId = productReviews.getHideOnOnlineStoreReviewId();
        if ((hiddenReviewId != 0) && productReviews.isIsEnableReview()) {
            By showHideLocator = By.xpath(str_showHideReviews.formatted(hiddenReviewId));

            // navigate to product review list page
            navigateToProductReviewListPage();

            // check permission
            if (permissions.getProduct().getReview().isShowAReview()) {
                ReviewManagementInfo info = productReviews.getReviewManagementInfo();

                String productName = info.getItemNames().get(info.getReviewIds().indexOf(hiddenReviewId));

                // search review by product
                commons.sendKeys(loc_txtSearchProduct, productName);

                // check can show review on online store
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(showHideLocator,
                                loc_dlgToastSuccess),
                        "Can not show a review on online store.");
            } else {
                // show restricted popup
                // if staff don’t have permission “Hide a review” and
                // click to Show a review of product in Product >> Review page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(showHideLocator),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Review >> Show a review.");
    }
}
