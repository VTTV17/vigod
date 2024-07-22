package mobile.buyer.login;

import org.openqa.selenium.By;

public class LoginElement {
    By loc_txtUsername = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]//*[ends-with(@resource-id,'edittext')]");
    By loc_txtPassword = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]//*[ends-with(@resource-id,'edittext')]");
    By loc_btnLogin = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    By loc_lnkSignup = By.xpath("//*[ends-with(@resource-id,'txt_sign_up')]");
    By loc_tabPhone = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    By loc_lnkForgotPassword = By.xpath("//*[ends-with(@resource-id,'forgot_pass')]");
}
