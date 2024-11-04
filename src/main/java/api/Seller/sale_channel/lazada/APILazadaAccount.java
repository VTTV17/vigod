package api.Seller.sale_channel.lazada;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APILazadaAccount {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String GET_LAZADA_ACCOUNT_MANAGEMENT_PATH = "/lazadaservices/api/bc-stores/%s/v2";
    String DOWNLOAD_ALL_PRODUCT_PATH = "/lazadaservices/api/products/store/%s/lazadashop/%s/download-products";
    String FETCH_STATUS_PATH = "/lazadaservices/api/products/store/%s/lazadashop/%s/fetch_status";
    Logger logger = LogManager.getLogger(APILazadaAccount.class);

    public APILazadaAccount(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @SneakyThrows
    public String getBranchIDAndLazadaShopConnected() {
        Response response = api.get(GET_LAZADA_ACCOUNT_MANAGEMENT_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        response.then().statusCode(200);
        String connectStatus = response.jsonPath().getString("[0].connectStatus");
        if (!connectStatus.equals("CONNECTED")) {
            throw new Exception("Lazada is not connected. ");
        }
        String branchId = String.valueOf(response.jsonPath().getInt("[0].branchId"));
        String sellerId = String.valueOf(response.jsonPath().getLong("[0].sellerId"));
        return branchId + "-" + sellerId;
    }

    public void callDownloadProductAPI(String lazadaShopId) {
        Response response = api.post(DOWNLOAD_ALL_PRODUCT_PATH.formatted(loginInfo.getStoreID(), lazadaShopId), loginInfo.getAccessToken());
        response.then().statusCode(200);
    }

    public Response callFetchStatus(String lazadaShopId) {
        Response response = api.get(FETCH_STATUS_PATH.formatted(loginInfo.getStoreID(), lazadaShopId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }

    /**
     * Download then wait for download done.
     */
    @SneakyThrows
    public void downloadLazadaProductByAPI() {
        String lazadaShopId = getBranchIDAndLazadaShopConnected().split("-")[1];
        callDownloadProductAPI(lazadaShopId);
        waitFetchProductAPI(20, lazadaShopId);
    }

    @SneakyThrows
    public void waitFetchProductAPI(int maxLoop, String lazadaShopId) {
        while (maxLoop != 0) {
            Response fetchRes = callFetchStatus(lazadaShopId);
            boolean isProcessing;
            try {
                isProcessing = fetchRes.jsonPath().getBoolean("isDownloading");
            } catch (Exception e) {
                isProcessing = fetchRes.jsonPath().getBoolean("isSyncing");
                logger.info("Syncing product");
            }
            if (!isProcessing) return;
            Thread.sleep(2000);
            maxLoop--;
        }
        throw new Exception("Download/Sync product not successful.");
    }
}
