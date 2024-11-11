package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.stream.IntStream;

public class APITiktokItemDownloadInformation {
    private static final Logger logger = LogManager.getLogger(APITiktokItemDownloadInformation.class);
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the APITiktokItemDownloadInformation instance with the seller's login information.
     *
     * @param credentials The seller's login credentials.
     */
    public APITiktokItemDownloadInformation(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Checks if a download is currently in progress for the specified store.
     *
     * @return true if the download is in progress, false otherwise.
     */
    private boolean isDownloading() {
        String path = String.format("/tiktokservices/api/item-download-informations/product/%d/is-downloading", loginInfo.getStoreID());
        return new API().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().as(Boolean.class);
    }

    /**
     * Waits for the product download to complete, polling for the download status up to a maximum number of retries.
     *
     * @throws RuntimeException if the download is still in progress after maximum retries.
     */
    public void waitForDownloadSuccess() {
        final int maxRetries = 5;
        if (!isDownloading()) {
            logger.info("No download in progress, exiting wait.");
            return;
        }

        IntStream.range(0, maxRetries).takeWhile(ignored -> isDownloading()).forEach(ignored -> {
            try {
                logger.info("Download in progress, sleeping for 5 seconds.");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                logger.error("Waiting for download interrupted.", e);
            }
        });

        if (isDownloading()) {
            logger.error("Download still in progress after maximum retries.");
            throw new RuntimeException("Download did not complete within the expected time frame.");
        }

        logger.info("Download completed successfully.");
    }
}
