package android;

import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIProductCollection;
import api.storefront.header.APIHeader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.home.BuyerHomePage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.sellerapp.account.SellerAccount;
import pages.sellerapp.general.SellerGeneral;
import pages.sellerapp.home.HomePage;
import pages.sellerapp.login.LoginPage;
import pages.sellerapp.product.SellerCreateCollection;
import pages.sellerapp.product.SellerProductCollection;
import pages.sellerapp.product.SellerProductManagement;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.screenshot.Screenshot;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.ADMIN_CREATE_NEW_SHOP_PASSWORD;

public class ProductCollectionTest {
    String sellerAppPackage;
    String selelrAppActivity;
    String buyerAppPackage;
    String buyerAppActivity;
    String language;
    String userDb;
    String passDb;
    WebDriver driver;
    DataGenerator generator;
    Login loginAPI;
    APIProductCollection productCollectAPI;
    APIMenus apiMenus;
    String[] productList;
    String productTitleTxt;
    String productPriceTxt;
    String containsOperateTxt;
    String equalToOperateProductTiteTxt;
    String startWithOperateTxt;
    String endsWithOperateTxt;
    String greaterThanTxt;
    String lessThanTxt;
    String equalToOperateProductPriceTxt;
    String condition;
    String allConditionTxt;
    String anyConditionTxt;
    String userName_goWeb;
    String userName_goApp;
    String userName_goPOS;
    String userName_goSocial;
    String userName_GoLead;
    String passwordCheckPermission;
    @BeforeClass
    public void setUp() throws Exception {
        sellerAppPackage = "com.mediastep.GoSellForSeller.STG";
        selelrAppActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
        buyerAppPackage = "com.mediastep.shop0037";
        buyerAppActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        language = "VIE";
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setDBLanguage(language);
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        generator = new DataGenerator();
        productTitleTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt");
        productPriceTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productPriceTxt");
        containsOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.containsTxt");
        equalToOperateProductTiteTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productTitleIsEqualToTxt");
        startWithOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.startsWithTxt");
        endsWithOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.endsWithTxt");
        greaterThanTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isGeaterThanTxt");
        lessThanTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isLessThanTxt");
        equalToOperateProductPriceTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productPriceIsEqualToTxt");
        allConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.allConditionsTxt");
        anyConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.anyConditionTxt");
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
    }
    public AppiumDriver launchApp(String appPackage, String appActivity) throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "R5CR92R4K7V");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", appPackage);
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability("noReset", "false");
        capabilities.setCapability("autoGrantPermissions","true");
        String url = "http://127.0.0.1:4723/wd/hub";
        return new InitAppiumDriver().getAppiumDriver(capabilities, url);
    }
    @AfterMethod
    public void restartApp(ITestResult result) throws IOException {
        new Screenshot().takeScreenshot(driver);
        ((AndroidDriver) driver).resetApp();
    }
    public HomePage loginSellerApp(){
       return new LoginPage(driver).performLogin(userDb,passDb);
    }
    public HomePage loginSellerApp(String userName, String pass){
        return new LoginPage(driver).performLogin(userName,pass);
    }
    public SellerCreateCollection goToCreateCollection(){
        new HomePage(driver).navigateToPage("Product");
       return new SellerProductManagement(driver).tapOnProductColectionIcon()
                .tapCreateCollectionIcon();
    }
    public HomePage changeLaguage(){
        return new SellerAccount(driver).changeLanguage(language);
    }
    public void callLoginAPI() {
        loginAPI = new Login();
        loginAPI.loginToDashboardWithPhone("+84", userDb,passDb);
    }

    public void callCreateMenuItemParentAPI(String collectionName) {
        callLoginAPI();
        productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        apiMenus = new APIMenus();
        int menuID = new APIHeader().getCurrentMenuId();
        apiMenus.CreateMenuItemParent(menuID, collectIDNewest, collectionName);
    }
    public void goToSellerCollectionPage() {
        new HomePage(driver).navigateToPage("Product");
        new SellerProductManagement(driver).tapOnProductColectionIcon();
    }
    public void createAutomationCollectionAndVerify(String collectionName, String conditionType, String... conditions) throws Exception {
        List<String> productExpectedList;
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (conditions.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(conditionType, conditions);
            productExpectedList = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (conditions.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(conditions[0]);
            System.out.println("productBelongCollectionMap: " + productBelongCollectionMap);
            productExpectedList = (List<String>) productBelongCollectionMap.get("ExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else {
            throw new Exception("Missing conditions");
        }
        System.out.println("Product: " + productExpectedList);
        loginSellerApp();
        changeLaguage();
        goToCreateCollection()
                .createAutomatedCollection(collectionName,conditionType,conditions)
                .verifyCreateSuccessfullyMessage()
                .refreshPage()
                .verifyQuantityNewest(countItemExpected)
                .verifyCollectionNameNewest(collectionName)
                .verifyCollectionTypeNewest("Automated")
                .selectNewestCollection()
                .verifyCollectionName(collectionName);
    }
    public void checkPermissionByPackageWhenTapCollectionBtn(String userName, boolean hasPermission){
        String collectionName = "Check permission "+ generator.randomNumberGeneratedFromEpochTime(10);
        loginSellerApp(userName,passwordCheckPermission);
        goToSellerCollectionPage();
        if(hasPermission){
            new SellerProductCollection(driver).verifyPageTitle()
                    .tapCreateCollectionIcon()
                    .inputCollectionName(collectionName)
                    .tapSaveIcon()
                    .verifyCreateSuccessfullyMessage()
                    .verifyCollectionNameNewest(collectionName);
            new SellerGeneral(driver).tapHeaderLeftIcon();
        }else {
            new SellerProductManagement(driver).verifyUpgradePopupWhenNoPermission()
                    .tapCancelBtnOnUpgradePopup();
        }
        new SellerGeneral(driver).tapHeaderLeftIcon();
        new HomePage(driver).LogOut();
    }
    @Test
    public void MPC01_VerifyTextByLanguage() throws Exception {
        loginSellerApp();
        changeLaguage();
        new HomePage(driver).navigateToPage("Product");
        new SellerProductManagement(driver).tapOnProductColectionIcon()
                .verifyText().tapCreateCollectionIcon().verifyText();
    }
    @Test
    public void MPC02_CreateManualCollectionWithNoProduct() throws Exception {
        String collectionName = "Collection no product "+ generator.randomNumberGeneratedFromEpochTime(10);
        loginSellerApp();
        changeLaguage();
        goToCreateCollection()
                .inputCollectionName(collectionName)
                .selectImage()
                .tapSaveIcon()
                .verifyCreateSuccessfullyMessage()
                .refreshPage()
                .verifyCollectionNameNewest(collectionName)
                .verifyCollectionTypeNewest("Manually")
                .selectNewestCollection()
                .verifyCollectionName(collectionName);
    }
    @Test
    public void MPC03_CreateManualCollection_HasProduct_NoPriotity(){
        String collectionName = "Collection has product "+ generator.randomNumberGeneratedFromEpochTime(10);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin",
                "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser",
                "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream",
                "Kem Chống Nắng Aprilskin Nâng Tông Dưỡng Ẩm Da SPF 50+ 38g Tone Up Skin Tint"};
        loginSellerApp();
        changeLaguage();
        goToCreateCollection()
                .inputCollectionName(collectionName)
                .selectImage()
                .selectProductsWithKeyword(productList)
                .tapSaveIcon()
                .verifyCreateSuccessfullyMessage()
                .refreshPage()
                .verifyQuantityNewest(productList.length)
                .verifyCollectionNameNewest(collectionName)
                .verifyCollectionTypeNewest("Manually")
                .selectNewestCollection()
                .verifyCollectionName(collectionName);
    }
    @Test
    public void MPC04_CreateManualCollection_HasProduct_HasPrioity(){
        String collectionName = "Collection has priority product "+ generator.randomNumberGeneratedFromEpochTime(10);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin",
                "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser",
                "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream",
                "Kem Chống Nắng Aprilskin Nâng Tông Dưỡng Ẩm Da SPF 50+ 38g Tone Up Skin Tint"};
        loginSellerApp();
        changeLaguage();
        goToCreateCollection()
                .inputCollectionName(collectionName)
                .selectImage()
                .selectProductsWithKeyword(productList)
                .inputPriority(true,false);
        new SellerCreateCollection(driver).tapSaveIcon()
                .verifyCreateSuccessfullyMessage()
                .refreshPage()
                .verifyQuantityNewest(productList.length)
                .verifyCollectionNameNewest(collectionName)
                .verifyCollectionTypeNewest("Manually")
                .selectNewestCollection()
                .verifyCollectionName(collectionName);
    }
    @Test
    public void MPC05_CreateAutomationCollection_ProductTitleContainsKeyword() throws Exception {
        String collectionName = "Collection title contains keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productTitleTxt+"-"+containsOperateTxt+"-Gilaa";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC06_CreateAutomationCollection_ProductTitleEqualKeyword() throws Exception {
        String collectionName = "Collection product title equals keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productTitleTxt+"-"+equalToOperateProductTiteTxt+"-Bột Uống Collagen Gilaa Kết Hợp Saffron 2gx60 Gói Premium Saffron Collagen";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC07_CreateAutomationCollection_ProductTitleStartWithKeyword() throws Exception {
        String collectionName = "Collection title start with keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productTitleTxt+"-"+startWithOperateTxt+"-Kem Dưỡng";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC08_CreateAutomationCollection_ProductTitleEndWithKeyword() throws Exception {
        String collectionName = "Collection title ends with keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productTitleTxt+"-"+endsWithOperateTxt+"-Skin";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC09_CreateAutomationCollection_ProductPriceGreaterKeyword() throws Exception {
        String collectionName = "Collection price greater keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productTitleTxt+"-"+endsWithOperateTxt+"-Skin";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC10_CreateAutomationCollection_ProductPriceLessKeyword() throws Exception {
        String collectionName = "Collection price less than keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productPriceTxt+"-"+lessThanTxt+"-100000";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC11_CreateAutomationCollection_ProductPriceEqualKeyword() throws Exception {
        String collectionName = "Collection price equal keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = productPriceTxt+"-"+equalToOperateProductPriceTxt+"-328000";
        createAutomationCollectionAndVerify(collectionName,allConditionTxt,condition);
    }
    @Test
    public void MPC12_CreateAutomationCollection_AllCondition() throws Exception {
        String[] conditions = {productPriceTxt+"-"+greaterThanTxt+"-300000", productTitleTxt+"-"+containsOperateTxt+"-Skin"};
        String collectionName = generator.generateString(5) + " - " + "and multiple condition";
        createAutomationCollectionAndVerify(collectionName, allConditionTxt, conditions);
    }
    @Test
    public void MPC13_CreateAutomationCollection_AnyCondition() throws Exception {
        String[] conditions = {productTitleTxt+"-"+containsOperateTxt+"-Phấn", productPriceTxt+"-"+lessThanTxt+"-200000"};
        String collectionName = generator.generateString(5) + " - " + "OR multiple condition";
        createAutomationCollectionAndVerify(collectionName, anyConditionTxt, conditions);
    }
    @Test
    public void MPC14_CheckPermissionByPackage(){
        checkPermissionByPackageWhenTapCollectionBtn(userName_goWeb,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goApp,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goPOS,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goSocial,false);
        checkPermissionByPackageWhenTapCollectionBtn(userName_GoLead,false);
    }
    public void MPC15_UdatePriorityNumber(){

    }
}
