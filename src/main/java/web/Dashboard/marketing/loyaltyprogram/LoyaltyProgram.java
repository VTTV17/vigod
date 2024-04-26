package web.Dashboard.marketing.loyaltyprogram;

import org.apache.commons.collections4.Get;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.marketing.landingpage.LandingPage;

import java.util.List;

public class LoyaltyProgram {

	final static Logger logger = LogManager.getLogger(LoyaltyProgram.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreateLoyaltyProgram createLoyaltyProgram;

	public LoyaltyProgram(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createLoyaltyProgram = new CreateLoyaltyProgram(driver);
	}

	By loc_btnCreateMembership = By.cssSelector(".loyalty .btn-save div");
	By loc_lst_lblProgramName = By.cssSelector(".loyalty-content-body .name");
	By loc_lst_icnDelete = By.cssSelector(".icon-delete");
	By loc_lst_icnUp = By.cssSelector(".icon-up");
	public LoyaltyProgram navigate() {
		new HomePage(driver).navigateToPage("Marketing", "Loyalty Program");
		return this;
	}

	public CreateLoyaltyProgram clickCreateMembershipBtn() {
		commonAction.click(loc_btnCreateMembership);
		logger.info("Clicked on 'Create Membership Level' button.");
		return new CreateLoyaltyProgram(driver);
	}
	
	public LoyaltyProgram deleteMembership(String membership) {
		String xpath = "//div[@class='gs-table-body-item name']/span[text()='%s']/parent::div/following-sibling::div//*[@class='icon-delete']".formatted(membership);
		commonAction.click(By.xpath(xpath));
		logger.info("Click on 'Delete' icon to delete membership '%s'.".formatted(membership));
		return this;
	}

	public LoyaltyProgram clickOKBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked on 'OK' button to confirm membership deletion.");
		return this;
	}
	
	public LoyaltyProgram clickCancelBtn() {
		new ConfirmationDialog(driver).clickGrayBtn();
		logger.info("Clicked on 'Cancel' button to abort membership deletion.");
		return this;
	}
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateLoyaltyProgram(String permission) {
		if (permission.contentEquals("A")) {
			clickCreateMembershipBtn().clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/
	public void navigateByUrl(){
		String url = Links.DOMAIN + "/marketing/loyalty/list";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		commonAction.sleepInMiliSecond(500);
	}
	/* Get Loyalty Program permission */
	public boolean hasViewListMembershipPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isViewListMembership();
	}
	public boolean hasCreateMembershipPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isCreateMembership();
	}
	public boolean hasEditMembershipPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isEditMembership();
	}
	public boolean hasDeleteMembershipPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isDeleteMembership();
	}
	public boolean hasCollocateMembershipPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isCollocateMembership();
	}
	public boolean hasViewMembershipDetailPers(){
		return allPermissions.getMarketing().getLoyaltyProgram().isViewMembershipDetail();
	}
	public boolean hasViewSegmentListPers(){
		return allPermissions.getCustomer().getSegment().isViewSegmentList();
	}
	/* Check staff permission */
	public void checkViewListMembershipPermission(){
		commonAction.waitForListLoaded(loc_lst_lblProgramName,2);
		List<WebElement> programName = commonAction.getElements(loc_lst_lblProgramName);
		if (hasViewListMembershipPers()) {
			assertCustomize.assertTrue(programName.size() > 0, "[Failed] Loyalty program list should be shown");
		} else
			assertCustomize.assertTrue(programName.isEmpty(), "[Failed] Loyalty program should not be shown");
		logger.info("Complete check View Loyalty program list permission.");
	}
	public void checkViewMembershipDetailPermission(){
		if(hasViewListMembershipPers()){
			navigateByUrl();
			if(hasViewMembershipDetailPers()){
				commonAction.waitForListLoaded(loc_lst_lblProgramName,4);
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(loc_lst_lblProgramName,0,createLoyaltyProgram.loc_txtMembershipName),
						"[Failed] Membership name not show.");
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_lblProgramName,0),
						"[Failed] Restricted page not show when go to membership detail.");
		}else{
			//Don't have View list membership permission, detail membership not show when navigate to url
			logger.info("Don't have View list membership permission, so no need check View detail");
		}
	}
	public void checkCreateMembershipPermission(){
		navigateByUrl();
		if(hasCreateMembershipPers()){
			clickCreateMembershipBtn();
			checkPermissionViewSegmentList();
			createLoyaltyProgram.navigateByUrl();
			createLoyaltyProgram.createRandomMembership().clickSaveBtn();
			String message = new ConfirmationDialog(driver).getPopUpContent();
			try {
				assertCustomize.assertEquals(message, PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyProgram.create.successMessage"),
						"[Failed] Created successfully message not shown after create, but '%s' is show".formatted(message));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateMembership),
					"[Failed] Restricted page not show when click on Create memberhip.");
	}
	public void checkPermissionViewSegmentList(){
		createLoyaltyProgram.clickAddSegment();
		commonAction.waitForListLoaded(new AddSegmentDialog(driver).loc_lblSegments,3);
		List<WebElement> segmentNames = commonAction.getElements(new AddSegmentDialog(driver).loc_lblSegments);
		if(hasViewSegmentListPers()){
			assertCustomize.assertTrue(segmentNames.size()>0,"[Failed] Customer segment should be shown");
		}else assertCustomize.assertTrue(segmentNames.isEmpty(),"[Failed] Customer segment should not be shown.");
		logger.info("Complete check View customer segment list permission");
	}
	public void checkPermissionEditMembership(){
		if(hasViewListMembershipPers()){
			navigateByUrl();
			if(hasViewMembershipDetailPers()){
				commonAction.click(loc_lst_lblProgramName,0);
				if(hasEditMembershipPers()){
					createLoyaltyProgram.clickSaveBtn();
					String message = new ConfirmationDialog(driver).getPopUpContent();
					try {
						assertCustomize.assertEquals(message,PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyProgram.update.successMessage"),"" +
								"[Failed] Updated successful should be shown.");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}else
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createLoyaltyProgram.loc_btnSave),
							"[Failed] Restricted popup not show when click on Save button on edit page");
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_lblProgramName,0),
					"[Failed] Restricted popup not show when go to detail page");

		}else logger.info("Don't have View membership detail, so no need check Edit permission.");
	}
	public void checkCollocateMembershipLevelPermission(){
		if(hasViewListMembershipPers()){
			navigateByUrl();
			List<WebElement> programNameList = commonAction.getElements(loc_lst_lblProgramName,10);
			if(programNameList.size()>1) {
				if (hasCollocateMembershipPers()) {
					String secondProgramName = commonAction.getText(loc_lst_lblProgramName, 1);
					commonAction.click(loc_lst_icnUp, 1);
					commonAction.sleepInMiliSecond(1000);
					String firstProgramName = commonAction.getText(loc_lst_lblProgramName, 0);
					assertCustomize.assertEquals(firstProgramName, secondProgramName, "[Failed]The second program is not moved to first");
				} else
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnUp, 1),
							"[Failed] Restricted popup not show.");
			}else logger.info("Membership program list size less than 2, so can't check Collocate permission.");
		}else logger.info("Don't have View Membership program list, so can't check Collocate permission.");
	}
	public void checkDeletePermission(){
		if(hasViewListMembershipPers()){
			navigateByUrl();
			if(hasDeleteMembershipPers()){
				commonAction.click(loc_lst_icnDelete,0);
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(message,PropertiesUtil.getPropertiesValueByDBLang("marketing.loyaltyProgram.delete.confirmContent"),
							"[Failed] Delete confirm message not show.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnDelete,0),
					"[Failed] Restricted popup not show.");
		}else logger.info("Don't have View list membership permission, so can't check Delete permission");
	}
	public LoyaltyProgram completeVerifyLoyaltyProgramPermission() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
	public void checkLoyaltyProgramPermission(AllPermissions allPermissions){
		this.allPermissions = allPermissions;
		checkViewListMembershipPermission();
		checkCreateMembershipPermission();
		checkPermissionEditMembership();
		checkViewMembershipDetailPermission();
		checkCollocateMembershipLevelPermission();
		checkDeletePermission();
		completeVerifyLoyaltyProgramPermission();
	}
}
