package web.Dashboard.supplier.debt.crud;

import api.Seller.setting.BranchManagement;
import api.Seller.supplier.debt.APICreateDebt;
import api.Seller.supplier.debt.APIDebtManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.time.Instant;
import java.util.List;

import static api.Seller.supplier.debt.APICreateDebt.ReceiptType;
import static utilities.links.Links.DOMAIN;

public class DebtPage extends DebtElement {
    WebDriver driver;
    AssertCustomize assertCustomize;
    UICommonAction commonAction;
    Logger logger = LogManager.getLogger(DebtPage.class);

    public DebtPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    void navigateToCreateDebtPage() {
        // navigate to create debt page by URL
        driver.get("%s/supplier/supplier-debt/create".formatted(DOMAIN));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to create debt page by URL.");
    }

    void navigateToDebtDetailPage(int debtId) {
        // navigate to debt detail page by URL
        driver.get("%s/supplier/supplier-debt/detail/%s".formatted(DOMAIN, debtId));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to debt detail page by URL, debtId: %s.".formatted(debtId));
    }

    void navigateToEditDebtPage(int debtId) {
        // navigate to edit debt page by URL
        driver.get("%s/supplier/supplier-debt/edit/%s".formatted(DOMAIN, debtId));
        driver.navigate().refresh();

        // log
        logger.info("Navigate to edit debt page by URL, debtId: %s.".formatted(debtId));
    }


    /*----------------------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-26888
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;

    public DebtPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        return this;
    }

    public void checkDebtPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // check create a debt
        checkCreateANewDebt();

        // check view debt history
        checkViewDebtHistory();
    }

    void checkViewDebtHistory() {
        // get debt ids
        APIDebtManagement apiDebtManagement = new APIDebtManagement(staffLoginInformation);
        List<Integer> debtIds = apiDebtManagement.getAllDebtInformation().getIds();

        if (!debtIds.isEmpty()) {
            int debtId = debtIds.get(0);
            // check permission
            if (permissions.getSuppliers().getDebt().isViewDebtHistory()) {
                // check can access to supplier debt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/supplier-debt/edit/%s".formatted(DOMAIN, debtId),
                                String.valueOf(debtId)),
                        "Can not access to supplier debt detail page.");

                // get staff branch information
                BranchInfo staffBranchInfo = new BranchManagement(staffLoginInformation).getInfo();

                // init api create debt supplier with seller token
                APICreateDebt createDebtWithSellerToken = new APICreateDebt(sellerLoginInformation);

                // check edit a payment debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.PAYMENT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.PAYMENT, staffBranchInfo);
                checkEditADebt(debtId);
                logger.info("Check edit a payment debt.");

                // check edit a receipt debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.RECEIPT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.RECEIPT, staffBranchInfo);
                checkEditADebt(debtId);
                logger.info("Check edit a receipt debt.");

                // check delete a payment debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.PAYMENT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.PAYMENT, staffBranchInfo);
                checkDeleteADebt(debtId);
                logger.info("Check delete a payment debt.");

                // check delete a receipt debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.RECEIPT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.RECEIPT, staffBranchInfo);
                checkDeleteADebt(debtId);
                logger.info("Check delete a receipt debt.");

                // check public a payment debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.PAYMENT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.PAYMENT, staffBranchInfo);
                checkPublicADebt(debtId);
                logger.info("Check public a payment debt.");

                // check public a receipt debt
                debtId = apiDebtManagement.getIdOfOpenDebt(ReceiptType.RECEIPT);
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(false, ReceiptType.RECEIPT, staffBranchInfo);
                checkPublicADebt(debtId);
                logger.info("Check public a receipt debt.");

                // check make a repayment payment debt
                debtId = apiDebtManagement.getIdOfPayableDebt();
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(true, ReceiptType.PAYMENT, staffBranchInfo);
                checkMakeADebtRepayment(debtId);
                logger.info("Check make a repayment payment debt.");

                // check make a repayment receipt debt
                debtId = apiDebtManagement.getIdOfReceivableDebt();
                if (debtId == 0)
                    debtId = createDebtWithSellerToken.createAndGetSupplierDebtId(true, ReceiptType.RECEIPT, staffBranchInfo);
                checkMakeADebtRepayment(debtId);
                logger.info("Check make a repayment receipt debt.");

            } else {
                // if staff don’t have "View debt history" permission
                // => show the restricted page
                // when access to debt detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/supplier-debt/edit/%s".formatted(DOMAIN, debtId)),
                        "Restricted page is not shown.");
            }
        }

    }

    void checkCreateANewDebt() {
        // check permission
        if (permissions.getSuppliers().getDebt().isCreateANewDebt()) {
            // check can access to create supplier debt page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/supplier-debt/create".formatted(DOMAIN),
                            "/supplier/supplier-debt/create"),
                    "Can not access to create supplier debt page.");
        } else {
            // if staff don’t have "Create a new debt" permission
            // => show the restricted page
            // when access to create a new supplier debt page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/supplier-debt/create".formatted(DOMAIN)),
                    "Restricted page is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Debt >> Create a new debt.");
    }

    void checkEditADebt(int debtId) {
        // navigate to debt detail page
        navigateToEditDebtPage(debtId);

        // check permission
        if (permissions.getSuppliers().getDebt().isEditADebt()) {
            // check edit a debt
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnSave,
                            loc_dlgToastSuccess),
                    "Can not edit supplier debt.");
        } else {
            // if staff don’t have "Edit a debt" permission
            // => show the restricted page
            // when edit a supplier debt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnSave),
                    "Restricted popup is not shown.");
        }


        // log
        logger.info("Check permission: Supplier >> Debt >> Edit a debt.");
    }

    void checkDeleteADebt(int debtId) {

        // navigate to debt detail page
        navigateToEditDebtPage(debtId);

        // check permission
        if (permissions.getSuppliers().getDebt().isDeleteADebt()) {
            // check delete supplier debt
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnDelete,
                            loc_dlgToastSuccess),
                    "Can not delete supplier debt.");
        } else {
            // if staff don’t have "Delete a debt" permission
            // => show the restricted page
            // when delete a supplier debt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnDelete),
                    "Restricted popup is not shown.");
        }


        // log
        logger.info("Check permission: Supplier >> Debt >> Delete a debt.");
    }

    void checkPublicADebt(int debtId) {
        // check public debt at create page
        if (permissions.getSuppliers().getDebt().isCreateANewDebt()) {
            // navigate to create supplier page
            navigateToCreateDebtPage();

            // check permission
            if (permissions.getSuppliers().getDebt().isPublicADebt()) {
                // open confirm public debt popup
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnPublic,
                                loc_dlgConfirmPublic),
                        "Can not open confirm public supplier debt popup.");
            } else {
                // check public debt
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPublic),
                        "Restricted popup is not shown.");
            }
        }

        // check public debt at edit page

        // navigate to debt detail page
        navigateToEditDebtPage(debtId);

        // check permission
        if (permissions.getSuppliers().getDebt().isPublicADebt()) {
            // open confirm public debt popup
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnPublic,
                            loc_dlgConfirmPublic),
                    "Can not open confirm public supplier debt popup.");

            // public a debt
            if (!commonAction.getListElement(loc_dlgConfirmPublic).isEmpty()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_dlgConfirmPublic_btnOk,
                                loc_dlgToastSuccess),
                        "Can not public supplier debt.");
            }
        } else {
            // if staff don’t have "Public a debt" permission
            // => show the restricted page
            // when public a supplier debt
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnPublic),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Debt >> Public a debt.");
    }

    void checkMakeADebtRepayment(int debtId) {

        // navigate to debt detail page
        navigateToDebtDetailPage(debtId);

        // check permission
        if (permissions.getSuppliers().getDebt().isMakeADebtRepayment()) {
            // open payment popup
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnMakeARepayment,
                            loc_dlgPayment),
                    "Can not open payment popup.");
            // get current payment history
            int numberOfPaymentHistories = commonAction.getListElement(loc_tblPaymentHistory_recordHistory).size();

            // make a debt repayment
            if (!commonAction.getListElement(loc_dlgPayment).isEmpty()) {
                // input payment description
                commonAction.sendKeys(loc_dlgPayment_txtDescription, "Payment description %s".formatted(Instant.now().toEpochMilli()));

                // confirm payment
                commonAction.click(loc_dlgPayment_btnConfirm);

                // check payment is created or not
                assertCustomize.assertTrue((commonAction.getListElement(loc_tblPaymentHistory_recordHistory).size() - numberOfPaymentHistories) == 1,
                        "Can not make a payment.");
            }
        } else {
            // if staff don’t have "Make a debt repayment" permission
            // => show the restricted page
            // when make a debt repayment
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnMakeARepayment),
                    "Restricted popup is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Debt >> Make a debt repayment.");
    }
}