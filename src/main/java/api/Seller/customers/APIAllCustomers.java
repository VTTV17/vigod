package api.Seller.customers;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class APIAllCustomers {

    String GET_LIST_SEGMENT_OF_CUSTOMER = "/beehiveservices/api/segments/%s/%s";
    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";

    String GET_ALL_CUSTOMERS_PATH = "/beehiveservices/api/customer-profiles/%s/v2?page=%s&size=100&keyword=&sort=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=en";

    String EXPORT_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/export/%s/v2?keyword=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=vi";

    LoginDashboardInfo loginInfo;
    API api = new API();
    LoginInformation loginInformation;
    private final static Cache<String, CustomerManagementInfo> customerCache = CacheBuilder.newBuilder().build();

    public APIAllCustomers(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class CustomerManagementInfo {
        List<String> customerName;
        List<Integer> customerId;
        List<String> userId;
        List<Integer> totalOrder;
        List<Float> debtAmount;
        List<String> saleChannel;
        List<Integer> responsibleStaffUserId;
        List<Boolean> guestUser;
    }

    public CustomerManagementInfo getCustomerManagementInfo() {
        CustomerManagementInfo info = customerCache.getIfPresent(loginInfo.getStaffPermissionToken());
        if (Optional.ofNullable(info).isEmpty()) {
            if (!loginInfo.getStaffPermissionToken().isEmpty()) {
                CustomerManagementInfo tempInfo = customerCache.getIfPresent("");
                customerCache.invalidateAll();
                if (Optional.ofNullable(tempInfo).isPresent()) {
                    customerCache.put("", tempInfo);
                }
            }
            info = new CustomerManagementInfo();
            int numberOfPages = Integer.parseInt(getAllCustomerResponse(0).getHeader("X-Total-Count")) / 100;
            numberOfPages = Math.min(numberOfPages, 99);

            // init temp array;
            List<String> customerName = new ArrayList<>();
            List<Integer> customerId = new ArrayList<>();
            List<String> userId = new ArrayList<>();
            List<Integer> totalOrder = new ArrayList<>();
            List<Float> debtAmount = new ArrayList<>();
            List<String> saleChannel = new ArrayList<>();
            List<Integer> responsibleStaffUserId = new ArrayList<>();
            List<Boolean> guestUser = new ArrayList<>();

            // get all customer info
            for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
                JsonPath jsonPath = getAllCustomerResponse(pageIndex).jsonPath();
                customerName.addAll(jsonPath.getList("fullName"));
                customerId.addAll(jsonPath.getList("id"));
                userId.addAll(jsonPath.getList("userId"));
                totalOrder.addAll(jsonPath.getList("totalOrder"));
                debtAmount.addAll(jsonPath.getList("orderDebtSummary"));
                saleChannel.addAll(jsonPath.getList("saleChannel"));
                responsibleStaffUserId.addAll(jsonPath.getList("responsibleStaffUserId"));
                guestUser.addAll(jsonPath.getList("guest"));
            }

            // set all customer info
            info.setCustomerName(customerName);
            info.setCustomerId(customerId);
            info.setUserId(userId);
            info.setTotalOrder(totalOrder);
            info.setDebtAmount(debtAmount);
            info.setSaleChannel(saleChannel);
            info.setResponsibleStaffUserId(responsibleStaffUserId);
            info.setGuestUser(guestUser);

            // save cache
            customerCache.put(loginInfo.getAccessToken(), info);
        }

        return info;
    }

    public List<Integer> getListSegmentOfCustomer(int customerId) {
        if (customerId != 0) {
            Response customerInfo = api.get(GET_LIST_SEGMENT_OF_CUSTOMER.formatted(loginInfo.getStoreID(), customerId), loginInfo.getAccessToken());
            customerInfo.then().statusCode(200);
            return customerInfo.jsonPath().getList("id");
        } else return null;
    }

    public int getCustomerID(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        return Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
    }

    public Response getAllCustomerResponse(int pageIndex) {
        return api.get(GET_ALL_CUSTOMERS_PATH.formatted(loginInfo.getStoreID(), pageIndex), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<String> getAllCustomerNames() {
        return getAllCustomerResponse(0).jsonPath().getList("fullName");
    }

    public List<Integer> getAllCustomerIds() {
        return getAllCustomerResponse(0).jsonPath().getList("id");
    }

    /**
     * Retrieves a JsonPath object containing information about customers assigned to a specific staff member.
     * @param staffUserId The ID (userId) of the staff member whose assigned customers are to be retrieved.
     * @return A JsonPath object representing the retrieved customer data, enabling easy navigation and extraction of information.
     */
    public JsonPath getCustomersAssignedToStaffJsonPath(int staffUserId) {
        Response response = api.get(GET_ALL_CUSTOMERS_PATH.formatted(loginInfo.getStoreID(), 0) + "&responsibleStaffUserIds=%s".formatted(staffUserId), loginInfo.getAccessToken())
                .then().statusCode(200).extract().response();
        return response.jsonPath();
    }

    public List<String> getNamesOfCustomersAssignedToStaff(int staffUserId) {
        return getCustomersAssignedToStaffJsonPath(staffUserId).getList("fullName");
    }

    public List<Integer> getIdsOfCustomersAssignedToStaff(int staffUserId) {
        return getCustomersAssignedToStaffJsonPath(staffUserId).getList("id");
    }

    public List<String> getAllAccountCustomer() {
        return getAllCustomerResponse(0).jsonPath().getList("findAll { it.guest == false }.fullName");
    }

    public List<Integer> getAllAccountCustomerId() {
        return getAllCustomerResponse(0).jsonPath().getList("findAll { it.guest == false }.id");
    }

    public void exportCustomerFile() {
        Response response = api.get(EXPORT_CUSTOMER_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        response.then().statusCode(200);
    }

    public CustomerInfo getAccountCustomerForCreatePOS() {
        CustomerManagementInfo info = getCustomerManagementInfo();
        List<Integer> customerId =  info.getCustomerId();
        List<String> customerName = info.getCustomerName();
        List<Boolean> guestUser = info.getGuestUser();

        CustomerInfo customerInfo = new CustomerInfo();
        for (int id : customerId) {
            if (!guestUser.get(customerId.indexOf(id))) {
                customerInfo.setCustomerId(id);
                customerInfo.setMainEmailName(customerName.get(customerId.indexOf(id)));
                break;
            }
        }
        return customerInfo;
    }
}
