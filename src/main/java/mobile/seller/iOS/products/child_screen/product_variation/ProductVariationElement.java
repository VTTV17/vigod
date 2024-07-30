package mobile.seller.iOS.products.child_screen.product_variation;

import org.openqa.selenium.By;

public class ProductVariationElement {
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_icnVariationImage = By.xpath("//XCUIElementTypeImage[@name=\"icon_selected_image_default\"]");
    By loc_txtVariationName = By.xpath("//XCUIElementTypeStaticText[@name=\"Product version name *\"]//following-sibling::XCUIElementTypeTextField");
    By loc_chkReuseProductDescription = By.xpath("//XCUIElementTypeStaticText[@name=\"Reuse the product description\"]//preceding-sibling::XCUIElementTypeOther");
    By loc_btnVariationDescription = By.xpath("//XCUIElementTypeStaticText[@name=\"Input product description\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_txtVariationListingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\"]//following-sibling::*//XCUIElementTypeTextField)[1]");
    By loc_txtVariationSellingPrice = By.xpath("(//XCUIElementTypeStaticText[@name=\"Selling price\"]//following-sibling::*//XCUIElementTypeTextField)[2]");
    By loc_txtVariationCostPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Cost price\"]//following-sibling::*//XCUIElementTypeTextField");
    By loc_btnEditVariationSKU = By.xpath("//XCUIElementTypeStaticText[@name=\"Edit\"]");
    By loc_txtVariationSKU = By.xpath("//XCUIElementTypeStaticText[@name=\"SKU\"]//following-sibling::XCUIElementTypeTextField");
    By loc_txtVariationBarcode = By.xpath("//XCUIElementTypeStaticText[@name=\"Barcode\"]//following-sibling::XCUIElementTypeTextField");
    By loc_icnInventory = By.xpath("//XCUIElementTypeImage[@name=\"icon_inventory\"]/preceding-sibling::XCUIElementTypeButton");
    By loc_btnDeactivate = By.xpath("//XCUIElementTypeButton[contains(@name, \"ctivate\")]");
}
