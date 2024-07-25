package mobile.seller.android.products.child_screen.edit_multiple;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdInstanceString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class EditMultipleElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_ddvSelectedBranch = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvFilterBranches".formatted(goSELLERBundleId)));
    By loc_lstBranches(int branchIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/ivUnChecked".formatted(goSELLERBundleId), branchIndex));
    }
    By loc_lblActions = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivAction".formatted(goSELLERBundleId)));
    By loc_lblUpdatePriceActions = By.xpath("(//*[@* = '%s:id/title'])[1]".formatted(goSELLERBundleId));
    By loc_lblUpdateStockActions = By.xpath("(//*[@* = '%s:id/title'])[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[@* = '%s:id/edtOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[@* = '%s:id/edtNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_dlgUpdatePrice_btnOK = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvOK".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtStock".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvUpdateStock".formatted(goSELLERBundleId)));
}
