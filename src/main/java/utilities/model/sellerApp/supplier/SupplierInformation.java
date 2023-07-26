package utilities.model.sellerApp.supplier;

import lombok.Data;

@Data
public class SupplierInformation {
    private boolean isVNSupplier;
    private String supplierName;
    private String supplierCode;
    private String supplierPhoneCode;
    private String supplierPhone;
    private String supplierEmail;
    private String country;
    private String vnAddress;
    private String vnCity;
    private String vnDistrict;
    private String vnWard;
    private String outsideVnStreetAddress;
    private String outsideVnAddress2;
    private String outsideVnState;
    private String outsideVNCity;
    private String outsideVnZipCode;
    private String responsibleStaff;
    private String description;
}
