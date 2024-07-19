package mobile.seller.android.products.child_screen.inventory;

import mobile.seller.android.products.child_screen.inventory.add_imei.AddIMEIScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;

import java.util.stream.IntStream;

public class InventoryScreen extends InventoryElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();

    public InventoryScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonMobile = new UICommonAndroid(driver);
    }

    public void addStock(boolean manageByIMEI, BranchInfo branchInfo, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, branchInfo.getBranchName().size()).forEach(branchIndex -> {
            // Get current branch
            String branchName = branchInfo.getBranchName().get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Add branch stock
            if (manageByIMEI) {
                // Navigate to add imei screen
                commonMobile.click(loc_txtBranchStock, branchIndex);

                // Add imei
                new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
            } else {
                // Input branch stock
                commonMobile.sendKeys(loc_txtBranchStock, branchIndex, String.valueOf(branchQuantity));
            }

            // Log
            logger.info("Add stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        commonMobile.click(rsId_btnSave);
    }

    public void updateStock(boolean manageByIMEI, BranchInfo branchInfo, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, branchInfo.getBranchName().size()).forEach(branchIndex -> {
            // Get current branch
            String branchName = branchInfo.getBranchName().get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Get current quantity
            String value = commonMobile.getText(loc_txtBranchStock, branchIndex).replaceAll("\\D+", "");
            int currentBranchQuantity = value.isEmpty() ? 0 : Integer.parseInt(value);

            // Only update stock when stock is changed
            if (branchQuantity != currentBranchQuantity) {
                // Add branch stock
                if (manageByIMEI) {
                    // Navigate to add imei screen
                    commonMobile.click(loc_txtBranchStock, branchIndex);

                    // Add imei
                    new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                } else {
                    // Click into branch stock textbox
                    commonMobile.click(loc_txtBranchStock, branchIndex);

                    // If update stock popup shows, update stock on popup
                    if (commonMobile.isShown(rsId_dlgUpdateStock)) {
                        // Switch to change tab
                        commonMobile.click(loc_dlgUpdateStock_tabChange);

                        // Input quantity
                        commonMobile.sendKeys(rsId_dlgUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                        // Save changes
                        commonMobile.click(rsId_dlgUpdateStock_btnOK);
                    } else {
                        // Input into branch stock textbox
                        commonMobile.sendKeys(loc_txtBranchStock, branchIndex, String.valueOf(branchQuantity));
                    }
                }
            }

            // Log
            logger.info("Update stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        commonMobile.click(rsId_btnSave);
    }
}
