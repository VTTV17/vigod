import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import utilities.api.API;

import java.util.List;

public class MigrateData {
    API api = new API();
    String URI = "https://api.beecow.info";
    int storeID;
    //    String username = "ketoankho.vlive@gmail.com";
//    String password = "xfmJgebl";
    String username = "newstaff@qa.team";
    String password = "Abc@12345";
    String commissionId;
    String loginPath = "/api/authenticate/mobile";
    String createCommissionPath = "/affiliateservice/api/commissions/";
    String accessToken;
    String userId;

    void loginDashboard(String username, String password) {
        RestAssured.baseURI = URI;
        String body = """
                {
                    "username": "%s",
                    "password": "%s",
                    "rememberMe": true
                }""".formatted(username, password);
        Response loginRes = api.login(loginPath, body);
        accessToken = loginRes.jsonPath().getString("accessToken");
        loginRes.then().statusCode(200);
        userId = loginRes.jsonPath().getString("id");
    }

    void getStoreID() {
        String storeInfoPath = "/storeservice/api/store-staffs/user/%s".formatted(userId);
        Response storeRes = api.get(storeInfoPath, accessToken);
        storeRes.then().statusCode(200);
        storeID = storeRes.jsonPath().getInt("id[0]");
    }

    void getStaffAccessToken() {
        String switchStaffPath = "/api/authenticate/store/%s/switch-staff".formatted(storeID);
        Response staffRes = api.post(switchStaffPath, accessToken);
        accessToken = staffRes.jsonPath().getString("accessToken");
        staffRes.prettyPrint();
        storeID = staffRes.jsonPath().getInt("store.id");
    }

    void createRevenueCommission() {
        String body = """
                {
                     "name": "Revenue commission for all partner",
                     "typeCommission": "REVENUE_COMMISSION",
                     "rate": 0,
                     "storeId": "%s",
                     "accumulateCommissionList": [
                         {
                             "rate": "30",
                             "revenueFrom": "5000000"
                         },
                         {
                             "rate": "35",
                             "revenueFrom": "10000000"
                         },
                         {
                             "rate": "37",
                             "revenueFrom": "50000000"
                         },
                         {
                             "rate": "40",
                             "revenueFrom": "180000000"
                         },
                         {
                             "rate": "43",
                             "revenueFrom": "600000000"
                         }
                     ],
                     "timeFrame": "MONTHLY"
                 }""".formatted(storeID);

        Response cRes = api.post(createCommissionPath, accessToken, body);
        cRes.then().statusCode(201);
        commissionId = cRes.jsonPath().getString("id");

    }

    List<String> getAllDropShipId() {
        String dropShipListPath = "/affiliateservice/api/partners/%s?partnerType=DROP_SHIP&partnerStatus=ACTIVATED&page=0&size=2000".formatted(storeID);
        Response drsRes = api.get(dropShipListPath, accessToken);
        drsRes.then().statusCode(200);
        return drsRes.jsonPath().getList("id").stream().map(Object::toString).toList();
    }

    PartnerInformationModel getPartnerInformation(String partnerId, String commissionId) {
        PartnerInformationModel pModel = new PartnerInformationModel();
        String partnerInfoPath = "/affiliateservice/api/partners/%s/pid/%s".formatted(storeID, partnerId);
        Response pRes = api.get(partnerInfoPath, accessToken);
        pRes.then().statusCode(200);
        JsonPath pInfo = pRes.jsonPath();

        pModel.setId(pInfo.getString("id"));
        pModel.setStoreId(pInfo.getString("storeId"));
        pModel.setName(pInfo.getString("name"));
        pModel.setEmail(pInfo.getString("email"));
        pModel.setPhoneCode(pInfo.getString("phoneCode") == null ? "" : pInfo.getString("phoneCode"));
        pModel.setPhoneNumber(pInfo.getString("phoneNumber"));
        pModel.setAddress(pInfo.getString("address"));
        pModel.setWardCode(pInfo.getString("wardCode") == null ? "" : pInfo.getString("wardCode"));
        pModel.setDistrictCode(pInfo.getString("districtCode") == null ? "" : pInfo.getString("districtCode"));
        pModel.setCityCode(pInfo.getString("cityCode") == null ? "" : pInfo.getString("cityCode"));
        pModel.setCountryCode(pInfo.getString("countryCode"));
        pModel.setPartnerCode(pInfo.getString("partnerCode"));
        pModel.setPartnerTypes(pInfo.getString("partnerType"));
        pModel.setBankName(pInfo.getString("payment.bankName"));
        pModel.setBankBranchName(pInfo.getString("payment.bankBranchName"));
        pModel.setAccountHolder(pInfo.getString("payment.accountHolder"));
        pModel.setAccountNumber(pInfo.getString("payment.accountNumber"));
        pModel.setPaymentCountryCode(pInfo.getString("payment.countryCode"));
        pModel.setCommissionId(commissionId);
        pModel.setTaxId(pInfo.getString("taxId") == null ? "" : pInfo.getString("taxId"));
        pModel.setIssuedDate(pInfo.getString("issuedDate") == null ? "" : pInfo.getString("issuedDate"));
        pModel.setIssuedPlace(pInfo.getString("issuedPlace") == null ? "" : pInfo.getString("issuedPlace"));
        pModel.setIdentityCardId(pInfo.getString("identityCardId") == null ? "" : pInfo.getString("identityCardId"));

        return pModel;
    }

    void updatePartnerInformation(String partnerId, String commissionId) {
        PartnerInformationModel pModel = getPartnerInformation(partnerId, commissionId);
        String updatePartnerPath = "/affiliateservice/api/partners/%s?langKey=vi&agencyCode=".formatted(storeID);
        String body = """
                {
                    "storeId": %s,
                    "name": "%s",
                    "email": "%s",
                    "phoneNumber": "%s",
                    "phoneCode": "%s",
                    "partnerTypes": "%s",
                    "address": "%s",
                    "cityCode": "%s",
                    "districtCode": "%s",
                    "wardCode": "%s",
                    "payment": {
                        "bankName": "%s",
                        "bankBranchName": "%s",
                        "accountHolder": "%s",
                        "accountNumber": "%s",
                        "countryCode": "%s",
                        "routingNumber": null
                    },
                    "partnerCode": "%s",
                    "countryCode": "%s",
                    "commissionIds": [%s],
                    "taxRate": 10,
                    "issuedDate": "%s",
                    "identityCardId": "%s",
                    "issuedPlace": "%s",
                    "taxId": "%s",
                    "id": %s,
                    "langKey": "vn",
                    "agencyCode": ""
                }""".formatted(pModel.getStoreId(),
                pModel.getName(),
                pModel.getEmail(),
                pModel.getPhoneNumber(),
                pModel.getPhoneCode(),
                pModel.getPartnerTypes(),
                pModel.getAddress(),
                pModel.getCityCode(),
                pModel.getDistrictCode(),
                pModel.getWardCode(),
                pModel.getBankName(),
                pModel.getBankBranchName(),
                pModel.getAccountHolder(),
                pModel.getAccountNumber(),
                pModel.getPaymentCountryCode(),
                pModel.getPartnerCode(),
                pModel.getCountryCode(),
                pModel.getCommissionId(),
                pModel.getIssuedDate(),
                pModel.getIdentityCardId(),
                pModel.getIssuedPlace(),
                pModel.getTaxId(),
                pModel.getId());
        Response rs = api.put(updatePartnerPath, accessToken, body);
        System.out.printf("%s: %s%n", partnerId, rs.getStatusCode());

    }

    @BeforeSuite
    void setup() {
        loginDashboard(username, password);
        getStoreID();
        getStaffAccessToken();
    }
    @Test
    void migrateTaxRateAndCommissionType() {
        createRevenueCommission();
        List<String> pId = getAllDropShipId();
        for (String p : pId) {
            updatePartnerInformation(p, commissionId);
        }
    }
}
