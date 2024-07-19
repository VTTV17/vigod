package mobile.seller.android.products.child_screen.select_image_popup;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class SelectImageElement {
    By loc_lstImages = By.xpath("//*[@*='%s:id/tvSelectIndex']".formatted(goSELLERBundleId));
    String rsId_btnSave = "%s:id/fragment_choose_photo_dialog_btn_choose".formatted(goSELLERBundleId);
}
