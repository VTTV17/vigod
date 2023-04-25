package android;

import api.dashboard.login.Login;
import api.dashboard.products.APIEditProduct;
import api.dashboard.services.CreateServiceAPI;
import api.dashboard.services.EditServiceAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.search.BuyerSearchDetailPage;
import pages.buyerapp.servicedetail.BuyerServiceDetail;
import pages.buyerapp.servicedetail.SelectLocationPage;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.services.ServiceInfo;

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
    String serviceNormalId;
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
        serviceNormalCheck = "Service automation to check";
        serviceListingCheck = "Service listing automation EIKpJC";
        serviceNormalId = "1067881";
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
        selectLocationTitle = PropertiesUtil.getPropertiesValueBySFLang("serviceDetail.selectLocationTitle");
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
//    @AfterMethod
//    public void restartApp(){
//        ((AndroidDriver) driver).resetApp();
//    }
    @Test
    public void SD01_CheckNormalService() throws Exception {
        //call api create service
        String searchKeyword = generator.generateString(6) ;
        String serviceName = "Service automation "+ searchKeyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generator.generateNumber(5));
        String[] location = new String[]{"thu duc","quan 1", "quan 2", "an 7","quan 2","quan 6","quan 8","quan 9"};
        String[] times = new String[]{"10:11","12:10"};
        boolean enableListing = false;
        ServiceInfo serviceInfo = new CreateServiceAPI().createService(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,enableListing);
        serviceNormalCheck = serviceName;
        serviceNormalId = String.valueOf(serviceInfo.getServiceId());
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
                .verifyLocationNumber(location.length)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(location)
                .verifyBookNowBtnDisplay()
                .verifyAddToCartBtnShow();
    }
    @Test
    public void SD02_CheckListingService() throws Exception {
        //call api create service
        String serviceName = "Service listing automation " + generator.generateString(6);
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = true;
        new CreateServiceAPI().createService(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
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
                .verifyLocationNumber(location.length)
                .verifyServiceDescription(serviceDescription)
                .verifyLocations(location)
                .verifyContactNowBtnDisplay()
                .verifyAddToCartBtnNotShow();
    }
    @Test
    public void SD03_CheckServiceDetailAfterEditTranslation() throws Exception {
        //call api create service
        String keyword = generator.generateString(6);
        String serviceName = "Service automation " + keyword;
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3" + generator.generateNumber(5));
        int sellingPrice = Integer.parseInt("2" + generator.generateNumber(5));
        String[] location = new String[]{"thu duc", "quan 1", "quan 2"};
        String[] times = new String[]{"10:11", "12:10"};
        boolean enableListing = false;
        ServiceInfo serviceInfo = new CreateServiceAPI().createService(serviceName, serviceDescription, listingPrice, sellingPrice, location, times, enableListing);
        serviceListingCheck = serviceInfo.getServiceName();
        int serviceId = (int) serviceInfo.getServiceId();
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
    public void SD04_CheckGuestTapOnBookNow(){
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
                .verifySimilarectionShow()
                .tapDescriptionTab()
                .verifyDescriptionSectionShow()
                .tapLocationsTab()
                .verifyLocationSectionShow();
    }
    @Test
    public void SD10_CheckGuestTapAddToCart(){
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
        //Call api edit service
        String serviceName = "Edit Service automation "+ generator.generateString(6);
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
        editServiceAPI.updateService(serviceNormalId);
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
    }
    @Test
    public void SD13_CheckServiceAfterDeactiveActive() throws JsonProcessingException {
        //Call api edit service to deactive service
        EditServiceAPI editServiceAPI = new EditServiceAPI();
        editServiceAPI.setActiveStatus(false);
        editServiceAPI.updateService("1063035");
        //Check on SF when service deactive
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch("Service automation jVIBUx")
                .verifySearchNotFound("Service automation jVIBUx");
        //Call api edit service to active service
        editServiceAPI = new EditServiceAPI();
        editServiceAPI.setActiveStatus(true);
        editServiceAPI.updateService("1063035");
        //Check on SF when service active
        new BuyerSearchDetailPage(driver).inputKeywordToSearch("Service automation jVIBUx")
                .tapSearchSuggestion();
        serviceDetail = new BuyerServiceDetail(driver);
        serviceDetail.verifyServiceName("Service automation jVIBUx");
    }
}
