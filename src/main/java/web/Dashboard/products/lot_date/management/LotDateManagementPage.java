package web.Dashboard.products.lot_date.management;

import api.Seller.products.lot_date.APILotDate;
import api.Seller.products.lot_date.APILotDate.LotDateManagementInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.lot_date.crud.LotDatePage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class LotDateManagementPage extends LotDateManagementElement {
    WebDriver driver;
    Logger logger = LogManager.getLogger(LotDateManagementPage.class);
    UICommonAction commonAction;

    public LotDateManagementPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-24808
    AllPermissions permissions;
    AssertCustomize assertCustomize;
    CheckPermission checkPermission;
    LotDatePage lotDatePage;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    public LotDateManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellerLoginInformation = sellerLoginInformation;

        return this;
    }

    public void checkLotDatePermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // init lot-date page
        lotDatePage = new LotDatePage(driver);

        // check view lot list
        LotDateManagementInfo info = new APILotDate(sellerLoginInformation).getAllLotDateInformation();
        checkViewLotList(info);

        // check view lot detail
        if (!info.getLotDateIds().isEmpty())
            lotDatePage.checkViewLotDetail(permissions, info.getLotDateIds().get(0));

        // check create lot
        checkCreateLot();

        // check delete a lot
        checkDeleteLot();
    }

    public void navigateToLotDateManagementPage() {
        if (!driver.getCurrentUrl().contains("/lot-date/list")) {
            driver.get("%s/lot-date/list".formatted(DOMAIN));
            logger.info("Navigate to lot-date management page by URL.");
        }
    }

    void checkViewLotList(LotDateManagementInfo info) {
        List<Integer> lotDateIds = new APILotDate(staffLoginInformation).getAllLotDateInformation().getLotDateIds();
        if (permissions.getProduct().getLotDate().isViewLotList()) {
            // check list lot-date
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(info.getLotDateIds(), lotDateIds),
                    "Lot-date list must be %s, but found %s.".formatted(info.getLotDateIds().toString(), lotDateIds.toString()));
        } else {
            // if staff don’t have permission “View lot list”
            // => don’t see any collection when access Lot date page
            assertCustomize.assertTrue(lotDateIds.isEmpty(),
                    "All lot-dates must be hidden, but found: %s.".formatted(lotDateIds.toString()));
        }
        logger.info("Check permission: Product >> Lot-date >> View lot list.");
    }

    void checkCreateLot() {
        // navigate to lot-date management page
        navigateToLotDateManagementPage();

        // check permission
        if (permissions.getProduct().getLotDate().isCreateLot()) {
            // check can open add lot-date popup
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCreateLotDate, loc_dlgAddLotDate),
                    "Can not open add lot-date popup.");

            // check can add new lot-date
            if (!commonAction.getListElement(loc_dlgAddLotDate).isEmpty()) {
                lotDatePage.addNewLotDate();
            }
        } else {
            // if staff don’t have permission “Create lot”
            // => show restricted popup
            // when click on Create Lot button
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateLotDate),
                    "Restricted popup is not shown.");
        }
        logger.info("Check permission: Product >> Lot-date >> Create lot.");
    }

    void checkDeleteLot() {
        // navigate to lot-date management page
        navigateToLotDateManagementPage();

        if (!commonAction.getListElement(loc_icnDelete).isEmpty()) {
            if (permissions.getProduct().getLotDate().isDeleteLot()) {
                // open confirm delete lot-date popup
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_icnDelete, 0, loc_dlgConfirmDeleteLot),
                        "Can not open confirm delete lot-date popup.");

                // confirm delete lot-date
                if (!commonAction.getListElement(loc_dlgConfirmDeleteLot).isEmpty()) {
                    commonAction.closePopup(loc_dlgConfirmDeleteLot_btnYes);

                    // confirm delete successfully
                    assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                            "Can not delete lot-date.");
                }
            } else {
                // if staff don’t have permission “Delete lot”
                // => show restricted popup
                // when click [Delete] icon in lot-date management page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnDelete),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Lot-date >> Delete lot.");
    }
}
