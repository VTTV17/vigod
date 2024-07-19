package mobile.seller.iOS.products.child_screen.inventory.add_imei;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

import java.util.stream.IntStream;

public class AddIMEIScreen extends AddIMEIElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();

    public AddIMEIScreen(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void addIMEI(int quantity, String branchName, String variation) {
        // Remove old IMEI
        int numberOfIMEIs = commonIOS.getListElements(loc_icnRemoveIMEI).size();
        IntStream.range(0, numberOfIMEIs)
                .forEach(index -> commonIOS.tapOnRightTopCorner(loc_icnRemoveIMEI));

        // Add imei value for variation
        IntStream.range(0, quantity).forEach(index -> {
            // Input imei value
            String imei = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
            commonIOS.sendKeys(loc_txtIMEI, imei);

            // Add
            commonIOS.tap(loc_btnAdd);

            // Log
            logger.info("Add imei into branch '{}', value: {}", branchName, imei);
        });

        // Save changes
        commonIOS.tap(loc_btnSave);
    }
}
