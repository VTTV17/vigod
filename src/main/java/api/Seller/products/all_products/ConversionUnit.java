package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductConversionInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversionUnit {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String getListConversionUnitPath = "/itemservice/api/item/conversion-units/search?page=0&size=10";
    String createConversionUnitPath = "/itemservice/api/item/conversion-units";
    String getConversion_NoModel_Path = "/itemservice/api/conversion-unit-items/edit/%s";
    String getConversion_HasModel_Path = "/itemservice/api/conversion-unit-items/edit/model/%s?modelId=%s";
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

    public int createConversionUnitAndGetId() {
        String name = "unitName" + Instant.now().toEpochMilli();
        String body = """
                {
                    "name": "%s"
                }""".formatted(name);
        return api.post(createConversionUnitPath, loginInfo.getAccessToken(), body).then().statusCode(200)
                .extract()
                .jsonPath()
                .getInt("id");
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConversionUnits {
        private int id;
        private String name;
    }

    public List<ConversionUnits> getAllConversionUnits() {
        String body = """
                {
                    "lstItemId": [],
                    "key": null
                }""";
        return api.post(getListConversionUnitPath, loginInfo.getAccessToken(), body)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("content", ConversionUnits.class);
    }

    public List<String> getListConversionUnitName() {
        return getAllConversionUnits().stream().map(ConversionUnits::getName).toList();
    }

    public List<Integer> getListConversionUnitIds() {
        return getAllConversionUnits().stream().map(ConversionUnits::getId).toList();
    }
    public List<ProductConversionInfo> getProductConversionInfo(int productParentId){
        Response response = api.get(getConversion_NoModel_Path.formatted(productParentId),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<ProductConversionInfo> productConversionInfoList = new ArrayList<>();
        for(int i = 0;i< response.jsonPath().getList("lstResult").size();i++){
            productConversionInfoList.addAll(response.jsonPath().getList("lstResult[%s].data".formatted(i), ProductConversionInfo.class)) ;
        }
        return productConversionInfoList;
    }
    public List<ProductConversionInfo> getProductConversionInfoHasModel(int productParentId, int modelId){
        Response response = api.get(getConversion_HasModel_Path.formatted(productParentId,modelId),loginInfo.getAccessToken());
        response.then().statusCode(200);
        response.prettyPrint();
        List<ProductConversionInfo> productConversionInfoList = Arrays.asList(response.as(ProductConversionInfo[].class));
        return productConversionInfoList;
    }
}
