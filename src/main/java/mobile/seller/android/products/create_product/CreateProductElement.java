package mobile.seller.android.products.create_product;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;


public class CreateProductElement {
    By loc_btnSave = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivActionBarIconRight".formatted(goSELLERBundleId)));
    By loc_icnUploadImages = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/rlSelectImages".formatted(goSELLERBundleId)));
    By loc_txtProductName = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductName".formatted(goSELLERBundleId)));
    By loc_btnProductDescription = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvProductDescription".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationListingPrice = new ByChained(androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductOrgPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationSellingPrice = new ByChained(androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductNewPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationCostPrice = new ByChained(androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductCostPrice".formatted(goSELLERBundleId))), By.id("%s:id/edtPriceCustom".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationSKU = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtSKU".formatted(goSELLERBundleId)));
    By loc_txtWithoutVariationBarcode = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtProductBarcode".formatted(goSELLERBundleId)));
    By loc_chkHideRemainingStock = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivHideStockOnOnlineStore".formatted(goSELLERBundleId)));
    By loc_chkDisplayIfOutOfStock = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivDisplayIfOutOfStock".formatted(goSELLERBundleId)));
    By loc_lblSelectedManageInventoryType = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/btnSwitchManageInventoryType".formatted(goSELLERBundleId)));
    By loc_lblManageInventoryByIMEI = By.xpath("//*[@* = '%s']".formatted("%s:id/llManageInventoryByImeiSerial".formatted(goSELLERBundleId)));
    By loc_chkManageStockByLotDate = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/ivManageStockByLotDate".formatted(goSELLERBundleId)));
    By loc_lblInventory = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/clInventoryContainer".formatted(goSELLERBundleId)));
    By loc_swShipping = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swShipping".formatted(goSELLERBundleId)));
    By loc_txtShippingWeight = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtShippingWeight".formatted(goSELLERBundleId)));
    By loc_txtShippingLength = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtShippingLength".formatted(goSELLERBundleId)));
    By loc_txtShippingWidth = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtShippingWidth".formatted(goSELLERBundleId)));
    By loc_txtShippingHeight = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtShippingHeight".formatted(goSELLERBundleId)));
    By loc_swWeb = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swPlatformWeb".formatted(goSELLERBundleId)));
    By loc_swApp = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swPlatformApp".formatted(goSELLERBundleId)));
    By loc_swInStore = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swPlatformInstore".formatted(goSELLERBundleId)));
    By loc_swGoSocial = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swPlatformGoSocial".formatted(goSELLERBundleId)));
    By loc_swPriority = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swPriority".formatted(goSELLERBundleId)));
    By loc_txtPriorityValue = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/edtPriority".formatted(goSELLERBundleId)));
    By loc_swVariations = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/swVariation".formatted(goSELLERBundleId)));
    By loc_btnAddVariation = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/tvAddVariation".formatted(goSELLERBundleId)));
    By loc_lstVariations = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/llVariationContainer".formatted(goSELLERBundleId)));
    By loc_btnEditMultiple = androidUIAutomator(androidUIAutomatorString.formatted("%s:id/clEditMultiple".formatted(goSELLERBundleId)));
}
