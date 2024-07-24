package mobile.seller.android.products.child_screen.select_image_popup;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorListString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class SelectImageElement {
    By loc_lstImages(int imageIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/tvSelectIndex".formatted(goSELLERBundleId), imageIndex));
    }

    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/fragment_choose_photo_dialog_btn_choose".formatted(goSELLERBundleId)));
}
