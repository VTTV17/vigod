package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.login.LoginPage;
import web.Dashboard.orders.delivery.delivery_management.DeliveryManagementPage;
import web.Dashboard.orders.orderlist.order_list.OrderManagementPage;
import web.Dashboard.orders.pos.check_permission.POSPage;
import web.Dashboard.orders.quotation.QuotationPage;
import web.Dashboard.orders.return_orders.return_order_management.ReturnOrdersManagementPage;
import web.Dashboard.products.all_products.management.ProductManagementPage;
import web.Dashboard.products.inventory.InventoryPage;
import web.Dashboard.products.location.LocationPage;
import web.Dashboard.products.location_receipt.management.LocationReceiptManagementPage;
import web.Dashboard.products.lot_date.management.LotDateManagementPage;
import web.Dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import web.Dashboard.products.productreviews.ProductReviews;
import web.Dashboard.products.transfer.management.TransferManagementPage;
import web.Dashboard.supplier.debt.management.DebtManagementPage;
import web.Dashboard.supplier.purchaseorders.management.PurchaseOrderManagementPage;
import web.Dashboard.supplier.supplier.management.SupplierManagementPage;

import static java.lang.Math.pow;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class PermissionTest extends BaseTest {
    @Test
    void Permission() {
        LoginInformation sellerLogin = new LoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        LoginInformation staffLogin = new LoginInformation("test1@qa.team", "Abc@12345");

        for (int index = 0; index < 10; index++) {
            driver = new InitWebdriver().getDriver("chrome");
//            driver = new InitWebdriver().getDriver("chrome", "false");

            CreatePermission createPermission = new CreatePermission();

            int prod = nextInt((int) pow(2, 20));
            System.out.printf("prod=%d;%n", prod);

            int inv = nextInt((int) pow(2, 7));
            System.out.printf("inv= %d;%n", inv);

            int trans = nextInt((int) pow(2, 7));
            System.out.printf("trans= %d;%n", trans);

            int col = nextInt((int) pow(2, 6));
            System.out.printf("col= %d;%n", col);

            int rev = nextInt((int) pow(2, 5));
            System.out.printf("rev= %d;%n", rev);

            int lot = nextInt((int) pow(2, 7));
            System.out.printf("lot= %d;%n", lot);

            int loc = nextInt((int) pow(2, 5));
            System.out.printf("loc= %d;%n", loc);

            int lor = nextInt((int) pow(2, 15));
            System.out.printf("lor= %d;%n", lor);

            int sup = nextInt((int) pow(2, 5));
            System.out.printf("sup= %d;%n", sup);

            int pur = nextInt((int) pow(2, 10));
            System.out.printf("pur= %d;%n", pur);

            int debt = nextInt((int) pow(2, 6));
            System.out.printf("debt= %d;%n", debt);

            int ord = nextInt((int) pow(2, 24));
            System.out.printf("ord= %d;%n", ord);

            int ret = nextInt((int) pow(2, 9));
            System.out.printf("ret= %d;%n", ret);

            int quo = nextInt(2);
            System.out.printf("pos= %d;%n", quo);

            int pos = nextInt((int) pow(2, 5));
            System.out.printf("pos= %d;%n", pos);

            int del = nextInt((int) pow(2, 6));
            System.out.printf("del= %d;%n", del);


            createPermission.setProduct_productManagement(Integer.toBinaryString(prod));
            createPermission.setProduct_inventory(Integer.toBinaryString(inv));
            createPermission.setProduct_transfer(Integer.toBinaryString(trans));
            createPermission.setProduct_collection(Integer.toBinaryString(col));
            createPermission.setProduct_review(Integer.toBinaryString(rev));
            createPermission.setProduct_lotDate(Integer.toBinaryString(lot));
            createPermission.setProduct_location(Integer.toBinaryString(loc));
            createPermission.setProduct_locationReceipt(Integer.toBinaryString(lor));
            createPermission.setSupplier_supplier(Integer.toBinaryString(sup));
            createPermission.setSupplier_purchaseOrder(Integer.toBinaryString(pur));
            createPermission.setSupplier_debt(Integer.toBinaryString(debt));
            new PermissionAPI(sellerLogin).createPermissionGroupThenGrantItToStaff(sellerLogin, staffLogin, createPermission);


            LoginDashboardInfo infoStaff = new Login().getInfo(staffLogin);

            AllPermissions permissions = new AllPermissions(infoStaff.getStaffPermissionToken());
            new LoginPage(driver).loginDashboardByJs(staffLogin);

            try {
                new ProductManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkProductManagementPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new InventoryPage(driver).getLoginInformation(sellerLogin, staffLogin).checkInventoryPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new TransferManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkTransferPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new ProductCollectionManagement(driver).getLoginInformation(sellerLogin, staffLogin).checkProductCollectionPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new ProductReviews(driver).getLoginInformation(staffLogin).checkProductReviewPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new LotDateManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkLotDatePermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new LocationPage(driver).getLoginInformation(staffLogin).checkLocationPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new LocationReceiptManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkLocationReceiptPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new SupplierManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkSupplierPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new PurchaseOrderManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkPurchaseOrderPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new DebtManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkDebtPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new OrderManagementPage(driver).getLoginInformation(sellerLogin, staffLogin).checkOrderManagementPermission(permissions);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new ReturnOrdersManagementPage(driver, permissions).getLoginInformation(sellerLogin, staffLogin).checkReturnOrdersPermission();
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new QuotationPage(driver, permissions).checkQuotationPermission();
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new POSPage(driver, permissions).getLoginInformation(staffLogin).checkPOSPermission();
            } catch (Exception ex) {
                System.out.println(ex);
            }

            try {
                new DeliveryManagementPage(driver, permissions).getLoginInformation(sellerLogin, staffLogin).checkDeliveryPermission();
            } catch (Exception ex) {
                System.out.println(ex);
            }
            driver.quit();
        }
    }
}
