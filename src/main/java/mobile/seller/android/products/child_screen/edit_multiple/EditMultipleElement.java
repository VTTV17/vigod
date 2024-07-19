package mobile.seller.android.products.child_screen.edit_multiple;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class EditMultipleElement {
    String rsId_btnSave = "%s:id/tvActionBarIconRight".formatted(goSELLERBundleId);
    String rsId_ddvSelectedBranch = "%s:id/tvFilterBranches".formatted(goSELLERBundleId);
    String xpath_ddvBranch = "//*[@text = '%s']";
    By loc_lblActions = By.xpath("//android.widget.TextView[@* = '%s:id/tvAction']".formatted(goSELLERBundleId));
    By loc_lblUpdatePriceActions = By.xpath("(//*[@* = '%s:id/title'])[1]".formatted(goSELLERBundleId));
    By loc_lblUpdateStockActions = By.xpath("(//*[@* = '%s:id/title'])[2]".formatted(goSELLERBundleId));
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[@* = '%s:id/edtOrgPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[@* = '%s:id/edtNewPrice']//*[@* = '%s:id/edtPriceCustom']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_dlgUpdatePrice_btnOK = "%s:id/tvOK".formatted(goSELLERBundleId);
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    String rsId_dlgUpdateStock_txtQuantity = "%s:id/edtStock".formatted(goSELLERBundleId);
    String rsId_dlgUpdateStock_btnOK = "%s:id/tvUpdateStock".formatted(goSELLERBundleId);
}
