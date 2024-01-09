package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class Setting {
    private Account account;
    private StoreInformation storeInformation;
    private BranchManagement branchManagement;
    private ShippingAndPayment shippingAndPayment;
    private Permission permission;
    private StoreLanguage storeLanguage;
    private StaffManagement staffManagement;
    private TAX tAX;
    private BankAccount bankAccount;
}