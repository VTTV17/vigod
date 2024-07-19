package mobile.seller.iOS.products.child_screen.crud_variations;

import org.openqa.selenium.By;

public class CRUDVariationElement {
    By loc_icnRemoveVariationGroup = By.xpath("//XCUIElementTypeImage[@name=\"ic-minus_circle\"]");
    By loc_txtVariationName = By.xpath("//XCUIElementTypeImage[@name=\"ic-minus_circle\"]/following-sibling::XCUIElementTypeTextField");
    By loc_txtVariationValue = By.xpath("//XCUIElementTypeImage[@name=\"ic-plus\"]/preceding-sibling::*//XCUIElementTypeTextField");
    By loc_icnAddVariationValue = By.xpath("//XCUIElementTypeTextField/parent::XCUIElementTypeOther/following-sibling::XCUIElementTypeImage[@name=\"ic-plus\"]");
    By loc_btnAddVariation = By.xpath("(//XCUIElementTypeImage[@name=\"ic-plus\"])[last()]");
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
}
