package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class APITiktokItemSyncInformation {
    private static final Logger logger = LogManager.getLogger(APITiktokItemSyncInformation.class);
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the APITiktokItemSyncInformation instance with the seller's login information.
     *
     * @param credentials The seller's login credentials.
     */
    public APITiktokItemSyncInformation(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    @Data
    public static class TikTokItemRequest {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> fields;
        private List<Integer> tiktokItemIds;
        private boolean createToGoSell;
    }

    private TikTokItemRequest createPayload(boolean isCreate, List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        TikTokItemRequest tikTokItemRequest = new TikTokItemRequest();
        tikTokItemRequest.setCreateToGoSell(isCreate);

        if (!isCreate) {
            tikTokItemRequest.setFields(List.of("FIELD_NAME", "FIELD_PRICE", "FIELD_DESCRIPTION", "FIELD_STOCK", "FIELD_IMAGE"));
            tikTokItemRequest.setTiktokItemIds(getLinkedProductIds(tikTokProducts));
        } else {
            tikTokItemRequest.setTiktokItemIds(getUnlinkedProductIds(tikTokProducts));
        }

        return tikTokItemRequest;
    }

    private List<Integer> getLinkedProductIds(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        // Get linked products and error products
        var linkedProducts = APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts);
        var errorProducts = APIGetTikTokProducts.getErrorTiktokProduct(tikTokProducts);

        // Combine the linked and error products into a single list
        var allRelevantProducts = new ArrayList<>(linkedProducts);
        allRelevantProducts.addAll(errorProducts);

        return allRelevantProducts.stream().map(APIGetTikTokProducts.TikTokProduct::getId).toList();
    }

    private List<Integer> getUnlinkedProductIds(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        return APIGetTikTokProducts.getUnLinkedTiktokProduct(tikTokProducts)
                .stream()
                .map(APIGetTikTokProducts.TikTokProduct::getId)
                .collect(Collectors.toList());
    }

    private boolean isSynchronizing(boolean isCreate, List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        String path = "/tiktokservices/api/items/sync_to_gosell/%d".formatted(loginInfo.getStoreID());
        return new API().post(path, loginInfo.getAccessToken(), createPayload(isCreate, tikTokProducts)).getStatusCode() == 200;
    }

    /**
     * Waits for the product synchronization to complete, polling for the sync status up to a maximum number of retries.
     *
     * @throws RuntimeException if the synchronization is still in progress after maximum retries.
     */
    public void waitSynced(boolean isCreate, List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        final int maxRetries = 12;

        if (!isSynchronizing(isCreate, tikTokProducts)) {
            logger.info("No synchronization in progress, exiting wait.");
            return;
        }

        IntStream.range(0, maxRetries).takeWhile(ignored -> isSynchronizing(isCreate, tikTokProducts))
                .forEach(ignored -> {
                    try {
                        logger.info("Synchronization in progress, sleeping for 10 seconds.");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                        logger.error("Waiting for synchronization interrupted.", e);
                    }
                });

        logger.info("Synchronization completed successfully.");
    }
}
