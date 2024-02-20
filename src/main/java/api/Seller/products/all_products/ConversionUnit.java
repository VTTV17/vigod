package api.Seller.products.all_products;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.List;

public class ConversionUnit {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String getListConversionUnitPath = "/itemservice/api/item/conversion-units/search?page=0&size=10";
    String createConversionUnitPath = "/itemservice/api/item/conversion-units";
    public ConversionUnit(LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public String createConversionUnitAndGetName() {
        String name = "unitName" + Instant.now().toEpochMilli();
        String body = """
                {
                    "name": "%s"
                }""".formatted(name);
        api.post(createConversionUnitPath, loginInfo.getAccessToken(), body).then().statusCode(200);
        return name;
    }

    public List<String> getListConversionUnitName() {
        String body = """
                {
                    "lstItemId": [],
                    "key": null
                }""";
        Response response = api.post(getListConversionUnitPath, loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response.jsonPath().getList("content.name");
    }
}
