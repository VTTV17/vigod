package pages.buyerapp.productDetail;

import org.openqa.selenium.By;

public class BuyerProductDetailElement {
    By FLASH_SALE_LABEL = By.xpath("//*[contains(@resource-id, 'rlFlashSaleContainer')]");
    By DISCOUNT_CAMPAIGN_LABEL = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_tv_wholesale_label')]");
    By WHOLESALE_PRODUCT_LABEL =By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_group_wholesale_pricing')]");
    By PRODUCT_NAME = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_title')]");
    By LISTING_PRICE = By.xpath("//*[contains(@resource-id, '//*[contains(@resource-id, 'item_market_product_detail_desc_original_price')]')]");
    By SELLING_PRICE = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_promotion_price')]");
    By CONVERSION_UNIT_TITLE = By.xpath("//*[contains(@resource-id, 'htvConversationUnit')]/preceding-sibling::*");
    By CONVERSION_UNIT_LIST = By.xpath("//*[contains(@resource-id, 'htvConversationUnit')]");
    By VARIATION_NAME_LIST = By.xpath("//*[contains(@resource-id, '//*[contains(@resource-id, 'item_market_product_detail_desc_tv_variation')]')]");
    By VARIATION_VALUE_LIST = By.xpath("//*[contains(@resource-id, 'item_market_product_detail_desc_htv_variation')]//*[contains(@resource-id, 'text')]");
    By BRANCH_LIST = By.xpath("//*[contains(@resource-id, 'tv_branch_name')]");
    By SEARCH_BRANCH_ICON = By.xpath("//*[contains(@resource-id, 'iv_show_search_branch')]");
    By DESCRIPTION = By.xpath("//*[contains(@resource-id, '//*[contains(@resource-id, 'activity_item_details_desc')]//android.widget.TextView')]");
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
}
