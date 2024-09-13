package web.Dashboard.products.all_products.crud.conversion_unit;

import org.openqa.selenium.By;

class ConversionUnitElement {
    /* Without variation config */
    By withoutVariationSelectUnitBtn = By.cssSelector(".gs-content-header-right-el .gs-button__green--outline");
     By withoutVariationSaveBtn = By.cssSelector(".gs-content-header-right-el .gs-button__green");
    By withoutVariationUnitTextBox = By.cssSelector("#unit-0");
    String unitLocator = "//*[text() = '%s']";
    By withoutVariationQuantity = By.cssSelector("[name *= quantity]");
    /* Variation config */
    By selectVariationBtn = By.cssSelector(".gs-button__green--outline");
    By variationSaveBtn = By.cssSelector(".gs-button__green");
    By selectVariationPopup = By.cssSelector(".modalSelectVariation");
    String variationLocator = "//div[* = '%s'][@class = 'variation-name']/div/input";
    By saveBtnOnSelectVariationPopup = By.cssSelector(".modal-footer > .gs-button__green");
    By variationConfigureBtn = By.cssSelector(".conversion-configure > .gs-button__blue--outline");
    By selectUnitBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green--outline");
    By saveBtnOnSetupVariationConversionUnitPage = By.cssSelector(".btn-header-wrapper > .gs-button__green");
    By unitTextBoxOnSetupVariationConversionUnitPage = By.cssSelector("#unit-0");
    By quantityOnSetupVariationConversionUnitPage = By.cssSelector("[name *= quantity]");
}
