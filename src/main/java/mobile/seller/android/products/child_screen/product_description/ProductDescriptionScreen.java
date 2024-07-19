package mobile.seller.android.products.child_screen.product_description;

import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAndroid;

public class ProductDescriptionScreen extends ProductDescriptionElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAndroid commonMobile;

    public ProductDescriptionScreen(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonMobile = new UICommonAndroid(driver);
    }

    public void inputDescription(String description) {
        // Input product description
        commonMobile.sendKeysActions(commonMobile.getElement(loc_txtContent), description);

        // Save changes
        commonMobile.click(rsId_btnSave);
    }
}
