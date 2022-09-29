package pages.dashboard.settings.branch_management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchPage extends BranchElement {
    WebDriverWait wait;

    public BranchPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private String removeCountry(String address) {
        String[] s = address.split(",");
        return "%s,%s,%s,%s".formatted(s[0], s[1], s[2], s[3]);
    }

    public Map<String, String> getBranchNameAndAddress() {
        Map<String, String> branchInfo = new HashMap<>();
        new HomePage(driver).navigateToSettingsPage();
        wait.until(ExpectedConditions.elementToBeClickable(BRANCH_MANAGEMENT_MENU)).click();
        waitElementList(BRANCH_NAME_LIST);
        for (int i = 0; i < BRANCH_NAME_LIST.size(); i++) {
            branchInfo.put(wait.until(ExpectedConditions.visibilityOf(BRANCH_NAME_LIST.get(i))).getText(),
                    removeCountry(wait.until(ExpectedConditions.visibilityOf(BRANCH_ADDRESS_LIST.get(i))).getText()));
        }
        return branchInfo;
    }

    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
}