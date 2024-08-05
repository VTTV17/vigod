package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.sale_channel.onlineshop.APIMenus;
import api.Seller.products.product_collections.APIProductCollection;
import api.Seller.services.CreateServiceAPI;
import api.Seller.services.EditServiceAPI;
import api.Seller.services.ServiceCollectionAPI;
import api.Seller.services.ServiceInfoAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;

import utilities.api.API;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.service.servicecollections.CreateServiceCollection;
import web.Dashboard.service.servicecollections.EditServiceCollection;
import web.Dashboard.service.servicecollections.ServiceCollectionManagement;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.productcollection.ProductCollectionSF;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.MenuItemType;
import utilities.file.FileNameAndPath;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceCollectionsInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utilities.constant.Constant.*;
import static utilities.account.AccountTest.*;
import static utilities.links.Links.*;

public class ServiceCollectionTest extends BaseTest {
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
    EditServiceCollection editServiceCollection;
    String serviceCollectionNameEdit;
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
        tcsFileName = FileNameAndPath.FILE_SERVICE_COLLECTION_TCS;
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, "false");
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        if (driver != null) driver.quit();
    }
    @AfterClass
    public void callAPIDeleteData(){
        //Delete Service
        ServiceInfoAPI serviceInfoAPI = new ServiceInfoAPI(loginInformation);
        List<Integer> serviceIdList = serviceInfoAPI.getServiceIdList(20);
        for (int serviceId :serviceIdList) {
            serviceInfoAPI.deleteService(serviceId);
        }
        //Delete Collection
        callDeleteCollectionAPI();
    }
    public void callDeleteCollectionAPI() {
        List<Integer> serviceCollectionToDetele = new ServiceCollectionAPI(loginInformation).getListServiceCollectionId(10);
        APIProductCollection productCollection = new APIProductCollection(loginInformation);
        for (int id:serviceCollectionToDetele){
            productCollection.deleteCollection(String.valueOf(id));
        }
    }
    public CreateServiceCollection loginAndNavigateToCreateServiceCollection() throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        new HomePage(driver).selectLanguage(languageDashboard);
        createServiceCollection = new CreateServiceCollection(driver);
        return createServiceCollection.navigate(languageDashboard);
    }
    public EditServiceCollection loginAndNavigateToEditServiceCollection(String collectionName) throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        new HomePage(driver).selectLanguage(languageDashboard);
        editServiceCollection = new EditServiceCollection(driver);
        return editServiceCollection.navigateEditServiceCollection(collectionName);
    }
    public ProductCollectionSF navigateSFAndGoToCollectionPage(String collectionName) {
        web.StoreFront.login.LoginPage loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF);
        headerSF.clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
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
        return serviceInfo ;
    }
    public void createAutomationCollectionAndVerify(String collectionName, String conditionType, String...conditions) throws Exception {
        List<String> productExpectedList;
        callLoginAPI();
        CreateServiceCollection createServiceCollection = new CreateServiceCollection(driver);
        if (conditions.length > 1) {
            productExpectedList = createServiceCollection.servicesBelongCollectionExpected_MultipleCondition(loginInformation, conditionType, conditions);
        } else if (conditions.length == 1) {
            productExpectedList = createServiceCollection.servicesBelongCollectionExpected_OneCondition(loginInformation,conditions[0]);
        } else {
            throw new Exception("Missing conditions");
        }
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        serviceCollectionsInfo.setCollectionName(collectionName);
        serviceCollectionsInfo.setCollectionType(Constant.AUTOMATED_OPTION);
        serviceCollectionsInfo.setConditionType(conditionType);
        serviceCollectionsInfo.setAutomatedConditions(conditions);
        System.out.println("Product: " + productExpectedList);
        loginAndNavigateToCreateServiceCollection()
                .createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, String.valueOf(productExpectedList.size()));
        callCreateMenuItemParentAPI(serviceCollectionsInfo.getCollectionName());
        //Check on SF
        navigateSFAndGoToCollectionPage(serviceCollectionsInfo.getCollectionName());
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(productExpectedList.size()/ PAGE_SIZE_SF_COLLECTION +1),productExpectedList);
    }
    public void editAutomationCollectionAndVerify(String collectionName, String conditionType, String...conditions) throws Exception {
        String[] allCondition = loginAndNavigateToEditServiceCollection(collectionName)
                .editAutomationCollection(conditionType, conditions);
        List<String> productExpectedList;
        callLoginAPI();
        CreateServiceCollection createServiceCollection = new CreateServiceCollection(driver);
        if (allCondition.length > 1) {
            productExpectedList = createServiceCollection.servicesBelongCollectionExpected_MultipleCondition(loginInformation, conditionType, allCondition);
        } else if (allCondition.length == 1) {
            productExpectedList = createServiceCollection.servicesBelongCollectionExpected_OneCondition(loginInformation,allCondition[0]);
        } else {
            throw new Exception("Missing conditions");
        }
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        serviceCollectionsInfo.setCollectionName(collectionName);
        serviceCollectionsInfo.setCollectionType(Constant.AUTOMATED_OPTION);
        serviceCollectionsInfo.setConditionType(conditionType);
        serviceCollectionsInfo.setAutomatedConditions(conditions);
        System.out.println("Product: " + productExpectedList);
        new ServiceCollectionManagement(driver)
                .refreshPage()
                .searchCollection(collectionName)
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, String.valueOf(productExpectedList.size()));
        callCreateMenuItemParentAPI(serviceCollectionsInfo.getCollectionName());
        //Check on SF
        navigateSFAndGoToCollectionPage(serviceCollectionsInfo.getCollectionName());
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(productExpectedList.size()/ PAGE_SIZE_SF_COLLECTION +1),productExpectedList);
    }
    public void navigateToSFAndVerifyCollectionPage(String collectName, boolean hasSetPriority) throws Exception {
        serviceCollectionAPI = new ServiceCollectionAPI(loginInformation);
        int collectIDNewest = serviceCollectionAPI.getNewestCollectionID();
        //Check service collection on SF, dùng lại của product
        navigateSFAndGoToCollectionPage(collectName);
        List<String> serviceListExpected;
        if(hasSetPriority) {
            serviceListExpected = CreateServiceCollection.sortServiceListByPriorityAndLastUpdatedDate(loginInformation, CreateServiceCollection.servicePriorityMap, collectIDNewest);
        }else {
            serviceListExpected = new ServiceInfoAPI(loginInformation).getServiceListInCollectionByLastModifeDate(collectIDNewest);
        }
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(serviceListExpected.size()/PAGE_SIZE_SF_COLLECTION +1),serviceListExpected);
    }
    public void checkPlanPermission(String userName, boolean hasPermission) throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userName, passwordCheckPermission);
        new HomePage(driver).selectLanguage(languageDashboard);
        ServiceCollectionManagement serviceCollectionManagement = new ServiceCollectionManagement(driver);
        serviceCollectionManagement.navigate();
        if(hasPermission){
            serviceCollectionManagement.clickCreateServiceCollection();
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
        }else {
            serviceCollectionManagement.checkSalePitchWhenNoPermision();
        }
        new HomePage(driver).clickLogout();
    }
    @Test(priority = 1)
    public void SC01_CreateManualServiceCollectionHasNoService() throws Exception {
        testCaseId = "SC01";
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
        serviceCollectionNameEdit = serviceCollectionName;
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(serviceCollectionName)
                .verifyCollectionEmpty();
    }
    @Test(dependsOnMethods = "SC01_CreateManualServiceCollectionHasNoService",priority = 2)
//    @Test
    public void SC02_AddServiceToExistingManualCollection() throws Exception {
        testCaseId = "SC02";
        serviceList = callAPICreateService(1);
//        serviceCollectionNameEdit="Collection has no service mhbNdmzpni";
        loginAndNavigateToEditServiceCollection(serviceCollectionNameEdit)
                .editServiceListInManualCollection(serviceList,false,false)
                .refreshPageUtilCollectUpdate(String.valueOf(serviceList.length))
                .searchCollection(serviceCollectionNameEdit)
                .verifyCollectionInfoAfterCreated(serviceCollectionNameEdit,SERVICE_TYPE,MANUALLY_MODE,String.valueOf(serviceList.length));
        navigateToSFAndVerifyCollectionPage(serviceCollectionNameEdit,false);
    }
    @Test(priority = 3)
    public void SC03_CreateManuaServiceCollectionHasAService() throws Exception {
        testCaseId = "SC03";
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
                verifyProductNameList(new ProductCollectionSF(driver).getProductNameListWithLazyLoad(serviceList.length/PAGE_SIZE_SF_COLLECTION +1), Arrays.stream(serviceList).toList());
    }
    @Test(priority = 4)
    public void SC04_CreateManualServiceCollectionWithServiceHasPriority() throws Exception {
        testCaseId = "SC04";
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
        serviceCollectionNameEdit = serviceCollectionName;
        callCreateMenuItemParentAPI(serviceCollectionName);
        serviceCollectionAPI = new ServiceCollectionAPI(loginInformation);
        int collectIDNewest = serviceCollectionAPI.getNewestCollectionID();
        //Check service collection on SF, dùng lại của product
        navigateSFAndGoToCollectionPage(serviceCollectionName);
        List<String> serviceListExpected = CreateServiceCollection.sortServiceListByPriorityAndLastUpdatedDate(loginInformation, CreateServiceCollection.servicePriorityMap, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(serviceListExpected.size()/PAGE_SIZE_SF_COLLECTION +1),serviceListExpected);
    }
    @Test(dependsOnMethods = "SC04_CreateManualServiceCollectionWithServiceHasPriority",priority = 5)
//    @Test
    public void SC05_UpdatePriorityNumber() throws Exception {
        testCaseId = "SC05";
//        serviceCollectionNameEdit="Collection service has priority TItXtDWIrL";
        loginAndNavigateToEditServiceCollection(serviceCollectionNameEdit)
                .editServicePriorityInCollection()
                .refreshPageUtilCollectUpdate(String.valueOf(serviceList.length))
                .searchCollection(serviceCollectionNameEdit)
                .verifyCollectionInfoAfterCreated(serviceCollectionNameEdit, Constant.SERVICE_TYPE, MANUALLY_MODE, String.valueOf(serviceList.length));
        navigateToSFAndVerifyCollectionPage(serviceCollectionNameEdit,true);
    }
    @Test(priority = 6)
    public void SC06_CreateManualServiceCollectionWithAServiceAndSEO() throws Exception {
        testCaseId = "SC06";
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
                .navigateToServiceDetailBySEOUrl(domainSF,SEOUrl)
                .verifySEOInfo(SEOTitle,SEODescription,SEOKeyword,serviceCollectionName)
                .verifyProductNameList(new ProductCollectionSF(driver).getProductNameListWithLazyLoad(serviceList.length/PAGE_SIZE_SF_COLLECTION +1), Arrays.stream(serviceList).toList());
    }
    @Test(priority = 7)
    public void SC07_CreateAutomatedServiceCollection_TitleContainKeyword() throws Exception {
        testCaseId = "SC07";
        String randomText =  generate.generateString(10);
        String serviceName = "Service contain keyword "+ randomText;
        String serviceCollectionName = "Collection service contains " + randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.CONTAINS+"-"+randomText};
        createAutomationCollectionAndVerify(serviceCollectionName,Constant.ALL_CONDITION,condition);
    }
    @Test(priority = 8)
    public void SC08_CreateAutomatedServiceCollection_TitleStartsWithKeyword() throws Exception {
        testCaseId = "SC08";
        String randomText =  generate.generateString(10);
        String serviceName = randomText + " service start keyword ";
        String serviceCollectionName = "Collection service starts with " + randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.STARTS_WITH+"-"+randomText};
        createAutomationCollectionAndVerify(serviceCollectionName,Constant.ALL_CONDITION,condition);
    }
    @Test(priority = 9)
    public void SC09_CreateAutomatedServiceCollection_TitleEndsWithKeyword() throws Exception {
        testCaseId = "SC09";
        String randomText =  generate.generateString(10);
        String serviceName = "Service ends keyword "+randomText;
        String serviceCollectionName = "Collection service ends with " + randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.ENDS_WITH+"-"+randomText};
        createAutomationCollectionAndVerify(serviceCollectionName,Constant.ALL_CONDITION,condition);
        serviceCollectionNameEdit = serviceCollectionName;
    }
    @Test(dependsOnMethods = "SC09_CreateAutomatedServiceCollection_TitleEndsWithKeyword",priority = 10)
    public void SC10_UpdateAutomedCollection_AndCondition() throws Exception {
        testCaseId = "SC10";
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+ STARTS_WITH+"-"+"Service"};
        editAutomationCollectionAndVerify(serviceCollectionNameEdit, ALL_CONDITION,condition);
    }
    @Test(priority = 11)
    public void SC11_CreateAutomatedServiceCollection_TitleEqualKeyword() throws Exception {
        testCaseId = "SC11";
        String randomText =  generate.generateString(10);
        String serviceName = "Service equal keyword "+randomText;
        String serviceCollectionName = "Collection service equal " + randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+ EQUAL_TO_TITLE+"-"+serviceName};
        createAutomationCollectionAndVerify(serviceCollectionName,Constant.ALL_CONDITION,condition);
        serviceCollectionNameEdit = serviceCollectionName;
    }
    @Test(dependsOnMethods = "SC11_CreateAutomatedServiceCollection_TitleEqualKeyword",priority = 12)
    public void SC12_UpdateAutomedCollection_OrCondition() throws Exception {
        testCaseId = "SC12";
        String randomText =  generate.generateString(10);
        String serviceName = "Service ends keyword "+randomText;
        callAPICreateService(serviceName);
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.ENDS_WITH+"-"+randomText};
        editAutomationCollectionAndVerify(serviceCollectionNameEdit, ANY_CONDITION,condition);
    }
    @Test(dependsOnMethods = "SC11_CreateAutomatedServiceCollection_TitleEqualKeyword",priority = 13)
    public void SC13_EditTranslation() throws Exception {
        testCaseId = "SC13";
        languageSF="ENG";
        String randomText = generate.generateString(10);
        String collectionNameTranslate = "Update collection " + randomText;
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        serviceCollectionsInfo.setCollectionName(collectionNameTranslate);
        serviceCollectionsInfo.setSEOTitleTranslation("SEO title "+randomText);
        serviceCollectionsInfo.setSEODescriptionTranslation("SEO description "+randomText);
        serviceCollectionsInfo.setSEOKeywordTranslation("SEO keyword "+randomText);
        serviceCollectionsInfo.setSEOUrlTranslation("url"+randomText);
        loginAndNavigateToEditServiceCollection(serviceCollectionNameEdit)
        .clickEditTranslationBtn()
                .editTranslation(serviceCollectionsInfo)
                .verifyUpdateTranslateSuccessfulMessage();
        web.StoreFront.login.LoginPage loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        headerSF.clickOnMenuItemByText(serviceCollectionNameEdit).waitTillLoaderDisappear();
        new ProductCollectionSF(driver).verifySEOInfo(serviceCollectionsInfo.getSEOTitleTranslation(),serviceCollectionsInfo.getSEODescriptionTranslation(),serviceCollectionsInfo.getSEOKeywordTranslation(),serviceCollectionsInfo.getCollectionNameTranslation())
                .navigateToServiceDetailBySEOUrl(domainSF,serviceCollectionsInfo.getSEOUrlTranslation())
                .verifySEOInfo(serviceCollectionsInfo.getSEOTitleTranslation(),serviceCollectionsInfo.getSEODescriptionTranslation(),serviceCollectionsInfo.getSEOKeywordTranslation(),serviceCollectionsInfo.getCollectionNameTranslation());
    }
    @Test(priority = 14)
    public void SC14_DeleteCollection(){
        testCaseId = "SC14";
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        new HomePage(driver).waitTillSpinnerDisappear1();
        ServiceCollectionManagement serviceCollectionManagement = new ServiceCollectionManagement(driver);
        serviceCollectionManagement.navigate();
        String firstCollection = serviceCollectionManagement.getTheFirstCollectionName();
        serviceCollectionManagement.deleteTheFirstCollection();
        serviceCollectionManagement.verifyCollectNameNotDisplayInList(firstCollection);
    }
    //out of date
//    @Test(priority = 15)
    public void SC15_CheckPermission() throws Exception {
        testCaseId = "SC15";
        checkPlanPermission(userName_goWeb,true);
        checkPlanPermission(userName_goApp,true);
        checkPlanPermission(userName_goPOS,false);
        checkPlanPermission(userName_goSocial,false);
        checkPlanPermission(userName_GoLead,false);
    }
    @Test(priority = 16)
    public void SC16_CheckText() throws Exception {
        loginAndNavigateToCreateServiceCollection().verifyText();
    }
    @Test(priority = 17)
    public void SC17_VerifyAutomatedCollectionWhenHasNewServiceMeetsCondition() throws Exception {
        testCaseId = "SC17";
        String randomText =  generate.generateString(10);
        String serviceName = "Service contain keyword "+ randomText;
        String serviceCollectionName = "Collection service contains " + randomText;
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.CONTAINS+"-"+randomText};
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.AUTOMATED_OPTION);
        serviceCollectionsInfo.setConditionType(ALL_CONDITION);
        serviceCollectionsInfo.setAutomatedConditions(condition);
        loginAndNavigateToCreateServiceCollection()
                .createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, "0");
        callAPICreateService(serviceName);
        callCreateMenuItemParentAPI(serviceCollectionsInfo.getCollectionName());
        serviceList = new String[]{serviceName.toLowerCase()};
        new ServiceCollectionManagement(driver)
                .refreshPageUtilCollectUpdate("1")
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, "1");
        //Check on SF
        navigateSFAndGoToCollectionPage(serviceCollectionsInfo.getCollectionName());
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(serviceList.length/PAGE_SIZE_SF_COLLECTION +1), Arrays.stream(serviceList).toList());
    }
    @Test(priority = 18)
    public void SC18_VerifyAutomatedCollectionWhenServiceBelongCollectionDoesNotMeetCondition() throws Exception {
        testCaseId = "SC18";
        String randomText =  generate.generateString(10);
        String serviceName = "Service contain keyword "+ randomText;
        String serviceCollectionName = "Collection service contains " + randomText;
        String[] condition = new String[]{Constant.SERVICE_TITLE+"-"+Constant.CONTAINS+"-"+randomText};
        ServiceInfo serviceInfo = callAPICreateService(serviceName);
        serviceList = new String[]{serviceName.toLowerCase()};
        ServiceCollectionsInfo serviceCollectionsInfo = new ServiceCollectionsInfo();
        serviceCollectionsInfo.setCollectionName(serviceCollectionName);
        serviceCollectionsInfo.setCollectionType(Constant.AUTOMATED_OPTION);
        serviceCollectionsInfo.setConditionType(ALL_CONDITION);
        serviceCollectionsInfo.setAutomatedConditions(condition);
        loginAndNavigateToCreateServiceCollection()
                .createServiceCollection(serviceCollectionsInfo)
                .refreshPage()
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, "1");
        //Update service
        EditServiceAPI editServiceAPI = new EditServiceAPI(loginInformation);
        editServiceAPI.setServiceNameEdit("Update name"+generate.generateString(5));
        editServiceAPI.updateService(serviceInfo.getServiceId());
        //Create menu item
        callCreateMenuItemParentAPI(serviceCollectionsInfo.getCollectionName());
        new ServiceCollectionManagement(driver)
                .refreshPageUtilCollectUpdate("0")
                .verifyCollectionInfoAfterCreated(serviceCollectionsInfo.getCollectionName(), Constant.SERVICE_TYPE, Constant.AUTOMATED_MODE, "0");
        //Check on SF
        navigateSFAndGoToCollectionPage(serviceCollectionsInfo.getCollectionName());
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyCollectionEmpty();
    }
}
