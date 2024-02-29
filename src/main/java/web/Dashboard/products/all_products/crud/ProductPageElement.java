package web.Dashboard.products.all_products.crud;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import web.Dashboard.products.all_products.management.ProductManagementElement;

public class ProductPageElement {
    By loc_ddvSelectedLanguage = By.cssSelector(".language-selector .uik-select__valueWrapper");
    String loc_ddvLanguageValue = "//*[@class = 'uik-select__label']/span[text()= '%s']";
    By loc_btnCreateProduct = new ProductManagementElement().getLoc_btnCreateProduct();
    By loc_btnPrintBarcode = By.xpath("(//div[contains(@class,'gs-content-header-right-el d-flex')]//button)[4]");
    /* General product information */
    By loc_txtProductName = By.cssSelector("input#productName");
    By loc_txaProductDescription = By.cssSelector("div.fr-wrapper > div");
    By loc_icnRemoveImages = By.cssSelector(".image-widget__btn-remove");
    By imgUploads = By.cssSelector(".image-drop-zone input");
    By loc_ddvSelectedVAT = By.cssSelector(".form-group .uik-select__valueWrapper");
    By loc_ddvNoVAT = By.xpath("//*[@class = 'uik-select__optionContent']/div[text()='Không áp dụng thuế'] | //*[@class = 'uik-select__optionContent']/div[text()='Tax does not apply']");
    String loc_ddvOthersVAT = "//*[@class = 'uik-select__optionContent']/div[text()='%s']";
    By loc_icnRemoveCollection = By.xpath("//*[@class = 'product-form-collection-selector2__selected-item-name']/following-sibling::*");
    By loc_txtCollectionSearchBox = By.cssSelector(".product-form-collection-selector2 input");
    String loc_ddvCollectionValue = "//div[text() = '%s']";
    By loc_dlgUpdateSKU = By.cssSelector(".product-multiple-branch-sku_editor_modal");
    By loc_txtWithoutVariationSKU = By.cssSelector("#productSKU,[class *=--n2] > div:nth-child(3) .align-items-center > span");
    By loc_lblUpdateStock = By.xpath("//div[contains(@class,'gs-witget-warehousing')]//div[contains(@class,'uik-widget-title')]//span[@class='gs-fake-link ']");
    By loc_ddlManageInventory = By.cssSelector("#manageInventory");
    By loc_chkManageStockByLotDate = By.xpath("//*[@id='lotAvailable']//parent::div//preceding-sibling::label/input");
    By loc_txtPriority = By.cssSelector("[name = productPriority]");
    By loc_txtWeight = By.cssSelector("[for ='productWeight'] +* input");
    By loc_txtLength = By.cssSelector("[for ='productLength'] +* input");
    By loc_txtWidth = By.cssSelector("[for ='productWidth'] +* input");
    By loc_txtHeight = By.cssSelector("[for ='productHeight'] +* input");
    By loc_chkApp = By.cssSelector("[name = onApp]");
    By loc_chkWeb = By.cssSelector("[name = onWeb]");
    By loc_chkInStore = By.cssSelector("[name = inStore]");
    By loc_chkGoSocial = By.cssSelector("[name = inGosocial]");
    By loc_btnSave = By.cssSelector(".gss-content-header .btn-save");
    By loc_btnDeactivate = By.cssSelector(".gss-content-header .gs-button__yellow--outline");
    By loc_btnDelete = By.cssSelector(".gss-content-header .gs-button__red--outline");
    By loc_dlgConfirmDelete_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgCommons = By.cssSelector(".modal-content");
    By loc_dlgNotification = By.cssSelector("[data-sherpherd='tour-guide-alert-modal']");
    By loc_dlgUpdateFailed = By.cssSelector(".modal-danger");
    By loc_dlgNotification_btnClose = By.cssSelector("[data-testid='closeBtn']");
    By loc_txtSEOTitle = By.cssSelector("input#seoTitle");
    By loc_txtSEODescription = By.cssSelector("input#seoDescription");
    By loc_txtSEOKeywords = By.cssSelector("input#seoKeywords");
    By loc_txtSEOUrl = By.cssSelector("input#seoUrl");
    /* Without variation product */
    By loc_txtWithoutVariationListingPrice = By.xpath("//label[@for='productDiscountPrice']//parent::div//preceding-sibling::div//label//following-sibling::div//input");
    By loc_txtWithoutVariationSellingPrice = By.cssSelector("[for = 'productDiscountPrice'] +* input");
    By loc_txtWithoutVariationCostPrice = By.xpath("//label[@for='productDiscountPrice']//parent::div//following-sibling::div//label[@for = 'productPrice'] //following-sibling::div//input");
    By loc_chkDisplayIfOutOfStock = By.xpath("//*[@name='showOutOfStock']/parent::div/preceding-sibling::label[2]/input");
    By loc_chkHideRemainingStock = By.xpath("//*[@name='isHideStock']/parent::div/preceding-sibling::label[2]/input");
    By loc_chkShowAsListingProduct = By.xpath("//*[@name='enabledListing']/parent::div/preceding-sibling::label[2]/input");
    By loc_txtWithoutVariationBranchStock = By.cssSelector(".branch-list-stock__wrapper__row  input");
    By loc_dlgUpdateStock = By.cssSelector(".product-multiple-branch-stock_editor_modal");
    By loc_dlgAddIMEI = By.cssSelector(".managed-inventory-modal");
    By loc_dlgAddIMEISelectedBranch = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By loc_dlgAddIMEI_chkSelectAllBranches = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
    By loc_dlgAddIMEI_txtAddIMEI = By.cssSelector(".input-code input");
    By loc_dlgAddIMEI_btnSave = By.cssSelector(".modal-footer > .gs-button__green");
    /* Variation product */
    @Getter
    By loc_btnAddVariation = By.cssSelector("div:nth-child(4) > div.gs-widget__header > span");
    @Getter
    By loc_txtVariationName = By.cssSelector("div.first-item > div > div > input");
    @Getter
    By loc_txtVariationValue = By.cssSelector(".second-item .css-nwjfc > input");
    By loc_tblVariation_chkSelectAll = By.cssSelector(".product-form-variation-selector__table  th:nth-child(1) input");
    By loc_tblVariation_lnkSelectAction = By.cssSelector("th .gs-fake-link");
    /**
     * <p>0: Update Price</p>
     * <p>1: Update Stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update Image</p>
     */
    By loc_tblVariation_ddvActions = By.cssSelector(".uik-menuDrop__list > button");
     By loc_tblVariation_txtStock = By.xpath("//*[contains(@name, 'stock')]|//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[2]/span");
    By loc_tblVariation_txtSKU = By.xpath("//*[contains(@name, 'sku')]|//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span");
    By loc_dlgUpdateSKU_txtInputSKU = By.cssSelector(".justify-content-center input");
    By loc_tblVariation_imgUploads = By.cssSelector("td > img");
    By loc_dlgUploadsImage_btnUploads = By.cssSelector(".modal-content [type = file]");
    By loc_dlgUpdatePrice_txtListingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'orgPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_txtSellingPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'discountPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgUpdatePrice_txtCostPrice = By.xpath("//*[contains(@class, 'product-variation-price-editor-modal__table')]//*[contains(@name, 'costPrice')]//parent::div//parent::div//preceding-sibling::input");
    By loc_dlgCommons_btnUpdate = By.cssSelector(".modal-footer .gs-button__green");
    By dlgCommons_btnCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgUpdateStock_ddvSelectedBranch = By.cssSelector(".gs-dropdown-multiple-select__drop-header .uik-select__valueWrapper");
    By loc_dlgUpdateStock_chkSelectAllBranches = By.cssSelector(".modal-body .uik-menuDrop__list > button:nth-child(1)  input");
    By loc_dlgUpdateStock_tabChange = By.cssSelector(".modal-body  div > div > .gs-button:nth-child(2)");
    By loc_dlgUpdateStock_txtStockValue = By.cssSelector(".modal-body  .quantity-input-field > input");
    By loc_dlgUpdateStock_txtBranchStock = By.cssSelector(".input-stock  > input");
    /* Product list page */
    /* Tien */
    @Getter
    By loc_btnDeleteVariation = By.xpath("//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/following-sibling::*/button");
    By loc_icnDeleteDeposit = By.xpath( "//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/parent::*/following-sibling::*/button");
    By loc_dlgPrintProductBarcode = By.cssSelector(".modal-content.product-list-barcode-printer");
    By btnAddDeposit = By.xpath("//input[@class='uik-checkbox__checkbox' and @name='enabledListing']/ancestor::div[contains(@class,'uik-widget__wrapper')]/following-sibling::*/div[1]//span");
    By loc_dlgEditTranslation = By.cssSelector(".modal.fade.show");
    By loc_dlgEditTranslation_ddvSelectedLanguage = By.cssSelector(".product-translate .text-truncate");
    String dlgEditTranslation_ddvOtherLanguage = "//*[@class = 'uik-select__label']//*[text()='%s']";
    By loc_dlgEditTranslation_txtProductName = By.cssSelector("#informationName");
    By loc_dlgEditTranslation_txtProductDescription = By.cssSelector(".modal-body .fr-element");
    By loc_dlgEditTranslation_txtVariationName = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[1]/descendant::input");
    By loc_dlgEditTranslation_txtVariationValue = By.xpath("//*[@class = 'product-translate-modal']/*[@class = 'product-translate__titleBody']/h3/parent::div/following-sibling::div[@class]/div[2]/descendant::input");
    By loc_dlgEditTranslation_txtSEOTitle = By.cssSelector(".modal-body #seoTitle");
    By loc_dlgEditTranslation_txtSEODescription = By.cssSelector(".modal-body #seoDescription");
    By loc_dlgEditTranslation_txtSEOKeywords = By.cssSelector(".modal-body #seoKeywords");
    By loc_dlgEditTranslation_txtSEOUrl = By.cssSelector(".modal-body #seoUrl");
    By loc_dlgEditTranslation_btnSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgToast = By.cssSelector(".Toastify__toast--success");
    By loc_dlgEditTranslation_icnClose = By.cssSelector(".modal.fade.show .close");
    @Getter
    By loc_chkAddWholesalePricing = By.cssSelector(".uik-checkbox__wrapper > [name='enabledListing']");
    @Getter
    By loc_btnConfigureWholesalePricing = By.xpath("//label/*[@name = 'enabledListing']//ancestor::div[contains(@class,'gs-widget__header')]/following-sibling::div//button");
    /* Conversion unit config */
    @Getter
    By loc_chkAddConversionUnit = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
    @Getter
    By loc_btnConfigureAddConversionUnit = By.xpath("//*[@name = 'conversionUnitCheckbox']//ancestor::div[contains(@class, 'border-bottom')]/following-sibling::div//button");
    @Getter
    By notSupportConversionUnitForProductManagedByIMEI = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(5) small");
    @Getter
    By loc_cntNoConversionUnitConfig = By.cssSelector(".gs-widget__content .bg-light-white p");
    By loc_dlgConfirm = By.cssSelector(".modal-content");
    By loc_dlgConfirm_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgConfirm_btnCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    public By loc_dlgConfirm_btnNo = By.cssSelector(".modal-footer .gs-button__yellow--outline");
    /* UI text element */
    /* Header */
    By loc_lnkGoBackToProductList = By.xpath("//a[@href='/product/list' and not(@name)]");
    By loc_ttlPage = By.cssSelector(".gss-content-header .gs-page-title");
    By loc_lblEditTranslation = By.xpath("//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/preceding-sibling::button/div");
    By loc_lblSave = By.cssSelector(".gss-content-header .gs-button__green > div");
    By loc_lblDeactivate = By.cssSelector(".gss-content-header .gs-button__yellow--outline > div");
    By loc_lblDelete = By.cssSelector(".gss-content-header .gs-button__red--outline > div");
    By loc_lblCancel = By.xpath("//div[contains(@class, 'gss-content-header ')]/descendant::button[contains(@class,'btn-save')]/following-sibling::button[contains(@class, 'gs-button__gray--outline')]/div");
    /* Product information */
    By loc_lblProductInformation = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(1) > .gs-widget__header > h3");
    By loc_lblProductName = By.cssSelector("[for = productName]");
    By loc_lblErrorWhenLeaveProductNameBlank = By.xpath("//*[@name='productName']/following-sibling::div");
    By loc_lblProductDescription = By.cssSelector("[for = productDescription]");
    /* Upload images */
    By loc_lblImages = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(2) > .gs-widget__header > h3");
    By loc_lblDragAndDrop = By.cssSelector(".image-drop-zone");
    /* Price and VAT */
    By loc_lblPricing = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(3) > .gs-widget__header > h3");
    By loc_lblListingPrice = By.xpath("(//*[@for ='productPrice'])[1]");
    By loc_lblSellingPrice = By.cssSelector("[for = productDiscountPrice]");
    By loc_lblCostPrice = By.xpath("(//*[@for ='productPrice'])[2]");
    By loc_lblVAT = By.cssSelector("label:not([for]).gs-frm-control__title");
    By loc_lblShowAsListingProduct = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(3) .uik-checkbox__label");
    /* Variation */
    @Getter
    By loc_lblVariations = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3");
    By loc_lblAddVariation = By.xpath("(//*[contains(@class, 'gs-widget__header')]/ span)[1]");
    By loc_cntVariation = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .gs-widget__content > p");
    By loc_lblVariationName = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(1) > label");
    By loc_lblVariationValue = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) .product-form-variation-selector > .d-none > div:nth-child(2) > label");
    By loc_plhVariationValue = By.cssSelector(".second-item .css-14el2xx-placeholder");
    // Variation table
    By loc_lblNumberOfSelectedVariations = By.xpath("//span[contains(@class,'gs-fake-link')]/ancestor::th/div");
    By loc_lnkSelectAction = By.xpath("//th/div/div/span");
    /**
     * <p>0: Update price</p>
     * <p>1: Update stock</p>
     * <p>2: Update SKU</p>
     * <p>3: Update image</p>
     */
    By loc_lblActionsList = By.cssSelector(".uik-menuDrop__list span");
    /**
     * <p>0: Image</p>
     * <p>1: Listing price</p>
     * <p>2: Selling price</p>
     * <p>3: Cost price</p>
     * <p>4: Stock quantity</p>
     * <p>5: SKU</p>
     * <p>6: Barcode</p>
     */
    By loc_tblVariation_lblColumn = By.xpath("//th[@class=' align-middle'][1] | //th[contains(@class, 'text-center')]");
    By loc_tblVariation_lblEditSKU = By.xpath("//input[contains(@name,'barcode')]/ancestor::td/preceding-sibling::td[1]/span");
    // Update variation price popup
    By loc_ttlUpdatePrice = By.cssSelector(".modal-title");
    By loc_dlgUpdatePrice_ddvSelectedPriceType = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By loc_dlgUpdatePrice_ddlPriceType = By.cssSelector(".uik-select__optionList");
    By loc_txtListingPrice_0 = By.xpath("//*[@name = '0-orgPrice']/parent::div/parent::div/preceding-sibling::input");
    By loc_txtCostPrice_0 = By.xpath("//*[@name = '0-costPrice']/parent::div/parent::div/preceding-sibling::input");
    /**
     * <p>0: Listing price</p>
     * <p>1: Selling price</p>
     * <p>2: Cost price</p>
     */
    By loc_dlgUpdatePrice_lblPriceType = By.cssSelector(".uik-select__optionList span");
    By loc_dlgUpdatePrice_lblApplyAll = By.cssSelector(".modal-body .gs-button__blue");
    /**
     * <p>size() - 3: Listing price</p>
     * <p>size() - 2: Selling price</p>
     * <p>size() - 1: Cost price</p>
     */
    By loc_dlgUpdatePrice_tblVariation_lblPriceType = By.cssSelector(".modal-body tr > th");
    By loc_dlgUpdatePrice_lblCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgUpdatePrice_lblUpdate = By.cssSelector(".modal-footer .gs-button__green");
    // Update normal variation stock popup
    By loc_ttlUpdateStock = By.cssSelector(".modal-title");
    /**
     * <p>0: Add stock</p>
     * <p>1: Change stock</p>
     */
    By loc_dlgUpdateStock_lblActions = By.cssSelector(".modal-body div > div > .gs-button");
    By loc_dlgUpdateStock_plhStockValue = By.cssSelector("[name= quantity]");
    By loc_dlgUpdateStock_lblCurrentAction = By.cssSelector(".modal-body strong");
    By loc_dlgUpdateStock_lblCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgUpdateStock_lblUpdate = By.cssSelector(".modal-footer .gs-button__green");
    // Add IMEI/Serial number popup
    By loc_ttlAddIMEI = By.cssSelector(".modal-title");
    By loc_dlgAddIMEI_lblBranch = By.cssSelector(".branch > h3");
    By loc_dlgAddIMEI_plhAddIMEI = By.cssSelector(".input-code input");
    By loc_dlgAddIMEI_lblProductName = By.cssSelector(".modal-body thead > tr > th:nth-child(1)");
    By loc_dlgAddIMEI_lblCancel = By.cssSelector(".modal-footer .gs-button__white");
    By loc_dlgAddIMEI_lblSave = By.cssSelector(".modal-footer .gs-button__green");
    // Update variation SKU popup
    By loc_ttlUpdateSKU = By.cssSelector(".modal-title");
    By loc_dlgUpdateSKU_lblBranch = By.cssSelector(".modal-body .uik-select__valueWrapper");
    By loc_dlgUpdateSKU_lblCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgUpdateSKU_lblUpdate = By.cssSelector(".modal-footer .gs-button__green");
    // Update variation image popup
    By loc_ttlUploadImages = By.cssSelector(".modal-title");
    By loc_dlgUploadImages_plhUpload = By.cssSelector(".modal-body .image-uploader__text");
    By loc_dlgUploadImages_lblCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    By loc_dlgUploadImages_lblSelect = By.cssSelector(".modal-footer .gs-button__green");
    // edit translation popup
    By loc_ttlEditTranslation = By.cssSelector(".product-translate__titleHeader > p");
    By loc_dlgEditTranslation_lblInformation = By.xpath("//*[@for='informationName']/parent::div/preceding-sibling::div");
    By loc_dlgEditTranslation_lblProductName = By.cssSelector("[for='informationName']");
    By loc_dlgEditTranslation_lblProductDescription = By.xpath("//*[@for='informationName']/parent::div/following-sibling::div/label");
    By loc_dlgEditTranslation_lblVariation = By.xpath("//div[@class='row']/preceding::div[@class = 'product-translate__titleBody'][1]");
    By loc_dlgEditTranslation_lblSEOSetting = By.xpath("//div[@class=' seo-editor']/preceding::div[@class = 'product-translate__titleBody'][1]");
    By loc_dlgEditTranslation_lblSEOLivePreview = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div/span)[2]");
    By loc_dlgEditTranslation_tltSEOLivePreview = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div//*[@data-tooltipped = ''])[2]");
    By loc_dlgEditTranslation_lblSEOTitle = By.xpath("(//*[@name= 'seoTitle']/parent::div/parent::div/preceding-sibling::div[1]/span)[2]");
    By loc_dlgEditTranslation_tltSEOTitle = By.xpath("(//*[contains(@class,  'seo-editor__live-preview-wrapper')]/following-sibling::div[1]//*[@data-tooltipped])[2]");
    By loc_dlgEditTranslation_lblSEODescription = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]/span)[2]");
    By loc_dlgEditTranslation_tltSEODescription = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[2]");
    By loc_dlgEditTranslation_lblSEOKeywords = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//span)[2]");
    By loc_dlgEditTranslation_tltSEOKeywords = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[2]");
    By loc_dlgEditTranslation_lblSEOUrl = By.xpath("(//*[@name= 'seoUrl']/parent::div/preceding-sibling::div[1]//span)[2]");
    By loc_dlgEditTranslation_lblSave = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgEditTranslation_lblCancel = By.cssSelector(".modal-footer .gs-button__white");
    /* Conversion unit */
    By loc_lblUnit = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(5) > .gs-widget__header > h3 > span");
    By loc_plhSearchUnit = By.cssSelector("#input-search");
    By loc_lblAddConversionUnit = By.xpath("//*[@name='conversionUnitCheckbox']/parent::label/div");
    By loc_tltConversionUnit = By.xpath("//*[@name= 'input-search']//ancestor::div[@class = 'gs-dropdown-search-form']/following-sibling::div//*[@data-tooltipped]");
    /* Wholesale product */
    By loc_lblAddWholesalePricing = By.xpath("//*[@name='enabledListing']/parent::label");
    @Getter
    By loc_cntNoWholesalePricingConfig = By.cssSelector(".gs-widget__content.bg-light-white p");
    @Getter
    By loc_lblConfigure = By.cssSelector(".gs-widget__content.bg-light-white .gs-button__green");
    /* Deposit */
    By loc_lblDeposit = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > h3");
    By loc_lblAddDeposit = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7) > .gs-widget__header > span");
    By loc_cntDeposit = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(7)  .gs-widget__content > p");
    /* SEO */
    By loc_lblSEOSetting = By.xpath("//div[contains(@class, ' seo-editor')]//div/h3");
    By loc_lblLivePreview = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div/span)[1]");
    By loc_tltLivePreview = By.xpath("(//*[contains(@class, 'seo-editor__live-preview-wrapper')]/preceding-sibling::div//*[@data-tooltipped = ''])[1]");
    By loc_lblSEOTitle = By.xpath("(//*[@name= 'seoTitle']/parent::div/parent::div/preceding-sibling::div[1]/span)[1]");
    By loc_tltSEOTitle = By.xpath("(//*[contains(@class,  'seo-editor__live-preview-wrapper')]/following-sibling::div[1]//*[@data-tooltipped])[1]");
    By loc_lblSEODescription = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]/span)[1]");
    By loc_tltSEODescription = By.xpath("(//*[@name= 'seoDescription']/parent::div/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[1]");
    By loc_lblSEOKeywords = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//span)[1]");
    By loc_tltSEOKeywords = By.xpath("(//*[@name= 'seoKeywords']/parent::div/preceding-sibling::div[1]//*[@data-tooltipped])[1]");
    By loc_lblSEOUrl = By.xpath("(//*[@name= 'seoUrl']/parent::div/preceding-sibling::div[1]//span)[1]");
    /* Sale chanel */
    By loc_lblSaleChannel = By.cssSelector("[class $= --n2] > div:nth-child(1) h3");
    String loc_tltSaleChannel = "//*[@id='%s']//div[@class='tippy-tooltip-content']";
    By loc_icnOnlineShop = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[1]/parent::span/parent::div");
    By loc_icnGoMua = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[2]/parent::span/parent::div");
    By loc_icnShopee = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[3]/parent::span/parent::div");
    By loc_icnTiktok = By.xpath("(//*[@class = 'gs-component-tooltip']//div[@class = 'channels-wrapper'])[4]/parent::span/parent::div");
    /* Collections */
    By loc_lblCollection = By.cssSelector("[class $= --n2] > div:nth-child(2) h3");
    By loc_plhSearchCollection = By.cssSelector(".product-form-collection-selector2 input");
    public By loc_cntNoCollection = By.cssSelector(".product-form-collection-selector2 > .no-content");
    public By loc_lnkCreateCollection = By.cssSelector(".product-form-collection-selector2 > .no-content > .gs-fake-link");
    /* Warehousing */
    By loc_lblWarehousing = By.cssSelector("[class $= --n2] > div:nth-child(3) h3");
    By loc_lblWithoutVariationSKU = By.xpath("//*[@name='barcode']/parent::div/preceding-sibling::div/label|//*[@for='productSKU']");
    By loc_lblWithoutVariationBarcode = By.cssSelector("[for = 'barcode']");
    By loc_lblManageInventory = By.cssSelector("[for = 'manageInventory']");
    By loc_lblManageByProduct = By.cssSelector("#manageInventory > [value = PRODUCT]");
    By loc_lblManageByIMEI = By.cssSelector("#manageInventory > [value = IMEI_SERIAL_NUMBER]");
    By loc_cntManageByIMEI = By.cssSelector(".Notice-product-quantity");
    By loc_lblWithoutVariationStockQuantity = By.cssSelector("[for = 'productQuantity']");
    By loc_lblApplyAll = By.xpath("//*[@for = 'productQuantity']/following-sibling::div[@class = 'row']/button/div");
    By loc_lblRemainingStock = By.cssSelector("[for = remaining]");
    By loc_lnkRemainingStock = By.xpath("//*[@for='remaining']/following-sibling::span");
    By loc_ttlRemainingStock = By.cssSelector(".modal-title");
    By loc_dlgRemainingStock_ddlBranch = By.cssSelector(".remaining-sold-item-modal__body__dropdown > button");
    By loc_dlgRemainingStock_chkAllBranches = By.xpath("//*[contains(@class,'modal-body')]/descendant::label[1]");
    By loc_dlgRemainingStock_lblNoBranchSelected = By.cssSelector(".remaining-sold-item-modal__body__error");
    By loc_dlgRemainingStock_btnClose = By.cssSelector(".modal-header > .close");
    By loc_lblSoldCount = By.cssSelector("[for = soldItem]");
    By loc_lnkSoldCount = By.xpath("//*[@for='soldItem']/following-sibling::span");
    By loc_ttlViewSoldCount = By.cssSelector(".modal-title");
    By loc_dlgViewSoldCount_ddlBranch = By.cssSelector(".remaining-sold-item-modal__body__dropdown > button");
    By loc_dlgViewSoldCount_chkSelectAllBranches = By.xpath("//*[contains(@class,'modal-body')]/descendant::label[1]");
    By loc_dlgViewSoldCount_lblNoBranchSelected = By.cssSelector(".remaining-sold-item-modal__body__error");
    By loc_dlgViewSoldCount_btnClose = By.cssSelector(".modal-header > .close");
    By loc_lblDisplayIfOutOfStock = By.xpath("//*[@id='showOutOfStock']/parent::div/preceding-sibling::label/div");
    By loc_lblHideRemainingStock = By.xpath("//*[@id='isHideStock']/parent::div/preceding-sibling::label/div");
    /* Package information */
    By loc_lblPackageInformation = By.cssSelector("[class $= --n2] > div:nth-child(4) h3");
    By loc_tltPackageInformation = By.xpath("//*[contains(@class, 'gs-witget-warehousing')]/following-sibling::div[1]//*[@data-tooltipped]");
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
