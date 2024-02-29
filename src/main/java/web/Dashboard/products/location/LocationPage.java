package web.Dashboard.products.location;

import api.Seller.products.location.APILocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.time.Instant;

import static utilities.links.Links.DOMAIN;

public class LocationPage extends LocationElement {
    WebDriver driver;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(LocationPage.class);

    public LocationPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    void navigateToLocationPage() {
        if (!driver.getCurrentUrl().contains("/product/location/list")) {
            driver.get("%s/product/location/list".formatted(DOMAIN));
            logger.info("Navigate to location page by URL.");
        }
    }

    void selectBranch(String branchName) {
        // select branch
        if (commonAction.getText(loc_dlgAddLocation_ddvSelectedBranch).equals(branchName)) {
            // open branch dropdown
            commonAction.click(loc_dlgAddLocation_ddvSelectedBranch);

            // select branch
            commonAction.clickJS(By.xpath(str_dlgAddLocation_ddvBranches.formatted(branchName)));
        }

        // log
        logger.info("Select branch: %s".formatted(branchName));
    }

    void changeBranch(String branchName) {
        // select branch
        if (commonAction.getText(loc_dlgEditLocation_ddvSelectedBranch).equals(branchName)) {
            // open branch dropdown
            commonAction.click(loc_dlgEditLocation_ddvSelectedBranch);

            // select branch
            commonAction.clickJS(By.xpath(str_dlgEditLocation_ddvBranches.formatted(branchName)));
        }

        // log
        logger.info("Select branch: %s".formatted(branchName));
    }

    void inputLocationName(String name) {
        // input location name
        commonAction.sendKeys(loc_dlgAddLocation_txtName, name);
        logger.info("Input location name: %s.".formatted(name));
    }

    void inputLocationCode(String code) {
        // input location code
        commonAction.sendKeys(loc_dlgAddLocation_txtCode, code);
        logger.info("Input location code: %s.".formatted(code));
    }

    void addLocation() {
        // add new location
        commonAction.click(loc_dlgAddLocation_icnAdd);
        logger.info("Add new location.");
    }

    void completeAddNewLocation() {
        // save changes
        commonAction.closePopup(loc_dlgAddLocation_btnSave);
        logger.info("Complete add new location.");

        // check location is added successful or not.
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                "Can not add new location.");
    }

    public void addNewLocation() {
        if (!commonAction.getListElement(loc_dlgAddLocation).isEmpty()) {
            Long epoch = Instant.now().toEpochMilli();
            String name = "Location name %s".formatted(epoch);
            String code = epoch.toString();
            inputLocationName(name);
            inputLocationCode(code);
            addLocation();
            completeAddNewLocation();
        } else logger.info("No add location popup shows.");
    }

    void openListActions() {
        commonAction.removeAttribute(loc_imgActions, 0, "hidden");
        commonAction.clickJS(loc_imgActions, 0);

    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-24809
    AllPermissions permissions;
    AssertCustomize assertCustomize;
    CheckPermission checkPermission;
    APILocation location;

    public void checkLocationPermission(AllPermissions permissions, LoginInformation loginInformation) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // init product lot-date API
        location = new APILocation(loginInformation);

        // check view location list
        checkViewLocationList();

        // check view location detail
        checkViewLocationDetail();

        // check add new location
        checkAddLocation();

        // check delete location
        checkDeleteLocation();

    }

    void checkViewLocationList() {
        navigateToLocationPage();
        int statusCode = location.getAllLocationResponse().statusCode();

        if (permissions.getProduct().getLocation().isViewLocationList()) {
            // check list lot-date
            assertCustomize.assertTrue(statusCode == 200, "No location shows.");
        } else {
            // if staff don’t have permission “View location list”
            // => don’t see any collection when access Product >> location page
            assertCustomize.assertTrue(statusCode == 403,
                    "All locations still showing when no 'View location list' permission.");
        }
        logger.info("Check permission: Product >> Location >> View location list.");
    }

    void checkAddLocation() {
        // check permission
        if (permissions.getProduct().getLocation().isAddLocation()) {
            // add new location
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnAddLocation, loc_dlgAddLocation),
                    "Can not open add new location popup.");

            addNewLocation();
            logger.info("Check create new location.");

            // add a new child location
            if (!commonAction.getListElement(loc_imgActions).isEmpty()) {
                openListActions();

                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 1, loc_dlgAddLocation),
                        "Can not open add new child location popup.");

                addNewLocation();
                logger.info("Check create new child location.");
            }

        } else {
            // if staff don’t have permission “Add location”
            // => show restricted popup
            // when click [Add location] in product >> location page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddLocation),
                    "Restricted popup is not shown.");

            // if staff don’t have permission “Add location”
            // => show restricted popup
            // when click [Add location] from a specific location
            if (!commonAction.getListElement(loc_imgActions).isEmpty()) {
                openListActions();
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 1),
                        "Restricted popup is not shown.");
            }
        }

        logger.info("Check permission: Product >> Location >> Add location.");
    }

    void checkViewLocationDetail() {
        // check permission
        if (permissions.getProduct().getLocation().isViewLocationDetail()) {
            if (!commonAction.getListElement(loc_imgActions).isEmpty()) {
                openListActions();

                // check can view location detail
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 0, loc_dlgEditLocation),
                        "Can not open edit location popup.");

                // check edit location permission
                if (!commonAction.getListElement(loc_dlgEditLocation).isEmpty()) checkEditLocation();
                else logger.info("No edit location popup shows.");
            }
        } else {
            // if staff don’t have permission “View location detail”
            // => show restricted popup
            // when click menu “Edit” a location
            if (!commonAction.getListElement(loc_imgActions).isEmpty()) {
                openListActions();
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_ddlListActions, 0),
                        "Restricted popup is not shown.");
            }
        }

        logger.info("Check permission: Product >> Location >> View location detail.");
    }

    void checkEditLocation() {
        if (permissions.getProduct().getLocation().isEditLocation()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgEditLocation_btnSave, loc_dlgToastSuccess),
                    "Can not edit location.");
        } else {
            // if staff don’t have permission “Edit location”
            // => show restricted popup
            // when click [Save] location detail
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgEditLocation_btnSave),
                    "Restricted popup is not shown.");
        }

        logger.info("Check permission: Product >> Location >> Edit location.");
    }

    void checkDeleteLocation() {
        if (!commonAction.getListElement(loc_imgActions).isEmpty()) {
            openListActions();

            // open confirm delete location popup
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_ddlListActions, 2, loc_dlgConfirmDeleteLocation),
                    "Can not open confirm delete location popup.");
            if (!commonAction.getListElement(loc_dlgConfirmDeleteLocation).isEmpty()) {
                // check permission
                if (permissions.getProduct().getLocation().isDeleteLocation()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmDeleteLocation_btnYes, loc_dlgToastSuccess),
                            "Can not delete location.");
                } else {
                    // if staff don’t have permission “Delete location”
                    // => show restricted popup
                    // when click [Yes] button on popup confirm delete a location
                    assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_dlgConfirmDeleteLocation_btnYes),
                            "Restricted popup is not shown.");
                }
            } else logger.info("No confirm delete location popup shows.");
        }
        logger.info("Check permission: Product >> Location >> Delete location.");
    }
}
