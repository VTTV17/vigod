import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import api.storefront.header.APIHeader;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.all_products.ProductPage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.createeditproductcollection.EditProductCollection;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.storefront.header.HeaderSF;
import pages.storefront.productcollection.ProductCollectionSF;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static utilities.Constant.PAGE_SIZE_SF_COLLECTION;
import static utilities.account.AccountTest.*;
import static utilities.enums.MenuItemType.COLLECTION_PRODUCT;
import static utilities.file.FileNameAndPath.*;
import static utilities.links.Links.SF_ShopVi;

public class ProductCollectionTest extends BaseTest {
    LoginPage loginDashboard;
    CreateProductCollection createProductCollection;
    ProductCollectionManagement productCollectionManagement;
    ProductCollectionSF productCollectionSF;
    EditProductCollection editProductCollection;
    String userNameDb;
    String passwordDb;
    String collectionName = "";
    String[] productList = {};
    String domainSF;
    int menuID;
    Login loginAPI;
    String token = "";
    String storeId = "";
    String SEOTitle = "";
    String SEODescription = "";
    String SEOKeyword = "";
    String SEOUrl = "";
    APIMenus menu;
    APIProductCollection productCollectAPI;
    String condition = "";
    HomePage home;
    String userName_goWeb;
    String userName_goApp;
    String userName_goPOS;
    String userName_goSocial;
    String userName_GoLead;
    String passwordCheckPermission;
    String collectNameEditPriority = "";
    static String collectionNameEditAutomationWithAndCondition = "";
    static String collectionNameEditAutomationWithOrCondition = "";
    String languageSF;
    String languageDashboard;
    String automatedMode;
    String manuallyMode;
    String productType;
    LoginInformation loginInformation;
    int collectionIdHasPriority;
    @BeforeClass
    public void getData() throws Exception {
        userNameDb = ADMIN_SHOP_VI_USERNAME;
        passwordDb = ADMIN_SHOP_VI_PASSWORD;
        domainSF = SF_ShopVi;
        loginInformation = new Login().setLoginInformation("+84",userNameDb,passwordDb).getLoginInformation();
        menuID = new APIHeader(loginInformation).getCurrentMenuId();
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        generate = new DataGenerator();
        languageDashboard = language;
        languageSF = language;
        automatedMode = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.automatedModeTxt");
        manuallyMode = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.manuallyModeTxt");
        productType = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.productTypeTxt");
        tcsFileName = FILE_PRODUCT_COLLECTION;
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, "false");
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        if (driver != null) driver.quit();
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
        productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        menu = new APIMenus(loginInformation);
        menu.CreateMenuItemParent(collectIDNewest, collectionName,COLLECTION_PRODUCT);
    }

    public void callDeleteMenuItemAndCollectionAPI(String collectionName) throws Exception {
        callLoginAPI();
        menu = new APIMenus(loginInformation);
        menu.deleteMenuItem(collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        APIProductCollection productCollection = new APIProductCollection(loginInformation);
        productCollection.deleteCollection(String.valueOf(collectIDNewest));
    }

    public CreateProductCollection loginAndNavigateToCreateProductCollection() throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        createProductCollection = new CreateProductCollection(driver);
        return createProductCollection.navigate(languageDashboard);
    }

    public EditProductCollection loginAndNavigateToEditCollection(String collectionName) throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        editProductCollection = new EditProductCollection(driver);
        return editProductCollection.navigateEditCollection(collectionName, languageDashboard);
    }

    public void navigateSFAndGoToCollectionPage(String collectionName) throws Exception {
        pages.storefront.login.LoginPage loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        headerSF. clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
    }

    public void navigateToSFAndVerifyCollectionPage(String collectNameEdit, boolean hasSetPriority) throws Exception {
        callLoginAPI();
        APIProductCollection productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        navigateSFAndGoToCollectionPage(collectNameEdit);
        List<String> productListSorted;
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        if (hasSetPriority) {
            productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation, CreateProductCollection.productPriorityMap, collectIDNewest);
        } else {
            productListSorted = apiAllProducts.getProductListInCollectionByLatest(String.valueOf(collectIDNewest));
        }
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListSorted);
    }

    /**
     * @param collectionName
     * @param conditionType: All conditions, Any condition
     * @param conditions
     * @throws Exception
     */
    public void createAutomationCollectionAndVerify(String collectionName, String conditionType, String... conditions) throws Exception {
        List<String> productExpectedList;
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (conditions.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(loginInformation, conditionType, conditions);
            productExpectedList = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (conditions.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(loginInformation, conditions[0]);
            System.out.println("productBelongCollectionMap: " + productBelongCollectionMap);
            productExpectedList = (List<String>) productBelongCollectionMap.get("ExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else {
            throw new Exception("Missing conditions");
        }
        System.out.println("Product: " + productExpectedList);

        loginAndNavigateToCreateProductCollection()
                .createProductAutomationCollectionWithoutSEO(collectionName, conditionType, conditions)
                .verifyCollectionInfoAfterCreated(collectionName, productType, automatedMode, String.valueOf(countItemExpected));
        callCreateMenuItemParentAPI(collectionName);
        //Check on SF
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
//        productCollectionSF.verifyProductCollectionName(collectionName)
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(productExpectedList.size()/PAGE_SIZE_SF_COLLECTION +1),productExpectedList);
    }

    public void editAutomationCollectionAndVerify(String collectionName, String conditionType, String... conditions) throws Exception {
        String[] allCondition = loginAndNavigateToEditCollection(collectionName)
                .EditAutomationCollection(conditionType, conditions);
        System.out.println("allCondition" + allCondition);
        List<String> productExpectedList;
        int countItemExpected;
        callLoginAPI();
        CreateProductCollection createProductCollection = new CreateProductCollection(driver);
        if (allCondition.length > 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(loginInformation, conditionType, allCondition);
            productExpectedList = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (allCondition.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition( loginInformation, conditions[0]);
            System.out.println("productBelongCollectionMap: " + productBelongCollectionMap);
            productExpectedList = (List<String>) productBelongCollectionMap.get("ExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else {
            throw new Exception("Missing conditions");
        }
        System.out.println("Product: " + productExpectedList);
        productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.verifyCollectionInfoAfterUpdated(collectionName, productType, automatedMode, String.valueOf(countItemExpected));
        callCreateMenuItemParentAPI(collectionName);
        //Check on SF
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
//        productCollectionSF.verifyProductCollectionName(collectionName)
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productExpectedList);
    }

    /**
     * @param packageType: GoWeb, GoApp, GoPos, GoSocial, GoLead.
     * @param userName
     * @throws IOException
     */
    public void checkPlanPermission(String packageType, String userName) throws IOException {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userName, passwordCheckPermission);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear()
                .checkPermissionAllPageByPackage("Products-Product Collections", packageType)
                .completeVerifyPermissionByPackage()
                .clickLogout();
    }

    @Test(priority = 1)
    public void PC_01_BH_4783_CreateManualProductCollection_V2() throws Exception {
        //product list is empty
        collectionName = "Manually: has no product " + generate.generateString(10);
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, "0");
        //product list: add some product, no input priority
        collectionName = "Manually collection has product " + generate.generateString(10);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin",
        "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser",
        "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream",
        "Kem Chống Nắng Aprilskin Nâng Tông Dưỡng Ẩm Da SPF 50+ 38g Tone Up Skin Tint"};
        productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.clickOnCreateCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(CreateProductCollection.productSelectedNumber))
                .clickLogout();
        callCreateMenuItemParentAPI(collectionName);
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        System.out.println("collectIDNewest: " + collectIDNewest);
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(String.valueOf(collectIDNewest));
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListExpected)
                .verifySEOInfo("", "", "", collectionName);
        //Delete menuItem (Clear data)
        callDeleteMenuItemAndCollectionAPI(collectionName);
        //product list: add some product, input priority
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_HasPriority(collectionName, productList, true, true)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(productList.length));
        callCreateMenuItemParentAPI(collectionName);
        productCollectAPI = new APIProductCollection(loginInformation);
        collectIDNewest = productCollectAPI.getNewestCollectionID();
        navigateSFAndGoToCollectionPage(collectionName);
        System.out.println("productPriorityMapInput: " + CreateProductCollection.productPriorityMap);
        List<String> productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation, CreateProductCollection.productPriorityMap, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListSorted);
//        collectionNameEditManual = collectionName;
    }

    @Test(priority = 2)
    public void PC_02_BH_4784_CreateManualProductCollectionAndAddAProductSEO() throws Exception {
        collectionName = "Manually: has SEO info" + generate.generateString(5);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin"};
        String radomText = generate.generateString(5);
        SEOTitle = "SEO title " + radomText;
        SEODescription = "SEO description " + radomText;
        SEOKeyword = "SEO keyword " + radomText;
        SEOUrl = "collectionseourl" + radomText;
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithSEO(collectionName, productList, SEOTitle, SEODescription, SEOKeyword, SEOUrl)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(CreateProductCollection.productSelectedNumber));
        callCreateMenuItemParentAPI(collectionName);
        //Check product collection on SF
        callLoginAPI();
        APIProductCollection productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(String.valueOf(collectIDNewest));
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
//        productCollectionSF.verifyProductCollectionName(collectionName)
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListExpected)
                .verifySEOInfo(SEOTitle, SEODescription, SEOKeyword, collectionName);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 3)
    public void PC_03_BH_4786_CreateAutomationProductCollectionWithTitleContainKeyword() throws Exception {
        condition = Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Gilaa";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        collectionNameEditAutomationWithOrCondition = collectionName;
    }

    @Test(priority = 4)
    public void PC_04_BH_4787_CreateAutomationProductCollectionWithTitleStartsWithKeyword() throws Exception {
        condition = Constant.PRODUCT_TITLE+"-"+Constant.STARTS_WITH+"-Kem Dưỡng";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        collectionNameEditAutomationWithAndCondition = collectionName;
    }

    @Test(priority = 5)
    public void PC_05_BH_4788_CreateAutomationProductCollectionWithTitleEndsWithKeyword() throws Exception {
        condition = Constant.PRODUCT_TITLE+"-"+Constant.ENDS_WITH+"-Skin";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 6)
    public void PC_06_BH_4789_CreateAutomationProductCollectionWithTitleEqualToKeyword() throws Exception {
        condition = Constant.PRODUCT_TITLE+"-"+Constant.EQUAL_TO_TITLE+"-Bột Uống Collagen Gilaa Kết Hợp Saffron 2gx60 Gói Premium Saffron Collagen";
        collectionName = generate.generateString(5) + " - " + "Product title-is equal to";
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 7)
    public void PC_07_BH_4790_CreateAutomationProductCollectionWithPriceEqualToNumber() throws Exception {
        condition = Constant.PRODUCT_PRICE+"-"+Constant.EQUAL_TO_PRICE+"-328000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 8)
    public void PC_08_BH_4791_CreateAutomationProductCollectionWithPriceLessThanNumber() throws Exception {
        condition = Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-100000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 9)
    public void PC_09_BH_4792_CreateAutomationProductCollectionWithPriceGreaterThanNumber() throws Exception {
        condition = Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-30000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 10)
    public void PC_10_BH_4793_CreateAutomationProductCollectionWithANDMultipleCondition() throws Exception {
        String[] conditions = {Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Skin", Constant.PRODUCT_PRICE+"-"+Constant.GREATER_THAN+"-300000"};
        collectionName = generate.generateString(5) + " - " + "and multiple condition";
        createAutomationCollectionAndVerify(collectionName, Constant.ALL_CONDITION, conditions);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 11)
    public void PC_11_BH_4794_CreateAutomationProductCollectionWithORMultipleCondition() throws Exception {
        String[] conditions = {Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-Phấn", Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-200000"};
        collectionName = generate.generateString(5) + " - " + "OR multiple condition";
        createAutomationCollectionAndVerify(collectionName, Constant.ANY_CONDITION, conditions);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(priority = 12)
    public void PC_12_BH_5239_CheckPermission() throws IOException {
        checkPlanPermission("GoWeb", userName_goWeb);
        checkPlanPermission("GoApp", userName_goApp);
        checkPlanPermission("GoPos", userName_goPOS);
        checkPlanPermission("GoSocial", userName_goSocial);
        checkPlanPermission("GoLead", userName_GoLead);
    }

    @Test(priority = 13)
    public void PC_13_BH_7670_CreateCollectionWithProductSortByPriorityNumber() throws Exception {
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        productList = new String[]{"Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin",
                "Kem Rửa Mặt Hada Labo Sạch Sâu Dưỡng Ẩm 80g Advanced Nourish Hyaluronic Acid Cleanser",
                "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream",
                "Bộ Sản Phẩm La Roche-Posay Phục Hồi Và Làm Dịu Da 2 Món Cicaplast Baume B5 40ml + Thermal Spring Water 50ml"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_HasPriority(collectionName, productList, false, true)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(productList.length));
        System.out.println("productPriorityMapInput: " + CreateProductCollection.productPriorityMap);
        callCreateMenuItemParentAPI(collectionName);
        callLoginAPI();
        APIProductCollection productCollectAPI = new APIProductCollection(loginInformation);
        int collectIDNewest = productCollectAPI.getNewestCollectionID();
        navigateSFAndGoToCollectionPage(collectionName);
        List<String> productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(loginInformation, CreateProductCollection.productPriorityMap, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListSorted);
        collectNameEditPriority = collectionName;
        collectionIdHasPriority = collectIDNewest;
    }

    @Test(dependsOnMethods = "PC_13_BH_7670_CreateCollectionWithProductSortByPriorityNumber",priority = 14)
    public void PC_14_BH_7671_UpdatePriorityNumberForProductInCollection() throws Exception {
        loginAndNavigateToEditCollection(collectNameEditPriority)
                .editProductPriorityInCollection();
        navigateToSFAndVerifyCollectionPage(collectNameEditPriority, true);
//        callDeleteMenuItemAndCollectionAPI(collectNameEditPriority);
    }

    @Test(priority = 15)
    public void PC_15_BH_4785_EditManualProductCollection() throws Exception {
        //create collection and edit new list
        collectionName = "Manually collection has product " + generate.generateString(5);
        productList = new String[]{"Gel Rửa Mặt Cosrx Tràm Trà, 0.5% BHA Có Độ pH Thấp 150ml Low pH Good Morning Gel Cleanser"};
        String[] productListEdit = new String[]{"Sữa Rửa Mặt Gilaa Khổ Qua Và Vegan BHA Giảm Mụn 160g Plant Serum Cleanser With Bitter Melon + BHA",
                "Bột Uống Collagen Gilaa Kết Hợp Saffron 2gx60 Gói Premium Saffron Collagen",
                "Sữa Rửa Mặt Gilaa Saffron Sạch Da Và Sáng Khỏe 160g Plant Serum Cleanser With Saffron Extract",
                "Kem Dưỡng Gilaa Khổ Qua & B5 Phục Hồi Giảm Mụn 50ml Bitter Melon Blemish Repair Cream"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(productList.length))
                .goToEditProductCollection(collectionName)
                .editProductListInManualCollection(productListEdit, true, false)
                .clickLogout();
        callCreateMenuItemParentAPI(collectionName);
        navigateToSFAndVerifyCollectionPage(collectionName, false);
        //edit product list, add new list, set priority
        productList = new String[]{"Phấn Nước Aprilskin Siêu Mỏng Nhẹ Màu 23 Be Tự Nhiên 15g Ultra Slim Cushion #23 Natural Beige",
                "Phấn Nước Aprilskin Ma Thuật Màu Sáng Hồng 22 Pink Beige 15g Magic Snow Cushion 2.0",
                "Bộ Sản Phẩm La Roche-Posay Phục Hồi Và Làm Dịu Da 2 Món Cicaplast Baume B5 40ml + Thermal Spring Water 50ml",
                "Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 400ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin"};
        loginAndNavigateToEditCollection(collectionName)
                .editProductListInManualCollection(productList, true, true);
        navigateToSFAndVerifyCollectionPage(collectionName, true);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }
    @Test(priority = 16)
    public void PC_16_BH_4796_AddProductToExistingManualCollection() throws Exception {
        //create collection
        collectionName = "Manually collection has product " + generate.generateString(5);
        productList = new String[]{"Gel Rửa Mặt Cosrx Tràm Trà, 0.5% BHA Có Độ pH Thấp 150ml Low pH Good Morning Gel Cleanser"};
        String[] productListEdit = new String[]{"Bộ Sản Phẩm La Roche-Posay Phục Hồi Và Làm Dịu Da 2 Món Cicaplast Baume B5 40ml + Thermal Spring Water 50ml"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(productList.length))
                .goToEditProductCollection(collectionName)
                .editProductListInManualCollection(productListEdit, false, false);
        callCreateMenuItemParentAPI(collectionName);
        navigateToSFAndVerifyCollectionPage(collectionName, false);//data no set priority before
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test(dependsOnMethods = "PC_04_BH_4787_CreateAutomationProductCollectionWithTitleStartsWithKeyword",priority = 17)
    public void PC_17_BH_4797_UpdateAutomationCollection_AndCondition() throws Exception {
        condition = Constant.PRODUCT_TITLE+"-"+Constant.CONTAINS+"-La Roche-Posay";
        editAutomationCollectionAndVerify(collectionNameEditAutomationWithAndCondition, Constant.ALL_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionNameEditAutomationWithAndCondition);
    }

    @Test(dependsOnMethods = "PC_03_BH_4786_CreateAutomationProductCollectionWithTitleContainKeyword",priority = 18)
    public void PC_18_BH_4798_UpdateAutomationCollection_OrCondition() throws Exception {
        condition = Constant.PRODUCT_PRICE+"-"+Constant.LESS_THAN+"-100000";
        editAutomationCollectionAndVerify(collectionNameEditAutomationWithOrCondition, Constant.ANY_CONDITION, condition);
        callDeleteMenuItemAndCollectionAPI(collectionNameEditAutomationWithOrCondition);
    }
    @Test(priority = 19)
    public void PC_19_BH_4795_DeleteAProductCollection() {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear();
        ProductCollectionManagement productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.navigateToProductCollectionManagement();
        String firstCollection = productCollectionManagement.getTheFirstCollectionName();
        productCollectionManagement.deleteTheFirstCollection();
        productCollectionManagement.verifyCollectNameNotDisplayInList(firstCollection);
    }
    @Test(priority = 20)
    public void PC_20_VerifyText() throws Exception {
        loginAndNavigateToCreateProductCollection()
                .verifyTextOfPage();
    }
//    @Test(dependsOnMethods = "PC_13_BH_7670_CreateCollectionWithProductSortByPriorityNumber",priority = 14)
    @Test
    public void PC_21_VerifyCollectionAfterImportProductToAvailableCollection_HasPriority(){
        collectionIdHasPriority =33218;
        Excel excel = new Excel();
        excel.writeCellValue(0,2,16,String.valueOf(collectionIdHasPriority),FOLDER_UPLOAD_FILE,FOLDER_IMPORT_PRODUCT, FILE_IMPORT_PRODUCT);
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        new HomePage(driver).navigateToPage("Products");
        new ProductPage(driver,loginInformation).importProduct(FILE_IMPORT_PRODUCT);

    }
}
