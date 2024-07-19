package mobile.seller.android.products.create_product;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class CreateProductElement {
    String rsId_prgLoading = "%s:id/pbLoading".formatted(goSELLERBundleId);
    String rsId_btnSave = "%s:id/tvActionBarIconRight".formatted(goSELLERBundleId);
    String rsId_btnSelectImage = "%s:id/rlSelectImages".formatted(goSELLERBundleId);
    String rsId_txtProductName = "%s:id/edtProductName".formatted(goSELLERBundleId);
    String rsId_btnProductDescription = "%s:id/tvProductDescription".formatted(goSELLERBundleId);
    String rsId_sctPrice = "%s:id/clProductPriceContainer".formatted(goSELLERBundleId);
    By loc_txtWithoutVariationListingPrice = By.xpath("//*[@*= '%s:id/edtProductOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtWithoutVariationSellingPrice = By.xpath("//*[@*= '%s:id/edtProductNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtWithoutVariationCostPrice = By.xpath("//*[@*= '%s:id/edtProductCostPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_txtWithoutVariationSKU = "%s:id/edtSKU".formatted(goSELLERBundleId);
    String rsId_txtWithoutVariationBarcode = "%s:id/edtProductBarcode".formatted(goSELLERBundleId);
    String rsId_chkHideRemainingStock = "%s:id/ivHideStockOnOnlineStore".formatted(goSELLERBundleId);
    String rsId_chkShowOutOfStock ="%s:id/ivDisplayIfOutOfStock".formatted(goSELLERBundleId);
    String rsId_chkShowListingPrice = "%s:id/ivListingProductCheckBox".formatted(goSELLERBundleId);
    String rsId_ddvSelectedManageType = "%s:id/btnSwitchManageInventoryType".formatted(goSELLERBundleId);
    String rsId_ddvManagedByProduct = "%s:id/llManageInventoryByProduct".formatted(goSELLERBundleId);
    String rsId_ddvManagedByIMEI = "%s:id/llManageInventoryByImeiSerial".formatted(goSELLERBundleId);
    String rsId_chkManageByLot = "%s:id/ivManageStockByLotDate".formatted(goSELLERBundleId);
    String rsId_chkExcludeExpiredStock = "%s:id/ivExcludeExpireQuantity".formatted(goSELLERBundleId);
    String rsId_btnInventory = "%s:id/clInventoryContainer".formatted(goSELLERBundleId);
    String rsId_swShipping = "%s:id/swShipping".formatted(goSELLERBundleId);
    String rsId_txtWeight = "%s:id/edtShippingWeight".formatted(goSELLERBundleId);
    String rsId_txtLength = "%s:id/edtShippingLength".formatted(goSELLERBundleId);
    String rsId_txtWidth = "%s:id/edtShippingWidth".formatted(goSELLERBundleId);
    String rsId_txtHeight = "%s:id/edtShippingHeight".formatted(goSELLERBundleId);
    String rsId_swWebPlatform = "%s:id/swPlatformWeb".formatted(goSELLERBundleId);
    String rsId_swAppPlatform = "%s:id/swPlatformApp".formatted(goSELLERBundleId);
    String rsId_swInStorePlatform = "%s:id/swPlatformInstore".formatted(goSELLERBundleId);
    String rsId_swGoSocialPlatform = "%s:id/swPlatformGoSocial".formatted(goSELLERBundleId);
    String rsId_btnAddCollection = "%s:id/tvAddCollection".formatted(goSELLERBundleId);
    String rsId_swPriority = "%s:id/swPriority".formatted(goSELLERBundleId);
    String rsId_txtPriority = "%s:id/edtPriority".formatted(goSELLERBundleId);
    String rsId_swVariations = "%s:id/swVariation".formatted(goSELLERBundleId);
    String rsId_lblVariation = "%s:id/tvVariationLabel".formatted(goSELLERBundleId);
    By loc_imgVariation = By.xpath("//*[@*= '%s:id/rivVariationImage']".formatted(goSELLERBundleId));
    String rsId_btnAddVariation = "%s:id/tvAddVariation".formatted(goSELLERBundleId);
    String rsId_btnEditMultiple = "%s:id/clEditMultiple".formatted(goSELLERBundleId);

}
