package web.Dashboard.promotion.discount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.segments.Segments;
import web.Dashboard.home.HomePage;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;
import web.Dashboard.promotion.discount.product_discount_code.ProductDiscountCodePage;
import web.Dashboard.promotion.discount.servicediscountcampaign.ServiceDiscountCampaignPage;
import web.Dashboard.promotion.discount.servicediscountcode.ServiceDiscountCodePage;
import utilities.commons.UICommonAction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static web.Dashboard.customers.allcustomers.create_customer.CreateCustomerPopup.customerTags;
import static web.Dashboard.customers.segments.createsegment.CreateSegment.segmentName;
import static utilities.links.Links.DOMAIN;

public class DiscountPage extends DiscountElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    public DiscountPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    Logger logger = LogManager.getLogger(DiscountPage.class);

    /**
     * navigate to discount page
     */
    public DiscountPage navigate() throws InterruptedException {
        // on home page:
        // 1: hide facebook bubble
        // 2: navigate to Promotion>>Discount page
        new HomePage(driver).hideFacebookBubble()
                .navigateToPromotion_DiscountPage();

        // log page title for debug
        logger.info("Current page is %s".formatted(driver.getTitle()));

        // wait page loaded
        commonAction.waitElementVisible(UI_SEARCH_PLACEHOLDER);

        return this;
    }

    /**
     * navigate to create product discount campaign page
     */
    public ProductDiscountCampaignPage openCreateProductDiscountCampaignPage() {
        // on Discount page:
        // 1: Click on wholesale pricing button
        // 2: Then click product wholesale pricing button
        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICING_BTN)).click();
        logger.info("Click on the Wholesale pricing button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WHOLESALE_PRICING)).click();
        logger.info("Navigate to create a new product discount campaign");
        return new ProductDiscountCampaignPage(driver);
    }

    public ProductDiscountCodePage openCreateProductDiscountCodePage() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PROMOTION_BTN)).click();
        logger.info("Click on the Create promotion button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DISCOUNT_CODE)).click();
        return new ProductDiscountCodePage(driver);
    }

    /**
     * create new segment with new customer, used to specific segment condition
     */
    public List<String> generateSegmentForTest() {
        // open new dashboard in new tab
        ((JavascriptExecutor) driver).executeScript("window.open('%s');".formatted(DOMAIN));

        // get list tabs
        var tabs = new ArrayList<>(driver.getWindowHandles());

        // switch to have just opened windows
        driver.switchTo().window(tabs.get(tabs.size() - 1));

        // wait Home page loaded and hide facebook bubble
        new HomePage(driver).verifyPageLoaded()
                .hideFacebookBubble();

        // in all customers page
        // create new customer
        new AllCustomers(driver).navigate()
                .clickCreateNewCustomerBtn()
                .inputCustomerName()
                .inputCustomerPhone()
                .inputCustomerTags()
                .clickAddBtn();

        // Create customer segment with customer tag condition
        new Segments(driver).navigate()
                .clickCreateSegmentBtn()
                .inputSegmentName()
                .selectDataGroupCondition("Customers data")
                .selectDataCondition("Customer tag")
                .selectComparisonOperatorCondition("is equal to")
                .inputComparedValueCondition(customerTags[0])
                .clickSaveBtn();

        // close window after create segment
        ((JavascriptExecutor) driver).executeScript("window.close();");

        // back to create discount/campaign page
        driver.switchTo().window(tabs.get(0));

        // return segment have just created
        return List.of(segmentName);
    }

    public DiscountPage clickDiscountCode() {
    	commonAction.clickElement(CREATE_PROMOTION_BTN);
    	logger.info("Clicked on 'Discount Code' button.");
    	return this;
    }
    
    public DiscountPage clickProductDiscountCode() {
    	if (commonAction.isElementVisiblyDisabled(PRODUCT_DISCOUNT_CODE.findElement(By.xpath("./parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(PRODUCT_DISCOUNT_CODE));
    		return this;
    	}
    	commonAction.clickElement(PRODUCT_DISCOUNT_CODE);
    	logger.info("Clicked on 'Product Discount Code' button.");
    	return this;
    }    
    
    public DiscountPage clickServiceDiscountCode() {
    	if (commonAction.isElementVisiblyDisabled(SERVICE_DISCOUNT_CODE.findElement(By.xpath("./parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(SERVICE_DISCOUNT_CODE));
    		return this;
    	}
    	commonAction.clickElement(SERVICE_DISCOUNT_CODE);
    	logger.info("Clicked on 'Service Discount Code' button.");
    	return this;
    }    

    public DiscountPage clickDiscountCampaign() {
    	commonAction.clickElement(WHOLESALE_PRICING_BTN);
    	logger.info("Clicked on 'Discount Campaign' button.");
    	return this;
    }    

    public DiscountPage clickProductDiscountCampaign() {
    	if (commonAction.isElementVisiblyDisabled(PRODUCT_DISCOUNT_CODE.findElement(By.xpath("./parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(PRODUCT_DISCOUNT_CODE));
    		return this;
    	}
    	commonAction.clickElement(PRODUCT_DISCOUNT_CODE);
    	logger.info("Clicked on 'Product Discount Campaign' button.");
    	return this;
    }       
    
    public DiscountPage clickServiceDiscountCampaign() {
    	if (commonAction.isElementVisiblyDisabled(SERVICE_DISCOUNT_CODE.findElement(By.xpath("./parent::*")))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(SERVICE_DISCOUNT_CODE));
    		return this;
    	}
    	commonAction.clickElement(SERVICE_DISCOUNT_CODE);
    	logger.info("Clicked on 'Service Discount Campaign' button.");
    	return this;
    }    
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateProductDiscountCodeForPlatform(String platform, String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
    	clickDiscountCode();
    	clickProductDiscountCode();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ProductDiscountCodePage productDiscountCode = new ProductDiscountCodePage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		List<String> platform1 = new ArrayList<>();
    		platform1.add(platform);
    		productDiscountCode.setPlatforms(platform1);
    	} else if (permission.contentEquals("D")) {
    		if (!originalURL.contentEquals(URL1)) {
    			Assert.assertTrue(productDiscountCode.isPlatformDisabled(platform));
    		} else {
    			logger.debug("Good for you! Failed from the outset.");
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCode();
    	}
    }

	public void verifyPermissionToCreateProductDiscountCodeAsReward(String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
    	clickDiscountCode();
    	clickProductDiscountCode();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ProductDiscountCodePage productDiscountCode = new ProductDiscountCodePage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
			productDiscountCode.setDiscountCodeAsReward(true);
    	} else if (permission.contentEquals("D")) {
			if (!originalURL.contentEquals(URL1)) {
				Assert.assertTrue(productDiscountCode.isSetDiscountCodeAsRewardCheckboxDisabled());
			} else {
				logger.debug("Good for you! Failed from the outset.");
			}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCode();
    	}
    }
    public void verifyPermissionToCreateServiceDiscountCodeForPlatform(String platform, String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
    	clickDiscountCode();
    	clickServiceDiscountCode();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ServiceDiscountCodePage serviceDiscountCode = new ServiceDiscountCodePage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		List<String> platform1 = new ArrayList<>();
    		platform1.add(platform);
    		serviceDiscountCode.setPlatforms(platform1);
    	} else if (permission.contentEquals("D")) {
    		if (!originalURL.contentEquals(URL1)) {
    			Assert.assertTrue(serviceDiscountCode.isPlatformDisabled(platform));
    		} else {
    			logger.debug("Good for you! Failed from the outset.");
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCode();
    	}
    } 
    public void verifyPermissionToServiceDiscountCodeAsReward(String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
    	clickDiscountCode();
    	clickServiceDiscountCode();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ServiceDiscountCodePage serviceDiscountCode = new ServiceDiscountCodePage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
			serviceDiscountCode.tickApplyDiscountCodeAsRewardCheckBox(true);
			serviceDiscountCode.inputRewardDescription("You're qualified for this reward");
    	} else if (permission.contentEquals("D")) {
			if (!originalURL.contentEquals(URL1)) {
				// Not reproducible but hardly happens
			} else {
				logger.debug("Good for you! Failed from the outset.");
			}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCode();
    	}
    }
    public void verifyPermissionToCreateProductDiscountCampaignFor(String applyTo, String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
		clickDiscountCampaign();
		clickProductDiscountCampaign();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ProductDiscountCampaignPage productDiscountCampaign = new ProductDiscountCampaignPage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	int typeOfProducts =-1;
    	
    	if (applyTo.contains("All Products")) {
    		typeOfProducts =0;
    	} else if (applyTo.contains("Specific Collections")) {
    		typeOfProducts =1;
    	} else {
    		typeOfProducts =2;
    	}
    	
    	if (permission.contentEquals("A")) {
			Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		productDiscountCampaign.tickAppliesTo(typeOfProducts);
    	} else if (permission.contentEquals("D")) {
			if (!originalURL.contentEquals(URL1)) {
				// Not reproducible but hardly happens
			} else {
				logger.debug("Good for you! Failed from the outset.");
			}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCampaign();
    	}
    }
    public void verifyPermissionToCreateServiceDiscountCampaignFor(String applyTo, String permission, String url) {
    	String originalURL = commonAction.getCurrentURL();
    	String URL1 = originalURL;
    	
    	clickDiscountCampaign();
    	clickServiceDiscountCampaign();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	ServiceDiscountCampaignPage serviceDiscountCampaign = new ServiceDiscountCampaignPage(driver);
    	
    	URL1 = commonAction.getCurrentURL();
    	
    	int typeOfProducts =-1;
    	
    	if (applyTo.contains("All Services")) {
    		typeOfProducts =0;
    	} else if (applyTo.contains("Specific Collections")) {
    		typeOfProducts =1;
    	} else {
    		typeOfProducts =2;
    	}
    	
    	if (permission.contentEquals("A")) {
    		Assert.assertTrue(commonAction.getCurrentURL().contains(url));
    		serviceDiscountCampaign.tickAppliesTo(typeOfProducts);
    	} else if (permission.contentEquals("D")) {
    		if (!originalURL.contentEquals(URL1)) {
    			// Not reproducible but hardly happens
    		} else {
    			logger.debug("Good for you! Failed from the outset.");
    		}
    	} else {
    		Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
    	}
    	
    	if (!originalURL.contentEquals(URL1)) {
    		commonAction.navigateBack();
    		new ConfirmationDialog(driver).clickOKBtn();
    	} else {
    		clickDiscountCampaign();
    	}
    }
    /*-------------------------------------*/   
    
    
}
