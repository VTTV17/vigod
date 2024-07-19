package mobile.seller.android.products.child_screen.product_variation;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class ProductVariationElement {
    String rsId_prgLoading = "%s:id/pbLoading".formatted(goSELLERBundleId);
    String rsId_btnSave = "%s:id/tvActionBarIconRight".formatted(goSELLERBundleId);
    String rsId_btnSelectImage = "%s:id/rlSelectImages".formatted(goSELLERBundleId);
    String rsId_txtVariationName = "%s:id/edtVersionName".formatted(goSELLERBundleId);
    String rsId_chkReuseProductDescription = "%s:id/ivUseProductDescription".formatted(goSELLERBundleId);
    String rsId_btnVariationDescription = "%s:id/tvVariationDescription".formatted(goSELLERBundleId);
    String rsId_sctPrice = "%s:id/llProductPriceContainer".formatted(goSELLERBundleId);
    By loc_txtVariationListingPrice = By.xpath("//*[@*= '%s:id/edtVariationOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationSellingPrice = By.xpath("//*[@*= '%s:id/edtVariationNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_txtVariationCostPrice = By.xpath("//*[@*= '%s:id/edtVariationCostPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_txtVariationSKU = "%s:id/edtSKU".formatted(goSELLERBundleId);
    String rsId_txtVariationBarcode = "%s:id/edtBarcode".formatted(goSELLERBundleId);
    String rsId_btnInventory = "%s:id/clInventoryContainer".formatted(goSELLERBundleId);
    String rsId_btnDeactivate = "%s:id/tvActiveDeactive".formatted(goSELLERBundleId);
}
