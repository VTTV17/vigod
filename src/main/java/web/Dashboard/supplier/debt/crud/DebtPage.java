package web.Dashboard.supplier.debt.crud;

import org.openqa.selenium.WebDriver;
import utilities.assert_customize.AssertCustomize;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import static utilities.links.Links.DOMAIN;

public class DebtPage {
    WebDriver driver;
    AssertCustomize assertCustomize;
    public DebtPage(WebDriver driver) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
    }

    /*----------------------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-26888
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    void checkViewDebtHistory(int debtId) {
        // check permission
        if (permissions.getSuppliers().getDebt().isViewDebtHistory()) {
            // check can access to supplier debt detail page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/supplier/supplier-debt/edit/%s".formatted(DOMAIN, debtId),
                            String.valueOf(debtId)),
                    "Can not access to supplier debt detail page.");
        } else {
            // if staff don’t have "View debt history" permission
            // => show the restricted page
            // when access to debt detail page
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/supplier/supplier-debt/edit/%s".formatted(DOMAIN, debtId)),
                    "Restricted page is not shown.");
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
    }

    void checkEditADebt() {

    }

    void checkDeleteADebt() {

    }

    void checkPublicADebt() {

    }

    void checkMakeADebtRepayment() {

    }
}
