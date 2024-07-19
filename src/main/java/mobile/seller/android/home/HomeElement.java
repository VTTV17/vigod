package mobile.seller.android.home;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class HomeElement {
    String rsId_sectionQuickAccess = "%s:id/rlvQuickActions".formatted(goSELLERBundleId);

    public enum QuickActions {
        createNewOrder, createReservation, addProduct, addNewCustomer, scanProduct, liveStream, facebook, zaloOA;

        static List<QuickActions> getAllQuickActions() {
            return Arrays.asList(QuickActions.values());
        }
    }

    By loc_btnQuickAccessActions = By.xpath("//*[@resource-id = '%s:id/rlvQuickActions']//*[@resource-id ='%s:id/ivIcon']".formatted(goSELLERBundleId, goSELLERBundleId));

    public enum ManagementActions {
        orders, products, customers, reservations, inventory, supplier, purchaseOrders, cashbook, affiliate, discount, imei, inventoryCheck;

        static List<ManagementActions> getAllManagementActions() {
            return Arrays.asList(ManagementActions.values());
        }
    }

    String rsId_sectionManagement = "%s:id/rlvManagements".formatted(goSELLERBundleId);
    By loc_btnManagementActions = By.xpath("//*[@* ='%s:id/rlvManagements']//*[@* = '%s:id/ivIcon']".formatted(goSELLERBundleId, goSELLERBundleId));
    By loc_tabAccount = By.xpath("//*[contains(@resource-id, 'bottom_navigation_tab_account')]");
    By loc_btnLogout = By.xpath("//*[contains(@resource-id, 'llLogout')]");
    By loc_btnOK = By.xpath("//*[contains(@resource-id, 'tvRightButton')]");
    By loc_btnLogoutAbort = By.xpath("//*[contains(@resource-id, 'tvLeftButton')]");
}
