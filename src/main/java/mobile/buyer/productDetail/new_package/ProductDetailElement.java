package mobile.buyer.productDetail.new_package;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

public class ProductDetailElement {
    By loc_lblFlashSaleBadge = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/llFlashSaleContainer1\")");
    By loc_lblProductName = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/item_market_product_detail_desc_title\")");
    By loc_lblListingPrice = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/item_market_product_detail_desc_original_price\")");
    By loc_lblWholesaleProductBadge = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/item_market_product_detail_tv_wholesale_label\")");
    By loc_icnSearchBranch = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/iv_show_search_branch\")");
    By loc_icnFilterBranch = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/iv_select_branch_filter\")");
    By loc_lstBranches = AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/rv_branch\")");
    By sctBranch(int branchIndex) {
        return AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"com.mediastep.shop0053:id/root\").instance(%d)".formatted(branchIndex));
    }
    By loc_lblBranchAndStock(int branchIndex) {
        return new ByChained(sctBranch(branchIndex), By.id("com.mediastep.shop0053:id/tv_branch_name"));
    }
}
