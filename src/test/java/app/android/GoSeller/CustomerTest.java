package app.android.GoSeller;

import app.GoSeller.account.SellerAccount;
import app.GoSeller.customer.cru_customer.ViewCreateUpdateCustomer;
import app.GoSeller.customer.customer_list.CustomerListScreen;
import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import app.GoSeller.product.SellerCreateCollection;
import app.GoSeller.product.SellerProductManagement;
import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.account.AccountTest;
import utilities.commons.UICommonMobile;
import utilities.driver.InitAndroidDriver;
import utilities.model.dashboard.customer.create.UICreateCustomerData;
import utilities.utils.PropertiesUtil;

import static utilities.account.AccountTest.ANDROID_GOBUYER_APPNAME_SHOPVI;
import static utilities.account.AccountTest.ANDROID_GoSELLER_APP;
import static utilities.environment.goBUYEREnvironment.goBUYERBundleId_ShopVi;
import static utilities.file.FileNameAndPath.getDirectorySlash;

public class CustomerTest extends BaseTest{
    String appPackage = goBUYERBundleId_ShopVi;
    String udid = PropertiesUtil.getEnvironmentData("udidAndroidVi");
    String apkFile = ANDROID_GoSELLER_APP;
    WebDriver driver;
    String userDb;
    String passDb;
    @BeforeClass
    public void setUp() throws Exception {
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
    }
    @BeforeMethod
    @SneakyThrows
    public void launchApp() {
        driver = new InitAndroidDriver().getAndroidDriver(udid, System.getProperty("user.dir") + getDirectorySlash("src") +
                getDirectorySlash("main") +   getDirectorySlash("resources") + getDirectorySlash("app") + apkFile);
        new UICommonMobile(driver).waitSplashScreenLoaded();
        loginSellerApp();
        changeLaguage();
    }
    @AfterMethod
    public void restartApp(ITestResult result) {
        ((AndroidDriver) driver).removeApp(appPackage);
        if (driver != null) driver.quit();
    }
    public HomePage loginSellerApp(){
        return new LoginPage(driver).performLogin(userDb,passDb);
    }
    public ViewCreateUpdateCustomer goToCreateCustomer(){
        new HomePage(driver).navigateToPage("AddNewCustomer");
        return new ViewCreateUpdateCustomer(driver);
    }
    public CustomerListScreen goToCustomerList(){
        new HomePage(driver).navigateToPage("Customer");
        return new CustomerListScreen(driver);
    }
    public HomePage changeLaguage(){
        return new SellerAccount(driver).changeLanguage(language);
    }
    @Test
    public void createCustomerInVietNam(){
        UICreateCustomerData customerInfoExpected = goToCreateCustomer().createCustomer(true);
        UICreateCustomerData customerInfoAfterCreate = new ViewCreateUpdateCustomer(driver).
                verifyCreateSuccessMessage().goToNewestCustomer()
                .getCustomerInfo(true);
        new ViewCreateUpdateCustomer(driver).verifyCustomerInfo(customerInfoExpected, customerInfoAfterCreate);
    }
    @Test
    public void createCustomerNonVietNam(){
        UICreateCustomerData customerInfoExpected = goToCreateCustomer().createCustomer(false);
        UICreateCustomerData customerInfoAfterCreate = new ViewCreateUpdateCustomer(driver).
                verifyCreateSuccessMessage().goToNewestCustomer()
                .getCustomerInfo(false);
        new ViewCreateUpdateCustomer(driver).verifyCustomerInfo(customerInfoExpected, customerInfoAfterCreate);
    }
    @Test
    public void updateCustomerInVietNam(){
        goToCustomerList();
        UICreateCustomerData customerInfoExpected = new CustomerListScreen(driver).goToNewestCustomer().
                editCustomer(true);
        UICreateCustomerData customerInfoAfterCreate = new ViewCreateUpdateCustomer(driver).
                verifyUpdateSuccessMessage().tapBackIcon().goToNewestCustomer()
                .getCustomerInfo(true);
        new ViewCreateUpdateCustomer(driver).verifyCustomerInfo(customerInfoExpected, customerInfoAfterCreate);
    }
    @Test
    public void updateCustomerNonVietNam(){
        goToCustomerList();
        UICreateCustomerData customerInfoExpected = new CustomerListScreen(driver).goToNewestCustomer().
                editCustomer(false);
        UICreateCustomerData customerInfoAfterCreate = new ViewCreateUpdateCustomer(driver).
                verifyUpdateSuccessMessage().tapBackIcon().goToNewestCustomer()
                .getCustomerInfo(false);
        new ViewCreateUpdateCustomer(driver).verifyCustomerInfo(customerInfoExpected, customerInfoAfterCreate);
    }
}
