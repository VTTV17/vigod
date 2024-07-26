package mobile.buyer.productDetail;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static org.openqa.selenium.By.*;
import static utilities.commons.UICommonAndroid.*;
import static utilities.environment.goBUYEREnvironment.goBUYERBundleId;

public class ProductDetailElement {
    By loc_lblFlashSaleBadge = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/llFlashSaleContainer1".formatted(goBUYERBundleId)));
    By loc_lblProductName = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_desc_title".formatted(goBUYERBundleId)));
    By loc_lblSoldOutMark = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/activity_item_details_tv_not_available".formatted(goBUYERBundleId)));
    By loc_lblListingPrice = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_desc_original_price".formatted(goBUYERBundleId)));
    By loc_lblDiscountCampaignBadge = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_tv_wholesale_label".formatted(goBUYERBundleId)));
    By loc_lblWholesaleProductBadge = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_desc_group_wholesale_pricing".formatted(goBUYERBundleId)));
    By loc_lblVariationName(String variationName) {
        return androidUIAutomator(androidUIAutomatorTextString.formatted(variationName));
    }
    By loc_lblVariationName1 = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_desc_tv_variation_1_label".formatted(goBUYERBundleId)));
    By loc_lblVariationName2 = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_desc_tv_variation_2_label".formatted(goBUYERBundleId)));
    By loc_lblVariationValue(String variationValue) {
        return androidUIAutomator(androidUIAutomatorTextString.formatted(variationValue));
    }
    By loc_icnSearchBranch = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/iv_show_search_branch".formatted(goBUYERBundleId)));
    By loc_icnFilterBranch = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/iv_select_branch_filter".formatted(goBUYERBundleId)));
    By loc_lblBranchAndStock(String branchName) {
        return androidUIAutomator(androidUIAutomatorPartTextString.formatted(branchName));
    }

    By loc_lblProductDescription = new ByChained(androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/activity_item_details_desc".formatted(goBUYERBundleId))),
            id("%s:id/llWebViewContainer".formatted(goBUYERBundleId)),
            xpath("//android.widget.TextView"));
    By loc_tabReview = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_lblProductReviewTitle = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_lblProductReviewDescription = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_lblReviewContent = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_lblNoReview= androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_icnAddToCart = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/ivIconAddToCart".formatted(goBUYERBundleId)));
    By loc_lblSellingPriceOnCart = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/product_detail_content_popup_variation_tv_product_price".formatted(goBUYERBundleId)));
    By loc_chkBuyInBulk = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/product_detail_content_popup_variation_iv_check_buy_in_bulk".formatted(goBUYERBundleId)));
    By loc_txtAddToCartQuantity = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/product_detail_content_popup_variation_edt_quantity".formatted(goBUYERBundleId)));
    By loc_btnAdd = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_icnCloseAddToCart = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/product_detail_content_popup_variation_rl_exit".formatted(goBUYERBundleId)));
    By loc_btnBuyNow = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/item_market_product_detail_footer_btn_buy_now".formatted(goBUYERBundleId)));
    By loc_txtBuyNowQuantity = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_btnBuy = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
    By loc_btnContactNow = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("%s:id/activity_item_details_btn_contact_now".formatted(goBUYERBundleId)));
    By loc_icnCart = androidUIAutomator(androidUIAutomatorResourcesIdString.formatted("".formatted(goBUYERBundleId)));
}
