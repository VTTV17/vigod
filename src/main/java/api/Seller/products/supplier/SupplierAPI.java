package api.Seller.products.supplier;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.sellerApp.supplier.SupplierInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SupplierAPI {
    String GET_SUPPLIER_LIST = "/itemservice/api/suppliers/store/%s?page=0&size=100&nameOrCode=%s";
    String CREATE_SUPPLIER_PATH = "/itemservice/api/suppliers";
    String SUPPLIER_DETAIL_PATH = "/itemservice/api/suppliers/%s";
    String GET_SUPPLIER_ORDER_HISTORY_PATH = "/itemservice/api/purchase-orders/store-id/%s?searchBy=id&purchaseId=%s&supplierId=%s&page=0&size=5&sort=id,desc";
    API api = new API();
    LoginDashboardInfo loginInfo;

    public SupplierAPI(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
    }

    public JsonPath getAllSupplierJsonPath(String supplierCode) {
        Response response = api.get(GET_SUPPLIER_LIST.formatted(loginInfo.getStoreID(), supplierCode), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath();
    }

    public List<String> getAllSupplierNames() {
        return getAllSupplierJsonPath("").getList("name");
    }

    public List<String> getListSupplierCode(String supplierCode) {
        return getAllSupplierJsonPath(supplierCode).getList("code");
    }

    public List<Integer> getListSupplierID(String supplierCode) {
        return getAllSupplierJsonPath(supplierCode).getList("id");
    }

    public List<SupplierInformation> getListSupplierInformation() {
        List<SupplierInformation> supplierInformationList = new ArrayList<>();
        JsonPath allSupplierJsonPath = getAllSupplierJsonPath("");
        List<String> supplierNameList = allSupplierJsonPath.getList("code");
        List<String> supplierCodeList = allSupplierJsonPath.getList("name");
        IntStream.range(0, supplierCodeList.size()).forEach(index -> {
            var supInfo = new SupplierInformation();
            supInfo.setSupplierCode(supplierCodeList.get(index));
            supInfo.setSupplierName(supplierNameList.get(index));
            supplierInformationList.add(supInfo);
        });
        return supplierInformationList;
    }

    public JsonPath createSupplier() {
        String body = """
                {
                    "address": "",
                    "code": "",
                    "district": "",
                    "email": "",
                    "name": "aas",
                    "phoneNumber": "",
                    "province": "",
                    "ward": "",
                    "storeId": "%s",
                    "address2": "",
                    "countryCode": "VN",
                    "zipCode": "",
                    "cityName": ""
                }""".formatted(loginInfo.getStoreID());
        Response createSupplierResponse = api.post(CREATE_SUPPLIER_PATH, loginInfo.getAccessToken(), body);
        createSupplierResponse.then().statusCode(201);

        return createSupplierResponse.jsonPath();
    }

    public String createSupplierAndGetSupplierCode() {
        return createSupplier().getString("code");
    }

    public int createSupplierAndGetSupplierID() {
        return createSupplier().getInt("id");
    }

    public JsonPath getSupplierInformationJsonPath(int supplierID) {
        Response supInfo = api.get(SUPPLIER_DETAIL_PATH.formatted(supplierID), loginInfo.getAccessToken());
        supInfo.then().statusCode(200);
        return supInfo.jsonPath();
    }

    /**
     * Key: name, code, phoneCode, phoneNumber, email, countryCode, address, address2, cityName, district, ward, zipcode, responsibleStaffName and description
     */
    public Map<String, String> getSupplierInformationMap(int supplierID) {
        Map<String, String> supplierInfo = new HashMap<>();
        JsonPath supJsonPath = getSupplierInformationJsonPath(supplierID);
        supplierInfo.put("name", supJsonPath.getString("name"));
        supplierInfo.put("code", supJsonPath.getString("code"));
        supplierInfo.put("phoneCode", supJsonPath.getString("phoneCode"));
        supplierInfo.put("phoneNumber", supJsonPath.getString("phoneNumber"));
        supplierInfo.put("email", supJsonPath.getString("email"));
        supplierInfo.put("countryCode", supJsonPath.getString("countryCode"));
        supplierInfo.put("address", supJsonPath.getString("address"));
        supplierInfo.put("address2", supJsonPath.getString("address2"));
        supplierInfo.put("cityName", supJsonPath.getString("cityName"));
        supplierInfo.put("district", supJsonPath.getString("district"));
        supplierInfo.put("ward", supJsonPath.getString("ward"));
        supplierInfo.put("zipCode", supJsonPath.getString("zipCode"));
        supplierInfo.put("responsibleStaff", supJsonPath.getString("responsibleStaff"));
        supplierInfo.put("description", supJsonPath.getString("description"));
        supplierInfo.put("province", supJsonPath.getString("province"));
        return supplierInfo;
    }

    public List<String> getListOrderId(String keyword, int supplierId) {
        Response getListOrderID = api.get(GET_SUPPLIER_ORDER_HISTORY_PATH.formatted(loginInfo.getStoreID(), keyword, supplierId), loginInfo.getAccessToken());
        getListOrderID.then().statusCode(200);
        return getListOrderID.jsonPath().getList("purchaseId");
    }

}
