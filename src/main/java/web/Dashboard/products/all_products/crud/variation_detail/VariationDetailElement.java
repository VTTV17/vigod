package web.Dashboard.products.all_products.crud.variation_detail;

import org.openqa.selenium.By;

public class VariationDetailElement {
    By loc_txtProductVersionName = By.cssSelector("#variationName");
    By loc_chkReuse = By.cssSelector(".des-header > div > label > input");
    By loc_rtfDescription = By.cssSelector(".fr-element");
    By loc_btnSave = By.xpath("(//*[text() = 'Lưu' or text() = 'Save'])[1]/parent::div/parent::button");
    By loc_btnDeactivate = By.xpath("(//*[text() = 'Ngừng bán' or text() = 'Deactivate' or text() = 'Bán ngay' or text() = 'Activate'])[1]/parent::div/parent::button");
    By loc_btnEditTranslation = By.xpath("(//*[text() = 'Sửa bản dịch' or text() = 'Edit Translation'])[1]/parent::div/parent::button");
    By loc_dlgEditTranslation = By.cssSelector(".modal-content");
    By loc_dlgEditTranslation_selectedLanguage = By.cssSelector(".uik-select__valueRenderedWrapper .text-truncate");
    String str_dlgEditTranslation_languageInDropdown = "//*[@class = 'uik-select__label']//*[text()='%s']";
    By loc_dlgEditTranslation_variationName = By.cssSelector("#informationName");
    By loc_dlgEditTranslation_variationDescription = By.cssSelector(".modal-body .fr-element");
    By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_chkReUseParentAttribution = By.cssSelector("[name='cbx-resue-attribute']");
    By loc_lnkAddAttribution = By.cssSelector(".uik-checkbox__wrapper +.gs-fake-link");
    By loc_icnDeleteAttribution = By.cssSelector(".attribute-item-row button");
    By loc_txtAttributionName = By.cssSelector("[name *='input-attribute-name']");
    By loc_txtAttributionValue = By.cssSelector("[name='attribute-value']");
    By loc_chkDisplayAttribution = By.cssSelector("td input.uik-checkbox__checkbox");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
