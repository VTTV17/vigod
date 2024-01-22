package app.Buyer.productDetail;

import org.openqa.selenium.By;

public class BuyerProductDetailElement {
    By PRODUCT_IMAGE = By.xpath("//*[contains(@resource-id, 'item_product_detail_image')]");
    By FLASH_SALE_LABEL = By.xpath("//*[contains(@resource-id, 'rlFlashSaleContainer')]");
    By DISCOUNT_CAMPAIGN_LABEL = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_tv_wholesale_label')]");
    By WHOLESALE_PRODUCT_LABEL = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_group_wholesale_pricing')]");
    By PRODUCT_NAME = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_title')]");
    By FLASH_SALE_BADGE = By.xpath("//*[contains(@resource-id, 'llFlashSale')]");
    By DISCOUNT_CAMPAIGN_BADGE = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_tv_wholesale_label')]");
    By WHOLE_SALE_PRODUCT_BADGE = By.xpath("//*[contains(@resource-id, 'group_wholesale_pricing')]");
    By SOLD_OUT_MARK = By.xpath("//*[contains(@resource-id, 'activity_item_details_tv_not_available')]");
    By LISTING_PRICE = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_original_price')]");
    By SELLING_PRICE = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_promotion_price')]");
    By CONVERSION_UNIT_TITLE = By.xpath("//*[contains(@resource-id, 'htvConversationUnit')]/preceding-sibling::*");
    By CONVERSION_UNIT_LIST = By.xpath("//*[contains(@resource-id, 'htvConversationUnit')]");
    By VARIATION_NAME_LIST = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_tv_variation')]");
    By VARIATION_VALUE_LIST = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_htv_variation')]//*[contains(@resource-id, 'text')]");
    By BRANCH_LIST = By.xpath("//*[contains(@resource-id, 'tv_branch_name')]");
    By FILTER_BRANCH_ICON = By.xpath("//*[contains(@resource-id, 'iv_select_branch_filter')]");
    By SEARCH_BRANCH_ICON = By.xpath("//*[contains(@resource-id, 'iv_show_search_branch')]");
    By PRODUCT_DESCRIPTION_TAB = By.xpath("//*[contains(@resource-id, 'activity_market_shop_profile_tabLayout')]//*[contains(@resource-id, 'product_detail_content_include_tabs_1')]");
    By PRODUCT_DESCRIPTION_CONTENT = By.xpath("//*[contains(@resource-id, 'activity_item_details_desc')]/*/*/*/*/*/android.widget.TextView");
    By PRODUCT_REVIEW_TAB = By.xpath("//*[ends-with(@resource-id, 'product_detail_content_include_tabs_text_2')]");
    By REVIEWCONTENT = By.xpath("//*[contains(@resource-id, 'product_detail_content_include_review')]");
    By EMPTYREVIEW = By.xpath("//*[ends-with(@resource-id, 'product_detail_content_include_review_rl_review_empty')]");
    By PRODUCT_REVIEW_TITLE = By.xpath("//*[ends-with(@resource-id, 'item_review_tv_title')]");
    By PRODUCT_REVIEW_DESCRIPTION = By.xpath("//*[ends-with(@resource-id, 'item_review_tv_description')]");

    By ADD_TO_CART_ICON = By.xpath("//*[contains(@resource-id, 'ivIconAddToCart')]");
    By ADD_TO_CART_POPUP_LISTING_PRICE = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_product_original_price')]");
    By ADD_TO_CART_POPUP_SELLING_PRICE = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_product_price')]");
    By ADD_TO_CART_POPUP_SELECTED_VARIATION = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_selected_variation')]");
    By ADD_TO_CART_POPUP_BUY_IN_BULK_CHECKBOX = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_iv_check_buy_in_bulk')]");
    By ADD_TO_CART_POPUP_QUANTITY_TEXT_BOX = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_edt_quantity')]");
    By ADD_TO_CART_POPUP_CLOSE_ICON = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_rl_exit')]");
    By ADD_TO_CART_POPUP_ADD_BTN = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_cl_button_container')]");
    By BUY_NOW_BTN = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_footer_btn_buy_now')]");
    By BUY_NOW_POPUP_LISTING_PRICE = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_product_original_price')]");
    By BUY_NOW_POPUP_SELLING_PRICE = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_product_price')]");
    By BUY_NOW_POPUP_BUY_IN_BULK_CHECKBOX = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_iv_check_buy_in_bulk')]");
    By BUY_NOW_POPUP_SELECTED_VARIATION = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_tv_selected_variation')]");
    By BUY_NOW_POPUP_QUANTITY_TEXT_BOX = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_edt_quantity')]");
    By BUY_NOW_POPUP_BUY_BTN = By.xpath("//*[contains(@resource-id, 'product_detail_content_popup_variation_cl_button_container')]");
    By SMART_PAYPAL = By.xpath("//*[contains(@resource-id, 'flSmartPaypalButton')]");
    By CONTACT_NOW_BTN =By.xpath("//*[contains(@resource-id, 'activity_item_details_btn_contact_now')]");
    By CART_ICON = By.xpath("//*[ends-with(@resource-id,'action_bar_basic_rl_shopping_cart')]");
    By ITEM_DETAIL_FOOTER = By.xpath("//*[contains(@resource-id, 'ItemsDetailsFooter')]");
}
