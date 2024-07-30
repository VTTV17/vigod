package mobile.seller.iOS.products.child_screen.edit_multiple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;

import java.util.stream.IntStream;

public class EditMultipleScreen extends EditMultipleElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public EditMultipleScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonIOS = new UICommonIOS(driver);
    }

    public void bulkUpdatePrice(long listingPrice, long sellingPrice) {
        // Open list actions
        commonIOS.click(loc_icnActions);

        // Select bulk update price actions
        commonIOS.click(loc_ddvUpdatePriceActions);

        // Input listing price
        commonIOS.sendKeys(loc_dlgUpdatePrice_txtListingPrice, String.valueOf(listingPrice));
        logger.info("Bulk listing price: %,d".formatted(listingPrice));

        // Input selling price
        commonIOS.sendKeys(loc_dlgUpdatePrice_txtSellingPrice, String.valueOf(sellingPrice));
        logger.info("Bulk selling price: %,d".formatted(sellingPrice));

        // Save changes
        commonIOS.click(loc_dlgUpdatePrice_btnOK);
    }

    public void bulkUpdateStock(boolean manageByIMEI, boolean manageByLot, BranchInfo branchInfo, int increaseNum, int... branchStock) {
        // Not supported for product managed by IMEI/Serial number
        if (manageByIMEI) logger.info("Can not bulk update stock with product that is managed by IMEI/Serial number.");
        else if (manageByLot) logger.info("Product is managed by lot, requiring stock updates in the lot-date screen.");
        else {
            // Update stock for each branch
            IntStream.range(0, branchInfo.getBranchName().size()).forEach(branchIndex -> {
                // Get branch name
                String branchName = branchInfo.getBranchName().get(branchIndex);

                // Get branch quantity
                int branchQuantity = ((branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex]) + branchIndex * increaseNum;

                // Open list branches
                commonIOS.click(loc_icnStoreBranch);

                // Switch branch
                commonIOS.click(loc_ddvBranch(branchName));

                // Open list actions
                commonIOS.click(loc_icnActions);

                // Select bulk update stock actions
                commonIOS.click(loc_ddvUpdateStockActions);

                // Switch to change tab
                commonIOS.click(loc_dlgUpdateStock_tabChange);

                // Input quantity
                commonIOS.sendKeys(loc_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                // Save changes
                commonIOS.click(loc_dlgUpdateStock_btnOK);

                // Log
                logger.info("Bulk update stock for branch '{}', quantity: {}", branchName, branchQuantity);
            });
        }

        // Save changes
        commonIOS.click(loc_btnSave);
    }
}
