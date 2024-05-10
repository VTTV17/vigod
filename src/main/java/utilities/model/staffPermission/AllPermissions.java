package utilities.model.staffPermission;

import io.restassured.path.json.JsonPath;
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

import java.util.Base64;
import java.util.LinkedHashMap;

import static java.lang.Integer.toBinaryString;

@Data
public class AllPermissions {
    private Orders orders = new Orders();
    private GoChat goChat = new GoChat();
    private Customer customer = new Customer();
    private GoWallet goWallet = new GoWallet();
    private Setting setting = new Setting();
    private Product product = new Product();
    private Service service = new Service();
    private Tiktok tiktok = new Tiktok();
    private Cashbook cashbook = new Cashbook();
    private Promotion promotion = new Promotion();
    private Affiliate affiliate = new Affiliate();
    private Analytics analytics = new Analytics();
    private Marketing marketing = new Marketing();
    private CallCenter callCenter = new CallCenter();
    private Reservation reservation = new Reservation();
    private OnlineStore onlineStore = new OnlineStore();
    private Lazada lazada = new Lazada();
    private Shopee shopee = new Shopee();
    private Suppliers suppliers = new Suppliers();
    private Home home = new Home();

    public AllPermissions() {
    }

    boolean checkPermission(String binaryPermission, int bitIndex) {
        return (bitIndex <= (binaryPermission.length() - 1)) && (binaryPermission.charAt(binaryPermission.length() - bitIndex - 1) == '1');
    }

    String getBinaryPermissionString(String payloadJson, String path) {
        try {
            return toBinaryString(JsonPath.from(payloadJson).getInt(path));
        } catch (NullPointerException ex) {
            return toBinaryString(0);
        }
    }

    void setGoWallet(String payloadJson) {
        // parse GoWallet
        String goWalletPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"goWallet-none\"]");
        goWallet.setViewGoWallet(checkPermission(goWalletPermission, 0));
        goWallet.setTopUpGoWallet(checkPermission(goWalletPermission, 1));
        goWallet.setChangAPIN(checkPermission(goWalletPermission, 2));
    }

    void setHome(String payloadJson) {
        // parse Home
        String homePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"home-none\"]");
        home.setNotification(checkPermission(homePermission, 0));
        home.setChangLanguage(checkPermission(homePermission, 1));
    }

    void setGoChat(String payloadJson) {
        // parse GoChat - Facebook
        String facebookPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"goChat-facebook\"]");
        goChat.getFacebook().setConnectAccount(checkPermission(facebookPermission, 0));
        goChat.getFacebook().setDisconnectAccount(checkPermission(facebookPermission, 1));
        goChat.getFacebook().setAddRemoveFBPage(checkPermission(facebookPermission, 2));
        goChat.getFacebook().setConnectPage(checkPermission(facebookPermission, 3));
        goChat.getFacebook().setDisconnectPage(checkPermission(facebookPermission, 4));
        goChat.getFacebook().setViewAllConversations(checkPermission(facebookPermission, 5));
        goChat.getFacebook().setViewAssignedConversation(checkPermission(facebookPermission, 6));
        goChat.getFacebook().setAssignStaffToConversation(checkPermission(facebookPermission, 7));
        goChat.getFacebook().setUnassignStaffFromConversation(checkPermission(facebookPermission, 8));
        goChat.getFacebook().setCreateNewTag(checkPermission(facebookPermission, 9));
        goChat.getFacebook().setDeleteTag(checkPermission(facebookPermission, 10));
        goChat.getFacebook().setHideTag(checkPermission(facebookPermission, 11));
        goChat.getFacebook().setAddTagToConversation(checkPermission(facebookPermission, 12));
        goChat.getFacebook().setRemoveTagFromConversation(checkPermission(facebookPermission, 13));
        goChat.getFacebook().setLinkCustomerWithFBUser(checkPermission(facebookPermission, 14));
        goChat.getFacebook().setUnlinkCustomerWithFBUser(checkPermission(facebookPermission, 15));
        goChat.getFacebook().setSendAMessage(checkPermission(facebookPermission, 16));
        goChat.getFacebook().setCreateOrder(checkPermission(facebookPermission, 17));
        goChat.getFacebook().setViewAutomationCampaignList(checkPermission(facebookPermission, 18));
        goChat.getFacebook().setViewAutomationCampaignDetail(checkPermission(facebookPermission, 19));
        goChat.getFacebook().setCreateAutomationCampaign(checkPermission(facebookPermission, 20));
        goChat.getFacebook().setEditAutomationCampaign(checkPermission(facebookPermission, 21));
        goChat.getFacebook().setDeleteAutomationCampaign(checkPermission(facebookPermission, 22));
        goChat.getFacebook().setViewBroadcastCampaignList(checkPermission(facebookPermission, 23));
        goChat.getFacebook().setViewBroadcastCampaignDetail(checkPermission(facebookPermission, 24));
        goChat.getFacebook().setCreateBroadcastCampaign(checkPermission(facebookPermission, 25));
        goChat.getFacebook().setEditBroadcastCampaign(checkPermission(facebookPermission, 26));
        goChat.getFacebook().setDeleteBroadcastCampaign(checkPermission(facebookPermission, 27));

        // parse GoChat - Zalo
        String zaloPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"goChat-zalo\"]");
        goChat.getZalo().setConnectOA(checkPermission(zaloPermission, 0));
        goChat.getZalo().setDisconnectOA(checkPermission(zaloPermission, 1));
        goChat.getZalo().setViewAllConversations(checkPermission(zaloPermission, 2));
        goChat.getZalo().setViewAssignedConversation(checkPermission(zaloPermission, 3));
        goChat.getZalo().setAssignStaffToConversation(checkPermission(zaloPermission, 4));
        goChat.getZalo().setUnassignStaffFromConversation(checkPermission(zaloPermission, 5));
        goChat.getZalo().setCreateNewTag(checkPermission(zaloPermission, 6));
        goChat.getZalo().setDeleteTag(checkPermission(zaloPermission, 7));
        goChat.getZalo().setHideTag(checkPermission(zaloPermission, 8));
        goChat.getZalo().setAddTagToConversation(checkPermission(zaloPermission, 9));
        goChat.getZalo().setRemoveTagFromConversation(checkPermission(zaloPermission, 10));
        goChat.getZalo().setLinkCustomerWithZaloUser(checkPermission(zaloPermission, 11));
        goChat.getZalo().setUnlinkCustomerWithZaloUser(checkPermission(zaloPermission, 12));
        goChat.getZalo().setSendAMessage(checkPermission(zaloPermission, 13));
        goChat.getZalo().setCreateOrder(checkPermission(zaloPermission, 14));
        goChat.getZalo().setCreateCampaigns(checkPermission(zaloPermission, 15));
        goChat.getZalo().setViewCampaigns(checkPermission(zaloPermission, 16));
        goChat.getZalo().setEditCampaigns(checkPermission(zaloPermission, 17));
        goChat.getZalo().setDeleteCampaigns(checkPermission(zaloPermission, 18));
        goChat.getZalo().setCreateTemplates(checkPermission(zaloPermission, 19));
        goChat.getZalo().setViewTemplates(checkPermission(zaloPermission, 20));
        goChat.getZalo().setEditTemplates(checkPermission(zaloPermission, 21));
        goChat.getZalo().setDeleteTemplates(checkPermission(zaloPermission, 22));
        goChat.getZalo().setPublishCampaigns(checkPermission(zaloPermission, 23));
        goChat.getZalo().setPublishTemplates(checkPermission(zaloPermission, 24));

        // parse GoChat - SMS campaign
        String smsCampaignPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"goChat-smsCampaign\"]");
        goChat.getSMSCampaign().setViewAllSMSCampaignList(checkPermission(smsCampaignPermission, 0));
        goChat.getSMSCampaign().setViewSMSDetail(checkPermission(smsCampaignPermission, 1));
        goChat.getSMSCampaign().setCreateSMSCampaign(checkPermission(smsCampaignPermission, 2));
        goChat.getSMSCampaign().setUpdateSMSCampaign(checkPermission(smsCampaignPermission, 3));
        goChat.getSMSCampaign().setRegisterSMSBrandName(checkPermission(smsCampaignPermission, 4));
        goChat.getSMSCampaign().setRegisterSMSTemplate(checkPermission(smsCampaignPermission, 5));
        goChat.getSMSCampaign().setRegisterSMSAccount(checkPermission(smsCampaignPermission, 6));
    }

    void setSupplier(String payloadJson) {
        // parse Supplier - Supplier
        String supplierPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"supplier-supplier\"]");
        suppliers.getSupplier().setViewSupplierList(checkPermission(supplierPermission, 0));
        suppliers.getSupplier().setViewSupplierDetail(checkPermission(supplierPermission, 1));
        suppliers.getSupplier().setAddSupplier(checkPermission(supplierPermission, 2));
        suppliers.getSupplier().setEditSupplier(checkPermission(supplierPermission, 3));
        suppliers.getSupplier().setDeleteSupplier(checkPermission(supplierPermission, 4));

        // parse Supplier - Purchase order
        String purchaseOrderPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"supplier-purchaseOrder\"]");
        suppliers.getPurchaseOrder().setViewPurchaseOrderList(checkPermission(purchaseOrderPermission, 0));
        suppliers.getPurchaseOrder().setViewListCreatedPurchaseOrder(checkPermission(purchaseOrderPermission, 1));
        suppliers.getPurchaseOrder().setViewPurchaseOrderDetail(checkPermission(purchaseOrderPermission, 2));
        suppliers.getPurchaseOrder().setCreatePurchaseOrder(checkPermission(purchaseOrderPermission, 3));
        suppliers.getPurchaseOrder().setEditPurchaseOrder(checkPermission(purchaseOrderPermission, 4));
        suppliers.getPurchaseOrder().setApprovePurchaseOrder(checkPermission(purchaseOrderPermission, 5));
        suppliers.getPurchaseOrder().setCompletePurchaseOrder(checkPermission(purchaseOrderPermission, 6));
        suppliers.getPurchaseOrder().setViewPurchaseOrderHistory(checkPermission(purchaseOrderPermission, 7));
        suppliers.getPurchaseOrder().setPrintPurchaseOrderReceipt(checkPermission(purchaseOrderPermission, 8));
        suppliers.getPurchaseOrder().setCancelPurchaseOrder(checkPermission(purchaseOrderPermission, 9));

        // parse Supplier - Debt
        String debtPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"supplier-debt\"]");
        suppliers.getDebt().setViewDebtHistory(checkPermission(debtPermission, 0));
        suppliers.getDebt().setCreateANewDebt(checkPermission(debtPermission, 1));
        suppliers.getDebt().setEditADebt(checkPermission(debtPermission, 2));
        suppliers.getDebt().setDeleteADebt(checkPermission(debtPermission, 3));
        suppliers.getDebt().setPublicADebt(checkPermission(debtPermission, 4));
        suppliers.getDebt().setMakeADebtRepayment(checkPermission(debtPermission, 5));
    }

    void setProduct(String payloadJson) {
        // parse Products - Product management
        String productManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-productManagement\"]");
        product.getProductManagement().setViewProductList(checkPermission(productManagementPermission, 0));
        product.getProductManagement().setViewCreatedProductList(checkPermission(productManagementPermission, 1));
        product.getProductManagement().setViewProductDetail(checkPermission(productManagementPermission, 2));
        product.getProductManagement().setCreateProduct(checkPermission(productManagementPermission, 3));
        product.getProductManagement().setEditProduct(checkPermission(productManagementPermission, 4));
        product.getProductManagement().setDeleteProduct(checkPermission(productManagementPermission, 5));
        product.getProductManagement().setAddVariation(checkPermission(productManagementPermission, 6));
        product.getProductManagement().setDeleteVariation(checkPermission(productManagementPermission, 7));
        product.getProductManagement().setActivateProduct(checkPermission(productManagementPermission, 8));
        product.getProductManagement().setDeactivateProduct(checkPermission(productManagementPermission, 9));
        product.getProductManagement().setExportProducts(checkPermission(productManagementPermission, 10));
        product.getProductManagement().setImportProducts(checkPermission(productManagementPermission, 11));
        product.getProductManagement().setPrintBarcode(checkPermission(productManagementPermission, 12));
        product.getProductManagement().setDownloadExportProduct(checkPermission(productManagementPermission, 13));
        product.getProductManagement().setUpdateWholesalePrice(checkPermission(productManagementPermission, 14));
        product.getProductManagement().setEditTax(checkPermission(productManagementPermission, 15));
        product.getProductManagement().setEditPrice(checkPermission(productManagementPermission, 16));
        product.getProductManagement().setViewProductCostPrice(checkPermission(productManagementPermission, 17));
        product.getProductManagement().setEditSEOData(checkPermission(productManagementPermission, 18));
        product.getProductManagement().setEditTranslation(checkPermission(productManagementPermission, 19));

        // parse Product - Inventory
        String inventoryPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-inventory\"]");
        product.getInventory().setViewProductInventory(checkPermission(inventoryPermission, 0));
        product.getInventory().setViewCreatedProductInventory(checkPermission(inventoryPermission, 1));
        product.getInventory().setViewInventoryHistory(checkPermission(inventoryPermission, 2));
        product.getInventory().setExportInventoryHistory(checkPermission(inventoryPermission, 3));
        product.getInventory().setClearStock(checkPermission(inventoryPermission, 4));
        product.getInventory().setUpdateStock(checkPermission(inventoryPermission, 5));
        product.getInventory().setDownloadExportedProduct(checkPermission(inventoryPermission, 6));

        // parse Product - Transfer
        String transferPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-transfer\"]");
        product.getTransfer().setViewTransferList(checkPermission(transferPermission, 0));
        product.getTransfer().setViewTransferDetail(checkPermission(transferPermission, 1));
        product.getTransfer().setCreateTransfer(checkPermission(transferPermission, 2));
        product.getTransfer().setEditTransfer(checkPermission(transferPermission, 3));
        product.getTransfer().setConfirmShipGoods(checkPermission(transferPermission, 4));
        product.getTransfer().setConfirmReceivedGoods(checkPermission(transferPermission, 5));
        product.getTransfer().setCancelTransfer(checkPermission(transferPermission, 6));

        // parse Product - Collection
        String collectionPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-collection\"]");
        product.getCollection().setViewCollectionList(checkPermission(collectionPermission, 0));
        product.getCollection().setViewCollectionDetail(checkPermission(collectionPermission, 1));
        product.getCollection().setCreateCollection(checkPermission(collectionPermission, 2));
        product.getCollection().setEditCollection(checkPermission(collectionPermission, 3));
        product.getCollection().setDeleteCollection(checkPermission(collectionPermission, 4));
        product.getCollection().setEditTranslation(checkPermission(collectionPermission, 5));

        // parse Product - Review
        String reviewPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-review\"]");
        product.getReview().setViewReview(checkPermission(reviewPermission, 0));
        product.getReview().setEnableReviewFeature(checkPermission(reviewPermission, 1));
        product.getReview().setDisableReviewFeature(checkPermission(reviewPermission, 2));
        product.getReview().setHideAReview(checkPermission(reviewPermission, 3));
        product.getReview().setShowAReview(checkPermission(reviewPermission, 4));

        // parse Product - LotDate
        String lotDatePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-lotDate\"]");
        product.getLotDate().setViewLotList(checkPermission(lotDatePermission, 0));
        product.getLotDate().setViewLotDetail(checkPermission(lotDatePermission, 1));
        product.getLotDate().setCreateLot(checkPermission(lotDatePermission, 2));
        product.getLotDate().setEditLot(checkPermission(lotDatePermission, 3));
        product.getLotDate().setDeleteLot(checkPermission(lotDatePermission, 4));
        product.getLotDate().setImportLot(checkPermission(lotDatePermission, 5));
        product.getLotDate().setEnableProductLot(checkPermission(lotDatePermission, 6));

        // parse Product - Location
        String locationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-location\"]");
        product.getLocation().setViewLocationList(checkPermission(locationPermission, 0));
        product.getLocation().setViewLocationDetail(checkPermission(locationPermission, 1));
        product.getLocation().setAddLocation(checkPermission(locationPermission, 2));
        product.getLocation().setEditLocation(checkPermission(locationPermission, 3));
        product.getLocation().setDeleteLocation(checkPermission(locationPermission, 4));

        // parse Product - Location receipt
        String locationReceiptPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"product-locationReceipt\"]");
        product.getLocationReceipt().setViewGetProductLocationReceiptList(checkPermission(locationReceiptPermission, 0));
        product.getLocationReceipt().setViewAddProductLocationReceiptList(checkPermission(locationReceiptPermission, 1));
        product.getLocationReceipt().setViewAddProductLocationReceiptDetail(checkPermission(locationReceiptPermission, 2));
        product.getLocationReceipt().setViewGetProductLocationReceiptDetail(checkPermission(locationReceiptPermission, 3));
        product.getLocationReceipt().setCreateDraftAddProductReceipt(checkPermission(locationReceiptPermission, 4));
        product.getLocationReceipt().setCreateCompletedAddProductReceipt(checkPermission(locationReceiptPermission, 5));
        product.getLocationReceipt().setCreateDraftGetProductReceipt(checkPermission(locationReceiptPermission, 6));
        product.getLocationReceipt().setCreateCompletedGetProductReceipt(checkPermission(locationReceiptPermission, 7));
        product.getLocationReceipt().setDeleteDraftAddProductReceipt(checkPermission(locationReceiptPermission, 8));
        product.getLocationReceipt().setDeleteDraftGetProductReceipt(checkPermission(locationReceiptPermission, 9));
        product.getLocationReceipt().setEditAddProductReceipt(checkPermission(locationReceiptPermission, 10));
        product.getLocationReceipt().setEditGetProductReceipt(checkPermission(locationReceiptPermission, 11));
        product.getLocationReceipt().setCompleteAddProductReceipt(checkPermission(locationReceiptPermission, 12));
        product.getLocationReceipt().setCompleteGetProductReceipt(checkPermission(locationReceiptPermission, 13));
        product.getLocationReceipt().setImportProductToLocation(checkPermission(locationReceiptPermission, 14));
    }

    void setService(String payloadJson) {
        // parse Service - Service management
        String serviceManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"service-serviceManagement\"]");
        service.getServiceManagement().setViewListService(checkPermission(serviceManagementPermission, 0));
        service.getServiceManagement().setViewListCreatedService(checkPermission(serviceManagementPermission, 1));
        service.getServiceManagement().setViewServiceDetail(checkPermission(serviceManagementPermission, 2));
        service.getServiceManagement().setCreateService(checkPermission(serviceManagementPermission, 3));
        service.getServiceManagement().setEditService(checkPermission(serviceManagementPermission, 4));
        service.getServiceManagement().setActivateService(checkPermission(serviceManagementPermission, 5));
        service.getServiceManagement().setDeactivateService(checkPermission(serviceManagementPermission, 6));
        service.getServiceManagement().setDeleteService(checkPermission(serviceManagementPermission, 7));

        // parse Service - Collection
        String collectionPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"service-serviceCollection\"]");
        service.getServiceCollection().setViewCollectionList(checkPermission(collectionPermission, 0));
        service.getServiceCollection().setViewCollectionDetail(checkPermission(collectionPermission, 1));
        service.getServiceCollection().setCreateCollection(checkPermission(collectionPermission, 2));
        service.getServiceCollection().setEditCollection(checkPermission(collectionPermission, 3));
        service.getServiceCollection().setDeleteCollection(checkPermission(collectionPermission, 4));
    }

    void setOrders(String payloadJson) {
        // parse Orders - Order list
        String orderManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"orders-orderList\"]");
        orders.getOrderManagement().setViewOrderList(checkPermission(orderManagementPermission, 0));
        orders.getOrderManagement().setViewCreatedOrderList(checkPermission(orderManagementPermission, 1));
        orders.getOrderManagement().setViewOrderDetail(checkPermission(orderManagementPermission, 2));
        orders.getOrderManagement().setConfirmOrder(checkPermission(orderManagementPermission, 3));
        orders.getOrderManagement().setEditOrder(checkPermission(orderManagementPermission, 4));
        orders.getOrderManagement().setCancelOrder(checkPermission(orderManagementPermission, 5));
        orders.getOrderManagement().setDeliveredOrders(checkPermission(orderManagementPermission, 6));
        orders.getOrderManagement().setPrintOrderSlip(checkPermission(orderManagementPermission, 7));
        orders.getOrderManagement().setPrintOrderReceipt(checkPermission(orderManagementPermission, 8));
        orders.getOrderManagement().setExportOrder(checkPermission(orderManagementPermission, 9));
        orders.getOrderManagement().setExportOrderByProduct(checkPermission(orderManagementPermission, 10));
        orders.getOrderManagement().setCreateOrderCost(checkPermission(orderManagementPermission, 11));
        orders.getOrderManagement().setViewOrderCostList(checkPermission(orderManagementPermission, 12));
        orders.getOrderManagement().setAddCostToOrder(checkPermission(orderManagementPermission, 13));
        orders.getOrderManagement().setCreateOrderTag(checkPermission(orderManagementPermission, 14));
        orders.getOrderManagement().setAddTagToOrder(checkPermission(orderManagementPermission, 15));
        orders.getOrderManagement().setRemoveTagFromOrder(checkPermission(orderManagementPermission, 16));
        orders.getOrderManagement().setViewTagList(checkPermission(orderManagementPermission, 17));
        orders.getOrderManagement().setDeleteTag(checkPermission(orderManagementPermission, 18));
        orders.getOrderManagement().setDisplayOrderSetting(checkPermission(orderManagementPermission, 19));
        orders.getOrderManagement().setDownloadExportedOrders(checkPermission(orderManagementPermission, 20));
        orders.getOrderManagement().setConfirmPayment(checkPermission(orderManagementPermission, 21));
        orders.getOrderManagement().setApplyDiscount(checkPermission(orderManagementPermission, 22));
        orders.getOrderManagement().setPrintOrders(checkPermission(orderManagementPermission, 23));

        // parse Orders - Return order
        String returnOrderPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"orders-returnOrder\"]");
        orders.getReturnOrder().setViewOrderReturnList(checkPermission(returnOrderPermission, 0));
        orders.getReturnOrder().setViewOrderReturnDetail(checkPermission(returnOrderPermission, 1));
        orders.getReturnOrder().setCreateReturnOrder(checkPermission(returnOrderPermission, 2));
        orders.getReturnOrder().setEditReturnOrder(checkPermission(returnOrderPermission, 3));
        orders.getReturnOrder().setRestockGoods(checkPermission(returnOrderPermission, 4));
        orders.getReturnOrder().setCompleteReturnOrder(checkPermission(returnOrderPermission, 5));
        orders.getReturnOrder().setCancelReturnOrder(checkPermission(returnOrderPermission, 6));
        orders.getReturnOrder().setConfirmPayment(checkPermission(returnOrderPermission, 7));

        // parse Orders - Quotation
        String quotationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"orders-quotation\"]");
        orders.getQuotation().setCreateQuotation(checkPermission(quotationPermission, 0));

        // parse Orders - POS
        String posPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"orders-pos\"]");
        orders.getPOSInstorePurchase().setCreateOrder(checkPermission(posPermission, 0));
        orders.getPOSInstorePurchase().setAddDirectDiscount(checkPermission(posPermission, 1));
        orders.getPOSInstorePurchase().setApplyDiscountCode(checkPermission(posPermission, 2));
        orders.getPOSInstorePurchase().setCreateDebtOrder(checkPermission(posPermission, 3));
        orders.getPOSInstorePurchase().setNotApplyEarningPoint(checkPermission(posPermission, 4));

        // parse Orders - Delivery
        String deliveryPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"orders-delivery\"]");
        orders.getDelivery().setViewDeliveryPackageList(checkPermission(deliveryPermission, 0));
        orders.getDelivery().setViewDeliveryPackageDetail(checkPermission(deliveryPermission, 1));
        orders.getDelivery().setUpdatePackageStatus(checkPermission(deliveryPermission, 2));
        orders.getDelivery().setAddShipmentPackageBy3rdParty(checkPermission(deliveryPermission, 3));
        orders.getDelivery().setAddShipmentPackageBySelfDeliveryOther(checkPermission(deliveryPermission, 4));
        orders.getDelivery().setPrintPackageSlip(checkPermission(deliveryPermission, 5));
    }

    void setReservation(String payloadJson) {
        // parse Reservation - Reservation management
        String reservationManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"reservation-reservationManagement\"]");
        reservation.getReservationManagement().setViewReservationList(checkPermission(reservationManagementPermission, 0));
        reservation.getReservationManagement().setViewReservationDetail(checkPermission(reservationManagementPermission, 1));
        reservation.getReservationManagement().setEditReservation(checkPermission(reservationManagementPermission, 2));
        reservation.getReservationManagement().setConfirmReservation(checkPermission(reservationManagementPermission, 3));
        reservation.getReservationManagement().setCompleteReservation(checkPermission(reservationManagementPermission, 4));
        reservation.getReservationManagement().setCancelReservation(checkPermission(reservationManagementPermission, 5));

        // parse Reservation - POS
        String posServicePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"reservation-posService\"]");
        reservation.getPOSService().setCreateReservation(checkPermission(posServicePermission, 0));
        reservation.getPOSService().setApplyDirectDiscount(checkPermission(posServicePermission, 1));
        reservation.getPOSService().setApplyDiscountCode(checkPermission(posServicePermission, 2));
    }

    void setSetting(String payloadJson) {
        // parse Setting - Account
        String accountPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-account\"]");
        setting.getAccount().setViewAccountDetail(checkPermission(accountPermission, 0));
        setting.getAccount().setUpdateAccountInformation(checkPermission(accountPermission, 1));
        setting.getAccount().setResetPassword(checkPermission(accountPermission, 2));
        setting.getAccount().setPurchasePackage(checkPermission(accountPermission, 3));
        setting.getAccount().setRenewPackage(checkPermission(accountPermission, 4));

        // parse Setting - Store Information
        String storeInformationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-storeInformation\"]");
        setting.getStoreInformation().setViewStoreInformation(checkPermission(storeInformationPermission, 0));
        setting.getStoreInformation().setUpdateInformation(checkPermission(storeInformationPermission, 1));

        // parse Setting - Shipping and Payment
        String shippingAndPaymentPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-shippingAndPayment\"]");
        setting.getShippingAndPayment().setEnableDisable3rdShippingMethod(checkPermission(shippingAndPaymentPermission, 0));
        setting.getShippingAndPayment().setEnableDisableSelfDeliveryMethod(checkPermission(shippingAndPaymentPermission, 1));
        setting.getShippingAndPayment().setAddRemoveGoogleAPIKey(checkPermission(shippingAndPaymentPermission, 2));
        setting.getShippingAndPayment().setEnableDisablePaymentMethod(checkPermission(shippingAndPaymentPermission, 3));
        setting.getShippingAndPayment().setUpdatePaymentMethodInformation(checkPermission(shippingAndPaymentPermission, 4));
        setting.getShippingAndPayment().setUpdate3rdShippingMethodInformation(checkPermission(shippingAndPaymentPermission, 5));
        setting.getShippingAndPayment().setUpdateSelfDeliveryInformation(checkPermission(shippingAndPaymentPermission, 6));

        // parse Setting - Bank account
        String bankAccountPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-bankAccount\"]");
        setting.getBankAccount().setViewBankInformation(checkPermission(bankAccountPermission, 0));
        setting.getBankAccount().setUpdateBankInformation(checkPermission(bankAccountPermission, 1));

        // parse Setting - Staff management
        String staffManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-staffManagement\"]");
        setting.getStaffManagement().setViewStaffList(checkPermission(staffManagementPermission, 0));
        setting.getStaffManagement().setAddStaff(checkPermission(staffManagementPermission, 1));
        setting.getStaffManagement().setEditStaff(checkPermission(staffManagementPermission, 2));
        setting.getStaffManagement().setActiveDeactivateStaff(checkPermission(staffManagementPermission, 3));
        setting.getStaffManagement().setDeleteStaff(checkPermission(staffManagementPermission, 4));

        // parse Setting - Permission
        String permissionPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-permission\"]");
        setting.getPermission().setViewPermissionGroupList(checkPermission(permissionPermission, 0));
        setting.getPermission().setCreatePermissionGroup(checkPermission(permissionPermission, 1));
        setting.getPermission().setEditPermissionGroup(checkPermission(permissionPermission, 2));
        setting.getPermission().setAddStaffToPermissionGroup(checkPermission(permissionPermission, 3));
        setting.getPermission().setDeletePermissionGroup(checkPermission(permissionPermission, 4));
        setting.getPermission().setRemoveStaffFromPermissionGroup(checkPermission(permissionPermission, 5));

        // parse Setting - Branch management
        String branchManagementPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-branchManagement\"]");
        setting.getBranchManagement().setViewBranchInformation(checkPermission(branchManagementPermission, 0));
        setting.getBranchManagement().setUpdateBranchInformation(checkPermission(branchManagementPermission, 1));
        setting.getBranchManagement().setAddBranch(checkPermission(branchManagementPermission, 2));
        setting.getBranchManagement().setPurchaseBranch(checkPermission(branchManagementPermission, 3));
        setting.getBranchManagement().setRenewBranch(checkPermission(branchManagementPermission, 4));
        setting.getBranchManagement().setUpgradeBranch(checkPermission(branchManagementPermission, 5));
        setting.getBranchManagement().setActiveDeactivateBranch(checkPermission(branchManagementPermission, 6));

        // parse Setting - TAX
        String taxPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-tax\"]");
        setting.getTAX().setViewTAXList(checkPermission(taxPermission, 0));
        setting.getTAX().setCreateSellingTAX(checkPermission(taxPermission, 1));
        setting.getTAX().setCreateImportingTAX(checkPermission(taxPermission, 2));
        setting.getTAX().setUpdateTAXConfiguration(checkPermission(taxPermission, 5));
        setting.getTAX().setDeleteTAX(checkPermission(taxPermission, 6));

        // parse Setting - Store language
        String storeLanguagePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"setting-storeLanguage\"]");
        setting.getStoreLanguage().setPublishLanguage(checkPermission(storeLanguagePermission, 0));
        setting.getStoreLanguage().setUnpublishLanguage(checkPermission(storeLanguagePermission, 1));
        setting.getStoreLanguage().setUpdateTranslation(checkPermission(storeLanguagePermission, 2));
        setting.getStoreLanguage().setAddLanguage(checkPermission(storeLanguagePermission, 3));
        setting.getStoreLanguage().setRemoveLanguage(checkPermission(storeLanguagePermission, 4));
        setting.getStoreLanguage().setPurchaseLanguagePackage(checkPermission(storeLanguagePermission, 5));
        setting.getStoreLanguage().setRenewLanguagePackage(checkPermission(storeLanguagePermission, 6));
        setting.getStoreLanguage().setChangeDefaultLanguage(checkPermission(storeLanguagePermission, 7));
    }

    void setTiktok(String payloadJson) {
        String tiktokPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"tiktok-none\"]");
        tiktok.setConnectAccount(checkPermission(tiktokPermission, 0));
        tiktok.setDisconnectAccount(checkPermission(tiktokPermission, 1));
        tiktok.setViewAccountInformation(checkPermission(tiktokPermission, 2));
        tiktok.setRemoveAccount(checkPermission(tiktokPermission, 3));
        tiktok.setDownloadProductsBulkIndividual(checkPermission(tiktokPermission, 4));
        tiktok.setViewProducts(checkPermission(tiktokPermission, 5));
        tiktok.setSyncOrders(checkPermission(tiktokPermission, 6));
        tiktok.setViewProductLinking(checkPermission(tiktokPermission, 7));
        tiktok.setLinkProductTiktokGoSELL(checkPermission(tiktokPermission, 8));
        tiktok.setViewOrderList(checkPermission(tiktokPermission, 9));
        tiktok.setViewOrderDetail(checkPermission(tiktokPermission, 10));
        tiktok.setConfirmOrder(checkPermission(tiktokPermission, 11));
        tiktok.setCancelOrder(checkPermission(tiktokPermission, 12));
        tiktok.setAcceptCancellationRequest(checkPermission(tiktokPermission, 13));
        tiktok.setRejectCancellationRequest(checkPermission(tiktokPermission, 14));
        tiktok.setCreateTiktokProductToGoSELL(checkPermission(tiktokPermission, 15));
        tiktok.setUpdateTiktokProductToGoSELL(checkPermission(tiktokPermission, 16));
        tiktok.setExportOrder(checkPermission(tiktokPermission, 17));
        tiktok.setPurchaseConnection(checkPermission(tiktokPermission, 18));
        tiktok.setRenewConnection(checkPermission(tiktokPermission, 19));
        tiktok.setUpgradeConnection(checkPermission(tiktokPermission, 20));
        tiktok.setUnlinkProductTiktokGoSELL(checkPermission(tiktokPermission, 21));
        tiktok.setSetting(checkPermission(tiktokPermission, 22));
    }

    void setLazada(String payloadJson) {
        String lazadaPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"lazada-none\"]");
        lazada.setConnectAccount(checkPermission(lazadaPermission, 0));
        lazada.setDisconnectAccount(checkPermission(lazadaPermission, 1));
        lazada.setViewAccountInformation(checkPermission(lazadaPermission, 2));
        lazada.setSyncProduct(checkPermission(lazadaPermission, 4));
        lazada.setSyncOrders(checkPermission(lazadaPermission, 5));
        lazada.setViewOrderList(checkPermission(lazadaPermission, 6));
        lazada.setViewOrderDetail(checkPermission(lazadaPermission, 7));
        lazada.setConfirmOrder(checkPermission(lazadaPermission, 8));
        lazada.setCancelOrder(checkPermission(lazadaPermission, 9));
        lazada.setExportOrder(checkPermission(lazadaPermission, 10));

    }

    void setShopee(String payloadJson) {
        String shopeePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"shopee-none\"]");
        shopee.setConnectAccount(checkPermission(shopeePermission, 0));
        shopee.setDisconnectAccount(checkPermission(shopeePermission, 1));
        shopee.setViewAccountInformation(checkPermission(shopeePermission, 2));
        shopee.setRemoveAccount(checkPermission(shopeePermission, 3));
        shopee.setDownloadProductsBulkIndividual(checkPermission(shopeePermission, 4));
        shopee.setViewProducts(checkPermission(shopeePermission, 5));
        shopee.setSyncOrders(checkPermission(shopeePermission, 6));
        shopee.setViewProductLinking(checkPermission(shopeePermission, 7));
        shopee.setLinkProductShopeeGoSELL(checkPermission(shopeePermission, 8));
        shopee.setViewOrderList(checkPermission(shopeePermission, 9));
        shopee.setViewOrderDetail(checkPermission(shopeePermission, 10));
        shopee.setConfirmOrder(checkPermission(shopeePermission, 11));
        shopee.setCancelOrder(checkPermission(shopeePermission, 12));
        shopee.setAcceptCancellationRequest(checkPermission(shopeePermission, 13));
        shopee.setRejectCancellationRequest(checkPermission(shopeePermission, 14));
        shopee.setCreateShopeeProductToGoSELL(checkPermission(shopeePermission, 15));
        shopee.setUpdateShopeeProductToGoSELL(checkPermission(shopeePermission, 16));
        shopee.setExportOrder(checkPermission(shopeePermission, 17));
        shopee.setPurchaseConnection(checkPermission(shopeePermission, 18));
        shopee.setRenewConnection(checkPermission(shopeePermission, 19));
        shopee.setUpgradeConnection(checkPermission(shopeePermission, 20));
        shopee.setUnlinkProductShopeeGoSELL(checkPermission(shopeePermission, 21));
        shopee.setSetting(checkPermission(shopeePermission, 22));
    }

    void setOnlineStore(String payloadJson) {
        // parse Online Store - Theme
        String themePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-theme\"]");
        onlineStore.getTheme().setViewThemeLibrary(checkPermission(themePermission, 0));
        onlineStore.getTheme().setViewThemeDetail(checkPermission(themePermission, 1));
        onlineStore.getTheme().setEditTheme(checkPermission(themePermission, 2));
        onlineStore.getTheme().setPublishTheme(checkPermission(themePermission, 3));
        onlineStore.getTheme().setUnpublishTheme(checkPermission(themePermission, 4));
        onlineStore.getTheme().setAddNewTheme(checkPermission(themePermission, 5));
        onlineStore.getTheme().setDeleteTheme(checkPermission(themePermission, 6));

        // parse Online Store - Blog
        String blogPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-blog\"]");
        onlineStore.getBlog().setViewArticleList(checkPermission(blogPermission, 0));
        onlineStore.getBlog().setViewArticleDetail(checkPermission(blogPermission, 1));
        onlineStore.getBlog().setAddArticle(checkPermission(blogPermission, 2));
        onlineStore.getBlog().setEditArticle(checkPermission(blogPermission, 3));
        onlineStore.getBlog().setViewBlogCategoryList(checkPermission(blogPermission, 4));
        onlineStore.getBlog().setAddBlogCategory(checkPermission(blogPermission, 5));
        onlineStore.getBlog().setEditBlogCategory(checkPermission(blogPermission, 6));
        onlineStore.getBlog().setDeleteBlogCategory(checkPermission(blogPermission, 7));
        onlineStore.getBlog().setTranslateArticle(checkPermission(blogPermission, 8));
        onlineStore.getBlog().setTranslateCategory(checkPermission(blogPermission, 9));

        // parse Online Store - Page
        String pagePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-page\"]");
        onlineStore.getPage().setViewPageList(checkPermission(pagePermission, 0));
        onlineStore.getPage().setAddPage(checkPermission(pagePermission, 1));
        onlineStore.getPage().setEditPage(checkPermission(pagePermission, 2));
        onlineStore.getPage().setDeletePage(checkPermission(pagePermission, 3));
        onlineStore.getPage().setTranslatePage(checkPermission(pagePermission, 4));

        // parse Online Store - Menu
        String menuPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-menu\"]");
        onlineStore.getMenu().setViewListMenu(checkPermission(menuPermission, 0));
        onlineStore.getMenu().setCreateMenu(checkPermission(menuPermission, 1));
        onlineStore.getMenu().setEditMenu(checkPermission(menuPermission, 2));
        onlineStore.getMenu().setTranslateMenu(checkPermission(menuPermission, 3));

        // parse Online Store - Domain
        String domainPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-domain\"]");
        onlineStore.getDomain().setEditSubdomain(checkPermission(domainPermission, 0));
        onlineStore.getDomain().setEditCustomDomain(checkPermission(domainPermission, 1));

        // parse Online Store - Preference
        String preferencesPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"onlineStore-preferences\"]");
        onlineStore.getPreferences().setEnableDisableFBChat(checkPermission(preferencesPermission, 0));
        onlineStore.getPreferences().setEnableDisableZaloChat(checkPermission(preferencesPermission, 1));
        onlineStore.getPreferences().setEnableDisableFacebookLogin(checkPermission(preferencesPermission, 2));
        onlineStore.getPreferences().setEnableDisableMulticurrency(checkPermission(preferencesPermission, 3));
        onlineStore.getPreferences().setEnableDisableGuestCheckout(checkPermission(preferencesPermission, 4));
        onlineStore.getPreferences().setEnableDisableProductListing(checkPermission(preferencesPermission, 5));
        onlineStore.getPreferences().setEnableDisableServiceListing(checkPermission(preferencesPermission, 6));
        onlineStore.getPreferences().setAddRemoveGoogleAnalyticsCode(checkPermission(preferencesPermission, 7));
        onlineStore.getPreferences().setExportGoogleShoppingProduct(checkPermission(preferencesPermission, 8));
        onlineStore.getPreferences().setExportGoogleShoppingProduct(checkPermission(preferencesPermission, 9));
        onlineStore.getPreferences().setAddRemoveGoogleTagManager(checkPermission(preferencesPermission, 10));
        onlineStore.getPreferences().setAddRemoveFacebookPixel(checkPermission(preferencesPermission, 11));
        onlineStore.getPreferences().setUpdateCustomCode(checkPermission(preferencesPermission, 12));
    }

    void setAffiliate(String payloadJson) {
        // parse Affiliate - Drop-ship information
        String dropshipInformationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-dropshipInformation\"]");
        affiliate.getDropshipInformation().setViewInformation(checkPermission(dropshipInformationPermission, 0));

        // parse Affiliate - Reseller information
        String resellerInformationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerInformation\"]");
        affiliate.getResellerInformation().setViewInformation(checkPermission(resellerInformationPermission, 0));

        // parse Affiliate - Drop-ship Partner
        String dropshipPartnerPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-dropshipPartner\"]");
        affiliate.getDropshipPartner().setViewDropshipPartnerList(checkPermission(dropshipPartnerPermission, 0));
        affiliate.getDropshipPartner().setViewDropshipPartnerDetail(checkPermission(dropshipPartnerPermission, 1));
        affiliate.getDropshipPartner().setAddDropshipPartner(checkPermission(dropshipPartnerPermission, 2));
        affiliate.getDropshipPartner().setEditDropshipPartner(checkPermission(dropshipPartnerPermission, 3));
        affiliate.getDropshipPartner().setExportPartner(checkPermission(dropshipPartnerPermission, 4));
        affiliate.getDropshipPartner().setDownloadExportedFile(checkPermission(dropshipPartnerPermission, 5));

        // parse Affiliate - Reseller partner
        String resellerPartnerPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerPartner\"]");
        affiliate.getResellerPartner().setViewResellerPartnerList(checkPermission(resellerPartnerPermission, 0));
        affiliate.getResellerPartner().setViewResellerPartnerDetail(checkPermission(resellerPartnerPermission, 1));
        affiliate.getResellerPartner().setAddResellerPartner(checkPermission(resellerPartnerPermission, 2));
        affiliate.getResellerPartner().setEditResellerPartner(checkPermission(resellerPartnerPermission, 3));

        // parse Affiliate - Commission
        String commissionPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-commission\"]");
        affiliate.getCommission().setViewProductCommissionList(checkPermission(commissionPermission, 0));
        affiliate.getCommission().setAddCommission(checkPermission(commissionPermission, 1));
        affiliate.getCommission().setEditCommission(checkPermission(commissionPermission, 2));
        affiliate.getCommission().setDeleteCommission(checkPermission(commissionPermission, 3));

        // parse Affiliate - Drop-ship order
        String dropshipOrdersPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-dropshipOrders\"]");
        affiliate.getDropshipOrders().setViewOrdersList(checkPermission(dropshipOrdersPermission, 0));
        affiliate.getDropshipOrders().setApproveCommission(checkPermission(dropshipOrdersPermission, 1));
        affiliate.getDropshipOrders().setRejectCommission(checkPermission(dropshipOrdersPermission, 2));

        // parse Affiliate - Reseller order
        String resellerOrdersPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerOrders\"]");
        affiliate.getResellerOrders().setViewOrdersList(checkPermission(resellerOrdersPermission, 0));
        affiliate.getResellerOrders().setExportOrderReseller(checkPermission(resellerOrdersPermission, 1));
        affiliate.getResellerOrders().setDownloadExportData(checkPermission(resellerOrdersPermission, 2));
        affiliate.getResellerOrders().setApproveCommission(checkPermission(resellerOrdersPermission, 3));
        affiliate.getResellerOrders().setRejectCommission(checkPermission(resellerOrdersPermission, 4));

        // parse Affiliate - Drop-ship payout
        String dropshipPayoutPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-dropshipPayout\"]");
        affiliate.getDropshipPayout().setViewPayoutSummary(checkPermission(dropshipPayoutPermission, 0));
        affiliate.getDropshipPayout().setExportPayout(checkPermission(dropshipPayoutPermission, 1));
        affiliate.getDropshipPayout().setImportPayout(checkPermission(dropshipPayoutPermission, 2));

        // parse Affiliate - Reseller payout
        String resellerPayoutPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerPayout\"]");
        affiliate.getResellerPayout().setViewPayoutSummary(checkPermission(resellerPayoutPermission, 0));
        affiliate.getResellerPayout().setExportPayout(checkPermission(resellerPayoutPermission, 1));
        affiliate.getResellerPayout().setImportPayout(checkPermission(resellerPayoutPermission, 2));

        // parse Affiliate - Reseller inventory
        String resellerInventoryPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerInventory\"]");
        affiliate.getResellerInventory().setViewInventorySummary(checkPermission(resellerInventoryPermission, 0));
        affiliate.getResellerInventory().setCreateTransferToReseller(checkPermission(resellerInventoryPermission, 1));
        affiliate.getResellerInventory().setViewTransferDetail(checkPermission(resellerInventoryPermission, 2));
        affiliate.getResellerInventory().setEditTransfer(checkPermission(resellerInventoryPermission, 3));
        affiliate.getResellerInventory().setCancelTransfer(checkPermission(resellerInventoryPermission, 4));
        affiliate.getResellerInventory().setConfirmShipGoods(checkPermission(resellerInventoryPermission, 5));
        affiliate.getResellerInventory().setConfirmReceivedGoods(checkPermission(resellerInventoryPermission, 6));

        // parse Affiliate - Reseller customer
        String resellerCustomerPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-resellerCustomer\"]");
        affiliate.getResellerCustomer().setDownloadCustomer(checkPermission(resellerCustomerPermission, 0));
        affiliate.getResellerCustomer().setExportCustomer(checkPermission(resellerCustomerPermission, 1));

        // parse Affiliate - Payout history
        String payoutHistoryPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"affiliate-payoutHistory\"]");
        affiliate.getPayoutHistory().setViewPayoutHistory(checkPermission(payoutHistoryPermission, 0));
    }

    void setMarketing(String payloadJson) {
        // parse Marketing - Landing page
        String landingPagePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-landingPage\"]");
        marketing.getLandingPage().setViewLandingPageList(checkPermission(landingPagePermission, 0));
        marketing.getLandingPage().setCreateLandingPage(checkPermission(landingPagePermission, 1));
        marketing.getLandingPage().setEditLandingPage(checkPermission(landingPagePermission, 2));
        marketing.getLandingPage().setPublishLandingPage(checkPermission(landingPagePermission, 3));
        marketing.getLandingPage().setUnpublishLandingPage(checkPermission(landingPagePermission, 4));
        marketing.getLandingPage().setDeleteLandingPage(checkPermission(landingPagePermission, 5));
        marketing.getLandingPage().setViewDetailLandingPage(checkPermission(landingPagePermission, 6));
        marketing.getLandingPage().setCloneLandingPage(checkPermission(landingPagePermission, 7));

        // parse Marketing - Buy link
        String buyLinkPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-buyLink\"]");
        marketing.getBuyLink().setViewBuyLinkList(checkPermission(buyLinkPermission, 0));
        marketing.getBuyLink().setCreateBuyLink(checkPermission(buyLinkPermission, 1));
        marketing.getBuyLink().setEditBuyLink(checkPermission(buyLinkPermission, 2));
        marketing.getBuyLink().setDeleteBuyLink(checkPermission(buyLinkPermission, 3));

        // parse Marketing - Email campaign
        String emailCampaignPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-emailCampaign\"]");
        marketing.getEmailCampaign().setViewCampaignList(checkPermission(emailCampaignPermission, 0));
        marketing.getEmailCampaign().setCreateCampaign(checkPermission(emailCampaignPermission, 1));
        marketing.getEmailCampaign().setEditCampaign(checkPermission(emailCampaignPermission, 2));
        marketing.getEmailCampaign().setDeleteCampaign(checkPermission(emailCampaignPermission, 3));


        // parse Marketing - Push notification
        String pushNotificationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-pushNotification\"]");
        marketing.getPushNotification().setViewCampaignList(checkPermission(pushNotificationPermission, 0));
        marketing.getPushNotification().setViewCampaignDetail(checkPermission(pushNotificationPermission, 1));
        marketing.getPushNotification().setCreateCampaign(checkPermission(pushNotificationPermission, 2));
        marketing.getPushNotification().setEditCampaign(checkPermission(pushNotificationPermission, 3));
        marketing.getPushNotification().setDeleteCampaign(checkPermission(pushNotificationPermission, 4));

        // parse Marketing - Loyalty program
        String loyaltyProgramPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-loyaltyProgram\"]");
        marketing.getLoyaltyProgram().setViewListMembership(checkPermission(loyaltyProgramPermission, 0));
        marketing.getLoyaltyProgram().setCreateMembership(checkPermission(loyaltyProgramPermission, 1));
        marketing.getLoyaltyProgram().setEditMembership(checkPermission(loyaltyProgramPermission, 2));
        marketing.getLoyaltyProgram().setDeleteMembership(checkPermission(loyaltyProgramPermission, 3));
        marketing.getLoyaltyProgram().setCollocateMembership(checkPermission(loyaltyProgramPermission, 4));
        marketing.getLoyaltyProgram().setViewMembershipDetail(checkPermission(loyaltyProgramPermission, 5));

        // parse Marketing - Loyalty point
        String loyaltyPointPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"marketing-loyaltyPoint\"]");
        marketing.getLoyaltyPoint().setViewPointProgramInformation(checkPermission(loyaltyPointPermission, 0));
        marketing.getLoyaltyPoint().setEnableProgram(checkPermission(loyaltyPointPermission, 1));
        marketing.getLoyaltyPoint().setDisableProgram(checkPermission(loyaltyPointPermission, 2));
        marketing.getLoyaltyPoint().setEditProgram(checkPermission(loyaltyPointPermission, 3));
    }

    void setAnalytics(String payloadJson) {
        // parse Analytics - Orders
        String ordersPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"analytics-orders\"]");
        analytics.getOrdersAnalytics().setViewDataOfAssignedBranch(checkPermission(ordersPermission, 0));
        analytics.getOrdersAnalytics().setViewCreatedData(checkPermission(ordersPermission, 1));

        // parse Analytics - Reservation
        String reservationPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"analytics-reservation\"]");
        analytics.getReservationAnalytics().setViewReservationAnalytics(checkPermission(reservationPermission, 0));
    }

    void setCashbook(String payloadJson) {
        // parse Cashbook
        String cashbookPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"cashbook-none\"]");
        cashbook.setViewReceiptTransactionList(checkPermission(cashbookPermission, 0));
        cashbook.setViewPaymentTransactionList(checkPermission(cashbookPermission, 1));
        cashbook.setViewReceiptTransactionDetail(checkPermission(cashbookPermission, 2));
        cashbook.setViewPaymentTransactionDetail(checkPermission(cashbookPermission, 3));
        cashbook.setCreateReceiptTransaction(checkPermission(cashbookPermission, 4));
        cashbook.setCreatePaymentTransaction(checkPermission(cashbookPermission, 5));
        cashbook.setEditReceiptTransaction(checkPermission(cashbookPermission, 6));
        cashbook.setEditPaymentTransaction(checkPermission(cashbookPermission, 7));
        cashbook.setDeleteReceiptTransaction(checkPermission(cashbookPermission, 8));
        cashbook.setDeletePaymentTransaction(checkPermission(cashbookPermission, 9));

    }

    void setCallCenter(String payloadJson) {
        // parse Call center
        String callCenterPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"callCenter-none\"]");
        callCenter.setViewCallHistory(checkPermission(callCenterPermission, 0));
        callCenter.setMakeACall(checkPermission(callCenterPermission, 1));
        callCenter.setPickUpCall(checkPermission(callCenterPermission, 2));
        callCenter.setBuyPackage(checkPermission(callCenterPermission, 3));
        callCenter.setRenewPackage(checkPermission(callCenterPermission, 4));
        callCenter.setUpgradePackage(checkPermission(callCenterPermission, 5));
    }

    void setCustomer(String payloadJson) {
        // parse Customer Management
        String customerManagement = getBinaryPermissionString(payloadJson, "staffPermissions[\"customer-customerManagement\"]");
        customer.getCustomerManagement().setViewAllCustomerList(checkPermission(customerManagement, 0));
        customer.getCustomerManagement().setViewAssignedCustomerList(checkPermission(customerManagement, 1));
        customer.getCustomerManagement().setViewCustomerGeneralInformation(checkPermission(customerManagement, 2));
        customer.getCustomerManagement().setViewCustomerBankInformation(checkPermission(customerManagement, 3));
        customer.getCustomerManagement().setViewCustomerActivity(checkPermission(customerManagement, 4));
        customer.getCustomerManagement().setAddCustomer(checkPermission(customerManagement, 5));
        customer.getCustomerManagement().setDeleteCustomer(checkPermission(customerManagement, 6));
        customer.getCustomerManagement().setImportCustomer(checkPermission(customerManagement, 7));
        customer.getCustomerManagement().setEditCustomerInformation(checkPermission(customerManagement, 8));
        customer.getCustomerManagement().setAssignPartner(checkPermission(customerManagement, 9));
        customer.getCustomerManagement().setAssignStaff(checkPermission(customerManagement, 10));
        customer.getCustomerManagement().setUpdateStatus(checkPermission(customerManagement, 11));
        customer.getCustomerManagement().setMergeCustomer(checkPermission(customerManagement, 12));
        customer.getCustomerManagement().setCustomerAnalytics(checkPermission(customerManagement, 13));
        customer.getCustomerManagement().setConfirmPayment(checkPermission(customerManagement, 14));
        customer.getCustomerManagement().setExportCustomer(checkPermission(customerManagement, 15));
        customer.getCustomerManagement().setDownloadExportedCustomer(checkPermission(customerManagement, 16));
        customer.getCustomerManagement().setPrintBarcode(checkPermission(customerManagement, 17));

        // parse Customers Segment
        String customerSegmentPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"customer-segment\"]");
        customer.getSegment().setViewSegmentList(checkPermission(customerSegmentPermission, 0));
        customer.getSegment().setCreateSegment(checkPermission(customerSegmentPermission, 1));
        customer.getSegment().setEditSegment(checkPermission(customerSegmentPermission, 2));
        customer.getSegment().setDeleteSegment(checkPermission(customerSegmentPermission, 3));

    }

    void setPromotion(String payloadJson) {
        // parse Promotion - Discount code
        String discountCodePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"promotion-discountCode\"]");
        promotion.getDiscountCode().setViewProductDiscountCodeList(checkPermission(discountCodePermission, 0));
        promotion.getDiscountCode().setViewProductDiscountCodeDetail(checkPermission(discountCodePermission, 1));
        promotion.getDiscountCode().setViewServiceDiscountCodeList(checkPermission(discountCodePermission, 2));
        promotion.getDiscountCode().setViewServiceDiscountCodeDetail(checkPermission(discountCodePermission, 3));
        promotion.getDiscountCode().setCreateProductDiscountCode(checkPermission(discountCodePermission, 4));
        promotion.getDiscountCode().setCreateServiceDiscountCode(checkPermission(discountCodePermission, 5));
        promotion.getDiscountCode().setEditProductDiscountCode(checkPermission(discountCodePermission, 6));
        promotion.getDiscountCode().setEditServiceDiscountCode(checkPermission(discountCodePermission, 7));
        promotion.getDiscountCode().setEndProductDiscountCode(checkPermission(discountCodePermission, 8));
        promotion.getDiscountCode().setEndServiceDiscountCode(checkPermission(discountCodePermission, 9));

        // parse Promotion - Discount campaign
        String discountCampaignPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"promotion-discountCampaign\"]");
        promotion.getDiscountCampaign().setViewProductCampaignList(checkPermission(discountCampaignPermission, 0));
        promotion.getDiscountCampaign().setViewProductDiscountCampaignDetail(checkPermission(discountCampaignPermission, 1));
        promotion.getDiscountCampaign().setViewServiceDiscountCampaignList(checkPermission(discountCampaignPermission, 2));
        promotion.getDiscountCampaign().setViewServiceDiscountCampaignDetail(checkPermission(discountCampaignPermission, 3));
        promotion.getDiscountCampaign().setCreateProductDiscountCampaign(checkPermission(discountCampaignPermission, 4));
        promotion.getDiscountCampaign().setCreateServiceDiscountCampaign(checkPermission(discountCampaignPermission, 5));
        promotion.getDiscountCampaign().setEditProductDiscountCampaign(checkPermission(discountCampaignPermission, 6));
        promotion.getDiscountCampaign().setEditServiceDiscountCampaign(checkPermission(discountCampaignPermission, 7));
        promotion.getDiscountCampaign().setEndProductDiscountCampaign(checkPermission(discountCampaignPermission, 8));
        promotion.getDiscountCampaign().setEndServiceDiscountCampaign(checkPermission(discountCampaignPermission, 9));

        // parse Promotion - BxGy
        String buyXGetYPermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"promotion-buyXGetY\"]");
        promotion.getBxGy().setViewBuyXGetYList(checkPermission(buyXGetYPermission, 0));
        promotion.getBxGy().setViewBuyXGetYDetail(checkPermission(buyXGetYPermission, 1));
        promotion.getBxGy().setCreateBuyXGetY(checkPermission(buyXGetYPermission, 2));
        promotion.getBxGy().setEndBuyXGetY(checkPermission(buyXGetYPermission, 3));
        promotion.getBxGy().setEditBuyXGetY(checkPermission(buyXGetYPermission, 4));

        // parse Promotion - Flash sale
        String flashSalePermission = getBinaryPermissionString(payloadJson, "staffPermissions[\"promotion-flashSale\"]");
        promotion.getFlashSale().setViewFlashSaleList(checkPermission(flashSalePermission, 0));
        promotion.getFlashSale().setViewFlashSaleDetail(checkPermission(flashSalePermission, 1));
        promotion.getFlashSale().setViewFlashSaleTime(checkPermission(flashSalePermission, 2));
        promotion.getFlashSale().setAddFlashSaleTime(checkPermission(flashSalePermission, 3));
        promotion.getFlashSale().setCreateFlashSale(checkPermission(flashSalePermission, 4));
        promotion.getFlashSale().setEditFlashSale(checkPermission(flashSalePermission, 5));
        promotion.getFlashSale().setDeleteFlashSale(checkPermission(flashSalePermission, 6));
    }

    public AllPermissions(String staffPermissionToken) {
        // get decode of token payload
        String payloadJson = new String(Base64.getUrlDecoder().decode(staffPermissionToken.split("\\.")[1]));
        setGoWallet(payloadJson);
        setHome(payloadJson);
        setGoChat(payloadJson);
        setSupplier(payloadJson);
        setProduct(payloadJson);
        setService(payloadJson);
        setOrders(payloadJson);
        setPromotion(payloadJson);
        setCustomer(payloadJson);
        setCallCenter(payloadJson);
        setCashbook(payloadJson);
        setAnalytics(payloadJson);
        setMarketing(payloadJson);
        setAffiliate(payloadJson);
        setOnlineStore(payloadJson);
        setShopee(payloadJson);
        setLazada(payloadJson);
        setTiktok(payloadJson);
        setSetting(payloadJson);
        setReservation(payloadJson);
    }
    
    public LinkedHashMap<String, Integer> getStaffPermissionFromToken(String staffPermissionToken) {
    	String payloadJson = new String(Base64.getUrlDecoder().decode(staffPermissionToken.split("\\.")[1]));
    	JsonPath json = new JsonPath(payloadJson);
    	return json.getJsonObject("staffPermissions");
    }
}