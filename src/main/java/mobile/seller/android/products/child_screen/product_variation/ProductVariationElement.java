package mobile.seller.android.products.child_screen.product_variation;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class ProductVariationElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_btnSelectImage = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/rlSelectImages".formatted(goSELLERBundleId)));
    By loc_txtVariationName = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtVersionName".formatted(goSELLERBundleId)));
    By loc_chkReuseProductDescription = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivUseProductDescription".formatted(goSELLERBundleId)));
    By loc_btnVariationDescription = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvVariationDescription".formatted(goSELLERBundleId)));
    By loc_txtVariationListingPrice = By.xpath("//*[@*= '%s:id/edtVariationOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationSellingPrice = By.xpath("//*[@*= '%s:id/edtVariationNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationCostPrice = By.xpath("//*[@*= '%s:id/edtVariationCostPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationSKU = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtSKU".formatted(goSELLERBundleId)));
    By loc_txtVariationBarcode = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtBarcode".formatted(goSELLERBundleId)));
    By loc_btnInventory = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/clInventoryContainer".formatted(goSELLERBundleId)));
    By loc_btnDeactivate = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvActiveDeactive".formatted(goSELLERBundleId)));
}
