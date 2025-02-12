package app.android.GoSeller;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.sale_channel.onlineshop.APIMenus;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.product_collections.APIProductCollection;
import api.Buyer.header.APIHeader;
import app.Buyer.buyergeneral.BuyerGeneral;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import lombok.SneakyThrows;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import app.Buyer.home.BuyerHomePage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.search.BuyerSearchPage;
import utilities.driver.InitAndroidDriver;
import utilities.utils.PropertiesUtil;
import web.Dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import app.GoSeller.account.SellerAccount;
import app.GoSeller.general.SellerGeneral;
import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import app.GoSeller.product.SellerCreateCollection;
import app.GoSeller.product.SellerProductCollection;
import app.GoSeller.product.SellerProductManagement;
import utilities.constant.Constant;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.enums.MenuItemType;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.ADMIN_CREATE_NEW_SHOP_PASSWORD;
import static utilities.environment.goBUYEREnvironment.goBUYERBundleId_ShopVi;
import static utilities.environment.goSELLEREnvironment.goSELLERBundleId;
import static utilities.file.FileNameAndPath.FILE_PRODUCT_COLLECTION_TCS;
import static utilities.file.FileNameAndPath.getDirectorySlash;

public class ProductCollectionTest extends BaseTest {
    String buyerAppPackage;
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
    String appPackage = goBUYERBundleId_ShopVi;
    String udid = PropertiesUtil.getEnvironmentData("udidAndroidVi");
    String buyerAppAPK =ANDROID_GOBUYER_APPNAME_SHOPVI;
    String sellerAppAPK = ANDROID_GoSELLER_APP;

    @BeforeClass
    public void setUp() throws Exception {
        buyerAppPackage = goBUYERBundleId_ShopVi;
        userDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        generator = new DataGenerator();
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        tcsFileName = FILE_PRODUCT_COLLECTION_TCS;
        callLoginAPI();
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

    @SneakyThrows
    public void launchAppBuyer() {
        driver = new InitAndroidDriver().getAndroidDriver(udid, System.getProperty("user.dir") + getDirectorySlash("src") +
                getDirectorySlash("main") +   getDirectorySlash("resources") + getDirectorySlash("app") + buyerAppAPK);
    }
    @SneakyThrows
    public void launchAppSeller() {
        driver = new InitAndroidDriver().getAndroidDriver(udid, System.getProperty("user.dir") + getDirectorySlash("src") +
                getDirectorySlash("main") +   getDirectorySlash("resources") + getDirectorySlash("app") + sellerAppAPK);
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        ((AndroidDriver) driver).removeApp(appPackage);
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
        apiMenus.CreateMenuItemParent(collectIDNewest, collectionName, MenuItemType.COLLECTION_PRODUCT);
    }
    public void callDeleteCollectionAPI(){
        int collectIDNewest = getNewestCollectionId();
        APIProductCollection collectionAPI = new APIProductCollection(loginInformation);
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
        new SellerProductCollection(driver).verifyUpdateSuccessfullyMessage().waitToUpdateCollection(2);
        new SellerGeneral(driver).tapHeaderLeftIcon();
        new SellerProductManagement(driver).tapOnProductColectionIcon();
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (allCondition.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(loginInformation,conditionType, allCondition);
            productExpectedList_AutomatedCollection = (List<String>) productBelongCollectionMap.get("productExpectedList");
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
        launchAppBuyer();
        new BuyerGeneral(driver).waitLoadingDisapear();
        new BuyerHomePage(driver).goToCollectionByMenuText(collectionName)
                .verifyProductsInCollection(productListExpected)
                .verifyCountProduct(productListExpected.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectionName)
                .verifyProductsInCollection(productListExpected);
    }
    @Test(priority = 1)
    public void MPC01_VerifyTextByLanguage() throws Exception {
        testCaseId = "MPC01";
        launchAppSeller();
        loginSellerApp();
        changeLaguage();
        new HomePage(driver).navigateToPage("Product");
        new SellerProductManagement(driver).tapOnProductColectionIcon()
                .verifyText().tapCreateCollectionIcon().verifyText();
    }
    @Test(priority = 2)
    public void MPC02_CreateManualCollectionWithNoProduct() throws Exception {
        testCaseId = "MPC02";
        launchAppSeller();
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
//    @Test
    public void MPC03_CheckCollectionDetail_NoProduct_Buyer(){
        testCaseId = "MPC03";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 4)
    public void MPC04_CreateManualCollection_HasProduct_NoPriotity() throws ParseException {
        testCaseId = "MPC04";
        launchAppSeller();
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
        testCaseId = "MPC05";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(dependsOnMethods = "MPC04_CreateManualCollection_HasProduct_NoPriotity",priority = 6)
    public void MPC06_UpdateProductList_ManualCollection() throws ParseException {
        testCaseId = "MPC06";
        launchAppSeller();
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
                .waitToUpdateCollection(3)
                .refreshPage()
                .verifyQuantityNewest(productList.length);
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer= new APIAllProducts(loginInformation).getProductListInCollectionByLatest(String.valueOf(getNewestCollectionId()));
        collectioNameCheckBuyer = collectionUpdateProductList;
    }
    @Test(dependsOnMethods = "MPC06_UpdateProductList_ManualCollection",priority = 7)
    public void MPC07_CheckCollectionDetail_UpdateProductList_Buyer(){
        testCaseId = "MPC07";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 8)
    public void MPC08_CreateManualCollection_HasProduct_HasPrioity() throws ParseException {
        testCaseId = "MPC08";
        launchAppSeller();
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
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation,priorityMap, getNewestCollectionId());
        collectioNameCheckBuyer = collectionName;
        collectionUpdatePriority = collectionName;

    }
    @Test(dependsOnMethods = "MPC08_CreateManualCollection_HasProduct_HasPrioity",priority = 9)
    public void MPC09_CheckCollectionDetail_HasProductHasPriority_Buyer() {
        testCaseId = "MPC09";
        launchAppBuyer();
        new BuyerGeneral(driver).waitLoadingDisapear();
        new BuyerHomePage(driver).goToCollectionByMenuText(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer)
                .verifyCountProduct(productListBelongCollectionCheckBuyer.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer);
    }
    @Test(dependsOnMethods = "MPC08_CreateManualCollection_HasProduct_HasPrioity",priority = 10)
    public void MPC10_UdatePriorityNumber() throws ParseException {
        testCaseId = "MPC10";
        launchAppSeller();
        loginSellerApp();
        changeLaguage();
        Map<String,Integer>priorityMap = goToCollectionDetailSeller(collectionUpdatePriority)
                .inputPriority(true,false);
        new SellerCreateCollection(driver).tapSaveIcon()
                .verifyUpdateSuccessfullyMessage();
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation,priorityMap, getNewestCollectionId());
        collectioNameCheckBuyer = collectionUpdatePriority;
    }
    @Test(dependsOnMethods = "MPC10_UdatePriorityNumber",priority = 11)
    public void MPC11_CheckCollectionDetail_AfterUpdatePriority_Buyer(){
        testCaseId = "MPC11";
        launchAppBuyer();
        new BuyerGeneral(driver).waitLoadingDisapear();
        new BuyerHomePage(driver).goToCollectionByMenuText(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer)
                .verifyCountProduct(productListBelongCollectionCheckBuyer.size())
                .tapBackIcon();
        goToCollectionDetail_Buyer(collectioNameCheckBuyer)
                .verifyProductsInCollection(productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 12)
    public void MPC12_CreateAutomationCollection_ProductTitleContainsKeyword() throws Exception {
        testCaseId = "MPC12";
        launchAppSeller();
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
        testCaseId = "MPC13";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 14)
    public void MPC14_CreateAutomationCollection_ProductTitleEqualKeyword() throws Exception {
        testCaseId = "MPC14";
        launchAppSeller();
        String collectionName = "Collection product title equals keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE+"-"+Constant.EQUAL_TO_TITLE+"-Bột Uống Collagen Gilaa Kết Hợp Saffron 2gx60 Gói Premium Saffron Collagen";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to run testcase
        collectionUpdateToAnyCondition = collectionName;
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC14_CreateAutomationCollection_ProductTitleEqualKeyword",priority = 15)
    public void MPC15_CheckCollectionDetail_ProductTitleEqualKeyword_Buyer(){
        testCaseId = "MPC15";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 16)
    public void MPC16_CreateAutomationCollection_ProductTitleStartWithKeyword() throws Exception {
        testCaseId = "MPC16";
        launchAppSeller();
        String collectionName = "Collection title start with keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_TITLE+"-"+Constant.STARTS_WITH+"-Kem Dưỡng";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        collectionUpdateToAllCondition = collectionName;
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC16_CreateAutomationCollection_ProductTitleStartWithKeyword",priority = 17)
    public void MPC17_CheckCollectionDetail_TitleStartWithKeyword_Buyer(){
        testCaseId = "MPC17";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 18)
    public void MPC18_CreateAutomationCollection_ProductTitleEndWithKeyword() throws Exception {
        testCaseId = "MPC18";
        launchAppSeller();
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
        testCaseId = "MPC19";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 20)
    public void MPC20_CreateAutomationCollection_ProductPriceGreaterKeyword() throws Exception {
        testCaseId = "MPC20";
        launchAppSeller();
        String collectionName = "Collection price greater keyword"+ generator.randomNumberGeneratedFromEpochTime(10);
        condition = Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000";
        createAutomationCollectionAndVerify(collectionName,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC20_CreateAutomationCollection_ProductPriceGreaterKeyword",priority = 21)
    public void MPC21_CheckCollectionDetail_PriceGreaterKeyword_Buyer(){
        testCaseId = "MPC21";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 22)
    public void MPC22_CreateAutomationCollection_ProductPriceLessKeyword() throws Exception {
        testCaseId = "MPC22";
        launchAppSeller();
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
        testCaseId = "MPC23";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 24)
    public void MPC24_CreateAutomationCollection_ProductPriceEqualKeyword() throws Exception {
        testCaseId = "MPC24";
        launchAppSeller();
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
        testCaseId = "MPC25";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 26)
    public void MPC26_CreateAutomationCollection_AllCondition() throws Exception {
        testCaseId = "MPC26";
        launchAppSeller();
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
        testCaseId = "MPC27";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(priority = 28)
    public void MPC28_CreateAutomationCollection_AnyCondition() throws Exception {
        testCaseId = "MPC28";
        launchAppSeller();
        String[] conditions = {Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Phấn", Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-200000"};
        String collectionName = generator.generateString(5) + " - " + "OR multiple condition";
        createAutomationCollectionAndVerify(collectionName, Constant.ANY_CONDITION, conditions);
        //set up data to check buyer app (the next testcase)
        callCreateMenuItemParentAPI(collectionName);
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionName;
    }
    @Test(dependsOnMethods = "MPC28_CreateAutomationCollection_AnyCondition",priority = 29)
    public void MPC29_CheckCollectionDetail_AnyCondition_Buyer(){
        testCaseId = "MPC29";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    //out of date
//    @Test(priority = 30)
    public void MPC30_CheckPermissionByPackage(){
        testCaseId = "MPC30";
        launchAppSeller();
        checkPermissionByPackageWhenTapCollectionBtn(userName_goWeb,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goApp,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goPOS,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_goSocial,true);
        checkPermissionByPackageWhenTapCollectionBtn(userName_GoLead,false);
    }

    @Test(dependsOnMethods = "MPC12_CreateAutomationCollection_ProductTitleContainsKeyword",priority = 31)
    public void MPC31_UpdateCollection_AllCondition() throws Exception {
        testCaseId = "MPC31";
        launchAppSeller();
        condition =Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000";
        editAutomatedCollectionAndVerify(collectionUpdateToAllCondition,Constant.ALL_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionUpdateToAllCondition;
    }
    @Test(dependsOnMethods = "MPC31_UpdateCollection_AllCondition",priority = 32)
    public void MPC32_CheckCollectionDetail_UpdateCollectionType_AllCondition(){
        testCaseId = "MPC32";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
        callDeleteCollectionAPI();
    }
    @Test(dependsOnMethods = "MPC14_CreateAutomationCollection_ProductTitleEqualKeyword",priority = 33)
    public void MPC33_UpdateCollection_AnyCondition() throws Exception {
        testCaseId = "MPC33";
        launchAppSeller();
        condition =Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000";
        editAutomatedCollectionAndVerify(collectionUpdateToAnyCondition,Constant.ANY_CONDITION,condition);
        //set up data to check buyer app (the next testcase)
        productListBelongCollectionCheckBuyer = productExpectedList_AutomatedCollection;
        collectioNameCheckBuyer = collectionUpdateToAnyCondition;
    }
    @Test(dependsOnMethods = "MPC33_UpdateCollection_AnyCondition",priority = 34)
    public void MPC34_CheckCollectionDetail_UpdateCollectionType_AnyCondition(){
        testCaseId = "MPC34";
        verifyCollectionOnBuyerApp(collectioNameCheckBuyer,productListBelongCollectionCheckBuyer);
    }
    @Test(priority = 35)
    public void MPC35_DeleteCollection_Seller(){
        testCaseId = "MPC35";
        launchAppSeller();
        loginSellerApp(userDb,passDb);
        goToSellerCollectionPage()
                .deleteCollectionAndVerify(collectioNameCheckBuyer);
    }
//    @Test(dependsOnMethods = "MPC35_DeleteCollection_Seller",priority = 36)
    @Test
    public void MPC36_CheckDeletedCollection_Buyer(){
        collectioNameCheckBuyer = "Collection product title equals keyword9266606654";
        testCaseId = "MPC36";
        launchAppBuyer();
        new BuyerGeneral(driver).waitLoadingDisapear();
        new BuyerHomePage(driver).verifyMenuItemNotShow(collectioNameCheckBuyer);
        new NavigationBar(driver).tapOnSearchIcon().verifyMenuItemNotShowInList(collectioNameCheckBuyer);
    }
}
