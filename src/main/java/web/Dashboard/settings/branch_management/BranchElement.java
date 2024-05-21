package web.Dashboard.settings.branch_management;

import org.openqa.selenium.By;
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

    @FindBy (css = "li:nth-child(7) > a.nav-link")
    WebElement BRANCH_MANAGEMENT_MENU;
    
    @FindBy (xpath = "(//div[contains(@class,'branch-management')]//button[contains(@class,'gs-button__green')])[1]")
    WebElement ADD_BRANCH_BTN;
    
    @FindBy (xpath = "(//div[contains(@class,'branch-management ')]//div[contains(@class,'branch-list-desktop')])[1]//tbody/tr/td")
    List<WebElement> FREE_BRANCH_INFO;

    @FindBy(css = ".branch-management  tbody > tr > td:nth-child(1)")
    List<WebElement> BRANCH_NAME_LIST;

    @FindBy (css = ".branch-management  tbody > tr > td:nth-child(3)")
    List<WebElement> BRANCH_ADDRESS_LIST;
    
    By loc_btnEdit = By.cssSelector("[data-testid='edit-branch-button']");
    
    By loc_lblBranchCodeInDialog = By.id("name");
    By loc_chkShowBranchOnSF = By.xpath("//input[@id='checkbox-hideOnStoreFrontGroup-undefined']/following-sibling::label");
    By loc_btnUpdate = By.cssSelector(".branch-modal__footer button.gs-button__green");
    
    By loc_btnRenewExpiredBranch = By.xpath("(//div[contains(@class,'uik-widget-title__wrapper')])[2]//button");
    
    By loc_blkExpiredBranch = By.cssSelector(".bg-expired-branch");
    
    By loc_btnActivateBranchToggle = By.xpath("(//input[@class='uik-checkbox__checkbox'])[2]/parent::*");
}
