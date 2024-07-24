package mobile.seller.android.products.child_screen.product_description;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class ProductDescriptionElement {
    By loc_txtContent = By.xpath("//android.widget.EditText");
    By loc_btnSave = AppiumBy.androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivActionBarIconRight".formatted(goSELLERBundleId)));
}
