package mobile.seller.iOS.products.child_screen.product_description;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonIOS;

import java.time.Duration;

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
        commonIOS.click(loc_rtfDescription);
        commonIOS.sendKeys(loc_rtfDescription, description);

        // Save changes
        commonIOS.click(loc_btnSave);
    }

    public void updateDescription(String oldDescription, String newDescription) {
        // Input product description
        commonIOS.click(loc_rtfDescription);
        commonIOS.sendKeys(loc_rtfDescription, newDescription);

        // Save changes
        commonIOS.click(loc_btnSave);
    }
}
