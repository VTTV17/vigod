import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.productcollection.createproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.storefront.header.HeaderSF;
import pages.storefront.productcollection.ProductCollectionSF;
import api.dashboard.login.Login;
import static utilities.account.AccountTest.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utilities.links.Links.SF_ShopVi;

public class ProductCollectionTest extends BaseTest {
    LoginPage loginDashboard;
    CreateProductCollection createProductCollection;
    ProductCollectionManagement productCollectionManagement;
    ProductCollectionSF productCollectionSF;
    String userNameDb = ADMIN_SHOP_VI_USERNAME;
    String passwordDb = ADMIN_SHOP_VI_PASSWORD;
    String collectionName = "";
    String[] productList = {};
    String domainSF = SF_ShopVi;
    String menuID = "1174";
    Login loginAPI ;
    String token = "";
    String storeId = "";
    String SEOTitle = "";
    String SEODescription = "";
    String SEOKeyword = "";
    String SEOUrl = "";
    APIMenus menu;
    APIProductCollection productCollectAPI ;
    String condition = "";
    String languageSF = "Vietnamese";
    String languageDashboard = "ENG";

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
    public void callDeleteMenuItemAndCollectionAPI( String collectionName) throws Exception {
        callLoginAPI();
        menu = new APIMenus();
        menu.deleteMenuItem(storeId, token, menuID, collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        APIProductCollection productCollection = new APIProductCollection();
        productCollection.deleteCollection(token,storeId, String.valueOf(collectIDNewest));
    }
    public CreateProductCollection loginAndNavigateToCreateProductCollection(){
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        createProductCollection = new CreateProductCollection(driver);
        return createProductCollection.navigate(languageDashboard);
    }
    public void navigateSFAndGoToCollectionPage(String collectionName) throws Exception {
        pages.storefront.login.LoginPage loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.selectLanguage(languageSF).waitTillLoaderDisappear();
        headerSF.clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
    }
    public void createAutomationCollectionAndVerify(String collectionName, String condition) throws Exception {
        List<String> productExpectedList = new ArrayList<>();
        callLoginAPI();
        APIAllProducts apiAllProducts = new APIAllProducts();
        String conditionField = condition.split("-")[0];
        String operater = condition.split("-")[1];
        String value = condition.split("-")[2];
        int countItemExpected = 0;
        Map productCollectionInfo = new HashMap<>();
        if(conditionField.equalsIgnoreCase("Product title")){
            Map productCreatedDate= apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(token,storeId,operater,value);
            productExpectedList = apiAllProducts.getListProductMatchCondition_SortNewest(productCreatedDate);
            countItemExpected = productExpectedList.size();
        }else if(conditionField.equalsIgnoreCase("Product price")){
            Map productCollection= apiAllProducts.getProductMatchPriceCondition(token,storeId,operater, Long.parseLong(value));
           productCollectionInfo= (Map) apiAllProducts.getMapProductAndCountItemMatchPriceCondition_SortNewest(productCollection);
            productExpectedList = (List<String>) productCollectionInfo.get("sortedProductList");
            countItemExpected = (int) productCollectionInfo.get("CountItem");
        }
        System.out.println("Product: "+productExpectedList);
        loginAndNavigateToCreateProductCollection()
                .CreateProductAutomationCollectionWithoutSEO(collectionName,"All conditions",condition)
                .verifyCollectionInfo(collectionName,"Product","Automated",String.valueOf(countItemExpected));
        callCreateMenuItemParentAPI(collectionName);
        //Check on SF
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductCollectionName(collectionName)
                .verifyProductNameList(productExpectedList);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }
    @Test
    public void BH_4783_CreateManualProductCollection_V2() throws Exception {
        //product list is empty
        collectionName = "Manually: has no product " + generate.generateString(10);
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO(collectionName, productList, false)
                .verifyCollectionInfo(collectionName, "Product", "Manually", "0");
        //product list: add some product, no input priority
        collectionName = "Manually collection has product " + generate.generateString(10);
        productList = new String[]{"Quần jeans nữ ống rộng", "Vỏ bưởi", "Áo thun unisex form rộng Nhật Bản đẹp độc lạ vải dày mịn", "Áo khoác jean chống nắng", "Xương rồng mini"};
        productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.clickOnCreateCollection()
                .createManualCollectionWithoutSEO(collectionName, productList, false)
                .verifyCollectionInfo(collectionName, "Product", "Manually", String.valueOf(CreateProductCollection.productSelectedNumber))
                .clickLogout();
        callCreateMenuItemParentAPI(collectionName);
        //Check product collection on SF
        navigateSFAndGoToCollectionPage(collectionName);
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        System.out.println("collectIDNewest: "+collectIDNewest);
        APIAllProducts apiAllProducts = new APIAllProducts();
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token,String.valueOf(collectIDNewest));
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productListExpected)
                .verifySEOInfo("","","",collectionName);
        //Delete menuItem (Clear data)
        callDeleteMenuItemAndCollectionAPI(collectionName);
        //product list: add some product, input priority
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        productList = new String[]{"Quần jeans nữ ống rộng", "Vỏ bưởi", "Áo thun unisex form rộng Nhật Bản đẹp độc lạ vải dày mịn", "Áo khoác jean chống nắng", "Xương rồng mini"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO(collectionName, productList, true)
                .verifyCollectionInfo(collectionName, "Product", "Manually", String.valueOf(productList.length));
        callCreateMenuItemParentAPI(collectionName);
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(CreateProductCollection.productSortByPriority);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }
    @Test
    public void BH_4784_CreateManualProductCollectionAndAddAProductSEO() throws Exception {
        collectionName = "Manually: has SEO info" + generate.generateString(5);
        productList = new String[]{"Quần jeans nữ ống rộng"};
        String radomText = generate.generateString(5);
        SEOTitle = "SEO title "+radomText;
        SEODescription = "SEO description "+radomText;
        SEOKeyword = "SEO keyword "+radomText;
        SEOUrl = "collectionseourl"+radomText;
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithSEO(collectionName,productList,false,SEOTitle,SEODescription,SEOKeyword,SEOUrl)
                .verifyCollectionInfo(collectionName,"Product","Manually",String.valueOf(CreateProductCollection.productSelectedNumber));
        callCreateMenuItemParentAPI(collectionName);
        //Check product collection on SF
        callLoginAPI();
        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        APIAllProducts apiAllProducts = new APIAllProducts();
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token, String.valueOf(collectIDNewest));
        navigateSFAndGoToCollectionPage(collectionName);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductCollectionName(collectionName)
                .verifyProductNameList(productListExpected)
                .verifySEOInfo(SEOTitle,SEODescription,SEOKeyword,collectionName);
        callDeleteMenuItemAndCollectionAPI(collectionName);
    }
    @Test
    public void BH_4786_CreateAutomationProductCollectionWithTitleContainKeyword() throws Exception {
        condition = "Product title-contains-nam";
        collectionName = generate.generateString(5)+" - "+ condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4787_CreateAutomationProductCollectionWithTitleStartsWithKeyword() throws Exception {
        condition = "Product title-starts with-Áo thun";
        collectionName = generate.generateString(5)+ " - "+condition ;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4788_CreateAutomationProductCollectionWithTitleEndsWithKeyword() throws Exception {
        condition = "Product title-ends with-hiện đại";
        collectionName = generate.generateString(5)+" - "+condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4789_CreateAutomationProductCollectionWithTitleEqualToKeyword() throws Exception {
        condition = "Product title-is equal to-Áo khoác 2 lớp có nón rút gấu chống nắng ulzzang thời trang nữ";
        collectionName = generate.generateString(5)+" - "+"Product title-is equal to";
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4790_CreateAutomationProductCollectionWithPriceEqualToNumber() throws Exception {
        condition = "Product price-is equal to-300000";
        collectionName = generate.generateString(5)+" - "+condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4791_CreateAutomationProductCollectionWithPriceLessThanNumber() throws Exception {
        condition = "Product price-is less than-20000";
        collectionName = generate.generateString(5)+" - "+condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4792_CreateAutomationProductCollectionWithPriceGreaterThanNumber() throws Exception {
        condition = "Product price-is greater than-10000000";
        collectionName = generate.generateString(5)+" - "+condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
    @Test
    public void BH_4793_CreateAutomationProductCollectionWithANDMultipleCondition() throws Exception {
        condition = "Product price-is greater than-10000000";
        collectionName = generate.generateString(5)+" - "+condition;
        createAutomationCollectionAndVerify(collectionName,condition);
    }
}
