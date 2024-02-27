package api.Seller.reservations;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class ReservationAPI {
    String GET_RESEVATION_PATH = "/orderservices2/api/order-bookings/%s/search?page=0&size=50&search=&searchType=&statusList=";
    String CONFIRM_RESERVATION_PATH = "/orderservices2/api/shop/bc-orders/confirm";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public ReservationAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public List<Integer> getListResevationId(){
        Response response = api.get(GET_RESEVATION_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return  response.jsonPath().getList("lstCollection.id");
    }
    public void confirmReservationAPI(int id){
        String payLoad = """
                {
                    "orderId": "%s",
                    "note": "",
                    "phoneNumber": "",
                    "email": "",
                    "length": 0,
                    "width": 0,
                    "height": 0
                }
                """.formatted(id);
       Response response = api.post(CONFIRM_RESERVATION_PATH,loginInfo.getAccessToken(),payLoad);
        response.prettyPrint();
        response.then()
                .statusCode(200);

    }
}
