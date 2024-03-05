package web.Dashboard.promotion.discount;

import api.Seller.promotion.ProductDiscountCampaign;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.segments.Segments;
import web.Dashboard.home.HomePage;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignElement;
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
	AssertCustomize assertCustomize;
	AllPermissions allPermissions;
	ProductDiscountCampaignElement productDiscountCampaignEl;
	ServiceDiscountCampaignPage serviceCampaignPage;
	HomePage homePage;

	public DiscountPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		productDiscountCampaignEl = new ProductDiscountCampaignElement(driver);
		homePage = new HomePage(driver);
		serviceCampaignPage = new ServiceDiscountCampaignPage(driver);
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
    public DiscountPage filterDiscountType(String discountType){
		commonAction.click(loc_ddlDiscountType);
		switch (discountType){
			case "All Types":
				commonAction.click(loc_ddvDiscountType,0);
				break;
			case "Product Discount Code":
				commonAction.click(loc_ddvDiscountType,1);
				break;
			case "Service Discount Code":
				commonAction.click(loc_ddvDiscountType,2);
				break;
			case "Product Discount Campaign":
				commonAction.click(loc_ddvDiscountType,3);
				break;
			case "Service Discount Campaign":
				commonAction.click(loc_ddvDiscountType,4);
				break;
			default:
				try {
					throw new Exception("Discount type not found!");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		}
		return this;
	}
	public DiscountPage filterDiscountStatus(String status){
		commonAction.click(loc_ddlDiscountType);
		switch (status){
			case "All Status" -> commonAction.click(loc_ddvDiscountStatus,0);
			case "Scheduled" ->	commonAction.click(loc_ddvDiscountStatus,1);
			case "Expired" -> commonAction.click(loc_ddvDiscountStatus,2);
			case "In Progress" -> commonAction.click(loc_ddvDiscountStatus,3);
			default ->{
				try {
					throw new Exception("Discount status not found!");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return this;
	}
	public DiscountPage navigateUrl(){
		String url = DOMAIN+"/discounts/list";
		commonAction.navigateToURL(url);
		return this;
	}
    public DiscountPage checkPermissionViewProductCampaignList(){
		filterDiscountType("Product Discount Campaign");
		List<WebElement> promotionList = commonAction.getElements(loc_lstPromotionName);
		if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
			assertCustomize.assertTrue(promotionList.size()>0,"[Failed]Product discount campaign not shown.");
		}else {
			assertCustomize.assertTrue(promotionList.isEmpty(),
					"[Failed] Don't have permission view prodcut discount campaign, but promotion list still shown (size=%s".formatted(promotionList.size()));
		}
		return this;
	}
	public DiscountPage checkPermissionViewProductCampaignDetail(int productCampaignScheduledId){
		String detailUrl = Links.DOMAIN + "/discounts/detail/WHOLE_SALE/" + productCampaignScheduledId;
		String editUrl = Links.DOMAIN + "/discounts/edit/WHOLE_SALE/" + productCampaignScheduledId;

		// has permission: View product campaign list then click on promotion list.
		if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
			navigateUrl();
			filterDiscountType("Product Discount Campaign");
			filterDiscountStatus("Scheduled");
			if(allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
				//click on promotion name 0 to check access to campaign detail page
				commonAction.click(loc_lstPromotionName,0);
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name, but '%s' is shown".formatted(name));
				//click on edit campaign 0 to check access to edit campaign page
				navigateUrl();
				commonAction.click(loc_lst_icnEdit,0);
				name = commonAction.getText(productDiscountCampaignEl.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name, but '%s' is shown".formatted(name));

			}else {
				//click on promotion to check
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lstPromotionName,0),
						"[Failed] Restricted page not shown.");
				//click on edit icon to check
				navigateUrl();
				filterDiscountType("Product Discount Campaign");
				filterDiscountStatus("Scheduled");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
						"[Failed] Restricted page not shown.");
			}
		}else { //don't have permission View list, then check by navigate url
			if (allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
				//check navigate to detail page
				new ProductDiscountCampaignPage(driver).navigateUrl(productCampaignScheduledId);
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name, but '%s' is shown".formatted(name));
				//check navigate to edit page
				new ProductDiscountCampaignPage(driver).navigateUrl(productCampaignScheduledId);
				name = commonAction.getText(productDiscountCampaignEl.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name, but '%s' is shown".formatted(name));
			} else {
				//check navigate to detail page.
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(detailUrl),
						"[Failed] Restricted page not shown.");
				//check navigate to edit page
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page not shown.");
			}
		}
		return this;
	}
	public DiscountPage checkPermissionViewServiceCampaignList(){
		filterDiscountType("Service Discount Campaign");
		List<WebElement> promotionList = commonAction.getElements(loc_lstPromotionName);
		if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
			assertCustomize.assertTrue(promotionList.size()>0,"[Failed]Service discount campaign list not shown.");
		}else {
			assertCustomize.assertTrue(promotionList.isEmpty(),
					"[Failed] Don't have permission view service discount campaign, but promotion list still shown (size=%s".formatted(promotionList.size()));
		}
		return this;
	}
	public DiscountPage checkPermissionViewServiceCampaignDetail(int serviceCampaignId){
		String detailUrl = Links.DOMAIN + "/discounts/detail/WHOLE_SALE_SERVICE/" + serviceCampaignId;
		String editUrl = Links.DOMAIN + "/discounts/edit/WHOLE_SALE_SERVICE/" + serviceCampaignId;

		// has permission: View product campaign list then click on promotion list.
		if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()){
			navigateUrl();
			filterDiscountType("Service Discount Campaign");
			filterDiscountStatus("Scheduled");
			if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignDetail()) {
				//click on promotion name 0 to check access to campaign detail page
				commonAction.click(loc_lstPromotionName,0);
				String name = commonAction.getText(serviceCampaignPage.loc_detailPage_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name, but '%s' is shown".formatted(name));
				//click on edit campaign 0 to check access to edit campaign page
				navigateUrl();
				commonAction.click(loc_lst_icnEdit,0);
				name = commonAction.getText(serviceCampaignPage.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name, but '%s' is shown".formatted(name));

			}else {
				//click on promotion to check
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lstPromotionName,0),
						"[Failed] Restricted page not shown.");
				//click on edit icon to check
				navigateUrl();
				filterDiscountType("Service Discount Campaign");
				filterDiscountStatus("Scheduled");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
						"[Failed] Restricted page not shown.");
			}
		}else { //don't have permission View list, then check by navigate url
			if (allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
				//check navigate to detail page
				new ProductDiscountCampaignPage(driver).navigateUrl(serviceCampaignId);
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name, but '%s' is shown".formatted(name));
				//check navigate to edit page
				new ProductDiscountCampaignPage(driver).navigateUrl(serviceCampaignId);
				name = commonAction.getText(serviceCampaignPage.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name, but '%s' is shown".formatted(name));
			} else {
				//check navigate to detail page.
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(detailUrl),
						"[Failed] Restricted page not shown.");
				//check navigate to edit page
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page not shown.");
			}
		}
		return this;
		//
//		String editProductCampaignUrl = Links.DOMAIN + "/discounts/detail/WHOLE_SALE_SERVICE/" + serviceCampaignId;
//		String serviceDiscountCa
//		if(allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()){
//			new ServiceDiscountCampaignPage(driver).navigateUrl(serviceCampaignId);
//			String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
//			assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name, but '%s' is shown".formatted(name));
//		}else {
//			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(productDiscountCampaignUrl),
//					"[Failed] Restricted page not shown.");
//		}
//		return this;
	}
	public DiscountPage checkPermissionCreateProductCampaign(){
		if (allPermissions.getPromotion().getDiscountCampaign().isCreateProductDiscountCampaign()){
			openCreateProductDiscountCampaignPage();
			homePage.waitTillSpinnerDisappear1();
			//Check permission View customer segment list.
			if(allPermissions.getCustomer().getSegment().isViewSegmentList()){

			}
			//
			String newProductCampaignName = new ProductDiscountCampaignPage(driver).createDefaultProductCampaign();
			navigateUrl();
			String firstPromotion = commonAction.getText(loc_lstPromotionName,0);
			assertCustomize.assertEquals(firstPromotion,newProductCampaignName,
					"[Failed]New product discount campaign not show on top.");
		}
		return this;
	}
}
