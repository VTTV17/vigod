package android;

import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIPreferences;
import api.dashboard.products.APIEditProduct;
import api.dashboard.services.CreateServiceAPI;
import api.dashboard.services.EditServiceAPI;
import api.dashboard.services.ServiceInfoAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.search.BuyerSearchDetailPage;
import pages.buyerapp.servicedetail.BuyerServiceDetail;
import pages.buyerapp.servicedetail.SelectLocationPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.screenshot.Screenshot;

import java.io.IOException;
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
    int serviceNormalId;
    String[] locations;
    String serviceDescription;
    int sellingPrice;
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "R5CR92R4K7V";
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
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
        selectLocationTitle = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.selectLocationTitle");
        ServiceInfo serviceInfo = callAPICreateService(false);
        serviceNormalCheck = serviceInfo.getServiceName();
        serviceNormalId = serviceInfo.getServiceId();
        sellingPrice = serviceInfo.getSellingPrice();
        locations = serviceInfo.getLocations();
        serviceDescription = serviceInfo.getServiceDescription();
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    @AfterMethod
    public void restartApp() throws IOException {
        new Screenshot().takeScreenshot(driver);
        ((AndroidDriver) driver).resetApp();
    }
    public ServiceInfo callAPICreateService(boolean enableListing){
        String serviceName = "Service automation "+ generator.generateString(6);
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] location = new String[]{"quan 1", "quan 2","quan 8","quan 9"};
        String[] times = new String[]{"21:11","22:10"};
        ServiceInfo serviceInfo = new CreateServiceAPI().createService(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,enableListing);
        return serviceInfo;
    }
    public void callAPIDeleteService(int serviceId){
        new ServiceInfoAPI().deleteService(serviceId);
    }
    @Test
    public void SD01_CheckNormalService() throws Exception {
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
        //call api create service
        ServiceInfo serviceInfo = callAPICreateService(false);
        int serviceId = serviceInfo.getServiceId();
        int sellingPrice = serviceInfo.getSellingPrice();
        String serviceName = serviceInfo.getServiceName();
        String serviceDescription = serviceInfo.getServiceDescription();
        String keyword = generator.generateString(6);
        String serviceNameUpdate = serviceName + " translator";
        serviceDescription = serviceDescription +" updated en.";
        new APIEditProduct().ediTranslation(serviceId,serviceDescription,serviceNameUpdate,"ENG");
        List<String> locationsEdit = new EditServiceAPI().editTranslationServiceLocations(serviceId);
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
        new APIPreferences().setUpGuestCheckout(false);
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
        new APIPreferences().setUpGuestCheckout(false);
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
        //Call api create service
        ServiceInfo createService = callAPICreateService(false);
        //Call api edit service
        String serviceName = createService.getServiceName()+" updated";
        String serviceDescription = serviceName + "update description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] locations = new String[]{"tan binh","phu nhan", "go vap"};
        String[] times = new String[]{"15:11","17:10"};
        EditServiceAPI editServiceAPI = new EditServiceAPI();
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
        //Call api edit service to deactive service
        EditServiceAPI editServiceAPI = new EditServiceAPI();
        editServiceAPI.setActiveStatus(false);
        editServiceAPI.updateService(serviceNormalId);
        //Check on SF when service deactive
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .verifySearchNotFound(serviceNormalCheck);
        //Call api edit service to active service
        editServiceAPI = new EditServiceAPI();
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
        callAPIDeleteService(serviceNormalId);
        //Check on SF when service deleted
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch(serviceNormalCheck)
                .verifySearchNotFound(serviceNormalCheck);
    }
}
