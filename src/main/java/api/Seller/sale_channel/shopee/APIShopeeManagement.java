package api.Seller.sale_channel.shopee;

import api.Seller.login.Login;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class APIShopeeManagement {
    Logger logger = LogManager.getLogger(APIShopeeManagement.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    @Getter
    private final static Cache<String, ShopeeManagementInformation> shopeeAccountCache = CacheBuilder.newBuilder().build();


    public APIShopeeManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    enum ShopeeStatus {
        CONNECTED, DISCONNECTED
    }

    enum ShopeeType {
        PAID, FREE
    }

    @Data
    public static class ShopeeManagementInformation {
        List<Integer> ids = new ArrayList<>();
        List<Integer> shopIds = new ArrayList<>();
        List<String> shopNames = new ArrayList<>();
        List<ShopeeStatus> statuses = new ArrayList<>();
        List<String> branchNames = new ArrayList<>();
        List<Integer> branchIds = new ArrayList<>();
        List<ShopeeType> shopTypes = new ArrayList<>();
    }

    String getShopeeManagementPath = "/shopeeservices/api/shops/%s/management";

    Response getShopeeManagementInformationResponse() {
        return api.get(getShopeeManagementPath.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    }

    public ShopeeManagementInformation getShopeeManagementInfo() {
        ShopeeManagementInformation info = shopeeAccountCache.getIfPresent(loginInfo.getStaffPermissionToken());
        if (Optional.ofNullable(info).isEmpty()) {
            if (!loginInfo.getStaffPermissionToken().isEmpty()) {
                ShopeeManagementInformation tempInfo = shopeeAccountCache.getIfPresent("");
                shopeeAccountCache.invalidateAll();
                if (Optional.ofNullable(tempInfo).isPresent()) {
                    shopeeAccountCache.put("", tempInfo);
                }
            }
            // init model
            info = new ShopeeManagementInformation();
            // get Shopee management response
            Response response = getShopeeManagementInformationResponse();

            if (response.getStatusCode() != 403) {
                JsonPath jsonPath = response.then()
                        .statusCode(200)
                        .extract()
                        .jsonPath();

                info.setIds(jsonPath.getList("id"));
                info.setShopIds(jsonPath.getList("shopId"));
                info.setShopNames(jsonPath.getList("shopName"));
                info.setStatuses(jsonPath.getList("connectStatus").stream().map(status -> ShopeeStatus.valueOf((String) status)).toList());
                info.setBranchNames(jsonPath.getList("branchName"));
                info.setBranchIds(jsonPath.getList("branchId"));
                info.setShopTypes(jsonPath.getList("shopType").stream().map(type -> ShopeeType.valueOf((String) type)).toList());
            }

            // save cache
            shopeeAccountCache.put(loginInfo.getStaffPermissionToken(), info);
        }
            return info;
        }

        public boolean isConnectedShopee () {
            return !getShopeeManagementInfo().getIds().isEmpty();
        }
    }
