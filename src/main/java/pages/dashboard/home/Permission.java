package pages.dashboard.home;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.dashboard.analytics.OrderAnalytics;
import pages.dashboard.analytics.ReservationAnalytics;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.customers.segments.Segments;
import pages.dashboard.gochat.Facebook;
import pages.dashboard.gochat.Zalo;
import pages.dashboard.marketing.buylink.BuyLinkManagement;
import pages.dashboard.marketing.emailcampaign.EmailCampaignManagement;
import pages.dashboard.marketing.landingpage.LandingPage;
import pages.dashboard.marketing.loyaltypoint.LoyaltyPoint;
import pages.dashboard.marketing.loyaltyprogram.LoyaltyProgram;
import pages.dashboard.marketing.pushnotification.PushNotificationManagement;
import pages.dashboard.onlineshop.Domains;
import pages.dashboard.onlineshop.Themes;
import pages.dashboard.onlineshop.blog.BlogManagement;
import pages.dashboard.onlineshop.menus.MenuManagement;
import pages.dashboard.onlineshop.pages.PageManagement;
import pages.dashboard.onlineshop.preferences.Configuration;
import pages.dashboard.orders.pos.POSPage;
import pages.dashboard.orders.createquotation.CreateQuotation;
import pages.dashboard.orders.orderlist.OrderList;
import pages.dashboard.orders.returnorders.ReturnOrders;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.products.inventory.Inventory;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.dashboard.products.productreviews.ProductReviews;
import pages.dashboard.products.purchaseorders.PurchaseOrders;
import pages.dashboard.products.supplier.function.management.SupplierManagementPage;
import pages.dashboard.products.transfer.Transfer;
import pages.dashboard.promotion.discount.DiscountPage;
import pages.dashboard.promotion.flashsale.FlashSalePage;
import pages.dashboard.reservation.ReservationManagement;
import pages.dashboard.saleschannels.shopee.Shopee;
import pages.dashboard.service.ServiceManagementPage;
import pages.dashboard.service.servicecollections.ServiceCollectionManagement;
import pages.dashboard.settings.account.AccountPage;
import pages.dashboard.settings.bankaccountinformation.BankAccountInformation;
import pages.dashboard.settings.branch_management.BranchPage;
import pages.dashboard.settings.shippingandpayment.ShippingAndPayment;
import pages.dashboard.settings.staff_management.StaffPage;
import pages.dashboard.settings.storeinformation.StoreInformation;
import pages.dashboard.settings.storelanguages.StoreLanguages;
import pages.dashboard.settings.vat.VATInformation;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.model.sellerApp.login.LoginInformation;

public class Permission {

	final static Logger logger = LogManager.getLogger(Permission.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	LoginInformation loginInformation;
	public Permission(WebDriver driver, LoginInformation loginInformation) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
		this.loginInformation = loginInformation;
	}

	/**
	 * @param packageName Input value: GoWEB/GoAPP/GoPOS/GoSOCIAL/GoLEAD
	 */
	public void testPermission(String... packageName) {

		HomePage home = new HomePage(driver);

		Map<String, String> permission = getFeaturePermissions(packageName);

		Map<String, String> url = getFeatureURL();

		//Identify the shop's location to skip testing permissions of certain features (delivery/payment methods) against NON-VN shops
		home.navigateToPage("Settings");
		boolean isInVietnam = false;
		String location = new BranchPage(driver).navigate().getFreeBranchInfo().get(2);
		if (location.contains("Vietnam") || location.contains("Việt Nam")) isInVietnam = true;

		SortedSet<String> sortedKeys = new TreeSet<>(permission.keySet());
		for (String menuComponent : sortedKeys) {
			logger.debug("============: " + menuComponent + " =========: " + permission.get(menuComponent));
			String parentMenu = menuComponent.split("-")[0];
			String subMenu = menuComponent.split("-")[1];
			String function = menuComponent.split("-")[2];

			//Skip testing permissions of certain features (Gomua, Lazada) against NON-VN shops
			if (!isInVietnam) {
				if (parentMenu.contentEquals("GoMua") || parentMenu.contentEquals("Lazada")) {
					logger.info("Skipped as this feature is not available for NON-VN shops");
					continue;
				}
			}

			navigate(parentMenu, subMenu);

			switch (parentMenu) {
				case "Home":

					switch (function) {
						case "Statistics" -> {
							home.verifyPermissionToDisplayStatistics(permission.get(menuComponent));
						}
						case "Create Product" -> {
							home.verifyPermissionToCreateProduct(permission.get(menuComponent), loginInformation);
						}
						case "Import Product From Shopee" -> {
							home.verifyPermissionToImportProductFromShopee(permission.get(menuComponent),
									url.get(menuComponent));
						}
						case "Import Product From Lazada" -> {
							if (!isInVietnam) {
								logger.info("Skipped as this feature is not available for NON-VN shops");
								break;
							}
							home.verifyPermissionToImportProductFromLazada(permission.get(menuComponent),
									url.get(menuComponent));
						}
						case "Add Domain" -> {
							home.verifyPermissionToAddDomain(permission.get(menuComponent));
						}
						case "Add Bank Account" -> {
							home.verifyPermissionToAddBankAccount(permission.get(menuComponent));
						}
						case "Customize Appearance" -> {
							home.verifyPermissionToCustomizeAppearance(permission.get(menuComponent));
						}

					}
					break;
				case "GoChat":
					switch (function) {
						case "Facebook" -> {
							new Facebook(driver).verifyPermissionToConnectToFacebook(permission.get(menuComponent));
						}
						case "Zalo" -> {
							new Zalo(driver).verifyPermissionToConnectToZalo(permission.get(menuComponent));
						}
					}
					break;
				case "Products":
					home.hideFacebookBubble();
					switch (subMenu) {
						case "All Products" -> {
							switch (function) {
								case "Print Barcode" -> {
									new ProductPage(driver, loginInformation).verifyPermissionToPrintBarCode(permission.get(menuComponent));
								}
								case "Create Product" -> {
									new ProductPage(driver, loginInformation).verifyPermissionToCreateProduct(permission.get(menuComponent),
											url.get(menuComponent));
								}
								case "Create Variation Product" -> {
									new ProductPage(driver, loginInformation).verifyPermissionToCreateVariationProduct(permission.get(menuComponent));
								}
								case "Create Deposit Product" -> {
									new ProductPage(driver, loginInformation).verifyPermissionToCreateDepositProduct(permission.get(menuComponent));
								}
								case "Product SEO" -> {
									new ProductPage(driver, loginInformation).verifyPermissionToCreateProductSEO(permission.get(menuComponent));
								}
							}
						}
						case "Inventory" -> {
							switch (function) {
								case "Inventory History" -> {
									new Inventory(driver).verifyPermissionToSeeInventoryHistory(permission.get(menuComponent),
											url.get(menuComponent));
								}
							}
						}
						case "Transfer" -> {
							new Transfer(driver).verifyPermissionToTransferProduct(permission.get(menuComponent),
									url.get(menuComponent));
						}
					}

					if (subMenu.contentEquals("Product Collections")) {
						new ProductCollectionManagement(driver).verifyPermissionToCreateProductCollection(permission.get(menuComponent));
					}
					if (subMenu.contentEquals("Product Reviews")) {
						new ProductReviews(driver).verifyPermissionToManageReviews(permission.get(menuComponent));
					}
					if (subMenu.contentEquals("Supplier")) {
						new SupplierManagementPage(driver, loginInformation).verifyPermissionToManageSupplier(permission.get(menuComponent),
								url.get(menuComponent));
					}
					if (subMenu.contentEquals("Purchase Orders")) {
						new PurchaseOrders(driver).verifyPermissionToManagePurchaseOrders(permission.get(menuComponent), url.get(menuComponent));
					}
					break;
				case "Services":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("All Services")) {
						if (function.contentEquals("Create Service")) {
							new ServiceManagementPage(driver).verifyPermissionToManageServices(permission.get(menuComponent));
						}
						if (function.contentEquals("Service SEO")) {
							new ServiceManagementPage(driver).verifyPermissionToCreateServiceSEO(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Service Collections")) {
						new ServiceCollectionManagement(driver).verifyPermissionToManageServiceCollection(permission.get(menuComponent));
					}
					break;
				case "Orders":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Order List")) {
						if (function.contentEquals("Export Order")) {
							new OrderList(driver).verifyPermissionToExportOrder(permission.get(menuComponent));
						}
						if (function.contentEquals("Export Order By Product")) {
							new OrderList(driver).verifyPermissionToExportOrderByProduct(permission.get(menuComponent));
						}
						if (function.contentEquals("Export History")) {
							new OrderList(driver).verifyPermissionToExportHistory(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Return Orders")) {
						if (function.contentEquals("Create Return Order")) {
							new ReturnOrders(driver).verifyPermissionToCreateReturnedOrder(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Export Return Order")) {
							new ReturnOrders(driver).verifyPermissionToExportReturnedOrder(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Export History")) {
							new ReturnOrders(driver).verifyPermissionToExportHistory(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Create Quotation")) {
						new CreateQuotation(driver).verifyPermissionToCreateQuotation(permission.get(menuComponent), url.get(menuComponent));
					}
					if (subMenu.contentEquals("POS")) {
						new POSPage(driver).verifyPermissionToUsePOS(permission.get(menuComponent));
					}
					break;
				case "Reservations":
					navigate(parentMenu, subMenu);
					home.hideFacebookBubble();
					new ReservationManagement(driver).verifyPermissionToManageReservation(permission.get(menuComponent));
					break;
				case "Promotion":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Discount")) {

						// Product Discount Code
						if (function.contentEquals("Create Product Discount Code For Web Platform")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCodeForPlatform("Web", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Product Discount Code For App Platform")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCodeForPlatform("App", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Product Discount Code For Instore")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCodeForPlatform("In-store", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Product Discount Code As Reward")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCodeAsReward(permission.get(menuComponent), url.get(menuComponent));
						}

						// Service Discount Code
						else if (function.contentEquals("Create Service Discount Code For Web Platform")) {
							new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCodeForPlatform("Web", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Service Discount Code For App Platform")) {
							new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCodeForPlatform("App", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Service Discount Code For Instore")) {
							if (!permission.get("Services-All Services-Create Service").contentEquals("A")) {
								new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCodeForPlatform("In-store", "D", url.get(menuComponent));
							} else {
								new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCodeForPlatform("In-store", permission.get(menuComponent), url.get(menuComponent));
							}
						} else if (function.contentEquals("Create Service Discount Code As Reward")) {
							new DiscountPage(driver).verifyPermissionToServiceDiscountCodeAsReward(permission.get(menuComponent), url.get(menuComponent));
						}

						// Product Discount Campaign
						else if (function.contentEquals("Create Product Discount Campaign For All Products")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCampaignFor("All Products", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Product Discount Campaign For Specific Collections")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCampaignFor("Specific Collections", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Product Discount Campaign For Specific Products")) {
							new DiscountPage(driver).verifyPermissionToCreateProductDiscountCampaignFor("Specific Products", permission.get(menuComponent), url.get(menuComponent));
						}

						// Service Discount Campaign
						else if (function.contentEquals("Create Service Discount Campaign For All Services")) {
							new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCampaignFor("All Services", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Service Discount Campaign For Specific Collections")) {
							new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCampaignFor("Specific Collections", permission.get(menuComponent), url.get(menuComponent));
						} else if (function.contentEquals("Create Service Discount Campaign For Specific Services")) {
							new DiscountPage(driver).verifyPermissionToCreateServiceDiscountCampaignFor("Specific Services", permission.get(menuComponent), url.get(menuComponent));
						}
					}
					else if (subMenu.contentEquals("Flash Sale")) {
						new FlashSalePage(driver).verifyPermissionToCreateFlashSale(permission.get(menuComponent));
					}
					break;
				case "Customers":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("All Customers")) {
						if (function.contentEquals("Export Customers")) {
							new AllCustomers(driver).verifyPermissionToExportCustomer(permission.get(menuComponent));
						}
						if (function.contentEquals("Import Customers")) {
							new AllCustomers(driver).verifyPermissionToImportCustomer(permission.get(menuComponent));
						}
						if (function.contentEquals("Print Customer Barcode")) {
							new AllCustomers(driver).verifyPermissionToPrintBarCode(permission.get(menuComponent));
						}
					} else if (subMenu.contentEquals("Segments")) {
						String displayLanguage = new HomePage(driver).getDashboardLanguage();
						Segments segment = new Segments(driver);
						String data = null;
						if (function.contentEquals("Create Segment According To Registration Date")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.registrationDate", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByCustomerData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Customer Tag")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.customerTag", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByCustomerData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Installed App")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.installedApp", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByCustomerData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Order Delivered")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.orderDelivered", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByOrderData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Total Order Count")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.totalOrderCount", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByOrderData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Total Purchase Amount")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.totalPurchaseAmount", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByOrderData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Purchased Date")) {
							try {
								data = PropertiesUtil.getPropertiesValueByDBLang("customers.segments.create.condition.data.purchaseDate", displayLanguage);
							} catch (Exception e) {
								e.printStackTrace();
							}
							segment.verifyPermissionToCreateSegmentByOrderData(data, permission.get(menuComponent));
						}
						if (function.contentEquals("Create Segment According To Purchased Product")) {
							segment.verifyPermissionToCreateSegmentByPurchasedProduct(permission.get(menuComponent));
						}
					}
					break;
				case "Analytics":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Order Analytic")) {
						new OrderAnalytics(driver).verifyPermissionToUseOrderAnalytics(permission.get(menuComponent));
					}
					if (subMenu.contentEquals("Reservations Analytic")) {
						new ReservationAnalytics(driver).verifyPermissionToUseAnalytics(permission.get(menuComponent));
					}
					break;
				case "Marketing":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Landing page")) {
						if (function.contentEquals("Create Landing Page")) {
							new LandingPage(driver).verifyPermissionToCreateLandingPage(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Analytics")) {
							new LandingPage(driver).verifyPermissionToAddAnalyticsToLandingPage(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("SEO")) {
							new LandingPage(driver).verifyPermissionToAddSEOToLandingPage(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Tags")) {
							new LandingPage(driver).verifyPermissionToAddCustomerTagToLandingPage(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Custom Domain")) {
							new LandingPage(driver).verifyPermissionToCustomDomain(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Buy Link")) {
						if (function.contentEquals("Create Buy Link")) {
							new BuyLinkManagement(driver).verifyPermissionToCreateBuyLink(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Email Campaign")) {
						if (function.contentEquals("Create Email Campaign")) {
							new EmailCampaignManagement(driver).verifyPermissionToCreateEmailCampaign(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Push Notification")) {
						if (function.contentEquals("Create Push Notification")) {
							new PushNotificationManagement(driver).verifyPermissionToCreatePushNotification(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Loyalty Program")) {
						if (function.contentEquals("Create Loyalty Program")) {
							new LoyaltyProgram(driver).verifyPermissionToCreateLoyaltyProgram(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Loyalty Point")) {
						if (function.contentEquals("Configure Loyalty Point")) {
							new LoyaltyPoint(driver).verifyPermissionToConfigureLoyaltyPoint(permission.get(menuComponent), url.get(menuComponent));
						}
					}

					break;
				case "Online Shop":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Themes")) {
						if (function.contentEquals("Customize Appearance")) {
							new Themes(driver).verifyPermissionToCustomizeAppearance(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Blog")) {
						if (function.contentEquals("Add Article")) {
							new BlogManagement(driver).verifyPermissionToAddArticle(permission.get(menuComponent));
						}
						if (function.contentEquals("Category Management")) {
							new BlogManagement(driver).verifyPermissionToCreateCategory(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Pages")) {
						if (function.contentEquals("Create Page")) {
							new PageManagement(driver).verifyPermissionToCreatePage(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Menus")) {
						if (function.contentEquals("Add Menu")) {
							new MenuManagement(driver).verifyPermissionToAddMenu(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Domains")) {
						if (function.contentEquals("Edit SubDomain")) {
							new Domains(driver).verifyPermissionToEditSubDomain(permission.get(menuComponent));
						}
						if (function.contentEquals("Edit New Domain")) {
							new Domains(driver).verifyPermissionToEditNewDomain(permission.get(menuComponent));
						}
					}
					if (subMenu.contentEquals("Preferences")) {
						home.waitTillLoadingDotsDisappear();
						if (function.contentEquals("Enable Facebook Messenger")) {
							new Configuration(driver).verifyPermissionToEnableFacebookMessenger(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Enable Zalo OA Messenger")) {
							new Configuration(driver).verifyPermissionToEnableZaloOAMessenger(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Enable Facebook Login For Online Store")) {
							new Configuration(driver).verifyPermissionToEnableFacebookLoginForOnlineStore(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Configure Google Analytics")) {
							new Configuration(driver).verifyPermissionToConfigureGoogleAnalytics(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Configure Google Shopping")) {
							new Configuration(driver).verifyPermissionToConfigureGoogleShopping(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Configure Google Tag Manager")) {
							new Configuration(driver).verifyPermissionToConfigureGoogleTagManager(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Configure Facebook Pixel")) {
							new Configuration(driver).verifyPermissionToConfigureFacebookPixel(permission.get(menuComponent), url.get(menuComponent));
						}
						if (function.contentEquals("Configure Facebook App ID")) {
							new Configuration(driver).verifyPermissionToConfigureFacebookAppID(permission.get(menuComponent), url.get(menuComponent));
						}
					}
					break;
				case "Shopee":
					home.hideFacebookBubble();
					if (subMenu.contentEquals("Account Information")) {
						if (function.contentEquals("Connect Shopee")) {
							new Shopee(driver).verifyPermissionToConnectShopee(permission.get(menuComponent));
						}
					}
					break;
				case "Settings":
					home.hideFacebookBubble();
					if (function.contentEquals("Account Tab")) {
						new AccountPage(driver).verifyPermissionToUseAccountTab(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Shop Name In Store Information")) {
						new StoreInformation(driver).verifyPermissionToSetStoreName(permission.get(menuComponent));
					}
					if (function.contentEquals("Set App Name In Store Information")) {
						new StoreInformation(driver).verifyPermissionToSetAppName(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Hotline And Email In Store Information")) {
						new StoreInformation(driver).verifyPermissionToSetHotlineAndEmail(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Store Address In Store Information")) {
						new StoreInformation(driver).verifyPermissionToSetStoreAddress(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Social Media In Store Information")) {
						new StoreInformation(driver).verifyPermissionToSetSocialMedia(permission.get(menuComponent));
					}
					if (function.contentEquals("Set SEO")) {
						new StoreInformation(driver).verifyPermissionToSetSEO(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Trade Logo In Store Information")) {
						new StoreInformation(driver).verifyPermissionToEnableTradeLogo(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable GHTK")) {
						if (!isInVietnam) {
							logger.info("Skipped as this feature is not available for NON-VN shops");
							break;
						}
						new ShippingAndPayment(driver).verifyPermissionToEnableGHTK(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable GHN")) {
						if (!isInVietnam) {
							logger.info("Skipped as this feature is not available for NON-VN shops");
							break;
						}
						new ShippingAndPayment(driver).verifyPermissionToEnableGHN(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Ahamove")) {
						if (!isInVietnam) {
							logger.info("Skipped as this feature is not available for NON-VN shops");
							break;
						}
						new ShippingAndPayment(driver).verifyPermissionToEnableAhamove(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Self Delivery")) {
						new ShippingAndPayment(driver).verifyPermissionToEnableSelfDelivery(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Local ATM Card")) {
						if (!isInVietnam) {
							logger.info("Skipped as this feature is not available for NON-VN shops");
							break;
						}
						new ShippingAndPayment(driver).verifyPermissionToEnableLocalATMCard(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Credit Card")) {
						if (!isInVietnam) {
							logger.info("Skipped as this feature is not available for NON-VN shops");
							break;
						}
						new ShippingAndPayment(driver).verifyPermissionToEnableCreditCard(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Cash On Delivery")) {
						new ShippingAndPayment(driver).verifyPermissionToEnableCOD(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Cash")) {
						new ShippingAndPayment(driver).verifyPermissionToEnableCash(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Debt")) {
						new ShippingAndPayment(driver).verifyPermissionToEnableDebt(permission.get(menuComponent));
					}
					if (function.contentEquals("Enable Paypal")) {
						new ShippingAndPayment(driver).verifyPermissionToEnablePaypal(permission.get(menuComponent));
					}
					if (function.contentEquals("Set Bank Account Information")) {
						new BankAccountInformation(driver).verifyPermissionToSetBankAccountInfo(permission.get(menuComponent));
					}
					if (function.contentEquals("Add Staff")) {
						new StaffPage(driver).verifyPermissionToAddStaff(permission.get(menuComponent));
					}
					if (function.contentEquals("Add Branch")) {
						new BranchPage(driver).verifyPermissionToAddBranch(permission.get(menuComponent));
					}
					if (function.contentEquals("Configure VAT")) {
						new VATInformation(driver).verifyPermissionToConfigureVAT(permission.get(menuComponent));
					}
					if (function.contentEquals("Add Store Languages")) {
						new StoreLanguages(driver).verifyPermissionToAddLanguages(permission.get(menuComponent));
					}
					break;
				case "Cashbook":
					navigate(parentMenu, subMenu);
					break;
			}
		}
	}

	public void navigate(String parentMenu, String subMenu) {
		if (subMenu.contentEquals("Main") || parentMenu.contentEquals("Home")) {
			new HomePage(driver).navigateToPage(parentMenu);
		} else {
			new HomePage(driver).navigateToPage(parentMenu, subMenu);
		}
	}

	public Map<String, String> getFeaturePermissions(String... features) {
		Excel excel = new Excel();
		Sheet permissionSheet = null;
		Map<String, String> map = new HashMap<>();

		try {
			permissionSheet = excel.getSheet(FileNameAndPath.FILE_FEATURE_PERMISSION, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String feature : features) {
			int lastRowIndex = permissionSheet.getLastRowNum();
			for (int row = 1; row <= lastRowIndex; row++) {
				int menuColIndex = excel.getCellIndexByCellValue(permissionSheet.getRow(0), "MenuItem");
				int packageColIndex = excel.getCellIndexByCellValue(permissionSheet.getRow(0), feature);

				String permission = permissionSheet.getRow(row).getCell(packageColIndex).getStringCellValue();
				String menuItem = permissionSheet.getRow(row).getCell(menuColIndex).getStringCellValue();

				if (map.get(menuItem) != null) {
					if (map.get(menuItem).contentEquals("A")) {
						continue;
					}
					if (permission.contentEquals("A")) {
						map.put(menuItem, permission);
						continue;
					}

					if (map.get(menuItem).contentEquals("D")) {
						continue;
					}
					if (permission.contentEquals("D")) {
						map.put(menuItem, permission);
						continue;
					}
				}
				map.put(menuItem, permission);
			}
		}
		return map;
	}

	public Map<String, String> getFeatureURL() {
		Excel excel = new Excel();
		Sheet permissionSheet = null;
		Map<String, String> map = new HashMap<>();

		try {
			permissionSheet = excel.getSheet(FileNameAndPath.FILE_FEATURE_PERMISSION, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int lastRowIndex = permissionSheet.getLastRowNum();
		for (int row = 1; row <= lastRowIndex; row++) {
			int menuColIndex = excel.getCellIndexByCellValue(permissionSheet.getRow(0), "MenuItem");
			int urlColIndex = excel.getCellIndexByCellValue(permissionSheet.getRow(0), "URL");

			String url = permissionSheet.getRow(row).getCell(urlColIndex).getStringCellValue();
			String menuItem = permissionSheet.getRow(row).getCell(menuColIndex).getStringCellValue();

			map.put(menuItem, url);
		}

		return map;
	}
}
