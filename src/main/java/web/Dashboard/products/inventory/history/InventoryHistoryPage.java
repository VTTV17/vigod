package web.Dashboard.products.inventory.history;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.FileUtils;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.inventory.InventoryPage;

import java.util.ArrayList;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class InventoryHistoryPage extends InventoryHistoryElement {
    WebDriver driver;
    UICommonAction commons;

    final static Logger logger = LogManager.getLogger(InventoryHistoryPage.class);

    public InventoryHistoryPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
    }


    public InventoryHistoryPage inputSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearchRecord, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        commons.sleepInMiliSecond(1000); //It takes some time for the search operation to commence after the search term is input
        new HomePage(driver).waitTillSpinnerDisappear1();
        return this;
    }

    public List<WebElement> getRecordElements(By byLocator) {
        return commons.getElements(byLocator);
    }

    public List<String> getSpecificRecord(int index) {
        //Wait until records are present
        for (int i = 0; i < 10; i++) {
            if (!getRecordElements(loc_tmpRecords).isEmpty()) {
                break;
            }
            commons.sleepInMiliSecond(500);
        }

        /*
         * Loop through the columns of the specific record
         * and store data of the column into an array.
         * Retry the process when StaleElementReferenceException occurs
         */
        try {
            List<String> rowData = new ArrayList<>();
            List<WebElement> columns = getRecordElements(loc_tmpRecords).get(index).findElements(By.xpath("./td"));
            for (int i = 0; i < columns.size(); i++) {
                if (i == 0) continue; //Skip the first column as it only contains pictures
                rowData.add(columns.get(i).getText());
            }
            return rowData;
        } catch (StaleElementReferenceException ex) {
            logger.debug("StaleElementReferenceException caught in getSpecificRecord(). Retrying...");
            List<String> rowData = new ArrayList<>();
            List<WebElement> columns = getRecordElements(loc_tmpRecords).get(index).findElements(By.xpath("./td"));
            for (int i = 0; i < columns.size(); i++) {
                if (i == 0) continue; //Skip the first column as it only contains pictures
                rowData.add(columns.get(i).getText());
            }
            return rowData;
        }
    }

    public List<List<String>> getRecords() {
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < getRecordElements(loc_tmpRecords).size(); i++) {
            table.add(getSpecificRecord(i));
        }
        return table;
    }

    void exportInventoryHistory() {
        commons.click(loc_btnExport);
        commons.click(loc_ddvExportActions, 0);
    }

    void navigateToExportInventoryHistoryPage(InventoryPage inventoryPage) {
        inventoryPage.navigateToInventoryHistoryPage();
        commons.click(loc_btnExport);
        commons.click(loc_ddvExportActions, 1);
    }

    // check permission
    // https://mediastep.atlassian.net/browse/BH-13841
    public void checkExportInventoryHistory(AllPermissions permissions, AssertCustomize assertCustomize, CheckPermission checkPermission, InventoryPage inventoryPage) {
        if (!permissions.getProduct().getInventory().isExportInventoryHistory()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnExport), "Restricted popup is not shown.");
        } else {
            // export inventory history
            exportInventoryHistory();

            // navigate to inventory history page
            navigateToExportInventoryHistoryPage(inventoryPage);

            // check download exported product
            checkDownloadExportedProduct(permissions, assertCustomize, checkPermission);
        }
        logger.info("Check permission: Product >> Inventory >> Export inventory history.");
    }

    void checkDownloadExportedProduct(AllPermissions permissions, AssertCustomize assertCustomize, CheckPermission checkPermission) {
        if (!permissions.getProduct().getInventory().isDownloadExportedProduct()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_icnDownloadExportedFile, 0), "Restricted popup is not shown.");
        } else {
            // init file utils
            FileUtils fileUtils = new FileUtils();

            // delete old wholesale price exported file
            fileUtils.deleteFileInDownloadFolder("EXPORT_INVENTORY_HISTORY");

            // download export history
            commons.clickJS(loc_icnDownloadExportedFile, 0);
            commons.sleepInMiliSecond(1000, "Waiting for download.");
            assertCustomize.assertTrue(fileUtils.isDownloadSuccessful("EXPORT_INVENTORY_HISTORY"), "No exported inventory history file is downloaded.");
        }
        logger.info("Check permission: Product >> Inventory >> Download exported product.");
    }
}
