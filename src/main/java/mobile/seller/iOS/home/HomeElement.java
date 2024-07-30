package mobile.seller.iOS.home;

import org.openqa.selenium.By;

public class HomeElement {
    public static By loc_icnAccount = By.xpath("//XCUIElementTypeImage[@name=\"icon_tabbar_account\"]/parent::XCUIElementTypeButton");
    By loc_icnCreateProduct = By.xpath("//XCUIElementTypeImage[@name=\"icon_home_create_new_product\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_icnProductManagement = By.xpath("//XCUIElementTypeImage[@name=\"icon_home_product_management\"]/preceding-sibling::XCUIElementTypeButton");
}
