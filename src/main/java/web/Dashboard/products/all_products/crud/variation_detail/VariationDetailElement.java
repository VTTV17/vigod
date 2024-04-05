package web.Dashboard.products.all_products.crud.variation_detail;

import org.openqa.selenium.By;

public class VariationDetailElement {
    By loc_txtProductVersionName = By.cssSelector("#variationName");
    By loc_chkReuse = By.cssSelector(".des-header > div > label > input");
    By loc_rtfDescription = By.cssSelector(".fr-element");
    By loc_btnSave = By.cssSelector(".gss-content-header .gs-button__green");
    By loc_btnEditTranslation = By.cssSelector(".gss-content-header span > .gs-button__gray--outline");
    By loc_btnDeactivate = By.cssSelector(".gss-content-header .gs-button__yellow--outline, .gs-button__cyan--outline");
    By loc_dlgEditTranslation = By.cssSelector(".modal-content");
    By loc_dlgEditTranslation_selectedLanguage = By.cssSelector(".uik-select__valueRenderedWrapper .text-truncate");
    String str_dlgEditTranslation_languageInDropdown = "//*[@class = 'uik-select__label']//*[text()='%s']";
    By loc_dlgEditTranslation_variationName = By.cssSelector("#informationName");
    By loc_dlgEditTranslation_variationDescription = By.cssSelector(".modal-body .fr-element");
    By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgEditTranslation_btnClose = By.cssSelector(".modal-header > button");
    /* UI */
    By loc_lblVariationStatus = By.cssSelector(".toolbar__product-status");
    By loc_lblSave = By.cssSelector(".gss-content-header .gs-button__green");
    By loc_lblCancel = By.cssSelector(".gss-content-header .gs-button__green ~.gs-button__gray--outline");
    By loc_lblDeactivate = By.cssSelector(".gss-content-header .gs-button__yellow--outline, .gs-button__cyan--outline");
    By loc_lblProductVersion = By.xpath("//*[@for='productName']/parent::div/parent::div/preceding-sibling::div");
    By loc_lblProductVersionName = By.cssSelector("[for='productName']");
    By loc_lblDescription = By.cssSelector("[for='productDescription']");
    By loc_lblReuseDescription = By.xpath("//*[@for='productDescription']/following-sibling::div");
    By loc_lblImages = By.xpath("//*[@for='productName']/parent::div/parent::div/parent::div/following-sibling::div[1]//h3");
    By loc_lblPricing = By.xpath("//*[@for='productName']/parent::div/parent::div/parent::div/following-sibling::div[2]//h3");
    By loc_lblListingPrice = By.cssSelector("[for='productPrice']");
    By loc_lblSellingPrice = By.cssSelector("[for='productDiscountPrice']");
    By loc_lblNumberOfVariations = By.cssSelector(".variation-number");
    By loc_lblBranch = By.xpath("//*[contains(@class, 'branch-list')]/parent::div/preceding-sibling::div");
    By loc_lblVariation = By.xpath("//*[contains(@class, 'variation-list')]/parent::div/parent::div/preceding-sibling::div");
    By loc_lblWarehousing = By.xpath("//*[@for='remaining']/parent::div/parent::div/preceding-sibling::div/h3");
    By loc_lblUpdateStock = By.xpath("//*[@for='remaining']/parent::div/parent::div/preceding-sibling::div/span");
    By loc_lblSKU = By.cssSelector("[for='variationSKU']");
    By loc_lblBarcode = By.cssSelector("[for='barcode']");
    By loc_lblRemainingStock = By.cssSelector("[for='remaining']");
    By loc_lblSoldCount = By.cssSelector("[for='soldItem']");
    By loc_dlgEditTranslationTitle = By.cssSelector(".product-translate__titleHeader > p");
    By loc_dlgEditTranslation_lblInformation = By.cssSelector(".product-translate__titleBody > h3");
    By loc_dlgEditTranslation_lblName = By.cssSelector("[for='informationName']");
    By loc_dlgEditTranslation_lblDescription = By.xpath("//*[@for='informationName']/parent::div/following-sibling::div/label");
    By loc_dlgEditTranslation_lblSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgEditTranslation_lblCancel =  By.cssSelector(".modal-footer .gs-button__white");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
