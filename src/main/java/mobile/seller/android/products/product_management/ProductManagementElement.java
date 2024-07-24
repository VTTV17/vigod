package mobile.seller.android.products.product_management;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorListString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class ProductManagementElement {
    By loc_txtSearchBox = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductSearch".formatted(goSELLERBundleId)));
    By loc_btnSort = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivSortType".formatted(goSELLERBundleId)));
    By loc_lstSortOptions(int index) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/tvStatus".formatted(goSELLERBundleId), index));
    }
    By loc_btnFilter = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnFilterProduct".formatted(goSELLERBundleId)));
    String str_lblProductName = "//android.widget.TextView[@* = '%s']".formatted(goSELLERBundleId);
    By loc_lblProductName = By.xpath("//*[@* ='%s:id/tvProductName']".formatted(goSELLERBundleId));

}
