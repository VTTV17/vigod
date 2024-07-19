package mobile.seller.android.products.child_screen.inventory;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class InventoryElement {
    By loc_txtBranchStock = By.xpath("//*[@* = '%s:id/edtStock']".formatted(goSELLERBundleId));
    String rsId_btnSave = "%s:id/tvActionBarIconRight".formatted(goSELLERBundleId);
    String rsId_dlgUpdateStock = "%s:id/tabLayoutUpdateStockType".formatted(goSELLERBundleId);
    By loc_dlgUpdateStock_tabChange = By.xpath("(//*[@* = '%s:id/tabLayoutUpdateStockType']//android.widget.TextView)[2]".formatted(goSELLERBundleId));
    String rsId_dlgUpdateStock_txtQuantity = "%s:id/edtStock".formatted(goSELLERBundleId);
    String rsId_dlgUpdateStock_btnOK = "%s:id/tvUpdateStock".formatted(goSELLERBundleId);
}
