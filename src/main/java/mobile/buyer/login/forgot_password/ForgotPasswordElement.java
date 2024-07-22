package mobile.buyer.login.forgot_password;

import org.openqa.selenium.By;

public class ForgotPasswordElement {
    By loc_txtUsername = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[1]");
    By loc_txtPassword = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[2]");
}
