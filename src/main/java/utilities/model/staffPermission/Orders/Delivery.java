package utilities.model.staffPermission.Orders;

import lombok.Data;

@Data
public class Delivery{
	private boolean addShipmentPackageBy3rdParty;
	private boolean viewDeliveryPackageDetail;
	private boolean printPackageSlip;
	private boolean viewDeliveryPackageList;
	private boolean addShipmentPackageBySelfDeliveryOther;
	private boolean updatePackageStatus;
}