package pages.dashboard.products.all_products.conversion_unit;

import org.openqa.selenium.By;

class ConversionUnitElement {

    /* Without variation config */
    By withoutVariationSelectUnitBtn = By.cssSelector(".btn-header-wrapper > .gs-button__green--outline");
     By withoutVariationSaveBtn = By.cssSelector(".btn-header-wrapper > .gs-button__green");
    By withoutVariationUnitTextBox = By.cssSelector("#unit-0");
    String unitLocator = "//*[text() = '%s']";
    By withoutVariationQuantity = By.cssSelector("[name *= quantity]");
    /* Variation config */
    By selectVariationBtn = By.cssSelector(".gs-button__green--outline");
    By variationSaveBtn = By.cssSelector(".gs-button__green");
    By selectVariationPopup = By.cssSelector(".modal-content");
    By variationOptions = By.cssSelector("input[name = variationUnit]");
    By saveBtnOnSelectVariationPopup = By.cssSelector(".modal-footer > .gs-button__green");
    By variationConfigureBtn = By.cssSelector(".conversion-configure > .gs-button__blue--outline");
    By selectUnitBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green--outline");
    By saveBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green");
    By unitTextBoxOnSetupVariationConversionUnitPage = By.cssSelector("#unit-0");
    By quantityOnSetupVariationConversionUnitPage = By.cssSelector("[name *= quantity]");
    /* UI element */
    By configureText = By.cssSelector(".gs-widget__content .bg-light-white button");

    By goBackToProductDetailLinkText = By.xpath("//*[contains(@class, 'gs-button__green--outline')]/parent::div/preceding-sibling::*");
    By withoutVariationPageTitle = By.cssSelector(".container-fluid h6");
    By variationPageTitle = By.cssSelector(".container-fluid h6, .container-fluid h5");
    By saveText = By.cssSelector(".gs-button__green");
    By noConfig = By.cssSelector(".bg-white > p");
    /* UI without variation */
    By withoutVariationSelectUnitText = By.cssSelector(".gs-button__green--outline");
    By withoutVariationCancelText = By.cssSelector(".gs-button__gray--outline");
    /**
     * <p>0: Unit</p>
     * <p>1: Quantity</p>
     */
    By withoutVariationSetupConversionUnitTable = By.cssSelector(".border label");
    By withoutVariationUnitPlaceholder = By.cssSelector("#unit-0");
    By withoutVariationNoResult = By.cssSelector(".search-list > p");
    By withoutVariationAddText = By.cssSelector(".icon_add_unit");

    /**
     * <p>0: Conversion unit</p>
     * <p>1: SKU</p>
     * <p>2: Barcode</p>
     * <p>3: Cost price</p>
     * <p>4: Listing price</p>
     * <p>5: Selling price</p>
     * <p>6: Stock</p>
     * <p>7: Weight</p>
     * <p>8: Length</p>
     * <p>9: Width</p>
     * <p>10: Height</p>
     */
    By withoutVariationAliasTable = By.cssSelector(".table-conversion-list > thead th");
    /* UI variation */
    By variationSelectVariationText = By.cssSelector(".gs-button__green--outline");
    By titleOfSelectVariationPopup = By.cssSelector(".modal-title");
    By okTextOnSelectVariationPopup = By.cssSelector(".modal-footer .gs-button__green");
    By cancelTextOnSelectVariationPopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By deleteTextOnSetupConversionUnitTable = By.cssSelector(".action-variation > span:nth-child(1)");
    By editTextOnSetupConversionUnitTable = By.cssSelector(".action-variation > span:nth-child(2)");
    By configureTextOnSetupConversionUnitTable = By.cssSelector(".gs-button__blue--outline");
    By goBackToSetupConversionUnitLinkText = By.cssSelector(".gs-fake-link");
    By setupVariationConversionUnitPageTitle = By.cssSelector(".conversion-unit-wrapper h5");
    By selectUnitTextOnSetupVariationConversionUnit = By.cssSelector(".gs-button__green--outline");
    By cancelTextOnSetupVariationConversionUnit = By.cssSelector(".gs-button__gray--outline");
    By saveTextOnSetupVariationConversionUnit = By.cssSelector(".gs-button__green");
    By setupVariationConversionUnitTable = By.cssSelector(".border label");
    By unitPlaceholderOnSetupVariationConversionUnitTable = By.cssSelector("#unit-0");
    By noResultTextOnSetupVariationConversionUnitTable = By.cssSelector(".search-list > p");
    By addTextOnSetupVariationConversionUnitTable = By.cssSelector(".icon_add_unit");
    /**
     * <p>0: Conversion unit</p>
     * <p>1: SKU</p>
     * <p>2: Barcode</p>
     * <p>3: Cost price</p>
     * <p>4: Listing price</p>
     * <p>5: Selling price</p>
     * <p>6: Stock</p>
     * <p>7: Weight</p>
     * <p>8: Length</p>
     * <p>9: Width</p>
     * <p>10: Height</p>
     */
    By variationAliasTable = By.cssSelector(".table-conversion-list > thead th");
}
