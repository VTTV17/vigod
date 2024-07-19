package mobile.seller.iOS.products.child_screen.filter.branch;

import org.openqa.selenium.By;

public class BranchElement {
    By loc_btnAllBranches = By.xpath("//XCUIElementTypeStaticText[@name=\"All branches\"]");
    By loc_btnBranch(String branchName) {
        return By.xpath("//XCUIElementTypeStaticText[@name=\"%s\"]".formatted(branchName));
    }
}
