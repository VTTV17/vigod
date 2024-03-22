package api.Seller.supplier.supplier;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class APISupplier {
    String GET_SUPPLIER_LIST = "/itemservice/api/suppliers/store/%s?page=%s&size=100&sort=id,desc&itemNameOrCode=%s";
    String CREATE_SUPPLIER_PATH = "/itemservice/api/suppliers";
    String SUPPLIER_DETAIL_PATH = "/itemservice/api/suppliers/%s";
    String GET_SUPPLIER_ORDER_HISTORY_PATH = "/itemservice/api/purchase-orders/store-id/%s?searchBy=id&purchaseId=%s&supplierId=%s&page=0&size=5&sort=id,desc";
    API api = new API();
    LoginDashboardInfo loginInfo;

    public APISupplier(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public static class AllSupplierInformation {
        List<Integer> ids = new ArrayList<>();
        List<String> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        List<Integer> totalBalance = new ArrayList<>();
        List<String> statues = new ArrayList<>();
    }

    public Response getAllSupplierResponse(String supplierCode, int pageIndex) {
        return api.get(GET_SUPPLIER_LIST.formatted(loginInfo.getStoreID(), pageIndex, supplierCode), loginInfo.getAccessToken());
    }

    public AllSupplierInformation getAllSupplierInformation(String... supplierCode) {
        String code = (supplierCode.length > 0) ? supplierCode[0] : "";

        // init model
        AllSupplierInformation info = new AllSupplierInformation();

        // init temp array
        List<Integer> ids = new ArrayList<>();
        List<String> codes = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        List<Integer> totalBalances = new ArrayList<>();
        List<String> statues = new ArrayList<>();

        // get page 0 response
        Response response = getAllSupplierResponse(code, 0);

        // if staff do not have permission view list, end.
        if (response.statusCode() == 403) return info;

        // get total products
        int totalOfSuppliers = Integer.parseInt(response.getHeader("X-Total-Count"));

        // get number of pages
        int numberOfPages = totalOfSuppliers / 100;

        // get other page data
        for (int pageIndex = 0; pageIndex <= numberOfPages; pageIndex++) {
            JsonPath jsonPath = getAllSupplierResponse(code, pageIndex)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath();
            ids.addAll(jsonPath.getList("id"));
            codes.addAll(jsonPath.getList("code"));
            names.addAll(jsonPath.getList("name"));
            emails.addAll(jsonPath.getList("email"));
            phoneNumbers.addAll(jsonPath.getList("phoneNumber"));
            totalBalances.addAll(jsonPath.getList("totalBalance"));
            statues.addAll(jsonPath.getList("status"));
        }

        // get info
        info.setIds(ids);
        info.setCodes(codes);
        info.setNames(names);
        info.setEmails(emails);
        info.setPhoneNumbers(phoneNumbers);
        info.setTotalBalance(totalBalances);
        info.setStatues(statues);

        return info;
    }

    public List<String> getAllSupplierNames() {
        return getAllSupplierInformation().getNames();
    }

    public List<String> getListSupplierCode(String supplierCode) {
        return getAllSupplierInformation(supplierCode).getCodes();
    }

    public List<Integer> getListSupplierID(String supplierCode) {
        return getAllSupplierInformation(supplierCode).getIds();
    }

    public JsonPath createSupplier() {
        String name = "Auto - Supplier %s".formatted(Instant.now().toEpochMilli());
        String body = """
                {
                    "address": "",
                    "code": "",
                    "district": "",
                    "email": "",
                    "name": "%s",
                    "phoneNumber": "",
                    "province": "",
                    "ward": "",
                    "storeId": "%s",
                    "address2": "",
                    "countryCode": "VN",
                    "zipCode": "",
                    "cityName": ""
                }""".formatted(name, loginInfo.getStoreID());
        return api.post(CREATE_SUPPLIER_PATH, loginInfo.getAccessToken(), body)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath();
    }

    public String createSupplierAndGetSupplierCode() {
        return createSupplier().getString("code");
    }

    public int createSupplierAndGetSupplierID() {
        return createSupplier().getInt("id");
    }

    public JsonPath getSupplierInformationJsonPath(int supplierID) {
        return api.get(SUPPLIER_DETAIL_PATH.formatted(supplierID), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath();

    }

    @Data
    public static class SupplierInformation {
        String name;
        String code;
        String phoneCode;
        String phoneNumber;
        String email;
        String countryCode;
        String address;
        String streetAddress;
        String address2;
        String cityName;
        String district;
        String ward;
        String zipcode;
        String responsibleStaff;
        String description;
        String province;
        boolean isVNSupplier;
    }

    public SupplierInformation getSupplierInformation(int supplierID) {
        SupplierInformation supplierInfo = new SupplierInformation();
        JsonPath supJsonPath = getSupplierInformationJsonPath(supplierID);
        supplierInfo.setName(supJsonPath.getString("name"));
        supplierInfo.setCode(supJsonPath.getString("code"));
        supplierInfo.setPhoneCode(supJsonPath.getString("phoneCode"));
        supplierInfo.setPhoneNumber(supJsonPath.getString("phoneNumber"));
        supplierInfo.setEmail(supJsonPath.getString("email"));
        supplierInfo.setCountryCode(supJsonPath.getString("countryCode"));
        supplierInfo.setAddress(supJsonPath.getString("address"));
        supplierInfo.setStreetAddress(supplierInfo.getAddress());
        supplierInfo.setAddress2(supJsonPath.getString("address2"));
        supplierInfo.setCityName(supJsonPath.getString("cityName"));
        supplierInfo.setDistrict(supJsonPath.getString("district"));
        supplierInfo.setWard(supJsonPath.getString("ward"));
        supplierInfo.setZipcode(supJsonPath.getString("zipCode"));
        supplierInfo.setResponsibleStaff(supJsonPath.getString("responsibleStaff"));
        supplierInfo.setDescription(supJsonPath.getString("description"));
        supplierInfo.setProvince(supJsonPath.getString("province"));
        return supplierInfo;
    }

    public List<String> getListOrderId(String keyword, int supplierId) {
        return api.get(GET_SUPPLIER_ORDER_HISTORY_PATH.formatted(loginInfo.getStoreID(), keyword, supplierId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("purchaseId");
    }

}
