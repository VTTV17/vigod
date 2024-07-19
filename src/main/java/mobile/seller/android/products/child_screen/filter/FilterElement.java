package mobile.seller.android.products.child_screen.filter;

import org.openqa.selenium.By;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class FilterElement {
    String rsId_btnReset = "%s:id/btnReset".formatted(goSELLERBundleId);
    String rsId_sctStatus = "%s:id/htvStatus".formatted(goSELLERBundleId);
    By loc_btnFilterByStatus = By.xpath("//*[@* = '%s:id/htvStatus']//*[@* = '%s:id/tag_container']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_sctChannel = "%s:id/htvChannel".formatted(goSELLERBundleId);
    By loc_btnFilterByChannel = By.xpath("//*[@* = '%s:id/htvChannel']//*[@* = '%s:id/tag_container']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_sctPlatform = "%s:id/htvPlatform".formatted(goSELLERBundleId);
    By loc_btnFilterByPlatform = By.xpath("//*[@* = '%s:id/htvPlatform']//*[@* = '%s:id/tag_container']".formatted(goSELLERBundleId, goSELLERBundleId));
    String rsId_btnSeeAllBranches = "%s:id/btnSeeAllBranches".formatted(goSELLERBundleId);
    String rsId_btnSeeAllCollections = "%s:id/btnSeeAllCollections".formatted(goSELLERBundleId);
    String rsId_btnApply = "%s:id/btnApply".formatted(goSELLERBundleId);

}
