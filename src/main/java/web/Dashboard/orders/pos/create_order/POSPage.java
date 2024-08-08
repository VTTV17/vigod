package web.Dashboard.orders.pos.create_order;

import api.Seller.products.all_products.APIProductDetailV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class POSPage extends POSElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger();
    public POSPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void selectBranch(String branchName) {
        // Open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // Select branch
        commonAction.clickJS(loc_lstBranches(branchName));

        // Log
        logger.info("Select branch: {}", branchName);
    }

    void selectProduct(LoginInformation loginInformation, List<Integer> productIds) {
        // Select product
        productIds.stream().forEach(productId -> {
            // Get product information
            APIProductDetailV2.ProductInfoV2 infoV2 = new APIProductDetailV2(loginInformation).getInfo(productId);

            // Search product
            commonAction.sendKeys(loc_txtProductSearchBox, infoV2.getName());

            // Log
            logger.info("Search product, keyword: {}", infoV2.getName());

            // Select product/variations
            infoV2.getBarcodeList().forEach(barcode -> {
                // Add product/variation to cart
                commonAction.clickJS(loc_lstProductResult(barcode));

                // Log
                logger.info("Add product/variation to cart, barcode: {}", barcode);
            });
        });
    }

    public void createPOSOrder(LoginInformation loginInformation, List<Integer> productIds) {
        selectProduct(loginInformation, productIds);
    }
}
