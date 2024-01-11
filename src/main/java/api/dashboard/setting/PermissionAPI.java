package api.dashboard.setting;

import api.dashboard.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.PropertiesUtil;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.CreatePermission;

public class PermissionAPI {
	String CREATE_GROUP_PERMISSION_PATH = "/storeservice/api/authorized-group-permissions/store/%s";
	String EDIT_GROUP_PERMISSION_PATH = CREATE_GROUP_PERMISSION_PATH + "/group/%s";
	String GRANT_GROUP_PERMISSION_TO_STAFF_PATH = "/storeservice/api/store_staffs/add-staff-to-permission-group/%s";
	String REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH = "/storeservice/api/store_staffs/remove-staff-from-permission-group/%s/%s";
	API api = new API();
	LoginDashboardInfo loginInfo;

	public PermissionAPI(LoginInformation loginInformation) {
		loginInfo = new Login().getInfo(loginInformation);
	}

	public JsonPath createGroupPermission(String name, String description, CreatePermission model) {
		String body = """
				               {
				"name": "%s",
				"description": "%s",
				"storeId": "%s",
				"permissions": [{
					"firstLevel": "goWallet",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "home",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "facebook",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "zalo",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "smsCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "productManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "inventory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "transfer",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "collection",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "review",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "supplier",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "purchaseOrder",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "debt",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "lotDate",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "location",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "locationReceipt",
					"permissionInBinary": %s
				}, {
					"firstLevel": "service",
					"secondLevel": "serviceManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "service",
					"secondLevel": "serviceCollection",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "orderList",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "returnOrder",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "quotation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "pos",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "delivery",
					"permissionInBinary": %s
				}, {
					"firstLevel": "reservation",
					"secondLevel": "reservationManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "reservation",
					"secondLevel": "posService",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "discountCode",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "discountCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "buyXGetY",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "flashSale",
					"permissionInBinary": %s
				}, {
					"firstLevel": "customer",
					"secondLevel": "customerManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "customer",
					"secondLevel": "segment",
					"permissionInBinary": %s
				}, {
					"firstLevel": "callCenter",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "cashbook",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "analytics",
					"secondLevel": "orders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "analytics",
					"secondLevel": "reservation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "landingPage",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "buyLink",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "emailCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "pushNotification",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "loyaltyProgram",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "loyaltyPoint",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipPartner",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerPartner",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "commission",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipOrders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerOrders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipPayout",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerPayout",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerInventory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerCustomer",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "payoutHistory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "theme",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "blog",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "page",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "menu",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "domain",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "preferences",
					"permissionInBinary": %s
				}, {
					"firstLevel": "shopee",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "lazada",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "tiktok",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "account",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "storeInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "shippingAndPayment",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "bankAccount",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "staffManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "permission",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "branchManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "tax",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "storeLanguage",
					"permissionInBinary": %s
				}]
				               }""".formatted(name, description, loginInfo.getStoreID(),
				Integer.parseInt(model.getGoWallet_none(), 2), Integer.parseInt(model.getHome_none(), 2),
				Integer.parseInt(model.getGoChat_facebook(), 2), Integer.parseInt(model.getGoChat_zalo(), 2),
				Integer.parseInt(model.getGoChat_smsCampaign(), 2),
				Integer.parseInt(model.getProduct_productManagement(), 2),
				Integer.parseInt(model.getProduct_inventory(), 2), Integer.parseInt(model.getProduct_transfer(), 2),
				Integer.parseInt(model.getProduct_collection(), 2), Integer.parseInt(model.getProduct_review(), 2),
				Integer.parseInt(model.getSupplier_supplier(), 2),
				Integer.parseInt(model.getSupplier_purchaseOrder(), 2), Integer.parseInt(model.getSupplier_debt(), 2),
				Integer.parseInt(model.getProduct_lotDate(), 2), Integer.parseInt(model.getProduct_location(), 2),
				Integer.parseInt(model.getProduct_locationReceipt(), 2),
				Integer.parseInt(model.getService_serviceManagement(), 2),
				Integer.parseInt(model.getService_serviceCollection(), 2),
				Integer.parseInt(model.getOrders_orderList(), 2), Integer.parseInt(model.getOrders_returnOrder(), 2),
				Integer.parseInt(model.getOrders_quotation(), 2), Integer.parseInt(model.getOrders_pos(), 2),
				Integer.parseInt(model.getOrders_delivery(), 2),
				Integer.parseInt(model.getReservation_reservationManagement(), 2),
				Integer.parseInt(model.getReservation_posService(), 2),
				Integer.parseInt(model.getPromotion_discountCode(), 2),
				Integer.parseInt(model.getPromotion_discountCampaign(), 2),
				Integer.parseInt(model.getPromotion_buyXGetY(), 2), Integer.parseInt(model.getPromotion_flashSale(), 2),
				Integer.parseInt(model.getCustomer_customerManagement(), 2),
				Integer.parseInt(model.getCustomer_segment(), 2), Integer.parseInt(model.getCallCenter_none(), 2),
				Integer.parseInt(model.getCashbook_none(), 2), Integer.parseInt(model.getAnalytics_orders(), 2),
				Integer.parseInt(model.getAnalytics_reservation(), 2),
				Integer.parseInt(model.getMarketing_landingPage(), 2),
				Integer.parseInt(model.getMarketing_buyLink(), 2),
				Integer.parseInt(model.getMarketing_emailCampaign(), 2),
				Integer.parseInt(model.getMarketing_pushNotification(), 2),
				Integer.parseInt(model.getMarketing_loyaltyProgram(), 2),
				Integer.parseInt(model.getMarketing_loyaltyPoint(), 2),
				Integer.parseInt(model.getAffiliate_dropshipInformation(), 2),
				Integer.parseInt(model.getAffiliate_resellerInformation(), 2),
				Integer.parseInt(model.getAffiliate_dropshipPartner(), 2),
				Integer.parseInt(model.getAffiliate_resellerPartner(), 2),
				Integer.parseInt(model.getAffiliate_commission(), 2),
				Integer.parseInt(model.getAffiliate_dropshipOrders(), 2),
				Integer.parseInt(model.getAffiliate_resellerOrders(), 2),
				Integer.parseInt(model.getAffiliate_dropshipPayout(), 2),
				Integer.parseInt(model.getAffiliate_resellerPayout(), 2),
				Integer.parseInt(model.getAffiliate_resellerInventory(), 2),
				Integer.parseInt(model.getAffiliate_resellerCustomer(), 2),
				Integer.parseInt(model.getAffiliate_payoutHistory(), 2),
				Integer.parseInt(model.getOnlineStore_theme(), 2), Integer.parseInt(model.getOnlineStore_blog(), 2),
				Integer.parseInt(model.getOnlineStore_page(), 2), Integer.parseInt(model.getOnlineStore_menu(), 2),
				Integer.parseInt(model.getOnlineStore_domain(), 2),
				Integer.parseInt(model.getOnlineStore_preferences(), 2), Integer.parseInt(model.getShopee_none(), 2),
				Integer.parseInt(model.getLazada_none(), 2), Integer.parseInt(model.getTiktok_none(), 2),
				Integer.parseInt(model.getSetting_account(), 2),
				Integer.parseInt(model.getSetting_storeInformation(), 2),
				Integer.parseInt(model.getSetting_shippingAndPayment(), 2),
				Integer.parseInt(model.getSetting_bankAccount(), 2),
				Integer.parseInt(model.getSetting_staffManagement(), 2),
				Integer.parseInt(model.getSetting_permission(), 2),
				Integer.parseInt(model.getSetting_branchManagement(), 2), Integer.parseInt(model.getSetting_tax(), 2),
				Integer.parseInt(model.getSetting_storeLanguage(), 2));

		Response response = api.post(CREATE_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
		response.then().statusCode(201);

		return response.jsonPath();
	}

	public int createGroupPermissionAndGetID(String name, String description, CreatePermission model) {
		return createGroupPermission(name, description, model).getInt("id");
	}

	public JsonPath editGroupPermission(int id, String name, String description, CreatePermission model) {
		String body = """
				               {
				"id": "%s",
				"name": "%s",
				"description": "%s",
				"storeId": "%s",
				"permissions": [{
					"firstLevel": "goWallet",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "home",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "facebook",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "zalo",
					"permissionInBinary": %s
				}, {
					"firstLevel": "goChat",
					"secondLevel": "smsCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "productManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "inventory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "transfer",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "collection",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "review",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "supplier",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "purchaseOrder",
					"permissionInBinary": %s
				}, {
					"firstLevel": "supplier",
					"secondLevel": "debt",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "lotDate",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "location",
					"permissionInBinary": %s
				}, {
					"firstLevel": "product",
					"secondLevel": "locationReceipt",
					"permissionInBinary": %s
				}, {
					"firstLevel": "service",
					"secondLevel": "serviceManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "service",
					"secondLevel": "serviceCollection",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "orderList",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "returnOrder",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "quotation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "pos",
					"permissionInBinary": %s
				}, {
					"firstLevel": "orders",
					"secondLevel": "delivery",
					"permissionInBinary": %s
				}, {
					"firstLevel": "reservation",
					"secondLevel": "reservationManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "reservation",
					"secondLevel": "posService",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "discountCode",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "discountCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "buyXGetY",
					"permissionInBinary": %s
				}, {
					"firstLevel": "promotion",
					"secondLevel": "flashSale",
					"permissionInBinary": %s
				}, {
					"firstLevel": "customer",
					"secondLevel": "customerManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "customer",
					"secondLevel": "segment",
					"permissionInBinary": %s
				}, {
					"firstLevel": "callCenter",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "cashbook",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "analytics",
					"secondLevel": "orders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "analytics",
					"secondLevel": "reservation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "landingPage",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "buyLink",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "emailCampaign",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "pushNotification",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "loyaltyProgram",
					"permissionInBinary": %s
				}, {
					"firstLevel": "marketing",
					"secondLevel": "loyaltyPoint",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipPartner",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerPartner",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "commission",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipOrders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerOrders",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "dropshipPayout",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerPayout",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerInventory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "resellerCustomer",
					"permissionInBinary": %s
				}, {
					"firstLevel": "affiliate",
					"secondLevel": "payoutHistory",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "theme",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "blog",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "page",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "menu",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "domain",
					"permissionInBinary": %s
				}, {
					"firstLevel": "onlineStore",
					"secondLevel": "preferences",
					"permissionInBinary": %s
				}, {
					"firstLevel": "shopee",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "lazada",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "tiktok",
					"secondLevel": "none",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "account",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "storeInformation",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "shippingAndPayment",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "bankAccount",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "staffManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "permission",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "branchManagement",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "tax",
					"permissionInBinary": %s
				}, {
					"firstLevel": "setting",
					"secondLevel": "storeLanguage",
					"permissionInBinary": %s
				}]
				               }""".formatted(id, name, description, loginInfo.getStoreID(),
				Integer.parseInt(model.getGoWallet_none(), 2), Integer.parseInt(model.getHome_none(), 2),
				Integer.parseInt(model.getGoChat_facebook(), 2), Integer.parseInt(model.getGoChat_zalo(), 2),
				Integer.parseInt(model.getGoChat_smsCampaign(), 2),
				Integer.parseInt(model.getProduct_productManagement(), 2),
				Integer.parseInt(model.getProduct_inventory(), 2), Integer.parseInt(model.getProduct_transfer(), 2),
				Integer.parseInt(model.getProduct_collection(), 2), Integer.parseInt(model.getProduct_review(), 2),
				Integer.parseInt(model.getSupplier_supplier(), 2),
				Integer.parseInt(model.getSupplier_purchaseOrder(), 2), Integer.parseInt(model.getSupplier_debt(), 2),
				Integer.parseInt(model.getProduct_lotDate(), 2), Integer.parseInt(model.getProduct_location(), 2),
				Integer.parseInt(model.getProduct_locationReceipt(), 2),
				Integer.parseInt(model.getService_serviceManagement(), 2),
				Integer.parseInt(model.getService_serviceCollection(), 2),
				Integer.parseInt(model.getOrders_orderList(), 2), Integer.parseInt(model.getOrders_returnOrder(), 2),
				Integer.parseInt(model.getOrders_quotation(), 2), Integer.parseInt(model.getOrders_pos(), 2),
				Integer.parseInt(model.getOrders_delivery(), 2),
				Integer.parseInt(model.getReservation_reservationManagement(), 2),
				Integer.parseInt(model.getReservation_posService(), 2),
				Integer.parseInt(model.getPromotion_discountCode(), 2),
				Integer.parseInt(model.getPromotion_discountCampaign(), 2),
				Integer.parseInt(model.getPromotion_buyXGetY(), 2), Integer.parseInt(model.getPromotion_flashSale(), 2),
				Integer.parseInt(model.getCustomer_customerManagement(), 2),
				Integer.parseInt(model.getCustomer_segment(), 2), Integer.parseInt(model.getCallCenter_none(), 2),
				Integer.parseInt(model.getCashbook_none(), 2), Integer.parseInt(model.getAnalytics_orders(), 2),
				Integer.parseInt(model.getAnalytics_reservation(), 2),
				Integer.parseInt(model.getMarketing_landingPage(), 2),
				Integer.parseInt(model.getMarketing_buyLink(), 2),
				Integer.parseInt(model.getMarketing_emailCampaign(), 2),
				Integer.parseInt(model.getMarketing_pushNotification(), 2),
				Integer.parseInt(model.getMarketing_loyaltyProgram(), 2),
				Integer.parseInt(model.getMarketing_loyaltyPoint(), 2),
				Integer.parseInt(model.getAffiliate_dropshipInformation(), 2),
				Integer.parseInt(model.getAffiliate_resellerInformation(), 2),
				Integer.parseInt(model.getAffiliate_dropshipPartner(), 2),
				Integer.parseInt(model.getAffiliate_resellerPartner(), 2),
				Integer.parseInt(model.getAffiliate_commission(), 2),
				Integer.parseInt(model.getAffiliate_dropshipOrders(), 2),
				Integer.parseInt(model.getAffiliate_resellerOrders(), 2),
				Integer.parseInt(model.getAffiliate_dropshipPayout(), 2),
				Integer.parseInt(model.getAffiliate_resellerPayout(), 2),
				Integer.parseInt(model.getAffiliate_resellerInventory(), 2),
				Integer.parseInt(model.getAffiliate_resellerCustomer(), 2),
				Integer.parseInt(model.getAffiliate_payoutHistory(), 2),
				Integer.parseInt(model.getOnlineStore_theme(), 2), Integer.parseInt(model.getOnlineStore_blog(), 2),
				Integer.parseInt(model.getOnlineStore_page(), 2), Integer.parseInt(model.getOnlineStore_menu(), 2),
				Integer.parseInt(model.getOnlineStore_domain(), 2),
				Integer.parseInt(model.getOnlineStore_preferences(), 2), Integer.parseInt(model.getShopee_none(), 2),
				Integer.parseInt(model.getLazada_none(), 2), Integer.parseInt(model.getTiktok_none(), 2),
				Integer.parseInt(model.getSetting_account(), 2),
				Integer.parseInt(model.getSetting_storeInformation(), 2),
				Integer.parseInt(model.getSetting_shippingAndPayment(), 2),
				Integer.parseInt(model.getSetting_bankAccount(), 2),
				Integer.parseInt(model.getSetting_staffManagement(), 2),
				Integer.parseInt(model.getSetting_permission(), 2),
				Integer.parseInt(model.getSetting_branchManagement(), 2), Integer.parseInt(model.getSetting_tax(), 2),
				Integer.parseInt(model.getSetting_storeLanguage(), 2));

		Response response = api.put(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), id), loginInfo.getAccessToken(), body);
		response.then().statusCode(200);

		return response.jsonPath();
	}

	public int editGroupPermissionAndGetID(int groupID, String name, String description, CreatePermission model) {
		return editGroupPermission(groupID, name, description, model).getInt("id");
	}	

	
	public void deleteGroupPermission(int groupID) {
		Response response = api.delete(EDIT_GROUP_PERMISSION_PATH.formatted(loginInfo.getStoreID(), groupID), loginInfo.getAccessToken());
		response.then().statusCode(204);
	}		
	
	public void grantGroupPermissionToStaff(int staffID, int groupID) {
        String body = """
                {
        		"staffIds": "%s"
                }""".formatted(staffID);
        Response response = api.post(GRANT_GROUP_PERMISSION_TO_STAFF_PATH.formatted(groupID), loginInfo.getAccessToken(), body);
        response.then().statusCode(200);
	}	
	
	public void removeGroupPermissionFromStaff(int staffID, int groupID) {
		Response response = api.delete(REMOVE_GROUP_PERMISSION_FROM_STAFF_PATH.formatted(groupID, staffID), loginInfo.getAccessToken());
		response.then().statusCode(200);
	}	
	
	public static void main(String[] args) {
		PropertiesUtil.setEnvironment("STAG");
		
		LoginInformation ownerCredentials = new Login().setLoginInformation("+84", "phu.staging.vn@mailnesia.com", "tma_13Tma").getLoginInformation();
		LoginInformation staffCredentials = new Login().setLoginInformation("+84", "staff.a@mailnesia.com", "fortesting!1").getLoginInformation();
		
		LoginDashboardInfo staffLoginInfo = new Login().getStaffInfo(staffCredentials);
		
		CreatePermission model = new CreatePermission();
		model.setHome_none("11");
		model.setPromotion_discountCode("1101000000");
		model.setGoWallet_none("010");
		model.setGoChat_smsCampaign("0111111");
		model.setReservation_posService("111111111111111");
		model.setCashbook_none("111111111111");

		int groupPermissionId = new PermissionAPI(ownerCredentials).createGroupPermissionAndGetID("Create Tien's Permission", "Create Description Tien's Permission", model);
		
		new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Tien's Permission", "Description Tien's Permission", model);
		
		int staffId = new StaffManagement(ownerCredentials).getStaffId(staffLoginInfo.getSellerID());
		
		new PermissionAPI(ownerCredentials).grantGroupPermissionToStaff(staffId, groupPermissionId);
		
		new PermissionAPI(ownerCredentials).removeGroupPermissionFromStaff(staffId, groupPermissionId);
		
		new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
	}

}
