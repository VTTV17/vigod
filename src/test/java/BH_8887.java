import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import api.storefront.login.LoginSF;
import api.storefront.signup.SignUp;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static api.dashboard.login.Login.storeName;
import static api.dashboard.products.CreateProduct.productID;
import static api.storefront.signup.SignUp.customerName;
import static java.lang.Thread.sleep;

public class BH_8887 extends BaseTest{

    @BeforeClass
    void createCustomerAndSegment() throws SQLException, InterruptedException {
        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);

        new SignUp().signUpByPhoneNumber();

        new LoginSF().LoginByPhoneNumber();

        sleep(3000);

        new Customers().addCustomerTag(customerName)
                .createSegment();
    }

    @BeforeTest
    public void setup() throws InterruptedException {
        super.setup();
    }

    @Test(groups = "Normal product - Without variation")
    void BH_8887_Case1_1_FlashSaleIsExpired() {
        int startMin = 1;
        int endMin = 2;
        int stockQuantity = 5;
        boolean isIMEIProduct = false;

        new CreateProduct().getTaxList()
                .getBranchList()
                .createWithoutVariationProduct(isIMEIProduct, stockQuantity);
        new CreatePromotion().createFlashSale(startMin, endMin)
                .createProductWholeSaleCampaign();
//                .endEarlyFlashSale();
        driver.get("https://%s.unisell.vn/vi/product/%s".formatted(storeName, productID));

    }

    @Test
    void T() {
//        System.out.println(Instant.now().getTimeInMillis());
    }

}
