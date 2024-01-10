package utilities.model.staffPermission.Affiliate;

import lombok.Data;

@Data
public class Affiliate {
    private ResellerCustomer resellerCustomer = new ResellerCustomer();
    private ResellerInventory resellerInventory = new ResellerInventory();
    private ResellerInformation resellerInformation = new ResellerInformation();
    private ResellerPartner resellerPartner = new ResellerPartner();
    private PayoutHistory payoutHistory = new PayoutHistory();
    private Commission commission = new Commission();
    private DropshipOrders dropshipOrders = new DropshipOrders();
    private DropshipInformation dropshipInformation = new DropshipInformation();
    private ResellerPayout resellerPayout = new ResellerPayout();
    private DropshipPartner dropshipPartner = new DropshipPartner();
    private DropshipPayout dropshipPayout = new DropshipPayout();
    private ResellerOrders resellerOrders = new ResellerOrders();
}