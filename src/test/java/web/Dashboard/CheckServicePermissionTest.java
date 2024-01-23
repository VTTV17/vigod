package web.Dashboard;

import org.testng.annotations.BeforeClass;
import web.BaseTest;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class CheckServicePermissionTest extends BaseTest {
    String sellerUserName;
    String sellerPassword;
    String languageDB;
    @BeforeClass
    public void beforeClass() throws Exception {
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        languageDB = language;
    }
}
