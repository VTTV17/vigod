package mobile.seller.android.products.child_screen.filter;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdInstanceString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class FilterElement {
    By loc_btnReset = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/btnReset".formatted(goSELLERBundleId)));
    By loc_btnFilterByStatus(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex));
    }
    By loc_btnFilterByChannel(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex + 4));
    }
    By loc_btnFilterByPlatform(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex + 7));
    }
    By loc_btnSeeAllBranches = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/btnSeeAllBranches".formatted(goSELLERBundleId)));
    By loc_btnSeeAllCollections = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/btnSeeAllCollections".formatted(goSELLERBundleId)));
    By loc_btnApply = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/btnApply".formatted(goSELLERBundleId)));
}
