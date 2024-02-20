package app.android.GoSeller;

import api.Seller.products.supplier.SupplierAPI;
import app.android.BaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import app.GoSeller.account.SellerAccount;
import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import app.GoSeller.supplier.create.CreateSupplierScreen;
import app.GoSeller.supplier.management.SupplierManagementScreen;
import app.GoSeller.supplier.update.UpdateSupplierScreen;
import utilities.driver.InitAppiumDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.sellerApp.supplier.SupplierInformation;

import java.io.File;
import java.io.IOException;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class SupplierTest extends BaseTest {
    String udid = "RFCW81B9BRX";
    String appPackage = "com.mediastep.GoSellForSeller.STG";
    String appActivity = "com.mediastep.gosellseller.modules.splash_screen.SplashScreenActivity";
    String URL = "http://127.0.0.1:4723/wd/hub";
    CreateSupplierScreen createSupplierScreen;
    UpdateSupplierScreen updateSupplierScreen;
    SupplierManagementScreen supplierManagementScreen;

    @BeforeClass
    void setup() throws Exception {
        tcsFileName = "android/Check CRUD supplier.xlsx".replace("/", File.separator);
        // init appium driver
        driver = new InitAppiumDriver().getAppiumDriver(udid, "ANDROID", appPackage, appActivity, URL);

        // login to GoSeller app
        new LoginPage(driver).performLogin(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);

        // set language for test
        new SellerAccount(driver).changeLanguage(language);

        // init create supplier page object
        createSupplierScreen = new CreateSupplierScreen(driver);

        // init update supplier page object
        updateSupplierScreen = new UpdateSupplierScreen(driver);

        // init supplier management page object
        supplierManagementScreen = new SupplierManagementScreen(driver);
    }

    @BeforeMethod
    void openSupplier() {
        // navigate to supplier management screen
        new HomePage(driver).navigateToPage("Supplier");
    }

    @Test
    void SUP01_ScenarioForVNSupplier() throws IOException {
        testCaseId = "SUP01";
        /* CREATE SUPPLIER */
        // Create new supplier
        createSupplierScreen.createNewSupplier(true);

        // get supplier information
        SupplierInformation supInfo = createSupplierScreen.getSupplierInfo();

        // check supplier information at supplier management
        supplierManagementScreen.checkSupplierInformationAtSupplierManagementScreen(supInfo);

        // check supplier information at supplier detail screen
        updateSupplierScreen.checkAllSupplierInformation(supInfo);

        /* UPDATE SUPPLIER */
        // update information
        updateSupplierScreen.updateNewSupplier(false);

        // get supplier information
        supInfo = updateSupplierScreen.getSupInfo();

        // check supplier information at supplier management
        supplierManagementScreen.checkSupplierInformationAtSupplierManagementScreen(supInfo);

        // check supplier information at supplier detail screen
        updateSupplierScreen.checkAllSupplierInformation(supInfo);

        // complete test
        supplierManagementScreen.completeTest();
    }

    @Test
    void SUP02_ScenarioForNonVNSupplier() throws IOException {
        testCaseId = "SUP02";
        /* CREATE SUPPLIER */
        // Create new supplier
        createSupplierScreen.createNewSupplier(false);

        // get supplier information
        SupplierInformation supInfo = createSupplierScreen.getSupplierInfo();

        // check supplier information at supplier management
        supplierManagementScreen.checkSupplierInformationAtSupplierManagementScreen(supInfo);

        // check supplier information at supplier detail screen
        updateSupplierScreen.checkAllSupplierInformation(supInfo);

        /* UPDATE SUPPLIER */
        // update information
        updateSupplierScreen.updateNewSupplier(true);

        // get supplier information
        supInfo = updateSupplierScreen.getSupInfo();

        // check supplier information at supplier management
        supplierManagementScreen.checkSupplierInformationAtSupplierManagementScreen(supInfo);

        // check supplier information at supplier detail screen
        updateSupplierScreen.checkAllSupplierInformation(supInfo);

        // complete test
        supplierManagementScreen.completeTest();
    }

    @Test
    void SUP03_ScenarioForDeletedSupplierAtSupplierManagement() {
        testCaseId = "SUP03";
        // init login information
        LoginInformation loginInfo = new LoginInformation();
        loginInfo.setEmail(ADMIN_ACCOUNT_THANG);
        loginInfo.setPassword(ADMIN_PASSWORD_THANG);

        // init supplier API
        SupplierAPI api = new SupplierAPI(loginInfo);

        // create and get supplier code
        String supCode = api.createSupplierAndGetSupplierCode();

        // delete supplier and verify supplier is deleted at supplier management screen
        supplierManagementScreen.deleteSupplier(supCode).verifySupplierIsDeleted(supCode);

        // complete test
        supplierManagementScreen.completeTest();
    }

    @Test
    void SUP04_ScenarioForDeletedSupplierAtSupplierDetail() {
        testCaseId = "SUP04";
        // init login information
        LoginInformation loginInfo = new LoginInformation();
        loginInfo.setEmail(ADMIN_ACCOUNT_THANG);
        loginInfo.setPassword(ADMIN_PASSWORD_THANG);

        // init supplier API
        SupplierAPI api = new SupplierAPI(loginInfo);

        // create and get supplier code
        String supCode = api.createSupplierAndGetSupplierCode();

        // open supplier at supplier management screen
        supplierManagementScreen.openSupplierDetailScreen(supCode);

        // delete supplier at supplier detail
        updateSupplierScreen.deleteSupplier();

        // verify supplier is deleted
        supplierManagementScreen.verifySupplierIsDeleted(supCode);

        // complete test
        supplierManagementScreen.completeTest();
    }

    @Test
    void SUP05_SearchSupplierAndCheckResult() {
        testCaseId = "SUP05";
        // search and check result
        supplierManagementScreen.searchAndVerifySearchResult();

        // complete test
        supplierManagementScreen.completeTest();
    }

    @AfterMethod
    void teardown() {
//        new UICommonMobile(driver).restartAppKeepLogin(appPackage, appActivity);
    }
}
