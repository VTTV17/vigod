import api.dashboard.login.Login;
import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.createeditproductcollection.EditProductCollection;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.storefront.header.HeaderSF;
import pages.storefront.productcollection.ProductCollectionSF;
import utilities.PropertiesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utilities.account.AccountTest.*;
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
    String menuID;
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
    String collectionNameEditManual = "";
    static String collectionNameEditAutomationWithAndCondition = "";
    static String collectionNameEditAutomationWithOrCondition = "";
    String languageSF;
    String languageDashboard;
    String automatedMode;
    String manuallyMode;
    String productType;
    @BeforeClass
    public void getData() throws Exception {
        userNameDb = ADMIN_SHOP_VI_USERNAME;
        passwordDb = ADMIN_SHOP_VI_PASSWORD;
        domainSF = SF_ShopVi;
        menuID = "7071";
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        passwordCheckPermission = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        languageSF = PropertiesUtil.getLanguageFromConfig("Storefront");
        languageDashboard = PropertiesUtil.getLanguageFromConfig("Dashboard");
        automatedMode = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.automatedModeTxt");
        manuallyMode = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.manuallyModeTxt");
        productType = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.productTypeTxt");
    }

    public void callLoginAPI() {
        loginAPI = new Login();
        Map<String, String> loginInfo = loginAPI.loginToDashboardWithPhone("+84", userNameDb, passwordDb);
        token = loginInfo.get("accessToken");
        storeId = loginInfo.get("storeID");
    }

    public void callCreateMenuItemParentAPI(String collectionName) {
        callLoginAPI();
        productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        menu = new APIMenus();
        menu.CreateMenuItemParent(token, menuID, collectIDNewest, collectionName);
    }

    public void callDeleteMenuItemAndCollectionAPI(String collectionName) throws Exception {
        callLoginAPI();
        menu = new APIMenus();
        menu.deleteMenuItem(storeId, token, menuID, collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        APIProductCollection productCollection = new APIProductCollection();
        productCollection.deleteCollection(token, storeId, String.valueOf(collectIDNewest));
    }

    public CreateProductCollection loginAndNavigateToCreateProductCollection() {
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
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        navigateSFAndGoToCollectionPage(collectNameEdit);
        List<String> productListSorted;
        APIAllProducts apiAllProducts = new APIAllProducts();
        if (hasSetPriority) {
            productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(CreateProductCollection.productPriorityMap, storeId, token, collectIDNewest);
        } else {
            productListSorted = apiAllProducts.getProductListInCollectionByLatest(storeId, token, String.valueOf(collectIDNewest));
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
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(token, storeId, conditionType, conditions);
            productExpectedList = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (conditions.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(token, storeId, conditions[0]);
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
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameListWithLazyLoad(3),productExpectedList);
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
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_MultipleCondition(token, storeId, conditionType, allCondition);
            productExpectedList = (List<String>) productBelongCollectionMap.get("productExpectedList");
            countItemExpected = (int) productBelongCollectionMap.get("CountItem");
        } else if (allCondition.length == 1) {
            Map productBelongCollectionMap = createProductCollection.productsBelongCollectionExpected_OneCondition(token, storeId, conditions[0]);
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

    @Test
    public void PC_01_BH_4783_CreateManualProductCollection_V2() throws Exception {
        //product list is empty
        collectionName = "Manually: has no product " + generate.generateString(10);
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, "0");
        //product list: add some product, no input priority
        collectionName = "Manually collection has product " + generate.generateString(10);
        productList = new String[]{"Dâu tây Đà Lạt", "Auto - Normal - variation - 08/02 09:56:04", "Handcrafted Concrete Gloves - Product API 1673499244"};
        productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.clickOnCreateCollection()
                .createManualCollectionWithoutSEO_NoPriority(collectionName, productList)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(CreateProductCollection.productSelectedNumber))
                .clickLogout();
        callCreateMenuItemParentAPI(collectionName);
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        System.out.println("collectIDNewest: " + collectIDNewest);
        APIAllProducts apiAllProducts = new APIAllProducts();
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token, String.valueOf(collectIDNewest));
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListExpected)
                .verifySEOInfo("", "", "", collectionName);
        //Delete menuItem (Clear data)
        callDeleteMenuItemAndCollectionAPI(collectionName);
        //product list: add some product, input priority
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        productList = new String[]{"Dâu tây Đà Lạt", "Auto - Normal - variation - 08/02 09:56:04", "Handcrafted Concrete Gloves - Product API 1673499244"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_HasPriority(collectionName, productList, true, true)
                .verifyCollectionInfoAfterCreated(collectionName, productType, manuallyMode, String.valueOf(productList.length));
        callCreateMenuItemParentAPI(collectionName);
        productCollectAPI = new APIProductCollection();
        collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        navigateSFAndGoToCollectionPage(collectionName);
        System.out.println("productPriorityMapInput: " + CreateProductCollection.productPriorityMap);
        List<String> productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(CreateProductCollection.productPriorityMap, storeId, token, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListSorted);
        collectionNameEditManual = collectionName;
    }

    @Test
    public void PC_02_BH_4784_CreateManualProductCollectionAndAddAProductSEO() throws Exception {
        collectionName = "Manually: has SEO info" + generate.generateString(5);
        productList = new String[]{"Dâu tây Đà Lạt"};
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
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        APIAllProducts apiAllProducts = new APIAllProducts();
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token, String.valueOf(collectIDNewest));
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
//        productCollectionSF.verifyProductCollectionName(collectionName)
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListExpected)
                .verifySEOInfo(SEOTitle, SEODescription, SEOKeyword, collectionName);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_03_BH_4786_CreateAutomationProductCollectionWithTitleContainKeyword() throws Exception {
        condition = "Product title-contains-Dâu";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        collectionNameEditAutomationWithOrCondition = collectionName;
    }

    @Test
    public void PC_04_BH_4787_CreateAutomationProductCollectionWithTitleStartsWithKeyword() throws Exception {
        condition = "Product title-starts with-Awesome";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        collectionNameEditAutomationWithAndCondition = collectionName;
    }

    @Test
    public void PC_05_BH_4788_CreateAutomationProductCollectionWithTitleEndsWithKeyword() throws Exception {
        condition = "Product title-ends with-Đà Lạt";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_06_BH_4789_CreateAutomationProductCollectionWithTitleEqualToKeyword() throws Exception {
        condition = "Product title-is equal to-Hồng trứng Đà Lạt";
        collectionName = generate.generateString(5) + " - " + "Product title-is equal to";
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_07_BH_4790_CreateAutomationProductCollectionWithPriceEqualToNumber() throws Exception {
        condition = "Product price-is equal to-499000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_08_BH_4791_CreateAutomationProductCollectionWithPriceLessThanNumber() throws Exception {
        condition = "Product price-is less than-50000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_09_BH_4792_CreateAutomationProductCollectionWithPriceGreaterThanNumber() throws Exception {
        condition = "Product price-is greater than-500000";
        collectionName = generate.generateString(5) + " - " + condition;
        createAutomationCollectionAndVerify(collectionName, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_10_BH_4793_CreateAutomationProductCollectionWithANDMultipleCondition() throws Exception {
        String[] conditions = {"Product title-contains-Dâu", "Product price-is greater than-100000"};
        collectionName = generate.generateString(5) + " - " + "and multiple condition";
        createAutomationCollectionAndVerify(collectionName, "All conditions", conditions);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_11_BH_4794_CreateAutomationProductCollectionWithORMultipleCondition() throws Exception {
        String[] conditions = {"Product title-contains-Dâu", "Product price-is greater than-5000000"};
        collectionName = generate.generateString(5) + " - " + "OR multiple condition";
        createAutomationCollectionAndVerify(collectionName, "Any condition", conditions);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }

    @Test
    public void PC_12_BH_5239_CheckPermission() throws IOException {
        checkPlanPermission("GoWeb", userName_goWeb);
        checkPlanPermission("GoApp", userName_goApp);
        checkPlanPermission("GoPos", userName_goPOS);
        checkPlanPermission("GoSocial", userName_goSocial);
        checkPlanPermission("GoLead", userName_GoLead);
    }

    @Test
    public void PC_13_BH_7670_CreateCollectionWithProductSortByPriorityNumber() throws Exception {
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        productList = new String[]{"Gorgeous Soft Sausages - Product has variation API 1673499230", "Hồng trứng Đà Lạt", "Dâu tây Đà Lạt"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO_HasPriority(collectionName, productList, false, true)
                .verifyCollectionInfoAfterCreated(collectionName, "Product", "Manually", String.valueOf(productList.length));
        callCreateMenuItemParentAPI(collectionName);
        navigateSFAndGoToCollectionPage(collectionName);
        callLoginAPI();
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        navigateSFAndGoToCollectionPage(collectionName);
        System.out.println("productPriorityMapInput: " + CreateProductCollection.productPriorityMap);
        List<String> productListSorted = CreateProductCollection.sortProductListByPriorityAndUpdatedDate(CreateProductCollection.productPriorityMap, storeId, token, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productCollectionSF.getProductNameList(),productListSorted);
        collectNameEditPriority = collectionName;
    }

    @Test(dependsOnMethods = "PC_13_BH_7670_CreateCollectionWithProductSortByPriorityNumber")
    public void PC_14_BH_7671_UpdatePriorityNumberForProductInCollection() throws Exception {
        loginAndNavigateToEditCollection(collectNameEditPriority)
                .editProductPriorityInCollection();
        navigateToSFAndVerifyCollectionPage(collectNameEditPriority, true);
        callDeleteMenuItemAndCollectionAPI(collectNameEditPriority);
    }

    @Test(dependsOnMethods = "PC_01_BH_4783_CreateManualProductCollection_V2")
    public void PC_15_BH_4785_EditManualProductCollection() throws Exception {
        //edit product list: add new list
        productList = new String[]{"Gorgeous Soft Sausages - Product has variation API 1673499230", "Hồng trứng Đà Lạt", "Dâu tây Đà Lạt"};
        loginAndNavigateToEditCollection(collectionNameEditManual)
                .editProductListInManualCollection(productList, true, false)
                .clickLogout();
        navigateToSFAndVerifyCollectionPage(collectionNameEditManual, false);
        //edit product list, add new list, set priority
        productList = new String[]{"Gorgeous Soft Sausages - Product has variation API 1673499230", "Hồng trứng Đà Lạt", "Dâu tây Đà Lạt","Fantastic Wooden Bacon - Product has variation API 1672995449"};
        loginAndNavigateToEditCollection(collectionNameEditManual)
                .editProductListInManualCollection(productList, true, true);
        navigateToSFAndVerifyCollectionPage(collectionNameEditManual, true);
        callDeleteMenuItemAndCollectionAPI(collectionNameEditManual);

    }
    @Test(dependsOnMethods = "PC_01_BH_4783_CreateManualProductCollection_V2")
    public void PC_16_BH_4796_AddProductToExistingManualCollection() throws Exception {
        productList = new String[]{"Gorgeous Soft Sausages - Product has variation API 1673499230"};
        loginAndNavigateToEditCollection(collectionNameEditManual)
                .editProductListInManualCollection(productList, false, false);
        navigateToSFAndVerifyCollectionPage(collectionNameEditManual, true);//data has set priority before
        callDeleteMenuItemAndCollectionAPI(collectionNameEditManual);

    }

    @Test(dependsOnMethods = "PC_04_BH_4787_CreateAutomationProductCollectionWithTitleStartsWithKeyword")
    public void PC_17_BH_4797_UpdateAutomationCollection_AndCondition() throws Exception {
        condition = "Product title-contains-Lạt";
        editAutomationCollectionAndVerify(collectionNameEditAutomationWithAndCondition, "All conditions", condition);
        callDeleteMenuItemAndCollectionAPI(collectionNameEditAutomationWithAndCondition);
    }

    @Test(dependsOnMethods = "PC_03_BH_4786_CreateAutomationProductCollectionWithTitleContainKeyword")
    public void PC_18_BH_4798_UpdateAutomationCollection_OrCondition() throws Exception {
        condition = "Product price-is less than-10000";
        editAutomationCollectionAndVerify(collectionNameEditAutomationWithOrCondition, "Any condition", condition);
        callDeleteMenuItemAndCollectionAPI(collectionNameEditAutomationWithOrCondition);
    }
    @Test
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
    @Test
    public void PC_20_VerifyText() throws Exception {
        loginAndNavigateToCreateProductCollection()
                .verifyTextOfPage();
    }

}
