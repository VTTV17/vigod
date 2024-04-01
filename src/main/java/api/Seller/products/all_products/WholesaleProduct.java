package api.Seller.products.all_products;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Collections;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_STOCK_QUANTITY;
import static utilities.character_limit.CharacterLimit.MAX_WHOLESALE_PRICE_TITLE;
import static utilities.links.Links.STORE_CURRENCY;

public class WholesaleProduct {
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public WholesaleProduct(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public void addWholesalePriceProduct(ProductInfo productInfo) {
        String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productInfo.getProductID()));
        String segmentIDs = "ALL";
        int num = productInfo.isHasModel() ? nextInt(productInfo.getVariationModelList().size()) + 1 : 1;
        if (productInfo.isHasModel()) {
            for (int i = 0; i < num; i++) {
                long price = productInfo.getProductSellingPrice().get(i) == 0
                        ? productInfo.getProductSellingPrice().get(i)
                        : nextLong(productInfo.getProductSellingPrice().get(i)) + 1;
                int maxStock = Collections.max(productInfo.getProductStockQuantityMap().get(productInfo.getVariationModelList().get(i)));
                int stock = nextInt(Math.min(MAX_STOCK_QUANTITY, Math.max(maxStock, 1))) + 1;
                String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
                String variationWholesaleConfig = """
                        {
                            "id": null,
                            "title": "%s",
                            "minQuatity": %s,
                            "itemModelIds": "%s",
                            "currency": "%s",
                            "price": %s,
                            "segmentIds": "%s",
                            "itemId": "%s",
                            "action": null
                        }""".formatted(title, stock, "%s_%s".formatted(productInfo.getProductID(), productInfo.getVariationModelList().get(i).split("-")[1]), STORE_CURRENCY, price, segmentIDs, productInfo.getProductID());
                body.append(variationWholesaleConfig);
                body.append((i == (num - 1)) ? "" : ",");
            }
        } else {
            String title = randomAlphabetic(nextInt(MAX_WHOLESALE_PRICE_TITLE) + 1);
            long price = nextLong(productInfo.getProductSellingPrice().get(0)) + 1;
            int maxStock = Collections.max(productInfo.getProductStockQuantityMap().get(productInfo.getVariationModelList().get(0)));
            int stock = nextInt(Math.min(MAX_STOCK_QUANTITY, Math.max(maxStock, 1))) + 1;
            String variationWholesaleConfig = """
                    {
                        "id": null,
                        "title": "%s",
                        "minQuatity": %s,
                        "itemModelIds": "%s",
                        "currency": "%s",
                        "price": %s,
                        "segmentIds": "%s",
                        "itemId": "%s",
                        "action": null
                    }""".formatted(title, stock, productInfo.getProductID(), STORE_CURRENCY, price, segmentIDs, productInfo.getProductID());
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = new API().post(CREATE_WHOLESALE_PRICE_PATH, loginInfo.getAccessToken(), String.valueOf(body));
        addWholesale.then().statusCode(200);
    }
}
