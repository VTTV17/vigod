package app.Buyer.servicedetail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class BuyerServiceDetailElement {
    WebDriver driver;
    public BuyerServiceDetailElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    By NAME = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_service_detail_tv_booking_name')]");
    By PRICE = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_service_detail_tv_price')]");
    By DESCRIPTION = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'service_detail_content_description_ll_webview_container')]//android.widget.TextView");
    By LOCATIONS = By.xpath("//android.widget.TextView[contains(@resource-id,'item_service_choose_location_tv_location')]");
    By CONTACT_NOW_BTN = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_service_detail_btn_contact_now')]");
    By BOOK_NOW_BTN = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_service_detail_tv_book_now')]");
    By LOCATION_NUMBER = By.xpath("//android.widget.TextView[contains(@resource-id,'activity_service_detail_tv_location_number')]");
    By DESCRIPTION_TAB = By.xpath("//android.widget.TextView[contains(@resource-id,'service_detail_content_tabs_text_1')]");
    By LOCATION_TAB = By.xpath("//android.widget.TextView[contains(@resource-id,'service_detail_content_tabs_text_2')]");
    By SIMILAR_TAB = By.xpath("//android.widget.TextView[contains(@resource-id,'service_detail_content_tabs_text_3')]");
    By DESCRIPTION_TITLE = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'activity_service_detail_description')]/android.widget.LinearLayout/android.widget.TextView");
    By LOCATION_TITLE = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'activity_service_detail_locations')]/android.widget.LinearLayout/android.widget.TextView");
    By SIMILAR_TITLE = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'service_detail_content_similar_ll_container')]/android.widget.TextView");
    By REQUIRE_LOGIN_POPUP = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'fragment_ask_for_login_or_create_new_account_dialog_ll_content_layout')]");
    By CONTACT_POPUP = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'popup_contact_rl_container')]");
    By ADD_TO_CART = By.xpath("//android.widget.ImageView[contains(@resource-id,'activity_service_detail_iv_add_to_cart')]");
    By SIMILAR_ITEM_LIST = By.xpath("//android.widget.FrameLayout[contains(@resource-id,'item_service_detail_similar_service_iv_image')]");
    By DESCRIPTION_SECTION = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'activity_service_detail_description')]");
    By LOCATION_SECTION = By.xpath("//android.widget.RelativeLayout[contains(@resource-id,'activity_service_detail_locations')]");
}

