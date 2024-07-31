package mobile.seller.iOS.products.child_screen.inventory;

import mobile.seller.iOS.products.child_screen.inventory.add_imei.AddIMEIScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;

import java.util.stream.IntStream;

public class InventoryScreen extends InventoryElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public InventoryScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonIOS = new UICommonIOS(driver);
    }

    public void addStock(boolean manageByIMEI, BranchInfo branchInfo, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, branchInfo.getBranchName().size()).forEach(branchIndex -> {
            // Get current branch
            String branchName = branchInfo.getBranchName().get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Add branch stock
            if (!(branchQuantity == 0 && commonIOS.getText(loc_txtBranchStock(branchName)).equals("Input quantity"))) {
                if (manageByIMEI) {
                    // Navigate to add imei screen
                    commonIOS.click(loc_txtBranchStock(branchName));

                    // Add imei
                    new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                } else {
                    // Input branch stock
                    commonIOS.sendKeys(loc_txtBranchStock(branchName), String.valueOf(branchQuantity));
                }
            }

            // Log
            logger.info("Add stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        commonIOS.click(loc_btnSave);
    }

    public void updateStock(boolean manageByIMEI, BranchInfo branchInfo, String variation, int... branchStock) {
        // Add stock for each branch
        IntStream.range(0, branchInfo.getBranchName().size()).forEach(branchIndex -> {
            // Get current branch
            String branchName = branchInfo.getBranchName().get(branchIndex);

            // Get branch quantity
            int branchQuantity = (branchIndex >= branchStock.length) ? 0 : branchStock[branchIndex];

            // Get current quantity
            String value = commonIOS.getText(loc_txtBranchStock(branchName)).replaceAll("\\D+", "");
            int currentBranchQuantity = value.isEmpty() ? 0 : Integer.parseInt(value);

            // Only update stock when stock is changed
            if (branchQuantity != currentBranchQuantity) {
                // Add branch stock
                if (manageByIMEI) {
                    // Navigate to add imei screen
                    commonIOS.click(loc_txtBranchStock(branchName));

                    // Add imei
                    new AddIMEIScreen(driver).addIMEI(branchQuantity, branchName, variation);
                } else {
                    // Click into branch stock textbox
                    commonIOS.click(loc_txtBranchStock(branchName));

                    // If update stock popup shows, update stock on popup
                    if (!commonIOS.getListElement(loc_dlgUpdateStock_tabChange).isEmpty()) {
                        // Switch to change tab
                        commonIOS.click(loc_dlgUpdateStock_tabChange);

                        // Input quantity
                        commonIOS.sendKeys(loc_txtUpdateStock_txtQuantity, String.valueOf(branchQuantity));

                        // Save changes
                        commonIOS.click(loc_dlgUpdateStock_btnOK);
                    } else {
                        // Input into branch stock textbox
                        commonIOS.sendKeys(loc_txtBranchStock(branchName), String.valueOf(branchQuantity));
                    }
                }
            }

            // Log
            logger.info("Update stock for branch '{}', quantity: {}", branchName, branchQuantity);
        });

        // Save changes
        commonIOS.click(loc_btnSave);
    }
}
