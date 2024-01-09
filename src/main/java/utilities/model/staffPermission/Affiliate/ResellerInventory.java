package utilities.model.staffPermission.Affiliate;

import lombok.Data;

@Data
public class ResellerInventory{
	private boolean editTransfer;
	private boolean cancelTransfer;
	private boolean confirmShipGoods;
	private boolean confirmReceivedGoods;
	private boolean viewInventorySummary;
	private boolean viewTransferDetail;
	private boolean createTransferToReseller;
}