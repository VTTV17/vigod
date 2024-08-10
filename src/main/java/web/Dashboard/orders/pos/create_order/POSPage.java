package web.Dashboard.orders.pos.create_order;

import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIProductDetailV2;
import api.Seller.products.lot_date.APICreateLotDate;
import api.Seller.products.lot_date.APIEditLotDate;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

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

    private List<Integer> createProductForPOSCart(LoginInformation loginInformation, int stockQuantity) {
        // Create lot
        int lotId = new APICreateLotDate(loginInformation).createLotDateAndGetLotId();

        // Init cart items
        List<Integer> items = new ArrayList<>();

        // Init stock
        int[] stock = new int[new BranchManagement(loginInformation).getInfo().getBranchID().size()];
        Arrays.fill(stock, stockQuantity);

        // Init API create product
        APICreateProduct apiCreateProduct = new APICreateProduct(loginInformation);

        // Create lot product
        int withoutVariationProductId = apiCreateProduct.setLotAvailable(true).createWithoutVariationProduct(false, stock).getProductID();
        int withVariationProductId = apiCreateProduct.setLotAvailable(true).createVariationProduct(false, 0, stock).getProductID();


        // Create without variation product
        items.add(apiCreateProduct.createWithoutVariationProduct(false, stock).getProductID());
        items.add(apiCreateProduct.createWithoutVariationProduct(true, stock).getProductID());

        // Create with variation product
        items.add(apiCreateProduct.createVariationProduct(false, 0, stock).getProductID());
        items.add(apiCreateProduct.createVariationProduct(true, 0, stock).getProductID());

        // Add product to lot and update stock
        new APIEditLotDate(loginInformation).addProductIntoLot(lotId, withoutVariationProductId, 5);
        new APIEditLotDate(loginInformation).addProductIntoLot(lotId, withVariationProductId, 5);
        items.add(withoutVariationProductId);
        items.add(withVariationProductId);

        return items;
    }

    void selectBranch(String branchName) {
        // Open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // Select branch
        commonAction.clickJS(loc_lstBranches(branchName));

        // Log
        logger.info("Select branch: {}", branchName);
    }

    void selectProduct(LoginInformation loginInformation, List<Integer> productIds, int quantity) {
        // Select product
        productIds.forEach(productId -> {
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

                // Wait API response
                commonAction.sleepInMiliSecond(500,"Wait product/variation is added to cart");

                // Log
                logger.info("Add product/variation to cart, barcode: {}", barcode);

                // Get product name
                String productName = infoV2.getName();

                // Get variation value
                String variationValue = infoV2.isHasModel()
                        ? infoV2.getVariationValuesMap()
                            .get(new StoreInformation(loginInformation).getInfo().getDefaultLanguage())
                            .get(infoV2.getBarcodeList().indexOf(barcode)).replace("|", " | ")
                        : "";

                // Input quantity
                commonAction.sendKeys(infoV2.isHasModel()
                                ? loc_txtProductQuantity(productName, variationValue)
                                : loc_txtProductQuantity(productName),
                        String.valueOf(quantity));

                // Select IMEI if product is managed by IMEI
                if (!infoV2.getInventoryManageType().equals("PRODUCT")) {
                    // Open Select IMEI popup
                    commonAction.click(infoV2.isHasModel()
                            ? loc_btnSelectIMEI(productName, variationValue)
                            : loc_btnSelectIMEI(productName));

                    // Log
                    logger.info("Open select IMEI popup");

                    // Select IMEI
                    IntStream.range(0, quantity)
                            .mapToObj(imeiIndex -> commonAction.getText(loc_dlgSelectIMEI_lstIMEI)) // Get IMEI value
                            .forEach(imeiValue -> {
                                // Select IMEI
                                commonAction.click(loc_dlgSelectIMEI_lstIMEI);
                                // Log
                                logger.info("Select IMEI: {}", imeiValue);
                            });

                    // Save changes
                    commonAction.click(loc_dlgSelectIMEI_btnSave);

                    // Log
                    logger.info("Close Select IMEI popup");
                }

                // Select Lot if product quantity is managed by Lot
                if (infoV2.isLotAvailable()) {
                    // Open Select Lot popup
                    commonAction.click(infoV2.isHasModel()
                            ? loc_btnSelectLot(productName, variationValue)
                            : loc_btnSelectLot(productName));

                    // Log
                    logger.info("Open Select Lot popup");

                    // Add lot quantity
                    commonAction.sendKeys(loc_dlgSelectLot_txtQuantity, String.valueOf(quantity));

                    // Log
                    logger.info("Select lot quantity: {}", quantity);

                    // Save changes
                    commonAction.click(loc_dlgSelectLot_btnSave);

                    // Log
                    logger.info("Close Select Lot popup");
                }
            });
        });
    }

    public void createPOSOrder(LoginInformation loginInformation, int stockQuantity) {
        // Create products for test
        List<Integer> productIds = createProductForPOSCart(loginInformation, stockQuantity);

        // Add product to cart
        selectProduct(loginInformation, productIds, nextInt(stockQuantity) + 1);
    }
}
