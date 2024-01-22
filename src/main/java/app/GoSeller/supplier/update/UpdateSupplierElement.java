package app.GoSeller.supplier.update;

import org.openqa.selenium.By;

public class UpdateSupplierElement {
    By BACK_ICON  = By.xpath("//*[contains(@resource-id, 'ivActionBarIconLeft')]");
    By SAVE_BTN = By.xpath("//*[contains(@resource-id, 'tvActionBarButtonRight')]");
    By SUPPLIER_NAME  = By.xpath("//*[contains(@resource-id, 'edtSupplierName')]");
    By SUPPLIER_CODE = By.xpath("//*[contains(@resource-id, 'edtSupplierCode')]");
    By SUPPLIER_PHONE_CODE = By.xpath("//*[contains(@resource-id, 'tvCountryPhoneCode')]");
    By SUPPLIER_PHONE = By.xpath("//*[contains(@resource-id, 'edtPhoneNumber')]");
    By SUPPLIER_EMAIL = By.xpath("//*[contains(@resource-id, 'edtEmail')]");
    By SELECTED_COUNTRY = By.xpath("//*[contains(@resource-id, 'tvSelectedCountry')]");
    By COUNTRY_SEARCH_ICON = By.xpath("//*[contains(@resource-id, 'btnSearch')]");
    By COUNTRY_SEARCH_BOX = By.xpath("//*[contains(@resource-id, 'search_src_text')]");
    By COUNTRY_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_ADDRESS = By.xpath("//*[contains(@resource-id, 'edtAddress')]");
    By SELECTED_VN_CITY = By.xpath("//*[contains(@resource-id, 'tvSelectedCity')]");
    By VN_CITY_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_CITY_DROPDOWN_CLOSE_ICON = By.xpath("//*[contains(@resource-id, 'btnClose')]");
    By SELECTED_VN_DISTRICT = By.xpath("//*[contains(@resource-id, 'tvSelectedDistrict')]");
    By VN_DISTRICT_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_DISTRICT_DROPDOWN_CLOSE_ICON = By.xpath("//*[contains(@resource-id, 'btnClose')]");
    By SELECTED_VN_WARD = By.xpath("//*[contains(@resource-id, 'tvSelectedWard')]");
    By VN_WARD_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_WARD_DROPDOWN_CLOSE_ICON = By.xpath("//*[contains(@resource-id, 'btnClose')]");
    By OUTSIDE_VN_STREET_ADDRESS = By.xpath("//*[contains(@resource-id, 'edtAddress')]");
    By OUTSIDE_VN_ADDRESS2 = By.xpath("//*[contains(@resource-id, 'edtAddress2')]");
    By SELECTED_OUTSIDE_VN_STATE = By.xpath("//*[contains(@resource-id, 'tvSelectedState')]");
    By OUTSIDE_VN_STATE_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By OUTSIDE_VN_STATE_DROPDOWN_CLOSE_ICON = By.xpath("//*[contains(@resource-id, 'btnClose')]");
    By OUTSIDE_VN_CITY = By.xpath("//*[contains(@resource-id, 'edtCityOutSideVN')]");
    By OUTSIDE_VN_ZIPCODE = By.xpath("//*[contains(@resource-id, 'edtZipCode')]");
    By SELECTED_RESPONSIBLE_STAFF = By.xpath("//*[contains(@resource-id, 'tvSelectedResponsibleStaff')]");
    By RESPONSIBLE_STAFF_LIST = By.xpath("//*[contains(@resource-id, 'tvTitle')]");
    By DESCRIPTION = By.xpath("//*[contains(@resource-id, 'edtDescription')]");

    By DELETE_BTN = By.xpath("//*[contains(@resource-id, 'tvDelete')]");
    By CONFIRM_POPUP_OK_BTN = By.xpath("//*[contains(@resource-id, 'tvRightButton')]");
}
