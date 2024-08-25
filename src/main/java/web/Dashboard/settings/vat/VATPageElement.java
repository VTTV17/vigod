package web.Dashboard.settings.vat;

import org.openqa.selenium.By;

public class VATPageElement {

	By loc_tabVAT = By.cssSelector("li:nth-child(8) > a.nav-link");
	By loc_btnAddVATInfo = By.cssSelector(".VAT .gs-button__green");
	
	By loc_dlgAddVAT = By.cssSelector(".modal-dialog.VATmodal");
	By loc_txtVAT = By.id("name");
	By loc_txtVATRate = By.id("rate");
	By loc_txtVATDescription = By.id("description");
	
	By loc_rdoSellingVATType = By.xpath("//input[@id='radio-taxType-SELL']/parent::*");
	By loc_rdoImportGoodsVATType = By.xpath("//input[@id='radio-taxType-IMPORT_GOODS']/parent::*");
	
	By loc_ddlDefaultVAT = By.cssSelector(".form-group button.uik-select__valueRendered");
	String ddvDefaultVATLocator = "//*[contains(@class,'uik-select__optionList')]/button//div[contains(@class,'uik-select__label') and text()='%s']";
	
	By loc_chkShowInWebApp = By.xpath("(//*[@class='uik-checkbox__wrapper custom-check-box'])[1]");
	By loc_chkApplyAfterDiscount = By.xpath("(//*[@class='uik-checkbox__wrapper custom-check-box'])[2]");
	
	String VATRowLocator = "//div[contains(@class,'VAT')]//tbody/tr/td[position()=2 %s]";
	
	String specificVATDeleteIconLocator = VATRowLocator + "/parent::*/td[last()]//*[contains(@style,'icon-delete')]";
	
	By loc_tblVATRows = By.xpath(VATRowLocator.formatted(""));
}
