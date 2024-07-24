package mobile.seller.android.products.child_screen.inventory.add_imei;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class AddIMEIElement {
    By loc_icnRemoveIMEI = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivDeleteIcon".formatted(goSELLERBundleId)));
    By loc_txtIMEI = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(goSELLERBundleId)));
    By loc_btnAdd = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(goSELLERBundleId)));
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
}
