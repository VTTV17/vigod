package web.Dashboard.orders.return_orders.edit_return_order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.util.stream.IntStream;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.links.Links.DOMAIN;

public class EditReturnOrderPage extends EditReturnOrderElement {
    WebDriver driver;
    UICommonAction commonAction;
    AssertCustomize assertCustomize;
    Logger logger = LogManager.getLogger(EditReturnOrderPage.class);

    public EditReturnOrderPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void navigateToEditReturnOrderPage(int returnOrderId) {
        driver.get("%s/order/return-order/edit/%s".formatted(DOMAIN, returnOrderId));
        driver.navigate().refresh();

        logger.info("Navigate to edit return order page by URL, returnOrderId: %s.".formatted(returnOrderId));
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/issues/BH-24812
    AllPermissions permissions;
    CheckPermission checkPermission;
    Boolean isRestockGoods;

    public EditReturnOrderPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
        isRestockGoods = permissions.getOrders().getReturnOrder().isRestockGoods();
    }

    void inputReturnStock() {
        int bound = commonAction.getListElement(loc_txtQuantity).size();

        for (int index = 0; index < bound; index++) {
            int remainingQuantity = Integer.parseInt(commonAction.getText(loc_lblRemainingQuantity, index).replaceAll("\\D+", ""));
            if (remainingQuantity > 0) {
                // input quantity
                int quantity = Math.max(nextInt(remainingQuantity), 1);
                commonAction.sendKeys(loc_txtQuantity, index, String.valueOf(quantity));
                logger.info("Input quantity: %s.".formatted(quantity));

                // select IMEI if any
                if (!commonAction.getListElement(loc_imgSelectIMEI).isEmpty()) {
                    commonAction.clickJS(loc_imgSelectIMEI);

                    if (!commonAction.getListElement(loc_dlgSelectIMEI).isEmpty()) {
                        // remove IMEI
                        if (!commonAction.getListElement(loc_dlgSelectIMEI_icnRemoveSelectedIMEI).isEmpty()) {
                            int removeBound = commonAction.getListElement(loc_dlgSelectIMEI_icnRemoveSelectedIMEI).size();
                            IntStream.iterate(removeBound - 1, removeIndex -> removeIndex >= 0, removeIndex -> removeIndex - 1)
                                    .forEachOrdered(removeIndex -> commonAction.clickJS(loc_dlgSelectIMEI_icnRemoveSelectedIMEI, removeIndex));
                        }


                        // select IMEI
                        int maxQuantityCanSelect = Integer.parseInt(commonAction.getText(loc_dlgSelectIMEI_lblSelectedMax).replaceAll("\\D+", ""));
                        IntStream.range(0, maxQuantityCanSelect).forEachOrdered(selectIndex -> commonAction.clickJS(loc_dlgSelectIMEI_lstIMEI, maxQuantityCanSelect - selectIndex - 1));

                        // save changes
                        commonAction.click(loc_dlgSelectIMEI_btnSave);
                    } else logger.error("Can not open Select IMEI/Serial number popup.");
                }
                break;
            }
        }
    }

    void checkRestockGoods() {
        if (isRestockGoods != null) {
            if (isRestockGoods) {
                if (!commonAction.isCheckedJS(loc_chkReceivedGoods)) {
                    commonAction.clickJS(loc_chkReceivedGoods);
                }
                assertCustomize.assertTrue(commonAction.isCheckedJS(loc_chkReceivedGoods),
                        "Can not restock goods.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_chkReceivedGoods),
                        "Restricted popup is not shown.");
            }
            logger.info("Check permission: Orders >> Return order >> Restock goods.");
        }
    }

    void completedEditReturnOrder() {
        commonAction.click(loc_btnSave);
        assertCustomize.assertFalse(commonAction.getListElement(loc_dlgToastSuccess).isEmpty(),
                "Can not create new return order.");
    }

    public void checkEditReturnOrder(int returnOrderId) {
        if (returnOrderId != 0) {
            // navigate to edit return order page
            navigateToEditReturnOrderPage(returnOrderId);

            // check edit return order permission
            if (permissions.getOrders().getReturnOrder().isEditReturnOrder()) {
                assertCustomize.assertTrue(driver.getCurrentUrl().contains("/order/return-order/edit/"), "Can not access to edit return order page.");

                checkRestockGoods();

                // save changes
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave, loc_dlgToastSuccess),
                        "Can not edit return order.");
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted page is not shown.");
            }
        }
        logger.info("Check permission: Orders >> Return order >> Check edit return order.");
    }
}
