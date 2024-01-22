package web.Dashboard.products.all_products.wholesale_price;

import org.openqa.selenium.By;

public class WholesaleProductElement {

    /* Without variation config*/
    By withoutVariationAddWholesalePricingBtn = By.cssSelector(".wholesale-btn-group-header .gs-button__gray--outline");
    By saveBtn = By.cssSelector(".wholesale-btn-group-header > .gs-button__green");
    By withoutVariationBuyFrom = By.cssSelector("[name ^= 'buyFrom']");
    By withoutVariationWholesalePrice = By.xpath("//*[contains(@name, 'buyFrom')]/parent::div/parent::div//following-sibling::div[@class='wholesale-grid-item'][1]//input");
    By withoutVariationSegmentDropdown = By.cssSelector(".dropdown-search-checkbox-custom");
    By allCustomerCheckbox = By.cssSelector("#ALL");
    String segmentLocator = "[id = '%s']";
    String segmentText = "[for = '%s']";
    /* Variation config */
    By variationAddVariationBtn = By.cssSelector(".wholesale-btn-group-header > .gs-button__gray--outline");
    By addVariationPopup = By.cssSelector(".modal-content");
    String variationLocator = "//*[text() = '%s']//ancestor::label/input";
    By okBtnOnAddVariationPopup = By.cssSelector(".footer-btn .gs-button__green");
    By variationAddWholesalePricingBtn = By.cssSelector(".border-bottom > .wholesale-group-header .gs-fake-link:nth-child(1)");
    By variationValue = By.cssSelector(".border-bottom > .wholesale-group-header > div > div");
    By variationBuyFrom = By.cssSelector("[name^='buyFrom-']");
    By variationWholesalePrice = By.xpath("//*[contains(@name, 'buyFrom')]/parent::div/parent::div//following-sibling::div[@class='wholesale-grid-item'][1]//input");
    By variationSegmentDropdown = By.cssSelector(".dropdown-search-checkbox-custom");

    /* Header */
    By goBackToProductDetailLinkText = By.cssSelector(".wholesale-group-header > span");
    By pageTitle = By.cssSelector(".container-fluid h5");
    By withoutVariationAddWholeSalePricingText = By.cssSelector(".wholesale-group-header .gs-button__gray--outline");
    By noConfigText = By.cssSelector(".container-fluid p");
    By addVariationText = By.cssSelector(".wholesale-group-header .gs-button__gray--outline");
    By saveText = By.cssSelector(".wholesale-group-header .gs-button__green");

    /**
     * <p>0: wholesale title</p>
     * <p>1: buy from</p>
     * <p>2: price per item</p>
     * <p>3: discount</p>
     * <p>4: customer segment</p>
     * <p></p>
     */
    By withoutVariationSetupWholesalePriceTable = By.cssSelector(".wholesale-grid-item label");
    By segmentSearchPlaceholder = By.cssSelector(".search-box input");
    By allCustomerTextInDropdown = By.xpath("(//*[@class ='options-checked'])[1]");
    By variationAddWholesalePricingText = By.cssSelector(".wholesale-group-header.ml-2 span:nth-child(1)");
    By variationDeleteText = By.cssSelector(".wholesale-group-header.ml-2 span:nth-child(2)");
    By variationEditText = By.cssSelector(".wholesale-group-header.ml-2 span:nth-child(3)");
    By variationSetupWholesalePriceTable = By.xpath("(//*[contains(@class, 'wholesale-grid-wrapper')])[1]//*[@class = 'wholesale-grid-item']//label");
    By segmentTooltips = By.cssSelector(".tippy-tooltip-content");
    /* Add variation popup */
    By titleOfAddVariationPopup = By.cssSelector(".modal-title");
    By selectAllLabelOnAddVariationPopup = By.cssSelector(".modal-body > section > div > label");
    By okTextOnAddVariationPopup = By.cssSelector(".footer-btn > .gs-button__green");
    By cancelTextOnAddVariationPopup = By.cssSelector(".footer-btn > .gs-button__gray--outline");
}