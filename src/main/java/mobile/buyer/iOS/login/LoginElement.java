package mobile.buyer.iOS.login;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

public class LoginElement {
    By loc_txtEmail = xpath("//XCUIElementTypeTextField[@value = \"Email\"]");
    By loc_txtPassword = xpath("//XCUIElementTypeSecureTextField[@value = \"Mật khẩu\"]|//XCUIElementTypeSecureTextField[@value=\"Password\"]");
    By loc_btnLogin = xpath("//XCUIElementTypeButton[@name=\"Đăng nhập\"]|//XCUIElementTypeButton[@name=\"Login\"]");
}
