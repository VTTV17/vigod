package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductConversionInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
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
    public List<ProductConversionInfo> getProductConversionInfoNoModel(int productParentId){
        Response response = api.get(getConversion_NoModel_Path.formatted(productParentId),loginInfo.getAccessToken());
        response.then().statusCode(200);
        List<ProductConversionInfo> productConversionInfoList = response.jsonPath().getList("lstResult[0].data", ProductConversionInfo.class);
//        response.prettyPrint();
//        List<ProductInfo> productInfoList = new ArrayList<>();
//        List<Integer> conversionUnitIds = response.jsonPath().get("lstResult[0].data.itemCloneId");
//        System.out.println("conversionUnitIds: "+conversionUnitIds);
//        List<Float> newPrices = response.jsonPath().getList("lstResult[0].data.newPrice",Float.class);
//
//        if(conversionUnitIds == null) return productInfoList;
//        for (int i = 0; i<conversionUnitIds.size();i++) {
//            List<Long> subNewPrice = new ArrayList<>();
//            subNewPrice.add(newPrices.get(i).longValue());
//
//            ProductInfo productInfo = new ProductInfo();
//            productInfo.setProductId(conversionUnitIds.get(i));
//            productInfo.setProductSellingPrice(subNewPrice);
//            productInfoList.add(productInfo);
//        }
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
