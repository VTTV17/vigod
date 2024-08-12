package web.Dashboard.marketing.loyaltypoint;

import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.dashboard.marketing.loyaltyPoint.LoyaltyPointInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.marketing.loyaltyprogram.LoyaltyProgram;

public class LoyaltyPoint {
	
	final static Logger logger = LogManager.getLogger(LoyaltyPoint.class);

    WebDriver driver;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	LoginInformation shopOwnerLoginInfo;
    
    public LoyaltyPoint (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
    }
	public LoyaltyPoint getLoginInformation(LoginInformation shopOwnerLoginInfo){
		this.shopOwnerLoginInfo = shopOwnerLoginInfo;
		return this;
	}
    By loc_btnSave = By.cssSelector(".loyalty-point-setting .gs-button__green");
    By loc_btnActivateNow = By.cssSelector(".loyalty-point-intro__left-col__activate");
	By loc_lblStatus = By.cssSelector(".loyalty-point-setting-section__header__status");
	By loc_btnStatus = By.cssSelector(".uik-checkbox__toggle");
    
    public LoyaltyPoint clickSave() {
    	commonAction.click(loc_btnSave);
    	logger.info("Clicked on 'Save' button.");
        return this;
    }
    
    public LoyaltyPoint clickActivateNow() {
    	commonAction.sleepInMiliSecond(1000);
    	if (commonAction.getElements(loc_btnActivateNow).size() == 0) return this;
    	
		if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_btnActivateNow))) {
			new HomePage(driver).isMenuClicked(commonAction.getElement(loc_btnActivateNow));
			return this;
		}
    	commonAction.clickElement(driver.findElement(loc_btnActivateNow));
    	logger.info("Clicked on 'Activate Now' button.");
    	return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToConfigureLoyaltyPoint(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickActivateNow();
			clickSave();
			new HomePage(driver).getToastMessage();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/
	public LoyaltyPoint navigateByUrl(){
		String url = Links.DOMAIN + "/marketing/loyalty-point/setting";
		commonAction.navigateToURL(url);
		commonAction.sleepInMiliSecond(500);
		logger.info("Navigate to url: "+url);
		return this;
	}
    public boolean hasViewPointProgramInformation(){
		return allPermissions.getMarketing().getLoyaltyPoint().isViewPointProgramInformation();
	}
	public boolean hasEnableProgram(){
		return allPermissions.getMarketing().getLoyaltyPoint().isEnableProgram();
	}
	public boolean hasDisableProgram(){
		return allPermissions.getMarketing().getLoyaltyPoint().isDisableProgram();
	}
	public boolean hasEditProgram(){
		return allPermissions.getMarketing().getLoyaltyPoint().isEditProgram();
	}
	public void checkPermissionViewPointProgramInfo(){
		if(hasViewPointProgramInformation()){
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(Links.DOMAIN+"/marketing/loyalty-point/setting","loyalty-point/setting"),
					"Loyalty point setting page not shown");
		}else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(Links.DOMAIN+"/marketing/loyalty-point/setting"),
					"Restricted page not show when navigate to loyalty point setting link.");
		logger.info("Verified View point program permission.");
	}
	public void checkPermissionEnableProgram(){
		Response response = new api.Seller.marketing.LoyaltyPoint(shopOwnerLoginInfo).enableOrDisableProgram(false);
		if(response != null) response.then().statusCode(200);
		if(hasViewPointProgramInformation()) {
			new HomePage(driver).navigateToPage("Home");
			new HomePage(driver).navigateToPage("Marketing","Loyalty Point");
			if (hasEnableProgram()) {
				commonAction.click(loc_btnStatus);
				String status = commonAction.getText(loc_lblStatus);
				try {
					assertCustomize.assertEquals(status,PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyPoint.status.enable"),
							"[Failed] Status = enable not show.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnStatus),
						"[Failed] Restricted popup not show when click to enable loyalty point.");
			logger.info("Verified Enable program permission.");
		}else logger.info("Don't have View loyalty point setting permission, so can't check enable program permission.");
	}
	public void checkPermissionDisableProgram(){
		Response response = new api.Seller.marketing.LoyaltyPoint(shopOwnerLoginInfo).enableOrDisableProgram(true);
		if(response != null) response.then().statusCode(200);
		commonAction.sleepInMiliSecond(1000, "Wait to update enable/disable point program.");
		if(hasViewPointProgramInformation()) {
			new HomePage(driver).navigateToPage("Home");
			new HomePage(driver).navigateToPage("Marketing","Loyalty Point");
			if (hasDisableProgram()) {
				commonAction.click(loc_btnStatus);
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertTrue(message.contains(PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyPoint.deletePointNotiMessage")),
							"[Failed] Delete point notification message should be shown.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				new ConfirmationDialog(driver).clickOnRedBtn();
				String status = commonAction.getText(loc_lblStatus);
				try {
					assertCustomize.assertEquals(status,PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyPoint.status.disable"),
							"[Failed] Status = enable not show.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnStatus),
						"[Failed] Restricted popup not show when click to disable loyalty point.");
			logger.info("Verified Disable program permission.");
		}else logger.info("Don't have View loyalty point setting permission, so can't check disable program permission.");
	}
	public void checkEditProgramPermission(){
		if(hasViewPointProgramInformation()){
			navigateByUrl();
			if(hasEditProgram()){
				clickSave();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyPoint.update.successMessage"),
							"[Failed] Updated successfully message not show");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnSave),
						"Restricted popup not show when click on Save button.");
			logger.info("Verified Edit program permission.");
		}else logger.info("Don't have View program info permission, so can't check Edit program permission.");
	}
	public LoyaltyPoint completeVerifyLoyaltyPointPermission() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
	public LoyaltyPoint checkLoyaltyPointPermission(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		checkPermissionViewPointProgramInfo();
		checkPermissionEnableProgram();
		checkPermissionDisableProgram();
		checkEditProgramPermission();
		completeVerifyLoyaltyPointPermission();
		return this;
	}
}
