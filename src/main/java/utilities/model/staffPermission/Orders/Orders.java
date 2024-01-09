package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class Orders{
	private OrderManagement orderManagement;
	private ReturnOrder returnOrder;
	private POSInstorePurchase pOSInstorePurchase;
	private Delivery delivery;
	private Quotation quotation;
}