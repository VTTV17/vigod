package web.Dashboard.products.transfer.management;

import api.Seller.login.Login;
import api.Seller.products.transfer.TransferManagement;
import api.Seller.setting.BranchManagement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.products.transfer.crud.TransferPage;

import java.util.List;
import java.util.Map;

import static api.Seller.products.transfer.TransferManagement.TransferInfo;
import static utilities.links.Links.DOMAIN;

public class TransferManagementPage extends TransferManagementElement {
    Logger logger = LogManager.getLogger(TransferManagement.class);
    WebDriver driver;
    TransferPage transferPage;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    TransferManagement transferManagement;

    public TransferManagementPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
    }

    public TransferManagementPage getLoginInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        return this;
    }

    public void checkTransferPermission(AllPermissions permissions, TransferInfo transferInfo) throws Exception {
        // get staff permission
        this.permissions = permissions;
        Map<String, Boolean> transferPermissionMap = new ObjectMapper().convertValue(permissions.getProduct().getTransfer(), new TypeReference<>() {});
        if (transferPermissionMap.containsValue(true)) {

            // init commons check no permission
            checkPermission = new CheckPermission(driver);

            // init transfer page PO
            transferPage = new TransferPage(driver);

            // init transfer management API
            transferManagement = new TransferManagement(loginInformation);

            // init branch management API
            BranchManagement branchManagement = new BranchManagement(loginInformation);

            // get assigned branches
            List<Integer> assignedBranchIds = (loginInfo.getAssignedBranchesIds() != null)
                    ? loginInfo.getAssignedBranchesIds() // staff
                    : branchManagement.getInfo().getBranchID(); // seller

            // check view transfer list
            checkViewTransferList(assignedBranchIds, transferInfo);

            // check create transfer
            checkCreateTransfer();

            // check view transfer detail
            int hasViewPermissionTransferId = transferManagement.getViewPermissionTransferId(assignedBranchIds, transferInfo);
            int noViewPermissionTransferId = transferManagement.getNoViewPermissionTransferId(assignedBranchIds, transferInfo);
            int hasConfirmShipGoodsPermissionTransferId = transferManagement.getConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfo);
            int noConfirmShipGoodsPermissionTransferId = transferManagement.getNoConfirmShipGoodsPermissionTransferId(assignedBranchIds, transferInfo);
            int hasConfirmReceivedGoodsPermissionTransferId = transferManagement.getConfirmReceiveGoodsPermissionTransferId(assignedBranchIds, transferInfo);
            int noConfirmReceivedGoodsPermissionTransferId = transferManagement.getNoConfirmReceiveGoodsPermissionTransferId(assignedBranchIds, transferInfo);
            int hasCancelPermissionTransferId = transferManagement.getCancelTransferPermissionTransferId(assignedBranchIds, transferInfo);
            transferPage.checkViewTransferDetail(loginInformation,
                    permissions,
                    hasViewPermissionTransferId,
                    noViewPermissionTransferId,
                    hasConfirmShipGoodsPermissionTransferId,
                    noConfirmShipGoodsPermissionTransferId,
                    hasConfirmShipGoodsPermissionTransferId,
                    noConfirmShipGoodsPermissionTransferId,
                    hasConfirmReceivedGoodsPermissionTransferId,
                    noConfirmReceivedGoodsPermissionTransferId,
                    hasCancelPermissionTransferId);

        }
    }

    void navigateToTransferManagementPage() {
        if (!driver.getCurrentUrl().contains("product/transfer/list")) {
            driver.get("%s/product/transfer/list".formatted(DOMAIN));
            logger.info("Navigate to transfer management list.");
        }
    }

    void checkViewTransferList(List<Integer> assignedBranchIds, TransferInfo allTransfersInfo) {
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
            transferPage.createTransfer(loginInformation);
        } else {
            // Staff without permission “Create transfer” => Show restricted when:
            // click on [Add transfer] button in Product >> Transfer
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateTransfer),
                    "Restricted popup does not shown.");
        }
    }
}
