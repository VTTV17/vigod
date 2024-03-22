package web.Dashboard.supplier.debt.management;

import api.Seller.login.Login;
import api.Seller.supplier.debt.APIDebtManagement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.supplier.debt.crud.DebtPage;

import java.util.List;

import static utilities.links.Links.DOMAIN;

public class DebtManagementPage {
    WebDriver driver;
    AssertCustomize assertCustomize;
    Logger logger = LogManager.getLogger(DebtManagementPage.class);

    public DebtManagementPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-26888
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    DebtPage debtPage;
    LoginDashboardInfo staffLoginInfo;

    public DebtManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    public void checkDebtPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init debt page
        debtPage = new DebtPage(driver);

        // check view debt history
        checkViewDebtHistory();

        // check create/edit/delete/public/make a debt repayment
        debtPage.getLoginInformation(staffLoginInformation).checkDebtPermission(permissions);
    }

    void checkViewDebtHistory() {
        // check permission
        if (permissions.getSuppliers().getDebt().isViewDebtHistory()) {
            // check can access to supplier debt management page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/supplier-debt/list".formatted(DOMAIN),
                            "/supplier/supplier-debt/list"),
                    "Can not access to supplier debt management page.");

            // check list debt
            List<Integer> listDebtIdWithStaffToken = new APIDebtManagement(staffLoginInformation)
                    .getAllDebtInformation()
                    .getIds();
            List<Integer> listDebtIdWithSellerToken = new APIDebtManagement(sellerLoginInformation)
                    .getAllDebtInformation(staffLoginInfo.getAssignedBranchesIds());
            assertCustomize.assertTrue(CollectionUtils.isEqualCollection(listDebtIdWithStaffToken, listDebtIdWithSellerToken),
                    "Debt list must be %s, but found %s.".formatted(listDebtIdWithSellerToken, listDebtIdWithStaffToken));
        } else {
            // if staff don’t have "View debt history" permission
            // => show the restricted page
            // when access to debt management page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/supplier-debt/list".formatted(DOMAIN)),
                    "Restricted page is not shown.");
        }

        // log
        logger.info("Check permission: Supplier >> Debt >> View debt history.");
    }
}
