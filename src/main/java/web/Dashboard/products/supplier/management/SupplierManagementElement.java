package web.Dashboard.products.supplier.management;

import org.openqa.selenium.By;

public class SupplierManagementElement {
    By loc_lblSelectedLanguage = By.cssSelector(".language-selector .uik-select__valueWrapper");
    String languageLocator = "//*[@class = 'uik-select__label']/span[text()= '%s']";
    By loc_txtSearchSupplier = By.cssSelector(".supplier-list-page .d-desktop-flex .uik-input__input");
    By loc_btnAddSupplier = By.cssSelector(".gs-button__green");
    By loc_lblSupplierCode = By.cssSelector(".gs-table-body-items strong");
    By loc_lblSupplierName = By.cssSelector(".gs-table-body-items .gs-table-body-item:nth-child(2)");
    By loc_lblEmail = By.cssSelector(".gs-table-body-items .gs-table-body-item:nth-child(3)");
    By loc_lblPhoneNumber = By.cssSelector(".gs-table-body-items .gs-table-body-item:nth-child(4)");

    /* UI */
    By loc_lblPageTitle = By.cssSelector(".gs-page-title");
    By loc_lblAddSupplier = By.cssSelector(".gs-button__green");
    By loc_plhSearchSupplier = By.cssSelector(".d-mobile-none .gs-search-box__wrapper input");
    By loc_tblSupplier = By.cssSelector(".d-mobile-none .gs-table-header-item");
    By loc_lblNoResult = By.cssSelector(".d-mobile-none.gs-table +* img +*");
}
