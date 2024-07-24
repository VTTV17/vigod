package mobile.seller.android.products.child_screen.filter;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.openqa.selenium.By.id;
import static utilities.commons.UICommonAndroid.androidUIAutomatorListString;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class FilterElement {
    By loc_btnReset = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnReset".formatted(goSELLERBundleId)));
    By loc_btnFilterByStatus(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex));
    }
    By loc_btnFilterByChannel(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex + 4));
    }
    By loc_btnFilterByPlatform(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorListString.formatted("%s:id/tag_container".formatted(goSELLERBundleId), actionsIndex + 7));
    }
    By loc_btnSeeAllBranches = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnSeeAllBranches".formatted(goSELLERBundleId)));
    By loc_btnSeeAllCollections = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnSeeAllCollections".formatted(goSELLERBundleId)));
    By loc_btnApply = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnApply".formatted(goSELLERBundleId)));
}
