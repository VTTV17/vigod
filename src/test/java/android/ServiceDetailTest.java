package android;

import api.dashboard.login.Login;
import api.dashboard.products.APIEditProduct;
import api.dashboard.services.CreateServiceAPI;
import api.dashboard.services.EditServiceAPI;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.servicedetail.BuyerServiceDetail;
import pages.buyerapp.servicedetail.SelectLocationPage;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;

import java.util.List;
import java.util.Map;
public class ServiceDetailTest {
    NavigationBar navigationBar;
    BuyerServiceDetail serviceDetail;
    WebDriver driver;
    DataGenerator generator;
    String userDb;
    String passDb;
    String serviceNormalCheck;
    String serviceListingCheck;
    String buyer;
    String passBuyer;
    String selectLocationTitle;
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "10.10.2.193:5555";
        String platformName = "Android";
        String appPackage = "com.mediastep.shop0037";
        String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        generator = new DataGenerator();
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage("VIE");
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        new Login().loginToDashboardWithPhone("+84",userDb,passDb);
        driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
        serviceNormalCheck = "Service automation to check";
        serviceListingCheck = "Service listing automation EIKpJC";
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
        selectLocationTitle = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.selectLocationTitle");
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
        String searchKeyword = generator.generateString(6) ;
        String serviceName = "Service automation "+ searchKeyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] location = new String[]{"thu duc","quan 1", "quan 2", "an 7"};
        String[] times = new String[]{"10:11","12:10"};
        boolean enableListing = false;
        new CreateServiceAPI().createServiceAPI(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,enableListing);
        serviceNormalCheck = serviceName;
        //Check on buyer app
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
        String serviceName = "Service listing automation " + generator.generateString(6);
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = true;
        new CreateServiceAPI().createServiceAPI(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
        //Check on buyer app
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
        String keyword = generator.generateString(6);
        String serviceName = "Service automation " + keyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = false;
        Map serviceInfo = new CreateServiceAPI().createServiceAPI(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
        serviceListingCheck = serviceName;
        int serviceId = (int) serviceInfo.get("serviceId");
        serviceName = serviceName + " updated en";
        serviceDescription = serviceDescription +" updated en.";
        new APIEditProduct().ediTranslation(serviceId,serviceDescription,serviceName,"ENG");
        List<String> locationsEdit = new EditServiceAPI().editTranslationServiceLocations(String.valueOf(serviceId));
        String[] locationEditArr = new String[locationsEdit.size()];
        locationsEdit.toArray(locationEditArr);
        //Check on buyer app
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
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(locationEditArr);
    }
    @Test
    public void SD04_checkGuestTapOnBookNow(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapOnBookingNow()
                .verifyRequireLoginPopUpShow();
    }
    @Test
    public void SD05_checkCustomerTapOnBookNow(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                        .clickLoginBtn()
                                .performLogin(buyer,passBuyer);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapOnBookingNow();
        new SelectLocationPage(driver).verifyPageTitle(selectLocationTitle);
    }
    @Test
    public void SD06_checkTapOnContactNow(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceListingCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapOnContactNow()
                .verifyContactPopUpShow();
    }
}
