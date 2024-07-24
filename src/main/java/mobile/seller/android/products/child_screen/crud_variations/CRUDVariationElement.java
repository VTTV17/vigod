package mobile.seller.android.products.child_screen.crud_variations;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class CRUDVariationElement {
    By loc_btnRemoveVariationGroup = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivDeleteVariation1".formatted(goSELLERBundleId)));
    By loc_btnAddVariation = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvAddVariation".formatted(goSELLERBundleId)));
    By loc_txtVariationName1 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtVariation1Name".formatted(goSELLERBundleId)));
    By loc_txtVariationValue1 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtInputImeiSerialNumberValue".formatted(goSELLERBundleId)));
    By loc_btnAddVariationValue1 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivAddNewImeiSerialNumber".formatted(goSELLERBundleId)));
    By loc_txtVariationName2 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtVariation2Name".formatted(goSELLERBundleId)));
    By loc_txtVariationValue2 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtVariation2Value".formatted(goSELLERBundleId)));
    By loc_btnAddVariationValue2 = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivAddValueForVariation2".formatted(goSELLERBundleId)));
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivActionBarIconRight".formatted(goSELLERBundleId)));
}
