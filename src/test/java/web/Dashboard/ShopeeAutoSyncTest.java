package web.Dashboard;

import java.util.List;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIShopeeProducts;
import utilities.model.dashboard.salechanel.shopee.ShopeeProduct;
import utilities.model.sellerApp.login.LoginInformation;

public class ShopeeAutoSyncTest extends BaseTest {
    
	LoginInformation credentials;

    
    @BeforeClass
    void loadCredentials() {
    	credentials = new Login().setLoginInformation("tienvan@mailnesia.com", "fortesting!1").getLoginInformation();
    }

    @Test
    public void TC_CheckCustomerInfoPostOrder() {
    	
        List<ShopeeProduct> shopeeProducts = new APIShopeeProducts(credentials).getProducts();

    }

//    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }
}
