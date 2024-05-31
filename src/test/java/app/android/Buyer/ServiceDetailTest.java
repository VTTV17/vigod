package app.android.Buyer;

import api.Seller.login.Login;
import api.Seller.sale_channel.onlineshop.APIPreferences;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.services.CreateServiceAPI;
import api.Seller.services.EditServiceAPI;
import api.Seller.services.ServiceInfoAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.*;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.search.BuyerSearchDetailPage;
import app.Buyer.servicedetail.BuyerServiceDetail;
import app.Buyer.servicedetail.SelectLocationPage;
import utilities.utils.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import static utilities.file.FileNameAndPath.FILE_CREATE_SERVICE_TCS;

public class ServiceDetailTest extends BaseTest {
    NavigationBar navigationBar;
    BuyerServiceDetail serviceDetail;
    DataGenerator generator;
    String userDb;
    String passDb;
    String serviceNormalCheck;
    String serviceListingCheck;
    String buyer;
    String passBuyer;
    String selectLocationTitle;
    int serviceNormalId;
    String[] locations;
    String serviceDescription;
    int sellingPrice;
    LoginInformation loginInformation;
    @BeforeClass
    public void setUp() throws Exception {
        String appPackage = "com.mediastep.shop0037";
        String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        driver=launchApp(appPackage,appActivity);
        generator = new DataGenerator();
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage("VIE");
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        loginInformation = new Login().setLoginInformation("+84",userDb,passDb).getLoginInformation();
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
        selectLocationTitle = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.selectLocationTitle");
        ServiceInfo serviceInfo = callAPICreateService(false);
        serviceNormalCheck = serviceInfo.getServiceName();
        serviceNormalId = serviceInfo.getServiceId();
        sellingPrice = serviceInfo.getSellingPrice();
        locations = serviceInfo.getLocations();
        serviceDescription = serviceInfo.getServiceDescription();
        tcsFileName = FILE_CREATE_SERVICE_TCS;
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    public AppiumDriver launchApp(String appPackage, String appActivity) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "R5CR92R4K7V");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability("noReset", "false");
        capabilities.setCapability("autoGrantPermissions","true");
        String url = "http://127.0.0.1:4723/wd/hub";
        try {
            return new InitAppiumDriver().getAppiumDriver(capabilities, url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    @AfterMethod
    public void restartApp(ITestResult result) throws IOException {
        super.writeResult(result);
        ((AndroidDriver) driver).resetApp();
    }
    public ServiceInfo callAPICreateService(boolean enableListing){
        String serviceName = "Service automation "+ generator.generateString(6);
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] location = new String[]{"quan 1", "quan 2","quan 8","quan 9"};
        String[] times = new String[]{"21:11","22:10"};
        ServiceInfo serviceInfo = new CreateServiceAPI(loginInformation).createService(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,enableListing);
        return serviceInfo;
    }
    public void callAPIDeleteService(int serviceId){
        new ServiceInfoAPI(loginInformation).deleteService(serviceId);
    }
    @Test
    public void SD01_CheckNormalService() throws Exception {
        testCaseId = "SD01";
        //Check on buyer app
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .verifySearchSuggestion(serviceNormalCheck, String.valueOf(sellingPrice))
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceNormalCheck)
                .verifyServicePrice(sellingPrice +" đ")
                .verifyLocationNumber(locations.length)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(locations)
                .verifyBookNowBtnDisplay()
                .verifyAddToCartBtnShow();
    }
    @Test
    public void SD02_CheckListingService() throws Exception {
        testCaseId = "SD02";
        //call api create service
        ServiceInfo serviceInfo = callAPICreateService(true);
        serviceListingCheck = serviceInfo.getServiceName();
        String[] locations = serviceInfo.getLocations();
        String serviceDescription = serviceInfo.getServiceDescription();
        //Check on buyer app
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceListingCheck)
                .verifySearchSuggestion(serviceListingCheck,"")
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceListingCheck)
                .verifyPriceNotDisplay()
                .verifyLocationNumber(locations.length)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(locations)
                .verifyContactNowBtnDisplay()
                .verifyAddToCartBtnNotShow();
    }
    @Test
    public void SD03_CheckServiceDetailAfterEditTranslation() throws Exception {
        testCaseId = "SD03";
        //call api create service
        ServiceInfo serviceInfo = callAPICreateService(false);
        int serviceId = serviceInfo.getServiceId();
        int sellingPrice = serviceInfo.getSellingPrice();
        String serviceName = serviceInfo.getServiceName();
        String serviceDescription = serviceInfo.getServiceDescription();
        String keyword = generator.generateString(6);
        String serviceNameUpdate = serviceName + " translator";
        serviceDescription = serviceDescription +" updated en.";
        new APIEditProduct(loginInformation).ediTranslation(serviceId,serviceDescription,serviceNameUpdate,"ENG");
        List<String> locationsEdit = new EditServiceAPI(loginInformation).editTranslationServiceLocations(serviceId);
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
                .inputKeywordToSearch(serviceNameUpdate)
                .verifySearchSuggestion(serviceNameUpdate, String.valueOf(sellingPrice))
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceNameUpdate)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(locationEditArr);
        callAPIDeleteService(serviceId);
    }
    @Test
    public void SD04_CheckGuestTapOnBookNow(){
        testCaseId = "SD04";
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
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
    public void SD05_CheckCustomerTapOnBookNow(){
        testCaseId = "SD05";
        //call api if serviceName = null
        if(serviceNormalCheck == null){
            ServiceInfo serviceInfo = callAPICreateService(false);
            serviceNormalCheck = serviceInfo.getServiceName();
            serviceNormalId = serviceInfo.getServiceId();
            sellingPrice = serviceInfo.getSellingPrice();
            locations = serviceInfo.getLocations();
            serviceDescription = serviceInfo.getServiceDescription();
        }

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
    public void SD06_CheckTapOnContactNow(){
        testCaseId = "SD06";
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceListingCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapOnContactNow()
                .verifyContactPopUpShow();
    }
    @Test
    public void SD07_CheckTextByLanguage() throws Exception {
        testCaseId = "SD07";
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyTextByLanguage()
                .verifyTextBookNowOrContactNowBtn(false);
        //
        new BuyerGeneral(driver).clickOnBackIcon();
        new BuyerSearchDetailPage(driver).tapCancelSearch();
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceListingCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyTextByLanguage()
                .verifyTextBookNowOrContactNowBtn(true);
    }
    @Test
    public void SD08_CheckSimilarSectionDisplay(){
        testCaseId = "SD08";
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifySimilarSectionDisplay();
    }
    @Test
    public void SD09_CheckWhenTapOnDescriptionLocationSimilar(){
        testCaseId = "SD09";
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapSimilarTab()
                .verifySimilarSectionShow()
                .tapDescriptionTab()
                .verifyDescriptionSectionShow()
                .tapLocationsTab()
                .verifyLocationSectionShow();
    }
    @Test
    public void SD10_CheckGuestTapAddToCart(){
        testCaseId = "SD10";
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapAddToCart()
                .verifyRequireLoginPopUpShow();
    }
    @Test
    public void SD11_CheckCustomerTapAddToCart(){
        testCaseId = "SD11";
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(buyer,passBuyer);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.tapAddToCart();
        new SelectLocationPage(driver).verifyPageTitle(selectLocationTitle);
    }
    @Test
    public void SD12_CheckServiceInfoAfterUpdate() throws Exception {
        testCaseId = "SD12";
        //Call api create service
        ServiceInfo createService = callAPICreateService(false);
        //Call api edit service
        String serviceName = createService.getServiceName()+" updated";
        String serviceDescription = serviceName + "update description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] locations = new String[]{"tan binh","phu nhan", "go vap"};
        String[] times = new String[]{"15:11","17:10"};
        EditServiceAPI editServiceAPI = new EditServiceAPI(loginInformation);
        editServiceAPI.setServiceNameEdit(serviceName);
        editServiceAPI.setServiceDescriptionEdit(serviceDescription);
        editServiceAPI.setListingPriceEdit(listingPrice);
        editServiceAPI.setSellingPriceEdit(sellingPrice);
        editServiceAPI.setLocations(locations);
        editServiceAPI.setTimes(times);
        editServiceAPI.setActiveStatus(true);
        editServiceAPI.updateService(createService.getServiceId());
        //Go to app to check
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceName)
                .verifySearchSuggestion(serviceName, String.valueOf(sellingPrice))
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceName)
                .verifyServicePrice(sellingPrice +" đ")
                .verifyLocationNumber(locations.length)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(locations);
        callAPIDeleteService(createService.getServiceId());
    }
    @Test
    public void SD13_CheckServiceAfterDeactiveActive() throws JsonProcessingException {
        testCaseId = "SD13";
        //Call api edit service to deactive service
        EditServiceAPI editServiceAPI = new EditServiceAPI(loginInformation);
        editServiceAPI.setActiveStatus(false);
        editServiceAPI.updateService(serviceNormalId);
        //Check on SF when service deactive
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .verifySearchNotFound(serviceNormalCheck);
        //Call api edit service to active service
        editServiceAPI = new EditServiceAPI(loginInformation);
        editServiceAPI.setActiveStatus(true);
        editServiceAPI.updateService(serviceNormalId);
        //Check on SF when service active
        new BuyerSearchDetailPage(driver).inputKeywordToSearch(serviceNormalCheck)
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName(serviceNormalCheck);
    }
    @Test
    public void SD14_CheckServiceAfterDelete(){
        testCaseId = "SD14";
        callAPIDeleteService(serviceNormalId);
        //Check on SF when service deleted
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .verifySearchNotFound(serviceNormalCheck);
    }
}
