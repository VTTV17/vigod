package utilities.model.staffPermission.Supplier;

import lombok.Data;

@Data

public class Suppliers {
    private Debt debt = new Debt();
    private PurchaseOrder purchaseOrder = new PurchaseOrder();
    private Supplier supplier = new Supplier();
}
