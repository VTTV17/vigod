package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class OrderManagement{
	private boolean addTagToOrder;
	private boolean deleteTag;
	private boolean printOrderReceipt;
	private boolean createOrderCost;
	private boolean createOrderTag;
	private boolean cancelOrder;
	private boolean printOrderSlip;
	private boolean viewOrderList;
	private boolean removeTagFromOrder;
	private boolean printOrders;
	private boolean viewOrderDetail;
	private boolean confirmOrder;
	private boolean deliveredOrders;
	private boolean exportOrder;
	private boolean addCostToOrder;
	private boolean viewCreatedOrderList;
	private boolean applyDiscount;
	private boolean downloadExportedOrders;
	private boolean confirmPayment;
	private boolean viewTagList;
	private boolean displayOrderSetting;
	private boolean editOrder;
	private boolean exportOrderByProduct;
	private boolean viewOrderCostList;
}