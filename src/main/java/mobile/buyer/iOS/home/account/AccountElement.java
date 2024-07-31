package mobile.buyer.iOS.home.account;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

public class AccountElement {
    By loc_btnLogout = xpath("//XCUIElementTypeImage[@name=\"ic_item_logout\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_dlgConfirmLogout_btnYes = xpath("//XCUIElementTypeButton[@name = \"Có\"] | //XCUIElementTypeButton[@name = \"Yes\"]");
    By loc_btnLogin = xpath("//XCUIElementTypeButton[@name =\"Đăng nhập\"]|//XCUIElementTypeButton[@name=\"Login\"]");
}
