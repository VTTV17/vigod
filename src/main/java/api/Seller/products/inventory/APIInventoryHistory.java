package api.Seller.products.inventory;

import api.Seller.affiliate.dropship.APIPartnerTransferDetail;
import api.Seller.affiliate.dropship.PartnerTransferManagement.PartnerTransferStatus;
import api.Seller.login.Login;
import api.Seller.orders.order_management.APIAllOrders.OrderStatus;
import api.Seller.orders.order_management.APIOrderDetail;
import api.Seller.orders.return_order.APIAllReturnOrder;
import api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus;
import api.Seller.products.transfer.APITransferDetail;
import api.Seller.products.transfer.TransferManagement.TransferStatus;
import api.Seller.supplier.purchase_orders.APIAllPurchaseOrders.PurchaseOrderStatus;
import api.Seller.supplier.purchase_orders.APIPurchaseOrderDetail;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.IN_PROGRESS;
import static api.Seller.products.inventory.APIInventoryHistory.InventoryActionType.*;

public class APIInventoryHistory {
    Logger logger = LogManager.getLogger(APIInventoryHistory.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIInventoryHistory(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum InventoryActionType {
        AUTO_SYNC_STOCK, FROM_CREATE_AT_ITEM_SCREEN, FROM_DELETE_LOT, FROM_EDIT_ORDER, FROM_EXCLUDE_STOCK_LOT_DATE, FROM_IMPORT, FROM_ITEM_LIST, FROM_LAZADA, FROM_LOCK, FROM_LOT_DATE_RESTOCK, FROM_PURCHASE_ORDER, FROM_RESET_STOCK, FROM_RETURN_ORDER, FROM_SHOPEE, FROM_SOLD, FROM_TIKI, FROM_TIKTOK, FROM_TRANSFER_AFFILIATE_IN, FROM_TRANSFER_AFFILIATE_OUT, FROM_TRANSFER_AFFILIATE_RESTOCK, FROM_TRANSFER_IN, FROM_TRANSFER_OUT, FROM_TRANSFER_RESTOCK, FROM_UNLOCK, FROM_UPDATE_AT_INSTORE_PURCHASE, FROM_UPDATE_AT_ITEM_SCREEN, FROM_UPDATE_AT_VARIATION_DETAIL, FROM_UPDATE_STOCK_IN_LOT
    }

    @Data
    public static class AllInventoryHistoryInfo {
        List<String> ids;
        List<String> orderIds;
        List<InventoryActionType> actionType;
    }

    String getInventoryHistoryPath = "/itemservice/api/inventory-search/%s?search=%s&branchIds=%s&page=%s&size=100";

    Response getInventoryResponse(int pageIndex, String keywords) {
        return api.get(getInventoryHistoryPath.formatted(loginInfo.getStoreID(), keywords, loginInfo.getAssignedBranchesIds().toString().replaceAll("[\\[\\] ]", ""), pageIndex), loginInfo.getAccessToken(), Map.of("langkey", "vi"))
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public AllInventoryHistoryInfo getInventoryHistoryInformation(String keywords) {
        AllInventoryHistoryInfo info = new AllInventoryHistoryInfo();

        // else get all inventory information
        List<String> ids = new ArrayList<>();
        List<String> orderIds = new ArrayList<>();
        List<String> actionType = new ArrayList<>();

        // get total products
        int totalOfProducts = Integer.parseInt(getInventoryResponse(0, keywords).getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfProducts / 100;

        // get all inventory
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jPath = getInventoryResponse(pageIndex, keywords)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jPath.getList("id"));
            orderIds.addAll(jPath.getList("orderId"));
            actionType.addAll(jPath.getList("actionType"));
        }

        // set inventory history info
        IntStream.iterate(orderIds.size() - 1, index -> index >= 0, index -> index - 1)
                .filter(index -> orderIds.get(index) == null)
                .forEach(index -> {
                    orderIds.remove(index);
                    ids.remove(index);
                    actionType.remove(index);
                });
        info.setIds(ids);
        info.setOrderIds(orderIds);
        info.setActionType(actionType.stream().map(InventoryActionType::valueOf).toList());

        return info;
    }

    public List<Integer> listOfCanNotBeDeletedProductIds(List<String> productIds) {
        APITransferDetail apiTransferDetail = new APITransferDetail(loginInformation);
        APIPartnerTransferDetail apiPartnerTransferDetail = new APIPartnerTransferDetail(loginInformation);
        List<Integer> itemIds = new ArrayList<>();
        for (String productId : productIds) {
            AllInventoryHistoryInfo info = getInventoryHistoryInformation(productId);
            for (int historyIndex = 0; historyIndex < info.getOrderIds().size(); historyIndex++) {
                if (info.getOrderIds().get(historyIndex).contains("CH")) {
                    if (Objects.equals(info.getActionType().get(historyIndex), FROM_TRANSFER_AFFILIATE_OUT)) {
                        PartnerTransferStatus status = apiPartnerTransferDetail.getTransferStatus(Integer.parseInt(info.getOrderIds().get(historyIndex).replaceAll("CH", "")));
                        if (!(Objects.equals(status, PartnerTransferStatus.RECEIVED) || Objects.equals(status, PartnerTransferStatus.CANCELLED))) {
                            itemIds.add(Integer.parseInt(productId));
                            break;
                        }
                    } else {
                        TransferStatus status = apiTransferDetail.getTransferStatus(Integer.parseInt(info.getOrderIds().get(historyIndex).replaceAll("CH", "")));
                        if (!(Objects.equals(status, TransferStatus.RECEIVED) || Objects.equals(status, TransferStatus.CANCELLED))) {
                            itemIds.add(Integer.parseInt(productId));
                            break;
                        }
                    }
                }
            }
        }
        return itemIds;
    }

    public List<Integer> listOfCanNotManagedByLotDateProductIds(List<String> productIds) {
        return productIds.stream().filter(productId -> !canManageByLotDate(productId)).map(Integer::parseInt).distinct().toList();
    }

    public boolean canManageByLotDate(String productId) {
        APITransferDetail apiTransferDetail = new APITransferDetail(loginInformation);
        APIPartnerTransferDetail apiPartnerTransferDetail = new APIPartnerTransferDetail(loginInformation);
        APIPurchaseOrderDetail apiPurchaseOrderDetail = new APIPurchaseOrderDetail(loginInformation);
        APIOrderDetail apiOrderDetail = new APIOrderDetail(loginInformation);
        AllInventoryHistoryInfo info = getInventoryHistoryInformation(productId);
        for (int historyIndex = 0; historyIndex < info.getOrderIds().size(); historyIndex++) {
            if (info.getOrderIds().get(historyIndex).contains("CH")) {
                if (Objects.equals(info.getActionType().get(historyIndex), FROM_TRANSFER_AFFILIATE_OUT)) {
                    PartnerTransferStatus status = apiPartnerTransferDetail.getTransferStatus(Integer.parseInt(info.getOrderIds().get(historyIndex).replaceAll("CH", "")));
                    if (!(Objects.equals(status, PartnerTransferStatus.RECEIVED)
                          || Objects.equals(status, PartnerTransferStatus.CANCELLED))) {
                        return false;
                    }
                } else {
                    TransferStatus status = apiTransferDetail.getTransferStatus(Integer.parseInt(info.getOrderIds().get(historyIndex).replaceAll("CH", "")));
                    if (!(Objects.equals(status, TransferStatus.RECEIVED)
                          || Objects.equals(status, TransferStatus.CANCELLED))) {
                        return false;
                    }
                }
            } else if (info.getOrderIds().get(historyIndex).contains("PO")) {
                PurchaseOrderStatus status = apiPurchaseOrderDetail.getPurchaseOrderStatus(Integer.parseInt(info.getOrderIds().get(historyIndex).replaceAll("PO", "")));
                if (!(Objects.equals(status, PurchaseOrderStatus.COMPLETED)
                      || Objects.equals(status, PurchaseOrderStatus.CANCELLED))) {
                    return false;
                }
            } else if (Objects.equals(info.getActionType().get(historyIndex), FROM_LOCK)) {
                return false;
            } else if (Objects.equals(info.getActionType().get(historyIndex), FROM_EDIT_ORDER)) {
                OrderStatus status = apiOrderDetail.getOrderStatus(Integer.parseInt(info.getOrderIds().get(historyIndex)));
                if (!(Objects.equals(status, OrderStatus.DELIVERED)
                      || Objects.equals(status, OrderStatus.CANCELLED))
                    || Objects.equals(status, OrderStatus.REJECTED)
                    || Objects.equals(status, OrderStatus.FAILED)) {
                    return false;
                }
            } else if (Objects.equals(info.getActionType().get(historyIndex), FROM_SOLD)) {
                List<ReturnOrderStatus> statuses = new APIAllReturnOrder(loginInformation).getAllReturnOrdersInformation(info.getOrderIds().get(historyIndex)).getStatues();
                if (statuses.stream().anyMatch(status -> Objects.equals(status, IN_PROGRESS))) {
                    return false;
                }
            }
        }
        return true;
    }
}
