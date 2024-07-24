package mobile.seller.android.products.child_screen.inventory;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorListString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InventoryElement {
    By loc_txtBranchStock(int branchIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/edtStock".formatted(goSELLERBundleId), branchIndex));
    }
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tabLayoutUpdateStockType".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtStock".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvUpdateStock".formatted(goSELLERBundleId)));
}
