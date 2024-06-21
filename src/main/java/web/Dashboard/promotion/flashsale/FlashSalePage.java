package web.Dashboard.promotion.flashsale;

import static java.lang.Thread.sleep;

import java.time.Duration;
import java.util.List;

import api.Seller.promotion.FlashSale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import web.Dashboard.home.HomePage;
import web.Dashboard.promotion.discount.DiscountPage;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;
import web.Dashboard.promotion.flashsale.campaign.FlashSaleCampaignElement;
import web.Dashboard.promotion.flashsale.campaign.FlashSaleCampaignPage;
import web.Dashboard.promotion.flashsale.time.TimeManagementElement;
import web.Dashboard.promotion.flashsale.time.TimeManagementPage;
import utilities.commons.UICommonAction;

public class FlashSalePage extends FlashSaleElement {
    WebDriverWait wait;
    UICommonAction commonAction;

    public static String flashSaleURL;
    AllPermissions allPermission;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    FlashSaleCampaignElement campaignDetailEl;
    TimeManagementElement timeManagementEl;
    public FlashSalePage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        campaignDetailEl = new FlashSaleCampaignElement(driver);
        timeManagementEl = new TimeManagementElement(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    Logger logger = LogManager.getLogger(FlashSalePage.class);

    /**
     * Open Flash Sale page
     */
    public FlashSalePage openFlashSaleCampaignManagementPage() throws InterruptedException {
        // On home page:
        // 1. Hide facebook bubble
        // 2. Navigate to Promotion/Flash Sale
        new HomePage(driver)
                .hideFacebookBubble()
                .navigateToPromotion_FlashSalePage();

        // log
        logger.info("Current page is %s".formatted(driver.getTitle()));

        return this;
    }

    public TimeManagementPage navigateToFlashSaleTimeManagementPage() throws InterruptedException {
        // wait flash sale intro page loaded, if any
        sleep(1000);

        // in case, flash sale intro page is shown, click on Explore Now button to skip
        if (driver.getCurrentUrl().contains("intro")) {
            // click Explore Now
            wait.until(ExpectedConditions.visibilityOf(EXPLORE_NOW_BTN)).click();
            logger.info("Skip Flash sale intro");
        }

        // get flashSaleURL
        flashSaleURL = driver.getCurrentUrl();

        // navigate to manage flash sale time page
        wait.until(ExpectedConditions.elementToBeClickable(MANAGE_FLASH_SALE_TIME_BTN)).click();
        logger.info("Navigate to manage flash sale time page");

        return new TimeManagementPage(driver);
    }
    
    public FlashSalePage clickCreateCampaign() {
    	commonAction.clickElement(CREATE_CAMPAIGN_BTN);
    	logger.info("Clicked on 'Create Campaign' button.");
    	return this;
    } 
    
    public FlashSalePage clickExploreNow() {
    	commonAction.clickElement(EXPLORE_NOW_BTN);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }     
   
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateFlashSale(String permission) {
		if (permission.contentEquals("A")) {
			clickExploreNow().clickCreateCampaign();
			new FlashSaleCampaignPage(driver).inputCampaignName("Test Permission");
			commonAction.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/
    public FlashSalePage navigateUrl(){
        String url = Links.DOMAIN + "/flash-sale/list";
        commonAction.navigateToURL(url);
        commonAction.sleepInMiliSecond(1000);
        logger.info("Navigate to url: "+url);
        return this;
    }
    public FlashSalePage clickOnEditFirstFlashSale(){
        commonAction.getElements(loc_icnEdit,3).get(0).click();
        logger.info("Click on edit icon.");
        return this;
    }
    public FlashSalePage filterCampaignStatus(String campaignStatus){
        commonAction.click(loc_ddlCampaignStatus);
        switch (campaignStatus){
            case "All Status":
                commonAction.click(loc_lst_ddvStatus,0);
                break;
            case "Scheduled":
                commonAction.click(loc_lst_ddvStatus,1);
                break;
            case "In Progress":
                commonAction.click(loc_lst_ddvStatus,2);
                break;
            case "Ended":
                commonAction.click(loc_lst_ddvStatus,3);
                break;
            default:
                try {
                    throw new Exception("Campaign Status not found!");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
        commonAction.sleepInMiliSecond(500,"Wait in filter flashsale.");
        return this;
    }
    public FlashSalePage clickOnManageFlashSaleTime(){
        commonAction.click(loc_btnManageFlashSaleTime);
        logger.info("CLick on Manage Flash Sale time button.");
        return this;
    }

    public FlashSalePage verifyPermissionViewFlashSaleList(){
        List<WebElement> flashSaleList = commonAction.getElements(loc_lst_lblFlashSaleCampaignName,3);
        if(allPermission.getPromotion().getFlashSale().isViewFlashSaleList()){
            assertCustomize.assertTrue(!flashSaleList.isEmpty(),
                    "[Failed] Flash sale list should be shown, but list have size = %s".formatted(flashSaleList.size()));
        }else {
            assertCustomize.assertTrue(flashSaleList.isEmpty(),
                    "[Failed] Flash sale list should be empty, but list have size = %s".formatted(flashSaleList.size()));
        }
        logger.info("Verified permission View flash sale list.");
        return this;
    }
    public FlashSalePage verifyPermissionViewFlashSaleDetail(int flashSaleId){
        boolean hasPermissionViewList = allPermission.getPromotion().getFlashSale().isViewFlashSaleList();
        boolean hasPermissionViewDetail = allPermission.getPromotion().getFlashSale().isViewFlashSaleDetail();
        if(hasPermissionViewList){
            filterCampaignStatus("Scheduled");
            clickOnEditFirstFlashSale();
            if(hasPermissionViewDetail){
                assertCustomize.assertTrue(!commonAction.getValue(campaignDetailEl.loc_txtCampaignName).isEmpty(),
                        "[Failed] Campaign name should not be empty.");
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(),
                        "[Failed] Restricted page should be shown.");
            }
        }else {
            String campaignDetailUrl = Links.DOMAIN +"/flash-sale/edit/"+ flashSaleId;
            if(hasPermissionViewDetail){
                assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(campaignDetailUrl,campaignDetailEl.loc_txtCampaignName),
                        "[Failed]Campaign should be shown value.");
            }else {
                assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(campaignDetailUrl),
                        "[Failed] Restricted page should be shown.");
            }
        }
        return this;
    }
    public FlashSalePage verifyPermissionViewFlashSaleTime(){
        navigateUrl();
        clickOnManageFlashSaleTime();
        List<WebElement> startTimeList = commonAction.getElements(timeManagementEl.loc_lst_lblStartTime,3);
        if(allPermission.getPromotion().getFlashSale().isViewFlashSaleTime()){
            assertCustomize.assertTrue(!startTimeList.isEmpty(),
                    "[Failed] Manage time list should be shown, but list have size = %s".formatted(startTimeList.size()));
        }else {
            assertCustomize.assertTrue(startTimeList.isEmpty(),
                    "[Failed] Manage time list should be empty, but list have size = %s".formatted(startTimeList.size()));
        }
        logger.info("Verified permission View manage time list.");
        return this;
    }
    public FlashSalePage verifyPermissionAddFlashSaleTime(){
        navigateUrl();
        clickOnManageFlashSaleTime();
        if(allPermission.getPromotion().getFlashSale().isAddFlashSaleTime()){
            new TimeManagementPage(driver).addAFlashSaleTime("01","10");
            commonAction.sleepInMiliSecond(1000);
            String modalMessage = new TimeManagementPage(driver).getPopUpMessage();
            try {
                assertCustomize.assertEquals(modalMessage, PropertiesUtil.getPropertiesValueByDBLang("promotion.flashSale.addTime.successMessage"),
                        "[Failed] Flash Sale time created successfully message should be shown");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(timeManagementEl.loc_btnAddTime),
                    "[Failed] Restricted popup should be shown.");
        }
        logger.info("Verified Add Flash Sale time permission.");
        return this;
    }
    public FlashSalePage verifyPermissionCreateFlashSale(String productNameOfShopOwner, String productNameOfStaff){
        navigateUrl();
        if(allPermission.getPromotion().getFlashSale().isCreateFlashSale()){
            boolean hasPermissionViewProductList = allPermission.getProduct().getProductManagement().isViewProductList();
            boolean hasPermissionViewCreatedProductList = allPermission.getProduct().getProductManagement().isViewCreatedProductList();
            boolean hasPermissionViewTimeList = allPermission.getPromotion().getFlashSale().isViewFlashSaleTime();
            if((hasPermissionViewProductList||hasPermissionViewCreatedProductList)&& hasPermissionViewTimeList){
                clickCreateCampaign();
                checkPermissionViewProductList(productNameOfShopOwner,productNameOfStaff);
                new FlashSaleCampaignPage(driver).navigateToCreateFlashSale()
                        .createRadomFlashSale(productNameOfStaff);
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("promotion.flashSale.create.successMessage"),
                            "[Failed] Created successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnCreateCampaign,"flash-sale/create"),
                    "[Failed] Create flash sale should be shown.");

        }else
            assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateCampaign),
                    "[Failed] Restricted page should be shown when click on Create FlashSale");
        logger.info("Verified Create FlashSale permission.");
        return this;
    }
    public FlashSalePage checkPermissionViewProductList(String productNameOfShopOwner, String productNameOfStaff){
        new FlashSaleCampaignPage(driver).clickOnAddProduct();
        if(allPermission.getProduct().getProductManagement().isViewProductList()){
            assertCustomize.assertTrue(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfShopOwner),
                    "[Failed]Product is created by shop owner: '%s' should be shown".formatted(productNameOfShopOwner));
            assertCustomize.assertTrue(new ProductDiscountCampaignPage(driver).isProductShowOnSelectProductList(productNameOfStaff),
                    "[Failed]Product is created by staff: '%s' should be shown".formatted(productNameOfStaff));
        }else if(allPermission.getProduct().getProductManagement().isViewCreatedProductList()){
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
    public FlashSalePage verifyPermissionEditFlashSale(int flashSaleScheduleId){
        boolean hasPermissionViewList = allPermission.getPromotion().getFlashSale().isViewFlashSaleList();
        boolean hasPermissionViewDetail = allPermission.getPromotion().getFlashSale().isViewFlashSaleDetail();
        boolean hasPermissionEdit =  allPermission.getPromotion().getFlashSale().isEditFlashSale();
        navigateUrl();
        if(hasPermissionViewDetail){
            if(hasPermissionViewList){
                filterCampaignStatus("Scheduled");
                clickOnEditFirstFlashSale();
                if(hasPermissionEdit){
                    new FlashSaleCampaignPage(driver).completeCreateFlashSaleCampaign();
                    String toastMessage =  new HomePage(driver).getToastMessage();
                    try {
                        assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("promotion.flashSale.edit.successMessage"),
                                "[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else
                    assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(campaignDetailEl.loc_btnSave),
                        "[Failed] Restricted popup should be shown when click on Save to edit flashsale campaign.");
            }else {
                //Navigate to Url if don't have View list permission
                String campaignDetailUrl = Links.DOMAIN +"/flash-sale/edit/"+ flashSaleScheduleId;
                commonAction.navigateToURL(campaignDetailUrl);
                logger.info("Navigate to Url because staff don't have View list permission");
                if(hasPermissionEdit){
                    new FlashSaleCampaignPage(driver).completeCreateFlashSaleCampaign();
                    String toastMessage =  new HomePage(driver).getToastMessage();
                    try {
                        assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("promotion.flashSale.edit.successMessage"),
                                "[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else
                    assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(campaignDetailEl.loc_btnSave),
                            "[Failed] Restricted popup should be shown when click on Save to edit flashsale campaign.");
            }
        }else logger.info("Don't has View detail permission, so no need check Edit permission.");
        return this;
    }
    public FlashSalePage verifyPermissionDeleteFlashSale(){
        if(allPermission.getPromotion().getFlashSale().isViewFlashSaleList()){
            navigateUrl();
            if(allPermission.getPromotion().getFlashSale().isDeleteFlashSale()){
                filterCampaignStatus("Scheduled");
                clickOnDeleteFirstFlashSale();
                commonAction.click(loc_dlgComfirmation_btnOK);
                String toastMessage = new HomePage(driver).getToastMessage();
                try {
                    assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("promotion.flashSale.edit.successMessage"),
                            "[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_icnDelete,0),
                    "[Failed] Restricted popup not show when click on delete button");
        }else logger.info("Don't has View FlashSale list, so no need check delete permission.");
        return this;
    }
    public FlashSalePage clickOnDeleteFirstFlashSale(){
        commonAction.click(loc_icnDelete,0);
        logger.info("Click on delete icon of first schedule flashsale");
        return this;
    }
    public FlashSalePage verifyPermissionFlashSale(AllPermissions allPermissions, int flashSaleScheduleId,String productNameOfShopOwner, String productNameOfStaff){
        this.allPermission = allPermissions;
        verifyPermissionViewFlashSaleList();
        verifyPermissionViewFlashSaleDetail(flashSaleScheduleId);
        verifyPermissionViewFlashSaleTime();
        verifyPermissionAddFlashSaleTime();
        verifyPermissionCreateFlashSale(productNameOfShopOwner,productNameOfStaff);
        verifyPermissionEditFlashSale(flashSaleScheduleId);
        verifyPermissionDeleteFlashSale();
        AssertCustomize.verifyTest();
        return this;
    }
    public FlashSalePage completeVerifyStaffPermissionFlashSale() {
        logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
        if (assertCustomize.getCountFalse() > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
        }
        return this;
    }
}
