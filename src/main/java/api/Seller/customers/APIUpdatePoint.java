package api.Seller.customers;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIUpdatePoint {
    final static Logger logger = LogManager.getLogger(APIUpdatePoint.class);
    LoginInformation loginInformation;
    API api = new API();
    LoginDashboardInfo loginInfo;
    String UPDATE_POINT_PATH = "/orderservice3/api/earning-point-customer";
    public APIUpdatePoint(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public void addMorePoint(int buyerId, int point){
        String payLoad = """
                {
                  "event": "EARN",
                  "pointNumber": "%s",
                  "value": "%s",
                  "storeId": "%s",
                  "buyerId": "%s"
                }
                """.formatted(point,point,loginInfo.getStoreID(),buyerId);
        api.put(UPDATE_POINT_PATH,loginInfo.getAccessToken(),payLoad);
    }
}
