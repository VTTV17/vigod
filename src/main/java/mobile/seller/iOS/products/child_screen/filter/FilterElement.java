package mobile.seller.iOS.products.child_screen.filter;

import org.openqa.selenium.By;

public class FilterElement {
    By loc_btnReset = By.xpath("//XCUIElementTypeButton[@name=\"Reset\"]");
    By loc_btnActiveStatus = By.xpath("//XCUIElementTypeButton[@name=\"Active\"]");
    By loc_btnInActiveStatus = By.xpath("//XCUIElementTypeButton[@name=\"Inactive\"]");
    By loc_btnErrorStatus =By.xpath("//XCUIElementTypeButton[@name=\"Error\"]");
    By loc_btnLazadaChannel =By.xpath("//XCUIElementTypeButton[@name=\"Lazada\"]");
    By loc_btnShopeeChannel =By.xpath("//XCUIElementTypeButton[@name=\"Shopee\"]");
    By loc_btnWebPlatform =By.xpath("//XCUIElementTypeButton[@name=\"Web\"]");
    By loc_btnAppPlatform =By.xpath("//XCUIElementTypeButton[@name=\"App\"]");
    By loc_btnInStorePlatform =By.xpath("//XCUIElementTypeButton[@name=\"In-store\"]");
    By loc_btnNonePlatform =By.xpath("//XCUIElementTypeButton[@name=\"None Platform\"]");
    By loc_btnSeeAllBranches =By.xpath("(//XCUIElementTypeButton[@name=\"See all\"])[1]");
    By loc_btnSeeAllCollections =By.xpath("(//XCUIElementTypeButton[@name=\"See all\"])[2]");
    By loc_btnApply = By.xpath("//XCUIElementTypeButton[@name=\"Apply\"]");

}
