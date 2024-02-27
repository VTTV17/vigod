package web.Dashboard.products.lot_date.crud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.lot_date.management.LotDateManagementPage;

import java.time.Instant;

import static utilities.links.Links.DOMAIN;

public class LotDatePage extends LotDateElement {
    WebDriver driver;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LotDatePage.class);
    public LotDatePage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    LotDateManagementPage lotDateManagementPage;

    void inputLotName(String name) {
        lotDateManagementPage = new LotDateManagementPage(driver);
        commonAction.sendKeys(lotDateManagementPage.loc_dlgAddLotDate_txtLotName, name);
        logger.info("Input lot name: %s.".formatted(name));
    }

    void inputLotCode(String code) {
        commonAction.sendKeys(lotDateManagementPage.loc_dlgAddLotDate_txtLotCode, code);
        logger.info("Input lot code: %s.".formatted(code));
    }

    void selectManufactureDate(String manufactureDate) {
        commonAction.sendKeys(lotDateManagementPage.loc_dlgAddLotDate_dtpManufactureDate, manufactureDate);
        logger.info("Select manufacture date: %s.".formatted(manufactureDate));
    }

    void selectExpiryDate(String expiryDate) {
        commonAction.sendKeys(lotDateManagementPage.loc_dlgAddLotDate_dtpExpiryDate, expiryDate);
        logger.info("Select expiry date: %s.".formatted(expiryDate));
    }

    void completeAddLotDate() {
        commonAction.closePopup(lotDateManagementPage.loc_dlgAddLotDate_btnSave);
        logger.info("Complete add new lot-date.");

        assertCustomize.assertFalse(commonAction.getListElement(lotDateManagementPage.loc_dlgToastSuccess).isEmpty(),
                "Can not add new lot-date.");
    }

    public void addNewLotDate() {
        // input lot name
        Long epoch = Instant.now().toEpochMilli();
        String name = "Lot date %s".formatted(epoch);
        inputLotName(name);

        // input lot code
        String code = epoch.toString();
        inputLotCode(code);

        // select manufacture date
        DataGenerator dataGenerator = new DataGenerator();
        String manufactureDate = dataGenerator.generateDateTime("dd/MM/YYYY",-1);
        selectManufactureDate(manufactureDate);

        // select expiry date
        String expiryDate = dataGenerator.generateDateTime("dd/MM/YYYY", 365);
        selectExpiryDate(expiryDate);

        // complete add lot date.
        completeAddLotDate();
    }
    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-24808
    AllPermissions permissions;
    AssertCustomize assertCustomize;
    CheckPermission checkPermission;

    public void checkViewLotDetail(AllPermissions permissions, int lotDateId) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // check permission
        if (permissions.getProduct().getLotDate().isViewLotDetail()) {
            // check can access to lot-date detail page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s%s".formatted(DOMAIN, "/lot-date/edit/%s".formatted(lotDateId)),
                            String.valueOf(lotDateId)),
                    "Can not access to lot date detail page.");

            // check edit lot-date
            checkEditLot();
        } else {
            // if staff don’t have permission “View lot detail”
            // => show restricted page
            //  when access lot detail page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s%s".formatted(DOMAIN, "/lot-date/edit/%s".formatted(lotDateId))),
                    "Restricted page is not shown.");
        }

        logger.info("Check permission: Product >> Lot-date >> View lot detail.");
    }

    void checkEditLot() {
        // check permission
        if (permissions.getProduct().getLotDate().isEditLot()) {
            // check can edit lot-date
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave,
                            loc_dlgToastSuccess),
                    "Can not edit lot-date.");
        } else {
            // if staff don’t have permission “Edit lot”
            // => show restricted popup
            // when click [Save] lot detail
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave),
                    "Restricted popup is not shown.");
        }

        logger.info("Check permission: Product >> Lot-date >> Edit list.");
    }
}
