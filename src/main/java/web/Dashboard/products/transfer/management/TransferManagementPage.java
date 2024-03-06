package web.Dashboard.products.transfer.management;

import api.Seller.login.Login;
import api.Seller.products.transfer.TransferManagement;
import api.Seller.setting.BranchManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.transfer.crud.TransferPage;

import java.util.List;

import static api.Seller.products.transfer.TransferManagement.TransferInfo;
import static utilities.links.Links.DOMAIN;

public class TransferManagementPage extends TransferManagementElement {
    Logger logger = LogManager.getLogger(TransferManagement.class);
    WebDriver driver;
    TransferPage transferPage;
    UICommonAction commonAction;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo loginInfo;

    public TransferManagementPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
    }

    public TransferManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellerLoginInformation = sellerLoginInformation;
        loginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    /*-------------------------------------*/
    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-31079
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    public void checkTransferPermission(AllPermissions permissions) throws Exception {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init transfer page PO
        transferPage = new TransferPage(driver);

        // init branch management API
        BranchManagement branchManagement = new BranchManagement(staffLoginInformation);

        // get assigned branches
        List<Integer> assignedBranchIds = loginInfo.getAssignedBranchesIds();

        // check view transfer list
        TransferInfo transferInfo = new TransferManagement(sellerLoginInformation).getAllTransferInfo();
        checkViewTransferList(assignedBranchIds, transferInfo);

        // check create transfer
        checkCreateTransfer();

        // check view transfer detail
        transferPage.checkViewTransferDetail(staffLoginInformation, permissions, assignedBranchIds, transferInfo);
    }

     void navigateToTransferManagementPage() {
        if (!driver.getCurrentUrl().contains("product/transfer/list")) {
            driver.get("%s/product/transfer/list".formatted(DOMAIN));
            logger.info("Navigate to transfer management list.");
        }
    }

    void checkViewTransferList(List<Integer> assignedBranchIds, TransferInfo allTransfersInfo) {
        // init transfer management API
        TransferManagement transferManagement = new TransferManagement(staffLoginInformation);

        // check permission
        if (permissions.getProduct().getTransfer().isViewTransferList()) {
            // list transfer ids that filter from all transfer ids
            List<Integer> checkList = transferManagement.getListTransferId(assignedBranchIds, allTransfersInfo);

            // list transfer ids get from API
            List<Integer> fromAPI = transferManagement.getAllTransferInfo().getIds();

            // Staff has permission “View transfer list”
            // => see all transfer that has original / destination branch in assigned branch list
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(checkList, fromAPI),
                    "Transfer list must be: %s, but found: %s.".formatted(checkList, fromAPI));
        } else {
            // Staff don’t have permission “View transfer list”
            // => don’t see any transfer in transfer page
            TransferInfo transferInfo = transferManagement.getAllTransferInfo();
            assertCustomize.assertTrue(transferInfo.getIds().isEmpty(),
                    "Transfer list must be empty, but found: %s.".formatted(transferInfo.getIds().toString()));
        }

        // log
        logger.info("Check permission: Product >> Transfer >> View transfer list.");
    }

    void checkCreateTransfer() throws Exception {
        navigateToTransferManagementPage();

        if (permissions.getProduct().getTransfer().isCreateTransfer()) {
            // check can create transfer
            transferPage.createTransfer(staffLoginInformation);

            // staff can view transfer detail after created
            if (permissions.getProduct().getTransfer().isViewTransferDetail()) {
                try {
                    commonAction.waitURLShouldBeContains("/product/transfer/wizard/");
                } catch (TimeoutException ex) {
                    logger.error("Transfer detail page is not shown.");
                }
            } else {
                // staff can not view transfer detail
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted popup is not shown.");
            }
        } else {
            // Staff without permission “Create transfer” => Show restricted when:
            // click on [Add transfer] button in Product >> Transfer
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddTransfer),
                    "Restricted popup is not shown.");
        }
    }
}
