package utilities.model.staffPermission.Lazada;

import lombok.Data;

@Data
public class Lazada{
	private boolean syncProduct;
	private boolean connectAccount;
	private boolean viewAccountInformation;
	private boolean syncOrders;
	private boolean cancelOrder;
	private boolean viewOrderList;
	private boolean viewOrderDetail;
	private boolean confirmOrder;
	private boolean exportOrder;
	private boolean disconnectAccount;
}