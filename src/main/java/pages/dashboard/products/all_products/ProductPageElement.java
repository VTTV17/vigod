package pages.dashboard.products.all_products;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ProductPageElement {
    By completedPopup = By.cssSelector(".modal-success");
    By selectedLanguage = By.cssSelector(".language-selector .uik-select__valueWrapper");
    String languageLocator = "//*[@class = 'uik-select__label']/span[text()= '%s']";
    By createProductBtn = By.cssSelector(".product-list-page > div > div > div >  button.gs-button__green");
    By loc_btnPrintBarcode = By.xpath("(//div[contains(@class,'gs-content-header-right-el d-flex')]//button)[4]");
    /* General product information */
    By productName = By.cssSelector("input#productName");
    By productDescription = By.cssSelector("div.fr-wrapper > div");
    By removeProductImageBtn = By.cssSelector(".image-widget__btn-remove");
    By productImage = By.cssSelector(".image-drop-zone input");
    By vatDropDown = By.cssSelector(".form-group .uik-select__valueWrapper");
    By noTax = By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='Không áp dụng thuế'] | //*[@class = 'uik-select__optionContent']/div[text()='Tax does not apply']");
    String othersTaxLocator = "//*[@class = 'uik-select__optionContent']/div[text()='%s']";
    By removeCollectionBtn = By.xpath("//*[@class = 'product-form-collection-selector2__selected-item-name']/following-sibling::*");
    By collectionSearchBox = By.cssSelector(".product-form-collection-selector2 input");
    String collectionLocator = "//div[text() = '%s']";
    By loc_dlgUpdateSKU = By.cssSelector(".product-multiple-branch-sku_editor_modal");
    By productSKUWithoutVariation = By.cssSelector("#productSKU,[class *=--n2] > div:nth-child(3) .align-items-center > span");
    By loc_lblUpdateStock = By.xpath("//div[contains(@class,'gs-witget-warehousing')]//div[contains(@class,'uik-widget-title')]//span[@class='gs-fake-link ']");
    By manageByInventory = By.cssSelector("#manageInventory");
    By priorityTextBox = By.cssSelector("[name = productPriority]");
    By productWeight = By.cssSelector("[for ='productWeight'] +* input");
    By productLength = By.cssSelector("[for ='productLength'] +* input");
    By productWidth = By.cssSelector("[for ='productWidth'] +* input");
    By productHeight = By.cssSelector("[for ='productHeight'] +* input");
    By platformApp = By.cssSelector("[name = onApp]");
    By platformWeb = By.cssSelector("[name = onWeb]");
    By platformInStore = By.cssSelector("[name = inStore]");
    By platformGoSocial = By.cssSelector("[name = inGosocial]");
    By saveBtn = By.cssSelector(".gss-content-header .btn-save");
    By deactivateBtn = By.cssSelector(".gss-content-header .gs-button__yellow--outline");
    By deleteBtn = By.cssSelector(".gss-content-header .gs-button__red--outline");
    By okBtnOnConfirmDeletePopup = By.cssSelector(".modal-footer .gs-button__green");
    By popup = By.cssSelector(".modal-content");
    By loc_dlgNotification = By.cssSelector("[data-sherpherd='tour-guide-alert-modal']");
    By failPopup = By.cssSelector(".modal-danger");
    By closeBtnOnNotificationPopup = By.cssSelector("[data-testid='closeBtn']");
    By seoTitle = By.cssSelector("input#seoTitle");
    By seoDescription = By.cssSelector("input#seoDescription");
    By seoKeywords = By.cssSelector("input#seoKeywords");
    By seoURL = By.cssSelector("input#seoUrl");
    /* Without variation product */
    By productListingPriceWithoutVariation = By.xpath("//label[@for='productDiscountPrice']//parent::div//preceding-sibling::div//label//following-sibling::div//input");
    By productSellingPriceWithoutVariation = By.cssSelector("[for = 'productDiscountPrice'] +* input");
    By productCostPriceWithoutVariation = By.xpath("//label[@for='productDiscountPrice']//parent::div//following-sibling::div//label[@for = 'productPrice'] //following-sibling::div//input");
    By displayIfOutOfStockCheckbox = By.xpath("//*[@name='showOutOfStock']/parent::div/preceding-sibling::label[2]/input");
    By hideRemainingStockOnOnlineStoreCheckbox = By.xpath("//*[@name='isHideStock']/parent::div/preceding-sibling::label[2]/input");
    By showAsListingProductOnStoreFrontCheckbox = By.xpath("//*[@name='enabledListing']/parent::div/preceding-sibling::label[2]/input");
    By branchStockWithoutVariation = By.cssSelector(".branch-list-stock__wrapper__row  input");
    By loc_dlgUpdateStock = By.cssSelector(".product-multiple-branch-stock_editor_modal");
    By loc_dlgAddIMEI = By.cssSelector(".managed-inventory-modal");
    By branchDropdownOnAddIMEIPopup = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By selectAllBranchesCheckboxOnAddIMEIPopup = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
    By textBoxOnAddIMEIPopup = By.cssSelector(".input-code input");
    By saveBtnOnAddIMEIPopup = By.cssSelector(".modal-footer > .gs-button__green");
    /* Variation product */
    By addVariationBtn = By.cssSelector("div:nth-child(4) > div.gs-widget__header > span");
    By variationName = By.cssSelector("div.first-item > div > div > input");
    By variationValue = By.cssSelector(".second-item .css-nwjfc > input");
    String variationLocator = "//div[text()='%s']";
    By selectAllCheckboxOnVariationTable = By.cssSelector(".product-form-variation-selector__table  th:nth-child(1) input");
    By selectActionLinkTextOnVariationTable = By.cssSelector("th .gs-fake-link");
    /**
     * <p>0: Update Price</p>
     * <p>1: Update Stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update Image</p>
     */
    By actionOnVariationTable = By.cssSelector(".uik-menuDrop__list > button");
    By stockQuantityOnVariationTable = By.xpath("//*[contains(@name, 'stock')]|//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[2]/span");
    By skuOnVariationTable = By.xpath("//*[contains(@name, 'sku')]|//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span");
    By textBoxOnUpdateSKUPopup = By.cssSelector(".justify-content-center input");
    By uploadImageBtnOnVariationTable = By.cssSelector("td > img");
    By uploadBtnOnUpdateImagePopup = By.cssSelector(".modal-content [type = file]");
    By listingPriceOnUpdatePricePopup = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'orgPrice')]//parent::div//parent::div//preceding-sibling::input");
    By sellingPriceOnUpdatePricePopup = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'discountPrice')]//parent::div//parent::div//preceding-sibling::input");
    By costPriceOnUpdatePricePopup = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'costPrice')]//parent::div//parent::div//preceding-sibling::input");
    By updateBtnOnPopup = By.cssSelector(".modal-footer .gs-button__green");
    By cancelBtnOnPopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By branchDropdownOnUpdateStockPopup = By.cssSelector(".gs-dropdown-multiple-select__drop-header .uik-select__valueWrapper");
    By selectAllBranchesCheckboxOnUpdateStockPopup = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
    By changeTabOnUpdateStockPopup = By.cssSelector(".modal-body  div > div > .gs-button:nth-child(2)");
    By quantityTextBoxOnUpdateStockPopup = By.cssSelector(".modal-body  .quantity-input-field > input");
    By stockQuantityTextBoxOnUpdateStockPopup = By.cssSelector(".input-stock  > input");
    /* Product list page */
    By searchBox = By.cssSelector(".d-mobile-none .uik-input__input");
    By productId = By.cssSelector("tbody > tr > td:nth-child(1) > span > b");
    /* Tien */
    By loc_btnDeleteVariation = By.xpath("//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/following-sibling::*/button");
    @FindBy(xpath = "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/parent::*/following-sibling::*/button")
    List<WebElement> DELETE_DEPOSIT_BTN;
    By PRINT_BARCODE_MODAL = By.cssSelector(".modal-content.product-list-barcode-printer");
    @FindBy(xpath = "//input[@class='uik-checkbox__checkbox' and @name='enabledListing']/ancestor::div[contains(@class,'uik-widget__wrapper')]/following-sibling::*/div[1]//span")
    WebElement ADD_DEPOSIT_BTN;
    By loc_dlgEditTranslation = By.cssSelector(".modal.fade.show");
    By loc_dlgEditTranslation_selectedLanguage = By.cssSelector(".product-translate .text-truncate");
    String str_dlgEditTranslation_languageInDropdown = "//*[@class = 'uik-select__label']//*[text()='%s']";
    By productNameOnEditTranslationPopup = By.cssSelector("#informationName");
    By productDescriptionOnEditTranslationPopup = By.cssSelector(".modal-body .fr-element");
    By variationNameOnEditTranslationPopup = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[1]/descendant::input");
    By variationValueOnEditTranslationPopup = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[2]/descendant::input");
    By seoTitleOnEditTranslationPopup = By.cssSelector(".modal-body #seoTitle");
    By seoDescriptionOnEditTranslationPopup = By.cssSelector(".modal-body #seoDescription");
    By seoKeywordsOnEditTranslationPopup = By.cssSelector(".modal-body #seoKeywords");
    By seoURLOnEditTranslationPopup = By.cssSelector(".modal-body #seoUrl");
    By saveBtnOnEditTranslationPopup = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_dlgEditTranslation_icnClose = By.cssSelector(".modal.fade.show .close");
    By addConversionUnitCheckbox = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
    @Getter
    By addWholesalePricingCheckbox = By.cssSelector(".uik-checkbox__wrapper > [name='enabledListing']");
    @Getter
    By configureWholesalePricingBtn = By.xpath("//label/*[@name = 'enabledListing']//ancestor::div[contains(@class,'gs-widget__header')]/following-sibling::div//button");
    /* Conversion unit config */
    @Getter
    By addConversionUnitCheckBox = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
    @Getter
    By configureConversionUnitBtn = By.xpath("//*[@name = 'conversionUnitCheckbox']//ancestor::div[contains(@class, 'border-bottom')]/following-sibling::div//button");
    @Getter
    By notSupportConversionUnitForProductManagedByIMEI = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(5) small");
    @Getter
    By noConversionUnitConfig = By.cssSelector(".gs-widget__content .bg-light-white p");
    By confirmPopup = By.cssSelector(".modal-content");
    By okBtnOnConfirmPopup = By.cssSelector(".modal-footer .gs-button__green");
    /* UI text element */
    /* Header */
    By goBackToProductListText = By.xpath("//a[@href='/product/list' and not(@name)]");
    By pageTitleText = By.cssSelector(".gss-content-header .gs-page-title");
    By editTranslationText = By.xpath("//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/preceding-sibling::button/div");
    By saveText = By.cssSelector(".gss-content-header .gs-button__green > div");
    By deactivateText = By.cssSelector(".gss-content-header .gs-button__yellow--outline > div");
    By deleteText = By.cssSelector(".gss-content-header .gs-button__red--outline > div");
    By cancelText = By.xpath("//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/following-sibling::button[contains(@class, 'gs-button__gray--outline')]/div");
    /* Product information */
    By productInformationLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(1) > .gs-widget__header > h3");
    By productNameLabel = By.cssSelector("[for = productName]");
    By productBlankErrorMessage = By.xpath("//*[@name='productName']/following-sibling::div");
    By productDescriptionLabel = By.cssSelector("[for = productDescription]");
    /* Upload images */
    By imagesLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(2) > .gs-widget__header > h3");
    By dragAndDropText = By.cssSelector(".image-drop-zone");
    /* Price and VAT */
    By pricingLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(3) > .gs-widget__header > h3");
    By listingPriceLabel = By.xpath("(//*[@for ='productPrice'])[1]");
    By sellingPriceLabel = By.cssSelector("[for = productDiscountPrice]");
    By costPriceLabel = By.xpath("(//*[@for ='productPrice'])[2]");
    By vatLabel = By.cssSelector("label:not([for]).gs-frm-control__title");
    By showAsListingProductOnStoreFrontText = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(3) .uik-checkbox__label");
    /* Variation */
    By variationsLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3");
    By addVariationText = By.xpath("(//*[contains(@class, 'gs-widget__header')]/ span)[1]");
    By variationDescriptionText = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .gs-widget__content > p");
    By variationNameLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(1) > label");
    By variationValueLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(2) > label");
    By variationValuePlaceholder = By.cssSelector(".second-item .css-14el2xx-placeholder");
    // Variation table
    By numberOfSelectedVariationsText = By.xpath("//span[contains(@class,'gs-fake-link')]/ancestor::th/div");
    By selectActionText = By.xpath("//th/div/div/span");
    /**
     * <p>0: Update price</p>
     * <p>1: Update stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update image</p>
     */
    By listActions = By.cssSelector(".uik-menuDrop__list span");
    /**
     * <p>0: Image</p>
     * <p>1: Listing price</p>
     * <p>2: Selling price</p>
     * <p>3: Cost price</p>
     * <p>4: Stock quantity</p>
     * <p>5: SKU</p>
     * <p>6: Barcode</p>
     */
    By variationTableColumnLabel = By.xpath("//th[@class=' align-middle'][1] | //th[contains(@class, 'text-center')]");
    By editSKUTextOnVariationTable = By.xpath("//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span");
    // Update variation price popup
    By titleOfUpdatePricePopup = By.cssSelector(".modal-title");
    By priceDropdownOnUpdatePricePopup = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By priceTypeDropdownOnUpdatePricePopup = By.cssSelector(".uik-select__optionList");
    /**
     * <p>0: Listing price</p>
     * <p>1: Selling price</p>
     * <p>2: Cost price</p>
     */
    By listOfPriceTypeOnUpdatePricePopup = By.cssSelector(".uik-select__optionList span");
    By applyTextOnUpdatePricePopup = By.cssSelector(".modal-body .gs-button__blue");
    /**
     * <p>size() - 3: Listing price</p>
     * <p>size() - 2: Selling price</p>
     * <p>size() - 1: Cost price</p>
     */
    By listOfPriceOnPriceTable = By.cssSelector(".modal-body tr > th");
    By cancelTextOnUpdatePricePopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By updateTextOnUpdatePricePopup = By.cssSelector(".modal-footer .gs-button__green");
    // Update normal variation stock popup
    By titleOfUpdateStockPopup = By.cssSelector(".modal-title");
    /**
     * <p>0: Add stock</p>
     * <p>1: Change stock</p>
     */
    By listActionsOnUpdateStockPopup = By.cssSelector(".modal-body div > div > .gs-button");
    By stockQuantityPlaceholderOnUpdateStockPopup = By.cssSelector("[name= quantity]");
    By actionTypeOnUpdateStockPopup = By.cssSelector(".modal-body strong");
    By cancelTextOnUpdateStockPopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By updateTextOnUpdateStockPopup = By.cssSelector(".modal-footer .gs-button__green");
    // Add IMEI/Serial number popup
    By titleOfAddIMEIPopup = By.cssSelector(".modal-title");
    By branchTextOnAddIMEIPopup = By.cssSelector(".branch > h3");
    By inputIMEIPlaceholderOnAddIMEIPopup = By.cssSelector(".input-code input");
    By productNameLabelOnAddIMEITable = By.cssSelector(".modal-body thead > tr > th:nth-child(1)");
    By cancelTextOnAddIMEIPopup = By.cssSelector(".modal-footer .gs-button__white");
    By saveTextOnAddIMEIPopup = By.cssSelector(".modal-footer .gs-button__green");
    // Update variation SKU popup
    By titleOfUpdateSKUPopup = By.cssSelector(".modal-title");
    By branchDropdownOnUpdateSKUPopup = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By cancelTextOnUpdateSKUPopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By updateTextOnUpdateSKUPopup = By.cssSelector(".modal-footer .gs-button__green");
    // Update variation image popup
    By titleOfUploadImagePopup = By.cssSelector(".modal-title");
    By uploadImagePlaceholderOnUploadImagePopup = By.cssSelector(".modal-body .image-uploader__text");
    By cancelTextOnUploadImagePopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By selectTextOnUploadImagePopup = By.cssSelector(".modal-footer .gs-button__green");
    // edit translation popup
    By titleOfEditTranslationPopup = By.cssSelector(".product-translate__titleHeader > p");
    By informationLabelOnEditTranslationPopup = By.xpath("//*[@for='informationName']/parent::div/preceding-sibling::div");
    By productNameLabelOnEditTranslationPopup = By.cssSelector("[for='informationName']");
    By productDescriptionLabelOnEditTranslationPopup = By.xpath("//*[@for='informationName']/parent::div/following-sibling::div/label");
    By variationLabelOnEditTranslationPopup = By.xpath("//div[@class='row']/preceding::div[@class = 'product-translate__titleBody'][1]");
    By seoSettingLabelOnEditTranslationPopup= By.xpath("//div[@class=' seo-editor']/preceding::div[@class = 'product-translate__titleBody'][1]");
    By livePreviewLabelOnEditTranslationPopup = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div/span)[2]");
    By livePreviewTooltipsOnEditTranslationPopup = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div//*[@data-tooltipped = ''])[2]");
    By seoTitleLabelOnEditTranslationPopup = By.xpath("(//*[@name= 'seoTitle']/parent::div/parent::div/preceding-sibling::div[1]/span)[2]");
    By seoTitleTooltipsOnEditTranslationPopup = By.xpath("(//*[contains(@class,  'seo-editor__live-preview-wrapper')]/following-sibling::div[1]//*[@data-tooltipped])[2]");
    By seoDescriptionLabelOnEditTranslationPopup = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]/span)[2]");
    By seoDescriptionTooltipsOnEditTranslationPopup = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[2]");
    By seoKeywordsLabelOnEditTranslationPopup = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//span)[2]");
    By seoKeywordsTooltipsOnEditTranslationPopup = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[2]");
    By urlLabelOnEditTranslationPopup = By.xpath("(//*[@name= 'seoUrl']/parent::div/preceding-sibling::div[1]//span)[2]");
    By saveTextOnEditTranslationPopup = By.cssSelector(".modal-footer .gs-button__green");
    By cancelTextOnEditTranslationPopup = By.cssSelector(".modal-footer .gs-button__white");
    /* Conversion unit */
    By unitLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(5) > .gs-widget__header > h3 > span");
    By searchUnitPlaceholder = By.cssSelector("#input-search");
    By addConversionUnitLabel = By.xpath("//*[@name='conversionUnitCheckbox']/parent::label/div");
    By conversionUnitTooltips = By.xpath("//*[@name= 'input-search']//ancestor::div[@class = 'gs-dropdown-search-form']/following-sibling::div//*[@data-tooltipped]");
    /* Wholesale product */
    By addWholesalePricingLabel = By.xpath("//*[@name='enabledListing']/parent::label");
    @Getter
    By noWholesaleProductConfigText = By.cssSelector(".gs-widget__content.bg-light-white p");
    @Getter
    By configureText = By.cssSelector(".gs-widget__content.bg-light-white .gs-button__green");
    /* Deposit */
    By depositLabel = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > h3");
    By addDepositText = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > span");
    By depositDescription = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7)  .gs-widget__content > p");
    /* SEO */
    By uiSEOSetting = By.xpath("//div[contains(@class, ' seo-editor')]//div/h3");
    By livePreviewLabel = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div/span)[1]");
    By livePreviewTooltips = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div//*[@data-tooltipped = ''])[1]");
    By seoTitleLabel = By.xpath("(//*[@name= 'seoTitle']/parent::div/parent::div/preceding-sibling::div[1]/span)[1]");
    By seoTitleTooltips = By.xpath("(//*[contains(@class,  'seo-editor__live-preview-wrapper')]/following-sibling::div[1]//*[@data-tooltipped])[1]");
    By seoDescriptionLabel = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]/span)[1]");
    By seoDescriptionTooltips = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[1]");
    By seoKeywordsLabel = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//span)[1]");
    By seoKeywordsTooltips = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[1]");
    By seoURLLabel = By.xpath("(//*[@name= 'seoUrl']/parent::div/preceding-sibling::div[1]//span)[1]");
    /* Sale chanel */
    By saleChannelLabel = By.cssSelector("[class $= --n2] > div:nth-child(1) h3");
    By allSaleChannelTooltips = By.cssSelector(".tippy-tooltip-content");
    By onlineShopIcon = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[1]");
    By gomuaIcon = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[2]");
    By shopeeIcon = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[3]");
    By tiktokIcon = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[4]");
    /* Collections */
    By collectionsLabel = By.cssSelector("[class $= --n2] > div:nth-child(2) h3");
    By searchCollectionPlaceholder = By.cssSelector(".product-form-collection-selector2 input");
    By noCollectionText = By.cssSelector(".product-form-collection-selector2 > .no-content");
    /* Warehousing */
    By warehousingLabel = By.cssSelector("[class $= --n2] > div:nth-child(3) h3");
    By withoutVariationSKULabel = By.xpath("//*[@name='barcode']/parent::div/preceding-sibling::div/label|//*[@for='productSKU']");
    By withoutVariationBarcodeLabel = By.cssSelector("[for = 'barcode']");
    By manageInventoryLabel = By.cssSelector("[for = 'manageInventory']");
    By manageByProductText = By.cssSelector("#manageInventory > [value = PRODUCT]");
    By manageByIMEIText = By.cssSelector("#manageInventory > [value = IMEI_SERIAL_NUMBER]");
    By manageByIMEINoticeText = By.cssSelector(".Notice-product-quantity");
    By withoutVariationStockQuantityLabel = By.cssSelector("[for = 'productQuantity']");
    By stockQuantityApplyAllText = By.xpath("//*[@for = 'productQuantity']/following-sibling::div[@class = 'row']/button/div");
    By remainingStockLabel = By.cssSelector("[for = remaining]");
    By remainingStockValue = By.xpath("//*[@for='remaining']/following-sibling::span");
    By titleOfRemainingStockPopup = By.cssSelector(".modal-title");
    By branchDropdownOnRemainingStockPopup = By.cssSelector(".remaining-sold-item-modal__body__dropdown > button");
    By allBranchesCheckboxOnRemainingStockPopup = By.xpath("//*[contains(@class,'modal-body')]/descendant::label[1]");
    By noBranchErrorMessageOnRemainingStockPopup = By.cssSelector(".remaining-sold-item-modal__body__error");
    By closeBtnOnRemainingPopup = By.cssSelector(".modal-header > .close");
    By soldCountLabel = By.cssSelector("[for = soldItem]");
    By soldCountValue = By.xpath("//*[@for='soldItem']/following-sibling::span");
    By titleOfViewSoldCountPopup = By.cssSelector(".modal-title");
    By branchDropdownOnViewSoldCountPopup = By.cssSelector(".remaining-sold-item-modal__body__dropdown > button");
    By allBranchesCheckboxOnViewSoldCountPopup = By.xpath("//*[contains(@class,'modal-body')]/descendant::label[1]");
    By noBranchErrorMessageOnViewSoldCountPopup = By.cssSelector(".remaining-sold-item-modal__body__error");
    By closeBtnOnViewSoldCountPopup = By.cssSelector(".modal-header > .close");
    By displayIfOutOfStockText = By.xpath("//*[@id='showOutOfStock']/parent::div/preceding-sibling::label/div");
    By hideRemainingStockOnOnlineStoreText = By.xpath("//*[@id='isHideStock']/parent::div/preceding-sibling::label/div");
    /* Package information */
    By packageInformationLabel = By.cssSelector("[class $= --n2] > div:nth-child(4) h3");
    By packageInformationTooltips = By.xpath("//*[contains(@class, 'gs-witget-warehousing')]/following-sibling::div[1]//*[@data-tooltipped]");
    By weightLabel = By.cssSelector("[for = productWeight]");
    By lengthLabel = By.cssSelector("[for = productLength]");
    By widthLabel = By.cssSelector("[for = productWidth]");
    By heightLabel = By.cssSelector("[for = productHeight]");
    By packageNote = By.cssSelector("[class $= --n2] > div:nth-child(4)  p > em");
    /* Priority */
    By priorityLabel = By.cssSelector("[class $= --n2] > div:nth-child(5) h3");
    By priorityTooltips = By.xpath("//*[contains(@class, 'gs-witget-warehousing')]/following-sibling::div[2]//*[@data-tooltipped]");
    By priorityPlaceholder = By.cssSelector("[name = productPriority]");
    /* Platform */
    By platformLabel = By.cssSelector("[class $= --n2] > div:nth-child(6) h3");
    By platformTooltips = By.xpath("//*[contains(@class, 'gs-witget-warehousing')]/following-sibling::div[3]//*[@data-tooltipped]");
    By appLabel = By.xpath("//*[@name='onApp']/following-sibling::div");
    By webLabel =  By.xpath("//*[@name='onWeb']/following-sibling::div");
    By inStoreLabel = By.xpath("//*[@name='inStore']/following-sibling::div");
    By goSocialLabel = By.xpath("//*[@name='inGosocial']/following-sibling::div");
    /* Confirm delete product popup */
    By titleOfConfirmDeleteProductPopup = By.cssSelector(".modal-title");
    By warningMessageOnConfirmDeleteProductPopup = By.cssSelector(".modal-body");
    By okTextOnConfirmDeleteProductPopup = By.cssSelector(".modal-footer .gs-button__green");
    By cancelTextOnDeleteProductPopup = By.cssSelector(".modal-footer .gs-button__gray--outline");
    /* Import product */
    @FindBy(xpath = "(//button[contains(@class,'gs-button__green')])[3]")
    WebElement IMPORT_BTN;
    @FindBy(xpath = "(//button[contains(@class,'gs-button__green')])[3]/following-sibling::div//button[1]")
    WebElement IMPORT_PRODUCT_BTN;
    @FindBy(xpath = "//input[@type='file']")
    WebElement FILE_INPUT;
    @FindBy(css = ".modal-footer .gs-button__green")
    WebElement IMPORT_BTN_MODAL;
    @FindBy(css = ".text-primary")
    WebElement IMPORTING_LBL;
}
