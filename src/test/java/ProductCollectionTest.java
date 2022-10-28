import api.dashboard.onlineshop.APIMenus;
import api.dashboard.products.APIAllProducts;
import api.dashboard.products.APIProductCollection;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.productcollection.createproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.storefront.header.HeaderSF;
import pages.storefront.productcollection.ProductCollectionSF;
import utilities.account.AccountTest;
import api.dashboard.login.Login;

import java.util.List;
import java.util.Map;

import static utilities.links.Links.SF_ShopVi;

public class ProductCollectionTest extends BaseTest {
    LoginPage loginDashboard;
    CreateProductCollection createProductCollection;
    ProductCollectionManagement productCollectionManagement;
    ProductCollectionSF productCollectionSF;
    String userNameDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
    String passwordDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
    String collectionName = "";
    String[] productList = {};
    String domainSF = SF_ShopVi;
    String menuID = "1174";
    Login loginAPI = new Login();;
    String token = "";
    String storeId = "";
    String SEOTitle = "";
    String SEODescription = "";
    String SEOKeyword = "";
    String SEOUrl = "";
    APIMenus menu = new APIMenus();
    APIProductCollection productCollectAPI = new APIProductCollection();

    public void callLoginAPI() {
        Map<String, String> loginInfo = loginAPI.loginToDashboardWithPhone("+84", userNameDb, passwordDb);
        token = loginInfo.get("accessToken");
        storeId = loginInfo.get("storeID");
    }

    public void callCreateMenuItemParentAPI(String collectionName) {
        callLoginAPI();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        menu.CreateMenuItemParent(token, menuID, collectIDNewest, collectionName);
    }
    public void callDeleteMenuItemAPI(String collectionName) throws Exception {
        callLoginAPI();
        menu.deleteMenuItem(storeId, token, menuID, collectionName);
    }
    public CreateProductCollection loginAndNavigateToCreateProductCollection(){
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        createProductCollection = new CreateProductCollection(driver);
        return createProductCollection.navigate();
    }
    @Test
    public void BH_4783_CreateManualProductCollection_V2() throws Exception {
        //product list is empty
        collectionName = "Manually collection has no product " + generate.generateString(10);
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
        pages.storefront.login.LoginPage loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();

        APIProductCollection productCollectAPI = new APIProductCollection();
        int collectIDNewest = productCollectAPI.getNewestCollectionID(storeId, token);
        System.out.println("collectIDNewest: "+collectIDNewest);
        APIAllProducts apiAllProducts = new APIAllProducts();
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token, collectIDNewest);
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(productListExpected)
                .verifySEOInfo("","","",collectionName);
        //Delete menuItem (Clear data)
        callDeleteMenuItemAPI(collectionName);
        //product list: add some product, input priority
        collectionName = "Manually collection has product and priority " + generate.generateString(5);
        productList = new String[]{"Quần jeans nữ ống rộng", "Vỏ bưởi", "Áo thun unisex form rộng Nhật Bản đẹp độc lạ vải dày mịn", "Áo khoác jean chống nắng", "Xương rồng mini"};
        loginAndNavigateToCreateProductCollection()
                .createManualCollectionWithoutSEO(collectionName, productList, true)
                .verifyCollectionInfo(collectionName, "Product", "Manually", String.valueOf(productList.length));
        callCreateMenuItemParentAPI(collectionName);
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductNameList(CreateProductCollection.productSortByPriority);
        callDeleteMenuItemAPI(collectionName);
    }
    @Test
    public void BH_4784_CreateManualProductCollectionAndAddAProductSEO() throws Exception {
        collectionName = "Manually collection has SEO info" + generate.generateString(5);
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
        List<String> productListExpected = apiAllProducts.getProductListInCollectionByLatest(storeId, token, collectIDNewest);

        pages.storefront.login.LoginPage loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(domainSF);
        HeaderSF headerSF = new HeaderSF(driver);
        headerSF.clickOnMenuItemByText(collectionName).waitTillLoaderDisappear();
        productCollectionSF = new ProductCollectionSF(driver);
        productCollectionSF.verifyProductCollectionName(collectionName)
                .verifyProductNameList(productListExpected)
                .verifySEOInfo(SEOTitle,SEODescription,SEOKeyword,collectionName);
    }
}
