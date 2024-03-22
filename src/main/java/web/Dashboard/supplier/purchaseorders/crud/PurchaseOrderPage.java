package web.Dashboard.supplier.purchaseorders.crud;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.all_products.APISuggestionProduct.AllSuggestionProductsInfo;
import api.Seller.products.all_products.ProductInformation;
import api.Seller.supplier.purchase_orders.APIPurchaseOrders;
import api.Seller.supplier.purchase_orders.APIPurchaseOrders.PurchaseOrderInformation;
import api.Seller.supplier.supplier.APISupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static utilities.links.Links.DOMAIN;

public class PurchaseOrderPage extends PurchaseOrderElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(PurchaseOrderPage.class);

    public PurchaseOrderPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    /*---------------------------------*/
    void navigateToCreatePurchaseOrderPageByURL() {
        // navigate to create purchase order page by URL
        driver.get("%s/product/purchase-order/create".formatted(DOMAIN));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to create purchase order page by URL");
    }

    void navigateToEditPurchaseOrderPageByURL(int purchaseId) {
        // navigate to edit purchase order page by url
        driver.get("%s/product/purchase-order/edit/%s".formatted(DOMAIN, purchaseId));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to edit purchase order page by url, purchaseId: %s.".formatted(purchaseId));
    }

    void navigateToPurchaseOrderDetailPageByURL(int purchaseId) {
        // navigate to purchase order detail page by url
        driver.get("%s/product/purchase-order/wizard/%s".formatted(DOMAIN, purchaseId));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to purchase order detail page by url, purchaseId: %s.".formatted(purchaseId));
    }

    void selectSupplier(String supplierName) throws Exception {
        if (!supplierName.isEmpty()) {
            // remove old supplier
            if (!commonAction.getListElement(loc_icnRemoveSupplier).isEmpty()) {
                commonAction.clickJS(loc_icnRemoveSupplier);
                logger.info("Remove old supplier.");
            }

            // search supplier by name
            commonAction.sendKeys(loc_txtSupplierSearchBox, supplierName);

            // select supplier
            commonAction.click(By.xpath(str_ddvSupplier.formatted(supplierName)));

            // log
            logger.info("Select supplier: %s.".formatted(supplierName));
        } else throw new Exception("Can not find any supplier.");
    }

    long importPrice;

    void selectProduct(AllSuggestionProductsInfo productsInfo) throws Exception {
        if (!productsInfo.getItemIds().isEmpty()) {
            // remove old products
            if (!commonAction.getListElement(loc_icnRemoveProduct).isEmpty()) {
                int bound = commonAction.getListElement(loc_icnRemoveProduct).size();
                IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1)
                        .forEach(index -> commonAction.clickJS(loc_icnRemoveProduct, index));
                logger.info("Remove old products.");
            }
            // search product by name
            commonAction.sendKeys(loc_txtProductSearchBox, productsInfo.getItemNames().get(0));

            // select product by barcode
            commonAction.click(By.xpath(str_ddvProduct.formatted(productsInfo.getBarcodes().get(0).replace("-", " - "))));
            logger.info("Select product with name: %s and barcode: %s.".formatted(productsInfo.getItemNames().get(0),
                    productsInfo.getBarcodes().get(0)));

            // input price
            importPrice = (productsInfo.getPrice().get(0) != 0L)
                    ? nextLong(productsInfo.getPrice().get(0))
                    : 0;
            commonAction.sendKeys(loc_txtImportPrice, 0, String.valueOf(importPrice));
            logger.info("Input import price: %s.".formatted(importPrice));

            // select imei
            if (productsInfo.getInventoryManageTypes().get(0).equals("IMEI_SERIAL_NUMBER")) {
                // open add IMEI popup
                commonAction.openPopupJS(loc_lnkAddIMEI, loc_dlgAddIMEI);
                logger.info("Open add IMEI popup.");

                // get variation value
                String variationValue = productsInfo.getModelNames().get(0);

                // init imei value
                String imei = "%s%s_IMEI_%s_%s\n".formatted((variationValue != null)
                                ? "%s_".formatted(variationValue)
                                : "",
                        staffLoginInfo.getAssignedBranchesNames().get(0),
                        Instant.now().toEpochMilli(),
                        0);
                // input imei
                commonAction.sendKeys(loc_dlgAddIMEI_txtInputIMEI, imei);

                // log
                logger.info("Input IMEI: %s.".formatted(imei.replace("\n", "")));

                // save imei
                commonAction.closePopup(loc_dlgAddIMEI_btnSave);
                logger.info("Close add IMEI popup.");
            }
        } else throw new Exception("Can not find any products.");
    }

    void inputPaymentAmount() {
        // open choose payment method popup
        commonAction.openPopupJS(loc_btnChangePaymentMethod, loc_dlgChoosePaymentMethod);
        logger.info("Open choose payment method popup.");

        // select cash
        if (!commonAction.isCheckedJS(loc_dlgChoosePaymentMethod_chkPaymentMethod, 0))
            commonAction.clickJS(loc_dlgChoosePaymentMethod_chkPaymentMethod, 0);
        logger.info("Select cash payment.");

        // close choose payment method popup
        commonAction.closePopup(loc_dlgChoosePaymentMethod_btnConfirm);
        logger.info("Close choose payment method popup.");

        // select cash payment
        if (!commonAction.isCheckedJS(loc_chkPaymentMethod, 0))
            commonAction.clickJS(loc_chkPaymentMethod, 0);
        logger.info("Select payment method: Cash.");

        // input cash amount
        commonAction.sendKeys(loc_txtPaymentAmount, 0, String.valueOf(importPrice));
        logger.info("Input cash amount: %s.".formatted(importPrice));
    }

    void createPurchaseOrder() {
        // change create option to create purchase order.
        commonAction.clickJS(loc_icnCreateOptions);
        commonAction.click(loc_btnCreateOptions, 0);

        // create purchase order.
        commonAction.click(loc_btnCreateOrder);

        // log
        logger.info("Create purchase order.");
    }

    void createAndApprovedPurchaseOrder() {
        // change create option to create and approved purchase order.
        commonAction.clickJS(loc_icnCreateOptions);
        commonAction.click(loc_btnCreateOptions, 1);

        // create purchase order.
        commonAction.click(loc_btnCreateAndApprove);

        // log
        logger.info("Create and approve purchase order.");
    }

    void inputPurchaseOrderInfo() throws Exception {
        // get supplier info
        List<String> allSupplierName = new APISupplier(staffLoginInformation).getAllSupplierNames();
        String supplierName = allSupplierName.isEmpty() ? "" : allSupplierName.get(0);

        // select supplier
        selectSupplier(supplierName);

        // get product info
        AllSuggestionProductsInfo productsInfo = new APISuggestionProduct(staffLoginInformation)
                .getAllSuggestProductIdNoManagedByLot(staffLoginInfo.getAssignedBranchesIds().get(0), false);

        selectProduct(productsInfo);
        inputPaymentAmount();
    }

    /*---------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-13850
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    APIPurchaseOrders purchaseOrders;

    public PurchaseOrderPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    public void checkPurchaseOrderPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init purchase order api
        purchaseOrders = new APIPurchaseOrders(sellerLoginInformation);

        // get purchaseId for view detail/ print purchase order
        List<Integer> listViewId = purchaseOrders.getListPurchaseOrderMatchWithCondition(staffLoginInfo.getAssignedBranchesNames());
        int viewDetailPurchaseId = listViewId.isEmpty() ? 0 : listViewId.get(0);

        // get purchase order that have status "ORDER" for check approve permission
        int orderPurchaseId = purchaseOrders.getOrderPurchaseId(staffLoginInfo.getAssignedBranchesNames());

        // get purchase order that have status "IN_PROGRESS" for check import goods permission
        int inProgressPurchaseId = purchaseOrders.getInProgressPurchaseId(staffLoginInfo.getAssignedBranchesNames());


        // check create purchase order
        try {
            checkCreatePurchaseOrder();
            checkCreateAndApprovePurchaseOrder();
        } catch (Exception ex) {
            logger.error(ex);
        }

        // check view purchase order detail
        checkViewPurchaseOrderDetail(viewDetailPurchaseId, orderPurchaseId, inProgressPurchaseId);
    }

    void checkViewPurchaseOrderDetail(int viewDetailPurchaseId, int orderPurchaseId, int inProgressPurchaseId) {
        if (viewDetailPurchaseId != 0) {
            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isViewPurchaseOrderDetail()) {
                // check can access to purchase order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/product/purchase-order/wizard/%s".formatted(DOMAIN, viewDetailPurchaseId),
                                String.valueOf(viewDetailPurchaseId)),
                        "Can not access to purchase detail page by URL, purchaseId: %s.".formatted(viewDetailPurchaseId));

                // check edit purchase order
                checkEditPurchaseOrder(Math.max(orderPurchaseId, inProgressPurchaseId));

                // check approve purchase order
                checkApprovePurchaseOrder(orderPurchaseId);

                // check complete purchase order
                checkCompletePurchaseOrder(inProgressPurchaseId);

                // check view purchase order history
                checkViewPurchaseOrderHistory(viewDetailPurchaseId);

                // check cancel purchase order
                checkCancelPurchaseOrder(orderPurchaseId);

                // check print purchase order
                checkPrintPurchaseOrderReceipt(viewDetailPurchaseId);
            } else {
                // show restricted page
                // if staff don’t have permission “View purchase order detail”
                // when click to view detail page of Purchase order
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/product/purchase-order/wizard/%s".formatted(DOMAIN, viewDetailPurchaseId)),
                        "Restricted page is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> View purchase order detail.");
    }

    void checkCreatePurchaseOrder() throws Exception {
        // navigate to create purchase order page by url
        navigateToCreatePurchaseOrderPageByURL();

        // input purchase order info
        inputPurchaseOrderInfo();

        // create order
        createPurchaseOrder();

        // check permission
        if (permissions.getSuppliers().getPurchaseOrder().isCreatePurchaseOrder()) {
            // check purchase order is created
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not create new purchase order.");
        } else {
            // if staff don’t have permission “Create purchase order”
            // => show restricted popup
            // when click on [Create order] button in when create purchase order
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Create purchase order.");
    }

    void checkCreateAndApprovePurchaseOrder() throws Exception {
        // navigate to create purchase order page by url
        navigateToCreatePurchaseOrderPageByURL();

        // input purchase order info
        inputPurchaseOrderInfo();

        // create order
        createAndApprovedPurchaseOrder();

        // check permission
        if (permissions.getSuppliers().getPurchaseOrder().isCreatePurchaseOrder()
                && permissions.getSuppliers().getPurchaseOrder().isApprovePurchaseOrder()) {
            // check purchase order is created
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(), "Can not create and approve new purchase order.");
        } else {
            // if staff don’t have permission “Create purchase order”
            // => show restricted popup
            // when click on [Create and Approve] button in when create purchase order
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Create purchase order.");
    }

    void checkEditPurchaseOrder(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to edit purchase order page
            navigateToEditPurchaseOrderPageByURL(purchaseId);

            // edit import price to enable Save button
            String importPrice = commonAction.getValue(loc_txtImportPrice, 0);
            commonAction.sendKeys(loc_txtImportPrice, 0, importPrice + Keys.TAB);

            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isEditPurchaseOrder()) {
                // check can save changes
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave,
                                loc_dlgToastSuccess),
                        "Can not edit purchase order.");
            } else {
                // if staff don’t have permission “Edit purchase order”
                // => show restricted popup
                // when click [Save] button in purchase order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Edit purchase order.");
    }

    void checkApprovePurchaseOrder(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to detail purchase order page
            navigateToPurchaseOrderDetailPageByURL(purchaseId);

            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isApprovePurchaseOrder()) {
                // check can approve purchase order
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave,
                                loc_dlgToastSuccess),
                        "Can not approve purchase order.");
            } else {
                // if staff don’t have permission “Approve purchase order”
                //  => show restricted popup
                // when click [Approved] button in purchase order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnApprove),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Approve purchase order.");
    }

    void checkCompletePurchaseOrder(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to detail purchase order page
            navigateToPurchaseOrderDetailPageByURL(purchaseId);

            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isCompletePurchaseOrder()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnImportGoods,
                                loc_dlgConfirmImportGoods),
                        "Can not open confirm import goods popup.");
                if (!commonAction.getListElement(loc_dlgConfirmImportGoods).isEmpty()) {
                    // check cost price
                    checkViewProductCostPrice(purchaseId);

                    // check can import goods
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmImportGoods_btnUpdate,
                                    loc_dlgToastSuccess),
                            "Can not import goods.");
                }
            } else {
                // if staff don’t have permission “Complete purchase order”
                // => show restricted popup
                // when click [Import goods] button in purchase order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnImportGoods),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Complete purchase order.");
    }

    void checkViewProductCostPrice(int purchaseId) {
        // get purchase info
        PurchaseOrderInformation purchaseInfo = new APIPurchaseOrders(sellerLoginInformation).getPurchaseOrderInformation(purchaseId);
        long importQuantity = purchaseInfo.getPurchaseOrderItems_quantity().get(0);
        long importPrice = purchaseInfo.getPurchaseOrderItems_importPrice().get(0);
        int itemId = purchaseInfo.getPurchaseOderItems_itemId().get(0);
        Integer modelId = purchaseInfo.getPurchaseOderItems_modelId().get(0);

        // get first product information
        ProductInfo productInfo = new ProductInformation(sellerLoginInformation).getInfo(itemId);
        String model = "%s%s".formatted(itemId, modelId == null ? "" : "-%s".formatted(modelId));
        long remainingStock = productInfo.getProductStockQuantityMap().get(model).stream().mapToInt(stock -> stock).asLongStream().sum();
        long costPrice = productInfo.getProductCostPrice().get(productInfo.getVariationModelList().indexOf(model));

        // New cost price = [Remaining stock before completed PO * cost price before completed PO + PO quantity * imported quantity] / [Total quantity]
        long newCostPrice = (importQuantity * importPrice + remainingStock * costPrice) / (importQuantity + remainingStock);

        // get new cost
        long newCost = Long.parseLong(commonAction.getValue(loc_dlgConfirmImportGoods_textAfterCost, 0)
                .replaceAll(",", ""));

        // check new cost
        assertCustomize.assertTrue(Math.abs(newCostPrice - newCost) <= 1,
                "After cost price must be %s, but found %s.".formatted(newCostPrice, newCost));

        // get before cost
        String beforeCost = commonAction.getValue(loc_dlgConfirmImportGoods_textBeforeCost, 0)
                .replaceAll(",", "");

        // check permission
        if (permissions.getProduct().getProductManagement().isViewProductCostPrice()) {
            // check cost price
            assertCustomize.assertTrue(beforeCost.equals(String.valueOf(costPrice)),
                    "Before price must be %s, but found %s.".formatted(costPrice, beforeCost));
        } else {
            //  if staff don’t have this permission “View cost price”
            // when import product at Purchase Order => not auto show before cost price
            assertCustomize.assertTrue(beforeCost.equals("0"),
                    "Before cost must be hidden, but found %s.".formatted(beforeCost));
        }

        // log
        logger.info("Check permission: Product >> Product management >> View product cost price.");
    }

    void checkViewPurchaseOrderHistory(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to detail purchase order page
            navigateToPurchaseOrderDetailPageByURL(purchaseId);
            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isViewPurchaseOrderHistory()) {
                // check can view purchase order history
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnPurchaseOrderHistory,
                                loc_dlgPurchaseOrderHistory),
                        "Can not open purchase order history popup.");
            } else {
                // if staff don’t have permission “View purchaser order history”
                // => show restricted popup
                // when click [Purchase order history] button in purchase order detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPurchaseOrderHistory),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> View purchase order history.");
    }

    void checkPrintPurchaseOrderReceipt(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to detail purchase order page
            navigateToPurchaseOrderDetailPageByURL(purchaseId);

            // print purchase order
            commonAction.clickJS(loc_ddlActions, commonAction.getListElement(loc_ddlActions).size() - 1);

            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isPrintPurchaseOrderReceipt()) {
                if (commonAction.getAllWindowHandles().size() > 1) {
                    // switch to print tab
                    commonAction.switchToWindow(1);
                }
                assertCustomize.assertTrue(driver.getCurrentUrl().contains("blob:"),
                        "Can not print purchase order.");

                // back to purchase order tab
                commonAction.switchToWindow(0);
            } else {
                // if staff don’t have permission “Print purchaser order receipt”
                // => show restricted popup
                // when click action [Print] in purchase order detail page
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Print purchase order receipt.");
    }

    void checkCancelPurchaseOrder(int purchaseId) {
        if (purchaseId != 0) {
            // navigate to detail purchase order page
            navigateToPurchaseOrderDetailPageByURL(purchaseId);

            // cancel purchase order
            commonAction.clickJS(loc_ddlActions, 1);

            // check permission
            if (permissions.getSuppliers().getPurchaseOrder().isCancelPurchaseOrder()) {
                // check can cancel purchase order
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirmCancel).isEmpty(),
                        "Can not open confirm cancel purchase order popup.");

                if (!commonAction.getListElement(loc_dlgConfirmCancel).isEmpty()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmCancel_btnOK,
                                    loc_dlgToastSuccess),
                            "Can not cancel purchase order.");
                }
            } else {
                // if staff don’t have permission “Cancel purchaser order”
                // => show restricted popup
                // when click action [Cancel] in purchase order detail page
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Supplier >> Purchase order >> Cancel purchase order.");
    }
}
