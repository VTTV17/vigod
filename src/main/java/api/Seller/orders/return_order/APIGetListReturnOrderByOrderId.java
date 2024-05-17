package api.Seller.orders.return_order;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.stream.IntStream;

import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus;
import static api.Seller.orders.return_order.APIAllReturnOrder.ReturnOrderStatus.CANCELLED;

public class APIGetListReturnOrderByOrderId {
    Logger logger = LogManager.getLogger(APIGetListReturnOrderByOrderId.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIGetListReturnOrderByOrderId(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class ItemReturnInformation {
        List<Integer> itemIds;
        List<Integer> quantity;
    }

    String findReturnOderByOrderIdPath = "/orderservices2/api/return-orders/find-by-bc-order/%s";

    public ItemReturnInformation getItemReturnInformation(long orderId) {
        // get jsonPath
        JsonPath jsonPath = api.get(findReturnOderByOrderIdPath.formatted(orderId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

        // get list item info
        List<ReturnOrderStatus> statuses = jsonPath.getList("status").stream().map(status -> ReturnOrderStatus.valueOf((String) status)).toList();
        List<List<Integer>> itemIds = jsonPath.getList("returnOrderItemList.itemId");
        List<List<Integer>> quantity = jsonPath.getList("returnOrderItemList.quantity");

        IntStream.iterate(statuses.size() - 1, index -> index >= 0, index -> index - 1)
                .filter(index -> Objects.equals(statuses.get(index), CANCELLED)).forEachOrdered(index -> {
                    itemIds.remove(index);
                    quantity.remove(index);
                });

        // flatMap
        List<Integer> flatItemIds = itemIds.stream().flatMap(Collection::stream).toList();
        List<Integer> flatQuantity = quantity.stream().flatMap(Collection::stream).toList();

        Map<Integer, Integer> itemMaps = new LinkedHashMap<>();
        IntStream.range(0, flatItemIds.size()).forEach(index -> itemMaps.put(flatItemIds.get(index), itemMaps.getOrDefault(flatItemIds.get(index), 0) + flatQuantity.get(index)));

        ItemReturnInformation info = new ItemReturnInformation();
        info.setItemIds(new ArrayList<>(itemMaps.keySet()));
        info.setQuantity(new ArrayList<>(itemMaps.values()));

        return info;
    }

}
