package mobile.seller.android.products.child_screen.inventory;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdInstanceString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InventoryElement {
    By loc_txtBranchStock(int branchIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/edtStock".formatted(goSELLERBundleId), branchIndex));
    }
    By loc_btnSave = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tabLayoutUpdateStockType".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtStock".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvUpdateStock".formatted(goSELLERBundleId)));
}
