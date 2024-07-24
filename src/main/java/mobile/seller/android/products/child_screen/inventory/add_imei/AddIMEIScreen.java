package mobile.seller.android.products.child_screen.inventory.add_imei;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;

import java.util.stream.IntStream;

public class AddIMEIScreen extends AddIMEIElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();
    public AddIMEIScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonMobile = new UICommonAndroid(driver);
    }

    public void addIMEI(int quantity, String branchName, String variation) {
        // Remove old IMEI
        while (commonMobile.isShown(loc_icnRemoveIMEI)) {
            commonMobile.click(loc_icnRemoveIMEI);
        }

        // Add imei value for variation
        IntStream.range(0, quantity).forEach(index -> {
            // Input imei value
            String imei = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
            commonMobile.sendKeys(loc_txtIMEI, imei);

            // Add
            commonMobile.click(loc_btnAdd);

            // Log
            logger.info("Add imei into branch '{}', value: {}", branchName, imei);
        });

        // Save changes
        commonMobile.click(loc_btnSave);
    }
}
