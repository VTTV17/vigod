package mobile.seller.iOS.products.child_screen.product_description;

import org.openqa.selenium.By;

public class ProductDescriptionElement {
    By loc_rtfDescription = By.xpath("//XCUIElementTypeNavigationBar/following-sibling::XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther[2]");
    By loc_txtContent(String description) {
        return By.xpath("//*[@value='%s']".formatted(description));
    }
//    By loc_txtContent = By.xpath("//XCUIElementTypeTextField | //XCUIElementTypeTextView");
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"Item\"]");
}
