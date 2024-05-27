package api.Seller.customers;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.regex.Pattern;

public class APIEditCustomer {
    Logger logger = LogManager.getLogger(APIEditCustomer.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIEditCustomer(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String UPDATE_CUSTOMER_PROFILE_PATH = "/beehiveservices/api/customer-profiles/edit/";
    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";
    String ASSIGN_PARTNER_TO_CUSTOMER = "/beehiveservices/api/customer-profiles/update-partner/%s";
    String ASSIGN_STAFF_TO_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/bulk-assign-customer-to-a-staff/%s";
    @Getter
    private String customerTag;

    public void addCustomerTagForMailCustomer(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        String customerName = Pattern.compile("fullName.{4}(\\w+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        int profileId = Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
        customerTag = "AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss");
        String body = """
                {
                     "id": "%s",
                     "fullName": "%s",
                     "phones": [],
                     "emails": [
                         {
                             "email": "%s",
                             "emailName": "%s"
                         }
                     ],
                     "note": "",
                     "tags": [
                         "%s"
                     ],
                     "countryCode": "VN",
                     "address": "",
                     "locationCode": "",
                     "districtCode": "",
                     "wardCode": "",
                     "gender": null,
                     "birthday": null,
                     "partnerId": null,
                     "companyName": "",
                     "taxCode": "",
                     "backupPhones": [],
                     "backupEmails": []
                 }""".formatted(profileId, customerName, keywords, customerName, customerTag);
        api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken(), body)
                .then().statusCode(200);
    }

    public void assignPartnerToCustomer(int customerId, int partnerId) {
        String body = """
                {
                    "customerIds": "%s",
                    "partnerId": %s
                }
                """.formatted(customerId, partnerId);
        Response response = api.put(ASSIGN_PARTNER_TO_CUSTOMER.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        response.then().statusCode(200);
    }

    /**
     * Assigns a specific staff member to a designated customer within the current store.
     * @param staffUserId The ID (userId) of the staff member to be assigned.
     * @param customerId The ID of the customer to whom the staff member will be assigned.
     */
    public void assignStaffToCustomer(int staffUserId, int customerId) {
        String body = """
                   		{
                "userId": %s,
                "storeId": "%s",
                "customerIds": [%s]
                	    }
                """.formatted(staffUserId, loginInfo.getStoreID(), customerId);
        Response createRecord = api.post(ASSIGN_STAFF_TO_CUSTOMER_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        createRecord.then().statusCode(200);
    }
}
