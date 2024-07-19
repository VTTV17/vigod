package api.Seller.affiliate.customers;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIResellerCustomers {
    Logger logger = LogManager.getLogger(APIResellerCustomers.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIResellerCustomers(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    String GET_RESELLER_LEVEL1_PATH = "affiliateservice/api/partners/find-all-reseller-store-by-level/storeId/%s/lvl/1";
    String EXPORT_RESELLER_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/store/%s/reseller/export?fromDate=%sT17:00:00.000Z&toDate=%sT16:59:59.999Z&lstStoreId=%s&langKey=vi";
    public List<Integer> getResellerLevel1(){
        Response response = api.get(GET_RESELLER_LEVEL1_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }
    public Response exportResellerCustomer(){
        List<Integer> resellerIds = getResellerLevel1();
        Response response = null;
        if(resellerIds.size()==0) {
            logger.info("Reseller list empty, so no need call api export");
        }else {
            String fromDate = new DataGenerator().generatePreviousTerm("yyyy-MM-dd");
            String toDate = new DataGenerator().generateDateTime("yyyy-MM-dd");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Time-Zone", "Asia/Bangkok");
            response = api.get(EXPORT_RESELLER_CUSTOMER_PATH.formatted(loginInfo.getStoreID(),fromDate,toDate,resellerIds.get(0)),loginInfo.getAccessToken(),headerMap);
        }
        return response;
    }
}
