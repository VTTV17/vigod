package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class APIProductConversionUnit {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIProductConversionUnit(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getWithoutVariationConversionUnit(int productId) {
        return "/itemservice/api/conversion-unit-items/edit/%d".formatted(productId);
    }

    String getVariationConversionUnit(int productId, int modelId) {
        return "/itemservice/api/conversion-unit-items/edit/model/%d?modelId=%d".formatted(productId, modelId);
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConversionUnitItem {
        private int id;
        private int conversionUnitId;
        private int quantity;
        private int itemId;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer modelId;
        private int itemCloneId;
        private String sku;
        private String barcode;
        private long orgPrice;
        private long newPrice;
        private long costPrice;
        private int width;
        private int weight;
        private int height;
        private int length;
        private String conversionUnitName;
    }

    public List<ConversionUnitItem> getItemConversionUnit(int productId) {
        // Get product information
        APIProductDetailV2.ProductInfoV2 infoV2 = new APIProductDetailV2(loginInformation).getInfo(productId);
        return infoV2.isHasModel()
                ? infoV2.getVariationModelList()
                    .stream()
                    .mapToInt(modelId -> modelId)
                    .mapToObj(modelId -> api.get(getVariationConversionUnit(productId, modelId), loginInfo.getAccessToken())
                            .then()
                            .statusCode(200)
                            .extract()
                            .jsonPath()
                            .getList(".", ConversionUnitItem.class))
                    .flatMap(Collection::stream)
                    .toList()
                : api.get(getWithoutVariationConversionUnit(productId), loginInfo.getAccessToken())
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList("lstResult[0].data", ConversionUnitItem.class);
    }
}
