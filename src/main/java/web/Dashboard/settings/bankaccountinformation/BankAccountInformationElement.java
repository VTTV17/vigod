package web.Dashboard.settings.bankaccountinformation;

import org.openqa.selenium.By;

public class BankAccountInformationElement {

	By loc_tabBankAccountInfo = By.cssSelector("li:nth-child(4) > a.nav-link");
	By loc_ddlCountry = By.id("countryCode");
	By loc_txtFullName = By.id("nameInside");
	By loc_txtTaxCode = By.id("idCard");
	By loc_txtAccountHolder = By.id("nameHolderInside");
	By loc_txtBankAccountNumber = By.id("accountNumberInside");
	By loc_txtBankName = By.id("bankId");
	By loc_ddlCityProvince = By.id("region");
	By loc_txtBranchName = By.id("branchName");
	By loc_btnSave = By.cssSelector(".gs-button__blue");
	
}
