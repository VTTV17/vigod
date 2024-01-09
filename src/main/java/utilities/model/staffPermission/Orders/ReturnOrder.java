package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class ReturnOrder{
	private boolean viewOrderReturnList;
	private boolean editReturnOrder;
	private boolean confirmPayment;
	private boolean viewOrderReturnDetail;
	private boolean createReturnOrder;
	private boolean restockGoods;
	private boolean completeReturnOrder;
	private boolean cancelReturnOrder;
}