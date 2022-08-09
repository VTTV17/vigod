package pages.dashboard.settings.staff_management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.dashboard.home.HomePage;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.links.Links.SETTING_PAGE_TITLE;

public class StaffPage extends StaffVerify {
    Logger logger = LogManager.getLogger(StaffPage.class);
    public static String staffMail;
    static boolean isNull = false;

    public StaffPage(WebDriver driver) {
        super(driver);
    }

    public StaffPage setFileName(String fileName) {
        StaffVerify.fileName = fileName;
        return this;
    }

    public StaffPage setStaffSheetID(int sheetID) {
        StaffVerify.staffSheetID = sheetID;
        return this;
    }

    public StaffPage setDomainSheetID(int sheetID) {
        StaffVerify.domainSheetID = sheetID;
        return this;
    }

    public StaffPage setLanguage(String language) {
        StaffVerify.language = language;
        return this;
    }

    public StaffPage setEnv(String env) {
        StaffVerify.env = env;
        return this;
    }

    public StaffPage navigate() throws InterruptedException {
        new HomePage(driver).selectLanguage(language).navigateToSettingsPage();
        logger.info("Access to Setting page");
        wait.until(ExpectedConditions.titleIs(SETTING_PAGE_TITLE));
        logger.info("Title of Setting page is %s".formatted(driver.getTitle()));
        wait.until(ExpectedConditions.elementToBeClickable(STAFF_MANAGEMENT_MENU)).click();
        logger.info("Switch to Staff Management tab");
        staffMail = wait.until(ExpectedConditions.visibilityOf(STAFF_MAIL_VALUE)).getText();
        return this;
    }

    public StaffPage waitLoginPage() throws InterruptedException {
        sleep(5000);
        return this;
    }

    public StaffPage clickOnTheAddStaffBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_STAFF_BTN)).click();
        logger.info("Click on Add Staff button to open the Add staff popup");
        return this;
    }

    public StaffPage clickOnTheEditIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(EDIT_ICON)).click();
        logger.info("Click on the Edit icon to open the Edit staff popup");
        return this;
    }

    public StaffPage clickOnTheDeleteIcon() throws IOException {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(DELETE_ICON)).click();
        } catch (TimeoutException ex) {
            new Screenshot().takeScreenshot(driver);
            logger.error("Store no have staff");
            isNull = true;
        }
        return this;
    }

    public StaffPage clickOnTheOKBtn() throws IOException {
        if (!isNull) {
            new Screenshot().takeScreenshot(driver);
            wait.until(ExpectedConditions.elementToBeClickable(OK_BTN)).click();
            logger.info("Staff has been deleted");
        } else {
            logger.info("Delete staff - SKIPPED - No staff");
        }
        return this;
    }

    public StaffPage inputStaffName(String staffName) {
        wait.until(ExpectedConditions.elementToBeClickable(STAFF_NAME)).clear();
        STAFF_NAME.sendKeys(staffName);
        logger.info("Input the staff name: %s".formatted(staffName));
        return this;
    }

    public StaffPage inputStaffMail(String staffMail) {
        wait.until(ExpectedConditions.elementToBeClickable(STAFF_MAIL)).sendKeys(staffMail);
        logger.info("Input the staff mail: %s".formatted(staffMail));
        return this;
    }

    public StaffPage selectStaffPermission(List<Integer> roleList) throws IOException {
        for (Integer role : roleList) {
            if ((role < STAFF_PERMISSIONS_LABEL.size())) {
                if ((role == 12) || (role == 13)) {
                    if (!STAFF_PERMISSIONS_CHECKBOX.get(0).isSelected()) {
                        STAFF_PERMISSIONS_LABEL.get(0).click();
                        logger.info("Add %s role to new staff".formatted(getRoleText().get(0)));
                    }
                }
                wait.until(ExpectedConditions.elementToBeClickable(STAFF_PERMISSIONS_LABEL.get(role))).click();
                logger.info("Add %s role to new staff".formatted(getRoleText().get(role)));
            }
        }
        return this;
    }

    public StaffPage deselectedAllStaffPermissions() {
        for (var i = STAFF_PERMISSIONS_CHECKBOX.size() - 1; i >= 0; i--) {
            if (STAFF_PERMISSIONS_CHECKBOX.get(i).isSelected()) {
                STAFF_PERMISSIONS_LABEL.get(i).click();
            }
        }
        logger.info("Deselect all permissions");
        return this;
    }

    public StaffPage deselectAllBranch() {
        for (int i = 0; i < STAFF_BRANCH_CHECKBOX.size(); i++) {
            if (STAFF_BRANCH_CHECKBOX.get(i).isSelected()) {
                STAFF_BRANCH_LABEL.get(i).click();
            }
        }
        logger.info("Deselect all branch");
        return this;
    }

    public StaffPage selectBranch(List<Integer> branchList) {
        for (Integer branch : branchList) {
            wait.until(ExpectedConditions.elementToBeClickable(STAFF_BRANCH_LABEL.get(branch))).click();
            logger.info("Assign %s branch to new staff".formatted(STAFF_BRANCH_LABEL.get(branch).getText()));
        }
        return this;
    }

    public StaffPage clickDoneBtn() throws IOException {
        wait.until(ExpectedConditions.elementToBeClickable(DONE_BTN)).click();
        logger.info("Click on the Done button to complete create new staff");
        new Screenshot().takeScreenshot(driver);
        return this;
    }
}
