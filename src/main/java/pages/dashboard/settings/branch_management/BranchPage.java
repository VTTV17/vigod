package pages.dashboard.settings.branch_management;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class BranchPage extends BranchElement {
	final static Logger logger = LogManager.getLogger(BranchPage.class);
	
    WebDriverWait wait;
	UICommonAction commonAction;
    
    public BranchPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

	public BranchPage navigate() {
		commonAction.clickElement(BRANCH_MANAGEMENT_MENU);
		logger.info("Navigated to Branch Management tab.");
		return this;
	}

	public BranchPage clickAddBranch() {
		if (commonAction.isElementVisiblyDisabled(ADD_BRANCH_BTN)) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(ADD_BRANCH_BTN));
			return this;
		}
		commonAction.clickElement(ADD_BRANCH_BTN);
		logger.info("Clicked on Add Branch button.");
		return this;
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

	/**
	 * <p>
	 * Get info of free branch
	 * <p>
	 * Example: List<String> info = getFreeBranchInfo()
	 * @return a List with the 0-indexed element containing the branch name, 1-indexed element denoting branch code,
	 * 2-indexed element denoting branch location, 3-indexed element denoting branch status.
	 */	
    public List<String> getFreeBranchInfo() {
    	waitElementList(BRANCH_NAME_LIST);
    	List<String> info = new ArrayList<>();
    	for (WebElement el:FREE_BRANCH_INFO) {
    		String rawInfo = commonAction.getText(el);
    		if (rawInfo.length()>0) info.add(rawInfo);
    	}
    	return info;
    }    
    
    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToAddBranch(String permission) {
    	navigate();
    	waitElementList(BRANCH_NAME_LIST);
    	clickAddBranch();
    	boolean flag = new ConfirmationDialog(driver).isConfirmationDialogDisplayed();
		if (permission.contentEquals("A")) {
			new ConfirmationDialog(driver).clickCancelBtn();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(flag);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       
}