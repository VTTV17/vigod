package api.Seller.customers;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.response.Response;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.BankInfo;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerInfoFull;
import utilities.model.dashboard.customer.CustomerPhone;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class APIEditCustomer {
    Logger logger = LogManager.getLogger(APIEditCustomer.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    PayLoad payLoad;
    public APIEditCustomer(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public APIEditCustomer getPayLoadFormat(int customerId){
        CustomerInfoFull customerInfo = new APICustomerDetail(loginInformation).getFullInfo(customerId);
        PayLoad payLoad = new PayLoad();
        payLoad.setId(customerInfo.getId());
        payLoad.setFullName(customerInfo.getFullName());
        payLoad.setPhones(customerInfo.getPhones());
        payLoad.setEmails(customerInfo.getEmails());
        payLoad.setNote(customerInfo.getNote());
        payLoad.setTags(customerInfo.getTags());
        if(customerInfo.getUserStatus()!=null)payLoad.setUserStatus(customerInfo.getUserStatus());
        payLoad.setCountryCode(customerInfo.getCustomerAddress().getCountryCode());
        if(customerInfo.getCustomerAddress().getAddress()!=null)payLoad.setAddress(customerInfo.getCustomerAddress().getAddress());
        if(customerInfo.getCustomerAddress().getAddress2()!=null)payLoad.setAddress2(customerInfo.getCustomerAddress().getAddress2());
        payLoad.setLocationCode(customerInfo.getCustomerAddress().getLocationCode());
        payLoad.setWardCode(customerInfo.getCustomerAddress().getWardCode());
        payLoad.setDistrictCode(customerInfo.getCustomerAddress().getDistrictCode());
        payLoad.setCity(customerInfo.getCustomerAddress().getCity());
        payLoad.setZipCode(customerInfo.getCustomerAddress().getZipCode());
        payLoad.setGender(customerInfo.getGender());
        payLoad.setBirthday(customerInfo.getBirthday());
        payLoad.setPartnerId(customerInfo.getPartnerId());
        payLoad.setCompanyName(customerInfo.getCompanyName());
        payLoad.setTaxCode(customerInfo.getTaxCode());
        payLoad.setBackupPhones(customerInfo.getBackupPhones());
        payLoad.setBackupEmails(customerInfo.getBackupEmails());
        payLoad.setBankInfos(customerInfo.getBankInfos());
        payLoad.setIdentityCard(customerInfo.getIdentityCard());
        this.payLoad = payLoad;
        return this;
    }
    String UPDATE_CUSTOMER_PROFILE_PATH = "/beehiveservices/api/customer-profiles/edit/";
    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";
    String ASSIGN_PARTNER_TO_CUSTOMER = "/beehiveservices/api/customer-profiles/update-partner/%s";
    String ASSIGN_STAFF_TO_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/bulk-assign-customer-to-a-staff/%s";
    @Getter
    private String customerTag;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @Data
    class PayLoad{
        private int id;
        private String fullName;
        private List<CustomerPhone> phones;
        private List<CustomerEmail> emails;
        private String note;
        private List<String> tags;
        private int responsibleStaffUserId;
        private String userStatus;
        private String address;
        private String address2;
        private String countryCode;
        private String locationCode;
        private String districtCode;
        private String city;
        private String zipCode;
        private String wardCode;
        private String gender;
        private String birthday;
        private Integer partnerId;
        private String companyName;
        private String taxCode;
        private List<CustomerPhone> backupPhones;
        private List<CustomerEmail> backupEmails;
        private List<BankInfo> bankInfos;
        private String identityCard;
    }
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

    /**
     * Need to call getPayLoadFormat function before call this function.
     * @param tagList
     */
    public void addMoreTagForCustomer(List<String> tagList){
        List<String> currentTabList = payLoad.getTags();
        if(currentTabList==null) payLoad.setTags(tagList);
        else {
            Set<String> allTab = new LinkedHashSet<>(tagList);
            allTab.addAll(currentTabList);
            payLoad.setTags(allTab.stream().toList());
        }
        Response response = api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH,loginInfo.getStoreID()),loginInfo.getAccessToken(),payLoad);
        response.then().statusCode(200);
    }
}
