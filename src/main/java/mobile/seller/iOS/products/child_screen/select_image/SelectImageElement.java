package mobile.seller.iOS.products.child_screen.select_image;

import org.openqa.selenium.By;

public class SelectImageElement {
    By loc_lstImages = By.xpath("//XCUIElementTypeCollectionView//XCUIElementTypeButton");
    By loc_btnSave = By.xpath("//XCUIElementTypeImage[@name=\"ic_DownArrow\"]//preceding-sibling::XCUIElementTypeButton[1]");
}
