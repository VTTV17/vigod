package mobile.buyer.login.signup;

import org.openqa.selenium.By;

public class SignupElement {
    By loc_ddvCountryCode = By.xpath("//*[ends-with(@resource-id,'country_code')]");
    By loc_icnSearch = By.xpath("//*[ends-with(@resource-id,'btn_search')]");
    By loc_txtCountrySearchBox = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By loc_lstSearchResult = By.xpath("//*[ends-with(@resource-id,'country_code_list_tv_title')]");

    By loc_tabMail = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[1]");
    By loc_tabPhone = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");

    By loc_txt = By.xpath("//*[ends-with(@resource-id,'limit_edittext')]");

    By loc_txtUserName = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]");
    By loc_txtPassword = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]");
    By loc_txtDisplayName = By.xpath("//*[ends-with(@resource-id,'displayName')]");
    By loc_txtBirthday = By.xpath("//*[ends-with(@resource-id,'birthday')]");
    By loc_btnBirthdayOK = By.xpath("//*[ends-with(@resource-id,'ok')]");

    By loc_chkTermOfUse = By.xpath("//*[ends-with(@resource-id,'btn_check_term_and_policy')]");
    By btnContinue = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");

    By loc_lblError = By.xpath("//*[contains(@resource-id,'error')]");

    By loc_txtVerificationCode = By.xpath("//*[ends-with(@resource-id,'verify_code_edittext')]");
    By loc_btnResend = By.xpath("//*[ends-with(@resource-id,'verify_code_resend_action')]");
    By loc_btnVerify = By.xpath("//*[ends-with(@resource-id,'verify_code_action')]");

    By loc_dlgToast = By.xpath("//*[ends-with(@class,'Toast')]");

}
