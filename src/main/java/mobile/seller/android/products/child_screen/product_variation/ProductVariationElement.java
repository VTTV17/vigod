package mobile.seller.android.products.child_screen.product_variation;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class ProductVariationElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_btnSelectImage = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/rlSelectImages".formatted(goSELLERBundleId)));
    By loc_txtVariationName = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtVersionName".formatted(goSELLERBundleId)));
    By loc_chkReuseProductDescription = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivUseProductDescription".formatted(goSELLERBundleId)));
    By loc_btnVariationDescription = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvVariationDescription".formatted(goSELLERBundleId)));
    By loc_txtVariationListingPrice = By.xpath("//*[@*= '%s:id/edtVariationOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationSellingPrice = By.xpath("//*[@*= '%s:id/edtVariationNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationCostPrice = By.xpath("//*[@*= '%s:id/edtVariationCostPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationSKU = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtSKU".formatted(goSELLERBundleId)));
    By loc_txtVariationBarcode = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtBarcode".formatted(goSELLERBundleId)));
    By loc_btnInventory = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/clInventoryContainer".formatted(goSELLERBundleId)));
    By loc_btnDeactivate = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvActiveDeactive".formatted(goSELLERBundleId)));
}
