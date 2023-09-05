import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.ProductCollection;
import api.dashboard.services.CreateServiceAPI;
import api.dashboard.services.ServiceCollectionAPI;

import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.dashboard.login.LoginPage;
import pages.dashboard.service.servicecollections.CreateServiceCollection;
import pages.storefront.header.HeaderSF;
import pages.storefront.productcollection.ProductCollectionSF;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.MenuItemType;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceCollectionsInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.ADMIN_CREATE_NEW_SHOP_PASSWORD;
import static utilities.links.Links.SF_ShopVi;

public class ServiceCollectionTest extends BaseTest{
    String userNameDb;
    String passwordDb;
    String domainSF;
    LoginInformation loginInformation;
    String userName_goWeb;
    String userName_goApp;
    String userName_goPOS;
    String userName_goSocial;
    String userName_GoLead;
    String passwordCheckPermission;
    String languageDashboard;
    String languageSF;
    LoginPage loginDashboard;
    CreateServiceCollection createServiceCollection;
    Login loginAPI;
    String token;
    String storeId;
    ServiceCollectionAPI serviceCollectionAPI;
    ProductCollectionSF productCollectionSF;
    APIMenus apiMenus;
    String[] serviceList = {};
    List<Integer> serviceIdList = new ArrayList<>();
    @BeforeClass
    public void getData() {
        userNameDb = ADMIN_SHOP_VI_USERNAME;
        passwordDb = ADMIN_SHOP_VI_PASSWORD;
        domainSF = SF_ShopVi;
        loginInformation = new Login().setLoginInformation("+84",userNameDb,passwordDb).getLoginInformation();
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        generate = new DataGenerator();
        languageDashboard = language;
        languageSF = language;
        tcsFileName = "";
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, "false");
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
//        if (driver != null) driver.quit();
    }
    @AfterClass
    public void callAPIDeleteData(){
        //Delete Service
        ProductCollection collectionAPI = new ProductCollection(loginInformation);
        for (int serviceId :serviceIdList) {
            collectionAPI.deleteCollection(serviceId);
        }
    }
    public CreateServiceCollection loginAndNavigateToCreateServiceCollection() throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        createServiceCollection = new CreateServiceCollection(driver);
        return createServiceCollection.navigate(languageDashboard);
    }
    public ProductCollectionSF navigateSFAndGoToCollectionPage(String collectionName) {
        new UICommonAction(driver).sleepInMiliSecond(1000);
        pages.storefront.login.LoginPage loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        headerSF. clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
        return new ProductCollectionSF(driver);
    }
    public void callLoginAPI() {
        loginAPI = new Login();
        loginInformation = loginAPI.setLoginInformation("+84", userNameDb, passwordDb).getLoginInformation();
        LoginDashboardInfo loginInfo = new Login().getInfo(loginInformation);
        token = loginInfo.getAccessToken();
        storeId = String.valueOf(loginInfo.getStoreID());
    }
    public void callCreateMenuItemParentAPI(String collectionName) {
        callLoginAPI();
        serviceCollectionAPI = new ServiceCollectionAPI(loginInformation);
        int collectIDNewest = serviceCollectionAPI.getNewestCollectionID();
        apiMenus = new APIMenus(loginInformation);
        apiMenus.CreateMenuItemParent(collectIDNewest, collectionName, MenuItemType.COLLECTION_SERVICE);
    }
    public String[] callAPICreateService(int quantity){
        String[] serviceList = new String[quantity];
        for (int i=0;i<quantity;i++){
            String serviceName = "service automation "+ generate.generateString(6).toLowerCase();
            String serviceDescription = serviceName + " description";
            int listingPrice = Integer.parseInt("3"+generate.generateNumber(5));
            int sellingPrice = Integer.parseInt("2"+generate.generateNumber(5));
            String[] location = new String[]{"quan 1", "quan 2","quan 8","quan 9"};
            String[] times = new String[]{"21:11","22:10"};
            ServiceInfo serviceInfo = new CreateServiceAPI(loginInformation).createService(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,false);
            serviceList[i]=serviceInfo.getServiceName();
            serviceIdList.add(serviceInfo.getServiceId());
        }
        return serviceList ;
    }
    public ServiceInfo callAPICreateService(String serviceName){
        String serviceDescription = serviceName + " description";
        int listingPrice = Integer.parseInt("3"+generate.generateNumber(5));
        int sellingPrice = Integer.parseInt("2"+generate.generateNumber(5));
        String[] location = new String[]{"quan 1", "quan 2","quan 8","quan 9"};
        String[] times = new String[]{"21:11","22:10"};
        ServiceInfo serviceInfo = new CreateServiceAPI(loginInformation).createService(serviceName,serviceDescription,listingPrice,sellingPrice,location,times,false);
        serviceIdList.add(serviceInfo.getServiceId());
        return serviceInfo ;
    }
    @Test
    public void SC01_CreateManualServiceCollectionHasNoService() throws Exception {
        loginAndNavigateToCreateServiceCollection();
        createServiceCollection = new CreateServiceCollection(driver);
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        String serviceCollectionName = "Collection has no service " + generate.generateString(10);
        //Set collection info
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.MANUAL_OPTION);
        serviceCollectionsInfo.setServiceList(serviceList);
        createServiceCollection.createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(),Constant.SERVICE_TYPE,Constant.MANUALLY_MODE,"0");
        callCreateMenuItemParentAPI(serviceCollectionName);
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(serviceCollectionName)
                .verifyCollectionEmpty();
    }
    @Test
    public void SC02_CreateManuaServiceCollectionHasAService() throws Exception {
        loginAndNavigateToCreateServiceCollection();
        createServiceCollection = new CreateServiceCollection(driver);
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        String serviceCollectionName = "Collection has a service " + generate.generateString(10);
        serviceList = callAPICreateService(1);
        //Set collection info
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.MANUAL_OPTION);
        serviceCollectionsInfo.setServiceList(serviceList);
        createServiceCollection.createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(),Constant.SERVICE_TYPE,Constant.MANUALLY_MODE,"1");
        callCreateMenuItemParentAPI(serviceCollectionName);
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(serviceCollectionName).
                verifyProductNameList(new ProductCollectionSF(driver).getProductNameList(), Arrays.stream(serviceList).toList());
    }
    @Test
    public void SC03_CreateManualServiceCollectionWithServiceHasPriority() throws Exception {
        loginAndNavigateToCreateServiceCollection();
        createServiceCollection = new CreateServiceCollection(driver);
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        String serviceCollectionName = "Collection service has priority " + generate.generateString(10);
        serviceList = callAPICreateService(5);
        //Set collection info
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.MANUAL_OPTION);
        serviceCollectionsInfo.setServiceList(serviceList);
        serviceCollectionsInfo.setInputPriority(true);
        serviceCollectionsInfo.setSetPriorityForAll(true);
        createServiceCollection.createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(),Constant.SERVICE_TYPE,Constant.MANUALLY_MODE,String.valueOf(serviceList.length));
        callCreateMenuItemParentAPI(serviceCollectionName);
        serviceCollectionAPI = new ServiceCollectionAPI(loginInformation);
        int collectIDNewest = serviceCollectionAPI.getNewestCollectionID();
        //Check service collection on SF, dùng lại của product
        navigateSFAndGoToCollectionPage(serviceCollectionName);
        List<String> serviceListExpected = CreateServiceCollection.sortServiceListByPriorityAndLastUpdatedDate(loginInformation, CreateServiceCollection.servicePriorityMap, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),serviceListExpected);
    }
    @Test
    public void SC04_CreateManualServiceCollectionWithAServiceAndSEO() throws Exception {
        loginAndNavigateToCreateServiceCollection();
        createServiceCollection = new CreateServiceCollection(driver);
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        String radomText = generate.generateString(10);
        String serviceCollectionName = "Collection service has priority " + radomText;
        serviceList = callAPICreateService(1);
        String SEOTitle = "SEO title " + radomText;
        String SEODescription = "SEO description " + radomText;
        String SEOKeyword = "SEO keyword " + radomText;
        String SEOUrl = "collectionseourl" + radomText;
        //Set collection info
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.MANUAL_OPTION);
        serviceCollectionsInfo.setServiceList(serviceList);
        serviceCollectionsInfo.setInputSEO(true);
        serviceCollectionsInfo.setSEOTitle(SEOTitle);
        serviceCollectionsInfo.setSEODescription(SEODescription);
        serviceCollectionsInfo.setSEOKeywords(SEOKeyword);
        serviceCollectionsInfo.setURLLink(SEOUrl);
        createServiceCollection.createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(),Constant.SERVICE_TYPE,Constant.MANUALLY_MODE,String.valueOf(serviceList.length));
        callCreateMenuItemParentAPI(serviceCollectionName);
        //Check service collection on SF, dùng lại của product

        navigateSFAndGoToCollectionPage(serviceCollectionName)
                .verifySEOInfo(SEOTitle,SEODescription,SEOKeyword,serviceCollectionName)
                .verifyProductNameList(new ProductCollectionSF(driver).getProductNameList(), Arrays.stream(serviceList).toList());
    }
    @Test
    public void SC05_CreateAutomatedServiceCollection_TitleContainKeyword() throws Exception {
        loginAndNavigateToCreateServiceCollection();
        createServiceCollection = new CreateServiceCollection(driver);
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        String randomText =  generate.generateString(10);
        String serviceName = "Service contain keyword "+ randomText;
        String serviceCollectionName = "Collection service has priority " + randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.CONTAINS+"-"+randomText};
        //Set collection info
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.AUTOMATED_OPTION);
        serviceCollectionsInfo.setCollectionType(Constant.ALL_CONDITION);
        serviceCollectionsInfo.setAutomatedConditions(condition);
        createServiceCollection.createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(),Constant.SERVICE_TYPE,Constant.MANUALLY_MODE,String.valueOf(serviceList.length));
        callCreateMenuItemParentAPI(serviceCollectionName);
    }

}
