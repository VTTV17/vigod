package mobile.seller.android.products.child_screen.filter.collections;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class CollectionsElement {
    By loc_btnAllCollections = By.xpath("(//*[@* = '%s:id/htvFullCollections'] // *[@* = '%s:id/tag_container'])[1]".formatted(goSELLERBundleId, goSELLERBundleId));
    String str_btnCollection = "//*[@text = '%s']";
}
