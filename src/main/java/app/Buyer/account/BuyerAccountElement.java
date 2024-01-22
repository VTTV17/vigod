package app.Buyer.account;

import org.openqa.selenium.By;

public class BuyerAccountElement {
    By DISPLAY_NAME = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_tab_account_tv_user_name')]");
    By MEMBERSHIP_LEVEL = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_tab_account_tv_membership_level')]");
    By BARCODE = By.xpath("//android.widget.LinearLayout[contains(@resource-id,'fragment_tab_account_user_profile_ll_my_barcode_container')]");
    By AVATAR = By.xpath("//android.widget.FrameLayout[contains(@resource-id,'fragment_tab_account_riv_avatar')]/android.widget.ImageView[1]");
    By MYBARCODE_BARCODE = By.xpath("//*[ends-with(@resource-id,'activity_my_barcode_tv_code')]");
    By MYBARCODE_NAME = By.xpath("//*[ends-with(@resource-id,'activity_my_barcode_tv_user_name')]");
    By LOGOUT_POPUP_LOGOUT_BTN = By.xpath("//*[ends-with(@resource-id,'button1')]");
}
