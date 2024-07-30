package mobile.seller.iOS.products.child_screen.select_image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class SelectImagePopup extends SelectImageElement{
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;
    Logger logger = LogManager.getLogger();
    public SelectImagePopup(WebDriver driver) {
        // Get driver
        this.driver = driver;

        // Init assert class
        assertCustomize = new AssertCustomize(driver);

        // Init commons class
        commonIOS = new UICommonIOS(driver);
    }

    public void selectImages() {
        // Select images
       commonIOS.click(loc_lstImages, 0);

        // Save changes
        commonIOS.click(loc_btnSave);
    }
}
