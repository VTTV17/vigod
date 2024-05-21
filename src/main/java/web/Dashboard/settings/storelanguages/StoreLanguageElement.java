package web.Dashboard.settings.storelanguages;

import org.openqa.selenium.By;

public class StoreLanguageElement {

	By loc_tabStoreLanguage = By.cssSelector("li:nth-child(9) > a.nav-link");
	By loc_btnAddLanguage = By.cssSelector(".languages-setting .gs-button__green");
	By loc_dlgAddLanguage = By.cssSelector(".modal-dialog.modal-change");
	
	String languageTableRow = "//div[contains(@class,'translated-languages__wrapper')]//tbody/tr";
	By loc_tblLanguageRow = By.xpath(languageTableRow);
	
	String languageNameRowByName = "/td[position()=1 %s]";
	
	String publishBtnByLanguage = languageTableRow + languageNameRowByName.formatted("and text()='%s'") + "/parent::*/td[position()=2]/span[contains(.,'Xuất') or contains(.,'Publish')]";
	String unpublishBtnByLanguage = languageTableRow + languageNameRowByName.formatted("and text()='%s'") + "/parent::*/td[position()=2]/span[contains(.,'xuất') or contains(.,'Unpublish')]";
	String removeBtnByLanguage = languageTableRow + languageNameRowByName.formatted("and text()='%s'") + "/parent::*/td[position()=3]//i[contains(@style,'icon-delete')]";
	By removeBtn = By.xpath(languageTableRow + languageNameRowByName.formatted("") + "/parent::*/td[position()=3]//i[contains(@style,'icon-delete') and not(contains(@class,'invisible'))]");
	
	By loc_btnTranslation = By.xpath(languageTableRow+"/td[3]//button");
	By loc_btnSaveTranslation = By.cssSelector(".language-translation-header button.gs-button__green");
	
	By loc_ddlAddLanguages = By.cssSelector(".modal-body .uik-select__placeholderEmpty");
	String ddvAddLanguageByName = "//*[@class='uik-select__optionContent']/div[@class='uik-select__label' and text()='%s']";
	
	By loc_btnRenew = By.cssSelector(".translated-languages__wrapper button.gs-button__green");
	By loc_btnChangeLanguage = By.cssSelector(".languages-setting button.gs-button__white");
	
	By ddvSelectDefaultLanguage = By.xpath("//*[@class='uik-select__optionContent']/div[@class='uik-select__label']");
}
