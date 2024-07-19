package mobile.seller.iOS.products.child_screen.inventory;

import org.openqa.selenium.By;

public class InventoryElement {
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_txtBranchStock(String branchName) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]/following-sibling::XCUIElementTypeTextField | //XCUIElementTypeStaticText[@name=\"%s\"]/preceding-sibling::XCUIElementTypeTextField\n".formatted(branchName, branchName));
    }

    By loc_dlgUpdateStock_tabChange = By.xpath("//XCUIElementTypeStaticText[@name=\"CHANGE\"]");
    By loc_txtUpdateStock_txtQuantity = By.xpath("//XCUIElementTypeStaticText[@name=\"Input quantity\"]/preceding-sibling::*/XCUIElementTypeTextField");
    By loc_dlgUpdateStock_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");
}
