package web.Dashboard.orders.orderlist.add_shipment_package;

import api.Seller.login.Login;
import api.Seller.orders.delivery.APIPartialDeliveryOrders;
import api.Seller.orders.delivery.APIPartialDeliveryOrders.DeliveryMethod;
import api.Seller.orders.order_management.APIAllOrders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static api.Seller.orders.delivery.APIPartialDeliveryOrders.DeliveryMethod.*;
import static api.Seller.orders.order_management.APIAllOrders.Channel.GOSELL;
import static utilities.links.Links.DOMAIN;

public class AddShipmentPackagePage extends AddShipmentPackageElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(AddShipmentPackagePage.class);

    public AddShipmentPackagePage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void navigateToAddShipmentPackagePageByURL(long orderId) {
        driver.get("%s/order/arrange-shipment/GOSELL/%s".formatted(DOMAIN, orderId));
        driver.navigate().refresh();
        logger.info("Navigate to add shipment package page by URL, orderId: %s.".formatted(orderId));
    }

    void selectLot() {
        // remove selected lot if any
        if (!commonAction.getListElement(loc_icnRemoveSelectedLot).isEmpty()) {
            int bound = commonAction.getListElement(loc_icnRemoveSelectedLot).size();
            IntStream.iterate(bound - 1, index -> index >= 0, index -> index - 1)
                    .forEach(index -> commonAction.clickJS(loc_icnRemoveSelectedLot, index));
        }
        // select lot if any
        if (!commonAction.getListElement(lnkSelectLot).isEmpty()) {
            int numberOfLotProduct = commonAction.getListElement(lnkSelectLot).size();
            for (int index = 0; index < numberOfLotProduct; index++) {
                // open select lot popup
                commonAction.clickJS(lnkSelectLot, index);
                long quantity = Long.parseLong(commonAction.getValue(loc_txtLotQuantity, index));

                // select lot
                if (!commonAction.getListElement(loc_dlgSelectLot).isEmpty()) {
                    if (!commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).isEmpty()) {
                        int bound = commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).size();

                        for (int lotIndex = 0; lotIndex < bound; lotIndex++) {
                            if (quantity > 0) {
                                // input quantity
                                long availableQuantity = Long.parseLong(commonAction.getText(loc_dlgSelectLot_txtConfirmQuantity, lotIndex));
                                commonAction.sendKeys(loc_dlgSelectLot_txtConfirmQuantity, String.valueOf(Math.min(quantity, availableQuantity)));
                                logger.info("Input lot quantity: %,d.".formatted(quantity));

                                // get current quantity
                                quantity -= Math.min(quantity, availableQuantity);
                            }
                        }

                        // save changes
                        commonAction.click(loc_dlgSelectLot_btnConfirm);
                    } else logger.warn("Can not found lot.");
                }
            }
        }
    }

    void selectIMEI() {
        int bound = commonAction.getListElement(loc_lnkSelectIMEI).size();
        for (int index = 0; index < bound; index++) {
            // get current IMEI quantity
            long quantity = Long.parseLong(commonAction.getText(loc_lblIMEIItemQuantity, index).replaceAll("\\D+", ""));
            // open select IMEI popup
            commonAction.clickJS(loc_lnkSelectIMEI, index);

            // check select IMEI show or not
            if (!commonAction.getListElement(loc_dlgSelectIMEI).isEmpty()) {
                // remove selected imei
                for (int removeIndex = commonAction.getListElement(loc_dlgSelectIMEI_lstRemoveIMEI).size() - 1; removeIndex >= 0; removeIndex--) {
                    commonAction.clickJS(loc_dlgSelectIMEI_lstRemoveIMEI, removeIndex);
                }

                // select IMEI
                for (int selectIndex = 0; selectIndex < quantity; selectIndex++) {
                    commonAction.clickJS(loc_dlgSelectIMEI_lstIMEI, selectIndex);
                }

                //save changes
                commonAction.click(loc_dlgSelectIMEI_btnSave);
            }
        }
    }

    void selectDeliveryMethod(DeliveryMethod deliveryMethod) {
        // select method
        commonAction.selectDropdownOptionByValue(loc_ddlDeliveryMethod, String.valueOf(deliveryMethod));

        if (!commonAction.getListElement(loc_icnUnfortunately).isEmpty()) {
            // input customer info if any
            if (!commonAction.getListElement(loc_chkAsSameAsSenderInformation).isEmpty()) {
                // add customer address
                if (!commonAction.isCheckedJS(loc_chkAsSameAsSenderInformation))
                    commonAction.clickJS(loc_chkAsSameAsSenderInformation);

                // input package information
                commonAction.sendKeys(loc_txtPackageInformationWeight, "1");
                commonAction.sendKeys(loc_txtPackageInformationLength, "1");
                commonAction.sendKeys(loc_txtPackageInformationWidth, "1");
                commonAction.sendKeys(loc_txtPackageInformationHeight, "1");
            }

            if (Objects.equals(deliveryMethod, ahamove)) {
                // open receiver province dropdown
                String senderProvince = commonAction.getValue(loc_ddlAhamoveSenderProvince);
                commonAction.click(loc_ddlAhamoveReceiverProvince);
                commonAction.clickJS(By.xpath(str_ddvAhamoveReceiverProvince.formatted(senderProvince)));

                // open receiver district dropdown
                String senderDistrict = commonAction.getValue(loc_ddlAhamoveSenderDistrict);
                commonAction.click(loc_ddlAhamoveReceiverDistrict);
                commonAction.clickJS(By.xpath(str_ddvAhamoveReceiverDistrict.formatted(senderDistrict)));

                // open receiver commune dropdown
                String senderCommune = commonAction.getValue(loc_ddlAhamoveSenderCommune);
                commonAction.click(loc_ddlAhamoveReceiverCommune);
                commonAction.clickJS(By.xpath(str_ddvAhamoveReceiverCommune.formatted(senderCommune)));
            }

            if (!commonAction.getListElement(loc_btnReEstimateDeliveryFee).isEmpty()) {
                commonAction.click(loc_btnReEstimateDeliveryFee);
                commonAction.sleepInMiliSecond(3000, "Wait estimate shipping fee.");
            }
        } else
            logger.warn("Unfortunately, %s does not offer delivery services for this address.".formatted(deliveryMethod));

        // log
        logger.info("Select delivery method: %s.".formatted(deliveryMethod));
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24815
    LoginInformation sellerLoginInformation;
    LoginInformation staffLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    APIPartialDeliveryOrders apiPartialDeliveryOrdersWithStaffToken;
    APIAllOrders apiAllOrdersWithSellerToken;
    LoginDashboardInfo staffLoginInfo;

    public AddShipmentPackagePage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public AddShipmentPackagePage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiPartialDeliveryOrdersWithStaffToken = new APIPartialDeliveryOrders(staffLoginInformation);
        apiAllOrdersWithSellerToken = new APIAllOrders(sellerLoginInformation);
        return this;
    }

    public void checkDeliveryPermission() {
        // check add shipment package by 3rd party
        checkAddShipmentPackageBy3rdParty();

        // check add shipment package by self-delivery/others.
        checkAddShipmentPackageBySelfDelivery();
    }


    void checkAddShipmentPackageBy3rdParty() {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForAddShipmentPackage(GOSELL, staffLoginInfo.getAssignedBranchesIds());
        if (orderId != 0) {
            navigateToAddShipmentPackagePageByURL(orderId);
            List<DeliveryMethod> availableDeliveryMethods = apiPartialDeliveryOrdersWithStaffToken.getListDeliveryMethodWithOrder(orderId);
            selectLot();
            selectIMEI();
            if (availableDeliveryMethods.contains(giaohangnhanh)) {
                selectDeliveryMethod(giaohangnhanh);
            } else if (availableDeliveryMethods.contains(giaohangtietkiem)) {
                selectDeliveryMethod(giaohangtietkiem);
            } else if (availableDeliveryMethods.contains(ahamove)) {
                selectDeliveryMethod(ahamove);
            } else logger.warn("Can not found 3rd party");

            if (permissions.getOrders().getDelivery().isAddShipmentPackageBy3rdParty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnConfirm, loc_dlgConfirm),
                        "Can not open confirm popup.");

                if (!commonAction.getListElement(loc_dlgConfirm).isEmpty()) {
                    assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirm_btnYes, loc_dlgToastSuccess),
                            "Can not add arrange shipment with 3rd party.");
                }
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnConfirm),
                        "Restricted popup is not shown.");
            }
        }

        // log
        logger.info("Check permission: Orders >> Delivery >> Add shipment package by 3rd party.");
    }

    void checkAddShipmentPackageBySelfDelivery() {
        long orderId = apiAllOrdersWithSellerToken.getOrderIdForAddShipmentPackage(GOSELL, staffLoginInfo.getAssignedBranchesIds());
        if (orderId != 0) {
            navigateToAddShipmentPackagePageByURL(orderId);
            List<DeliveryMethod> availableDeliveryMethods = apiPartialDeliveryOrdersWithStaffToken.getListDeliveryMethodWithOrder(orderId);
            selectLot();
            selectIMEI();
            if (availableDeliveryMethods.contains(selfdelivery)) {
                selectDeliveryMethod(selfdelivery);
            } else if (availableDeliveryMethods.contains(others)) {
                selectDeliveryMethod(others);
            } else logger.warn("Can not found self-delivery methods.");

            // click confirm button
            commonAction.click(loc_btnConfirm);

            if (permissions.getOrders().getDelivery().isAddShipmentPackageBySelfDeliveryOther()) {
                if (commonAction.getListElement(loc_dlgToastSuccess).isEmpty()) {
                    assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirm).isEmpty(),
                            "Can not open confirm popup.");

                    if (!commonAction.getListElement(loc_dlgConfirm).isEmpty()) {
                        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirm_btnYes, loc_dlgToastSuccess),
                                "Can not add arrange shipment with self-delivery/others.");
                    }
                }
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                        "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Orders >> Delivery >> Add shipment package by Self-delivery/Others.");
    }
}
