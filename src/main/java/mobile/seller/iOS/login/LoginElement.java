package mobile.seller.iOS.login;

import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.iOSNsPredicateString;

public class LoginElement {
    By loc_txtUsername = iOSNsPredicateString("type == \"XCUIElementTypeTextField\"");
    By loc_txtPassword = iOSNsPredicateString("type == \"XCUIElementTypeSecureTextField\"");
    By loc_chkTermOfUse = By.xpath("(//XCUIElementTypeTextView[@value]//preceding-sibling::*)[1]");
    By loc_btnLogin = By.xpath("//XCUIElementTypeStaticText[@name=\"LOGIN\"]");
}
