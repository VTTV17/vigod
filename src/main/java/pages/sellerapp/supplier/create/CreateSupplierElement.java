package pages.sellerapp.supplier.create;

import org.openqa.selenium.By;

public class CreateSupplierElement {
    By BACK_ICON  = By.xpath("//*[contains(@resource-id, 'ivActionBarIconLeft')]");
    By SAVE_BTN = By.xpath("//*[contains(@resource-id, 'tvActionBarButtonRight')]");
    By SUPPLIER_NAME  = By.xpath("//*[contains(@resource-id, 'edtSupplierName')]");
    By SUPPLIER_CODE = By.xpath("//*[contains(@resource-id, 'edtSupplierCode')]");
    By SUPPLIER_PHONE = By.xpath("//*[contains(@resource-id, 'edtPhoneNumber')]");
    By SUPPLIER_EMAIL = By.xpath("//*[contains(@resource-id, 'edtEmail')]");
    By COUNTRY_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectCountry')]");
    By COUNTRY_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_ADDRESS = By.xpath("//*[contains(@resource-id, 'edtAddress')]");
    By VN_CITY_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectCity')]");
    By VN_CITY_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_DISTRICT_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectDistrict')]");
    By VN_DISTRICT_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By VN_WARD_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectWard')]");
    By VN_WARD_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By OUTSIDE_VN_STREET_ADDRESS = By.xpath("//*[contains(@resource-id, 'edtAddress')]");
    By OUTSIDE_VN_ADDRESS2 = By.xpath("//*[contains(@resource-id, 'edtAddress2')]");
    By OUTSIDE_VN_STATE_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectState')]");
    By OUTSIDE_VN_STATE_LIST = By.xpath("//*[contains(@resource-id, 'item_list_region_name')]");
    By OUTSIDE_VN_CITY = By.xpath("//*[contains(@resource-id, 'edtCityOutSideVN')]");
    By OUTSIDE_VN_ZIPCODE = By.xpath("//*[contains(@resource-id, 'edtZipCode')]");
    By RESPONSIBLE_STAFF_DROPDOWN = By.xpath("//*[contains(@resource-id, 'llSelectResponsibleStaff')]");
    By RESPONSIBLE_STAFF_LIST = By.xpath("//*[contains(@resource-id, 'tvTitle')]");
    By DESCRIPTION = By.xpath("//*[contains(@resource-id, 'edtDescription')]");
}
