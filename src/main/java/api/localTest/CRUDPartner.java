package api.localTest;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CRUDPartner {
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public CRUDPartner(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        this.loginInfo = new Login().getInfo(loginInformation);
    }

    API api = new API();
    String crudPartnerPath = "/affiliateservice/api/partners/%s?langKey=vi&agencyCode=&fromSource=DASHBOARD";

    Response createPartnerResponse(String phoneCode, String phoneNumber) {
        String body = """
                {
                    "storeId": %s,
                    "name": "%s",
                    "email": "1234@qa.team",
                    "phoneNumber": "%s",
                    "phoneCode": "%s",
                    "partnerTypes": [
                        "DROP_SHIP"
                    ],
                    "address": "",
                    "cityCode": "",
                    "districtCode": "",
                    "wardCode": "",
                    "payment": {
                        "bankName": "",
                        "bankBranchName": "",
                        "bankCity": "VN-44",
                        "accountHolder": "",
                        "accountNumber": "",
                        "countryCode": "VN",
                        "routingNumber": null
                    },
                    "partnerCode": "",
                    "allowUpdatePrice": false,
                    "countryCode": "VN",
                    "commissionIds": [],
                    "taxRate": 0,
                    "identityCardId": "",
                    "issuedPlace": "",
                    "taxId": "",
                    "viewDownline": -1,
                    "viewOrderDownline": -1,
                    "langKey": "vi",
                    "agencyCode": ""
                }""".formatted(loginInfo.getStoreID(), RandomStringUtils.randomAlphabetic(5), phoneNumber, phoneCode);
        return api.post(crudPartnerPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
    }

    Response updatePartnerResponse(int partnerId, String partnerCode, String phoneCode, String phoneNumber) {
        String body = """
                {
                    "storeId": %s,
                    "name": "%s",
                    "email": "1234@qa.team",
                    "phoneNumber": "%s",
                    "phoneCode": "%s",
                    "partnerTypes": [
                        "DROP_SHIP"
                    ],
                    "address": "",
                    "cityCode": "",
                    "districtCode": "",
                    "wardCode": "",
                    "payment": {
                        "bankName": "",
                        "bankBranchName": "",
                        "bankCity": "VN-44",
                        "accountHolder": "",
                        "accountNumber": "",
                        "countryCode": "VN",
                        "routingNumber": null
                    },
                    "partnerCode": "%s",
                    "allowUpdatePrice": false,
                    "countryCode": "VN",
                    "commissionIds": [],
                    "taxRate": 0,
                    "identityCardId": "",
                    "issuedPlace": "",
                    "taxId": "",
                    "viewDownline": -1,
                    "id": %s,
                    "viewOrderDownline": -1,
                    "langKey": "vi",
                    "agencyCode": ""
                }""".formatted(loginInfo.getStoreID(), RandomStringUtils.randomAlphabetic(5), phoneNumber, phoneCode, partnerCode, partnerId);
        return api.put(crudPartnerPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
    }

    int partnerId;
    String partnerCode;

    List<String> partnerCodes = List.of("UONDTTLC", "DMHSOOSD");
    List<Integer> partnerIds = List.of(5729403, 5733707);

    public void checkCRUPartner() {
        DataGenerator dataGenerator = new DataGenerator();
        List<String> countryList = dataGenerator.getCountryList();
        countryList.forEach(countryName -> {
            String phoneCode = dataGenerator.getPhoneCode(countryName);

            /* ------------------------------------------------ */
            // CREATE
            // check phone number not start with "0"
            String phoneNumber = String.valueOf(Instant.now().toEpochMilli());
            partnerId = 0;
            partnerCode = "";
            while (partnerId == 0) {
                try {
                    Response response = createPartnerResponse(phoneCode, phoneNumber).then().statusCode(200).extract().response();
                    partnerId = response.jsonPath().getInt("partners[0].id");
                    partnerCode = response.jsonPath().getString("partners[0].partnerCode");
                } catch (AssertionError error) {
                    phoneNumber = String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli());
                }
            }
            System.out.printf("[Create] PartnerId: %s, code: %s, phoneNumber:%s:%s%n", partnerId, partnerCode, phoneCode, phoneNumber);

            Response response = createPartnerResponse(phoneCode, "0" + phoneNumber);
            System.out.printf("[Create] Check phone: %s:0%s%n", phoneCode, phoneNumber);

            while (response.statusCode() == 500) {
                response = createPartnerResponse(phoneCode, "0" + phoneNumber);
            }
            if (phoneCode.equals("+7")) response.prettyPrint();
            boolean check = response.getBody().asString().contains("error.user.id.is.partner.validate")
                    || response.getBody().asString().contains("error.partner.already.exist");
            if (!check) System.out.printf("[Create][ERR] Phone number: %s:0%s%n", phoneCode, phoneNumber);

            // check phone number start with "0"
            phoneNumber = String.valueOf(Instant.now().toEpochMilli());
            partnerId = 0;
            partnerCode = "";
            while (partnerId == 0) {
                try {
                    response = createPartnerResponse(phoneCode, "0" + phoneNumber).then().statusCode(200).extract().response();
                    partnerId = response.jsonPath().getInt("partners[0].id");
                    partnerCode = response.jsonPath().getString("partners[0].partnerCode");
                } catch (AssertionError error) {
                    phoneNumber = String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli());
                }
            }
            System.out.printf("[Create] PartnerId: %s, code: %s, phoneNumber:%s:0%s%n", partnerId, partnerCode, phoneCode, phoneNumber);

            response = createPartnerResponse(phoneCode, phoneNumber);
            System.out.printf("[Create] Check phone: %s:%s%n", phoneCode, phoneNumber);

            while (response.statusCode() == 500) {
                response = createPartnerResponse(phoneCode, phoneNumber);
            }
            check = response.getBody().asString().contains("error.user.id.is.partner.validate")
                    || response.getBody().asString().contains("error.partner.already.exist");
            if (phoneCode.equals("+7")) response.prettyPrint();
            if (!check) System.out.printf("[Create][ERR] Phone number: %s:%s%n", phoneCode, phoneNumber);

            /* ------------------------------------------------ */
            // UPDATE
            // check phone number not start with "0"
            phoneNumber = String.valueOf(Instant.now().toEpochMilli());
            partnerId = partnerIds.get(0);
            partnerCode = partnerCodes.get(0);
            while (true) {
                try {
                    updatePartnerResponse(partnerId, partnerCode, phoneCode, phoneNumber).then().statusCode(200).extract().response();
                    break;
                } catch (AssertionError error) {
                    phoneNumber = String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli());
                }
            }
            System.out.printf("[Update] PartnerId: %s, code: %s, phoneNumber:%s:%s%n", partnerId, partnerCode, phoneCode, phoneNumber);

            partnerId = partnerIds.get(1);
            partnerCode = partnerCodes.get(1);
            response = updatePartnerResponse(partnerId, partnerCode, phoneCode, "0" + phoneNumber);
            System.out.printf("[Update] Check phone: %s:0%s%n", phoneCode, phoneNumber);

            while (response.statusCode() == 500) {
                response = updatePartnerResponse(partnerId, partnerCode, phoneCode, "0" + phoneNumber);
            }
            if (phoneCode.equals("+7")) response.prettyPrint();
            check = response.getBody().asString().contains("error.user.id.is.partner.validate")
                    || response.getBody().asString().contains("error.partner.already.exist");
            if (!check) System.out.printf("[Update][ERR] Phone number: %s:0%s%n", phoneCode, phoneNumber);

            // check phone number start with "0"
            phoneNumber = String.valueOf(Instant.now().toEpochMilli());
            partnerId = partnerIds.get(0);
            partnerCode = partnerCodes.get(0);
            while (true) {
                try {
                    updatePartnerResponse(partnerId, partnerCode, phoneCode, "0" + phoneNumber).then().statusCode(200).extract().response();
                    break;
                } catch (AssertionError error) {
                    phoneNumber = String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli());
                }
            }
            System.out.printf("[Update] PartnerId: %s, code: %s, phoneNumber:%s:0%s%n", partnerId, partnerCode, phoneCode, phoneNumber);

            partnerId = partnerIds.get(1);
            partnerCode = partnerCodes.get(1);
            response = updatePartnerResponse(partnerId, partnerCode, phoneCode, phoneNumber);
            System.out.printf("[Update] Check phone: %s:%s%n", phoneCode, phoneNumber);

            while (response.statusCode() == 500) {
                response = updatePartnerResponse(partnerId, partnerCode, phoneCode, phoneNumber);
            }
            check = response.getBody().asString().contains("error.user.id.is.partner.validate")
                    || response.getBody().asString().contains("error.partner.already.exist");
            if (phoneCode.equals("+7")) response.prettyPrint();
            if (!check) System.out.printf("[Update][ERR] Phone number: %s:%s%n", phoneCode, phoneNumber);
        });
    }

}
