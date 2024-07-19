package mobile.seller.iOS.products.child_screen.inventory.add_imei;

import org.openqa.selenium.By;

public class AddIMEIElement {
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_icnRemoveIMEI = By.xpath("//XCUIElementTypeScrollView/XCUIElementTypeOther[1]/XCUIElementTypeOther[2]//XCUIElementTypeButton/XCUIElementTypeStaticText");
    By loc_txtIMEI = By.xpath("//XCUIElementTypeTextField[@value=\"Input IMEI/Serial number\"]");
    By loc_btnAdd = By.xpath("//XCUIElementTypeImage[@name = \"icon_plus-white\"]");
}
