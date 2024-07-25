package mobile.seller.android.products.create_product;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;


public class CreateProductElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_icnUploadImages = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/rlSelectImages".formatted(goSELLERBundleId)));
    By loc_txtProductName = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtProductName".formatted(goSELLERBundleId)));
    By loc_btnProductDescription = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvProductDescription".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationListingPrice = new ByChained(androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtProductOrgPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationSellingPrice = new ByChained(androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtProductNewPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationCostPrice = new ByChained(androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtProductCostPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationSKU = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtSKU".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationBarcode = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtProductBarcode".formatted(goSELLERBundleId)));
    By loc_chkHideRemainingStock = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivHideStockOnOnlineStore".formatted(goSELLERBundleId)));
    By loc_chkDisplayIfOutOfStock = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivDisplayIfOutOfStock".formatted(goSELLERBundleId)));
    By loc_lblSelectedManageInventoryType = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/btnSwitchManageInventoryType".formatted(goSELLERBundleId)));
    By loc_lblManageInventoryByIMEI = By.xpath("//*[@* = '%s']".formatted("%s:id/llManageInventoryByImeiSerial".formatted(goSELLERBundleId)));
    By loc_chkManageStockByLotDate = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivManageStockByLotDate".formatted(goSELLERBundleId)));
    By loc_lblInventory = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/clInventoryContainer".formatted(goSELLERBundleId)));
    By loc_swShipping = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swShipping".formatted(goSELLERBundleId)));
    By loc_txtShippingWeight = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtShippingWeight".formatted(goSELLERBundleId)));
    By loc_txtShippingLength = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtShippingLength".formatted(goSELLERBundleId)));
    By loc_txtShippingWidth = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtShippingWidth".formatted(goSELLERBundleId)));
    By loc_txtShippingHeight = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtShippingHeight".formatted(goSELLERBundleId)));
    By loc_swWeb = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swPlatformWeb".formatted(goSELLERBundleId)));
    By loc_swApp = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swPlatformApp".formatted(goSELLERBundleId)));
    By loc_swInStore = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swPlatformInstore".formatted(goSELLERBundleId)));
    By loc_swGoSocial = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swPlatformGoSocial".formatted(goSELLERBundleId)));
    By loc_swPriority = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swPriority".formatted(goSELLERBundleId)));
    By loc_txtPriorityValue = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/edtPriority".formatted(goSELLERBundleId)));
    By loc_swVariations = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/swVariation".formatted(goSELLERBundleId)));
    By loc_btnAddVariation = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/tvAddVariation".formatted(goSELLERBundleId)));
    By loc_lstVariations = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/llVariationContainer".formatted(goSELLERBundleId)));
    By loc_btnEditMultiple = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/clEditMultiple".formatted(goSELLERBundleId)));
}
