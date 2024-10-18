package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class APIAddConversionUnit {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIAddConversionUnit(
            LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    @AllArgsConstructor
    public static class AddConversionUnitPayload {
        private String itemId;
        private boolean hasModel;
        private List<ConversionUnitItemDto> lstConversionUnitItemDto;
    }

    @Data
    @AllArgsConstructor
    public static class ConversionUnitItemDto {
        private final String id = "";
        private final String itemCloneId = "";
        private final String action = "";
        private int conversionUnitId;
        private int quantity;
        private String itemId;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer modelId;
        private final String sku = "";
        private final String barcode = "";
        private long costPrice;
        private long orgPrice;
        private long newPrice;
        private int weight;
        private int width;
        private int height;
        private int length;
    }

    String addConversionUnitPath = "/itemservice/api/conversion-unit-items";

    public AddConversionUnitPayload getAddConversionUnitPayload(int productId) {
        // Log
        LogManager.getLogger().info("Add conversion unit to productId: {}", productId);

        // Get product information
        APIProductDetailV2.ProductInfoV2 infoV2 = new APIProductDetailV2(loginInformation).getInfo(productId);

        // Get list units
        List<Integer> unitIds = new ConversionUnit(loginInformation).getListConversionUnitIds();

        // Get conversion unit ID
        int conversionUnitId = unitIds.isEmpty() ? new ConversionUnit(loginInformation).createConversionUnitAndGetId() : unitIds.get(0);

        // int exchange quantity
        int exchangeQuantity = nextInt(Collections.max(infoV2.getProductStockQuantityMap().values().stream().map(Collections::max).toList())) + 1;
        LogManager.getLogger().info("Exchange quantity: {}", exchangeQuantity);

        // Get list conversion unit items
        List<ConversionUnitItemDto> conversionUnitItems = infoV2.getVariationModelList()
                .stream().map(modelId -> new ConversionUnitItemDto(conversionUnitId,
                        exchangeQuantity,
                        String.valueOf(productId),
                        modelId,
                        (modelId != null)
                                ? infoV2.getProductCostPrice().get(infoV2.getVariationModelList().indexOf(modelId))
                                : infoV2.getCostPrice(),
                        (modelId != null)
                                ? infoV2.getProductListingPrice().get(infoV2.getVariationModelList().indexOf(modelId))
                                : infoV2.getOrgPrice(),
                        (modelId != null)
                                ? infoV2.getProductSellingPrice().get(infoV2.getVariationModelList().indexOf(modelId))
                                : infoV2.getNewPrice(),
                        infoV2.getShippingInfo().getWeight(),
                        infoV2.getShippingInfo().getWidth(),
                        infoV2.getShippingInfo().getHeight(),
                        infoV2.getShippingInfo().getLength()))
                .toList();

        // Return Add conversion unit payload
        return new AddConversionUnitPayload(String.valueOf(productId), infoV2.isHasModel(), conversionUnitItems);
    }

    @SneakyThrows
    public void addConversionUnitToProduct(int productId) {
        api.post(addConversionUnitPath, loginInfo.getAccessToken(), getAddConversionUnitPayload(productId)).then().statusCode(200);
    }
}
