package web.Dashboard.reservation;

import com.github.dockerjava.api.model.Link;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.Home.Home;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.service.CreateServiceElement;
import web.Dashboard.service.ServiceManagementPage;

import java.util.List;

public class ReservationManagement {

	final static Logger logger = LogManager.getLogger(ReservationManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	ReservationDetail reservationDetailUI;

	public ReservationManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		reservationDetailUI = new ReservationDetail(driver);
	}

	By loc_txtSearch = By.cssSelector(".reservation-list__filter-container input.uik-input__input");
	By loc_frmDatePicker = By.cssSelector("#searchDate");
	By loc_btnReset = By.cssSelector(".show-calendar .cancelBtn");
	By loc_lst_lblServiceName = By.cssSelector(".service-name .full-name");

	public ReservationManagement inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearch, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToManageReservation(String permission) {
		if (permission.contentEquals("A")) {
			inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/
	public ReservationManagement navigateToReservationsUrl(){
		commonAction.navigateToURL(Links.DOMAIN+"/reservation/list");
		logger.info("Navigate to reservation list.");
		commonAction.sleepInMiliSecond(200);
		return this;
	}
	public ReservationManagement clickDatePicker(){
		commonAction.click(loc_frmDatePicker);
		return this;
	}
	public ReservationManagement clickResetPicker(){
		commonAction.click(loc_btnReset);
		return this;
	}
	public ReservationManagement resetDatePicker(){
		clickDatePicker();
		clickResetPicker();
		return this;
	}
	public void checkPermissionViewReservationList(){
		navigateToReservationsUrl();
		resetDatePicker();
		new HomePage(driver).waitTillSpinnerDisappear1();
		int serviceListSize = commonAction.getElements(loc_lst_lblServiceName).size();
		if(allPermissions.getReservation().getReservationManagement().isViewReservationList())
			assertCustomize.assertTrue(serviceListSize>0,
					"[Failed] Reservation list should be shown, but list size is %s".formatted(serviceListSize));
		else
			assertCustomize.assertTrue(serviceListSize==0,
					"[Failed] Reservation list should not be shown, but currently list size is %s".formatted(serviceListSize));
		logger.info("Verified permission View Reservation list.");
	}
	public void checkPermissionViewReservationDetail(int reservationId){
		boolean hasPermissionViewReservationList = allPermissions.getReservation().getReservationManagement().isViewReservationList();
		boolean hasPermissionViewReservationDetail = allPermissions.getReservation().getReservationManagement().isViewReservationDetail();
		if(hasPermissionViewReservationList){
			navigateToReservationsUrl();
			resetDatePicker();
			if(hasPermissionViewReservationDetail){
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_lst_lblServiceName,reservationDetailUI.loc_lblServiceName),
						"[Failed] Service name on detail page not show.");
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_lblServiceName,0),
						"[Failed] Restricted page or popup not show.");
		}else {
			String reservationDetailUrl = Links.DOMAIN+"/reservation/detail/"+reservationId;
			if(hasPermissionViewReservationDetail){
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(reservationDetailUrl,reservationDetailUI.loc_lblServiceName),
						"[Failed] No has view list reservation. Service name on detail page not show.");
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(reservationDetailUrl),
						"[Failed] No has view list reservation. Restricted page or popup not show.");
		}
		logger.info("Verified permission View Reservation detail.");
	}
	public void checkPermissionEditReservation(int reservationId) {
		boolean hasPermissionEditReservation = allPermissions.getReservation().getReservationManagement().isEditReservation();
		boolean hasPermissionViewReservationDetail = allPermissions.getReservation().getReservationManagement().isViewReservationDetail();
		if (hasPermissionViewReservationDetail) {
			String detailUrl = Links.DOMAIN + "/reservation/detail/" + reservationId;
			commonAction.navigateToURL(detailUrl);
			new HomePage(driver).waitTillSpinnerDisappear1();
			if (hasPermissionEditReservation)
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(reservationDetailUI.loc_btnEdit, reservationDetailUI.loc_dlgEditReservation),
						"[Failed] Edit popup not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(reservationDetailUI.loc_btnEdit),
						"[Failed] Restricted page or popup not show.");
		} else {
			logger.info("Don't has permission View detail, so no need check edit reservation permission.");
			return;
		}
		logger.info("Verified Edit reservation permission.");
	}
	public void checkPermissionConfirmReservation(int reservationId){
		boolean hasPermissionConfirmReservation = allPermissions.getReservation().getReservationManagement().isConfirmReservation();
		boolean hasPermissionViewReservationDetail = allPermissions.getReservation().getReservationManagement().isViewReservationDetail();
		if (hasPermissionViewReservationDetail) {
			String detailUrl = Links.DOMAIN + "/reservation/detail/" + reservationId;
			commonAction.navigateToURL(detailUrl);
			new HomePage(driver).waitTillSpinnerDisappear1();
			if(hasPermissionConfirmReservation)
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(reservationDetailUI.loc_btnConfirm,reservationDetailUI.loc_cntToastMessage),
						"[Failed] Toast message after confirm not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(reservationDetailUI.loc_btnConfirm),
						"[Failed] Restrict popup not show.");
		}else {
			logger.info("Don't has permission View detail, so no need check confirm reservation permission.");
			return;
		}
		logger.info("Verified Confirm reservation permission.");
	}
	public void checkPermissionCompleteReservation(int confirmedReservationId){
		boolean hasPermissionCompleteReservation = allPermissions.getReservation().getReservationManagement().isCompleteReservation();
		boolean hasPermissionViewReservationDetail = allPermissions.getReservation().getReservationManagement().isViewReservationDetail();
		if (hasPermissionViewReservationDetail) {
			String detailUrl = Links.DOMAIN + "/reservation/detail/" + confirmedReservationId;
			commonAction.navigateToURL(detailUrl);
			new HomePage(driver).waitTillSpinnerDisappear1();
			commonAction.sleepInMiliSecond(2000);
			System.out.println("hasPermissionCompleteReservation: "+hasPermissionCompleteReservation);
			if(hasPermissionCompleteReservation)
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(reservationDetailUI.loc_btnConfirm,reservationDetailUI.loc_cntToastMessage),
						"[Failed] Completed successfully toast message not show.");
			else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(reservationDetailUI.loc_btnConfirm),
						"[Failed] Restrict popup not show.");
		}else {
			logger.info("Don't has permission View detail, so no need check complete reservation permission.");
			return;
		}
		logger.info("Verified Complete reservation permission.");
	}
	public void checkPermissionCancelReservation(int reservationId){
		boolean hasPermissionCancelReservation = allPermissions.getReservation().getReservationManagement().isCancelReservation();
		boolean hasPermissionViewReservationDetail = allPermissions.getReservation().getReservationManagement().isViewReservationDetail();
		if (hasPermissionViewReservationDetail) {
			String detailUrl = Links.DOMAIN + "/reservation/detail/" + reservationId;
			commonAction.navigateToURL(detailUrl);
			new HomePage(driver).waitTillSpinnerDisappear1();
			commonAction.click(reservationDetailUI.loc_btnCancel);
			if(hasPermissionCancelReservation) {
				try {
					String confirmCancelMessageExpected = PropertiesUtil.getPropertiesValueByDBLang("reservations.detail.confirmCancelMessage");
					String messageActual = commonAction.getText(reservationDetailUI.loc_dlgConfirmation_ctnMessage);
					assertCustomize.assertEquals(messageActual,confirmCancelMessageExpected,"[Failed] Cancel confirmation message should be shown, but '%s' is shown.".formatted(messageActual));

				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(reservationDetailUI.loc_btnCancel),
						"[Failed] Restrict popup not show.");
		}else {
			logger.info("Don't has permission View detail, so no need check complete reservation permission.");
			return;
		}
		logger.info("Verified Cancel reservation permission.");
	}
	public ReservationManagement checkPermissionReservationManagement(AllPermissions allPermissions, int toConfirmReservationId, int confirmedReservationId ){
		this.allPermissions = allPermissions;
		checkPermissionViewReservationList();
		checkPermissionViewReservationDetail(toConfirmReservationId);
		checkPermissionEditReservation(toConfirmReservationId);
		checkPermissionConfirmReservation(toConfirmReservationId);
		checkPermissionCompleteReservation(confirmedReservationId);
		checkPermissionCancelReservation(toConfirmReservationId);
		return this;
	}
	public ReservationManagement completeVerifyStaffPermissionReservationManagement() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
}
