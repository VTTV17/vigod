package mobile.buyer.iOS.search;

import org.openqa.selenium.By;

import static org.openqa.selenium.By.xpath;

public class SearchElement {
    By loc_btnSearch = xpath("//XCUIElementTypeButton[@name=\"Tìm kiếm\"]|//XCUIElementTypeButton[@name=\"Search\"]");
    By loc_txtSearchBox = xpath("//XCUIElementTypeImage[@name=\"ic_search\"]//following-sibling::XCUIElementTypeTextField");

    By loc_lstResult(String keyword) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(keyword));
    }
}
