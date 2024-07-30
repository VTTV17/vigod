package mobile.seller.iOS.products.child_screen.product_description;

import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

public class ProductDescriptionScreen extends ProductDescriptionElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonIOS commonIOS;

    public ProductDescriptionScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonIOS = new UICommonIOS(driver);
    }

    public void inputDescription(String description) {
        // Input product description
        commonIOS.sendKeys(loc_txtContent, description);

        // Save changes
        commonIOS.click(loc_btnSave);
    }
}
