package utilities.model.staffPermission.Product;

import lombok.Data;

@Data
public class Transfer{
	private boolean editTransfer;
	private boolean cancelTransfer;
	private boolean viewTransferList;
	private boolean confirmShipGoods;
	private boolean confirmReceivedGoods;
	private boolean viewTransferDetail;
	private boolean createTransfer;
}