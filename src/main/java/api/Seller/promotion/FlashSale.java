package api.Seller.promotion;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class FlashSale {
    String CREATE_FLASH_SALE_PATH = "/itemservice/api/campaigns/";
    String END_EARLY_FLASH_SALE_PATH = "/itemservice/api/campaigns/end-early/";
    String DELETE_FLASH_SALE_PATH = "/itemservice/api/campaigns/delete/%s?storeId=%s";
    String FLASH_SALE_LIST_PATH = "/itemservice/api/campaigns/search/";
    String FLASH_SALE_DETAIL = "/itemservice/api/campaigns/%s?storeId=%s";
    API api = new API();
    Logger logger = LogManager.getLogger(FlashSale.class);

    // flash sale
    private List<Long> flashSalePrice;

    private List<Integer> flashSaleStock;
    private Map<String, List<String>> flashSaleStatus = new HashMap<>();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    BranchInfo brInfo;

    public FlashSale(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    @Data
    public class FlashSaleInfo {
        private List<Long> flashSalePrice;
        private List<Integer> flashSaleStock;
        private Map<String, List<String>> flashSaleStatus;
        private String flashSaleName;
        private int flashSaleId;
    }

    public void endEarlyFlashSale() {
        // get schedule flash sale list
        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        logger.debug("schedule flash sale list: %s".formatted(scheduleList));
        if (scheduleList != null)
            scheduleList.forEach(id -> new API().delete(DELETE_FLASH_SALE_PATH.formatted(id, loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200));

        // get in progress flash sale
        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        logger.debug("in-progress flash sale list: %s".formatted(inProgressList));
        if (inProgressList != null)
            inProgressList.forEach(id -> new API().post("%s%s?storeId=%s".formatted(END_EARLY_FLASH_SALE_PATH, id, loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200));
    }

    String getItem(ProductInfo productInfo, int varIndex) {
        // get model
        String model = productInfo.getVariationModelList().get(varIndex);

        // if variation stock > 0 => add variation to flash sale campaign
        if (Collections.max(productInfo.getProductStockQuantityMap().get(model))  > 0) {
            int stock = nextInt(Collections.max(productInfo.getProductStockQuantityMap().get(model))) + 1;

            // purchase limit
            int purchaseLimit = nextInt(stock) + 1;

            // variation price
            long price = nextLong(productInfo.getProductSellingPrice().get(varIndex));

            return productInfo.isHasModel() ? """
                {
                            "itemId": "%s",
                            "limitPurchaseStock": "%s",
                            "modelId": "%s",
                            "price": "%s",
                            "saleStock": "%s"
                        }
                """.formatted(productInfo.getProductId(), purchaseLimit, model.split("-")[1], price, stock)
                    : """
                {
                            "itemId": "%s",
                            "limitPurchaseStock": "%s",
                            "price": "%s",
                            "saleStock": "%s"
                        }
                """.formatted(productInfo.getProductId(), purchaseLimit, price, stock);
        } else return "";
    }

    String getItems(ProductInfo productInfo) {
        return IntStream.range(0, nextInt(productInfo.getVariationModelList().size()) + 1).mapToObj(varIndex -> getItem(productInfo, varIndex)).filter(item -> !item.isEmpty()).toList().toString();
    }

    String getFlashSaleBody(ProductInfo productInfo, int... time) {
        // flash sale name
        String flashSaleName = "Auto - Flash sale campaign - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        // start date
        int startMin = time.length > 0 ? time[0] : nextInt(60);
        Instant flashSaleStartTime = Instant.now().plus(startMin, ChronoUnit.MINUTES);

        // end date
        int endMin = time.length > 1 ? time[1] : startMin + nextInt(60);
        Instant flashSaleEndTime = Instant.now().plus(endMin, ChronoUnit.MINUTES);

        return """
                {
                    "name": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "items": %s}""".formatted(flashSaleName, flashSaleStartTime, flashSaleEndTime, getItems(productInfo));
    }

    public void createFlashSale(ProductInfo productInfo, int... time) {
        endEarlyFlashSale();
        // post api create new flash sale campaign
        Response createFlashSale = api.post(CREATE_FLASH_SALE_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), getFlashSaleBody(productInfo, time));

        logger.debug("Flash sale id: %s.".formatted(createFlashSale.jsonPath().getInt("id")));

        createFlashSale.then().statusCode(200);

    }

    void getFlashSaleInformation(int flashSaleID, List<String> listVariationModelId) {
        Response flashSaleDetail = api.get(FLASH_SALE_DETAIL.formatted(flashSaleID, loginInfo.getStoreID()), loginInfo.getAccessToken());
        flashSaleDetail.then().statusCode(200);
        JsonPath flashSaleDetailJson = flashSaleDetail.jsonPath();

        // init flash sale stock list
        if (flashSaleStock == null) {
            flashSaleStock = new ArrayList<>();
            listVariationModelId.forEach(barcode -> flashSaleStock.add(0));
        }

        // update flash sale status map
        String status = flashSaleDetailJson.getString("status");
        List<String> hasFlashSaleBarcodeList = flashSaleDetailJson.getList("items.itemModelId");
        brInfo.getBranchName().forEach(brName -> {
            List<String> statusList = new ArrayList<>(flashSaleStatus.get(brName));
            hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEachOrdered(promotionBarcode -> statusList.set(listVariationModelId.indexOf(promotionBarcode), status));
            flashSaleStatus.put(brName, statusList);
        });

        // update flash sale price
        List<Long> flashSalePrice = Pattern.compile("newPrice.{3}(\\d+)").matcher(flashSaleDetail.asPrettyString()).results().map(matchResult -> Long.valueOf(matchResult.group(1))).toList();
        hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEachOrdered(s -> this.flashSalePrice.set(listVariationModelId.indexOf(s), flashSalePrice.get(hasFlashSaleBarcodeList.indexOf(s))));

        // update flash sale stock
        hasFlashSaleBarcodeList.stream().filter(listVariationModelId::contains).forEach(promotionBarcode -> flashSaleStock.set(listVariationModelId.indexOf(promotionBarcode), IntStream.range(0, flashSaleDetailJson.getList("items.saleStock").size()).mapToObj(itemID -> (int) flashSaleDetailJson.getFloat("items[%s].saleStock".formatted(itemID))).toList().get(hasFlashSaleBarcodeList.indexOf(promotionBarcode))));
    }

    public FlashSaleInfo getFlashSaleInfo(List<String> listVariationModelId, List<Long> sellingPrice) {
        // init flash sale information model
        FlashSaleInfo info = new FlashSaleInfo();

        // init flash sale list
        List<Integer> flashSaleList = new ArrayList<>();

        // get in-progress list
        List<Integer> inProgressList = new API().get("%s%s?status=IN_PROGRESS".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        if (inProgressList != null) flashSaleList.addAll(inProgressList);

        // get schedule list
        List<Integer> scheduleList = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken()).jsonPath().getList("id");
        if (scheduleList != null) flashSaleList.addAll(scheduleList);

        // init flash sale status map
        if (flashSaleStatus == null || flashSaleStatus.keySet().isEmpty() || flashSaleList.isEmpty()) {
            flashSaleStatus = new HashMap<>();
            brInfo.getBranchName().forEach(brName -> flashSaleStatus.put(brName, listVariationModelId.stream().map(barcode -> "EXPIRED").toList()));
        }

        // init flash sale price list
        if (flashSalePrice == null || flashSalePrice.isEmpty()) {
            flashSalePrice = new ArrayList<>();
            listVariationModelId.forEach(barcode -> flashSalePrice.add(sellingPrice.get(listVariationModelId.indexOf(barcode))));
        }

        // get last flash sale info
        flashSaleList.forEach(flsID -> getFlashSaleInformation(flsID, listVariationModelId));

        // set last flash sale price
        info.setFlashSalePrice(flashSalePrice);

        // set last flash sale status
        info.setFlashSaleStatus(flashSaleStatus);

        // set last flash sale stock
        info.setFlashSaleStock(flashSaleStock);

        return info;
    }
    public int getAFlashSaleScheduled(){
        Response response = new API().get("%s%s?status=SCHEDULED".formatted(FLASH_SALE_LIST_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken());
        List<Integer> ids = response.jsonPath().getList("id");
        if(ids.isEmpty()) return 0;
        return ids.get(0);
    }

}
