package web.Dashboard.products.all_products.crud;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import web.Dashboard.products.all_products.management.ProductManagementElement;

public class ProductPageElement {
    By loc_bodyApp = By.cssSelector("#app-body");
    By loc_ddvSelectedLanguage = By.cssSelector(".language-selector span.uik-btn__content");
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
    By loc_btnSave = By.cssSelector("[data-testid=\"desktop-saveBtn\"]");
    By loc_btnDeactivate = By.xpath("(//*[text() = 'Ngừng bán' or text() = 'Deactivate'])[1]/parent::button");
    By loc_btnDelete = By.xpath("(//*[text() = 'Xóa' or text() = 'Delete'])[1]/parent::button");
    By loc_dlgConfirmDelete_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    public static By loc_dlgSuccessNotification = By.cssSelector(".modal-success");
    By loc_dlgNotification_btnClose = By.cssSelector("[data-testid='closeBtn']");
    By loc_btnAddAttribution = By.cssSelector("div:nth-child(8) > div.gs-widget__header .gs-fake-link");
    By loc_icnDeleteAttribution = By.cssSelector("div:nth-child(8) > div.gs-widget__content-wrapper button");
    By loc_txtAttributionName = By.cssSelector("[name *= 'input-attribute-name']");
    By loc_txtAttributionValue = By.cssSelector("[id*= 'input-attribute-value']");
    By loc_chkDisplayAttribute = By.cssSelector("div:nth-child(8) > div.gs-widget__content-wrapper .uik-checkbox__checkbox");
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
    By loc_dlgAddIMEI_chkSelectAllBranches = By.cssSelector(".managed-inventory-modal .uik-menuDrop__list > button:nth-child(1)  input");
    By loc_dlgAddIMEI_icnDeleteIMEI = By.cssSelector(".code .fa-times");
    By loc_dlgAddIMEI_txtAddIMEI = By.cssSelector(".input-code input");
    By loc_dlgAddIMEI_btnSave = By.cssSelector(".modal-footer > .gs-button__green");
    /* Variation product */
    public static final By loc_btnAddVariation = By.cssSelector("div:nth-child(4) > div.gs-widget__header > span");
    public static final By loc_txtVariationName = By.cssSelector("div.first-item > div > div > input");
    public static final By loc_txtVariationValue = By.cssSelector(".second-item .box-input input");
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
    By loc_dlgUpdateStock_txtBranchStock(String branchName) {
        return By.xpath("//td[text() ='%s']/following-sibling::td//input | //tbody//tr//td[count(//*[text()='%s']/preceding-sibling::th) +1]//*[@name='search-input']".formatted(branchName, branchName));
    }
    /* Product list page */
    /* Tien */
    public final static By loc_btnDeleteVariation = By.cssSelector(".d-none .product-form-variation-selector__btn-delete");
    By loc_icnDeleteDeposit = By.xpath("//div[contains(@class,'product-form-variation-selector__gs-tag')]/parent::*/parent::*/following-sibling::*/button");
    By loc_dlgPrintProductBarcode = By.cssSelector(".modal-content.product-list-barcode-printer");
    By btnAddDeposit = By.xpath("//input[@class='uik-checkbox__checkbox' and @name='enabledListing']/ancestor::div[contains(@class,'uik-widget__wrapper')]/following-sibling::*/div[1]//span");
    By loc_dlgEditTranslation = By.cssSelector(".modal.fade.show");
    By loc_dlgEditTranslation_ddvSelectedLanguage = By.cssSelector(".product-translate .text-truncate");
    By loc_dlgEditTranslation_ddlLanguages = By.cssSelector(".product-translate .uik-select__optionList");
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
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
    By loc_lblVariations = By.cssSelector("[class $= --n1] > .gs-widget:nth-child(4) > .gs-widget__header > h3");
    public final static By loc_chkAddWholesalePricing = By.cssSelector(".uik-checkbox__wrapper > [name='enabledListing']");
    public final static By loc_btnConfigureWholesalePricing = By.xpath("//label/*[@name = 'enabledListing']//ancestor::div[contains(@class,'gs-widget__header')]/following-sibling::div//button");
    /* Conversion unit config */
    public final static By loc_chkAddConversionUnit = By.cssSelector(".uik-checkbox__wrapper > [name='conversionUnitCheckbox']");
    public final static By loc_btnConfigureAddConversionUnit = By.xpath("//*[@name = 'conversionUnitCheckbox']//ancestor::div[contains(@class, 'border-radius-bottom')]/following-sibling::div//button");
    By loc_dlgConfirm = By.cssSelector(".modal-content");
    By loc_dlgConfirm_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    By loc_dlgConfirm_btnCancel = By.cssSelector(".modal-footer .gs-button__gray--outline");
    public By loc_dlgConfirm_btnNo = By.cssSelector(".modal-footer .gs-button__yellow--outline");
    By loc_lblEditTranslation = By.xpath("(//*[text() = 'Sửa bản dịch' or text() = 'Edit Translation'])[1]/parent::button");

    By loc_ttlUpdatePrice = By.cssSelector(".modal-title");
    By loc_txtVariationListingPrice = By.xpath("//*[contains(@name,'orgPrice')]/parent::div/parent::div/preceding-sibling::input");
    By loc_txtVariationCostPrice = By.xpath("//*[contains(@name, 'costPrice')]/parent::div/parent::div/preceding-sibling::input");
    By loc_ttlUpdateSKU = By.cssSelector(".modal-title");
    @Getter
    By loc_cntNoWholesalePricingConfig = By.cssSelector(".gs-widget__content.bg-light-white p");
    @Getter
    By loc_lblConfigure = By.cssSelector(".gs-widget__content.bg-light-white .gs-button__green");
    By loc_lblSEOSetting = By.xpath("//div[contains(@class, ' seo-editor')]//div/h3");
    public By loc_cntNoCollection = By.cssSelector(".product-form-collection-selector2 > .no-content");
    public By loc_lnkCreateCollection = By.cssSelector(".product-form-collection-selector2 > .no-content > .gs-fake-link");
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
