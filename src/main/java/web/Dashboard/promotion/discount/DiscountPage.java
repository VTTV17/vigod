package web.Dashboard.promotion.discount;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
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
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
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
import java.util.stream.Collectors;

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
	LoginInformation loginInformation;

	public DiscountPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
	}
	public DiscountPage(WebDriver driver, LoginInformation loginInformation) {
		super(driver);
		this.loginInformation = loginInformation;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		productDiscountCampaignEl = new ProductDiscountCampaignElement(driver);
		homePage = new HomePage(driver);
		serviceCampaignPage = new ServiceDiscountCampaignPage(driver);
	}

    Logger logger = LogManager.getLogger(DiscountPage.class);

	public String translatePromotionType(String type) {
		String translatedPromotionType = null;
		try {
			translatedPromotionType = PropertiesUtil.getPropertiesValueByDBLang("discount.%s".formatted(type));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedPromotionType;
	}

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
		commonAction.clickElement(WHOLESALE_PRICING_BTN);
//        wait.until(ExpectedConditions.elementToBeClickable(WHOLESALE_PRICING_BTN)).click();
        logger.info("Click on the Wholesale pricing button");
		commonAction.clickElement(PRODUCT_WHOLESALE_PRICING);
//        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_WHOLESALE_PRICING)).click();
        logger.info("Navigate to create a new product discount campaign");
        return new ProductDiscountCampaignPage(driver);
    }

    public ProductDiscountCodePage openCreateProductDiscountCodePage() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_PROMOTION_BTN)).click();
        logger.info("Click on the Create promotion button");
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_DISCOUNT_CODE)).click();
        return new ProductDiscountCodePage(driver);
    }
	public ProductDiscountCampaignPage openCreateServiceDiscountCampaignPage() {
		// on Discount page:
		// 1: Click on wholesale pricing button
		// 2: Then click product wholesale pricing button
		commonAction.click(loc_btnDiscountCampaign);
		logger.info("Click on the Wholesale pricing button");
		commonAction.click(loc_btnServiceCampaign);
		logger.info("Navigate to create a new service discount campaign");
		return new ProductDiscountCampaignPage(driver);
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
		commonAction.sleepInMiliSecond(500);
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
		logger.info("Selected discount type: "+discountType);
		commonAction.sleepInMiliSecond(500,"Wait in filterDiscountType.");
		return this;
	}
	public DiscountPage filterDiscountStatus(String status){
		commonAction.click(loc_ddlStatus);
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
		logger.info("Selected status: "+status);
		commonAction.sleepInMiliSecond(500,"Wait in filterDiscountStatus.");
		return this;
	}
	public DiscountPage navigateUrl(){
		String url = DOMAIN+"/discounts/list";
		commonAction.navigateToURL(url);
		new HomePage(driver).waitTillSpinnerDisappear1();
//		commonAction.sleepInMiliSecond(500);
		return this;
	}

    public List<List<String>> getPromotionTable() {
    	commonAction.sleepInMiliSecond(1000, "Wait a little till promotion list appears"); //Will find a better way to handle this
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < commonAction.getElements(loc_lstPromotionName).size(); i++) {
            List<String> rowData = new ArrayList<>();
            rowData.add(commonAction.getText(loc_lstPromotionName, i));
            rowData.add(commonAction.getText(loc_lstPromotionType, i));
            rowData.add(commonAction.getText(loc_lstPromotionActiveDate, i));
            rowData.add(commonAction.getText(loc_lstPromotionStatus, i));
            table.add(rowData);
        }
        return table;
    }


    boolean permissionToViewProductDiscountCodeList(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isViewProductDiscountCodeList();
    }
    boolean permissionToViewServiceDiscountCodeList(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isViewServiceDiscountCodeList();
    }
    boolean permissionToCreateProductDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isCreateProductDiscountCode();
    }
    boolean permissionToViewProductDiscountCodeDetail(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isViewProductDiscountCodeDetail();
    }
    boolean permissionToViewServiceDiscountCodeDetail(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isViewServiceDiscountCodeDetail();
    }
    boolean permissionToCreateServiceDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isCreateServiceDiscountCode();
    }
    boolean permissionToEditProductDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isEditProductDiscountCode();
    }
    boolean permissionToEditServiceDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isEditServiceDiscountCode();
    }
    boolean permissionToEndProductDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isEndProductDiscountCode();
    }
    boolean permissionToEndServiceDiscountCode(AllPermissions staffPermission) {
    	return staffPermission.getPromotion().getDiscountCode().isEndServiceDiscountCode();
    }
    
    void checkPermissionToViewDiscounCodeList(AllPermissions staffPermission) {
    	navigateUrl(); 
    	List<List<String>> records = getPromotionTable();

    	for (int i=0; i<2; i++) {
    		boolean isPermissionGranted;
    		List<String> promotionType;
    		String type;
    		if (i==0) {
    			type = translatePromotionType("productDiscountCode");
    			isPermissionGranted = permissionToViewProductDiscountCodeList(staffPermission);
    		} else {
    			type = translatePromotionType("serviceDiscountCode");
    			isPermissionGranted = permissionToViewServiceDiscountCodeList(staffPermission);
    		}
    		promotionType = records.stream().filter(record -> records.size()>0).filter(record -> record.get(1).contains(type)).map(record -> record.get(1)).collect(Collectors.toList());
    		
    		if (isPermissionGranted) {
        		Assert.assertFalse(promotionType.isEmpty(), promotionType.toString());
    		} else {
        		Assert.assertTrue(promotionType.isEmpty(), promotionType.toString());
    		}
    	}
		logger.info("Finished checkPermissionToViewDiscounCodeList");
    }    
    
	void checkPermissionToViewDiscountCodeDetail(AllPermissions staffPermission, int productDiscountCodeId, int serviceDiscountCodeId) {
	   ProductDiscountCodePage productDiscountCodePage = new ProductDiscountCodePage(driver);
	   ServiceDiscountCodePage serviceDiscountCodePage = new ServiceDiscountCodePage(driver);
	   
	   for (int i=0; i<2; i++) {
    		boolean isPermissionGranted;
    		if (i==0) {
    			isPermissionGranted = staffPermission.getPromotion().getDiscountCode().isViewProductDiscountCodeDetail();
    			productDiscountCodePage.navigateToDiscountCodeDetailScreenByURL(productDiscountCodeId);
    		} else {
    			isPermissionGranted = staffPermission.getPromotion().getDiscountCode().isViewServiceDiscountCodeDetail();
    			serviceDiscountCodePage.navigateToDiscountCodeDetailScreenByURL(serviceDiscountCodeId);
    		}
    		
    		if (isPermissionGranted) {
    			if (i==0) {
    				Assert.assertFalse(productDiscountCodePage.getPageTitle().isEmpty());
    			} else {
    				Assert.assertFalse(serviceDiscountCodePage.getPageTitle().isEmpty());
    			}
    		} else {
    			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		}
		}
		logger.info("Finished checkPermissionToViewDiscountCodeDetail");
    }    
   
	void checkPermissionToCreateProductDiscountCode(AllPermissions staffPermission, String productNotCreatedByStaff, String productCreatedByStaff) {
		ProductDiscountCodePage productDiscountCodePage = new ProductDiscountCodePage(driver);

		productDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();

		if (permissionToCreateProductDiscountCode(staffPermission)) {
			
			allPermissions = staffPermission;
			//In combination with viewSegmentList
			AssertCustomize.setCountFalse(0);
			checkPermissionViewCustomerSegmentList();
			Assert.assertTrue(AssertCustomize.getCountFalse()==0);

			//In combination with viewAllProductList
			productDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();
			productDiscountCodePage.selectApplyToOption(2);
			productDiscountCodePage.clickAddProductLink();

			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				productDiscountCodePage.inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
				productDiscountCodePage.inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					productDiscountCodePage.inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(productDiscountCodePage.isProductPresentInDialog());
					productDiscountCodePage.inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
				} else {
					Assert.assertFalse(productDiscountCodePage.isProductPresentInDialog());
				}
			}
			
			//In combination with viewProductCollectionList
			productDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();
			productDiscountCodePage.selectApplyToOption(1);
			productDiscountCodePage.clickAddCollectionLink();
			
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(productDiscountCodePage.isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(productDiscountCodePage.isCollectionPresentInDialog());
			}
			// Need to actually create one?
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}

		//In combination with cloneDiscount permission
		if (permissionToViewProductDiscountCodeList(staffPermission)) {
			navigateUrl();
			filterDiscountType("Product Discount Code");
			filterDiscountStatus("Scheduled");
			commonAction.sleepInMiliSecond(500, "Wait a little till there's a change in data");
			commonAction.click(loc_lst_icnClone,0);
			
			if (permissionToCreateProductDiscountCode(staffPermission)) {
				try {
					Assert.assertEquals(new HomePage(driver).getToastMessage(), PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.cloneSuccessMessage"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		}
		logger.info("Finished checkPermissionToCreateProductDiscountCode");
	}  
	
	void checkPermissionToCreateServiceDiscountCode(AllPermissions staffPermission, String serviceNotCreatedByStaff, String serviceCreatedByStaff) {
		
		ServiceDiscountCodePage serviceDiscountCodePage = new ServiceDiscountCodePage(driver);
		
		serviceDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();
		
		if (permissionToCreateServiceDiscountCode(staffPermission)) {
			
			allPermissions = staffPermission;
			//In combination with viewSegmentList
			AssertCustomize.setCountFalse(0);
			checkPermissionViewCustomerSegmentList();
			Assert.assertTrue(AssertCustomize.getCountFalse()==0);
			
			//In combination with viewAllServiceList
			serviceDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();
			serviceDiscountCodePage.selectApplyToOption(2);
			serviceDiscountCodePage.clickAddServiceLink();
			
			if (staffPermission.getService().getServiceManagement().isViewListService()) {
				serviceDiscountCodePage.inputSearchTermInDialog(serviceNotCreatedByStaff);
				Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
				serviceDiscountCodePage.inputSearchTermInDialog(serviceCreatedByStaff);
				Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
			} else {
				if (staffPermission.getService().getServiceManagement().isViewListCreatedService()) {
					serviceDiscountCodePage.inputSearchTermInDialog(serviceNotCreatedByStaff);
					Assert.assertFalse(serviceDiscountCodePage.isServicePresentInDialog());
					serviceDiscountCodePage.inputSearchTermInDialog(serviceCreatedByStaff);
					Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
				} else {
					Assert.assertFalse(serviceDiscountCodePage.isServicePresentInDialog());
				}
			}
			
			//In combination with viewCollectionList
			serviceDiscountCodePage.navigateToCreateDiscountCodeScreenByURL();
			serviceDiscountCodePage.selectApplyToOption(1);
			serviceDiscountCodePage.clickAddCollectionLink();
			
			if (staffPermission.getService().getServiceCollection().isViewCollectionList()) {
				Assert.assertTrue(serviceDiscountCodePage.isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(serviceDiscountCodePage.isCollectionPresentInDialog());
			}
			// Need to actually create one?
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		//In combination with cloneDiscount permission
		if (permissionToViewServiceDiscountCodeList(staffPermission)) {
			navigateUrl();
			filterDiscountType("Service Discount Code");
			filterDiscountStatus("Scheduled");
			commonAction.sleepInMiliSecond(500, "Wait a little till there's a change in data");
			commonAction.click(loc_lst_icnClone,0);
			
			if (permissionToCreateServiceDiscountCode(staffPermission)) {
				try {
					Assert.assertEquals(new HomePage(driver).getToastMessage(), PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.cloneSuccessMessage"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		}
		logger.info("Finished checkPermissionToCreateServiceDiscountCode");
	}    

	void checkPermissionToEditProductDiscountCode(AllPermissions staffPermission, int productDiscountCodeId, String productNotCreatedByStaff, String productCreatedByStaff) {
		ProductDiscountCodePage productDiscountCodePage = new ProductDiscountCodePage(driver);

		productDiscountCodePage.navigateToEditDiscountCodeScreenByURL(productDiscountCodeId);
		
		if (staffPermission.getPromotion().getDiscountCode().isViewProductDiscountCodeDetail()) {
			allPermissions = staffPermission;
			//In combination with viewSegmentList
			AssertCustomize.setCountFalse(0);
			checkPermissionViewCustomerSegmentList();
			Assert.assertTrue(AssertCustomize.getCountFalse()==0);

			//In combination with viewAllProductList
			productDiscountCodePage.navigateToEditDiscountCodeScreenByURL(productDiscountCodeId);
			productDiscountCodePage.selectApplyToOption(2);
			productDiscountCodePage.clickAddProductLink();

			if (staffPermission.getProduct().getProductManagement().isViewProductList()) {
				productDiscountCodePage.inputSearchTermInDialog(productNotCreatedByStaff);
				Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
				productDiscountCodePage.inputSearchTermInDialog(productCreatedByStaff);
				Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
			} else {
				if (staffPermission.getProduct().getProductManagement().isViewCreatedProductList()) {
					productDiscountCodePage.inputSearchTermInDialog(productNotCreatedByStaff);
					Assert.assertFalse(productDiscountCodePage.isProductPresentInDialog());
					productDiscountCodePage.inputSearchTermInDialog(productCreatedByStaff);
					Assert.assertTrue(productDiscountCodePage.isProductPresentInDialog());
				} else {
					Assert.assertFalse(productDiscountCodePage.isProductPresentInDialog());
				}
			}
			
			//In combination with viewProductCollectionList
			productDiscountCodePage.navigateToEditDiscountCodeScreenByURL(productDiscountCodeId);
			productDiscountCodePage.selectApplyToOption(1);
			productDiscountCodePage.clickAddCollectionLink();
			
			if (staffPermission.getProduct().getCollection().isViewCollectionList()) {
				Assert.assertTrue(productDiscountCodePage.isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(productDiscountCodePage.isCollectionPresentInDialog());
			}
			
			productDiscountCodePage.navigateToEditDiscountCodeScreenByURL(productDiscountCodeId);
			productDiscountCodePage.getPageTitle();
			productDiscountCodePage.clickOnTheSaveBtn();
			
			if (permissionToEditProductDiscountCode(staffPermission)) {
				Assert.assertTrue(commonAction.isElementDisplay(loc_lst_icnEdit));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
			
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToEditProductDiscountCode");
	}  	
	
	void checkPermissionToEditServiceDiscountCode(AllPermissions staffPermission, int serviceDiscountCodeId, String serviceNotCreatedByStaff, String serviceCreatedByStaff) {
		
		ServiceDiscountCodePage serviceDiscountCodePage = new ServiceDiscountCodePage(driver);
		
		serviceDiscountCodePage.navigateToEditDiscountCodeScreenByURL(serviceDiscountCodeId);
		
		if (staffPermission.getPromotion().getDiscountCode().isViewServiceDiscountCodeDetail()) {
			allPermissions = staffPermission;
			//In combination with viewSegmentList
			AssertCustomize.setCountFalse(0);
			checkPermissionViewCustomerSegmentList();
			Assert.assertTrue(AssertCustomize.getCountFalse()==0);
			
			//In combination with viewAllServiceList
			serviceDiscountCodePage.navigateToEditDiscountCodeScreenByURL(serviceDiscountCodeId);
			serviceDiscountCodePage.selectApplyToOption(2);
			serviceDiscountCodePage.clickAddServiceLink();
			
			if (staffPermission.getService().getServiceManagement().isViewListService()) {
				serviceDiscountCodePage.inputSearchTermInDialog(serviceNotCreatedByStaff);
				Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
				serviceDiscountCodePage.inputSearchTermInDialog(serviceCreatedByStaff);
				Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
			} else {
				if (staffPermission.getService().getServiceManagement().isViewListCreatedService()) {
					serviceDiscountCodePage.inputSearchTermInDialog(serviceNotCreatedByStaff);
					Assert.assertFalse(serviceDiscountCodePage.isServicePresentInDialog());
					serviceDiscountCodePage.inputSearchTermInDialog(serviceCreatedByStaff);
					Assert.assertTrue(serviceDiscountCodePage.isServicePresentInDialog());
				} else {
					Assert.assertFalse(serviceDiscountCodePage.isServicePresentInDialog());
				}
			}
			
			//In combination with viewCollectionList
			serviceDiscountCodePage.navigateToEditDiscountCodeScreenByURL(serviceDiscountCodeId);
			serviceDiscountCodePage.selectApplyToOption(1);
			serviceDiscountCodePage.clickAddCollectionLink();
			
			if (staffPermission.getService().getServiceCollection().isViewCollectionList()) {
				Assert.assertTrue(serviceDiscountCodePage.isCollectionPresentInDialog());
			} else {
				Assert.assertFalse(serviceDiscountCodePage.isCollectionPresentInDialog());
			}
			
			serviceDiscountCodePage.navigateToEditDiscountCodeScreenByURL(serviceDiscountCodeId);
			serviceDiscountCodePage.getPageTitle();
			serviceDiscountCodePage.clickSaveBtn();
			
			if (permissionToEditServiceDiscountCode(staffPermission)) {
				Assert.assertTrue(commonAction.isElementDisplay(loc_lst_icnEdit));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
			
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToEditServiceDiscountCode");
	}  	

	void checkPermissionToEndProductDiscountCode(AllPermissions staffPermission, int productDiscountCodeId) {
		ProductDiscountCodePage productDiscountCodePage = new ProductDiscountCodePage(driver);

		if (permissionToViewProductDiscountCodeDetail(staffPermission)) {
			productDiscountCodePage.navigateToDiscountCodeDetailScreenByURL(productDiscountCodeId);
			
			commonAction.click(productDiscountCampaignEl.loc_btnEndEarly);
			
			if (permissionToEndProductDiscountCode(staffPermission)) {
				new ConfirmationDialog(driver).clickOKBtn();
//				Assert.assertTrue(commonAction.getCurrentURL().contains("/home"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			logger.info("Skipped ending product discount code from product discount code detail screen");
		}		
		
		if (permissionToViewProductDiscountCodeList(staffPermission)) {
			navigateUrl();
			filterDiscountType("Product Discount Code");
			filterDiscountStatus("In Progress");
			
			if (permissionToEndProductDiscountCode(staffPermission)) {
				endFirstCampaign();
				String statusFirstCampaign = commonAction.getText(loc_lst_lblStatus, 0);
				String expectedStatus = "";
				try {
					expectedStatus = PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.status.expired");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Assert.assertEquals(statusFirstCampaign, expectedStatus);
			} else {
				Assert.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEnd, 0));
			}
		} else {
			logger.info("Skipped ending product discount code from promotion management screen");
		}
		logger.info("Finished checkPermissionToEndProductDiscountCode");
	}    	
	
	void checkPermissionToEndServiceDiscountCode(AllPermissions staffPermission, int serviceDiscountCodeId) {
		ServiceDiscountCodePage serviceDiscountCodePage = new ServiceDiscountCodePage(driver);
		
		if (permissionToViewServiceDiscountCodeDetail(staffPermission)) {
			serviceDiscountCodePage.navigateToDiscountCodeDetailScreenByURL(serviceDiscountCodeId);
			
			commonAction.click(productDiscountCampaignEl.loc_btnEndEarly);
			
			if (permissionToEndServiceDiscountCode(staffPermission)) {
				new ConfirmationDialog(driver).clickOKBtn();
//				Assert.assertTrue(commonAction.getCurrentURL().contains("/home"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			logger.info("Skipped ending service discount code from service discount code detail screen");
		}		
		
		if (permissionToViewServiceDiscountCodeList(staffPermission)) {
			navigateUrl();
			filterDiscountType("Service Discount Code");
			filterDiscountStatus("In Progress");
			
			if (permissionToEndServiceDiscountCode(staffPermission)) {
				endFirstCampaign();
				String statusFirstCampaign = commonAction.getText(loc_lst_lblStatus, 0);
				String expectedStatus = "";
				try {
					expectedStatus = PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.status.expired");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Assert.assertEquals(statusFirstCampaign, expectedStatus);
			} else {
				Assert.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEnd, 0));
			}
		} else {
			logger.info("Skipped ending service discount code from promotion management screen");
		}
		logger.info("Finished checkPermissionToEndServiceDiscountCode");
	}    	
	
    public void checkDiscountPermission(AllPermissions staffPermission, int productDiscountCodeId, int serviceDiscountCodeId, int productDiscountCodeIdToEnd, int serviceDiscountCodeIdToEnd, String productNotCreatedByStaff, String productCreatedByStaff, String serviceNotCreatedByStaff, String serviceCreatedByStaff) {
    	logger.info("Permissions: " + staffPermission);
    	assertCustomize = new AssertCustomize(driver);
    	productDiscountCampaignEl = new ProductDiscountCampaignElement(driver);
		homePage = new HomePage(driver);
		serviceCampaignPage = new ServiceDiscountCampaignPage(driver);
    	
    	checkPermissionToViewDiscounCodeList(staffPermission);
    	checkPermissionToViewDiscountCodeDetail(staffPermission, productDiscountCodeId, serviceDiscountCodeId);
    	checkPermissionToCreateProductDiscountCode(staffPermission, productNotCreatedByStaff, productCreatedByStaff);
    	checkPermissionToCreateServiceDiscountCode(staffPermission, serviceNotCreatedByStaff, serviceCreatedByStaff);
    	checkPermissionToEditProductDiscountCode(staffPermission, productDiscountCodeId, productNotCreatedByStaff, productCreatedByStaff);
    	checkPermissionToEditServiceDiscountCode(staffPermission, serviceDiscountCodeId, serviceNotCreatedByStaff, serviceCreatedByStaff);
    	checkPermissionToEndProductDiscountCode(staffPermission, productDiscountCodeIdToEnd);
    	checkPermissionToEndServiceDiscountCode(staffPermission, serviceDiscountCodeIdToEnd);
    } 

    public DiscountPage verifyPermissionViewProductCampaignList(){
		navigateUrl();
		filterDiscountType("Product Discount Campaign");
		List<WebElement> promotionList = commonAction.getElements(loc_lstPromotionName);
		if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
			assertCustomize.assertTrue(promotionList.size()>0,"[Failed]Product discount campaign not shown.");
		}else {
			assertCustomize.assertTrue(promotionList.isEmpty(),
					"[Failed] Don't have permission view prodcut discount campaign, but promotion list still shown (size=%s".formatted(promotionList.size()));
		}
		logger.info("Verified View product campaign list permission.");
		return this;
	}
	public DiscountPage verifyPermissionViewProductCampaignDetail(int productCampaignScheduledId){
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
				commonAction.sleepInMiliSecond(1000,"Wait to view product campaign detail on detail page");
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign name should be shown on detail page, but '%s' is shown".formatted(name));
				//click on edit campaign 0 to check access to edit campaign page
				navigateUrl();
				filterDiscountType("Product Discount Campaign");
				filterDiscountStatus("Scheduled");
				commonAction.click(loc_lst_icnEdit,0);
				new HomePage(driver).waitTillSpinnerDisappear1();
//				commonAction.sleepInMiliSecond(3000,"Wait to view product campaign detail on edit page");
				name = commonAction.getValue(productDiscountCampaignEl.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign name should be shown edit page, but '%s' is shown".formatted(name));

			}else {
				//click on promotion to check
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lstPromotionName,0),
						"[Failed] Restricted page not shown when access to product campaign detail.");
				//click on edit icon to check
				navigateUrl();
				filterDiscountType("Product Discount Campaign");
				filterDiscountStatus("Scheduled");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
						"[Failed] Restricted page not shown when access to edit product campaign page.");
			}
		}else { //don't have permission View list, then check by navigate url
			if (allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
				//check navigate to detail page
				commonAction.navigateToURL(detailUrl);
				commonAction.sleepInMiliSecond(1000,"Wait to view product campaign detail on detail page");
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name when access to product detail page, but '%s' is shown".formatted(name));
				//check navigate to edit page
				commonAction.navigateToURL(editUrl);
				new HomePage(driver).waitTillSpinnerDisappear1();
//				commonAction.sleepInMiliSecond(3000,"Wait to view product campaign detail on edit page");
				name = commonAction.getValue(productDiscountCampaignEl.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Product discount campaign should be shown name when access to edit product detail page, but '%s' is shown".formatted(name));
			} else {
				//check navigate to detail page.
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(detailUrl),
						"[Failed] Restricted page not shown when access to product detail page.");
				//check navigate to edit page
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page not shown when access to edit product.");
			}
		}
		logger.info("Verified View product discount campaign detail permission.");
		return this;
	}
	public DiscountPage verifyPermissionViewServiceCampaignList(){
		navigateUrl();
		filterDiscountType("Service Discount Campaign");
		List<WebElement> promotionList = commonAction.getElements(loc_lstPromotionName);
		if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()){
			assertCustomize.assertTrue(promotionList.size()>0,"[Failed]Service discount campaign list not shown.");
		}else {
			assertCustomize.assertTrue(promotionList.isEmpty(),
					"[Failed] Don't have permission view service discount campaign, but service campaign list still shown (size=%s)".formatted(promotionList.size()));
		}
		logger.info("Verified View service campaign list permission.");
		return this;
	}
	public DiscountPage verifyPermissionViewServiceCampaignDetail(int serviceCampaignId){
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
				commonAction.sleepInMiliSecond(1000);
				String name = commonAction.getText(serviceCampaignPage.loc_detailPage_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign name should be shown on detail page, but '%s' is shown".formatted(name));
				//click on edit campaign 0 to check access to edit campaign page
				navigateUrl();
				filterDiscountType("Service Discount Campaign");
				filterDiscountStatus("Scheduled");
				commonAction.click(loc_lst_icnEdit,0);
				commonAction.sleepInMiliSecond(1000);
				name = commonAction.getValue(serviceCampaignPage.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign name should be shown on edit page, but '%s' is shown".formatted(name));

			}else {
				//click on promotion to check
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lstPromotionName,0),
						"[Failed] Restricted page not shown when access to service detail page.");
				//click on edit icon to check
				navigateUrl();
				filterDiscountType("Service Discount Campaign");
				filterDiscountStatus("Scheduled");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
						"[Failed] Restricted page not shown when access to edit service.");
			}
		}else { //don't have permission View list, then check by navigate url
			if (allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignDetail()) {
				//check navigate to detail page
				commonAction.navigateToURL(detailUrl);
				String name = commonAction.getText(productDiscountCampaignEl.loc_detail_lblDiscountCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name on service detail page, but '%s' is shown".formatted(name));
				//check navigate to edit page
				commonAction.navigateToURL(editUrl);
				name = commonAction.getValue(serviceCampaignPage.loc_txtCampaignName);
				assertCustomize.assertFalse(name.isEmpty(), "[Failed] Service discount campaign should be shown name on service edit page, but '%s' is shown".formatted(name));
			} else {
				//check navigate to detail page.
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(detailUrl),
						"[Failed] Restricted page not shown when access to service detail page.");
				//check navigate to edit page
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page not shown when access to edit service.");
			}
		}
		logger.info("Verifed View service campaign detail permission.");
		return this;
	}
	public DiscountPage verifyPermissionCreateProductCampaign(String productNameOfShopOwner, String productNameOfStaff){
		navigateUrl();
		commonAction.sleepInMiliSecond(500);
		openCreateProductDiscountCampaignPage();
		homePage.waitTillSpinnerDisappear1();
		if (allPermissions.getPromotion().getDiscountCampaign().isCreateProductDiscountCampaign()){
			//Check permission View customer segment list.
			checkPermissionViewCustomerSegmentList();
			//Check permission View product list or View created product list
			new ProductDiscountCampaignPage(driver)
					.navigateToCreateProductCampaignPageUrl();
			checkPermissionViewProductList(productNameOfShopOwner,productNameOfStaff);
			//Check permission View product collection.
			new ProductDiscountCampaignPage(driver)
					.navigateToCreateProductCampaignPageUrl();
			checkPermissionViewProductCollectionList();
			//Check branch permission
			new ProductDiscountCampaignPage(driver)
					.navigateToCreateProductCampaignPageUrl();
			checkPermissionViewBranchList();
			//Create product campaign
			new ProductDiscountCampaignPage(driver)
					.navigateToCreateProductCampaignPageUrl();
			String newProductCampaignName = new ProductDiscountCampaignPage(driver).createDefaultCampaign();
			if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
				navigateUrl();
				commonAction.waitForListLoaded(loc_lstPromotionName,5);
				String currentUrl = commonAction.getCurrentURL();
				assertCustomize.assertTrue(currentUrl.contains("discount/list"),
						"[Failed]Promotion list should be shown after create product campaign");
			}else assertCustomize.assertFalse(new CheckPermission(driver).isAccessRestrictedPresent(),"[Failed] Restricted popup should not be shown");
			//Check has Clone permission.
			if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()){
				filterDiscountType("Product Discount Campaign");
				commonAction.click(loc_lst_icnClone,0);
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.cloneSuccessMessage"),
							"[Failed] Successed message should be shown, but '%s' shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else logger.info("Don't has View product list permission, so no need check clone permission");
		}else {
			assertCustomize.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(),
					"[Failed] Restricted page not show when click create product discount campaign.");
			//Check staff don't have Clone permission.
			if(allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()) {
				navigateUrl();
				filterDiscountType("Product Discount Campaign");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnClone, 0),
						"[Restricted page not show when click on Clone product campaign]");
			}else logger.info("Don't has View product list permission, so no need check clone permission");
		}
		logger.info("Verified Create product discount campaign permission.");
		return this;
	}
	public DiscountPage checkPermissionViewCustomerSegmentList(){
		commonAction.clickJS(productDiscountCampaignEl.loc_lst_chkCustomerSegment,1);
		new ProductDiscountCampaignPage(driver).clickOnAddSegment();
		List<WebElement> segmentList = commonAction.getElements(productDiscountCampaignEl.loc_lst_lblSegmentName);
		if(allPermissions.getCustomer().getSegment().isViewSegmentList())
			assertCustomize.assertTrue(!segmentList.isEmpty(),"[Failed] Segment list should be shown");
		else
			assertCustomize.assertTrue(segmentList.isEmpty(),"[Failed] Segment list should be empty.");
		logger.info("Verified View Customer Segment list permission.");
		return this;
	}
	public DiscountPage checkPermissionViewProductCollectionList(){
		new ProductDiscountCampaignPage(driver)
				.tickAppliesTo(1)
				.clickOnAddCollection();
		List<WebElement> collectionList = commonAction.getElements(productDiscountCampaignEl.loc_lst_lblCollectionName);
		if(allPermissions.getProduct().getCollection().isViewCollectionList())
			assertCustomize.assertTrue(!collectionList.isEmpty(),"[Failed]Product collection list should be shown");
		else
			assertCustomize.assertTrue(collectionList.isEmpty(),"[Failed]Product collection list should be empty.");
		logger.info("Verified View product collection list permission.");
		return this;
	}
	public DiscountPage checkPermissionViewProductList(String productNameOfShopOwner, String productNameOfStaff){
		new ProductDiscountCampaignPage(driver)
				.tickAppliesTo(2)
				.clickOnAddProducts();
		if(allPermissions.getProduct().getProductManagement().isViewProductList()){
			assertCustomize.assertTrue(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should be shown".formatted(productNameOfStaff));
		}else if(allPermissions.getProduct().getProductManagement().isViewCreatedProductList()){
			assertCustomize.assertFalse(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfStaff),
					"[Failed]Product is created by: '%s' should be shown".formatted(productNameOfStaff));
		}else {
			assertCustomize.assertFalse(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertFalse(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should not be shown".formatted(productNameOfStaff));
		}
		logger.info("Verified View product list permission.");
		return this;
	}
	public DiscountPage checkPermissionViewServiceList(String serviceNameOfShopOwner, String serviceNameOfStaff){
		new ServiceDiscountCampaignPage(driver)
				.tickAppliesTo(2)
				.clickOnAddService();
		if(allPermissions.getService().getServiceManagement().isViewListService()){
			assertCustomize.assertTrue(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfShopOwner),
					"[Failed]Service is created by shop owner: '%s' should be shown".formatted(serviceNameOfShopOwner));
			assertCustomize.assertTrue(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfStaff),
					"[Failed]Service is created by staff: '%s' should be shown".formatted(serviceNameOfStaff));
		}else if(allPermissions.getService().getServiceManagement().isViewListCreatedService()){
			assertCustomize.assertFalse(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfShopOwner),
					"[Failed]Service is created by shop owner: '%s' should not be shown".formatted(serviceNameOfShopOwner));
			assertCustomize.assertTrue(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfStaff),
					"[Failed]Service is created by: '%s' should be shown".formatted(serviceNameOfStaff));
		}else {
			assertCustomize.assertFalse(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfShopOwner),
					"[Failed]Service is created by shop owner: '%s' should not be shown".formatted(serviceNameOfShopOwner));
			assertCustomize.assertFalse(new ServiceDiscountCampaignPage(driver).isServiceShowOnSelectServiceList(serviceNameOfStaff),
					"[Failed]Service is created by staff: '%s' should not be shown".formatted(serviceNameOfStaff));
		}
		logger.info("Verified View service list permission.");
		return this;
	}
	public DiscountPage checkPermissionViewServiceCollectionList(){
		commonAction.sleepInMiliSecond(1000);
		new ServiceDiscountCampaignPage(driver)
				.tickAppliesTo(1)
				.clickOnAddServiceCollection();
		List<WebElement> collectionList = commonAction.getElements(new ServiceDiscountCampaignPage(driver).loc_lst_lblCollectionName);
		if(allPermissions.getService().getServiceCollection().isViewCollectionList())
			assertCustomize.assertTrue(!collectionList.isEmpty(),"[Failed]Service collection list should be shown");
		else
			assertCustomize.assertTrue(collectionList.isEmpty(),"[Failed]Service collection list should be empty.");
		logger.info("Verified View service collection list permission.");
		return this;
	}
	public DiscountPage checkPermissionViewBranchList(){
		List<Integer> branchIds = new Login().getInfo(loginInformation).getAssignedBranchesIds();
		List<String> branchNamesAssigned = new BranchManagement(loginInformation).getBranchNameById(branchIds);
		new ProductDiscountCampaignPage(driver)
				.tickApplicableBranch(1)
				.clickOnSelectBranch();
		commonAction.sleepInMiliSecond(1000);
		List<String> branchListActual = new ProductDiscountCampaignPage(driver).getBranchList();
		assertCustomize.assertEquals(branchListActual,branchNamesAssigned,
				"[Failed] Branch list expected: %s \nBranch list actual: %s".formatted(branchNamesAssigned,branchListActual));
		logger.info("Verified View Branch list permission.");
		return this;
	}
	public DiscountPage verifyPermissionCreateServiceCampaign(String serviceNameOfShopOwner, String serviceNameOfStaff){
		navigateUrl();
		openCreateServiceDiscountCampaignPage();
		homePage.waitTillSpinnerDisappear1();
		if (allPermissions.getPromotion().getDiscountCampaign().isCreateServiceDiscountCampaign()){
			//Check permission View customer segment list.
			checkPermissionViewCustomerSegmentList();
			//Check permission View service list or View created service list
			new ServiceDiscountCampaignPage(driver).navigateToCreateServiceCampaign();
			 checkPermissionViewServiceList(serviceNameOfShopOwner,serviceNameOfStaff);
			//Check permission View service collection.
			new ServiceDiscountCampaignPage(driver).navigateToCreateServiceCampaign();
			checkPermissionViewServiceCollectionList();
			//Create service campaign
			new ServiceDiscountCampaignPage(driver).navigateToCreateServiceCampaign();
			String newServiceCampaignName = new ProductDiscountCampaignPage(driver).createDefaultCampaign();
			if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()){
				navigateUrl();
				commonAction.waitForListLoaded(loc_lstPromotionName,5);
				String currentUrl = commonAction.getCurrentURL();
				assertCustomize.assertTrue(currentUrl.contains("discount/list"),
						"[Failed]Promotion list should be shown after create service campaign");
			}else assertCustomize.assertFalse(new CheckPermission(driver).isAccessRestrictedPresent(),"[Failed] Restricted popup should not be shown after click on Create service campaign.");
		//Check has Clone permission.
			if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()){
				filterDiscountType("Service Discount Campaign");
				commonAction.click(loc_lst_icnClone,0);
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.cloneSuccessMessage"),
							"[Failed] Successed message should be shown, but '%s' shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else logger.info("Don't has View service campaign list permission, so no need check Clone permission ");
		}else {
			assertCustomize.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(),
					"[Failed] Restricted page not show when click create service discount campaign.");
			//Check staff don't have Clone permission.
			if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()) {
				navigateUrl();
				filterDiscountType("Service Discount Campaign");
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnClone, 0),
						"[Restricted page not show when click on Clone service campaign]");
			}else logger.info("Don't has View service campaign list permission, so no need check Clone permission ");
		}
		logger.info("Verified Create service campaign permission.");
		return this;
	}
	public DiscountPage verifyPermissionEditProductDiscountCampaign(int productCampaignScheduleId, String productNameOfShopOwner, String productNameOfStaff){
		String editUrl = DOMAIN + "/discounts/edit/WHOLE_SALE/" + productCampaignScheduleId;
		if (allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
			commonAction.navigateToURL(editUrl);
			//Check View customer segment list permission
			checkPermissionViewCustomerSegmentList();
			//Check permission View product list or View created product list
			commonAction.navigateToURL(editUrl);
			checkPermissionViewProductList(productNameOfShopOwner,productNameOfStaff);
			//Check permission View product collection.
			commonAction.navigateToURL(editUrl);
			checkPermissionViewProductCollectionList();
			//Check edit product campaign permission
			if (allPermissions.getPromotion().getDiscountCampaign().isEditProductDiscountCampaign()) {
				commonAction.click(productDiscountCampaignEl.loc_btnSave);
				assertCustomize.assertTrue(commonAction.isElementDisplay(loc_lst_icnEdit),
						"[Failed]Promotion management page should be shown after edit successfully.");
			}else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(productDiscountCampaignEl.loc_btnSave),
						"[Failed] Restricted page not show.");
			}
			logger.info("Verified Edit product campaign permission.");
		}else logger.info("Don't have View product campaign detail, so no need check Edit permission.");
		return this;
	}
	public DiscountPage verifyPermissionEditServiceDiscountCampaign(int serviceCampaignScheduleId, String serviceNameOfShopOwner, String serviceNameOfStaff){
		String editUrl = DOMAIN +"/discounts/edit/WHOLE_SALE_SERVICE/"+serviceCampaignScheduleId;
		if (allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignDetail()) {
			commonAction.navigateToURL(editUrl);
			//Check View customer segment list permission
			checkPermissionViewCustomerSegmentList();
			//Check permission View product list or View created product list
			commonAction.navigateToURL(editUrl);
			checkPermissionViewServiceList(serviceNameOfShopOwner,serviceNameOfStaff);
			//Check permission View product collection.
			commonAction.navigateToURL(editUrl);
			checkPermissionViewServiceCollectionList();
			//Check edit product campaign permission
			if (allPermissions.getPromotion().getDiscountCampaign().isEditServiceDiscountCampaign()) {
				commonAction.click(serviceCampaignPage.loc_btnSave);
				assertCustomize.assertTrue(commonAction.isElementDisplay(loc_lst_icnEdit),
						"[Failed]Promotion management page should be shown after edit service campaign successfully.");
			}else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(serviceCampaignPage.loc_btnSave),
						"[Failed] Restricted page not show when click on save to edit service campaign.");
			}
			logger.info("Verified Edit service campaign permission.");
		}else logger.info("Don't have View service campaign detail, so no need check Edit service campaign permission.");
		return this;
	}
	public DiscountPage endFirstCampaign(){
		commonAction.click(loc_lst_icnEnd,0);
		new ConfirmationDialog(driver).clickOKBtn();
		commonAction.sleepInMiliSecond(500, "Wait in endFirstCampaign");
		return this;
	}
	public DiscountPage verifyPermissionEndProductDiscountCampaign(int productCampaignInprogressId) {
		boolean hasEndEarlyPermission = allPermissions.getPromotion().getDiscountCampaign().isEndProductDiscountCampaign();
		//Check permission end early on detail page
		String url = DOMAIN + "/discounts/detail/WHOLE_SALE/" + productCampaignInprogressId;
		commonAction.navigateToURL(url);
		if (allPermissions.getPromotion().getDiscountCampaign().isViewProductDiscountCampaignDetail()) {
			if (hasEndEarlyPermission) {
				new ProductDiscountCampaignPage(driver).clickOnEndEarlyBtn();
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(new ConfirmationDialog(driver).loc_btnOK,
						"home"), "[Failed] Should be navigate to home page when end early successfully.");
			} else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(productDiscountCampaignEl.loc_btnEndEarly),
						"[Failed]Restricted modal not show.");
			}
			logger.info("Verified End product campaign on Campaign detail");
		}else
			logger.info("Don't have View product campaign detail permission, so no need check end product campaign permission on Discount campaign detail");
		//Check permission end early on promotion list.
		if (allPermissions.getPromotion().getDiscountCampaign().isViewProductCampaignList()) {
			navigateUrl();
			filterDiscountType("Product Discount Campaign");
			filterDiscountStatus("In Progress");
			if (hasEndEarlyPermission) {
				endFirstCampaign();
				String statusFirstCampaign = commonAction.getText(loc_lst_lblStatus, 0);
				try {
					assertCustomize.assertEquals(statusFirstCampaign, PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.status.expired"),
							"[Failed]Ended status should be shown, but status show '%s'".formatted(statusFirstCampaign));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEnd, 0),
						"[Failed] Restricted popup not show.");
			}
			logger.info("Verified End product campaign on Campaign list");
		} else
			logger.info("Don't have View product campain list permission, so no need check end product campaign permission on Discount campaign list.");
		return this;
	}
	public DiscountPage verifyPermissionEndServiceDiscountCampaign(int serviceCampaignInprogessId){
		boolean hasEndEarlyPermission = allPermissions.getPromotion().getDiscountCampaign().isEndServiceDiscountCampaign();
		//Check permission end early on detail page
		String url = DOMAIN + "/discounts/detail/WHOLE_SALE_SERVICE/"+ serviceCampaignInprogessId;
		commonAction.navigateToURL(url);
		if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignDetail()) {
			if (hasEndEarlyPermission) {
				new ServiceDiscountCampaignPage(driver).clickOnEndEarlyBtn();
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(new ConfirmationDialog(driver).loc_btnOK,
						"home"), "[Failed] Should be navigate to home page when end early successfully.");
			} else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(serviceCampaignPage.loc_btnEndEarly),
						"[Failed]Restricted modal not show.");
			}
			logger.info("Verified End service permission in detail page");
		}else
			logger.info("Don't have View service campaign detail permission, so no need check end service campaign permission on Discount campaign detail");

		//Check permission end early on promotion list.
		if(allPermissions.getPromotion().getDiscountCampaign().isViewServiceDiscountCampaignList()){
			navigateUrl();
			filterDiscountType("Service Discount Campaign");
			filterDiscountStatus("In Progress");
			if(hasEndEarlyPermission){
				endFirstCampaign();
				String statusFirstCampaign = commonAction.getText(loc_lst_lblStatus,0);
				try {
					assertCustomize.assertEquals(statusFirstCampaign,PropertiesUtil.getPropertiesValueByDBLang("promotion.discount.promotionManagement.status.expired"),
							"[Failed]Ended status should be shown, but status show '%s'".formatted(statusFirstCampaign));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEnd, 0),
						"[Failed] Restricted popup not show.");
			}
			logger.info("Verified End service campaign on Campaign list");
		}else
			logger.info("Don't have View service campain list permission, so no need check end service campaign permission on Discount campaign list.");
		return this;
	}
	public DiscountPage verifyPermissionDiscountCampaign(AllPermissions allPermissions, String productNameCreatedByShopOwner, String productNameCreatedByStaff, String serviceNameCreatedByShopOwner, String serviceNameCreatedByStaff, int productCampaignInprogressId, int serviceCampaignInprogessId, int productCampaignScheduleId, int serviceCampaignScheduleId ){
		this.allPermissions = allPermissions;
		verifyPermissionViewProductCampaignList();
		verifyPermissionViewProductCampaignDetail(productCampaignScheduleId);
		verifyPermissionViewServiceCampaignList();
		verifyPermissionViewServiceCampaignDetail(serviceCampaignScheduleId);
		verifyPermissionCreateProductCampaign(productNameCreatedByShopOwner,productNameCreatedByStaff);
		verifyPermissionCreateServiceCampaign(serviceNameCreatedByShopOwner,serviceNameCreatedByStaff);
		verifyPermissionEditProductDiscountCampaign(productCampaignScheduleId,productNameCreatedByShopOwner,productNameCreatedByStaff);
		verifyPermissionEditServiceDiscountCampaign(serviceCampaignScheduleId,serviceNameCreatedByShopOwner,serviceNameCreatedByStaff);
		verifyPermissionEndProductDiscountCampaign(productCampaignInprogressId);
		verifyPermissionEndServiceDiscountCampaign(serviceCampaignInprogessId);
		return this;
	}
	public DiscountPage completeVerifyStaffPermissionDiscountCampaign() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
}
