package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class POSInstorePurchase{
	private boolean createDebtOrder;
	private boolean notApplyEarningPoint;
	private boolean addDirectDiscount;
	private boolean createOrder;
	private boolean applyDiscountCode;
}