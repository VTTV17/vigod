package utilities.model.staffPermission.Supplier;

import lombok.Data;

@Data
public class PurchaseOrder{
	private boolean viewListCreatedPurchaseOrder;
	private boolean viewPurchaseOrderDetail;
	private boolean approvePurchaseOrder;
	private boolean viewPurchaseOrderHistory;
	private boolean editPurchaseOrder;
	private boolean createPurchaseOrder;
	private boolean viewPurchaseOrderList;
	private boolean completePurchaseOrder;
	private boolean printPurchaseOrderReceipt;
	private boolean cancelPurchaseOrder;
}