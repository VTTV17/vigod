package web.Dashboard.reservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;

public class ReservationDetail {
    final static Logger logger = LogManager.getLogger(ReservationDetail.class);

    WebDriver driver;
    UICommonAction commonAction;

    public ReservationDetail(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
    By loc_lblServiceName = By.cssSelector(".detail-center .name");
    By loc_btnEdit = By.cssSelector(".action > span");
    By loc_dlgEditReservation = By.cssSelector(".modal-cancel-reservation-confirm");
    By loc_btnConfirm = By.cssSelector("#btn-main");
    By loc_cntToastMessage = By.cssSelector(".Toastify__toast-body");
    By loc_btnCancel = By.cssSelector("#btn-cancelOrder");
    By loc_dlgConfirmation_ctnMessage = By.cssSelector(".modal-body");
}
