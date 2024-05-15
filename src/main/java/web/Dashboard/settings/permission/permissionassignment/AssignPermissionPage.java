package web.Dashboard.settings.permission.permissionassignment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class AssignPermissionPage {

	final static Logger logger = LogManager.getLogger(AssignPermissionPage.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	AssignPermissionPageElement elements;

	public AssignPermissionPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new AssignPermissionPageElement();
	}
	
	public AssignPermissionPage clickAddStaffBtn() {
		commonAction.click(elements.loc_btnAddStaff);
		logger.info("Clicked 'Aff Staff' button.");
		return this;
	}	
	
	public AssignPermissionPage inputSelectStaffSearchTerm(String nameOrEmail) {
		commonAction.inputText(elements.loc_txtSelectStaffSearch, nameOrEmail);
		logger.info("Input staff name/mail to search: " + nameOrEmail);
		commonAction.sleepInMiliSecond(1000, "Wait after inputing search term");
		return this;
	}	
	
	//Temporary function. Will think of a better to handle this
	public AssignPermissionPage selectFirstStaff() {
		commonAction.click(elements.loc_chkStaff);
		logger.info("Clicked first staff.");
		return this;
	}	
	
	public AssignPermissionPage clickAddBtn() {
		new ConfirmationDialog(driver).clickGreenBtn();
		logger.info("Clicked 'Add' button in 'Assign Staff To Group' dialog.");
		return this;
	}	
	
	public AssignPermissionPage removeStaffFromAssignmentList(String staffMail) {
		commonAction.click(By.xpath(elements.loc_btnSpecificDeleteIcon.formatted(staffMail)));
		logger.info("Removing '%s' from assignment list".formatted(staffMail));
		return this;
	}		
	
}
