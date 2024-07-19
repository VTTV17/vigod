package mobile.seller.iOS.products.child_screen.edit_multiple;

import org.openqa.selenium.By;

public class EditMultipleElement {
    By loc_btnSave = By.xpath("//XCUIElementTypeButton[@name=\"icon checked white\"]");
    By loc_icnStoreBranch = By.xpath("//XCUIElementTypeImage[@name=\"icon_store_branch\"]");

    By loc_ddvBranch(String branchName) {
        return By.xpath("(//XCUIElementTypeStaticText[@name=\"%s\"])[1]".formatted(branchName));
    }

    By loc_icnActions = By.xpath("//XCUIElementTypeImage[@name=\"icon_tree_dots\"]");
    By loc_ddvUpdatePriceActions = By.xpath("//XCUIElementTypeStaticText[@name=\"Update price\"]");
    By loc_ddvUpdateStockActions = By.xpath("//XCUIElementTypeStaticText[@name=\"Update stock\"]");

    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Selling price\"]//following-sibling::XCUIElementTypeOther[1]//XCUIElementTypeTextField");
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//XCUIElementTypeStaticText[@name=\"Selling price\"]//following-sibling::XCUIElementTypeOther[2]//XCUIElementTypeTextField");
    By loc_dlgUpdatePrice_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");

    By loc_dlgUpdateStock_tabChange = By.xpath("//XCUIElementTypeButton[@name=\"CHANGE\"]");
    By loc_dlgUpdateStock_txtQuantity = By.xpath("//XCUIElementTypeStaticText[@name=\"Input quantity\"]/preceding-sibling::*/XCUIElementTypeTextField");
    By loc_dlgUpdateStock_btnOK = By.xpath("//XCUIElementTypeButton[@name=\"OK\"]");
}
