package web.Dashboard.supplier.debt.management;

import api.Seller.login.Login;
import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.supplier.debt.crud.DebtPage;

import static utilities.links.Links.DOMAIN;

public class DebtManagementPage {
    WebDriver driver;
    AssertCustomize assertCustomize;
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

    void checkViewDebtHistory() {
        // check permission
        if (permissions.getSuppliers().getDebt().isViewDebtHistory()) {
            // check can access to supplier debt management page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/supplier-debt/list".formatted(DOMAIN),
                            "/supplier/supplier-debt/list"),
                    "Can not access to supplier debt management page.");
        } else {
            // if staff don’t have "View debt history" permission
            // => show the restricted page
            // when access to debt management page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/supplier-debt/list".formatted(DOMAIN)),
                    "Restricted page is not shown.");
        }
    }
}
