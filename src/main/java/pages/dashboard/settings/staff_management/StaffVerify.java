package pages.dashboard.settings.staff_management;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.jsonFileUtility;
import utilities.role_matrix.RoleMatrix;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class StaffVerify extends StaffElement {
    WebDriverWait wait;
    AssertCustomize assertCustomize;
    static int countFail = 0;
    static String fileName;
    static int staffSheetID;
    static int domainSheetID;
    static String language;
    static String env;
    Logger logger = LogManager.getLogger(StaffVerify.class);
    RoleMatrix matrix = new RoleMatrix();

    public StaffVerify(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertCustomize = new AssertCustomize(driver);
    }

    public String getDomainURL() throws IOException {
        return matrix.getDomain(fileName, domainSheetID, env).get(0);
    }

    public String getDomainTitle() throws IOException {
        return matrix.getDomain(fileName, domainSheetID, env).get(1);
    }

    public Map<Integer, String> getPagePath() throws IOException {
        return matrix.pagePath(fileName, staffSheetID);
    }

    public Map<Integer, List<Integer>> getPermissions() throws IOException {
        return matrix.staffPermissions(fileName, staffSheetID);
    }

    public Map<Integer, String> getPageTitle() throws IOException {
        if (language.equals("VIE")) {
            return matrix.pageTitleVI(fileName, staffSheetID);
        } else {
            return matrix.pageTitleEN(fileName, staffSheetID);
        }
    }

    public Map<Integer, String> getRoleText() throws IOException {
        if (language.equals("VIE")) {
            return matrix.permissionTextVI(fileName, staffSheetID);
        } else {
            return matrix.permissionTextEN(fileName, staffSheetID);
        }
    }

    //JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");

    public JsonNode getText() {
        return jsonFileUtility.readJsonFile("%s.json".formatted(language)).findValue("dashboard");
    }

    public StaffVerify checkHeader() throws IOException {
        String headerJson = getText().findValue("text").findValue("staff_management").findValue("header").asText();
        String headerGetText = wait.until(ExpectedConditions.visibilityOf(STAFF_MANAGEMENT_HEADER)).getText();
        countFail = assertCustomize.assertEquals(countFail, headerGetText, headerJson, "[UI] Header text does not match");
        return this;
    }

    public StaffVerify checkStaffNameColumn() throws IOException {
        String staffNameColumnJson = getText().findValue("text").findValue("staff_management").findValue("staff_name_column").asText();
        String staffNameColumnGetText = wait.until(ExpectedConditions.visibilityOf(STAFF_NAME_COLUMN)).getText();
        countFail = assertCustomize.assertEquals(countFail, staffNameColumnGetText, staffNameColumnJson, "[UI] Staff Name column text does not match");
        return this;
    }

    public StaffVerify checkStaffPermissionsColumn() throws IOException {
        String staffPermissionsColumnJson = getText().findValue("text").findValue("staff_management").findValue("staff_permissions_column").asText();
        String staffPermissionsColumnGetText = wait.until(ExpectedConditions.visibilityOf(STAFF_PERMISSIONS_COLUMN)).getText();
        countFail = assertCustomize.assertEquals(countFail, staffPermissionsColumnGetText, staffPermissionsColumnJson, "[UI] Staff Permissions column text does not match");
        return this;
    }

    public StaffVerify checkStaffStatusColumn() throws IOException {
        String staffStatusColumnJson = getText().findValue("text").findValue("staff_management").findValue("staff_status_column").asText();
        String staffStatusColumnGetText = wait.until(ExpectedConditions.visibilityOf(STAFF_STATUS_COLUMN)).getText();
        countFail = assertCustomize.assertEquals(countFail, staffStatusColumnGetText, staffStatusColumnJson, "[UI] Staff Status column text does not match");
        return this;
    }

    public StaffVerify checkActionsColumn() throws IOException {
        String actionsColumnJson = getText().findValue("text").findValue("staff_management").findValue("actions_column").asText();
        String actionsColumnGetText = wait.until(ExpectedConditions.visibilityOf(ACTIONS_COLUMN)).getText();
        countFail = assertCustomize.assertEquals(countFail, actionsColumnGetText, actionsColumnJson, "[UI] Actions column text does not match");
        return this;
    }

    public StaffVerify checkFooter() throws IOException {
        String footerJson = getText().findValue("text").findValue("staff_management").findValue("footer").asText();
        String footerGetText = wait.until(ExpectedConditions.visibilityOf(STAFF_MANAGEMENT_FOOTER)).getText();
        countFail = assertCustomize.assertEquals(countFail, footerGetText, footerJson, "[UI] Footer text does not match");
        return this;
    }

    public void checkNoPermission(int pageId) throws InterruptedException, IOException {
        driver.get(getDomainURL() + getPagePath().get(pageId));
        logger.info("Access to %s page".formatted(getDomainTitle() + getPageTitle().get(pageId)));
        sleep(1000);
        countFail = assertCustomize.assertEquals(countFail, driver.getCurrentUrl(), getDomainURL() + "/404", "[URL] 404 page is not displayed.");
        logger.info("Verify that 404 page should be shown instead of %s".formatted(getDomainTitle() + getPageTitle().get(pageId)));
    }

    public void checkPermission(int pageId) throws InterruptedException, IOException {
        String path = getPagePath().get(pageId);
        String title = getDomainTitle() + getPageTitle().get(pageId);
        driver.get(getDomainURL() + path);
        logger.info("Access to %s page".formatted(title));
        sleep(1000);
        countFail = assertCustomize.assertEquals(countFail, driver.getCurrentUrl().replace("/intro", ""), getDomainURL() + path.replace("/intro", ""), "[URL] %s page is not displayed.".formatted(title));
        logger.info(("Verify that current URL is: %s").formatted(getDomainURL() + path));
        countFail = assertCustomize.assertEquals(countFail, driver.getTitle(), title, "[Title] %s title does not match.".formatted(title));
        logger.info("Verify that page should be %s".formatted(title));
    }

    public List<Integer> getRoleList(List<Integer> roleList) {
        List<Integer> list = new ArrayList<>();
        for (int role : roleList) {
            if (role < 15) {
                list.add(role);
            }
        }
        return list;
    }

    public List<Integer> mixRoleList(List<Integer> roleList) throws IOException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < getPermissions().get(0).size(); i++) {
            list.add(0);
        }
        for (int role : roleList) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, list.get(i) + getPermissions().get(role).get(i));
            }
        }
        return list;
    }

    public StaffVerify verifyPermissionOfStaff(List<Integer> roleList) throws InterruptedException, IOException {
        roleList = getRoleList(roleList);
        List<Integer> list = mixRoleList(roleList);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 0) {
                checkNoPermission(i);
            } else {
                checkPermission(i);
            }
        }
        return this;
    }

    public StaffVerify verifyCreateStaffSuccessfully() throws IOException {
        countFail = assertCustomize.assertEquals(countFail, wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE)).getText(), "Created successfully!", "[Create Staff] Can not create staff");
        return this;
    }

    public StaffVerify verifyUpdateStaffSuccessfully() throws IOException {
        countFail = assertCustomize.assertEquals(countFail, wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE)).getText(), "Updated successfully!", "[Update Staff] Can not update staff");
        return this;
    }

    public StaffVerify verifyDeleteStaffSuccessfully() throws IOException {
        countFail = assertCustomize.assertEquals(countFail, wait.until(ExpectedConditions.visibilityOf(TOAST_MESSAGE)).getText(), "Deleted successfully!", "[Delete Staff] Can not delete staff");
        return this;
    }

    public StaffVerify completeVerify() {
        logger.info("countFail = %s".formatted(countFail));
        if (countFail > 0) {
            Assert.fail("[Failed] Fail %d cases".formatted(countFail));
        }
        countFail = 0;
        return this;
    }

    public void logout() throws InterruptedException, IOException {
        if (driver.getCurrentUrl().contains("404")) {
            driver.get(getDomainURL());
            sleep(3000);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", LOGOUT_BTN);
        logger.info("Logout");
    }
}
