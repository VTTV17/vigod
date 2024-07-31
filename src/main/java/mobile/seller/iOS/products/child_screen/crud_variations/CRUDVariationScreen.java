package mobile.seller.iOS.products.child_screen.crud_variations;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;
import utilities.data.DataGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CRUDVariationScreen extends CRUDVariationElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    @Getter
    @Setter
    private static Map<String, List<String>> variationMap;

    public CRUDVariationScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonIOS = new UICommonIOS(driver);
    }

    public CRUDVariationScreen removeOldVariation() {
        // Remove old variation
        if (!commonIOS.getListElement(loc_icnRemoveVariationGroup).isEmpty())
            IntStream.iterate(commonIOS.getListElement(loc_icnRemoveVariationGroup).size() - 1, removeIndex -> removeIndex >= 0, removeIndex -> removeIndex - 1)
                    .forEach(removeIndex -> commonIOS.click(loc_icnRemoveVariationGroup, removeIndex));

        return this;
    }

    public void addVariation(String defaultLanguage) {
        // Init variation map
        variationMap = new DataGenerator().randomVariationMap(defaultLanguage);

        // Remove old variation
        removeOldVariation();

        // Add variation
        IntStream.range(0, variationMap.keySet().size()).forEachOrdered(groupIndex -> {
            // Get variation name
            String variationName = variationMap.keySet().stream().toList().get(groupIndex);

            // Get variation value
            List<String> variationValue = variationMap.get(variationName);

            // Add new variation group
            commonIOS.click(loc_btnAddVariation);

            // Input variation group
            commonIOS.sendKeys(loc_txtVariationName, groupIndex, variationName);
            logger.info("Add variation group {}, name: {}", groupIndex + 1, variationName);

            // Input variation value
            for (String value : variationValue) {
                commonIOS.sendKeys(loc_txtVariationValue, groupIndex, value);
                commonIOS.click(loc_icnAddVariationValue, groupIndex);
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
        commonIOS.click(loc_btnSave);
    }
}
