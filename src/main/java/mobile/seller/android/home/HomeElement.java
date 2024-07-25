package mobile.seller.android.home;

import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static io.appium.java_client.AppiumBy.androidUIAutomator;
import static utilities.commons.UICommonAndroid.androidUIAutomatorResourcesIdInstanceString;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;

public class HomeElement {
    public enum QuickActions {
        createNewOrder, createReservation, addProduct, addNewCustomer, scanProduct, liveStream, facebook, zaloOA;

        static List<QuickActions> getAllQuickActions() {
            return Arrays.asList(QuickActions.values());
        }
    }

    By loc_btnQuickAccessActions(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/ivIcon".formatted(goSELLERBundleId), actionsIndex));
    }

    public enum ManagementActions {
        orders, products, customers, reservations, inventory, supplier, purchaseOrders, cashbook, affiliate, discount, imei, inventoryCheck;

        static List<ManagementActions> getAllManagementActions() {
            return Arrays.asList(ManagementActions.values());
        }
    }

    By loc_btnManagementActions(int actionsIndex) {
        return androidUIAutomator(androidUIAutomatorResourcesIdInstanceString.formatted("%s:id/ivIcon".formatted(goSELLERBundleId), QuickActions.getAllQuickActions().size() + actionsIndex));
    } 
    By loc_tabAccount = By.xpath("//*[contains(@resource-id, 'bottom_navigation_tab_account')]");
    By loc_btnLogout = By.xpath("//*[contains(@resource-id, 'llLogout')]");
    By loc_btnOK = By.xpath("//*[contains(@resource-id, 'tvRightButton')]");
    By loc_btnLogoutAbort = By.xpath("//*[contains(@resource-id, 'tvLeftButton')]");
}
