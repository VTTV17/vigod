package mobile.seller.android.products.child_screen.crud_variations;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;
import utilities.data.DataGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CRUDVariationScreen extends CRUDVariationElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonAndroid;
    Logger logger = LogManager.getLogger();
    @Getter
    private static Map<String, List<String>> variationMap;

    public CRUDVariationScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAndroid = new UICommonAndroid(driver);
    }

    public CRUDVariationScreen removeOldVariation() {
        // Remove old variation
        while (commonAndroid.isShown(loc_btnRemoveVariationGroup)) {
            commonAndroid.click(loc_btnRemoveVariationGroup);
        }

        return this;
    }

    public void addVariation(String defaultLanguage) {
        // Init variation map
        variationMap = new DataGenerator().randomVariationMap(defaultLanguage);

        // Remove old variation
        removeOldVariation();

        // Add variation
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(groupIndex -> {
            // Get variation group
            String variationGroup = variationMap.keySet().stream().toList().get(groupIndex);

            // Get variation value
            List<String> variationValue = variationMap.get(variationGroup);

            // Add new variation group
            commonAndroid.click(loc_btnAddVariation);

            // Input variation group
            commonAndroid.sendKeys(groupIndex == 0 ? loc_txtVariationName1 : loc_txtVariationName2, variationGroup);
            logger.info("Add variation group {}, group: {}", groupIndex + 1, variationGroup);

            // Input variation value
            for (String value : variationValue) {
                commonAndroid.sendKeys(groupIndex == 0 ? loc_txtVariationValue1 : loc_txtVariationValue2, value);
                commonAndroid.click(groupIndex == 0 ? loc_btnAddVariationValue1 : loc_btnAddVariationValue2);
                logger.info("Add variation value for group {}, value: {}", groupIndex + 1, value);
            }
        });

        // Save changes
        saveChanges();

        // Log
        logger.info("Complete add variations");
    }

    public void saveChanges() {
        // Save changes
        commonAndroid.click(loc_btnSave);
    }
}
