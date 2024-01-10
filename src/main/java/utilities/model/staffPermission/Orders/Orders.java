package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class Orders{
	private OrderManagement orderManagement = new OrderManagement();
	private ReturnOrder returnOrder = new ReturnOrder();
	private POSInstorePurchase pOSInstorePurchase = new POSInstorePurchase();
	private Delivery delivery = new Delivery();
	private Quotation quotation = new Quotation();
}