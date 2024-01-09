package utilities.model.staffPermission;

import lombok.Data;
import utilities.model.staffPermission.Affiliate.Affiliate;
import utilities.model.staffPermission.Analytics.Analytics;
import utilities.model.staffPermission.CallCenter.CallCenter;
import utilities.model.staffPermission.Cashbook.Cashbook;
import utilities.model.staffPermission.Customer.Customer;
import utilities.model.staffPermission.GoChat.GoChat;
import utilities.model.staffPermission.GoWallet.GoWallet;
import utilities.model.staffPermission.Home.Home;
import utilities.model.staffPermission.Lazada.Lazada;
import utilities.model.staffPermission.Marketing.Marketing;
import utilities.model.staffPermission.OnlineStore.OnlineStore;
import utilities.model.staffPermission.Orders.Orders;
import utilities.model.staffPermission.Product.Product;
import utilities.model.staffPermission.Promotion.Promotion;
import utilities.model.staffPermission.Reservation.Reservation;
import utilities.model.staffPermission.Service.Service;
import utilities.model.staffPermission.Setting.Setting;
import utilities.model.staffPermission.Shopee.Shopee;
import utilities.model.staffPermission.Supplier.Suppliers;
import utilities.model.staffPermission.Tiktok.Tiktok;

@Data
public class AllPermissions{
	private Orders orders;
	private GoChat goChat;
	private Customer customer;
	private GoWallet goWallet;
	private Setting setting;
	private Product product;
	private Service service;
	private Tiktok tiktok;
	private Cashbook cashbook;
	private Promotion promotion;
	private Affiliate affiliate;
	private Analytics analytics;
	private Marketing marketing;
	private CallCenter callCenter;
	private Reservation reservation;
	private OnlineStore onlineStore;
	private Lazada lazada;
	private Shopee shopee;
	private Suppliers suppliers;
	private Home home;
}