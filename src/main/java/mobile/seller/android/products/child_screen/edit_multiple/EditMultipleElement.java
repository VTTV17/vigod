package mobile.seller.android.products.child_screen.edit_multiple;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorListString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class EditMultipleElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_ddvSelectedBranch = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvFilterBranches".formatted(goSELLERBundleId)));
    By loc_lstBranches(int branchIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/ivUnChecked".formatted(goSELLERBundleId), branchIndex));
    }
    By loc_lblActions = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivAction".formatted(goSELLERBundleId)));
    By loc_lblUpdatePriceActions = By.xpath("(//*[@* = '%s:id/title'])[1]".formatted(goSELLERBundleId));
    By loc_lblUpdateStockActions = By.xpath("(//*[@* = '%s:id/title'])[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[@* = '%s:id/edtOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[@* = '%s:id/edtNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_dlgUpdatePrice_btnOK = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvOK".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdateStock_txtQuantity = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtStock".formatted(goSELLERBundleId)));
    By loc_dlgUpdateStock_btnOK = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvUpdateStock".formatted(goSELLERBundleId)));
}
