package web.Dashboard.orders.quotation;

import org.openqa.selenium.By;

public class QuotationElement {
    By loc_lblPageTitle = By.cssSelector(".gs-page-title");
    By loc_lblTableHeader = By.cssSelector(".quotation-in-store-purchase-cart-product-list__table-header");
    By loc_ddlSearchBy = By.cssSelector(".quotation-instore-purchase .uik-btn__base");
    By loc_txtSearchProduct = By.cssSelector("#dropdownSuggestionProduct input.uik-input__input");
    By loc_tmpProductSearchResult = By.cssSelector(".search-list__result .product-item-row");
    By loc_txtSearchCustomer = By.cssSelector(".order-in-store-purchase-customer input.uik-input__input");
    By loc_tmpCustomerSearchResult = By.cssSelector(".search-result .mobile-customer-profile-row__info");
    By loc_lblSelectedCustomerName = By.cssSelector(".information .name");
    By loc_lblSelectedCustomerPhone = By.cssSelector(".information .phone");
    By loc_lblMoneyAmount = By.cssSelector(".quotation-in-store-purchase-complete .align-items-center");
    By loc_btnCreate = By.cssSelector(".quotation-in-store-purchase-complete button.gs-button__green");
    By loc_txtQuantity = By.cssSelector(".quotation-in-store-purchase-cart-product-list__stock-input");
    By loc_btnRemoveProduct = By.xpath("//i[starts-with(@class,'gs-action-button')]");

    String PRODUCT_NAME_IN_RESULT = ".//*[contains(@class,'product-item-row__product-name') %s]";
    String VARIATION_IN_RESULT = ".//*[contains(@class,'product-item-row__variation-name') %s]";
    String PRODUCT_BARCODE_IN_RESULT = ".//code[' ' %s]";
    String PRICE_IN_RESULT = ".//*[contains(@class,'product-item-row__price') %s]";
    String CONV_UNIT_IN_RESULT = ".//p[' ' %s]";
}
