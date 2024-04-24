package api.Seller.reservations;

import api.Seller.login.Login;
import api.Seller.services.ServiceInfoAPI;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.awt.print.Book;
import java.util.List;

public class CreateBookingPOS {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String CREATE_BOOKING_POS_PATH = "/orderservices2/api/gs/checkout/service/in-store";
    public CreateBookingPOS(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public String getBookingCartItemInfo(List<Integer> itemIds){
        String futureDate = new DataGenerator().generateDateTime("yyyy-MM-dd",1);
        String body = """
                "bookingCartItemVMs": [
                """;
        int receiveAmount = 0;
        for (int itemId:itemIds) {
            ServiceInfo serviceInfo = new ServiceInfoAPI(loginInformation).getServiceInfo(itemId);
           receiveAmount = receiveAmount + serviceInfo.getSellingPrice();
            body = body + """
                    {
                                "date": "%s",
                                "itemId": %s,
                                 "modelId": %s,
                                "quantity": 1
                    }
                    """.formatted(futureDate,itemId,serviceInfo.getServiceModelId());
            if(itemIds.indexOf(itemId)<itemIds.size()-1)
                body = body + ",";
        }
        body = body+ """
                ],
                "receivedAmount":%s,
                """.formatted(receiveAmount);
        return body;
    }
    @Data
    public static class BookingCustomerInfo{
        private boolean isGuest = true;
        private Integer profileId;
        private Integer buyerId;
    }
    public int CreateBookingPOS(List<Integer>itemIds){
        String payLoad = "{"+getBookingCartItemInfo(itemIds);
        BookingCustomerInfo bookingCustomerInfo = new BookingCustomerInfo();
        payLoad = payLoad + """
                    "profileId": %s,
                    "guest": %s,
                    "buyerId": %s,
                """.formatted(bookingCustomerInfo.getProfileId(),bookingCustomerInfo.isGuest(),bookingCustomerInfo.getBuyerId())
                +"""
                    "langKey": "vi",
                    "paymentCode": "",
                    "paymentMethod": "CASH",
                    "storeId": "%s",
                    "platform": "IN_STORE"
                    }
                 """.formatted(loginInfo.getStoreID());
        Response response = api.post(CREATE_BOOKING_POS_PATH,loginInfo.getAccessToken(),payLoad);
        int bookingId = response
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("orders[0].id");
        response.prettyPrint();
        return bookingId;
    }
}
