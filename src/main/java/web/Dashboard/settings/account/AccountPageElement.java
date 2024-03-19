package web.Dashboard.settings.account;

import org.openqa.selenium.By;

public class AccountPageElement {

	By loc_tabAccount = By.cssSelector("li:nth-child(1) > a.nav-link");
	By loc_btnSeePlan = By.cssSelector(".see-plan button");
	By loc_btnRenew = By.cssSelector(".current__plan_information .gs-button__green");
	
	String loc_tmpRecords = "//*[contains(@class, 'current__plan_information')]//*[contains(@class,'gs-widget__content')]";
	String loc_tmpPlanColumn = "(%s)[index]//*[@class='account__block']//b".formatted(loc_tmpRecords);
	
	By loc_txtFirstName = By.id("firstName");
	By loc_txtLastName = By.id("lastName");
	By loc_txtEmail = By.id("email");
	By loc_txtPhone = By.id("phoneNumber");
	By loc_btnSaveAccountInfo = By.cssSelector(".account__information .setting_btn_save");
	By loc_txtCurrentPassword = By.id("currentPassword");
	By loc_txtNewPassword = By.id("newPassword");
	By loc_txtConfirmPassword = By.id("confirmPassword");
	By loc_btnSaveResetPassword = By.cssSelector(".reset-password__information .setting_btn_save");
}
