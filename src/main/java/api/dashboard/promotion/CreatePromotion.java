package api.dashboard.promotion;

import utilities.api.API;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.products.CreateProduct.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_FLASH_SALE_CAMPAIGN_NAME;
import static utilities.character_limit.CharacterLimit.MIN_FLASH_SALE_CAMPAIGN_NAME;

public class CreatePromotion {
    String CREATE_FLASH_SALE_PATH = "/itemservice/api/campaigns/";
    API api = new API();
    public void createFlashSale() {
        String flashSaleName = randomAlphabetic(nextInt(MAX_FLASH_SALE_CAMPAIGN_NAME - MIN_FLASH_SALE_CAMPAIGN_NAME + 1) + MIN_FLASH_SALE_CAMPAIGN_NAME);
        String startDate = Instant.now().plus(1, ChronoUnit.MINUTES).toString();
        String endDate = Instant.now().plus(2, ChronoUnit.MINUTES).toString();
        StringBuilder body = new StringBuilder("""
                {
                    "name": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "items": [""".formatted(flashSaleName, startDate, endDate));

        if (isVariation) {
            for (int i = 0; i < variationList.size(); i++) {
                if (variationStockQuantity.get(i) > 0) {
                    int saleStock = nextInt(variationStockQuantity.get(i)) + 1;
                    int limitPurchaseStock = nextInt(saleStock);
                    int modelID = variationModelID.get(i);
                    int price = nextInt(variationSellingPrice.get(i));

                    String flashSaleProduct = """
                            {
                                        "itemId": "%s",
                                        "limitPurchaseStock": "%s",
                                        "modelId": "%s",
                                        "price": "%s",
                                        "saleStock": "%s"
                                    }
                            """.formatted(productID, limitPurchaseStock, modelID, price, saleStock);
                    body.append(flashSaleProduct);
                    body.append(i < variationList.size() - 1 ? "," : "");
                }
            }
        } else {
            int saleStock = nextInt(withoutVariationStock);
            int limitPurchaseStock = nextInt(saleStock);
            int price = nextInt(withoutVariationSellingPrice);
            String flashSaleProduct = """
                    {
                                "itemId": "%s",
                                "limitPurchaseStock": "%s",
                                "price": "%s",
                                "saleStock": "%s"
                            }
                    """.formatted(productID, limitPurchaseStock, price, saleStock);
            body.append(flashSaleProduct);
        }
        body.append("]}");

//       Response createFlashSaleResponse =
        api.create(CREATE_FLASH_SALE_PATH + storeID, accessToken, String.valueOf(body)).prettyPrint();
    }
}
