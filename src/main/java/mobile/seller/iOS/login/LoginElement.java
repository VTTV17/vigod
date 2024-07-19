package mobile.seller.iOS.login;

import org.openqa.selenium.By;

public class LoginElement {
    By loc_txtUsername = By.xpath("//XCUIElementTypeTextField[@value=\"Phone number or email\"]");
    By loc_txtPassword = By.xpath("//XCUIElementTypeSecureTextField[@value=\"Password\"]");
    By loc_chkTermOfUse = By.xpath("//XCUIElementTypeImage[@name=\"ic_green_rectangle_unselected\"]");
    By loc_btnLogin = By.xpath("//XCUIElementTypeStaticText[@name=\"LOGIN\"]");
}
