package pages.dashboard.settings.branch_management;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class BranchElement {
    WebDriver driver;
    public BranchElement(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy (css = "li:nth-child(6) > a.nav-link")
    WebElement BRANCH_MANAGEMENT_MENU;
    
    @FindBy (xpath = "(//div[contains(@class,'branch-management')]//button[contains(@class,'gs-button__green')])[1]")
    WebElement ADD_BRANCH_BTN;
    
    @FindBy (xpath = "(//div[contains(@class,'branch-management ')]//div[contains(@class,'branch-list-desktop')])[1]//tbody/tr/td")
    List<WebElement> FREE_BRANCH_INFO;

    @FindBy(css = ".branch-management  tbody > tr > td:nth-child(1)")
    List<WebElement> BRANCH_NAME_LIST;

    @FindBy (css = ".branch-management  tbody > tr > td:nth-child(3)")
    List<WebElement> BRANCH_ADDRESS_LIST;
}
