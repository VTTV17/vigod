package android;

import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import api.dashboard.products.ProductCollection;
import api.storefront.header.APIHeader;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.home.BuyerHomePage;
import pages.buyerapp.navigationbar.NavigationBar;
import pages.buyerapp.search.BuyerSearchPage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.sellerapp.account.SellerAccount;
import pages.sellerapp.general.SellerGeneral;
import pages.sellerapp.home.HomePage;
import pages.sellerapp.login.LoginPage;
import pages.sellerapp.product.SellerCreateCollection;
import pages.sellerapp.product.SellerProductCollection;
import pages.sellerapp.product.SellerProductManagement;
import utilities.Constant;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.ADMIN_CREATE_NEW_SHOP_PASSWORD;

public class ProductCollectionTest extends BaseTest{
    String sellerAppPackage;
    String selelrAppActivity;
    String buyerAppPackage;
    String buyerAppActivity;
    String userDb;
    String passDb;
    DataGenerator generator;
    Login loginAPI;
    APIProductCollection productCollectAPI;
    APIMenus apiMenus;
    String[] productList;
    String condition;
    String userName_goWeb;
    String userName_goApp;
    String userName_goPOS;
    String userName_goSocial;
    String userName_GoLead;
    String passwordCheckPermission;
    String collectionUpdatePriority;
    String collectionUpdateProductList;
    String collectionUpdateToAllCondition;
    String collectionUpdateToAnyCondition;
    String collectioNameCheckBuyer;
    List<String> productListBelongCollectionCheckBuyer;
    List<String> productExpectedList_AutomatedCollection;
    LoginInformation loginInformation;

    @BeforeClass
    public void setUp() throws Exception {
        sellerAppPackage = "com.mediastep.GoSellForSeller.STG";
        selelrAppActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
        buyerAppPackage = "com.mediastep.shop0037";
        buyerAppActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        generator = new DataGenerator();
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        callLoginAPI();
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
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        new Screenshot().takeScreenshot(driver);
        if (driver != null) driver.quit();
    }
    public HomePage changeLaguage(){
        return new SellerAccount(driver).changeLanguage(language);
    }
    public void callLoginAPI() {
        loginAPI = new Login();
        loginInformation = loginAPI.setLoginInformation("+84", userDb,passDb).getLoginInformation();
    }
    public SellerCreateCollection goToCollectionDetailSeller(String collectionName){
        goToSellerCollectionPage();
        return new SellerProductCollection(driver).goToCollectionBySearch(collectionName);
    }
    public int getNewestCollectionId(){
        productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        return collectIDNewest;
    }
    public void callCreateMenuItemParentAPI(String collectionName) {
        int collectIDNewest = getNewestCollectionId();
        apiMenus = new APIMenus(loginInformation);
        int menuID = new APIHeader(loginInformation).getCurrentMenuId();
        apiMenus.CreateMenuItemParent(menuID, collectIDNewest, collectionName);
    }
    public void callDeleteCollectionAPI(){
        int collectIDNewest = getNewestCollectionId();
        ProductCollection collectionAPI = new ProductCollection(loginInformation);
        collectionAPI.deleteCollection(collectIDNewest);
    }
    public SellerProductCollection goToSellerCollectionPage() {
        new HomePage(driver).navigateToPage("Product");
        return new SellerProductManagement(driver).tapOnProductColectionIcon();
    }
    public void createAutomationCollectionAndVerify(String collectionName, String conditionType, String... conditions) throws Exception {
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (conditions.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(loginInformation,conditionType, conditions);
            productExpectedList_AutomatedCollection = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (conditions.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(loginInformation,conditions[0]);
            productExpectedList_AutomatedCollection = (List<String>) productBelongCollectionMap.get("ExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else {
            throw new Exception("Missing conditions");
        }
        System.out.println("Product: " + productExpectedList_AutomatedCollection);
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
    public void editAutomatedCollectionAndVerify(String collectionName, String conditionType, String... conditions) throws Exception {
        loginSellerApp();
        changeLaguage();
        String[] allCondition = goToCollectionDetailSeller(collectionName)
                .editAutomationCollection(conditionType,conditions);
        new SellerProductCollection(driver).verifyUpdateSuccessfullyMessage();
        System.out.println("allCondition" + Arrays.stream(allCondition).toList());
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (allCondition.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(loginInformation,conditionType, allCondition);
            productExpectedList_AutomatedCollection = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (allCondition.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(loginInformation, conditions[0]);
            productExpectedList_AutomatedCollection = (List<String>) productBelongCollectionMap.get("ExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else {
            throw new Exception("Missing conditions");
        }
        System.out.println("Product: " + productExpectedList_AutomatedCollection);
        new SellerProductCollection(driver)
                .inputToSearch(collectionName)
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
    public BuyerSearchPage goToCollectionDetail_Buyer(String collectionName){
        return new NavigationBar(driver).tapOnSearchIcon().goToCollectionDetailOnSearchTab(collectionName);
    }
    public void verifyCollectionOnBuyerApp(String collectionName,List<String> productListExpected){
        driver = launchApp(buyerAppPackage,buyerAppActivity);
        new BuyerHomePage(driver).goToCollectionByMenuText(collectionName)
                .verifyProductsInCollection(productListExpected,false)
                .verifyCountProduct(productListExpected.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectionName)
                .verifyProductsInCollection(productListExpected);
    }
    @Test(priority = 1)
    public void MPC01_VerifyTextByLanguage() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        loginSellerApp();
        changeLaguage();
        new HomePage(driver).navigateToPage("Product");
        new SellerProductManagement(driver).tapOnProductColectionIcon()
                .verifyText().tapCreateCollectionIcon().verifyText();
    }
    @Test(priority = 2)
    public void MPC02_CreateManualCollectionWithNoProduct() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
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
        callCreateMenuItemParentAPI(collectionName);
        collectioNameCheckBuyer = collectionName;
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        productListBelongCollectionCheckBuyer= apiAllProducts.getProductListInCollectionByLatest(String.valueOf(getNewestCollectionId()));
    }
    @Test(dependsOnMethods = "MPC02_CreateManualCollectionWithNoProduct",priority =3)
    public void MPC03_CheckCollectionDetail_NoProduct_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 4)
    public void MPC04_CreateManualCollection_HasProduct_NoPriotity() throws ParseException {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
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
        callCreateMenuItemParentAPI(collectionName);
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        productListBelongCollectionCheckBuyer= apiAllProducts.getProductListInCollectionByLatest(String.valueOf(getNewestCollectionId()));
        collectioNameCheckBuyer = collectionName;
        collectionUpdateProductList = collectionName;
    }
    @Test(dependsOnMethods = "MPC04_CreateManualCollection_HasProduct_NoPriotity",priority = 5)
    public void MPC05_CheckCollectionDetail_HasProductNoPriority_Buyer() {
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(dependsOnMethods = "MPC04_CreateManualCollection_HasProduct_NoPriotity",priority = 6)
    public void MPC06_UpdateProductList_ManualCollection() throws ParseException {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        productList = new String[]{
                "Sữa Rửa Mặt Gilaa Saffron Sạch Da Và Sáng Khỏe 160g Plant Serum Cleanser With Saffron Extract",
                "Bột Uống Collagen Gilaa Kết Hợp Saffron 2gx60 Gói Premium Saffron Collagen",
                "Phấn Nước Aprilskin Siêu Mỏng Nhẹ Màu 23 Be Tự Nhiên 15g Ultra Slim Cushion #23 Natural Beige",
                "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser"};
        loginSellerApp();
        changeLaguage();
        goToCollectionDetailSeller(collectionUpdateProductList)
                .deleteProductList()
                .selectProductsWithKeyword(productList)
                .tapSaveIcon()
                .verifyUpdateSuccessfullyMessage()
                .refreshPage()
                .verifyQuantityNewest(productList.length);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionUpdateProductList);
        productListBelongCollectionCheckBuyer= new APIAllProducts(loginInformation).getProductListInCollectionByLatest(String.valueOf(getNewestCollectionId()));
        collectioNameCheckBuyer = collectionUpdateProductList;
    }
    @Test(dependsOnMethods = "MPC06_UpdateProductList_ManualCollection",priority = 7)
    public void MPC07_CheckCollectionDetail_UpdateProductList_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 8)
    public void MPC08_CreateManualCollection_HasProduct_HasPrioity() throws ParseException {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection has priority product "+ generator.randomNumberGeneratedFromEpochTime(10);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin",
                "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser",
                "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream",
                "Kem Chống Nắng Aprilskin Nâng Tông Dưỡng Ẩm Da SPF 50+ 38g Tone Up Skin Tint"};
        loginSellerApp();
        changeLaguage();
        Map<String, Integer> priorityMap = goToCreateCollection()
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
        collectionUpdatePriority = collectionName;
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation,priorityMap, getNewestCollectionId());
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC08_CreateManualCollection_HasProduct_HasPrioity",priority = 9)
    public void MPC09_CheckCollectionDetail_HasProductHasPriority_Buyer() {
        driver = launchApp(buyerAppPackage,buyerAppActivity);
        new BuyerHomePage(driver).goToCollectionByMenuText(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer,true)
                .verifyCountProduct(productListBelongCollectionCheckBuyer.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer);
    }
    @Test(dependsOnMethods = "MPC08_CreateManualCollection_HasProduct_HasPrioity",priority = 10)
    public void MPC10_UdatePriorityNumber() throws ParseException {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        loginSellerApp();
        changeLaguage();
        Map<String,Integer>priorityMap = goToCollectionDetailSeller(collectionUpdatePriority)
                .inputPriority(true,false);
        new SellerCreateCollection(driver).tapSaveIcon()
                .verifyUpdateSuccessfullyMessage();
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionUpdatePriority);
        productListBelongCollectionCheckBuyer = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation,priorityMap, getNewestCollectionId());
        collectioNameCheckBuyer = collectionUpdatePriority;
    }
    @Test(dependsOnMethods = "MPC10_UdatePriorityNumber",priority = 11)
    public void MPC11_CheckCollectionDetail_AfterUpdatePriority_Buyer(){
        driver = launchApp(buyerAppPackage,buyerAppActivity);
        new BuyerHomePage(driver).goToCollectionByMenuText(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer,true)
                .verifyCountProduct(productListBelongCollectionCheckBuyer.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 12)
    public void MPC12_CreateAutomationCollection_ProductTitleContainsKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection title contains keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE +"-"+Constant.CONTAINS+"-Gilaa";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        collectionUpdateToAllCondition = collectionName;
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC12_CreateAutomationCollection_ProductTitleContainsKeyword",priority = 13)
    public void MPC13_CheckCollectionDetail_TitleContainKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 14)
    public void MPC14_CreateAutomationCollection_ProductTitleEqualKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection product title equals keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE+"-"+Constant.EQUAL_TO_TITLE+"-BÃ´Ì£t UÃ´Ìng Collagen Gilaa KÃªÌt HÆ¡Ì£p Saffron 2gx60 GÃ³i Premium Saffron Collagen";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to run testcase:
        collectionUpdateToAnyCondition = collectionName;
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC14_CreateAutomationCollection_ProductTitleEqualKeyword",priority = 15)
    public void MPC15_CheckCollectionDetail_ProductTitleEqualKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 16)
    public void MPC16_CreateAutomationCollection_ProductTitleStartWithKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection title start with keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE+"-"+Constant.STARTS_WITH+"-Kem DÆ°á»¡ng";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        collectionUpdateToAllCondition = collectionName;
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC16_CreateAutomationCollection_ProductTitleStartWithKeyword",priority = 17)
    public void MPC17_CheckCollectionDetail_TitleStartWithKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 18)
    public void MPC18_CreateAutomationCollection_ProductTitleEndWithKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection title ends with keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE+"-"+Constant.ENDS_WITH+"-Skin";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC18_CreateAutomationCollection_ProductTitleEndWithKeyword",priority = 19)
    public void MPC19_CheckCollectionDetail_TitleEndsWithKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 20)
    public void MPC20_CreateAutomationCollection_ProductPriceGreaterKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection price greater keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-30000";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC20_CreateAutomationCollection_ProductPriceGreaterKeyword",priority = 21)
    public void MPC21_CheckCollectionDetail_PriceGreaterKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 22)
    public void MPC22_CreateAutomationCollection_ProductPriceLessKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection price less than keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-300000";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC22_CreateAutomationCollection_ProductPriceLessKeyword",priority = 23)
    public void MPC23_CheckCollectionDetail_PriceLessThanKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 24)
    public void MPC24_CreateAutomationCollection_ProductPriceEqualKeyword() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String collectionName = "Collection price equal keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_PRICE+"-"+Constant.EQUAL_TO_PRICE+"-328000";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }

    @Test(dependsOnMethods = "MPC24_CreateAutomationCollection_ProductPriceEqualKeyword",priority = 25)
    public void MPC25_CheckCollectionDetail_PriceEqualKeyword_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 26)
    public void MPC26_CreateAutomationCollection_AllCondition() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String[] conditions = {Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000", Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Skin"};
        String collectionName = generator.generateString(5) + " - " + "and multiple condition";
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, conditions);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC26_CreateAutomationCollection_AllCondition",priority = 27)
    public void MPC27_CheckCollectionDetail_AllCondition_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 28)
    public void MPC28_CreateAutomationCollection_AnyCondition() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        String[] conditions = {Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Pháº¥n", Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-200000"};
        String collectionName = generator.generateString(5) + " - " + "OR multiple condition";
        createAutomationCollectionAndVerify(collectionName, Constant.ANY_CONDITION, conditions);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC28_CreateAutomationCollection_AnyCondition",priority = 29)
    public void MPC29_CheckCollectionDetail_AnyCondition_Buyer(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 30)
    public void MPC30_CheckPermissionByPackage(){
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goWeb,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goApp,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goPOS,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goSocial,false);
        checkPermissionByPackageWhenTapCollectionBtn(userName_GoLead,false);
    }

    @Test(dependsOnMethods = "MPC12_CreateAutomationCollection_ProductTitleContainsKeyword",priority = 31)
    public void MPC31_UpdateCollection_AllCondition() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        condition =Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000";
        editAutomatedCollectionAndVerify(collectionUpdateToAllCondition,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionUpdateToAllCondition;
    }
    @Test(dependsOnMethods = "MPC31_UpdateCollection_AllCondition",priority = 32)
    public void MPC32_CheckCollectionDetail_UpdateCollectionType_AllCondition(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(dependsOnMethods = "MPC14_CreateAutomationCollection_ProductTitleEqualKeyword",priority = 33)
    public void MPC33_UpdateCollection_AnyCondition() throws Exception {
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        condition =Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000";
        editAutomatedCollectionAndVerify(collectionUpdateToAnyCondition,Constant.ANY_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionUpdateToAnyCondition;
    }
    @Test(dependsOnMethods = "MPC33_UpdateCollection_AnyCondition",priority = 34)
    public void MPC34_CheckCollectionDetail_UpdateCollectionType_AnyCondition(){
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 35)
    public void MPC35_DeleteCollection_Seller(){
        collectioNameCheckBuyer="Collection price greater keyword9665822652";
        driver = launchApp(sellerAppPackage,selelrAppActivity);
        loginSellerApp(userDb,passDb);
        goToSellerCollectionPage()
                .deleteCollectionAndVerify(collectioNameCheckBuyer);
    }
    @Test(dependsOnMethods = "MPC35_DeleteCollection_Seller",priority = 36)
    public void MPC36_CheckDeletedCollection_Buyer(){
        driver = launchApp(buyerAppPackage,buyerAppActivity);
        new BuyerHomePage(driver).verifyMenuItemNotShow(collectioNameCheckBuyer);
        new NavigationBar(driver).tapOnSearchIcon().verifyMenuItemNotShowInList(collectioNameCheckBuyer);
    }
}
