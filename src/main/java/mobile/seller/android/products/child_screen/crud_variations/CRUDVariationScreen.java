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
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();
    @Getter
    private static Map<String, List<String>> variationMap;

    public CRUDVariationScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonMobile = new UICommonAndroid(driver);
    }

    public CRUDVariationScreen removeOldVariation() {
        // Remove old variation
        if (commonMobile.isShown(rsId_btnRemoveVariationGroup2))
            commonMobile.click(rsId_btnRemoveVariationGroup2);
        if (commonMobile.isShown(rsId_btnRemoveVariationGroup1))
            commonMobile.click(rsId_btnRemoveVariationGroup1);

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
            commonMobile.click(rsId_btnAddVariation);

            // Input variation group
            commonMobile.sendKeys(groupIndex == 0 ? rsId_txtVariationGroup1 : rsId_txtVariationGroup2, variationGroup);
            logger.info("Add variation group {}, group: {}", groupIndex + 1, variationGroup);

            // Input variation value
            for (String value : variationValue) {
                commonMobile.sendKeys(groupIndex == 0 ? rsId_txtVariationValue1 : rsId_txtVariationValue2, value);
                commonMobile.click(groupIndex == 0 ? rsId_btnAddVariationValue1 : rsId_btnAddVariationValue2);
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
        commonMobile.click(rsId_btnSave);
    }
}
