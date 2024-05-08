package api.Seller.affiliate.partner;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICreateEditPartner {
    String CREATE_PARTNER_PATH = "affiliateservice/api/partners/%s?langKey=vi&agencyCode=&fromSource=DASHBOARD";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICreateEditPartner(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public String getPartnerType(String...type){
        if(type.length == 0){
            return """
                    [
                            "RESELLER",
                            "DROP_SHIP"
                    ]
                    """;
        }else if(type[0].toLowerCase().equals("dropship")){
            return """
                    [
                            "DROP_SHIP"
                    ]
                    """;
        }else if (type[0].toLowerCase().equals("reseller")){
            return """
                    [
                            "RESELLER"
                    ]
                    """;
        }else
            try {
                throw new Exception("Partner type not found.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    public void createPartner(String...type){
        String random = new DataGenerator().generateNumber(10);
        String name = "Partner "+ random;
        String email = "email"+random+ "@mailnesia.com";
        String body = """
                {
                    "storeId": %s,
                    "name": "%s",
                    "email": "%s",
                    "phoneNumber": "03%s",
                    "phoneCode": "+84",
                    "partnerTypes": %s,
                    "address": "so33",
                    "cityCode": "VN-CT",
                    "districtCode": "5508",
                    "wardCode": "550802",
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
                }
                """.formatted(loginInfo.getStoreID(),name,email,random,getPartnerType(type));
        Response response = api.post(CREATE_PARTNER_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
    }
}
