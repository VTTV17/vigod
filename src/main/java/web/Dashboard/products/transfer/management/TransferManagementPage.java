package web.Dashboard.products.transfer.management;

import api.Seller.products.transfer.TransferManagement;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.util.List;
import java.util.stream.IntStream;

public class TransferManagementPage extends TransferManagementElement {
    WebDriver driver;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;
    public TransferManagementPage(WebDriver driver) {
        this.driver = driver;
    }
    boolean checkBranch(List<Integer> assignedBranch, int branch) {
        return assignedBranch.contains(branch);
    }

    void checkViewTransferList() {
        TransferManagement.TransferInfo info = new TransferManagement(loginInformation).getAllTransferInfo();
        if (permissions.getProduct().getTransfer().isViewTransferList()) {
            List<Integer> assignedBranch = loginInfo.getAssignedBranchesIds();
            IntStream.range(0, info.getIds().size()).forEachOrdered(index -> {
                int originBranch = info.getOriginBranchIds().get(index);
                int destinationBranch = info.getDestinationBranchIds().get(index);
                assertCustomize.assertTrue(assignedBranch.contains(originBranch) || assignedBranch.contains(destinationBranch), "[Failed] Transfer of unassigned branch is shown. Branch: %s.".formatted(List.of(originBranch, destinationBranch)));
            });
        } else {
            assertCustomize.assertTrue(info.getIds().isEmpty(), "[Failed] All transfers must be hidden, but found: %s.".formatted(info.getIds()));
        }
    }

    void checkCreateTransfer() {
        if (!permissions.getProduct().getTransfer().isCreateTransfer()) {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateTransfer), "Restricted popup does not shown.");
        }
    }

}
