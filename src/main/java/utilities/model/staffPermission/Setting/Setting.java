package utilities.model.staffPermission.Setting;

import lombok.Data;

@Data
public class Setting {
    private Account account = new Account();
    private StoreInformation storeInformation = new StoreInformation();
    private BranchManagement branchManagement = new BranchManagement();
    private ShippingAndPayment shippingAndPayment = new ShippingAndPayment();
    private Permission permission = new Permission();
    private StoreLanguage storeLanguage = new StoreLanguage();
    private StaffManagement staffManagement = new StaffManagement();
    private TAX tAX = new TAX();
    private BankAccount bankAccount = new BankAccount();
}