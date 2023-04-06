package android;

import api.dashboard.login.Login;
import api.dashboard.products.APIEditProduct;
import api.dashboard.services.CreateServiceAPI;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.search.BuyerSearchDetailPage;
import pages.buyerapp.search.BuyerSearchPage;
import pages.buyerapp.servicedetail.BuyerServiceDetail;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;

import java.net.URL;
import java.util.Map;

public class ServiceDetailTest {
    NavigationBar navigationBar;
    BuyerServiceDetail serviceDetail;
    WebDriver driver;
    DataGenerator generator;
    String userDb;
    String passDb;
    @BeforeClass
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "Android");
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "11.0");
        caps.setCapability("udid", "10.10.2.193:5555");
//        caps.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
//        caps.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        caps.setCapability("appPackage", "com.mediastep.shop0037");
        caps.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");

        caps.setCapability("noReset", "false");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        generator = new DataGenerator();
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage("VIE");
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    @AfterMethod
    public void restartApp(){
        ((AndroidDriver) driver).resetApp();
    }
    @Test
    public void SD01_checkNormalService() throws Exception {
        //call api create service
        new Login().loginToDashboardWithPhone("+84",userDb,passDb);
        String searchKeyword = generator.generateString(6) ;
        String serviceName = "Service automation "+ searchKeyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] location = new String[]{"thu duc","quan 1", "quan 2", "an 7"};
        String[] times = new String[]{"10:11","12:10"};
        boolean enableListing = false;
        new CreateServiceAPI().createServiceAPI(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,enableListing);
        //Check on buyer app
//        new BuyerGeneral(driver).waitInMiliSecond(2000);
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(searchKeyword)
                .verifySearchSuggestion(serviceName, String.valueOf(sellingPrice))
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceName)
                .verifyServicePrice(sellingPrice +" đ")
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(location)
                .verifyBookNowBtnDisplay();
    }
    @Test
    public void SD02_checkListingService() throws Exception {
        //call api create service
        new Login().loginToDashboardWithPhone("+84",userDb,passDb);
        String serviceName = "Service listing automation " + generator.generateString(6);
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = true;
        new CreateServiceAPI().createServiceAPI(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
        //Check on buyer app
//        new BuyerGeneral(driver).waitInMiliSecond(2000);
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceName)
                .verifySearchSuggestion(serviceName,"")
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceName)
                .verifyPriceNotDisplay()
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(location)
                .verifyContactNowBtnDisplay();
    }
    @Test
    public void SD03_checkServiceDetailAfterEditTranslation() throws Exception {
        //call api create service
        new Login().loginToDashboardWithPhone("+84",userDb,passDb);
        String keyword = generator.generateString(6);
        String serviceName = "Service listing automation " + keyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = false;
        Map serviceInfo = new CreateServiceAPI().createServiceAPI(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
        int serviceId = (int) serviceInfo.get("serviceId");
        serviceName = serviceName + " updated en";
        serviceDescription = serviceDescription +" updated en.";
        new APIEditProduct().ediTranslation(serviceId,serviceDescription,serviceName,"ENG");
        //Check on buyer app
//        new BuyerGeneral(driver).waitInMiliSecond(2000);
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                        .clickLanguageBtn()
                                .changeLanguage("ENG");
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(keyword)
                .verifySearchSuggestion(serviceName, String.valueOf(sellingPrice))
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceName)
                .verifyServiceDescription(serviceDescription);
    }
}
