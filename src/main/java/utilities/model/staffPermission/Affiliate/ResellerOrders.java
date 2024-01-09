package utilities.model.staffPermission.Affiliate;

import lombok.Data;

@Data
public class ResellerOrders{
	private boolean approveCommission;
	private boolean downloadExportData;
	private boolean viewOrdersList;
	private boolean exportOrderReseller;
	private boolean rejectCommission;
}