package mobile.seller.android.products.child_screen.select_image_popup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;

import java.util.List;
import java.util.stream.IntStream;

public class SelectImagePopup extends SelectImageElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;
    Logger logger = LogManager.getLogger();
    public SelectImagePopup(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonMobile = new UICommonAndroid(driver);
    }

    public void selectImages(List<String> imageFileNames) {
        // Select images
        IntStream.range(0, imageFileNames.size()).forEach(imageIndex -> commonMobile.click(loc_lstImages, imageIndex));

        // Save changes
        commonMobile.click(rsId_btnSave);
    }
}
