package web.Dashboard.orders.pos;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

public class POSElement {
    By loc_ddvSelectedBranch = By.cssSelector(".pos-selector .uik-select__valueWrapper");
    String str_ddvBranches = "//*[contains(text(),'%s')]";
    By loc_dlgConfirmChangeBranch = By.cssSelector(".confirm-modal");
    By loc_dlgConfirmChangeBranch_btnOK = By.cssSelector(".modal-footer .gs-button__green");
    By loc_txtSearchProduct = By.cssSelector("#dropdownSuggestionProduct input");
    By loc_ddvSelectedSearchType = By.cssSelector("#dropdownSuggestionProduct +* .uik-select__valueRendered");

    enum SearchType {
        product, sku, barcode;
        public static List<SearchType> getAllSearchType() {
            return Arrays.asList(SearchType.values());
        }
    }

    By loc_ddlSearchType = By.cssSelector(".uik-select__option");
    By loc_ddlSearchResult = By.cssSelector(".product-item-row__product-summary");
    By loc_txtItemQuantity = By.cssSelector(".order-in-store-purchase-cart-product-list__group-stock-input input");
    By loc_icnAddStock = By.cssSelector(".err-out-of-stock i");
    By loc_dlgAddStock = By.cssSelector(".order-in-store-purchase-complete__quantity-modal");
    By loc_dlgAddStock_txtStock = By.cssSelector(".order-in-store-purchase-complete__quantity-modal .order-in-store-purchase-complete__input-stock > input");
    By loc_dlgAddStock_btnApply = By.cssSelector(".order-in-store-purchase-complete__quantity-modal .gs-button__green");
    By loc_dlgAddIMEI = By.cssSelector(".managed-inventory-modal");
    By loc_dlgAddIMEI_txtIMEI = By.cssSelector(".managed-inventory-modal [name='serial']");
    By loc_dlgAddIMEI_btnSave = By.cssSelector(".managed-inventory-modal .gs-button__green");
    By loc_tblCart_lnkSelectLot = By.cssSelector(".order-in-store-purchase-cart-product-list__product-row +* .gs-fake-link");
    By loc_dlgSelectLot = By.cssSelector(".modal-lot-select");
    By loc_dlgSelectLot_lblAvailableQuantity = By.xpath("//*[@class = 'get-quantity']/preceding-sibling::td[1]");
    By loc_dlgSelectLot_txtConfirmQuantity = By.cssSelector("td.get-quantity");
    By loc_dlgSelectLot_btnConfirm = By.cssSelector(".modal-lot-select .gs-button__green");
    By loc_btnPromotion = By.cssSelector(".title-promotion");
    By loc_dlgDiscount = By.cssSelector(".order-instore-purchase-discount-modal");

    enum DiscountType {
        discountCode, discountAmount, discountPercent;
        public static List<DiscountType> getAllDiscountType() {
            return Arrays.asList(DiscountType.values());
        }
    }

    By loc_dlgDiscount_tabDiscountType = By.cssSelector(".box-promotion-title");
    By loc_dlgDiscount_tabDiscountCode_lblDiscountCode = By.cssSelector(".order-instore-purchase-discount-modal .discount-code-type");
    By loc_dlgDiscount_tabDiscountCode_txtEnterCouponCode = By.cssSelector(".order-instore-purchase-discount-modal .search-input-keyword");
    By loc_dlgDiscount_tabDiscountCode_btnApply = By.cssSelector(".order-instore-purchase-discount-modal .color--inherit");
    By loc_dlgDiscount_tabDiscountCode_btnSave = By.cssSelector(".order-instore-purchase-discount-modal .color--gradient-blue");
    By loc_dlgDiscount_tabDiscountAmount_txtAmount = By.cssSelector(".order-instore-purchase-discount-modal input[name='fixAmount']");
    By loc_dlgDiscount_tabDiscountAmount_btnApply = By.cssSelector(".order-instore-purchase-discount-modal .color--gradient-blue");
    By loc_dlgDiscount_tabDiscountPercent_txtPercent = By.cssSelector(".order-instore-purchase-discount-modal input[name='percentage']");
    By loc_dlgDiscount_tabDiscountPercent_btnApply = By.cssSelector(".order-instore-purchase-discount-modal .color--gradient-blue");
    By loc_icnAddCustomer = By.cssSelector("[alt='add customer']");
    By loc_dlgAddCustomer = By.cssSelector(".create-customer-modal");
    By loc_dlgAddCustomer_txtFullName = By.cssSelector(".create-customer-modal [name='fullName']");
    By loc_dlgAddCustomer_txtPhoneNumber = By.cssSelector(".create-customer-modal [name='phone']");
    By loc_dlgAddCustomer_btnAdd = By.cssSelector(".create-customer-modal .gs-button__green");
    By loc_lblTotalAmount = By.cssSelector(".value-total");
    By loc_txtReceivedAmount = By.cssSelector(".order-pos__received-input");
    By loc_btnComplete = By.cssSelector(".color--gradient-blue");
    By loc_dlgReceivedNotEnough = By.cssSelector(".modal-dialog");
    By loc_dlgReceivedNotEnough_btnApply = By.cssSelector(".modal-dialog .gs-button__green");
    By loc_dlgToastSuccess = By.cssSelector(".Toastify__toast--success");
}
