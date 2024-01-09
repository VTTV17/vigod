package utilities.model.staffPermission.Affiliate;

import lombok.Data;

@Data
public class Affiliate{
	private ResellerCustomer resellerCustomer;
	private ResellerInventory resellerInventory;
	private ResellerInformation resellerInformation;
	private ResellerPartner resellerPartner;
	private PayoutHistory payoutHistory;
	private Commission commission;
	private DropshipOrders dropshipOrders;
	private DropshipInformation dropshipInformation;
	private ResellerPayout resellerPayout;
	private DropshipPartner dropshipPartner;
	private DropshipPayout dropshipPayout;
	private ResellerOrders resellerOrders;
}