package web.Dashboard;

import api.Seller.login.Login;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.home.Permission;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.pushnotification.PushNotificationManagement;
import web.Dashboard.products.productreviews.ProductReviews;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static utilities.account.AccountTest.*;
import static utilities.links.Links.DOMAIN;

public class CheckPermissionTest extends BaseTest {

	LoginPage dbLoginPage;
	HomePage homePage;
	ProductReviews productReviewPage;

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		dbLoginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		productReviewPage = new ProductReviews(driver);
	}	
	
	@BeforeMethod
	public void setup() throws InterruptedException {
		instantiatePageObjects();
	}

	@Test
	public void PermissionToUseProductReviews() throws Exception {
		
        Map<String, String> permission = new HashMap<String, String>();
        permission.put(ADMIN_USERNAME_GOWEB, "A");
        permission.put(ADMIN_USERNAME_GOAPP, "A");
        permission.put(ADMIN_USERNAME_GOPOS, "S");
        permission.put(ADMIN_USERNAME_GOSOCIAL, "S");
        permission.put(ADMIN_USERNAME_GOLEAD, "S");
		
        for (String username : permission.keySet()) {
    		dbLoginPage.navigate().performLogin(username, "fortesting!1");
    		homePage.waitTillSpinnerDisappear();
    		productReviewPage.navigate().verifyPermissionToManageReviews(permission.get(username));
    		homePage.clickLogout();        	
        }
	}    
	
	@Test
	public void PermissionToUsePushNotification() throws Exception {
		
		Map<String, String> permission = new HashMap<String, String>();
		permission.put(ADMIN_USERNAME_GOWEB, "D");
		permission.put(ADMIN_USERNAME_GOAPP, "A");
		permission.put(ADMIN_USERNAME_GOPOS, "D");
		permission.put(ADMIN_USERNAME_GOSOCIAL, "D");
		permission.put(ADMIN_USERNAME_GOLEAD, "D");
		
		Map<String, String> url = new Permission(driver, new Login().setLoginInformation("Vietnam", ADMIN_USERNAME_GOWEB, "fortesting!1").getLoginInformation()).getFeatureURL();
		
		for (String username : permission.keySet()) {
			dbLoginPage.navigate().performLogin(username, "fortesting!1");
			homePage.waitTillSpinnerDisappear().navigateToPage("Marketing", "Push Notification");
			new PushNotificationManagement(driver).verifyPermissionToCreatePushNotification(permission.get(username), url.get("Marketing-Push Notification-Create Push Notification"));
			
			if (permission.get(username) == "D") {
	            commonAction.navigateToURL(DOMAIN + url.get("Marketing-Push Notification-Create Push Notification"));
	            Assert.assertTrue(commonAction.getCurrentURL().contains("/404"));
	            commonAction.navigateToURL(DOMAIN);
			}
			
			homePage.clickLogout();        	
		}
	}    
	

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        driver.quit();
    }
	
}
