package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.sale_channel.lazada.APILazadaProducts;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class AutoSyncLazadaTest extends BaseTest{
    String sellerUsername;
    String sellerPass;
    LoginInformation loginInformation;
    @BeforeClass
    public void beforeClass() {
        sellerUsername = ADMIN_SHOP_VI_USERNAME;
        sellerPass = ADMIN_SHOP_VI_PASSWORD;
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
        LoginInformation loginInformation = new Login().setLoginInformation("+84", sellerUsername, sellerPass).getLoginInformation();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateToPage(Domain.valueOf(domain), DisplayLanguage.valueOf(language))
                .performValidLogin("Vietnam", sellerUsername, sellerPass);

    }
    @Test
    public void CreateProductToGoSell(){
    }
}
