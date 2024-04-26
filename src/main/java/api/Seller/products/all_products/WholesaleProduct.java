package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.products.wholesaleProduct.WholesaleProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_STOCK_QUANTITY;
import static utilities.character_limit.CharacterLimit.MAX_WHOLESALE_PRICE_TITLE;
import static utilities.links.Links.STORE_CURRENCY;

public class WholesaleProduct {
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();

    public WholesaleProduct(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public void addWholesalePriceProduct(ProductInfo productInfo) {
        String CREATE_WHOLESALE_PRICE_PATH = "/itemservice/api/item/wholesale-pricing";
        StringBuilder body = new StringBuilder("""
                {
                    "itemId": "%s",
                    "lstWholesalePricingDto": [""".formatted(productInfo.getProductId()));
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
                        }""".formatted(title, stock, "%s_%s".formatted(productInfo.getProductId(), productInfo.getVariationModelList().get(i).split("-")[1]), STORE_CURRENCY, price, segmentIDs, productInfo.getProductId());
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
                    }""".formatted(title, stock, productInfo.getProductId(), STORE_CURRENCY, price, segmentIDs, productInfo.getProductId());
            body.append(variationWholesaleConfig);
        }
        body.append("]}");

        Response addWholesale = api.post(CREATE_WHOLESALE_PRICE_PATH, loginInfo.getAccessToken(), String.valueOf(body));
        addWholesale.then().statusCode(200);
    }

    /**
     * return {barcode, list segment, list price, list stock}
     */
    String GET_WHOLESALE_PRODUCT_DETAIL_PATH = "/itemservice/api/item/wholesale-pricing/edit/%s?langKey=vi&page=0&size=100";
    public WholesaleProductInfo wholesaleProductInfo(ProductInfo productInfo, List<Integer> listSegmentOfCustomer) {
        /* get wholesale product raw data from API */
        Response wholesaleProductInfo = api.get(GET_WHOLESALE_PRODUCT_DETAIL_PATH.formatted(productInfo.getProductId()), loginInfo.getAccessToken());
        wholesaleProductInfo.then().statusCode(200);
        // get sale barcode group list
        List<String> barcodeList = wholesaleProductInfo.jsonPath().getList("lstResult.itemModelIds");
        // get sale price list
        List<Long> salePrice = Pattern.compile("price.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        // get sale stock list
        List<Integer> saleStock = Pattern.compile("minQuatity.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();
        // get sale segment group list
        JsonPath wholesaleJsonPath = wholesaleProductInfo.jsonPath();
        List<Object> segmentList = new ArrayList<>();
        for (int i = 0; i < wholesaleJsonPath.getList("lstResult").size(); i++) {
            for (int configId = 0; configId < wholesaleJsonPath.getList("lstResult[%s].paging.content".formatted(i)).size(); configId++) {
                segmentList.add(wholesaleJsonPath.getString("lstResult[%s].paging.content[%s].segmentIds".formatted(i, configId)));
            }
        }

        // get number config per group barcode
        List<Integer> totalElements = Pattern.compile("totalElements.{3}(\\d+)").matcher(wholesaleProductInfo.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList();

        /* raw data */
        List<APIProductDetail.WholesaleProductRawData> configs = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < totalElements.size(); i++) {
            APIProductDetail.WholesaleProductRawData wholesaleRawData = new APIProductDetail.WholesaleProductRawData();
            wholesaleRawData.setBarcode(barcodeList.get(i));
            List<Long> priceList = new ArrayList<>();
            List<Integer> stockList = new ArrayList<>();
            for (int id = index; id < index + totalElements.get(i); id++) {
                if (segmentList.get(id) != null) {
                    if (listSegmentOfCustomer != null) {
                        if (segmentList.get(id).equals("ALL") || (!listSegmentOfCustomer.isEmpty() && Arrays.stream(segmentList.get(id).toString().split(",")).toList().stream().anyMatch(segId -> listSegmentOfCustomer.contains(Integer.valueOf(segId)))))
                            if (stockList.contains(saleStock.get(id)))
                                priceList.set(stockList.indexOf(saleStock.get(id)), Math.min(salePrice.get(id), priceList.get(stockList.indexOf(saleStock.get(id)))));
                            else {
                                priceList.add(salePrice.get(id));
                                stockList.add(saleStock.get(id));
                            }
                    }
                }
            }
            wholesaleRawData.setPrice(priceList);
            wholesaleRawData.setStock(stockList);
            if (!stockList.isEmpty()) configs.add(wholesaleRawData);
            index += totalElements.get(i);
        }

        /* analyze data */
        // get product barcode list
        List<String> listVariationModelId = new ArrayList<>(productInfo.getVariationModelList());
        listVariationModelId.replaceAll(barcode -> barcode.replace("-", "_"));

        // get branch name
        List<String> branchNameList = new BranchManagement(loginInformation).getInfo().getBranchName();

        // init wholesale product status map
        Map<String, List<Boolean>> wholesaleProductStatus = new HashMap<>();

        List<String> saleBarcode = configs.stream().flatMap(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(","))).distinct().toList();

        // if variation has wholesale product => set status = true
        branchNameList.forEach(brName -> wholesaleProductStatus
                .put(brName, listVariationModelId.stream().map(saleBarcode::contains).toList()));

        // get wholesale product price
        List<Long> wholesaleProductPrice = new ArrayList<>(productInfo.getProductSellingPrice());
        configs.forEach(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(",")).toList().forEach(code -> wholesaleProductPrice.set(listVariationModelId.indexOf(code), wpConfig.getPrice().get(0))));

        // get wholesale product stock
        List<Integer> wholesaleProductStock = new ArrayList<>();
        IntStream.range(0, listVariationModelId.size()).forEachOrdered(i -> wholesaleProductStock.add(0));
        configs.forEach(wpConfig -> Arrays.stream(wpConfig.getBarcode().split(",")).toList().forEach(code -> wholesaleProductStock.set(listVariationModelId.indexOf(code), wpConfig.getStock().get(0))));

        WholesaleProductInfo analyzedData = new WholesaleProductInfo();
        analyzedData.setStatusMap(wholesaleProductStatus);
        analyzedData.setPriceList(wholesaleProductPrice);
        analyzedData.setStockList(wholesaleProductStock);
        return analyzedData;
    }
}
